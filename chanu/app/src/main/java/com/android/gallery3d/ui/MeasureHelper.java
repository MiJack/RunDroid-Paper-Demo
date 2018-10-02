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

import android.graphics.Rect;
import android.view.View.MeasureSpec;

class MeasureHelper {

    private static MeasureHelper sInstance = new MeasureHelper(null);

    private GLView mComponent;
    private int mPreferredWidth;
    private int mPreferredHeight;

    private MeasureHelper(GLView component) {
        mComponent = component;
    }

    public static MeasureHelper getInstance(GLView component) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.ui.MeasureHelper com.android.gallery3d.ui.MeasureHelper.getInstance(com.android.gallery3d.ui.GLView)",component);try{sInstance.mComponent = component;
        {com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.ui.MeasureHelper com.android.gallery3d.ui.MeasureHelper.getInstance(com.android.gallery3d.ui.GLView)");return sInstance;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.ui.MeasureHelper com.android.gallery3d.ui.MeasureHelper.getInstance(com.android.gallery3d.ui.GLView)",throwable);throw throwable;}
    }

    public MeasureHelper setPreferredContentSize(int width, int height) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.MeasureHelper com.android.gallery3d.ui.MeasureHelper.setPreferredContentSize(int,int)",this,width,height);try{mPreferredWidth = width;
        mPreferredHeight = height;
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.MeasureHelper com.android.gallery3d.ui.MeasureHelper.setPreferredContentSize(int,int)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.MeasureHelper com.android.gallery3d.ui.MeasureHelper.setPreferredContentSize(int,int)",this,throwable);throw throwable;}
    }

    public void measure(int widthSpec, int heightSpec) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.MeasureHelper.measure(int,int)",this,widthSpec,heightSpec);try{Rect p = mComponent.getPaddings();
        setMeasuredSize(
                getLength(widthSpec, mPreferredWidth + p.left + p.right),
                getLength(heightSpec, mPreferredHeight + p.top + p.bottom));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.MeasureHelper.measure(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.MeasureHelper.measure(int,int)",this,throwable);throw throwable;}
    }

    private static int getLength(int measureSpec, int prefered) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.ui.MeasureHelper.getLength(int,int)",measureSpec,prefered);try{int specLength = MeasureSpec.getSize(measureSpec);
        switch(MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY: {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.MeasureHelper.getLength(int,int)");return specLength;}
            case MeasureSpec.AT_MOST: {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.MeasureHelper.getLength(int,int)");return Math.min(prefered, specLength);}
            default: {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.MeasureHelper.getLength(int,int)");return prefered;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.ui.MeasureHelper.getLength(int,int)",throwable);throw throwable;}
    }

    protected void setMeasuredSize(int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.MeasureHelper.setMeasuredSize(int,int)",this,width,height);try{mComponent.setMeasuredSize(width, height);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.MeasureHelper.setMeasuredSize(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.MeasureHelper.setMeasuredSize(int,int)",this,throwable);throw throwable;}
    }

}
