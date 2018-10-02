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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;

import com.android.gallery3d.app.GalleryContext;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DecodeUtils;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.JobContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class TileImageView extends GLView {
    public static final int SIZE_UNKNOWN = -1;

    @SuppressWarnings("unused")
    private static final String TAG = "TileImageView";

    /*// TILE_SIZE must be 2^N - 2. We put one pixel border in each side of the*/
    /*// texture to avoid seams between tiles.*/
    private static final int TILE_SIZE = 254;
    private static final int TILE_BORDER = 1;
    private static final int UPLOAD_LIMIT = 1;

    /*
     *  This is the tile state in the CPU side.
     *  Life of a Tile:
     *      ACTIVATED (initial state)
     *              --> IN_QUEUE - by queueForDecode()
     *              --> RECYCLED - by recycleTile()
     *      IN_QUEUE --> DECODING - by decodeTile()
     *               --> RECYCLED - by recycleTile)
     *      DECODING --> RECYCLING - by recycleTile()
     *               --> DECODED  - by decodeTile()
     *               --> DECODE_FAIL - by decodeTile()
     *      RECYCLING --> RECYCLED - by decodeTile()
     *      DECODED --> ACTIVATED - (after the decoded bitmap is uploaded)
     *      DECODED --> RECYCLED - by recycleTile()
     *      DECODE_FAIL -> RECYCLED - by recycleTile()
     *      RECYCLED --> ACTIVATED - by obtainTile()
     */
    private static final int STATE_ACTIVATED = 0x01;
    private static final int STATE_IN_QUEUE = 0x02;
    private static final int STATE_DECODING = 0x04;
    private static final int STATE_DECODED = 0x08;
    private static final int STATE_DECODE_FAIL = 0x10;
    private static final int STATE_RECYCLING = 0x20;
    private static final int STATE_RECYCLED = 0x40;

    private Model mModel;
    protected BitmapTexture mBackupImage;
    protected int mLevelCount;  /*// cache the value of mScaledBitmaps.length*/

    /*// The mLevel variable indicates which level of bitmap we should use.*/
    /*// Level 0 means the original full-sized bitmap, and a larger value means*/
    /*// a smaller scaled bitmap (The width and height of each scaled bitmap is*/
    /*// half size of the previous one). If the value is in [0, mLevelCount), we*/
    /*// use the bitmap in mScaledBitmaps[mLevel] for display, otherwise the value*/
    /*// is mLevelCount, and that means we use mBackupTexture for display.*/
    private int mLevel = 0;

    /*// The offsets of the (left, top) of the upper-left tile to the (left, top)*/
    /*// of the view.*/
    private int mOffsetX;
    private int mOffsetY;

    private int mUploadQuota;
    private boolean mRenderComplete;

    private final RectF mSourceRect = new RectF();
    private final RectF mTargetRect = new RectF();

    private final HashMap<Long, Tile> mActiveTiles = new HashMap<Long, Tile>();

    /*// The following three queue is guarded by TileImageView.this*/
    private TileQueue mRecycledQueue = new TileQueue();
    private TileQueue mUploadQueue = new TileQueue();
    private TileQueue mDecodeQueue = new TileQueue();

    /*// The width and height of the full-sized bitmap*/
    protected int mImageWidth = SIZE_UNKNOWN;
    protected int mImageHeight = SIZE_UNKNOWN;

    protected int mCenterX;
    protected int mCenterY;
    protected float mScale;
    protected int mRotation;

    /*// Temp variables to avoid memory allocation*/
    private final Rect mTileRange = new Rect();
    private final Rect mActiveRange[] = {new Rect(), new Rect()};

    private final TileUploader mTileUploader = new TileUploader();
    private boolean mIsTextureFreed;
    private Future<Void> mTileDecoder;
    private ThreadPool mThreadPool;
    private boolean mBackgroundTileUploaded;

    public static interface Model {
        public int getLevelCount();
        public Bitmap getBackupImage();
        public int getImageWidth();
        public int getImageHeight();

        /*// The method would be called in another thread*/
        public Bitmap getTile(int level, int x, int y, int tileSize);
        public boolean isFailedToLoad();
    }

    public TileImageView(GalleryContext context) {
        mThreadPool = context.getThreadPool();
        mTileDecoder = mThreadPool.submit(new TileDecoder());
    }

    public void setModel(Model model) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.setModel(Model)",this,model);try{mModel = model;
        if (model != null) {notifyModelInvalidated();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.setModel(Model)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.setModel(Model)",this,throwable);throw throwable;}
    }

    private void updateBackupTexture(Bitmap backup) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.updateBackupTexture(android.graphics.Bitmap)",this,backup);try{if (backup == null) {
            if (mBackupImage != null) {mBackupImage.recycle();}
            mBackupImage = null;
        } else {
            if (mBackupImage != null) {
                if (mBackupImage.getBitmap() != backup) {
                    mBackupImage.recycle();
                    mBackupImage = new BitmapTexture(backup);
                }
            } else {
                mBackupImage = new BitmapTexture(backup);
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.updateBackupTexture(android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.updateBackupTexture(android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    public void notifyModelInvalidated() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.notifyModelInvalidated()",this);try{invalidateTiles();
        if (mModel == null) {
            mBackupImage = null;
            mImageWidth = 0;
            mImageHeight = 0;
            mLevelCount = 0;
        } else {
            updateBackupTexture(mModel.getBackupImage());
            mImageWidth = mModel.getImageWidth();
            mImageHeight = mModel.getImageHeight();
            mLevelCount = mModel.getLevelCount();
        }
        layoutTiles(mCenterX, mCenterY, mScale, mRotation);
        invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.notifyModelInvalidated()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.notifyModelInvalidated()",this,throwable);throw throwable;}
    }

    @Override
    protected void onLayout(
            boolean changeSize, int left, int top, int right, int bottom) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.onLayout(boolean,int,int,int,int)",this,changeSize,left,top,right,bottom);try{super.onLayout(changeSize, left, top, right, bottom);
        if (changeSize) {layoutTiles(mCenterX, mCenterY, mScale, mRotation);}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.onLayout(boolean,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.onLayout(boolean,int,int,int,int)",this,throwable);throw throwable;}
    }

    /*// Prepare the tiles we want to use for display.*/
    /*//*/
    /*// 1. Decide the tile level we want to use for display.*/
    /*// 2. Decide the tile levels we want to keep as texture (in addition to*/
    /*//    the one we use for display).*/
    /*// 3. Recycle unused tiles.*/
    /*// 4. Activate the tiles we want.*/
    private void layoutTiles(int centerX, int centerY, float scale, int rotation) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.layoutTiles(int,int,float,int)",this,centerX,centerY,scale,rotation);try{/*// The width and height of this view.*/
        int width = getWidth();
        int height = getHeight();

        /*// The tile levels we want to keep as texture is in the range*/
        /*// [fromLevel, endLevel).*/
        int fromLevel;
        int endLevel;

        /*// We want to use a texture larger than or equal to the display size.*/
        mLevel = Utils.clamp(Utils.floorLog2(1f / scale), 0, mLevelCount);

        /*// We want to keep one more tile level as texture in addition to what*/
        /*// we use for display. So it can be faster when the scale moves to the*/
        /*// next level. We choose a level closer to the current scale.*/
        if (mLevel != mLevelCount) {
            Rect range = mTileRange;
            getRange(range, centerX, centerY, mLevel, scale, rotation);
            mOffsetX = Math.round(width / 2f + (range.left - centerX) * scale);
            mOffsetY = Math.round(height / 2f + (range.top - centerY) * scale);
            fromLevel = scale * (1 << mLevel) > 0.75f ? mLevel - 1 : mLevel;
        } else {
            /*// Activate the tiles of the smallest two levels.*/
            fromLevel = mLevel - 2;
            mOffsetX = Math.round(width / 2f - centerX * scale);
            mOffsetY = Math.round(height / 2f - centerY * scale);
        }

        fromLevel = Math.max(0, Math.min(fromLevel, mLevelCount - 2));
        endLevel = Math.min(fromLevel + 2, mLevelCount);

        Rect range[] = mActiveRange;
        for (int i = fromLevel; i < endLevel; ++i) {
            getRange(range[i - fromLevel], centerX, centerY, i, rotation);
        }

        /*// If rotation is transient, don't update the tile.*/
        if (rotation % 90 != 0) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.layoutTiles(int,int,float,int)",this);return;}}

        synchronized (this) {
            mDecodeQueue.clean();
            mUploadQueue.clean();
            mBackgroundTileUploaded = false;
        }

        /*// Recycle unused tiles: if the level of the active tile is outside the*/
        /*// range [fromLevel, endLevel) or not in the visible range.*/
        Iterator<Map.Entry<Long, Tile>>
                iter = mActiveTiles.entrySet().iterator();
        while (iter.hasNext()) {
            Tile tile = iter.next().getValue();
            int level = tile.mTileLevel;
            if (level < fromLevel || level >= endLevel
                    || !range[level - fromLevel].contains(tile.mX, tile.mY)) {
                iter.remove();
                recycleTile(tile);
            }
        }

        for (int i = fromLevel; i < endLevel; ++i) {
            int size = TILE_SIZE << i;
            Rect r = range[i - fromLevel];
            for (int y = r.top, bottom = r.bottom; y < bottom; y += size) {
                for (int x = r.left, right = r.right; x < right; x += size) {
                    activateTile(x, y, i);
                }
            }
        }
        invalidate();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.layoutTiles(int,int,float,int)",this,throwable);throw throwable;}
    }

    protected synchronized void invalidateTiles() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.invalidateTiles()",this);try{mDecodeQueue.clean();
        mUploadQueue.clean();
        /*// TODO disable decoder*/
        for (Tile tile : mActiveTiles.values()) {
            recycleTile(tile);
        }
        mActiveTiles.clear();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.invalidateTiles()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.invalidateTiles()",this,throwable);throw throwable;}
    }

    private void getRange(Rect out, int cX, int cY, int level, int rotation) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.getRange(android.graphics.Rect,int,int,int,int)",this,out,cX,cY,level,rotation);try{getRange(out, cX, cY, level, 1f / (1 << (level + 1)), rotation);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.getRange(android.graphics.Rect,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.getRange(android.graphics.Rect,int,int,int,int)",this,throwable);throw throwable;}
    }

    /*// If the bitmap is scaled by the given factor "scale", return the*/
    /*// rectangle containing visible range. The left-top coordinate returned is*/
    /*// aligned to the tile boundary.*/
    /*//*/
    /*// (cX, cY) is the point on the original bitmap which will be put in the*/
    /*// center of the ImageViewer.*/
    private void getRange(Rect out,
            int cX, int cY, int level, float scale, int rotation) {

        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.getRange(android.graphics.Rect,int,int,int,float,int)",this,out,cX,cY,level,scale,rotation);try{double radians = Math.toRadians(-rotation);
        double w = getWidth();
        double h = getHeight();

        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        int width = (int) Math.ceil(Math.max(
                Math.abs(cos * w - sin * h), Math.abs(cos * w + sin * h)));
        int height = (int) Math.ceil(Math.max(
                Math.abs(sin * w + cos * h), Math.abs(sin * w - cos * h)));

        int left = (int) Math.floor(cX - width / (2f * scale));
        int top = (int) Math.floor(cY - height / (2f * scale));
        int right = (int) Math.ceil(left + width / scale);
        int bottom = (int) Math.ceil(top + height / scale);

        /*// align the rectangle to tile boundary*/
        int size = TILE_SIZE << level;
        left = Math.max(0, size * (left / size));
        top = Math.max(0, size * (top / size));
        right = Math.min(mImageWidth, right);
        bottom = Math.min(mImageHeight, bottom);

        out.set(left, top, right, bottom);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.getRange(android.graphics.Rect,int,int,int,float,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.getRange(android.graphics.Rect,int,int,int,float,int)",this,throwable);throw throwable;}
    }

    public boolean setPosition(int centerX, int centerY, float scale, int rotation) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.TileImageView.setPosition(int,int,float,int)",this,centerX,centerY,scale,rotation);try{if (mCenterX == centerX
                && mCenterY == centerY && mScale == scale) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.TileImageView.setPosition(int,int,float,int)",this);return false;}}
        mCenterX = centerX;
        mCenterY = centerY;
        mScale = scale;
        mRotation = rotation;
        layoutTiles(centerX, centerY, scale, rotation);
        invalidate();
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.TileImageView.setPosition(int,int,float,int)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.TileImageView.setPosition(int,int,float,int)",this,throwable);throw throwable;}
    }

    public void freeTextures() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.freeTextures()",this);try{mIsTextureFreed = true;

        if (mTileDecoder != null) {
            mTileDecoder.cancel();
            mTileDecoder.get();
            mTileDecoder = null;
        }

        for (Tile texture : mActiveTiles.values()) {
            texture.recycle();
        }
        mTileRange.set(0, 0, 0, 0);
        mActiveTiles.clear();

        synchronized (this) {
            mUploadQueue.clean();
            mDecodeQueue.clean();
            Tile tile = mRecycledQueue.pop();
            while (tile != null) {
                tile.recycle();
                tile = mRecycledQueue.pop();
            }
        }
        updateBackupTexture(null);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.freeTextures()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.freeTextures()",this,throwable);throw throwable;}
    }

    public void prepareTextures() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.prepareTextures()",this);try{if (mTileDecoder == null) {
            mTileDecoder = mThreadPool.submit(new TileDecoder());
        }
        if (mIsTextureFreed) {
            layoutTiles(mCenterX, mCenterY, mScale, mRotation);
            mIsTextureFreed = false;
            updateBackupTexture(mModel != null ? mModel.getBackupImage() : null);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.prepareTextures()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.prepareTextures()",this,throwable);throw throwable;}
    }

    @Override
    protected void render(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.render(GLCanvas)",this,canvas);try{mUploadQuota = UPLOAD_LIMIT;
        mRenderComplete = true;

        int level = mLevel;
        int rotation = mRotation;

        if (rotation != 0) {
            canvas.save(GLCanvas.SAVE_FLAG_MATRIX);
            int centerX = getWidth() / 2, centerY = getHeight() / 2;
            canvas.translate(centerX, centerY, 0);
            canvas.rotate(rotation, 0, 0, 1);
            canvas.translate(-centerX, -centerY, 0);
        }
        try {
            if (level != mLevelCount) {
                int size = (TILE_SIZE << level);
                float length = size * mScale;
                Rect r = mTileRange;

                for (int ty = r.top, i = 0; ty < r.bottom; ty += size, i++) {
                    float y = mOffsetY + i * length;
                    for (int tx = r.left, j = 0; tx < r.right; tx += size, j++) {
                        float x = mOffsetX + j * length;
                        drawTile(canvas, tx, ty, level, x, y, length);
                    }
                }
            } else if (mBackupImage != null) {
                mBackupImage.draw(canvas, mOffsetX, mOffsetY,
                        Math.round(mImageWidth * mScale),
                        Math.round(mImageHeight * mScale));
            }
        } finally {
            if (rotation != 0) {canvas.restore();}
        }

        if (mRenderComplete) {
            if (!mBackgroundTileUploaded) {uploadBackgroundTiles(canvas);}
        } else {
            invalidate();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.render(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.render(GLCanvas)",this,throwable);throw throwable;}
    }

    private void uploadBackgroundTiles(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.uploadBackgroundTiles(GLCanvas)",this,canvas);try{mBackgroundTileUploaded = true;
        for (Tile tile : mActiveTiles.values()) {
            if (!tile.isContentValid(canvas)) {queueForDecode(tile);}
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.uploadBackgroundTiles(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.uploadBackgroundTiles(GLCanvas)",this,throwable);throw throwable;}
    }

    void queueForUpload(Tile tile) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.queueForUpload(com.android.gallery3d.ui.TileImageView$Tile)",this,tile);try{synchronized (this) {
            mUploadQueue.push(tile);
        }
        if (mTileUploader.mActive.compareAndSet(false, true)) {
            getGLRoot().addOnGLIdleListener(mTileUploader);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.queueForUpload(com.android.gallery3d.ui.TileImageView$Tile)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.queueForUpload(com.android.gallery3d.ui.TileImageView$Tile)",this,throwable);throw throwable;}
    }

    synchronized void queueForDecode(Tile tile) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.queueForDecode(com.android.gallery3d.ui.TileImageView$Tile)",this,tile);try{if (tile.mTileState == STATE_ACTIVATED) {
            tile.mTileState = STATE_IN_QUEUE;
            if (mDecodeQueue.push(tile)) {notifyAll();}
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.queueForDecode(com.android.gallery3d.ui.TileImageView$Tile)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.queueForDecode(com.android.gallery3d.ui.TileImageView$Tile)",this,throwable);throw throwable;}
    }

    boolean decodeTile(Tile tile) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.TileImageView.decodeTile(com.android.gallery3d.ui.TileImageView$Tile)",this,tile);try{synchronized (this) {
            if (tile.mTileState != STATE_IN_QUEUE) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.TileImageView.decodeTile(com.android.gallery3d.ui.TileImageView$Tile)",this);return false;}}
            tile.mTileState = STATE_DECODING;
        }
        boolean decodeComplete = tile.decode();
        synchronized (this) {
            if (tile.mTileState == STATE_RECYCLING) {
                tile.mTileState = STATE_RECYCLED;
                tile.mDecodedTile = null;
                mRecycledQueue.push(tile);
                {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.TileImageView.decodeTile(com.android.gallery3d.ui.TileImageView$Tile)",this);return false;}
            }
            tile.mTileState = decodeComplete ? STATE_DECODED : STATE_DECODE_FAIL;
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.TileImageView.decodeTile(com.android.gallery3d.ui.TileImageView$Tile)",this);return decodeComplete;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.TileImageView.decodeTile(com.android.gallery3d.ui.TileImageView$Tile)",this,throwable);throw throwable;}
    }

    private synchronized Tile obtainTile(int x, int y, int level) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView.obtainTile(int,int,int)",this,x,y,level);try{Tile tile = mRecycledQueue.pop();
        if (tile != null) {
            tile.mTileState = STATE_ACTIVATED;
            tile.update(x, y, level);
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView.obtainTile(int,int,int)",this);return tile;}
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView.obtainTile(int,int,int)",this);return new Tile(x, y, level);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView.obtainTile(int,int,int)",this,throwable);throw throwable;}
    }

    synchronized void recycleTile(Tile tile) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.recycleTile(com.android.gallery3d.ui.TileImageView$Tile)",this,tile);try{if (tile.mTileState == STATE_DECODING) {
            tile.mTileState = STATE_RECYCLING;
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.recycleTile(com.android.gallery3d.ui.TileImageView$Tile)",this);return;}
        }
        tile.mTileState = STATE_RECYCLED;
        tile.mDecodedTile = null;
        mRecycledQueue.push(tile);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.recycleTile(com.android.gallery3d.ui.TileImageView$Tile)",this,throwable);throw throwable;}
    }

    private void activateTile(int x, int y, int level) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.activateTile(int,int,int)",this,x,y,level);try{Long key = makeTileKey(x, y, level);
        Tile tile = mActiveTiles.get(key);
        if (tile != null) {
            if (tile.mTileState == STATE_IN_QUEUE) {
                tile.mTileState = STATE_ACTIVATED;
            }
            {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.activateTile(int,int,int)",this);return;}
        }
        tile = obtainTile(x, y, level);
        mActiveTiles.put(key, tile);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.activateTile(int,int,int)",this,throwable);throw throwable;}
    }

    private Tile getTile(int x, int y, int level) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView.getTile(int,int,int)",this,x,y,level);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView.getTile(int,int,int)",this);return mActiveTiles.get(makeTileKey(x, y, level));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView.getTile(int,int,int)",this,throwable);throw throwable;}
    }

    private static Long makeTileKey(int x, int y, int level) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.Long com.android.gallery3d.ui.TileImageView.makeTileKey(int,int,int)",x,y,level);try{long result = x;
        result = (result << 16) | y;
        result = (result << 16) | level;
        {com.mijack.Xlog.logStaticMethodExit("java.lang.Long com.android.gallery3d.ui.TileImageView.makeTileKey(int,int,int)");return Long.valueOf(result);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.Long com.android.gallery3d.ui.TileImageView.makeTileKey(int,int,int)",throwable);throw throwable;}
    }

    private class TileUploader implements GLRoot.OnGLIdleListener {
        AtomicBoolean mActive = new AtomicBoolean(false);

        @Override
        public boolean onGLIdle(GLRoot root, GLCanvas canvas) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.TileImageView$TileUploader.onGLIdle(GLRoot,GLCanvas)",this,root,canvas);try{int quota = UPLOAD_LIMIT;
            Tile tile;
            while (true) {
                synchronized (TileImageView.this) {
                    tile = mUploadQueue.pop();
                }
                if (tile == null || quota <= 0) {break;}
                if (!tile.isContentValid(canvas)) {
                    /*//Utils.assertTrue(tile.mTileState == STATE_DECODED, "Tile %s content is not valid, state = %s", tile.toString(), tile.mTileState);*/
                    tile.updateContent(canvas);
                    --quota;
                }
            }
            mActive.set(tile != null);
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.TileImageView$TileUploader.onGLIdle(GLRoot,GLCanvas)",this);return tile != null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.TileImageView$TileUploader.onGLIdle(GLRoot,GLCanvas)",this,throwable);throw throwable;}
        }
    }

    /*// Draw the tile to a square at canvas that locates at (x, y) and*/
    /*// has a side length of length.*/
    public void drawTile(GLCanvas canvas,
            int tx, int ty, int level, float x, float y, float length) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView.drawTile(GLCanvas,int,int,int,float,float,float)",this,canvas,tx,ty,level,x,y,length);try{RectF source = mSourceRect;
        RectF target = mTargetRect;
        target.set(x, y, x + length, y + length);
        source.set(0, 0, TILE_SIZE, TILE_SIZE);

        Tile tile = getTile(tx, ty, level);
        if (tile != null) {
            if (!tile.isContentValid(canvas)) {
                if (tile.mTileState == STATE_DECODED) {
                    if (mUploadQuota > 0) {
                        --mUploadQuota;
                        tile.updateContent(canvas);
                    } else {
                        mRenderComplete = false;
                    }
                } else if (tile.mTileState != STATE_DECODE_FAIL){
                    mRenderComplete = false;
                    queueForDecode(tile);
                }
            }
            if (drawTile(tile, canvas, source, target)) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView.drawTile(GLCanvas,int,int,int,float,float,float)",this);return;}}
        }
        if (mBackupImage != null) {
            BasicTexture backup = mBackupImage;
            int size = TILE_SIZE << level;
            float scaleX = (float) backup.getWidth() / mImageWidth;
            float scaleY = (float) backup.getHeight() / mImageHeight;
            source.set(tx * scaleX, ty * scaleY, (tx + size) * scaleX,
                    (ty + size) * scaleY);
            canvas.drawTexture(backup, source, target);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView.drawTile(GLCanvas,int,int,int,float,float,float)",this,throwable);throw throwable;}
    }

    /*// TODO: avoid drawing the unused part of the textures.*/
    static boolean drawTile(
            Tile tile, GLCanvas canvas, RectF source, RectF target) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.ui.TileImageView.drawTile(com.android.gallery3d.ui.TileImageView$Tile,GLCanvas,android.graphics.RectF,android.graphics.RectF)",tile,canvas,source,target);try{while (true) {
            if (tile.isContentValid(canvas)) {
                /*// offset source rectangle for the texture border.*/
                source.offset(TILE_BORDER, TILE_BORDER);
                canvas.drawTexture(tile, source, target);
                {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.ui.TileImageView.drawTile(com.android.gallery3d.ui.TileImageView$Tile,GLCanvas,android.graphics.RectF,android.graphics.RectF)");return true;}
            }

            /*// Parent can be divided to four quads and tile is one of the four.*/
            Tile parent = tile.getParentTile();
            if (parent == null) {{com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.ui.TileImageView.drawTile(com.android.gallery3d.ui.TileImageView$Tile,GLCanvas,android.graphics.RectF,android.graphics.RectF)");return false;}}
            if (tile.mX == parent.mX) {
                source.left /= 2f;
                source.right /= 2f;
            } else {
                source.left = (TILE_SIZE + source.left) / 2f;
                source.right = (TILE_SIZE + source.right) / 2f;
            }
            if (tile.mY == parent.mY) {
                source.top /= 2f;
                source.bottom /= 2f;
            } else {
                source.top = (TILE_SIZE + source.top) / 2f;
                source.bottom = (TILE_SIZE + source.bottom) / 2f;
            }
            tile = parent;
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.ui.TileImageView.drawTile(com.android.gallery3d.ui.TileImageView$Tile,GLCanvas,android.graphics.RectF,android.graphics.RectF)",throwable);throw throwable;}
    }

    private class Tile extends UploadedTexture {
        int mX;
        int mY;
        int mTileLevel;
        Tile mNext;
        Bitmap mDecodedTile;
        volatile int mTileState = STATE_ACTIVATED;

        public Tile(int x, int y, int level) {
            mX = x;
            mY = y;
            mTileLevel = level;
        }

        @Override
        protected void onFreeBitmap(Bitmap bitmap) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView$Tile.onFreeBitmap(android.graphics.Bitmap)",this,bitmap);try{bitmap.recycle();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView$Tile.onFreeBitmap(android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView$Tile.onFreeBitmap(android.graphics.Bitmap)",this,throwable);throw throwable;}
        }

        boolean decode() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.TileImageView$Tile.decode()",this);try{/*// Get a tile from the original image. The tile is down-scaled*/
            /*// by (1 << mTilelevel) from a region in the original image.*/
            int tileLength = (TILE_SIZE + 2 * TILE_BORDER);
            int borderLength = TILE_BORDER << mTileLevel;
            try {
                mDecodedTile = DecodeUtils.ensureGLCompatibleBitmap(mModel.getTile(
                        mTileLevel, mX - borderLength, mY - borderLength, tileLength));
            } catch (Throwable t) {
                Log.w(TAG, "fail to decode tile", t);
            }
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.TileImageView$Tile.decode()",this);return mDecodedTile != null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.TileImageView$Tile.decode()",this,throwable);throw throwable;}
        }

        @Override
        protected Bitmap onGetBitmap() {
            com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.TileImageView$Tile.onGetBitmap()",this);try{/*// Utils.assertTrue(mTileState == STATE_DECODED);*/
        	/*// instead of failing when state is not decoded we create empty black filled tile*/
        	if (mTileState == STATE_DECODED) {
	            Bitmap bitmap = mDecodedTile;
	            mDecodedTile = null;
	            mTileState = STATE_ACTIVATED;
	            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.TileImageView$Tile.onGetBitmap()",this);return bitmap;}
        	} else {
        		mDecodedTile = null;
	            mTileState = STATE_ACTIVATED;
	            
                int borderLength = TILE_BORDER << mTileLevel;
                Bitmap bitmap = Bitmap.createBitmap(mX - borderLength, mY - borderLength, Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
	            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.TileImageView$Tile.onGetBitmap()",this);return bitmap;}
        	}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.TileImageView$Tile.onGetBitmap()",this,throwable);throw throwable;}
        }

        @Override
        protected BitmapFactory.Options onGetBitmapBounds() {
            com.mijack.Xlog.logMethodEnter("BitmapFactory.Options com.android.gallery3d.ui.TileImageView$Tile.onGetBitmapBounds()",this);try{BitmapFactory.Options options = new BitmapFactory.Options();
            if (mDecodedTile != null) {
                options.outWidth = mDecodedTile.getWidth();
                options.outHeight = mDecodedTile.getHeight();
            }
            else {
                options.outWidth = 0;
                options.outHeight = 0;
            }
            {com.mijack.Xlog.logMethodExit("BitmapFactory.Options com.android.gallery3d.ui.TileImageView$Tile.onGetBitmapBounds()",this);return options;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("BitmapFactory.Options com.android.gallery3d.ui.TileImageView$Tile.onGetBitmapBounds()",this,throwable);throw throwable;}
        }

        public void update(int x, int y, int level) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView$Tile.update(int,int,int)",this,x,y,level);try{mX = x;
            mY = y;
            mTileLevel = level;
            invalidateContent();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView$Tile.update(int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView$Tile.update(int,int,int)",this,throwable);throw throwable;}
        }

        public Tile getParentTile() {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView$Tile.getParentTile()",this);try{if (mTileLevel + 1 == mLevelCount) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView$Tile.getParentTile()",this);return null;}}
            int size = TILE_SIZE << (mTileLevel + 1);
            int x = size * (mX / size);
            int y = size * (mY / size);
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView$Tile.getParentTile()",this);return getTile(x, y, mTileLevel + 1);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView$Tile.getParentTile()",this,throwable);throw throwable;}
        }

        @Override
        public String toString() {
            com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.ui.TileImageView$Tile.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.ui.TileImageView$Tile.toString()",this);return String.format("tile(%s, %s, %s / %s)",
                    mX / TILE_SIZE, mY / TILE_SIZE, mLevel, mLevelCount);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.ui.TileImageView$Tile.toString()",this,throwable);throw throwable;}
        }
    }

    private static class TileQueue {
        private Tile mHead;

        public Tile pop() {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView$TileQueue.pop()",this);try{Tile tile = mHead;
            if (tile != null) {mHead = tile.mNext;}
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView$TileQueue.pop()",this);return tile;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.TileImageView$Tile com.android.gallery3d.ui.TileImageView$TileQueue.pop()",this,throwable);throw throwable;}
        }

        public boolean push(Tile tile) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.TileImageView$TileQueue.push(com.android.gallery3d.ui.TileImageView$Tile)",this,tile);try{boolean wasEmpty = mHead == null;
            tile.mNext = mHead;
            mHead = tile;
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.TileImageView$TileQueue.push(com.android.gallery3d.ui.TileImageView$Tile)",this);return wasEmpty;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.TileImageView$TileQueue.push(com.android.gallery3d.ui.TileImageView$Tile)",this,throwable);throw throwable;}
        }

        public void clean() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView$TileQueue.clean()",this);try{mHead = null;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView$TileQueue.clean()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView$TileQueue.clean()",this,throwable);throw throwable;}
        }
    }

    private class TileDecoder implements ThreadPool.Job<Void> {

        private CancelListener mNotifier = new CancelListener() {
            @Override
            public void onCancel() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageView$TileDecoder$1.onCancel()",this);try{synchronized (TileImageView.this) {
                    TileImageView.this.notifyAll();
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageView$TileDecoder$1.onCancel()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageView$TileDecoder$1.onCancel()",this,throwable);throw throwable;}
            }
        };

        @Override
        public Void run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("java.lang.Void com.android.gallery3d.ui.TileImageView$TileDecoder.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{jc.setMode(ThreadPool.MODE_NONE);
            jc.setCancelListener(mNotifier);
            while (!jc.isCancelled()) {
                Tile tile = null;
                synchronized(TileImageView.this) {
                    tile = mDecodeQueue.pop();
                    if (tile == null && !jc.isCancelled()) {
                        Utils.waitWithoutInterrupt(TileImageView.this);
                    }
                }
                if (tile == null) {continue;}
                if (decodeTile(tile)) {queueForUpload(tile);}
            }
            {com.mijack.Xlog.logMethodExit("java.lang.Void com.android.gallery3d.ui.TileImageView$TileDecoder.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Void com.android.gallery3d.ui.TileImageView$TileDecoder.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }
}
