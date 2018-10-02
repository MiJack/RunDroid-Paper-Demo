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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.photoeditor.PhotoView;

/**
 * Factory to create tools that will be used by effect actions.
 */
public class EffectToolFactory {

    public enum ScalePickerType {
        LIGHT, SHADOW, COLOR, GENERIC
    }

    private final ViewGroup effectToolPanel;
    private final LayoutInflater inflater;

    public EffectToolFactory(ViewGroup effectToolPanel, LayoutInflater inflater) {
        this.effectToolPanel = effectToolPanel;
        this.inflater = inflater;
    }

    private View createFullscreenTool(int toolId) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.android.gallery3d.photoeditor.actions.EffectToolFactory.createFullscreenTool(int)",this,toolId);try{/*// Create full screen effect tool on top of photo-view and place it within the same*/
        /*// view group that contains photo-view.*/
        View photoView = effectToolPanel.getRootView().findViewById(R.id.photo_view);
        ViewGroup parent = (ViewGroup) photoView.getParent();
        FullscreenToolView view = (FullscreenToolView) inflater.inflate(toolId, parent, false);
        view.setPhotoBounds(((PhotoView) photoView).getPhotoBounds());
        parent.addView(view, parent.indexOfChild(photoView) + 1);
        {com.mijack.Xlog.logMethodExit("android.view.View com.android.gallery3d.photoeditor.actions.EffectToolFactory.createFullscreenTool(int)",this);return view;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.android.gallery3d.photoeditor.actions.EffectToolFactory.createFullscreenTool(int)",this,throwable);throw throwable;}
    }

    private View createPanelTool(int toolId) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.android.gallery3d.photoeditor.actions.EffectToolFactory.createPanelTool(int)",this,toolId);try{View view = inflater.inflate(toolId, effectToolPanel, false);
        effectToolPanel.addView(view, 0);
        {com.mijack.Xlog.logMethodExit("android.view.View com.android.gallery3d.photoeditor.actions.EffectToolFactory.createPanelTool(int)",this);return view;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.android.gallery3d.photoeditor.actions.EffectToolFactory.createPanelTool(int)",this,throwable);throw throwable;}
    }

    private int getScalePickerBackground(ScalePickerType type) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.photoeditor.actions.EffectToolFactory.getScalePickerBackground(ScalePickerType)",this,type);try{switch (type) {
            case LIGHT:
                {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.photoeditor.actions.EffectToolFactory.getScalePickerBackground(ScalePickerType)",this);return R.drawable.photoeditor_scale_seekbar_light;}

            case SHADOW:
                {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.photoeditor.actions.EffectToolFactory.getScalePickerBackground(ScalePickerType)",this);return R.drawable.photoeditor_scale_seekbar_shadow;}

            case COLOR:
                {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.photoeditor.actions.EffectToolFactory.getScalePickerBackground(ScalePickerType)",this);return R.drawable.photoeditor_scale_seekbar_color;}
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.photoeditor.actions.EffectToolFactory.getScalePickerBackground(ScalePickerType)",this);return R.drawable.photoeditor_scale_seekbar_generic;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.photoeditor.actions.EffectToolFactory.getScalePickerBackground(ScalePickerType)",this,throwable);throw throwable;}
    }

    public ScaleSeekBar createScalePicker(ScalePickerType type) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.photoeditor.actions.ScaleSeekBar com.android.gallery3d.photoeditor.actions.EffectToolFactory.createScalePicker(ScalePickerType)",this,type);try{ScaleSeekBar scalePicker = (ScaleSeekBar) createPanelTool(
                R.layout.photoeditor_scale_seekbar);
        scalePicker.setBackgroundResource(getScalePickerBackground(type));
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.photoeditor.actions.ScaleSeekBar com.android.gallery3d.photoeditor.actions.EffectToolFactory.createScalePicker(ScalePickerType)",this);return scalePicker;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.photoeditor.actions.ScaleSeekBar com.android.gallery3d.photoeditor.actions.EffectToolFactory.createScalePicker(ScalePickerType)",this,throwable);throw throwable;}
    }

    public ColorSeekBar createColorPicker() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.photoeditor.actions.ColorSeekBar com.android.gallery3d.photoeditor.actions.EffectToolFactory.createColorPicker()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.photoeditor.actions.ColorSeekBar com.android.gallery3d.photoeditor.actions.EffectToolFactory.createColorPicker()",this);return (ColorSeekBar) createPanelTool(R.layout.photoeditor_color_seekbar);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.photoeditor.actions.ColorSeekBar com.android.gallery3d.photoeditor.actions.EffectToolFactory.createColorPicker()",this,throwable);throw throwable;}
    }

    public DoodleView createDoodleView() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.photoeditor.actions.DoodleView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createDoodleView()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.photoeditor.actions.DoodleView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createDoodleView()",this);return (DoodleView) createFullscreenTool(R.layout.photoeditor_doodle_view);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.photoeditor.actions.DoodleView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createDoodleView()",this,throwable);throw throwable;}
    }

    public TouchView createTouchView() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.photoeditor.actions.TouchView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createTouchView()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.photoeditor.actions.TouchView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createTouchView()",this);return (TouchView) createFullscreenTool(R.layout.photoeditor_touch_view);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.photoeditor.actions.TouchView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createTouchView()",this,throwable);throw throwable;}
    }

    public FlipView createFlipView() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.photoeditor.actions.FlipView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createFlipView()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.photoeditor.actions.FlipView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createFlipView()",this);return (FlipView) createFullscreenTool(R.layout.photoeditor_flip_view);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.photoeditor.actions.FlipView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createFlipView()",this,throwable);throw throwable;}
    }

    public RotateView createRotateView() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.photoeditor.actions.RotateView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createRotateView()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.photoeditor.actions.RotateView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createRotateView()",this);return (RotateView) createFullscreenTool(R.layout.photoeditor_rotate_view);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.photoeditor.actions.RotateView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createRotateView()",this,throwable);throw throwable;}
    }

    public CropView createCropView() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.photoeditor.actions.CropView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createCropView()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.photoeditor.actions.CropView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createCropView()",this);return (CropView) createFullscreenTool(R.layout.photoeditor_crop_view);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.photoeditor.actions.CropView com.android.gallery3d.photoeditor.actions.EffectToolFactory.createCropView()",this,throwable);throw throwable;}
    }
}
