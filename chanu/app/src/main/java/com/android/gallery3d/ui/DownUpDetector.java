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

package com.android.gallery3d.ui;

import android.view.MotionEvent;

public class DownUpDetector {
    public interface DownUpListener {
        void onDown(MotionEvent e);
        void onUp(MotionEvent e);
    }

    private boolean mStillDown;
    private DownUpListener mListener;

    public DownUpDetector(DownUpListener listener) {
        mListener = listener;
    }

    private void setState(boolean down, MotionEvent e) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DownUpDetector.setState(boolean,android.view.MotionEvent)",this,down,e);try{if (down == mStillDown) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DownUpDetector.setState(boolean,android.view.MotionEvent)",this);return;}}
        mStillDown = down;
        if (down) {
            mListener.onDown(e);
        } else {
            mListener.onUp(e);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DownUpDetector.setState(boolean,android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    public void onTouchEvent(MotionEvent ev) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DownUpDetector.onTouchEvent(android.view.MotionEvent)",this,ev);try{switch (ev.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            setState(true, ev);
            break;

        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_POINTER_DOWN:  /*// Multitouch event - abort.*/
            setState(false, ev);
            break;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DownUpDetector.onTouchEvent(android.view.MotionEvent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DownUpDetector.onTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    public boolean isDown() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.DownUpDetector.isDown()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.DownUpDetector.isDown()",this);return mStillDown;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.DownUpDetector.isDown()",this,throwable);throw throwable;}
    }
}
