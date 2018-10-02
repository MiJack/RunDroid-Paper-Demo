/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.common;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An LRU cache which stores recently inserted entries and all entries ever
 * inserted which still has a strong reference elsewhere.
 */
public class LruCache<K, V> {

    private final HashMap<K, V> mLruMap;
    private final HashMap<K, Entry<K, V>> mWeakMap =
            new HashMap<K, Entry<K, V>>();
    private ReferenceQueue<V> mQueue = new ReferenceQueue<V>();

    @SuppressWarnings("serial")
    public LruCache(final int capacity) {
        mLruMap = new LinkedHashMap<K, V>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.common.LruCache<K, V>$1.removeEldestEntry(Map.Entry)",this,eldest);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.common.LruCache<K, V>$1.removeEldestEntry(Map.Entry)",this);return size() > capacity;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.common.LruCache<K, V>$1.removeEldestEntry(Map.Entry)",this,throwable);throw throwable;}
            }
        };
    }

    private static class Entry<K, V> extends WeakReference<V> {
        K mKey;

        public Entry(K key, V value, ReferenceQueue<V> queue) {
            super(value, queue);
            mKey = key;
        }
    }

    @SuppressWarnings("unchecked")
    private void cleanUpWeakMap() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.common.LruCache<K, V>.cleanUpWeakMap()",this);try{Entry<K, V> entry = (Entry<K, V>) mQueue.poll();
        while (entry != null) {
            mWeakMap.remove(entry.mKey);
            entry = (Entry<K, V>) mQueue.poll();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.common.LruCache<K, V>.cleanUpWeakMap()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.common.LruCache<K, V>.cleanUpWeakMap()",this,throwable);throw throwable;}
    }

    public synchronized boolean containsKey(K key) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.common.LruCache<K, V>.containsKey(K)",this,key);try{cleanUpWeakMap();
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.common.LruCache<K, V>.containsKey(K)",this);return mWeakMap.containsKey(key);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.common.LruCache<K, V>.containsKey(K)",this,throwable);throw throwable;}
    }

    public synchronized V put(K key, V value) {
        com.mijack.Xlog.logMethodEnter("V com.android.gallery3d.common.LruCache<K, V>.put(K,V)",this,key,value);try{cleanUpWeakMap();
        mLruMap.put(key, value);
        Entry<K, V> entry = mWeakMap.put(
                key, new Entry<K, V>(key, value, mQueue));
        {com.mijack.Xlog.logMethodExit("V com.android.gallery3d.common.LruCache<K, V>.put(K,V)",this);return entry == null ? null : entry.get();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("V com.android.gallery3d.common.LruCache<K, V>.put(K,V)",this,throwable);throw throwable;}
    }

    public synchronized V get(K key) {
        com.mijack.Xlog.logMethodEnter("V com.android.gallery3d.common.LruCache<K, V>.get(K)",this,key);try{cleanUpWeakMap();
        V value = mLruMap.get(key);
        if (value != null) {{com.mijack.Xlog.logMethodExit("V com.android.gallery3d.common.LruCache<K, V>.get(K)",this);return value;}}
        Entry<K, V> entry = mWeakMap.get(key);
        {com.mijack.Xlog.logMethodExit("V com.android.gallery3d.common.LruCache<K, V>.get(K)",this);return entry == null ? null : entry.get();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("V com.android.gallery3d.common.LruCache<K, V>.get(K)",this,throwable);throw throwable;}
    }

    public synchronized void clear() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.common.LruCache<K, V>.clear()",this);try{mLruMap.clear();
        mWeakMap.clear();
        mQueue = new ReferenceQueue<V>();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.common.LruCache<K, V>.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.common.LruCache<K, V>.clear()",this,throwable);throw throwable;}
    }
}
