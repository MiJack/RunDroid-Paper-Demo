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

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.LocalImage;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.picasasource.PicasaSource;
import com.android.gallery3d.ui.BitmapTileProvider;
import com.android.gallery3d.ui.CropView;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.ui.TileImageViewAdapter;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.InterruptableOutputStream;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The activity can crop specific region of interest from an image.
 */
public class CropImage extends AbstractGalleryActivity {
    private static final String TAG = "CropImage";
    public static final String ACTION_CROP = "com.android.camera.action.CROP";

    private static final int MAX_PIXEL_COUNT = 5 * 1000000; /*// 5M pixels*/
    private static final int MAX_FILE_INDEX = 1000;
    private static final int TILE_SIZE = 512;
    private static final int BACKUP_PIXEL_COUNT = 480000; /*// around 800x600*/

    private static final int MSG_LARGE_BITMAP = 1;
    private static final int MSG_BITMAP = 2;
    private static final int MSG_SAVE_COMPLETE = 3;
    private static final int MSG_SHOW_SAVE_ERROR = 4;

    private static final int MAX_BACKUP_IMAGE_SIZE = 320;
    private static final int DEFAULT_COMPRESS_QUALITY = 90;
    private static final String TIME_STAMP_NAME = "'IMG'_yyyyMMdd_HHmmss";

    /*// Change these to Images.Media.WIDTH/HEIGHT after they are unhidden.*/
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";

    public static final String KEY_RETURN_DATA = "return-data";
    public static final String KEY_CROPPED_RECT = "cropped-rect";
    public static final String KEY_ASPECT_X = "aspectX";
    public static final String KEY_ASPECT_Y = "aspectY";
    public static final String KEY_SPOTLIGHT_X = "spotlightX";
    public static final String KEY_SPOTLIGHT_Y = "spotlightY";
    public static final String KEY_OUTPUT_X = "outputX";
    public static final String KEY_OUTPUT_Y = "outputY";
    public static final String KEY_SCALE = "scale";
    public static final String KEY_DATA = "data";
    public static final String KEY_SCALE_UP_IF_NEEDED = "scaleUpIfNeeded";
    public static final String KEY_OUTPUT_FORMAT = "outputFormat";
    public static final String KEY_SET_AS_WALLPAPER = "set-as-wallpaper";
    public static final String KEY_NO_FACE_DETECTION = "noFaceDetection";

    private static final String KEY_STATE = "state";

    private static final int STATE_INIT = 0;
    private static final int STATE_LOADED = 1;
    private static final int STATE_SAVING = 2;

    public static final String DOWNLOAD_STRING = "download";
    public static final File DOWNLOAD_BUCKET = new File(
            Environment.getExternalStorageDirectory(), DOWNLOAD_STRING);

    public static final String CROP_ACTION = "com.android.camera.action.CROP";

    private int mState = STATE_INIT;

    private CropView mCropView;

    private boolean mDoFaceDetection = true;

    private Handler mMainHandler;

    /*// We keep the following members so that we can free them*/

    /*// mBitmap is the unrotated bitmap we pass in to mCropView for detect faces.*/
    /*// mCropView is responsible for rotating it to the way that it is viewed by users.*/
    private Bitmap mBitmap;
    private BitmapTileProvider mBitmapTileProvider;
    private BitmapRegionDecoder mRegionDecoder;
    private Bitmap mBitmapInIntent;
    private boolean mUseRegionDecoder = false;

    private ProgressDialog mProgressDialog;
    private Future<BitmapRegionDecoder> mLoadTask;
    private Future<Bitmap> mLoadBitmapTask;
    private Future<Intent> mSaveTask;

    private MediaItem mMediaItem;

    @Override
    public void onCreate(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage.onCreate(android.os.Bundle)",this,bundle);try{super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        /*// Initialize UI*/
        setContentView(R.layout.cropimage);
        mCropView = new CropView(this);
        getGLRoot().setContentPane(mCropView);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
                ActionBar.DISPLAY_HOME_AS_UP);

        mMainHandler = new SynchronizedHandler(getGLRoot()) {
            @Override
            public void handleMessage(Message message) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage$1.handleMessage(android.os.Message)",this,message);try{switch (message.what) {
                    case MSG_LARGE_BITMAP: {
                        mProgressDialog.dismiss();
                        onBitmapRegionDecoderAvailable((BitmapRegionDecoder) message.obj);
                        break;
                    }
                    case MSG_BITMAP: {
                        mProgressDialog.dismiss();
                        onBitmapAvailable((Bitmap) message.obj);
                        break;
                    }
                    case MSG_SHOW_SAVE_ERROR: {
                        mProgressDialog.dismiss();
                        setResult(RESULT_CANCELED);
                        Toast.makeText(CropImage.this,
                                CropImage.this.getString(R.string.save_error),
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                    case MSG_SAVE_COMPLETE: {
                        mProgressDialog.dismiss();
                        setResult(RESULT_OK, (Intent) message.obj);
                        finish();
                        break;
                    }
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage$1.handleMessage(android.os.Message)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage$1.handleMessage(android.os.Message)",this,throwable);throw throwable;}
            }
        };

        setCropParameters();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage.onSaveInstanceState(android.os.Bundle)",this,saveState);try{saveState.putInt(KEY_STATE, mState);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.CropImage.onCreateOptionsMenu(android.view.Menu)",this,menu);try{super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.crop, menu);
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.CropImage.onCreateOptionsMenu(android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.CropImage.onCreateOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.CropImage.onOptionsItemSelected(android.view.MenuItem)",this,item);try{if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.cancel) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (item.getItemId() == R.id.save) {
            onSaveClicked();
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.CropImage.onOptionsItemSelected(android.view.MenuItem)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.CropImage.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    private class SaveOutput implements Job<Intent> {
        private final RectF mCropRect;

        public SaveOutput(RectF cropRect) {
            mCropRect = cropRect;
        }

        public Intent run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("android.content.Intent com.android.gallery3d.app.CropImage$SaveOutput.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{RectF cropRect = mCropRect;
            Bundle extra = getIntent().getExtras();

            Rect rect = new Rect(
                    Math.round(cropRect.left), Math.round(cropRect.top),
                    Math.round(cropRect.right), Math.round(cropRect.bottom));

            Intent result = new Intent();
            result.putExtra(KEY_CROPPED_RECT, rect);
            Bitmap cropped = null;
            boolean outputted = false;
            if (extra != null) {
                Uri uri = (Uri) extra.getParcelable(MediaStore.EXTRA_OUTPUT);
                if (uri != null) {
                    if (jc.isCancelled()) {{com.mijack.Xlog.logMethodExit("android.content.Intent com.android.gallery3d.app.CropImage$SaveOutput.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
                    outputted = true;
                    cropped = getCroppedImage(rect);
                    if (!saveBitmapToUri(jc, cropped, uri)) {{com.mijack.Xlog.logMethodExit("android.content.Intent com.android.gallery3d.app.CropImage$SaveOutput.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
                }
                if (extra.getBoolean(KEY_RETURN_DATA, false)) {
                    if (jc.isCancelled()) {{com.mijack.Xlog.logMethodExit("android.content.Intent com.android.gallery3d.app.CropImage$SaveOutput.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
                    outputted = true;
                    if (cropped == null) {cropped = getCroppedImage(rect);}
                    result.putExtra(KEY_DATA, cropped);
                }
                if (extra.getBoolean(KEY_SET_AS_WALLPAPER, false)) {
                    if (jc.isCancelled()) {{com.mijack.Xlog.logMethodExit("android.content.Intent com.android.gallery3d.app.CropImage$SaveOutput.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
                    outputted = true;
                    if (cropped == null) {cropped = getCroppedImage(rect);}
                    if (!setAsWallpaper(jc, cropped)) {{com.mijack.Xlog.logMethodExit("android.content.Intent com.android.gallery3d.app.CropImage$SaveOutput.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
                }
            }
            if (!outputted) {
                if (jc.isCancelled()) {{com.mijack.Xlog.logMethodExit("android.content.Intent com.android.gallery3d.app.CropImage$SaveOutput.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
                if (cropped == null) {cropped = getCroppedImage(rect);}
                Uri data = saveToMediaProvider(jc, cropped);
                if (data != null) {result.setData(data);}
            }
            {com.mijack.Xlog.logMethodExit("android.content.Intent com.android.gallery3d.app.CropImage$SaveOutput.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.Intent com.android.gallery3d.app.CropImage$SaveOutput.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }

    public static String determineCompressFormat(MediaObject obj) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.app.CropImage.determineCompressFormat(com.android.gallery3d.data.MediaObject)",obj);try{String compressFormat = "JPEG";
        if (obj instanceof MediaItem) {
            String mime = ((MediaItem) obj).getMimeType();
            if (mime.contains("png") || mime.contains("gif")) {
              /*// Set the compress format to PNG for png and gif images*/
              /*// because they may contain alpha values.*/
              compressFormat = "PNG";
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.app.CropImage.determineCompressFormat(com.android.gallery3d.data.MediaObject)");return compressFormat;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.app.CropImage.determineCompressFormat(com.android.gallery3d.data.MediaObject)",throwable);throw throwable;}
    }

    private boolean setAsWallpaper(JobContext jc, Bitmap wallpaper) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.CropImage.setAsWallpaper(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this,jc,wallpaper);try{try {
            WallpaperManager.getInstance(this).setBitmap(wallpaper);
        } catch (IOException e) {
            Log.w(TAG, "fail to set wall paper", e);
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.CropImage.setAsWallpaper(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.CropImage.setAsWallpaper(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    private File saveMedia(
            JobContext jc, Bitmap cropped, File directory, String filename) {
        com.mijack.Xlog.logMethodEnter("java.io.File com.android.gallery3d.app.CropImage.saveMedia(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,java.io.File,java.lang.String)",this,jc,cropped,directory,filename);try{/*// Try file-1.jpg, file-2.jpg, ... until we find a filename*/
        /*// which does not exist yet.*/
        File candidate = null;
        String fileExtension = getFileExtension();
        for (int i = 1; i < MAX_FILE_INDEX; ++i) {
            candidate = new File(directory, filename + "-" + i + "."
                    + fileExtension);
            try {
                if (candidate.createNewFile()) {break;}
            } catch (IOException e) {
                Log.e(TAG, "fail to create new file: "
                        + candidate.getAbsolutePath(), e);
                {com.mijack.Xlog.logMethodExit("java.io.File com.android.gallery3d.app.CropImage.saveMedia(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,java.io.File,java.lang.String)",this);return null;}
            }
        }
        if (!candidate.exists() || !candidate.isFile()) {
            throw new RuntimeException("cannot create file: " + filename);
        }

        candidate.setReadable(true, false);
        candidate.setWritable(true, false);

        try {
            FileOutputStream fos = new FileOutputStream(candidate);
            try {
                saveBitmapToOutputStream(jc, cropped,
                        convertExtensionToCompressFormat(fileExtension), fos);
            } finally {
                fos.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "fail to save image: "
                    + candidate.getAbsolutePath(), e);
            candidate.delete();
            {com.mijack.Xlog.logMethodExit("java.io.File com.android.gallery3d.app.CropImage.saveMedia(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,java.io.File,java.lang.String)",this);return null;}
        }

        if (jc.isCancelled()) {
            candidate.delete();
            {com.mijack.Xlog.logMethodExit("java.io.File com.android.gallery3d.app.CropImage.saveMedia(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,java.io.File,java.lang.String)",this);return null;}
        }

        {com.mijack.Xlog.logMethodExit("java.io.File com.android.gallery3d.app.CropImage.saveMedia(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,java.io.File,java.lang.String)",this);return candidate;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.io.File com.android.gallery3d.app.CropImage.saveMedia(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,java.io.File,java.lang.String)",this,throwable);throw throwable;}
    }

    private Uri saveToMediaProvider(JobContext jc, Bitmap cropped) {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.app.CropImage.saveToMediaProvider(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this,jc,cropped);try{if (PicasaSource.isPicasaImage(mMediaItem)) {
            {com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.app.CropImage.saveToMediaProvider(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this);return savePicasaImage(jc, cropped);}
        } else if (mMediaItem instanceof LocalImage) {
            {com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.app.CropImage.saveToMediaProvider(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this);return saveLocalImage(jc, cropped);}
        } else {
            {com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.app.CropImage.saveToMediaProvider(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this);return saveGenericImage(jc, cropped);}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.app.CropImage.saveToMediaProvider(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    private Uri savePicasaImage(JobContext jc, Bitmap cropped) {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.app.CropImage.savePicasaImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this,jc,cropped);try{if (!DOWNLOAD_BUCKET.isDirectory() && !DOWNLOAD_BUCKET.mkdirs()) {
            throw new RuntimeException("cannot create download folder");
        }

        String filename = PicasaSource.getImageTitle(mMediaItem);
        int pos = filename.lastIndexOf('.');
        if (pos >= 0) {filename = filename.substring(0, pos);}
        File output = saveMedia(jc, cropped, DOWNLOAD_BUCKET, filename);
        if (output == null) {{com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.app.CropImage.savePicasaImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this);return null;}}

        copyExif(mMediaItem, output.getAbsolutePath(), cropped.getWidth(), cropped.getHeight());

        long now = System.currentTimeMillis() / 1000;
        ContentValues values = new ContentValues();
        values.put(Images.Media.TITLE, PicasaSource.getImageTitle(mMediaItem));
        values.put(Images.Media.DISPLAY_NAME, output.getName());
        values.put(Images.Media.DATE_TAKEN, PicasaSource.getDateTaken(mMediaItem));
        values.put(Images.Media.DATE_MODIFIED, now);
        values.put(Images.Media.DATE_ADDED, now);
        values.put(Images.Media.MIME_TYPE, getOutputMimeType());
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, output.getAbsolutePath());
        values.put(Images.Media.SIZE, output.length());
        values.put(WIDTH, cropped.getWidth());
        values.put(HEIGHT, cropped.getHeight());

        double latitude = PicasaSource.getLatitude(mMediaItem);
        double longitude = PicasaSource.getLongitude(mMediaItem);
        if (GalleryUtils.isValidLocation(latitude, longitude)) {
            values.put(Images.Media.LATITUDE, latitude);
            values.put(Images.Media.LONGITUDE, longitude);
        }
        {com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.app.CropImage.savePicasaImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this);return getContentResolver().insert(
                Images.Media.EXTERNAL_CONTENT_URI, values);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.app.CropImage.savePicasaImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    private Uri saveLocalImage(JobContext jc, Bitmap cropped) {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.app.CropImage.saveLocalImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this,jc,cropped);try{LocalImage localImage = (LocalImage) mMediaItem;

        File oldPath = new File(localImage.filePath);
        File directory = new File(oldPath.getParent());

        String filename = oldPath.getName();
        int pos = filename.lastIndexOf('.');
        if (pos >= 0) {filename = filename.substring(0, pos);}
        File output = saveMedia(jc, cropped, directory, filename);
        if (output == null) {{com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.app.CropImage.saveLocalImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this);return null;}}

        copyExif(oldPath.getAbsolutePath(), output.getAbsolutePath(),
                cropped.getWidth(), cropped.getHeight());

        long now = System.currentTimeMillis() / 1000;
        ContentValues values = new ContentValues();
        values.put(Images.Media.TITLE, localImage.caption);
        values.put(Images.Media.DISPLAY_NAME, output.getName());
        values.put(Images.Media.DATE_TAKEN, localImage.dateTakenInMs);
        values.put(Images.Media.DATE_MODIFIED, now);
        values.put(Images.Media.DATE_ADDED, now);
        values.put(Images.Media.MIME_TYPE, getOutputMimeType());
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, output.getAbsolutePath());
        values.put(Images.Media.SIZE, output.length());
        values.put(WIDTH, cropped.getWidth());
        values.put(HEIGHT, cropped.getHeight());

        if (GalleryUtils.isValidLocation(localImage.latitude, localImage.longitude)) {
            values.put(Images.Media.LATITUDE, localImage.latitude);
            values.put(Images.Media.LONGITUDE, localImage.longitude);
        }
        {com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.app.CropImage.saveLocalImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this);return getContentResolver().insert(
                Images.Media.EXTERNAL_CONTENT_URI, values);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.app.CropImage.saveLocalImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    private Uri saveGenericImage(JobContext jc, Bitmap cropped) {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.app.CropImage.saveGenericImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this,jc,cropped);try{if (!DOWNLOAD_BUCKET.isDirectory() && !DOWNLOAD_BUCKET.mkdirs()) {
            throw new RuntimeException("cannot create download folder");
        }

        long now = System.currentTimeMillis();
        String filename = new SimpleDateFormat(TIME_STAMP_NAME).
                format(new Date(now));

        File output = saveMedia(jc, cropped, DOWNLOAD_BUCKET, filename);
        if (output == null) {{com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.app.CropImage.saveGenericImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this);return null;}}

        ContentValues values = new ContentValues();
        values.put(Images.Media.TITLE, filename);
        values.put(Images.Media.DISPLAY_NAME, output.getName());
        values.put(Images.Media.DATE_TAKEN, now);
        values.put(Images.Media.DATE_MODIFIED, now / 1000);
        values.put(Images.Media.DATE_ADDED, now / 1000);
        values.put(Images.Media.MIME_TYPE, getOutputMimeType());
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, output.getAbsolutePath());
        values.put(Images.Media.SIZE, output.length());
        values.put(WIDTH, cropped.getWidth());
        values.put(HEIGHT, cropped.getHeight());

        {com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.app.CropImage.saveGenericImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this);return getContentResolver().insert(
                Images.Media.EXTERNAL_CONTENT_URI, values);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.app.CropImage.saveGenericImage(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    private boolean saveBitmapToOutputStream(
            JobContext jc, Bitmap bitmap, CompressFormat format, OutputStream os) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.CropImage.saveBitmapToOutputStream(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,android.graphics.Bitmap.CompressFormat,com.android.gallery3d.util.InterruptableOutputStream)",this,jc,bitmap,format,os);try{/*// We wrap the OutputStream so that it can be interrupted.*/
        final InterruptableOutputStream ios = new InterruptableOutputStream(os);
        jc.setCancelListener(new CancelListener() {
                public void onCancel() {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage$2.onCancel()",this);try{ios.interrupt();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage$2.onCancel()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage$2.onCancel()",this,throwable);throw throwable;}
                }
            });
        try {
            bitmap.compress(format, DEFAULT_COMPRESS_QUALITY, os);
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.CropImage.saveBitmapToOutputStream(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,android.graphics.Bitmap.CompressFormat,com.android.gallery3d.util.InterruptableOutputStream)",this);return !jc.isCancelled();}
        } finally {
            jc.setCancelListener(null);
            Utils.closeSilently(os);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.CropImage.saveBitmapToOutputStream(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,android.graphics.Bitmap.CompressFormat,com.android.gallery3d.util.InterruptableOutputStream)",this,throwable);throw throwable;}
    }

    private boolean saveBitmapToUri(JobContext jc, Bitmap bitmap, Uri uri) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.CropImage.saveBitmapToUri(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,android.net.Uri)",this,jc,bitmap,uri);try{try {
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.CropImage.saveBitmapToUri(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,android.net.Uri)",this);return saveBitmapToOutputStream(jc, bitmap,
                    convertExtensionToCompressFormat(getFileExtension()),
                    getContentResolver().openOutputStream(uri));}
        } catch (FileNotFoundException e) {
            Log.w(TAG, "cannot write output", e);
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.CropImage.saveBitmapToUri(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,android.net.Uri)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.CropImage.saveBitmapToUri(com.android.gallery3d.util.ThreadPool.JobContext,android.graphics.Bitmap,android.net.Uri)",this,throwable);throw throwable;}
    }

    private CompressFormat convertExtensionToCompressFormat(String extension) {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap.CompressFormat com.android.gallery3d.app.CropImage.convertExtensionToCompressFormat(java.lang.String)",this,extension);try{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap.CompressFormat com.android.gallery3d.app.CropImage.convertExtensionToCompressFormat(java.lang.String)",this);return extension.equals("png")
                ? CompressFormat.PNG
                : CompressFormat.JPEG;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap.CompressFormat com.android.gallery3d.app.CropImage.convertExtensionToCompressFormat(java.lang.String)",this,throwable);throw throwable;}
    }

    private String getOutputMimeType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.app.CropImage.getOutputMimeType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.app.CropImage.getOutputMimeType()",this);return getFileExtension().equals("png") ? "image/png" : "image/jpeg";}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.app.CropImage.getOutputMimeType()",this,throwable);throw throwable;}
    }

    private String getFileExtension() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.app.CropImage.getFileExtension()",this);try{String requestFormat = getIntent().getStringExtra(KEY_OUTPUT_FORMAT);
        String outputFormat = (requestFormat == null)
                ? determineCompressFormat(mMediaItem)
                : requestFormat;

        outputFormat = outputFormat.toLowerCase();
        {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.app.CropImage.getFileExtension()",this);return (outputFormat.equals("png") || outputFormat.equals("gif"))
                ? "png" /*// We don't support gif compression.*/
                : "jpg";}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.app.CropImage.getFileExtension()",this,throwable);throw throwable;}
    }

    private void onSaveClicked() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage.onSaveClicked()",this);try{Bundle extra = getIntent().getExtras();
        RectF cropRect = mCropView.getCropRectangle();
        if (cropRect == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.onSaveClicked()",this);return;}}
        mState = STATE_SAVING;
        int messageId = extra != null && extra.getBoolean(KEY_SET_AS_WALLPAPER)
                ? R.string.wallpaper
                : R.string.saving_image;
        mProgressDialog = ProgressDialog.show(
                this, null, getString(messageId), true, false);
        mSaveTask = getThreadPool().submit(new SaveOutput(cropRect),
                new FutureListener<Intent>() {
            public void onFutureDone(Future<Intent> future) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage$3.onFutureDone(com.android.gallery3d.util.Future)",this,future);try{mSaveTask = null;
                if (future.isCancelled()) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.onSaveClicked()",this);{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage$3.onFutureDone(com.android.gallery3d.util.Future)",this);return;}}}
                Intent intent = future.get();
                if (intent != null) {
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(
                            MSG_SAVE_COMPLETE, intent));
                } else {
                    mMainHandler.sendEmptyMessage(MSG_SHOW_SAVE_ERROR);
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage$3.onFutureDone(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.onSaveClicked()",this,throwable);throw throwable;}
    }

    private Bitmap getCroppedImage(Rect rect) {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.app.CropImage.getCroppedImage(android.graphics.Rect)",this,rect);try{Utils.assertTrue(rect.width() > 0 && rect.height() > 0);

        Bundle extras = getIntent().getExtras();
        /*// (outputX, outputY) = the width and height of the returning bitmap.*/
        int outputX = rect.width();
        int outputY = rect.height();
        if (extras != null) {
            outputX = extras.getInt(KEY_OUTPUT_X, outputX);
            outputY = extras.getInt(KEY_OUTPUT_Y, outputY);
        }

        if (outputX * outputY > MAX_PIXEL_COUNT) {
            float scale = (float) Math.sqrt(
                    (double) MAX_PIXEL_COUNT / outputX / outputY);
            Log.w(TAG, "scale down the cropped image: " + scale);
            outputX = Math.round(scale * outputX);
            outputY = Math.round(scale * outputY);
        }

        /*// (rect.width() * scaleX, rect.height() * scaleY) =*/
        /*// the size of drawing area in output bitmap*/
        float scaleX = 1;
        float scaleY = 1;
        Rect dest = new Rect(0, 0, outputX, outputY);
        if (extras == null || extras.getBoolean(KEY_SCALE, true)) {
            scaleX = (float) outputX / rect.width();
            scaleY = (float) outputY / rect.height();
            if (extras == null || !extras.getBoolean(
                    KEY_SCALE_UP_IF_NEEDED, false)) {
                if (scaleX > 1f) {scaleX = 1;}
                if (scaleY > 1f) {scaleY = 1;}
            }
        }

        /*// Keep the content in the center (or crop the content)*/
        int rectWidth = Math.round(rect.width() * scaleX);
        int rectHeight = Math.round(rect.height() * scaleY);
        dest.set(Math.round((outputX - rectWidth) / 2f),
                Math.round((outputY - rectHeight) / 2f),
                Math.round((outputX + rectWidth) / 2f),
                Math.round((outputY + rectHeight) / 2f));

        if (mBitmapInIntent != null) {
            Bitmap source = mBitmapInIntent;
            Bitmap result = Bitmap.createBitmap(
                    outputX, outputY, Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(source, rect, dest, null);
            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.app.CropImage.getCroppedImage(android.graphics.Rect)",this);return result;}
        }

        if (mUseRegionDecoder) {
            int rotation = mMediaItem.getFullImageRotation();
            rotateRectangle(rect, mCropView.getImageWidth(),
                    mCropView.getImageHeight(), 360 - rotation);
            rotateRectangle(dest, outputX, outputY, 360 - rotation);

            BitmapFactory.Options options = new BitmapFactory.Options();
            int sample = BitmapUtils.computeSampleSizeLarger(
                    Math.max(scaleX, scaleY));
            options.inSampleSize = sample;
            if ((rect.width() / sample) == dest.width()
                    && (rect.height() / sample) == dest.height()
                    && rotation == 0) {
                /*// To prevent concurrent access in GLThread*/
                synchronized (mRegionDecoder) {
                    {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.app.CropImage.getCroppedImage(android.graphics.Rect)",this);return mRegionDecoder.decodeRegion(rect, options);}
                }
            }
            Bitmap result = Bitmap.createBitmap(
                    outputX, outputY, Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            rotateCanvas(canvas, outputX, outputY, rotation);
            drawInTiles(canvas, mRegionDecoder, rect, dest, sample);
            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.app.CropImage.getCroppedImage(android.graphics.Rect)",this);return result;}
        } else {
            int rotation = mMediaItem.getRotation();
            rotateRectangle(rect, mCropView.getImageWidth(),
                    mCropView.getImageHeight(), 360 - rotation);
            rotateRectangle(dest, outputX, outputY, 360 - rotation);
            Bitmap result = Bitmap.createBitmap(outputX, outputY, Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            rotateCanvas(canvas, outputX, outputY, rotation);
            canvas.drawBitmap(mBitmap,
                    rect, dest, new Paint(Paint.FILTER_BITMAP_FLAG));
            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.app.CropImage.getCroppedImage(android.graphics.Rect)",this);return result;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.app.CropImage.getCroppedImage(android.graphics.Rect)",this,throwable);throw throwable;}
    }

    private static void rotateCanvas(
            Canvas canvas, int width, int height, int rotation) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.app.CropImage.rotateCanvas(android.graphics.Canvas,int,int,int)",canvas,width,height,rotation);try{canvas.translate(width / 2, height / 2);
        canvas.rotate(rotation);
        if (((rotation / 90) & 0x01) == 0) {
            canvas.translate(-width / 2, -height / 2);
        } else {
            canvas.translate(-height / 2, -width / 2);
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.app.CropImage.rotateCanvas(android.graphics.Canvas,int,int,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.rotateCanvas(android.graphics.Canvas,int,int,int)",throwable);throw throwable;}
    }

    private static void rotateRectangle(
            Rect rect, int width, int height, int rotation) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.app.CropImage.rotateRectangle(android.graphics.Rect,int,int,int)",rect,width,height,rotation);try{if (rotation == 0 || rotation == 360) {{com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.app.CropImage.rotateRectangle(android.graphics.Rect,int,int,int)");return;}}

        int w = rect.width();
        int h = rect.height();
        switch (rotation) {
            case 90: {
                rect.top = rect.left;
                rect.left = height - rect.bottom;
                rect.right = rect.left + h;
                rect.bottom = rect.top + w;
                {com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.app.CropImage.rotateRectangle(android.graphics.Rect,int,int,int)");return;}
            }
            case 180: {
                rect.left = width - rect.right;
                rect.top = height - rect.bottom;
                rect.right = rect.left + w;
                rect.bottom = rect.top + h;
                {com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.app.CropImage.rotateRectangle(android.graphics.Rect,int,int,int)");return;}
            }
            case 270: {
                rect.left = rect.top;
                rect.top = width - rect.right;
                rect.right = rect.left + h;
                rect.bottom = rect.top + w;
                {com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.app.CropImage.rotateRectangle(android.graphics.Rect,int,int,int)");return;}
            }
            default: throw new AssertionError();
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.rotateRectangle(android.graphics.Rect,int,int,int)",throwable);throw throwable;}
    }

    private void drawInTiles(Canvas canvas,
            BitmapRegionDecoder decoder, Rect rect, Rect dest, int sample) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage.drawInTiles(android.graphics.Canvas,android.graphics.BitmapRegionDecoder,android.graphics.Rect,android.graphics.Rect,int)",this,canvas,decoder,rect,dest,sample);try{int tileSize = TILE_SIZE * sample;
        Rect tileRect = new Rect();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inSampleSize = sample;
        canvas.translate(dest.left, dest.top);
        canvas.scale((float) sample * dest.width() / rect.width(),
                (float) sample * dest.height() / rect.height());
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        for (int tx = rect.left, x = 0;
                tx < rect.right; tx += tileSize, x += TILE_SIZE) {
            for (int ty = rect.top, y = 0;
                    ty < rect.bottom; ty += tileSize, y += TILE_SIZE) {
                tileRect.set(tx, ty, tx + tileSize, ty + tileSize);
                if (tileRect.intersect(rect)) {
                    Bitmap bitmap;

                    /*// To prevent concurrent access in GLThread*/
                    synchronized (decoder) {
                        bitmap = decoder.decodeRegion(tileRect, options);
                    }
                    canvas.drawBitmap(bitmap, x, y, paint);
                    bitmap.recycle();
                }
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.drawInTiles(android.graphics.Canvas,android.graphics.BitmapRegionDecoder,android.graphics.Rect,android.graphics.Rect,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.drawInTiles(android.graphics.Canvas,android.graphics.BitmapRegionDecoder,android.graphics.Rect,android.graphics.Rect,int)",this,throwable);throw throwable;}
    }

    private void onBitmapRegionDecoderAvailable(
            BitmapRegionDecoder regionDecoder) {

        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage.onBitmapRegionDecoderAvailable(android.graphics.BitmapRegionDecoder)",this,regionDecoder);try{if (regionDecoder == null) {
            Toast.makeText(this, "fail to load image", Toast.LENGTH_SHORT).show();
            finish();
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.onBitmapRegionDecoderAvailable(android.graphics.BitmapRegionDecoder)",this);return;}
        }
        mRegionDecoder = regionDecoder;
        mUseRegionDecoder = true;
        mState = STATE_LOADED;

        BitmapFactory.Options options = new BitmapFactory.Options();
        int width = regionDecoder.getWidth();
        int height = regionDecoder.getHeight();
        options.inSampleSize = BitmapUtils.computeSampleSize(width, height,
                BitmapUtils.UNCONSTRAINED, BACKUP_PIXEL_COUNT);
        mBitmap = regionDecoder.decodeRegion(
                new Rect(0, 0, width, height), options);
        mCropView.setDataModel(new TileImageViewAdapter(
                mBitmap, regionDecoder), mMediaItem.getFullImageRotation());
        if (mDoFaceDetection) {
            mCropView.detectFaces(mBitmap);
        } else {
            mCropView.initializeHighlightRectangle();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.onBitmapRegionDecoderAvailable(android.graphics.BitmapRegionDecoder)",this,throwable);throw throwable;}
    }

    private void onBitmapAvailable(Bitmap bitmap) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage.onBitmapAvailable(android.graphics.Bitmap)",this,bitmap);try{if (bitmap == null) {
            Toast.makeText(this, "fail to load image", Toast.LENGTH_SHORT).show();
            finish();
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.onBitmapAvailable(android.graphics.Bitmap)",this);return;}
        }
        mUseRegionDecoder = false;
        mState = STATE_LOADED;

        mBitmap = bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        mCropView.setDataModel(new BitmapTileProvider(bitmap, 512),
                mMediaItem.getRotation());
        if (mDoFaceDetection) {
            mCropView.detectFaces(bitmap);
        } else {
            mCropView.initializeHighlightRectangle();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.onBitmapAvailable(android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    private void setCropParameters() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage.setCropParameters()",this);try{Bundle extras = getIntent().getExtras();
        if (extras == null)
            {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.setCropParameters()",this);return;}}
        int aspectX = extras.getInt(KEY_ASPECT_X, 0);
        int aspectY = extras.getInt(KEY_ASPECT_Y, 0);
        if (aspectX != 0 && aspectY != 0) {
            mCropView.setAspectRatio((float) aspectX / aspectY);
        }

        float spotlightX = extras.getFloat(KEY_SPOTLIGHT_X, 0);
        float spotlightY = extras.getFloat(KEY_SPOTLIGHT_Y, 0);
        if (spotlightX != 0 && spotlightY != 0) {
            mCropView.setSpotlightRatio(spotlightX, spotlightY);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.setCropParameters()",this,throwable);throw throwable;}
    }

    private void initializeData() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage.initializeData()",this);try{Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(KEY_NO_FACE_DETECTION)) {
                mDoFaceDetection = !extras.getBoolean(KEY_NO_FACE_DETECTION);
            }

            mBitmapInIntent = extras.getParcelable(KEY_DATA);

            if (mBitmapInIntent != null) {
                mBitmapTileProvider =
                        new BitmapTileProvider(mBitmapInIntent, MAX_BACKUP_IMAGE_SIZE);
                mCropView.setDataModel(mBitmapTileProvider, 0);
                if (mDoFaceDetection) {
                    mCropView.detectFaces(mBitmapInIntent);
                } else {
                    mCropView.initializeHighlightRectangle();
                }
                mState = STATE_LOADED;
                {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.initializeData()",this);return;}
            }
        }

        mProgressDialog = ProgressDialog.show(
                this, null, getString(R.string.loading_image), true, false);

        mMediaItem = getMediaItemFromIntentData();
        if (mMediaItem == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.initializeData()",this);return;}}

        boolean supportedByBitmapRegionDecoder =
            (mMediaItem.getSupportedOperations() & MediaItem.SUPPORT_FULL_IMAGE) != 0;
        if (supportedByBitmapRegionDecoder) {
            mLoadTask = getThreadPool().submit(new LoadDataTask(mMediaItem),
                    new FutureListener<BitmapRegionDecoder>() {
                public void onFutureDone(Future<BitmapRegionDecoder> future) {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage$4.onFutureDone(com.android.gallery3d.util.Future)",this,future);try{mLoadTask = null;
                    BitmapRegionDecoder decoder = future.get();
                    if (future.isCancelled()) {
                        if (decoder != null) {decoder.recycle();}
                        {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.initializeData()",this);{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage$4.onFutureDone(com.android.gallery3d.util.Future)",this);return;}}
                    }
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(
                            MSG_LARGE_BITMAP, decoder));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage$4.onFutureDone(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
                }
            });
        } else {
            mLoadBitmapTask = getThreadPool().submit(new LoadBitmapDataTask(mMediaItem),
                    new FutureListener<Bitmap>() {
                public void onFutureDone(Future<Bitmap> future) {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage$5.onFutureDone(com.android.gallery3d.util.Future)",this,future);try{mLoadBitmapTask = null;
                    Bitmap bitmap = future.get();
                    if (future.isCancelled()) {
                        if (bitmap != null) {bitmap.recycle();}
                        {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.initializeData()",this);{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage$5.onFutureDone(com.android.gallery3d.util.Future)",this);return;}}
                    }
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(
                            MSG_BITMAP, bitmap));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage$5.onFutureDone(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
                }
            });
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.initializeData()",this,throwable);throw throwable;}
    }

    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage.onResume()",this);try{super.onResume();
        if (mState == STATE_INIT) {initializeData();}
        if (mState == STATE_SAVING) {onSaveClicked();}

        /*// TODO: consider to do it in GLView system*/
        GLRoot root = getGLRoot();
        root.lockRenderThread();
        try {
            mCropView.resume();
        } finally {
            root.unlockRenderThread();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.onResume()",this,throwable);throw throwable;}
    }

    @Override
    protected void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.CropImage.onPause()",this);try{super.onPause();

        Future<BitmapRegionDecoder> loadTask = mLoadTask;
        if (loadTask != null && !loadTask.isDone()) {
            /*// load in progress, try to cancel it*/
            loadTask.cancel();
            loadTask.waitDone();
            mProgressDialog.dismiss();
        }

        Future<Bitmap> loadBitmapTask = mLoadBitmapTask;
        if (loadBitmapTask != null && !loadBitmapTask.isDone()) {
            /*// load in progress, try to cancel it*/
            loadBitmapTask.cancel();
            loadBitmapTask.waitDone();
            mProgressDialog.dismiss();
        }

        Future<Intent> saveTask = mSaveTask;
        if (saveTask != null && !saveTask.isDone()) {
            /*// save in progress, try to cancel it*/
            saveTask.cancel();
            saveTask.waitDone();
            mProgressDialog.dismiss();
        }
        GLRoot root = getGLRoot();
        root.lockRenderThread();
        try {
            mCropView.pause();
        } finally {
            root.unlockRenderThread();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.CropImage.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.onPause()",this,throwable);throw throwable;}
    }

    private MediaItem getMediaItemFromIntentData() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.CropImage.getMediaItemFromIntentData()",this);try{Uri uri = getIntent().getData();
        DataManager manager = getDataManager();
        if (uri == null) {
            Log.w(TAG, "no data given");
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.CropImage.getMediaItemFromIntentData()",this);return null;}
        }
        Path path = manager.findPathByUri(uri);
        if (path == null) {
            Log.w(TAG, "cannot get path for: " + uri);
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.CropImage.getMediaItemFromIntentData()",this);return null;}
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.CropImage.getMediaItemFromIntentData()",this);return (MediaItem) manager.getMediaObject(path);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.CropImage.getMediaItemFromIntentData()",this,throwable);throw throwable;}
    }

    private class LoadDataTask implements Job<BitmapRegionDecoder> {
        MediaItem mItem;

        public LoadDataTask(MediaItem item) {
            mItem = item;
        }

        public BitmapRegionDecoder run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("android.graphics.BitmapRegionDecoder com.android.gallery3d.app.CropImage$LoadDataTask.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{com.mijack.Xlog.logMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.app.CropImage$LoadDataTask.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return mItem == null ? null : mItem.requestLargeImage().run(jc);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.BitmapRegionDecoder com.android.gallery3d.app.CropImage$LoadDataTask.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }

    private class LoadBitmapDataTask implements Job<Bitmap> {
        MediaItem mItem;

        public LoadBitmapDataTask(MediaItem item) {
            mItem = item;
        }
        public Bitmap run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.app.CropImage$LoadBitmapDataTask.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.app.CropImage$LoadBitmapDataTask.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return mItem == null
                    ? null
                    : mItem.requestImage(MediaItem.TYPE_THUMBNAIL).run(jc);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.app.CropImage$LoadBitmapDataTask.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }

    private static final String[] EXIF_TAGS = {
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_FLASH,
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_GPS_LATITUDE_REF,
            ExifInterface.TAG_GPS_LONGITUDE_REF,
            ExifInterface.TAG_GPS_ALTITUDE,
            ExifInterface.TAG_GPS_ALTITUDE_REF,
            ExifInterface.TAG_GPS_TIMESTAMP,
            ExifInterface.TAG_GPS_DATESTAMP,
            ExifInterface.TAG_WHITE_BALANCE,
            ExifInterface.TAG_FOCAL_LENGTH,
            ExifInterface.TAG_GPS_PROCESSING_METHOD};

    private static void copyExif(MediaItem item, String destination, int newWidth, int newHeight) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.app.CropImage.copyExif(com.android.gallery3d.data.MediaItem,java.lang.String,int,int)",item,destination,newWidth,newHeight);try{try {
            ExifInterface newExif = new ExifInterface(destination);
            PicasaSource.extractExifValues(item, newExif);
            newExif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, String.valueOf(newWidth));
            newExif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, String.valueOf(newHeight));
            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(0));
            newExif.saveAttributes();
        } catch (Throwable t) {
            Log.w(TAG, "cannot copy exif: " + item, t);
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.app.CropImage.copyExif(com.android.gallery3d.data.MediaItem,java.lang.String,int,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.copyExif(com.android.gallery3d.data.MediaItem,java.lang.String,int,int)",throwable);throw throwable;}
    }

    private static void copyExif(String source, String destination, int newWidth, int newHeight) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.app.CropImage.copyExif(java.lang.String,java.lang.String,int,int)",source,destination,newWidth,newHeight);try{try {
            ExifInterface oldExif = new ExifInterface(source);
            ExifInterface newExif = new ExifInterface(destination);

            newExif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, String.valueOf(newWidth));
            newExif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, String.valueOf(newHeight));
            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(0));

            for (String tag : EXIF_TAGS) {
                String value = oldExif.getAttribute(tag);
                if (value != null) {
                    newExif.setAttribute(tag, value);
                }
            }

            /*// Handle some special values here*/
            String value = oldExif.getAttribute(ExifInterface.TAG_APERTURE);
            if (value != null) {
                try {
                    float aperture = Float.parseFloat(value);
                    newExif.setAttribute(ExifInterface.TAG_APERTURE,
                            String.valueOf((int) (aperture * 10 + 0.5f)) + "/10");
                } catch (NumberFormatException e) {
                    Log.w(TAG, "cannot parse aperture: " + value);
                }
            }

            /*// TODO: The code is broken, need to fix the JHEAD lib*/
            /*
            value = oldExif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            if (value != null) {
                try {
                    double exposure = Double.parseDouble(value);
                    testToRational("test exposure", exposure);
                    newExif.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, value);
                } catch (NumberFormatException e) {
                    Log.w(TAG, "cannot parse exposure time: " + value);
                }
            }

            value = oldExif.getAttribute(ExifInterface.TAG_ISO);
            if (value != null) {
                try {
                    int iso = Integer.parseInt(value);
                    newExif.setAttribute(ExifInterface.TAG_ISO, String.valueOf(iso) + "/1");
                } catch (NumberFormatException e) {
                    Log.w(TAG, "cannot parse exposure time: " + value);
                }
            }*/
            newExif.saveAttributes();
        } catch (Throwable t) {
            Log.w(TAG, "cannot copy exif: " + source, t);
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.app.CropImage.copyExif(java.lang.String,java.lang.String,int,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.app.CropImage.copyExif(java.lang.String,java.lang.String,int,int)",throwable);throw throwable;}
    }
}
