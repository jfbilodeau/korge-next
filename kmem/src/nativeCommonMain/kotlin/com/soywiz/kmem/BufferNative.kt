// @WARNING: File AUTOGENERATED by `korlibs-generator-jvm/src/com/soywiz/korlibs` @ korlibs/kmem do not modify manually!
// @TODO: USELESS_CAST is required since it requires a cast to work, but IDE says that that cast is not necessary
@file:Suppress("NOTHING_TO_INLINE", "EXTENSION_SHADOWED_BY_MEMBER", "RedundantUnitReturnType", "FunctionName", "USELESS_CAST")
package com.soywiz.kmem

import kotlinx.cinterop.*
import platform.posix.*
import com.soywiz.kmem.internal.*

actual class MemBuffer(val data: ByteArray)
actual fun MemBufferAlloc(size: Int): MemBuffer = MemBuffer(ByteArray(size))
actual fun MemBufferAllocNoDirect(size: Int): MemBuffer = MemBuffer(ByteArray(size))
actual fun MemBufferWrap(array: ByteArray): MemBuffer = MemBuffer(array)
actual inline val MemBuffer.size: Int get() = data.size

actual fun MemBuffer._sliceInt8Buffer(offset: Int, size: Int): Int8Buffer =
    Int8Buffer(this, offset * 1, size)
actual fun MemBuffer._sliceInt16Buffer(offset: Int, size: Int): Int16Buffer =
    Int16Buffer(this, offset * 2, size)
actual fun MemBuffer._sliceInt32Buffer(offset: Int, size: Int): Int32Buffer =
    Int32Buffer(this, offset * 4, size)
actual fun MemBuffer._sliceFloat32Buffer(offset: Int, size: Int): Float32Buffer =
    Float32Buffer(this, offset * 4, size)
actual fun MemBuffer._sliceFloat64Buffer(offset: Int, size: Int): Float64Buffer =
    Float64Buffer(this, offset * 8, size)

// @TODO: https://youtrack.jetbrains.com/issue/KT-46427
//@SymbolName("Kotlin_ByteArray_setFloatAtUnsafe") public external fun ByteArray.setFloatAtUnsafe(index: Int, value: Float)
//@SymbolName("Kotlin_ByteArray_getFloatAtUnsafe") public external fun ByteArray.getFloatAtUnsafe(index: Int): Float

private inline fun ByteArray.setFloatAtUnsafe(index: Int, value: Float) = setFloatAt(index, value)
private inline fun ByteArray.getFloatAtUnsafe(index: Int): Float = getFloatAt(index)

actual typealias DataBuffer = MemBuffer
actual val DataBuffer.mem: MemBuffer get() = this
actual fun MemBuffer.getData(): DataBuffer = this
actual fun DataBuffer.getByte(index: Int): Byte = data.get(index)
actual fun DataBuffer.getShort(index: Int): Short = data.getShortAt(index)
actual fun DataBuffer.getInt(index: Int): Int = data.getIntAt(index)
actual fun DataBuffer.getFloat(index: Int): Float = data.getFloatAtUnsafe(index)
actual fun DataBuffer.getDouble(index: Int): Double = data.getDoubleAt(index)
actual fun DataBuffer.setByte(index: Int, value: Byte): Unit = data.set(index, value)
actual fun DataBuffer.setShort(index: Int, value: Short): Unit = data.setShortAt(index, value)
actual fun DataBuffer.setInt(index: Int, value: Int): Unit = data.setIntAt(index, value)
actual fun DataBuffer.setFloat(index: Int, value: Float): Unit = data.setFloatAtUnsafe(index, value)
actual fun DataBuffer.setDouble(index: Int, value: Double): Unit = data.setDoubleAt(index, value)

actual class Int8Buffer(val mbuffer: MemBuffer, val byteOffset: Int, val size: Int) {
    //companion object { const val SIZE = 1 }
    val MEM_OFFSET = byteOffset / 1/*SIZE*/
    //val MSIZE = size / SIZE // @TODO: Caused problems with Kotlin/Native because: #define	MSIZE		(1 << MSIZESHIFT) in param.h
    val MEM_SIZE = size / 1/*SIZE*/
    fun getByteIndex(index: Int) = byteOffset + index * 1/*SIZE*/
}
actual val Int8Buffer.mem: MemBuffer get() = mbuffer
actual val Int8Buffer.offset: Int get() = MEM_OFFSET
actual val Int8Buffer.size: Int get() = MEM_SIZE
actual operator fun Int8Buffer.get(index: Int): Byte = mbuffer.getByte(getByteIndex(index))
actual operator fun Int8Buffer.set(index: Int, value: Byte): Unit = mbuffer.setByte(getByteIndex(index), value)

actual class Int16Buffer(val mbuffer: MemBuffer, val byteOffset: Int, val size: Int) {
    //companion object { const val SIZE = 2 }
    val MEM_OFFSET = byteOffset / 2/*SIZE*/
    val MEM_SIZE = size / 2/*SIZE*/
    fun getByteIndex(index: Int) = byteOffset + index * 2/*SIZE*/
}
actual val Int16Buffer.mem: MemBuffer get() = mbuffer
actual val Int16Buffer.offset: Int get() = MEM_OFFSET
actual val Int16Buffer.size: Int get() = MEM_SIZE
actual operator fun Int16Buffer.get(index: Int): Short = mbuffer.getShort(getByteIndex(index))
actual operator fun Int16Buffer.set(index: Int, value: Short): Unit = mbuffer.setShort(getByteIndex(index), value)

actual class Int32Buffer(val mbuffer: MemBuffer, val byteOffset: Int, val size: Int) {
    //companion object { const val SIZE = 4 }
    val MEM_OFFSET = byteOffset / 4/*SIZE*/
    val MEM_SIZE = size / 4/*SIZE*/
    fun getByteIndex(index: Int) = byteOffset + index * 4/*SIZE*/
}
actual val Int32Buffer.mem: MemBuffer get() = mbuffer
actual val Int32Buffer.offset: Int get() = MEM_OFFSET
actual val Int32Buffer.size: Int get() = MEM_SIZE
actual operator fun Int32Buffer.get(index: Int): Int = mbuffer.getInt(getByteIndex(index))
actual operator fun Int32Buffer.set(index: Int, value: Int): Unit = mbuffer.setInt(getByteIndex(index), value)

actual class Float32Buffer(val mbuffer: MemBuffer, val byteOffset: Int, val size: Int) {
    //companion object { const val SIZE = 4 }
    val MEM_OFFSET = byteOffset / 4/*SIZE*/
    val MEM_SIZE = size / 4/*SIZE*/
    fun getByteIndex(index: Int) = byteOffset + index * 4/*SIZE*/
}
actual val Float32Buffer.mem: MemBuffer get() = mbuffer
actual val Float32Buffer.offset: Int get() = MEM_OFFSET
actual val Float32Buffer.size: Int get() = MEM_SIZE
actual operator fun Float32Buffer.get(index: Int): Float = mbuffer.getFloat(getByteIndex(index))
actual operator fun Float32Buffer.set(index: Int, value: Float): Unit = mbuffer.setFloat(getByteIndex(index), value)

actual class Float64Buffer(val mbuffer: MemBuffer, val byteOffset: Int, val size: Int) {
    //companion object { const val SIZE = 8 }
    val MEM_OFFSET = byteOffset / 8/*SIZE*/
    val MEM_SIZE = size / 8/*SIZE*/
    fun getByteIndex(index: Int) = byteOffset + index * 8/*SIZE*/
}
actual val Float64Buffer.mem: MemBuffer get() = mbuffer
actual val Float64Buffer.offset: Int get() = MEM_OFFSET
actual val Float64Buffer.size: Int get() = MEM_SIZE
actual operator fun Float64Buffer.get(index: Int): Double = mbuffer.getDouble(getByteIndex(index))
actual operator fun Float64Buffer.set(index: Int, value: Double): Unit = mbuffer.setDouble(getByteIndex(index), value)

// @TODO: Can we do this without pins that are data classes? Since GC is not going to be executed inside memmove?
inline fun <T : Any, R : Any> anyArrayCopy(input: T, inputAddress: (Pinned<T>) -> CPointer<*>?, output: R, outputAddress: (Pinned<R>) -> CPointer<*>?, sizeBytes: Int) {
    if (sizeBytes == 0) return
    output.usePinned { out ->
        input.usePinned { inp ->
            val outp = outputAddress(out)
            val inpp = inputAddress(inp)
            memmove(outp, inpp, sizeBytes.convert())
        }
    }
}

actual fun arraycopy(src: MemBuffer, srcPos: Int, dst: MemBuffer, dstPos: Int, size: Int): Unit {
    anyArrayCopy(src.data, { it.addressOf(srcPos) }, dst.data, { it.addressOf(dstPos) }, size * Byte.SIZE_BYTES)
}
actual fun arraycopy(src: ByteArray, srcPos: Int, dst: MemBuffer, dstPos: Int, size: Int): Unit {
    anyArrayCopy(src, { it.addressOf(srcPos) }, dst.data, { it.addressOf(dstPos) }, size * Byte.SIZE_BYTES)
}
actual fun arraycopy(src: MemBuffer, srcPos: Int, dst: ByteArray, dstPos: Int, size: Int): Unit {
    anyArrayCopy(src.data, { it.addressOf(srcPos) }, dst, { it.addressOf(dstPos * Byte.SIZE_BYTES) }, size * Byte.SIZE_BYTES)
}
actual fun arraycopy(src: ShortArray, srcPos: Int, dst: MemBuffer, dstPos: Int, size: Int): Unit {
    anyArrayCopy(src, { it.addressOf(srcPos) }, dst.data, { it.addressOf(dstPos * Short.SIZE_BYTES) }, size * Short.SIZE_BYTES)
}
actual fun arraycopy(src: IntArray, srcPos: Int, dst: MemBuffer, dstPos: Int, size: Int): Unit {
    anyArrayCopy(src, { it.addressOf(srcPos) }, dst.data, { it.addressOf(dstPos * Int.SIZE_BYTES) }, size * Int.SIZE_BYTES)
}
actual fun arraycopy(src: FloatArray, srcPos: Int, dst: MemBuffer, dstPos: Int, size: Int): Unit {
    anyArrayCopy(src, { it.addressOf(srcPos) }, dst.data, { it.addressOf(dstPos * Float.SIZE_BYTES) }, size * Float.SIZE_BYTES)
}
actual fun arraycopy(src: DoubleArray, srcPos: Int, dst: MemBuffer, dstPos: Int, size: Int): Unit {
    anyArrayCopy(src, { it.addressOf(srcPos) }, dst.data, { it.addressOf(dstPos * Double.SIZE_BYTES) }, size * Double.SIZE_BYTES)
}
actual fun arraycopy(src: MemBuffer, srcPos: Int, dst: ShortArray, dstPos: Int, size: Int): Unit {
    anyArrayCopy(src.data, { it.addressOf(srcPos * Short.SIZE_BYTES) }, dst, { it.addressOf(dstPos) }, size * Short.SIZE_BYTES)
}
actual fun arraycopy(src: MemBuffer, srcPos: Int, dst: IntArray, dstPos: Int, size: Int): Unit {
    anyArrayCopy(src.data, { it.addressOf(srcPos * Int.SIZE_BYTES) }, dst, { it.addressOf(dstPos) }, size * Int.SIZE_BYTES)
}
actual fun arraycopy(src: MemBuffer, srcPos: Int, dst: FloatArray, dstPos: Int, size: Int): Unit {
    anyArrayCopy(src.data, { it.addressOf(srcPos * Float.SIZE_BYTES) }, dst, { it.addressOf(dstPos) }, size * Float.SIZE_BYTES)
}
actual fun arraycopy(src: MemBuffer, srcPos: Int, dst: DoubleArray, dstPos: Int, size: Int): Unit {
    anyArrayCopy(src.data, { it.addressOf(srcPos * Double.SIZE_BYTES) }, dst, { it.addressOf(dstPos) }, size * Double.SIZE_BYTES)
}

actual abstract class Fast32Buffer(val bb: ByteArray)
//actual /*inline*/ class Fast32Buffer(val bb: ByteArray)
class Fast32BufferF(bb: ByteArray) : Fast32Buffer(bb)

actual fun NewFast32Buffer(mem: MemBuffer): Fast32Buffer = Fast32BufferF(mem.data)

actual val Fast32Buffer.length: Int get() = this.bb.size * 4
actual inline fun Fast32Buffer.getF(index: Int): Float = this.bb.getFloatAt(index * 4)
actual inline fun Fast32Buffer.setF(index: Int, value: Float) { this.bb.setFloatAt(index * 4, value) }
actual inline fun Fast32Buffer.getI(index: Int): Int = this.bb.getIntAt(index * 4)
actual inline fun Fast32Buffer.setI(index: Int, value: Int) { this.bb.setIntAt(index * 4, value) }
