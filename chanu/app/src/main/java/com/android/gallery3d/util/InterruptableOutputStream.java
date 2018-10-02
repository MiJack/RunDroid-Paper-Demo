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

package com.android.gallery3d.util;

import com.android.gallery3d.common.Utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

public class InterruptableOutputStream extends OutputStream {

    private static final int MAX_WRITE_BYTES = 4096;

    private OutputStream mOutputStream;
    private volatile boolean mIsInterrupted = false;

    public InterruptableOutputStream(OutputStream outputStream) {
        mOutputStream = Utils.checkNotNull(outputStream);
    }

    @Override
    public void write(int oneByte) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.InterruptableOutputStream.write(int)",this,oneByte);try{if (mIsInterrupted) {throw new InterruptedIOException();}
        mOutputStream.write(oneByte);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.InterruptableOutputStream.write(int)",this,throwable);throw throwable;}
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.InterruptableOutputStream.write([byte,int,int)",this,buffer,offset,count);try{int end = offset + count;
        while (offset < end) {
            if (mIsInterrupted) {throw new InterruptedIOException();}
            int bytesCount = Math.min(MAX_WRITE_BYTES, end - offset);
            mOutputStream.write(buffer, offset, bytesCount);
            offset += bytesCount;
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.InterruptableOutputStream.write([byte,int,int)",this,throwable);throw throwable;}
    }

    @Override
    public void close() throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.InterruptableOutputStream.close()",this);try{mOutputStream.close();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.InterruptableOutputStream.close()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.InterruptableOutputStream.close()",this,throwable);throw throwable;}
    }

    @Override
    public void flush() throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.InterruptableOutputStream.flush()",this);try{if (mIsInterrupted) {throw new InterruptedIOException();}
        mOutputStream.flush();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.InterruptableOutputStream.flush()",this,throwable);throw throwable;}
    }

    public void interrupt() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.InterruptableOutputStream.interrupt()",this);try{mIsInterrupted = true;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.InterruptableOutputStream.interrupt()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.InterruptableOutputStream.interrupt()",this,throwable);throw throwable;}
    }
}
