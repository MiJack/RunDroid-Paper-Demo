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

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.photoeditor.PhotoView;
import com.android.gallery3d.photoeditor.filters.RotateFilter;

/**
 * An action handling rotate effect.
 */
public class RotateAction extends EffectAction {

    private static final float DEFAULT_ANGLE = 0.0f;
    private static final float DEFAULT_ROTATE_SPAN = 360.0f;

    private RotateFilter filter;
    private float rotateDegrees;
    private Runnable queuedRotationChange;
    private RotateView rotateView;

    public RotateAction(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void doBegin() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateAction.doBegin()",this);try{filter = new RotateFilter();

        rotateView = factory.createRotateView();
        rotateView.setOnRotateChangeListener(new RotateView.OnRotateChangeListener() {

            /*// Directly transform photo-view because running the rotate filter isn't fast enough.*/
            PhotoView photoView = (PhotoView) rotateView.getRootView().findViewById(
                    R.id.photo_view);

            @Override
            public void onAngleChanged(float degrees, boolean fromUser){
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateAction$1.onAngleChanged(float,boolean)",this,degrees,fromUser);try{if (fromUser) {
                    rotateDegrees = degrees;
                    updateRotateFilter(false);
                    transformPhotoView(degrees);
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateAction$1.onAngleChanged(float,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateAction$1.onAngleChanged(float,boolean)",this,throwable);throw throwable;}
            }

            @Override
            public void onStartTrackingTouch() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateAction$1.onStartTrackingTouch()",this);try{/*// no-op*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateAction$1.onStartTrackingTouch()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateAction$1.onStartTrackingTouch()",this,throwable);throw throwable;}
            }

            @Override
            public void onStopTrackingTouch() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateAction$1.onStopTrackingTouch()",this);try{roundRotateDegrees();
                updateRotateFilter(false);
                transformPhotoView(rotateDegrees);
                rotateView.setRotatedAngle(rotateDegrees);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateAction$1.onStopTrackingTouch()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateAction$1.onStopTrackingTouch()",this,throwable);throw throwable;}
            }

            private void transformPhotoView(final float degrees) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateAction$1.transformPhotoView(float)",this,degrees);try{/*// Remove the outdated rotation change before queuing a new one.*/
                if (queuedRotationChange != null) {
                    photoView.remove(queuedRotationChange);
                }
                queuedRotationChange = new Runnable() {

                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateAction$1$1.run()",this);try{photoView.rotatePhoto(degrees);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateAction$1$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateAction$1$1.run()",this,throwable);throw throwable;}
                    }
                };
                photoView.queue(queuedRotationChange);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateAction$1.transformPhotoView(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateAction$1.transformPhotoView(float)",this,throwable);throw throwable;}
            }
        });
        rotateView.setRotatedAngle(DEFAULT_ANGLE);
        rotateView.setRotateSpan(DEFAULT_ROTATE_SPAN);
        rotateDegrees = 0;
        queuedRotationChange = null;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateAction.doBegin()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateAction.doBegin()",this,throwable);throw throwable;}
    }

    @Override
    public void doEnd() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateAction.doEnd()",this);try{rotateView.setOnRotateChangeListener(null);
        /*// Round the current rotation degrees in case rotation tracking has not stopped yet.*/
        roundRotateDegrees();
        updateRotateFilter(true);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateAction.doEnd()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateAction.doEnd()",this,throwable);throw throwable;}
    }

    /**
     * Rounds rotate degrees to multiples of 90 degrees.
     */
    private void roundRotateDegrees() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateAction.roundRotateDegrees()",this);try{if (rotateDegrees % 90 != 0) {
            rotateDegrees = Math.round(rotateDegrees / 90) * 90;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateAction.roundRotateDegrees()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateAction.roundRotateDegrees()",this,throwable);throw throwable;}
    }

    private void updateRotateFilter(boolean outputFilter) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateAction.updateRotateFilter(boolean)",this,outputFilter);try{filter.setAngle(rotateDegrees);
        notifyFilterChanged(filter, outputFilter);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateAction.updateRotateFilter(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateAction.updateRotateFilter(boolean)",this,throwable);throw throwable;}
    }
}
