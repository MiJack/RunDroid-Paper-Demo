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


public class GLPaint {
    public static final int FLAG_ANTI_ALIAS = 0x01;

    private int mFlags = 0;
    private float mLineWidth = 1f;
    private int mColor = 0;

    public int getFlags() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.GLPaint.getFlags()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.GLPaint.getFlags()",this);return mFlags;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.GLPaint.getFlags()",this,throwable);throw throwable;}
    }

    public void setFlags(int flags) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLPaint.setFlags(int)",this,flags);try{mFlags = flags;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLPaint.setFlags(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLPaint.setFlags(int)",this,throwable);throw throwable;}
    }

    public void setColor(int color) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLPaint.setColor(int)",this,color);try{mColor = color;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLPaint.setColor(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLPaint.setColor(int)",this,throwable);throw throwable;}
    }

    public int getColor() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.GLPaint.getColor()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.GLPaint.getColor()",this);return mColor;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.GLPaint.getColor()",this,throwable);throw throwable;}
    }

    public void setLineWidth(float width) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLPaint.setLineWidth(float)",this,width);try{Utils.assertTrue(width >= 0);
        mLineWidth = width;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLPaint.setLineWidth(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLPaint.setLineWidth(float)",this,throwable);throw throwable;}
    }

    public float getLineWidth() {
        com.mijack.Xlog.logMethodEnter("float com.android.gallery3d.ui.GLPaint.getLineWidth()",this);try{com.mijack.Xlog.logMethodExit("float com.android.gallery3d.ui.GLPaint.getLineWidth()",this);return mLineWidth;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.android.gallery3d.ui.GLPaint.getLineWidth()",this,throwable);throw throwable;}
    }

    public void setAntiAlias(boolean enabled) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLPaint.setAntiAlias(boolean)",this,enabled);try{if (enabled) {
            mFlags |= FLAG_ANTI_ALIAS;
        } else {
            mFlags &= ~FLAG_ANTI_ALIAS;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLPaint.setAntiAlias(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLPaint.setAntiAlias(boolean)",this,throwable);throw throwable;}
    }

    public boolean getAntiAlias(){
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLPaint.getAntiAlias()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLPaint.getAntiAlias()",this);return (mFlags & FLAG_ANTI_ALIAS) != 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLPaint.getAntiAlias()",this,throwable);throw throwable;}
    }
}
