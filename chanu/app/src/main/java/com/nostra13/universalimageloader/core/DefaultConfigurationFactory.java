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
package com.nostra13.universalimageloader.core;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.deque.LIFOLinkedBlockingDeque;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * Factory for providing of default options for {@linkplain ImageLoaderConfiguration configuration}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.6
 */
public class DefaultConfigurationFactory {

	/** Creates default implementation of task executor */
	public static Executor createExecutor(int threadPoolSize, int threadPriority, QueueProcessingType tasksProcessingType) {
		com.mijack.Xlog.logStaticMethodEnter("java.util.concurrent.Executor com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createExecutor(int,int,com.nostra13.universalimageloader.core.assist.QueueProcessingType)",threadPoolSize,threadPriority,tasksProcessingType);try{boolean lifo = tasksProcessingType == QueueProcessingType.LIFO;
		BlockingQueue<Runnable> taskQueue = lifo ? new LIFOLinkedBlockingDeque<Runnable>() : new LinkedBlockingQueue<Runnable>();
		{com.mijack.Xlog.logStaticMethodExit("java.util.concurrent.Executor com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createExecutor(int,int,com.nostra13.universalimageloader.core.assist.QueueProcessingType)");return new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue, createThreadFactory(threadPriority));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.concurrent.Executor com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createExecutor(int,int,com.nostra13.universalimageloader.core.assist.QueueProcessingType)",throwable);throw throwable;}
	}

	/** Creates {@linkplain HashCodeFileNameGenerator default implementation} of FileNameGenerator */
	public static FileNameGenerator createFileNameGenerator() {
		com.mijack.Xlog.logStaticMethodEnter("com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createFileNameGenerator()");try{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createFileNameGenerator()");return new HashCodeFileNameGenerator();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createFileNameGenerator()",throwable);throw throwable;}
	}

	/** Creates default implementation of {@link DisckCacheAware} depends on incoming parameters */
	public static DiscCacheAware createDiscCache(Context context, FileNameGenerator discCacheFileNameGenerator, int discCacheSize, int discCacheFileCount) {
		com.mijack.Xlog.logStaticMethodEnter("com.nostra13.universalimageloader.cache.disc.DiscCacheAware com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createDiscCache(android.content.Context,com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator,int,int)",context,discCacheFileNameGenerator,discCacheSize,discCacheFileCount);try{if (discCacheSize > 0) {
			File individualCacheDir = StorageUtils.getIndividualCacheDirectory(context);
			{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.cache.disc.DiscCacheAware com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createDiscCache(android.content.Context,com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator,int,int)");return new TotalSizeLimitedDiscCache(individualCacheDir, discCacheFileNameGenerator, discCacheSize);}
		} else if (discCacheFileCount > 0) {
			File individualCacheDir = StorageUtils.getIndividualCacheDirectory(context);
			{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.cache.disc.DiscCacheAware com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createDiscCache(android.content.Context,com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator,int,int)");return new FileCountLimitedDiscCache(individualCacheDir, discCacheFileNameGenerator, discCacheFileCount);}
		} else {
			File cacheDir = StorageUtils.getCacheDirectory(context);
			{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.cache.disc.DiscCacheAware com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createDiscCache(android.content.Context,com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator,int,int)");return new UnlimitedDiscCache(cacheDir, discCacheFileNameGenerator);}
		}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.nostra13.universalimageloader.cache.disc.DiscCacheAware com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createDiscCache(android.content.Context,com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator,int,int)",throwable);throw throwable;}
	}

	/** Creates reserve disc cache which will be used if primary disc cache becomes unavailable */
	public static DiscCacheAware createReserveDiscCache(Context context) {
		com.mijack.Xlog.logStaticMethodEnter("com.nostra13.universalimageloader.cache.disc.DiscCacheAware com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createReserveDiscCache(android.content.Context)",context);try{File cacheDir = context.getCacheDir();
		File individualDir = new File(cacheDir, "uil-images");
		if (individualDir.exists() || individualDir.mkdir()) {
			cacheDir = individualDir;
		}
		{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.cache.disc.DiscCacheAware com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createReserveDiscCache(android.content.Context)");return new TotalSizeLimitedDiscCache(cacheDir, 2 * 1024 * 1024);} /*// limit - 2 Mb*/}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.nostra13.universalimageloader.cache.disc.DiscCacheAware com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createReserveDiscCache(android.content.Context)",throwable);throw throwable;}
	}

	/**
	 * Creates default implementation of {@link MemoryCacheAware} depends on incoming parameters: <br />
	 * {@link LruMemoryCache} (for API >= 9) or {@link LRULimitedMemoryCache} (for API < 9).<br />
	 * Default cache size = 1/8 of available app memory.
	 */
	public static MemoryCacheAware<String, Bitmap> createMemoryCache(int memoryCacheSize) {
		com.mijack.Xlog.logStaticMethodEnter("com.nostra13.universalimageloader.cache.memory.MemoryCacheAware com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createMemoryCache(int)",memoryCacheSize);try{if (memoryCacheSize == 0) {
			memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
		}
		MemoryCacheAware<String, Bitmap> memoryCache;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			memoryCache = new LruMemoryCache(memoryCacheSize);
		} else {
			memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
		}
		{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.cache.memory.MemoryCacheAware com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createMemoryCache(int)");return memoryCache;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.nostra13.universalimageloader.cache.memory.MemoryCacheAware com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createMemoryCache(int)",throwable);throw throwable;}
	}

	/** Creates default implementation of {@link ImageDownloader} - {@link BaseImageDownloader} */
	public static ImageDownloader createImageDownloader(Context context) {
		com.mijack.Xlog.logStaticMethodEnter("com.nostra13.universalimageloader.core.download.BaseImageDownloader com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createImageDownloader(android.content.Context)",context);try{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.core.download.BaseImageDownloader com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createImageDownloader(android.content.Context)");return new BaseImageDownloader(context);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.nostra13.universalimageloader.core.download.BaseImageDownloader com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createImageDownloader(android.content.Context)",throwable);throw throwable;}
	}

	/** Creates default implementation of {@link ImageDecoder} - {@link BaseImageDecoder} */
	public static ImageDecoder createImageDecoder(boolean loggingEnabled) {
		com.mijack.Xlog.logStaticMethodEnter("com.nostra13.universalimageloader.core.decode.BaseImageDecoder com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createImageDecoder(boolean)",loggingEnabled);try{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.core.decode.BaseImageDecoder com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createImageDecoder(boolean)");return new BaseImageDecoder(loggingEnabled);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.nostra13.universalimageloader.core.decode.BaseImageDecoder com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createImageDecoder(boolean)",throwable);throw throwable;}
	}

	/** Creates default implementation of {@link BitmapDisplayer} - {@link SimpleBitmapDisplayer} */
	public static BitmapDisplayer createBitmapDisplayer() {
		com.mijack.Xlog.logStaticMethodEnter("com.nostra13.universalimageloader.core.display.BitmapDisplayer com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createBitmapDisplayer()");try{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.core.display.BitmapDisplayer com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createBitmapDisplayer()");return new SimpleBitmapDisplayer();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.nostra13.universalimageloader.core.display.BitmapDisplayer com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createBitmapDisplayer()",throwable);throw throwable;}
	}

	/** Creates default implementation of {@linkplain ThreadFactory thread factory} for task executor */
	private static ThreadFactory createThreadFactory(int threadPriority) {
		com.mijack.Xlog.logStaticMethodEnter("java.util.concurrent.ThreadFactory com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createThreadFactory(int)",threadPriority);try{com.mijack.Xlog.logStaticMethodExit("java.util.concurrent.ThreadFactory com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createThreadFactory(int)");return new DefaultThreadFactory(threadPriority);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.concurrent.ThreadFactory com.nostra13.universalimageloader.core.DefaultConfigurationFactory.createThreadFactory(int)",throwable);throw throwable;}
	}

	private static class DefaultThreadFactory implements ThreadFactory {

		private static final AtomicInteger poolNumber = new AtomicInteger(1);

		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;
		private final int threadPriority;

		DefaultThreadFactory(int threadPriority) {
			this.threadPriority = threadPriority;
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
		}

		public Thread newThread(Runnable r) {
			com.mijack.Xlog.logMethodEnter("java.lang.Thread com.nostra13.universalimageloader.core.DefaultConfigurationFactory$DefaultThreadFactory.newThread(java.lang.Runnable)",this,r);try{Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon()) {t.setDaemon(false);}
			t.setPriority(threadPriority);
			{com.mijack.Xlog.logMethodExit("java.lang.Thread com.nostra13.universalimageloader.core.DefaultConfigurationFactory$DefaultThreadFactory.newThread(java.lang.Runnable)",this);return t;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Thread com.nostra13.universalimageloader.core.DefaultConfigurationFactory$DefaultThreadFactory.newThread(java.lang.Runnable)",this,throwable);throw throwable;}
		}
	}
}
