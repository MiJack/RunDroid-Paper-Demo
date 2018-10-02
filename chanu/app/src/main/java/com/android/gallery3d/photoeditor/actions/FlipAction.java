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
import com.android.gallery3d.photoeditor.filters.FlipFilter;

/**
 * An action handling flip effect.
 */
public class FlipAction extends EffectAction {

    private static final float DEFAULT_ANGLE = 0.0f;
    private static final float DEFAULT_FLIP_SPAN = 180.0f;

    private FlipFilter filter;
    private float horizontalFlipDegrees;
    private float verticalFlipDegrees;
    private Runnable queuedFlipChange;
    private FlipView flipView;

    public FlipAction(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void doBegin() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipAction.doBegin()",this);try{filter = new FlipFilter();

        flipView = factory.createFlipView();
        flipView.setOnFlipChangeListener(new FlipView.OnFlipChangeListener() {

            /*// Directly transform photo-view because running the flip filter isn't fast enough.*/
            PhotoView photoView = (PhotoView) flipView.getRootView().findViewById(
                    R.id.photo_view);

            @Override
            public void onAngleChanged(float horizontalDegrees, float verticalDegrees,
                    boolean fromUser) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipAction$1.onAngleChanged(float,float,boolean)",this,horizontalDegrees,verticalDegrees,fromUser);try{if (fromUser) {
                    horizontalFlipDegrees = horizontalDegrees;
                    verticalFlipDegrees = verticalDegrees;
                    updateFlipFilter(false);
                    transformPhotoView(horizontalDegrees, verticalDegrees);
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipAction$1.onAngleChanged(float,float,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipAction$1.onAngleChanged(float,float,boolean)",this,throwable);throw throwable;}
            }

            @Override
            public void onStartTrackingTouch() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipAction$1.onStartTrackingTouch()",this);try{/*// no-op*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipAction$1.onStartTrackingTouch()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipAction$1.onStartTrackingTouch()",this,throwable);throw throwable;}
            }

            @Override
            public void onStopTrackingTouch() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipAction$1.onStopTrackingTouch()",this);try{roundFlipDegrees();
                updateFlipFilter(false);
                transformPhotoView(horizontalFlipDegrees, verticalFlipDegrees);
                flipView.setFlippedAngles(horizontalFlipDegrees, verticalFlipDegrees);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipAction$1.onStopTrackingTouch()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipAction$1.onStopTrackingTouch()",this,throwable);throw throwable;}
            }

            private void transformPhotoView(final float horizontalDegrees,
                    final float verticalDegrees) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipAction$1.transformPhotoView(float,float)",this,horizontalDegrees,verticalDegrees);try{/*// Remove the outdated flip change before queuing a new one.*/
                if (queuedFlipChange != null) {
                    photoView.remove(queuedFlipChange);
                }
                queuedFlipChange = new Runnable() {

                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipAction$1$1.run()",this);try{photoView.flipPhoto(horizontalDegrees, verticalDegrees);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipAction$1$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipAction$1$1.run()",this,throwable);throw throwable;}
                    }
                };
                photoView.queue(queuedFlipChange);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipAction$1.transformPhotoView(float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipAction$1.transformPhotoView(float,float)",this,throwable);throw throwable;}
            }
        });
        flipView.setFlippedAngles(DEFAULT_ANGLE, DEFAULT_ANGLE);
        flipView.setFlipSpan(DEFAULT_FLIP_SPAN);
        horizontalFlipDegrees = 0;
        verticalFlipDegrees = 0;
        queuedFlipChange = null;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipAction.doBegin()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipAction.doBegin()",this,throwable);throw throwable;}
    }

    @Override
    public void doEnd() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipAction.doEnd()",this);try{flipView.setOnFlipChangeListener(null);
        /*// Round the current flip degrees in case flip tracking has not stopped yet.*/
        roundFlipDegrees();
        updateFlipFilter(true);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipAction.doEnd()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipAction.doEnd()",this,throwable);throw throwable;}
    }

    /**
     * Rounds flip degrees to multiples of 180 degrees.
     */
    private void roundFlipDegrees() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipAction.roundFlipDegrees()",this);try{if (horizontalFlipDegrees % 180 != 0) {
            horizontalFlipDegrees = Math.round(horizontalFlipDegrees / 180) * 180;
        }
        if (verticalFlipDegrees % 180 != 0) {
            verticalFlipDegrees = Math.round(verticalFlipDegrees / 180) * 180;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipAction.roundFlipDegrees()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipAction.roundFlipDegrees()",this,throwable);throw throwable;}
    }

    private void updateFlipFilter(boolean outputFilter) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipAction.updateFlipFilter(boolean)",this,outputFilter);try{/*// Flip the filter if the flipped degrees are at the opposite directions.*/
        filter.setFlip(((int) horizontalFlipDegrees / 180) % 2 != 0,
                ((int) verticalFlipDegrees / 180) % 2 != 0);
        notifyFilterChanged(filter, outputFilter);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipAction.updateFlipFilter(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipAction.updateFlipFilter(boolean)",this,throwable);throw throwable;}
    }
}
