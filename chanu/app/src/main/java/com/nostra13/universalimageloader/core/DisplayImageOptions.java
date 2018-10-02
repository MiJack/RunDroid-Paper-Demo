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
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * Contains options for image display. Defines:
 * <ul>
 * <li>whether stub image will be displayed in {@link android.widget.ImageView ImageView} during image loading</li>
 * <li>whether stub image will be displayed in {@link android.widget.ImageView ImageView} if empty URI is passed</li>
 * <li>whether stub image will be displayed in {@link android.widget.ImageView ImageView} if image loading fails</li>
 * <li>whether {@link android.widget.ImageView ImageView} should be reset before image loading start</li>
 * <li>whether loaded image will be cached in memory</li>
 * <li>whether loaded image will be cached on disc</li>
 * <li>image scale type</li>
 * <li>decoding options (including bitmap decoding configuration)</li>
 * <li>delay before loading of image</li>
 * <li>auxiliary object which will be passed to {@link ImageDownloader# ImageDownloader}</li>
 * <li>pre-processor for image Bitmap (before caching in memory)</li>
 * <li>post-processor for image Bitmap (after caching in memory, before displaying)</li>
 * <li>how decoded {@link Bitmap} will be displayed</li>
 * </ul>
 * 
 * You can create instance:
 * <ul>
 * <li>with {@link Builder}:<br />
 * <b>i.e.</b> :
 * <code>new {@link DisplayImageOptions}.{@link Builder#Builder() Builder()}.{@link Builder#cacheInMemory() cacheInMemory()}.
 * {@link Builder#showStubImage(int) showStubImage()}.{@link Builder#build() build()}</code><br />
 * </li>
 * <li>or by static method: {@link #createSimple()}</li> <br />
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public final class DisplayImageOptions implements Cloneable {
	private static final String TAG = "DisplayImageOptions";

	private final int stubImage;
	private final int imageForEmptyUri;
	private final int imageOnFail;
	private final boolean resetViewBeforeLoading;
	private boolean cacheInMemory;
	private boolean cacheOnDisc;
	private final ImageScaleType imageScaleType;
	private final ImageSize imageSize;
	private final Options decodingOptions;
	private final int delayBeforeLoading;
	private final Object extraForDownloader;
	private final BitmapProcessor preProcessor;
	private final BitmapProcessor postProcessor;
	private final BitmapDisplayer displayer;
	private final Handler handler;
	private final String fullSizeImageLocation;
    private boolean centerCrop = false;

	private DisplayImageOptions(Builder builder) {
		stubImage = builder.stubImage;
		imageForEmptyUri = builder.imageForEmptyUri;
		imageOnFail = builder.imageOnFail;
		resetViewBeforeLoading = builder.resetViewBeforeLoading;
		cacheInMemory = builder.cacheInMemory;
		cacheOnDisc = builder.cacheOnDisc;
		imageScaleType = builder.imageScaleType;
		imageSize = builder.imageSize;
		decodingOptions = builder.decodingOptions;
		delayBeforeLoading = builder.delayBeforeLoading;
		extraForDownloader = builder.extraForDownloader;
		preProcessor = builder.preProcessor;
		postProcessor = builder.postProcessor;
        fullSizeImageLocation = builder.fullSizeImageLocation;
		displayer = builder.displayer;
		handler = builder.handler;
	}

	public boolean shouldShowStubImage() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldShowStubImage()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldShowStubImage()",this);return stubImage != 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldShowStubImage()",this,throwable);throw throwable;}
	}

	public boolean shouldShowImageForEmptyUri() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldShowImageForEmptyUri()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldShowImageForEmptyUri()",this);return imageForEmptyUri != 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldShowImageForEmptyUri()",this,throwable);throw throwable;}
	}

	public boolean shouldShowImageOnFail() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldShowImageOnFail()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldShowImageOnFail()",this);return imageOnFail != 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldShowImageOnFail()",this,throwable);throw throwable;}
	}

	public boolean shouldPreProcess() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldPreProcess()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldPreProcess()",this);return preProcessor != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldPreProcess()",this,throwable);throw throwable;}
	}

	public boolean shouldPostProcess() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldPostProcess()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldPostProcess()",this);return postProcessor != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldPostProcess()",this,throwable);throw throwable;}
	}

	public boolean shouldDelayBeforeLoading() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldDelayBeforeLoading()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldDelayBeforeLoading()",this);return delayBeforeLoading > 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.shouldDelayBeforeLoading()",this,throwable);throw throwable;}
	}

	public int getStubImage() {
		com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.core.DisplayImageOptions.getStubImage()",this);try{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.core.DisplayImageOptions.getStubImage()",this);return stubImage;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.core.DisplayImageOptions.getStubImage()",this,throwable);throw throwable;}
	}

	public int getImageForEmptyUri() {
		com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.core.DisplayImageOptions.getImageForEmptyUri()",this);try{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.core.DisplayImageOptions.getImageForEmptyUri()",this);return imageForEmptyUri;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.core.DisplayImageOptions.getImageForEmptyUri()",this,throwable);throw throwable;}
	}
	
	ImageSize getImageSize() {
        com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.assist.ImageSize com.nostra13.universalimageloader.core.DisplayImageOptions.getImageSize()",this);try{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.assist.ImageSize com.nostra13.universalimageloader.core.DisplayImageOptions.getImageSize()",this);return imageSize;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.assist.ImageSize com.nostra13.universalimageloader.core.DisplayImageOptions.getImageSize()",this,throwable);throw throwable;}
    }

	public int getImageOnFail() {
		com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.core.DisplayImageOptions.getImageOnFail()",this);try{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.core.DisplayImageOptions.getImageOnFail()",this);return imageOnFail;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.core.DisplayImageOptions.getImageOnFail()",this,throwable);throw throwable;}
	}

	public boolean isResetViewBeforeLoading() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isResetViewBeforeLoading()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isResetViewBeforeLoading()",this);return resetViewBeforeLoading;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isResetViewBeforeLoading()",this,throwable);throw throwable;}
	}

	public boolean isCacheInMemory() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isCacheInMemory()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isCacheInMemory()",this);return cacheInMemory;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isCacheInMemory()",this,throwable);throw throwable;}
	}

	public boolean isCacheOnDisc() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isCacheOnDisc()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isCacheOnDisc()",this);return cacheOnDisc;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isCacheOnDisc()",this,throwable);throw throwable;}
	}

	public ImageScaleType getImageScaleType() {
		com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.assist.ImageScaleType com.nostra13.universalimageloader.core.DisplayImageOptions.getImageScaleType()",this);try{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.assist.ImageScaleType com.nostra13.universalimageloader.core.DisplayImageOptions.getImageScaleType()",this);return imageScaleType;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.assist.ImageScaleType com.nostra13.universalimageloader.core.DisplayImageOptions.getImageScaleType()",this,throwable);throw throwable;}
	}

	String getFullSizeImageLocation() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.nostra13.universalimageloader.core.DisplayImageOptions.getFullSizeImageLocation()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.nostra13.universalimageloader.core.DisplayImageOptions.getFullSizeImageLocation()",this);return fullSizeImageLocation;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.nostra13.universalimageloader.core.DisplayImageOptions.getFullSizeImageLocation()",this,throwable);throw throwable;}
	}
	
	boolean isCenterCrop() {
		com.mijack.Xlog.logMethodEnter("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isCenterCrop()",this);try{com.mijack.Xlog.logMethodExit("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isCenterCrop()",this);return centerCrop;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.nostra13.universalimageloader.core.DisplayImageOptions.isCenterCrop()",this,throwable);throw throwable;}
	}

	public Options getDecodingOptions() {
		com.mijack.Xlog.logMethodEnter("android.graphics.BitmapFactory.Options com.nostra13.universalimageloader.core.DisplayImageOptions.getDecodingOptions()",this);try{com.mijack.Xlog.logMethodExit("android.graphics.BitmapFactory.Options com.nostra13.universalimageloader.core.DisplayImageOptions.getDecodingOptions()",this);return decodingOptions;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.BitmapFactory.Options com.nostra13.universalimageloader.core.DisplayImageOptions.getDecodingOptions()",this,throwable);throw throwable;}
	}

	public int getDelayBeforeLoading() {
		com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.core.DisplayImageOptions.getDelayBeforeLoading()",this);try{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.core.DisplayImageOptions.getDelayBeforeLoading()",this);return delayBeforeLoading;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.core.DisplayImageOptions.getDelayBeforeLoading()",this,throwable);throw throwable;}
	}

	public Object getExtraForDownloader() {
		com.mijack.Xlog.logMethodEnter("java.lang.Object com.nostra13.universalimageloader.core.DisplayImageOptions.getExtraForDownloader()",this);try{com.mijack.Xlog.logMethodExit("java.lang.Object com.nostra13.universalimageloader.core.DisplayImageOptions.getExtraForDownloader()",this);return extraForDownloader;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Object com.nostra13.universalimageloader.core.DisplayImageOptions.getExtraForDownloader()",this,throwable);throw throwable;}
	}

	public BitmapProcessor getPreProcessor() {
		com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.process.BitmapProcessor com.nostra13.universalimageloader.core.DisplayImageOptions.getPreProcessor()",this);try{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.process.BitmapProcessor com.nostra13.universalimageloader.core.DisplayImageOptions.getPreProcessor()",this);return preProcessor;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.process.BitmapProcessor com.nostra13.universalimageloader.core.DisplayImageOptions.getPreProcessor()",this,throwable);throw throwable;}
	}

	public BitmapProcessor getPostProcessor() {
		com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.process.BitmapProcessor com.nostra13.universalimageloader.core.DisplayImageOptions.getPostProcessor()",this);try{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.process.BitmapProcessor com.nostra13.universalimageloader.core.DisplayImageOptions.getPostProcessor()",this);return postProcessor;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.process.BitmapProcessor com.nostra13.universalimageloader.core.DisplayImageOptions.getPostProcessor()",this,throwable);throw throwable;}
	}

    public DisplayImageOptions createClone() {
        com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.createClone()",this);try{try {
            {com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.createClone()",this);return (DisplayImageOptions)clone();}
        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "Clone error", e);
        }
        {com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.createClone()",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.createClone()",this,throwable);throw throwable;}
    }

	public DisplayImageOptions modifyCenterCrop(boolean centerCropImage) {
		com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.modifyCenterCrop(boolean)",this,centerCropImage);try{try {
			DisplayImageOptions options = (DisplayImageOptions)clone();
			options.centerCrop = centerCropImage;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.modifyCenterCrop(boolean)",this);return options;}
		} catch (CloneNotSupportedException e) {
			Log.e(TAG, "Clone error", e);
		}
		{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.modifyCenterCrop(boolean)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.modifyCenterCrop(boolean)",this,throwable);throw throwable;}
	}

    public DisplayImageOptions noCache() {
        com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.noCache()",this);try{try {
            DisplayImageOptions options = (DisplayImageOptions)clone();
            options.cacheOnDisc = false;
            options.cacheInMemory = false;
            {com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.noCache()",this);return options;}
        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "Clone error", e);
        }
        {com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.noCache()",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.noCache()",this,throwable);throw throwable;}
    }

    public BitmapDisplayer getDisplayer() {
		com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.display.BitmapDisplayer com.nostra13.universalimageloader.core.DisplayImageOptions.getDisplayer()",this);try{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.display.BitmapDisplayer com.nostra13.universalimageloader.core.DisplayImageOptions.getDisplayer()",this);return displayer;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.display.BitmapDisplayer com.nostra13.universalimageloader.core.DisplayImageOptions.getDisplayer()",this,throwable);throw throwable;}
	}

	public Handler getHandler() {
        com.mijack.Xlog.logMethodEnter("android.os.Handler com.nostra13.universalimageloader.core.DisplayImageOptions.getHandler()",this);try{{com.mijack.Xlog.logMethodExit("android.os.Handler com.nostra13.universalimageloader.core.DisplayImageOptions.getHandler()",this);return (handler == null ? new Handler() : handler);}
        /*
        if (handler != null)
            return handler;
        else if (Looper.myLooper() != null)
            return new Handler();
        else
            return null;
	    */}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Handler com.nostra13.universalimageloader.core.DisplayImageOptions.getHandler()",this,throwable);throw throwable;}
	}

	/**
	 * Builder for {@link DisplayImageOptions}
	 * 
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 */
	public static class Builder {
		private int stubImage = 0;
		private int imageForEmptyUri = 0;
		private int imageOnFail = 0;
		private boolean resetViewBeforeLoading = false;
		private boolean cacheInMemory = false;
		private boolean cacheOnDisc = false;
		private ImageScaleType imageScaleType = ImageScaleType.IN_SAMPLE_POWER_OF_2;
		private ImageSize imageSize = null;
		private String fullSizeImageLocation = null;
		private Options decodingOptions = new Options();
		private int delayBeforeLoading = 0;
		private Object extraForDownloader = null;
		private BitmapProcessor preProcessor = null;
		private BitmapProcessor postProcessor = null;
		private BitmapDisplayer displayer = DefaultConfigurationFactory.createBitmapDisplayer();
		private Handler handler = null;

		public Builder() {
			decodingOptions.inPurgeable = true;
			decodingOptions.inInputShareable = true;
		}

		/**
		 * Stub image will be displayed in {@link android.widget.ImageView ImageView} during image loading
		 * 
		 * @param stubImageRes Stub image resource
		 */
		public Builder showStubImage(int stubImageRes) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.showStubImage(int)",this,stubImageRes);try{stubImage = stubImageRes;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.showStubImage(int)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.showStubImage(int)",this,throwable);throw throwable;}
		}

		/**
		 * Incoming image will be displayed in {@link android.widget.ImageView ImageView} if empty URI (null or empty
		 * string) will be passed to <b>ImageLoader.displayImage(...)</b> method.
		 * 
		 * @param imageRes Image resource
		 */
		public Builder showImageForEmptyUri(int imageRes) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.showImageForEmptyUri(int)",this,imageRes);try{imageForEmptyUri = imageRes;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.showImageForEmptyUri(int)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.showImageForEmptyUri(int)",this,throwable);throw throwable;}
		}

		/**
		 * Incoming image will be displayed in {@link android.widget.ImageView ImageView} if some error occurs during
		 * requested image loading/decoding.
		 * 
		 * @param imageRes Image resource
		 */
		public Builder showImageOnFail(int imageRes) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.showImageOnFail(int)",this,imageRes);try{imageOnFail = imageRes;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.showImageOnFail(int)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.showImageOnFail(int)",this,throwable);throw throwable;}
		}

		/** {@link android.widget.ImageView ImageView} will be reset (set <b>null</b>) before image loading start */
		public Builder resetViewBeforeLoading() {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.resetViewBeforeLoading()",this);try{resetViewBeforeLoading = true;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.resetViewBeforeLoading()",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.resetViewBeforeLoading()",this,throwable);throw throwable;}
		}

		/** Loaded image will be cached in memory */
		public Builder cacheInMemory() {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.cacheInMemory()",this);try{cacheInMemory = true;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.cacheInMemory()",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.cacheInMemory()",this,throwable);throw throwable;}
		}

		/** Loaded image will be cached on disc */
		public Builder cacheOnDisc() {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.cacheOnDisc()",this);try{cacheOnDisc = true;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.cacheOnDisc()",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.cacheOnDisc()",this,throwable);throw throwable;}
		}

		/**
		 * Sets {@linkplain ImageScaleType scale type} for decoding image. This parameter is used while define scale
		 * size for decoding image to Bitmap. Default value - {@link ImageScaleType#IN_SAMPLE_POWER_OF_2}
		 */
		public Builder imageScaleType(ImageScaleType imageScaleType) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType)",this,imageScaleType);try{this.imageScaleType = imageScaleType;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType)",this,throwable);throw throwable;}
		}
		
		public Builder imageSize(ImageSize imageSize) {
            com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.imageSize(com.nostra13.universalimageloader.core.assist.ImageSize)",this,imageSize);try{this.imageSize = imageSize;
            {com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.imageSize(com.nostra13.universalimageloader.core.assist.ImageSize)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.imageSize(com.nostra13.universalimageloader.core.assist.ImageSize)",this,throwable);throw throwable;}
        }

        public Builder fullSizeImageLocation(String imageLocation) {
            com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.fullSizeImageLocation(java.lang.String)",this,imageLocation);try{this.fullSizeImageLocation = imageLocation;
            {com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.fullSizeImageLocation(java.lang.String)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.fullSizeImageLocation(java.lang.String)",this,throwable);throw throwable;}
        }

		/** Sets {@link Bitmap.Config bitmap config} for image decoding. Default value - {@link Bitmap.Config#ARGB_8888} */
		public Builder bitmapConfig(Bitmap.Config bitmapConfig) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.bitmapConfig(Bitmap.Config)",this,bitmapConfig);try{decodingOptions.inPreferredConfig = bitmapConfig;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.bitmapConfig(Bitmap.Config)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.bitmapConfig(Bitmap.Config)",this,throwable);throw throwable;}
		}

		/**
		 * Sets options for image decoding.<br />
		 * <b>NOTE:</b> {@link Options#inSampleSize} of incoming options will <b>NOT</b> be considered. Library
		 * calculate the most appropriate sample size itself according yo {@link #imageScaleType(ImageScaleType)}
		 * options.<br />
		 * <b>NOTE:</b> This option overlaps {@link #bitmapConfig(android.graphics.Bitmap.Config) bitmapConfig()}
		 * option.
		 */
		public Builder decodingOptions(Options decodingOptions) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.decodingOptions(android.graphics.BitmapFactory.Options)",this,decodingOptions);try{this.decodingOptions = decodingOptions;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.decodingOptions(android.graphics.BitmapFactory.Options)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.decodingOptions(android.graphics.BitmapFactory.Options)",this,throwable);throw throwable;}
		}

		/** Sets delay time before starting loading task. Default - no delay. */
		public Builder delayBeforeLoading(int delayInMillis) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.delayBeforeLoading(int)",this,delayInMillis);try{this.delayBeforeLoading = delayInMillis;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.delayBeforeLoading(int)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.delayBeforeLoading(int)",this,throwable);throw throwable;}
		}

		/** Sets auxiliary object which will be passed to {@link ImageDownloader#} */
		public Builder extraForDownloader(Object extra) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.extraForDownloader(java.lang.Object)",this,extra);try{this.extraForDownloader = extra;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.extraForDownloader(java.lang.Object)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.extraForDownloader(java.lang.Object)",this,throwable);throw throwable;}
		}

		/**
		 * Sets bitmap processor which will be process bitmaps before they will be cached in memory. So memory cache
		 * will contain bitmap processed by incoming preProcessor.<br />
		 * Image will be pre-processed even if caching in memory is disabled.
		 */
		public Builder preProcessor(BitmapProcessor preProcessor) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.preProcessor(com.nostra13.universalimageloader.core.process.BitmapProcessor)",this,preProcessor);try{this.preProcessor = preProcessor;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.preProcessor(com.nostra13.universalimageloader.core.process.BitmapProcessor)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.preProcessor(com.nostra13.universalimageloader.core.process.BitmapProcessor)",this,throwable);throw throwable;}
		}

		/**
		 * Sets bitmap processor which will be process bitmaps before they will be displayed in {@link ImageView} but
		 * after they'll have been saved in memory cache.
		 */
		public Builder postProcessor(BitmapProcessor postProcessor) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.postProcessor(com.nostra13.universalimageloader.core.process.BitmapProcessor)",this,postProcessor);try{this.postProcessor = postProcessor;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.postProcessor(com.nostra13.universalimageloader.core.process.BitmapProcessor)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.postProcessor(com.nostra13.universalimageloader.core.process.BitmapProcessor)",this,throwable);throw throwable;}
		}

		/**
		 * Sets custom {@link BitmapDisplayer displayer} for image loading task. Default value -
		 * {@link DefaultConfigurationFactory#createBitmapDisplayer()}
		 */
		public Builder displayer(BitmapDisplayer displayer) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.displayer(com.nostra13.universalimageloader.core.display.BitmapDisplayer)",this,displayer);try{this.displayer = displayer;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.displayer(com.nostra13.universalimageloader.core.display.BitmapDisplayer)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.displayer(com.nostra13.universalimageloader.core.display.BitmapDisplayer)",this,throwable);throw throwable;}
		}

		/**
		 * Sets custom {@linkplain Handler handler} for displaying images and firing {@linkplain ImageLoadingListener
		 * listener} events.
		 */
		public Builder handler(Handler handler) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.handler(android.os.Handler)",this,handler);try{this.handler = handler;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.handler(android.os.Handler)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.handler(android.os.Handler)",this,throwable);throw throwable;}
		}

		/** Sets all options equal to incoming options */
		public Builder cloneFrom(DisplayImageOptions options) {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.cloneFrom(com.nostra13.universalimageloader.core.DisplayImageOptions)",this,options);try{stubImage = options.stubImage;
			imageForEmptyUri = options.imageForEmptyUri;
			imageOnFail = options.imageOnFail;
			resetViewBeforeLoading = options.resetViewBeforeLoading;
			cacheInMemory = options.cacheInMemory;
			cacheOnDisc = options.cacheOnDisc;
			imageScaleType = options.imageScaleType;
			decodingOptions = options.decodingOptions;
			delayBeforeLoading = options.delayBeforeLoading;
			extraForDownloader = options.extraForDownloader;
			preProcessor = options.preProcessor;
			postProcessor = options.postProcessor;
			displayer = options.displayer;
			handler = options.handler;
			fullSizeImageLocation = options.fullSizeImageLocation;
			{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.cloneFrom(com.nostra13.universalimageloader.core.DisplayImageOptions)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions$Builder com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.cloneFrom(com.nostra13.universalimageloader.core.DisplayImageOptions)",this,throwable);throw throwable;}
		}

		/** Builds configured {@link DisplayImageOptions} object */
		public DisplayImageOptions build() {
			com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.build()",this);try{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.build()",this);return new DisplayImageOptions(this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions$Builder.build()",this,throwable);throw throwable;}
		}
	}

	/**
	 * Creates options appropriate for single displaying:
	 * <ul>
	 * <li>View will <b>not</b> be reset before loading</li>
	 * <li>Loaded image will <b>not</b> be cached in memory</li>
	 * <li>Loaded image will <b>not</b> be cached on disc</li>
	 * <li>{@link ImageScaleType#IN_SAMPLE_POWER_OF_2} decoding type will be used</li>
	 * <li>{@link Bitmap.Config#ARGB_8888} bitmap config will be used for image decoding</li>
	 * <li>{@link SimpleBitmapDisplayer} will be used for image displaying</li>
	 * </ul>
	 * 
	 * These option are appropriate for simple single-use image (from drawables or from Internet) displaying.
	 */
	public static DisplayImageOptions createSimple() {
		com.mijack.Xlog.logStaticMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.createSimple()");try{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.createSimple()");return new Builder().build();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions com.nostra13.universalimageloader.core.DisplayImageOptions.createSimple()",throwable);throw throwable;}
	}
}
