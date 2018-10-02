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

import com.android.gallery3d.anim.CanvasAnimation;
import com.android.gallery3d.common.Utils;

import android.graphics.Rect;
import android.os.SystemClock;
import android.view.MotionEvent;

import java.util.ArrayList;

/*// GLView is a UI component. It can render to a GLCanvas and accept touch*/
/*// events. A GLView may have zero or more child GLView and they form a tree*/
/*// structure. The rendering and event handling will pass through the tree*/
/*// structure.*/
/*//*/
/*// A GLView tree should be attached to a GLRoot before event dispatching and*/
/*// rendering happens. GLView asks GLRoot to re-render or re-layout the*/
/*// GLView hierarchy using requestRender() and requestLayoutContentPane().*/
/*//*/
/*// The render() method is called in a separate thread. Before calling*/
/*// dispatchTouchEvent() and layout(), GLRoot acquires a lock to avoid the*/
/*// rendering thread running at the same time. If there are other entry points*/
/*// from main thread (like a Handler) in your GLView, you need to call*/
/*// lockRendering() if the rendering thread should not run at the same time.*/
/*//*/
public class GLView {
    private static final String TAG = "GLView";

    public static final int VISIBLE = 0;
    public static final int INVISIBLE = 1;

    private static final int FLAG_INVISIBLE = 1;
    private static final int FLAG_SET_MEASURED_SIZE = 2;
    private static final int FLAG_LAYOUT_REQUESTED = 4;

    protected final Rect mBounds = new Rect();
    protected final Rect mPaddings = new Rect();

    private GLRoot mRoot;
    protected GLView mParent;
    private ArrayList<GLView> mComponents;
    private GLView mMotionTarget;

    private CanvasAnimation mAnimation;

    private int mViewFlags = 0;

    protected int mMeasuredWidth = 0;
    protected int mMeasuredHeight = 0;

    private int mLastWidthSpec = -1;
    private int mLastHeightSpec = -1;

    protected int mScrollY = 0;
    protected int mScrollX = 0;
    protected int mScrollHeight = 0;
    protected int mScrollWidth = 0;

    public void startAnimation(CanvasAnimation animation) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.startAnimation(com.android.gallery3d.anim.CanvasAnimation)",this,animation);try{GLRoot root = getGLRoot();
        if (root == null) {throw new IllegalStateException();}
        mAnimation = animation;
        if (mAnimation != null) {
            mAnimation.start();
            root.registerLaunchedAnimation(mAnimation);
        }
        invalidate();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.startAnimation(com.android.gallery3d.anim.CanvasAnimation)",this,throwable);throw throwable;}
    }

    /*// Sets the visiblity of this GLView (either GLView.VISIBLE or*/
    /*// GLView.INVISIBLE).*/
    public void setVisibility(int visibility) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.setVisibility(int)",this,visibility);try{if (visibility == getVisibility()) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.setVisibility(int)",this);return;}}
        if (visibility == VISIBLE) {
            mViewFlags &= ~FLAG_INVISIBLE;
        } else {
            mViewFlags |= FLAG_INVISIBLE;
        }
        onVisibilityChanged(visibility);
        invalidate();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.setVisibility(int)",this,throwable);throw throwable;}
    }

    /*// Returns GLView.VISIBLE or GLView.INVISIBLE*/
    public int getVisibility() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.GLView.getVisibility()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.GLView.getVisibility()",this);return (mViewFlags & FLAG_INVISIBLE) == 0 ? VISIBLE : INVISIBLE;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.GLView.getVisibility()",this,throwable);throw throwable;}
    }

    /*// This should only be called on the content pane (the topmost GLView).*/
    public void attachToRoot(GLRoot root) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.attachToRoot(GLRoot)",this,root);try{Utils.assertTrue(mParent == null);
        Utils.assertTrue(mRoot == null);
        onAttachToRoot(root);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.attachToRoot(GLRoot)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.attachToRoot(GLRoot)",this,throwable);throw throwable;}
    }

    /*// This should only be called on the content pane (the topmost GLView).*/
    public void detachFromRoot() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.detachFromRoot()",this);try{Utils.assertTrue(mParent == null && mRoot != null);
        onDetachFromRoot();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.detachFromRoot()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.detachFromRoot()",this,throwable);throw throwable;}
    }

    /*// Returns the number of children of the GLView.*/
    public int getComponentCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.GLView.getComponentCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.GLView.getComponentCount()",this);return mComponents == null ? 0 : mComponents.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.GLView.getComponentCount()",this,throwable);throw throwable;}
    }

    /*// Returns the children for the given index.*/
    public GLView getComponent(int index) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.GLView com.android.gallery3d.ui.GLView.getComponent(int)",this,index);try{if (mComponents == null) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.GLView com.android.gallery3d.ui.GLView.getComponent(int)",this);return mComponents.get(index);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.GLView com.android.gallery3d.ui.GLView.getComponent(int)",this,throwable);throw throwable;}
    }

    /*// Adds a child to this GLView.*/
    public void addComponent(GLView component) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.addComponent(com.android.gallery3d.ui.GLView)",this,component);try{/*// Make sure the component doesn't have a parent currently.*/
        if (component.mParent != null) {throw new IllegalStateException();}

        /*// Build parent-child links*/
        if (mComponents == null) {
            mComponents = new ArrayList<GLView>();
        }
        mComponents.add(component);
        component.mParent = this;

        /*// If this is added after we have a root, tell the component.*/
        if (mRoot != null) {
            component.onAttachToRoot(mRoot);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.addComponent(com.android.gallery3d.ui.GLView)",this,throwable);throw throwable;}
    }

    /*// Removes a child from this GLView.*/
    public boolean removeComponent(GLView component) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLView.removeComponent(com.android.gallery3d.ui.GLView)",this,component);try{if (mComponents == null) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.removeComponent(com.android.gallery3d.ui.GLView)",this);return false;}}
        if (mComponents.remove(component)) {
            removeOneComponent(component);
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.removeComponent(com.android.gallery3d.ui.GLView)",this);return true;}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.removeComponent(com.android.gallery3d.ui.GLView)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLView.removeComponent(com.android.gallery3d.ui.GLView)",this,throwable);throw throwable;}
    }

    /*// Removes all children of this GLView.*/
    public void removeAllComponents() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.removeAllComponents()",this);try{for (int i = 0, n = mComponents.size(); i < n; ++i) {
            removeOneComponent(mComponents.get(i));
        }
        mComponents.clear();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.removeAllComponents()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.removeAllComponents()",this,throwable);throw throwable;}
    }

    private void removeOneComponent(GLView component) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.removeOneComponent(com.android.gallery3d.ui.GLView)",this,component);try{if (mMotionTarget == component) {
            long now = SystemClock.uptimeMillis();
            MotionEvent cancelEvent = MotionEvent.obtain(
                    now, now, MotionEvent.ACTION_CANCEL, 0, 0, 0);
            dispatchTouchEvent(cancelEvent);
            cancelEvent.recycle();
        }
        component.onDetachFromRoot();
        component.mParent = null;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.removeOneComponent(com.android.gallery3d.ui.GLView)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.removeOneComponent(com.android.gallery3d.ui.GLView)",this,throwable);throw throwable;}
    }

    public Rect bounds() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Rect com.android.gallery3d.ui.GLView.bounds()",this);try{com.mijack.Xlog.logMethodExit("android.graphics.Rect com.android.gallery3d.ui.GLView.bounds()",this);return mBounds;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Rect com.android.gallery3d.ui.GLView.bounds()",this,throwable);throw throwable;}
    }

    public int getWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.GLView.getWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.GLView.getWidth()",this);return mBounds.right - mBounds.left;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.GLView.getWidth()",this,throwable);throw throwable;}
    }

    public int getHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.GLView.getHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.GLView.getHeight()",this);return mBounds.bottom - mBounds.top;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.GLView.getHeight()",this,throwable);throw throwable;}
    }

    public GLRoot getGLRoot() {
        com.mijack.Xlog.logMethodEnter("GLRoot com.android.gallery3d.ui.GLView.getGLRoot()",this);try{com.mijack.Xlog.logMethodExit("GLRoot com.android.gallery3d.ui.GLView.getGLRoot()",this);return mRoot;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("GLRoot com.android.gallery3d.ui.GLView.getGLRoot()",this,throwable);throw throwable;}
    }

    /*// Request re-rendering of the view hierarchy.*/
    /*// This is used for animation or when the contents changed.*/
    public void invalidate() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.invalidate()",this);try{GLRoot root = getGLRoot();
        if (root != null) {root.requestRender();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.invalidate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.invalidate()",this,throwable);throw throwable;}
    }

    /*// Request re-layout of the view hierarchy.*/
    public void requestLayout() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.requestLayout()",this);try{mViewFlags |= FLAG_LAYOUT_REQUESTED;
        mLastHeightSpec = -1;
        mLastWidthSpec = -1;
        if (mParent != null) {
            mParent.requestLayout();
        } else {
            /*// Is this a content pane ?*/
            GLRoot root = getGLRoot();
            if (root != null) {root.requestLayoutContentPane();}
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.requestLayout()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.requestLayout()",this,throwable);throw throwable;}
    }

    protected void render(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.render(GLCanvas)",this,canvas);try{renderBackground(canvas);
        for (int i = 0, n = getComponentCount(); i < n; ++i) {
            renderChild(canvas, getComponent(i));
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.render(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.render(GLCanvas)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.renderBackground(GLCanvas)",this,view);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.renderBackground(GLCanvas)",this);}

    protected void renderChild(GLCanvas canvas, GLView component) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.renderChild(GLCanvas,com.android.gallery3d.ui.GLView)",this,canvas,component);try{if (component.getVisibility() != GLView.VISIBLE
                && component.mAnimation == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.renderChild(GLCanvas,com.android.gallery3d.ui.GLView)",this);return;}}

        int xoffset = component.mBounds.left - mScrollX;
        int yoffset = component.mBounds.top - mScrollY;

        canvas.translate(xoffset, yoffset, 0);

        CanvasAnimation anim = component.mAnimation;
        if (anim != null) {
            canvas.save(anim.getCanvasSaveFlags());
            if (anim.calculate(canvas.currentAnimationTimeMillis())) {
                invalidate();
            } else {
                component.mAnimation = null;
            }
            anim.apply(canvas);
        }
        component.render(canvas);
        if (anim != null) {canvas.restore();}
        canvas.translate(-xoffset, -yoffset, 0);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.renderChild(GLCanvas,com.android.gallery3d.ui.GLView)",this,throwable);throw throwable;}
    }

    protected boolean onTouch(MotionEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLView.onTouch(android.view.MotionEvent)",this,event);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.onTouch(android.view.MotionEvent)",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLView.onTouch(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    protected boolean dispatchTouchEvent(MotionEvent event,
            int x, int y, GLView component, boolean checkBounds) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLView.dispatchTouchEvent(android.view.MotionEvent,int,int,com.android.gallery3d.ui.GLView,boolean)",this,event,x,y,component,checkBounds);try{Rect rect = component.mBounds;
        int left = rect.left;
        int top = rect.top;
        if (!checkBounds || rect.contains(x, y)) {
            event.offsetLocation(-left, -top);
            if (component.dispatchTouchEvent(event)) {
                event.offsetLocation(left, top);
                {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.dispatchTouchEvent(android.view.MotionEvent,int,int,com.android.gallery3d.ui.GLView,boolean)",this);return true;}
            }
            event.offsetLocation(left, top);
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.dispatchTouchEvent(android.view.MotionEvent,int,int,com.android.gallery3d.ui.GLView,boolean)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLView.dispatchTouchEvent(android.view.MotionEvent,int,int,com.android.gallery3d.ui.GLView,boolean)",this,throwable);throw throwable;}
    }

    protected boolean dispatchTouchEvent(MotionEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLView.dispatchTouchEvent(android.view.MotionEvent)",this,event);try{int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        if (mMotionTarget != null) {
            if (action == MotionEvent.ACTION_DOWN) {
                MotionEvent cancel = MotionEvent.obtain(event);
                cancel.setAction(MotionEvent.ACTION_CANCEL);
                dispatchTouchEvent(cancel, x, y, mMotionTarget, false);
                mMotionTarget = null;
            } else {
                dispatchTouchEvent(event, x, y, mMotionTarget, false);
                if (action == MotionEvent.ACTION_CANCEL
                        || action == MotionEvent.ACTION_UP) {
                    mMotionTarget = null;
                }
                {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.dispatchTouchEvent(android.view.MotionEvent)",this);return true;}
            }
        }
        if (action == MotionEvent.ACTION_DOWN) {
            /*// in the reverse rendering order*/
            for (int i = getComponentCount() - 1; i >= 0; --i) {
                GLView component = getComponent(i);
                if (component.getVisibility() != GLView.VISIBLE) {continue;}
                if (dispatchTouchEvent(event, x, y, component, true)) {
                    mMotionTarget = component;
                    {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.dispatchTouchEvent(android.view.MotionEvent)",this);return true;}
                }
            }
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.dispatchTouchEvent(android.view.MotionEvent)",this);return onTouch(event);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLView.dispatchTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    public Rect getPaddings() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Rect com.android.gallery3d.ui.GLView.getPaddings()",this);try{com.mijack.Xlog.logMethodExit("android.graphics.Rect com.android.gallery3d.ui.GLView.getPaddings()",this);return mPaddings;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Rect com.android.gallery3d.ui.GLView.getPaddings()",this,throwable);throw throwable;}
    }

    public void setPaddings(Rect paddings) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.setPaddings(android.graphics.Rect)",this,paddings);try{mPaddings.set(paddings);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.setPaddings(android.graphics.Rect)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.setPaddings(android.graphics.Rect)",this,throwable);throw throwable;}
    }

    public void setPaddings(int left, int top, int right, int bottom) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.setPaddings(int,int,int,int)",this,left,top,right,bottom);try{mPaddings.set(left, top, right, bottom);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.setPaddings(int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.setPaddings(int,int,int,int)",this,throwable);throw throwable;}
    }

    public void layout(int left, int top, int right, int bottom) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.layout(int,int,int,int)",this,left,top,right,bottom);try{boolean sizeChanged = setBounds(left, top, right, bottom);
        if (sizeChanged) {
            mViewFlags &= ~FLAG_LAYOUT_REQUESTED;
            onLayout(true, left, top, right, bottom);
        } else if ((mViewFlags & FLAG_LAYOUT_REQUESTED)!= 0) {
            mViewFlags &= ~FLAG_LAYOUT_REQUESTED;
            onLayout(false, left, top, right, bottom);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.layout(int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.layout(int,int,int,int)",this,throwable);throw throwable;}
    }

    private boolean setBounds(int left, int top, int right, int bottom) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLView.setBounds(int,int,int,int)",this,left,top,right,bottom);try{boolean sizeChanged = (right - left) != (mBounds.right - mBounds.left)
                || (bottom - top) != (mBounds.bottom - mBounds.top);
        mBounds.set(left, top, right, bottom);
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.setBounds(int,int,int,int)",this);return sizeChanged;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLView.setBounds(int,int,int,int)",this,throwable);throw throwable;}
    }

    public void measure(int widthSpec, int heightSpec) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.measure(int,int)",this,widthSpec,heightSpec);try{if (widthSpec == mLastWidthSpec && heightSpec == mLastHeightSpec
                && (mViewFlags & FLAG_LAYOUT_REQUESTED) == 0) {
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.measure(int,int)",this);return;}
        }

        mLastWidthSpec = widthSpec;
        mLastHeightSpec = heightSpec;

        mViewFlags &= ~FLAG_SET_MEASURED_SIZE;
        onMeasure(widthSpec, heightSpec);
        if ((mViewFlags & FLAG_SET_MEASURED_SIZE) == 0) {
            throw new IllegalStateException(getClass().getName()
                    + " should call setMeasuredSize() in onMeasure()");
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.measure(int,int)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.onMeasure(int,int)",this,widthSpec,heightSpec);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.onMeasure(int,int)",this);}

    protected void setMeasuredSize(int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.setMeasuredSize(int,int)",this,width,height);try{mViewFlags |= FLAG_SET_MEASURED_SIZE;
        mMeasuredWidth = width;
        mMeasuredHeight = height;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.setMeasuredSize(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.setMeasuredSize(int,int)",this,throwable);throw throwable;}
    }

    public int getMeasuredWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.GLView.getMeasuredWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.GLView.getMeasuredWidth()",this);return mMeasuredWidth;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.GLView.getMeasuredWidth()",this,throwable);throw throwable;}
    }

    public int getMeasuredHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.GLView.getMeasuredHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.GLView.getMeasuredHeight()",this);return mMeasuredHeight;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.GLView.getMeasuredHeight()",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.onLayout(boolean,int,int,int,int)",this,changeSize,left,top,right,bottom);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.onLayout(boolean,int,int,int,int)",this);}

    /**
     * Gets the bounds of the given descendant that relative to this view.
     */
    public boolean getBoundsOf(GLView descendant, Rect out) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLView.getBoundsOf(com.android.gallery3d.ui.GLView,android.graphics.Rect)",this,descendant,out);try{int xoffset = 0;
        int yoffset = 0;
        GLView view = descendant;
        while (view != this) {
            if (view == null) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.getBoundsOf(com.android.gallery3d.ui.GLView,android.graphics.Rect)",this);return false;}}
            Rect bounds = view.mBounds;
            xoffset += bounds.left;
            yoffset += bounds.top;
            view = view.mParent;
        }
        out.set(xoffset, yoffset, xoffset + descendant.getWidth(),
                yoffset + descendant.getHeight());
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLView.getBoundsOf(com.android.gallery3d.ui.GLView,android.graphics.Rect)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLView.getBoundsOf(com.android.gallery3d.ui.GLView,android.graphics.Rect)",this,throwable);throw throwable;}
    }

    protected void onVisibilityChanged(int visibility) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.onVisibilityChanged(int)",this,visibility);try{for (int i = 0, n = getComponentCount(); i < n; ++i) {
            GLView child = getComponent(i);
            if (child.getVisibility() == GLView.VISIBLE) {
                child.onVisibilityChanged(visibility);
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.onVisibilityChanged(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.onVisibilityChanged(int)",this,throwable);throw throwable;}
    }

    protected void onAttachToRoot(GLRoot root) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.onAttachToRoot(GLRoot)",this,root);try{mRoot = root;
        for (int i = 0, n = getComponentCount(); i < n; ++i) {
            getComponent(i).onAttachToRoot(root);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.onAttachToRoot(GLRoot)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.onAttachToRoot(GLRoot)",this,throwable);throw throwable;}
    }

    protected void onDetachFromRoot() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.onDetachFromRoot()",this);try{for (int i = 0, n = getComponentCount(); i < n; ++i) {
            getComponent(i).onDetachFromRoot();
        }
        mRoot = null;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.onDetachFromRoot()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.onDetachFromRoot()",this,throwable);throw throwable;}
    }

    public void lockRendering() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.lockRendering()",this);try{if (mRoot != null) {
            mRoot.lockRenderThread();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.lockRendering()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.lockRendering()",this,throwable);throw throwable;}
    }

    public void unlockRendering() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.unlockRendering()",this);try{if (mRoot != null) {
            mRoot.unlockRenderThread();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.unlockRendering()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.unlockRendering()",this,throwable);throw throwable;}
    }

    /*// This is for debugging only.*/
    /*// Dump the view hierarchy into log.*/
    void dumpTree(String prefix) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLView.dumpTree(java.lang.String)",this,prefix);try{Log.d(TAG, prefix + getClass().getSimpleName());
        for (int i = 0, n = getComponentCount(); i < n; ++i) {
            getComponent(i).dumpTree(prefix + "....");
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLView.dumpTree(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLView.dumpTree(java.lang.String)",this,throwable);throw throwable;}
    }
}
