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

import android.graphics.Bitmap;
import android.view.View;

/**
 * A convenient class to extend when you only want to listen for a subset of all the image loading events. This
 * implements all methods in the {@link ImageLoadingListener} but does nothing.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.4.0
 */
public class SimpleImageLoadingListener implements ImageLoadingListener {
	@Override
	public void onLoadingStarted(String imageUri, View view) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingStarted(java.lang.String,android.view.View)",this,imageUri,view);try{/*// Empty implementation*/com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingStarted(java.lang.String,android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingStarted(java.lang.String,android.view.View)",this,throwable);throw throwable;}
	}

	@Override
	public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingFailed(java.lang.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this,imageUri,view,failReason);try{/*// Empty implementation*/com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingFailed(java.lang.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingFailed(java.lang.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this,throwable);throw throwable;}
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingComplete(java.lang.String,android.view.View,android.graphics.Bitmap)",this,imageUri,view,loadedImage);try{/*// Empty implementation*/com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingComplete(java.lang.String,android.view.View,android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingComplete(java.lang.String,android.view.View,android.graphics.Bitmap)",this,throwable);throw throwable;}
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view) {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingCancelled(java.lang.String,android.view.View)",this,imageUri,view);try{/*// Empty implementation*/com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingCancelled(java.lang.String,android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener.onLoadingCancelled(java.lang.String,android.view.View)",this,throwable);throw throwable;}
	}
}
