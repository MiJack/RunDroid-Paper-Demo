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
import com.android.gallery3d.util.IntArray;

import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLU;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Stack;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

public class GLCanvasImpl implements GLCanvas {
    @SuppressWarnings("unused")
    private static final String TAG = "GLCanvasImp";

    private static final float OPAQUE_ALPHA = 0.95f;

    private static final int OFFSET_FILL_RECT = 0;
    private static final int OFFSET_DRAW_LINE = 4;
    private static final int OFFSET_DRAW_RECT = 6;
    private static final float[] BOX_COORDINATES = {
            0, 0, 1, 0, 0, 1, 1, 1,  /*// used for filling a rectangle*/
            0, 0, 1, 1,              /*// used for drawing a line*/
            0, 0, 0, 1, 1, 1, 1, 0}; /*// used for drawing the outline of a rectangle*/

    private final GL11 mGL;

    private final float mMatrixValues[] = new float[16];
    private final float mTextureMatrixValues[] = new float[16];

    /*// mapPoints needs 10 input and output numbers.*/
    private final float mMapPointsBuffer[] = new float[10];

    private final float mTextureColor[] = new float[4];

    private int mBoxCoords;

    private final GLState mGLState;

    private long mAnimationTime;

    private float mAlpha;
    private final Rect mClipRect = new Rect();
    private final Stack<ConfigState> mRestoreStack =
            new Stack<ConfigState>();
    private ConfigState mRecycledRestoreAction;

    private final RectF mDrawTextureSourceRect = new RectF();
    private final RectF mDrawTextureTargetRect = new RectF();
    private final float[] mTempMatrix = new float[32];
    private final IntArray mUnboundTextures = new IntArray();
    private final IntArray mDeleteBuffers = new IntArray();
    private int mHeight;
    private boolean mBlendEnabled = true;

    /*// Drawing statistics*/
    int mCountDrawLine;
    int mCountFillRect;
    int mCountDrawMesh;
    int mCountTextureRect;
    int mCountTextureOES;

    GLCanvasImpl(GL11 gl) {
        mGL = gl;
        mGLState = new GLState(gl);
        initialize();
    }

    public void setSize(int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.setSize(int,int)",this,width,height);try{Utils.assertTrue(width >= 0 && height >= 0);
        mHeight = height;

        GL11 gl = mGL;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL11.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, 0, width, 0, height);

        gl.glMatrixMode(GL11.GL_MODELVIEW);
        gl.glLoadIdentity();
        float matrix[] = mMatrixValues;

        Matrix.setIdentityM(matrix, 0);
        Matrix.translateM(matrix, 0, 0, mHeight, 0);
        Matrix.scaleM(matrix, 0, 1, -1, 1);

        mClipRect.set(0, 0, width, height);
        gl.glScissor(0, 0, width, height);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.setSize(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.setSize(int,int)",this,throwable);throw throwable;}
    }

    public long currentAnimationTimeMillis() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.ui.GLCanvasImpl.currentAnimationTimeMillis()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.ui.GLCanvasImpl.currentAnimationTimeMillis()",this);return mAnimationTime;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.ui.GLCanvasImpl.currentAnimationTimeMillis()",this,throwable);throw throwable;}
    }

    public void setAlpha(float alpha) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.setAlpha(float)",this,alpha);try{Utils.assertTrue(alpha >= 0 && alpha <= 1);
        mAlpha = alpha;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.setAlpha(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.setAlpha(float)",this,throwable);throw throwable;}
    }

    public void multiplyAlpha(float alpha) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.multiplyAlpha(float)",this,alpha);try{Utils.assertTrue(alpha >= 0 && alpha <= 1);
        mAlpha *= alpha;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.multiplyAlpha(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.multiplyAlpha(float)",this,throwable);throw throwable;}
    }

    public float getAlpha() {
        com.mijack.Xlog.logMethodEnter("float com.android.gallery3d.ui.GLCanvasImpl.getAlpha()",this);try{com.mijack.Xlog.logMethodExit("float com.android.gallery3d.ui.GLCanvasImpl.getAlpha()",this);return mAlpha;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.android.gallery3d.ui.GLCanvasImpl.getAlpha()",this,throwable);throw throwable;}
    }

    private static ByteBuffer allocateDirectNativeOrderBuffer(int size) {
        com.mijack.Xlog.logStaticMethodEnter("java.nio.ByteBuffer com.android.gallery3d.ui.GLCanvasImpl.allocateDirectNativeOrderBuffer(int)",size);try{com.mijack.Xlog.logStaticMethodExit("java.nio.ByteBuffer com.android.gallery3d.ui.GLCanvasImpl.allocateDirectNativeOrderBuffer(int)");return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.nio.ByteBuffer com.android.gallery3d.ui.GLCanvasImpl.allocateDirectNativeOrderBuffer(int)",throwable);throw throwable;}
    }

    private void initialize() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.initialize()",this);try{GL11 gl = mGL;

        /*// First create an nio buffer, then create a VBO from it.*/
        int size = BOX_COORDINATES.length * Float.SIZE / Byte.SIZE;
        FloatBuffer xyBuffer = allocateDirectNativeOrderBuffer(size).asFloatBuffer();
        xyBuffer.put(BOX_COORDINATES, 0, BOX_COORDINATES.length).position(0);

        int[] name = new int[1];
        gl.glGenBuffers(1, name, 0);
        mBoxCoords = name[0];

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, mBoxCoords);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER,
                xyBuffer.capacity() * (Float.SIZE / Byte.SIZE),
                xyBuffer, GL11.GL_STATIC_DRAW);

        gl.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
        gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);

        /*// Enable the texture coordinate array for Texture 1*/
        gl.glClientActiveTexture(GL11.GL_TEXTURE1);
        gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
        gl.glClientActiveTexture(GL11.GL_TEXTURE0);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        /*// mMatrixValues will be initialized in setSize()*/
        mAlpha = 1.0f;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.initialize()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.initialize()",this,throwable);throw throwable;}
    }

    public void drawRect(float x, float y, float width, float height, GLPaint paint) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.drawRect(float,float,float,float,com.android.gallery3d.ui.GLPaint)",this,x,y,width,height,paint);try{GL11 gl = mGL;

        mGLState.setColorMode(paint.getColor(), mAlpha);
        mGLState.setLineWidth(paint.getLineWidth());
        mGLState.setLineSmooth(paint.getAntiAlias());

        saveTransform();
        translate(x, y, 0);
        scale(width, height, 1);

        gl.glLoadMatrixf(mMatrixValues, 0);
        gl.glDrawArrays(GL11.GL_LINE_LOOP, OFFSET_DRAW_RECT, 4);

        restoreTransform();
        mCountDrawLine++;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawRect(float,float,float,float,com.android.gallery3d.ui.GLPaint)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.drawRect(float,float,float,float,com.android.gallery3d.ui.GLPaint)",this,throwable);throw throwable;}
    }

    public void drawLine(float x1, float y1, float x2, float y2, GLPaint paint) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.drawLine(float,float,float,float,com.android.gallery3d.ui.GLPaint)",this,x1,y1,x2,y2,paint);try{GL11 gl = mGL;

        mGLState.setColorMode(paint.getColor(), mAlpha);
        mGLState.setLineWidth(paint.getLineWidth());
        mGLState.setLineSmooth(paint.getAntiAlias());

        saveTransform();
        translate(x1, y1, 0);
        scale(x2 - x1, y2 - y1, 1);

        gl.glLoadMatrixf(mMatrixValues, 0);
        gl.glDrawArrays(GL11.GL_LINE_STRIP, OFFSET_DRAW_LINE, 2);

        restoreTransform();
        mCountDrawLine++;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawLine(float,float,float,float,com.android.gallery3d.ui.GLPaint)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.drawLine(float,float,float,float,com.android.gallery3d.ui.GLPaint)",this,throwable);throw throwable;}
    }

    public void fillRect(float x, float y, float width, float height, int color) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.fillRect(float,float,float,float,int)",this,x,y,width,height,color);try{mGLState.setColorMode(color, mAlpha);
        GL11 gl = mGL;

        saveTransform();
        translate(x, y, 0);
        scale(width, height, 1);

        gl.glLoadMatrixf(mMatrixValues, 0);
        gl.glDrawArrays(GL11.GL_TRIANGLE_STRIP, OFFSET_FILL_RECT, 4);

        restoreTransform();
        mCountFillRect++;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.fillRect(float,float,float,float,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.fillRect(float,float,float,float,int)",this,throwable);throw throwable;}
    }

    public void translate(float x, float y, float z) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.translate(float,float,float)",this,x,y,z);try{Matrix.translateM(mMatrixValues, 0, x, y, z);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.translate(float,float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.translate(float,float,float)",this,throwable);throw throwable;}
    }

    public void scale(float sx, float sy, float sz) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.scale(float,float,float)",this,sx,sy,sz);try{Matrix.scaleM(mMatrixValues, 0, sx, sy, sz);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.scale(float,float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.scale(float,float,float)",this,throwable);throw throwable;}
    }

    public void rotate(float angle, float x, float y, float z) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.rotate(float,float,float,float)",this,angle,x,y,z);try{float[] temp = mTempMatrix;
        Matrix.setRotateM(temp, 0, angle, x, y, z);
        Matrix.multiplyMM(temp, 16, mMatrixValues, 0, temp, 0);
        System.arraycopy(temp, 16, mMatrixValues, 0, 16);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.rotate(float,float,float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.rotate(float,float,float,float)",this,throwable);throw throwable;}
    }

    public void multiplyMatrix(float matrix[], int offset) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.multiplyMatrix(float,int)",this,matrix[],offset);try{float[] temp = mTempMatrix;
        Matrix.multiplyMM(temp, 0, mMatrixValues, 0, matrix, offset);
        System.arraycopy(temp, 0, mMatrixValues, 0, 16);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.multiplyMatrix(float,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.multiplyMatrix(float,int)",this,throwable);throw throwable;}
    }

    private void textureRect(float x, float y, float width, float height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.textureRect(float,float,float,float)",this,x,y,width,height);try{GL11 gl = mGL;

        saveTransform();
        translate(x, y, 0);
        scale(width, height, 1);

        gl.glLoadMatrixf(mMatrixValues, 0);
        gl.glDrawArrays(GL11.GL_TRIANGLE_STRIP, OFFSET_FILL_RECT, 4);

        restoreTransform();
        mCountTextureRect++;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.textureRect(float,float,float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.textureRect(float,float,float,float)",this,throwable);throw throwable;}
    }

    public void drawMesh(BasicTexture tex, int x, int y, int xyBuffer,
            int uvBuffer, int indexBuffer, int indexCount) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.drawMesh(com.android.gallery3d.ui.BasicTexture,int,int,int,int,int,int)",this,tex,x,y,xyBuffer,uvBuffer,indexBuffer,indexCount);try{float alpha = mAlpha;
        if (!bindTexture(tex)) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawMesh(com.android.gallery3d.ui.BasicTexture,int,int,int,int,int,int)",this);return;}}

        mGLState.setBlendEnabled(mBlendEnabled
                && (!tex.isOpaque() || alpha < OPAQUE_ALPHA));
        mGLState.setTextureAlpha(alpha);

        /*// Reset the texture matrix. We will set our own texture coordinates*/
        /*// below.*/
        setTextureCoords(0, 0, 1, 1);

        saveTransform();
        translate(x, y, 0);

        mGL.glLoadMatrixf(mMatrixValues, 0);

        mGL.glBindBuffer(GL11.GL_ARRAY_BUFFER, xyBuffer);
        mGL.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);

        mGL.glBindBuffer(GL11.GL_ARRAY_BUFFER, uvBuffer);
        mGL.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);

        mGL.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        mGL.glDrawElements(GL11.GL_TRIANGLE_STRIP,
                indexCount, GL11.GL_UNSIGNED_BYTE, 0);

        mGL.glBindBuffer(GL11.GL_ARRAY_BUFFER, mBoxCoords);
        mGL.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
        mGL.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);

        restoreTransform();
        mCountDrawMesh++;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.drawMesh(com.android.gallery3d.ui.BasicTexture,int,int,int,int,int,int)",this,throwable);throw throwable;}
    }

    private float[] mapPoints(float matrix[], int x1, int y1, int x2, int y2) {
        com.mijack.Xlog.logMethodEnter("[float com.android.gallery3d.ui.GLCanvasImpl.mapPoints(float,int,int,int,int)",this,matrix[],x1,y1,x2,y2);try{float[] point = mMapPointsBuffer;
        int srcOffset = 6;
        point[srcOffset] = x1;
        point[srcOffset + 1] = y1;
        point[srcOffset + 2] = 0;
        point[srcOffset + 3] = 1;

        int resultOffset = 0;
        Matrix.multiplyMV(point, resultOffset, matrix, 0, point, srcOffset);
        point[resultOffset] /= point[resultOffset + 3];
        point[resultOffset + 1] /= point[resultOffset + 3];

        /*// map the second point*/
        point[srcOffset] = x2;
        point[srcOffset + 1] = y2;
        resultOffset = 2;
        Matrix.multiplyMV(point, resultOffset, matrix, 0, point, srcOffset);
        point[resultOffset] /= point[resultOffset + 3];
        point[resultOffset + 1] /= point[resultOffset + 3];

        {com.mijack.Xlog.logMethodExit("[float com.android.gallery3d.ui.GLCanvasImpl.mapPoints(float,int,int,int,int)",this);return point;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[float com.android.gallery3d.ui.GLCanvasImpl.mapPoints(float,int,int,int,int)",this,throwable);throw throwable;}
    }

    public boolean clipRect(int left, int top, int right, int bottom) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLCanvasImpl.clipRect(int,int,int,int)",this,left,top,right,bottom);try{float point[] = mapPoints(mMatrixValues, left, top, right, bottom);

        /*// mMatrix could be a rotation matrix. In this case, we need to find*/
        /*// the boundaries after rotation. (only handle 90 * n degrees)*/
        if (point[0] > point[2]) {
            left = (int) point[2];
            right = (int) point[0];
        } else {
            left = (int) point[0];
            right = (int) point[2];
        }
        if (point[1] > point[3]) {
            top = (int) point[3];
            bottom = (int) point[1];
        } else {
            top = (int) point[1];
            bottom = (int) point[3];
        }
        Rect clip = mClipRect;

        boolean intersect = clip.intersect(left, top, right, bottom);
        if (!intersect) {clip.set(0, 0, 0, 0);}
        mGL.glScissor(clip.left, clip.top, clip.width(), clip.height());
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLCanvasImpl.clipRect(int,int,int,int)",this);return intersect;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLCanvasImpl.clipRect(int,int,int,int)",this,throwable);throw throwable;}
    }

    private void drawBoundTexture(
            BasicTexture texture, int x, int y, int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.drawBoundTexture(com.android.gallery3d.ui.BasicTexture,int,int,int,int)",this,texture,x,y,width,height);try{/*// Test whether it has been rotated or flipped, if so, glDrawTexiOES*/
        /*// won't work*/
        if (isMatrixRotatedOrFlipped(mMatrixValues)) {
            if (texture.hasBorder()) {
                setTextureCoords(
                        1.0f / texture.getTextureWidth(),
                        1.0f / texture.getTextureHeight(),
                        (texture.getWidth() - 1.0f) / texture.getTextureWidth(),
                        (texture.getHeight() - 1.0f) / texture.getTextureHeight());
            } else {
                setTextureCoords(0, 0,
                        (float) texture.getWidth() / texture.getTextureWidth(),
                        (float) texture.getHeight() / texture.getTextureHeight());
            }
            textureRect(x, y, width, height);
        } else {
            /*// draw the rect from bottom-left to top-right*/
            float points[] = mapPoints(
                    mMatrixValues, x, y + height, x + width, y);
            x = Math.round(points[0]);
            y = Math.round(points[1]);
            width = Math.round(points[2]) - x;
            height = Math.round(points[3]) - y;
            if (width > 0 && height > 0) {
                ((GL11Ext) mGL).glDrawTexiOES(x, y, 0, width, height);
                mCountTextureOES++;
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawBoundTexture(com.android.gallery3d.ui.BasicTexture,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.drawBoundTexture(com.android.gallery3d.ui.BasicTexture,int,int,int,int)",this,throwable);throw throwable;}
    }

    public void drawTexture(
            BasicTexture texture, int x, int y, int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.drawTexture(com.android.gallery3d.ui.BasicTexture,int,int,int,int)",this,texture,x,y,width,height);try{drawTexture(texture, x, y, width, height, mAlpha);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawTexture(com.android.gallery3d.ui.BasicTexture,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.drawTexture(com.android.gallery3d.ui.BasicTexture,int,int,int,int)",this,throwable);throw throwable;}
    }

    public void setBlendEnabled(boolean enabled) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.setBlendEnabled(boolean)",this,enabled);try{mBlendEnabled = enabled;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.setBlendEnabled(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.setBlendEnabled(boolean)",this,throwable);throw throwable;}
    }

    public void drawTexture(BasicTexture texture,
            int x, int y, int width, int height, float alpha) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.drawTexture(com.android.gallery3d.ui.BasicTexture,int,int,int,int,float)",this,texture,x,y,width,height,alpha);try{if (width <= 0 || height <= 0) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawTexture(com.android.gallery3d.ui.BasicTexture,int,int,int,int,float)",this);return;}}

        mGLState.setBlendEnabled(mBlendEnabled
                && (!texture.isOpaque() || alpha < OPAQUE_ALPHA));
        if (!bindTexture(texture)) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawTexture(com.android.gallery3d.ui.BasicTexture,int,int,int,int,float)",this);return;}}
        mGLState.setTextureAlpha(alpha);
        drawBoundTexture(texture, x, y, width, height);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.drawTexture(com.android.gallery3d.ui.BasicTexture,int,int,int,int,float)",this,throwable);throw throwable;}
    }

    public void drawTexture(BasicTexture texture, RectF source, RectF target) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.drawTexture(com.android.gallery3d.ui.BasicTexture,android.graphics.RectF,android.graphics.RectF)",this,texture,source,target);try{if (target.width() <= 0 || target.height() <= 0) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawTexture(com.android.gallery3d.ui.BasicTexture,android.graphics.RectF,android.graphics.RectF)",this);return;}}

        /*// Copy the input to avoid changing it.*/
        mDrawTextureSourceRect.set(source);
        mDrawTextureTargetRect.set(target);
        source = mDrawTextureSourceRect;
        target = mDrawTextureTargetRect;

        mGLState.setBlendEnabled(mBlendEnabled
                && (!texture.isOpaque() || mAlpha < OPAQUE_ALPHA));
        if (!bindTexture(texture)) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawTexture(com.android.gallery3d.ui.BasicTexture,android.graphics.RectF,android.graphics.RectF)",this);return;}}
        convertCoordinate(source, target, texture);
        setTextureCoords(source);
        mGLState.setTextureAlpha(mAlpha);
        textureRect(target.left, target.top, target.width(), target.height());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.drawTexture(com.android.gallery3d.ui.BasicTexture,android.graphics.RectF,android.graphics.RectF)",this,throwable);throw throwable;}
    }

    /*// This function changes the source coordinate to the texture coordinates.*/
    /*// It also clips the source and target coordinates if it is beyond the*/
    /*// bound of the texture.*/
    private void convertCoordinate(RectF source, RectF target,
            BasicTexture texture) {

        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.convertCoordinate(android.graphics.RectF,android.graphics.RectF,com.android.gallery3d.ui.BasicTexture)",this,source,target,texture);try{int width = texture.getWidth();
        int height = texture.getHeight();
        int texWidth = texture.getTextureWidth();
        int texHeight = texture.getTextureHeight();
        /*// Convert to texture coordinates*/
        source.left /= texWidth;
        source.right /= texWidth;
        source.top /= texHeight;
        source.bottom /= texHeight;

        /*// Clip if the rendering range is beyond the bound of the texture.*/
        float xBound = (float) width / texWidth;
        if (source.right > xBound) {
            target.right = target.left + target.width() *
                    (xBound - source.left) / source.width();
            source.right = xBound;
        }
        float yBound = (float) height / texHeight;
        if (source.bottom > yBound) {
            target.bottom = target.top + target.height() *
                    (yBound - source.top) / source.height();
            source.bottom = yBound;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.convertCoordinate(android.graphics.RectF,android.graphics.RectF,com.android.gallery3d.ui.BasicTexture)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.convertCoordinate(android.graphics.RectF,android.graphics.RectF,com.android.gallery3d.ui.BasicTexture)",this,throwable);throw throwable;}
    }

    public void drawMixed(BasicTexture from,
            int toColor, float ratio, int x, int y, int w, int h) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,int,float,int,int,int,int)",this,from,toColor,ratio,x,y,w,h);try{drawMixed(from, toColor, ratio, x, y, w, h, mAlpha);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,int,float,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,int,float,int,int,int,int)",this,throwable);throw throwable;}
    }

    public void drawMixed(BasicTexture from, BasicTexture to,
            float ratio, int x, int y, int w, int h) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,com.android.gallery3d.ui.BasicTexture,float,int,int,int,int)",this,from,to,ratio,x,y,w,h);try{drawMixed(from, to, ratio, x, y, w, h, mAlpha);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,com.android.gallery3d.ui.BasicTexture,float,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,com.android.gallery3d.ui.BasicTexture,float,int,int,int,int)",this,throwable);throw throwable;}
    }

    private boolean bindTexture(BasicTexture texture) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLCanvasImpl.bindTexture(com.android.gallery3d.ui.BasicTexture)",this,texture);try{if (!texture.onBind(this)) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLCanvasImpl.bindTexture(com.android.gallery3d.ui.BasicTexture)",this);return false;}}
        mGLState.setTexture2DEnabled(true);
        mGL.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLCanvasImpl.bindTexture(com.android.gallery3d.ui.BasicTexture)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLCanvasImpl.bindTexture(com.android.gallery3d.ui.BasicTexture)",this,throwable);throw throwable;}
    }

    private void setTextureColor(float r, float g, float b, float alpha) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.setTextureColor(float,float,float,float)",this,r,g,b,alpha);try{float[] color = mTextureColor;
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = alpha;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.setTextureColor(float,float,float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.setTextureColor(float,float,float,float)",this,throwable);throw throwable;}
    }

    private void drawMixed(BasicTexture from, int toColor,
            float ratio, int x, int y, int width, int height, float alpha) {

        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,int,float,int,int,int,int,float)",this,from,toColor,ratio,x,y,width,height,alpha);try{if (ratio <= 0) {
            drawTexture(from, x, y, width, height, alpha);
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,int,float,int,int,int,int,float)",this);return;}
        } else if (ratio >= 1) {
            fillRect(x, y, width, height, toColor);
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,int,float,int,int,int,int,float)",this);return;}
        }

        mGLState.setBlendEnabled(mBlendEnabled && (!from.isOpaque()
                || !Utils.isOpaque(toColor) || alpha < OPAQUE_ALPHA));

        final GL11 gl = mGL;
        if (!bindTexture(from)) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,int,float,int,int,int,int,float)",this);return;}}

        /*//*/
        /*// The formula we want:*/
        /*//     alpha * ((1 - ratio) * from + ratio * to)*/
        /*// The formula that GL supports is in the form of:*/
        /*//     combo * (modulate * from) + (1 - combo) * to*/
        /*//*/
        /*// So, we have combo = 1 - alpha * ratio*/
        /*//     and     modulate = alpha * (1f - ratio) / combo*/
        /*//*/
        float comboRatio = 1 - alpha * ratio;

        /*// handle the case that (1 - comboRatio) == 0*/
        if (alpha < OPAQUE_ALPHA) {
            mGLState.setTextureAlpha(alpha * (1f - ratio) / comboRatio);
        } else {
            mGLState.setTextureAlpha(1f);
        }

        /*// Interpolate the RGB and alpha values between both textures.*/
        mGLState.setTexEnvMode(GL11.GL_COMBINE);
        /*// Specify the interpolation factor via the alpha component of*/
        /*// GL_TEXTURE_ENV_COLORs.*/
        /*// RGB component are get from toColor and will used as SRC1*/
        float colorAlpha = (float) (toColor >>> 24) / (0xff * 0xff);
        setTextureColor(((toColor >>> 16) & 0xff) * colorAlpha,
                ((toColor >>> 8) & 0xff) * colorAlpha,
                (toColor & 0xff) * colorAlpha, comboRatio);
        gl.glTexEnvfv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, mTextureColor, 0);

        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_COMBINE_RGB, GL11.GL_INTERPOLATE);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_COMBINE_ALPHA, GL11.GL_INTERPOLATE);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC1_RGB, GL11.GL_CONSTANT);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC1_ALPHA, GL11.GL_CONSTANT);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);

        /*// Wire up the interpolation factor for RGB.*/
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC2_RGB, GL11.GL_CONSTANT);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);

        /*// Wire up the interpolation factor for alpha.*/
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC2_ALPHA, GL11.GL_CONSTANT);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND2_ALPHA, GL11.GL_SRC_ALPHA);

        drawBoundTexture(from, x, y, width, height);
        mGLState.setTexEnvMode(GL11.GL_REPLACE);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,int,float,int,int,int,int,float)",this,throwable);throw throwable;}
    }

    private void drawMixed(BasicTexture from, BasicTexture to,
            float ratio, int x, int y, int width, int height, float alpha) {

        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,com.android.gallery3d.ui.BasicTexture,float,int,int,int,int,float)",this,from,to,ratio,x,y,width,height,alpha);try{if (ratio <= 0) {
            drawTexture(from, x, y, width, height, alpha);
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,com.android.gallery3d.ui.BasicTexture,float,int,int,int,int,float)",this);return;}
        } else if (ratio >= 1) {
            drawTexture(to, x, y, width, height, alpha);
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,com.android.gallery3d.ui.BasicTexture,float,int,int,int,int,float)",this);return;}
        }

        /*// In the current implementation the two textures must have the*/
        /*// same size.*/
        Utils.assertTrue(from.getWidth() == to.getWidth()
                && from.getHeight() == to.getHeight());

        mGLState.setBlendEnabled(mBlendEnabled && (!from.isOpaque()
                || !to.isOpaque() || alpha < OPAQUE_ALPHA));

        final GL11 gl = mGL;
        if (!bindTexture(from)) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,com.android.gallery3d.ui.BasicTexture,float,int,int,int,int,float)",this);return;}}

        /*//*/
        /*// The formula we want:*/
        /*//     alpha * ((1 - ratio) * from + ratio * to)*/
        /*// The formula that GL supports is in the form of:*/
        /*//     combo * (modulate * from) + (1 - combo) * to*/
        /*//*/
        /*// So, we have combo = 1 - alpha * ratio*/
        /*//     and     modulate = alpha * (1f - ratio) / combo*/
        /*//*/
        float comboRatio = 1 - alpha * ratio;

        /*// handle the case that (1 - comboRatio) == 0*/
        if (alpha < OPAQUE_ALPHA) {
            mGLState.setTextureAlpha(alpha * (1f - ratio) / comboRatio);
        } else {
            mGLState.setTextureAlpha(1f);
        }

        gl.glActiveTexture(GL11.GL_TEXTURE1);
        if (!bindTexture(to)) {
            /*// Disable TEXTURE1.*/
            gl.glDisable(GL11.GL_TEXTURE_2D);
            /*// Switch back to the default texture unit.*/
            gl.glActiveTexture(GL11.GL_TEXTURE0);
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,com.android.gallery3d.ui.BasicTexture,float,int,int,int,int,float)",this);return;}
        }
        gl.glEnable(GL11.GL_TEXTURE_2D);

        /*// Interpolate the RGB and alpha values between both textures.*/
        mGLState.setTexEnvMode(GL11.GL_COMBINE);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_COMBINE_RGB, GL11.GL_INTERPOLATE);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_COMBINE_ALPHA, GL11.GL_INTERPOLATE);

        /*// Specify the interpolation factor via the alpha component of*/
        /*// GL_TEXTURE_ENV_COLORs.*/
        /*// We don't use the RGB color, so just give them 0s.*/
        setTextureColor(0, 0, 0, comboRatio);
        gl.glTexEnvfv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, mTextureColor, 0);

        /*// Wire up the interpolation factor for RGB.*/
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC2_RGB, GL11.GL_CONSTANT);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);

        /*// Wire up the interpolation factor for alpha.*/
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC2_ALPHA, GL11.GL_CONSTANT);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND2_ALPHA, GL11.GL_SRC_ALPHA);

        /*// Draw the combined texture.*/
        drawBoundTexture(to, x, y, width, height);

        /*// Disable TEXTURE1.*/
        gl.glDisable(GL11.GL_TEXTURE_2D);
        /*// Switch back to the default texture unit.*/
        gl.glActiveTexture(GL11.GL_TEXTURE0);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.drawMixed(com.android.gallery3d.ui.BasicTexture,com.android.gallery3d.ui.BasicTexture,float,int,int,int,int,float)",this,throwable);throw throwable;}
    }

    /*// TODO: the code only work for 2D should get fixed for 3D or removed*/
    private static final int MSKEW_X = 4;
    private static final int MSKEW_Y = 1;
    private static final int MSCALE_X = 0;
    private static final int MSCALE_Y = 5;

    private static boolean isMatrixRotatedOrFlipped(float matrix[]) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.ui.GLCanvasImpl.isMatrixRotatedOrFlipped(float)",matrix[]);try{final float eps = 1e-5f;
        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.ui.GLCanvasImpl.isMatrixRotatedOrFlipped(float)");return Math.abs(matrix[MSKEW_X]) > eps
                || Math.abs(matrix[MSKEW_Y]) > eps
                || matrix[MSCALE_X] < -eps
                || matrix[MSCALE_Y] > eps;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLCanvasImpl.isMatrixRotatedOrFlipped(float)",throwable);throw throwable;}
    }

    public BasicTexture copyTexture(int x, int y, int width, int height) {

        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.BasicTexture com.android.gallery3d.ui.GLCanvasImpl.copyTexture(int,int,int,int)",this,x,y,width,height);try{if (isMatrixRotatedOrFlipped(mMatrixValues)) {
            throw new IllegalArgumentException("cannot support rotated matrix");
        }
        float points[] = mapPoints(mMatrixValues, x, y + height, x + width, y);
        x = (int) points[0];
        y = (int) points[1];
        width = (int) points[2] - x;
        height = (int) points[3] - y;

        GL11 gl = mGL;

        RawTexture texture = RawTexture.newInstance(this);
        gl.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
        texture.setSize(width, height);

        int[] cropRect = {0,  0, width, height};
        gl.glTexParameteriv(GL11.GL_TEXTURE_2D,
                GL11Ext.GL_TEXTURE_CROP_RECT_OES, cropRect, 0);
        gl.glTexParameteri(GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        gl.glTexParameterf(GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        gl.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0,
                GL11.GL_RGB, x, y, texture.getTextureWidth(),
                texture.getTextureHeight(), 0);

        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.BasicTexture com.android.gallery3d.ui.GLCanvasImpl.copyTexture(int,int,int,int)",this);return texture;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.BasicTexture com.android.gallery3d.ui.GLCanvasImpl.copyTexture(int,int,int,int)",this,throwable);throw throwable;}
    }

    private static class GLState {

        private final GL11 mGL;

        private int mTexEnvMode = GL11.GL_REPLACE;
        private float mTextureAlpha = 1.0f;
        private boolean mTexture2DEnabled = true;
        private boolean mBlendEnabled = true;
        private float mLineWidth = 1.0f;
        private boolean mLineSmooth = false;

        public GLState(GL11 gl) {
            mGL = gl;

            /*// Disable unused state*/
            gl.glDisable(GL11.GL_LIGHTING);

            /*// Enable used features*/
            gl.glEnable(GL11.GL_DITHER);
            gl.glEnable(GL11.GL_SCISSOR_TEST);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glEnable(GL11.GL_TEXTURE_2D);

            gl.glTexEnvf(GL11.GL_TEXTURE_ENV,
                    GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);

            /*// Set the background color*/
            gl.glClearColor(0f, 0f, 0f, 0f);
            gl.glClearStencil(0);

            gl.glEnable(GL11.GL_BLEND);
            gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

            /*// We use 565 or 8888 format, so set the alignment to 2 bytes/pixel.*/
            gl.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 2);
        }

        public void setTexEnvMode(int mode) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setTexEnvMode(int)",this,mode);try{if (mTexEnvMode == mode) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setTexEnvMode(int)",this);return;}}
            mTexEnvMode = mode;
            mGL.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, mode);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setTexEnvMode(int)",this,throwable);throw throwable;}
        }

        public void setLineWidth(float width) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setLineWidth(float)",this,width);try{if (mLineWidth == width) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setLineWidth(float)",this);return;}}
            mLineWidth = width;
            mGL.glLineWidth(width);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setLineWidth(float)",this,throwable);throw throwable;}
        }

        public void setLineSmooth(boolean enabled) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setLineSmooth(boolean)",this,enabled);try{if (mLineSmooth == enabled) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setLineSmooth(boolean)",this);return;}}
            mLineSmooth = enabled;
            if (enabled) {
                mGL.glEnable(GL11.GL_LINE_SMOOTH);
            } else {
                mGL.glDisable(GL11.GL_LINE_SMOOTH);
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setLineSmooth(boolean)",this,throwable);throw throwable;}
        }

        public void setTextureAlpha(float alpha) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setTextureAlpha(float)",this,alpha);try{if (mTextureAlpha == alpha) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setTextureAlpha(float)",this);return;}}
            mTextureAlpha = alpha;
            if (alpha >= OPAQUE_ALPHA) {
                /*// The alpha is need for those texture without alpha channel*/
                mGL.glColor4f(1, 1, 1, 1);
                setTexEnvMode(GL11.GL_REPLACE);
            } else {
                mGL.glColor4f(alpha, alpha, alpha, alpha);
                setTexEnvMode(GL11.GL_MODULATE);
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setTextureAlpha(float)",this,throwable);throw throwable;}
        }

        public void setColorMode(int color, float alpha) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setColorMode(int,float)",this,color,alpha);try{setBlendEnabled(!Utils.isOpaque(color) || alpha < OPAQUE_ALPHA);

            /*// Set mTextureAlpha to an invalid value, so that it will reset*/
            /*// again in setTextureAlpha(float) later.*/
            mTextureAlpha = -1.0f;

            setTexture2DEnabled(false);

            float prealpha = (color >>> 24) * alpha * 65535f / 255f / 255f;
            mGL.glColor4x(
                    Math.round(((color >> 16) & 0xFF) * prealpha),
                    Math.round(((color >> 8) & 0xFF) * prealpha),
                    Math.round((color & 0xFF) * prealpha),
                    Math.round(255 * prealpha));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setColorMode(int,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setColorMode(int,float)",this,throwable);throw throwable;}
        }

        public void setTexture2DEnabled(boolean enabled) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setTexture2DEnabled(boolean)",this,enabled);try{if (mTexture2DEnabled == enabled) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setTexture2DEnabled(boolean)",this);return;}}
            mTexture2DEnabled = enabled;
            if (enabled) {
                mGL.glEnable(GL11.GL_TEXTURE_2D);
            } else {
                mGL.glDisable(GL11.GL_TEXTURE_2D);
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setTexture2DEnabled(boolean)",this,throwable);throw throwable;}
        }

        public void setBlendEnabled(boolean enabled) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setBlendEnabled(boolean)",this,enabled);try{if (mBlendEnabled == enabled) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setBlendEnabled(boolean)",this);return;}}
            mBlendEnabled = enabled;
            if (enabled) {
                mGL.glEnable(GL11.GL_BLEND);
            } else {
                mGL.glDisable(GL11.GL_BLEND);
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl$GLState.setBlendEnabled(boolean)",this,throwable);throw throwable;}
        }
    }

    public GL11 getGLInstance() {
        com.mijack.Xlog.logMethodEnter("javax.microedition.khronos.opengles.GL11 com.android.gallery3d.ui.GLCanvasImpl.getGLInstance()",this);try{com.mijack.Xlog.logMethodExit("javax.microedition.khronos.opengles.GL11 com.android.gallery3d.ui.GLCanvasImpl.getGLInstance()",this);return mGL;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("javax.microedition.khronos.opengles.GL11 com.android.gallery3d.ui.GLCanvasImpl.getGLInstance()",this,throwable);throw throwable;}
    }

    public void setCurrentAnimationTimeMillis(long time) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.setCurrentAnimationTimeMillis(long)",this,time);try{Utils.assertTrue(time >= 0);
        mAnimationTime = time;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.setCurrentAnimationTimeMillis(long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.setCurrentAnimationTimeMillis(long)",this,throwable);throw throwable;}
    }

    public void clearBuffer() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.clearBuffer()",this);try{mGL.glClear(GL10.GL_COLOR_BUFFER_BIT);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.clearBuffer()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.clearBuffer()",this,throwable);throw throwable;}
    }

    private void setTextureCoords(RectF source) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.setTextureCoords(android.graphics.RectF)",this,source);try{setTextureCoords(source.left, source.top, source.right, source.bottom);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.setTextureCoords(android.graphics.RectF)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.setTextureCoords(android.graphics.RectF)",this,throwable);throw throwable;}
    }

    private void setTextureCoords(float left, float top,
            float right, float bottom) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.setTextureCoords(float,float,float,float)",this,left,top,right,bottom);try{mGL.glMatrixMode(GL11.GL_TEXTURE);
        mTextureMatrixValues[0] = right - left;
        mTextureMatrixValues[5] = bottom - top;
        mTextureMatrixValues[10] = 1;
        mTextureMatrixValues[12] = left;
        mTextureMatrixValues[13] = top;
        mTextureMatrixValues[15] = 1;
        mGL.glLoadMatrixf(mTextureMatrixValues, 0);
        mGL.glMatrixMode(GL11.GL_MODELVIEW);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.setTextureCoords(float,float,float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.setTextureCoords(float,float,float,float)",this,throwable);throw throwable;}
    }

    /*// unloadTexture and deleteBuffer can be called from the finalizer thread,*/
    /*// so we synchronized on the mUnboundTextures object.*/
    public boolean unloadTexture(BasicTexture t) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.GLCanvasImpl.unloadTexture(com.android.gallery3d.ui.BasicTexture)",this,t);try{synchronized (mUnboundTextures) {
            if (!t.isLoaded(this)) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLCanvasImpl.unloadTexture(com.android.gallery3d.ui.BasicTexture)",this);return false;}}
            mUnboundTextures.add(t.mId);
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.GLCanvasImpl.unloadTexture(com.android.gallery3d.ui.BasicTexture)",this);return true;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.GLCanvasImpl.unloadTexture(com.android.gallery3d.ui.BasicTexture)",this,throwable);throw throwable;}
    }

    public void deleteBuffer(int bufferId) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.deleteBuffer(int)",this,bufferId);try{synchronized (mUnboundTextures) {
            mDeleteBuffers.add(bufferId);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.deleteBuffer(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.deleteBuffer(int)",this,throwable);throw throwable;}
    }

    public void deleteRecycledResources() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.deleteRecycledResources()",this);try{synchronized (mUnboundTextures) {
            IntArray ids = mUnboundTextures;
            if (ids.size() > 0) {
                mGL.glDeleteTextures(ids.size(), ids.getInternalArray(), 0);
                ids.clear();
            }

            ids = mDeleteBuffers;
            if (ids.size() > 0) {
                mGL.glDeleteBuffers(ids.size(), ids.getInternalArray(), 0);
                ids.clear();
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.deleteRecycledResources()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.deleteRecycledResources()",this,throwable);throw throwable;}
    }

    public int save() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.GLCanvasImpl.save()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.GLCanvasImpl.save()",this);return save(SAVE_FLAG_ALL);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.GLCanvasImpl.save()",this,throwable);throw throwable;}
    }

    public int save(int saveFlags) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.GLCanvasImpl.save(int)",this,saveFlags);try{ConfigState config = obtainRestoreConfig();

        if ((saveFlags & SAVE_FLAG_ALPHA) != 0) {
            config.mAlpha = mAlpha;
        } else {
            config.mAlpha = -1;
        }

        if ((saveFlags & SAVE_FLAG_CLIP) != 0) {
            config.mRect.set(mClipRect);
        } else {
            config.mRect.left = Integer.MAX_VALUE;
        }

        if ((saveFlags & SAVE_FLAG_MATRIX) != 0) {
            System.arraycopy(mMatrixValues, 0, config.mMatrix, 0, 16);
        } else {
            config.mMatrix[0] = Float.NEGATIVE_INFINITY;
        }

        mRestoreStack.push(config);
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.GLCanvasImpl.save(int)",this);return mRestoreStack.size() - 1;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.GLCanvasImpl.save(int)",this,throwable);throw throwable;}
    }

    public void restore() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.restore()",this);try{if (mRestoreStack.isEmpty()) {throw new IllegalStateException();}
        ConfigState config = mRestoreStack.pop();
        config.restore(this);
        freeRestoreConfig(config);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.restore()",this,throwable);throw throwable;}
    }

    private void freeRestoreConfig(ConfigState action) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.freeRestoreConfig(com.android.gallery3d.ui.GLCanvasImpl$ConfigState)",this,action);try{action.mNextFree = mRecycledRestoreAction;
        mRecycledRestoreAction = action;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.freeRestoreConfig(com.android.gallery3d.ui.GLCanvasImpl$ConfigState)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.freeRestoreConfig(com.android.gallery3d.ui.GLCanvasImpl$ConfigState)",this,throwable);throw throwable;}
    }

    private ConfigState obtainRestoreConfig() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.GLCanvasImpl$ConfigState com.android.gallery3d.ui.GLCanvasImpl.obtainRestoreConfig()",this);try{if (mRecycledRestoreAction != null) {
            ConfigState result = mRecycledRestoreAction;
            mRecycledRestoreAction = result.mNextFree;
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.GLCanvasImpl$ConfigState com.android.gallery3d.ui.GLCanvasImpl.obtainRestoreConfig()",this);return result;}
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.GLCanvasImpl$ConfigState com.android.gallery3d.ui.GLCanvasImpl.obtainRestoreConfig()",this);return new ConfigState();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.GLCanvasImpl$ConfigState com.android.gallery3d.ui.GLCanvasImpl.obtainRestoreConfig()",this,throwable);throw throwable;}
    }

    private static class ConfigState {
        float mAlpha;
        Rect mRect = new Rect();
        float mMatrix[] = new float[16];
        ConfigState mNextFree;

        public void restore(GLCanvasImpl canvas) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl$ConfigState.restore(com.android.gallery3d.ui.GLCanvasImpl)",this,canvas);try{if (mAlpha >= 0) {canvas.setAlpha(mAlpha);}
            if (mRect.left != Integer.MAX_VALUE) {
                Rect rect = mRect;
                canvas.mClipRect.set(rect);
                canvas.mGL.glScissor(
                        rect.left, rect.top, rect.width(), rect.height());
            }
            if (mMatrix[0] != Float.NEGATIVE_INFINITY) {
                System.arraycopy(mMatrix, 0, canvas.mMatrixValues, 0, 16);
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl$ConfigState.restore(com.android.gallery3d.ui.GLCanvasImpl)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl$ConfigState.restore(com.android.gallery3d.ui.GLCanvasImpl)",this,throwable);throw throwable;}
        }
    }

    public void dumpStatisticsAndClear() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.dumpStatisticsAndClear()",this);try{String line = String.format(
                "MESH:%d, TEX_OES:%d, TEX_RECT:%d, FILL_RECT:%d, LINE:%d",
                mCountDrawMesh, mCountTextureRect, mCountTextureOES,
                mCountFillRect, mCountDrawLine);
        mCountDrawMesh = 0;
        mCountTextureRect = 0;
        mCountTextureOES = 0;
        mCountFillRect = 0;
        mCountDrawLine = 0;
        Log.d(TAG, line);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.dumpStatisticsAndClear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.dumpStatisticsAndClear()",this,throwable);throw throwable;}
    }

    private void saveTransform() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.saveTransform()",this);try{System.arraycopy(mMatrixValues, 0, mTempMatrix, 0, 16);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.saveTransform()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.saveTransform()",this,throwable);throw throwable;}
    }

    private void restoreTransform() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GLCanvasImpl.restoreTransform()",this);try{System.arraycopy(mTempMatrix, 0, mMatrixValues, 0, 16);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GLCanvasImpl.restoreTransform()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GLCanvasImpl.restoreTransform()",this,throwable);throw throwable;}
    }
}
