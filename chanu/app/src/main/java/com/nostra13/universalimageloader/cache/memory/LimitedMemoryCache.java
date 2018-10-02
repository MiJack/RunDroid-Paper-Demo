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
package com.nostra13.universalimageloader.cache.memory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.nostra13.universalimageloader.utils.L;

/**
 * Limited cache. Provides object storing. Size of all stored bitmaps will not to exceed size limit (
 * {@link #getSizeLimit()}).<br />
 * <br />
 * <b>NOTE:</b> This cache uses strong and weak references for stored Bitmaps. Strong references - for limited count of
 * Bitmaps (depends on cache size), weak references - for all other cached Bitmaps.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 * @see BaseMemoryCache
 */
public abstract class LimitedMemoryCache<K, V> extends BaseMemoryCache<K, V> {

	private static final int MAX_NORMAL_CACHE_SIZE_IN_MB = 16;
	private static final int MAX_NORMAL_CACHE_SIZE = MAX_NORMAL_CACHE_SIZE_IN_MB * 1024 * 1024;

	private final int sizeLimit;

	private final AtomicInteger cacheSize;

	/**
	 * Contains strong references to stored objects. Each next object is added last. If hard cache size will exceed
	 * limit then first object is deleted (but it continue exist at {@link #softMap} and can be collected by GC at any
	 * time)
	 */
	private final List<V> hardCache = Collections.synchronizedList(new LinkedList<V>());

	/**
	 * @param sizeLimit Maximum size for cache (in bytes)
	 */
	public LimitedMemoryCache(int sizeLimit) {
		this.sizeLimit = sizeLimit;
		cacheSize = new AtomicInteger();
		if (sizeLimit > MAX_NORMAL_CACHE_SIZE) {
			L.w("You set too large memory cache size (more than %1$d Mb)", MAX_NORMAL_CACHE_SIZE_IN_MB);
		}
	}

	@Override
	public boolean put(K key, V value) {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.put(K,V)",this,key,value);try{boolean putSuccessfully = false;
		/*// Try to add value to hard cache*/
		int valueSize = getSize(value);
		int sizeLimit = getSizeLimit();
		int curCacheSize = cacheSize.get();
		if (valueSize < sizeLimit) {
			while (curCacheSize + valueSize > sizeLimit) {
				V removedValue = removeNext();
				if (hardCache.remove(removedValue)) {
					curCacheSize = cacheSize.addAndGet(-getSize(removedValue));
				}
			}
			hardCache.add(value);
			cacheSize.addAndGet(valueSize);

			putSuccessfully = true;
		}
		/*// Add value to soft cache*/
		super.put(key, value);
		{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.put(K,V)",this);return putSuccessfully;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.put(K,V)",this,throwable);throw throwable;}
	}

	@Override
	public void remove(K key) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.remove(K)",this,key);try{V value = super.get(key);
		if (value != null) {
			if (hardCache.remove(value)) {
				cacheSize.addAndGet(-getSize(value));
			}
		}
		super.remove(key);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.remove(K)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.remove(K)",this,throwable);throw throwable;}
	}

	@Override
	public void clear() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.clear()",this);try{hardCache.clear();
		cacheSize.set(0);
		super.clear();com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.clear()",this,throwable);throw throwable;}
	}

	protected int getSizeLimit() {
		com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.getSizeLimit()",this);try{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.getSizeLimit()",this);return sizeLimit;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache<K, V>.getSizeLimit()",this,throwable);throw throwable;}
	}

	protected abstract int getSize(V value);

	protected abstract V removeNext();
}
