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
import com.android.gallery3d.provider.GalleryProvider;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.hardware.usb.UsbDevice;
import android.mtp.MtpObjectInfo;
import android.net.Uri;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

public class MtpImage extends MediaItem {
    private static final String TAG = "MtpImage";

    private final int mDeviceId;
    private int mObjectId;
    private int mObjectSize;
    private long mDateTaken;
    private String mFileName;
    private final ThreadPool mThreadPool;
    private final MtpContext mMtpContext;
    private final MtpObjectInfo mObjInfo;
    private final int mImageWidth;
    private final int mImageHeight;
    private final Context mContext;

    MtpImage(Path path, GalleryApp application, int deviceId,
            MtpObjectInfo objInfo, MtpContext mtpContext) {
        super(path, nextVersionNumber());
        mContext = application.getAndroidContext();
        mDeviceId = deviceId;
        mObjInfo = objInfo;
        mObjectId = objInfo.getObjectHandle();
        mObjectSize = objInfo.getCompressedSize();
        mDateTaken = objInfo.getDateCreated();
        mFileName = objInfo.getName();
        mImageWidth = objInfo.getImagePixWidth();
        mImageHeight = objInfo.getImagePixHeight();
        mThreadPool = application.getThreadPool();
        mMtpContext = mtpContext;
    }

    MtpImage(Path path, GalleryApp app, int deviceId, int objectId, MtpContext mtpContext) {
        this(path, app, deviceId, MtpDevice.getObjectInfo(mtpContext, deviceId, objectId),
                mtpContext);
    }

    @Override
    public long getDateInMs() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.MtpImage.getDateInMs()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.MtpImage.getDateInMs()",this);return mDateTaken;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.MtpImage.getDateInMs()",this,throwable);throw throwable;}
    }

    @Override
    public Job<Bitmap> requestImage(int type) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.MtpImage.requestImage(int)",this,type);try{{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.MtpImage.requestImage(int)",this);return new Job<Bitmap>() {
            public Bitmap run(JobContext jc) {
                com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.MtpImage$1.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{byte[] thumbnail = mMtpContext.getMtpClient().getThumbnail(
                        UsbDevice.getDeviceName(mDeviceId), mObjectId);
                if (thumbnail == null) {
                    Log.w(TAG, "decoding thumbnail failed");
                    {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.MtpImage.requestImage(int)",this);{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.data.MtpImage$1.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}
                }
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.MtpImage.requestImage(int)",this);{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.data.MtpImage$1.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return DecodeUtils.requestDecode(jc, thumbnail, null);}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.MtpImage$1.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
            }
        };}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.MtpImage.requestImage(int)",this,throwable);throw throwable;}
    }

    @Override
    public Job<BitmapRegionDecoder> requestLargeImage() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.MtpImage.requestLargeImage()",this);try{{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.MtpImage.requestLargeImage()",this);return new Job<BitmapRegionDecoder>() {
            public BitmapRegionDecoder run(JobContext jc) {
                com.mijack.Xlog.logMethodEnter("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.MtpImage$2.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{byte[] bytes = mMtpContext.getMtpClient().getObject(
                        UsbDevice.getDeviceName(mDeviceId), mObjectId, mObjectSize);
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.MtpImage.requestLargeImage()",this);{com.mijack.Xlog.logMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.MtpImage$2.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return DecodeUtils.requestCreateBitmapRegionDecoder(
                        jc, bytes, 0, bytes.length, false);}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.MtpImage$2.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
            }
        };}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.MtpImage.requestLargeImage()",this,throwable);throw throwable;}
    }

    public byte[] getImageData() {
        com.mijack.Xlog.logMethodEnter("[byte com.android.gallery3d.data.MtpImage.getImageData()",this);try{com.mijack.Xlog.logMethodExit("[byte com.android.gallery3d.data.MtpImage.getImageData()",this);return mMtpContext.getMtpClient().getObject(
                UsbDevice.getDeviceName(mDeviceId), mObjectId, mObjectSize);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[byte com.android.gallery3d.data.MtpImage.getImageData()",this,throwable);throw throwable;}
    }

    @Override
    public boolean Import() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.MtpImage.Import()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MtpImage.Import()",this);return mMtpContext.copyFile(UsbDevice.getDeviceName(mDeviceId), mObjInfo);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.MtpImage.Import()",this,throwable);throw throwable;}
    }

    @Override
    public int getSupportedOperations() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MtpImage.getSupportedOperations()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MtpImage.getSupportedOperations()",this);return SUPPORT_FULL_IMAGE | SUPPORT_IMPORT;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MtpImage.getSupportedOperations()",this,throwable);throw throwable;}
    }

    public void updateContent(MtpObjectInfo info) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpImage.updateContent(android.mtp.MtpObjectInfo)",this,info);try{if (mObjectId != info.getObjectHandle() || mDateTaken != info.getDateCreated()) {
            mObjectId = info.getObjectHandle();
            mDateTaken = info.getDateCreated();
            mDataVersion = nextVersionNumber();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpImage.updateContent(android.mtp.MtpObjectInfo)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpImage.updateContent(android.mtp.MtpObjectInfo)",this,throwable);throw throwable;}
    }

    @Override
    public String getMimeType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.MtpImage.getMimeType()",this);try{/*// Currently only JPEG is supported in MTP.*/
        {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.MtpImage.getMimeType()",this);return "image/jpeg";}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.MtpImage.getMimeType()",this,throwable);throw throwable;}
    }

    @Override
    public int getMediaType() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MtpImage.getMediaType()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MtpImage.getMediaType()",this);return MEDIA_TYPE_IMAGE;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MtpImage.getMediaType()",this,throwable);throw throwable;}
    }

    @Override
    public long getSize() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.MtpImage.getSize()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.MtpImage.getSize()",this);return mObjectSize;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.MtpImage.getSize()",this,throwable);throw throwable;}
    }

    @Override
    public Uri getContentUri() {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.data.MtpImage.getContentUri()",this);try{com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.data.MtpImage.getContentUri()",this);return GalleryProvider.getUriFor(mContext, mPath);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.data.MtpImage.getContentUri()",this,throwable);throw throwable;}
    }

    @Override
    public MediaDetails getDetails() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.MtpImage.getDetails()",this);try{MediaDetails details = super.getDetails();
        DateFormat formater = DateFormat.getDateTimeInstance();
        details.addDetail(MediaDetails.INDEX_TITLE, mFileName);
        details.addDetail(MediaDetails.INDEX_DATETIME, formater.format(new Date(mDateTaken)));
        details.addDetail(MediaDetails.INDEX_WIDTH, mImageWidth);
        details.addDetail(MediaDetails.INDEX_HEIGHT, mImageHeight);
        details.addDetail(MediaDetails.INDEX_SIZE, Long.valueOf(mObjectSize));
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.MtpImage.getDetails()",this);return details;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.MtpImage.getDetails()",this,throwable);throw throwable;}
    }

    @Override
    public int getWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MtpImage.getWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MtpImage.getWidth()",this);return mImageWidth;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MtpImage.getWidth()",this,throwable);throw throwable;}
    }

    @Override
    public int getHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MtpImage.getHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MtpImage.getHeight()",this);return mImageHeight;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MtpImage.getHeight()",this,throwable);throw throwable;}
    }
}
