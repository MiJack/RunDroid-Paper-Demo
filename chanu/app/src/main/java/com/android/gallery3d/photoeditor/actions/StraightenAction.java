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
import android.util.AttributeSet;

import com.android.gallery3d.photoeditor.filters.StraightenFilter;

/**
 * An action handling straighten effect.
 */
public class StraightenAction extends EffectAction {

    private static final float DEFAULT_ANGLE = 0.0f;
    private static final float DEFAULT_ROTATE_SPAN = StraightenFilter.MAX_DEGREES * 2;

    private RotateView rotateView;

    public StraightenAction(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void doBegin() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.StraightenAction.doBegin()",this);try{final StraightenFilter filter = new StraightenFilter();

        rotateView = factory.createRotateView();
        rotateView.setOnRotateChangeListener(new RotateView.OnRotateChangeListener() {

            @Override
            public void onAngleChanged(float degrees, boolean fromUser){
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.StraightenAction$1.onAngleChanged(float,boolean)",this,degrees,fromUser);try{if (fromUser) {
                    filter.setAngle(degrees);
                    notifyFilterChanged(filter, true);
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.StraightenAction$1.onAngleChanged(float,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.StraightenAction$1.onAngleChanged(float,boolean)",this,throwable);throw throwable;}
            }

            @Override
            public void onStartTrackingTouch() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.StraightenAction$1.onStartTrackingTouch()",this);try{/*// no-op*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.StraightenAction$1.onStartTrackingTouch()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.StraightenAction$1.onStartTrackingTouch()",this,throwable);throw throwable;}
            }

            @Override
            public void onStopTrackingTouch() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.StraightenAction$1.onStopTrackingTouch()",this);try{/*// no-op*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.StraightenAction$1.onStopTrackingTouch()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.StraightenAction$1.onStopTrackingTouch()",this,throwable);throw throwable;}
            }
        });
        rotateView.setDrawGrids(true);
        rotateView.setRotatedAngle(DEFAULT_ANGLE);
        rotateView.setRotateSpan(DEFAULT_ROTATE_SPAN);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.StraightenAction.doBegin()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.StraightenAction.doBegin()",this,throwable);throw throwable;}
    }

    @Override
    public void doEnd() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.StraightenAction.doEnd()",this);try{rotateView.setOnRotateChangeListener(null);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.StraightenAction.doEnd()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.StraightenAction.doEnd()",this,throwable);throw throwable;}
    }
}
