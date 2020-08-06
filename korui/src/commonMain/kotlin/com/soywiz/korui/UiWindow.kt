package com.soywiz.korui

import com.soywiz.korev.*
import com.soywiz.korio.lang.*
import com.soywiz.korio.util.*
import com.soywiz.korui.native.*

open class UiWindow(app: UiApplication, val window: NativeUiFactory.NativeWindow = app.factory.createWindow()) : UiContainer(app, window) {
    var title by redirect(window::title)
    var menu by redirect(window::menu)
    val pixelFactory by redirect(window::pixelFactor)
    fun onResize(handler: (ReshapeEvent) -> Unit): Disposable = window.onResize(handler)
}