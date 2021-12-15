/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package me.shetj.base.ktx

import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.Type



/**
 * list<type>
 */
fun list(type: Type?): Type {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, MutableList::class.java, type)
}

/**
 * set<type>
 */
fun set(type: Type?): Type {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, MutableSet::class.java, type)
}

/**
 * hashMap<type,type2>
 */
fun hashMap(type: Type?, type2: Type?): Type {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, HashMap::class.java, type, type2)
}

/**
 * map<type,type2>
 */
fun map(type: Type?, type2: Type?): Type {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, MutableMap::class.java, type, type2)
}


/**
 * ownerType.rawType<typeArguments>
 */
fun parameterized(ownerType: Type?, rawType: Type?, vararg typeArguments: Type?): Type {
    return `$Gson$Types`.newParameterizedTypeWithOwner(ownerType, rawType, *typeArguments)
}

/**
 * 数组类型
 */
fun array(type: Type?): Type {
    return `$Gson$Types`.arrayOf(type)
}

/**
 * type的子类
 */
fun subtypeOf(type: Type?): Type {
    return `$Gson$Types`.subtypeOf(type)
}


/**
 * type的父类
 */
fun supertypeOf(type: Type?): Type {
    return `$Gson$Types`.supertypeOf(type)
}