package com.rainfool.callback2coroutines;

import com.rainfool.coroutine.CoroutineMethod;

import java.util.List;

public class MyClass {

    public static void testStaticFunc() {
        System.out.println("Method Test");
    }

    //    @CoroutineMethod
    public static void testStaticFunc(int pram1, Param param2, ParamsInner paramsInner) {
        System.out.println("Method Test");
    }

    @CoroutineMethod
    public static ParamsInner testStaticFunc(List<Integer> p1, List<Param> p2, ICallback<Param> callback) {
        return null;
    }

    @CoroutineMethod
    public static void testFunc1(ICallback<String> callback) {
        try {
            Thread.sleep(1000);
            callback.onSuccess("success in test func 1");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError();
        }
    }

    @CoroutineMethod
    public void testFunc2(ICallback<Param> callback) {

    }

    public class ParamsInner {

    }
}