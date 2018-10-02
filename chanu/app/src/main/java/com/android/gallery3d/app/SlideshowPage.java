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
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.GLView;
import com.android.gallery3d.ui.SlideshowView;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

public class SlideshowPage extends ActivityState {
    private static final String TAG = "SlideshowPage";

    public static final String KEY_SET_PATH = "media-set-path";
    public static final String KEY_ITEM_PATH = "media-item-path";
    public static final String KEY_PHOTO_INDEX = "photo-index";
    public static final String KEY_RANDOM_ORDER = "random-order";
    public static final String KEY_REPEAT = "repeat";

    private static final long SLIDESHOW_DELAY = 3000; /*// 3 seconds*/

    private static final int MSG_LOAD_NEXT_BITMAP = 1;
    private static final int MSG_SHOW_PENDING_BITMAP = 2;

    public static interface Model {
        public void pause();
        public void resume();
        public Future<Slide> nextSlide(FutureListener<Slide> listener);
    }

    public static class Slide {
        public Bitmap bitmap;
        public MediaItem item;
        public int index;

        public Slide(MediaItem item, int index, Bitmap bitmap) {
            this.bitmap = bitmap;
            this.item = item;
            this.index = index;
        }
    }

    private Handler mHandler;
    private Model mModel;
    private SlideshowView mSlideshowView;

    private Slide mPendingSlide = null;
    private boolean mIsActive = false;
    private Intent mResultIntent = new Intent();

    private GLView mRootPane = new GLView() {
        @Override
        protected void onLayout(
                boolean changed, int left, int top, int right, int bottom) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage$1.onLayout(boolean,int,int,int,int)",this,changed,left,top,right,bottom);try{mSlideshowView.layout(0, 0, right - left, bottom - top);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage$1.onLayout(boolean,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage$1.onLayout(boolean,int,int,int,int)",this,throwable);throw throwable;}
        }

        @Override
        protected boolean onTouch(MotionEvent event) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.SlideshowPage$1.onTouch(android.view.MotionEvent)",this,event);try{if (event.getAction() == MotionEvent.ACTION_UP) {
                onBackPressed();
            }
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.SlideshowPage$1.onTouch(android.view.MotionEvent)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.SlideshowPage$1.onTouch(android.view.MotionEvent)",this,throwable);throw throwable;}
        }

        @Override
        protected void renderBackground(GLCanvas canvas) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage$1.renderBackground(com.android.gallery3d.ui.GLCanvas)",this,canvas);try{canvas.clearBuffer();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage$1.renderBackground(com.android.gallery3d.ui.GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage$1.renderBackground(com.android.gallery3d.ui.GLCanvas)",this,throwable);throw throwable;}
        }
    };

    @Override
    public void onCreate(Bundle data, Bundle restoreState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage.onCreate(android.os.Bundle,android.os.Bundle)",this,data,restoreState);try{mFlags |= (FLAG_HIDE_ACTION_BAR | FLAG_HIDE_STATUS_BAR);

        mHandler = new SynchronizedHandler(mActivity.getGLRoot()) {
            @Override
            public void handleMessage(Message message) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage$2.handleMessage(android.os.Message)",this,message);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage$2.handleMessage(android.os.Message)",this);switch (message.what) {
                    case MSG_SHOW_PENDING_BITMAP:
                        showPendingBitmap();
                        break;
                    case MSG_LOAD_NEXT_BITMAP:
                        loadNextBitmap();
                        break;
                    default: throw new AssertionError();
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage$2.handleMessage(android.os.Message)",this,throwable);throw throwable;}
            }
        };
        initializeViews();
        initializeData(data);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage.onCreate(android.os.Bundle,android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void loadNextBitmap() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage.loadNextBitmap()",this);try{mModel.nextSlide(new FutureListener<Slide>() {
            public void onFutureDone(Future<Slide> future) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage$3.onFutureDone(com.android.gallery3d.util.Future)",this,future);try{mPendingSlide = future.get();
                mHandler.sendEmptyMessage(MSG_SHOW_PENDING_BITMAP);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage$3.onFutureDone(com.android.gallery3d.util.Future)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage$3.onFutureDone(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage.loadNextBitmap()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage.loadNextBitmap()",this,throwable);throw throwable;}
    }

    private void showPendingBitmap() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage.showPendingBitmap()",this);try{/*// mPendingBitmap could be null, if*/
        /*//    1.) there is no more items*/
        /*//    2.) mModel is paused*/
        Slide slide = mPendingSlide;
        if (slide == null) {
            if (mIsActive) {
                mActivity.getStateManager().finishState(SlideshowPage.this);
            }
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage.showPendingBitmap()",this);return;}
        }

        mSlideshowView.next(slide.bitmap, slide.item.getRotation());

        setStateResult(Activity.RESULT_OK, mResultIntent
                .putExtra(KEY_ITEM_PATH, slide.item.getPath().toString())
                .putExtra(KEY_PHOTO_INDEX, slide.index));
        mHandler.sendEmptyMessageDelayed(MSG_LOAD_NEXT_BITMAP,
                SLIDESHOW_DELAY);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage.showPendingBitmap()",this,throwable);throw throwable;}
    }

    @Override
    public void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage.onPause()",this);try{super.onPause();
        mIsActive = false;
        mModel.pause();
        mSlideshowView.release();

        mHandler.removeMessages(MSG_LOAD_NEXT_BITMAP);
        mHandler.removeMessages(MSG_SHOW_PENDING_BITMAP);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage.onPause()",this,throwable);throw throwable;}
    }

    @Override
    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage.onResume()",this);try{super.onResume();
        mIsActive = true;
        mModel.resume();

        if (mPendingSlide != null) {
            showPendingBitmap();
        } else {
            loadNextBitmap();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage.onResume()",this,throwable);throw throwable;}
    }

    private void initializeData(Bundle data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage.initializeData(android.os.Bundle)",this,data);try{boolean random = data.getBoolean(KEY_RANDOM_ORDER, false);

        /*// We only want to show slideshow for images only, not videos.*/
        String mediaPath = data.getString(KEY_SET_PATH);
        mediaPath = FilterUtils.newFilterPath(mediaPath,
                FilterUtils.FILTER_IMAGE_ONLY);
        MediaSet mediaSet = mActivity.getDataManager().getMediaSet(mediaPath);

        if (random) {
            boolean repeat = data.getBoolean(KEY_REPEAT);
            mModel = new SlideshowDataAdapter(
                    mActivity, new ShuffleSource(mediaSet, repeat), 0);
            setStateResult(Activity.RESULT_OK,
                    mResultIntent.putExtra(KEY_PHOTO_INDEX, 0));
        } else {
            int index = data.getInt(KEY_PHOTO_INDEX);
            boolean repeat = data.getBoolean(KEY_REPEAT);
            mModel = new SlideshowDataAdapter(mActivity,
                    new SequentialSource(mediaSet, repeat), index);
            setStateResult(Activity.RESULT_OK,
                    mResultIntent.putExtra(KEY_PHOTO_INDEX, index));
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage.initializeData(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage.initializeData(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void initializeViews() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage.initializeViews()",this);try{mSlideshowView = new SlideshowView();
        mRootPane.addComponent(mSlideshowView);
        setContentPane(mRootPane);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage.initializeViews()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage.initializeViews()",this,throwable);throw throwable;}
    }

    private static MediaItem findMediaItem(MediaSet mediaSet, int index) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage.findMediaItem(com.android.gallery3d.data.MediaSet,int)",mediaSet,index);try{for (int i = 0, n = mediaSet.getSubMediaSetCount(); i < n; ++i) {
            MediaSet subset = mediaSet.getSubMediaSet(i);
            int count = subset.getTotalMediaItemCount();
            if (index < count) {
                {com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage.findMediaItem(com.android.gallery3d.data.MediaSet,int)");return findMediaItem(subset, index);}
            }
            index -= count;
        }
        ArrayList<MediaItem> list = mediaSet.getMediaItem(index, 1);
        {com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage.findMediaItem(com.android.gallery3d.data.MediaSet,int)");return list.isEmpty() ? null : list.get(0);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage.findMediaItem(com.android.gallery3d.data.MediaSet,int)",throwable);throw throwable;}
    }

    private static class ShuffleSource implements SlideshowDataAdapter.SlideshowSource {
        private static final int RETRY_COUNT = 5;
        private final MediaSet mMediaSet;
        private final Random mRandom = new Random();
        private int mOrder[] = new int[0];
        private boolean mRepeat;
        private long mSourceVersion = MediaSet.INVALID_DATA_VERSION;
        private int mLastIndex = -1;

        public ShuffleSource(MediaSet mediaSet, boolean repeat) {
            mMediaSet = Utils.checkNotNull(mediaSet);
            mRepeat = repeat;
        }

        public MediaItem getMediaItem(int index) {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage$ShuffleSource.getMediaItem(int)",this,index);try{if (!mRepeat && index >= mOrder.length) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage$ShuffleSource.getMediaItem(int)",this);return null;}}
            if (mOrder.length == 0) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage$ShuffleSource.getMediaItem(int)",this);return null;}}
            mLastIndex = mOrder[index % mOrder.length];
            MediaItem item = findMediaItem(mMediaSet, mLastIndex);
            for (int i = 0; i < RETRY_COUNT && item == null; ++i) {
                Log.w(TAG, "fail to find image: " + mLastIndex);
                mLastIndex = mRandom.nextInt(mOrder.length);
                item = findMediaItem(mMediaSet, mLastIndex);
            }
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage$ShuffleSource.getMediaItem(int)",this);return item;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage$ShuffleSource.getMediaItem(int)",this,throwable);throw throwable;}
        }

        public long reload() {
            com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.app.SlideshowPage$ShuffleSource.reload()",this);try{long version = mMediaSet.reload();
            if (version != mSourceVersion) {
                mSourceVersion = version;
                int count = mMediaSet.getTotalMediaItemCount();
                if (count != mOrder.length) {generateOrderArray(count);}
            }
            {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.app.SlideshowPage$ShuffleSource.reload()",this);return version;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.app.SlideshowPage$ShuffleSource.reload()",this,throwable);throw throwable;}
        }

        private void generateOrderArray(int totalCount) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage$ShuffleSource.generateOrderArray(int)",this,totalCount);try{if (mOrder.length != totalCount) {
                mOrder = new int[totalCount];
                for (int i = 0; i < totalCount; ++i) {
                    mOrder[i] = i;
                }
            }
            for (int i = totalCount - 1; i > 0; --i) {
                Utils.swap(mOrder, i, mRandom.nextInt(i + 1));
            }
            if (mOrder[0] == mLastIndex && totalCount > 1) {
                Utils.swap(mOrder, 0, mRandom.nextInt(totalCount - 1) + 1);
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage$ShuffleSource.generateOrderArray(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage$ShuffleSource.generateOrderArray(int)",this,throwable);throw throwable;}
        }

        public void addContentListener(ContentListener listener) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage$ShuffleSource.addContentListener(com.android.gallery3d.data.ContentListener)",this,listener);try{mMediaSet.addContentListener(listener);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage$ShuffleSource.addContentListener(com.android.gallery3d.data.ContentListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage$ShuffleSource.addContentListener(com.android.gallery3d.data.ContentListener)",this,throwable);throw throwable;}
        }

        public void removeContentListener(ContentListener listener) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage$ShuffleSource.removeContentListener(com.android.gallery3d.data.ContentListener)",this,listener);try{mMediaSet.removeContentListener(listener);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage$ShuffleSource.removeContentListener(com.android.gallery3d.data.ContentListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage$ShuffleSource.removeContentListener(com.android.gallery3d.data.ContentListener)",this,throwable);throw throwable;}
        }
    }

    private static class SequentialSource implements SlideshowDataAdapter.SlideshowSource {
        private static final int DATA_SIZE = 32;

        private ArrayList<MediaItem> mData = new ArrayList<MediaItem>();
        private int mDataStart = 0;
        private long mDataVersion = MediaObject.INVALID_DATA_VERSION;
        private final MediaSet mMediaSet;
        private final boolean mRepeat;

        public SequentialSource(MediaSet mediaSet, boolean repeat) {
            mMediaSet = mediaSet;
            mRepeat = repeat;
        }

        public MediaItem getMediaItem(int index) {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage$SequentialSource.getMediaItem(int)",this,index);try{int dataEnd = mDataStart + mData.size();

            if (mRepeat) {
                int count = mMediaSet.getMediaItemCount();
                if (count == 0) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage$SequentialSource.getMediaItem(int)",this);return null;}}
                index = index % count;
            }
            if (index < mDataStart || index >= dataEnd) {
                mData = mMediaSet.getMediaItem(index, DATA_SIZE);
                mDataStart = index;
                dataEnd = index + mData.size();
            }

            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage$SequentialSource.getMediaItem(int)",this);return (index < mDataStart || index >= dataEnd)
                    ? null
                    : mData.get(index - mDataStart);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SlideshowPage$SequentialSource.getMediaItem(int)",this,throwable);throw throwable;}
        }

        public long reload() {
            com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.app.SlideshowPage$SequentialSource.reload()",this);try{long version = mMediaSet.reload();
            if (version != mDataVersion) {
                mDataVersion = version;
                mData.clear();
            }
            {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.app.SlideshowPage$SequentialSource.reload()",this);return mDataVersion;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.app.SlideshowPage$SequentialSource.reload()",this,throwable);throw throwable;}
        }

        public void addContentListener(ContentListener listener) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage$SequentialSource.addContentListener(com.android.gallery3d.data.ContentListener)",this,listener);try{mMediaSet.addContentListener(listener);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage$SequentialSource.addContentListener(com.android.gallery3d.data.ContentListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage$SequentialSource.addContentListener(com.android.gallery3d.data.ContentListener)",this,throwable);throw throwable;}
        }

        public void removeContentListener(ContentListener listener) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SlideshowPage$SequentialSource.removeContentListener(com.android.gallery3d.data.ContentListener)",this,listener);try{mMediaSet.removeContentListener(listener);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SlideshowPage$SequentialSource.removeContentListener(com.android.gallery3d.data.ContentListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SlideshowPage$SequentialSource.removeContentListener(com.android.gallery3d.data.ContentListener)",this,throwable);throw throwable;}
        }
    }
}
