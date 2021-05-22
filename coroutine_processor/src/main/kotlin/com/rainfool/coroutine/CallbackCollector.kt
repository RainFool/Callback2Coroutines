package com.rainfool.coroutine

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

/**
 * @author rainfool
 */
class CallbackCollector {

    val transformerList = mutableListOf<ExecutableElement>()

    fun process(callbackClassElementSet: MutableSet<out Element>) {
        callbackClassElementSet.forEach {
            val callbackClass = it as ExecutableElement
            transformerList.add(callbackClass)
        }

    }

    fun isCallback(element: VariableElement): Boolean {
        return transformerList.any { GenericHelper.isTypeEqualsIgnoreGenerics(it.returnType, element.asType()) }
    }

    fun getTransformer(type: TypeMirror): ExecutableElement {
        val executableElement = transformerList.find { GenericHelper.isTypeEqualsIgnoreGenerics(it.returnType, type) }
        if (executableElement == null) {
            Log.error("Is transformer not annotation?")
        }
        return executableElement!!
    }

}