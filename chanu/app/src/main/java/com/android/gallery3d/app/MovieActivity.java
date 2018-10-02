/*
 * Copyright (C) 2007 The Android Open Source Project
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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.VideoColumns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * This activity plays a video from a specified URI.
 */
public class MovieActivity extends Activity {
    @SuppressWarnings("unused")
    private static final String TAG = "MovieActivity";

    private MoviePlayer mPlayer;
    private boolean mFinishOnCompletion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MovieActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setContentView(R.layout.movie_view);
        View rootView = findViewById(R.id.root);
        Intent intent = getIntent();
        initializeActionBar(intent);
        mFinishOnCompletion = intent.getBooleanExtra(
                MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        mPlayer = new MoviePlayer(rootView, this, intent.getData(), savedInstanceState,
                !mFinishOnCompletion) {
            @Override
            public void onCompletion() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MovieActivity$1.onCompletion()",this);try{if (mFinishOnCompletion) {
                    finish();
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MovieActivity$1.onCompletion()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MovieActivity$1.onCompletion()",this,throwable);throw throwable;}
            }
        };
        if (intent.hasExtra(MediaStore.EXTRA_SCREEN_ORIENTATION)) {
            int orientation = intent.getIntExtra(
                    MediaStore.EXTRA_SCREEN_ORIENTATION,
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            if (orientation != getRequestedOrientation()) {
                setRequestedOrientation(orientation);
            }
        }
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.buttonBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
        winParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        win.setAttributes(winParams);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MovieActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MovieActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void initializeActionBar(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MovieActivity.initializeActionBar(android.content.Intent)",this,intent);try{ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
                ActionBar.DISPLAY_HOME_AS_UP);
        String title = intent.getStringExtra(Intent.EXTRA_TITLE);
        if (title == null) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(intent.getData(),
                        new String[] {VideoColumns.TITLE}, null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    title = cursor.getString(0);
                }
            } catch (Throwable t) {
                Log.w(TAG, "cannot get title from: " + intent.getDataString(), t);
            } finally {
                if (cursor != null) {cursor.close();}
            }
        }
        if (title != null) {actionBar.setTitle(title);}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MovieActivity.initializeActionBar(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MovieActivity.initializeActionBar(android.content.Intent)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.MovieActivity.onOptionsItemSelected(android.view.MenuItem)",this,item);try{if (item.getItemId() == android.R.id.home) {
            finish();
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.MovieActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.MovieActivity.onOptionsItemSelected(android.view.MenuItem)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.MovieActivity.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    @Override
    public void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MovieActivity.onStart()",this);try{((AudioManager) getSystemService(AUDIO_SERVICE))
                .requestAudioFocus(null, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        super.onStart();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MovieActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MovieActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    protected void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MovieActivity.onStop()",this);try{((AudioManager) getSystemService(AUDIO_SERVICE))
                .abandonAudioFocus(null);
        super.onStop();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MovieActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MovieActivity.onStop()",this,throwable);throw throwable;}
    }

    @Override
    public void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MovieActivity.onPause()",this);try{mPlayer.onPause();
        super.onPause();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MovieActivity.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MovieActivity.onPause()",this,throwable);throw throwable;}
    }

    @Override
    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MovieActivity.onResume()",this);try{mPlayer.onResume();
        super.onResume();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MovieActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MovieActivity.onResume()",this,throwable);throw throwable;}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MovieActivity.onSaveInstanceState(android.os.Bundle)",this,outState);try{super.onSaveInstanceState(outState);
        mPlayer.onSaveInstanceState(outState);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MovieActivity.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MovieActivity.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MovieActivity.onDestroy()",this);try{mPlayer.onDestroy();
        super.onDestroy();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MovieActivity.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MovieActivity.onDestroy()",this,throwable);throw throwable;}
    }
}
