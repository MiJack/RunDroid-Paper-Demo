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

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.util.MediaSetUtils;

import android.mtp.MtpDeviceInfo;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*// MtpDeviceSet -- MtpDevice -- MtpImage*/
public class MtpDeviceSet extends MediaSet {
    private static final String TAG = "MtpDeviceSet";

    private GalleryApp mApplication;
    private final ArrayList<MediaSet> mDeviceSet = new ArrayList<MediaSet>();
    private final ChangeNotifier mNotifier;
    private final MtpContext mMtpContext;
    private final String mName;

    public MtpDeviceSet(Path path, GalleryApp application, MtpContext mtpContext) {
        super(path, nextVersionNumber());
        mApplication = application;
        mNotifier = new ChangeNotifier(this, Uri.parse("mtp://"), application);
        mMtpContext = mtpContext;
        mName = application.getResources().getString(R.string.set_label_mtp_devices);
    }

    private void loadDevices() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpDeviceSet.loadDevices()",this);try{DataManager dataManager = mApplication.getDataManager();
        /*// Enumerate all devices*/
        mDeviceSet.clear();
        List<android.mtp.MtpDevice> devices = mMtpContext.getMtpClient().getDeviceList();
        Log.v(TAG, "loadDevices: " + devices + ", size=" + devices.size());
        for (android.mtp.MtpDevice mtpDevice : devices) {
            int deviceId = mtpDevice.getDeviceId();
            Path childPath = mPath.getChild(deviceId);
            MtpDevice device = (MtpDevice) dataManager.peekMediaObject(childPath);
            if (device == null) {
                device = new MtpDevice(childPath, mApplication, deviceId, mMtpContext);
            }
            Log.d(TAG, "add device " + device);
            mDeviceSet.add(device);
        }

        Collections.sort(mDeviceSet, MediaSetUtils.NAME_COMPARATOR);
        for (int i = 0, n = mDeviceSet.size(); i < n; i++) {
            mDeviceSet.get(i).reload();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpDeviceSet.loadDevices()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpDeviceSet.loadDevices()",this,throwable);throw throwable;}
    }

    public static String getDeviceName(MtpContext mtpContext, int deviceId) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.data.MtpDeviceSet.getDeviceName(com.android.gallery3d.data.MtpContext,int)",mtpContext,deviceId);try{android.mtp.MtpDevice device = mtpContext.getMtpClient().getDevice(deviceId);
        if (device == null) {
            {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.data.MtpDeviceSet.getDeviceName(com.android.gallery3d.data.MtpContext,int)");return "";}
        }
        MtpDeviceInfo info = device.getDeviceInfo();
        if (info == null) {
            {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.data.MtpDeviceSet.getDeviceName(com.android.gallery3d.data.MtpContext,int)");return "";}
        }
        String manufacturer = info.getManufacturer().trim();
        String model = info.getModel().trim();
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.data.MtpDeviceSet.getDeviceName(com.android.gallery3d.data.MtpContext,int)");return manufacturer + " " + model;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.MtpDeviceSet.getDeviceName(com.android.gallery3d.data.MtpContext,int)",throwable);throw throwable;}
    }

    @Override
    public MediaSet getSubMediaSet(int index) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.MtpDeviceSet.getSubMediaSet(int)",this,index);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.MtpDeviceSet.getSubMediaSet(int)",this);return index < mDeviceSet.size() ? mDeviceSet.get(index) : null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.MtpDeviceSet.getSubMediaSet(int)",this,throwable);throw throwable;}
    }

    @Override
    public int getSubMediaSetCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MtpDeviceSet.getSubMediaSetCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MtpDeviceSet.getSubMediaSetCount()",this);return mDeviceSet.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MtpDeviceSet.getSubMediaSetCount()",this,throwable);throw throwable;}
    }

    @Override
    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.MtpDeviceSet.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.MtpDeviceSet.getName()",this);return mName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.MtpDeviceSet.getName()",this,throwable);throw throwable;}
    }

    @Override
    public long reload() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.MtpDeviceSet.reload()",this);try{if (mNotifier.isDirty()) {
            mDataVersion = nextVersionNumber();
            loadDevices();
        }
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.MtpDeviceSet.reload()",this);return mDataVersion;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.MtpDeviceSet.reload()",this,throwable);throw throwable;}
    }
}
