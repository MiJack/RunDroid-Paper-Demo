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
import android.graphics.Path;
import android.util.AttributeSet;

import com.android.gallery3d.photoeditor.filters.DoodleFilter;

/**
 * An action handling doodle effect.
 */
public class DoodleAction extends EffectAction {

    private static final int DEFAULT_COLOR_INDEX = 4;

    private DoodleFilter filter;
    private ColorSeekBar colorPicker;
    private DoodleView doodleView;

    public DoodleAction(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void doBegin() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleAction.doBegin()",this);try{filter = new DoodleFilter();

        colorPicker = factory.createColorPicker();
        colorPicker.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {

            @Override
            public void onColorChanged(int color, boolean fromUser) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleAction$1.onColorChanged(int,boolean)",this,color,fromUser);try{if (fromUser) {
                    doodleView.setColor(color);
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleAction$1.onColorChanged(int,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleAction$1.onColorChanged(int,boolean)",this,throwable);throw throwable;}
            }
        });
        colorPicker.setColorIndex(DEFAULT_COLOR_INDEX);

        doodleView = factory.createDoodleView();
        doodleView.setOnDoodleChangeListener(new DoodleView.OnDoodleChangeListener() {

            @Override
            public void onDoodleInPhotoBounds() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleAction$2.onDoodleInPhotoBounds()",this);try{/*// Notify the user has drawn within photo bounds and made visible changes on photo.*/
                filter.setDoodledInPhotoBounds();
                notifyFilterChanged(filter, false);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleAction$2.onDoodleInPhotoBounds()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleAction$2.onDoodleInPhotoBounds()",this,throwable);throw throwable;}
            }

            @Override
            public void onDoodleFinished(Path path, int color) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleAction$2.onDoodleFinished(android.graphics.Path,int)",this,path,color);try{filter.addPath(path, color);
                notifyFilterChanged(filter, false);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleAction$2.onDoodleFinished(android.graphics.Path,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleAction$2.onDoodleFinished(android.graphics.Path,int)",this,throwable);throw throwable;}
            }
        });
        doodleView.setColor(colorPicker.getColor());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleAction.doBegin()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleAction.doBegin()",this,throwable);throw throwable;}
    }

    @Override
    public void doEnd() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleAction.doEnd()",this);try{colorPicker.setOnColorChangeListener(null);
        doodleView.setOnDoodleChangeListener(null);
        notifyFilterChanged(filter, true);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleAction.doEnd()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleAction.doEnd()",this,throwable);throw throwable;}
    }
}
