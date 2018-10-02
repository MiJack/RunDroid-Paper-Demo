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

import android.hardware.usb.UsbDevice;
import android.mtp.MtpConstants;
import android.mtp.MtpObjectInfo;
import android.mtp.MtpStorageInfo;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MtpDevice extends MediaSet {
    private static final String TAG = "MtpDevice";

    private final GalleryApp mApplication;
    private final int mDeviceId;
    private final String mDeviceName;
    private final DataManager mDataManager;
    private final MtpContext mMtpContext;
    private final String mName;
    private final ChangeNotifier mNotifier;
    private final Path mItemPath;
    private List<MtpObjectInfo> mJpegChildren;

    public MtpDevice(Path path, GalleryApp application, int deviceId,
            String name, MtpContext mtpContext) {
        super(path, nextVersionNumber());
        mApplication = application;
        mDeviceId = deviceId;
        mDeviceName = UsbDevice.getDeviceName(deviceId);
        mDataManager = application.getDataManager();
        mMtpContext = mtpContext;
        mName = name;
        mNotifier = new ChangeNotifier(this, Uri.parse("mtp://"), application);
        mItemPath = Path.fromString("/mtp/item/" + String.valueOf(deviceId));
        mJpegChildren = new ArrayList<MtpObjectInfo>();
    }

    public MtpDevice(Path path, GalleryApp application, int deviceId,
            MtpContext mtpContext) {
        this(path, application, deviceId,
                MtpDeviceSet.getDeviceName(mtpContext, deviceId), mtpContext);
    }

    private List<MtpObjectInfo> loadItems() {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.data.MtpDevice.loadItems()",this);try{ArrayList<MtpObjectInfo> result = new ArrayList<MtpObjectInfo>();

        List<MtpStorageInfo> storageList = mMtpContext.getMtpClient()
                 .getStorageList(mDeviceName);
        if (storageList == null) {{com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.MtpDevice.loadItems()",this);return result;}}

        for (MtpStorageInfo info : storageList) {
            collectJpegChildren(info.getStorageId(), 0, result);
        }

        {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.MtpDevice.loadItems()",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.MtpDevice.loadItems()",this,throwable);throw throwable;}
    }

    private void collectJpegChildren(int storageId, int objectId,
            ArrayList<MtpObjectInfo> result) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpDevice.collectJpegChildren(int,int,java.util.ArrayList)",this,storageId,objectId,result);try{ArrayList<MtpObjectInfo> dirChildren = new ArrayList<MtpObjectInfo>();

        queryChildren(storageId, objectId, result, dirChildren);

        for (int i = 0, n = dirChildren.size(); i < n; i++) {
            MtpObjectInfo info = dirChildren.get(i);
            collectJpegChildren(storageId, info.getObjectHandle(), result);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpDevice.collectJpegChildren(int,int,java.util.ArrayList)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpDevice.collectJpegChildren(int,int,java.util.ArrayList)",this,throwable);throw throwable;}
    }

    private void queryChildren(int storageId, int objectId,
            ArrayList<MtpObjectInfo> jpeg, ArrayList<MtpObjectInfo> dir) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpDevice.queryChildren(int,int,java.util.ArrayList,java.util.ArrayList)",this,storageId,objectId,jpeg,dir);try{List<MtpObjectInfo> children = mMtpContext.getMtpClient().getObjectList(
                mDeviceName, storageId, objectId);
        if (children == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpDevice.queryChildren(int,int,java.util.ArrayList,java.util.ArrayList)",this);return;}}

        for (MtpObjectInfo obj : children) {
            int format = obj.getFormat();
            switch (format) {
                case MtpConstants.FORMAT_JFIF:
                case MtpConstants.FORMAT_EXIF_JPEG:
                    jpeg.add(obj);
                    break;
                case MtpConstants.FORMAT_ASSOCIATION:
                    dir.add(obj);
                    break;
                default:
                    Log.w(TAG, "other type: name = " + obj.getName()
                            + ", format = " + format);
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpDevice.queryChildren(int,int,java.util.ArrayList,java.util.ArrayList)",this,throwable);throw throwable;}
    }

    public static MtpObjectInfo getObjectInfo(MtpContext mtpContext, int deviceId,
            int objectId) {
        com.mijack.Xlog.logStaticMethodEnter("android.mtp.MtpObjectInfo com.android.gallery3d.data.MtpDevice.getObjectInfo(com.android.gallery3d.data.MtpContext,int,int)",mtpContext,deviceId,objectId);try{String deviceName = UsbDevice.getDeviceName(deviceId);
        {com.mijack.Xlog.logStaticMethodExit("android.mtp.MtpObjectInfo com.android.gallery3d.data.MtpDevice.getObjectInfo(com.android.gallery3d.data.MtpContext,int,int)");return mtpContext.getMtpClient().getObjectInfo(deviceName, objectId);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.mtp.MtpObjectInfo com.android.gallery3d.data.MtpDevice.getObjectInfo(com.android.gallery3d.data.MtpContext,int,int)",throwable);throw throwable;}
    }

    @Override
    public ArrayList<MediaItem> getMediaItem(int start, int count) {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.data.MtpDevice.getMediaItem(int,int)",this,start,count);try{ArrayList<MediaItem> result = new ArrayList<MediaItem>();
        int begin = start;
        int end = Math.min(start + count, mJpegChildren.size());

        DataManager dataManager = mApplication.getDataManager();
        for (int i = begin; i < end; i++) {
            MtpObjectInfo child = mJpegChildren.get(i);
            Path childPath = mItemPath.getChild(child.getObjectHandle());
            MtpImage image = (MtpImage) dataManager.peekMediaObject(childPath);
            if (image == null) {
                image = new MtpImage(
                        childPath, mApplication, mDeviceId, child, mMtpContext);
            } else {
                image.updateContent(child);
            }
            result.add(image);
        }
        {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.MtpDevice.getMediaItem(int,int)",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.MtpDevice.getMediaItem(int,int)",this,throwable);throw throwable;}
    }

    @Override
    public int getMediaItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MtpDevice.getMediaItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MtpDevice.getMediaItemCount()",this);return mJpegChildren.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MtpDevice.getMediaItemCount()",this,throwable);throw throwable;}
    }

    @Override
    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.MtpDevice.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.MtpDevice.getName()",this);return mName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.MtpDevice.getName()",this,throwable);throw throwable;}
    }

    @Override
    public long reload() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.MtpDevice.reload()",this);try{if (mNotifier.isDirty()) {
            mDataVersion = nextVersionNumber();
            mJpegChildren = loadItems();
        }
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.MtpDevice.reload()",this);return mDataVersion;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.MtpDevice.reload()",this,throwable);throw throwable;}
    }

    @Override
    public int getSupportedOperations() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MtpDevice.getSupportedOperations()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MtpDevice.getSupportedOperations()",this);return SUPPORT_IMPORT;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MtpDevice.getSupportedOperations()",this,throwable);throw throwable;}
    }

    @Override
    public boolean Import() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.MtpDevice.Import()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MtpDevice.Import()",this);return mMtpContext.copyAlbum(mDeviceName, mName, mJpegChildren);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.MtpDevice.Import()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isLeafAlbum() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.MtpDevice.isLeafAlbum()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MtpDevice.isLeafAlbum()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.MtpDevice.isLeafAlbum()",this,throwable);throw throwable;}
    }
}
