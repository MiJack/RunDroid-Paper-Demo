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

import android.graphics.drawable.Drawable;
import android.os.Build;
import com.android.gallery3d.anim.CanvasAnimation;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.GalleryUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Process;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/*// The root component of all <code>GLView</code>s. The rendering is done in GL*/
/*// thread while the event handling is done in the main thread.  To synchronize*/
/*// the two threads, the entry points of this package need to synchronize on the*/
/*// <code>GLRootView</code> instance unless it can be proved that the rendering*/
/*// thread won't access the same thing as the method. The entry points include:*/
/*// (1) The public methods of HeadUpDisplay*/
/*// (2) The public methods of CameraHeadUpDisplay*/
/*// (3) The overridden methods in GLRootView.*/
public class GLRootView extends GLSurfaceView
        implements GLSurfaceView.Renderer, GLRoot {
    private static final String TAG = "GLRootView";

    private static final boolean DEBUG_FPS = false;
    private int mFrameCount = 0;
    private long mFrameCountingStart = 0;

    private static final boolean DEBUG_INVALIDATE = false;
    private int mInvalidateColor = 0;

    private static final boolean DEBUG_DRAWING_STAT = false;

    private static final int FLAG_INITIALIZED = 1;
    private static final int FLAG_NEED_LAYOUT = 2;

    private GL11 mGL;
    private GLCanvasImpl mCanvas;

    private GLView mContentView;
    private DisplayMetrics mDisplayMetrics;

    private int mFlags = FLAG_NEED_LAYOUT;
    private volatile boolean mRenderRequested = false;

    private Rect mClipRect = new Rect();
    private int mClipRetryCount = 0;

    private final GalleryEGLConfigChooser mEglConfigChooser =
            new GalleryEGLConfigChooser();

    private final ArrayList<CanvasAnimation> mAnimations =
            new ArrayList<CanvasAnimation>();

    private final LinkedList<OnGLIdleListener> mIdleListeners =
            new LinkedList<OnGLIdleListener>();

    private final IdleRunner mIdleRunner = new IdleRunner();

    private final ReentrantLock mRenderLock = new ReentrantLock();

    private static final int TARGET_FRAME_TIME = 33;
    private long mLastDrawFinishTime;
    private boolean mInDownState = false;

    public GLRootView(Context context) {
        this(context, null);
    }

    public GLRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFlags |= FLAG_INITIALIZED;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            {deprecatedSetBackground(null);}
        else
            {setBackground(null);}
        setEGLConfigChooser(mEglConfigChooser);
        setRenderer(this);
        getHolder().setFormat(PixelFormat.RGB_565);

        /*// Uncomment this to enable gl error check.*/
        /*//setDebugFlags(DEBUG_CHECK_GL_ERROR);*/
    }

    @SuppressWarnings("deprecation")
    protected void deprecatedSetBackground(Drawable d) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.deprecatedSetBackground(android.graphics.drawable.Drawable)",this,d);try{setBackgroundDrawable(d);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.deprecatedSetBackground(android.graphics.drawable.Drawable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.deprecatedSetBackground(android.graphics.drawable.Drawable)",this,throwable);throw throwable;}
    }

    public GalleryEGLConfigChooser getEGLConfigChooser() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.GalleryEGLConfigChooser com.android.gallery3d.ui.GLRootView.getEGLConfigChooser()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.GalleryEGLConfigChooser com.android.gallery3d.ui.GLRootView.getEGLConfigChooser()",this);return mEglConfigChooser;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.GalleryEGLConfigChooser com.android.gallery3d.ui.GLRootView.getEGLConfigChooser()",this,throwable);throw throwable;}
    }

    @Override
    public boolean hasStencil() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLRootView.hasStencil()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLRootView.hasStencil()",this);return getEGLConfigChooser().getStencilBits() > 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLRootView.hasStencil()",this,throwable);throw throwable;}
    }

    @Override
    public void registerLaunchedAnimation(CanvasAnimation animation) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.registerLaunchedAnimation(com.android.gallery3d.anim.CanvasAnimation)",this,animation);try{/*// Register the newly launched animation so that we can set the start*/
        /*// time more precisely. (Usually, it takes much longer for first*/
        /*// rendering, so we set the animation start time as the time we*/
        /*// complete rendering)*/
        mAnimations.add(animation);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.registerLaunchedAnimation(com.android.gallery3d.anim.CanvasAnimation)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.registerLaunchedAnimation(com.android.gallery3d.anim.CanvasAnimation)",this,throwable);throw throwable;}
    }

    @Override
    public void addOnGLIdleListener(OnGLIdleListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.addOnGLIdleListener(OnGLIdleListener)",this,listener);try{synchronized (mIdleListeners) {
            mIdleListeners.addLast(listener);
            mIdleRunner.enable();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.addOnGLIdleListener(OnGLIdleListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.addOnGLIdleListener(OnGLIdleListener)",this,throwable);throw throwable;}
    }

    @Override
    public void setContentPane(GLView content) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.setContentPane(com.android.gallery3d.ui.GLView)",this,content);try{if (mContentView == content) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.setContentPane(com.android.gallery3d.ui.GLView)",this);return;}}
        if (mContentView != null) {
            if (mInDownState) {
                long now = SystemClock.uptimeMillis();
                MotionEvent cancelEvent = MotionEvent.obtain(
                        now, now, MotionEvent.ACTION_CANCEL, 0, 0, 0);
                mContentView.dispatchTouchEvent(cancelEvent);
                cancelEvent.recycle();
                mInDownState = false;
            }
            mContentView.detachFromRoot();
            BasicTexture.yieldAllTextures();
        }
        mContentView = content;
        if (content != null) {
            content.attachToRoot(this);
            requestLayoutContentPane();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.setContentPane(com.android.gallery3d.ui.GLView)",this,throwable);throw throwable;}
    }

    public GLView getContentPane() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.GLView com.android.gallery3d.ui.GLRootView.getContentPane()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.GLView com.android.gallery3d.ui.GLRootView.getContentPane()",this);return mContentView;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.GLView com.android.gallery3d.ui.GLRootView.getContentPane()",this,throwable);throw throwable;}
    }

    @Override
    public void requestRender() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.requestRender()",this);try{if (DEBUG_INVALIDATE) {
            StackTraceElement e = Thread.currentThread().getStackTrace()[4];
            String caller = e.getFileName() + ":" + e.getLineNumber() + " ";
            Log.d(TAG, "invalidate: " + caller);
        }
        if (mRenderRequested) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.requestRender()",this);return;}}
        mRenderRequested = true;
        super.requestRender();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.requestRender()",this,throwable);throw throwable;}
    }

    @Override
    public void requestLayoutContentPane() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.requestLayoutContentPane()",this);try{mRenderLock.lock();
        try {
            if (mContentView == null || (mFlags & FLAG_NEED_LAYOUT) != 0) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.requestLayoutContentPane()",this);return;}}

            /*// "View" system will invoke onLayout() for initialization(bug ?), we*/
            /*// have to ignore it since the GLThread is not ready yet.*/
            if ((mFlags & FLAG_INITIALIZED) == 0) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.requestLayoutContentPane()",this);return;}}

            mFlags |= FLAG_NEED_LAYOUT;
            requestRender();
        } finally {
            mRenderLock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.requestLayoutContentPane()",this,throwable);throw throwable;}
    }

    private void layoutContentPane() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.layoutContentPane()",this);try{mFlags &= ~FLAG_NEED_LAYOUT;
        int width = getWidth();
        int height = getHeight();
        Log.i(TAG, "layout content pane " + width + "x" + height);
        if (mContentView != null && width != 0 && height != 0) {
            mContentView.layout(0, 0, width, height);
        }
        /*// Uncomment this to dump the view hierarchy.*/
        /*//mContentView.dumpTree("");*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.layoutContentPane()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.layoutContentPane()",this,throwable);throw throwable;}
    }

    @Override
    protected void onLayout(
            boolean changed, int left, int top, int right, int bottom) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.onLayout(boolean,int,int,int,int)",this,changed,left,top,right,bottom);try{if (changed) {requestLayoutContentPane();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.onLayout(boolean,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.onLayout(boolean,int,int,int,int)",this,throwable);throw throwable;}
    }

    /**
     * Called when the context is created, possibly after automatic destruction.
     */
    /*// This is a GLSurfaceView.Renderer callback*/
    @Override
    public void onSurfaceCreated(GL10 gl1, EGLConfig config) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.onSurfaceCreated(javax.microedition.khronos.opengles.GL10,javax.microedition.khronos.egl.EGLConfig)",this,gl1,config);try{GL11 gl = (GL11) gl1;
        if (mGL != null) {
            /*// The GL Object has changed*/
            Log.i(TAG, "GLObject has changed from " + mGL + " to " + gl);
        }
        mGL = gl;
        mCanvas = new GLCanvasImpl(gl);
        if (!DEBUG_FPS) {
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        } else {
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.onSurfaceCreated(javax.microedition.khronos.opengles.GL10,javax.microedition.khronos.egl.EGLConfig)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.onSurfaceCreated(javax.microedition.khronos.opengles.GL10,javax.microedition.khronos.egl.EGLConfig)",this,throwable);throw throwable;}
    }

    /**
     * Called when the OpenGL surface is recreated without destroying the
     * context.
     */
    /*// This is a GLSurfaceView.Renderer callback*/
    @Override
    public void onSurfaceChanged(GL10 gl1, int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.onSurfaceChanged(javax.microedition.khronos.opengles.GL10,int,int)",this,gl1,width,height);try{Log.i(TAG, "onSurfaceChanged: " + width + "x" + height
                + ", gl10: " + gl1.toString());
        Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
        GalleryUtils.setRenderThread();
        GL11 gl = (GL11) gl1;
        Utils.assertTrue(mGL == gl);

        mCanvas.setSize(width, height);

        mClipRect.set(0, 0, width, height);
        mClipRetryCount = 2;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.onSurfaceChanged(javax.microedition.khronos.opengles.GL10,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.onSurfaceChanged(javax.microedition.khronos.opengles.GL10,int,int)",this,throwable);throw throwable;}
    }

    private void outputFps() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.outputFps()",this);try{long now = System.nanoTime();
        if (mFrameCountingStart == 0) {
            mFrameCountingStart = now;
        } else if ((now - mFrameCountingStart) > 1000000000) {
            Log.d(TAG, "fps: " + (double) mFrameCount
                    * 1000000000 / (now - mFrameCountingStart));
            mFrameCountingStart = now;
            mFrameCount = 0;
        }
        ++mFrameCount;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.outputFps()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.outputFps()",this,throwable);throw throwable;}
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.onDrawFrame(javax.microedition.khronos.opengles.GL10)",this,gl);try{mRenderLock.lock();
        try {
            onDrawFrameLocked(gl);
        } finally {
            mRenderLock.unlock();
        }
        long end = SystemClock.uptimeMillis();

        if (mLastDrawFinishTime != 0) {
            long wait = mLastDrawFinishTime + TARGET_FRAME_TIME - end;
            if (wait > 0) {
                SystemClock.sleep(wait);
            }
        }
        mLastDrawFinishTime = SystemClock.uptimeMillis();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.onDrawFrame(javax.microedition.khronos.opengles.GL10)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.onDrawFrame(javax.microedition.khronos.opengles.GL10)",this,throwable);throw throwable;}
    }

    private void onDrawFrameLocked(GL10 gl) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.onDrawFrameLocked(javax.microedition.khronos.opengles.GL10)",this,gl);try{if (DEBUG_FPS) {outputFps();}

        /*// release the unbound textures and deleted buffers.*/
        mCanvas.deleteRecycledResources();

        /*// reset texture upload limit*/
        UploadedTexture.resetUploadLimit();

        mRenderRequested = false;

        if ((mFlags & FLAG_NEED_LAYOUT) != 0) {layoutContentPane();}

        /*// OpenGL seems having a bug causing us not being able to reset the*/
        /*// scissor box in "onSurfaceChanged()". We have to do it in the second*/
        /*// onDrawFrame().*/
        if (mClipRetryCount > 0) {
            --mClipRetryCount;
            Rect clip = mClipRect;
            gl.glScissor(clip.left, clip.top, clip.width(), clip.height());
        }

        mCanvas.setCurrentAnimationTimeMillis(SystemClock.uptimeMillis());
        if (mContentView != null) {
           mContentView.render(mCanvas);
        }

        if (!mAnimations.isEmpty()) {
            long now = SystemClock.uptimeMillis();
            for (int i = 0, n = mAnimations.size(); i < n; i++) {
                mAnimations.get(i).setStartTime(now);
            }
            mAnimations.clear();
        }

        if (UploadedTexture.uploadLimitReached()) {
            requestRender();
        }

        synchronized (mIdleListeners) {
            if (!mRenderRequested && !mIdleListeners.isEmpty()) {
                mIdleRunner.enable();
            }
        }

        if (DEBUG_INVALIDATE) {
            mCanvas.fillRect(10, 10, 5, 5, mInvalidateColor);
            mInvalidateColor = ~mInvalidateColor;
        }

        if (DEBUG_DRAWING_STAT) {
            mCanvas.dumpStatisticsAndClear();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.onDrawFrameLocked(javax.microedition.khronos.opengles.GL10)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.onDrawFrameLocked(javax.microedition.khronos.opengles.GL10)",this,throwable);throw throwable;}
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLRootView.dispatchTouchEvent(android.view.MotionEvent)",this,event);try{int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_UP) {
            mInDownState = false;
        } else if (!mInDownState && action != MotionEvent.ACTION_DOWN) {
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLRootView.dispatchTouchEvent(android.view.MotionEvent)",this);return false;}
        }
        mRenderLock.lock();
        try {
            /*// If this has been detached from root, we don't need to handle event*/
            boolean handled = mContentView != null
                    && mContentView.dispatchTouchEvent(event);
            if (action == MotionEvent.ACTION_DOWN && handled) {
                mInDownState = true;
            }
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLRootView.dispatchTouchEvent(android.view.MotionEvent)",this);return handled;}
        } finally {
            mRenderLock.unlock();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLRootView.dispatchTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    public DisplayMetrics getDisplayMetrics() {
        com.mijack.Xlog.logMethodEnter("android.util.DisplayMetrics com.android.gallery3d.ui.GLRootView.getDisplayMetrics()",this);try{if (mDisplayMetrics == null) {
            mDisplayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager()
                    .getDefaultDisplay().getMetrics(mDisplayMetrics);
        }
        {com.mijack.Xlog.logMethodExit("android.util.DisplayMetrics com.android.gallery3d.ui.GLRootView.getDisplayMetrics()",this);return mDisplayMetrics;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.util.DisplayMetrics com.android.gallery3d.ui.GLRootView.getDisplayMetrics()",this,throwable);throw throwable;}
    }

    public GLCanvas getCanvas() {
        com.mijack.Xlog.logMethodEnter("GLCanvas com.android.gallery3d.ui.GLRootView.getCanvas()",this);try{com.mijack.Xlog.logMethodExit("GLCanvas com.android.gallery3d.ui.GLRootView.getCanvas()",this);return mCanvas;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("GLCanvas com.android.gallery3d.ui.GLRootView.getCanvas()",this,throwable);throw throwable;}
    }

    private class IdleRunner implements Runnable {
        /*// true if the idle runner is in the queue*/
        private boolean mActive = false;

        @Override
        public void run() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView$IdleRunner.run()",this);try{OnGLIdleListener listener;
            synchronized (mIdleListeners) {
                mActive = false;
                if (mRenderRequested) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView$IdleRunner.run()",this);return;}}
                if (mIdleListeners.isEmpty()) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView$IdleRunner.run()",this);return;}}
                listener = mIdleListeners.removeFirst();
            }
            mRenderLock.lock();
            try {
                if (!listener.onGLIdle(GLRootView.this, mCanvas)) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView$IdleRunner.run()",this);return;}}
            } finally {
                mRenderLock.unlock();
            }
            synchronized (mIdleListeners) {
                mIdleListeners.addLast(listener);
                enable();
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView$IdleRunner.run()",this,throwable);throw throwable;}
        }

        public void enable() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView$IdleRunner.enable()",this);try{/*// Who gets the flag can add it to the queue*/
            if (mActive) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView$IdleRunner.enable()",this);return;}}
            mActive = true;
            queueEvent(this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView$IdleRunner.enable()",this,throwable);throw throwable;}
        }
    }

    @Override
    public void lockRenderThread() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.lockRenderThread()",this);try{mRenderLock.lock();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.lockRenderThread()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.lockRenderThread()",this,throwable);throw throwable;}
    }

    @Override
    public void unlockRenderThread() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLRootView.unlockRenderThread()",this);try{mRenderLock.unlock();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLRootView.unlockRenderThread()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLRootView.unlockRenderThread()",this,throwable);throw throwable;}
    }
}
