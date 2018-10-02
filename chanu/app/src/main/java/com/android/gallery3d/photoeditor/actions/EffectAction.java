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
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.photoeditor.FilterStack;
import com.android.gallery3d.photoeditor.OnDoneCallback;
import com.android.gallery3d.photoeditor.filters.Filter;

/**
 * An action binding UI controls and effect operation for editing photo.
 */
public abstract class EffectAction extends LinearLayout {

    /**
     * Listener of effect action.
     */
    public interface Listener {

        void onClick();

        void onDone();
    }

    protected EffectToolFactory factory;

    private Listener listener;
    private Toast tooltip;
    private FilterStack filterStack;
    private boolean pushedFilter;
    private FilterChangedCallback lastFilterChangedCallback;

    public EffectAction(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListener(Listener l) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.EffectAction.setListener(Listener)",this,l);try{listener = l;
        findViewById(R.id.effect_button).setOnClickListener(
                (listener == null) ? null : new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.EffectAction$1.onClick(android.view.View)",this,v);try{listener.onClick();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.EffectAction$1.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.EffectAction$1.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.EffectAction.setListener(Listener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.EffectAction.setListener(Listener)",this,throwable);throw throwable;}
    }

    public CharSequence name() {
        com.mijack.Xlog.logMethodEnter("java.lang.CharSequence com.android.gallery3d.photoeditor.actions.EffectAction.name()",this);try{com.mijack.Xlog.logMethodExit("java.lang.CharSequence com.android.gallery3d.photoeditor.actions.EffectAction.name()",this);return ((TextView) findViewById(R.id.effect_label)).getText();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.CharSequence com.android.gallery3d.photoeditor.actions.EffectAction.name()",this,throwable);throw throwable;}
    }

    public void begin(FilterStack filterStack, EffectToolFactory factory) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.EffectAction.begin(com.android.gallery3d.photoeditor.FilterStack,com.android.gallery3d.photoeditor.actions.EffectToolFactory)",this,filterStack,factory);try{/*// This view is already detached from UI view hierarchy by reaching here; findViewById()*/
        /*// could only access its own child views from here.*/
        this.filterStack = filterStack;
        this.factory = factory;

        /*// Shows the tooltip if it's available.*/
        if (getTag() != null) {
            tooltip = Toast.makeText(getContext(), (String) getTag(), Toast.LENGTH_SHORT);
            tooltip.setGravity(Gravity.CENTER, 0, 0);
            tooltip.show();
        }
        doBegin();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.EffectAction.begin(com.android.gallery3d.photoeditor.FilterStack,com.android.gallery3d.photoeditor.actions.EffectToolFactory)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.EffectAction.begin(com.android.gallery3d.photoeditor.FilterStack,com.android.gallery3d.photoeditor.actions.EffectToolFactory)",this,throwable);throw throwable;}
    }

    /**
     * Ends the effect and then executes the runnable after the effect is finished.
     */
    public void end(final Runnable runnableOnODone) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.EffectAction.end(java.lang.Runnable)",this,runnableOnODone);try{doEnd();

        /*// Wait till last output callback is done before finishing.*/
        if ((lastFilterChangedCallback == null) || lastFilterChangedCallback.done) {
            finish(runnableOnODone);
        } else {
            lastFilterChangedCallback.runnableOnReady = new Runnable() {

                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.EffectAction$2.run()",this);try{finish(runnableOnODone);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.EffectAction$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.EffectAction$2.run()",this,throwable);throw throwable;}
                }
            };
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.EffectAction.end(java.lang.Runnable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.EffectAction.end(java.lang.Runnable)",this,throwable);throw throwable;}
    }

    private void finish(Runnable runnableOnDone) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.EffectAction.finish(java.lang.Runnable)",this,runnableOnDone);try{/*// Close the tooltip if it's still showing.*/
        if ((tooltip != null) && (tooltip.getView().getParent() != null)) {
            tooltip.cancel();
            tooltip = null;
        }
        pushedFilter = false;
        lastFilterChangedCallback = null;

        runnableOnDone.run();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.EffectAction.finish(java.lang.Runnable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.EffectAction.finish(java.lang.Runnable)",this,throwable);throw throwable;}
    }

    protected void notifyDone() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.EffectAction.notifyDone()",this);try{if (listener != null) {
            listener.onDone();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.EffectAction.notifyDone()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.EffectAction.notifyDone()",this,throwable);throw throwable;}
    }

    protected void notifyFilterChanged(Filter filter, boolean output) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.EffectAction.notifyFilterChanged(com.android.gallery3d.photoeditor.filters.Filter,boolean)",this,filter,output);try{if (!pushedFilter && filter.isValid()) {
            filterStack.pushFilter(filter);
            pushedFilter = true;
        }
        if (pushedFilter && output) {
            /*// Notify the stack to execute the changed top filter and output the results.*/
            lastFilterChangedCallback = new FilterChangedCallback();
            filterStack.topFilterChanged(lastFilterChangedCallback);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.EffectAction.notifyFilterChanged(com.android.gallery3d.photoeditor.filters.Filter,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.EffectAction.notifyFilterChanged(com.android.gallery3d.photoeditor.filters.Filter,boolean)",this,throwable);throw throwable;}
    }

    /**
     * Subclasses should creates a specific filter and binds the filter to necessary UI controls
     * here when the action is about to begin.
     */
    protected abstract void doBegin();

    /**
     * Subclasses could do specific ending operations here when the action is about to end.
     */
    protected abstract void doEnd();

    /**
     * Done callback for executing top filter changes.
     */
    private class FilterChangedCallback implements OnDoneCallback {

        private boolean done;
        private Runnable runnableOnReady;

        @Override
        public void onDone() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.EffectAction$FilterChangedCallback.onDone()",this);try{done = true;

            if (runnableOnReady != null) {
                runnableOnReady.run();
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.EffectAction$FilterChangedCallback.onDone()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.EffectAction$FilterChangedCallback.onDone()",this,throwable);throw throwable;}
        }
    }
}
