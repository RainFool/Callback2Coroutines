package com.rainfool.coroutine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个transformer，把协程转成callback，这样框架内部可以直接用生成的协程内去调用已有带有callback的接口
 *
 * @author rainfool
 */
@Retention(RetentionPolicy.CLASS)
@Target(value = {ElementType.METHOD})
public @interface CallbackTransformer {
}
