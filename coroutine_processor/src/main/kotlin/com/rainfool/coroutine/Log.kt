package com.rainfool.coroutine

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic

/**
 *
 * @author rainfool
 */
object Log {

    lateinit var env: ProcessingEnvironment

    fun error(message: String) {
        env.messager.printMessage(Diagnostic.Kind.ERROR, message)
    }

    fun error(element: Element, message: String) {
        env.messager.printMessage(Diagnostic.Kind.ERROR, message, element)
    }

    fun note(element: Element, message: String, vararg args: Any) {
        printMessage(Diagnostic.Kind.NOTE, element, message, args)
    }

    private fun printMessage(kind: Diagnostic.Kind, element: Element, message: String, args: Array<out Any>) {
        val msg = if (args.isNotEmpty()) {
            String.format(message, *args)
        } else {
            message
        }
        env.messager.printMessage(kind, msg, element)
    }

}