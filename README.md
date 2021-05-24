# Callback2Coroutines
Transform Java callback to coroutines suspend method.  
将传统Java callback 方法转换为kotlin中的suspend方法。

## 接入指南
在app的build.gradle中加入依赖，release_version为最新release版本。
```
    implementation 'com.github.RainFool.Callback2Coroutines:coroutines_annotation:release_version'
    kapt 'com.github.RainFool.Callback2Coroutines:coroutines_processor:release_version'
```
需要先使用`CallbackTransformer`注解标记一个转换器，作用是将协程和callback连接，比如：
```
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
```
最后，在含有callback的方法上标记`@CoroutineMethod`即可，编译完模块后会自动生成含有协程方法的类。

## 原理简述

在维护老项目时，经常遇到使用callback进行回调，比如下列代码，在MyClass中声明了一个静态方法和一个普通方法：
```
// 静态方法
public static void testFunc1(ICallback<String> callback) {}

// 普通方法
public void testFunc2(ICallback<Param> callback) {}
```
而多重的嵌套会让代码十分不友好。

本项目可以通过一个简单的注解`@CoroutineMethod`将上述两种，callback在最后一个参数的Java代码转换为kotlin的suspend方法：
```
// 生成的协程代码
object MyClassCoroutine {
  suspend fun testStaticFunc(p1: List<Int>, p2: List<Param>): CoroutineResult<Param> =
      suspendCoroutine<CoroutineResult<Param>> {
    val callback = callbackTransformer(it)
    testStaticFunc(p1, p2, callback)
  }

  suspend fun testFunc1(): CoroutineResult<String> = suspendCoroutine<CoroutineResult<String>> {
    val callback = callbackTransformer(it)
    testFunc1(callback)
  }
}

suspend fun MyClass.testFunc2(): CoroutineResult<Param> = suspendCoroutine<CoroutineResult<Param>> {
  val callback = callbackTransformer(it)
  this.testFunc2(callback)
}

```
其中，静态方法将会生成一个新类，已原始类名+Coroutine命名，普通方法将生成一个kotlin扩展方法。

随后就可以愉快的进行subspend方法调用了：
```
    val data = async { MyClassCoroutine.testFunc1() }
    
    val data = MyClass.testFunc2()
```
