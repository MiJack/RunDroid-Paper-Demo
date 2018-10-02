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

import com.android.gallery3d.common.Utils;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/*// BasicTexture is a Texture corresponds to a real GL texture.*/
/*// The state of a BasicTexture indicates whether its data is loaded to GL memory.*/
/*// If a BasicTexture is loaded into GL memory, it has a GL texture id.*/
abstract class BasicTexture implements Texture {

    @SuppressWarnings("unused")
    private static final String TAG = "BasicTexture";
    protected static final int UNSPECIFIED = -1;

    protected static final int STATE_UNLOADED = 0;
    protected static final int STATE_LOADED = 1;
    protected static final int STATE_ERROR = -1;

    protected int mId;
    protected int mState;

    protected int mWidth = UNSPECIFIED;
    protected int mHeight = UNSPECIFIED;

    private int mTextureWidth;
    private int mTextureHeight;

    private boolean mHasBorder;

    protected WeakReference<GLCanvas> mCanvasRef = null;
    private static WeakHashMap<BasicTexture, Object> sAllTextures
            = new WeakHashMap<BasicTexture, Object>();
    private static ThreadLocal sInFinalizer = new ThreadLocal();

    protected BasicTexture(GLCanvas canvas, int id, int state) {
        setAssociatedCanvas(canvas);
        mId = id;
        mState = state;
        synchronized (sAllTextures) {
            sAllTextures.put(this, null);
        }
    }

    protected BasicTexture() {
        this(null, 0, STATE_UNLOADED);
    }

    protected void setAssociatedCanvas(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.BasicTexture.setAssociatedCanvas(GLCanvas)",this,canvas);try{mCanvasRef = canvas == null
                ? null
                : new WeakReference<GLCanvas>(canvas);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.BasicTexture.setAssociatedCanvas(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.BasicTexture.setAssociatedCanvas(GLCanvas)",this,throwable);throw throwable;}
    }

    /**
     * Sets the content size of this texture. In OpenGL, the actual texture
     * size must be of power of 2, the size of the content may be smaller.
     */
    protected void setSize(int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.BasicTexture.setSize(int,int)",this,width,height);try{mWidth = width;
        mHeight = height;
        mTextureWidth = Utils.nextPowerOf2(width);
        mTextureHeight = Utils.nextPowerOf2(height);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.BasicTexture.setSize(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.BasicTexture.setSize(int,int)",this,throwable);throw throwable;}
    }

    public int getId() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.BasicTexture.getId()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.BasicTexture.getId()",this);return mId;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.BasicTexture.getId()",this,throwable);throw throwable;}
    }

    public int getWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.BasicTexture.getWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.BasicTexture.getWidth()",this);return mWidth;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.BasicTexture.getWidth()",this,throwable);throw throwable;}
    }

    public int getHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.BasicTexture.getHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.BasicTexture.getHeight()",this);return mHeight;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.BasicTexture.getHeight()",this,throwable);throw throwable;}
    }

    /*// Returns the width rounded to the next power of 2.*/
    public int getTextureWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.BasicTexture.getTextureWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.BasicTexture.getTextureWidth()",this);return mTextureWidth;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.BasicTexture.getTextureWidth()",this,throwable);throw throwable;}
    }

    /*// Returns the height rounded to the next power of 2.*/
    public int getTextureHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.BasicTexture.getTextureHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.BasicTexture.getTextureHeight()",this);return mTextureHeight;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.BasicTexture.getTextureHeight()",this,throwable);throw throwable;}
    }

    /*// Returns true if the texture has one pixel transparent border around the*/
    /*// actual content. This is used to avoid jigged edges.*/
    /*//*/
    /*// The jigged edges appear because we use GL_CLAMP_TO_EDGE for texture wrap*/
    /*// mode (GL_CLAMP is not available in OpenGL ES), so a pixel partially*/
    /*// covered by the texture will use the color of the edge texel. If we add*/
    /*// the transparent border, the color of the edge texel will be mixed with*/
    /*// appropriate amount of transparent.*/
    /*//*/
    /*// Currently our background is black, so we can draw the thumbnails without*/
    /*// enabling blending.*/
    public boolean hasBorder() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.BasicTexture.hasBorder()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.BasicTexture.hasBorder()",this);return mHasBorder;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.BasicTexture.hasBorder()",this,throwable);throw throwable;}
    }

    protected void setBorder(boolean hasBorder) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.BasicTexture.setBorder(boolean)",this,hasBorder);try{mHasBorder = hasBorder;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.BasicTexture.setBorder(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.BasicTexture.setBorder(boolean)",this,throwable);throw throwable;}
    }

    public void draw(GLCanvas canvas, int x, int y) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.BasicTexture.draw(GLCanvas,int,int)",this,canvas,x,y);try{canvas.drawTexture(this, x, y, getWidth(), getHeight());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.BasicTexture.draw(GLCanvas,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.BasicTexture.draw(GLCanvas,int,int)",this,throwable);throw throwable;}
    }

    public void draw(GLCanvas canvas, int x, int y, int w, int h) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.BasicTexture.draw(GLCanvas,int,int,int,int)",this,canvas,x,y,w,h);try{canvas.drawTexture(this, x, y, w, h);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.BasicTexture.draw(GLCanvas,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.BasicTexture.draw(GLCanvas,int,int,int,int)",this,throwable);throw throwable;}
    }

    /*// onBind is called before GLCanvas binds this texture.*/
    /*// It should make sure the data is uploaded to GL memory.*/
    abstract protected boolean onBind(GLCanvas canvas);

    public boolean isLoaded(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.BasicTexture.isLoaded(GLCanvas)",this,canvas);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.BasicTexture.isLoaded(GLCanvas)",this);return mState == STATE_LOADED && mCanvasRef.get() == canvas;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.BasicTexture.isLoaded(GLCanvas)",this,throwable);throw throwable;}
    }

    /*// recycle() is called when the texture will never be used again,*/
    /*// so it can free all resources.*/
    public void recycle() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.BasicTexture.recycle()",this);try{freeResource();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.BasicTexture.recycle()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.BasicTexture.recycle()",this,throwable);throw throwable;}
    }

    /*// yield() is called when the texture will not be used temporarily,*/
    /*// so it can free some resources.*/
    /*// The default implementation unloads the texture from GL memory, so*/
    /*// the subclass should make sure it can reload the texture to GL memory*/
    /*// later, or it will have to override this method.*/
    public void yield() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.BasicTexture.yield()",this);try{freeResource();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.BasicTexture.yield()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.BasicTexture.yield()",this,throwable);throw throwable;}
    }

    private void freeResource() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.BasicTexture.freeResource()",this);try{GLCanvas canvas = mCanvasRef == null ? null : mCanvasRef.get();
        if (canvas != null && isLoaded(canvas)) {
            canvas.unloadTexture(this);
        }
        mState = BasicTexture.STATE_UNLOADED;
        setAssociatedCanvas(null);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.BasicTexture.freeResource()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.BasicTexture.freeResource()",this,throwable);throw throwable;}
    }

    @Override
    protected void finalize() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.BasicTexture.finalize()",this);try{sInFinalizer.set(BasicTexture.class);
        recycle();
        sInFinalizer.set(null);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.BasicTexture.finalize()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.BasicTexture.finalize()",this,throwable);throw throwable;}
    }

    /*// This is for deciding if we can call Bitmap's recycle().*/
    /*// We cannot call Bitmap's recycle() in finalizer because at that point*/
    /*// the finalizer of Bitmap may already be called so recycle() will crash.*/
    public static boolean inFinalizer() {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.ui.BasicTexture.inFinalizer()");try{com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.ui.BasicTexture.inFinalizer()");return sInFinalizer.get() != null;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.ui.BasicTexture.inFinalizer()",throwable);throw throwable;}
    }

    public static void yieldAllTextures() {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.ui.BasicTexture.yieldAllTextures()");try{synchronized (sAllTextures) {
            for (BasicTexture t : sAllTextures.keySet()) {
                t.yield();
            }
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.ui.BasicTexture.yieldAllTextures()");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.ui.BasicTexture.yieldAllTextures()",throwable);throw throwable;}
    }
}
