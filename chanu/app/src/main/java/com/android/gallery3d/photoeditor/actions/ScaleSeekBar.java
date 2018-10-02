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
import android.widget.SeekBar;

/**
 * Seek-bar that has a draggable thumb to set and get the normalized scale value from 0 to 1.
 */
class ScaleSeekBar extends AbstractSeekBar {

    /**
     * Listens to scale changes.
     */
    public interface OnScaleChangeListener {

        void onProgressChanged(float progress, boolean fromUser);
    }

    public ScaleSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        setMax(100);
    }

    public void setOnScaleChangeListener(final OnScaleChangeListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar.setOnScaleChangeListener(OnScaleChangeListener)",this,listener);try{setOnSeekBarChangeListener((listener == null) ? null : new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar$1.onProgressChanged(android.widget.SeekBar,int,boolean)",this,seekBar,progress,fromUser);try{listener.onProgressChanged((float) progress / getMax(), fromUser);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar$1.onProgressChanged(android.widget.SeekBar,int,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar$1.onProgressChanged(android.widget.SeekBar,int,boolean)",this,throwable);throw throwable;}
            }

            {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar$1.onStartTrackingTouch(android.widget.SeekBar)",this,seekBar);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar$1.onStartTrackingTouch(android.widget.SeekBar)",this);}

            {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar$1.onStopTrackingTouch(android.widget.SeekBar)",this,seekBar);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar$1.onStopTrackingTouch(android.widget.SeekBar)",this);}
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar.setOnScaleChangeListener(OnScaleChangeListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar.setOnScaleChangeListener(OnScaleChangeListener)",this,throwable);throw throwable;}
    }

    public void setProgress(float progress) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar.setProgress(float)",this,progress);try{setProgress((int) (progress * getMax()));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar.setProgress(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.ScaleSeekBar.setProgress(float)",this,throwable);throw throwable;}
    }
}
