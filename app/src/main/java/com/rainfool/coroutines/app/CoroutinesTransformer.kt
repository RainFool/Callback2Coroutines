package com.tencent.cymini

import com.rainfool.coroutine.CallbackTransformer
import com.rainfool.coroutine.CoroutineResult
import com.rainfool.coroutines.app.IResultListener
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume


/**
 * 默认的转换器
 * @author rainfool
 */
object CoroutinesTransformer {
    @CallbackTransformer
    fun <T> defaultTransformer(it: Continuation<CoroutineResult<T>>): IResultListener<T> {
        return object : IResultListener<T> {
            override fun onSuccess(result: T) {
                it.resume(CoroutineResult(true, result))
            }

            override fun onError(errorCode: Int, errorMessage: String?) {
                it.resume(CoroutineResult<T>(false, null, errorCode, ""))
            }

        }
    }
}