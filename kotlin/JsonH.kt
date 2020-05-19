/*
 * Copyright (C) 2011 by Andrea Giammarchi, @WebReflection
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import org.json.JSONArray
import org.json.JSONObject

/**
 * json homogeneous collections compressor
 *
 * dependencies: org.json:json
 */
object JsonH {

    @JvmStatic
    fun pack(raw: String): String {
        val originJson = JSONArray(raw)
        val length = originJson.length()
        if (length == 0) return "[]"

        val keys: List<String> =
            run {
                val list = mutableListOf<String>()
                val iterator = originJson.getJSONObject(0).keys()
                while (iterator.hasNext()) list.add(iterator.next())
                list
            }.apply { sortBy { it } }
        val result = JSONArray()
        val keysArray = JSONArray().apply {
            keys.forEach { put(it) }
        }
        result.put(keysArray)
        for (i in 0 until length) {
            val o = originJson.getJSONObject(i)
            val n = JSONArray()
            keys.forEach {
                n.put(o.get(it))
            }
            result.put(n)
        }
        return result.toString()
    }

    @JvmStatic
    fun unpack(encoded: String): String {
        val originJson = JSONArray(encoded)
        val length = originJson.length()
        if (length == 0) return "[]"
        val result = JSONArray()

        val keysArray = originJson.getJSONArray(0)
        val keySize = keysArray.length()
        val keys = mutableListOf<String>()
        for (i in 0 until keySize)
            keys.add(keysArray.getString(i))

        for (i in 1 until length) {
            val o = originJson.getJSONArray(i)
            val oSize = o.length()
            if (oSize > keySize)
                throw IllegalStateException("object properties size is larger than keys")
            val n = JSONObject()
            for (j in 0 until oSize)
                n.put(keys[j], o[j])
            result.put(n)
        }
        return result.toString()
    }
}