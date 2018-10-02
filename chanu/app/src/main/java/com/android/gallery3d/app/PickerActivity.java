/*
 * Copyright (C) 2011 The Android Open Source Project
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
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.GLRootView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class PickerActivity extends AbstractGalleryActivity
        implements OnClickListener {

    public static final String KEY_ALBUM_PATH = "album-path";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PickerActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);

        /*// We show the picker in two ways. One smaller screen we use a full*/
        /*// screen window with an action bar. On larger screen we use a dialog.*/
        boolean isDialog = getResources().getBoolean(R.bool.picker_is_dialog);

        if (!isDialog) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR);
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        }

        setContentView(R.layout.dialog_picker);

        if (isDialog) {
            /*// In dialog mode, we don't have the action bar to show the*/
            /*// "cancel" action, so we show an additional "cancel" button.*/
            View view = findViewById(R.id.cancel);
            view.setOnClickListener(this);
            view.setVisibility(View.VISIBLE);

            /*// We need this, otherwise the view will be dimmed because it*/
            /*// is "behind" the dialog.*/
            ((GLRootView) findViewById(R.id.gl_root_view)).setZOrderOnTop(true);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PickerActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PickerActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.PickerActivity.onCreateOptionsMenu(android.view.Menu)",this,menu);try{MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pickup, menu);
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.PickerActivity.onCreateOptionsMenu(android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.PickerActivity.onCreateOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.PickerActivity.onOptionsItemSelected(android.view.MenuItem)",this,item);try{if (item.getItemId() == R.id.action_cancel) {
            finish();
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.PickerActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.PickerActivity.onOptionsItemSelected(android.view.MenuItem)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.PickerActivity.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    @Override
    public void onBackPressed() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PickerActivity.onBackPressed()",this);try{/*// send the back event to the top sub-state*/
        GLRoot root = getGLRoot();
        root.lockRenderThread();
        try {
            getStateManager().getTopState().onBackPressed();
        } finally {
            root.unlockRenderThread();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PickerActivity.onBackPressed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PickerActivity.onBackPressed()",this,throwable);throw throwable;}
    }

    @Override
    public void onClick(View v) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.PickerActivity.onClick(com.android.gallery3d.ui.GLRootView)",this,v);try{if (v.getId() == R.id.cancel) {finish();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.PickerActivity.onClick(com.android.gallery3d.ui.GLRootView)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.PickerActivity.onClick(com.android.gallery3d.ui.GLRootView)",this,throwable);throw throwable;}
    }
}
