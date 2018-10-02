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

package com.android.gallery3d.data;

import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.webkit.MimeTypeMap;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;

public class UriImage extends MediaItem {
    private static final String TAG = "UriImage";

    private static final int STATE_INIT = 0;
    private static final int STATE_DOWNLOADING = 1;
    private static final int STATE_DOWNLOADED = 2;
    private static final int STATE_ERROR = -1;

    private final Uri mUri;
    private final String mContentType;

    private DownloadCache.Entry mCacheEntry;
    private ParcelFileDescriptor mFileDescriptor;
    private int mState = STATE_INIT;
    private int mWidth;
    private int mHeight;

    private GalleryApp mApplication;

    public UriImage(GalleryApp application, Path path, Uri uri) {
        super(path, nextVersionNumber());
        mUri = uri;
        mApplication = Utils.checkNotNull(application);
        mContentType = getMimeType(uri);
    }

    private String getMimeType(Uri uri) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.UriImage.getMimeType(android.net.Uri)",this,uri);try{if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            String extension =
                    MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            String type = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(extension);
            if (type != null) {{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.UriImage.getMimeType(android.net.Uri)",this);return type;}}
        }
        {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.UriImage.getMimeType(android.net.Uri)",this);return mApplication.getContentResolver().getType(uri);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.UriImage.getMimeType(android.net.Uri)",this,throwable);throw throwable;}
    }

    @Override
    public Job<Bitmap> requestImage(int type) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.UriImage.requestImage(int)",this,type);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.UriImage.requestImage(int)",this);return new BitmapJob(type);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.UriImage.requestImage(int)",this,throwable);throw throwable;}
    }

    @Override
    public Job<BitmapRegionDecoder> requestLargeImage() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.UriImage.requestLargeImage()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.UriImage.requestLargeImage()",this);return new RegionDecoderJob();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.UriImage.requestLargeImage()",this,throwable);throw throwable;}
    }

    private void openFileOrDownloadTempFile(JobContext jc) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.UriImage.openFileOrDownloadTempFile(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{int state = openOrDownloadInner(jc);
        synchronized (this) {
            mState = state;
            if (mState != STATE_DOWNLOADED) {
                if (mFileDescriptor != null) {
                    Utils.closeSilently(mFileDescriptor);
                    mFileDescriptor = null;
                }
            }
            notifyAll();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.UriImage.openFileOrDownloadTempFile(com.android.gallery3d.util.ThreadPool.JobContext)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.UriImage.openFileOrDownloadTempFile(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
    }

    private int openOrDownloadInner(JobContext jc) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.UriImage.openOrDownloadInner(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{String scheme = mUri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)
                || ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)
                || ContentResolver.SCHEME_FILE.equals(scheme)) {
            try {
                mFileDescriptor = mApplication.getContentResolver()
                        .openFileDescriptor(mUri, "r");
                if (jc.isCancelled()) {{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.UriImage.openOrDownloadInner(com.android.gallery3d.util.ThreadPool.JobContext)",this);return STATE_INIT;}}
                {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.UriImage.openOrDownloadInner(com.android.gallery3d.util.ThreadPool.JobContext)",this);return STATE_DOWNLOADED;}
            } catch (FileNotFoundException e) {
                Log.w(TAG, "fail to open: " + mUri, e);
                {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.UriImage.openOrDownloadInner(com.android.gallery3d.util.ThreadPool.JobContext)",this);return STATE_ERROR;}
            }
        } else {
            try {
                URL url = new URI(mUri.toString()).toURL();
                mCacheEntry = mApplication.getDownloadCache().download(jc, url);
                if (jc.isCancelled()) {{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.UriImage.openOrDownloadInner(com.android.gallery3d.util.ThreadPool.JobContext)",this);return STATE_INIT;}}
                if (mCacheEntry == null) {
                    Log.w(TAG, "download failed " + url);
                    {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.UriImage.openOrDownloadInner(com.android.gallery3d.util.ThreadPool.JobContext)",this);return STATE_ERROR;}
                }
                mFileDescriptor = ParcelFileDescriptor.open(
                        mCacheEntry.cacheFile, ParcelFileDescriptor.MODE_READ_ONLY);
                {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.UriImage.openOrDownloadInner(com.android.gallery3d.util.ThreadPool.JobContext)",this);return STATE_DOWNLOADED;}
            } catch (Throwable t) {
                Log.w(TAG, "download error", t);
                {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.UriImage.openOrDownloadInner(com.android.gallery3d.util.ThreadPool.JobContext)",this);return STATE_ERROR;}
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.UriImage.openOrDownloadInner(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
    }

    private boolean prepareInputFile(JobContext jc) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.UriImage.prepareInputFile(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{jc.setCancelListener(new CancelListener() {
            public void onCancel() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.UriImage$1.onCancel()",this);try{synchronized (this) {
                    notifyAll();
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.UriImage$1.onCancel()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.UriImage$1.onCancel()",this,throwable);throw throwable;}
            }
        });

        while (true) {
            synchronized (this) {
                if (jc.isCancelled()) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.UriImage.prepareInputFile(com.android.gallery3d.util.ThreadPool.JobContext)",this);return false;}}
                if (mState == STATE_INIT) {
                    mState = STATE_DOWNLOADING;
                    /*// Then leave the synchronized block and continue.*/
                } else if (mState == STATE_ERROR) {
                    {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.UriImage.prepareInputFile(com.android.gallery3d.util.ThreadPool.JobContext)",this);return false;}
                } else if (mState == STATE_DOWNLOADED) {
                    {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.UriImage.prepareInputFile(com.android.gallery3d.util.ThreadPool.JobContext)",this);return true;}
                } else /* if (mState == STATE_DOWNLOADING) */ {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        /*// ignored.*/
                    }
                    continue;
                }
            }
            /*// This is only reached for STATE_INIT->STATE_DOWNLOADING*/
            openFileOrDownloadTempFile(jc);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.UriImage.prepareInputFile(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
    }

    private class RegionDecoderJob implements Job<BitmapRegionDecoder> {
        public BitmapRegionDecoder run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.UriImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{if (!prepareInputFile(jc)) {{com.mijack.Xlog.logMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.UriImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
            BitmapRegionDecoder decoder = DecodeUtils.requestCreateBitmapRegionDecoder(
                    jc, mFileDescriptor.getFileDescriptor(), false);
            mWidth = decoder.getWidth();
            mHeight = decoder.getHeight();
            {com.mijack.Xlog.logMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.UriImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return decoder;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.UriImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }

    private class BitmapJob implements Job<Bitmap> {
        private int mType;

        protected BitmapJob(int type) {
            mType = type;
        }

        public Bitmap run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.UriImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{if (!prepareInputFile(jc)) {{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.data.UriImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
            int targetSize = LocalImage.getTargetSize(mType);
            Options options = new Options();
            options.inPreferredConfig = Config.ARGB_8888;
            Bitmap bitmap = DecodeUtils.requestDecode(jc,
                    mFileDescriptor.getFileDescriptor(), options, targetSize);
            if (jc.isCancelled() || bitmap == null) {
                {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.data.UriImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}
            }

            if (mType == MediaItem.TYPE_MICROTHUMBNAIL) {
                bitmap = BitmapUtils.resizeDownAndCropCenter(bitmap,
                        targetSize, true);
            } else {
                bitmap = BitmapUtils.resizeDownBySideLength(bitmap,
                        targetSize, true);
            }

            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.data.UriImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return bitmap;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.UriImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }

    @Override
    public int getSupportedOperations() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.UriImage.getSupportedOperations()",this);try{int supported = SUPPORT_EDIT | SUPPORT_SETAS;
        if (isSharable()) {supported |= SUPPORT_SHARE;}
        if (BitmapUtils.isSupportedByRegionDecoder(mContentType)) {
            supported |= SUPPORT_FULL_IMAGE;
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.UriImage.getSupportedOperations()",this);return supported;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.UriImage.getSupportedOperations()",this,throwable);throw throwable;}
    }

    private boolean isSharable() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.UriImage.isSharable()",this);try{/*// We cannot grant read permission to the receiver since we put*/
        /*// the data URI in EXTRA_STREAM instead of the data part of an intent*/
        /*// And there are issues in MediaUploader and Bluetooth file sender to*/
        /*// share a general image data. So, we only share for local file.*/
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.UriImage.isSharable()",this);return ContentResolver.SCHEME_FILE.equals(mUri.getScheme());}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.UriImage.isSharable()",this,throwable);throw throwable;}
    }

    @Override
    public int getMediaType() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.UriImage.getMediaType()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.UriImage.getMediaType()",this);return MEDIA_TYPE_IMAGE;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.UriImage.getMediaType()",this,throwable);throw throwable;}
    }

    @Override
    public Uri getContentUri() {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.data.UriImage.getContentUri()",this);try{com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.data.UriImage.getContentUri()",this);return mUri;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.data.UriImage.getContentUri()",this,throwable);throw throwable;}
    }

    @Override
    public MediaDetails getDetails() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.UriImage.getDetails()",this);try{MediaDetails details = super.getDetails();
        if (mWidth != 0 && mHeight != 0) {
            details.addDetail(MediaDetails.INDEX_WIDTH, mWidth);
            details.addDetail(MediaDetails.INDEX_HEIGHT, mHeight);
        }
        details.addDetail(MediaDetails.INDEX_MIMETYPE, mContentType);
        if (ContentResolver.SCHEME_FILE.equals(mUri.getScheme())) {
            String filePath = mUri.getPath();
            details.addDetail(MediaDetails.INDEX_PATH, filePath);
            MediaDetails.extractExifInfo(details, filePath);
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.UriImage.getDetails()",this);return details;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.UriImage.getDetails()",this,throwable);throw throwable;}
    }

    @Override
    public String getMimeType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.UriImage.getMimeType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.UriImage.getMimeType()",this);return mContentType;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.UriImage.getMimeType()",this,throwable);throw throwable;}
    }

    @Override
    protected void finalize() throws Throwable {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.UriImage.finalize()",this);try{try {
            if (mFileDescriptor != null) {
                Utils.closeSilently(mFileDescriptor);
            }
        } finally {
            super.finalize();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.UriImage.finalize()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.UriImage.finalize()",this,throwable);throw throwable;}
    }

    @Override
    public int getWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.UriImage.getWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.UriImage.getWidth()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.UriImage.getWidth()",this,throwable);throw throwable;}
    }

    @Override
    public int getHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.UriImage.getHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.UriImage.getHeight()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.UriImage.getHeight()",this,throwable);throw throwable;}
    }
}
