package com.rainfool.coroutines.app;

/**
 * @author krystian
 */
public interface IResultListener<T> {

    void onSuccess(T obj);

    void onError(int errorCode, String errorMsg);
}
