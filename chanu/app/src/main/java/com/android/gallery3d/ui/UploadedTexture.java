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

import android.graphics.BitmapFactory;
import com.android.gallery3d.common.Utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;

import java.util.HashMap;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

/*// UploadedTextures use a Bitmap for the content of the texture.*/
/*//*/
/*// Subclasses should implement onGetBitmap() to provide the Bitmap and*/
/*// implement onFreeBitmap(mBitmap) which will be called when the Bitmap*/
/*// is not needed anymore.*/
/*//*/
/*// isContentValid() is meaningful only when the isLoaded() returns true.*/
/*// It means whether the content needs to be updated.*/
/*//*/
/*// The user of this class should call recycle() when the texture is not*/
/*// needed anymore.*/
/*//*/
/*// By default an UploadedTexture is opaque (so it can be drawn faster without*/
/*// blending). The user or subclass can override it using setOpaque().*/
abstract class UploadedTexture extends BasicTexture {

    /*// To prevent keeping allocation the borders, we store those used borders here.*/
    /*// Since the length will be power of two, it won't use too much memory.*/
    private static HashMap<BorderKey, Bitmap> sBorderLines =
            new HashMap<BorderKey, Bitmap>();
    private static BorderKey sBorderKey = new BorderKey();

    @SuppressWarnings("unused")
    private static final String TAG = "Texture";
    private boolean mContentValid = true;
    private boolean mOpaque = true;
    private boolean mThrottled = false;
    private static int sUploadedCount;
    private static final int UPLOAD_LIMIT = 100;

    protected Bitmap mBitmap;
    protected BitmapFactory.Options mBitmapOptions;
    private int mBorder;

    protected UploadedTexture() {
        this(false);
    }

    protected UploadedTexture(boolean hasBorder) {
        super(null, 0, STATE_UNLOADED);
        if (hasBorder) {
            setBorder(true);
            mBorder = 1;
        }
    }

    private static class BorderKey implements Cloneable {
        public boolean vertical;
        public Config config;
        public int length;

        @Override
        public int hashCode() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.UploadedTexture$BorderKey.hashCode()",this);try{int x = config.hashCode() ^ length;
            {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.UploadedTexture$BorderKey.hashCode()",this);return vertical ? x : -x;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.UploadedTexture$BorderKey.hashCode()",this,throwable);throw throwable;}
        }

        @Override
        public boolean equals(Object object) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.UploadedTexture$BorderKey.equals(java.lang.Object)",this,object);try{if (!(object instanceof BorderKey)) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.UploadedTexture$BorderKey.equals(java.lang.Object)",this);return false;}}
            BorderKey o = (BorderKey) object;
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.UploadedTexture$BorderKey.equals(java.lang.Object)",this);return vertical == o.vertical
                    && config == o.config && length == o.length;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.UploadedTexture$BorderKey.equals(java.lang.Object)",this,throwable);throw throwable;}
        }

        @Override
        public BorderKey clone() {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.UploadedTexture$BorderKey com.android.gallery3d.ui.UploadedTexture$BorderKey.clone()",this);try{try {
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.UploadedTexture$BorderKey com.android.gallery3d.ui.UploadedTexture$BorderKey.clone()",this);return (BorderKey) super.clone();}
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.UploadedTexture$BorderKey com.android.gallery3d.ui.UploadedTexture$BorderKey.clone()",this,throwable);throw throwable;}
        }
    }

    protected void setThrottled(boolean throttled) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.UploadedTexture.setThrottled(boolean)",this,throttled);try{mThrottled = throttled;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.UploadedTexture.setThrottled(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.UploadedTexture.setThrottled(boolean)",this,throwable);throw throwable;}
    }

    private static Bitmap getBorderLine(
            boolean vertical, Config config, int length) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.UploadedTexture.getBorderLine(boolean,android.graphics.Bitmap.Config,int)",vertical,config,length);try{BorderKey key = sBorderKey;
        key.vertical = vertical;
        key.config = config;
        key.length = length;
        Bitmap bitmap = sBorderLines.get(key);
        if (bitmap == null) {
            bitmap = vertical
                    ? Bitmap.createBitmap(1, length, config)
                    : Bitmap.createBitmap(length, 1, config);
            sBorderLines.put(key.clone(), bitmap);
        }
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.UploadedTexture.getBorderLine(boolean,android.graphics.Bitmap.Config,int)");return bitmap;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.UploadedTexture.getBorderLine(boolean,android.graphics.Bitmap.Config,int)",throwable);throw throwable;}
    }

    private void getBitmapBounds() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.UploadedTexture.getBitmapBounds()",this);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.UploadedTexture.getBitmapBounds()",this);if (mBitmapOptions == null) {
            mBitmapOptions = onGetBitmapBounds();
            if (mBitmapOptions == null) {
                Log.e(TAG, "Couldn't get bitmap bounds");
            }
            else {
                int w = mBitmapOptions.outWidth + mBorder * 2;
                int h = mBitmapOptions.outHeight + mBorder * 2;
                if (mWidth == UNSPECIFIED) {
                    setSize(w, h);
                } else if (mWidth != w || mHeight != h) {
                    throw new IllegalStateException(String.format(
                            "cannot change size: this = %s, orig = %sx%s, new = %sx%s",
                            toString(), mWidth, mHeight, w, h));
                }
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.UploadedTexture.getBitmapBounds()",this,throwable);throw throwable;}
    }

    private Bitmap getBitmap() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.UploadedTexture.getBitmap()",this);try{if (mBitmap == null) {
            mBitmap = onGetBitmap();
            if (mBitmap == null) {
                Log.e(TAG, "Couldn't get bitmap");
            }
            else {
                int w = mBitmap.getWidth() + mBorder * 2;
                int h = mBitmap.getHeight() + mBorder * 2;
                if (mWidth == UNSPECIFIED) {
                    setSize(w, h);
                }
/*//                else if (mWidth != w || mHeight != h) {*/
/*//                    throw new IllegalStateException(String.format(*/
/*//                            "cannot change size: this = %s, orig = %sx%s, new = %sx%s",*/
/*//                            toString(), mWidth, mHeight, w, h));*/
/*//                }*/
            }
        }
        {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.UploadedTexture.getBitmap()",this);return mBitmap;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.UploadedTexture.getBitmap()",this,throwable);throw throwable;}
    }

    private void freeBitmap() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.UploadedTexture.freeBitmap()",this);try{Utils.assertTrue(mBitmap != null);
        onFreeBitmap(mBitmap);
        mBitmap = null;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.UploadedTexture.freeBitmap()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.UploadedTexture.freeBitmap()",this,throwable);throw throwable;}
    }

    @Override
    public int getWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.UploadedTexture.getWidth()",this);try{if (mWidth == UNSPECIFIED) {getBitmapBounds();}
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.UploadedTexture.getWidth()",this);return mWidth;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.UploadedTexture.getWidth()",this,throwable);throw throwable;}
    }

    @Override
    public int getHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.UploadedTexture.getHeight()",this);try{if (mWidth == UNSPECIFIED) {getBitmapBounds();}
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.UploadedTexture.getHeight()",this);return mHeight;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.UploadedTexture.getHeight()",this,throwable);throw throwable;}
    }

    protected abstract Bitmap onGetBitmap();
    protected abstract BitmapFactory.Options onGetBitmapBounds();

    protected abstract void onFreeBitmap(Bitmap bitmap);

    protected void invalidateContent() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.UploadedTexture.invalidateContent()",this);try{if (mBitmap != null) {freeBitmap();}
        mContentValid = false;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.UploadedTexture.invalidateContent()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.UploadedTexture.invalidateContent()",this,throwable);throw throwable;}
    }

    /**
     * Whether the content on GPU is valid.
     */
    public boolean isContentValid(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.UploadedTexture.isContentValid(GLCanvas)",this,canvas);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.UploadedTexture.isContentValid(GLCanvas)",this);return isLoaded(canvas) && mContentValid;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.UploadedTexture.isContentValid(GLCanvas)",this,throwable);throw throwable;}
    }

    /**
     * Updates the content on GPU's memory.
     * @param canvas
     */
    public void updateContent(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.UploadedTexture.updateContent(GLCanvas)",this,canvas);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.UploadedTexture.updateContent(GLCanvas)",this);if (!isLoaded(canvas)) {
            if (mThrottled && ++sUploadedCount > UPLOAD_LIMIT) {
                return;
            }
            uploadToCanvas(canvas);
        } else if (!mContentValid) {
            try {
            Bitmap bitmap = getBitmap();
            int format = GLUtils.getInternalFormat(bitmap);
            int type = GLUtils.getType(bitmap);
            canvas.getGLInstance().glBindTexture(GL11.GL_TEXTURE_2D, mId);
            GLUtils.texSubImage2D(GL11.GL_TEXTURE_2D, 0, mBorder, mBorder,
                    bitmap, format, type);
            freeBitmap();
            mContentValid = true;
            }
            catch (Error e) {
                Log.e(TAG, "Error loading bitmap", e);
                if (mBitmap != null)
                    {freeBitmap();}
                mContentValid = false;
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.UploadedTexture.updateContent(GLCanvas)",this,throwable);throw throwable;}
    }

    public static void resetUploadLimit() {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.ui.UploadedTexture.resetUploadLimit()");try{sUploadedCount = 0;com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.ui.UploadedTexture.resetUploadLimit()");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.ui.UploadedTexture.resetUploadLimit()",throwable);throw throwable;}
    }

    public static boolean uploadLimitReached() {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.ui.UploadedTexture.uploadLimitReached()");try{com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.ui.UploadedTexture.uploadLimitReached()");return sUploadedCount > UPLOAD_LIMIT;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.ui.UploadedTexture.uploadLimitReached()",throwable);throw throwable;}
    }

    static int[] sTextureId = new int[1];
    static float[] sCropRect = new float[4];

    private void uploadToCanvas(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.UploadedTexture.uploadToCanvas(GLCanvas)",this,canvas);try{GL11 gl = canvas.getGLInstance();

        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            try {
                int bWidth = bitmap.getWidth();
                int bHeight = bitmap.getHeight();
                int width = bWidth + mBorder * 2;
                int height = bHeight + mBorder * 2;
                int texWidth = getTextureWidth();
                int texHeight = getTextureHeight();
                /*// Define a vertically flipped crop rectangle for*/
                /*// OES_draw_texture.*/
                /*// The four values in sCropRect are: left, bottom, width, and*/
                /*// height. Negative value of width or height means flip.*/
                sCropRect[0] = mBorder;
                sCropRect[1] = mBorder + bHeight;
                sCropRect[2] = bWidth;
                sCropRect[3] = -bHeight;

                /*// Upload the bitmap to a new texture.*/
                gl.glGenTextures(1, sTextureId, 0);
                gl.glBindTexture(GL11.GL_TEXTURE_2D, sTextureId[0]);
                gl.glTexParameterfv(GL11.GL_TEXTURE_2D,
                        GL11Ext.GL_TEXTURE_CROP_RECT_OES, sCropRect, 0);
                gl.glTexParameteri(GL11.GL_TEXTURE_2D,
                        GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP_TO_EDGE);
                gl.glTexParameteri(GL11.GL_TEXTURE_2D,
                        GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP_TO_EDGE);
                gl.glTexParameterf(GL11.GL_TEXTURE_2D,
                        GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                gl.glTexParameterf(GL11.GL_TEXTURE_2D,
                        GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

                if (bWidth == texWidth && bHeight == texHeight) {
                    GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, bitmap, 0);
                } else {
                    int format = GLUtils.getInternalFormat(bitmap);
                    int type = GLUtils.getType(bitmap);
                    Config config = bitmap.getConfig();
                    gl.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format,
                            texWidth, texHeight, 0, format, type, null);
                    GLUtils.texSubImage2D(GL11.GL_TEXTURE_2D, 0,
                            mBorder, mBorder, bitmap, format, type);

                    if (mBorder > 0) {
                        /*// Left border*/
                        Bitmap line = getBorderLine(true, config, texHeight);
                        GLUtils.texSubImage2D(GL11.GL_TEXTURE_2D, 0,
                                0, 0, line, format, type);

                        /*// Top border*/
                        line = getBorderLine(false, config, texWidth);
                        GLUtils.texSubImage2D(GL11.GL_TEXTURE_2D, 0,
                                0, 0, line, format, type);
                    }

                    /*// Right border*/
                    if (mBorder + bWidth < texWidth) {
                        Bitmap line = getBorderLine(true, config, texHeight);
                        GLUtils.texSubImage2D(GL11.GL_TEXTURE_2D, 0,
                                mBorder + bWidth, 0, line, format, type);
                    }

                    /*// Bottom border*/
                    if (mBorder + bHeight < texHeight) {
                        Bitmap line = getBorderLine(false, config, texWidth);
                        GLUtils.texSubImage2D(GL11.GL_TEXTURE_2D, 0,
                                0, mBorder + bHeight, line, format, type);
                    }                    
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception handling texture", e);
            } finally {
                freeBitmap();
            }
            /*// Update texture state.*/
            setAssociatedCanvas(canvas);
            mId = sTextureId[0];
            mState = UploadedTexture.STATE_LOADED;
            mContentValid = true;
        } else {
            mState = STATE_ERROR;
            throw new RuntimeException("Texture load fail, no bitmap");
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.UploadedTexture.uploadToCanvas(GLCanvas)",this,throwable);throw throwable;}
    }

    @Override
    protected boolean onBind(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.UploadedTexture.onBind(GLCanvas)",this,canvas);try{updateContent(canvas);
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.UploadedTexture.onBind(GLCanvas)",this);return isContentValid(canvas);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.UploadedTexture.onBind(GLCanvas)",this,throwable);throw throwable;}
    }

    public void setOpaque(boolean isOpaque) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.UploadedTexture.setOpaque(boolean)",this,isOpaque);try{mOpaque = isOpaque;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.UploadedTexture.setOpaque(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.UploadedTexture.setOpaque(boolean)",this,throwable);throw throwable;}
    }

    public boolean isOpaque() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.UploadedTexture.isOpaque()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.UploadedTexture.isOpaque()",this);return mOpaque;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.UploadedTexture.isOpaque()",this,throwable);throw throwable;}
    }

    @Override
    public void recycle() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.UploadedTexture.recycle()",this);try{super.recycle();
        if (mBitmap != null) {freeBitmap();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.UploadedTexture.recycle()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.UploadedTexture.recycle()",this,throwable);throw throwable;}
    }
}
