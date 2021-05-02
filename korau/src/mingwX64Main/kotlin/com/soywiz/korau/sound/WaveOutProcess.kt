package com.soywiz.korau.sound

import com.soywiz.kmem.*
import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.*
import kotlin.native.concurrent.*

private class ConcurrentDeque<T : Any> {
    private val items = AtomicReference<List<T>>(emptyList<T>().freeze())

    init {
        this.freeze()
    }

    val size get() = items.value.size

    fun add(item: T) {
        do {
            val oldList = this.items.value
            val newList = (oldList + item).freeze()
        } while (!this.items.compareAndSet(oldList, newList))
    }

    //val length get() = items.value.size

    fun consume(): T? {
        while (true) {
            val oldList = this.items.value
            if (oldList.isEmpty()) return null
            val lastItem = oldList.first()
            val newList = oldList.subList(1, oldList.size).freeze()
            if (this.items.compareAndSet(oldList, newList)) return lastItem
        }
    }
}

private interface WaveOutPart

private object WaveOutEnd : WaveOutPart
private object WaveOutFlush : WaveOutPart

private class WaveOutReopen(val freq: Int) : WaveOutPart {
    init {
        this.freeze()
    }
}

private interface WaveOutDataBase : WaveOutPart {
    fun computeData(): ShortArray
}

//private class WaveOutData(val data: ShortArray) : WaveOutDataBase {
//    override fun computeData(): ShortArray = data
//
//    init {
//        this.freeze()
//    }
//}

private class WaveOutDataEx(
    val adata: Array<ShortArray>,
    val pitch: Double,
    val volume: Double,
    val panning: Double,
    val freq: Int
) : WaveOutDataBase {
    override fun computeData(): ShortArray =
        AudioSamples(2, adata[0].size, Array(2) { adata[it % adata.size] })
            //.resampleIfRequired(freq, 44100)
            .interleaved()
            .applyProps(pitch, panning, volume)
            .data

    init {
        this.freeze()
    }
}
class WaveOutProcess(val freq: Int, val nchannels: Int) {
    private val sPosition = AtomicLong(0L)
    private val sLength = AtomicLong(0L)
    private val completed = AtomicLong(0L)
    private val numPendingChunks = AtomicLong(0L)
    private val deque = ConcurrentDeque<WaveOutPart>()
    private val info = AtomicReference<Future<Unit>?>(null)

    val position get() = sPosition.value
    val length get() = sLength.value
    //val isCompleted get() = completed.value != 0L
    val pendingAudio get() = numPendingChunks.value != 0L || deque.size > 0

    init {
        freeze()
    }

    val pendingCommands get() = deque.size

    //fun addData(data: ShortArray) {
    //    sLength.addAndGet(data.size / nchannels)
    //    deque.add(WaveOutData(data))
    //}

    fun addData(samples: AudioSamples, offset: Int, size: Int, pitch: Double, volume: Double, panning: Double, freq: Int) {
        sLength.addAndGet(size)
        deque.add(WaveOutDataEx(
            Array(samples.channels) { samples.data[it].copyOfRange(offset, offset + size) },
            pitch, volume, panning, freq
        ))
    }

    fun stop() {
        deque.add(WaveOutEnd)
    }

    fun reopen(freq: Int) {
        sPosition.value = 0L
        sLength.value = 0L
        completed.value = 0L
        deque.add(WaveOutReopen(freq))
    }

    fun stopAndWait() {
        stop()
        info?.value?.consume {  }
    }

    fun start(_worker: Worker): WaveOutProcess {
        val _info = this
        _info.info.value = _worker.execute(TransferMode.SAFE, { _info }) { info ->
            memScoped {
                val nchannels = info.nchannels // 2
                val hWaveOut = alloc<HWAVEOUTVar>()
                val pendingChunks = ArrayDeque<WaveOutChunk>()

                fun clearCompletedChunks() {
                    while (pendingChunks.isNotEmpty() && pendingChunks.first().completed) {
                        val chunk = pendingChunks.removeFirst()
                        waveOutUnprepareHeader(hWaveOut.value, chunk.hdr.ptr, sizeOf<WAVEHDR>().convert())
                        info.sPosition.addAndGet(chunk.data.size / nchannels)
                        chunk.dispose()
                    }
                }

                fun waveReset() {
                    clearCompletedChunks()
                    while (pendingChunks.isNotEmpty()) {
                        Sleep(5.convert())
                        clearCompletedChunks()
                    }
                    waveOutReset(hWaveOut.value)
                    info.sPosition.value = 0L
                }

                fun waveClose() {
                    waveReset()
                    waveOutClose(hWaveOut.value)
                }

                var openedFreq = 0

                fun waveOpen(freq: Int) {
                    openedFreq = freq
                    memScoped {
                        val format = alloc<WAVEFORMATEX>().apply {
                            this.wFormatTag = WAVE_FORMAT_PCM.convert()
                            this.nChannels = nchannels.convert() // 2?
                            this.nSamplesPerSec = freq.convert()
                            this.wBitsPerSample = Short.SIZE_BITS.convert() // 16
                            this.nBlockAlign = (info.nchannels * Short.SIZE_BYTES).convert()
                            this.nAvgBytesPerSec = this.nSamplesPerSec * this.nBlockAlign
                            this.cbSize = sizeOf<WAVEFORMATEX>().convert()
                            //this.cbSize = 0.convert()
                        }

                        waveOutOpen(hWaveOut.ptr, WAVE_MAPPER, format.ptr, 0.convert(), 0.convert(), CALLBACK_NULL)
                    }
                }

                waveOpen(info.freq)

                try {
                    process@while (true) {
                        clearCompletedChunks()
                        while (true) {
                            val it = info.deque.consume() ?: break
                            //println("CONSUME: $item")
                            when (it) {
                                is WaveOutReopen -> {
                                    if (it.freq != openedFreq) {
                                        waveClose()
                                        waveOpen(it.freq)
                                    }
                                }
                                is WaveOutEnd -> break@process
                                is WaveOutDataBase -> {
                                    val chunk = WaveOutChunk(it.computeData())
                                    //info.sLength.addAndGet(chunk.data.size / info.nchannels)
                                    pendingChunks.add(chunk)
                                    waveOutPrepareHeader(hWaveOut.value, chunk.hdr.ptr, sizeOf<WAVEHDR>().convert())
                                    waveOutWrite(hWaveOut.value, chunk.hdr.ptr, sizeOf<WAVEHDR>().convert())
                                }
                                is WaveOutFlush -> {
                                    waveReset()
                                }
                            }
                        }
                        Sleep(5.convert())
                    }
                } finally {
                    //println("finalizing...")
                    waveClose()
                    info.completed.value = 1L
                }
            }
        }
        return _info
    }
}

private class WaveOutChunk(val data: ShortArray) {
    val scope = Arena()
    val dataPin = data.pin()
    val hdr = scope.alloc<WAVEHDR>().apply {
        //println(samplesInterleaved.data.toList())
        this.lpData = dataPin.startAddressOf.reinterpret()
        this.dwBufferLength = (data.size * Short.SIZE_BYTES).convert()
        this.dwFlags = 0.convert()
    }
    val completed: Boolean get() = (hdr.dwFlags.toInt() and WHDR_DONE.toInt()) != 0

    fun dispose() {
        dataPin.unpin()
        scope.clear()
    }
}
