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

import java.util.Collection;
import java.util.Comparator;

import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;

/**
 * Decorator for {@link MemoryCacheAware}. Provides special feature for cache: some different keys are considered as
 * equals (using {@link Comparator comparator}). And when you try to put some value into cache by key so entries with
 * "equals" keys will be removed from cache before.<br />
 * <b>NOTE:</b> Used for internal needs. Normally you don't need to use this class.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class FuzzyKeyMemoryCache<K, V> implements MemoryCacheAware<K, V> {

	private final MemoryCacheAware<K, V> cache;
	private final Comparator<K> keyComparator;

	public FuzzyKeyMemoryCache(MemoryCacheAware<K, V> cache, Comparator<K> keyComparator) {
		this.cache = cache;
		this.keyComparator = keyComparator;
	}

	@Override
	public boolean put(K key, V value) {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.put(K,V)",this,key,value);try{/*// Search equal key and remove this entry*/
		synchronized (cache) {
			K keyToRemove = null;
			for (K cacheKey : cache.keys()) {
				if (keyComparator.compare(key, cacheKey) == 0) {
					keyToRemove = cacheKey;
					break;
				}
			}
			if (keyToRemove != null) {
				cache.remove(keyToRemove);
			}
		}
		{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.put(K,V)",this);return cache.put(key, value);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.put(K,V)",this,throwable);throw throwable;}
	}

	@Override
	public V get(K key) {
		com.mijack.Xlog.logMethodEnter("V com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.get(K)",this,key);try{com.mijack.Xlog.logMethodExit("V com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.get(K)",this);return cache.get(key);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("V com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.get(K)",this,throwable);throw throwable;}
	}

	@Override
	public void remove(K key) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.remove(K)",this,key);try{cache.remove(key);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.remove(K)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.remove(K)",this,throwable);throw throwable;}
	}

	@Override
	public void clear() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.clear()",this);try{cache.clear();com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.clear()",this,throwable);throw throwable;}
	}

	@Override
	public Collection<K> keys() {
		com.mijack.Xlog.logMethodEnter("java.util.Collection com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.keys()",this);try{com.mijack.Xlog.logMethodExit("java.util.Collection com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.keys()",this);return cache.keys();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.Collection com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache<K, V>.keys()",this,throwable);throw throwable;}
	}
}
