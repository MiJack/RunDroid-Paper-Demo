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

import com.android.gallery3d.photoeditor.filters.FaceliftFilter;

/**
 * An action handling facelift effect.
 */
public class FaceliftAction extends EffectAction {

    private static final float DEFAULT_SCALE = 0.5f;

    private ScaleSeekBar scalePicker;

    public FaceliftAction(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void doBegin() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FaceliftAction.doBegin()",this);try{final FaceliftFilter filter = new FaceliftFilter();

        scalePicker = factory.createScalePicker(EffectToolFactory.ScalePickerType.GENERIC);
        scalePicker.setOnScaleChangeListener(new ScaleSeekBar.OnScaleChangeListener() {

            @Override
            public void onProgressChanged(float progress, boolean fromUser) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FaceliftAction$1.onProgressChanged(float,boolean)",this,progress,fromUser);try{if (fromUser) {
                    filter.setScale(progress);
                    notifyFilterChanged(filter, true);
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FaceliftAction$1.onProgressChanged(float,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FaceliftAction$1.onProgressChanged(float,boolean)",this,throwable);throw throwable;}
            }
        });
        scalePicker.setProgress(DEFAULT_SCALE);

        filter.setScale(DEFAULT_SCALE);
        notifyFilterChanged(filter, true);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FaceliftAction.doBegin()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FaceliftAction.doBegin()",this,throwable);throw throwable;}
    }

    @Override
    public void doEnd() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FaceliftAction.doEnd()",this);try{scalePicker.setOnScaleChangeListener(null);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FaceliftAction.doEnd()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FaceliftAction.doEnd()",this,throwable);throw throwable;}
    }
}
