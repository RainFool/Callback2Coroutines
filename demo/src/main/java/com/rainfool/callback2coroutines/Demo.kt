package com.rainfool.callback2coroutines;

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *
 * @author rainfool
 */
fun main() {
    test()
}

fun test() = runBlocking {
    // 正常，会使用这种办法异步获取数据
    MyClass.testFunc1(object : ICallback<String> {
        override fun onSuccess(t: String?) {
            println("正常callback获取到数据：$t")
        }

        override fun onError() {

        }

    })

    // 通过生成的异步方法获取数据
    val data = async { MyClassCoroutine.testFunc1() }
    println("通过协程获取到数据：${data.await()}")

    // 模拟当前是Android UI线程，必须新起线程去拿数据
    launch(Dispatchers.IO) {
        val dataInIOThread = MyClassCoroutine.testFunc1()
        println("子线程展示数据：$dataInIOThread")
    }
}