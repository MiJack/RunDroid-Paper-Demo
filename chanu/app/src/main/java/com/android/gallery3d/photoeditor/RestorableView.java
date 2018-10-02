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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * View that holds a single child and could be recreated/restored after orientation changes.
 */
public abstract class RestorableView extends FrameLayout {

    private static final float ENABLED_ALPHA = 1;
    private static final float DISABLED_ALPHA = 0.47f;

    private final HashMap<Integer, Runnable> clickRunnables = new HashMap<Integer, Runnable>();
    private final HashSet<Integer> changedViews = new HashSet<Integer>();
    private final LayoutInflater inflater;

    public RestorableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    protected abstract int childLayoutId();

    private void recreateChildView() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.RestorableView.recreateChildView()",this);try{if (getChildCount() != 0) {
            removeAllViews();
        }
        inflater.inflate(childLayoutId(), this, true);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.RestorableView.recreateChildView()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.RestorableView.recreateChildView()",this,throwable);throw throwable;}
    }

    @Override
    protected void onFinishInflate() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.RestorableView.onFinishInflate()",this);try{super.onFinishInflate();
        recreateChildView();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.RestorableView.onFinishInflate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.RestorableView.onFinishInflate()",this,throwable);throw throwable;}
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.RestorableView.onConfigurationChanged(android.content.res.Configuration)",this,newConfig);try{super.onConfigurationChanged(newConfig);

        /*// Remember the removing child before recreating the child.*/
        View view = getChildAt(0);
        recreateChildView();

        /*// Restore its runnables and status of views that have been changed.*/
        for (Entry<Integer, Runnable> entry : clickRunnables.entrySet()) {
            setClickRunnable(entry.getKey(), entry.getValue());
        }
        for (int id : changedViews) {
            View changed = view.findViewById(id);
            setViewEnabled(id, changed.isEnabled());
            setViewSelected(id, changed.isSelected());
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.RestorableView.onConfigurationChanged(android.content.res.Configuration)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.RestorableView.onConfigurationChanged(android.content.res.Configuration)",this,throwable);throw throwable;}
    }

    public void setClickRunnable(int id, final Runnable r) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.RestorableView.setClickRunnable(int,java.lang.Runnable)",this,id,r);try{findViewById(id).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.RestorableView$1.onClick(android.view.View)",this,v);try{if (isEnabled()) {
                    r.run();
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.RestorableView$1.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.RestorableView$1.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });
        clickRunnables.put(id, r);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.RestorableView.setClickRunnable(int,java.lang.Runnable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.RestorableView.setClickRunnable(int,java.lang.Runnable)",this,throwable);throw throwable;}
    }

    public void setViewEnabled(int id, boolean enabled) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.RestorableView.setViewEnabled(int,boolean)",this,id,enabled);try{View view = findViewById(id);
        view.setEnabled(enabled);
        view.setAlpha(enabled ? ENABLED_ALPHA : DISABLED_ALPHA);
        /*// Track views whose enabled status has been updated.*/
        changedViews.add(id);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.RestorableView.setViewEnabled(int,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.RestorableView.setViewEnabled(int,boolean)",this,throwable);throw throwable;}
    }

    public void setViewSelected(int id, boolean selected) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.RestorableView.setViewSelected(int,boolean)",this,id,selected);try{findViewById(id).setSelected(selected);
        /*// Track views whose selected status has been updated.*/
        changedViews.add(id);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.RestorableView.setViewSelected(int,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.RestorableView.setViewSelected(int,boolean)",this,throwable);throw throwable;}
    }
}
