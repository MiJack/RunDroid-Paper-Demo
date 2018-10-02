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
package com.android.gallery3d.util;

import com.android.gallery3d.common.Utils;

public class UpdateHelper {

    private boolean mUpdated = false;

    public int update(int original, int update) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.util.UpdateHelper.update(int,int)",this,original,update);try{if (original != update) {
            mUpdated = true;
            original = update;
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.util.UpdateHelper.update(int,int)",this);return original;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.util.UpdateHelper.update(int,int)",this,throwable);throw throwable;}
    }

    public long update(long original, long update) {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.util.UpdateHelper.update(long,long)",this,original,update);try{if (original != update) {
            mUpdated = true;
            original = update;
        }
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.util.UpdateHelper.update(long,long)",this);return original;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.util.UpdateHelper.update(long,long)",this,throwable);throw throwable;}
    }

    public double update(double original, double update) {
        com.mijack.Xlog.logMethodEnter("double com.android.gallery3d.util.UpdateHelper.update(double,double)",this,original,update);try{if (original != update) {
            mUpdated = true;
            original = update;
        }
        {com.mijack.Xlog.logMethodExit("double com.android.gallery3d.util.UpdateHelper.update(double,double)",this);return original;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.android.gallery3d.util.UpdateHelper.update(double,double)",this,throwable);throw throwable;}
    }

    public double update(float original, float update) {
        com.mijack.Xlog.logMethodEnter("double com.android.gallery3d.util.UpdateHelper.update(float,float)",this,original,update);try{if (original != update) {
            mUpdated = true;
            original = update;
        }
        {com.mijack.Xlog.logMethodExit("double com.android.gallery3d.util.UpdateHelper.update(float,float)",this);return original;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.android.gallery3d.util.UpdateHelper.update(float,float)",this,throwable);throw throwable;}
    }

    public <T> T update(T original, T update) {
        com.mijack.Xlog.logMethodEnter("java.lang.Object com.android.gallery3d.util.UpdateHelper.update(java.lang.Object,java.lang.Object)",this,original,update);try{if (!Utils.equals(original, update)) {
            mUpdated = true;
            original = update;
        }
        {com.mijack.Xlog.logMethodExit("java.lang.Object com.android.gallery3d.util.UpdateHelper.update(java.lang.Object,java.lang.Object)",this);return original;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Object com.android.gallery3d.util.UpdateHelper.update(java.lang.Object,java.lang.Object)",this,throwable);throw throwable;}
    }

    public boolean isUpdated() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.util.UpdateHelper.isUpdated()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.util.UpdateHelper.isUpdated()",this);return mUpdated;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.util.UpdateHelper.isUpdated()",this,throwable);throw throwable;}
    }
}
