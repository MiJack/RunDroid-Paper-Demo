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

import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.PhotoView;
import com.android.gallery3d.ui.PhotoView.ImageData;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.ui.TileImageViewAdapter;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class PhotoDataAdapter implements PhotoPage.Model {
    @SuppressWarnings("unused")
    private static final String TAG = "PhotoDataAdapter";

    private static final int MSG_LOAD_START = 1;
    private static final int MSG_LOAD_FINISH = 2;
    private static final int MSG_RUN_OBJECT = 3;

    private static final int MIN_LOAD_COUNT = 8;
    private static final int DATA_CACHE_SIZE = 32;
    private static final int IMAGE_CACHE_SIZE = 5;

    private static final int BIT_SCREEN_NAIL = 1;
    private static final int BIT_FULL_IMAGE = 2;

    private static final long VERSION_OUT_OF_RANGE = MediaObject.nextVersionNumber();

    /*// sImageFetchSeq is the fetching sequence for images.*/
    /*// We want to fetch the current screennail first (offset = 0), the next*/
    /*// screennail (offset = +1), then the previous screennail (offset = -1) etc.*/
    /*// After all the screennail are fetched, we fetch the full images (only some*/
    /*// of them because of we don't want to use too much memory).*/
    private static ImageFetch[] sImageFetchSeq;

    private static class ImageFetch {
        int indexOffset;
        int imageBit;
        public ImageFetch(int offset, int bit) {
            indexOffset = offset;
            imageBit = bit;
        }
    }

    static {
        int k = 0;
        sImageFetchSeq = new ImageFetch[1 + (IMAGE_CACHE_SIZE - 1) * 2 + 3];
        sImageFetchSeq[k++] = new ImageFetch(0, BIT_SCREEN_NAIL);

        for (int i = 1; i < IMAGE_CACHE_SIZE; ++i) {
            sImageFetchSeq[k++] = new ImageFetch(i, BIT_SCREEN_NAIL);
            sImageFetchSeq[k++] = new ImageFetch(-i, BIT_SCREEN_NAIL);
        }

        sImageFetchSeq[k++] = new ImageFetch(0, BIT_FULL_IMAGE);
        sImageFetchSeq[k++] = new ImageFetch(1, BIT_FULL_IMAGE);
        sImageFetchSeq[k++] = new ImageFetch(-1, BIT_FULL_IMAGE);
    }

    private final TileImageViewAdapter mTileProvider = new TileImageViewAdapter();

    /*// PhotoDataAdapter caches MediaItems (data) and ImageEntries (image).*/
    /*//*/
    /*// The MediaItems are stored in the mData array, which has DATA_CACHE_SIZE*/
    /*// entries. The valid index range are [mContentStart, mContentEnd). We keep*/
    /*// mContentEnd - mContentStart <= DATA_CACHE_SIZE, so we can use*/
    /*// (i % DATA_CACHE_SIZE) as index to the array.*/
    /*//*/
    /*// The valid MediaItem window size (mContentEnd - mContentStart) may be*/
    /*// smaller than DATA_CACHE_SIZE because we only update the window and reload*/
    /*// the MediaItems when there are significant changes to the window position*/
    /*// (>= MIN_LOAD_COUNT).*/
    private final MediaItem mData[] = new MediaItem[DATA_CACHE_SIZE];
    private int mContentStart = 0;
    private int mContentEnd = 0;

    /*
     * The ImageCache is a version-to-ImageEntry map. It only holds
     * the ImageEntries in the range of [mActiveStart, mActiveEnd).
     * We also keep mActiveEnd - mActiveStart <= IMAGE_CACHE_SIZE.
     * Besides, the [mActiveStart, mActiveEnd) range must be contained
     * within the[mContentStart, mContentEnd) range.
     */
    private HashMap<Long, ImageEntry> mImageCache = new HashMap<Long, ImageEntry>();
    private int mActiveStart = 0;
    private int mActiveEnd = 0;

    /*// mCurrentIndex is the "center" image the user is viewing. The change of*/
    /*// mCurrentIndex triggers the data loading and image loading.*/
    private int mCurrentIndex;

    /*// mChanges keeps the version number (of MediaItem) about the previous,*/
    /*// current, and next image. If the version number changes, we invalidate*/
    /*// the model. This is used after a database reload or mCurrentIndex changes.*/
    private final long mChanges[] = new long[3];

    private final Handler mMainHandler;
    private final ThreadPool mThreadPool;

    private final PhotoView mPhotoView;
    private final MediaSet mSource;
    private ReloadTask mReloadTask;

    private long mSourceVersion = MediaObject.INVALID_DATA_VERSION;
    private int mSize = 0;
    private Path mItemPath;
    private boolean mIsActive;

    public interface DataListener extends LoadingListener {
        public void onPhotoAvailable(long version, boolean fullImage);
        public void onPhotoChanged(int index, Path item);
    }

    private DataListener mDataListener;

    private final SourceListener mSourceListener = new SourceListener();

    /*// The path of the current viewing item will be stored in mItemPath.*/
    /*// If mItemPath is not null, mCurrentIndex is only a hint for where we*/
    /*// can find the item. If mItemPath is null, then we use the mCurrentIndex to*/
    /*// find the image being viewed.*/
    public PhotoDataAdapter(GalleryActivity activity,
            PhotoView view, MediaSet mediaSet, Path itemPath, int indexHint) {
        mSource = Utils.checkNotNull(mediaSet);
        mPhotoView = Utils.checkNotNull(view);
        mItemPath = Utils.checkNotNull(itemPath);
        mCurrentIndex = indexHint;
        mThreadPool = activity.getThreadPool();

        Arrays.fill(mChanges, MediaObject.INVALID_DATA_VERSION);

        mMainHandler = new SynchronizedHandler(activity.getGLRoot()) {
            @SuppressWarnings("unchecked")
            @Override
            public void handleMessage(Message message) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter$1.handleMessage(android.os.Message)",this,message);try{switch (message.what) {
                    case MSG_RUN_OBJECT:
                        ((Runnable) message.obj).run();
                        {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$1.handleMessage(android.os.Message)",this);return;}
                    case MSG_LOAD_START: {
                        if (mDataListener != null) {mDataListener.onLoadingStarted();}
                        {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$1.handleMessage(android.os.Message)",this);return;}
                    }
                    case MSG_LOAD_FINISH: {
                        if (mDataListener != null) {mDataListener.onLoadingFinished();}
                        {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$1.handleMessage(android.os.Message)",this);return;}
                    }
                    default: throw new AssertionError();
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter$1.handleMessage(android.os.Message)",this,throwable);throw throwable;}
            }
        };

        updateSlidingWindow();
    }

    private long getVersion(int index) {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.app.PhotoDataAdapter.getVersion(int)",this,index);try{if (index < 0 || index >= mSize) {{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.app.PhotoDataAdapter.getVersion(int)",this);return VERSION_OUT_OF_RANGE;}}
        if (index >= mContentStart && index < mContentEnd) {
            MediaItem item = mData[index % DATA_CACHE_SIZE];
            if (item != null) {{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.app.PhotoDataAdapter.getVersion(int)",this);return item.getDataVersion();}}
        }
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.app.PhotoDataAdapter.getVersion(int)",this);return MediaObject.INVALID_DATA_VERSION;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.app.PhotoDataAdapter.getVersion(int)",this,throwable);throw throwable;}
    }

    private void fireModelInvalidated() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.fireModelInvalidated()",this);try{for (int i = -1; i <= 1; ++i) {
            long current = getVersion(mCurrentIndex + i);
            long change = mChanges[i + 1];
            if (current != change) {
                mPhotoView.notifyImageInvalidated(i);
                mChanges[i + 1] = current;
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.fireModelInvalidated()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.fireModelInvalidated()",this,throwable);throw throwable;}
    }

    public void setDataListener(DataListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.setDataListener(DataListener)",this,listener);try{mDataListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.setDataListener(DataListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.setDataListener(DataListener)",this,throwable);throw throwable;}
    }

    private void updateScreenNail(long version, Future<Bitmap> future) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.updateScreenNail(long,com.android.gallery3d.util.Future)",this,version,future);try{ImageEntry entry = mImageCache.get(version);
        if (entry == null || entry.screenNailTask != future) {
            Bitmap screenNail = future.get();
            if (screenNail != null) {screenNail.recycle();}
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.updateScreenNail(long,com.android.gallery3d.util.Future)",this);return;}
        }

        entry.screenNailTask = null;
        entry.screenNail = future.get();

        if (entry.screenNail == null) {
            entry.failToLoad = true;
        } else {
            if (mDataListener != null) {
                mDataListener.onPhotoAvailable(version, false);
            }
            for (int i = -1; i <=1; ++i) {
                if (version == getVersion(mCurrentIndex + i)) {
                    if (i == 0) {updateTileProvider(entry);}
                    mPhotoView.notifyImageInvalidated(i);
                }
            }
        }
        updateImageRequests();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.updateScreenNail(long,com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
    }

    private void updateFullImage(long version, Future<BitmapRegionDecoder> future) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.updateFullImage(long,com.android.gallery3d.util.Future)",this,version,future);try{ImageEntry entry = mImageCache.get(version);
        if (entry == null || entry.fullImageTask != future) {
            BitmapRegionDecoder fullImage = future.get();
            if (fullImage != null) {fullImage.recycle();}
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.updateFullImage(long,com.android.gallery3d.util.Future)",this);return;}
        }

        entry.fullImageTask = null;
        entry.fullImage = future.get();
        if (entry.fullImage != null) {
            if (mDataListener != null) {
                mDataListener.onPhotoAvailable(version, true);
            }
            if (version == getVersion(mCurrentIndex)) {
                updateTileProvider(entry);
                mPhotoView.notifyImageInvalidated(0);
            }
        }
        updateImageRequests();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.updateFullImage(long,com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.resume()",this);try{mIsActive = true;
        mSource.addContentListener(mSourceListener);
        updateImageCache();
        updateImageRequests();

        mReloadTask = new ReloadTask();
        mReloadTask.start();

        mPhotoView.notifyModelInvalidated();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.resume()",this,throwable);throw throwable;}
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.pause()",this);try{mIsActive = false;

        mReloadTask.terminate();
        mReloadTask = null;

        mSource.removeContentListener(mSourceListener);

        for (ImageEntry entry : mImageCache.values()) {
            if (entry.fullImageTask != null) {entry.fullImageTask.cancel();}
            if (entry.screenNailTask != null) {entry.screenNailTask.cancel();}
        }
        mImageCache.clear();
        mTileProvider.clear();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.pause()",this,throwable);throw throwable;}
    }

    private ImageData getImage(int index) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.PhotoDataAdapter.getImage(int)",this,index);try{if (index < 0 || index >= mSize || !mIsActive) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.PhotoDataAdapter.getImage(int)",this);return null;}}
        Utils.assertTrue(index >= mActiveStart && index < mActiveEnd);

        ImageEntry entry = mImageCache.get(getVersion(index));
        Bitmap screennail = entry == null ? null : entry.screenNail;
        if (screennail != null) {
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.PhotoDataAdapter.getImage(int)",this);return new ImageData(screennail, entry.rotation);}
        } else {
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.PhotoDataAdapter.getImage(int)",this);return new ImageData(null, 0);}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.PhotoDataAdapter.getImage(int)",this,throwable);throw throwable;}
    }

    public ImageData getPreviousImage() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.PhotoDataAdapter.getPreviousImage()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.PhotoDataAdapter.getPreviousImage()",this);return getImage(mCurrentIndex - 1);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.PhotoDataAdapter.getPreviousImage()",this,throwable);throw throwable;}
    }

    public ImageData getNextImage() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.PhotoDataAdapter.getNextImage()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.PhotoDataAdapter.getNextImage()",this);return getImage(mCurrentIndex + 1);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.PhotoDataAdapter.getNextImage()",this,throwable);throw throwable;}
    }

    private void updateCurrentIndex(int index) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.updateCurrentIndex(int)",this,index);try{mCurrentIndex = index;
        updateSlidingWindow();

        MediaItem item = mData[index % DATA_CACHE_SIZE];
        mItemPath = item == null ? null : item.getPath();

        updateImageCache();
        updateImageRequests();
        updateTileProvider();
        mPhotoView.notifyOnNewImage();

        if (mDataListener != null) {
            mDataListener.onPhotoChanged(index, mItemPath);
        }
        fireModelInvalidated();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.updateCurrentIndex(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.updateCurrentIndex(int)",this,throwable);throw throwable;}
    }

    public void next() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.next()",this);try{updateCurrentIndex(mCurrentIndex + 1);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.next()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.next()",this,throwable);throw throwable;}
    }

    public void previous() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.previous()",this);try{updateCurrentIndex(mCurrentIndex - 1);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.previous()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.previous()",this,throwable);throw throwable;}
    }

    public void jumpTo(int index) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.jumpTo(int)",this,index);try{if (mCurrentIndex == index) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.jumpTo(int)",this);return;}}
        updateCurrentIndex(index);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.jumpTo(int)",this,throwable);throw throwable;}
    }

    public Bitmap getBackupImage() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.app.PhotoDataAdapter.getBackupImage()",this);try{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.app.PhotoDataAdapter.getBackupImage()",this);return mTileProvider.getBackupImage();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.app.PhotoDataAdapter.getBackupImage()",this,throwable);throw throwable;}
    }

    public int getImageHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.PhotoDataAdapter.getImageHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.PhotoDataAdapter.getImageHeight()",this);return mTileProvider.getImageHeight();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.PhotoDataAdapter.getImageHeight()",this,throwable);throw throwable;}
    }

    public int getImageWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.PhotoDataAdapter.getImageWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.PhotoDataAdapter.getImageWidth()",this);return mTileProvider.getImageWidth();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.PhotoDataAdapter.getImageWidth()",this,throwable);throw throwable;}
    }

    public int getImageRotation() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.PhotoDataAdapter.getImageRotation()",this);try{ImageEntry entry = mImageCache.get(getVersion(mCurrentIndex));
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.PhotoDataAdapter.getImageRotation()",this);return entry == null ? 0 : entry.rotation;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.PhotoDataAdapter.getImageRotation()",this,throwable);throw throwable;}
    }

    public int getLevelCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.PhotoDataAdapter.getLevelCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.PhotoDataAdapter.getLevelCount()",this);return mTileProvider.getLevelCount();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.PhotoDataAdapter.getLevelCount()",this,throwable);throw throwable;}
    }

    public Bitmap getTile(int level, int x, int y, int tileSize) {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.app.PhotoDataAdapter.getTile(int,int,int,int)",this,level,x,y,tileSize);try{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.app.PhotoDataAdapter.getTile(int,int,int,int)",this);return mTileProvider.getTile(level, x, y, tileSize);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.app.PhotoDataAdapter.getTile(int,int,int,int)",this,throwable);throw throwable;}
    }

    public boolean isFailedToLoad() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.PhotoDataAdapter.isFailedToLoad()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.PhotoDataAdapter.isFailedToLoad()",this);return mTileProvider.isFailedToLoad();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.PhotoDataAdapter.isFailedToLoad()",this,throwable);throw throwable;}
    }

    public boolean isEmpty() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.PhotoDataAdapter.isEmpty()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.PhotoDataAdapter.isEmpty()",this);return mSize == 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.PhotoDataAdapter.isEmpty()",this,throwable);throw throwable;}
    }

    public int getCurrentIndex() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.PhotoDataAdapter.getCurrentIndex()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.PhotoDataAdapter.getCurrentIndex()",this);return mCurrentIndex;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.PhotoDataAdapter.getCurrentIndex()",this,throwable);throw throwable;}
    }

    public MediaItem getCurrentMediaItem() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.PhotoDataAdapter.getCurrentMediaItem()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.PhotoDataAdapter.getCurrentMediaItem()",this);return mData[mCurrentIndex % DATA_CACHE_SIZE];}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.PhotoDataAdapter.getCurrentMediaItem()",this,throwable);throw throwable;}
    }

    public void setCurrentPhoto(Path path, int indexHint) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.setCurrentPhoto(com.android.gallery3d.data.Path,int)",this,path,indexHint);try{if (mItemPath == path) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.setCurrentPhoto(com.android.gallery3d.data.Path,int)",this);return;}}
        mItemPath = path;
        mCurrentIndex = indexHint;
        updateSlidingWindow();
        updateImageCache();
        fireModelInvalidated();

        /*// We need to reload content if the path doesn't match.*/
        MediaItem item = getCurrentMediaItem();
        if (item != null && item.getPath() != path) {
            if (mReloadTask != null) {mReloadTask.notifyDirty();}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.setCurrentPhoto(com.android.gallery3d.data.Path,int)",this,throwable);throw throwable;}
    }

    private void updateTileProvider() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.updateTileProvider()",this);try{ImageEntry entry = mImageCache.get(getVersion(mCurrentIndex));
        if (entry == null) { /*// in loading*/
            mTileProvider.clear();
        } else {
            updateTileProvider(entry);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.updateTileProvider()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.updateTileProvider()",this,throwable);throw throwable;}
    }

    private void updateTileProvider(ImageEntry entry) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.updateTileProvider(com.android.gallery3d.app.PhotoDataAdapter$ImageEntry)",this,entry);try{Bitmap screenNail = entry.screenNail;
        BitmapRegionDecoder fullImage = entry.fullImage;
        if (screenNail != null) {
            if (fullImage != null) {
                mTileProvider.setBackupImage(screenNail,
                        fullImage.getWidth(), fullImage.getHeight());
                mTileProvider.setRegionDecoder(fullImage);
            } else {
                int width = screenNail.getWidth();
                int height = screenNail.getHeight();
                mTileProvider.setBackupImage(screenNail, width, height);
            }
        } else {
            mTileProvider.clear();
            if (entry.failToLoad) {mTileProvider.setFailedToLoad();}
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.updateTileProvider(com.android.gallery3d.app.PhotoDataAdapter$ImageEntry)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.updateTileProvider(com.android.gallery3d.app.PhotoDataAdapter$ImageEntry)",this,throwable);throw throwable;}
    }

    private void updateSlidingWindow() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.updateSlidingWindow()",this);try{/*// 1. Update the image window*/
        int start = Utils.clamp(mCurrentIndex - IMAGE_CACHE_SIZE / 2,
                0, Math.max(0, mSize - IMAGE_CACHE_SIZE));
        int end = Math.min(mSize, start + IMAGE_CACHE_SIZE);

        if (mActiveStart == start && mActiveEnd == end) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.updateSlidingWindow()",this);return;}}

        mActiveStart = start;
        mActiveEnd = end;

        /*// 2. Update the data window*/
        start = Utils.clamp(mCurrentIndex - DATA_CACHE_SIZE / 2,
                0, Math.max(0, mSize - DATA_CACHE_SIZE));
        end = Math.min(mSize, start + DATA_CACHE_SIZE);
        if (mContentStart > mActiveStart || mContentEnd < mActiveEnd
                || Math.abs(start - mContentStart) > MIN_LOAD_COUNT) {
            for (int i = mContentStart; i < mContentEnd; ++i) {
                if (i < start || i >= end) {
                    mData[i % DATA_CACHE_SIZE] = null;
                }
            }
            mContentStart = start;
            mContentEnd = end;
            if (mReloadTask != null) {mReloadTask.notifyDirty();}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.updateSlidingWindow()",this,throwable);throw throwable;}
    }

    private void updateImageRequests() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.updateImageRequests()",this);try{if (!mIsActive) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.updateImageRequests()",this);return;}}

        int currentIndex = mCurrentIndex;
        MediaItem item = mData[currentIndex % DATA_CACHE_SIZE];
        if (item == null || item.getPath() != mItemPath) {
            /*// current item mismatch - don't request image*/
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.updateImageRequests()",this);return;}
        }

        /*// 1. Find the most wanted request and start it (if not already started).*/
        Future<?> task = null;
        for (int i = 0; i < sImageFetchSeq.length; i++) {
            int offset = sImageFetchSeq[i].indexOffset;
            int bit = sImageFetchSeq[i].imageBit;
            task = startTaskIfNeeded(currentIndex + offset, bit);
            if (task != null) {break;}
        }

        /*// 2. Cancel everything else.*/
        for (ImageEntry entry : mImageCache.values()) {
            if (entry.screenNailTask != null && entry.screenNailTask != task) {
                entry.screenNailTask.cancel();
                entry.screenNailTask = null;
                entry.requestedBits &= ~BIT_SCREEN_NAIL;
            }
            if (entry.fullImageTask != null && entry.fullImageTask != task) {
                entry.fullImageTask.cancel();
                entry.fullImageTask = null;
                entry.requestedBits &= ~BIT_FULL_IMAGE;
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.updateImageRequests()",this,throwable);throw throwable;}
    }

    private static class ScreenNailJob implements Job<Bitmap> {
        private MediaItem mItem;

        public ScreenNailJob(MediaItem item) {
            mItem = item;
        }

        @Override
        public Bitmap run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.app.PhotoDataAdapter$ScreenNailJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{Bitmap bitmap = mItem.requestImage(MediaItem.TYPE_THUMBNAIL).run(jc);
            if (jc.isCancelled()) {{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.app.PhotoDataAdapter$ScreenNailJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
            if (bitmap != null) {
                bitmap = BitmapUtils.rotateBitmap(bitmap,
                    mItem.getRotation() - mItem.getFullImageRotation(), true);
            }
            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.app.PhotoDataAdapter$ScreenNailJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return bitmap;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.app.PhotoDataAdapter$ScreenNailJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }

    /*// Returns the task if we started the task or the task is already started.*/
    private Future<?> startTaskIfNeeded(int index, int which) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.Future com.android.gallery3d.app.PhotoDataAdapter.startTaskIfNeeded(int,int)",this,index,which);try{if (index < mActiveStart || index >= mActiveEnd) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.app.PhotoDataAdapter.startTaskIfNeeded(int,int)",this);return null;}}

        ImageEntry entry = mImageCache.get(getVersion(index));
        if (entry == null) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.app.PhotoDataAdapter.startTaskIfNeeded(int,int)",this);return null;}}

        if (which == BIT_SCREEN_NAIL && entry.screenNailTask != null) {
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.app.PhotoDataAdapter.startTaskIfNeeded(int,int)",this);return entry.screenNailTask;}
        } else if (which == BIT_FULL_IMAGE && entry.fullImageTask != null) {
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.app.PhotoDataAdapter.startTaskIfNeeded(int,int)",this);return entry.fullImageTask;}
        }

        MediaItem item = mData[index % DATA_CACHE_SIZE];
        Utils.assertTrue(item != null);

        if (which == BIT_SCREEN_NAIL
                && (entry.requestedBits & BIT_SCREEN_NAIL) == 0) {
            entry.requestedBits |= BIT_SCREEN_NAIL;
            entry.screenNailTask = mThreadPool.submit(
                    new ScreenNailJob(item),
                    new ScreenNailListener(item.getDataVersion()));
            /*// request screen nail*/
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.app.PhotoDataAdapter.startTaskIfNeeded(int,int)",this);return entry.screenNailTask;}
        }
        if (which == BIT_FULL_IMAGE
                && (entry.requestedBits & BIT_FULL_IMAGE) == 0
                && (item.getSupportedOperations()
                & MediaItem.SUPPORT_FULL_IMAGE) != 0) {
            entry.requestedBits |= BIT_FULL_IMAGE;
            entry.fullImageTask = mThreadPool.submit(
                    item.requestLargeImage(),
                    new FullImageListener(item.getDataVersion()));
            /*// request full image*/
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.app.PhotoDataAdapter.startTaskIfNeeded(int,int)",this);return entry.fullImageTask;}
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.app.PhotoDataAdapter.startTaskIfNeeded(int,int)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.Future com.android.gallery3d.app.PhotoDataAdapter.startTaskIfNeeded(int,int)",this,throwable);throw throwable;}
    }

    private void updateImageCache() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter.updateImageCache()",this);try{HashSet<Long> toBeRemoved = new HashSet<Long>(mImageCache.keySet());
        for (int i = mActiveStart; i < mActiveEnd; ++i) {
            MediaItem item = mData[i % DATA_CACHE_SIZE];
            long version = item == null
                    ? MediaObject.INVALID_DATA_VERSION
                    : item.getDataVersion();
            if (version == MediaObject.INVALID_DATA_VERSION) {continue;}
            ImageEntry entry = mImageCache.get(version);
            toBeRemoved.remove(version);
            if (entry != null) {
                if (Math.abs(i - mCurrentIndex) > 1) {
                    if (entry.fullImageTask != null) {
                        entry.fullImageTask.cancel();
                        entry.fullImageTask = null;
                    }
                    entry.fullImage = null;
                    entry.requestedBits &= ~BIT_FULL_IMAGE;
                }
            } else {
                entry = new ImageEntry();
                entry.rotation = item.getFullImageRotation();
                mImageCache.put(version, entry);
            }
        }

        /*// Clear the data and requests for ImageEntries outside the new window.*/
        for (Long version : toBeRemoved) {
            ImageEntry entry = mImageCache.remove(version);
            if (entry.fullImageTask != null) {entry.fullImageTask.cancel();}
            if (entry.screenNailTask != null) {entry.screenNailTask.cancel();}
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter.updateImageCache()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter.updateImageCache()",this,throwable);throw throwable;}
    }

    private class FullImageListener
            implements Runnable, FutureListener<BitmapRegionDecoder> {
        private final long mVersion;
        private Future<BitmapRegionDecoder> mFuture;

        public FullImageListener(long version) {
            mVersion = version;
        }

        @Override
        public void onFutureDone(Future<BitmapRegionDecoder> future) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter$FullImageListener.onFutureDone(com.android.gallery3d.util.Future)",this,future);try{mFuture = future;
            mMainHandler.sendMessage(
                    mMainHandler.obtainMessage(MSG_RUN_OBJECT, this));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$FullImageListener.onFutureDone(com.android.gallery3d.util.Future)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter$FullImageListener.onFutureDone(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
        }

        @Override
        public void run() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter$FullImageListener.run()",this);try{updateFullImage(mVersion, mFuture);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$FullImageListener.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter$FullImageListener.run()",this,throwable);throw throwable;}
        }
    }

    private class ScreenNailListener
            implements Runnable, FutureListener<Bitmap> {
        private final long mVersion;
        private Future<Bitmap> mFuture;

        public ScreenNailListener(long version) {
            mVersion = version;
        }

        @Override
        public void onFutureDone(Future<Bitmap> future) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter$ScreenNailListener.onFutureDone(com.android.gallery3d.util.Future)",this,future);try{mFuture = future;
            mMainHandler.sendMessage(
                    mMainHandler.obtainMessage(MSG_RUN_OBJECT, this));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$ScreenNailListener.onFutureDone(com.android.gallery3d.util.Future)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter$ScreenNailListener.onFutureDone(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
        }

        @Override
        public void run() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter$ScreenNailListener.run()",this);try{updateScreenNail(mVersion, mFuture);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$ScreenNailListener.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter$ScreenNailListener.run()",this,throwable);throw throwable;}
        }
    }

    private static class ImageEntry {
        public int requestedBits = 0;
        public int rotation;
        public BitmapRegionDecoder fullImage;
        public Bitmap screenNail;
        public Future<Bitmap> screenNailTask;
        public Future<BitmapRegionDecoder> fullImageTask;
        public boolean failToLoad = false;
    }

    private class SourceListener implements ContentListener {
        public void onContentDirty() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter$SourceListener.onContentDirty()",this);try{if (mReloadTask != null) {mReloadTask.notifyDirty();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$SourceListener.onContentDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter$SourceListener.onContentDirty()",this,throwable);throw throwable;}
        }
    }

    private <T> T executeAndWait(Callable<T> callable) {
        com.mijack.Xlog.logMethodEnter("java.lang.Object com.android.gallery3d.app.PhotoDataAdapter.executeAndWait(java.util.concurrent.Callable)",this,callable);try{FutureTask<T> task = new FutureTask<T>(callable);
        mMainHandler.sendMessage(
                mMainHandler.obtainMessage(MSG_RUN_OBJECT, task));
        try {
            {com.mijack.Xlog.logMethodExit("java.lang.Object com.android.gallery3d.app.PhotoDataAdapter.executeAndWait(java.util.concurrent.Callable)",this);return task.get();}
        } catch (InterruptedException e) {
            {com.mijack.Xlog.logMethodExit("java.lang.Object com.android.gallery3d.app.PhotoDataAdapter.executeAndWait(java.util.concurrent.Callable)",this);return null;}
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Object com.android.gallery3d.app.PhotoDataAdapter.executeAndWait(java.util.concurrent.Callable)",this,throwable);throw throwable;}
    }

    private static class UpdateInfo {
        public long version;
        public boolean reloadContent;
        public Path target;
        public int indexHint;
        public int contentStart;
        public int contentEnd;

        public int size;
        public ArrayList<MediaItem> items;
    }

    private class GetUpdateInfo implements Callable<UpdateInfo> {

        private boolean needContentReload() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.PhotoDataAdapter$GetUpdateInfo.needContentReload()",this);try{for (int i = mContentStart, n = mContentEnd; i < n; ++i) {
                if (mData[i % DATA_CACHE_SIZE] == null) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.PhotoDataAdapter$GetUpdateInfo.needContentReload()",this);return true;}}
            }
            MediaItem current = mData[mCurrentIndex % DATA_CACHE_SIZE];
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.PhotoDataAdapter$GetUpdateInfo.needContentReload()",this);return current == null || current.getPath() != mItemPath;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.PhotoDataAdapter$GetUpdateInfo.needContentReload()",this,throwable);throw throwable;}
        }

        @Override
        public UpdateInfo call() throws Exception {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.PhotoDataAdapter$UpdateInfo com.android.gallery3d.app.PhotoDataAdapter$GetUpdateInfo.call()",this);try{/*// TODO: Try to load some data in first update*/
            UpdateInfo info = new UpdateInfo();
            info.version = mSourceVersion;
            info.reloadContent = needContentReload();
            info.target = mItemPath;
            info.indexHint = mCurrentIndex;
            info.contentStart = mContentStart;
            info.contentEnd = mContentEnd;
            info.size = mSize;
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.PhotoDataAdapter$UpdateInfo com.android.gallery3d.app.PhotoDataAdapter$GetUpdateInfo.call()",this);return info;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.PhotoDataAdapter$UpdateInfo com.android.gallery3d.app.PhotoDataAdapter$GetUpdateInfo.call()",this,throwable);throw throwable;}
        }
    }

    private class UpdateContent implements Callable<Void> {
        UpdateInfo mUpdateInfo;

        public UpdateContent(UpdateInfo updateInfo) {
            mUpdateInfo = updateInfo;
        }

        @Override
        public Void call() throws Exception {
            com.mijack.Xlog.logMethodEnter("java.lang.Void com.android.gallery3d.app.PhotoDataAdapter$UpdateContent.call()",this);try{UpdateInfo info = mUpdateInfo;
            mSourceVersion = info.version;

            if (info.size != mSize) {
                mSize = info.size;
                if (mContentEnd > mSize) {mContentEnd = mSize;}
                if (mActiveEnd > mSize) {mActiveEnd = mSize;}
            }

            if (info.indexHint == MediaSet.INDEX_NOT_FOUND) {
                /*// The image has been deleted, clear mItemPath, the*/
                /*// mCurrentIndex will be updated in the updateCurrentItem().*/
                mItemPath = null;
                updateCurrentItem();
            } else {
                mCurrentIndex = info.indexHint;
            }

            updateSlidingWindow();

            if (info.items != null) {
                int start = Math.max(info.contentStart, mContentStart);
                int end = Math.min(info.contentStart + info.items.size(), mContentEnd);
                int dataIndex = start % DATA_CACHE_SIZE;
                for (int i = start; i < end; ++i) {
                    mData[dataIndex] = info.items.get(i - info.contentStart);
                    if (++dataIndex == DATA_CACHE_SIZE) {dataIndex = 0;}
                }
            }
            if (mItemPath == null) {
                MediaItem current = mData[mCurrentIndex % DATA_CACHE_SIZE];
                mItemPath = current == null ? null : current.getPath();
            }
            updateImageCache();
            updateTileProvider();
            updateImageRequests();
            fireModelInvalidated();
            {com.mijack.Xlog.logMethodExit("java.lang.Void com.android.gallery3d.app.PhotoDataAdapter$UpdateContent.call()",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Void com.android.gallery3d.app.PhotoDataAdapter$UpdateContent.call()",this,throwable);throw throwable;}
        }

        private void updateCurrentItem() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter$UpdateContent.updateCurrentItem()",this);try{if (mSize == 0) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$UpdateContent.updateCurrentItem()",this);return;}}
            if (mCurrentIndex >= mSize) {
                mCurrentIndex = mSize - 1;
                mPhotoView.notifyOnNewImage();
                mPhotoView.startSlideInAnimation(PhotoView.TRANS_SLIDE_IN_LEFT);
            } else {
                mPhotoView.notifyOnNewImage();
                mPhotoView.startSlideInAnimation(PhotoView.TRANS_SLIDE_IN_RIGHT);
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter$UpdateContent.updateCurrentItem()",this,throwable);throw throwable;}
        }
    }

    private class ReloadTask extends Thread {
        private volatile boolean mActive = true;
        private volatile boolean mDirty = true;

        private boolean mIsLoading = false;

        private void updateLoading(boolean loading) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.updateLoading(boolean)",this,loading);try{if (mIsLoading == loading) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.updateLoading(boolean)",this);return;}}
            mIsLoading = loading;
            mMainHandler.sendEmptyMessage(loading ? MSG_LOAD_START : MSG_LOAD_FINISH);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.updateLoading(boolean)",this,throwable);throw throwable;}
        }

        @Override
        public void run() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.run()",this);try{while (mActive) {
                synchronized (this) {
                    if (!mDirty && mActive) {
                        updateLoading(false);
                        Utils.waitWithoutInterrupt(this);
                        continue;
                    }
                }
                mDirty = false;
                UpdateInfo info = executeAndWait(new GetUpdateInfo());
                synchronized (DataManager.LOCK) {
                    updateLoading(true);
                    long version = mSource.reload();
                    if (info.version != version) {
                        info.reloadContent = true;
                        info.size = mSource.getMediaItemCount();
                    }
                    if (!info.reloadContent) {continue;}
                    info.items =  mSource.getMediaItem(info.contentStart, info.contentEnd);
                    MediaItem item = findCurrentMediaItem(info);
                    if (item == null || item.getPath() != info.target) {
                        info.indexHint = findIndexOfTarget(info);
                    }
                }
                executeAndWait(new UpdateContent(info));
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.run()",this,throwable);throw throwable;}
        }

        public synchronized void notifyDirty() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.notifyDirty()",this);try{mDirty = true;
            notifyAll();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.notifyDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.notifyDirty()",this,throwable);throw throwable;}
        }

        public synchronized void terminate() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.terminate()",this);try{mActive = false;
            notifyAll();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.terminate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.terminate()",this,throwable);throw throwable;}
        }

        private MediaItem findCurrentMediaItem(UpdateInfo info) {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.findCurrentMediaItem(com.android.gallery3d.app.PhotoDataAdapter$UpdateInfo)",this,info);try{ArrayList<MediaItem> items = info.items;
            int index = info.indexHint - info.contentStart;
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.findCurrentMediaItem(com.android.gallery3d.app.PhotoDataAdapter$UpdateInfo)",this);return index < 0 || index >= items.size() ? null : items.get(index);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.findCurrentMediaItem(com.android.gallery3d.app.PhotoDataAdapter$UpdateInfo)",this,throwable);throw throwable;}
        }

        private int findIndexOfTarget(UpdateInfo info) {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.findIndexOfTarget(com.android.gallery3d.app.PhotoDataAdapter$UpdateInfo)",this,info);try{if (info.target == null) {{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.findIndexOfTarget(com.android.gallery3d.app.PhotoDataAdapter$UpdateInfo)",this);return info.indexHint;}}
            ArrayList<MediaItem> items = info.items;

            /*// First, try to find the item in the data just loaded*/
            if (items != null) {
                for (int i = 0, n = items.size(); i < n; ++i) {
                    if (items.get(i).getPath() == info.target) {{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.findIndexOfTarget(com.android.gallery3d.app.PhotoDataAdapter$UpdateInfo)",this);return i + info.contentStart;}}
                }
            }

            /*// Not found, find it in mSource.*/
            {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.findIndexOfTarget(com.android.gallery3d.app.PhotoDataAdapter$UpdateInfo)",this);return mSource.getIndexOfItem(info.target, info.indexHint);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.PhotoDataAdapter$ReloadTask.findIndexOfTarget(com.android.gallery3d.app.PhotoDataAdapter$UpdateInfo)",this,throwable);throw throwable;}
        }
    }
}
