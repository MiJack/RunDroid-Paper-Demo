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

import android.graphics.Rect;

import com.android.gallery3d.app.GalleryActivity;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.ui.PositionRepository.Position;

public class AlbumView extends SlotView {
    @SuppressWarnings("unused")
    private static final String TAG = "AlbumView";
    private static final int CACHE_SIZE = 128;

    private int mVisibleStart = 0;
    private int mVisibleEnd = 0;

    private AlbumSlidingWindow mDataWindow;
    private final GalleryActivity mActivity;
    private SelectionDrawer mSelectionDrawer;
    private int mCacheThumbSize;

    private boolean mIsActive = false;

    public static interface Model {
        public int size();
        public MediaItem get(int index);
        public void setActiveWindow(int start, int end);
        public void setModelListener(ModelListener listener);
    }

    public static interface ModelListener {
        public void onWindowContentChanged(int index);
        public void onSizeChanged(int size);
    }

    public AlbumView(GalleryActivity activity, SlotView.Spec spec,
            int cacheThumbSize) {
        super(activity.getAndroidContext());
        mCacheThumbSize = cacheThumbSize;
        setSlotSpec(spec);
        mActivity = activity;
    }

    public void setSelectionDrawer(SelectionDrawer drawer) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this,drawer);try{mSelectionDrawer = drawer;
        if (mDataWindow != null) {mDataWindow.setSelectionDrawer(drawer);}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this,throwable);throw throwable;}
    }

    public void setModel(Model model) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView.setModel(Model)",this,model);try{if (mDataWindow != null) {
            mDataWindow.setListener(null);
            setSlotCount(0);
            mDataWindow = null;
        }
        if (model != null) {
            mDataWindow = new AlbumSlidingWindow(
                    mActivity, model, CACHE_SIZE,
                    mCacheThumbSize);
            mDataWindow.setSelectionDrawer(mSelectionDrawer);
            mDataWindow.setListener(new MyDataModelListener());
            setSlotCount(model.size());
            updateVisibleRange(getVisibleStart(), getVisibleEnd());
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView.setModel(Model)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView.setModel(Model)",this,throwable);throw throwable;}
    }

    public void setFocusIndex(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView.setFocusIndex(int)",this,slotIndex);try{if (mDataWindow != null) {
            mDataWindow.setFocusIndex(slotIndex);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView.setFocusIndex(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView.setFocusIndex(int)",this,throwable);throw throwable;}
    }

    private void putSlotContent(int slotIndex, DisplayItem item) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView.putSlotContent(int,com.android.gallery3d.ui.DisplayItem)",this,slotIndex,item);try{Rect rect = getSlotRect(slotIndex);
        Position position = new Position(
                (rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2, 0);
        putDisplayItem(position, position, item);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView.putSlotContent(int,com.android.gallery3d.ui.DisplayItem)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView.putSlotContent(int,com.android.gallery3d.ui.DisplayItem)",this,throwable);throw throwable;}
    }

    private void updateVisibleRange(int start, int end) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView.updateVisibleRange(int,int)",this,start,end);try{if (start == mVisibleStart && end == mVisibleEnd) {
            /*// we need to set the mDataWindow active range in any case.*/
            mDataWindow.setActiveWindow(start, end);
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView.updateVisibleRange(int,int)",this);return;}
        }

        if (!mIsActive) {
            mVisibleStart = start;
            mVisibleEnd = end;
            mDataWindow.setActiveWindow(start, end);
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView.updateVisibleRange(int,int)",this);return;}
        }

        if (start >= mVisibleEnd || mVisibleStart >= end) {
            for (int i = mVisibleStart, n = mVisibleEnd; i < n; ++i) {
                DisplayItem item = mDataWindow.get(i);
                if (item != null) {removeDisplayItem(item);}
            }
            mDataWindow.setActiveWindow(start, end);
            for (int i = start; i < end; ++i) {
                putSlotContent(i, mDataWindow.get(i));
            }
        } else {
            for (int i = mVisibleStart; i < start; ++i) {
                DisplayItem item = mDataWindow.get(i);
                if (item != null) {removeDisplayItem(item);}
            }
            for (int i = end, n = mVisibleEnd; i < n; ++i) {
                DisplayItem item = mDataWindow.get(i);
                if (item != null) {removeDisplayItem(item);}
            }
            mDataWindow.setActiveWindow(start, end);
            for (int i = start, n = mVisibleStart; i < n; ++i) {
                putSlotContent(i, mDataWindow.get(i));
            }
            for (int i = mVisibleEnd; i < end; ++i) {
                putSlotContent(i, mDataWindow.get(i));
            }
        }

        mVisibleStart = start;
        mVisibleEnd = end;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView.updateVisibleRange(int,int)",this,throwable);throw throwable;}
    }

    @Override
    protected void onLayoutChanged(int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView.onLayoutChanged(int,int)",this,width,height);try{/*// Reput all the items*/
        updateVisibleRange(0, 0);
        updateVisibleRange(getVisibleStart(), getVisibleEnd());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView.onLayoutChanged(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView.onLayoutChanged(int,int)",this,throwable);throw throwable;}
    }

    @Override
    protected void onScrollPositionChanged(int position) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView.onScrollPositionChanged(int)",this,position);try{super.onScrollPositionChanged(position);
        updateVisibleRange(getVisibleStart(), getVisibleEnd());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView.onScrollPositionChanged(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView.onScrollPositionChanged(int)",this,throwable);throw throwable;}
    }

    @Override
    protected void render(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView.render(GLCanvas)",this,canvas);try{mSelectionDrawer.prepareDrawing();
        super.render(canvas);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView.render(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView.render(GLCanvas)",this,throwable);throw throwable;}
    }

    private class MyDataModelListener implements AlbumSlidingWindow.Listener {

        public void onContentInvalidated() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView$MyDataModelListener.onContentInvalidated()",this);try{invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView$MyDataModelListener.onContentInvalidated()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView$MyDataModelListener.onContentInvalidated()",this,throwable);throw throwable;}
        }

        public void onSizeChanged(int size) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView$MyDataModelListener.onSizeChanged(int)",this,size);try{if (setSlotCount(size)) {
                /*// If the layout parameters are changed, we need reput all items.*/
                /*// We keep the visible range at the same center but with size 0.*/
                /*// So that we can:*/
                /*//     1.) flush all visible items*/
                /*//     2.) keep the cached data*/
                int center = (getVisibleStart() + getVisibleEnd()) / 2;
                updateVisibleRange(center, center);
            }
            updateVisibleRange(getVisibleStart(), getVisibleEnd());
            invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView$MyDataModelListener.onSizeChanged(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView$MyDataModelListener.onSizeChanged(int)",this,throwable);throw throwable;}
        }

        public void onWindowContentChanged(
                int slotIndex, DisplayItem old, DisplayItem update) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView$MyDataModelListener.onWindowContentChanged(int,com.android.gallery3d.ui.DisplayItem,com.android.gallery3d.ui.DisplayItem)",this,slotIndex,old,update);try{removeDisplayItem(old);
            putSlotContent(slotIndex, update);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView$MyDataModelListener.onWindowContentChanged(int,com.android.gallery3d.ui.DisplayItem,com.android.gallery3d.ui.DisplayItem)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView$MyDataModelListener.onWindowContentChanged(int,com.android.gallery3d.ui.DisplayItem,com.android.gallery3d.ui.DisplayItem)",this,throwable);throw throwable;}
        }
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView.resume()",this);try{mIsActive = true;
        mDataWindow.resume();
        for (int i = mVisibleStart, n = mVisibleEnd; i < n; ++i) {
            putSlotContent(i, mDataWindow.get(i));
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView.resume()",this,throwable);throw throwable;}
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumView.pause()",this);try{mIsActive = false;
        for (int i = mVisibleStart, n = mVisibleEnd; i < n; ++i) {
            removeDisplayItem(mDataWindow.get(i));
        }
        mDataWindow.pause();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumView.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumView.pause()",this,throwable);throw throwable;}
    }
}
