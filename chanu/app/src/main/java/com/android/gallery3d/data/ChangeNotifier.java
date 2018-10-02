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

import android.net.Uri;

import java.util.concurrent.atomic.AtomicBoolean;

/*// This handles change notification for media sets.*/
public class ChangeNotifier {

    private MediaSet mMediaSet;
    private AtomicBoolean mContentDirty = new AtomicBoolean(true);

    public ChangeNotifier(MediaSet set, Uri uri, GalleryApp application) {
        mMediaSet = set;
        application.getDataManager().registerChangeNotifier(uri, this);
    }

    /*// Returns the dirty flag and clear it.*/
    public boolean isDirty() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.ChangeNotifier.isDirty()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.ChangeNotifier.isDirty()",this);return mContentDirty.compareAndSet(true, false);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.ChangeNotifier.isDirty()",this,throwable);throw throwable;}
    }

    public void fakeChange() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ChangeNotifier.fakeChange()",this);try{onChange(false);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ChangeNotifier.fakeChange()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ChangeNotifier.fakeChange()",this,throwable);throw throwable;}
    }

    public void clearDirty() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ChangeNotifier.clearDirty()",this);try{mContentDirty.set(false);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ChangeNotifier.clearDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ChangeNotifier.clearDirty()",this,throwable);throw throwable;}
    }

    protected void onChange(boolean selfChange) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ChangeNotifier.onChange(boolean)",this,selfChange);try{if (mContentDirty.compareAndSet(false, true)) {
            mMediaSet.notifyContentChanged();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ChangeNotifier.onChange(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ChangeNotifier.onChange(boolean)",this,throwable);throw throwable;}
    }
}