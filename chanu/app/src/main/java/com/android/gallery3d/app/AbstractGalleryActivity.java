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

import android.os.Handler;
import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.ImageCacheService;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.GLRootView;
import com.android.gallery3d.ui.PositionRepository;
import com.android.gallery3d.util.ThreadPool;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;

public class AbstractGalleryActivity extends Activity implements GalleryActivity {
    @SuppressWarnings("unused")
    private static final String TAG = "AbstractGalleryActivity";
    protected GLRootView mGLRootView;
    private StateManager mStateManager;
    private PositionRepository mPositionRepository = new PositionRepository();

    private AlertDialog mAlertDialog = null;
    private BroadcastReceiver mMountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity$1.onReceive(android.content.Context,android.content.Intent)",this,context,intent);try{if (getExternalCacheDir() != null) {onStorageReady();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity$1.onReceive(android.content.Context,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity$1.onReceive(android.content.Context,android.content.Intent)",this,throwable);throw throwable;}
        }
    };
    private IntentFilter mMountFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
    private Handler handler;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity.onSaveInstanceState(android.os.Bundle)",this,outState);try{if (mGLRootView != null) {
	        mGLRootView.lockRenderThread();
	        try {
	            super.onSaveInstanceState(outState);
	            getStateManager().saveState(outState);
	        } finally {
	            mGLRootView.unlockRenderThread();
	        }
    	}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity.onConfigurationChanged(android.content.res.Configuration)",this,config);try{super.onConfigurationChanged(config);
        mStateManager.onConfigurationChange(config);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity.onConfigurationChanged(android.content.res.Configuration)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity.onConfigurationChanged(android.content.res.Configuration)",this,throwable);throw throwable;}
    }

    public Context getAndroidContext() {
        com.mijack.Xlog.logMethodEnter("android.content.Context com.android.gallery3d.app.AbstractGalleryActivity.getAndroidContext()",this);try{com.mijack.Xlog.logMethodExit("android.content.Context com.android.gallery3d.app.AbstractGalleryActivity.getAndroidContext()",this);return this;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.Context com.android.gallery3d.app.AbstractGalleryActivity.getAndroidContext()",this,throwable);throw throwable;}
    }

    public ImageCacheService getImageCacheService() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.ImageCacheService com.android.gallery3d.app.AbstractGalleryActivity.getImageCacheService()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.ImageCacheService com.android.gallery3d.app.AbstractGalleryActivity.getImageCacheService()",this);return ((GalleryApp) getApplication()).getImageCacheService();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.ImageCacheService com.android.gallery3d.app.AbstractGalleryActivity.getImageCacheService()",this,throwable);throw throwable;}
    }

    public DataManager getDataManager() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.DataManager com.android.gallery3d.app.AbstractGalleryActivity.getDataManager()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.DataManager com.android.gallery3d.app.AbstractGalleryActivity.getDataManager()",this);return ((GalleryApp) getApplication()).getDataManager();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.DataManager com.android.gallery3d.app.AbstractGalleryActivity.getDataManager()",this,throwable);throw throwable;}
    }

    public ThreadPool getThreadPool() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool com.android.gallery3d.app.AbstractGalleryActivity.getThreadPool()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool com.android.gallery3d.app.AbstractGalleryActivity.getThreadPool()",this);return ((GalleryApp) getApplication()).getThreadPool();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool com.android.gallery3d.app.AbstractGalleryActivity.getThreadPool()",this,throwable);throw throwable;}
    }

    public GalleryApp getGalleryApplication() {
        com.mijack.Xlog.logMethodEnter("GalleryApp com.android.gallery3d.app.AbstractGalleryActivity.getGalleryApplication()",this);try{com.mijack.Xlog.logMethodExit("GalleryApp com.android.gallery3d.app.AbstractGalleryActivity.getGalleryApplication()",this);return (GalleryApp) getApplication();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("GalleryApp com.android.gallery3d.app.AbstractGalleryActivity.getGalleryApplication()",this,throwable);throw throwable;}
    }

    public synchronized StateManager getStateManager() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.StateManager com.android.gallery3d.app.AbstractGalleryActivity.getStateManager()",this);try{if (mStateManager == null) {
            mStateManager = new StateManager(this);
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.StateManager com.android.gallery3d.app.AbstractGalleryActivity.getStateManager()",this);return mStateManager;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.StateManager com.android.gallery3d.app.AbstractGalleryActivity.getStateManager()",this,throwable);throw throwable;}
    }

    public GLRoot getGLRoot() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.GLRoot com.android.gallery3d.app.AbstractGalleryActivity.getGLRoot()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.GLRoot com.android.gallery3d.app.AbstractGalleryActivity.getGLRoot()",this);return mGLRootView;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.GLRoot com.android.gallery3d.app.AbstractGalleryActivity.getGLRoot()",this,throwable);throw throwable;}
    }

    public PositionRepository getPositionRepository() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PositionRepository com.android.gallery3d.app.AbstractGalleryActivity.getPositionRepository()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PositionRepository com.android.gallery3d.app.AbstractGalleryActivity.getPositionRepository()",this);return mPositionRepository;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PositionRepository com.android.gallery3d.app.AbstractGalleryActivity.getPositionRepository()",this,throwable);throw throwable;}
    }

    @Override
    public void setContentView(int resId) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity.setContentView(int)",this,resId);try{super.setContentView(resId);
        mGLRootView = (GLRootView) findViewById(R.id.gl_root_view);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity.setContentView(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity.setContentView(int)",this,throwable);throw throwable;}
    }

    public int getActionBarHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.AbstractGalleryActivity.getActionBarHeight()",this);try{ActionBar actionBar = getActionBar();
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.AbstractGalleryActivity.getActionBarHeight()",this);return actionBar != null ? actionBar.getHeight() : 0;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.AbstractGalleryActivity.getActionBarHeight()",this,throwable);throw throwable;}
    }

    protected void onStorageReady() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity.onStorageReady()",this);try{if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
            unregisterReceiver(mMountReceiver);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity.onStorageReady()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity.onStorageReady()",this,throwable);throw throwable;}
    }

    @Override
    protected void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity.onStart()",this);try{super.onStart();
        handler = new Handler();
        if (getExternalCacheDir() == null) {
            OnCancelListener onCancel = new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity$2.onCancel(android.content.DialogInterface)",this,dialog);try{finish();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity$2.onCancel(android.content.DialogInterface)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity$2.onCancel(android.content.DialogInterface)",this,throwable);throw throwable;}
                }
            };
            OnClickListener onClick = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity$3.onClick(android.content.DialogInterface,int)",this,dialog,which);try{dialog.cancel();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity$3.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity$3.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                }
            };
            mAlertDialog = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("No Storage")
                    .setMessage("No external storage available.")
                    .setNegativeButton(android.R.string.cancel, onClick)
                    .setOnCancelListener(onCancel)
                    .show();
            registerReceiver(mMountReceiver, mMountFilter);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    protected void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity.onStop()",this);try{super.onStop();
        handler = null;
        if (mAlertDialog != null) {
            unregisterReceiver(mMountReceiver);
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity.onStop()",this,throwable);throw throwable;}
    }

    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity.onResume()",this);try{super.onResume();
        if (mGLRootView != null) {
	        mGLRootView.lockRenderThread();
	        try {
	            getStateManager().resume();
	            getDataManager().resume();
	        } finally {
	            mGLRootView.unlockRenderThread();
	        }
	        mGLRootView.onResume();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity.onResume()",this,throwable);throw throwable;}
    }

    @Override
    protected void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity.onPause()",this);try{super.onPause();
        if (mGLRootView != null) {
	        mGLRootView.onPause();
	        mGLRootView.lockRenderThread();
	        try {
	            getStateManager().pause();
	            getDataManager().pause();
	        } finally {
	            mGLRootView.unlockRenderThread();
	        }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity.onPause()",this,throwable);throw throwable;}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AbstractGalleryActivity.onActivityResult(int,int,android.content.Intent)",this,requestCode,resultCode,data);try{if (mGLRootView != null) {
	        mGLRootView.lockRenderThread();
	        try {
	            getStateManager().notifyActivityResult(
	                    requestCode, resultCode, data);
	        } finally {
	            mGLRootView.unlockRenderThread();
	        }
    	}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AbstractGalleryActivity.onActivityResult(int,int,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AbstractGalleryActivity.onActivityResult(int,int,android.content.Intent)",this,throwable);throw throwable;}
    }

    @Override
    public GalleryActionBar getGalleryActionBar() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.GalleryActionBar com.android.gallery3d.app.AbstractGalleryActivity.getGalleryActionBar()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.GalleryActionBar com.android.gallery3d.app.AbstractGalleryActivity.getGalleryActionBar()",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.GalleryActionBar com.android.gallery3d.app.AbstractGalleryActivity.getGalleryActionBar()",this,throwable);throw throwable;}
    }

    @Override
    public Handler getHandler() {
        com.mijack.Xlog.logMethodEnter("android.os.Handler com.android.gallery3d.app.AbstractGalleryActivity.getHandler()",this);try{com.mijack.Xlog.logMethodExit("android.os.Handler com.android.gallery3d.app.AbstractGalleryActivity.getHandler()",this);return handler;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Handler com.android.gallery3d.app.AbstractGalleryActivity.getHandler()",this,throwable);throw throwable;}
    }

}
