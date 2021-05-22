package com.rainfool.callback2coroutines;

/**
 * @author rainfool
 */
public interface ICallback<T> {

    void onSuccess(T t);

    void onError();
}