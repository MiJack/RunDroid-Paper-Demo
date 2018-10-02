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

import com.android.gallery3d.app.GalleryContext;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SelectionManager {
    @SuppressWarnings("unused")
    private static final String TAG = "SelectionManager";

    public static final int ENTER_SELECTION_MODE = 1;
    public static final int LEAVE_SELECTION_MODE = 2;
    public static final int SELECT_ALL_MODE = 3;

    private Set<Path> mClickedSet;
    private MediaSet mSourceMediaSet;
    private SelectionListener mListener;
    private DataManager mDataManager;
    private boolean mInverseSelection;
    private boolean mIsAlbumSet;
    private boolean mInSelectionMode;
    private boolean mAutoLeave = true;
    private int mTotal;
    private Path mPressedPath;

    public interface SelectionListener {
        public void onSelectionModeChange(int mode);
        public void onSelectionChange(Path path, boolean selected);
    }

    public SelectionManager(GalleryContext galleryContext, boolean isAlbumSet) {
        mDataManager = galleryContext.getDataManager();
        mClickedSet = new HashSet<Path>();
        mIsAlbumSet = isAlbumSet;
        mTotal = -1;
    }

    /*// Whether we will leave selection mode automatically once the number of*/
    /*// selected items is down to zero.*/
    public void setAutoLeaveSelectionMode(boolean enable) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SelectionManager.setAutoLeaveSelectionMode(boolean)",this,enable);try{mAutoLeave = enable;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SelectionManager.setAutoLeaveSelectionMode(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SelectionManager.setAutoLeaveSelectionMode(boolean)",this,throwable);throw throwable;}
    }

    public void setSelectionListener(SelectionListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SelectionManager.setSelectionListener(SelectionListener)",this,listener);try{mListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SelectionManager.setSelectionListener(SelectionListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SelectionManager.setSelectionListener(SelectionListener)",this,throwable);throw throwable;}
    }

    public void selectAll() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SelectionManager.selectAll()",this);try{mInverseSelection = true;
        mClickedSet.clear();
        enterSelectionMode();
        if (mListener != null) {mListener.onSelectionModeChange(SELECT_ALL_MODE);}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SelectionManager.selectAll()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SelectionManager.selectAll()",this,throwable);throw throwable;}
    }

    public void deSelectAll() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SelectionManager.deSelectAll()",this);try{leaveSelectionMode();
        mInverseSelection = false;
        mClickedSet.clear();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SelectionManager.deSelectAll()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SelectionManager.deSelectAll()",this,throwable);throw throwable;}
    }

    public boolean inSelectAllMode() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.SelectionManager.inSelectAllMode()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.SelectionManager.inSelectAllMode()",this);return mInverseSelection;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.SelectionManager.inSelectAllMode()",this,throwable);throw throwable;}
    }

    public boolean inSelectionMode() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.SelectionManager.inSelectionMode()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.SelectionManager.inSelectionMode()",this);return mInSelectionMode;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.SelectionManager.inSelectionMode()",this,throwable);throw throwable;}
    }

    public void enterSelectionMode() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SelectionManager.enterSelectionMode()",this);try{if (mInSelectionMode) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SelectionManager.enterSelectionMode()",this);return;}}

        mInSelectionMode = true;
        if (mListener != null) {mListener.onSelectionModeChange(ENTER_SELECTION_MODE);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SelectionManager.enterSelectionMode()",this,throwable);throw throwable;}
    }

    public void leaveSelectionMode() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SelectionManager.leaveSelectionMode()",this);try{if (!mInSelectionMode) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SelectionManager.leaveSelectionMode()",this);return;}}

        mInSelectionMode = false;
        mInverseSelection = false;
        mClickedSet.clear();
        if (mListener != null) {mListener.onSelectionModeChange(LEAVE_SELECTION_MODE);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SelectionManager.leaveSelectionMode()",this,throwable);throw throwable;}
    }

    public boolean isItemSelected(Path itemId) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.SelectionManager.isItemSelected(com.android.gallery3d.data.Path)",this,itemId);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.SelectionManager.isItemSelected(com.android.gallery3d.data.Path)",this);return mInverseSelection ^ mClickedSet.contains(itemId);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.SelectionManager.isItemSelected(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public int getSelectedCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.SelectionManager.getSelectedCount()",this);try{int count = mClickedSet.size();
        if (mInverseSelection) {
            if (mTotal < 0) {
                mTotal = mIsAlbumSet
                        ? mSourceMediaSet.getSubMediaSetCount()
                        : mSourceMediaSet.getMediaItemCount();
            }
            count = mTotal - count;
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.SelectionManager.getSelectedCount()",this);return count;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.SelectionManager.getSelectedCount()",this,throwable);throw throwable;}
    }

    public void toggle(Path path) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SelectionManager.toggle(com.android.gallery3d.data.Path)",this,path);try{if (mClickedSet.contains(path)) {
            mClickedSet.remove(path);
        } else {
            enterSelectionMode();
            mClickedSet.add(path);
        }

        if (mListener != null) {mListener.onSelectionChange(path, isItemSelected(path));}
        if (getSelectedCount() == 0 && mAutoLeave) {
            leaveSelectionMode();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SelectionManager.toggle(com.android.gallery3d.data.Path)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SelectionManager.toggle(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public void setPressedPath(Path path) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SelectionManager.setPressedPath(com.android.gallery3d.data.Path)",this,path);try{mPressedPath = path;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SelectionManager.setPressedPath(com.android.gallery3d.data.Path)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SelectionManager.setPressedPath(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public boolean isPressedPath(Path path) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.SelectionManager.isPressedPath(com.android.gallery3d.data.Path)",this,path);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.SelectionManager.isPressedPath(com.android.gallery3d.data.Path)",this);return path != null && path == mPressedPath;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.SelectionManager.isPressedPath(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    private static void expandMediaSet(ArrayList<Path> items, MediaSet set) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.ui.SelectionManager.expandMediaSet(java.util.ArrayList,com.android.gallery3d.data.MediaSet)",items,set);try{int subCount = set.getSubMediaSetCount();
        for (int i = 0; i < subCount; i++) {
            expandMediaSet(items, set.getSubMediaSet(i));
        }
        int total = set.getMediaItemCount();
        int batch = 50;
        int index = 0;

        while (index < total) {
            int count = index + batch < total
                    ? batch
                    : total - index;
            ArrayList<MediaItem> list = set.getMediaItem(index, count);
            for (MediaItem item : list) {
                items.add(item.getPath());
            }
            index += batch;
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.ui.SelectionManager.expandMediaSet(java.util.ArrayList,com.android.gallery3d.data.MediaSet)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.ui.SelectionManager.expandMediaSet(java.util.ArrayList,com.android.gallery3d.data.MediaSet)",throwable);throw throwable;}
    }

    public ArrayList<Path> getSelected(boolean expandSet) {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.ui.SelectionManager.getSelected(boolean)",this,expandSet);try{ArrayList<Path> selected = new ArrayList<Path>();
        if (mIsAlbumSet) {
            if (mInverseSelection) {
                int max = mSourceMediaSet.getSubMediaSetCount();
                for (int i = 0; i < max; i++) {
                    MediaSet set = mSourceMediaSet.getSubMediaSet(i);
                    Path id = set.getPath();
                    if (!mClickedSet.contains(id)) {
                        if (expandSet) {
                            expandMediaSet(selected, set);
                        } else {
                            selected.add(id);
                        }
                    }
                }
            } else {
                for (Path id : mClickedSet) {
                    if (expandSet) {
                        expandMediaSet(selected, mDataManager.getMediaSet(id));
                    } else {
                        selected.add(id);
                    }
                }
            }
        } else {
            if (mInverseSelection) {

                int total = mSourceMediaSet.getMediaItemCount();
                int index = 0;
                while (index < total) {
                    int count = Math.min(total - index, MediaSet.MEDIAITEM_BATCH_FETCH_COUNT);
                    ArrayList<MediaItem> list = mSourceMediaSet.getMediaItem(index, count);
                    for (MediaItem item : list) {
                        Path id = item.getPath();
                        if (!mClickedSet.contains(id)) {selected.add(id);}
                    }
                    index += count;
                }
            } else {
                for (Path id : mClickedSet) {
                    selected.add(id);
                }
            }
        }
        {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.ui.SelectionManager.getSelected(boolean)",this);return selected;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.ui.SelectionManager.getSelected(boolean)",this,throwable);throw throwable;}
    }

    public void setSourceMediaSet(MediaSet set) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SelectionManager.setSourceMediaSet(com.android.gallery3d.data.MediaSet)",this,set);try{mSourceMediaSet = set;
        mTotal = -1;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SelectionManager.setSourceMediaSet(com.android.gallery3d.data.MediaSet)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SelectionManager.setSourceMediaSet(com.android.gallery3d.data.MediaSet)",this,throwable);throw throwable;}
    }

    public MediaSet getSourceMediaSet() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaSet com.android.gallery3d.ui.SelectionManager.getSourceMediaSet()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaSet com.android.gallery3d.ui.SelectionManager.getSourceMediaSet()",this);return mSourceMediaSet;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaSet com.android.gallery3d.ui.SelectionManager.getSourceMediaSet()",this,throwable);throw throwable;}
    }
}
