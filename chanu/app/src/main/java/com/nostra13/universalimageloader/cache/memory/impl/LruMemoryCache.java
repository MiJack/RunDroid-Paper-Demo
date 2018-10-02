package com.nostra13.universalimageloader.cache.memory.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;

/**
 * A cache that holds strong references to a limited number of Bitmaps. Each time a Bitmap is accessed, it is moved to
 * the head of a queue. When a Bitmap is added to a full cache, the Bitmap at the end of that queue is evicted and may
 * become eligible for garbage collection.<br />
 * <br />
 * <b>NOTE:</b> This cache uses only strong references for stored Bitmaps.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.1
 */
public class LruMemoryCache implements MemoryCacheAware<String, Bitmap> {

	private final LinkedHashMap<String, Bitmap> map;

	private final int maxSize;
	/** Size of this cache in bytes */
	private int size;

	/**
	 * @param maxSize Maximum sum of the sizes of the Bitmaps in this cache
	 */
	public LruMemoryCache(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize <= 0");
		}
		this.maxSize = maxSize;
		this.map = new LinkedHashMap<String, Bitmap>(0, 0.75f, true);
	}

	/**
	 * Returns the Bitmap for {@code key} if it exists in the cache. If a Bitmap was returned, it is moved to the head
	 * of the queue. This returns null if a Bitmap is not cached.
	 */
	@Override
	public final Bitmap get(String key) {
		com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.get(java.lang.String)",this,key);try{if (key == null) {
			throw new NullPointerException("key == null");
		}

		synchronized (this) {
			{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.get(java.lang.String)",this);return map.get(key);}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.get(java.lang.String)",this,throwable);throw throwable;}
	}

	/**
	 * Caches {@code Bitmap} for {@code key}. The Bitmap is moved to the head of the queue.
	 */
	@Override
	public final boolean put(String key, Bitmap value) {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.put(java.lang.String,android.graphics.Bitmap)",this,key,value);try{if (key == null || value == null) {
			throw new NullPointerException("key == null || value == null");
		}

		synchronized (this) {
			size += sizeOf(key, value);
			Bitmap previous = map.put(key, value);
			if (previous != null) {
				size -= sizeOf(key, previous);
			}
		}

		trimToSize(maxSize);
		{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.put(java.lang.String,android.graphics.Bitmap)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.put(java.lang.String,android.graphics.Bitmap)",this,throwable);throw throwable;}
	}

	/**
	 * Remove the eldest entries until the total of remaining entries is at or below the requested size.
	 * 
	 * @param maxSize the maximum size of the cache before returning. May be -1 to evict even 0-sized elements.
	 */
	private void trimToSize(int maxSize) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.trimToSize(int)",this,maxSize);try{com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.trimToSize(int)",this);while (true) {
			String key;
			Bitmap value;
			synchronized (this) {
				if (size < 0 || (map.isEmpty() && size != 0)) {
					throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
				}

				if (size <= maxSize || map.isEmpty()) {
					break;
				}

				Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator().next();
				if (toEvict == null) {
					break;
				}
				key = toEvict.getKey();
				value = toEvict.getValue();
				map.remove(key);
				size -= sizeOf(key, value);
			}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.trimToSize(int)",this,throwable);throw throwable;}
	}

	/** Removes the entry for {@code key} if it exists. */
	@Override
	public final void remove(String key) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.remove(java.lang.String)",this,key);try{if (key == null) {
			throw new NullPointerException("key == null");
		}

		synchronized (this) {
			Bitmap previous = map.remove(key);
			if (previous != null) {
				size -= sizeOf(key, previous);
			}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.remove(java.lang.String)",this,throwable);throw throwable;}
	}

	@Override
	public Collection<String> keys() {
		com.mijack.Xlog.logMethodEnter("java.util.Collection com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.keys()",this);try{com.mijack.Xlog.logMethodExit("java.util.Collection com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.keys()",this);return new HashSet<String>(map.keySet());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.Collection com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.keys()",this,throwable);throw throwable;}
	}

	@Override
	public void clear() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.clear()",this);try{trimToSize(-1); /*// -1 will evict 0-sized elements*/com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.clear()",this,throwable);throw throwable;}
	}

	/**
	 * Returns the size {@code Bitmap} in bytes.
	 * <p>
	 * An entry's size must not change while it is in the cache.
	 */
	private int sizeOf(String key, Bitmap value) {
		com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.sizeOf(java.lang.String,android.graphics.Bitmap)",this,key,value);try{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.sizeOf(java.lang.String,android.graphics.Bitmap)",this);return value.getRowBytes() * value.getHeight();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.sizeOf(java.lang.String,android.graphics.Bitmap)",this,throwable);throw throwable;}
	}

	@Override
	public synchronized final String toString() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.toString()",this);return String.format("LruCache[maxSize=%d]", maxSize);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache.toString()",this,throwable);throw throwable;}
	}
}