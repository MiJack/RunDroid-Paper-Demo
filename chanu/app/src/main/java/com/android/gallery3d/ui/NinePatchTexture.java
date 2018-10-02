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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.microedition.khronos.opengles.GL11;

/*// NinePatchTexture is a texture backed by a NinePatch resource.*/
/*//*/
/*// getPaddings() returns paddings specified in the NinePatch.*/
/*// getNinePatchChunk() returns the layout data specified in the NinePatch.*/
/*//*/
public class NinePatchTexture extends ResourceTexture {
    @SuppressWarnings("unused")
    private static final String TAG = "NinePatchTexture";
    private NinePatchChunk mChunk;
    private MyCacheMap<Long, NinePatchInstance> mInstanceCache =
            new MyCacheMap<Long, NinePatchInstance>();

    public NinePatchTexture(Context context, int resId) {
        super(context, resId);
    }

    @Override
    protected Bitmap onGetBitmap() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.NinePatchTexture.onGetBitmap()",this);try{if (mBitmap != null) {{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.NinePatchTexture.onGetBitmap()",this);return mBitmap;}}

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(
                mContext.getResources(), mResId, options);
        mBitmap = bitmap;
        setSize(bitmap.getWidth(), bitmap.getHeight());
        byte[] chunkData = bitmap.getNinePatchChunk();
        mChunk = chunkData == null
                ? null
                : NinePatchChunk.deserialize(bitmap.getNinePatchChunk());
        if (mChunk == null) {
            Log.e(TAG, "Invalid nine-patch image: " + mResId);
            throw new RuntimeException("invalid nine-patch image: " + mResId);
        }
        {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.NinePatchTexture.onGetBitmap()",this);return bitmap;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.NinePatchTexture.onGetBitmap()",this,throwable);throw throwable;}
    }

    @Override
    protected BitmapFactory.Options onGetBitmapBounds() {
        com.mijack.Xlog.logMethodEnter("BitmapFactory.Options com.android.gallery3d.ui.NinePatchTexture.onGetBitmapBounds()",this);try{BitmapFactory.Options options = new BitmapFactory.Options();
        if (mBitmap != null) {
            options.outWidth = mBitmap.getWidth();
            options.outHeight = mBitmap.getHeight();
        }
        else {
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(
                mContext.getResources(), mResId, options);
            setSize(options.outWidth, options.outHeight);
        }
        {com.mijack.Xlog.logMethodExit("BitmapFactory.Options com.android.gallery3d.ui.NinePatchTexture.onGetBitmapBounds()",this);return options;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("BitmapFactory.Options com.android.gallery3d.ui.NinePatchTexture.onGetBitmapBounds()",this,throwable);throw throwable;}
    }

    public Rect getPaddings() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Rect com.android.gallery3d.ui.NinePatchTexture.getPaddings()",this);try{/*// get the paddings from nine patch*/
        if (mChunk == null) {onGetBitmap();}
        {com.mijack.Xlog.logMethodExit("android.graphics.Rect com.android.gallery3d.ui.NinePatchTexture.getPaddings()",this);return mChunk.mPaddings;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Rect com.android.gallery3d.ui.NinePatchTexture.getPaddings()",this,throwable);throw throwable;}
    }

    public NinePatchChunk getNinePatchChunk() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.NinePatchChunk com.android.gallery3d.ui.NinePatchTexture.getNinePatchChunk()",this);try{if (mChunk == null) {onGetBitmap();}
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.NinePatchChunk com.android.gallery3d.ui.NinePatchTexture.getNinePatchChunk()",this);return mChunk;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.NinePatchChunk com.android.gallery3d.ui.NinePatchTexture.getNinePatchChunk()",this,throwable);throw throwable;}
    }

    private static class MyCacheMap<K, V> extends LinkedHashMap<K, V> {
        private int CACHE_SIZE = 16;
        private V mJustRemoved;

        public MyCacheMap() {
            super(4, 0.75f, true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.NinePatchTexture$MyCacheMap<K, V>.removeEldestEntry(Map.Entry)",this,eldest);try{if (size() > CACHE_SIZE) {
                mJustRemoved = eldest.getValue();
                {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.NinePatchTexture$MyCacheMap<K, V>.removeEldestEntry(Map.Entry)",this);return true;}
            }
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.NinePatchTexture$MyCacheMap<K, V>.removeEldestEntry(Map.Entry)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.NinePatchTexture$MyCacheMap<K, V>.removeEldestEntry(Map.Entry)",this,throwable);throw throwable;}
        }

        public V getJustRemoved() {
            com.mijack.Xlog.logMethodEnter("V com.android.gallery3d.ui.NinePatchTexture$MyCacheMap<K, V>.getJustRemoved()",this);try{V result = mJustRemoved;
            mJustRemoved = null;
            {com.mijack.Xlog.logMethodExit("V com.android.gallery3d.ui.NinePatchTexture$MyCacheMap<K, V>.getJustRemoved()",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("V com.android.gallery3d.ui.NinePatchTexture$MyCacheMap<K, V>.getJustRemoved()",this,throwable);throw throwable;}
        }
    }

    private NinePatchInstance findInstance(GLCanvas canvas, int w, int h) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.NinePatchInstance com.android.gallery3d.ui.NinePatchTexture.findInstance(GLCanvas,int,int)",this,canvas,w,h);try{long key = w;
        key = (key << 32) | h;
        NinePatchInstance instance = mInstanceCache.get(key);

        if (instance == null) {
            instance = new NinePatchInstance(this, w, h);
            mInstanceCache.put(key, instance);
            NinePatchInstance removed = mInstanceCache.getJustRemoved();
            if (removed != null) {
                removed.recycle(canvas);
            }
        }

        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.NinePatchInstance com.android.gallery3d.ui.NinePatchTexture.findInstance(GLCanvas,int,int)",this);return instance;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.NinePatchInstance com.android.gallery3d.ui.NinePatchTexture.findInstance(GLCanvas,int,int)",this,throwable);throw throwable;}
    }

    @Override
    public void draw(GLCanvas canvas, int x, int y, int w, int h) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.NinePatchTexture.draw(GLCanvas,int,int,int,int)",this,canvas,x,y,w,h);try{if (!isLoaded(canvas)) {
            mInstanceCache.clear();
        }

        if (w != 0 && h != 0) {
            findInstance(canvas, w, h).draw(canvas, this, x, y);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.NinePatchTexture.draw(GLCanvas,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.NinePatchTexture.draw(GLCanvas,int,int,int,int)",this,throwable);throw throwable;}
    }

    @Override
    public void recycle() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.NinePatchTexture.recycle()",this);try{super.recycle();
        GLCanvas canvas = mCanvasRef == null ? null : mCanvasRef.get();
        if (canvas == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.NinePatchTexture.recycle()",this);return;}}
        for (NinePatchInstance instance : mInstanceCache.values()) {
            instance.recycle(canvas);
        }
        mInstanceCache.clear();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.NinePatchTexture.recycle()",this,throwable);throw throwable;}
    }
}

/*// This keeps data for a specialization of NinePatchTexture with the size*/
/*// (width, height). We pre-compute the coordinates for efficiency.*/
class NinePatchInstance {

    @SuppressWarnings("unused")
    private static final String TAG = "NinePatchInstance";

    /*// We need 16 vertices for a normal nine-patch image (the 4x4 vertices)*/
    private static final int VERTEX_BUFFER_SIZE = 16 * 2;

    /*// We need 22 indices for a normal nine-patch image, plus 2 for each*/
    /*// transparent region. Current there are at most 1 transparent region.*/
    private static final int INDEX_BUFFER_SIZE = 22 + 2;

    private FloatBuffer mXyBuffer;
    private FloatBuffer mUvBuffer;
    private ByteBuffer mIndexBuffer;

    /*// Names for buffer names: xy, uv, index.*/
    private int[] mBufferNames;

    private int mIdxCount;

    public NinePatchInstance(NinePatchTexture tex, int width, int height) {
        NinePatchChunk chunk = tex.getNinePatchChunk();

        if (width <= 0 || height <= 0) {
            throw new RuntimeException("invalid dimension");
        }

        /*// The code should be easily extended to handle the general cases by*/
        /*// allocating more space for buffers. But let's just handle the only*/
        /*// use case.*/
        if (chunk.mDivX.length != 2 || chunk.mDivY.length != 2) {
            throw new RuntimeException("unsupported nine patch");
        }

        float divX[] = new float[4];
        float divY[] = new float[4];
        float divU[] = new float[4];
        float divV[] = new float[4];

        int nx = stretch(divX, divU, chunk.mDivX, tex.getWidth(), width);
        int ny = stretch(divY, divV, chunk.mDivY, tex.getHeight(), height);

        prepareVertexData(divX, divY, divU, divV, nx, ny, chunk.mColor);
    }

    /**
     * Stretches the texture according to the nine-patch rules. It will
     * linearly distribute the strechy parts defined in the nine-patch chunk to
     * the target area.
     *
     * <pre>
     *                      source
     *          /--------------^---------------\
     *         u0    u1       u2  u3     u4   u5
     * div ---> |fffff|ssssssss|fff|ssssss|ffff| ---> u
     *          |    div0    div1 div2   div3  |
     *          |     |       /   /      /    /
     *          |     |      /   /     /    /
     *          |     |     /   /    /    /
     *          |fffff|ssss|fff|sss|ffff| ---> x
     *         x0    x1   x2  x3  x4   x5
     *          \----------v------------/
     *                  target
     *
     * f: fixed segment
     * s: stretchy segment
     * </pre>
     *
     * @param div the stretch parts defined in nine-patch chunk
     * @param source the length of the texture
     * @param target the length on the drawing plan
     * @param u output, the positions of these dividers in the texture
     *        coordinate
     * @param x output, the corresponding position of these dividers on the
     *        drawing plan
     * @return the number of these dividers.
     */
    private static int stretch(
            float x[], float u[], int div[], int source, int target) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.ui.NinePatchInstance.stretch(float,float,int,int,int)",x[],u[],div[],source,target);try{int textureSize = Utils.nextPowerOf2(source);
        float textureBound = (float) source / textureSize;

        float stretch = 0;
        for (int i = 0, n = div.length; i < n; i += 2) {
            stretch += div[i + 1] - div[i];
        }

        float remaining = target - source + stretch;

        float lastX = 0;
        float lastU = 0;

        x[0] = 0;
        u[0] = 0;
        for (int i = 0, n = div.length; i < n; i += 2) {
            /*// Make the stretchy segment a little smaller to prevent sampling*/
            /*// on neighboring fixed segments.*/
            /*// fixed segment*/
            x[i + 1] = lastX + (div[i] - lastU) + 0.5f;
            u[i + 1] = Math.min((div[i] + 0.5f) / textureSize, textureBound);

            /*// stretchy segment*/
            float partU = div[i + 1] - div[i];
            float partX = remaining * partU / stretch;
            remaining -= partX;
            stretch -= partU;

            lastX = x[i + 1] + partX;
            lastU = div[i + 1];
            x[i + 2] = lastX - 0.5f;
            u[i + 2] = Math.min((lastU - 0.5f)/ textureSize, textureBound);
        }
        /*// the last fixed segment*/
        x[div.length + 1] = target;
        u[div.length + 1] = textureBound;

        /*// remove segments with length 0.*/
        int last = 0;
        for (int i = 1, n = div.length + 2; i < n; ++i) {
            if ((x[i] - x[last]) < 1f) {continue;}
            x[++last] = x[i];
            u[last] = u[i];
        }
        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.ui.NinePatchInstance.stretch(float,float,int,int,int)");return last + 1;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.ui.NinePatchInstance.stretch(float,float,int,int,int)",throwable);throw throwable;}
    }

    private void prepareVertexData(float x[], float y[], float u[], float v[],
            int nx, int ny, int[] color) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.NinePatchInstance.prepareVertexData(float,float,float,float,int,int,[int)",this,x[],y[],u[],v[],nx,ny,color);try{/*
         * Given a 3x3 nine-patch image, the vertex order is defined as the
         * following graph:
         *
         * (0) (1) (2) (3)
         *  |  /|  /|  /|
         *  | / | / | / |
         * (4) (5) (6) (7)
         *  | \ | \ | \ |
         *  |  \|  \|  \|
         * (8) (9) (A) (B)
         *  |  /|  /|  /|
         *  | / | / | / |
         * (C) (D) (E) (F)
         *
         * And we draw the triangle strip in the following index order:
         *
         * index: 04152637B6A5948C9DAEBF
         */
        int pntCount = 0;
        float xy[] = new float[VERTEX_BUFFER_SIZE];
        float uv[] = new float[VERTEX_BUFFER_SIZE];
        for (int j = 0; j < ny; ++j) {
            for (int i = 0; i < nx; ++i) {
                int xIndex = (pntCount++) << 1;
                int yIndex = xIndex + 1;
                xy[xIndex] = x[i];
                xy[yIndex] = y[j];
                uv[xIndex] = u[i];
                uv[yIndex] = v[j];
            }
        }

        int idxCount = 1;
        boolean isForward = false;
        byte index[] = new byte[INDEX_BUFFER_SIZE];
        for (int row = 0; row < ny - 1; row++) {
            --idxCount;
            isForward = !isForward;

            int start, end, inc;
            if (isForward) {
                start = 0;
                end = nx;
                inc = 1;
            } else {
                start = nx - 1;
                end = -1;
                inc = -1;
            }

            for (int col = start; col != end; col += inc) {
                int k = row * nx + col;
                if (col != start) {
                    int colorIdx = row * (nx - 1) + col;
                    if (isForward) {colorIdx--;}
                    if (color[colorIdx] == NinePatchChunk.TRANSPARENT_COLOR) {
                        index[idxCount] = index[idxCount - 1];
                        ++idxCount;
                        index[idxCount++] = (byte) k;
                    }
                }

                index[idxCount++] = (byte) k;
                index[idxCount++] = (byte) (k + nx);
            }
        }

        mIdxCount = idxCount;

        int size = (pntCount * 2) * (Float.SIZE / Byte.SIZE);
        mXyBuffer = allocateDirectNativeOrderBuffer(size).asFloatBuffer();
        mUvBuffer = allocateDirectNativeOrderBuffer(size).asFloatBuffer();
        mIndexBuffer = allocateDirectNativeOrderBuffer(mIdxCount);

        mXyBuffer.put(xy, 0, pntCount * 2).position(0);
        mUvBuffer.put(uv, 0, pntCount * 2).position(0);
        mIndexBuffer.put(index, 0, idxCount).position(0);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.NinePatchInstance.prepareVertexData(float,float,float,float,int,int,[int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.NinePatchInstance.prepareVertexData(float,float,float,float,int,int,[int)",this,throwable);throw throwable;}
    }

    private static ByteBuffer allocateDirectNativeOrderBuffer(int size) {
        com.mijack.Xlog.logStaticMethodEnter("java.nio.ByteBuffer com.android.gallery3d.ui.NinePatchInstance.allocateDirectNativeOrderBuffer(int)",size);try{com.mijack.Xlog.logStaticMethodExit("java.nio.ByteBuffer com.android.gallery3d.ui.NinePatchInstance.allocateDirectNativeOrderBuffer(int)");return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.nio.ByteBuffer com.android.gallery3d.ui.NinePatchInstance.allocateDirectNativeOrderBuffer(int)",throwable);throw throwable;}
    }

    private void prepareBuffers(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.NinePatchInstance.prepareBuffers(GLCanvas)",this,canvas);try{mBufferNames = new int[3];
        GL11 gl = canvas.getGLInstance();
        gl.glGenBuffers(3, mBufferNames, 0);

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, mBufferNames[0]);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER,
                mXyBuffer.capacity() * (Float.SIZE / Byte.SIZE),
                mXyBuffer, GL11.GL_STATIC_DRAW);

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, mBufferNames[1]);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER,
                mUvBuffer.capacity() * (Float.SIZE / Byte.SIZE),
                mUvBuffer, GL11.GL_STATIC_DRAW);

        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mBufferNames[2]);
        gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER,
                mIndexBuffer.capacity(),
                mIndexBuffer, GL11.GL_STATIC_DRAW);

        /*// These buffers are never used again.*/
        mXyBuffer = null;
        mUvBuffer = null;
        mIndexBuffer = null;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.NinePatchInstance.prepareBuffers(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.NinePatchInstance.prepareBuffers(GLCanvas)",this,throwable);throw throwable;}
    }

    public void draw(GLCanvas canvas, NinePatchTexture tex, int x, int y) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.NinePatchInstance.draw(GLCanvas,com.android.gallery3d.ui.NinePatchTexture,int,int)",this,canvas,tex,x,y);try{if (mBufferNames == null) {
            prepareBuffers(canvas);
        }
        canvas.drawMesh(tex, x, y, mBufferNames[0], mBufferNames[1],
                mBufferNames[2], mIdxCount);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.NinePatchInstance.draw(GLCanvas,com.android.gallery3d.ui.NinePatchTexture,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.NinePatchInstance.draw(GLCanvas,com.android.gallery3d.ui.NinePatchTexture,int,int)",this,throwable);throw throwable;}
    }

    public void recycle(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.NinePatchInstance.recycle(GLCanvas)",this,canvas);try{if (mBufferNames != null) {
            canvas.deleteBuffer(mBufferNames[0]);
            canvas.deleteBuffer(mBufferNames[1]);
            canvas.deleteBuffer(mBufferNames[2]);
            mBufferNames = null;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.NinePatchInstance.recycle(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.NinePatchInstance.recycle(GLCanvas)",this,throwable);throw throwable;}
    }
}
