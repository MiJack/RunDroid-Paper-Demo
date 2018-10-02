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
import android.graphics.Color;
import android.text.Layout;

public class GridDrawer extends IconDrawer {
    private Texture mImportLabel;
    private int mGridWidth;
    private final SelectionManager mSelectionManager;
    private final Context mContext;
    private final int IMPORT_FONT_SIZE = 14;
    private final int IMPORT_FONT_COLOR = Color.WHITE;
    private final int IMPORT_LABEL_MARGIN = 10;
    private boolean mSelectionMode;

    public GridDrawer(Context context, SelectionManager selectionManager) {
        super(context);
        mContext = context;
        mSelectionManager = selectionManager;
    }

    @Override
    public void prepareDrawing() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GridDrawer.prepareDrawing()",this);try{mSelectionMode = mSelectionManager.inSelectionMode();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GridDrawer.prepareDrawing()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GridDrawer.prepareDrawing()",this,throwable);throw throwable;}
    }

    @Override
    public void draw(GLCanvas canvas, Texture content, int width,
            int height, int rotation, Path path,
            int dataSourceType, int mediaType, boolean isPanorama,
            int labelBackgroundHeight, boolean wantCache, boolean isCaching) {

        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GridDrawer.draw(GLCanvas,Texture,int,int,int,com.android.gallery3d.data.Path,int,int,boolean,int,boolean,boolean)",this,canvas,content,width,height,rotation,path,dataSourceType,mediaType,isPanorama,labelBackgroundHeight,wantCache,isCaching);try{int x = -width / 2;
        int y = -height / 2;

        drawWithRotation(canvas, content, x, y, width, height, rotation);

        if (((rotation / 90) & 0x01) == 1) {
            int temp = width;
            width = height;
            height = temp;
            x = -width / 2;
            y = -height / 2;
        }

        drawMediaTypeOverlay(canvas, mediaType, isPanorama, x, y, width, height);
        drawLabelBackground(canvas, width, height, labelBackgroundHeight);
        drawIcon(canvas, width, height, dataSourceType);
        if (dataSourceType == DATASOURCE_TYPE_MTP) {
            drawImportLabel(canvas, width, height);
        }

        if (mSelectionManager.isPressedPath(path)) {
            drawPressedFrame(canvas, x, y, width, height);
        } else if (mSelectionMode && mSelectionManager.isItemSelected(path)) {
            drawSelectedFrame(canvas, x, y, width, height);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GridDrawer.draw(GLCanvas,Texture,int,int,int,com.android.gallery3d.data.Path,int,int,boolean,int,boolean,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GridDrawer.draw(GLCanvas,Texture,int,int,int,com.android.gallery3d.data.Path,int,int,boolean,int,boolean,boolean)",this,throwable);throw throwable;}
    }

    /*// Draws the "click to import" label at the center of the frame*/
    private void drawImportLabel(GLCanvas canvas, int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GridDrawer.drawImportLabel(GLCanvas,int,int)",this,canvas,width,height);try{if (mImportLabel == null || mGridWidth != width) {
            mGridWidth = width;
            mImportLabel = MultiLineTexture.newInstance(
                    mContext.getString(R.string.click_import),
                    width - 2 * IMPORT_LABEL_MARGIN,
                    IMPORT_FONT_SIZE, IMPORT_FONT_COLOR,
                    Layout.Alignment.ALIGN_CENTER);
        }
        int w = mImportLabel.getWidth();
        int h = mImportLabel.getHeight();
        mImportLabel.draw(canvas, -w / 2, -h / 2);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GridDrawer.drawImportLabel(GLCanvas,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.GridDrawer.drawImportLabel(GLCanvas,int,int)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.GridDrawer.drawFocus(GLCanvas,int,int)",this,canvas,width,height);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.GridDrawer.drawFocus(GLCanvas,int,int)",this);}
}
