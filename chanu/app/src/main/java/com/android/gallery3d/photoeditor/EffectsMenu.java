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

package com.android.gallery3d.photoeditor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.chanapps.four.gallery3d.R;

/**
 * Effects menu that contains toggles mapping to corresponding groups of effects.
 */
public class EffectsMenu extends RestorableView {

    /**
     * Listener of toggle changes.
     */
    public interface OnToggleListener {

        /**
         * Listens to the selected status and mapped effects-id of the clicked toggle.
         *
         * @return true to make the toggle selected; otherwise, make it unselected.
         */
        boolean onToggle(boolean isSelected, int effectsId);
    }

    public EffectsMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int childLayoutId() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.photoeditor.EffectsMenu.childLayoutId()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.photoeditor.EffectsMenu.childLayoutId()",this);return R.layout.photoeditor_effects_menu;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.photoeditor.EffectsMenu.childLayoutId()",this,throwable);throw throwable;}
    }

    public void setOnToggleListener(OnToggleListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsMenu.setOnToggleListener(OnToggleListener)",this,listener);try{setToggleRunnalbe(listener, R.id.exposure_button, R.layout.photoeditor_effects_exposure);
        setToggleRunnalbe(listener, R.id.artistic_button, R.layout.photoeditor_effects_artistic);
        setToggleRunnalbe(listener, R.id.color_button, R.layout.photoeditor_effects_color);
        setToggleRunnalbe(listener, R.id.fix_button, R.layout.photoeditor_effects_fix);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsMenu.setOnToggleListener(OnToggleListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsMenu.setOnToggleListener(OnToggleListener)",this,throwable);throw throwable;}
    }

    private void setToggleRunnalbe(final OnToggleListener listener, final int toggleId,
            final int effectsId) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsMenu.setToggleRunnalbe(OnToggleListener,int,int)",this,listener,toggleId,effectsId);try{setClickRunnable(toggleId, new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsMenu$1.run()",this);try{boolean selected = findViewById(toggleId).isSelected();
                setViewSelected(toggleId, listener.onToggle(selected, effectsId));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsMenu$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsMenu$1.run()",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsMenu.setToggleRunnalbe(OnToggleListener,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsMenu.setToggleRunnalbe(OnToggleListener,int,int)",this,throwable);throw throwable;}
    }

    public void clearSelected() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.EffectsMenu.clearSelected()",this);try{ViewGroup menu = (ViewGroup) findViewById(R.id.toggles);
        for (int i = 0; i < menu.getChildCount(); i++) {
            View toggle = menu.getChildAt(i);
            if (toggle.isSelected()) {
                setViewSelected(toggle.getId(), false);
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.EffectsMenu.clearSelected()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.EffectsMenu.clearSelected()",this,throwable);throw throwable;}
    }
}
