/*
 * Copyright (C) 2011 The Android Open Source Project
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
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

import java.util.LinkedList;

/*// Limit the number of concurrent jobs that has been submitted into a ThreadPool*/
@SuppressWarnings("rawtypes")
public class JobLimiter implements FutureListener {
    private static final String TAG = "JobLimiter";

    /*// State Transition:*/
    /*//      INIT -> DONE, CANCELLED*/
    /*//      DONE -> CANCELLED*/
    private static final int STATE_INIT = 0;
    private static final int STATE_DONE = 1;
    private static final int STATE_CANCELLED = 2;

    private final LinkedList<JobWrapper<?>> mJobs = new LinkedList<JobWrapper<?>>();
    private final ThreadPool mPool;
    private int mLimit;

    private static class JobWrapper<T> implements Future<T>, Job<T> {
        private int mState = STATE_INIT;
        private Job<T> mJob;
        private Future<T> mDelegate;
        private FutureListener<T> mListener;
        private T mResult;

        public JobWrapper(Job<T> job, FutureListener<T> listener) {
            mJob = job;
            mListener = listener;
        }

        public synchronized void setFuture(Future<T> future) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.JobLimiter$JobWrapper<T>.setFuture(Future)",this,future);try{if (mState != STATE_INIT) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.JobLimiter$JobWrapper<T>.setFuture(Future)",this);return;}}
            mDelegate = future;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.JobLimiter$JobWrapper<T>.setFuture(Future)",this,throwable);throw throwable;}
        }

        @Override
        public void cancel() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.JobLimiter$JobWrapper<T>.cancel()",this);try{FutureListener<T> listener = null;
            synchronized (this) {
                if (mState != STATE_DONE) {
                    listener = mListener;
                    mJob = null;
                    mListener = null;
                    if (mDelegate != null) {
                        mDelegate.cancel();
                        mDelegate = null;
                    }
                }
                mState = STATE_CANCELLED;
                mResult = null;
                notifyAll();
            }
            if (listener != null) {listener.onFutureDone(this);}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.JobLimiter$JobWrapper<T>.cancel()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.JobLimiter$JobWrapper<T>.cancel()",this,throwable);throw throwable;}
        }

        @Override
        public synchronized boolean isCancelled() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.util.JobLimiter$JobWrapper<T>.isCancelled()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.JobLimiter$JobWrapper<T>.isCancelled()",this);return mState == STATE_CANCELLED;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.util.JobLimiter$JobWrapper<T>.isCancelled()",this,throwable);throw throwable;}
        }

        @Override
        public boolean isDone() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.util.JobLimiter$JobWrapper<T>.isDone()",this);try{/*// Both CANCELLED AND DONE is considered as done*/
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.JobLimiter$JobWrapper<T>.isDone()",this);return mState !=  STATE_INIT;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.util.JobLimiter$JobWrapper<T>.isDone()",this,throwable);throw throwable;}
        }

        @Override
        public synchronized T get() {
            com.mijack.Xlog.logMethodEnter("T com.android.gallery3d.util.JobLimiter$JobWrapper<T>.get()",this);try{while (mState == STATE_INIT) {
                /*// handle the interrupted exception of wait()*/
                Utils.waitWithoutInterrupt(this);
            }
            {com.mijack.Xlog.logMethodExit("T com.android.gallery3d.util.JobLimiter$JobWrapper<T>.get()",this);return mResult;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("T com.android.gallery3d.util.JobLimiter$JobWrapper<T>.get()",this,throwable);throw throwable;}
        }

        @Override
        public void waitDone() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.JobLimiter$JobWrapper<T>.waitDone()",this);try{get();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.JobLimiter$JobWrapper<T>.waitDone()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.JobLimiter$JobWrapper<T>.waitDone()",this,throwable);throw throwable;}
        }

        @Override
        public T run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("T com.android.gallery3d.util.JobLimiter$JobWrapper<T>.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{Job<T> job = null;
            synchronized (this) {
                if (mState == STATE_CANCELLED) {{com.mijack.Xlog.logMethodExit("T com.android.gallery3d.util.JobLimiter$JobWrapper<T>.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
                job = mJob;
            }
            T result  = null;
            try {
                result = job.run(jc);
            } catch (Throwable t) {
                Log.w(TAG, "error executing job: " + job, t);
            }
            FutureListener<T> listener = null;
            synchronized (this) {
                if (mState == STATE_CANCELLED) {{com.mijack.Xlog.logMethodExit("T com.android.gallery3d.util.JobLimiter$JobWrapper<T>.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
                mState = STATE_DONE;
                listener = mListener;
                mListener = null;
                mJob = null;
                mResult = result;
                notifyAll();
            }
            if (listener != null) {listener.onFutureDone(this);}
            {com.mijack.Xlog.logMethodExit("T com.android.gallery3d.util.JobLimiter$JobWrapper<T>.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("T com.android.gallery3d.util.JobLimiter$JobWrapper<T>.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }

    public JobLimiter(ThreadPool pool, int limit) {
        mPool = Utils.checkNotNull(pool);
        mLimit = limit;
    }

    public synchronized <T> Future<T> submit(Job<T> job, FutureListener<T> listener) {
        com.mijack.Xlog.logMethodEnter("Future com.android.gallery3d.util.JobLimiter.submit(com.android.gallery3d.util.ThreadPool.Job,FutureListener)",this,job,listener);try{JobWrapper<T> future = new JobWrapper<T>(Utils.checkNotNull(job), listener);
        mJobs.addLast(future);
        submitTasksIfAllowed();
        {com.mijack.Xlog.logMethodExit("Future com.android.gallery3d.util.JobLimiter.submit(com.android.gallery3d.util.ThreadPool.Job,FutureListener)",this);return future;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Future com.android.gallery3d.util.JobLimiter.submit(com.android.gallery3d.util.ThreadPool.Job,FutureListener)",this,throwable);throw throwable;}
    }

    public <T> Future<T> submit(Job<T> job) {
        com.mijack.Xlog.logMethodEnter("Future com.android.gallery3d.util.JobLimiter.submit(com.android.gallery3d.util.ThreadPool.Job)",this,job);try{com.mijack.Xlog.logMethodExit("Future com.android.gallery3d.util.JobLimiter.submit(com.android.gallery3d.util.ThreadPool.Job)",this);return submit(job, null);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Future com.android.gallery3d.util.JobLimiter.submit(com.android.gallery3d.util.ThreadPool.Job)",this,throwable);throw throwable;}
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void submitTasksIfAllowed() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.JobLimiter.submitTasksIfAllowed()",this);try{while (mLimit > 0 && !mJobs.isEmpty()) {
            JobWrapper wrapper = mJobs.removeFirst();
            if (!wrapper.isCancelled()) {
                --mLimit;
                wrapper.setFuture(mPool.submit(wrapper, this));
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.JobLimiter.submitTasksIfAllowed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.JobLimiter.submitTasksIfAllowed()",this,throwable);throw throwable;}
    }

    @Override
    public synchronized void onFutureDone(Future future) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.JobLimiter.onFutureDone(Future)",this,future);try{++mLimit;
        submitTasksIfAllowed();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.JobLimiter.onFutureDone(Future)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.JobLimiter.onFutureDone(Future)",this,throwable);throw throwable;}
    }
}
