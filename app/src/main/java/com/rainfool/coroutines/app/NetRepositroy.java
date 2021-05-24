package com.rainfool.coroutines.app;

import com.rainfool.coroutine.CoroutineMethod;

/**
 * 测试模拟网络请求的代码
 *
 * @author krystian
 */
public class NetRepositroy {

    /**
     * 模拟static方法异步请求
     */
    @CoroutineMethod
    public static void callAsyncStatic(IResultListener<String> listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listener.onSuccess("Async success");
            }
        }).start();
    }

    /**
     * 模拟static异步错误请求
     */
    @CoroutineMethod
    public static void callAsyncStaticError(IResultListener<String> listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listener.onError(-100, "Async error");
            }
        }).start();
    }

    @CoroutineMethod
    public void callAsync(IResultListener<String> listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listener.onSuccess("Async success");
            }
        }).start();
    }

    @CoroutineMethod
    public void callAsyncError(IResultListener<String> listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listener.onError(-100, "Async error");
            }
        }).start();
    }

}
