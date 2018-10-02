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

import com.android.gallery3d.ui.GLView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

abstract public class ActivityState {
    public static final int FLAG_HIDE_ACTION_BAR = 1;
    public static final int FLAG_HIDE_STATUS_BAR = 2;

    protected GalleryActivity mActivity;
    protected Bundle mData;
    protected int mFlags;

    protected ResultEntry mReceivedResults;
    protected ResultEntry mResult;

    protected static class ResultEntry {
        public int requestCode;
        public int resultCode = Activity.RESULT_CANCELED;
        public Intent resultData;
        ResultEntry next;
    }

    private boolean mDestroyed = false;

    protected ActivityState() {
    }

    protected void setContentPane(GLView content) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.setContentPane(com.android.gallery3d.ui.GLView)",this,content);try{mActivity.getGLRoot().setContentPane(content);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.setContentPane(com.android.gallery3d.ui.GLView)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ActivityState.setContentPane(com.android.gallery3d.ui.GLView)",this,throwable);throw throwable;}
    }

    void initialize(GalleryActivity activity, Bundle data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.initialize(GalleryActivity,android.os.Bundle)",this,activity,data);try{mActivity = activity;
        mData = data;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.initialize(GalleryActivity,android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ActivityState.initialize(GalleryActivity,android.os.Bundle)",this,throwable);throw throwable;}
    }

    public Bundle getData() {
        com.mijack.Xlog.logMethodEnter("android.os.Bundle com.android.gallery3d.app.ActivityState.getData()",this);try{com.mijack.Xlog.logMethodExit("android.os.Bundle com.android.gallery3d.app.ActivityState.getData()",this);return mData;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Bundle com.android.gallery3d.app.ActivityState.getData()",this,throwable);throw throwable;}
    }

    protected void onBackPressed() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.onBackPressed()",this);try{mActivity.getStateManager().finishState(this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.onBackPressed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ActivityState.onBackPressed()",this,throwable);throw throwable;}
    }

    protected void setStateResult(int resultCode, Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.setStateResult(int,android.content.Intent)",this,resultCode,data);try{if (mResult == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.setStateResult(int,android.content.Intent)",this);return;}}
        mResult.resultCode = resultCode;
        mResult.resultData = data;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ActivityState.setStateResult(int,android.content.Intent)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.onConfigurationChanged(android.content.res.Configuration)",this,config);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.onConfigurationChanged(android.content.res.Configuration)",this);}

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.onSaveState(android.os.Bundle)",this,outState);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.onSaveState(android.os.Bundle)",this);}

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.onStateResult(int,int,android.content.Intent)",this,requestCode,resultCode,data);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.onStateResult(int,int,android.content.Intent)",this);}

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.onCreate(android.os.Bundle,android.os.Bundle)",this,data,storedState);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.onCreate(android.os.Bundle,android.os.Bundle)",this);}

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.onPause()",this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.onPause()",this);}

    /*// should only be called by StateManager*/
    void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.resume()",this);try{Activity activity = (Activity) mActivity;
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            if ((mFlags & FLAG_HIDE_ACTION_BAR) != 0) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
            int stateCount = mActivity.getStateManager().getStateCount();
            actionBar.setDisplayOptions(
                    stateCount == 1 ? 0 : ActionBar.DISPLAY_HOME_AS_UP,
                    ActionBar.DISPLAY_HOME_AS_UP);
            actionBar.setHomeButtonEnabled(true);
        }

        activity.invalidateOptionsMenu();

        if ((mFlags & FLAG_HIDE_STATUS_BAR) != 0) {
            WindowManager.LayoutParams params = ((Activity) mActivity).getWindow().getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
            ((Activity) mActivity).getWindow().setAttributes(params);
        } else {
            WindowManager.LayoutParams params = ((Activity) mActivity).getWindow().getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
            ((Activity) mActivity).getWindow().setAttributes(params);
        }

        ResultEntry entry = mReceivedResults;
        if (entry != null) {
            mReceivedResults = null;
            onStateResult(entry.requestCode, entry.resultCode, entry.resultData);
        }
        onResume();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ActivityState.resume()",this,throwable);throw throwable;}
    }

    /*// a subclass of ActivityState should override the method to resume itself*/
    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.onResume()",this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.onResume()",this);}

    protected boolean onCreateActionBar(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.ActivityState.onCreateActionBar(android.view.Menu)",this,menu);try{/*// TODO: we should return false if there is no menu to show*/
        /*//       this is a workaround for a bug in system*/
        GalleryActionBar actionBar = mActivity.getGalleryActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.ActivityState.onCreateActionBar(android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.ActivityState.onCreateActionBar(android.view.Menu)",this,throwable);throw throwable;}
    }

    protected boolean onItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.ActivityState.onItemSelected(android.view.MenuItem)",this,item);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.ActivityState.onItemSelected(android.view.MenuItem)",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.ActivityState.onItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    protected void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.ActivityState.onDestroy()",this);try{mDestroyed = true;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.ActivityState.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.ActivityState.onDestroy()",this,throwable);throw throwable;}
    }

    boolean isDestroyed() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.ActivityState.isDestroyed()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.ActivityState.isDestroyed()",this);return mDestroyed;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.ActivityState.isDestroyed()",this,throwable);throw throwable;}
    }
}
