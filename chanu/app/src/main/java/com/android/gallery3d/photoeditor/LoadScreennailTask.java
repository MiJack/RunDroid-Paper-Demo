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

package com.android.gallery3d.photoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

import com.chanapps.four.gallery3d.R;

/**
 * Asynchronous task for loading source photo screennail.
 */
public class LoadScreennailTask extends AsyncTask<Uri, Void, Bitmap> {

    /**
     * Callback for the completed asynchronous task.
     */
    public interface Callback {

        void onComplete(Bitmap result);
    }

    private static final int SCREENNAIL_WIDTH = 1280;
    private static final int SCREENNAIL_HEIGHT = 960;

    private final Context context;
    private final Callback callback;

    public LoadScreennailTask(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    /**
     * The task should be executed with one given source photo uri.
     */
    @Override
    protected Bitmap doInBackground(Uri... params) {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.photoeditor.LoadScreennailTask.doInBackground([android.net.Uri)",this,params);try{if (params[0] == null) {
            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.photoeditor.LoadScreennailTask.doInBackground([android.net.Uri)",this);return null;}
        }
        {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.photoeditor.LoadScreennailTask.doInBackground([android.net.Uri)",this);return new BitmapUtils(context).getBitmap(params[0], SCREENNAIL_WIDTH, SCREENNAIL_HEIGHT);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.photoeditor.LoadScreennailTask.doInBackground([android.net.Uri)",this,throwable);throw throwable;}
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.LoadScreennailTask.onPostExecute(android.graphics.Bitmap)",this,result);try{if (result == null) {
            Toast toast = Toast.makeText(context, R.string.loading_failure, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        callback.onComplete(result);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.LoadScreennailTask.onPostExecute(android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.LoadScreennailTask.onPostExecute(android.graphics.Bitmap)",this,throwable);throw throwable;}
    }
}
