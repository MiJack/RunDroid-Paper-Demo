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

package com.android.gallery3d.data;

import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.JobContext;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;

public class DecodeUtils {
    private static final String TAG = "DecodeService";

    private static class DecodeCanceller implements CancelListener {
        Options mOptions;
        public DecodeCanceller(Options options) {
            mOptions = options;
        }
        public void onCancel() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DecodeUtils$DecodeCanceller.onCancel()",this);try{mOptions.requestCancelDecode();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DecodeUtils$DecodeCanceller.onCancel()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DecodeUtils$DecodeCanceller.onCancel()",this,throwable);throw throwable;}
        }
    }

    public static Bitmap requestDecode(JobContext jc, final String filePath,
            Options options) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String,android.graphics.BitmapFactory.Options)",jc,filePath,options);try{if (options == null) {options = new Options();}
        jc.setCancelListener(new DecodeCanceller(options));
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String,android.graphics.BitmapFactory.Options)");return ensureGLCompatibleBitmap(
                BitmapFactory.decodeFile(filePath, options));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String,android.graphics.BitmapFactory.Options)",throwable);throw throwable;}
    }

    public static Bitmap requestDecode(JobContext jc, FileDescriptor fd, Options options) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,android.graphics.BitmapFactory.Options)",jc,fd,options);try{if (options == null) {options = new Options();}
        jc.setCancelListener(new DecodeCanceller(options));
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,android.graphics.BitmapFactory.Options)");return ensureGLCompatibleBitmap(
                BitmapFactory.decodeFileDescriptor(fd, null, options));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,android.graphics.BitmapFactory.Options)",throwable);throw throwable;}
    }

    public static Bitmap requestDecode(JobContext jc, byte[] bytes,
            Options options) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,[byte,android.graphics.BitmapFactory.Options)",jc,bytes,options);try{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,[byte,android.graphics.BitmapFactory.Options)");return requestDecode(jc, bytes, 0, bytes.length, options);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,[byte,android.graphics.BitmapFactory.Options)",throwable);throw throwable;}
    }

    public static Bitmap requestDecode(JobContext jc, byte[] bytes, int offset,
            int length, Options options) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,[byte,int,int,android.graphics.BitmapFactory.Options)",jc,bytes,offset,length,options);try{if (options == null) {options = new Options();}
        jc.setCancelListener(new DecodeCanceller(options));
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,[byte,int,int,android.graphics.BitmapFactory.Options)");return ensureGLCompatibleBitmap(
                BitmapFactory.decodeByteArray(bytes, offset, length, options));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,[byte,int,int,android.graphics.BitmapFactory.Options)",throwable);throw throwable;}
    }

    public static Bitmap requestDecode(JobContext jc, final String filePath,
            Options options, int targetSize) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String,android.graphics.BitmapFactory.Options,int)",jc,filePath,options,targetSize);try{FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            FileDescriptor fd = fis.getFD();
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String,android.graphics.BitmapFactory.Options,int)");return requestDecode(jc, fd, options, targetSize);}
        } catch (Exception ex) {
            Log.w(TAG, ex);
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String,android.graphics.BitmapFactory.Options,int)");return null;}
        } finally {
            Utils.closeSilently(fis);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String,android.graphics.BitmapFactory.Options,int)",throwable);throw throwable;}
    }

    public static Bitmap requestDecode(JobContext jc, FileDescriptor fd,
            Options options, int targetSize) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,android.graphics.BitmapFactory.Options,int)",jc,fd,options,targetSize);try{if (options == null) {options = new Options();}
        jc.setCancelListener(new DecodeCanceller(options));

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        if (jc.isCancelled()) {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,android.graphics.BitmapFactory.Options,int)");return null;}}

        options.inSampleSize = BitmapUtils.computeSampleSizeLarger(
                options.outWidth, options.outHeight, targetSize);
        options.inJustDecodeBounds = false;

        Bitmap result = BitmapFactory.decodeFileDescriptor(fd, null, options);
        /*// We need to resize down if the decoder does not support inSampleSize.*/
        /*// (For example, GIF images.)*/
        result = BitmapUtils.resizeDownIfTooBig(result, targetSize, true);
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,android.graphics.BitmapFactory.Options,int)");return ensureGLCompatibleBitmap(result);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,android.graphics.BitmapFactory.Options,int)",throwable);throw throwable;}
    }

    /**
     * Decodes the bitmap from the given byte array if the image size is larger than the given
     * requirement.
     *
     * Note: The returned image may be resized down. However, both width and height must be
     * larger than the <code>targetSize</code>.
     */
    public static Bitmap requestDecodeIfBigEnough(JobContext jc, byte[] data,
            Options options, int targetSize) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecodeIfBigEnough(com.android.gallery3d.util.ThreadPool.JobContext,[byte,android.graphics.BitmapFactory.Options,int)",jc,data,options,targetSize);try{if (options == null) {options = new Options();}
        jc.setCancelListener(new DecodeCanceller(options));

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        if (jc.isCancelled()) {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecodeIfBigEnough(com.android.gallery3d.util.ThreadPool.JobContext,[byte,android.graphics.BitmapFactory.Options,int)");return null;}}
        if (options.outWidth < targetSize || options.outHeight < targetSize) {
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecodeIfBigEnough(com.android.gallery3d.util.ThreadPool.JobContext,[byte,android.graphics.BitmapFactory.Options,int)");return null;}
        }
        options.inSampleSize = BitmapUtils.computeSampleSizeLarger(
                options.outWidth, options.outHeight, targetSize);
        options.inJustDecodeBounds = false;
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecodeIfBigEnough(com.android.gallery3d.util.ThreadPool.JobContext,[byte,android.graphics.BitmapFactory.Options,int)");return ensureGLCompatibleBitmap(
                BitmapFactory.decodeByteArray(data, 0, data.length, options));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecodeIfBigEnough(com.android.gallery3d.util.ThreadPool.JobContext,[byte,android.graphics.BitmapFactory.Options,int)",throwable);throw throwable;}
    }

    public static Bitmap requestDecode(JobContext jc,
            FileDescriptor fileDescriptor, Rect paddings, Options options) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,android.graphics.Rect,android.graphics.BitmapFactory.Options)",jc,fileDescriptor,paddings,options);try{if (options == null) {options = new Options();}
        jc.setCancelListener(new DecodeCanceller(options));
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,android.graphics.Rect,android.graphics.BitmapFactory.Options)");return ensureGLCompatibleBitmap(BitmapFactory.decodeFileDescriptor
                (fileDescriptor, paddings, options));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.requestDecode(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,android.graphics.Rect,android.graphics.BitmapFactory.Options)",throwable);throw throwable;}
    }

    /*// TODO: This function should not be called directly from*/
    /*// DecodeUtils.requestDecode(...), since we don't have the knowledge*/
    /*// if the bitmap will be uploaded to GL.*/
    public static Bitmap ensureGLCompatibleBitmap(Bitmap bitmap) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.ensureGLCompatibleBitmap(android.graphics.Bitmap)",bitmap);try{if (bitmap == null || bitmap.getConfig() != null) {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.ensureGLCompatibleBitmap(android.graphics.Bitmap)");return bitmap;}}
        Bitmap newBitmap = bitmap.copy(Config.ARGB_8888, false);
        bitmap.recycle();
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.ensureGLCompatibleBitmap(android.graphics.Bitmap)");return newBitmap;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.DecodeUtils.ensureGLCompatibleBitmap(android.graphics.Bitmap)",throwable);throw throwable;}
    }

    public static BitmapRegionDecoder requestCreateBitmapRegionDecoder(
            JobContext jc, byte[] bytes, int offset, int length,
            boolean shareable) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,[byte,int,int,boolean)",jc,bytes,offset,length,shareable);try{if (offset < 0 || length <= 0 || offset + length > bytes.length) {
            throw new IllegalArgumentException(String.format(
                    "offset = %s, length = %s, bytes = %s",
                    offset, length, bytes.length));
        }

        try {
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,[byte,int,int,boolean)");return BitmapRegionDecoder.newInstance(
                    bytes, offset, length, shareable);}
        } catch (Throwable t)  {
            Log.w(TAG, t);
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,[byte,int,int,boolean)");return null;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,[byte,int,int,boolean)",throwable);throw throwable;}
    }

    public static BitmapRegionDecoder requestCreateBitmapRegionDecoder(
            JobContext jc, String filePath, boolean shareable) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String,boolean)",jc,filePath,shareable);try{try {
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String,boolean)");return BitmapRegionDecoder.newInstance(filePath, shareable);}
        } catch (Throwable t)  {
            Log.w(TAG, t);
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String,boolean)");return null;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String,boolean)",throwable);throw throwable;}
    }

    public static BitmapRegionDecoder requestCreateBitmapRegionDecoder(
            JobContext jc, FileDescriptor fd, boolean shareable) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,boolean)",jc,fd,shareable);try{try {
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,boolean)");return BitmapRegionDecoder.newInstance(fd, shareable);}
        } catch (Throwable t)  {
            Log.w(TAG, t);
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,boolean)");return null;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,android.os.ParcelFileDescriptor,boolean)",throwable);throw throwable;}
    }

    public static BitmapRegionDecoder requestCreateBitmapRegionDecoder(
            JobContext jc, InputStream is, boolean shareable) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,java.io.FileInputStream,boolean)",jc,is,shareable);try{try {
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,java.io.FileInputStream,boolean)");return BitmapRegionDecoder.newInstance(is, shareable);}
        } catch (Throwable t)  {
            /*// We often cancel the creating of bitmap region decoder,*/
            /*// so just log one line.*/
            Log.w(TAG, "requestCreateBitmapRegionDecoder: " + t);
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,java.io.FileInputStream,boolean)");return null;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,java.io.FileInputStream,boolean)",throwable);throw throwable;}
    }

    public static BitmapRegionDecoder requestCreateBitmapRegionDecoder(
            JobContext jc, Uri uri, ContentResolver resolver,
            boolean shareable) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,android.net.Uri,android.content.ContentResolver,boolean)",jc,uri,resolver,shareable);try{ParcelFileDescriptor pfd = null;
        try {
            pfd = resolver.openFileDescriptor(uri, "r");
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,android.net.Uri,android.content.ContentResolver,boolean)");return BitmapRegionDecoder.newInstance(
                    pfd.getFileDescriptor(), shareable);}
        } catch (Throwable t) {
            Log.w(TAG, t);
            {com.mijack.Xlog.logStaticMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,android.net.Uri,android.content.ContentResolver,boolean)");return null;}
        } finally {
            Utils.closeSilently(pfd);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.DecodeUtils.requestCreateBitmapRegionDecoder(com.android.gallery3d.util.ThreadPool.JobContext,android.net.Uri,android.content.ContentResolver,boolean)",throwable);throw throwable;}
    }
}
