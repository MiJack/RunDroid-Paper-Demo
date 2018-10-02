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
package com.nostra13.universalimageloader.core.assist;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Utility for generating of keys for memory cache, key comparing and other work with memory cache
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.6.3
 */
public final class MemoryCacheUtil {

	private static final String URI_AND_SIZE_SEPARATOR = "_";
	private static final String WIDTH_AND_HEIGHT_SEPARATOR = "x";

	private MemoryCacheUtil() {
	}

	/**
	 * Generates key for memory cache for incoming image (URI + size).<br />
	 * Pattern for cache key - {@value #MEMORY_CACHE_KEY_FORMAT}, where (1) - image URI, (2) - image size
	 * ([width]x[height]).
	 */
	public static String generateKey(String imageUri, ImageSize targetSize) {
		com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.generateKey(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageSize)",imageUri,targetSize);try{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.generateKey(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageSize)");return new StringBuilder(imageUri).append(URI_AND_SIZE_SEPARATOR).append(targetSize.getWidth()).append(WIDTH_AND_HEIGHT_SEPARATOR)
				.append(targetSize.getHeight()).toString();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.generateKey(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageSize)",throwable);throw throwable;}
	}

	public static Comparator<String> createFuzzyKeyComparator() {
		com.mijack.Xlog.logStaticMethodEnter("java.util.Comparator com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.createFuzzyKeyComparator()");try{{com.mijack.Xlog.logStaticMethodExit("java.util.Comparator com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.createFuzzyKeyComparator()");return new Comparator<String>() {
			@Override
			public int compare(String key1, String key2) {
				com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.core.assist.MemoryCacheUtil$1.compare(java.lang.String,java.lang.String)",this,key1,key2);try{String imageUri1 = key1.substring(0, key1.lastIndexOf(URI_AND_SIZE_SEPARATOR));
				String imageUri2 = key2.substring(0, key2.lastIndexOf(URI_AND_SIZE_SEPARATOR));
				{com.mijack.Xlog.logStaticMethodExit("java.util.Comparator com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.createFuzzyKeyComparator()");{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.core.assist.MemoryCacheUtil$1.compare(java.lang.String,java.lang.String)",this);return imageUri1.compareTo(imageUri2);}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.core.assist.MemoryCacheUtil$1.compare(java.lang.String,java.lang.String)",this,throwable);throw throwable;}
			}
		};}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.Comparator com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.createFuzzyKeyComparator()",throwable);throw throwable;}
	}

	/**
	 * Searches all bitmaps in memory cache which are corresponded to incoming URI.<br />
	 * <b>Note:</b> Memory cache can contain multiple sizes of the same image if only you didn't set
	 * {@link ImageLoaderConfiguration.Builder#denyCacheImageMultipleSizesInMemory()
	 * denyCacheImageMultipleSizesInMemory()} option in {@linkplain ImageLoaderConfiguration configuration}
	 */
	public static List<Bitmap> findCachedBitmapsForImageUri(String imageUri, MemoryCacheAware<String, Bitmap> memoryCache) {
		com.mijack.Xlog.logStaticMethodEnter("java.util.ArrayList com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.findCachedBitmapsForImageUri(java.lang.String,com.nostra13.universalimageloader.cache.memory.MemoryCacheAware)",imageUri,memoryCache);try{List<Bitmap> values = new ArrayList<Bitmap>();
		for (String key : memoryCache.keys()) {
			if (key.startsWith(imageUri)) {
				values.add(memoryCache.get(key));
			}
		}
		{com.mijack.Xlog.logStaticMethodExit("java.util.ArrayList com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.findCachedBitmapsForImageUri(java.lang.String,com.nostra13.universalimageloader.cache.memory.MemoryCacheAware)");return values;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.ArrayList com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.findCachedBitmapsForImageUri(java.lang.String,com.nostra13.universalimageloader.cache.memory.MemoryCacheAware)",throwable);throw throwable;}
	}

	/**
	 * Searches all keys in memory cache which are corresponded to incoming URI.<br />
	 * <b>Note:</b> Memory cache can contain multiple sizes of the same image if only you didn't set
	 * {@link ImageLoaderConfiguration.Builder#denyCacheImageMultipleSizesInMemory()
	 * denyCacheImageMultipleSizesInMemory()} option in {@linkplain ImageLoaderConfiguration configuration}
	 */
	public static List<String> findCacheKeysForImageUri(String imageUri, MemoryCacheAware<String, Bitmap> memoryCache) {
		com.mijack.Xlog.logStaticMethodEnter("java.util.ArrayList com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.findCacheKeysForImageUri(java.lang.String,com.nostra13.universalimageloader.cache.memory.MemoryCacheAware)",imageUri,memoryCache);try{List<String> values = new ArrayList<String>();
		for (String key : memoryCache.keys()) {
			if (key.startsWith(imageUri)) {
				values.add(key);
			}
		}
		{com.mijack.Xlog.logStaticMethodExit("java.util.ArrayList com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.findCacheKeysForImageUri(java.lang.String,com.nostra13.universalimageloader.cache.memory.MemoryCacheAware)");return values;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.ArrayList com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.findCacheKeysForImageUri(java.lang.String,com.nostra13.universalimageloader.cache.memory.MemoryCacheAware)",throwable);throw throwable;}
	}

	/**
	 * Removes from memory cache all images for incoming URI.<br />
	 * <b>Note:</b> Memory cache can contain multiple sizes of the same image if only you didn't set
	 * {@link ImageLoaderConfiguration.Builder#denyCacheImageMultipleSizesInMemory()
	 * denyCacheImageMultipleSizesInMemory()} option in {@linkplain ImageLoaderConfiguration configuration}
	 */
	public static void removeFromCache(String imageUri, MemoryCacheAware<String, Bitmap> memoryCache) {
		com.mijack.Xlog.logStaticMethodEnter("void com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.removeFromCache(java.lang.String,com.nostra13.universalimageloader.cache.memory.MemoryCacheAware)",imageUri,memoryCache);try{List<String> keysToRemove = new ArrayList<String>();
		for (String key : memoryCache.keys()) {
			if (key.startsWith(imageUri)) {
				keysToRemove.add(key);
			}
		}
		for (String keyToRemove : keysToRemove) {
			memoryCache.remove(keyToRemove);
		}com.mijack.Xlog.logStaticMethodExit("void com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.removeFromCache(java.lang.String,com.nostra13.universalimageloader.cache.memory.MemoryCacheAware)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.MemoryCacheUtil.removeFromCache(java.lang.String,com.nostra13.universalimageloader.cache.memory.MemoryCacheAware)",throwable);throw throwable;}
	}
}
