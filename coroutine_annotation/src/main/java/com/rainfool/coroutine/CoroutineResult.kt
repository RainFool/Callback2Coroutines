package com.rainfool.coroutine

/**
 * callback转换为协程后的返回值
 * @author rainfool
 */
data class CoroutineResult<T>(val isSuccess: Boolean,
                              val data: T? = null,
                              val errorCode: Int = 0,
                              val errorMsg: String = "")