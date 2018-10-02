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

import javax.microedition.khronos.opengles.GL11;

/*// RawTexture is used for texture created by glCopyTexImage2D.*/
/*//*/
/*// It will throw RuntimeException in onBind() if used with a different GL*/
/*// context. It is only used internally by copyTexture() in GLCanvas.*/
class RawTexture extends BasicTexture {

    private RawTexture(GLCanvas canvas, int id) {
        super(canvas, id, STATE_LOADED);
    }

    public static RawTexture newInstance(GLCanvas canvas) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.ui.RawTexture com.android.gallery3d.ui.RawTexture.newInstance(GLCanvas)",canvas);try{int[] textureId = new int[1];
        GL11 gl = canvas.getGLInstance();
        gl.glGenTextures(1, textureId, 0);
        {com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.ui.RawTexture com.android.gallery3d.ui.RawTexture.newInstance(GLCanvas)");return new RawTexture(canvas, textureId[0]);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.ui.RawTexture com.android.gallery3d.ui.RawTexture.newInstance(GLCanvas)",throwable);throw throwable;}
    }

    @Override
    protected boolean onBind(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.RawTexture.onBind(GLCanvas)",this,canvas);try{if (mCanvasRef.get() != canvas) {
            throw new RuntimeException("cannot bind to different canvas");
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.RawTexture.onBind(GLCanvas)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.RawTexture.onBind(GLCanvas)",this,throwable);throw throwable;}
    }

    public boolean isOpaque() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.RawTexture.isOpaque()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.RawTexture.isOpaque()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.RawTexture.isOpaque()",this,throwable);throw throwable;}
    }

    @Override
    public void yield() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.RawTexture.yield()",this);try{/*// we cannot free the texture because we have no backup.*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.RawTexture.yield()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.RawTexture.yield()",this,throwable);throw throwable;}
    }
}
