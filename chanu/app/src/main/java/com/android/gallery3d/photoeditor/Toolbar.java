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
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.chanapps.four.gallery3d.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Toolbar that contains all tools and controls their idle/awake behaviors from UI thread.
 */
public class Toolbar extends RelativeLayout {

    private final ToolbarIdleHandler idleHandler;

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        idleHandler = new ToolbarIdleHandler(context);
        setOnHierarchyChangeListener(idleHandler);
        idleHandler.killIdle();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.Toolbar.dispatchTouchEvent(android.view.MotionEvent)",this,ev);try{idleHandler.killIdle();
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.Toolbar.dispatchTouchEvent(android.view.MotionEvent)",this);return super.dispatchTouchEvent(ev);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.Toolbar.dispatchTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    private static class ToolbarIdleHandler implements OnHierarchyChangeListener {

        private static final int MAKE_IDLE = 1;
        private static final int TIMEOUT_IDLE = 8000;

        private final List<View> childViews = new ArrayList<View>();
        private final Handler mainHandler;
        private final Animation fadeIn;
        private final Animation fadeOut;
        private boolean idle;

        public ToolbarIdleHandler(Context context) {
            mainHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler$1.handleMessage(android.os.Message)",this,msg);try{switch (msg.what) {
                        case MAKE_IDLE:
                            if (!idle) {
                                idle = true;
                                for (View view : childViews) {
                                    view.startAnimation(fadeOut);
                                }
                            }
                            break;
                    }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler$1.handleMessage(android.os.Message)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler$1.handleMessage(android.os.Message)",this,throwable);throw throwable;}
                }
            };

            fadeIn = AnimationUtils.loadAnimation(context, R.anim.photoeditor_fade_in);
            fadeOut = AnimationUtils.loadAnimation(context, R.anim.photoeditor_fade_out);
        }

        public void killIdle() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler.killIdle()",this);try{mainHandler.removeMessages(MAKE_IDLE);
            if (idle) {
                idle = false;
                for (View view : childViews) {
                    view.startAnimation(fadeIn);
                }
            }
            mainHandler.sendEmptyMessageDelayed(MAKE_IDLE, TIMEOUT_IDLE);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler.killIdle()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler.killIdle()",this,throwable);throw throwable;}
        }

        @Override
        public void onChildViewAdded(View parent, View child) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler.onChildViewAdded(android.view.View,android.view.View)",this,parent,child);try{/*// All child views, except photo-view, will fade out on inactivity timeout.*/
            if (child.getId() != R.id.photo_view) {
                childViews.add(child);
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler.onChildViewAdded(android.view.View,android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler.onChildViewAdded(android.view.View,android.view.View)",this,throwable);throw throwable;}
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler.onChildViewRemoved(android.view.View,android.view.View)",this,parent,child);try{childViews.remove(child);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler.onChildViewRemoved(android.view.View,android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.Toolbar$ToolbarIdleHandler.onChildViewRemoved(android.view.View,android.view.View)",this,throwable);throw throwable;}
        }
    }
}
