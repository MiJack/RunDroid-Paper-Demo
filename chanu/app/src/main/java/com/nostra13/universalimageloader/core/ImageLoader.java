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

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FakeBitmapDisplayer;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.nostra13.universalimageloader.utils.L;

/**
 * Singletone for image loading and displaying at {@link ImageView ImageViews}<br />
 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before any other method.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class ImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();

	static final String LOG_INIT_CONFIG = "Initialize ImageLoader with configuration";
	static final String LOG_DESTROY = "Destroy ImageLoader";
	static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image from memory cache [%s]";

	private static final String WARNING_RE_INIT_CONFIG = "Try to initialize ImageLoader which had already been initialized before. "
			+ "To re-init ImageLoader with new configuration call ImageLoader.destroy() at first.";
	private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (ImageView reference must not be null)";
	private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
	private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";

	private ImageLoaderConfiguration configuration;
	private ImageLoaderEngine engine;

	private final ImageLoadingListener emptyListener = new SimpleImageLoadingListener();
	private final BitmapDisplayer fakeBitmapDisplayer = new FakeBitmapDisplayer();

	private volatile static ImageLoader instance;

	/** Returns singleton class instance */
	public static ImageLoader getInstance() {
		com.mijack.Xlog.logStaticMethodEnter("com.nostra13.universalimageloader.core.ImageLoader com.nostra13.universalimageloader.core.ImageLoader.getInstance()");try{if (instance == null) {
			synchronized (ImageLoader.class) {
				if (instance == null) {
					instance = new ImageLoader();
				}
			}
		}
		{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.core.ImageLoader com.nostra13.universalimageloader.core.ImageLoader.getInstance()");return instance;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.nostra13.universalimageloader.core.ImageLoader com.nostra13.universalimageloader.core.ImageLoader.getInstance()",throwable);throw throwable;}
	}

	protected ImageLoader() {
	}

	/**
	 * Initializes ImageLoader instance with configuration.<br />
	 * If configurations was set before ( {@link #isInited()} == true) then this method does nothing.<br />
	 * To force initialization with new configuration you should {@linkplain #destroy() destroy ImageLoader} at first.
	 * 
	 * @param configuration {@linkplain ImageLoaderConfiguration ImageLoader configuration}
	 * @throws IllegalArgumentException if <b>configuration</b> parameter is null
	 */
	public synchronized void init(ImageLoaderConfiguration configuration) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.init(com.nostra13.universalimageloader.core.ImageLoaderConfiguration)",this,configuration);try{if (configuration == null) {
			throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
		}
		if (this.configuration == null) {
			if (configuration.loggingEnabled) {L.d(LOG_INIT_CONFIG);}
			engine = new ImageLoaderEngine(configuration);
			this.configuration = configuration;
		} else {
			L.w(WARNING_RE_INIT_CONFIG);
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.init(com.nostra13.universalimageloader.core.ImageLoaderConfiguration)",this,throwable);throw throwable;}
	}

	/**
	 * Returns <b>true</b> - if ImageLoader {@linkplain #init(ImageLoaderConfiguration) is initialized with
	 * configuration}; <b>false</b> - otherwise
	 */
	public boolean isInited() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.ImageLoader.isInited()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.ImageLoader.isInited()",this);return configuration != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.ImageLoader.isInited()",this,throwable);throw throwable;}
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn. <br/>
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView)",this,uri,imageView);try{displayImage(uri, imageView, null, null);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView)",this,throwable);throw throwable;}
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 * @param options {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> -
	 *            default display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView,com.nostra13.universalimageloader.core.DisplayImageOptions)",this,uri,imageView,options);try{displayImage(uri, imageView, options, null);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView,com.nostra13.universalimageloader.core.DisplayImageOptions)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView,com.nostra13.universalimageloader.core.DisplayImageOptions)",this,throwable);throw throwable;}
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,uri,imageView,listener);try{displayImage(uri, imageView, null, listener);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,throwable);throw throwable;}
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 * @param options {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> -
	 *            default display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView,com.nostra13.universalimageloader.core.DisplayImageOptions,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,uri,imageView,options,listener);try{checkConfiguration();
		if (imageView == null) {
			throw new IllegalArgumentException(ERROR_WRONG_ARGUMENTS);
		}
		if (listener == null) {
			listener = emptyListener;
		}
		if (options == null) {
			options = configuration.defaultDisplayImageOptions;
		}

		if (TextUtils.isEmpty(uri)) {
			engine.cancelDisplayTaskFor(imageView);
			listener.onLoadingStarted(uri, imageView);
			if (options.shouldShowImageForEmptyUri()) {
				imageView.setImageResource(options.getImageForEmptyUri());
			} else {
				imageView.setImageBitmap(null);
			}
			listener.onLoadingComplete(uri, imageView, null);
			{com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView,com.nostra13.universalimageloader.core.DisplayImageOptions,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this);return;}
		}

		ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageView, configuration.maxImageWidthForMemoryCache,
				configuration.maxImageHeightForMemoryCache);
		String memoryCacheKey = MemoryCacheUtil.generateKey(uri, targetSize);
		engine.prepareDisplayTaskFor(imageView, memoryCacheKey);

		listener.onLoadingStarted(uri, imageView);
		Bitmap bmp = configuration.memoryCache.get(memoryCacheKey);
		if (bmp != null && !bmp.isRecycled()) {
			if (configuration.loggingEnabled) {L.i(LOG_LOAD_IMAGE_FROM_MEMORY_CACHE, memoryCacheKey);}

			if (options.shouldPostProcess()) {
				ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageView, targetSize, memoryCacheKey, options, listener,
						engine.getLockForUri(uri));
				ProcessAndDisplayImageTask displayTask = new ProcessAndDisplayImageTask(engine, bmp, imageLoadingInfo, options.getHandler());
				engine.submit(displayTask);
			} else {
				options.getDisplayer().display(bmp, imageView);
				listener.onLoadingComplete(uri, imageView, bmp);
			}
		} else {
			if (options.shouldShowStubImage()) {
				imageView.setImageResource(options.getStubImage());
			} else {
				if (options.isResetViewBeforeLoading()) {
					imageView.setImageBitmap(null);
				}
			}

			ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageView, targetSize, memoryCacheKey, options, listener, engine.getLockForUri(uri));
			LoadAndDisplayImageTask displayTask = new LoadAndDisplayImageTask(engine, imageLoadingInfo, options.getHandler());
			engine.submit(displayTask);
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.displayImage(java.lang.String,android.widget.ImageView,com.nostra13.universalimageloader.core.DisplayImageOptions,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,throwable);throw throwable;}
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, ImageLoadingListener listener) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,uri,listener);try{loadImage(uri, null, null, listener);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,throwable);throw throwable;}
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param minImageSize Minimal size for {@link Bitmap} which will be returned in
	 *            {@linkplain ImageLoadingListener#onLoadingComplete(Bitmap) callback}. Downloaded image will be decoded
	 *            and scaled to {@link Bitmap} of the size which is <b>equal or larger</b> (usually a bit larger) than
	 *            incoming minImageSize .
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, ImageSize minImageSize, ImageLoadingListener listener) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageSize,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,uri,minImageSize,listener);try{loadImage(uri, minImageSize, null, listener);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageSize,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageSize,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,throwable);throw throwable;}
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param options {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> -
	 *            default display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.<br />
	 *            Incoming options should contain {@link FakeBitmapDisplayer} as displayer.
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, DisplayImageOptions options, ImageLoadingListener listener) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.DisplayImageOptions,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,uri,options,listener);try{loadImage(uri, null, options, listener);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.DisplayImageOptions,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.DisplayImageOptions,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,throwable);throw throwable;}
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param targetImageSize Minimal size for {@link Bitmap} which will be returned in
	 *            {@linkplain ImageLoadingListener#onLoadingComplete(Bitmap) callback}. Downloaded image will be decoded
	 *            and scaled to {@link Bitmap} of the size which is <b>equal or larger</b> (usually a bit larger) than
	 *            incoming minImageSize .
	 * @param options {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> -
	 *            default display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.<br />
	 *            Incoming options should contain {@link FakeBitmapDisplayer} as displayer.
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options, ImageLoadingListener listener) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageSize,com.nostra13.universalimageloader.core.DisplayImageOptions,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,uri,targetImageSize,options,listener);try{checkConfiguration();
		if (targetImageSize == null) {
			targetImageSize = new ImageSize(configuration.maxImageWidthForMemoryCache, configuration.maxImageHeightForMemoryCache);
		}
		if (options == null) {
			options = configuration.defaultDisplayImageOptions;
		}

		DisplayImageOptions optionsWithFakeDisplayer;
		if (options.getDisplayer() instanceof FakeBitmapDisplayer) {
			optionsWithFakeDisplayer = options;
		} else {
			optionsWithFakeDisplayer = new DisplayImageOptions.Builder().cloneFrom(options).displayer(fakeBitmapDisplayer).build();
		}

		ImageView fakeImage = new ImageView(configuration.context);
		fakeImage.setLayoutParams(new LayoutParams(targetImageSize.getWidth(), targetImageSize.getHeight()));
		fakeImage.setScaleType(ScaleType.CENTER_CROP);

		displayImage(uri, fakeImage, optionsWithFakeDisplayer, listener);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageSize,com.nostra13.universalimageloader.core.DisplayImageOptions,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.loadImage(java.lang.String,com.nostra13.universalimageloader.core.assist.ImageSize,com.nostra13.universalimageloader.core.DisplayImageOptions,com.nostra13.universalimageloader.core.assist.ImageLoadingListener)",this,throwable);throw throwable;}
	}

	/**
	 * Checks if ImageLoader's configuration was initialized
	 * 
	 * @throws IllegalStateException if configuration wasn't initialized
	 */
	private void checkConfiguration() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.checkConfiguration()",this);try{com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.checkConfiguration()",this);if (configuration == null) {
			throw new IllegalStateException(ERROR_NOT_INIT);
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.checkConfiguration()",this,throwable);throw throwable;}
	}

	/**
	 * Returns memory cache
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public MemoryCacheAware<String, Bitmap> getMemoryCache() {
		com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.cache.memory.MemoryCacheAware com.nostra13.universalimageloader.core.ImageLoader.getMemoryCache()",this);try{checkConfiguration();
		{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.cache.memory.MemoryCacheAware com.nostra13.universalimageloader.core.ImageLoader.getMemoryCache()",this);return configuration.memoryCache;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.cache.memory.MemoryCacheAware com.nostra13.universalimageloader.core.ImageLoader.getMemoryCache()",this,throwable);throw throwable;}
	}

	/**
	 * Clears memory cache
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void clearMemoryCache() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.clearMemoryCache()",this);try{checkConfiguration();
		configuration.memoryCache.clear();com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.clearMemoryCache()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.clearMemoryCache()",this,throwable);throw throwable;}
	}

	/**
	 * Returns disc cache
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public DiscCacheAware getDiscCache() {
		com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.cache.disc.DiscCacheAware com.nostra13.universalimageloader.core.ImageLoader.getDiscCache()",this);try{checkConfiguration();
		{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.cache.disc.DiscCacheAware com.nostra13.universalimageloader.core.ImageLoader.getDiscCache()",this);return configuration.discCache;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.cache.disc.DiscCacheAware com.nostra13.universalimageloader.core.ImageLoader.getDiscCache()",this,throwable);throw throwable;}
	}

	/**
	 * Clears disc cache.
	 * 
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void clearDiscCache() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.clearDiscCache()",this);try{checkConfiguration();
		configuration.discCache.clear();com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.clearDiscCache()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.clearDiscCache()",this,throwable);throw throwable;}
	}

	/** Returns URI of image which is loading at this moment into passed {@link ImageView} */
	public String getLoadingUriForView(ImageView imageView) {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.nostra13.universalimageloader.core.ImageLoader.getLoadingUriForView(android.widget.ImageView)",this,imageView);try{com.mijack.Xlog.logMethodExit("java.lang.String com.nostra13.universalimageloader.core.ImageLoader.getLoadingUriForView(android.widget.ImageView)",this);return engine.getLoadingUriForView(imageView);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.nostra13.universalimageloader.core.ImageLoader.getLoadingUriForView(android.widget.ImageView)",this,throwable);throw throwable;}
	}

	/**
	 * Cancel the task of loading and displaying image for passed {@link ImageView}.
	 * 
	 * @param imageView {@link ImageView} for which display task will be cancelled
	 */
	public void cancelDisplayTask(ImageView imageView) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.cancelDisplayTask(android.widget.ImageView)",this,imageView);try{engine.cancelDisplayTaskFor(imageView);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.cancelDisplayTask(android.widget.ImageView)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.cancelDisplayTask(android.widget.ImageView)",this,throwable);throw throwable;}
	}

	/**
	 * Denies or allows ImageLoader to download images from the network.<br />
	 * <br />
	 * If downloads are denied and if image isn't cached then
	 * {@link ImageLoadingListener#onLoadingFailed(String, View, FailReason)} callback will be fired with
	 * {@link FailReason#NETWORK_DENIED}
	 * 
	 * @param denyNetworkDownloads pass <b>true</b> - to deny engine to download images from the network; <b>false</b> -
	 *            to allow engine to download images from network.
	 */
	public void denyNetworkDownloads(boolean denyNetworkDownloads) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.denyNetworkDownloads(boolean)",this,denyNetworkDownloads);try{engine.denyNetworkDownloads(denyNetworkDownloads);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.denyNetworkDownloads(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.denyNetworkDownloads(boolean)",this,throwable);throw throwable;}
	}

	/**
	 * Sets option whether ImageLoader will use {@link FlushedInputStream} for network downloads to handle <a
	 * href="http://code.google.com/p/android/issues/detail?id=6066">this known problem</a> or not.
	 * 
	 * @param handleSlowNetwork pass <b>true</b> - to use {@link FlushedInputStream} for network downloads; <b>false</b>
	 *            - otherwise.
	 */
	public void handleSlowNetwork(boolean handleSlowNetwork) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.handleSlowNetwork(boolean)",this,handleSlowNetwork);try{engine.handleSlowNetwork(handleSlowNetwork);com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.handleSlowNetwork(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.handleSlowNetwork(boolean)",this,throwable);throw throwable;}
	}

	/**
	 * Pause ImageLoader. All new "load&display" tasks won't be executed until ImageLoader is {@link #resume() resumed}.<br />
	 * Already running tasks are not paused.
	 */
	public void pause() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.pause()",this);try{engine.pause();com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.pause()",this,throwable);throw throwable;}
	}

	/** Resumes waiting "load&display" tasks */
	public void resume() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.resume()",this);try{engine.resume();com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.resume()",this,throwable);throw throwable;}
	}

	/**
	 * Cancels all running and scheduled display image tasks.<br />
	 * ImageLoader still can be used after calling this method.
	 */
	public void stop() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.stop()",this);try{engine.stop();com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.stop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.stop()",this,throwable);throw throwable;}
	}

	/**
	 * {@linkplain #stop() Stops ImageLoader} and clears current configuration. <br />
	 * You can {@linkplain #init(ImageLoaderConfiguration) init} ImageLoader with new configuration after calling this
	 * method.
	 */
	public void destroy() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.ImageLoader.destroy()",this);try{if (configuration != null && configuration.loggingEnabled) {L.d(LOG_DESTROY);}
		stop();
		engine = null;
		configuration = null;com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.ImageLoader.destroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.ImageLoader.destroy()",this,throwable);throw throwable;}
	}
}
