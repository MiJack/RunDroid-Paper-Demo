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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * {@link ImageLoader} engine which responsible for {@linkplain LoadAndDisplayImageTask display task} execution.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.7.1
 */
class ImageLoaderEngine {

	final ImageLoaderConfiguration configuration;

	private Executor taskExecutor;
	private Executor taskExecutorForCachedImages;
	private ExecutorService taskDistributor;

	private final Map<Integer, String> cacheKeysForImageViews = Collections.synchronizedMap(new HashMap<Integer, String>());
	private final Map<String, ReentrantLock> uriLocks = new WeakHashMap<String, ReentrantLock>();

	private final AtomicBoolean paused = new AtomicBoolean(false);
	private final AtomicBoolean networkDenied = new AtomicBoolean(false);
	private final AtomicBoolean slowNetwork = new AtomicBoolean(false);

	ImageLoaderEngine(ImageLoaderConfiguration configuration) {
		this.configuration = configuration;

		taskExecutor = configuration.taskExecutor;
		taskExecutorForCachedImages = configuration.taskExecutorForCachedImages;

		taskDistributor = Executors.newCachedThreadPool();
	}

	/** Submits task to execution pool */
	void submit(final LoadAndDisplayImageTask task) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoaderEngine.submit(com.nostra13.universalimageloader.core.LoadAndDisplayImageTask)",this,task);try{taskDistributor.execute(new Runnable() {
			@Override
			public void run() {
				com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoaderEngine$1.run()",this);try{boolean isImageCachedOnDisc = configuration.discCache.get(task.getLoadingUri()).exists();
				initExecutorsIfNeed();
				if (isImageCachedOnDisc) {
					taskExecutorForCachedImages.execute(task);
				} else {
					taskExecutor.execute(task);
				}com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoaderEngine$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoaderEngine$1.run()",this,throwable);throw throwable;}
			}
		});com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoaderEngine.submit(com.nostra13.universalimageloader.core.LoadAndDisplayImageTask)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoaderEngine.submit(com.nostra13.universalimageloader.core.LoadAndDisplayImageTask)",this,throwable);throw throwable;}
	}

	/** Submits task to execution pool */
	void submit(ProcessAndDisplayImageTask task) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoaderEngine.submit(com.nostra13.universalimageloader.core.ProcessAndDisplayImageTask)",this,task);try{initExecutorsIfNeed();
		taskExecutorForCachedImages.execute(task);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoaderEngine.submit(com.nostra13.universalimageloader.core.ProcessAndDisplayImageTask)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoaderEngine.submit(com.nostra13.universalimageloader.core.ProcessAndDisplayImageTask)",this,throwable);throw throwable;}
	}

	private void initExecutorsIfNeed() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoaderEngine.initExecutorsIfNeed()",this);try{if (taskExecutor == null) {
			taskExecutor = createTaskExecutor();
		}
		if (taskExecutorForCachedImages == null) {
			taskExecutorForCachedImages = createTaskExecutor();
		}com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoaderEngine.initExecutorsIfNeed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoaderEngine.initExecutorsIfNeed()",this,throwable);throw throwable;}
	}

	private Executor createTaskExecutor() {
		com.mijack.Xlog.logMethodEnter("java.util.concurrent.Executor com.nostra13.universalimageloader.core.ImageLoaderEngine.createTaskExecutor()",this);try{com.mijack.Xlog.logMethodExit("java.util.concurrent.Executor com.nostra13.universalimageloader.core.ImageLoaderEngine.createTaskExecutor()",this);return DefaultConfigurationFactory.createExecutor(configuration.threadPoolSize, configuration.threadPriority, configuration.tasksProcessingType);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.concurrent.Executor com.nostra13.universalimageloader.core.ImageLoaderEngine.createTaskExecutor()",this,throwable);throw throwable;}
	}

	/** Returns URI of image which is loading at this moment into passed {@link ImageView} */
	String getLoadingUriForView(ImageView imageView) {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.nostra13.universalimageloader.core.ImageLoaderEngine.getLoadingUriForView(android.widget.ImageView)",this,imageView);try{com.mijack.Xlog.logMethodExit("java.lang.String com.nostra13.universalimageloader.core.ImageLoaderEngine.getLoadingUriForView(android.widget.ImageView)",this);return cacheKeysForImageViews.get(imageView.hashCode());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.nostra13.universalimageloader.core.ImageLoaderEngine.getLoadingUriForView(android.widget.ImageView)",this,throwable);throw throwable;}
	}

	/**
	 * Associates <b>memoryCacheKey</b> with <b>imageView</b>. Then it helps to define image URI is loaded into
	 * ImageView at exact moment.
	 */
	void prepareDisplayTaskFor(ImageView imageView, String memoryCacheKey) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoaderEngine.prepareDisplayTaskFor(android.widget.ImageView,java.lang.String)",this,imageView,memoryCacheKey);try{cacheKeysForImageViews.put(imageView.hashCode(), memoryCacheKey);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoaderEngine.prepareDisplayTaskFor(android.widget.ImageView,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoaderEngine.prepareDisplayTaskFor(android.widget.ImageView,java.lang.String)",this,throwable);throw throwable;}
	}

	/**
	 * Cancels the task of loading and displaying image for incoming <b>imageView</b>.
	 * 
	 * @param imageView {@link ImageView} for which display task will be cancelled
	 */
	void cancelDisplayTaskFor(ImageView imageView) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoaderEngine.cancelDisplayTaskFor(android.widget.ImageView)",this,imageView);try{cacheKeysForImageViews.remove(imageView.hashCode());com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoaderEngine.cancelDisplayTaskFor(android.widget.ImageView)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoaderEngine.cancelDisplayTaskFor(android.widget.ImageView)",this,throwable);throw throwable;}
	}

	/**
	 * Denies or allows engine to download images from the network.<br />
	 * <br />
	 * If downloads are denied and if image isn't cached then
	 * {@link ImageLoadingListener#onLoadingFailed(String, View, FailReason)} callback will be fired with
	 * {@link FailReason#NETWORK_DENIED}
	 * 
	 * @param denyNetworkDownloads pass <b>true</b> - to deny engine to download images from the network; <b>false</b> -
	 *            to allow engine to download images from network.
	 */
	void denyNetworkDownloads(boolean denyNetworkDownloads) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoaderEngine.denyNetworkDownloads(boolean)",this,denyNetworkDownloads);try{networkDenied.set(denyNetworkDownloads);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoaderEngine.denyNetworkDownloads(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoaderEngine.denyNetworkDownloads(boolean)",this,throwable);throw throwable;}
	}

	/**
	 * Sets option whether ImageLoader will use {@link FlushedInputStream} for network downloads to handle <a
	 * href="http://code.google.com/p/android/issues/detail?id=6066">this known problem</a> or not.
	 * 
	 * @param handleSlowNetwork pass <b>true</b> - to use {@link FlushedInputStream} for network downloads; <b>false</b>
	 *            - otherwise.
	 */
	void handleSlowNetwork(boolean handleSlowNetwork) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoaderEngine.handleSlowNetwork(boolean)",this,handleSlowNetwork);try{slowNetwork.set(handleSlowNetwork);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoaderEngine.handleSlowNetwork(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoaderEngine.handleSlowNetwork(boolean)",this,throwable);throw throwable;}
	}

	/**
	 * Pauses engine. All new "load&display" tasks won't be executed until ImageLoader is {@link #resume() resumed}.<br />
	 * Already running tasks are not paused.
	 */
	void pause() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoaderEngine.pause()",this);try{paused.set(true);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoaderEngine.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoaderEngine.pause()",this,throwable);throw throwable;}
	}

	/** Resumes engine work. Paused "load&display" tasks will continue its work. */
	void resume() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoaderEngine.resume()",this);try{synchronized (paused) {
			paused.set(false);
			paused.notifyAll();
		}com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoaderEngine.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoaderEngine.resume()",this,throwable);throw throwable;}
	}

	/** Stops engine, cancels all running and scheduled display image tasks. Clears internal data. */
	void stop() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoaderEngine.stop()",this);try{if (!configuration.customExecutor) {
			taskExecutor = null;
		}
		if (!configuration.customExecutorForCachedImages) {
			taskExecutorForCachedImages = null;
		}

		cacheKeysForImageViews.clear();
		uriLocks.clear();com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoaderEngine.stop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoaderEngine.stop()",this,throwable);throw throwable;}
	}

	ReentrantLock getLockForUri(String uri) {
		com.mijack.Xlog.logMethodEnter("java.util.concurrent.locks.ReentrantLock com.nostra13.universalimageloader.core.ImageLoaderEngine.getLockForUri(java.lang.String)",this,uri);try{ReentrantLock lock = uriLocks.get(uri);
		if (lock == null) {
			lock = new ReentrantLock();
			uriLocks.put(uri, lock);
		}
		{com.mijack.Xlog.logMethodExit("java.util.concurrent.locks.ReentrantLock com.nostra13.universalimageloader.core.ImageLoaderEngine.getLockForUri(java.lang.String)",this);return lock;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.concurrent.locks.ReentrantLock com.nostra13.universalimageloader.core.ImageLoaderEngine.getLockForUri(java.lang.String)",this,throwable);throw throwable;}
	}

	AtomicBoolean getPause() {
		com.mijack.Xlog.logMethodEnter("java.util.concurrent.atomic.AtomicBoolean com.nostra13.universalimageloader.core.ImageLoaderEngine.getPause()",this);try{com.mijack.Xlog.logMethodExit("java.util.concurrent.atomic.AtomicBoolean com.nostra13.universalimageloader.core.ImageLoaderEngine.getPause()",this);return paused;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.concurrent.atomic.AtomicBoolean com.nostra13.universalimageloader.core.ImageLoaderEngine.getPause()",this,throwable);throw throwable;}
	}

	boolean isNetworkDenied() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.ImageLoaderEngine.isNetworkDenied()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.ImageLoaderEngine.isNetworkDenied()",this);return networkDenied.get();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.ImageLoaderEngine.isNetworkDenied()",this,throwable);throw throwable;}
	}

	boolean isSlowNetwork() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.ImageLoaderEngine.isSlowNetwork()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.ImageLoaderEngine.isSlowNetwork()",this);return slowNetwork.get();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.ImageLoaderEngine.isSlowNetwork()",this,throwable);throw throwable;}
	}
}
