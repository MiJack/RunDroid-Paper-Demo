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

class MtpSource extends MediaSource {
    private static final String TAG = "MtpSource";

    private static final int MTP_DEVICESET = 0;
    private static final int MTP_DEVICE = 1;
    private static final int MTP_ITEM = 2;

    GalleryApp mApplication;
    PathMatcher mMatcher;
    MtpContext mMtpContext;

    public MtpSource(GalleryApp application) {
        super("mtp");
        mApplication = application;
        mMatcher = new PathMatcher();
        mMatcher.add("/mtp", MTP_DEVICESET);
        mMatcher.add("/mtp/*", MTP_DEVICE);
        mMatcher.add("/mtp/item/*/*", MTP_ITEM);
        mMtpContext = new MtpContext(mApplication.getAndroidContext());
    }

    @Override
    public MediaObject createMediaObject(Path path) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.MtpSource.createMediaObject(com.android.gallery3d.data.Path)",this,path);try{switch (mMatcher.match(path)) {
            case MTP_DEVICESET: {
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.MtpSource.createMediaObject(com.android.gallery3d.data.Path)",this);return new MtpDeviceSet(path, mApplication, mMtpContext);}
            }
            case MTP_DEVICE: {
                int deviceId = mMatcher.getIntVar(0);
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.MtpSource.createMediaObject(com.android.gallery3d.data.Path)",this);return new MtpDevice(path, mApplication, deviceId, mMtpContext);}
            }
            case MTP_ITEM: {
                int deviceId = mMatcher.getIntVar(0);
                int objectId = mMatcher.getIntVar(1);
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.MtpSource.createMediaObject(com.android.gallery3d.data.Path)",this);return new MtpImage(path, mApplication, deviceId, objectId, mMtpContext);}
            }
            default:
                throw new RuntimeException("bad path: " + path);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.MtpSource.createMediaObject(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    @Override
    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpSource.pause()",this);try{mMtpContext.pause();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpSource.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpSource.pause()",this,throwable);throw throwable;}
    }

    @Override
    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpSource.resume()",this);try{mMtpContext.resume();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpSource.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpSource.resume()",this,throwable);throw throwable;}
    }
}
