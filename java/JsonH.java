/*
 * Copyright (C) 2011 by Andrea Giammarchi, @WebReflection
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright ntice and this permission ntice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * json homogeneous collections compressor
 *
 * dependencies: org.json:json
 */
public class JsonH {

    public static String pack(String raw) {
        if (raw == null) return "[]";

        JSONArray originJson = new JSONArray(raw);
        int length = originJson.length();
        if (length == 0) return "[]";

        List<String> keys = new ArrayList<>();
        Iterator<String> iterator = originJson.getJSONObject(0).keys();
        while (iterator.hasNext()) keys.add(iterator.next());
        Collections.sort(keys);

        JSONArray result = new JSONArray();
        final JSONArray keyArray = new JSONArray();
        keys.forEach(keyArray::put);
        result.put(keyArray);

        for (int i = 0; i < length; i++) {
            JSONObject o = originJson.getJSONObject(i);
            JSONArray n = new JSONArray();
            keys.forEach(key -> n.put(o.get(key)));
            result.put(n);
        }

        return result.toString();
    }

    public static String unpack(String encoded) {
        if (encoded == null) return "[]";

        JSONArray originJson = new JSONArray(encoded);
        int length = originJson.length();
        if (length == 0) return "[]";

        JSONArray result = new JSONArray();

        JSONArray keysArray = originJson.getJSONArray(0);
        int keySize = keysArray.length();
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < keySize; i++) keys.add(keysArray.getString(i));
        Collections.sort(keys);

        for (int i = 1; i < length; i++) {
            JSONArray o = originJson.getJSONArray(i);
            int oSize = o.length();
            if (oSize > keySize)
                throw new IllegalStateException("object properties size is larger than keys");
            JSONObject n = new JSONObject();
            for (int j = 0; j < oSize; j++) n.put(keys.get(j), o.get(j));
            result.put(n);
        }

        return result.toString();
    }
}
