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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache;

import android.graphics.Bitmap;

/**
 * Limited {@link Bitmap bitmap} cache. Provides {@link Bitmap bitmaps} storing. Size of all stored bitmaps will not to
 * exceed size limit. When cache reaches limit size then the bitmap which used the least frequently is deleted from
 * cache.<br />
 * <br />
 * <b>NOTE:</b> This cache uses strong and weak references for stored Bitmaps. Strong references - for limited count of
 * Bitmaps (depends on cache size), weak references - for all other cached Bitmaps.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class LargestLimitedMemoryCache extends LimitedMemoryCache<String, Bitmap> {
	/**
	 * Contains strong references to stored objects (keys) and last object usage date (in milliseconds). If hard cache
	 * size will exceed limit then object with the least frequently usage is deleted (but it continue exist at
	 * {@link #softMap} and can be collected by GC at any time)
	 */
	private final Map<Bitmap, Integer> valueSizes = Collections.synchronizedMap(new HashMap<Bitmap, Integer>());

	public LargestLimitedMemoryCache(int sizeLimit) {
		super(sizeLimit);
	}

	@Override
	public boolean put(String key, Bitmap value) {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.put(java.lang.String,android.graphics.Bitmap)",this,key,value);try{if (super.put(key, value)) {
			valueSizes.put(value, getSize(value));
			{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.put(java.lang.String,android.graphics.Bitmap)",this);return true;}
		} else {
			{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.put(java.lang.String,android.graphics.Bitmap)",this);return false;}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.put(java.lang.String,android.graphics.Bitmap)",this,throwable);throw throwable;}
	}

	@Override
	public void remove(String key) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.remove(java.lang.String)",this,key);try{Bitmap value = super.get(key);
		if (value != null) {
			valueSizes.remove(value);
		}
		super.remove(key);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.remove(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.remove(java.lang.String)",this,throwable);throw throwable;}
	}

	@Override
	public void clear() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.clear()",this);try{valueSizes.clear();
		super.clear();com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.clear()",this,throwable);throw throwable;}
	}

	@Override
	protected int getSize(Bitmap value) {
		com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.getSize(android.graphics.Bitmap)",this,value);try{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.getSize(android.graphics.Bitmap)",this);return value.getRowBytes() * value.getHeight();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.getSize(android.graphics.Bitmap)",this,throwable);throw throwable;}
	}

	@Override
	protected Bitmap removeNext() {
		com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.removeNext()",this);try{Integer maxSize = null;
		Bitmap largestValue = null;
		Set<Entry<Bitmap, Integer>> entries = valueSizes.entrySet();
		synchronized (valueSizes) {
			for (Entry<Bitmap, Integer> entry : entries) {
				if (largestValue == null) {
					largestValue = entry.getKey();
					maxSize = entry.getValue();
				} else {
					Integer size = entry.getValue();
					if (size > maxSize) {
						maxSize = size;
						largestValue = entry.getKey();
					}
				}
			}
		}
		valueSizes.remove(largestValue);
		{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.removeNext()",this);return largestValue;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.removeNext()",this,throwable);throw throwable;}
	}

	@Override
	protected Reference<Bitmap> createReference(Bitmap value) {
		com.mijack.Xlog.logMethodEnter("java.lang.ref.Reference com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.createReference(android.graphics.Bitmap)",this,value);try{com.mijack.Xlog.logMethodExit("java.lang.ref.Reference com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.createReference(android.graphics.Bitmap)",this);return new WeakReference<Bitmap>(value);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.ref.Reference com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache.createReference(android.graphics.Bitmap)",this,throwable);throw throwable;}
	}
}
