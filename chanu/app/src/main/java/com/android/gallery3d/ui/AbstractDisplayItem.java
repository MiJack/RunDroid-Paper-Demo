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

package com.android.gallery3d.ui;

import com.android.gallery3d.data.MediaItem;

import android.graphics.Bitmap;

public abstract class AbstractDisplayItem extends DisplayItem {

    private static final String TAG = "AbstractDisplayItem";

    private static final int STATE_INVALID = 0x01;
    private static final int STATE_VALID = 0x02;
    private static final int STATE_UPDATING = 0x04;
    private static final int STATE_CANCELING = 0x08;
    private static final int STATE_ERROR = 0x10;

    private int mState = STATE_INVALID;
    private boolean mImageRequested = false;
    private boolean mRecycling = false;
    private Bitmap mBitmap;

    protected final MediaItem mMediaItem;
    private int mRotation;

    public AbstractDisplayItem(MediaItem item) {
        mMediaItem = item;
        if (item == null) {mState = STATE_ERROR;}
        if (item != null) {mRotation = mMediaItem.getRotation();}
    }

    protected void updateImage(Bitmap bitmap, boolean isCancelled) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AbstractDisplayItem.updateImage(android.graphics.Bitmap,boolean)",this,bitmap,isCancelled);try{if (mRecycling) {
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AbstractDisplayItem.updateImage(android.graphics.Bitmap,boolean)",this);return;}
        }

        if (isCancelled && bitmap == null) {
            mState = STATE_INVALID;
            if (mImageRequested) {
                /*// request image again.*/
                requestImage();
            }
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AbstractDisplayItem.updateImage(android.graphics.Bitmap,boolean)",this);return;}
        }

        mBitmap = bitmap;
        mState = bitmap == null ? STATE_ERROR : STATE_VALID ;
        onBitmapAvailable(mBitmap);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AbstractDisplayItem.updateImage(android.graphics.Bitmap,boolean)",this,throwable);throw throwable;}
    }

    @Override
    public int getRotation() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.AbstractDisplayItem.getRotation()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.AbstractDisplayItem.getRotation()",this);return mRotation;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.AbstractDisplayItem.getRotation()",this,throwable);throw throwable;}
    }

    @Override
    public long getIdentity() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.ui.AbstractDisplayItem.getIdentity()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.ui.AbstractDisplayItem.getIdentity()",this);return mMediaItem != null
                ? System.identityHashCode(mMediaItem.getPath())
                : System.identityHashCode(this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.ui.AbstractDisplayItem.getIdentity()",this,throwable);throw throwable;}
    }

    public void requestImage() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AbstractDisplayItem.requestImage()",this);try{mImageRequested = true;
        if (mState == STATE_INVALID) {
            mState = STATE_UPDATING;
            startLoadBitmap();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AbstractDisplayItem.requestImage()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AbstractDisplayItem.requestImage()",this,throwable);throw throwable;}
    }

    public void cancelImageRequest() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AbstractDisplayItem.cancelImageRequest()",this);try{mImageRequested = false;
        if (mState == STATE_UPDATING) {
            mState = STATE_CANCELING;
            cancelLoadBitmap();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AbstractDisplayItem.cancelImageRequest()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AbstractDisplayItem.cancelImageRequest()",this,throwable);throw throwable;}
    }

    private boolean inState(int states) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.AbstractDisplayItem.inState(int)",this,states);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.AbstractDisplayItem.inState(int)",this);return (mState & states) != 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.AbstractDisplayItem.inState(int)",this,throwable);throw throwable;}
    }

    public void recycle() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.AbstractDisplayItem.recycle()",this);try{if (!inState(STATE_UPDATING | STATE_CANCELING)) {
            if (mBitmap != null) {mBitmap = null;}
        } else {
            mRecycling = true;
            cancelImageRequest();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.AbstractDisplayItem.recycle()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.AbstractDisplayItem.recycle()",this,throwable);throw throwable;}
    }

    public boolean isRequestInProgress() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.AbstractDisplayItem.isRequestInProgress()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.AbstractDisplayItem.isRequestInProgress()",this);return mImageRequested && inState(STATE_UPDATING | STATE_CANCELING);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.AbstractDisplayItem.isRequestInProgress()",this,throwable);throw throwable;}
    }

    abstract protected void startLoadBitmap();
    abstract protected void cancelLoadBitmap();
    abstract protected void onBitmapAvailable(Bitmap bitmap);
}
