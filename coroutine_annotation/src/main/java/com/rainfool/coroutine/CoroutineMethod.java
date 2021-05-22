package com.rainfool.coroutine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记将目标方法转为协程
 *
 * @author rainfool
 */
@Retention(RetentionPolicy.CLASS)
@Target(value = {ElementType.METHOD})
public @interface CoroutineMethod {
}
