/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.cache.memory.impl;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache;

/**
 * Limited {@link Bitmap bitmap} cache. Provides {@link Bitmap bitmaps} storing. Size of all stored bitmaps will not to
 * exceed size limit. When cache reaches limit size then the least recently used bitmap is deleted from cache.<br />
 * <br />
 * <b>NOTE:</b> This cache uses strong and weak references for stored Bitmaps. Strong references - for limited count of
 * Bitmaps (depends on cache size), weak references - for all other cached Bitmaps.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.3.0
 */
public class LRULimitedMemoryCache extends LimitedMemoryCache<String, Bitmap> {

	private static final int INITIAL_CAPACITY = 10;
	private static final float LOAD_FACTOR = 1.1f;

	/** Cache providing Least-Recently-Used logic */
	private final Map<String, Bitmap> lruCache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(INITIAL_CAPACITY, LOAD_FACTOR, true));

	/**
	 * @param maxSize Maximum sum of the sizes of the Bitmaps in this cache
	 */
	public LRULimitedMemoryCache(int maxSize) {
		super(maxSize);
	}

	@Override
	public boolean put(String key, Bitmap value) {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.put(java.lang.String,android.graphics.Bitmap)",this,key,value);try{if (super.put(key, value)) {
			lruCache.put(key, value);
			{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.put(java.lang.String,android.graphics.Bitmap)",this);return true;}
		} else {
			{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.put(java.lang.String,android.graphics.Bitmap)",this);return false;}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.put(java.lang.String,android.graphics.Bitmap)",this,throwable);throw throwable;}
	}

	@Override
	public Bitmap get(String key) {
		com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.get(java.lang.String)",this,key);try{lruCache.get(key); /*// call "get" for LRU logic*/
		{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.get(java.lang.String)",this);return super.get(key);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.get(java.lang.String)",this,throwable);throw throwable;}
	}

	@Override
	public void remove(String key) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.remove(java.lang.String)",this,key);try{lruCache.remove(key);
		super.remove(key);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.remove(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.remove(java.lang.String)",this,throwable);throw throwable;}
	}

	@Override
	public void clear() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.clear()",this);try{lruCache.clear();
		super.clear();com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.clear()",this,throwable);throw throwable;}
	}

	@Override
	protected int getSize(Bitmap value) {
		com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.getSize(android.graphics.Bitmap)",this,value);try{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.getSize(android.graphics.Bitmap)",this);return value.getRowBytes() * value.getHeight();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.getSize(android.graphics.Bitmap)",this,throwable);throw throwable;}
	}

	@Override
	protected Bitmap removeNext() {
		com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.removeNext()",this);try{Bitmap mostLongUsedValue = null;
		synchronized (lruCache) {
			Iterator<Entry<String, Bitmap>> it = lruCache.entrySet().iterator();
			if (it.hasNext()) {
				Entry<String, Bitmap> entry = it.next();
				mostLongUsedValue = entry.getValue();
				it.remove();
			}
		}
		{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.removeNext()",this);return mostLongUsedValue;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.removeNext()",this,throwable);throw throwable;}
	}

	@Override
	protected Reference<Bitmap> createReference(Bitmap value) {
		com.mijack.Xlog.logMethodEnter("java.lang.ref.Reference com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.createReference(android.graphics.Bitmap)",this,value);try{com.mijack.Xlog.logMethodExit("java.lang.ref.Reference com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.createReference(android.graphics.Bitmap)",this);return new WeakReference<Bitmap>(value);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.ref.Reference com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache.createReference(android.graphics.Bitmap)",this,throwable);throw throwable;}
	}
}
