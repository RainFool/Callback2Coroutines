package com.rainfool.coroutine

import javax.lang.model.type.TypeMirror

/**
 *
 * @author rainfool
 */
object GenericHelper {

    fun isTypeEqualsIgnoreGenerics(type1: TypeMirror, type2: TypeMirror): Boolean {
        val name1 = type1.toString().substringBefore('<')
        val name2 = type2.toString().substringBefore('<')
        return name1 == name2
    }

    fun getTypeGeneric(type: TypeMirror?): String {
        if (type == null) {
            return ""
        }
        val name = type.toString()
        if (!name.contains('<') || !name.contains('>')) {
            return ""
        }
        val start = name.indexOf('<')
        val end = name.lastIndexOf('>')
        return name.substring(start + 1, end)
    }
}