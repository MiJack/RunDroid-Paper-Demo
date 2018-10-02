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

package com.android.gallery3d.app;

import com.android.gallery3d.app.SlideshowPage.Slide;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

import android.graphics.Bitmap;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlideshowDataAdapter implements SlideshowPage.Model {
    @SuppressWarnings("unused")
    private static final String TAG = "SlideshowDataAdapter";

    private static final int IMAGE_QUEUE_CAPACITY = 3;

    public interface SlideshowSource {
        public void addContentListener(ContentListener listener);
        public void removeContentListener(ContentListener listener);
        public long reload();
        public MediaItem getMediaItem(int index);
    }

    private final SlideshowSource mSource;

    private int mLoadIndex = 0;
    private int mNextOutput = 0;
    private boolean mIsActive = false;
    private boolean mNeedReset;
    private boolean mDataReady;

    private final LinkedList<Slide> mImageQueue = new LinkedList<Slide>();

    private Future<Void> mReloadTask;
    private final ThreadPool mThreadPool;

    private long mDataVersion = MediaObject.INVALID_DATA_VERSION;
    private final AtomicBoolean mNeedReload = new AtomicBoolean(false);
    private final SourceListener mSourceListener = new SourceListener();

    public SlideshowDataAdapter(GalleryContext context, SlideshowSource source, int index) {
        mSource = source;
        mLoadIndex = index;
        mNextOutput = index;
        mThreadPool = context.getThreadPool();
    }

    private MediaItem loadItem() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowDataAdapter.loadItem()",this);try{if (mNeedReload.compareAndSet(true, false)) {
            long v = mSource.reload();
            if (v != mDataVersion) {
                mDataVersion = v;
                mNeedReset = true;
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowDataAdapter.loadItem()",this);return null;}
            }
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowDataAdapter.loadItem()",this);return mSource.getMediaItem(mLoadIndex);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowDataAdapter.loadItem()",this,throwable);throw throwable;}
    }

    private class ReloadTask implements Job<Void> {
        public Void run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("java.lang.Void com.android.gallery3d.app.SlideshowDataAdapter$ReloadTask.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{com.mijack.Xlog.logMethodExit("java.lang.Void com.android.gallery3d.app.SlideshowDataAdapter$ReloadTask.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);while (true) {
                synchronized (SlideshowDataAdapter.this) {
                    while (mIsActive && (!mDataReady
                            || mImageQueue.size() >= IMAGE_QUEUE_CAPACITY)) {
                        try {
                            SlideshowDataAdapter.this.wait();
                        } catch (InterruptedException ex) {
                            /*// ignored.*/
                        }
                        continue;
                    }
                }
                if (!mIsActive) {return null;}
                mNeedReset = false;

                MediaItem item = loadItem();

                if (mNeedReset) {
                    synchronized (SlideshowDataAdapter.this) {
                        mImageQueue.clear();
                        mLoadIndex = mNextOutput;
                    }
                    continue;
                }

                if (item == null) {
                    synchronized (SlideshowDataAdapter.this) {
                        if (!mNeedReload.get()) {mDataReady = false;}
                        SlideshowDataAdapter.this.notifyAll();
                    }
                    continue;
                }

                Bitmap bitmap = item
                        .requestImage(MediaItem.TYPE_THUMBNAIL)
                        .run(jc);

                if (bitmap != null) {
                    synchronized (SlideshowDataAdapter.this) {
                        mImageQueue.addLast(
                                new Slide(item, mLoadIndex, bitmap));
                        if (mImageQueue.size() == 1) {
                            SlideshowDataAdapter.this.notifyAll();
                        }
                    }
                }
                ++mLoadIndex;
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Void com.android.gallery3d.app.SlideshowDataAdapter$ReloadTask.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }

    private class SourceListener implements ContentListener {
        public void onContentDirty() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowDataAdapter$SourceListener.onContentDirty()",this);try{synchronized (SlideshowDataAdapter.this) {
                mNeedReload.set(true);
                mDataReady = true;
                SlideshowDataAdapter.this.notifyAll();
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowDataAdapter$SourceListener.onContentDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowDataAdapter$SourceListener.onContentDirty()",this,throwable);throw throwable;}
        }
    }

    private synchronized Slide innerNextBitmap() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.SlideshowPage.Slide com.android.gallery3d.app.SlideshowDataAdapter.innerNextBitmap()",this);try{while (mIsActive && mDataReady && mImageQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException t) {
                throw new AssertionError();
            }
        }
        if (mImageQueue.isEmpty()) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.SlideshowPage.Slide com.android.gallery3d.app.SlideshowDataAdapter.innerNextBitmap()",this);return null;}}
        mNextOutput++;
        this.notifyAll();
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.SlideshowPage.Slide com.android.gallery3d.app.SlideshowDataAdapter.innerNextBitmap()",this);return mImageQueue.removeFirst();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.SlideshowPage.Slide com.android.gallery3d.app.SlideshowDataAdapter.innerNextBitmap()",this,throwable);throw throwable;}
    }

    public Future<Slide> nextSlide(FutureListener<Slide> listener) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.Future com.android.gallery3d.app.SlideshowDataAdapter.nextSlide(com.android.gallery3d.util.FutureListener)",this,listener);try{{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.app.SlideshowDataAdapter.nextSlide(com.android.gallery3d.util.FutureListener)",this);return mThreadPool.submit(new Job<Slide>() {
            public Slide run(JobContext jc) {
                com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.SlideshowPage.Slide com.android.gallery3d.app.SlideshowDataAdapter$1.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{jc.setMode(ThreadPool.MODE_NONE);
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.app.SlideshowDataAdapter.nextSlide(com.android.gallery3d.util.FutureListener)",this);{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.SlideshowPage.Slide com.android.gallery3d.app.SlideshowDataAdapter$1.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return innerNextBitmap();}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.SlideshowPage.Slide com.android.gallery3d.app.SlideshowDataAdapter$1.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
            }
        }, listener);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.Future com.android.gallery3d.app.SlideshowDataAdapter.nextSlide(com.android.gallery3d.util.FutureListener)",this,throwable);throw throwable;}
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowDataAdapter.pause()",this);try{synchronized (this) {
            mIsActive = false;
            notifyAll();
        }
        mSource.removeContentListener(mSourceListener);
        mReloadTask.cancel();
        mReloadTask.waitDone();
        mReloadTask = null;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowDataAdapter.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowDataAdapter.pause()",this,throwable);throw throwable;}
    }

    public synchronized void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowDataAdapter.resume()",this);try{mIsActive = true;
        mSource.addContentListener(mSourceListener);
        mNeedReload.set(true);
        mDataReady = true;
        mReloadTask = mThreadPool.submit(new ReloadTask());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowDataAdapter.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowDataAdapter.resume()",this,throwable);throw throwable;}
    }
}
