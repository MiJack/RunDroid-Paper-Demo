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

public class IntArray {
    private static final int INIT_CAPACITY = 8;

    private int mData[] = new int[INIT_CAPACITY];
    private int mSize = 0;

    public void add(int value) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.IntArray.add(int)",this,value);try{if (mData.length == mSize) {
            int temp[] = new int[mSize + mSize];
            System.arraycopy(mData, 0, temp, 0, mSize);
            mData = temp;
        }
        mData[mSize++] = value;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.IntArray.add(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.IntArray.add(int)",this,throwable);throw throwable;}
    }

    public int size() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.util.IntArray.size()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.util.IntArray.size()",this);return mSize;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.util.IntArray.size()",this,throwable);throw throwable;}
    }

    public int[] toArray(int[] result) {
        com.mijack.Xlog.logMethodEnter("[int com.android.gallery3d.util.IntArray.toArray([int)",this,result);try{if (result == null || result.length < mSize) {
            result = new int[mSize];
        }
        System.arraycopy(mData, 0, result, 0, mSize);
        {com.mijack.Xlog.logMethodExit("[int com.android.gallery3d.util.IntArray.toArray([int)",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[int com.android.gallery3d.util.IntArray.toArray([int)",this,throwable);throw throwable;}
    }

    public int[] getInternalArray() {
        com.mijack.Xlog.logMethodEnter("[int com.android.gallery3d.util.IntArray.getInternalArray()",this);try{com.mijack.Xlog.logMethodExit("[int com.android.gallery3d.util.IntArray.getInternalArray()",this);return mData;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[int com.android.gallery3d.util.IntArray.getInternalArray()",this,throwable);throw throwable;}
    }

    public void clear() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.IntArray.clear()",this);try{mSize = 0;
        if (mData.length != INIT_CAPACITY) {mData = new int[INIT_CAPACITY];}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.IntArray.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.IntArray.clear()",this,throwable);throw throwable;}
    }
}
