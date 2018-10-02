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

import com.android.gallery3d.common.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Stack;

public class StateManager {
    @SuppressWarnings("unused")
    private static final String TAG = "StateManager";
    private boolean mIsResumed = false;

    private static final String KEY_MAIN = "activity-state";
    private static final String KEY_DATA = "data";
    private static final String KEY_STATE = "bundle";
    private static final String KEY_CLASS = "class";
    private static final String KEY_LAUNCH_GALLERY_ON_TOP = "launch-gallery-on-top";

    private GalleryActivity mContext;
    private Stack<StateEntry> mStack = new Stack<StateEntry>();
    private ActivityState.ResultEntry mResult;
    private boolean mLaunchGalleryOnTop = false;

    public StateManager(GalleryActivity context) {
        mContext = context;
    }

    public void startState(Class<? extends ActivityState> klass,
            Bundle data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.startState(java.lang.Class,android.os.Bundle)",this,klass,data);try{Log.v(TAG, "startState " + klass);
        ActivityState state = null;
        try {
            state = klass.newInstance();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        if (!mStack.isEmpty()) {
            ActivityState top = getTopState();
            if (mIsResumed) {top.onPause();}
        }
        state.initialize(mContext, data);

        mStack.push(new StateEntry(data, state));
        state.onCreate(data, null);
        if (mIsResumed) {state.resume();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.startState(java.lang.Class,android.os.Bundle)",this,throwable);throw throwable;}
    }

    public void setLaunchGalleryOnTop(boolean enabled) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.setLaunchGalleryOnTop(boolean)",this,enabled);try{mLaunchGalleryOnTop = enabled;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.StateManager.setLaunchGalleryOnTop(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.setLaunchGalleryOnTop(boolean)",this,throwable);throw throwable;}
    }

    public void startStateForResult(Class<? extends ActivityState> klass,
            int requestCode, Bundle data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.startStateForResult(java.lang.Class,int,android.os.Bundle)",this,klass,requestCode,data);try{Log.v(TAG, "startStateForResult " + klass + ", " + requestCode);
        ActivityState state = null;
        try {
            state = klass.newInstance();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        state.initialize(mContext, data);
        state.mResult = new ActivityState.ResultEntry();
        state.mResult.requestCode = requestCode;

        if (!mStack.isEmpty()) {
            ActivityState as = getTopState();
            as.mReceivedResults = state.mResult;
            if (mIsResumed) {as.onPause();}
        } else {
            mResult = state.mResult;
        }

        mStack.push(new StateEntry(data, state));
        state.onCreate(data, null);
        if (mIsResumed) {state.resume();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.startStateForResult(java.lang.Class,int,android.os.Bundle)",this,throwable);throw throwable;}
    }

    public boolean createOptionsMenu(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.StateManager.createOptionsMenu(android.view.Menu)",this,menu);try{if (!mStack.isEmpty()) {
            ((Activity) mContext).setProgressBarIndeterminateVisibility(false);
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.StateManager.createOptionsMenu(android.view.Menu)",this);return getTopState().onCreateActionBar(menu);}
        } else {
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.StateManager.createOptionsMenu(android.view.Menu)",this);return false;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.StateManager.createOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
    }

    public void onConfigurationChange(Configuration config) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.onConfigurationChange(android.content.res.Configuration)",this,config);try{for (StateEntry entry : mStack) {
            entry.activityState.onConfigurationChanged(config);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.StateManager.onConfigurationChange(android.content.res.Configuration)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.onConfigurationChange(android.content.res.Configuration)",this,throwable);throw throwable;}
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.resume()",this);try{if (mIsResumed) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.StateManager.resume()",this);return;}}
        mIsResumed = true;
        if (!mStack.isEmpty()) {getTopState().resume();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.resume()",this,throwable);throw throwable;}
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.pause()",this);try{if (!mIsResumed) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.StateManager.pause()",this);return;}}
        mIsResumed = false;
        if (!mStack.isEmpty()) {getTopState().onPause();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.pause()",this,throwable);throw throwable;}
    }

    public void notifyActivityResult(int requestCode, int resultCode, Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.notifyActivityResult(int,int,android.content.Intent)",this,requestCode,resultCode,data);try{getTopState().onStateResult(requestCode, resultCode, data);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.StateManager.notifyActivityResult(int,int,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.notifyActivityResult(int,int,android.content.Intent)",this,throwable);throw throwable;}
    }

    public int getStateCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.StateManager.getStateCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.StateManager.getStateCount()",this);return mStack.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.StateManager.getStateCount()",this,throwable);throw throwable;}
    }

    public boolean itemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.StateManager.itemSelected(android.view.MenuItem)",this,item);try{if (!mStack.isEmpty()) {
            if (item.getItemId() == android.R.id.home) {
                if (mStack.size() > 1) {
                    getTopState().onBackPressed();
                } else if (mLaunchGalleryOnTop) {
                    Activity activity = (Activity) mContext;
                    Intent intent = new Intent(activity, Gallery.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ((Activity) mContext).startActivity(intent);
                }
                {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.StateManager.itemSelected(android.view.MenuItem)",this);return true;}
            } else {
                {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.StateManager.itemSelected(android.view.MenuItem)",this);return getTopState().onItemSelected(item);}
            }
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.StateManager.itemSelected(android.view.MenuItem)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.StateManager.itemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    public void onBackPressed() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.onBackPressed()",this);try{if (!mStack.isEmpty()) {
            getTopState().onBackPressed();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.StateManager.onBackPressed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.onBackPressed()",this,throwable);throw throwable;}
    }
    
    public void compactActivityStateStack() {
    	com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.compactActivityStateStack()",this);try{Class<? extends ActivityState> stateClass = getTopState().getClass();
    	for (int i = mStack.size() - 2; i >= 0; i--) {
    		StateEntry entry = mStack.get(i);
    		if (entry.activityState.getClass().equals(stateClass)) {
    			Log.i(TAG, "Removing state from stack " + i);
    			mStack.remove(i);
    		} else {
    			break;
    		}
    	}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.StateManager.compactActivityStateStack()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.compactActivityStateStack()",this,throwable);throw throwable;}
    }
    
    public String getStackDescription() {
    	com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.app.StateManager.getStackDescription()",this);try{StringBuffer buf = new StringBuffer();
    	for (int i = mStack.size() - 1; i >= 0; i--) {
    		StateEntry entry = mStack.get(i);
    		buf.append(" " + i + ". " + entry.activityState.getClass());
    	}
    	{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.app.StateManager.getStackDescription()",this);return buf.toString();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.app.StateManager.getStackDescription()",this,throwable);throw throwable;}
    }

    void finishState(ActivityState state) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.finishState(com.android.gallery3d.app.ActivityState)",this,state);try{Log.v(TAG, "finishState " + state.getClass());
        if (state != mStack.peek().activityState) {
            if (state.isDestroyed()) {
                Log.d(TAG, "The state is already destroyed");
                {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.StateManager.finishState(com.android.gallery3d.app.ActivityState)",this);return;}
            } else {
                throw new IllegalArgumentException("The stateview to be finished"
                        + " is not at the top of the stack: " + state + ", "
                        + mStack.peek().activityState);
            }
        }

        /*// Remove the top state.*/
        mStack.pop();
        if (mIsResumed) {state.onPause();}
        mContext.getGLRoot().setContentPane(null);
        state.onDestroy();

        if (mStack.isEmpty()) {
            Log.v(TAG, "no more state, finish activity");
            Activity activity = (Activity) mContext.getAndroidContext();
            if (mResult != null) {
                activity.setResult(mResult.resultCode, mResult.resultData);
            }
            activity.finish();

            /*// The finish() request is rejected (only happens under Monkey),*/
            /*// so we start the default page instead.*/
            if (!activity.isFinishing()) {
                Log.v(TAG, "finish() failed, start default page");
                ((Gallery) mContext).startDefaultPage();
            }
        } else {
            /*// Restore the immediately previous state*/
            ActivityState top = mStack.peek().activityState;
            if (mIsResumed) {top.resume();}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.finishState(com.android.gallery3d.app.ActivityState)",this,throwable);throw throwable;}
    }

    void switchState(ActivityState oldState,
            Class<? extends ActivityState> klass, Bundle data) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.switchState(com.android.gallery3d.app.ActivityState,java.lang.Class,android.os.Bundle)",this,oldState,klass,data);try{Log.v(TAG, "switchState " + oldState + ", " + klass);
        if (oldState != mStack.peek().activityState) {
            throw new IllegalArgumentException("The stateview to be finished"
                    + " is not at the top of the stack: " + oldState + ", "
                    + mStack.peek().activityState);
        }
        /*// Remove the top state.*/
        mStack.pop();
        if (mIsResumed) {oldState.onPause();}
        oldState.onDestroy();

        /*// Create new state.*/
        ActivityState state = null;
        try {
            state = klass.newInstance();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        state.initialize(mContext, data);
        mStack.push(new StateEntry(data, state));
        state.onCreate(data, null);
        if (mIsResumed) {state.resume();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.switchState(com.android.gallery3d.app.ActivityState,java.lang.Class,android.os.Bundle)",this,throwable);throw throwable;}
    }

    public void destroy() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.destroy()",this);try{Log.v(TAG, "destroy");
        while (!mStack.isEmpty()) {
            mStack.pop().activityState.onDestroy();
        }
        mStack.clear();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.StateManager.destroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.destroy()",this,throwable);throw throwable;}
    }

    @SuppressWarnings("unchecked")
    public void restoreFromState(Bundle inState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.restoreFromState(android.os.Bundle)",this,inState);try{Log.v(TAG, "restoreFromState");
        mLaunchGalleryOnTop = inState.getBoolean(KEY_LAUNCH_GALLERY_ON_TOP, false);
        Parcelable list[] = inState.getParcelableArray(KEY_MAIN);
        for (Parcelable parcelable : list) {
            Bundle bundle = (Bundle) parcelable;
            Class<? extends ActivityState> klass =
                    (Class<? extends ActivityState>) bundle.getSerializable(KEY_CLASS);

            Bundle data = bundle.getBundle(KEY_DATA);
            Bundle state = bundle.getBundle(KEY_STATE);

            ActivityState activityState;
            try {
                Log.v(TAG, "restoreFromState " + klass);
                activityState = klass.newInstance();
            } catch (Exception e) {
                throw new AssertionError(e);
            }
            activityState.initialize(mContext, data);
            activityState.onCreate(data, state);
            mStack.push(new StateEntry(data, activityState));
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.restoreFromState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    public void saveState(Bundle outState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.StateManager.saveState(android.os.Bundle)",this,outState);try{Log.v(TAG, "saveState");

        outState.putBoolean(KEY_LAUNCH_GALLERY_ON_TOP, mLaunchGalleryOnTop);
        Parcelable list[] = new Parcelable[mStack.size()];
        int i = 0;
        for (StateEntry entry : mStack) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_CLASS, entry.activityState.getClass());
            bundle.putBundle(KEY_DATA, entry.data);
            Bundle state = new Bundle();
            entry.activityState.onSaveState(state);
            bundle.putBundle(KEY_STATE, state);
            Log.v(TAG, "saveState " + entry.activityState.getClass());
            list[i++] = bundle;
        }
        outState.putParcelableArray(KEY_MAIN, list);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.StateManager.saveState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.StateManager.saveState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    public boolean hasStateClass(Class<? extends ActivityState> klass) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.StateManager.hasStateClass(java.lang.Class)",this,klass);try{for (StateEntry entry : mStack) {
            if (klass.isInstance(entry.activityState)) {
                {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.StateManager.hasStateClass(java.lang.Class)",this);return true;}
            }
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.StateManager.hasStateClass(java.lang.Class)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.StateManager.hasStateClass(java.lang.Class)",this,throwable);throw throwable;}
    }

    public ActivityState getTopState() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.ActivityState com.android.gallery3d.app.StateManager.getTopState()",this);try{Utils.assertTrue(!mStack.isEmpty());
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ActivityState com.android.gallery3d.app.StateManager.getTopState()",this);return mStack.peek().activityState;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.ActivityState com.android.gallery3d.app.StateManager.getTopState()",this,throwable);throw throwable;}
    }

    private static class StateEntry {
        public Bundle data;
        public ActivityState activityState;

        public StateEntry(Bundle data, ActivityState state) {
            this.data = data;
            this.activityState = state;
        }
    }
}
