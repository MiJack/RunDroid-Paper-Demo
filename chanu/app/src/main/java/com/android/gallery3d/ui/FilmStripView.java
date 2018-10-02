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

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.anim.AlphaAnimation;
import com.android.gallery3d.app.AlbumDataAdapter;
import com.android.gallery3d.app.GalleryActivity;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;

public class FilmStripView extends GLView implements ScrollBarView.Listener,
        UserInteractionListener {
    @SuppressWarnings("unused")
    private static final String TAG = "FilmStripView";

    private static final int HIDE_ANIMATION_DURATION = 300;  /*// 0.3 sec*/

    public interface Listener {
        /*// Returns false if it cannot jump to the specified index at this time.*/
        boolean onSlotSelected(int slotIndex);
    }

    private int mTopMargin, mMidMargin, mBottomMargin;
    private int mContentSize, mBarSize, mGripSize;
    private AlbumView mAlbumView;
    private ScrollBarView mScrollBarView;
    private AlbumDataAdapter mAlbumDataAdapter;
    private StripDrawer mStripDrawer;
    private Listener mListener;
    private UserInteractionListener mUIListener;
    private NinePatchTexture mBackgroundTexture;

    /*// The layout of FileStripView is*/
    /*// topMargin*/
    /*//             ----+----+*/
    /*//            /    +----+--\*/
    /*// contentSize     |    |   thumbSize*/
    /*//            \    +----+--/*/
    /*//             ----+----+*/
    /*// midMargin*/
    /*//             ----+----+*/
    /*//            /    +----+--\*/
    /*//     barSize     |    |   gripSize*/
    /*//            \    +----+--/*/
    /*//             ----+----+*/
    /*// bottomMargin*/
    public FilmStripView(GalleryActivity activity, MediaSet mediaSet,
            int topMargin, int midMargin, int bottomMargin, int contentSize,
            int thumbSize, int barSize, int gripSize, int gripWidth) {
        mTopMargin = topMargin;
        mMidMargin = midMargin;
        mBottomMargin = bottomMargin;
        mContentSize = contentSize;
        mBarSize = barSize;
        mGripSize = gripSize;

        mStripDrawer = new StripDrawer((Context) activity);
        SlotView.Spec spec = new SlotView.Spec();
        spec.slotWidth = thumbSize;
        spec.slotHeight = thumbSize;
        mAlbumView = new AlbumView(activity, spec, thumbSize);
        mAlbumView.setOverscrollEffect(SlotView.OVERSCROLL_NONE);
        mAlbumView.setSelectionDrawer(mStripDrawer);
        mAlbumView.setListener(new SlotView.SimpleListener() {
            @Override
            public void onDown(int index) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView$1.onDown(int)",this,index);try{FilmStripView.this.onDown(index);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView$1.onDown(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView$1.onDown(int)",this,throwable);throw throwable;}
            }
            @Override
            public void onUp() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView$1.onUp()",this);try{FilmStripView.this.onUp();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView$1.onUp()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView$1.onUp()",this,throwable);throw throwable;}
            }
            @Override
            public void onSingleTapUp(int slotIndex) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView$1.onSingleTapUp(int)",this,slotIndex);try{FilmStripView.this.onSingleTapUp(slotIndex);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView$1.onSingleTapUp(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView$1.onSingleTapUp(int)",this,throwable);throw throwable;}
            }
            @Override
            public void onLongTap(int slotIndex) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView$1.onLongTap(int)",this,slotIndex);try{FilmStripView.this.onLongTap(slotIndex);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView$1.onLongTap(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView$1.onLongTap(int)",this,throwable);throw throwable;}
            }
            @Override
            public void onScrollPositionChanged(int position, int total) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView$1.onScrollPositionChanged(int,int)",this,position,total);try{FilmStripView.this.onScrollPositionChanged(position, total);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView$1.onScrollPositionChanged(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView$1.onScrollPositionChanged(int,int)",this,throwable);throw throwable;}
            }
        });
        mAlbumView.setUserInteractionListener(this);
        mAlbumDataAdapter = new AlbumDataAdapter(activity, mediaSet);
        addComponent(mAlbumView);
        mScrollBarView = new ScrollBarView(activity.getAndroidContext(),
                mGripSize, gripWidth);
        mScrollBarView.setListener(this);
        addComponent(mScrollBarView);

        mAlbumView.setModel(mAlbumDataAdapter);
        mBackgroundTexture = new NinePatchTexture(activity.getAndroidContext(),
                R.drawable.navstrip_translucent);
    }

    public void setListener(Listener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.setListener(Listener)",this,listener);try{mListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.setListener(Listener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.setListener(Listener)",this,throwable);throw throwable;}
    }

    public void setUserInteractionListener(UserInteractionListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.setUserInteractionListener(UserInteractionListener)",this,listener);try{mUIListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.setUserInteractionListener(UserInteractionListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.setUserInteractionListener(UserInteractionListener)",this,throwable);throw throwable;}
    }

    public void show() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.show()",this);try{if (getVisibility() == GLView.VISIBLE) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.show()",this);return;}}
        startAnimation(null);
        setVisibility(GLView.VISIBLE);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.show()",this,throwable);throw throwable;}
    }

    public void hide() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.hide()",this);try{if (getVisibility() == GLView.INVISIBLE) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.hide()",this);return;}}
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(HIDE_ANIMATION_DURATION);
        try {
            startAnimation(animation);
        }
        catch (IllegalStateException e) {
            Log.i(TAG, "Exception running hide animation for film strip, setting invisible", e);
        }
        setVisibility(GLView.INVISIBLE);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.hide()",this,throwable);throw throwable;}
    }

    @Override
    protected void onVisibilityChanged(int visibility) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onVisibilityChanged(int)",this,visibility);try{super.onVisibilityChanged(visibility);
        if (visibility == GLView.VISIBLE) {
            onUserInteraction();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onVisibilityChanged(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onVisibilityChanged(int)",this,throwable);throw throwable;}
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onMeasure(int,int)",this,widthSpec,heightSpec);try{int height = mTopMargin + mContentSize + mMidMargin + mBarSize + mBottomMargin;
        MeasureHelper.getInstance(this)
                .setPreferredContentSize(MeasureSpec.getSize(widthSpec), height)
                .measure(widthSpec, heightSpec);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onMeasure(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onMeasure(int,int)",this,throwable);throw throwable;}
    }

    @Override
    protected void onLayout(
            boolean changed, int left, int top, int right, int bottom) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onLayout(boolean,int,int,int,int)",this,changed,left,top,right,bottom);try{if (!changed) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onLayout(boolean,int,int,int,int)",this);return;}}
        mAlbumView.layout(0, mTopMargin, right - left, mTopMargin + mContentSize);
        int barStart = mTopMargin + mContentSize + mMidMargin;
        mScrollBarView.layout(0, barStart, right - left, barStart + mBarSize);
        int width = right - left;
        int height = bottom - top;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onLayout(boolean,int,int,int,int)",this,throwable);throw throwable;}
    }

    @Override
    protected boolean onTouch(MotionEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.FilmStripView.onTouch(android.view.MotionEvent)",this,event);try{/*// consume all touch events on the "gray area", so they don't go to*/
        /*// the photo view below. (otherwise you can scroll the picture through*/
        /*// it).*/
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.FilmStripView.onTouch(android.view.MotionEvent)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.FilmStripView.onTouch(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    @Override
    protected boolean dispatchTouchEvent(MotionEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.FilmStripView.dispatchTouchEvent(android.view.MotionEvent)",this,event);try{switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                onUserInteractionBegin();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onUserInteractionEnd();
                break;
        }

        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.FilmStripView.dispatchTouchEvent(android.view.MotionEvent)",this);return super.dispatchTouchEvent(event);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.FilmStripView.dispatchTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    @Override
    protected void render(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.render(GLCanvas)",this,canvas);try{mBackgroundTexture.draw(canvas, 0, 0, getWidth(), getHeight());
        super.render(canvas);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.render(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.render(GLCanvas)",this,throwable);throw throwable;}
    }

    private void onDown(int index) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onDown(int)",this,index);try{MediaItem item = mAlbumDataAdapter.get(index);
        Path path = (item == null) ? null : item.getPath();
        mStripDrawer.setPressedPath(path);
        mAlbumView.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onDown(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onDown(int)",this,throwable);throw throwable;}
    }

    private void onUp() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onUp()",this);try{mStripDrawer.setPressedPath(null);
        mAlbumView.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onUp()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onUp()",this,throwable);throw throwable;}
    }

    private void onSingleTapUp(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onSingleTapUp(int)",this,slotIndex);try{if (mListener.onSlotSelected(slotIndex)) {
            mAlbumView.setFocusIndex(slotIndex);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onSingleTapUp(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onSingleTapUp(int)",this,throwable);throw throwable;}
    }

    private void onLongTap(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onLongTap(int)",this,slotIndex);try{onSingleTapUp(slotIndex);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onLongTap(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onLongTap(int)",this,throwable);throw throwable;}
    }

    private void onScrollPositionChanged(int position, int total) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onScrollPositionChanged(int,int)",this,position,total);try{mScrollBarView.setContentPosition(position, total);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onScrollPositionChanged(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onScrollPositionChanged(int,int)",this,throwable);throw throwable;}
    }

    /*// Called by AlbumView*/
    @Override
    public void onUserInteractionBegin() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onUserInteractionBegin()",this);try{mUIListener.onUserInteractionBegin();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onUserInteractionBegin()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onUserInteractionBegin()",this,throwable);throw throwable;}
    }

    /*// Called by AlbumView*/
    @Override
    public void onUserInteractionEnd() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onUserInteractionEnd()",this);try{mUIListener.onUserInteractionEnd();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onUserInteractionEnd()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onUserInteractionEnd()",this,throwable);throw throwable;}
    }

    /*// Called by AlbumView*/
    @Override
    public void onUserInteraction() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onUserInteraction()",this);try{mUIListener.onUserInteraction();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onUserInteraction()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onUserInteraction()",this,throwable);throw throwable;}
    }

    /*// Called by ScrollBarView*/
    @Override
    public void onScrollBarPositionChanged(int position) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.onScrollBarPositionChanged(int)",this,position);try{mAlbumView.setScrollPosition(position);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.onScrollBarPositionChanged(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.onScrollBarPositionChanged(int)",this,throwable);throw throwable;}
    }

    public void setFocusIndex(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.setFocusIndex(int)",this,slotIndex);try{mAlbumView.setFocusIndex(slotIndex);
        mAlbumView.makeSlotVisible(slotIndex);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.setFocusIndex(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.setFocusIndex(int)",this,throwable);throw throwable;}
    }

    public void setStartIndex(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.setStartIndex(int)",this,slotIndex);try{mAlbumView.setStartIndex(slotIndex);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.setStartIndex(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.setStartIndex(int)",this,throwable);throw throwable;}
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.pause()",this);try{mAlbumView.pause();
        mAlbumDataAdapter.pause();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.pause()",this,throwable);throw throwable;}
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FilmStripView.resume()",this);try{mAlbumView.resume();
        mAlbumDataAdapter.resume();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FilmStripView.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FilmStripView.resume()",this,throwable);throw throwable;}
    }
}
