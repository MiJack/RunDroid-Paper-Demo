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
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Renders and displays photo in the surface view.
 */
public class PhotoView extends GLSurfaceView {

    private final PhotoRenderer renderer;

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        renderer = new PhotoRenderer();
        setEGLContextClientVersion(2);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public RectF getPhotoBounds() {
        com.mijack.Xlog.logMethodEnter("android.graphics.RectF com.android.gallery3d.photoeditor.PhotoView.getPhotoBounds()",this);try{RectF photoBounds;
        synchronized (renderer.photoBounds) {
            photoBounds = new RectF(renderer.photoBounds);
        }
        {com.mijack.Xlog.logMethodExit("android.graphics.RectF com.android.gallery3d.photoeditor.PhotoView.getPhotoBounds()",this);return photoBounds;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.RectF com.android.gallery3d.photoeditor.PhotoView.getPhotoBounds()",this,throwable);throw throwable;}
    }

    /**
     * Queues a runnable and renders a frame after execution. Queued runnables could be later
     * removed by remove() or flush().
     */
    public void queue(Runnable r) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView.queue(java.lang.Runnable)",this,r);try{renderer.queue.add(r);
        requestRender();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView.queue(java.lang.Runnable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView.queue(java.lang.Runnable)",this,throwable);throw throwable;}
    }

    /**
     * Removes the specified queued runnable.
     */
    public void remove(Runnable runnable) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView.remove(java.lang.Runnable)",this,runnable);try{renderer.queue.remove(runnable);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView.remove(java.lang.Runnable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView.remove(java.lang.Runnable)",this,throwable);throw throwable;}
    }

    /**
     * Flushes all queued runnables to cancel their execution.
     */
    public void flush() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView.flush()",this);try{renderer.queue.clear();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView.flush()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView.flush()",this,throwable);throw throwable;}
    }

    /**
     * Sets photo for display; this method must be queued for GL thread.
     */
    public void setPhoto(Photo photo, boolean clearTransform) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView.setPhoto(com.android.gallery3d.photoeditor.Photo,boolean)",this,photo,clearTransform);try{renderer.setPhoto(photo, clearTransform);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView.setPhoto(com.android.gallery3d.photoeditor.Photo,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView.setPhoto(com.android.gallery3d.photoeditor.Photo,boolean)",this,throwable);throw throwable;}
    }

    /**
     * Rotates displayed photo; this method must be queued for GL thread.
     */
    public void rotatePhoto(float degrees) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView.rotatePhoto(float)",this,degrees);try{renderer.rotatePhoto(degrees);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView.rotatePhoto(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView.rotatePhoto(float)",this,throwable);throw throwable;}
    }

    /**
     * Flips displayed photo; this method must be queued for GL thread.
     */
    public void flipPhoto(float horizontalDegrees, float verticalDegrees) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView.flipPhoto(float,float)",this,horizontalDegrees,verticalDegrees);try{renderer.flipPhoto(horizontalDegrees, verticalDegrees);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView.flipPhoto(float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView.flipPhoto(float,float)",this,throwable);throw throwable;}
    }

    /**
     * Renderer that renders the GL surface-view and only be called from the GL thread.
     */
    private class PhotoRenderer implements GLSurfaceView.Renderer {

        final Vector<Runnable> queue = new Vector<Runnable>();
        final RectF photoBounds = new RectF();
        RendererUtils.RenderContext renderContext;
        Photo photo;
        int viewWidth;
        int viewHeight;
        float rotatedDegrees;
        float flippedHorizontalDegrees;
        float flippedVerticalDegrees;

        void setPhoto(Photo photo, boolean clearTransform) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.setPhoto(com.android.gallery3d.photoeditor.Photo,boolean)",this,photo,clearTransform);try{int width = (photo != null) ? photo.width() : 0;
            int height = (photo != null) ? photo.height() : 0;
            boolean changed;
            synchronized (photoBounds) {
                changed = (photoBounds.width() != width) || (photoBounds.height() != height);
                if (changed) {
                    photoBounds.set(0, 0, width, height);
                }
            }
            this.photo = photo;
            updateSurface(clearTransform, changed);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.setPhoto(com.android.gallery3d.photoeditor.Photo,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.setPhoto(com.android.gallery3d.photoeditor.Photo,boolean)",this,throwable);throw throwable;}
        }

        void updateSurface(boolean clearTransform, boolean sizeChanged) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.updateSurface(boolean,boolean)",this,clearTransform,sizeChanged);try{boolean flipped = (flippedHorizontalDegrees != 0) || (flippedVerticalDegrees != 0);
            boolean transformed = (rotatedDegrees != 0) || flipped;
            if ((clearTransform && transformed) || (sizeChanged && !transformed)) {
                /*// Fit photo when clearing existing transforms or changing surface/photo sizes.*/
                if (photo != null) {
                    RendererUtils.setRenderToFit(renderContext, photo.width(), photo.height(),
                            viewWidth, viewHeight);
                    rotatedDegrees = 0;
                    flippedHorizontalDegrees = 0;
                    flippedVerticalDegrees = 0;
                }
            } else {
                /*// Restore existing transformations for orientation changes or awaking from sleep.*/
                if (rotatedDegrees != 0) {
                    rotatePhoto(rotatedDegrees);
                } else if (flipped) {
                    flipPhoto(flippedHorizontalDegrees, flippedVerticalDegrees);
                }
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.updateSurface(boolean,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.updateSurface(boolean,boolean)",this,throwable);throw throwable;}
        }

        void rotatePhoto(float degrees) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.rotatePhoto(float)",this,degrees);try{if (photo != null) {
                RendererUtils.setRenderToRotate(renderContext, photo.width(), photo.height(),
                        viewWidth, viewHeight, degrees);
                rotatedDegrees = degrees;
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.rotatePhoto(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.rotatePhoto(float)",this,throwable);throw throwable;}
        }

        void flipPhoto(float horizontalDegrees, float verticalDegrees) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.flipPhoto(float,float)",this,horizontalDegrees,verticalDegrees);try{if (photo != null) {
                RendererUtils.setRenderToFlip(renderContext, photo.width(), photo.height(),
                        viewWidth, viewHeight, horizontalDegrees, verticalDegrees);
                flippedHorizontalDegrees = horizontalDegrees;
                flippedVerticalDegrees = verticalDegrees;
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.flipPhoto(float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.flipPhoto(float,float)",this,throwable);throw throwable;}
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.onDrawFrame(javax.microedition.khronos.opengles.GL10)",this,gl);try{Runnable r = null;
            synchronized (queue) {
                if (!queue.isEmpty()) {
                    r = queue.remove(0);
                }
            }
            if (r != null) {
                r.run();
            }
            if (!queue.isEmpty()) {
                requestRender();
            }
            RendererUtils.renderBackground();
            if (photo != null) {
                RendererUtils.renderTexture(renderContext, photo.texture(), viewWidth, viewHeight);
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.onDrawFrame(javax.microedition.khronos.opengles.GL10)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.onDrawFrame(javax.microedition.khronos.opengles.GL10)",this,throwable);throw throwable;}
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.onSurfaceChanged(javax.microedition.khronos.opengles.GL10,int,int)",this,gl,width,height);try{viewWidth = width;
            viewHeight = height;
            updateSurface(false, true);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.onSurfaceChanged(javax.microedition.khronos.opengles.GL10,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.onSurfaceChanged(javax.microedition.khronos.opengles.GL10,int,int)",this,throwable);throw throwable;}
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.onSurfaceCreated(javax.microedition.khronos.opengles.GL10,javax.microedition.khronos.egl.EGLConfig)",this,gl,config);try{renderContext = RendererUtils.createProgram();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.onSurfaceCreated(javax.microedition.khronos.opengles.GL10,javax.microedition.khronos.egl.EGLConfig)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoView$PhotoRenderer.onSurfaceCreated(javax.microedition.khronos.opengles.GL10,javax.microedition.khronos.egl.EGLConfig)",this,throwable);throw throwable;}
        }
    }
}
