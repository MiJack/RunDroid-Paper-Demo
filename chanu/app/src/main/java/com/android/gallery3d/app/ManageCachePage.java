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

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.AlbumSetView;
import com.android.gallery3d.ui.CacheStorageUsageInfo;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.GLView;
import com.android.gallery3d.ui.ManageCacheDrawer;
import com.android.gallery3d.ui.MenuExecutor;
import com.android.gallery3d.ui.SelectionDrawer;
import com.android.gallery3d.ui.SelectionManager;
import com.android.gallery3d.ui.SlotView;
import com.android.gallery3d.ui.StaticBackground;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ManageCachePage extends ActivityState implements
        SelectionManager.SelectionListener, MenuExecutor.ProgressListener,
        EyePosition.EyePositionListener, OnClickListener {
    public static final String KEY_MEDIA_PATH = "media-path";

    private static final String TAG = "ManageCachePage";

    private static final float USER_DISTANCE_METER = 0.3f;
    private static final int DATA_CACHE_SIZE = 256;
    private static final int MSG_REFRESH_STORAGE_INFO = 1;
    private static final int MSG_REQUEST_LAYOUT = 2;
    private static final int PROGRESS_BAR_MAX = 10000;

    private StaticBackground mStaticBackground;
    private AlbumSetView mAlbumSetView;

    private MediaSet mMediaSet;

    protected SelectionManager mSelectionManager;
    protected SelectionDrawer mSelectionDrawer;
    private AlbumSetDataAdapter mAlbumSetDataAdapter;
    private float mUserDistance; /*// in pixel*/

    private EyePosition mEyePosition;

    /*// The eyes' position of the user, the origin is at the center of the*/
    /*// device and the unit is in pixels.*/
    private float mX;
    private float mY;
    private float mZ;

    private int mAlbumCountToMakeAvailableOffline;
    private View mFooterContent;
    private CacheStorageUsageInfo mCacheStorageInfo;
    private Future<Void> mUpdateStorageInfo;
    private Handler mHandler;
    private boolean mLayoutReady = false;

    private GLView mRootPane = new GLView() {
        private float mMatrix[] = new float[16];

        @Override
        protected void onLayout(
                boolean changed, int left, int top, int right, int bottom) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage$1.onLayout(boolean,int,int,int,int)",this,changed,left,top,right,bottom);try{/*// Hack: our layout depends on other components on the screen.*/
            /*// We assume the other components will complete before we get a change*/
            /*// to run a message in main thread.*/
            if (!mLayoutReady) {
                mHandler.sendEmptyMessage(MSG_REQUEST_LAYOUT);
                {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage$1.onLayout(boolean,int,int,int,int)",this);return;}
            }
            mLayoutReady = false;

            mStaticBackground.layout(0, 0, right - left, bottom - top);
            mEyePosition.resetPosition();
            Activity activity = (Activity) mActivity;
            int slotViewTop = GalleryActionBar.getHeight(activity);
            int slotViewBottom = bottom - top;

            View footer = activity.findViewById(R.id.footer);
            if (footer != null) {
                int location[] = {0, 0};
                footer.getLocationOnScreen(location);
                slotViewBottom = location[1];
            }

            mAlbumSetView.layout(0, slotViewTop, right - left, slotViewBottom);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage$1.onLayout(boolean,int,int,int,int)",this,throwable);throw throwable;}
        }

        @Override
        protected void render(GLCanvas canvas) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage$1.render(com.android.gallery3d.ui.GLCanvas)",this,canvas);try{canvas.save(GLCanvas.SAVE_FLAG_MATRIX);
            GalleryUtils.setViewPointMatrix(mMatrix,
                        getWidth() / 2 + mX, getHeight() / 2 + mY, mZ);
            canvas.multiplyMatrix(mMatrix, 0);
            super.render(canvas);
            canvas.restore();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage$1.render(com.android.gallery3d.ui.GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage$1.render(com.android.gallery3d.ui.GLCanvas)",this,throwable);throw throwable;}
        }
    };

    public void onEyePositionChanged(float x, float y, float z) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onEyePositionChanged(float,float,float)",this,x,y,z);try{mRootPane.lockRendering();
        mX = x;
        mY = y;
        mZ = z;
        mRootPane.unlockRendering();
        mRootPane.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onEyePositionChanged(float,float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.onEyePositionChanged(float,float,float)",this,throwable);throw throwable;}
    }

    private void onDown(int index) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onDown(int)",this,index);try{MediaSet set = mAlbumSetDataAdapter.getMediaSet(index);
        Path path = (set == null) ? null : set.getPath();
        mSelectionManager.setPressedPath(path);
        mAlbumSetView.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onDown(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.onDown(int)",this,throwable);throw throwable;}
    }

    private void onUp() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onUp()",this);try{mSelectionManager.setPressedPath(null);
        mAlbumSetView.invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onUp()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.onUp()",this,throwable);throw throwable;}
    }

    public void onSingleTapUp(int slotIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onSingleTapUp(int)",this,slotIndex);try{MediaSet targetSet = mAlbumSetDataAdapter.getMediaSet(slotIndex);
        if (targetSet == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onSingleTapUp(int)",this);return;}} /*// Content is dirty, we shall reload soon*/

        /*// ignore selection action if the target set does not support cache*/
        /*// operation (like a local album).*/
        if ((targetSet.getSupportedOperations()
                & MediaSet.SUPPORT_CACHE) == 0) {
            showToastForLocalAlbum();
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onSingleTapUp(int)",this);return;}
        }

        Path path = targetSet.getPath();
        boolean isFullyCached =
                (targetSet.getCacheFlag() == MediaObject.CACHE_FLAG_FULL);
        boolean isSelected = mSelectionManager.isItemSelected(path);

        if (!isFullyCached) {
            /*// We only count the media sets that will be made available offline*/
            /*// in this session.*/
            if (isSelected) {
                --mAlbumCountToMakeAvailableOffline;
            } else {
                ++mAlbumCountToMakeAvailableOffline;
            }
        }

        long sizeOfTarget = targetSet.getCacheSize();
        mCacheStorageInfo.increaseTargetCacheSize(
                (isFullyCached ^ isSelected) ? -sizeOfTarget : sizeOfTarget);
        refreshCacheStorageInfo();

        mSelectionManager.toggle(path);
        mAlbumSetView.invalidate();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.onSingleTapUp(int)",this,throwable);throw throwable;}
    }

    @Override
    public void onCreate(Bundle data, Bundle restoreState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onCreate(android.os.Bundle,android.os.Bundle)",this,data,restoreState);try{mCacheStorageInfo = new CacheStorageUsageInfo(mActivity);
        initializeViews();
        initializeData(data);
        mEyePosition = new EyePosition(mActivity.getAndroidContext(), this);
        mHandler = new SynchronizedHandler(mActivity.getGLRoot()) {
            @Override
            public void handleMessage(Message message) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage$2.handleMessage(android.os.Message)",this,message);try{switch (message.what) {
                    case MSG_REFRESH_STORAGE_INFO:
                        refreshCacheStorageInfo();
                        break;
                    case MSG_REQUEST_LAYOUT: {
                        mLayoutReady = true;
                        removeMessages(MSG_REQUEST_LAYOUT);
                        mRootPane.requestLayout();
                        break;
                    }
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage$2.handleMessage(android.os.Message)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage$2.handleMessage(android.os.Message)",this,throwable);throw throwable;}
            }
        };com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onCreate(android.os.Bundle,android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.onCreate(android.os.Bundle,android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onConfigurationChanged(android.content.res.Configuration)",this,config);try{/*// We use different layout resources for different configs*/
        initializeFooterViews();
        FrameLayout layout = (FrameLayout) ((Activity) mActivity).findViewById(R.id.footer);
        if (layout.getVisibility() == View.VISIBLE) {
            layout.removeAllViews();
            layout.addView(mFooterContent);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onConfigurationChanged(android.content.res.Configuration)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.onConfigurationChanged(android.content.res.Configuration)",this,throwable);throw throwable;}
    }

    @Override
    public void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onPause()",this);try{super.onPause();
        mAlbumSetDataAdapter.pause();
        mAlbumSetView.pause();
        mEyePosition.pause();

        if (mUpdateStorageInfo != null) {
            mUpdateStorageInfo.cancel();
            mUpdateStorageInfo = null;
        }
        mHandler.removeMessages(MSG_REFRESH_STORAGE_INFO);

        FrameLayout layout = (FrameLayout) ((Activity) mActivity).findViewById(R.id.footer);
        layout.removeAllViews();
        layout.setVisibility(View.INVISIBLE);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.onPause()",this,throwable);throw throwable;}
    }

    private Job<Void> mUpdateStorageInfoJob = new Job<Void>() {
        @Override
        public Void run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("java.lang.Void com.android.gallery3d.app.ManageCachePage$3.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{mCacheStorageInfo.loadStorageInfo(jc);
            if (!jc.isCancelled()) {
                mHandler.sendEmptyMessage(MSG_REFRESH_STORAGE_INFO);
            }
            {com.mijack.Xlog.logMethodExit("java.lang.Void com.android.gallery3d.app.ManageCachePage$3.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Void com.android.gallery3d.app.ManageCachePage$3.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    };

    @Override
    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onResume()",this);try{super.onResume();
        setContentPane(mRootPane);
        mAlbumSetDataAdapter.resume();
        mAlbumSetView.resume();
        mEyePosition.resume();
        mUpdateStorageInfo = mActivity.getThreadPool().submit(mUpdateStorageInfoJob);
        FrameLayout layout = (FrameLayout) ((Activity) mActivity).findViewById(R.id.footer);
        layout.addView(mFooterContent);
        layout.setVisibility(View.VISIBLE);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.onResume()",this,throwable);throw throwable;}
    }

    private void initializeData(Bundle data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.initializeData(android.os.Bundle)",this,data);try{mUserDistance = GalleryUtils.meterToPixel(USER_DISTANCE_METER);
        String mediaPath = data.getString(ManageCachePage.KEY_MEDIA_PATH);
        mMediaSet = mActivity.getDataManager().getMediaSet(mediaPath);
        mSelectionManager.setSourceMediaSet(mMediaSet);

        /*// We will always be in selection mode in this page.*/
        mSelectionManager.setAutoLeaveSelectionMode(false);
        mSelectionManager.enterSelectionMode();

        mAlbumSetDataAdapter = new AlbumSetDataAdapter(
                mActivity, mMediaSet, DATA_CACHE_SIZE);
        mAlbumSetView.setModel(mAlbumSetDataAdapter);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.initializeData(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.initializeData(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void initializeViews() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.initializeViews()",this);try{Activity activity = (Activity) mActivity;

        mSelectionManager = new SelectionManager(mActivity, true);
        mSelectionManager.setSelectionListener(this);
        mStaticBackground = new StaticBackground(activity);
        mRootPane.addComponent(mStaticBackground);

        Config.ManageCachePage config = Config.ManageCachePage.get(activity);
        mSelectionDrawer = new ManageCacheDrawer((Context) mActivity,
                mSelectionManager, config.cachePinSize, config.cachePinMargin);
        mAlbumSetView = new AlbumSetView(mActivity, mSelectionDrawer,
                config.slotViewSpec, config.labelSpec);
        mAlbumSetView.setListener(new SlotView.SimpleListener() {
            @Override
            public void onDown(int index) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage$4.onDown(int)",this,index);try{ManageCachePage.this.onDown(index);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage$4.onDown(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage$4.onDown(int)",this,throwable);throw throwable;}
            }

            @Override
            public void onUp() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage$4.onUp()",this);try{ManageCachePage.this.onUp();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage$4.onUp()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage$4.onUp()",this,throwable);throw throwable;}
            }

            @Override
            public void onSingleTapUp(int slotIndex) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage$4.onSingleTapUp(int)",this,slotIndex);try{ManageCachePage.this.onSingleTapUp(slotIndex);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage$4.onSingleTapUp(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage$4.onSingleTapUp(int)",this,throwable);throw throwable;}
            }
        });
        mRootPane.addComponent(mAlbumSetView);
        initializeFooterViews();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.initializeViews()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.initializeViews()",this,throwable);throw throwable;}
    }

    private void initializeFooterViews() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.initializeFooterViews()",this);try{Activity activity = (Activity) mActivity;

        FrameLayout footer = (FrameLayout) activity.findViewById(R.id.footer);
        LayoutInflater inflater = activity.getLayoutInflater();
        mFooterContent = inflater.inflate(R.layout.manage_offline_bar, null);

        mFooterContent.findViewById(R.id.done).setOnClickListener(this);
        mStaticBackground.setImage(R.drawable.background, R.drawable.background_portrait);
        refreshCacheStorageInfo();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.initializeFooterViews()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.initializeFooterViews()",this,throwable);throw throwable;}
    }

    @Override
    public void onClick(View view) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onClick(com.android.gallery3d.ui.AlbumSetView)",this,view);try{Utils.assertTrue(view.getId() == R.id.done);

        ArrayList<Path> ids = mSelectionManager.getSelected(false);
        if (ids.size() == 0) {
            onBackPressed();
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onClick(com.android.gallery3d.ui.AlbumSetView)",this);return;}
        }
        showToast();

        MenuExecutor menuExecutor = new MenuExecutor(mActivity, mSelectionManager);
        menuExecutor.startAction(R.id.action_toggle_full_caching,
                R.string.process_caching_requests, this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.onClick(com.android.gallery3d.ui.AlbumSetView)",this,throwable);throw throwable;}
    }

    private void showToast() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.showToast()",this);try{if (mAlbumCountToMakeAvailableOffline > 0) {
            Activity activity = (Activity) mActivity;
            Toast.makeText(activity, activity.getResources().getQuantityString(
                    R.string.make_available_offline,
                    mAlbumCountToMakeAvailableOffline),
                    Toast.LENGTH_SHORT).show();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.showToast()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.showToast()",this,throwable);throw throwable;}
    }

    private void showToastForLocalAlbum() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.showToastForLocalAlbum()",this);try{Activity activity = (Activity) mActivity;
        Toast.makeText(activity, activity.getResources().getString(
            R.string.try_to_set_local_album_available_offline),
            Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.showToastForLocalAlbum()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.showToastForLocalAlbum()",this,throwable);throw throwable;}
    }

    private void refreshCacheStorageInfo() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.refreshCacheStorageInfo()",this);try{ProgressBar progressBar = (ProgressBar) mFooterContent.findViewById(R.id.progress);
        TextView status = (TextView) mFooterContent.findViewById(R.id.status);
        progressBar.setMax(PROGRESS_BAR_MAX);
        long totalBytes = mCacheStorageInfo.getTotalBytes();
        long usedBytes = mCacheStorageInfo.getUsedBytes();
        long expectedBytes = mCacheStorageInfo.getExpectedUsedBytes();
        long freeBytes = mCacheStorageInfo.getFreeBytes();

        Activity activity = (Activity) mActivity;
        if (totalBytes == 0) {
            progressBar.setProgress(0);
            progressBar.setSecondaryProgress(0);

            /*// TODO: get the string translated*/
            String label = activity.getString(R.string.free_space_format, "-");
            status.setText(label);
        } else {
            progressBar.setProgress((int) (usedBytes * PROGRESS_BAR_MAX / totalBytes));
            progressBar.setSecondaryProgress(
                    (int) (expectedBytes * PROGRESS_BAR_MAX / totalBytes));
            String label = activity.getString(R.string.free_space_format,
                    Formatter.formatFileSize(activity, freeBytes));
            status.setText(label);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.refreshCacheStorageInfo()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.refreshCacheStorageInfo()",this,throwable);throw throwable;}
    }

    public void onProgressComplete(int result) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onProgressComplete(int)",this,result);try{onBackPressed();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onProgressComplete(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ManageCachePage.onProgressComplete(int)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onProgressUpdate(int)",this,index);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onProgressUpdate(int)",this);}

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onSelectionModeChange(int)",this,mode);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onSelectionModeChange(int)",this);}

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ManageCachePage.onSelectionChange(com.android.gallery3d.data.Path,boolean)",this,path,selected);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ManageCachePage.onSelectionChange(com.android.gallery3d.data.Path,boolean)",this);}

}
