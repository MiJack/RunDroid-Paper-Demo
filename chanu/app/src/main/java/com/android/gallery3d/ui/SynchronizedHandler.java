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

import com.android.gallery3d.common.Utils;

import android.os.Handler;
import android.os.Message;

public class SynchronizedHandler extends Handler {

    private final GLRoot mRoot;

    public SynchronizedHandler(GLRoot root) {
        mRoot = Utils.checkNotNull(root);
    }

    @Override
    public void dispatchMessage(Message message) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SynchronizedHandler.dispatchMessage(android.os.Message)",this,message);try{mRoot.lockRenderThread();
        try {
            super.dispatchMessage(message);
        } finally {
            mRoot.unlockRenderThread();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SynchronizedHandler.dispatchMessage(android.os.Message)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SynchronizedHandler.dispatchMessage(android.os.Message)",this,throwable);throw throwable;}
    }
}
