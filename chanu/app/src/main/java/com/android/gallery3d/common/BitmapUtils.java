/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.common;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BitmapUtils {
    private static final String TAG = "BitmapUtils";
    public static final int UNCONSTRAINED = -1;
    private static final int COMPRESS_JPEG_QUALITY = 90;

    private BitmapUtils(){}

    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a
     * bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
     */
    public static int computeSampleSize(int width, int height,
            int minSideLength, int maxNumOfPixels) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.BitmapUtils.computeSampleSize(int,int,int,int)",width,height,minSideLength,maxNumOfPixels);try{int initialSize = computeInitialSampleSize(
                width, height, minSideLength, maxNumOfPixels);

        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.BitmapUtils.computeSampleSize(int,int,int,int)");return initialSize <= 8
                ? Utils.nextPowerOf2(initialSize)
                : (initialSize + 7) / 8 * 8;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.BitmapUtils.computeSampleSize(int,int,int,int)",throwable);throw throwable;}
    }

    private static int computeInitialSampleSize(int w, int h,
            int minSideLength, int maxNumOfPixels) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.BitmapUtils.computeInitialSampleSize(int,int,int,int)",w,h,minSideLength,maxNumOfPixels);try{if (maxNumOfPixels == UNCONSTRAINED
                && minSideLength == UNCONSTRAINED) {{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.BitmapUtils.computeInitialSampleSize(int,int,int,int)");return 1;}}

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt((double) (w * h) / maxNumOfPixels));

        if (minSideLength == UNCONSTRAINED) {
            {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.BitmapUtils.computeInitialSampleSize(int,int,int,int)");return lowerBound;}
        } else {
            int sampleSize = Math.min(w / minSideLength, h / minSideLength);
            {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.BitmapUtils.computeInitialSampleSize(int,int,int,int)");return Math.max(sampleSize, lowerBound);}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.BitmapUtils.computeInitialSampleSize(int,int,int,int)",throwable);throw throwable;}
    }

    /*// This computes a sample size which makes the longer side at least*/
    /*// minSideLength long. If that's not possible, return 1.*/
    public static int computeSampleSizeLarger(int w, int h,
            int minSideLength) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.BitmapUtils.computeSampleSizeLarger(int,int,int)",w,h,minSideLength);try{int initialSize = Math.max(w / minSideLength, h / minSideLength);
        if (initialSize <= 1) {{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.BitmapUtils.computeSampleSizeLarger(int,int,int)");return 1;}}

        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.BitmapUtils.computeSampleSizeLarger(int,int,int)");return initialSize <= 8
                ? Utils.prevPowerOf2(initialSize)
                : initialSize / 8 * 8;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.BitmapUtils.computeSampleSizeLarger(int,int,int)",throwable);throw throwable;}
    }

    /*// Fin the min x that 1 / x <= scale*/
    public static int computeSampleSizeLarger(float scale) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.BitmapUtils.computeSampleSizeLarger(float)",scale);try{int initialSize = (int) Math.floor(1f / scale);
        if (initialSize <= 1) {{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.BitmapUtils.computeSampleSizeLarger(float)");return 1;}}

        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.BitmapUtils.computeSampleSizeLarger(float)");return initialSize <= 8
                ? Utils.prevPowerOf2(initialSize)
                : initialSize / 8 * 8;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.BitmapUtils.computeSampleSizeLarger(float)",throwable);throw throwable;}
    }

    /*// Find the max x that 1 / x >= scale.*/
    public static int computeSampleSize(float scale) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.BitmapUtils.computeSampleSize(float)",scale);try{Utils.assertTrue(scale > 0);
        int initialSize = Math.max(1, (int) Math.ceil(1 / scale));
        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.BitmapUtils.computeSampleSize(float)");return initialSize <= 8
                ? Utils.nextPowerOf2(initialSize)
                : (initialSize + 7) / 8 * 8;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.BitmapUtils.computeSampleSize(float)",throwable);throw throwable;}
    }

    public static Bitmap resizeDownToPixels(
            Bitmap bitmap, int targetPixels, boolean recycle) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownToPixels(android.graphics.Bitmap,int,boolean)",bitmap,targetPixels,recycle);try{int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = (float) Math.sqrt(
                (double) targetPixels / (width * height));
        if (scale >= 1.0f) {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownToPixels(android.graphics.Bitmap,int,boolean)");return bitmap;}}
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownToPixels(android.graphics.Bitmap,int,boolean)");return resizeBitmapByScale(bitmap, scale, recycle);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownToPixels(android.graphics.Bitmap,int,boolean)",throwable);throw throwable;}
    }

    public static Bitmap resizeBitmapByScale(
            Bitmap bitmap, float scale, boolean recycle) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeBitmapByScale(android.graphics.Bitmap,float,boolean)",bitmap,scale,recycle);try{int width = Math.round(bitmap.getWidth() * scale);
        int height = Math.round(bitmap.getHeight() * scale);
        if (width == bitmap.getWidth()
                && height == bitmap.getHeight()) {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeBitmapByScale(android.graphics.Bitmap,float,boolean)");return bitmap;}}
        Bitmap target = Bitmap.createBitmap(width, height, getConfig(bitmap));
        Canvas canvas = new Canvas(target);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle) {bitmap.recycle();}
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeBitmapByScale(android.graphics.Bitmap,float,boolean)");return target;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeBitmapByScale(android.graphics.Bitmap,float,boolean)",throwable);throw throwable;}
    }

    private static Bitmap.Config getConfig(Bitmap bitmap) {
        com.mijack.Xlog.logStaticMethodEnter("Bitmap.Config com.android.gallery3d.common.BitmapUtils.getConfig(android.graphics.Bitmap)",bitmap);try{Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        {com.mijack.Xlog.logStaticMethodExit("Bitmap.Config com.android.gallery3d.common.BitmapUtils.getConfig(android.graphics.Bitmap)");return config;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("Bitmap.Config com.android.gallery3d.common.BitmapUtils.getConfig(android.graphics.Bitmap)",throwable);throw throwable;}
    }

    public static Bitmap resizeDownBySideLength(
            Bitmap bitmap, int maxLength, boolean recycle) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownBySideLength(android.graphics.Bitmap,int,boolean)",bitmap,maxLength,recycle);try{int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scale = Math.min(
                (float) maxLength / srcWidth, (float) maxLength / srcHeight);
        if (scale >= 1.0f) {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownBySideLength(android.graphics.Bitmap,int,boolean)");return bitmap;}}
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownBySideLength(android.graphics.Bitmap,int,boolean)");return resizeBitmapByScale(bitmap, scale, recycle);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownBySideLength(android.graphics.Bitmap,int,boolean)",throwable);throw throwable;}
    }

    /*// Resize the bitmap if each side is >= targetSize * 2*/
    public static Bitmap resizeDownIfTooBig(
            Bitmap bitmap, int targetSize, boolean recycle) {
    	com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownIfTooBig(android.graphics.Bitmap,int,boolean)",bitmap,targetSize,recycle);try{if (bitmap == null) {
    		{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownIfTooBig(android.graphics.Bitmap,int,boolean)");return null;}
    	}
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scale = Math.max(
                (float) targetSize / srcWidth, (float) targetSize / srcHeight);
        if (scale > 0.5f) {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownIfTooBig(android.graphics.Bitmap,int,boolean)");return bitmap;}}
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownIfTooBig(android.graphics.Bitmap,int,boolean)");return resizeBitmapByScale(bitmap, scale, recycle);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownIfTooBig(android.graphics.Bitmap,int,boolean)",throwable);throw throwable;}
    }

    /*// Crops a square from the center of the original image.*/
    public static Bitmap cropCenter(Bitmap bitmap, boolean recycle) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.cropCenter(android.graphics.Bitmap,boolean)",bitmap,recycle);try{int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width == height) {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.cropCenter(android.graphics.Bitmap,boolean)");return bitmap;}}
        int size = Math.min(width, height);

        Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
        Canvas canvas = new Canvas(target);
        canvas.translate((size - width) / 2, (size - height) / 2);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle) {bitmap.recycle();}
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.cropCenter(android.graphics.Bitmap,boolean)");return target;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.cropCenter(android.graphics.Bitmap,boolean)",throwable);throw throwable;}
    }

    public static Bitmap resizeDownAndCropCenter(Bitmap bitmap, int size,
            boolean recycle) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownAndCropCenter(android.graphics.Bitmap,int,boolean)",bitmap,size,recycle);try{int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int minSide = Math.min(w, h);
        if (w == h && minSide <= size) {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownAndCropCenter(android.graphics.Bitmap,int,boolean)");return bitmap;}}
        size = Math.min(size, minSide);

        float scale = Math.max((float) size / bitmap.getWidth(),
                (float) size / bitmap.getHeight());
        Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
        int width = Math.round(scale * bitmap.getWidth());
        int height = Math.round(scale * bitmap.getHeight());
        Canvas canvas = new Canvas(target);
        canvas.translate((size - width) / 2f, (size - height) / 2f);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle) {bitmap.recycle();}
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownAndCropCenter(android.graphics.Bitmap,int,boolean)");return target;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.resizeDownAndCropCenter(android.graphics.Bitmap,int,boolean)",throwable);throw throwable;}
    }

    public static void recycleSilently(Bitmap bitmap) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.BitmapUtils.recycleSilently(android.graphics.Bitmap)",bitmap);try{if (bitmap == null) {{com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.BitmapUtils.recycleSilently(android.graphics.Bitmap)");return;}}
        try {
            bitmap.recycle();
        } catch (Throwable t) {
            Log.w(TAG, "unable recycle bitmap", t);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.BitmapUtils.recycleSilently(android.graphics.Bitmap)",throwable);throw throwable;}
    }

    public static Bitmap rotateBitmap(Bitmap source, int rotation, boolean recycle) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.rotateBitmap(android.graphics.Bitmap,int,boolean)",source,rotation,recycle);try{if (rotation == 0) {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.rotateBitmap(android.graphics.Bitmap,int,boolean)");return source;}}
        int w = source.getWidth();
        int h = source.getHeight();
        Matrix m = new Matrix();
        m.postRotate(rotation);
        Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, w, h, m, true);
        if (recycle) {source.recycle();}
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.rotateBitmap(android.graphics.Bitmap,int,boolean)");return bitmap;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.rotateBitmap(android.graphics.Bitmap,int,boolean)",throwable);throw throwable;}
    }

    public static Bitmap createVideoThumbnail(String filePath) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.createVideoThumbnail(java.lang.String)",filePath);try{/*// MediaMetadataRetriever is available on API Level 8*/
        /*// but is hidden until API Level 10*/
        Class<?> clazz = null;
        Object instance = null;
        try {
            clazz = Class.forName("android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();

            Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, filePath);

            /*// The method name changes between API Level 9 and 10.*/
            if (Build.VERSION.SDK_INT <= 9) {
                {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.createVideoThumbnail(java.lang.String)");return null;} /*// return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);*/
            } else {
                byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null) {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.createVideoThumbnail(java.lang.String)");return bitmap;}}
                }
                {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.createVideoThumbnail(java.lang.String)");return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);}
            }
        } catch (IllegalArgumentException ex) {
            /*// Assume this is a corrupt video file*/
        } catch (RuntimeException ex) {
            /*// Assume this is a corrupt video file.*/
        } catch (InstantiationException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } finally {
            /* // we don't use videos
            try {
                if (instance != null) {
                    clazz.getMethod("release").invoke(instance);
                }
            } catch (Exception ignored) {
            }
            */
        }
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.createVideoThumbnail(java.lang.String)");return null;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.common.BitmapUtils.createVideoThumbnail(java.lang.String)",throwable);throw throwable;}
    }

    public static byte[] compressBitmap(Bitmap bitmap) {
        com.mijack.Xlog.logStaticMethodEnter("[byte com.android.gallery3d.common.BitmapUtils.compressBitmap(android.graphics.Bitmap)",bitmap);try{ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                COMPRESS_JPEG_QUALITY, os);
        {com.mijack.Xlog.logStaticMethodExit("[byte com.android.gallery3d.common.BitmapUtils.compressBitmap(android.graphics.Bitmap)");return os.toByteArray();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[byte com.android.gallery3d.common.BitmapUtils.compressBitmap(android.graphics.Bitmap)",throwable);throw throwable;}
    }

    public static boolean isSupportedByRegionDecoder(String mimeType) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.common.BitmapUtils.isSupportedByRegionDecoder(java.lang.String)",mimeType);try{if (mimeType == null) {{com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.BitmapUtils.isSupportedByRegionDecoder(java.lang.String)");return false;}}
        mimeType = mimeType.toLowerCase();
        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.BitmapUtils.isSupportedByRegionDecoder(java.lang.String)");return mimeType.startsWith("image/") &&
                (!mimeType.equals("image/gif") && !mimeType.endsWith("bmp"));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.common.BitmapUtils.isSupportedByRegionDecoder(java.lang.String)",throwable);throw throwable;}
    }

    public static boolean isRotationSupported(String mimeType) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.common.BitmapUtils.isRotationSupported(java.lang.String)",mimeType);try{if (mimeType == null) {{com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.BitmapUtils.isRotationSupported(java.lang.String)");return false;}}
        mimeType = mimeType.toLowerCase();
        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.BitmapUtils.isRotationSupported(java.lang.String)");return mimeType.equals("image/jpeg");}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.common.BitmapUtils.isRotationSupported(java.lang.String)",throwable);throw throwable;}
    }

    public static byte[] compressToBytes(Bitmap bitmap, int quality) {
        com.mijack.Xlog.logStaticMethodEnter("[byte com.android.gallery3d.common.BitmapUtils.compressToBytes(android.graphics.Bitmap,int)",bitmap,quality);try{ByteArrayOutputStream baos = new ByteArrayOutputStream(65536);
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        {com.mijack.Xlog.logStaticMethodExit("[byte com.android.gallery3d.common.BitmapUtils.compressToBytes(android.graphics.Bitmap,int)");return baos.toByteArray();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[byte com.android.gallery3d.common.BitmapUtils.compressToBytes(android.graphics.Bitmap,int)",throwable);throw throwable;}
    }
}
