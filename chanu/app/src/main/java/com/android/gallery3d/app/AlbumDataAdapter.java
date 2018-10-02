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

import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.ui.AlbumView;
import com.android.gallery3d.ui.SynchronizedHandler;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class AlbumDataAdapter implements AlbumView.Model {
    @SuppressWarnings("unused")
    private static final String TAG = "AlbumDataAdapter";
    private static final int DATA_CACHE_SIZE = 1000;

    private static final int MSG_LOAD_START = 1;
    private static final int MSG_LOAD_FINISH = 2;
    private static final int MSG_RUN_OBJECT = 3;

    private static final int MIN_LOAD_COUNT = 32;
    private static final int MAX_LOAD_COUNT = 64;

    private final MediaItem[] mData;
    private final long[] mItemVersion;
    private final long[] mSetVersion;

    private int mActiveStart = 0;
    private int mActiveEnd = 0;

    private int mContentStart = 0;
    private int mContentEnd = 0;

    private final MediaSet mSource;
    private long mSourceVersion = MediaObject.INVALID_DATA_VERSION;

    private final Handler mMainHandler;
    private int mSize = 0;

    private AlbumView.ModelListener mModelListener;
    private MySourceListener mSourceListener = new MySourceListener();
    private LoadingListener mLoadingListener;

    private ReloadTask mReloadTask;

    public AlbumDataAdapter(GalleryActivity context, MediaSet mediaSet) {
        mSource = mediaSet;

        mData = new MediaItem[DATA_CACHE_SIZE];
        mItemVersion = new long[DATA_CACHE_SIZE];
        mSetVersion = new long[DATA_CACHE_SIZE];
        Arrays.fill(mItemVersion, MediaObject.INVALID_DATA_VERSION);
        Arrays.fill(mSetVersion, MediaObject.INVALID_DATA_VERSION);

        mMainHandler = new SynchronizedHandler(context.getGLRoot()) {
            @Override
            public void handleMessage(Message message) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter$1.handleMessage(android.os.Message)",this,message);try{switch (message.what) {
                    case MSG_RUN_OBJECT:
                        ((Runnable) message.obj).run();
                        {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter$1.handleMessage(android.os.Message)",this);return;}
                    case MSG_LOAD_START:
                        if (mLoadingListener != null) {mLoadingListener.onLoadingStarted();}
                        {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter$1.handleMessage(android.os.Message)",this);return;}
                    case MSG_LOAD_FINISH:
                        if (mLoadingListener != null) {mLoadingListener.onLoadingFinished();}
                        {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter$1.handleMessage(android.os.Message)",this);return;}
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter$1.handleMessage(android.os.Message)",this,throwable);throw throwable;}
            }
        };
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter.resume()",this);try{mSource.addContentListener(mSourceListener);
        mReloadTask = new ReloadTask();
        if (mReloadTask != null)
            {mReloadTask.start();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter.resume()",this,throwable);throw throwable;}
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter.pause()",this);try{if (mReloadTask != null)
            {mReloadTask.terminate();}
        mReloadTask = null;
        mSource.removeContentListener(mSourceListener);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter.pause()",this,throwable);throw throwable;}
    }

    public MediaItem get(int index) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.AlbumDataAdapter.get(int)",this,index);try{if (!isActive(index)) {
            throw new IllegalArgumentException(String.format(
                    "%s not in (%s, %s)", index, mActiveStart, mActiveEnd));
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.AlbumDataAdapter.get(int)",this);return mData[index % mData.length];}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.AlbumDataAdapter.get(int)",this,throwable);throw throwable;}
    }

    public int getActiveStart() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.AlbumDataAdapter.getActiveStart()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AlbumDataAdapter.getActiveStart()",this);return mActiveStart;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.AlbumDataAdapter.getActiveStart()",this,throwable);throw throwable;}
    }

    public int getActiveEnd() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.AlbumDataAdapter.getActiveEnd()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AlbumDataAdapter.getActiveEnd()",this);return mActiveEnd;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.AlbumDataAdapter.getActiveEnd()",this,throwable);throw throwable;}
    }

    public boolean isActive(int index) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.AlbumDataAdapter.isActive(int)",this,index);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumDataAdapter.isActive(int)",this);return index >= mActiveStart && index < mActiveEnd;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.AlbumDataAdapter.isActive(int)",this,throwable);throw throwable;}
    }

    public int size() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.AlbumDataAdapter.size()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AlbumDataAdapter.size()",this);return mSize;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.AlbumDataAdapter.size()",this,throwable);throw throwable;}
    }

    private void clearSlot(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter.clearSlot(int)",this,slotIndex);try{mData[slotIndex] = null;
        mItemVersion[slotIndex] = MediaObject.INVALID_DATA_VERSION;
        mSetVersion[slotIndex] = MediaObject.INVALID_DATA_VERSION;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter.clearSlot(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter.clearSlot(int)",this,throwable);throw throwable;}
    }

    private void setContentWindow(int contentStart, int contentEnd) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter.setContentWindow(int,int)",this,contentStart,contentEnd);try{if (contentStart == mContentStart && contentEnd == mContentEnd) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter.setContentWindow(int,int)",this);return;}}
        int end = mContentEnd;
        int start = mContentStart;

        /*// We need change the content window before calling reloadData(...)*/
        synchronized (this) {
            mContentStart = contentStart;
            mContentEnd = contentEnd;
        }
        long[] itemVersion = mItemVersion;
        long[] setVersion = mSetVersion;
        if (contentStart >= end || start >= contentEnd) {
            for (int i = start, n = end; i < n; ++i) {
                clearSlot(i % DATA_CACHE_SIZE);
            }
        } else {
            for (int i = start; i < contentStart; ++i) {
                clearSlot(i % DATA_CACHE_SIZE);
            }
            for (int i = contentEnd, n = end; i < n; ++i) {
                clearSlot(i % DATA_CACHE_SIZE);
            }
        }
        if (mReloadTask != null) {mReloadTask.notifyDirty();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter.setContentWindow(int,int)",this,throwable);throw throwable;}
    }

    public void setActiveWindow(int start, int end) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter.setActiveWindow(int,int)",this,start,end);try{if (start == mActiveStart && end == mActiveEnd) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter.setActiveWindow(int,int)",this);return;}}

        Utils.assertTrue(start <= end
                && end - start <= mData.length && end <= mSize);

        int length = mData.length;
        mActiveStart = start;
        mActiveEnd = end;

        /*// If no data is visible, keep the cache content*/
        if (start == end) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter.setActiveWindow(int,int)",this);return;}}

        int contentStart = Utils.clamp((start + end) / 2 - length / 2,
                0, Math.max(0, mSize - length));
        int contentEnd = Math.min(contentStart + length, mSize);
        if (mContentStart > start || mContentEnd < end
                || Math.abs(contentStart - mContentStart) > MIN_LOAD_COUNT) {
            setContentWindow(contentStart, contentEnd);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter.setActiveWindow(int,int)",this,throwable);throw throwable;}
    }

    private class MySourceListener implements ContentListener {
        public void onContentDirty() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter$MySourceListener.onContentDirty()",this);try{if (mReloadTask != null) {mReloadTask.notifyDirty();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter$MySourceListener.onContentDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter$MySourceListener.onContentDirty()",this,throwable);throw throwable;}
        }
    }

    public void setModelListener(AlbumView.ModelListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter.setModelListener(AlbumView.ModelListener)",this,listener);try{mModelListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter.setModelListener(AlbumView.ModelListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter.setModelListener(AlbumView.ModelListener)",this,throwable);throw throwable;}
    }

    public void setLoadingListener(LoadingListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter.setLoadingListener(LoadingListener)",this,listener);try{mLoadingListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter.setLoadingListener(LoadingListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter.setLoadingListener(LoadingListener)",this,throwable);throw throwable;}
    }

    private <T> T executeAndWait(Callable<T> callable) {
        com.mijack.Xlog.logMethodEnter("java.lang.Object com.android.gallery3d.app.AlbumDataAdapter.executeAndWait(java.util.concurrent.Callable)",this,callable);try{FutureTask<T> task = new FutureTask<T>(callable);
        mMainHandler.sendMessage(
                mMainHandler.obtainMessage(MSG_RUN_OBJECT, task));
        try {
            {com.mijack.Xlog.logMethodExit("java.lang.Object com.android.gallery3d.app.AlbumDataAdapter.executeAndWait(java.util.concurrent.Callable)",this);return task.get();}
        } catch (InterruptedException e) {
            {com.mijack.Xlog.logMethodExit("java.lang.Object com.android.gallery3d.app.AlbumDataAdapter.executeAndWait(java.util.concurrent.Callable)",this);return null;}
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Object com.android.gallery3d.app.AlbumDataAdapter.executeAndWait(java.util.concurrent.Callable)",this,throwable);throw throwable;}
    }

    private static class UpdateInfo {
        public long version;
        public int reloadStart;
        public int reloadCount;

        public int size;
        public ArrayList<MediaItem> items;
    }

    private class GetUpdateInfo implements Callable<UpdateInfo> {
        private final long mVersion;

        public GetUpdateInfo(long version) {
            mVersion = version;
        }

        public UpdateInfo call() throws Exception {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.AlbumDataAdapter$UpdateInfo com.android.gallery3d.app.AlbumDataAdapter$GetUpdateInfo.call()",this);try{UpdateInfo info = new UpdateInfo();
            long version = mVersion;
            info.version = mSourceVersion;
            info.size = mSize;
            long setVersion[] = mSetVersion;
            for (int i = mContentStart, n = mContentEnd; i < n; ++i) {
                int index = i % DATA_CACHE_SIZE;
                if (setVersion[index] != version) {
                    info.reloadStart = i;
                    info.reloadCount = Math.min(MAX_LOAD_COUNT, n - i);
                    {com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.AlbumDataAdapter$UpdateInfo com.android.gallery3d.app.AlbumDataAdapter$GetUpdateInfo.call()",this);return info;}
                }
            }
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.AlbumDataAdapter$UpdateInfo com.android.gallery3d.app.AlbumDataAdapter$GetUpdateInfo.call()",this);return mSourceVersion == mVersion ? null : info;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.AlbumDataAdapter$UpdateInfo com.android.gallery3d.app.AlbumDataAdapter$GetUpdateInfo.call()",this,throwable);throw throwable;}
        }
    }

    private class UpdateContent implements Callable<Void> {

        private UpdateInfo mUpdateInfo;

        public UpdateContent(UpdateInfo info) {
            mUpdateInfo = info;
        }

        @Override
        public Void call() throws Exception {
            com.mijack.Xlog.logMethodEnter("java.lang.Void com.android.gallery3d.app.AlbumDataAdapter$UpdateContent.call()",this);try{UpdateInfo info = mUpdateInfo;
            mSourceVersion = info.version;
            if (mSize != info.size) {
                mSize = info.size;
                if (mModelListener != null) {mModelListener.onSizeChanged(mSize);}
                if (mContentEnd > mSize) {mContentEnd = mSize;}
                if (mActiveEnd > mSize) {mActiveEnd = mSize;}
            }

            ArrayList<MediaItem> items = info.items;

            if (items == null) {{com.mijack.Xlog.logMethodExit("java.lang.Void com.android.gallery3d.app.AlbumDataAdapter$UpdateContent.call()",this);return null;}}
            int start = Math.max(info.reloadStart, mContentStart);
            int end = Math.min(info.reloadStart + items.size(), mContentEnd);

            for (int i = start; i < end; ++i) {
                int index = i % DATA_CACHE_SIZE;
                mSetVersion[index] = info.version;
                MediaItem updateItem = items.get(i - info.reloadStart);
                long itemVersion = updateItem.getDataVersion();
                if (mItemVersion[index] != itemVersion) {
                    mItemVersion[index] = itemVersion;
                    mData[index] = updateItem;
                    if (mModelListener != null && i >= mActiveStart && i < mActiveEnd) {
                        mModelListener.onWindowContentChanged(i);
                    }
                }
            }
            {com.mijack.Xlog.logMethodExit("java.lang.Void com.android.gallery3d.app.AlbumDataAdapter$UpdateContent.call()",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Void com.android.gallery3d.app.AlbumDataAdapter$UpdateContent.call()",this,throwable);throw throwable;}
        }
    }

    /*
     * The thread model of ReloadTask
     *      *
     * [Reload Task]       [Main Thread]
     *       |                   |
     * getUpdateInfo() -->       |           (synchronous call)
     *     (wait) <----    getUpdateInfo()
     *       |                   |
     *   Load Data               |
     *       |                   |
     * updateContent() -->       |           (synchronous call)
     *     (wait)          updateContent()
     *       |                   |
     *       |                   |
     */
    private class ReloadTask extends Thread {

        private volatile boolean mActive = true;
        private volatile boolean mDirty = true;
        private boolean mIsLoading = false;

        private void updateLoading(boolean loading) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.updateLoading(boolean)",this,loading);try{if (mIsLoading == loading) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.updateLoading(boolean)",this);return;}}
            mIsLoading = loading;
            mMainHandler.sendEmptyMessage(loading ? MSG_LOAD_START : MSG_LOAD_FINISH);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.updateLoading(boolean)",this,throwable);throw throwable;}
        }

        @Override
        public void run() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.run()",this);try{boolean updateComplete = false;
            while (mActive) {
                synchronized (this) {
                    if (mActive && !mDirty && updateComplete) {
                        updateLoading(false);
                        Utils.waitWithoutInterrupt(this);
                        continue;
                    }
                }
                mDirty = false;
                updateLoading(true);
                long version;
                synchronized (DataManager.LOCK) {
                    version = mSource.reload();
                }
                UpdateInfo info = executeAndWait(new GetUpdateInfo(version));
                updateComplete = info == null;
                if (updateComplete) {continue;}
                synchronized (DataManager.LOCK) {
                    if (info.version != version) {
                        info.size = mSource.getMediaItemCount();
                        info.version = version;
                    }
                    if (info.reloadCount > 0) {
                        info.items = mSource.getMediaItem(info.reloadStart, info.reloadCount);
                    }
                }
                executeAndWait(new UpdateContent(info));
            }
            updateLoading(false);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.run()",this,throwable);throw throwable;}
        }

        public synchronized void notifyDirty() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.notifyDirty()",this);try{mDirty = true;
            notifyAll();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.notifyDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.notifyDirty()",this,throwable);throw throwable;}
        }

        public synchronized void terminate() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.terminate()",this);try{mActive = false;
            notifyAll();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.terminate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumDataAdapter$ReloadTask.terminate()",this,throwable);throw throwable;}
        }
    }
}
