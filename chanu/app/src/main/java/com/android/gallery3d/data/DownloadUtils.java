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

import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.JobContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.URL;

public class DownloadUtils {
    private static final String TAG = "DownloadService";

    public static boolean requestDownload(JobContext jc, URL url, File file) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.data.DownloadUtils.requestDownload(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL,java.io.File)",jc,url,file);try{FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.data.DownloadUtils.requestDownload(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL,java.io.File)");return download(jc, url, fos);}
        } catch (Throwable t) {
            {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.data.DownloadUtils.requestDownload(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL,java.io.File)");return false;}
        } finally {
            Utils.closeSilently(fos);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.data.DownloadUtils.requestDownload(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL,java.io.File)",throwable);throw throwable;}
    }

    public static byte[] requestDownload(JobContext jc, URL url) {
        com.mijack.Xlog.logStaticMethodEnter("[byte com.android.gallery3d.data.DownloadUtils.requestDownload(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL)",jc,url);try{ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            if (!download(jc, url, baos)) {
                {com.mijack.Xlog.logStaticMethodExit("[byte com.android.gallery3d.data.DownloadUtils.requestDownload(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL)");return null;}
            }
            {com.mijack.Xlog.logStaticMethodExit("[byte com.android.gallery3d.data.DownloadUtils.requestDownload(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL)");return baos.toByteArray();}
        } catch (Throwable t) {
            Log.w(TAG, t);
            {com.mijack.Xlog.logStaticMethodExit("[byte com.android.gallery3d.data.DownloadUtils.requestDownload(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL)");return null;}
        } finally {
            Utils.closeSilently(baos);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[byte com.android.gallery3d.data.DownloadUtils.requestDownload(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL)",throwable);throw throwable;}
    }

    public static void dump(JobContext jc, InputStream is, OutputStream os)
            throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.data.DownloadUtils.dump(com.android.gallery3d.util.ThreadPool.JobContext,java.io.InputStream,java.io.ByteArrayOutputStream)",jc,is,os);try{byte buffer[] = new byte[4096];
        int rc = is.read(buffer, 0, buffer.length);
        final Thread thread = Thread.currentThread();
        jc.setCancelListener(new CancelListener() {
            public void onCancel() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DownloadUtils$1.onCancel()",this);try{thread.interrupt();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DownloadUtils$1.onCancel()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DownloadUtils$1.onCancel()",this,throwable);throw throwable;}
            }
        });
        while (rc > 0) {
            if (jc.isCancelled()) {throw new InterruptedIOException();}
            os.write(buffer, 0, rc);
            rc = is.read(buffer, 0, buffer.length);
        }
        jc.setCancelListener(null);
        Thread.interrupted(); /*// consume the interrupt signal*/}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.data.DownloadUtils.dump(com.android.gallery3d.util.ThreadPool.JobContext,java.io.InputStream,java.io.ByteArrayOutputStream)",throwable);throw throwable;}
    }

    public static boolean download(JobContext jc, URL url, OutputStream output) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.data.DownloadUtils.download(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL,java.io.ByteArrayOutputStream)",jc,url,output);try{InputStream input = null;
        try {
            input = url.openStream();
            dump(jc, input, output);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.data.DownloadUtils.download(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL,java.io.ByteArrayOutputStream)");return true;}
        } catch (Throwable t) {
            Log.w(TAG, "fail to download", t);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.data.DownloadUtils.download(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL,java.io.ByteArrayOutputStream)");return false;}
        } finally {
            Utils.closeSilently(input);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.data.DownloadUtils.download(com.android.gallery3d.util.ThreadPool.JobContext,java.net.URL,java.io.ByteArrayOutputStream)",throwable);throw throwable;}
    }
}