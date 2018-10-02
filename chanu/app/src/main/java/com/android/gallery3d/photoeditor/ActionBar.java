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
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.ViewSwitcher;

import com.chanapps.four.gallery3d.R;

/**
 * Action bar that contains buttons such as undo, redo, save, etc.
 */
public class ActionBar extends RestorableView {

    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int childLayoutId() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.photoeditor.ActionBar.childLayoutId()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.photoeditor.ActionBar.childLayoutId()",this);return R.layout.photoeditor_actionbar;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.photoeditor.ActionBar.childLayoutId()",this,throwable);throw throwable;}
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.ActionBar.onLayout(boolean,int,int,int,int)",this,changed,l,t,r,b);try{super.onLayout(changed, l, t, r, b);

        /*// Show the action-bar title only when there's still room for it; otherwise, hide it.*/
        int width = 0;
        for (int i = 0; i < getChildCount(); i++) {
            width += getChildAt(i).getWidth();
        }
        findViewById(R.id.action_bar_title).setVisibility(((width > r - l)) ? INVISIBLE: VISIBLE);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.ActionBar.onLayout(boolean,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.ActionBar.onLayout(boolean,int,int,int,int)",this,throwable);throw throwable;}
    }

    @Override
    protected void onFinishInflate() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.ActionBar.onFinishInflate()",this);try{super.onFinishInflate();
        updateButtons(false, false);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.ActionBar.onFinishInflate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.ActionBar.onFinishInflate()",this,throwable);throw throwable;}
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.ActionBar.onConfigurationChanged(android.content.res.Configuration)",this,newConfig);try{super.onConfigurationChanged(newConfig);
        showSaveOrShare();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.ActionBar.onConfigurationChanged(android.content.res.Configuration)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.ActionBar.onConfigurationChanged(android.content.res.Configuration)",this,throwable);throw throwable;}
    }

    /**
     * Save/share button may need being switched when undo/save enabled status is changed/restored.
     */
    private void showSaveOrShare() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.ActionBar.showSaveOrShare()",this);try{/*// Show share-button only after photo is edited and saved; otherwise, show save-button.*/
        boolean showShare = findViewById(R.id.undo_button).isEnabled()
                && !findViewById(R.id.save_button).isEnabled();
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.save_share_buttons);
        int next = switcher.getNextView().getId();
        if ((showShare && (next == R.id.share_button))
                || (!showShare && (next == R.id.save_button))) {
            switcher.showNext();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.ActionBar.showSaveOrShare()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.ActionBar.showSaveOrShare()",this,throwable);throw throwable;}
    }

    public void updateButtons(boolean canUndo, boolean canRedo) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.ActionBar.updateButtons(boolean,boolean)",this,canUndo,canRedo);try{setViewEnabled(R.id.undo_button, canUndo);
        setViewEnabled(R.id.redo_button, canRedo);
        setViewEnabled(R.id.save_button, canUndo);
        showSaveOrShare();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.ActionBar.updateButtons(boolean,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.ActionBar.updateButtons(boolean,boolean)",this,throwable);throw throwable;}
    }

    public void updateSave(boolean canSave) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.ActionBar.updateSave(boolean)",this,canSave);try{setViewEnabled(R.id.save_button, canSave);
        showSaveOrShare();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.ActionBar.updateSave(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.ActionBar.updateSave(boolean)",this,throwable);throw throwable;}
    }

    public void clickBack() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.ActionBar.clickBack()",this);try{findViewById(R.id.action_bar_back).performClick();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.ActionBar.clickBack()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.ActionBar.clickBack()",this,throwable);throw throwable;}
    }

    public void clickSave() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.ActionBar.clickSave()",this);try{findViewById(R.id.save_button).performClick();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.ActionBar.clickSave()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.ActionBar.clickSave()",this,throwable);throw throwable;}
    }

    public boolean canSave() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.ActionBar.canSave()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.ActionBar.canSave()",this);return findViewById(R.id.save_button).isEnabled();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.ActionBar.canSave()",this,throwable);throw throwable;}
    }
}
