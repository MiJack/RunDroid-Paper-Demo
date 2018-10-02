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

package com.chanapps.four.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.gallery3d.app.AlbumPicker;
import com.android.gallery3d.app.CropImage;
import com.android.gallery3d.app.DialogPicker;
import com.android.gallery3d.gadget.WidgetTypeChooser;
import com.chanapps.four.gallery3d.R;

public class PhotoAppWidgetConfigure extends Activity {
    @SuppressWarnings("unused")
    private static final String TAG = "PhotoAppWidgetConfigure";

    public static final String KEY_WIDGET_TYPE = "widget-type";

    private static final int REQUEST_WIDGET_TYPE = 1;
    private static final int REQUEST_CHOOSE_ALBUM = 2;
    private static final int REQUEST_CROP_IMAGE = 3;
    private static final int REQUEST_GET_PHOTO = 4;

    public static final int RESULT_ERROR = RESULT_FIRST_USER;

    /*// Scale up the widget size since we only specified the minimized*/
    /*// size of the gadget. The real size could be larger.*/
    /*// Note: There is also a limit on the size of data that can be*/
    /*// passed in Binder's transaction.*/
    private static float WIDGET_SCALE_FACTOR = 1.5f;
    private static int MAX_WIDGET_SIDE = 360;

    private int mAppWidgetId = -1;
    private int mWidgetType = 0;
    private Uri mPickedItem;

    @Override
    protected void onCreate(Bundle bundle) {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.PhotoAppWidgetConfigure.onCreate(android.os.Bundle)",this,bundle);try{Log.i(TAG, "onCreate called");
        super.onCreate(bundle);
        mAppWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

        if (mAppWidgetId == -1) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.PhotoAppWidgetConfigure.onCreate(android.os.Bundle)",this);return;}
        }

        if (mWidgetType == 0) {
            Intent intent = new Intent(this, WidgetTypeChooser.class);
            startActivityForResult(intent, REQUEST_WIDGET_TYPE);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.PhotoAppWidgetConfigure.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void updateWidgetAndFinish(WidgetDatabaseHelper.Entry entry) {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.PhotoAppWidgetConfigure.updateWidgetAndFinish(WidgetDatabaseHelper.Entry)",this,entry);try{Log.i(TAG, "updateWidgetAndFinish called");
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        RemoteViews views = PhotoAppWidgetProvider.buildWidget(this, mAppWidgetId, entry);
        manager.updateAppWidget(mAppWidgetId, views);
        setResult(RESULT_OK, new Intent().putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));
        finish();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.PhotoAppWidgetConfigure.updateWidgetAndFinish(WidgetDatabaseHelper.Entry)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.PhotoAppWidgetConfigure.updateWidgetAndFinish(WidgetDatabaseHelper.Entry)",this,throwable);throw throwable;}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.PhotoAppWidgetConfigure.onActivityResult(int,int,android.content.Intent)",this,requestCode,resultCode,data);try{if (resultCode != RESULT_OK) {
            setResult(resultCode, new Intent().putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));
            finish();
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.PhotoAppWidgetConfigure.onActivityResult(int,int,android.content.Intent)",this);return;}
        }

        if (requestCode == REQUEST_WIDGET_TYPE) {
            setWidgetType(data);
        } else if (requestCode == REQUEST_CHOOSE_ALBUM) {
            setChoosenAlbum(data);
        } else if (requestCode == REQUEST_GET_PHOTO) {
            setChoosenPhoto(data);
        } else if (requestCode == REQUEST_CROP_IMAGE) {
            setPhotoWidget(data);
        } else {
            throw new AssertionError("unknown request: " + requestCode);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.PhotoAppWidgetConfigure.onActivityResult(int,int,android.content.Intent)",this,throwable);throw throwable;}
    }

    private void setPhotoWidget(Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setPhotoWidget(android.content.Intent)",this,data);try{/*// Store the cropped photo in our database*/
        Bitmap bitmap = (Bitmap) data.getParcelableExtra("data");
        WidgetDatabaseHelper helper = new WidgetDatabaseHelper(this);
        try {
            helper.setPhoto(mAppWidgetId, mPickedItem, bitmap);
            updateWidgetAndFinish(helper.getEntry(mAppWidgetId));
        } finally {
            helper.close();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setPhotoWidget(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setPhotoWidget(android.content.Intent)",this,throwable);throw throwable;}
    }

    private void setChoosenPhoto(Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setChoosenPhoto(android.content.Intent)",this,data);try{Resources res = getResources();

        float width = res.getDimension(R.dimen.appwidget_width);
        float height = res.getDimension(R.dimen.appwidget_height);

        /*// We try to crop a larger image (by scale factor), but there is still*/
        /*// a bound on the binder limit.*/
        float scale = Math.min(WIDGET_SCALE_FACTOR,
                MAX_WIDGET_SIDE / Math.max(width, height));

        int widgetWidth = Math.round(width * scale);
        int widgetHeight = Math.round(height * scale);

        mPickedItem = data.getData();
        Intent request = new Intent(CropImage.ACTION_CROP, mPickedItem)
                .putExtra(CropImage.KEY_OUTPUT_X, widgetWidth)
                .putExtra(CropImage.KEY_OUTPUT_Y, widgetHeight)
                .putExtra(CropImage.KEY_ASPECT_X, widgetWidth)
                .putExtra(CropImage.KEY_ASPECT_Y, widgetHeight)
                .putExtra(CropImage.KEY_SCALE_UP_IF_NEEDED, true)
                .putExtra(CropImage.KEY_SCALE, true)
                .putExtra(CropImage.KEY_RETURN_DATA, true);
        startActivityForResult(request, REQUEST_CROP_IMAGE);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setChoosenPhoto(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setChoosenPhoto(android.content.Intent)",this,throwable);throw throwable;}
    }

    private void setChoosenAlbum(Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setChoosenAlbum(android.content.Intent)",this,data);try{String albumPath = data.getStringExtra(AlbumPicker.KEY_ALBUM_PATH);
        WidgetDatabaseHelper helper = new WidgetDatabaseHelper(this);
        try {
            helper.setWidget(mAppWidgetId,
                    WidgetDatabaseHelper.TYPE_ALBUM, albumPath);
            updateWidgetAndFinish(helper.getEntry(mAppWidgetId));
        } finally {
            helper.close();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setChoosenAlbum(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setChoosenAlbum(android.content.Intent)",this,throwable);throw throwable;}
    }

    private void setWidgetType(Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setWidgetType(android.content.Intent)",this,data);try{mWidgetType = data.getIntExtra(KEY_WIDGET_TYPE, R.id.widget_type_shuffle);
        if (mWidgetType == R.id.widget_type_album) {
            Intent intent = new Intent(this, AlbumPicker.class);
            startActivityForResult(intent, REQUEST_CHOOSE_ALBUM);
        } else if (mWidgetType == R.id.widget_type_shuffle) {
            WidgetDatabaseHelper helper = new WidgetDatabaseHelper(this);
            try {
                helper.setWidget(mAppWidgetId, WidgetDatabaseHelper.TYPE_SHUFFLE, null);
                updateWidgetAndFinish(helper.getEntry(mAppWidgetId));
            } finally {
                helper.close();
            }
        } else {
            /*// Explicitly send the intent to the DialogPhotoPicker*/
            Intent request = new Intent(this, DialogPicker.class)
                    .setAction(Intent.ACTION_GET_CONTENT)
                    .setType("image/*");
            startActivityForResult(request, REQUEST_GET_PHOTO);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setWidgetType(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.PhotoAppWidgetConfigure.setWidgetType(android.content.Intent)",this,throwable);throw throwable;}
    }
}
