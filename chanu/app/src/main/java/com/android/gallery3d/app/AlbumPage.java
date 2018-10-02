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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.chanapps.four.gallery3d.R;
import com.chanapps.four.service.ThreadImageDownloadService;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.MtpDevice;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.ActionModeHandler;
import com.android.gallery3d.ui.ActionModeHandler.ActionModeListener;
import com.android.gallery3d.ui.AlbumView;
import com.android.gallery3d.ui.DetailsHelper;
import com.android.gallery3d.ui.DetailsHelper.CloseListener;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.GLView;
import com.android.gallery3d.ui.GridDrawer;
import com.android.gallery3d.ui.HighlightDrawer;
import com.android.gallery3d.ui.Log;
import com.android.gallery3d.ui.PositionProvider;
import com.android.gallery3d.ui.PositionRepository;
import com.android.gallery3d.ui.PositionRepository.Position;
import com.android.gallery3d.ui.SelectionManager;
import com.android.gallery3d.ui.SlotView;
import com.android.gallery3d.ui.StaticBackground;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.GalleryUtils;

import java.util.ArrayList;
import java.util.Random;

public class AlbumPage extends ActivityState implements GalleryActionBar.ClusterRunner,
        SelectionManager.SelectionListener, MediaSet.SyncListener {
    @SuppressWarnings("unused")
    private static final String TAG = "AlbumPage";

    public static final String KEY_MEDIA_PATH = "media-path";
    public static final String KEY_SET_CENTER = "set-center";
    public static final String KEY_AUTO_SELECT_ALL = "auto-select-all";
    public static final String KEY_SHOW_CLUSTER_MENU = "cluster-menu";

    private static final int REQUEST_SLIDESHOW = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_DO_ANIMATION = 3;

    private static final float USER_DISTANCE_METER = 0.3f;

    private boolean mIsActive = false;
    private StaticBackground mStaticBackground;
    private AlbumView mAlbumView;
    private Path mMediaSetPath;

    private AlbumDataAdapter mAlbumDataAdapter;

    protected SelectionManager mSelectionManager;
    private GridDrawer mGridDrawer;
    private HighlightDrawer mHighlightDrawer;

    private boolean mGetContent;
    private boolean mShowClusterMenu;

    private ActionMode mActionMode;
    private ActionModeHandler mActionModeHandler;
    private int mFocusIndex = 0;
    private DetailsHelper mDetailsHelper;
    private MyDetailsSource mDetailsSource;
    private MediaSet mMediaSet;
    private boolean mShowDetails;
    private float mUserDistance; /*// in pixel*/

    private ProgressDialog mProgressDialog;
    private Future<?> mPendingTask;

    private Future<Integer> mSyncTask = null;

    private GLView mRootPane = new GLView() {
        private float mMatrix[] = new float[16];

        @Override
        protected void onLayout(
                boolean changed, int left, int top, int right, int bottom) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage$1.onLayout(boolean,int,int,int,int)",this,changed,left,top,right,bottom);try{mStaticBackground.layout(0, 0, right - left, bottom - top);

            int slotViewTop = GalleryActionBar.getHeight((Activity) mActivity);
            int slotViewBottom = bottom - top;
            int slotViewRight = right - left;

            if (mShowDetails) {
                mDetailsHelper.layout(left, slotViewTop, right, bottom);
            } else {
                mAlbumView.setSelectionDrawer(mGridDrawer);
            }

            mAlbumView.layout(0, slotViewTop, slotViewRight, slotViewBottom);
            GalleryUtils.setViewPointMatrix(mMatrix,
                    (right - left) / 2, (bottom - top) / 2, -mUserDistance);
            PositionRepository.getInstance(mActivity).setOffset(
                    0, slotViewTop);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage$1.onLayout(boolean,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage$1.onLayout(boolean,int,int,int,int)",this,throwable);throw throwable;}
        }

        @Override
        protected void render(GLCanvas canvas) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage$1.render(com.android.gallery3d.ui.GLCanvas)",this,canvas);try{canvas.save(GLCanvas.SAVE_FLAG_MATRIX);
            canvas.multiplyMatrix(mMatrix, 0);
            super.render(canvas);
            canvas.restore();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage$1.render(com.android.gallery3d.ui.GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage$1.render(com.android.gallery3d.ui.GLCanvas)",this,throwable);throw throwable;}
        }
    };

    @Override
    protected void onBackPressed() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onBackPressed()",this);try{if (mShowDetails) {
            hideDetails();
        } else if (mSelectionManager.inSelectionMode()) {
            mSelectionManager.leaveSelectionMode();
        } else {
            mAlbumView.savePositions(PositionRepository.getInstance(mActivity));
            super.onBackPressed();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onBackPressed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onBackPressed()",this,throwable);throw throwable;}
    }

    private void onDown(int index) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onDown(int)",this,index);try{MediaItem item = mAlbumDataAdapter.get(index);
        Path path = (item == null) ? null : item.getPath();
        mSelectionManager.setPressedPath(path);
        mAlbumView.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onDown(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onDown(int)",this,throwable);throw throwable;}
    }

    private void onUp() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onUp()",this);try{mSelectionManager.setPressedPath(null);
        mAlbumView.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onUp()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onUp()",this,throwable);throw throwable;}
    }

    public void onSingleTapUp(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onSingleTapUp(int)",this,slotIndex);try{MediaItem item = mAlbumDataAdapter.get(slotIndex);
        if (item == null) {
            Log.w(TAG, "item not ready yet, ignore the click");
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onSingleTapUp(int)",this);return;}
        }
        if (mShowDetails) {
            mHighlightDrawer.setHighlightItem(item.getPath());
            mDetailsHelper.reloadDetails(slotIndex);
        } else if (!mSelectionManager.inSelectionMode()) {
            if (mGetContent) {
                onGetContent(item);
            } else {
                boolean playVideo =
                    (item.getSupportedOperations() & MediaItem.SUPPORT_PLAY) != 0;
                if (playVideo) {
                    /*// Play the video.*/
                    PhotoPage.playVideo((Activity) mActivity, item.getPlayUri(), item.getPath(), item.getMimeType());
                } else {
                    /*// Get into the PhotoPage.*/
                    Bundle data = new Bundle();
                    mAlbumView.savePositions(PositionRepository.getInstance(mActivity));
                    data.putInt(PhotoPage.KEY_INDEX_HINT, slotIndex);
                    data.putString(PhotoPage.KEY_MEDIA_SET_PATH,
                            mMediaSetPath.toString());
                    data.putString(PhotoPage.KEY_MEDIA_ITEM_PATH,
                            item.getPath().toString());
                    mActivity.getStateManager().startStateForResult(
                            PhotoPage.class, REQUEST_PHOTO, data);
                }
            }
        } else {
            mSelectionManager.toggle(item.getPath());
            mAlbumView.invalidate();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onSingleTapUp(int)",this,throwable);throw throwable;}
    }

    private void onGetContent(final MediaItem item) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onGetContent(com.android.gallery3d.data.MediaItem)",this,item);try{DataManager dm = mActivity.getDataManager();
        Activity activity = (Activity) mActivity;
        if (mData.getString(Gallery.EXTRA_CROP) != null) {
            /*// TODO: Handle MtpImagew*/
            Uri uri = dm.getContentUri(item.getPath());
            Intent intent = new Intent(CropImage.ACTION_CROP, uri)
                    .addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                    .putExtras(getData());
            if (mData.getParcelable(MediaStore.EXTRA_OUTPUT) == null) {
                intent.putExtra(CropImage.KEY_RETURN_DATA, true);
            }
            activity.startActivity(intent);
            activity.finish();
        } else {
            activity.setResult(Activity.RESULT_OK,
                    new Intent(null, item.getContentUri()));
            activity.finish();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onGetContent(com.android.gallery3d.data.MediaItem)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onGetContent(com.android.gallery3d.data.MediaItem)",this,throwable);throw throwable;}
    }

    public void onLongTap(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onLongTap(int)",this,slotIndex);try{if (mGetContent) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onLongTap(int)",this);return;}}
        if (mShowDetails) {
            onSingleTapUp(slotIndex);
        } else {
            MediaItem item = mAlbumDataAdapter.get(slotIndex);
            if (item == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onLongTap(int)",this);return;}}
            mSelectionManager.setAutoLeaveSelectionMode(true);
            mSelectionManager.toggle(item.getPath());
            mDetailsSource.findIndex(slotIndex);
            mAlbumView.invalidate();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onLongTap(int)",this,throwable);throw throwable;}
    }

    public void doCluster(int clusterType) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.doCluster(int)",this,clusterType);try{String basePath = mMediaSet.getPath().toString();
        String newPath = FilterUtils.newClusterPath(basePath, clusterType);
        Bundle data = new Bundle(getData());
        data.putString(AlbumSetPage.KEY_MEDIA_PATH, newPath);
        if (mShowClusterMenu) {
            Context context = mActivity.getAndroidContext();
            data.putString(AlbumSetPage.KEY_SET_TITLE, mMediaSet.getName());
            data.putString(AlbumSetPage.KEY_SET_SUBTITLE,
                    GalleryActionBar.getClusterByTypeString(context, clusterType));
        }

        mAlbumView.savePositions(PositionRepository.getInstance(mActivity));
        mActivity.getStateManager().startStateForResult(
                AlbumSetPage.class, REQUEST_DO_ANIMATION, data);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.doCluster(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.doCluster(int)",this,throwable);throw throwable;}
    }

    public void doFilter(int filterType) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.doFilter(int)",this,filterType);try{String basePath = mMediaSet.getPath().toString();
        String newPath = FilterUtils.switchFilterPath(basePath, filterType);
        Bundle data = new Bundle(getData());
        data.putString(AlbumPage.KEY_MEDIA_PATH, newPath);
        mAlbumView.savePositions(PositionRepository.getInstance(mActivity));
        mActivity.getStateManager().switchState(this, AlbumPage.class, data);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.doFilter(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.doFilter(int)",this,throwable);throw throwable;}
    }

    public void onOperationComplete() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onOperationComplete()",this);try{mAlbumView.invalidate();
        /*// TODO: enable animation*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onOperationComplete()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onOperationComplete()",this,throwable);throw throwable;}
    }

    @Override
    protected void onCreate(Bundle data, Bundle restoreState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onCreate(android.os.Bundle,android.os.Bundle)",this,data,restoreState);try{mUserDistance = GalleryUtils.meterToPixel(USER_DISTANCE_METER);
        initializeViews();
        initializeData(data);
        mGetContent = data.getBoolean(Gallery.KEY_GET_CONTENT, false);
        mShowClusterMenu = data.getBoolean(KEY_SHOW_CLUSTER_MENU, false);
        mDetailsSource = new MyDetailsSource();
        Context context = mActivity.getAndroidContext();
        startTransition(data);

        /*// Enable auto-select-all for mtp album*/
        if (data.getBoolean(KEY_AUTO_SELECT_ALL)) {
            mSelectionManager.selectAll();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onCreate(android.os.Bundle,android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onCreate(android.os.Bundle,android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void startTransition() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.startTransition()",this);try{final PositionRepository repository =
                PositionRepository.getInstance(mActivity);
        mAlbumView.startTransition(new PositionProvider() {
            private Position mTempPosition = new Position();
            public Position getPosition(long identity, Position target) {
                com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.app.AlbumPage$2.getPosition(long,com.android.gallery3d.ui.PositionRepository.Position)",this,identity,target);try{Position p = repository.get(identity);
                if (p != null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.startTransition()",this);{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.app.AlbumPage$2.getPosition(long,com.android.gallery3d.ui.PositionRepository.Position)",this);return p;}}}
                mTempPosition.set(target);
                mTempPosition.z = 128;
                {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.startTransition()",this);{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.app.AlbumPage$2.getPosition(long,com.android.gallery3d.ui.PositionRepository.Position)",this);return mTempPosition;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.app.AlbumPage$2.getPosition(long,com.android.gallery3d.ui.PositionRepository.Position)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.startTransition()",this,throwable);throw throwable;}
    }

    private void startTransition(Bundle data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.startTransition(android.os.Bundle)",this,data);try{final PositionRepository repository =
                PositionRepository.getInstance(mActivity);
        final int[] center = data == null
                ? null
                : data.getIntArray(KEY_SET_CENTER);
        final Random random = new Random();
        mAlbumView.startTransition(new PositionProvider() {
            private Position mTempPosition = new Position();
            public Position getPosition(long identity, Position target) {
                com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.app.AlbumPage$3.getPosition(long,com.android.gallery3d.ui.PositionRepository.Position)",this,identity,target);try{Position p = repository.get(identity);
                if (p != null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.startTransition(android.os.Bundle)",this);{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.app.AlbumPage$3.getPosition(long,com.android.gallery3d.ui.PositionRepository.Position)",this);return p;}}}
                if (center != null) {
                    random.setSeed(identity);
                    mTempPosition.set(center[0], center[1],
                            0, random.nextInt(60) - 30, 0);
                } else {
                    mTempPosition.set(target);
                    mTempPosition.z = 128;
                }
                {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.startTransition(android.os.Bundle)",this);{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.app.AlbumPage$3.getPosition(long,com.android.gallery3d.ui.PositionRepository.Position)",this);return mTempPosition;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PositionRepository.Position com.android.gallery3d.app.AlbumPage$3.getPosition(long,com.android.gallery3d.ui.PositionRepository.Position)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.startTransition(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onResume()",this);try{super.onResume();
        mIsActive = true;
        setContentPane(mRootPane);
        mAlbumDataAdapter.resume();
        mAlbumView.resume();
        mActionModeHandler.resume();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onResume()",this,throwable);throw throwable;}
    }

    @Override
    protected void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onPause()",this);try{super.onPause();
        mIsActive = false;
        mAlbumDataAdapter.pause();
        mAlbumView.pause();
        DetailsHelper.pause();
        Future<?> task = mPendingTask;
        if (task != null) {
            /*// cancel on going task*/
            task.cancel();
            task.waitDone();
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
        if (mSyncTask != null) {
            mSyncTask.cancel();
            mSyncTask = null;
        }
        mActionModeHandler.pause();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onPause()",this,throwable);throw throwable;}
    }

    @Override
    protected void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onDestroy()",this);try{super.onDestroy();
        if (mAlbumDataAdapter != null) {
            mAlbumDataAdapter.setLoadingListener(null);
        }
        
        GalleryUtils.removeActivity(mActivity);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onDestroy()",this,throwable);throw throwable;}
    }

    private void initializeViews() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.initializeViews()",this);try{mStaticBackground = new StaticBackground((Context) mActivity);
        mRootPane.addComponent(mStaticBackground);

        mSelectionManager = new SelectionManager(mActivity, false);
        mSelectionManager.setSelectionListener(this);
        mGridDrawer = new GridDrawer((Context) mActivity, mSelectionManager);
        Config.AlbumPage config = Config.AlbumPage.get((Context) mActivity);
        mAlbumView = new AlbumView(mActivity, config.slotViewSpec,
                0 /* don't cache thumbnail */);
        mAlbumView.setSelectionDrawer(mGridDrawer);
        mRootPane.addComponent(mAlbumView);
        mAlbumView.setListener(new SlotView.SimpleListener() {
            @Override
            public void onDown(int index) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage$4.onDown(int)",this,index);try{AlbumPage.this.onDown(index);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage$4.onDown(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage$4.onDown(int)",this,throwable);throw throwable;}
            }

            @Override
            public void onUp() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage$4.onUp()",this);try{AlbumPage.this.onUp();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage$4.onUp()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage$4.onUp()",this,throwable);throw throwable;}
            }

            @Override
            public void onSingleTapUp(int slotIndex) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage$4.onSingleTapUp(int)",this,slotIndex);try{AlbumPage.this.onSingleTapUp(slotIndex);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage$4.onSingleTapUp(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage$4.onSingleTapUp(int)",this,throwable);throw throwable;}
            }

            @Override
            public void onLongTap(int slotIndex) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage$4.onLongTap(int)",this,slotIndex);try{AlbumPage.this.onLongTap(slotIndex);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage$4.onLongTap(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage$4.onLongTap(int)",this,throwable);throw throwable;}
            }
        });
        mActionModeHandler = new ActionModeHandler(mActivity, mSelectionManager);
        mActionModeHandler.setActionModeListener(new ActionModeListener() {
            public boolean onActionItemClicked(MenuItem item) {
                com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.AlbumPage$5.onActionItemClicked(android.view.MenuItem)",this,item);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumPage$5.onActionItemClicked(android.view.MenuItem)",this);{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.initializeViews()",this);return onItemSelected(item);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.AlbumPage$5.onActionItemClicked(android.view.MenuItem)",this,throwable);throw throwable;}
            }
        });
        mStaticBackground.setImage(R.drawable.background,
                R.drawable.background_portrait);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.initializeViews()",this,throwable);throw throwable;}
    }

    private void initializeData(Bundle data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.initializeData(android.os.Bundle)",this,data);try{mMediaSetPath = Path.fromString(data.getString(KEY_MEDIA_PATH));
        mMediaSet = mActivity.getDataManager().getMediaSet(mMediaSetPath);
        Utils.assertTrue(mMediaSet != null,
                "MediaSet is null. Path = %s", mMediaSetPath);
        mSelectionManager.setSourceMediaSet(mMediaSet);
        mAlbumDataAdapter = new AlbumDataAdapter(mActivity, mMediaSet);
        mAlbumDataAdapter.setLoadingListener(new MyLoadingListener());
        mAlbumView.setModel(mAlbumDataAdapter);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.initializeData(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.initializeData(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void showDetails() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.showDetails()",this);try{mShowDetails = true;
        if (mDetailsHelper == null) {
            mHighlightDrawer = new HighlightDrawer(mActivity.getAndroidContext(),
                    mSelectionManager);
            mDetailsHelper = new DetailsHelper(mActivity, mRootPane, mDetailsSource);
            mDetailsHelper.setCloseListener(new CloseListener() {
                public void onClose() {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage$6.onClose()",this);try{hideDetails();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage$6.onClose()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage$6.onClose()",this,throwable);throw throwable;}
                }
            });
        }
        mAlbumView.setSelectionDrawer(mHighlightDrawer);
        mDetailsHelper.show();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.showDetails()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.showDetails()",this,throwable);throw throwable;}
    }

    private void hideDetails() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.hideDetails()",this);try{mShowDetails = false;
        mDetailsHelper.hide();
        mAlbumView.setSelectionDrawer(mGridDrawer);
        mAlbumView.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.hideDetails()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.hideDetails()",this,throwable);throw throwable;}
    }

    @Override
    protected boolean onCreateActionBar(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.AlbumPage.onCreateActionBar(android.view.Menu)",this,menu);try{GalleryActionBar actionBar = mActivity.getGalleryActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Activity activity = (Activity) mActivity;
        Log.w(TAG, "Action bar for activity " + mActivity.getClass().getCanonicalName() + " is " + actionBar
        		+ " and it's title is '" + actionBar.getTitle() + "'");
        MenuInflater inflater = activity.getMenuInflater();

        if (mGetContent) {
        } else {
            inflater.inflate(R.menu.album, menu);
            /*//if (actionBar != null && actionBar.getTitle() != null && !actionBar.getTitle().isEmpty()) {*/
           /*// 	actionBar.setTitle(mMediaSet.getName());*/
            /*//}*/
            if (mMediaSet instanceof MtpDevice) {
                menu.findItem(R.id.action_slideshow).setVisible(false);
            } else {
                menu.findItem(R.id.action_slideshow).setVisible(true);
            }

            /*//MenuItem groupBy = menu.findItem(R.id.action_group_by);*/
            /*//FilterUtils.setupMenuItems(actionBar, mMediaSetPath, true);*/

            /*//if (groupBy != null) {*/
            /*//    groupBy.setVisible(mShowClusterMenu);*/
            /*//}*/
            
            /*//if (actionBar.getTitle() != null && !actionBar.getTitle().isEmpty()) {*/
	        /*//    actionBar.setTitle(mMediaSet.getName());*/
            /*//}*/
        }
        actionBar.setSubtitle(null);
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumPage.onCreateActionBar(android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.AlbumPage.onCreateActionBar(android.view.Menu)",this,throwable);throw throwable;}
    }

    @Override
    protected boolean onItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.AlbumPage.onItemSelected(android.view.MenuItem)",this,item);try{if (item.getItemId() == R.id.action_cancel) {
            mActivity.getStateManager().finishState(this);
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumPage.onItemSelected(android.view.MenuItem)",this);return true;}
        } else if (item.getItemId() == R.id.action_slideshow) {
            Bundle data = new Bundle();
            data.putString(SlideshowPage.KEY_SET_PATH,
                    mMediaSetPath.toString());
            data.putBoolean(SlideshowPage.KEY_REPEAT, true);
            mActivity.getStateManager().startStateForResult(
                    SlideshowPage.class, REQUEST_SLIDESHOW, data);
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumPage.onItemSelected(android.view.MenuItem)",this);return true;}
        } else if (item.getItemId() == R.id.action_details) {
            if (mShowDetails) {
                hideDetails();
            } else {
                showDetails();
            }
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumPage.onItemSelected(android.view.MenuItem)",this);return true;}
        } else if (item.getItemId() == R.id.action_download) {
        	ArrayList<Path> ids = mSelectionManager.getSelected(true);
        	ThreadImageDownloadService.startDownloadViaGalleryView(mActivity.getAndroidContext(), mMediaSetPath, ids);
            Toast.makeText(mActivity.getAndroidContext(),
                    R.string.download_all_images_notice,
                    Toast.LENGTH_SHORT)
                    .show();
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumPage.onItemSelected(android.view.MenuItem)",this);return true;}
        } else { 
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.AlbumPage.onItemSelected(android.view.MenuItem)",this);return false;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.AlbumPage.onItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    @Override
    protected void onStateResult(int request, int result, Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onStateResult(int,int,android.content.Intent)",this,request,result,data);try{switch (request) {
            case REQUEST_SLIDESHOW: {
                /*// data could be null, if there is no images in the album*/
                if (data == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onStateResult(int,int,android.content.Intent)",this);return;}}
                mFocusIndex = data.getIntExtra(SlideshowPage.KEY_PHOTO_INDEX, 0);
                mAlbumView.setCenterIndex(mFocusIndex);
                break;
            }
            case REQUEST_PHOTO: {
                if (data == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onStateResult(int,int,android.content.Intent)",this);return;}}
                mFocusIndex = data.getIntExtra(PhotoPage.KEY_INDEX_HINT, 0);
                mAlbumView.setCenterIndex(mFocusIndex);
                startTransition();
                break;
            }
            case REQUEST_DO_ANIMATION: {
                startTransition(null);
                break;
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onStateResult(int,int,android.content.Intent)",this,throwable);throw throwable;}
    }

    public void onSelectionModeChange(int mode) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onSelectionModeChange(int)",this,mode);try{switch (mode) {
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
                mActionModeHandler.updateSupportedOperation();
                mRootPane.invalidate();
                break;
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onSelectionModeChange(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onSelectionModeChange(int)",this,throwable);throw throwable;}
    }

    public void onSelectionChange(Path path, boolean selected) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onSelectionChange(com.android.gallery3d.data.Path,boolean)",this,path,selected);try{Utils.assertTrue(mActionMode != null);
        int count = mSelectionManager.getSelectedCount();
        String format = mActivity.getResources().getQuantityString(
                R.plurals.number_of_items_selected, count);
        mActionModeHandler.setTitle(String.format(format, count));
        mActionModeHandler.updateSupportedOperation(path, selected);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onSelectionChange(com.android.gallery3d.data.Path,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onSelectionChange(com.android.gallery3d.data.Path,boolean)",this,throwable);throw throwable;}
    }

    @Override
    public void onSyncDone(final MediaSet mediaSet, final int resultCode) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage.onSyncDone(com.android.gallery3d.data.MediaSet,int)",this,mediaSet,resultCode);try{Log.d(TAG, "onSyncDone: " + Utils.maskDebugInfo(mediaSet.getName()) + " result="
                + resultCode);
        Handler handler = mActivity.getHandler();
        if (handler != null)
            {handler.post(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage$7.run()",this);try{if (!mIsActive) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage.onSyncDone(com.android.gallery3d.data.MediaSet,int)",this);{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage$7.run()",this);return;}}}
                mediaSet.notifyContentChanged(); /*// force reload to handle spinner*/

                if (resultCode == MediaSet.SYNC_RESULT_ERROR) {
                    Toast.makeText((Context) mActivity, R.string.sync_album_error,
                            Toast.LENGTH_LONG).show();
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage$7.run()",this,throwable);throw throwable;}
            }
        });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage.onSyncDone(com.android.gallery3d.data.MediaSet,int)",this,throwable);throw throwable;}
    }

    private class MyLoadingListener implements LoadingListener {
        @Override
        public void onLoadingStarted() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage$MyLoadingListener.onLoadingStarted()",this);try{GalleryUtils.setSpinnerVisibility((Activity) mActivity, true);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage$MyLoadingListener.onLoadingStarted()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage$MyLoadingListener.onLoadingStarted()",this,throwable);throw throwable;}
        }

        @Override
        public void onLoadingFinished() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPage$MyLoadingListener.onLoadingFinished()",this);try{if (!mIsActive) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPage$MyLoadingListener.onLoadingFinished()",this);return;}}
            if (mAlbumDataAdapter.size() == 0) {
                if (mSyncTask == null) {
                    mSyncTask = mMediaSet.requestSync(AlbumPage.this);
                }
                if (mSyncTask.isDone()){
                    Toast.makeText((Context) mActivity,
                            R.string.empty_album, Toast.LENGTH_LONG).show();
                    mActivity.getStateManager().finishState(AlbumPage.this);
                }
            }
            if (mSyncTask == null || mSyncTask.isDone()) {
                GalleryUtils.setSpinnerVisibility((Activity) mActivity, false);
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPage$MyLoadingListener.onLoadingFinished()",this,throwable);throw throwable;}
        }
    }

    private class MyDetailsSource implements DetailsHelper.DetailsSource {
        private int mIndex;
        public int size() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.AlbumPage$MyDetailsSource.size()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AlbumPage$MyDetailsSource.size()",this);return mAlbumDataAdapter.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.AlbumPage$MyDetailsSource.size()",this,throwable);throw throwable;}
        }

        public int getIndex() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.AlbumPage$MyDetailsSource.getIndex()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AlbumPage$MyDetailsSource.getIndex()",this);return mIndex;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.AlbumPage$MyDetailsSource.getIndex()",this,throwable);throw throwable;}
        }

        /*// If requested index is out of active window, suggest a valid index.*/
        /*// If there is no valid index available, return -1.*/
        public int findIndex(int indexHint) {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.AlbumPage$MyDetailsSource.findIndex(int)",this,indexHint);try{if (mAlbumDataAdapter.isActive(indexHint)) {
                mIndex = indexHint;
            } else {
                mIndex = mAlbumDataAdapter.getActiveStart();
                if (!mAlbumDataAdapter.isActive(mIndex)) {
                    {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AlbumPage$MyDetailsSource.findIndex(int)",this);return -1;}
                }
            }
            {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AlbumPage$MyDetailsSource.findIndex(int)",this);return mIndex;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.AlbumPage$MyDetailsSource.findIndex(int)",this,throwable);throw throwable;}
        }

        public MediaDetails getDetails() {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaDetails com.android.gallery3d.app.AlbumPage$MyDetailsSource.getDetails()",this);try{MediaObject item = mAlbumDataAdapter.get(mIndex);
            if (item != null) {
                mHighlightDrawer.setHighlightItem(item.getPath());
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.android.gallery3d.app.AlbumPage$MyDetailsSource.getDetails()",this);return item.getDetails();}
            } else {
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.android.gallery3d.app.AlbumPage$MyDetailsSource.getDetails()",this);return null;}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaDetails com.android.gallery3d.app.AlbumPage$MyDetailsSource.getDetails()",this,throwable);throw throwable;}
        }
    }
}
