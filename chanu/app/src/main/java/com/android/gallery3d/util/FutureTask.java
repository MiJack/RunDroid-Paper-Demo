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

import java.util.concurrent.Callable;

/*// NOTE: If the Callable throws any Throwable, the result value will be null.*/
public class FutureTask<T> implements Runnable, Future<T> {
    private static final String TAG = "FutureTask";
    private Callable<T> mCallable;
    private FutureListener<T> mListener;
    private volatile boolean mIsCancelled;
    private boolean mIsDone;
    private T mResult;

    public FutureTask(Callable<T> callable, FutureListener<T> listener) {
        mCallable = callable;
        mListener = listener;
    }

    public FutureTask(Callable<T> callable) {
        this(callable, null);
    }

    public void cancel() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.FutureTask<T>.cancel()",this);try{mIsCancelled = true;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.FutureTask<T>.cancel()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.FutureTask<T>.cancel()",this,throwable);throw throwable;}
    }

    public synchronized T get() {
        com.mijack.Xlog.logMethodEnter("T com.android.gallery3d.util.FutureTask<T>.get()",this);try{while (!mIsDone) {
            try {
                wait();
            } catch (InterruptedException t) {
                /*// ignore.*/
            }
        }
        {com.mijack.Xlog.logMethodExit("T com.android.gallery3d.util.FutureTask<T>.get()",this);return mResult;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("T com.android.gallery3d.util.FutureTask<T>.get()",this,throwable);throw throwable;}
    }

    public void waitDone() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.FutureTask<T>.waitDone()",this);try{get();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.FutureTask<T>.waitDone()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.FutureTask<T>.waitDone()",this,throwable);throw throwable;}
    }

    public synchronized boolean isDone() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.util.FutureTask<T>.isDone()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.FutureTask<T>.isDone()",this);return mIsDone;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.util.FutureTask<T>.isDone()",this,throwable);throw throwable;}
    }

    public boolean isCancelled() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.util.FutureTask<T>.isCancelled()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.FutureTask<T>.isCancelled()",this);return mIsCancelled;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.util.FutureTask<T>.isCancelled()",this,throwable);throw throwable;}
    }

    public void run() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.FutureTask<T>.run()",this);try{T result = null;

        if (!mIsCancelled) {
            try {
                result = mCallable.call();
            } catch (Throwable ex) {
                Log.w(TAG, "Exception in running a task", ex);
            }
        }

        synchronized(this) {
            mResult = result;
            mIsDone = true;
            if (mListener != null) {
                mListener.onFutureDone(this);
            }
            notifyAll();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.FutureTask<T>.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.FutureTask<T>.run()",this,throwable);throw throwable;}
    }
}
