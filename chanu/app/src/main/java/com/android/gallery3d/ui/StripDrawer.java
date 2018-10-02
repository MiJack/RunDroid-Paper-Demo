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

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.data.Path;

import android.content.Context;
import android.graphics.Rect;

public class StripDrawer extends SelectionDrawer {
    private NinePatchTexture mFramePressed;
    private NinePatchTexture mFocusBox;
    private Rect mFocusBoxPadding;
    private Path mPressedPath;

    public StripDrawer(Context context) {
        mFramePressed = new NinePatchTexture(context, R.drawable.grid_pressed);
        mFocusBox = new NinePatchTexture(context, R.drawable.thumb_selected);
        mFocusBoxPadding = mFocusBox.getPaddings();
    }

    public void setPressedPath(Path path) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.StripDrawer.setPressedPath(com.android.gallery3d.data.Path)",this,path);try{mPressedPath = path;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.StripDrawer.setPressedPath(com.android.gallery3d.data.Path)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.StripDrawer.setPressedPath(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    private boolean isPressedPath(Path path) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.StripDrawer.isPressedPath(com.android.gallery3d.data.Path)",this,path);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.StripDrawer.isPressedPath(com.android.gallery3d.data.Path)",this);return path != null && path == mPressedPath;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.StripDrawer.isPressedPath(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.StripDrawer.prepareDrawing()",this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.StripDrawer.prepareDrawing()",this);}

    @Override
    public void draw(GLCanvas canvas, Texture content,
            int width, int height, int rotation, Path path,
            int dataSourceType, int mediaType, boolean isPanorama,
            int labelBackgroundHeight, boolean wantCache, boolean isCaching) {

        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.StripDrawer.draw(GLCanvas,Texture,int,int,int,com.android.gallery3d.data.Path,int,int,boolean,int,boolean,boolean)",this,canvas,content,width,height,rotation,path,dataSourceType,mediaType,isPanorama,labelBackgroundHeight,wantCache,isCaching);try{int x = -width / 2;
        int y = -height / 2;

        drawWithRotation(canvas, content, x, y, width, height, rotation);

        if (isPressedPath(path)) {
            drawFrame(canvas, mFramePressed, x, y, width, height);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.StripDrawer.draw(GLCanvas,Texture,int,int,int,com.android.gallery3d.data.Path,int,int,boolean,int,boolean,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.StripDrawer.draw(GLCanvas,Texture,int,int,int,com.android.gallery3d.data.Path,int,int,boolean,int,boolean,boolean)",this,throwable);throw throwable;}
    }

    @Override
    public void drawFocus(GLCanvas canvas, int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.StripDrawer.drawFocus(GLCanvas,int,int)",this,canvas,width,height);try{int x = -width / 2;
        int y = -height / 2;
        Rect p = mFocusBoxPadding;
        mFocusBox.draw(canvas, x - p.left, y - p.top,
                width + p.left + p.right, height + p.top + p.bottom);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.StripDrawer.drawFocus(GLCanvas,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.StripDrawer.drawFocus(GLCanvas,int,int)",this,throwable);throw throwable;}
    }
}
