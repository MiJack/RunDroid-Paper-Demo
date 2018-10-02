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

package com.android.gallery3d.photoeditor.actions;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.android.gallery3d.photoeditor.filters.RedEyeFilter;

/**
 * An action handling red-eye removal.
 */
public class RedEyeAction extends EffectAction {

    private TouchView touchView;

    public RedEyeAction(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void doBegin() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RedEyeAction.doBegin()",this);try{final RedEyeFilter filter = new RedEyeFilter();

        touchView = factory.createTouchView();
        touchView.setSingleTapListener(new TouchView.SingleTapListener() {

            @Override
            public void onSingleTap(PointF point) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RedEyeAction$1.onSingleTap(android.graphics.PointF)",this,point);try{filter.addRedEyePosition(point);
                notifyFilterChanged(filter, true);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RedEyeAction$1.onSingleTap(android.graphics.PointF)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RedEyeAction$1.onSingleTap(android.graphics.PointF)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RedEyeAction.doBegin()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RedEyeAction.doBegin()",this,throwable);throw throwable;}
    }

    @Override
    public void doEnd() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RedEyeAction.doEnd()",this);try{touchView.setSingleTapListener(null);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RedEyeAction.doEnd()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RedEyeAction.doEnd()",this,throwable);throw throwable;}
    }
}
