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

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Base memory cache. Implements common functionality for memory cache. Provides object references (
 * {@linkplain Reference not strong}) storing.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public abstract class BaseMemoryCache<K, V> implements MemoryCacheAware<K, V> {

	/** Stores not strong references to objects */
	private final Map<K, Reference<V>> softMap = Collections.synchronizedMap(new HashMap<K, Reference<V>>());

	@Override
	public V get(K key) {
		com.mijack.Xlog.logMethodEnter("V com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.get(K)",this,key);try{V result = null;
		Reference<V> reference = softMap.get(key);
		if (reference != null) {
			result = reference.get();
		}
		{com.mijack.Xlog.logMethodExit("V com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.get(K)",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("V com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.get(K)",this,throwable);throw throwable;}
	}

	@Override
	public boolean put(K key, V value) {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.put(K,V)",this,key,value);try{softMap.put(key, createReference(value));
		{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.put(K,V)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.put(K,V)",this,throwable);throw throwable;}
	}

	@Override
	public void remove(K key) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.remove(K)",this,key);try{softMap.remove(key);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.remove(K)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.remove(K)",this,throwable);throw throwable;}
	}

	@Override
	public Collection<K> keys() {
		com.mijack.Xlog.logMethodEnter("java.util.Collection com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.keys()",this);try{com.mijack.Xlog.logMethodExit("java.util.Collection com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.keys()",this);synchronized (softMap) {
			return new HashSet<K>(softMap.keySet());
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.Collection com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.keys()",this,throwable);throw throwable;}
	}

	@Override
	public void clear() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.clear()",this);try{softMap.clear();com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.BaseMemoryCache<K, V>.clear()",this,throwable);throw throwable;}
	}

	/** Creates {@linkplain Reference not strong} reference of value */
	protected abstract Reference<V> createReference(V value);
}
