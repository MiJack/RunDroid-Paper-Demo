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
package com.nostra13.universalimageloader.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

/**
 * Provides application storage paths
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public final class StorageUtils {

	private static final String INDIVIDUAL_DIR_NAME = "uil-images";

	private StorageUtils() {
	}

	/**
	 * Returns application cache directory. Cache directory will be created on SD card
	 * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted. Else - Android defines cache directory on
	 * device's file system.
	 * 
	 * @param context Application context
	 * @return Cache {@link File directory}
	 */
	public static File getCacheDirectory(Context context) {
		com.mijack.Xlog.logStaticMethodEnter("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getCacheDirectory(android.content.Context)",context);try{File appCacheDir = null;
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			appCacheDir = getExternalCacheDir(context);
		}
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir();
		}
		{com.mijack.Xlog.logStaticMethodExit("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getCacheDirectory(android.content.Context)");return appCacheDir;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getCacheDirectory(android.content.Context)",throwable);throw throwable;}
	}

	/**
	 * Returns individual application cache directory (for only image caching from ImageLoader). Cache directory will be
	 * created on SD card <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is mounted. Else -
	 * Android defines cache directory on device's file system.
	 * 
	 * @param context Application context
	 * @return Cache {@link File directory}
	 */
	public static File getIndividualCacheDirectory(Context context) {
		com.mijack.Xlog.logStaticMethodEnter("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getIndividualCacheDirectory(android.content.Context)",context);try{File cacheDir = getCacheDirectory(context);
		File individualCacheDir = new File(cacheDir, INDIVIDUAL_DIR_NAME);
		if (!individualCacheDir.exists()) {
			if (!individualCacheDir.mkdir()) {
				individualCacheDir = cacheDir;
			}
		}
		{com.mijack.Xlog.logStaticMethodExit("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getIndividualCacheDirectory(android.content.Context)");return individualCacheDir;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getIndividualCacheDirectory(android.content.Context)",throwable);throw throwable;}
	}

	/**
	 * Returns specified application cache directory. Cache directory will be created on SD card by defined path if card
	 * is mounted. Else - Android defines cache directory on device's file system.
	 * 
	 * @param context Application context
	 * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
	 * @return Cache {@link File directory}
	 */
	public static File getOwnCacheDirectory(Context context, String cacheDir) {
		com.mijack.Xlog.logStaticMethodEnter("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getOwnCacheDirectory(android.content.Context,java.lang.String)",context,cacheDir);try{File appCacheDir = null;
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
		}
		if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
			appCacheDir = context.getCacheDir();
		}
		{com.mijack.Xlog.logStaticMethodExit("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getOwnCacheDirectory(android.content.Context,java.lang.String)");return appCacheDir;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getOwnCacheDirectory(android.content.Context,java.lang.String)",throwable);throw throwable;}
	}

	private static File getExternalCacheDir(Context context) {
		com.mijack.Xlog.logStaticMethodEnter("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getExternalCacheDir(android.content.Context)",context);try{File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				L.w("Unable to create external cache directory");
				{com.mijack.Xlog.logStaticMethodExit("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getExternalCacheDir(android.content.Context)");return null;}
            }
        }
        try {
            File f = new File(appCacheDir, ".nomedia");
            if (!f.exists())
                {f.createNewFile();}
        } catch (IOException e) {
            L.i("Can't create \".nomedia\" file in app external cache dir " + appCacheDir);
        }
        {com.mijack.Xlog.logStaticMethodExit("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getExternalCacheDir(android.content.Context)");return appCacheDir;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.io.File com.nostra13.universalimageloader.utils.StorageUtils.getExternalCacheDir(android.content.Context)",throwable);throw throwable;}
	}
}
