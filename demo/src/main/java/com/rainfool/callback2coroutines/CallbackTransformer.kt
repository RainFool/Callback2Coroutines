package com.rainfool.callback2coroutines;

import com.rainfool.coroutine.CallbackTransformer
import com.rainfool.coroutine.CoroutineResult
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume


/**
 *
 * @author rainfool
 */
object CallbackTransformer {
    @CallbackTransformer
    fun <T> callbackTransformer(it: Continuation<CoroutineResult<T>>): ICallback<T> {
        return object : ICallback<T> {
            override fun onSuccess(t: T) {
                it.resume(CoroutineResult(true, t))
            }

            override fun onError() {
                it.resume(CoroutineResult(false, errorCode = 0))
            }

        }
    }
}
