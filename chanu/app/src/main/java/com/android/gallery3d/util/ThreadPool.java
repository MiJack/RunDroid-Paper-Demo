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

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    private static final String TAG = "ThreadPool";
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 8;
    private static final int KEEP_ALIVE_TIME = 10; /*// 10 seconds*/

    /*// Resource type*/
    public static final int MODE_NONE = 0;
    public static final int MODE_CPU = 1;
    public static final int MODE_NETWORK = 2;

    public static final JobContext JOB_CONTEXT_STUB = new JobContextStub();

    ResourceCounter mCpuCounter = new ResourceCounter(2);
    ResourceCounter mNetworkCounter = new ResourceCounter(2);

    /*// A Job is like a Callable, but it has an addition JobContext parameter.*/
    public interface Job<T> {
        public T run(JobContext jc);
    }

    public interface JobContext {
        boolean isCancelled();
        void setCancelListener(CancelListener listener);
        boolean setMode(int mode);
    }

    private static class JobContextStub implements JobContext {
        @Override
        public boolean isCancelled() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.util.ThreadPool$JobContextStub.isCancelled()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.ThreadPool$JobContextStub.isCancelled()",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.util.ThreadPool$JobContextStub.isCancelled()",this,throwable);throw throwable;}
        }

        {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.ThreadPool$JobContextStub.setCancelListener(CancelListener)",this,listener);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.ThreadPool$JobContextStub.setCancelListener(CancelListener)",this);}

        @Override
        public boolean setMode(int mode) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.util.ThreadPool$JobContextStub.setMode(int)",this,mode);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.ThreadPool$JobContextStub.setMode(int)",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.util.ThreadPool$JobContextStub.setMode(int)",this,throwable);throw throwable;}
        }
    }

    public interface CancelListener {
        public void onCancel();
    }

    private static class ResourceCounter {
        public int value;
        public ResourceCounter(int v) {
            value = v;
        }
    }

    private final Executor mExecutor;

    public ThreadPool() {
        mExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new PriorityThreadFactory("thread-pool",
                android.os.Process.THREAD_PRIORITY_BACKGROUND));
    }

    /*// Submit a job to the thread pool. The listener will be called when the*/
    /*// job is finished (or cancelled).*/
    public <T> Future<T> submit(Job<T> job, FutureListener<T> listener) {
        com.mijack.Xlog.logMethodEnter("Future com.android.gallery3d.util.ThreadPool.submit(Job,FutureListener)",this,job,listener);try{Worker<T> w = new Worker<T>(job, listener);
        mExecutor.execute(w);
        {com.mijack.Xlog.logMethodExit("Future com.android.gallery3d.util.ThreadPool.submit(Job,FutureListener)",this);return w;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Future com.android.gallery3d.util.ThreadPool.submit(Job,FutureListener)",this,throwable);throw throwable;}
    }

    public <T> Future<T> submit(Job<T> job) {
        com.mijack.Xlog.logMethodEnter("Future com.android.gallery3d.util.ThreadPool.submit(Job)",this,job);try{com.mijack.Xlog.logMethodExit("Future com.android.gallery3d.util.ThreadPool.submit(Job)",this);return submit(job, null);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Future com.android.gallery3d.util.ThreadPool.submit(Job)",this,throwable);throw throwable;}
    }

    private class Worker<T> implements Runnable, Future<T>, JobContext {
        private static final String TAG = "Worker";
        private Job<T> mJob;
        private FutureListener<T> mListener;
        private CancelListener mCancelListener;
        private ResourceCounter mWaitOnResource;
        private volatile boolean mIsCancelled;
        private boolean mIsDone;
        private T mResult;
        private int mMode;

        public Worker(Job<T> job, FutureListener<T> listener) {
            mJob = job;
            mListener = listener;
        }

        /*// This is called by a thread in the thread pool.*/
        public void run() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.ThreadPool$Worker<T>.run()",this);try{T result = null;

            /*// A job is in CPU mode by default. setMode returns false*/
            /*// if the job is cancelled.*/
            if (setMode(MODE_CPU)) {
                try {
                    result = mJob.run(this);
                } catch (Throwable ex) {
                    Log.w(TAG, "Exception in running a job", ex);
                }
            }

            synchronized(this) {
                setMode(MODE_NONE);
                mResult = result;
                mIsDone = true;
                notifyAll();
            }
            if (mListener != null) {mListener.onFutureDone(this);}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.ThreadPool$Worker<T>.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.ThreadPool$Worker<T>.run()",this,throwable);throw throwable;}
        }

        /*// Below are the methods for Future.*/
        public synchronized void cancel() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.ThreadPool$Worker<T>.cancel()",this);try{if (mIsCancelled) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.ThreadPool$Worker<T>.cancel()",this);return;}}
            mIsCancelled = true;
            if (mWaitOnResource != null) {
                synchronized (mWaitOnResource) {
                    mWaitOnResource.notifyAll();
                }
            }
            if (mCancelListener != null) {
                mCancelListener.onCancel();
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.ThreadPool$Worker<T>.cancel()",this,throwable);throw throwable;}
        }

        public boolean isCancelled() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.isCancelled()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.isCancelled()",this);return mIsCancelled;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.isCancelled()",this,throwable);throw throwable;}
        }

        public synchronized boolean isDone() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.isDone()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.isDone()",this);return mIsDone;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.isDone()",this,throwable);throw throwable;}
        }

        public synchronized T get() {
            com.mijack.Xlog.logMethodEnter("T com.android.gallery3d.util.ThreadPool$Worker<T>.get()",this);try{while (!mIsDone) {
                try {
                    wait();
                } catch (Exception ex) {
                    Log.w(TAG, "ingore exception", ex);
                    /*// ignore.*/
                }
            }
            {com.mijack.Xlog.logMethodExit("T com.android.gallery3d.util.ThreadPool$Worker<T>.get()",this);return mResult;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("T com.android.gallery3d.util.ThreadPool$Worker<T>.get()",this,throwable);throw throwable;}
        }

        public void waitDone() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.ThreadPool$Worker<T>.waitDone()",this);try{get();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.ThreadPool$Worker<T>.waitDone()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.ThreadPool$Worker<T>.waitDone()",this,throwable);throw throwable;}
        }

        /*// Below are the methods for JobContext (only called from the*/
        /*// thread running the job)*/
        public synchronized void setCancelListener(CancelListener listener) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.ThreadPool$Worker<T>.setCancelListener(CancelListener)",this,listener);try{mCancelListener = listener;
            if (mIsCancelled && mCancelListener != null) {
                mCancelListener.onCancel();
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.ThreadPool$Worker<T>.setCancelListener(CancelListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.ThreadPool$Worker<T>.setCancelListener(CancelListener)",this,throwable);throw throwable;}
        }

        public boolean setMode(int mode) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.setMode(int)",this,mode);try{/*// Release old resource*/
            ResourceCounter rc = modeToCounter(mMode);
            if (rc != null) {releaseResource(rc);}
            mMode = MODE_NONE;

            /*// Acquire new resource*/
            rc = modeToCounter(mode);
            if (rc != null) {
                if (!acquireResource(rc)) {
                    {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.setMode(int)",this);return false;}
                }
                mMode = mode;
            }

            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.setMode(int)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.setMode(int)",this,throwable);throw throwable;}
        }

        private ResourceCounter modeToCounter(int mode) {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool$ResourceCounter com.android.gallery3d.util.ThreadPool$Worker<T>.modeToCounter(int)",this,mode);try{if (mode == MODE_CPU) {
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool$ResourceCounter com.android.gallery3d.util.ThreadPool$Worker<T>.modeToCounter(int)",this);return mCpuCounter;}
            } else if (mode == MODE_NETWORK) {
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool$ResourceCounter com.android.gallery3d.util.ThreadPool$Worker<T>.modeToCounter(int)",this);return mNetworkCounter;}
            } else {
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool$ResourceCounter com.android.gallery3d.util.ThreadPool$Worker<T>.modeToCounter(int)",this);return null;}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool$ResourceCounter com.android.gallery3d.util.ThreadPool$Worker<T>.modeToCounter(int)",this,throwable);throw throwable;}
        }

        private boolean acquireResource(ResourceCounter counter) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.acquireResource(com.android.gallery3d.util.ThreadPool$ResourceCounter)",this,counter);try{while (true) {
                synchronized (this) {
                    if (mIsCancelled) {
                        mWaitOnResource = null;
                        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.acquireResource(com.android.gallery3d.util.ThreadPool$ResourceCounter)",this);return false;}
                    }
                    mWaitOnResource = counter;
                }

                synchronized (counter) {
                    if (counter.value > 0) {
                        counter.value--;
                        break;
                    } else {
                        try {
                            counter.wait();
                        } catch (InterruptedException ex) {
                            /*// ignore.*/
                        }
                    }
                }
            }

            synchronized (this) {
                mWaitOnResource = null;
            }

            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.acquireResource(com.android.gallery3d.util.ThreadPool$ResourceCounter)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.util.ThreadPool$Worker<T>.acquireResource(com.android.gallery3d.util.ThreadPool$ResourceCounter)",this,throwable);throw throwable;}
        }

        private void releaseResource(ResourceCounter counter) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.ThreadPool$Worker<T>.releaseResource(com.android.gallery3d.util.ThreadPool$ResourceCounter)",this,counter);try{synchronized (counter) {
                counter.value++;
                counter.notifyAll();
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.ThreadPool$Worker<T>.releaseResource(com.android.gallery3d.util.ThreadPool$ResourceCounter)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.ThreadPool$Worker<T>.releaseResource(com.android.gallery3d.util.ThreadPool$ResourceCounter)",this,throwable);throw throwable;}
        }
    }
}
