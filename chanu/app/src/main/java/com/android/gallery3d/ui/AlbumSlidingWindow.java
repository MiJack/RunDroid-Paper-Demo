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

package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.os.Message;

import com.android.gallery3d.app.GalleryActivity;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.LruCache;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.JobLimiter;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

public class AlbumSlidingWindow implements AlbumView.ModelListener {
    @SuppressWarnings("unused")
    private static final String TAG = "AlbumSlidingWindow";

    private static final int MSG_LOAD_BITMAP_DONE = 0;
    private static final int MSG_UPDATE_SLOT = 1;
    private static final int JOB_LIMIT = 2;
    private static final int PLACEHOLDER_COLOR = 0xFF222222;

    public static interface Listener {
        public void onSizeChanged(int size);
        public void onContentInvalidated();
        public void onWindowContentChanged(
                int slot, DisplayItem old, DisplayItem update);
    }

    private final AlbumView.Model mSource;
    private int mSize;

    private int mContentStart = 0;
    private int mContentEnd = 0;

    private int mActiveStart = 0;
    private int mActiveEnd = 0;

    private Listener mListener;
    private int mFocusIndex = -1;

    private final AlbumDisplayItem mData[];
    private final ColorTexture mWaitLoadingTexture;
    private SelectionDrawer mSelectionDrawer;

    private SynchronizedHandler mHandler;
    private JobLimiter mThreadPool;

    private int mActiveRequestCount = 0;
    private boolean mIsActive = false;

    private int mCacheThumbSize;  /*// 0: Don't cache the thumbnails*/
    private LruCache<Path, Bitmap> mImageCache = new LruCache<Path, Bitmap>(1000);

    public AlbumSlidingWindow(GalleryActivity activity,
            AlbumView.Model source, int cacheSize,
            int cacheThumbSize) {
        source.setModelListener(this);
        mSource = source;
        mData = new AlbumDisplayItem[cacheSize];
        mSize = source.size();

        mWaitLoadingTexture = new ColorTexture(PLACEHOLDER_COLOR);
        mWaitLoadingTexture.setSize(1, 1);

        mHandler = new SynchronizedHandler(activity.getGLRoot()) {
            @Override
            public void handleMessage(Message message) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow$1.handleMessage(android.os.Message)",this,message);try{switch (message.what) {
                    case MSG_LOAD_BITMAP_DONE: {
                        ((AlbumDisplayItem) message.obj).onLoadBitmapDone();
                        break;
                    }
                    case MSG_UPDATE_SLOT: {
                        updateSlotContent(message.arg1);
                        break;
                    }
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow$1.handleMessage(android.os.Message)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow$1.handleMessage(android.os.Message)",this,throwable);throw throwable;}
            }
        };

        mThreadPool = new JobLimiter(activity.getThreadPool(), JOB_LIMIT);
    }

    public void setSelectionDrawer(SelectionDrawer drawer) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this,drawer);try{mSelectionDrawer = drawer;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this,throwable);throw throwable;}
    }

    public void setListener(Listener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.setListener(com.android.gallery3d.util.FutureListener)",this,listener);try{mListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.setListener(com.android.gallery3d.util.FutureListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.setListener(com.android.gallery3d.util.FutureListener)",this,throwable);throw throwable;}
    }

    public void setFocusIndex(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.setFocusIndex(int)",this,slotIndex);try{mFocusIndex = slotIndex;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.setFocusIndex(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.setFocusIndex(int)",this,throwable);throw throwable;}
    }

    public DisplayItem get(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.DisplayItem com.android.gallery3d.ui.AlbumSlidingWindow.get(int)",this,slotIndex);try{Utils.assertTrue(isActiveSlot(slotIndex),
                "invalid slot: %s outsides (%s, %s)",
                slotIndex, mActiveStart, mActiveEnd);
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.DisplayItem com.android.gallery3d.ui.AlbumSlidingWindow.get(int)",this);return mData[slotIndex % mData.length];}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.DisplayItem com.android.gallery3d.ui.AlbumSlidingWindow.get(int)",this,throwable);throw throwable;}
    }

    public int size() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.AlbumSlidingWindow.size()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.AlbumSlidingWindow.size()",this);return mSize;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.AlbumSlidingWindow.size()",this,throwable);throw throwable;}
    }

    public boolean isActiveSlot(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.AlbumSlidingWindow.isActiveSlot(int)",this,slotIndex);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.AlbumSlidingWindow.isActiveSlot(int)",this);return slotIndex >= mActiveStart && slotIndex < mActiveEnd;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.AlbumSlidingWindow.isActiveSlot(int)",this,throwable);throw throwable;}
    }

    private void setContentWindow(int contentStart, int contentEnd) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.setContentWindow(int,int)",this,contentStart,contentEnd);try{if (contentStart == mContentStart && contentEnd == mContentEnd) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.setContentWindow(int,int)",this);return;}}

        if (!mIsActive) {
            mContentStart = contentStart;
            mContentEnd = contentEnd;
            mSource.setActiveWindow(contentStart, contentEnd);
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.setContentWindow(int,int)",this);return;}
        }

        if (contentStart >= mContentEnd || mContentStart >= contentEnd) {
            for (int i = mContentStart, n = mContentEnd; i < n; ++i) {
                freeSlotContent(i);
            }
            mSource.setActiveWindow(contentStart, contentEnd);
            for (int i = contentStart; i < contentEnd; ++i) {
                prepareSlotContent(i);
            }
        } else {
            for (int i = mContentStart; i < contentStart; ++i) {
                freeSlotContent(i);
            }
            for (int i = contentEnd, n = mContentEnd; i < n; ++i) {
                freeSlotContent(i);
            }
            mSource.setActiveWindow(contentStart, contentEnd);
            for (int i = contentStart, n = mContentStart; i < n; ++i) {
                prepareSlotContent(i);
            }
            for (int i = mContentEnd; i < contentEnd; ++i) {
                prepareSlotContent(i);
            }
        }

        mContentStart = contentStart;
        mContentEnd = contentEnd;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.setContentWindow(int,int)",this,throwable);throw throwable;}
    }

    public void setActiveWindow(int start, int end) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.setActiveWindow(int,int)",this,start,end);try{Utils.assertTrue(start <= end
                && end - start <= mData.length && end <= mSize,
                "%s, %s, %s, %s", start, end, mData.length, mSize);
        DisplayItem data[] = mData;

        mActiveStart = start;
        mActiveEnd = end;

        int contentStart = Utils.clamp((start + end) / 2 - data.length / 2,
                0, Math.max(0, mSize - data.length));
        int contentEnd = Math.min(contentStart + data.length, mSize);
        setContentWindow(contentStart, contentEnd);
        if (mIsActive) {updateAllImageRequests();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.setActiveWindow(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.setActiveWindow(int,int)",this,throwable);throw throwable;}
    }

    /*// We would like to request non active slots in the following order:*/
    /*// Order:    8 6 4 2                   1 3 5 7*/
    /*//         |---------|---------------|---------|*/
    /*//                   |<-  active  ->|*/
    /*//         |<-------- cached range ----------->|*/
    private void requestNonactiveImages() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.requestNonactiveImages()",this);try{int range = Math.max(
                (mContentEnd - mActiveEnd), (mActiveStart - mContentStart));
        for (int i = 0 ;i < range; ++i) {
            requestSlotImage(mActiveEnd + i, false);
            requestSlotImage(mActiveStart - 1 - i, false);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.requestNonactiveImages()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.requestNonactiveImages()",this,throwable);throw throwable;}
    }

    private void requestSlotImage(int slotIndex, boolean isActive) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.requestSlotImage(int,boolean)",this,slotIndex,isActive);try{if (slotIndex < mContentStart || slotIndex >= mContentEnd) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.requestSlotImage(int,boolean)",this);return;}}
        AlbumDisplayItem item = mData[slotIndex % mData.length];
        item.requestImage();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.requestSlotImage(int,boolean)",this,throwable);throw throwable;}
    }

    private void cancelNonactiveImages() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.cancelNonactiveImages()",this);try{int range = Math.max(
                (mContentEnd - mActiveEnd), (mActiveStart - mContentStart));
        for (int i = 0 ;i < range; ++i) {
            cancelSlotImage(mActiveEnd + i, false);
            cancelSlotImage(mActiveStart - 1 - i, false);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.cancelNonactiveImages()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.cancelNonactiveImages()",this,throwable);throw throwable;}
    }

    private void cancelSlotImage(int slotIndex, boolean isActive) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.cancelSlotImage(int,boolean)",this,slotIndex,isActive);try{if (slotIndex < mContentStart || slotIndex >= mContentEnd) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.cancelSlotImage(int,boolean)",this);return;}}
        AlbumDisplayItem item = mData[slotIndex % mData.length];
        item.cancelImageRequest();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.cancelSlotImage(int,boolean)",this,throwable);throw throwable;}
    }

    private void freeSlotContent(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.freeSlotContent(int)",this,slotIndex);try{AlbumDisplayItem data[] = mData;
        int index = slotIndex % data.length;
        AlbumDisplayItem original = data[index];
        if (original != null) {
            original.recycle();
            data[index] = null;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.freeSlotContent(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.freeSlotContent(int)",this,throwable);throw throwable;}
    }

    private void prepareSlotContent(final int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.prepareSlotContent(int)",this,slotIndex);try{mData[slotIndex % mData.length] = new AlbumDisplayItem(
                slotIndex, mSource.get(slotIndex));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.prepareSlotContent(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.prepareSlotContent(int)",this,throwable);throw throwable;}
    }

    private void updateSlotContent(final int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.updateSlotContent(int)",this,slotIndex);try{MediaItem item = mSource.get(slotIndex);
        AlbumDisplayItem data[] = mData;
        int index = slotIndex % data.length;
        AlbumDisplayItem original = data[index];
        AlbumDisplayItem update = new AlbumDisplayItem(slotIndex, item);
        data[index] = update;
        boolean isActive = isActiveSlot(slotIndex);
        if (mListener != null && isActive) {
            mListener.onWindowContentChanged(slotIndex, original, update);
        }
        if (original != null) {
            if (isActive && original.isRequestInProgress()) {
                --mActiveRequestCount;
            }
            original.recycle();
        }
        if (isActive) {
            if (mActiveRequestCount == 0) {cancelNonactiveImages();}
            ++mActiveRequestCount;
            update.requestImage();
        } else {
            if (mActiveRequestCount == 0) {update.requestImage();}
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.updateSlotContent(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.updateSlotContent(int)",this,throwable);throw throwable;}
    }

    private void updateAllImageRequests() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.updateAllImageRequests()",this);try{mActiveRequestCount = 0;
        AlbumDisplayItem data[] = mData;
        for (int i = mActiveStart, n = mActiveEnd; i < n; ++i) {
            AlbumDisplayItem item = data[i % data.length];
            item.requestImage();
            if (item.isRequestInProgress()) {++mActiveRequestCount;}
        }
        if (mActiveRequestCount == 0) {
            requestNonactiveImages();
        } else {
            cancelNonactiveImages();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.updateAllImageRequests()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.updateAllImageRequests()",this,throwable);throw throwable;}
    }

    private class AlbumDisplayItem extends AbstractDisplayItem
            implements FutureListener<Bitmap>, Job<Bitmap> {
        private Future<Bitmap> mFuture;
        private final int mSlotIndex;
        private final int mMediaType;
        private Texture mContent;
        private boolean mIsPanorama;
        private boolean mWaitLoadingDisplayed;

        public AlbumDisplayItem(int slotIndex, MediaItem item) {
            super(item);
            mMediaType = (item == null)
                    ? MediaItem.MEDIA_TYPE_UNKNOWN
                    : item.getMediaType();
            mSlotIndex = slotIndex;
            mIsPanorama = GalleryUtils.isPanorama(item);
            updateContent(mWaitLoadingTexture);
        }

        @Override
        protected void onBitmapAvailable(Bitmap bitmap) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.onBitmapAvailable(android.graphics.Bitmap)",this,bitmap);try{boolean isActiveSlot = isActiveSlot(mSlotIndex);
            if (isActiveSlot) {
                --mActiveRequestCount;
                if (mActiveRequestCount == 0) {requestNonactiveImages();}
            }
            if (bitmap != null) {
                BitmapTexture texture = new BitmapTexture(bitmap, true);
                texture.setThrottled(true);
                if (mWaitLoadingDisplayed) {
                    updateContent(new FadeInTexture(PLACEHOLDER_COLOR, texture));
                } else {
                    updateContent(texture);
                }
                if (mListener != null && isActiveSlot) {
                    mListener.onContentInvalidated();
                }
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.onBitmapAvailable(android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.onBitmapAvailable(android.graphics.Bitmap)",this,throwable);throw throwable;}
        }

        private void updateContent(Texture content) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.updateContent(Texture)",this,content);try{mContent = content;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.updateContent(Texture)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.updateContent(Texture)",this,throwable);throw throwable;}
        }

        @Override
        public int render(GLCanvas canvas, int pass) {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.render(GLCanvas,int)",this,canvas,pass);try{/*// Fit the content into the box*/
            int width = mContent.getWidth();
            int height = mContent.getHeight();

            float scalex = mBoxWidth / (float) width;
            float scaley = mBoxHeight / (float) height;
            float scale = Math.min(scalex, scaley);

            width = (int) Math.floor(width * scale);
            height = (int) Math.floor(height * scale);

            /*// Now draw it*/
            if (pass == 0) {
                Path path = null;
                if (mMediaItem != null) {path = mMediaItem.getPath();}
                mSelectionDrawer.draw(canvas, mContent, width, height,
                        getRotation(), path, mMediaType, mIsPanorama);
                if (mContent == mWaitLoadingTexture) {
                       mWaitLoadingDisplayed = true;
                }
                int result = 0;
                if (mFocusIndex == mSlotIndex) {
                    result |= RENDER_MORE_PASS;
                }
                if ((mContent instanceof FadeInTexture) &&
                        ((FadeInTexture) mContent).isAnimating()) {
                    result |= RENDER_MORE_FRAME;
                }
                {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.render(GLCanvas,int)",this);return result;}
            } else if (pass == 1) {
                mSelectionDrawer.drawFocus(canvas, width, height);
            }
            {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.render(GLCanvas,int)",this);return 0;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.render(GLCanvas,int)",this,throwable);throw throwable;}
        }

        @Override
        public void startLoadBitmap() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.startLoadBitmap()",this);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.startLoadBitmap()",this);if (mCacheThumbSize > 0) {
                Path path = mMediaItem.getPath();
                if (mImageCache.containsKey(path)) {
                    Bitmap bitmap = mImageCache.get(path);
                    updateImage(bitmap, false);
                    return;
                }
                mFuture = mThreadPool.submit(this, this);
            } else {
                mFuture = mThreadPool.submit(mMediaItem.requestImage(
                        MediaItem.TYPE_MICROTHUMBNAIL), this);
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.startLoadBitmap()",this,throwable);throw throwable;}
        }

        /*// This gets the bitmap and scale it down.*/
        public Bitmap run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{Job<Bitmap> job = mMediaItem.requestImage(
                    MediaItem.TYPE_MICROTHUMBNAIL);
            Bitmap bitmap = job.run(jc);
            if (bitmap != null) {
                bitmap = BitmapUtils.resizeDownBySideLength(
                        bitmap, mCacheThumbSize, true);
            }
            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return bitmap;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }

        @Override
        public void cancelLoadBitmap() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.cancelLoadBitmap()",this);try{if (mFuture != null) {
                mFuture.cancel();
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.cancelLoadBitmap()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.cancelLoadBitmap()",this,throwable);throw throwable;}
        }

        @Override
        public void onFutureDone(Future<Bitmap> bitmap) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.onFutureDone(com.android.gallery3d.util.Future)",this,bitmap);try{mHandler.sendMessage(mHandler.obtainMessage(MSG_LOAD_BITMAP_DONE, this));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.onFutureDone(com.android.gallery3d.util.Future)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.onFutureDone(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
        }

        private void onLoadBitmapDone() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.onLoadBitmapDone()",this);try{Future<Bitmap> future = mFuture;
            mFuture = null;
            Bitmap bitmap = future.get();
            boolean isCancelled = future.isCancelled();
            if (mCacheThumbSize > 0 && (bitmap != null || !isCancelled)) {
                Path path = mMediaItem.getPath();
                mImageCache.put(path, bitmap);
            }
            updateImage(bitmap, isCancelled);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.onLoadBitmapDone()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.onLoadBitmapDone()",this,throwable);throw throwable;}
        }

        @Override
        public String toString() {
            com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.toString()",this);return String.format("AlbumDisplayItem[%s]", mSlotIndex);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.ui.AlbumSlidingWindow$AlbumDisplayItem.toString()",this,throwable);throw throwable;}
        }
    }

    public void onSizeChanged(int size) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.onSizeChanged(int)",this,size);try{if (mSize != size) {
            mSize = size;
            if (mListener != null) {mListener.onSizeChanged(mSize);}
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.onSizeChanged(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.onSizeChanged(int)",this,throwable);throw throwable;}
    }

    public void onWindowContentChanged(int index) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.onWindowContentChanged(int)",this,index);try{if (index >= mContentStart && index < mContentEnd && mIsActive) {
            updateSlotContent(index);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.onWindowContentChanged(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.onWindowContentChanged(int)",this,throwable);throw throwable;}
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.resume()",this);try{mIsActive = true;
        for (int i = mContentStart, n = mContentEnd; i < n; ++i) {
            prepareSlotContent(i);
        }
        updateAllImageRequests();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.resume()",this,throwable);throw throwable;}
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSlidingWindow.pause()",this);try{mIsActive = false;
        for (int i = mContentStart, n = mContentEnd; i < n; ++i) {
            freeSlotContent(i);
        }
        mImageCache.clear();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSlidingWindow.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSlidingWindow.pause()",this,throwable);throw throwable;}
    }
}
