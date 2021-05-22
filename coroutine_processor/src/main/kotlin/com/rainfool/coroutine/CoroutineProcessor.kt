package com.rainfool.coroutine

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 *
 * @author rainfool
 */
class CoroutineProcessor : AbstractProcessor() {

    companion object {
        // 生成帮助类文件尾缀
        const val COROUTINE_CLASS_SUFFIX = "Coroutine"
    }

    private lateinit var filer: Filer

    override fun init(env: ProcessingEnvironment) {
        super.init(env)
        filer = env.filer
        Log.env = env
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
                CoroutineMethod::class.java.canonicalName,
                CallbackTransformer::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        if (annotations == null) return false
        if (roundEnv == null) return false
        val callbackCollector = CallbackCollector()

        val callbackClassElementSet = roundEnv.getElementsAnnotatedWith(CallbackTransformer::class.java)
        callbackCollector.process(callbackClassElementSet)

        val methodElementSet: Set<Element> = roundEnv.getElementsAnnotatedWith(CoroutineMethod::class.java)
        CallbackMethodGenerator(callbackCollector).process(methodElementSet, filer)
        return false
    }

}
