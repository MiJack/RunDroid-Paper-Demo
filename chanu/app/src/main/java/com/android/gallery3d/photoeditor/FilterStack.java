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

import android.graphics.Bitmap;

import com.android.gallery3d.photoeditor.filters.Filter;

import java.util.Stack;

/**
 * A stack of filters to be applied onto a photo.
 */
public class FilterStack {

    /**
     * Listener of stack changes.
     */
    public interface StackListener {

        void onStackChanged(boolean canUndo, boolean canRedo);
    }

    private final Stack<Filter> appliedStack = new Stack<Filter>();
    private final Stack<Filter> redoStack = new Stack<Filter>();

    /*// Use two photo buffers as in and out in turns to apply filters in the stack.*/
    private final Photo[] buffers = new Photo[2];
    private final PhotoView photoView;
    private final StackListener stackListener;

    private Photo source;
    private Runnable queuedTopFilterChange;
    private boolean topFilterOutputted;
    private volatile boolean paused;

    public FilterStack(PhotoView photoView, StackListener stackListener) {
        this.photoView = photoView;
        this.stackListener = stackListener;
    }

    private void reallocateBuffer(int target) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.reallocateBuffer(int)",this,target);try{int other = target ^ 1;
        buffers[target] = Photo.create(buffers[other].width(), buffers[other].height());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.reallocateBuffer(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.reallocateBuffer(int)",this,throwable);throw throwable;}
    }

    private void invalidate() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.invalidate()",this);try{/*// In/out buffers need redrawn by re-applying filters on source photo.*/
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i] != null) {
                buffers[i].clear();
                buffers[i] = null;
            }
        }
        if (source != null) {
            buffers[0] = Photo.create(source.width(), source.height());
            reallocateBuffer(1);

            /*// Source photo will be displayed if there is no filter stacked.*/
            Photo photo = source;
            int size = topFilterOutputted ? appliedStack.size() : appliedStack.size() - 1;
            for (int i = 0; i < size && !paused; i++) {
                photo = runFilter(i);
            }
            photoView.setPhoto(photo, topFilterOutputted);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.invalidate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.invalidate()",this,throwable);throw throwable;}
    }

    private void invalidateTopFilter() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.invalidateTopFilter()",this);try{if (!appliedStack.empty()) {
            photoView.setPhoto(runFilter(appliedStack.size() - 1), true);
            topFilterOutputted = true;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.invalidateTopFilter()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.invalidateTopFilter()",this,throwable);throw throwable;}
    }

    private Photo runFilter(int filterIndex) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.photoeditor.Photo com.android.gallery3d.photoeditor.FilterStack.runFilter(int)",this,filterIndex);try{int out = getOutBufferIndex(filterIndex);
        Photo input = (filterIndex > 0) ? buffers[out ^ 1] : source;
        if ((input != null) && (buffers[out] != null)) {
            if (!buffers[out].matchDimension(input)) {
                buffers[out].clear();
                reallocateBuffer(out);
            }
            appliedStack.get(filterIndex).process(input, buffers[out]);
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.photoeditor.Photo com.android.gallery3d.photoeditor.FilterStack.runFilter(int)",this);return buffers[out];}
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.photoeditor.Photo com.android.gallery3d.photoeditor.FilterStack.runFilter(int)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.photoeditor.Photo com.android.gallery3d.photoeditor.FilterStack.runFilter(int)",this,throwable);throw throwable;}
    }

    private int getOutBufferIndex(int filterIndex) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.photoeditor.FilterStack.getOutBufferIndex(int)",this,filterIndex);try{/*// buffers[0] and buffers[1] are swapped in turns as the in/out buffers for*/
        /*// processing stacked filters. For example, the first filter reads buffer[0] and*/
        /*// writes buffer[1]; the second filter then reads buffer[1] and writes buffer[0].*/
        /*// The returned index should only be used when the applied filter stack isn't empty.*/
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.photoeditor.FilterStack.getOutBufferIndex(int)",this);return (filterIndex + 1) % 2;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.photoeditor.FilterStack.getOutBufferIndex(int)",this,throwable);throw throwable;}
    }

    private void callbackDone(final OnDoneCallback callback) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.callbackDone(OnDoneCallback)",this,callback);try{/*// GL thread calls back to report UI thread the task is done.*/
        photoView.post(new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack$1.run()",this);try{callback.onDone();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack$1.run()",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.callbackDone(OnDoneCallback)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.callbackDone(OnDoneCallback)",this,throwable);throw throwable;}
    }

    private void stackChanged() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.stackChanged()",this);try{/*// GL thread calls back to report UI thread the stack is changed.*/
        final boolean canUndo = !appliedStack.empty();
        final boolean canRedo = !redoStack.empty();
        photoView.post(new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack$2.run()",this);try{stackListener.onStackChanged(canUndo, canRedo);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack$2.run()",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.stackChanged()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.stackChanged()",this,throwable);throw throwable;}
    }

    public void saveBitmap(final OnDoneBitmapCallback callback) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.saveBitmap(OnDoneBitmapCallback)",this,callback);try{photoView.queue(new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack$3.run()",this);try{int filterIndex = appliedStack.size() - (topFilterOutputted ? 1 : 2);
                Photo photo = (filterIndex < 0) ? source : buffers[getOutBufferIndex(filterIndex)];
                final Bitmap bitmap = (photo != null) ? photo.save() : null;
                photoView.post(new Runnable() {

                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack$3$1.run()",this);try{callback.onDone(bitmap);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack$3$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack$3$1.run()",this,throwable);throw throwable;}
                    }
                });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack$3.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack$3.run()",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.saveBitmap(OnDoneBitmapCallback)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.saveBitmap(OnDoneBitmapCallback)",this,throwable);throw throwable;}
    }

    public void setPhotoSource(final Bitmap bitmap, final OnDoneCallback callback) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.setPhotoSource(android.graphics.Bitmap,OnDoneCallback)",this,bitmap,callback);try{photoView.queue(new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack$4.run()",this);try{source = Photo.create(bitmap);
                invalidate();
                callbackDone(callback);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack$4.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack$4.run()",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.setPhotoSource(android.graphics.Bitmap,OnDoneCallback)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.setPhotoSource(android.graphics.Bitmap,OnDoneCallback)",this,throwable);throw throwable;}
    }

    private void pushFilterInternal(Filter filter) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.pushFilterInternal(com.android.gallery3d.photoeditor.filters.Filter)",this,filter);try{appliedStack.push(filter);
        topFilterOutputted = false;
        stackChanged();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.pushFilterInternal(com.android.gallery3d.photoeditor.filters.Filter)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.pushFilterInternal(com.android.gallery3d.photoeditor.filters.Filter)",this,throwable);throw throwable;}
    }

    public void pushFilter(final Filter filter) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.pushFilter(com.android.gallery3d.photoeditor.filters.Filter)",this,filter);try{photoView.queue(new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack$5.run()",this);try{while (!redoStack.empty()) {
                    redoStack.pop().release();
                }
                pushFilterInternal(filter);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack$5.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack$5.run()",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.pushFilter(com.android.gallery3d.photoeditor.filters.Filter)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.pushFilter(com.android.gallery3d.photoeditor.filters.Filter)",this,throwable);throw throwable;}
    }

    public void undo(final OnDoneCallback callback) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.undo(OnDoneCallback)",this,callback);try{photoView.queue(new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack$6.run()",this);try{if (!appliedStack.empty()) {
                    redoStack.push(appliedStack.pop());
                    stackChanged();
                    invalidate();
                }
                callbackDone(callback);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack$6.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack$6.run()",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.undo(OnDoneCallback)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.undo(OnDoneCallback)",this,throwable);throw throwable;}
    }

    public void redo(final OnDoneCallback callback) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.redo(OnDoneCallback)",this,callback);try{photoView.queue(new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack$7.run()",this);try{if (!redoStack.empty()) {
                    pushFilterInternal(redoStack.pop());
                    invalidateTopFilter();
                }
                callbackDone(callback);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack$7.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack$7.run()",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.redo(OnDoneCallback)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.redo(OnDoneCallback)",this,throwable);throw throwable;}
    }

    public void topFilterChanged(final OnDoneCallback callback) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.topFilterChanged(OnDoneCallback)",this,callback);try{/*// Remove the outdated top-filter change before queuing a new one.*/
        if (queuedTopFilterChange != null) {
            photoView.remove(queuedTopFilterChange);
        }
        queuedTopFilterChange = new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack$8.run()",this);try{invalidateTopFilter();
                callbackDone(callback);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack$8.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack$8.run()",this,throwable);throw throwable;}
            }
        };
        photoView.queue(queuedTopFilterChange);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.topFilterChanged(OnDoneCallback)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.topFilterChanged(OnDoneCallback)",this,throwable);throw throwable;}
    }

    public void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.onPause()",this);try{/*// Flush pending queued operations and release effect-context before GL context is lost.*/
        /*// Use the flag to break from lengthy invalidate() in GL thread for not blocking onPause().*/
        paused = true;
        photoView.flush();
        photoView.queueEvent(new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack$9.run()",this);try{Filter.releaseContext();
                /*// Textures will be automatically deleted when GL context is lost.*/
                photoView.setPhoto(null, false);
                source = null;
                for (int i = 0; i < buffers.length; i++) {
                    buffers[i] = null;
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack$9.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack$9.run()",this,throwable);throw throwable;}
            }
        });
        photoView.onPause();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.onPause()",this,throwable);throw throwable;}
    }

    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.FilterStack.onResume()",this);try{photoView.onResume();
        paused = false;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.FilterStack.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.FilterStack.onResume()",this,throwable);throw throwable;}
    }
}
