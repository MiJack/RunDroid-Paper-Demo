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
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.ui.PositionRepository.Position;

import java.util.Random;

public class AlbumSetView extends SlotView {
    @SuppressWarnings("unused")
    private static final String TAG = "AlbumSetView";
    private static final int CACHE_SIZE = 64;
    private static final float PHOTO_DISTANCE = 35f;

    private int mVisibleStart;
    private int mVisibleEnd;

    private final Random mRandom = new Random();
    private final long mSeed = mRandom.nextLong();

    private AlbumSetSlidingWindow mDataWindow;
    private final GalleryActivity mActivity;
    private final LabelSpec mLabelSpec;

    private SelectionDrawer mSelectionDrawer;

    public static interface Model {
        public MediaItem[] getCoverItems(int index);
        public MediaSet getMediaSet(int index);
        public int size();
        public void setActiveWindow(int start, int end);
        public void setModelListener(ModelListener listener);
    }

    public static interface ModelListener {
        public void onWindowContentChanged(int index);
        public void onSizeChanged(int size);
    }

    public static class AlbumSetItem {
        public DisplayItem[] covers;
        public DisplayItem labelItem;
        public long setDataVersion;
    }

    public static class LabelSpec {
        public int labelBackgroundHeight;
        public int titleOffset;
        public int countOffset;
        public int titleFontSize;
        public int countFontSize;
        public int leftMargin;
        public int iconSize;
    }

    public AlbumSetView(GalleryActivity activity, SelectionDrawer drawer,
            SlotView.Spec slotViewSpec, LabelSpec labelSpec) {
        super(activity.getAndroidContext());
        mActivity = activity;
        setSelectionDrawer(drawer);
        setSlotSpec(slotViewSpec);
        mLabelSpec = labelSpec;
    }

    public void setSelectionDrawer(SelectionDrawer drawer) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this,drawer);try{mSelectionDrawer = drawer;
        if (mDataWindow != null) {
            mDataWindow.setSelectionDrawer(drawer);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView.setSelectionDrawer(com.android.gallery3d.ui.SelectionDrawer)",this,throwable);throw throwable;}
    }

    public void setModel(AlbumSetView.Model model) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView.setModel(AlbumSetView.Model)",this,model);try{if (mDataWindow != null) {
            mDataWindow.setListener(null);
            setSlotCount(0);
            mDataWindow = null;
        }
        if (model != null) {
            mDataWindow = new AlbumSetSlidingWindow(mActivity, mLabelSpec,
                    mSelectionDrawer, model, CACHE_SIZE);
            mDataWindow.setListener(new MyCacheListener());
            setSlotCount(mDataWindow.size());
            updateVisibleRange(getVisibleStart(), getVisibleEnd());
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView.setModel(AlbumSetView.Model)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView.setModel(AlbumSetView.Model)",this,throwable);throw throwable;}
    }

    private void putSlotContent(int slotIndex, AlbumSetItem entry) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView.putSlotContent(int,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem)",this,slotIndex,entry);try{/*// Get displayItems from mItemsetMap or create them from MediaSet.*/
        Utils.assertTrue(entry != null);
        Rect rect = getSlotRect(slotIndex);

        DisplayItem[] items = entry.covers;
        mRandom.setSeed(slotIndex ^ mSeed);

        int x = (rect.left + rect.right) / 2;
        int y = (rect.top + rect.bottom) / 2;

        Position basePosition = new Position(x, y, 0);

        /*// Put the cover items in reverse order, so that the first item is on*/
        /*// top of the rest.*/
        Position position = new Position(x, y, 0f);
        putDisplayItem(position, position, entry.labelItem);

        for (int i = 0, n = items.length; i < n; ++i) {
            DisplayItem item = items[i];
            float dx = 0;
            float dy = 0;
            float dz = 0f;
            float theta = 0;
            if (i != 0) {
                dz = i * PHOTO_DISTANCE;
            }
            position = new Position(x + dx, y + dy, dz);
            position.theta = theta;
            putDisplayItem(position, basePosition, item);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView.putSlotContent(int,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView.putSlotContent(int,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem)",this,throwable);throw throwable;}

    }

    private void freeSlotContent(int index, AlbumSetItem entry) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView.freeSlotContent(int,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem)",this,index,entry);try{if (entry == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView.freeSlotContent(int,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem)",this);return;}}
        for (DisplayItem item : entry.covers) {
            removeDisplayItem(item);
        }
        removeDisplayItem(entry.labelItem);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView.freeSlotContent(int,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem)",this,throwable);throw throwable;}
    }

    public int size() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.AlbumSetView.size()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.AlbumSetView.size()",this);return mDataWindow.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.AlbumSetView.size()",this,throwable);throw throwable;}
    }

    @Override
    public void onLayoutChanged(int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView.onLayoutChanged(int,int)",this,width,height);try{updateVisibleRange(0, 0);
        updateVisibleRange(getVisibleStart(), getVisibleEnd());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView.onLayoutChanged(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView.onLayoutChanged(int,int)",this,throwable);throw throwable;}
    }

    @Override
    public void onScrollPositionChanged(int position) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView.onScrollPositionChanged(int)",this,position);try{super.onScrollPositionChanged(position);
        updateVisibleRange(getVisibleStart(), getVisibleEnd());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView.onScrollPositionChanged(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView.onScrollPositionChanged(int)",this,throwable);throw throwable;}
    }

    private void updateVisibleRange(int start, int end) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView.updateVisibleRange(int,int)",this,start,end);try{if (start == mVisibleStart && end == mVisibleEnd) {
            /*// we need to set the mDataWindow active range in any case.*/
            mDataWindow.setActiveWindow(start, end);
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView.updateVisibleRange(int,int)",this);return;}
        }
        if (start >= mVisibleEnd || mVisibleStart >= end) {
            for (int i = mVisibleStart, n = mVisibleEnd; i < n; ++i) {
                freeSlotContent(i, mDataWindow.get(i));
            }
            mDataWindow.setActiveWindow(start, end);
            for (int i = start; i < end; ++i) {
                putSlotContent(i, mDataWindow.get(i));
            }
        } else {
            for (int i = mVisibleStart; i < start; ++i) {
                freeSlotContent(i, mDataWindow.get(i));
            }
            for (int i = end, n = mVisibleEnd; i < n; ++i) {
                freeSlotContent(i, mDataWindow.get(i));
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
        mVisibleEnd = end;

        invalidate();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView.updateVisibleRange(int,int)",this,throwable);throw throwable;}
    }

    @Override
    protected void render(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView.render(GLCanvas)",this,canvas);try{mSelectionDrawer.prepareDrawing();
        super.render(canvas);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView.render(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView.render(GLCanvas)",this,throwable);throw throwable;}
    }

    private class MyCacheListener implements AlbumSetSlidingWindow.Listener {

        public void onSizeChanged(int size) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView$MyCacheListener.onSizeChanged(int)",this,size);try{if (setSlotCount(size)) {
                /*// If the layout parameters are changed, we need reput all items.*/
                /*// We keep the visible range at the same center but with size 0.*/
                /*// So that we can:*/
                /*//     1.) flush all visible items*/
                /*//     2.) keep the cached data*/
                int center = (getVisibleStart() + getVisibleEnd()) / 2;
                updateVisibleRange(center, center);
            }
            updateVisibleRange(getVisibleStart(), getVisibleEnd());
            invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView$MyCacheListener.onSizeChanged(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView$MyCacheListener.onSizeChanged(int)",this,throwable);throw throwable;}
        }

        public void onWindowContentChanged(int slot, AlbumSetItem old, AlbumSetItem update) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView$MyCacheListener.onWindowContentChanged(int,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem)",this,slot,old,update);try{freeSlotContent(slot, old);
            putSlotContent(slot, update);
            invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView$MyCacheListener.onWindowContentChanged(int,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView$MyCacheListener.onWindowContentChanged(int,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem,com.android.gallery3d.ui.AlbumSetView$AlbumSetItem)",this,throwable);throw throwable;}
        }

        public void onContentInvalidated() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView$MyCacheListener.onContentInvalidated()",this);try{invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView$MyCacheListener.onContentInvalidated()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView$MyCacheListener.onContentInvalidated()",this,throwable);throw throwable;}
        }
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView.pause()",this);try{for (int i = mVisibleStart, n = mVisibleEnd; i < n; ++i) {
            freeSlotContent(i, mDataWindow.get(i));
        }
        mDataWindow.pause();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView.pause()",this,throwable);throw throwable;}
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AlbumSetView.resume()",this);try{mDataWindow.resume();
        for (int i = mVisibleStart, n = mVisibleEnd; i < n; ++i) {
            putSlotContent(i, mDataWindow.get(i));
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AlbumSetView.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AlbumSetView.resume()",this,throwable);throw throwable;}
    }
}
