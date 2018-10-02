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

package com.android.gallery3d.data;

import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.Future;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;

/*// MediaSet is a directory-like data structure.*/
/*// It contains MediaItems and sub-MediaSets.*/
/*//*/
/*// The primary interface are:*/
/*// getMediaItemCount(), getMediaItem() and*/
/*// getSubMediaSetCount(), getSubMediaSet().*/
/*//*/
/*// getTotalMediaItemCount() returns the number of all MediaItems, including*/
/*// those in sub-MediaSets.*/
public abstract class MediaSet extends MediaObject {
    public static final int MEDIAITEM_BATCH_FETCH_COUNT = 500;
    public static final int INDEX_NOT_FOUND = -1;

    public static final int SYNC_RESULT_SUCCESS = 0;
    public static final int SYNC_RESULT_CANCELLED = 1;
    public static final int SYNC_RESULT_ERROR = 2;

    /** Listener to be used with requestSync(SyncListener). */
    public static interface SyncListener {
        /**
         * Called when the sync task completed. Completion may be due to normal termination,
         * an exception, or cancellation.
         *
         * @param mediaSet the MediaSet that's done with sync
         * @param resultCode one of the SYNC_RESULT_* constants
         */
        void onSyncDone(MediaSet mediaSet, int resultCode);
    }

    public MediaSet(Path path, long version) {
        super(path, version);
    }

    public int getMediaItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaSet.getMediaItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaSet.getMediaItemCount()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaSet.getMediaItemCount()",this,throwable);throw throwable;}
    }

    /*// Returns the media items in the range [start, start + count).*/
    /*//*/
    /*// The number of media items returned may be less than the specified count*/
    /*// if there are not enough media items available. The number of*/
    /*// media items available may not be consistent with the return value of*/
    /*// getMediaItemCount() because the contents of database may have already*/
    /*// changed.*/
    public ArrayList<MediaItem> getMediaItem(int start, int count) {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.data.MediaSet.getMediaItem(int,int)",this,start,count);try{com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.MediaSet.getMediaItem(int,int)",this);return new ArrayList<MediaItem>();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.MediaSet.getMediaItem(int,int)",this,throwable);throw throwable;}
    }

    public int getSubMediaSetCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaSet.getSubMediaSetCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaSet.getSubMediaSetCount()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaSet.getSubMediaSetCount()",this,throwable);throw throwable;}
    }

    public MediaSet getSubMediaSet(int index) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.MediaSet.getSubMediaSet(int)",this,index);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.MediaSet.getSubMediaSet(int)",this);throw new IndexOutOfBoundsException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.MediaSet.getSubMediaSet(int)",this,throwable);throw throwable;}
    }

    public boolean isLeafAlbum() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.MediaSet.isLeafAlbum()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MediaSet.isLeafAlbum()",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.MediaSet.isLeafAlbum()",this,throwable);throw throwable;}
    }

    public int getTotalMediaItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaSet.getTotalMediaItemCount()",this);try{int total = getMediaItemCount();
        for (int i = 0, n = getSubMediaSetCount(); i < n; i++) {
            total += getSubMediaSet(i).getTotalMediaItemCount();
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaSet.getTotalMediaItemCount()",this);return total;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaSet.getTotalMediaItemCount()",this,throwable);throw throwable;}
    }

    /*// TODO: we should have better implementation of sub classes*/
    public int getIndexOfItem(Path path, int hint) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaSet.getIndexOfItem(com.android.gallery3d.data.Path,int)",this,path,hint);try{/*// hint < 0 is handled below*/
        /*// first, try to find it around the hint*/
        int start = Math.max(0,
                hint - MEDIAITEM_BATCH_FETCH_COUNT / 2);
        ArrayList<MediaItem> list = getMediaItem(
                start, MEDIAITEM_BATCH_FETCH_COUNT);
        int index = getIndexOf(path, list);
        if (index != INDEX_NOT_FOUND) {{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaSet.getIndexOfItem(com.android.gallery3d.data.Path,int)",this);return start + index;}}

        /*// try to find it globally*/
        start = start == 0 ? MEDIAITEM_BATCH_FETCH_COUNT : 0;
        list = getMediaItem(start, MEDIAITEM_BATCH_FETCH_COUNT);
        while (true) {
            index = getIndexOf(path, list);
            if (index != INDEX_NOT_FOUND) {{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaSet.getIndexOfItem(com.android.gallery3d.data.Path,int)",this);return start + index;}}
            if (list.size() < MEDIAITEM_BATCH_FETCH_COUNT) {{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaSet.getIndexOfItem(com.android.gallery3d.data.Path,int)",this);return INDEX_NOT_FOUND;}}
            start += MEDIAITEM_BATCH_FETCH_COUNT;
            list = getMediaItem(start, MEDIAITEM_BATCH_FETCH_COUNT);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaSet.getIndexOfItem(com.android.gallery3d.data.Path,int)",this,throwable);throw throwable;}
    }

    protected int getIndexOf(Path path, ArrayList<MediaItem> list) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaSet.getIndexOf(com.android.gallery3d.data.Path,java.util.ArrayList)",this,path,list);try{for (int i = 0, n = list.size(); i < n; ++i) {
            if (list.get(i).mPath.equals(path)) {{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaSet.getIndexOf(com.android.gallery3d.data.Path,java.util.ArrayList)",this);return i;}}
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaSet.getIndexOf(com.android.gallery3d.data.Path,java.util.ArrayList)",this);return INDEX_NOT_FOUND;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaSet.getIndexOf(com.android.gallery3d.data.Path,java.util.ArrayList)",this,throwable);throw throwable;}
    }

    public abstract String getName();

    private WeakHashMap<ContentListener, Object> mListeners =
            new WeakHashMap<ContentListener, Object>();

    /*// NOTE: The MediaSet only keeps a weak reference to the listener. The*/
    /*// listener is automatically removed when there is no other reference to*/
    /*// the listener.*/
    public void addContentListener(ContentListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSet.addContentListener(ContentListener)",this,listener);try{if (mListeners.containsKey(listener)) {
            throw new IllegalArgumentException();
        }
        mListeners.put(listener, null);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaSet.addContentListener(ContentListener)",this,throwable);throw throwable;}
    }

    public void removeContentListener(ContentListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSet.removeContentListener(ContentListener)",this,listener);try{if (!mListeners.containsKey(listener)) {
            throw new IllegalArgumentException();
        }
        mListeners.remove(listener);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaSet.removeContentListener(ContentListener)",this,throwable);throw throwable;}
    }

    /*// This should be called by subclasses when the content is changed.*/
    public void notifyContentChanged() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSet.notifyContentChanged()",this);try{for (ContentListener listener : mListeners.keySet()) {
            listener.onContentDirty();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSet.notifyContentChanged()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaSet.notifyContentChanged()",this,throwable);throw throwable;}
    }

    /*// Reload the content. Return the current data version. reload() should be called*/
    /*// in the same thread as getMediaItem(int, int) and getSubMediaSet(int).*/
    public abstract long reload();

    @Override
    public MediaDetails getDetails() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.MediaSet.getDetails()",this);try{MediaDetails details = super.getDetails();
        details.addDetail(MediaDetails.INDEX_TITLE, getName());
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.MediaSet.getDetails()",this);return details;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.MediaSet.getDetails()",this,throwable);throw throwable;}
    }

    /*// Enumerate all media items in this media set (including the ones in sub*/
    /*// media sets), in an efficient order. ItemConsumer.consumer() will be*/
    /*// called for each media item with its index.*/
    public void enumerateMediaItems(ItemConsumer consumer) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSet.enumerateMediaItems(ItemConsumer)",this,consumer);try{enumerateMediaItems(consumer, 0);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSet.enumerateMediaItems(ItemConsumer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaSet.enumerateMediaItems(ItemConsumer)",this,throwable);throw throwable;}
    }

    public void enumerateTotalMediaItems(ItemConsumer consumer) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSet.enumerateTotalMediaItems(ItemConsumer)",this,consumer);try{enumerateTotalMediaItems(consumer, 0);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSet.enumerateTotalMediaItems(ItemConsumer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaSet.enumerateTotalMediaItems(ItemConsumer)",this,throwable);throw throwable;}
    }

    public static interface ItemConsumer {
        void consume(int index, MediaItem item);
    }

    /*// The default implementation uses getMediaItem() for enumerateMediaItems().*/
    /*// Subclasses may override this and use more efficient implementations.*/
    /*// Returns the number of items enumerated.*/
    protected int enumerateMediaItems(ItemConsumer consumer, int startIndex) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaSet.enumerateMediaItems(ItemConsumer,int)",this,consumer,startIndex);try{int total = getMediaItemCount();
        int start = 0;
        while (start < total) {
            int count = Math.min(MEDIAITEM_BATCH_FETCH_COUNT, total - start);
            ArrayList<MediaItem> items = getMediaItem(start, count);
            for (int i = 0, n = items.size(); i < n; i++) {
                MediaItem item = items.get(i);
                consumer.consume(startIndex + start + i, item);
            }
            start += count;
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaSet.enumerateMediaItems(ItemConsumer,int)",this);return total;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaSet.enumerateMediaItems(ItemConsumer,int)",this,throwable);throw throwable;}
    }

    /*// Recursively enumerate all media items under this set.*/
    /*// Returns the number of items enumerated.*/
    protected int enumerateTotalMediaItems(
            ItemConsumer consumer, int startIndex) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaSet.enumerateTotalMediaItems(ItemConsumer,int)",this,consumer,startIndex);try{int start = 0;
        start += enumerateMediaItems(consumer, startIndex);
        int m = getSubMediaSetCount();
        for (int i = 0; i < m; i++) {
            start += getSubMediaSet(i).enumerateTotalMediaItems(
                    consumer, startIndex + start);
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaSet.enumerateTotalMediaItems(ItemConsumer,int)",this);return start;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaSet.enumerateTotalMediaItems(ItemConsumer,int)",this,throwable);throw throwable;}
    }

    /**
     * Requests sync on this MediaSet. It returns a Future object that can be used by the caller
     * to query the status of the sync. The sync result code is one of the SYNC_RESULT_* constants
     * defined in this class and can be obtained by Future.get().
     *
     * Subclasses should perform sync on a different thread.
     *
     * The default implementation here returns a Future stub that does nothing and returns
     * SYNC_RESULT_SUCCESS by get().
     */
    public Future<Integer> requestSync(SyncListener listener) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.Future com.android.gallery3d.data.MediaSet.requestSync(SyncListener)",this,listener);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.data.MediaSet.requestSync(SyncListener)",this);return FUTURE_STUB;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.Future com.android.gallery3d.data.MediaSet.requestSync(SyncListener)",this,throwable);throw throwable;}
    }

    private static final Future<Integer> FUTURE_STUB = new Future<Integer>() {
        {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSet$1.cancel()",this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSet$1.cancel()",this);}

        @Override
        public boolean isCancelled() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.MediaSet$1.isCancelled()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MediaSet$1.isCancelled()",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.MediaSet$1.isCancelled()",this,throwable);throw throwable;}
        }

        @Override
        public boolean isDone() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.MediaSet$1.isDone()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MediaSet$1.isDone()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.MediaSet$1.isDone()",this,throwable);throw throwable;}
        }

        @Override
        public Integer get() {
            com.mijack.Xlog.logMethodEnter("java.lang.Integer com.android.gallery3d.data.MediaSet$1.get()",this);try{com.mijack.Xlog.logMethodExit("java.lang.Integer com.android.gallery3d.data.MediaSet$1.get()",this);return SYNC_RESULT_SUCCESS;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Integer com.android.gallery3d.data.MediaSet$1.get()",this,throwable);throw throwable;}
        }

        {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSet$1.waitDone()",this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSet$1.waitDone()",this);}
    };

    protected Future<Integer> requestSyncOnEmptySets(MediaSet[] sets, SyncListener listener) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.Future com.android.gallery3d.data.MediaSet.requestSyncOnEmptySets([com.android.gallery3d.data.MediaSet,SyncListener)",this,sets,listener);try{MultiSetSyncFuture future = new MultiSetSyncFuture(listener);
        future.requestSyncOnEmptySets(sets);
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.data.MediaSet.requestSyncOnEmptySets([com.android.gallery3d.data.MediaSet,SyncListener)",this);return future;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.Future com.android.gallery3d.data.MediaSet.requestSyncOnEmptySets([com.android.gallery3d.data.MediaSet,SyncListener)",this,throwable);throw throwable;}
    }

    private class MultiSetSyncFuture implements Future<Integer>, SyncListener {
        private static final String TAG = "Gallery.MultiSetSync";

        private final HashMap<MediaSet, Future<Integer>> mMediaSetMap =
                new HashMap<MediaSet, Future<Integer>>();
        private final SyncListener mListener;

        private boolean mIsCancelled = false;
        private int mResult = -1;

        MultiSetSyncFuture(SyncListener listener) {
            mListener = listener;
        }

        synchronized void requestSyncOnEmptySets(MediaSet[] sets) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.requestSyncOnEmptySets([com.android.gallery3d.data.MediaSet)",this,sets);try{for (MediaSet set : sets) {
                if ((set.getMediaItemCount() == 0) && !mMediaSetMap.containsKey(set)) {
                    /*// Sync results are handled in this.onSyncDone().*/
                    Future<Integer> future = set.requestSync(this);
                    if (!future.isDone()) {
                        mMediaSetMap.put(set, future);
                        Log.d(TAG, "  request sync: " + Utils.maskDebugInfo(set.getName()));
                    }
                }
            }
            Log.d(TAG, "requestSyncOnEmptySets actual=" + mMediaSetMap.size());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.requestSyncOnEmptySets([com.android.gallery3d.data.MediaSet)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.requestSyncOnEmptySets([com.android.gallery3d.data.MediaSet)",this,throwable);throw throwable;}
        }

        @Override
        public synchronized void cancel() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.cancel()",this);try{if (mIsCancelled) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.cancel()",this);return;}}
            mIsCancelled = true;
            for (Future<Integer> future : mMediaSetMap.values()) {future.cancel();}
            mMediaSetMap.clear();
            if (mResult < 0) {mResult = SYNC_RESULT_CANCELLED;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.cancel()",this,throwable);throw throwable;}
        }

        @Override
        public synchronized boolean isCancelled() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.isCancelled()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.isCancelled()",this);return mIsCancelled;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.isCancelled()",this,throwable);throw throwable;}
        }

        @Override
        public synchronized boolean isDone() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.isDone()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.isDone()",this);return mMediaSetMap.isEmpty();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.isDone()",this,throwable);throw throwable;}
        }

        @Override
        public synchronized Integer get() {
            com.mijack.Xlog.logMethodEnter("java.lang.Integer com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.get()",this);try{waitDone();
            {com.mijack.Xlog.logMethodExit("java.lang.Integer com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.get()",this);return mResult;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Integer com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.get()",this,throwable);throw throwable;}
        }

        @Override
        public synchronized void waitDone() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.waitDone()",this);try{try {
                while (!isDone()) {wait();}
            } catch (InterruptedException e) {
                Log.d(TAG, "waitDone() interrupted");
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.waitDone()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.waitDone()",this,throwable);throw throwable;}
        }

        /*// SyncListener callback*/
        @Override
        public void onSyncDone(MediaSet mediaSet, int resultCode) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.onSyncDone(com.android.gallery3d.data.MediaSet,int)",this,mediaSet,resultCode);try{SyncListener listener = null;
            synchronized (this) {
                if (mMediaSetMap.remove(mediaSet) != null) {
                    Log.d(TAG, "onSyncDone: " + Utils.maskDebugInfo(mediaSet.getName())
                            + " #pending=" + mMediaSetMap.size());
                    if (resultCode == SYNC_RESULT_ERROR) {
                        mResult = SYNC_RESULT_ERROR;
                    }
                    if (mMediaSetMap.isEmpty()) {
                        if (mResult < 0) {mResult = SYNC_RESULT_SUCCESS;}
                        notifyAll();
                        listener = mListener;
                    }
                }
            }
            if (listener != null) {listener.onSyncDone(MediaSet.this, mResult);}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.onSyncDone(com.android.gallery3d.data.MediaSet,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaSet$MultiSetSyncFuture.onSyncDone(com.android.gallery3d.data.MediaSet,int)",this,throwable);throw throwable;}
        }
    }
}
