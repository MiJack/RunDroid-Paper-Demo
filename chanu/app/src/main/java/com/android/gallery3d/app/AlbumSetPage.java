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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.ActionModeHandler;
import com.android.gallery3d.ui.ActionModeHandler.ActionModeListener;
import com.android.gallery3d.ui.AlbumSetView;
import com.android.gallery3d.ui.DetailsHelper;
import com.android.gallery3d.ui.DetailsHelper.CloseListener;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.GLView;
import com.android.gallery3d.ui.GridDrawer;
import com.android.gallery3d.ui.HighlightDrawer;
import com.android.gallery3d.ui.PositionProvider;
import com.android.gallery3d.ui.PositionRepository;
import com.android.gallery3d.ui.PositionRepository.Position;
import com.android.gallery3d.ui.SelectionManager;
import com.android.gallery3d.ui.SlotView;
import com.android.gallery3d.ui.StaticBackground;
import com.android.gallery3d.util.Future;

public class AlbumSetPage extends ActivityState implements
        SelectionManager.SelectionListener, GalleryActionBar.ClusterRunner,
        EyePosition.EyePositionListener, MediaSet.SyncListener {
    @SuppressWarnings("unused")
    private static final String TAG = "AlbumSetPage";

    public static final String KEY_MEDIA_PATH = "media-path";
    public static final String KEY_SET_TITLE = "set-title";
    public static final String KEY_SET_SUBTITLE = "set-subtitle";
    public static final String KEY_SELECTED_CLUSTER_TYPE = "selected-cluster";

    private static final int DATA_CACHE_SIZE = 256;
    private static final int REQUEST_DO_ANIMATION = 1;
    private static final int MSG_GOTO_MANAGE_CACHE_PAGE = 1;

    private boolean mIsActive = false;
    private StaticBackground mStaticBackground;
    private AlbumSetView mAlbumSetView;

    private MediaSet mMediaSet;
    private String mSubtitle;
    private boolean mShowClusterMenu;
    private int mSelectedAction;

    protected SelectionManager mSelectionManager;
    private AlbumSetDataAdapter mAlbumSetDataAdapter;
    private GridDrawer mGridDrawer;
    private HighlightDrawer mHighlightDrawer;

    private boolean mGetContent;
    private boolean mGetAlbum;
    private ActionMode mActionMode;
    private ActionModeHandler mActionModeHandler;
    private DetailsHelper mDetailsHelper;
    private MyDetailsSource mDetailsSource;
    private boolean mShowDetails;
    private EyePosition mEyePosition;

    /*// The eyes' position of the user, the origin is at the center of the*/
    /*// device and the unit is in pixels.*/
    private float mX;
    private float mY;
    private float mZ;

    private Future<Integer> mSyncTask = null;

    private final GLView mRootPane = new GLView() {
        private final float mMatrix[] = new float[16];

        @Override
        protected void onLayout(
                boolean changed, int left, int top, int right, int bottom) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage$1.onLayout(boolean,int,int,int,int)",this,changed,left,top,right,bottom);try{mStaticBackground.layout(0, 0, right - left, bottom - top);
            mEyePosition.resetPosition();

            int slotViewTop = GalleryActionBar.getHeight((Activity) mActivity);
            int slotViewBottom = bottom - top;
            int slotViewRight = right - left;

            if (mShowDetails) {
                mDetailsHelper.layout(left, slotViewTop, right, bottom);
            } else {
                mAlbumSetView.setSelectionDrawer(mGridDrawer);
            }

            mAlbumSetView.layout(0, slotViewTop, slotViewRight, slotViewBottom);
            PositionRepository.getInstance(mActivity).setOffset(
                    0, slotViewTop);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage$1.onLayout(boolean,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage$1.onLayout(boolean,int,int,int,int)",this,throwable);throw throwable;}
        }

        @Override
        protected void render(GLCanvas canvas) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage$1.render(com.android.gallery3d.ui.GLCanvas)",this,canvas);try{canvas.save(GLCanvas.SAVE_FLAG_MATRIX);
            GalleryUtils.setViewPointMatrix(mMatrix,
                    getWidth() / 2 + mX, getHeight() / 2 + mY, mZ);
            canvas.multiplyMatrix(mMatrix, 0);
            super.render(canvas);
            canvas.restore();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage$1.render(com.android.gallery3d.ui.GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage$1.render(com.android.gallery3d.ui.GLCanvas)",this,throwable);throw throwable;}
        }
    };

    @Override
    public void onEyePositionChanged(float x, float y, float z) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onEyePositionChanged(float,float,float)",this,x,y,z);try{mRootPane.lockRendering();
        mX = x;
        mY = y;
        mZ = z;
        mRootPane.unlockRendering();
        mRootPane.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onEyePositionChanged(float,float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onEyePositionChanged(float,float,float)",this,throwable);throw throwable;}
    }

    @Override
    public void onBackPressed() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onBackPressed()",this);try{if (mShowDetails) {
            hideDetails();
        } else if (mSelectionManager.inSelectionMode()) {
            mSelectionManager.leaveSelectionMode();
        } else {
            mAlbumSetView.savePositions(
                    PositionRepository.getInstance(mActivity));
            super.onBackPressed();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onBackPressed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onBackPressed()",this,throwable);throw throwable;}
    }

    private void savePositions(int slotIndex, int center[]) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.savePositions(int,int)",this,slotIndex,center[]);try{Rect offset = new Rect();
        mRootPane.getBoundsOf(mAlbumSetView, offset);
        mAlbumSetView.savePositions(PositionRepository.getInstance(mActivity));
        Rect r = mAlbumSetView.getSlotRect(slotIndex);
        int scrollX = mAlbumSetView.getScrollX();
        int scrollY = mAlbumSetView.getScrollY();
        center[0] = offset.left + (r.left + r.right) / 2 - scrollX;
        center[1] = offset.top + (r.top + r.bottom) / 2 - scrollY;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.savePositions(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.savePositions(int,int)",this,throwable);throw throwable;}
    }

    public void onSingleTapUp(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onSingleTapUp(int)",this,slotIndex);try{MediaSet targetSet = mAlbumSetDataAdapter.getMediaSet(slotIndex);
        if (targetSet == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onSingleTapUp(int)",this);return;}} /*// Content is dirty, we shall reload soon*/

        if (mShowDetails) {
            Path path = targetSet.getPath();
            mHighlightDrawer.setHighlightItem(path);
            mDetailsHelper.reloadDetails(slotIndex);
        } else if (!mSelectionManager.inSelectionMode()) {
            Bundle data = new Bundle(getData());
            String mediaPath = targetSet.getPath().toString();
            int[] center = new int[2];
            savePositions(slotIndex, center);
            data.putIntArray(AlbumPage.KEY_SET_CENTER, center);
            if (mGetAlbum && targetSet.isLeafAlbum()) {
                Activity activity = (Activity) mActivity;
                Intent result = new Intent()
                        .putExtra(AlbumPicker.KEY_ALBUM_PATH, targetSet.getPath().toString());
                activity.setResult(Activity.RESULT_OK, result);
                activity.finish();
            } else if (targetSet.getSubMediaSetCount() > 0) {
                data.putString(AlbumSetPage.KEY_MEDIA_PATH, mediaPath);
                mActivity.getStateManager().startStateForResult(
                        AlbumSetPage.class, REQUEST_DO_ANIMATION, data);
            } else {
                if (!mGetContent && (targetSet.getSupportedOperations()
                        & MediaObject.SUPPORT_IMPORT) != 0) {
                    data.putBoolean(AlbumPage.KEY_AUTO_SELECT_ALL, true);
                }
                data.putString(AlbumPage.KEY_MEDIA_PATH, mediaPath);
                boolean inAlbum = mActivity.getStateManager().hasStateClass(AlbumPage.class);
                /*// We only show cluster menu in the first AlbumPage in stack*/
                data.putBoolean(AlbumPage.KEY_SHOW_CLUSTER_MENU, !inAlbum);
                mActivity.getStateManager().startStateForResult(
                        AlbumPage.class, REQUEST_DO_ANIMATION, data);
            }
        } else {
            mSelectionManager.toggle(targetSet.getPath());
            mAlbumSetView.invalidate();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onSingleTapUp(int)",this,throwable);throw throwable;}
    }

    private void onDown(int index) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onDown(int)",this,index);try{MediaSet set = mAlbumSetDataAdapter.getMediaSet(index);
        Path path = (set == null) ? null : set.getPath();
        mSelectionManager.setPressedPath(path);
        mAlbumSetView.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onDown(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onDown(int)",this,throwable);throw throwable;}
    }

    private void onUp() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onUp()",this);try{mSelectionManager.setPressedPath(null);
        mAlbumSetView.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onUp()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onUp()",this,throwable);throw throwable;}
    }

    public void onLongTap(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onLongTap(int)",this,slotIndex);try{if (mGetContent || mGetAlbum) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onLongTap(int)",this);return;}}
        if (mShowDetails) {
            onSingleTapUp(slotIndex);
        } else {
            MediaSet set = mAlbumSetDataAdapter.getMediaSet(slotIndex);
            if (set == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onLongTap(int)",this);return;}}
            mSelectionManager.setAutoLeaveSelectionMode(true);
            mSelectionManager.toggle(set.getPath());
            mDetailsSource.findIndex(slotIndex);
            mAlbumSetView.invalidate();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onLongTap(int)",this,throwable);throw throwable;}
    }

    public void doCluster(int clusterType) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.doCluster(int)",this,clusterType);try{String basePath = mMediaSet.getPath().toString();
        String newPath = FilterUtils.switchClusterPath(basePath, clusterType);
        Bundle data = new Bundle(getData());
        data.putString(AlbumSetPage.KEY_MEDIA_PATH, newPath);
        data.putInt(KEY_SELECTED_CLUSTER_TYPE, clusterType);
        mAlbumSetView.savePositions(PositionRepository.getInstance(mActivity));
        mActivity.getStateManager().switchState(this, AlbumSetPage.class, data);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.doCluster(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.doCluster(int)",this,throwable);throw throwable;}
    }

    public void doFilter(int filterType) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.doFilter(int)",this,filterType);try{String basePath = mMediaSet.getPath().toString();
        String newPath = FilterUtils.switchFilterPath(basePath, filterType);
        Bundle data = new Bundle(getData());
        data.putString(AlbumSetPage.KEY_MEDIA_PATH, newPath);
        mAlbumSetView.savePositions(PositionRepository.getInstance(mActivity));
        mActivity.getStateManager().switchState(this, AlbumSetPage.class, data);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.doFilter(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.doFilter(int)",this,throwable);throw throwable;}
    }

    public void onOperationComplete() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onOperationComplete()",this);try{mAlbumSetView.invalidate();
        /*// TODO: enable animation*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onOperationComplete()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onOperationComplete()",this,throwable);throw throwable;}
    }

    @Override
    public void onCreate(Bundle data, Bundle restoreState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onCreate(android.os.Bundle,android.os.Bundle)",this,data,restoreState);try{initializeViews();
        initializeData(data);
        Context context = mActivity.getAndroidContext();
        mGetContent = data.getBoolean(Gallery.KEY_GET_CONTENT, false);
        mGetAlbum = data.getBoolean(Gallery.KEY_GET_ALBUM, false);
        mSubtitle = data.getString(AlbumSetPage.KEY_SET_SUBTITLE);
        mEyePosition = new EyePosition(context, this);
        mDetailsSource = new MyDetailsSource();
        mSelectedAction = FilterUtils.CLUSTER_BY_ALBUM;
        startTransition();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onCreate(android.os.Bundle,android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onCreate(android.os.Bundle,android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onPause()",this);try{super.onPause();
        mIsActive = false;
        mActionModeHandler.pause();
        mAlbumSetDataAdapter.pause();
        mAlbumSetView.pause();
        mEyePosition.pause();
        DetailsHelper.pause();
        if (mSyncTask != null) {
            mSyncTask.cancel();
            mSyncTask = null;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onPause()",this,throwable);throw throwable;}
    }

    @Override
    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onResume()",this);try{super.onResume();
        mIsActive = true;
        setContentPane(mRootPane);
        mAlbumSetDataAdapter.resume();
        mAlbumSetView.resume();
        mEyePosition.resume();
        mActionModeHandler.resume();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onResume()",this,throwable);throw throwable;}
    }

    private void initializeData(Bundle data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.initializeData(android.os.Bundle)",this,data);try{String mediaPath = data.getString(AlbumSetPage.KEY_MEDIA_PATH);
        mMediaSet = mActivity.getDataManager().getMediaSet(mediaPath);
        mSelectionManager.setSourceMediaSet(mMediaSet);
        mAlbumSetDataAdapter = new AlbumSetDataAdapter(
                mActivity, mMediaSet, DATA_CACHE_SIZE);
        mAlbumSetDataAdapter.setLoadingListener(new MyLoadingListener());
        mAlbumSetView.setModel(mAlbumSetDataAdapter);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.initializeData(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.initializeData(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void initializeViews() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.initializeViews()",this);try{mSelectionManager = new SelectionManager(mActivity, true);
        mSelectionManager.setSelectionListener(this);
        mStaticBackground = new StaticBackground(mActivity.getAndroidContext());
        mRootPane.addComponent(mStaticBackground);

        mGridDrawer = new GridDrawer((Context) mActivity, mSelectionManager);
        Config.AlbumSetPage config = Config.AlbumSetPage.get((Context) mActivity);
        mAlbumSetView = new AlbumSetView(mActivity, mGridDrawer,
                config.slotViewSpec, config.labelSpec);
        mAlbumSetView.setListener(new SlotView.SimpleListener() {
            @Override
            public void onDown(int index) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage$2.onDown(int)",this,index);try{AlbumSetPage.this.onDown(index);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage$2.onDown(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage$2.onDown(int)",this,throwable);throw throwable;}
            }

            @Override
            public void onUp() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage$2.onUp()",this);try{AlbumSetPage.this.onUp();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage$2.onUp()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage$2.onUp()",this,throwable);throw throwable;}
            }

            @Override
            public void onSingleTapUp(int slotIndex) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage$2.onSingleTapUp(int)",this,slotIndex);try{AlbumSetPage.this.onSingleTapUp(slotIndex);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage$2.onSingleTapUp(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage$2.onSingleTapUp(int)",this,throwable);throw throwable;}
            }

            @Override
            public void onLongTap(int slotIndex) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage$2.onLongTap(int)",this,slotIndex);try{AlbumSetPage.this.onLongTap(slotIndex);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage$2.onLongTap(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage$2.onLongTap(int)",this,throwable);throw throwable;}
            }
        });

        mActionModeHandler = new ActionModeHandler(mActivity, mSelectionManager);
        mActionModeHandler.setActionModeListener(new ActionModeListener() {
            public boolean onActionItemClicked(MenuItem item) {
                com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.AlbumSetPage$3.onActionItemClicked(android.view.MenuItem)",this,item);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumSetPage$3.onActionItemClicked(android.view.MenuItem)",this);{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.initializeViews()",this);return onItemSelected(item);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.AlbumSetPage$3.onActionItemClicked(android.view.MenuItem)",this,throwable);throw throwable;}
            }
        });
        mRootPane.addComponent(mAlbumSetView);

        mStaticBackground.setImage(R.drawable.background,
                R.drawable.background_portrait);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.initializeViews()",this,throwable);throw throwable;}
    }

    @Override
    protected boolean onCreateActionBar(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.AlbumSetPage.onCreateActionBar(android.view.Menu)",this,menu);try{GalleryActionBar actionBar = mActivity.getGalleryActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        /*
        MenuInflater inflater = activity.getMenuInflater();

        final boolean inAlbum = mActivity.getStateManager().hasStateClass(
                AlbumPage.class);

        if (mGetContent) {
            inflater.inflate(R.menu.pickup, menu);
            int typeBits = mData.getInt(
                    Gallery.KEY_TYPE_BITS, DataManager.INCLUDE_IMAGE);
            int id = R.string.select_image;
            if ((typeBits & DataManager.INCLUDE_VIDEO) != 0) {
                id = (typeBits & DataManager.INCLUDE_IMAGE) == 0
                        ? R.string.select_video
                        : R.string.select_item;
            }
            actionBar.setTitle(id);
        } else  if (mGetAlbum) {
            inflater.inflate(R.menu.pickup, menu);
            actionBar.setTitle(R.string.select_album);
        } else {
            mShowClusterMenu = !inAlbum;
            inflater.inflate(R.menu.albumset, menu);
        }
        */
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumSetPage.onCreateActionBar(android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.AlbumSetPage.onCreateActionBar(android.view.Menu)",this,throwable);throw throwable;}
    }

    @Override
    protected boolean onItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.AlbumSetPage.onItemSelected(android.view.MenuItem)",this,item);try{Activity activity = (Activity) mActivity;
        if (item.getItemId() == R.id.action_cancel) {
            activity.setResult(Activity.RESULT_CANCELED);
            activity.finish();
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumSetPage.onItemSelected(android.view.MenuItem)",this);return true;}
        } else if (item.getItemId() == R.id.action_details) {
            if (mAlbumSetDataAdapter.size() != 0) {
                if (mShowDetails) {
                    hideDetails();
                } else {
                    showDetails();
                }
            } else {
                Toast.makeText(activity,
                        activity.getText(R.string.no_albums_alert),
                        Toast.LENGTH_SHORT).show();
            }
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumSetPage.onItemSelected(android.view.MenuItem)",this);return true;}
        } else {
        	{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumSetPage.onItemSelected(android.view.MenuItem)",this);return false;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.AlbumSetPage.onItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    @Override
    protected void onStateResult(int requestCode, int resultCode, Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onStateResult(int,int,android.content.Intent)",this,requestCode,resultCode,data);try{switch (requestCode) {
            case REQUEST_DO_ANIMATION: {
                startTransition();
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onStateResult(int,int,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onStateResult(int,int,android.content.Intent)",this,throwable);throw throwable;}
    }

    private void startTransition() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.startTransition()",this);try{final PositionRepository repository =
                PositionRepository.getInstance(mActivity);
        mAlbumSetView.startTransition(new PositionProvider() {
            private final Position mTempPosition = new Position();
            public Position getPosition(long identity, Position target) {
                com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.app.AlbumSetPage$4.getPosition(long,com.android.gallery3d.ui.PositionRepository.Position)",this,identity,target);try{Position p = repository.get(identity);
                if (p == null) {
                    p = mTempPosition;
                    p.set(target.x, target.y, 128, target.theta, 1);
                }
                {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.startTransition()",this);{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.app.AlbumSetPage$4.getPosition(long,com.android.gallery3d.ui.PositionRepository.Position)",this);return p;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.app.AlbumSetPage$4.getPosition(long,com.android.gallery3d.ui.PositionRepository.Position)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.startTransition()",this,throwable);throw throwable;}
    }

    private String getSelectedString() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.app.AlbumSetPage.getSelectedString()",this);try{int count = mSelectionManager.getSelectedCount();
        int string = R.plurals.number_of_albums_selected;
        String format = mActivity.getResources().getQuantityString(string, count);
        {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.app.AlbumSetPage.getSelectedString()",this);return String.format(format, count);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.app.AlbumSetPage.getSelectedString()",this,throwable);throw throwable;}
    }

    public void onSelectionModeChange(int mode) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onSelectionModeChange(int)",this,mode);try{switch (mode) {
            case SelectionManager.ENTER_SELECTION_MODE: {
                mActionMode = mActionModeHandler.startActionMode();
                break;
            }
            case SelectionManager.LEAVE_SELECTION_MODE: {
                mActionMode.finish();
                mRootPane.invalidate();
                break;
            }
            case SelectionManager.SELECT_ALL_MODE: {
                mActionModeHandler.setTitle(getSelectedString());
                mRootPane.invalidate();
                break;
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onSelectionModeChange(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onSelectionModeChange(int)",this,throwable);throw throwable;}
    }

    public void onSelectionChange(Path path, boolean selected) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onSelectionChange(com.android.gallery3d.data.Path,boolean)",this,path,selected);try{Utils.assertTrue(mActionMode != null);
        mActionModeHandler.setTitle(getSelectedString());
        mActionModeHandler.updateSupportedOperation(path, selected);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onSelectionChange(com.android.gallery3d.data.Path,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onSelectionChange(com.android.gallery3d.data.Path,boolean)",this,throwable);throw throwable;}
    }

    private void hideDetails() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.hideDetails()",this);try{mShowDetails = false;
        mDetailsHelper.hide();
        mAlbumSetView.setSelectionDrawer(mGridDrawer);
        mAlbumSetView.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.hideDetails()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.hideDetails()",this,throwable);throw throwable;}
    }

    private void showDetails() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.showDetails()",this);try{mShowDetails = true;
        if (mDetailsHelper == null) {
            mHighlightDrawer = new HighlightDrawer(mActivity.getAndroidContext(),
                    mSelectionManager);
            mDetailsHelper = new DetailsHelper(mActivity, mRootPane, mDetailsSource);
            mDetailsHelper.setCloseListener(new CloseListener() {
                public void onClose() {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage$5.onClose()",this);try{hideDetails();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage$5.onClose()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage$5.onClose()",this,throwable);throw throwable;}
                }
            });
        }
        mAlbumSetView.setSelectionDrawer(mHighlightDrawer);
        mDetailsHelper.show();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.showDetails()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.showDetails()",this,throwable);throw throwable;}
    }

    @Override
    public void onSyncDone(final MediaSet mediaSet, final int resultCode) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage.onSyncDone(com.android.gallery3d.data.MediaSet,int)",this,mediaSet,resultCode);try{if (resultCode == MediaSet.SYNC_RESULT_ERROR) {
            Log.d(TAG, "onSyncDone: " + Utils.maskDebugInfo(mediaSet.getName()) + " result="
                    + resultCode);
        }
        Handler handler = mActivity.getHandler();
        if (handler != null)
            {handler.post(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage$6.run()",this);try{if (!mIsActive) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage.onSyncDone(com.android.gallery3d.data.MediaSet,int)",this);{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage$6.run()",this);return;}}}
                mediaSet.notifyContentChanged(); /*// force reload to handle spinner*/

                if (resultCode == MediaSet.SYNC_RESULT_ERROR) {
                    Toast.makeText((Context) mActivity, R.string.sync_album_set_error,
                            Toast.LENGTH_LONG).show();
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage$6.run()",this,throwable);throw throwable;}
            }
        });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage.onSyncDone(com.android.gallery3d.data.MediaSet,int)",this,throwable);throw throwable;}
    }

    private class MyLoadingListener implements LoadingListener {
        public void onLoadingStarted() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage$MyLoadingListener.onLoadingStarted()",this);try{GalleryUtils.setSpinnerVisibility((Activity) mActivity, true);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage$MyLoadingListener.onLoadingStarted()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage$MyLoadingListener.onLoadingStarted()",this,throwable);throw throwable;}
        }

        public void onLoadingFinished() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumSetPage$MyLoadingListener.onLoadingFinished()",this);try{if (!mIsActive) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumSetPage$MyLoadingListener.onLoadingFinished()",this);return;}}

            if (mSyncTask == null) {
                /*// Request sync in case the mediaSet hasn't been sync'ed before.*/
                mSyncTask = mMediaSet.requestSync(AlbumSetPage.this);
            }
            if (mSyncTask.isDone()){
                /*// The mediaSet is in sync. Turn off the loading indicator.*/
                GalleryUtils.setSpinnerVisibility((Activity) mActivity, false);

                /*// Only show toast when there's no album and we are going to finish*/
                /*// the page. Toast is redundant if we are going to stay on this page.*/
                if ((mAlbumSetDataAdapter.size() == 0)
                        && (mActivity.getStateManager().getStateCount() > 1)) {
                    Toast.makeText((Context) mActivity,
                            R.string.empty_album, Toast.LENGTH_LONG).show();
                    mActivity.getStateManager().finishState(AlbumSetPage.this);
                }
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumSetPage$MyLoadingListener.onLoadingFinished()",this,throwable);throw throwable;}
        }
    }

    private class MyDetailsSource implements DetailsHelper.DetailsSource {
        private int mIndex;
        public int size() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.size()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.size()",this);return mAlbumSetDataAdapter.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.size()",this,throwable);throw throwable;}
        }

        public int getIndex() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.getIndex()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.getIndex()",this);return mIndex;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.getIndex()",this,throwable);throw throwable;}
        }

        /*// If requested index is out of active window, suggest a valid index.*/
        /*// If there is no valid index available, return -1.*/
        public int findIndex(int indexHint) {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.findIndex(int)",this,indexHint);try{if (mAlbumSetDataAdapter.isActive(indexHint)) {
                mIndex = indexHint;
            } else {
                mIndex = mAlbumSetDataAdapter.getActiveStart();
                if (!mAlbumSetDataAdapter.isActive(mIndex)) {
                    {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.findIndex(int)",this);return -1;}
                }
            }
            {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.findIndex(int)",this);return mIndex;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.findIndex(int)",this,throwable);throw throwable;}
        }

        public MediaDetails getDetails() {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaDetails com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.getDetails()",this);try{MediaObject item = mAlbumSetDataAdapter.getMediaSet(mIndex);
            if (item != null) {
                mHighlightDrawer.setHighlightItem(item.getPath());
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.getDetails()",this);return item.getDetails();}
            } else {
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.getDetails()",this);return null;}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaDetails com.android.gallery3d.app.AlbumSetPage$MyDetailsSource.getDetails()",this,throwable);throw throwable;}
        }
    }
}
