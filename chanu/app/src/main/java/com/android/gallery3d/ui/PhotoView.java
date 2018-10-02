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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.android.gallery3d.app.GalleryActivity;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.PositionRepository.Position;
import com.chanapps.four.gallery3d.R;

public class PhotoView extends GLView {
    private static final String TAG = PhotoView.class.getSimpleName();
    private static final boolean DEBUG = false;

    public static final int INVALID_SIZE = -1;

    private static final int MSG_TRANSITION_COMPLETE = 1;
    private static final int MSG_SHOW_LOADING = 2;

    private static final long DELAY_SHOW_LOADING = 250; /*// 250ms;*/

    private static final int TRANS_NONE = 0;
    private static final int TRANS_SWITCH_NEXT = 3;
    private static final int TRANS_SWITCH_PREVIOUS = 4;

    public static final int TRANS_SLIDE_IN_RIGHT = 1;
    public static final int TRANS_SLIDE_IN_LEFT = 2;
    public static final int TRANS_OPEN_ANIMATION = 5;

    private static final int LOADING_INIT = 0;
    private static final int LOADING_TIMEOUT = 1;
    private static final int LOADING_COMPLETE = 2;
    private static final int LOADING_FAIL = 3;

    private static final int ENTRY_PREVIOUS = 0;
    private static final int ENTRY_NEXT = 1;

    private static final int IMAGE_GAP = 96;
    private static final int SWITCH_THRESHOLD = 256;
    private static final float SWIPE_THRESHOLD = 300f;

    private static final float DEFAULT_TEXT_SIZE = 20;

    public interface PhotoTapListener {
        public void onSingleTapUp(int x, int y);
    }

    /*// the previous/next image entries*/
    private final ScreenNailEntry mScreenNails[] = new ScreenNailEntry[2];

    private final ScaleGestureDetector mScaleDetector;
    private final GestureDetector mGestureDetector;
    private final DownUpDetector mDownUpDetector;

    private PhotoTapListener mPhotoTapListener;

    private final PositionController mPositionController;

    private Model mModel;
    private StringTexture mLoadingText;
    private StringTexture mNoThumbnailText;
    private int mTransitionMode = TRANS_NONE;
    private final TileImageView mTileView;
    private EdgeView mEdgeView;
    private Texture mVideoPlayIcon;

    private boolean mShowVideoPlayIcon;
    private ProgressSpinner mLoadingSpinner;

    private SynchronizedHandler mHandler;

    private int mLoadingState = LOADING_COMPLETE;

    private int mImageRotation;

    private Path mOpenedItemPath;
    private GalleryActivity mActivity;

    public PhotoView(GalleryActivity activity) {
        mActivity = activity;
        mTileView = new TileImageView(activity);
        addComponent(mTileView);
        Context context = activity.getAndroidContext();
        mEdgeView = new EdgeView(context);
        addComponent(mEdgeView);
        mLoadingSpinner = new ProgressSpinner(context);
        mLoadingText = StringTexture.newInstance(
                context.getString(R.string.loading),
                DEFAULT_TEXT_SIZE, Color.WHITE);
        mNoThumbnailText = StringTexture.newInstance(
                context.getString(R.string.no_thumbnail),
                DEFAULT_TEXT_SIZE, Color.WHITE);

        mHandler = new SynchronizedHandler(activity.getGLRoot()) {
            @Override
            public void handleMessage(Message message) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView$1.handleMessage(android.os.Message)",this,message);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView$1.handleMessage(android.os.Message)",this);switch (message.what) {
                    case MSG_TRANSITION_COMPLETE: {
                        onTransitionComplete();
                        break;
                    }
                    case MSG_SHOW_LOADING: {
                        if (mLoadingState == LOADING_INIT) {
                            /*// We don't need the opening animation*/
                            mOpenedItemPath = null;

                            mLoadingSpinner.startAnimation();
                            mLoadingState = LOADING_TIMEOUT;
                            invalidate();
                        }
                        break;
                    }
                    default: throw new AssertionError(message.what);
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView$1.handleMessage(android.os.Message)",this,throwable);throw throwable;}
            }
        };

        mGestureDetector = new GestureDetector(context,
                new MyGestureListener(), null, true /* ignoreMultitouch */);
        mScaleDetector = new ScaleGestureDetector(context, new MyScaleListener());
        mDownUpDetector = new DownUpDetector(new MyDownUpListener());

        for (int i = 0, n = mScreenNails.length; i < n; ++i) {
            mScreenNails[i] = new ScreenNailEntry();
        }

        mPositionController = new PositionController(this, context, mEdgeView);
        mVideoPlayIcon = new ResourceTexture(context, R.drawable.ic_control_play);
    }


    public void setModel(Model model) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.setModel(Model)",this,model);try{if (mModel == model) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.setModel(Model)",this);return;}}
        mModel = model;
        mTileView.setModel(model);
        if (model != null) {notifyOnNewImage();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.setModel(Model)",this,throwable);throw throwable;}
    }

    public void setPhotoTapListener(PhotoTapListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.setPhotoTapListener(PhotoTapListener)",this,listener);try{mPhotoTapListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.setPhotoTapListener(PhotoTapListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.setPhotoTapListener(PhotoTapListener)",this,throwable);throw throwable;}
    }

    private boolean setTileViewPosition(int centerX, int centerY, float scale) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView.setTileViewPosition(int,int,float)",this,centerX,centerY,scale);try{int inverseX = mPositionController.getImageWidth() - centerX;
        int inverseY = mPositionController.getImageHeight() - centerY;
        TileImageView t = mTileView;
        int rotation = mImageRotation;
        switch (rotation) {
            case 0: {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.setTileViewPosition(int,int,float)",this);return t.setPosition(centerX, centerY, scale, 0);}
            case 90: {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.setTileViewPosition(int,int,float)",this);return t.setPosition(centerY, inverseX, scale, 90);}
            case 180: {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.setTileViewPosition(int,int,float)",this);return t.setPosition(inverseX, inverseY, scale, 180);}
            case 270: {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.setTileViewPosition(int,int,float)",this);return t.setPosition(inverseY, centerX, scale, 270);}
            default: throw new IllegalArgumentException(String.valueOf(rotation));
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView.setTileViewPosition(int,int,float)",this,throwable);throw throwable;}
    }

    public void setPosition(int centerX, int centerY, float scale) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.setPosition(int,int,float)",this,centerX,centerY,scale);try{Log.i(TAG, "setPosition x=" + centerX + " y=" + centerY + " scale=" + scale);
        if (setTileViewPosition(centerX, centerY, scale)) {
            layoutScreenNails();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.setPosition(int,int,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.setPosition(int,int,float)",this,throwable);throw throwable;}
    }

    private void updateScreenNailEntry(int which, ImageData data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.updateScreenNailEntry(int,com.android.gallery3d.ui.PhotoView$ImageData)",this,which,data);try{if (mTransitionMode == TRANS_SWITCH_NEXT
                || mTransitionMode == TRANS_SWITCH_PREVIOUS) {
            /*// ignore screen nail updating during switching*/
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.updateScreenNailEntry(int,com.android.gallery3d.ui.PhotoView$ImageData)",this);return;}
        }
        ScreenNailEntry entry = mScreenNails[which];
        if (data == null) {
            entry.set(false, null, 0);
        } else {
            entry.set(true, data.bitmap, data.rotation);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.updateScreenNailEntry(int,com.android.gallery3d.ui.PhotoView$ImageData)",this,throwable);throw throwable;}
    }

    /*// -1 previous, 0 current, 1 next*/
    public void notifyImageInvalidated(int which) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.notifyImageInvalidated(int)",this,which);try{switch (which) {
            case -1: {
                updateScreenNailEntry(
                        ENTRY_PREVIOUS, mModel.getPreviousImage());
                layoutScreenNails();
                invalidate();
                break;
            }
            case 1: {
                updateScreenNailEntry(ENTRY_NEXT, mModel.getNextImage());
                layoutScreenNails();
                invalidate();
                break;
            }
            case 0: {
                /*// mImageWidth and mImageHeight will get updated*/
                mTileView.notifyModelInvalidated();

                mImageRotation = mModel.getImageRotation();
                if (((mImageRotation / 90) & 1) == 0) {
                    mPositionController.setImageSize(
                            mTileView.mImageWidth, mTileView.mImageHeight);
                } else {
                    mPositionController.setImageSize(
                            mTileView.mImageHeight, mTileView.mImageWidth);
                }
                updateLoadingState();
                break;
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.notifyImageInvalidated(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.notifyImageInvalidated(int)",this,throwable);throw throwable;}
    }

    private void updateLoadingState() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.updateLoadingState()",this);try{/*// Possible transitions of mLoadingState:*/
        /*//        INIT --> TIMEOUT, COMPLETE, FAIL*/
        /*//     TIMEOUT --> COMPLETE, FAIL, INIT*/
        /*//    COMPLETE --> INIT*/
        /*//        FAIL --> INIT*/
        if (mModel.getLevelCount() != 0 || mModel.getBackupImage() != null) {
            mHandler.removeMessages(MSG_SHOW_LOADING);
            mLoadingState = LOADING_COMPLETE;
        } else if (mModel.isFailedToLoad()) {
            mHandler.removeMessages(MSG_SHOW_LOADING);
            mLoadingState = LOADING_FAIL;
        } else if (mLoadingState != LOADING_INIT) {
            mLoadingState = LOADING_INIT;
            mHandler.removeMessages(MSG_SHOW_LOADING);
            mHandler.sendEmptyMessageDelayed(
                    MSG_SHOW_LOADING, DELAY_SHOW_LOADING);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.updateLoadingState()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.updateLoadingState()",this,throwable);throw throwable;}
    }

    public void notifyModelInvalidated() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.notifyModelInvalidated()",this);try{if (mModel == null) {
            updateScreenNailEntry(ENTRY_PREVIOUS, null);
            updateScreenNailEntry(ENTRY_NEXT, null);
        } else {
            updateScreenNailEntry(ENTRY_PREVIOUS, mModel.getPreviousImage());
            updateScreenNailEntry(ENTRY_NEXT, mModel.getNextImage());
        }
        layoutScreenNails();

        if (mModel == null) {
            mTileView.notifyModelInvalidated();
            mImageRotation = 0;
            mPositionController.setImageSize(0, 0);
            updateLoadingState();
        } else {
            notifyImageInvalidated(0);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.notifyModelInvalidated()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.notifyModelInvalidated()",this,throwable);throw throwable;}
    }

    @Override
    protected boolean onTouch(MotionEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView.onTouch(android.view.MotionEvent)",this,event);try{mGestureDetector.onTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        mDownUpDetector.onTouchEvent(event);
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.onTouch(android.view.MotionEvent)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView.onTouch(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    @Override
    protected void onLayout(
            boolean changeSize, int left, int top, int right, int bottom) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.onLayout(boolean,int,int,int,int)",this,changeSize,left,top,right,bottom);try{mTileView.layout(left, top, right, bottom);
        mEdgeView.layout(left, top, right, bottom);
        if (changeSize) {
            mPositionController.setViewSize(getWidth(), getHeight());
            for (ScreenNailEntry entry : mScreenNails) {
                entry.updateDrawingSize();
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.onLayout(boolean,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.onLayout(boolean,int,int,int,int)",this,throwable);throw throwable;}
    }

    private static int gapToSide(int imageWidth, int viewWidth) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.ui.PhotoView.gapToSide(int,int)",imageWidth,viewWidth);try{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.PhotoView.gapToSide(int,int)");return Math.max(0, (viewWidth - imageWidth) / 2);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.ui.PhotoView.gapToSide(int,int)",throwable);throw throwable;}
    }

    /*
     * Here is how we layout the screen nails
     *
     *  previous            current           next
     *  ___________       ________________     __________
     * |  _______  |     |   __________   |   |  ______  |
     * | |       | |     |  |   right->|  |   | |      | |
     * | |       |<-------->|<--left   |  |   | |      | |
     * | |_______| |  |  |  |__________|  |   | |______| |
     * |___________|  |  |________________|   |__________|
     *                |  <--> gapToSide()
     *                |
     * IMAGE_GAP + Max(previous.gapToSide(), current.gapToSide)
     */
    private void layoutScreenNails() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.layoutScreenNails()",this);try{int width = getWidth();
        int height = getHeight();

        /*// Use the image width in AC, since we may fake the size if the*/
        /*// image is unavailable*/
        RectF bounds = mPositionController.getImageBounds();
        int left = Math.round(bounds.left);
        int right = Math.round(bounds.right);
        int gap = gapToSide(right - left, width);

        /*// layout the previous image*/
        ScreenNailEntry entry = mScreenNails[ENTRY_PREVIOUS];

        if (entry.isEnabled()) {
            entry.layoutRightEdgeAt(left - (
                    IMAGE_GAP + Math.max(gap, entry.gapToSide())));
        }

        /*// layout the next image*/
        entry = mScreenNails[ENTRY_NEXT];
        if (entry.isEnabled()) {
            entry.layoutLeftEdgeAt(right + (
                    IMAGE_GAP + Math.max(gap, entry.gapToSide())));
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.layoutScreenNails()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.layoutScreenNails()",this,throwable);throw throwable;}
    }

    @Override
    protected void render(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.render(GLCanvas)",this,canvas);try{PositionController p = mPositionController;

        try {
	        /*// Draw the current photo*/
	        if (mLoadingState == LOADING_COMPLETE) {
	            super.render(canvas);
	        }
        } catch (Exception e) {
        	Log.e(TAG, "Rendering error", e);
        }

        /*// Draw the previous and the next photo*/
        if (mTransitionMode != TRANS_SLIDE_IN_LEFT
                && mTransitionMode != TRANS_SLIDE_IN_RIGHT
                && mTransitionMode != TRANS_OPEN_ANIMATION) {
            ScreenNailEntry prevNail = mScreenNails[ENTRY_PREVIOUS];
            ScreenNailEntry nextNail = mScreenNails[ENTRY_NEXT];

            if (prevNail.mVisible) {prevNail.draw(canvas);}
            if (nextNail.mVisible) {nextNail.draw(canvas);}
        }

        /*// Draw the progress spinner and the text below it*/
        /*//*/
        /*// (x, y) is where we put the center of the spinner.*/
        /*// s is the size of the video play icon, and we use s to layout text*/
        /*// because we want to keep the text at the same place when the video*/
        /*// play icon is shown instead of the spinner.*/
        int w = getWidth();
        int h = getHeight();
        int x = Math.round(mPositionController.getImageBounds().centerX());
        int y = h / 2;
        int s = Math.min(getWidth(), getHeight()) / 6;

        try {
            if (mLoadingState == LOADING_TIMEOUT) {
                StringTexture m = mLoadingText;
                ProgressSpinner r = mLoadingSpinner;
                r.draw(canvas, x - r.getWidth() / 2, y - r.getHeight() / 2);
                m.draw(canvas, x - m.getWidth() / 2, y + s / 2 + 5);
                invalidate(); /*// we need to keep the spinner rotating*/
            } else if (mLoadingState == LOADING_FAIL) {
                StringTexture m = mNoThumbnailText;
                m.draw(canvas, x - m.getWidth() / 2, y + s / 2 + 5);
            }

            /*// Draw the video play icon (in the place where the spinner was)*/
            if (mShowVideoPlayIcon
                    && mLoadingState != LOADING_INIT
                    && mLoadingState != LOADING_TIMEOUT) {
                mVideoPlayIcon.draw(canvas, x - s / 2, y - s / 2, s, s);
            }

            if (mPositionController.advanceAnimation()) {invalidate();}
        }
        catch (RuntimeException e) {
            Log.e(TAG, "Exception rentering spinner", e);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.render(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.render(GLCanvas)",this,throwable);throw throwable;}
    }

    private void stopCurrentSwipingIfNeeded() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.stopCurrentSwipingIfNeeded()",this);try{/*// Enable fast sweeping*/
        if (mTransitionMode == TRANS_SWITCH_NEXT) {
            mTransitionMode = TRANS_NONE;
            mPositionController.stopAnimation();
            switchToNextImage();
        } else if (mTransitionMode == TRANS_SWITCH_PREVIOUS) {
            mTransitionMode = TRANS_NONE;
            mPositionController.stopAnimation();
            switchToPreviousImage();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.stopCurrentSwipingIfNeeded()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.stopCurrentSwipingIfNeeded()",this,throwable);throw throwable;}
    }

    private boolean swipeImages(float velocity) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView.swipeImages(float)",this,velocity);try{if (mTransitionMode != TRANS_NONE
                && mTransitionMode != TRANS_SWITCH_NEXT
                && mTransitionMode != TRANS_SWITCH_PREVIOUS) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.swipeImages(float)",this);return false;}}

        ScreenNailEntry next = mScreenNails[ENTRY_NEXT];
        ScreenNailEntry prev = mScreenNails[ENTRY_PREVIOUS];

        int width = getWidth();

        /*// If we are at the edge of the current photo and the sweeping velocity*/
        /*// exceeds the threshold, switch to next / previous image.*/
        PositionController controller = mPositionController;
        boolean isMinimal = controller.isAtMinimalScale();

        if (velocity < -SWIPE_THRESHOLD &&
                (isMinimal || controller.isAtRightEdge())) {
            stopCurrentSwipingIfNeeded();
            if (next.isEnabled()) {
                mTransitionMode = TRANS_SWITCH_NEXT;
                controller.startHorizontalSlide(next.mOffsetX - width / 2);
                {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.swipeImages(float)",this);return true;}
            }
        } else if (velocity > SWIPE_THRESHOLD &&
                (isMinimal || controller.isAtLeftEdge())) {
            stopCurrentSwipingIfNeeded();
            if (prev.isEnabled()) {
                mTransitionMode = TRANS_SWITCH_PREVIOUS;
                controller.startHorizontalSlide(prev.mOffsetX - width / 2);
                {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.swipeImages(float)",this);return true;}
            }
        }

        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.swipeImages(float)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView.swipeImages(float)",this,throwable);throw throwable;}
    }

    public boolean snapToNeighborImage() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView.snapToNeighborImage()",this);try{if (mTransitionMode != TRANS_NONE) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.snapToNeighborImage()",this);return false;}}

        ScreenNailEntry next = mScreenNails[ENTRY_NEXT];
        ScreenNailEntry prev = mScreenNails[ENTRY_PREVIOUS];

        int width = getWidth();
        PositionController controller = mPositionController;

        RectF bounds = controller.getImageBounds();
        int left = Math.round(bounds.left);
        int right = Math.round(bounds.right);
        int threshold = SWITCH_THRESHOLD + gapToSide(right - left, width);

        /*// If we have moved the picture a lot, switching.*/
        if (next.isEnabled() && threshold < width - right) {
            mTransitionMode = TRANS_SWITCH_NEXT;
            controller.startHorizontalSlide(next.mOffsetX - width / 2);
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.snapToNeighborImage()",this);return true;}
        }
        if (prev.isEnabled() && threshold < left) {
            mTransitionMode = TRANS_SWITCH_PREVIOUS;
            controller.startHorizontalSlide(prev.mOffsetX - width / 2);
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.snapToNeighborImage()",this);return true;}
        }

        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.snapToNeighborImage()",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView.snapToNeighborImage()",this,throwable);throw throwable;}
    }

    private boolean mIgnoreUpEvent = false;

    private class MyGestureListener
            extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(
                MotionEvent e1, MotionEvent e2, float dx, float dy) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onScroll(android.view.MotionEvent,android.view.MotionEvent,float,float)",this,e1,e2,dx,dy);try{if (mTransitionMode != TRANS_NONE) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onScroll(android.view.MotionEvent,android.view.MotionEvent,float,float)",this);return true;}}

            ScreenNailEntry next = mScreenNails[ENTRY_NEXT];
            ScreenNailEntry prev = mScreenNails[ENTRY_PREVIOUS];

            mPositionController.startScroll(dx, dy, next.isEnabled(),
                    prev.isEnabled());
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onScroll(android.view.MotionEvent,android.view.MotionEvent,float,float)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onScroll(android.view.MotionEvent,android.view.MotionEvent,float,float)",this,throwable);throw throwable;}
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onSingleTapUp(android.view.MotionEvent)",this,e);try{if (mPhotoTapListener != null) {
                mPhotoTapListener.onSingleTapUp((int) e.getX(), (int) e.getY());
            }
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onSingleTapUp(android.view.MotionEvent)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onSingleTapUp(android.view.MotionEvent)",this,throwable);throw throwable;}
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onFling(android.view.MotionEvent,android.view.MotionEvent,float,float)",this,e1,e2,velocityX,velocityY);try{if (mTransitionMode != TRANS_NONE) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onFling(android.view.MotionEvent,android.view.MotionEvent,float,float)",this);return true;}}
            boolean zoomedOut = mPositionController == null || mPositionController.getCurrentScale() <= 1.0f || mPositionController.isAtMinimalScale();
            if (!zoomedOut) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onFling(android.view.MotionEvent,android.view.MotionEvent,float,float)",this);return true;}}
            if (swipeImages(velocityX)) {
                mIgnoreUpEvent = true;
            } else if (mPositionController.fling(velocityX, velocityY)) {
                mIgnoreUpEvent = true;
            }
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onFling(android.view.MotionEvent,android.view.MotionEvent,float,float)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onFling(android.view.MotionEvent,android.view.MotionEvent,float,float)",this,throwable);throw throwable;}
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onDoubleTap(android.view.MotionEvent)",this,e);try{if (mTransitionMode != TRANS_NONE) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onDoubleTap(android.view.MotionEvent)",this);return true;}}
            PositionController controller = mPositionController;
            float scale = controller.getCurrentScale();
            /*// onDoubleTap happened on the second ACTION_DOWN.*/
            /*// We need to ignore the next UP event.*/
            mIgnoreUpEvent = true;
            if (scale <= 1.0f || controller.isAtMinimalScale()) {
                float newScale = controller.getScaleMax();
                controller.zoomIn(e.getX(), e.getY(), newScale);
            } else {
                controller.resetToFullView();
            }
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onDoubleTap(android.view.MotionEvent)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView$MyGestureListener.onDoubleTap(android.view.MotionEvent)",this,throwable);throw throwable;}
        }

        @Override
        public void onLongPress(MotionEvent e) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView$MyGestureListener.onLongPress(android.view.MotionEvent)",this,e);try{if (mTransitionMode != TRANS_NONE) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView$MyGestureListener.onLongPress(android.view.MotionEvent)",this);return;}}
            PositionController controller = mPositionController;
            float scale = controller.getCurrentScale();
            /*// onDoubleTap happened on the second ACTION_DOWN.*/
            /*// We need to ignore the next UP event.*/
            mIgnoreUpEvent = true;
            if (scale <= 1.0f || controller.isAtMinimalScale()) {
            /*//if (scale <= 1.0f) {*/
                /*// Convert the tap position to image coordinate*/
                float newScale = controller.getScaleMax();
                controller.zoomIn(e.getX(), e.getY(), newScale);
            } else {
                controller.resetToFullView();
            }
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView$MyGestureListener.onLongPress(android.view.MotionEvent)",this);return;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView$MyGestureListener.onLongPress(android.view.MotionEvent)",this,throwable);throw throwable;}
        }
    }

    private class MyScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView$MyScaleListener.onScale(android.view.ScaleGestureDetector)",this,detector);try{float s = mPositionController.getCurrentScale();
            float f = detector.getScaleFactor();
            float t = s * f;
            if (DEBUG) {Log.v(TAG, "onScale() s=" + mPositionController.getCurrentScale() + " f=" + f + " newScale=" + t);}
            if (Float.isNaN(t) || Float.isInfinite(t)
                    || mTransitionMode != TRANS_NONE) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyScaleListener.onScale(android.view.ScaleGestureDetector)",this);return true;}}
            mPositionController.scaleBy(t,
                    detector.getFocusX(), detector.getFocusY());
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyScaleListener.onScale(android.view.ScaleGestureDetector)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView$MyScaleListener.onScale(android.view.ScaleGestureDetector)",this,throwable);throw throwable;}
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView$MyScaleListener.onScaleBegin(android.view.ScaleGestureDetector)",this,detector);try{if (mTransitionMode != TRANS_NONE) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyScaleListener.onScaleBegin(android.view.ScaleGestureDetector)",this);return false;}}
            if (DEBUG) {Log.v(TAG, "onScaleBegin() s=" + mPositionController.getCurrentScale());}
            mPositionController.beginScale(
                detector.getFocusX(), detector.getFocusY());
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$MyScaleListener.onScaleBegin(android.view.ScaleGestureDetector)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView$MyScaleListener.onScaleBegin(android.view.ScaleGestureDetector)",this,throwable);throw throwable;}
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView$MyScaleListener.onScaleEnd(android.view.ScaleGestureDetector)",this,detector);try{if (DEBUG) {Log.v(TAG, "onScaleEnd() s=" + mPositionController.getCurrentScale());}
            mPositionController.endScale();
            snapToNeighborImage();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView$MyScaleListener.onScaleEnd(android.view.ScaleGestureDetector)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView$MyScaleListener.onScaleEnd(android.view.ScaleGestureDetector)",this,throwable);throw throwable;}
        }
    }

    public boolean jumpTo(int index) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView.jumpTo(int)",this,index);try{if (mTransitionMode != TRANS_NONE) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.jumpTo(int)",this);return false;}}
        mModel.jumpTo(index);
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.jumpTo(int)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView.jumpTo(int)",this,throwable);throw throwable;}
    }

    public void notifyOnNewImage() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.notifyOnNewImage()",this);try{mPositionController.setImageSize(0, 0);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.notifyOnNewImage()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.notifyOnNewImage()",this,throwable);throw throwable;}
    }

    public void startSlideInAnimation(int direction) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.startSlideInAnimation(int)",this,direction);try{PositionController a = mPositionController;
        a.stopAnimation();
        switch (direction) {
            case TRANS_SLIDE_IN_LEFT:
            case TRANS_SLIDE_IN_RIGHT: {
                mTransitionMode = direction;
                a.startSlideInAnimation(direction);
                break;
            }
            default: throw new IllegalArgumentException(String.valueOf(direction));
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.startSlideInAnimation(int)",this,throwable);throw throwable;}
    }

    private class MyDownUpListener implements DownUpDetector.DownUpListener {
        {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView$MyDownUpListener.onDown(android.view.MotionEvent)",this,e);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView$MyDownUpListener.onDown(android.view.MotionEvent)",this);}

        public void onUp(MotionEvent e) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView$MyDownUpListener.onUp(android.view.MotionEvent)",this,e);try{mEdgeView.onRelease();

            if (mIgnoreUpEvent) {
                mIgnoreUpEvent = false;
                {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView$MyDownUpListener.onUp(android.view.MotionEvent)",this);return;}
            }
            if (!snapToNeighborImage() && mTransitionMode == TRANS_NONE) {
                mPositionController.up();
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView$MyDownUpListener.onUp(android.view.MotionEvent)",this,throwable);throw throwable;}
        }
    }

    private void switchToNextImage() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.switchToNextImage()",this);try{/*// We update the texture here directly to prevent texture uploading.*/
        ScreenNailEntry prevNail = mScreenNails[ENTRY_PREVIOUS];
        ScreenNailEntry nextNail = mScreenNails[ENTRY_NEXT];
        mTileView.invalidateTiles();
        if (prevNail.mTexture != null) {prevNail.mTexture.recycle();}
        prevNail.mTexture = mTileView.mBackupImage;
        mTileView.mBackupImage = nextNail.mTexture;
        nextNail.mTexture = null;
        mModel.next();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.switchToNextImage()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.switchToNextImage()",this,throwable);throw throwable;}
    }

    private void switchToPreviousImage() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.switchToPreviousImage()",this);try{/*// We update the texture here directly to prevent texture uploading.*/
        ScreenNailEntry prevNail = mScreenNails[ENTRY_PREVIOUS];
        ScreenNailEntry nextNail = mScreenNails[ENTRY_NEXT];
        mTileView.invalidateTiles();
        if (nextNail.mTexture != null) {nextNail.mTexture.recycle();}
        nextNail.mTexture = mTileView.mBackupImage;
        mTileView.mBackupImage = prevNail.mTexture;
        nextNail.mTexture = null;
        mModel.previous();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.switchToPreviousImage()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.switchToPreviousImage()",this,throwable);throw throwable;}
    }

    public void notifyTransitionComplete() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.notifyTransitionComplete()",this);try{mHandler.sendEmptyMessage(MSG_TRANSITION_COMPLETE);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.notifyTransitionComplete()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.notifyTransitionComplete()",this,throwable);throw throwable;}
    }

    private void onTransitionComplete() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.onTransitionComplete()",this);try{int mode = mTransitionMode;
        mTransitionMode = TRANS_NONE;

        if (mModel == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.onTransitionComplete()",this);return;}}
        if (mode == TRANS_SWITCH_NEXT) {
            switchToNextImage();
        } else if (mode == TRANS_SWITCH_PREVIOUS) {
            switchToPreviousImage();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.onTransitionComplete()",this,throwable);throw throwable;}
    }

    public boolean isDown() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView.isDown()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.isDown()",this);return mDownUpDetector.isDown();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView.isDown()",this,throwable);throw throwable;}
    }

    public static interface Model extends TileImageView.Model {
        public void next();
        public void previous();
        public void jumpTo(int index);
        public int getImageRotation();

        /*// Return null if the specified image is unavailable.*/
        public ImageData getNextImage();
        public ImageData getPreviousImage();
    }

    public static class ImageData {
        public int rotation;
        public Bitmap bitmap;

        public ImageData(Bitmap bitmap, int rotation) {
            this.bitmap = bitmap;
            this.rotation = rotation;
        }
    }

    private static int getRotated(int degree, int original, int theother) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.ui.PhotoView.getRotated(int,int,int)",degree,original,theother);try{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.PhotoView.getRotated(int,int,int)");return ((degree / 90) & 1) == 0 ? original : theother;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.ui.PhotoView.getRotated(int,int,int)",throwable);throw throwable;}
    }

    private class ScreenNailEntry {
        private boolean mVisible;
        private boolean mEnabled;

        private int mRotation;
        private int mDrawWidth;
        private int mDrawHeight;
        private int mOffsetX;

        private BitmapTexture mTexture;

        public void set(boolean enabled, Bitmap bitmap, int rotation) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.set(boolean,android.graphics.Bitmap,int)",this,enabled,bitmap,rotation);try{mEnabled = enabled;
            mRotation = rotation;
            if (bitmap == null) {
                if (mTexture != null) {mTexture.recycle();}
                mTexture = null;
            } else {
                if (mTexture != null) {
                    if (mTexture.getBitmap() != bitmap) {
                        mTexture.recycle();
                        mTexture = new BitmapTexture(bitmap);
                    }
                } else {
                    mTexture = new BitmapTexture(bitmap);
                }
                updateDrawingSize();
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.set(boolean,android.graphics.Bitmap,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.set(boolean,android.graphics.Bitmap,int)",this,throwable);throw throwable;}
        }

        public void layoutRightEdgeAt(int x) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.layoutRightEdgeAt(int)",this,x);try{mVisible = x > 0;
            mOffsetX = x - getRotated(
                    mRotation, mDrawWidth, mDrawHeight) / 2;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.layoutRightEdgeAt(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.layoutRightEdgeAt(int)",this,throwable);throw throwable;}
        }

        public void layoutLeftEdgeAt(int x) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.layoutLeftEdgeAt(int)",this,x);try{mVisible = x < getWidth();
            mOffsetX = x + getRotated(
                    mRotation, mDrawWidth, mDrawHeight) / 2;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.layoutLeftEdgeAt(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.layoutLeftEdgeAt(int)",this,throwable);throw throwable;}
        }

        public int gapToSide() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.PhotoView$ScreenNailEntry.gapToSide()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.PhotoView$ScreenNailEntry.gapToSide()",this);return ((mRotation / 90) & 1) != 0
                    ? PhotoView.gapToSide(mDrawHeight, getWidth())
                    : PhotoView.gapToSide(mDrawWidth, getWidth());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.PhotoView$ScreenNailEntry.gapToSide()",this,throwable);throw throwable;}
        }

        public void updateDrawingSize() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.updateDrawingSize()",this);try{if (mTexture == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.updateDrawingSize()",this);return;}}

            int width = mTexture.getWidth();
            int height = mTexture.getHeight();

            /*// Calculate the initial scale that will used by PositionController*/
            /*// (usually fit-to-screen)*/
            float s = ((mRotation / 90) & 0x01) == 0
                    ? mPositionController.getMinimalScale(width, height)
                    : mPositionController.getMinimalScale(height, width);

            mDrawWidth = Math.round(width * s);
            mDrawHeight = Math.round(height * s);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.updateDrawingSize()",this,throwable);throw throwable;}
        }

        public boolean isEnabled() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView$ScreenNailEntry.isEnabled()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView$ScreenNailEntry.isEnabled()",this);return mEnabled;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView$ScreenNailEntry.isEnabled()",this,throwable);throw throwable;}
        }

        public void draw(GLCanvas canvas) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.draw(GLCanvas)",this,canvas);try{int x = mOffsetX;
            int y = getHeight() / 2;

            if (mTexture != null) {
                if (mRotation != 0) {
                    canvas.save(GLCanvas.SAVE_FLAG_MATRIX);
                    canvas.translate(x, y, 0);
                    canvas.rotate(mRotation, 0, 0, 1); /*//mRotation*/
                    canvas.translate(-x, -y, 0);
                }
                mTexture.draw(canvas, x - mDrawWidth / 2, y - mDrawHeight / 2,
                        mDrawWidth, mDrawHeight);
                if (mRotation != 0) {
                    canvas.restore();
                }
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.draw(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView$ScreenNailEntry.draw(GLCanvas)",this,throwable);throw throwable;}
        }
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.pause()",this);try{mPositionController.skipAnimation();
        mTransitionMode = TRANS_NONE;
        mTileView.freeTextures();
        for (ScreenNailEntry entry : mScreenNails) {
            entry.set(false, null, 0);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.pause()",this,throwable);throw throwable;}
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.resume()",this);try{mTileView.prepareTextures();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.resume()",this,throwable);throw throwable;}
    }

    public void setOpenedItem(Path itemPath) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.setOpenedItem(com.android.gallery3d.data.Path)",this,itemPath);try{mOpenedItemPath = itemPath;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.setOpenedItem(com.android.gallery3d.data.Path)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.setOpenedItem(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public void showVideoPlayIcon(boolean show) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.showVideoPlayIcon(boolean)",this,show);try{mShowVideoPlayIcon = show;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.showVideoPlayIcon(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.showVideoPlayIcon(boolean)",this,throwable);throw throwable;}
    }

    /*// Returns the position saved by the previous page.*/
    public Position retrieveSavedPosition() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.ui.PhotoView.retrieveSavedPosition()",this);try{if (mOpenedItemPath != null) {
            Position position = PositionRepository
                    .getInstance(mActivity).get(Long.valueOf(
                    System.identityHashCode(mOpenedItemPath)));
            mOpenedItemPath = null;
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.ui.PhotoView.retrieveSavedPosition()",this);return position;}
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.ui.PhotoView.retrieveSavedPosition()",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.ui.PhotoView.retrieveSavedPosition()",this,throwable);throw throwable;}
    }

    public void openAnimationStarted() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PhotoView.openAnimationStarted()",this);try{mTransitionMode = TRANS_OPEN_ANIMATION;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PhotoView.openAnimationStarted()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PhotoView.openAnimationStarted()",this,throwable);throw throwable;}
    }

    public boolean isInTransition() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PhotoView.isInTransition()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PhotoView.isInTransition()",this);return mTransitionMode != TRANS_NONE;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PhotoView.isInTransition()",this,throwable);throw throwable;}
    }
}
