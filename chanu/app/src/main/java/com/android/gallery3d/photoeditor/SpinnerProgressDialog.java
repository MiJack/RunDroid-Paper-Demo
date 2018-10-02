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

import android.app.Dialog;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.chanapps.four.gallery3d.R;

/**
 * Spinner model progress dialog that disables all tools for user interaction after it shows up and
 * and re-enables them after it dismisses.
 */
public class SpinnerProgressDialog extends Dialog {

    private final ViewGroup tools;

    public static SpinnerProgressDialog show(ViewGroup tools) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.photoeditor.SpinnerProgressDialog com.android.gallery3d.photoeditor.SpinnerProgressDialog.show(android.view.ViewGroup)",tools);try{SpinnerProgressDialog dialog = new SpinnerProgressDialog(tools);
        dialog.setCancelable(false);
        dialog.show();
        {com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.photoeditor.SpinnerProgressDialog com.android.gallery3d.photoeditor.SpinnerProgressDialog.show(android.view.ViewGroup)");return dialog;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.photoeditor.SpinnerProgressDialog com.android.gallery3d.photoeditor.SpinnerProgressDialog.show(android.view.ViewGroup)",throwable);throw throwable;}
    }

    private SpinnerProgressDialog(ViewGroup tools) {
        super(tools.getContext(), R.style.SpinnerProgressDialog);

        addContentView(new ProgressBar(tools.getContext()), new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        this.tools = tools;
        enableTools(false);
    }

    @Override
    public void dismiss() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.SpinnerProgressDialog.dismiss()",this);try{super.dismiss();

        enableTools(true);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.SpinnerProgressDialog.dismiss()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.SpinnerProgressDialog.dismiss()",this,throwable);throw throwable;}
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.SpinnerProgressDialog.onTouchEvent(android.view.MotionEvent)",this,event);try{super.onTouchEvent(event);

        /*// Pass touch events to tools for killing idle even when the progress dialog is shown.*/
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.SpinnerProgressDialog.onTouchEvent(android.view.MotionEvent)",this);return tools.dispatchTouchEvent(event);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.SpinnerProgressDialog.onTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    private void enableTools(boolean enabled) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.SpinnerProgressDialog.enableTools(boolean)",this,enabled);try{for (int i = 0; i < tools.getChildCount(); i++) {
            tools.getChildAt(i).setEnabled(enabled);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.SpinnerProgressDialog.enableTools(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.SpinnerProgressDialog.enableTools(boolean)",this,throwable);throw throwable;}
    }
}
