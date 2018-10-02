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
import android.graphics.Color;
import android.os.Message;

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.app.GalleryActivity;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.AlbumSetView.AlbumSetItem;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.MediaSetUtils;
import com.android.gallery3d.util.ThreadPool;

public class AlbumSetSlidingWindow implements AlbumSetView.ModelListener {
    private static final String TAG = "GallerySlidingWindow";
    private static final int MSG_LOAD_BITMAP_DONE = 0;
    private static final int PLACEHOLDER_COLOR = 0xFF222222;

    public static interface Listener {
        public void onSizeChanged(int size);
        public void onContentInvalidated();
        public void onWindowContentChanged(
                int slot, AlbumSetItem old, AlbumSetItem update);
    }

    private final AlbumSetView.Model mSource;
    private int mSize;
    private final AlbumSetView.LabelSpec mLabelSpec;

    private int mContentStart = 0;
    private int mContentEnd = 0;

    private int mActiveStart = 0;
    private int mActiveEnd = 0;

    private Listener mListener;

    private final MyAlbumSetItem mData[];
    private SelectionDrawer mSelectionDrawer;
    private final ColorTexture mWaitLoadingTexture;

    private final SynchronizedHandler mHandler;
    private final ThreadPool mThreadPool;

    private int mActiveRequestCount = 0;
    private final String mLoadingLabel;
    private boolean mIsActive = false;

    private static class MyAlbumSetItem extends AlbumSetItem {
        public Path setPath;
        public int sourceType;
        public int cacheFlag;
        public int cacheStatus;
    }

    public AlbumSetSlidingWindow(GalleryActivity activity,
            AlbumSetView.LabelSpec labelSpec, SelectionDrawer drawer,
            AlbumSetView.Model source, int cacheSize) {
        source.setModelListener(this);
        mLabelSpec = labelSpec;
        mLoadingLabel = activity.getAndroidContext().getString(R.string.loading);
        mSource = source;
        mSelectionDrawer = drawer;
        mData = new MyAlbumSetItem[cacheSize];
        mSize = source.size();

        mWaitLoadingTexture = new ColorTexture(PLACEHOLDER_COLOR);
        mWaitLoadingTexture.setSize(1, 1);

        mHandler = new SynchronizedHandler(activity.getGLRoot()) {
            @Override
            public void handleMessage(Message message) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow$1.handleMessage(android.os.Message)",this,message);try{Utils.assertTrue(message.what == MSG_LOAD_BITMAP_DONE);
                ((GalleryDisplayItem) message.obj).onLoadBitmapDone();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow$1.handleMessage(android.os.Message)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow$1.handleMessage(android.os.Message)",this,throwable);throw throwable;}
            }
        };

        mThreadPool = activity.getThreadPool();
    }

    public void setSelectionDrawer(SelectionDrawer drawer) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this,drawer);try{mSelectionDrawer = drawer;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this,throwable);throw throwable;}
    }

    public void setListener(Listener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setListener(com.android.gallery3d.util.FutureListener)",this,listener);try{mListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setListener(com.android.gallery3d.util.FutureListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setListener(com.android.gallery3d.util.FutureListener)",this,throwable);throw throwable;}
    }

    public AlbumSetItem get(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.AlbumSetView.AlbumSetItem com.android.gallery3d.ui.AlbumSetSlidingWindow.get(int)",this,slotIndex);try{Utils.assertTrue(isActiveSlot(slotIndex),
                "invalid slot: %s outsides (%s, %s)",
                slotIndex, mActiveStart, mActiveEnd);
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.AlbumSetView.AlbumSetItem com.android.gallery3d.ui.AlbumSetSlidingWindow.get(int)",this);return mData[slotIndex % mData.length];}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.AlbumSetView.AlbumSetItem com.android.gallery3d.ui.AlbumSetSlidingWindow.get(int)",this,throwable);throw throwable;}
    }

    public int size() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.AlbumSetSlidingWindow.size()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.AlbumSetSlidingWindow.size()",this);return mSize;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.AlbumSetSlidingWindow.size()",this,throwable);throw throwable;}
    }

    public boolean isActiveSlot(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow.isActiveSlot(int)",this,slotIndex);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow.isActiveSlot(int)",this);return slotIndex >= mActiveStart && slotIndex < mActiveEnd;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow.isActiveSlot(int)",this,throwable);throw throwable;}
    }

    private void setContentWindow(int contentStart, int contentEnd) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setContentWindow(int,int)",this,contentStart,contentEnd);try{if (contentStart == mContentStart && contentEnd == mContentEnd) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setContentWindow(int,int)",this);return;}}

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
        mContentEnd = contentEnd;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setContentWindow(int,int)",this,throwable);throw throwable;}
    }

    public void setActiveWindow(int start, int end) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setActiveWindow(int,int)",this,start,end);try{Utils.assertTrue(
                start <= end && end - start <= mData.length && end <= mSize,
                "start = %s, end = %s, length = %s, size = %s",
                start, end, mData.length, mSize);

        AlbumSetItem data[] = mData;

        mActiveStart = start;
        mActiveEnd = end;

        int contentStart = Utils.clamp((start + end) / 2 - data.length / 2,
                0, Math.max(0, mSize - data.length));
        int contentEnd = Math.min(contentStart + data.length, mSize);
        setContentWindow(contentStart, contentEnd);
        if (mIsActive) {updateAllImageRequests();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setActiveWindow(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.setActiveWindow(int,int)",this,throwable);throw throwable;}
    }

    /*// We would like to request non active slots in the following order:*/
    /*// Order:    8 6 4 2                   1 3 5 7*/
    /*//         |---------|---------------|---------|*/
    /*//                   |<-  active  ->|*/
    /*//         |<-------- cached range ----------->|*/
    private void requestNonactiveImages() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.requestNonactiveImages()",this);try{int range = Math.max(
                mContentEnd - mActiveEnd, mActiveStart - mContentStart);
        for (int i = 0 ;i < range; ++i) {
            requestImagesInSlot(mActiveEnd + i);
            requestImagesInSlot(mActiveStart - 1 - i);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.requestNonactiveImages()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.requestNonactiveImages()",this,throwable);throw throwable;}
    }

    private void cancelNonactiveImages() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.cancelNonactiveImages()",this);try{int range = Math.max(
                mContentEnd - mActiveEnd, mActiveStart - mContentStart);
        for (int i = 0 ;i < range; ++i) {
            cancelImagesInSlot(mActiveEnd + i);
            cancelImagesInSlot(mActiveStart - 1 - i);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.cancelNonactiveImages()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.cancelNonactiveImages()",this,throwable);throw throwable;}
    }

    private void requestImagesInSlot(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.requestImagesInSlot(int)",this,slotIndex);try{if (slotIndex < mContentStart || slotIndex >= mContentEnd) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.requestImagesInSlot(int)",this);return;}}
        AlbumSetItem items = mData[slotIndex % mData.length];
        for (DisplayItem item : items.covers) {
            ((GalleryDisplayItem) item).requestImage();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.requestImagesInSlot(int)",this,throwable);throw throwable;}
    }

    private void cancelImagesInSlot(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.cancelImagesInSlot(int)",this,slotIndex);try{if (slotIndex < mContentStart || slotIndex >= mContentEnd) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.cancelImagesInSlot(int)",this);return;}}
        AlbumSetItem items = mData[slotIndex % mData.length];
        for (DisplayItem item : items.covers) {
            ((GalleryDisplayItem) item).cancelImageRequest();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.cancelImagesInSlot(int)",this,throwable);throw throwable;}
    }

    private void freeSlotContent(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.freeSlotContent(int)",this,slotIndex);try{AlbumSetItem data[] = mData;
        int index = slotIndex % data.length;
        AlbumSetItem original = data[index];
        if (original != null) {
            data[index] = null;
            for (DisplayItem item : original.covers) {
                ((GalleryDisplayItem) item).recycle();
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.freeSlotContent(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.freeSlotContent(int)",this,throwable);throw throwable;}
    }

    private long getMediaSetDataVersion(MediaSet set) {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.ui.AlbumSetSlidingWindow.getMediaSetDataVersion(com.android.gallery3d.data.MediaSet)",this,set);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.ui.AlbumSetSlidingWindow.getMediaSetDataVersion(com.android.gallery3d.data.MediaSet)",this);return set == null
                ? MediaSet.INVALID_DATA_VERSION
                : set.getDataVersion();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.ui.AlbumSetSlidingWindow.getMediaSetDataVersion(com.android.gallery3d.data.MediaSet)",this,throwable);throw throwable;}
    }

    private void prepareSlotContent(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.prepareSlotContent(int)",this,slotIndex);try{MediaSet set = mSource.getMediaSet(slotIndex);

        MyAlbumSetItem item = new MyAlbumSetItem();
        MediaItem[] coverItems = mSource.getCoverItems(slotIndex);
        item.covers = new GalleryDisplayItem[coverItems.length];
        item.sourceType = identifySourceType(set);
        item.cacheFlag = identifyCacheFlag(set);
        item.cacheStatus = identifyCacheStatus(set);
        item.setPath = set == null ? null : set.getPath();

        for (int i = 0; i < coverItems.length; ++i) {
            item.covers[i] = new GalleryDisplayItem(slotIndex, i, coverItems[i]);
        }
        item.labelItem = new LabelDisplayItem(slotIndex);
        item.setDataVersion = getMediaSetDataVersion(set);
        mData[slotIndex % mData.length] = item;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.prepareSlotContent(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.prepareSlotContent(int)",this,throwable);throw throwable;}
    }

    private boolean isCoverItemsChanged(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow.isCoverItemsChanged(int)",this,slotIndex);try{AlbumSetItem original = mData[slotIndex % mData.length];
        if (original == null) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow.isCoverItemsChanged(int)",this);return true;}}
        MediaItem[] coverItems = mSource.getCoverItems(slotIndex);

        if (original.covers.length != coverItems.length) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow.isCoverItemsChanged(int)",this);return true;}}
        for (int i = 0, n = coverItems.length; i < n; ++i) {
            GalleryDisplayItem g = (GalleryDisplayItem) original.covers[i];
            if (g.mDataVersion != coverItems[i].getDataVersion()) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow.isCoverItemsChanged(int)",this);return true;}}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow.isCoverItemsChanged(int)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow.isCoverItemsChanged(int)",this,throwable);throw throwable;}
    }

    private void updateSlotContent(final int slotIndex) {

        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.updateSlotContent(int)",this,slotIndex);try{MyAlbumSetItem data[] = mData;
        int pos = slotIndex % data.length;
        MyAlbumSetItem original = data[pos];

        if (!isCoverItemsChanged(slotIndex)) {
            MediaSet set = mSource.getMediaSet(slotIndex);
            original.sourceType = identifySourceType(set);
            original.cacheFlag = identifyCacheFlag(set);
            original.cacheStatus = identifyCacheStatus(set);
            original.setPath = set == null ? null : set.getPath();
            ((LabelDisplayItem) original.labelItem).updateContent();
            if (mListener != null) {mListener.onContentInvalidated();}
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.updateSlotContent(int)",this);return;}
        }

        prepareSlotContent(slotIndex);
        AlbumSetItem update = data[pos];

        if (mListener != null && isActiveSlot(slotIndex)) {
            mListener.onWindowContentChanged(slotIndex, original, update);
        }
        if (original != null) {
            for (DisplayItem item : original.covers) {
                ((GalleryDisplayItem) item).recycle();
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.updateSlotContent(int)",this,throwable);throw throwable;}
    }

    private void notifySlotChanged(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.notifySlotChanged(int)",this,slotIndex);try{/*// If the updated content is not cached, ignore it*/
        if (slotIndex < mContentStart || slotIndex >= mContentEnd) {
            Log.w(TAG, String.format(
                    "invalid update: %s is outside (%s, %s)",
                    slotIndex, mContentStart, mContentEnd) );
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.notifySlotChanged(int)",this);return;}
        }
        updateSlotContent(slotIndex);
        boolean isActiveSlot = isActiveSlot(slotIndex);
        if (mActiveRequestCount == 0 || isActiveSlot) {
            for (DisplayItem item : mData[slotIndex % mData.length].covers) {
                GalleryDisplayItem galleryItem = (GalleryDisplayItem) item;
                galleryItem.requestImage();
                if (isActiveSlot && galleryItem.isRequestInProgress()) {
                    ++mActiveRequestCount;
                }
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.notifySlotChanged(int)",this,throwable);throw throwable;}
    }

    private void updateAllImageRequests() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.updateAllImageRequests()",this);try{mActiveRequestCount = 0;
        for (int i = mActiveStart, n = mActiveEnd; i < n; ++i) {
            for (DisplayItem item : mData[i % mData.length].covers) {
                GalleryDisplayItem coverItem = (GalleryDisplayItem) item;
                coverItem.requestImage();
                if (coverItem.isRequestInProgress()) {++mActiveRequestCount;}
            }
        }
        if (mActiveRequestCount == 0) {
            requestNonactiveImages();
        } else {
            cancelNonactiveImages();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.updateAllImageRequests()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.updateAllImageRequests()",this,throwable);throw throwable;}
    }

    private class GalleryDisplayItem extends AbstractDisplayItem
            implements FutureListener<Bitmap> {
        private Future<Bitmap> mFuture;
        private final int mSlotIndex;
        private final int mCoverIndex;
        private final int mMediaType;
        private Texture mContent;
        private final long mDataVersion;
        private final boolean mIsPanorama;
        private boolean mWaitLoadingDisplayed;

        public GalleryDisplayItem(int slotIndex, int coverIndex, MediaItem item) {
            super(item);
            mSlotIndex = slotIndex;
            mCoverIndex = coverIndex;
            mMediaType = item.getMediaType();
            mDataVersion = item.getDataVersion();
            mIsPanorama = GalleryUtils.isPanorama(item);
            updateContent(mWaitLoadingTexture);
        }

        @Override
        protected void onBitmapAvailable(Bitmap bitmap) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.onBitmapAvailable(android.graphics.Bitmap)",this,bitmap);try{if (isActiveSlot(mSlotIndex)) {
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
                if (mListener != null) {mListener.onContentInvalidated();}
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.onBitmapAvailable(android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.onBitmapAvailable(android.graphics.Bitmap)",this,throwable);throw throwable;}
        }

        private void updateContent(Texture content) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.updateContent(Texture)",this,content);try{mContent = content;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.updateContent(Texture)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.updateContent(Texture)",this,throwable);throw throwable;}
        }

        @Override
        public int render(GLCanvas canvas, int pass) {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.render(GLCanvas,int)",this,canvas,pass);try{/*// Fit the content into the box*/
            int width = mContent.getWidth();
            int height = mContent.getHeight();

            float scalex = mBoxWidth / (float) width;
            float scaley = mBoxHeight / (float) height;
            float scale = Math.min(scalex, scaley);

            width = (int) Math.floor(width * scale);
            height = (int) Math.floor(height * scale);

            /*// Now draw it*/
            int sourceType = SelectionDrawer.DATASOURCE_TYPE_NOT_CATEGORIZED;
            int cacheFlag = MediaSet.CACHE_FLAG_NO;
            int cacheStatus = MediaSet.CACHE_STATUS_NOT_CACHED;
            MyAlbumSetItem set = mData[mSlotIndex % mData.length];
            Path path = set.setPath;
            if (mCoverIndex == 0) {
                sourceType = set.sourceType;
                cacheFlag = set.cacheFlag;
                cacheStatus = set.cacheStatus;
            }

            mSelectionDrawer.draw(canvas, mContent, width, height,
                    getRotation(), path, sourceType, mMediaType,
                    mIsPanorama, mLabelSpec.labelBackgroundHeight,
                    cacheFlag == MediaSet.CACHE_FLAG_FULL,
                    (cacheFlag == MediaSet.CACHE_FLAG_FULL)
                    && (cacheStatus != MediaSet.CACHE_STATUS_CACHED_FULL));

            if (mContent == mWaitLoadingTexture) {
                mWaitLoadingDisplayed = true;
            }

            if ((mContent instanceof FadeInTexture) &&
                    ((FadeInTexture) mContent).isAnimating()) {
                {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.render(GLCanvas,int)",this);return RENDER_MORE_FRAME;}
            } else {
                {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.render(GLCanvas,int)",this);return 0;}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.render(GLCanvas,int)",this,throwable);throw throwable;}
        }

        @Override
        public void startLoadBitmap() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.startLoadBitmap()",this);try{mFuture = mThreadPool.submit(mMediaItem.requestImage(
                    MediaItem.TYPE_MICROTHUMBNAIL), this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.startLoadBitmap()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.startLoadBitmap()",this,throwable);throw throwable;}
        }

        @Override
        public void cancelLoadBitmap() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.cancelLoadBitmap()",this);try{mFuture.cancel();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.cancelLoadBitmap()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.cancelLoadBitmap()",this,throwable);throw throwable;}
        }

        @Override
        public void onFutureDone(Future<Bitmap> future) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.onFutureDone(com.android.gallery3d.util.Future)",this,future);try{mHandler.sendMessage(mHandler.obtainMessage(MSG_LOAD_BITMAP_DONE, this));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.onFutureDone(com.android.gallery3d.util.Future)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.onFutureDone(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
        }

        private void onLoadBitmapDone() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.onLoadBitmapDone()",this);try{Future<Bitmap> future = mFuture;
            mFuture = null;
            updateImage(future.get(), future.isCancelled());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.onLoadBitmapDone()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.onLoadBitmapDone()",this,throwable);throw throwable;}
        }

        @Override
        public String toString() {
            com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.toString()",this);return String.format("GalleryDisplayItem(%s, %s)", mSlotIndex, mCoverIndex);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.ui.AlbumSetSlidingWindow$GalleryDisplayItem.toString()",this,throwable);throw throwable;}
        }
    }

    private static int identifySourceType(MediaSet set) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifySourceType(com.android.gallery3d.data.MediaSet)",set);try{if (set == null) {
            {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifySourceType(com.android.gallery3d.data.MediaSet)");return SelectionDrawer.DATASOURCE_TYPE_NOT_CATEGORIZED;}
        }

        Path path = set.getPath();
        if (MediaSetUtils.isCameraSource(path)) {
            {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifySourceType(com.android.gallery3d.data.MediaSet)");return SelectionDrawer.DATASOURCE_TYPE_CAMERA;}
        }

        int type = SelectionDrawer.DATASOURCE_TYPE_NOT_CATEGORIZED;
        String prefix = path.getPrefix();

        if (prefix.equals("picasa")) {
            type = SelectionDrawer.DATASOURCE_TYPE_PICASA;
        } else if (prefix.equals("local") || prefix.equals("merge")) {
            type = SelectionDrawer.DATASOURCE_TYPE_LOCAL;
        } else if (prefix.equals("mtp")) {
            type = SelectionDrawer.DATASOURCE_TYPE_MTP;
        }

        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifySourceType(com.android.gallery3d.data.MediaSet)");return type;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifySourceType(com.android.gallery3d.data.MediaSet)",throwable);throw throwable;}
    }

    private static int identifyCacheFlag(MediaSet set) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifyCacheFlag(com.android.gallery3d.data.MediaSet)",set);try{if (set == null || (set.getSupportedOperations()
                & MediaSet.SUPPORT_CACHE) == 0) {
            {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifyCacheFlag(com.android.gallery3d.data.MediaSet)");return MediaSet.CACHE_FLAG_NO;}
        }

        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifyCacheFlag(com.android.gallery3d.data.MediaSet)");return set.getCacheFlag();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifyCacheFlag(com.android.gallery3d.data.MediaSet)",throwable);throw throwable;}
    }

    private static int identifyCacheStatus(MediaSet set) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifyCacheStatus(com.android.gallery3d.data.MediaSet)",set);try{if (set == null || (set.getSupportedOperations()
                & MediaSet.SUPPORT_CACHE) == 0) {
            {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifyCacheStatus(com.android.gallery3d.data.MediaSet)");return MediaSet.CACHE_STATUS_NOT_CACHED;}
        }

        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifyCacheStatus(com.android.gallery3d.data.MediaSet)");return set.getCacheStatus();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.ui.AlbumSetSlidingWindow.identifyCacheStatus(com.android.gallery3d.data.MediaSet)",throwable);throw throwable;}
    }

    private class LabelDisplayItem extends DisplayItem {
        private static final int FONT_COLOR_TITLE = Color.WHITE;
        private static final int FONT_COLOR_COUNT = 0x80FFFFFF;  /*// 50% white*/

        private StringTexture mTextureTitle;
        private StringTexture mTextureCount;
        private String mTitle;
        private String mCount;
        private int mLastWidth;
        private final int mSlotIndex;
        private boolean mHasIcon;

        public LabelDisplayItem(int slotIndex) {
            mSlotIndex = slotIndex;
        }

        public boolean updateContent() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow$LabelDisplayItem.updateContent()",this);try{String title = mLoadingLabel;
            String count = "";
            MediaSet set = mSource.getMediaSet(mSlotIndex);
            if (set != null) {
                title = Utils.ensureNotNull(set.getName());
                count = "" + set.getTotalMediaItemCount();
            }
            if (Utils.equals(title, mTitle)
                    && Utils.equals(count, mCount)
                    && Utils.equals(mBoxWidth, mLastWidth)) {
                    {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow$LabelDisplayItem.updateContent()",this);return false;}
            }
            mTitle = title;
            mCount = count;
            mLastWidth = mBoxWidth;
            mHasIcon = (identifySourceType(set) !=
                    SelectionDrawer.DATASOURCE_TYPE_NOT_CATEGORIZED);

            AlbumSetView.LabelSpec s = mLabelSpec;
            mTextureTitle = StringTexture.newInstance(
                    title, s.titleFontSize, FONT_COLOR_TITLE,
                    mBoxWidth - s.leftMargin, false);
            mTextureCount = StringTexture.newInstance(
                    count, s.countFontSize, FONT_COLOR_COUNT,
                    mBoxWidth - s.leftMargin, true);

            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow$LabelDisplayItem.updateContent()",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.AlbumSetSlidingWindow$LabelDisplayItem.updateContent()",this,throwable);throw throwable;}
        }

        @Override
        public int render(GLCanvas canvas, int pass) {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.AlbumSetSlidingWindow$LabelDisplayItem.render(GLCanvas,int)",this,canvas,pass);try{if (mBoxWidth != mLastWidth) {
                updateContent();
            }

            AlbumSetView.LabelSpec s = mLabelSpec;
            int x = -mBoxWidth / 2;
            int y = (mBoxHeight + 1) / 2 - s.labelBackgroundHeight;
            y += s.titleOffset;
            mTextureTitle.draw(canvas, x + s.leftMargin, y);
            y += s.titleFontSize + s.countOffset;
            x += mHasIcon ? s.iconSize : s.leftMargin;
            mTextureCount.draw(canvas, x, y);
            {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.AlbumSetSlidingWindow$LabelDisplayItem.render(GLCanvas,int)",this);return 0;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.AlbumSetSlidingWindow$LabelDisplayItem.render(GLCanvas,int)",this,throwable);throw throwable;}
        }

        @Override
        public long getIdentity() {
            com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.ui.AlbumSetSlidingWindow$LabelDisplayItem.getIdentity()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.ui.AlbumSetSlidingWindow$LabelDisplayItem.getIdentity()",this);return System.identityHashCode(this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.ui.AlbumSetSlidingWindow$LabelDisplayItem.getIdentity()",this,throwable);throw throwable;}
        }
    }

    public void onSizeChanged(int size) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.onSizeChanged(int)",this,size);try{if (mIsActive && mSize != size) {
            mSize = size;
            if (mListener != null) {mListener.onSizeChanged(mSize);}
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.onSizeChanged(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.onSizeChanged(int)",this,throwable);throw throwable;}
    }

    public void onWindowContentChanged(int index) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.onWindowContentChanged(int)",this,index);try{if (!mIsActive) {
            /*// paused, ignore slot changed event*/
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.onWindowContentChanged(int)",this);return;}
        }
        notifySlotChanged(index);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.onWindowContentChanged(int)",this,throwable);throw throwable;}
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.pause()",this);try{mIsActive = false;
        for (int i = mContentStart, n = mContentEnd; i < n; ++i) {
            freeSlotContent(i);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.pause()",this,throwable);throw throwable;}
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetSlidingWindow.resume()",this);try{mIsActive = true;
        for (int i = mContentStart, n = mContentEnd; i < n; ++i) {
            prepareSlotContent(i);
        }
        updateAllImageRequests();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetSlidingWindow.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetSlidingWindow.resume()",this,throwable);throw throwable;}
    }
}
