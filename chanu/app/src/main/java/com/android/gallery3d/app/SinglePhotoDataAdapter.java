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

package com.android.gallery3d.app;

import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.PhotoView;
import com.android.gallery3d.ui.PhotoView.ImageData;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.ui.TileImageViewAdapter;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.ThreadPool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;

public class SinglePhotoDataAdapter extends TileImageViewAdapter
        implements PhotoPage.Model {

    private static final String TAG = "SinglePhotoDataAdapter";
    private static final int SIZE_BACKUP = 1024;
    private static final int MSG_UPDATE_IMAGE = 1;

    private MediaItem mItem;
    private boolean mHasFullImage;
    private Future<?> mTask;
    private Handler mHandler;

    private PhotoView mPhotoView;
    private ThreadPool mThreadPool;

    public SinglePhotoDataAdapter(
            GalleryActivity activity, PhotoView view, MediaItem item) {
        mItem = Utils.checkNotNull(item);
        mHasFullImage = (item.getSupportedOperations() &
                MediaItem.SUPPORT_FULL_IMAGE) != 0;
        mPhotoView = Utils.checkNotNull(view);
        mHandler = new SynchronizedHandler(activity.getGLRoot()) {
            @Override
            @SuppressWarnings("unchecked")
            public void handleMessage(Message message) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SinglePhotoDataAdapter$1.handleMessage(android.os.Message)",this,message);try{Utils.assertTrue(message.what == MSG_UPDATE_IMAGE);
                if (mHasFullImage) {
                    onDecodeLargeComplete((ImageBundle) message.obj);
                } else {
                    onDecodeThumbComplete((Future<Bitmap>) message.obj);
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SinglePhotoDataAdapter$1.handleMessage(android.os.Message)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SinglePhotoDataAdapter$1.handleMessage(android.os.Message)",this,throwable);throw throwable;}
            }
        };
        mThreadPool = activity.getThreadPool();
    }

    private static class ImageBundle {
        public final BitmapRegionDecoder decoder;
        public final Bitmap backupImage;

        public ImageBundle(BitmapRegionDecoder decoder, Bitmap backupImage) {
            this.decoder = decoder;
            this.backupImage = backupImage;
        }
    }

    private FutureListener<BitmapRegionDecoder> mLargeListener =
            new FutureListener<BitmapRegionDecoder>() {
        public void onFutureDone(Future<BitmapRegionDecoder> future) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SinglePhotoDataAdapter$2.onFutureDone(com.android.gallery3d.util.Future)",this,future);try{BitmapRegionDecoder decoder = future.get();
            if (decoder == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SinglePhotoDataAdapter$2.onFutureDone(com.android.gallery3d.util.Future)",this);return;}}
            int width = decoder.getWidth();
            int height = decoder.getHeight();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = BitmapUtils.computeSampleSize(
                    (float) SIZE_BACKUP / Math.max(width, height));
            Bitmap bitmap = decoder.decodeRegion(new Rect(0, 0, width, height), options);
            mHandler.sendMessage(mHandler.obtainMessage(
                    MSG_UPDATE_IMAGE, new ImageBundle(decoder, bitmap)));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SinglePhotoDataAdapter$2.onFutureDone(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
        }
    };

    private FutureListener<Bitmap> mThumbListener =
            new FutureListener<Bitmap>() {
        public void onFutureDone(Future<Bitmap> future) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SinglePhotoDataAdapter$3.onFutureDone(com.android.gallery3d.util.Future)",this,future);try{mHandler.sendMessage(
                    mHandler.obtainMessage(MSG_UPDATE_IMAGE, future));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SinglePhotoDataAdapter$3.onFutureDone(com.android.gallery3d.util.Future)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SinglePhotoDataAdapter$3.onFutureDone(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
        }
    };

    public boolean isEmpty() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.SinglePhotoDataAdapter.isEmpty()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.SinglePhotoDataAdapter.isEmpty()",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.SinglePhotoDataAdapter.isEmpty()",this,throwable);throw throwable;}
    }

    public int getImageRotation() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.SinglePhotoDataAdapter.getImageRotation()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.SinglePhotoDataAdapter.getImageRotation()",this);return mItem.getRotation();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.SinglePhotoDataAdapter.getImageRotation()",this,throwable);throw throwable;}
    }

    private void onDecodeLargeComplete(ImageBundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SinglePhotoDataAdapter.onDecodeLargeComplete(com.android.gallery3d.app.SinglePhotoDataAdapter$ImageBundle)",this,bundle);try{try {
            setBackupImage(bundle.backupImage,
                    bundle.decoder.getWidth(), bundle.decoder.getHeight());
            setRegionDecoder(bundle.decoder);
            mPhotoView.notifyImageInvalidated(0);
        } catch (Throwable t) {
            Log.w(TAG, "fail to decode large", t);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SinglePhotoDataAdapter.onDecodeLargeComplete(com.android.gallery3d.app.SinglePhotoDataAdapter$ImageBundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SinglePhotoDataAdapter.onDecodeLargeComplete(com.android.gallery3d.app.SinglePhotoDataAdapter$ImageBundle)",this,throwable);throw throwable;}
    }

    private void onDecodeThumbComplete(Future<Bitmap> future) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SinglePhotoDataAdapter.onDecodeThumbComplete(com.android.gallery3d.util.Future)",this,future);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SinglePhotoDataAdapter.onDecodeThumbComplete(com.android.gallery3d.util.Future)",this);try {
            Bitmap backup = future.get();
            if (backup == null) {return;}
            setBackupImage(backup, backup.getWidth(), backup.getHeight());
            mPhotoView.notifyOnNewImage();
            mPhotoView.notifyImageInvalidated(0); /*// the current image*/
        } catch (Throwable t) {
            Log.w(TAG, "fail to decode thumb", t);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SinglePhotoDataAdapter.onDecodeThumbComplete(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SinglePhotoDataAdapter.resume()",this);try{if (mTask == null) {
            if (mHasFullImage) {
                mTask = mThreadPool.submit(
                        mItem.requestLargeImage(), mLargeListener);
            } else {
                mTask = mThreadPool.submit(
                        mItem.requestImage(MediaItem.TYPE_THUMBNAIL),
                        mThumbListener);
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SinglePhotoDataAdapter.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SinglePhotoDataAdapter.resume()",this,throwable);throw throwable;}
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SinglePhotoDataAdapter.pause()",this);try{Future<?> task = mTask;
        task.cancel();
        task.waitDone();
        if (task.get() == null) {
            mTask = null;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SinglePhotoDataAdapter.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SinglePhotoDataAdapter.pause()",this,throwable);throw throwable;}
    }

    public ImageData getNextImage() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.SinglePhotoDataAdapter.getNextImage()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.SinglePhotoDataAdapter.getNextImage()",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.SinglePhotoDataAdapter.getNextImage()",this,throwable);throw throwable;}
    }

    public ImageData getPreviousImage() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.SinglePhotoDataAdapter.getPreviousImage()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.SinglePhotoDataAdapter.getPreviousImage()",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PhotoView.ImageData com.android.gallery3d.app.SinglePhotoDataAdapter.getPreviousImage()",this,throwable);throw throwable;}
    }

    public void next() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SinglePhotoDataAdapter.next()",this);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SinglePhotoDataAdapter.next()",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SinglePhotoDataAdapter.next()",this,throwable);throw throwable;}
    }

    public void previous() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SinglePhotoDataAdapter.previous()",this);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SinglePhotoDataAdapter.previous()",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SinglePhotoDataAdapter.previous()",this,throwable);throw throwable;}
    }

    public void jumpTo(int index) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SinglePhotoDataAdapter.jumpTo(int)",this,index);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SinglePhotoDataAdapter.jumpTo(int)",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SinglePhotoDataAdapter.jumpTo(int)",this,throwable);throw throwable;}
    }

    public MediaItem getCurrentMediaItem() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SinglePhotoDataAdapter.getCurrentMediaItem()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SinglePhotoDataAdapter.getCurrentMediaItem()",this);return mItem;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaItem com.android.gallery3d.app.SinglePhotoDataAdapter.getCurrentMediaItem()",this,throwable);throw throwable;}
    }

    public int getCurrentIndex() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.SinglePhotoDataAdapter.getCurrentIndex()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.SinglePhotoDataAdapter.getCurrentIndex()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.SinglePhotoDataAdapter.getCurrentIndex()",this,throwable);throw throwable;}
    }

    public void setCurrentPhoto(Path path, int indexHint) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.SinglePhotoDataAdapter.setCurrentPhoto(com.android.gallery3d.data.Path,int)",this,path,indexHint);try{/*// ignore*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.SinglePhotoDataAdapter.setCurrentPhoto(com.android.gallery3d.data.Path,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.SinglePhotoDataAdapter.setCurrentPhoto(com.android.gallery3d.data.Path,int)",this,throwable);throw throwable;}
    }
}
