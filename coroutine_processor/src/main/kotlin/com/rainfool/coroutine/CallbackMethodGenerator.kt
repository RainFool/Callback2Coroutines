package com.rainfool.coroutine

import com.google.auto.common.MoreElements
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.lang.model.type.DeclaredType
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

/**
 *
 * @author rainfool
 */
class CallbackMethodGenerator(private val callbackCollector: CallbackCollector) {

    companion object {
        private val suspendCoroutineClassName = MemberName("kotlin.coroutines", "suspendCoroutine")
        private val coroutineResultClassName = ClassName("com.rainfool.coroutine", "CoroutineResult")
        private val resumeClassName = ClassName("kotlin.coroutines", "resume")
        private val resumeWithExceptionClassName = ClassName("kotlin.coroutines", "resumeWithException")
        val resultClassName = ClassName("com.rainfool.coroutine", "CoroutineResult")

    }

    fun process(elementSet: Set<Element>, filer: Filer) {
        val methodDesMap = mutableMapOf<TypeElement, CallbackMethodDes>()
        fillMethodDesMap(elementSet, methodDesMap)
        writeFile(methodDesMap, filer)
    }

    private fun fillMethodDesMap(elementSet: Set<Element>, methodDesMap: MutableMap<TypeElement, CallbackMethodDes>) {
        elementSet.forEach { element ->
            if (element !is ExecutableElement) return@forEach
            if (element.enclosingElement !is TypeElement) return@forEach
            val enclosingElement = element.enclosingElement as TypeElement
            val methodDes = methodDesMap.getOrPut(enclosingElement) {
                CallbackMethodDes(enclosingElement)
            }
            if (!element.modifiers.contains(Modifier.PUBLIC)) {
                return@forEach
            }
            if (element.modifiers.contains(Modifier.STATIC)) {
                methodDes.staticMethodList.add(element)
            } else {
                methodDes.normalMethodList.add(element)
            }
        }
    }

    private fun writeFile(methodDesMap: MutableMap<TypeElement, CallbackMethodDes>, filer: Filer) {
        methodDesMap.values.forEach { methodDes ->
            val className = getClassName(methodDes.enclosingElement)
            val file = FileSpec.builder(className.packageName, className.simpleName)
            if (methodDes.staticMethodList.isNotEmpty()) {
                val objectBuilder = obtainObjectBuilder(className, methodDes)
                file.addType(objectBuilder.build())
            }
            methodDes.normalMethodList.forEach {
                val extensionBuilder = obtainExtensionBuilder(className, it)
                file.addFunction(extensionBuilder.build())
            }
            file.build().writeTo(filer)
        }

    }

    private fun obtainExtensionBuilder(className: ClassName, normalMethod: ExecutableElement): FunSpec.Builder {
        val methodName = normalMethod.simpleName.toString()
        val hostTypeElement = normalMethod.enclosingElement as TypeElement
        val funBuilder = FunSpec.builder(methodName)
                .receiver(hostTypeElement.asType().asTypeName())
        brewMethodStatement(funBuilder, normalMethod)
        return funBuilder
    }

    /**
     * todo VariableElement asType is not safe, consider metadata api
     */
    private fun obtainObjectBuilder(className: ClassName, methodDes: CallbackMethodDes): TypeSpec.Builder {
        val objectBuilder = TypeSpec.objectBuilder(className.simpleName)
        methodDes.staticMethodList.forEach { normalMethod ->
            val funBuilder = buildForStaticMethod(normalMethod)
            objectBuilder.addFunction(funBuilder.build())
        }
        return objectBuilder
    }

    private fun buildForStaticMethod(staticMethod: ExecutableElement): FunSpec.Builder {
        val funBuilder = FunSpec.builder(staticMethod.simpleName.toString())
        brewMethodStatement(funBuilder, staticMethod)
        return funBuilder
    }

    private fun brewMethodStatement(funBuilder: FunSpec.Builder, method: ExecutableElement) {
        funBuilder.addModifiers(KModifier.SUSPEND)
        // callback参数
        var callbackParam: VariableElement? = null
        method.parameters.forEach {
            if (callbackCollector.isCallback(it)) {
                // skip callback
                callbackParam = it
            } else {
                funBuilder.addParameter(it.simpleName.toString(), it.asType().asTypeName().javaToKotlinType())
            }
        }
        if (callbackParam == null) {
            Log.error(method, "this method has not callback or callback transformer?")
        }
        val callbackType = callbackParam!!.asType()
        // 数据泛型
        val resultType = (callbackType as DeclaredType).typeArguments[0]
        val resultOfDataClass = resultType.asTypeName().javaToKotlinType()
        funBuilder.returns(coroutineResultClassName.parameterizedBy(resultOfDataClass))
        funBuilder.beginControlFlow("return %M<%T<%T>>", suspendCoroutineClassName, coroutineResultClassName, resultOfDataClass)

        val transformer = callbackCollector.getTransformer(callbackType)
        val transformerMember = MemberName(transformer.enclosingElement.toString(), transformer.simpleName.toString())
        funBuilder.addStatement("val callback = %M(it)", transformerMember)

        val staticMethodMember = MemberName(method.enclosingElement.toString(), method.simpleName.toString())
        val paramStatement = method.parameters.joinToString(prefix = "(", postfix = ")") {
            if (callbackCollector.isCallback(it)) {
                "callback"
            } else {
                it.simpleName
            }
        }
        if (method.modifiers.contains(Modifier.STATIC)) {
            funBuilder.addStatement("%M%L", staticMethodMember, paramStatement)
        } else {
            funBuilder.addStatement("this.%L%L", staticMethodMember.simpleName, paramStatement)
        }
        funBuilder.endControlFlow()
    }


    private fun getClassName(typeElement: TypeElement): ClassName {
        val packageName: String = MoreElements.getPackage(typeElement).qualifiedName.toString()
        val className = typeElement.qualifiedName.toString().substring(
                packageName.length + 1).replace('.', '$')
        return ClassName(packageName, className + CoroutineProcessor.COROUTINE_CLASS_SUFFIX)
    }

}

data class CallbackMethodDes(val enclosingElement: TypeElement) {

    val staticMethodList = mutableListOf<ExecutableElement>()
    val normalMethodList = mutableListOf<ExecutableElement>()
}

fun TypeName.javaToKotlinType(): TypeName {
    return when (this) {
        is ParameterizedTypeName -> {
            (rawType.javaToKotlinType() as ClassName).parameterizedBy(*(typeArguments.map { it.javaToKotlinType() }.toTypedArray()))
        }
        is WildcardTypeName -> {
            outTypes[0].javaToKotlinType()
        }
        else -> {
            val className = JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(toString()))?.asSingleFqName()?.asString()
            return if (className == null) {
                this
            } else {
                ClassName.bestGuess(className)
            }
        }
    }
}