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

import java.util.ArrayList;

/*// FilterSet filters a base MediaSet according to a condition. Currently the*/
/*// condition is a matching media type. It can be extended to other conditions*/
/*// if needed.*/
public class FilterSet extends MediaSet implements ContentListener {
    @SuppressWarnings("unused")
    private static final String TAG = "FilterSet";

    private final DataManager mDataManager;
    private final MediaSet mBaseSet;
    private final int mMediaType;
    private final ArrayList<Path> mPaths = new ArrayList<Path>();
    private final ArrayList<MediaSet> mAlbums = new ArrayList<MediaSet>();

    public FilterSet(Path path, DataManager dataManager, MediaSet baseSet,
            int mediaType) {
        super(path, INVALID_DATA_VERSION);
        mDataManager = dataManager;
        mBaseSet = baseSet;
        mMediaType = mediaType;
        mBaseSet.addContentListener(this);
    }

    @Override
    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.FilterSet.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.FilterSet.getName()",this);return mBaseSet.getName();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.FilterSet.getName()",this,throwable);throw throwable;}
    }

    @Override
    public MediaSet getSubMediaSet(int index) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.FilterSet.getSubMediaSet(int)",this,index);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.FilterSet.getSubMediaSet(int)",this);return mAlbums.get(index);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.FilterSet.getSubMediaSet(int)",this,throwable);throw throwable;}
    }

    @Override
    public int getSubMediaSetCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.FilterSet.getSubMediaSetCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.FilterSet.getSubMediaSetCount()",this);return mAlbums.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.FilterSet.getSubMediaSetCount()",this,throwable);throw throwable;}
    }

    @Override
    public int getMediaItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.FilterSet.getMediaItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.FilterSet.getMediaItemCount()",this);return mPaths.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.FilterSet.getMediaItemCount()",this,throwable);throw throwable;}
    }

    @Override
    public ArrayList<MediaItem> getMediaItem(int start, int count) {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.data.FilterSet.getMediaItem(int,int)",this,start,count);try{com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.FilterSet.getMediaItem(int,int)",this);return ClusterAlbum.getMediaItemFromPath(
                mPaths, start, count, mDataManager);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.FilterSet.getMediaItem(int,int)",this,throwable);throw throwable;}
    }

    @Override
    public long reload() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.FilterSet.reload()",this);try{if (mBaseSet.reload() > mDataVersion) {
            updateData();
            mDataVersion = nextVersionNumber();
        }
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.FilterSet.reload()",this);return mDataVersion;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.FilterSet.reload()",this,throwable);throw throwable;}
    }

    @Override
    public void onContentDirty() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.FilterSet.onContentDirty()",this);try{notifyContentChanged();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.FilterSet.onContentDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.FilterSet.onContentDirty()",this,throwable);throw throwable;}
    }

    private void updateData() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.FilterSet.updateData()",this);try{/*// Albums*/
        mAlbums.clear();
        String basePath = "/filter/mediatype/" + mMediaType;

        for (int i = 0, n = mBaseSet.getSubMediaSetCount(); i < n; i++) {
            MediaSet set = mBaseSet.getSubMediaSet(i);
            String filteredPath = basePath + "/{" + set.getPath().toString() + "}";
            MediaSet filteredSet = mDataManager.getMediaSet(filteredPath);
            filteredSet.reload();
            if (filteredSet.getMediaItemCount() > 0
                    || filteredSet.getSubMediaSetCount() > 0) {
                mAlbums.add(filteredSet);
            }
        }

        /*// Items*/
        mPaths.clear();
        final int total = mBaseSet.getMediaItemCount();
        final Path[] buf = new Path[total];

        mBaseSet.enumerateMediaItems(new MediaSet.ItemConsumer() {
            public void consume(int index, MediaItem item) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.FilterSet$1.consume(int,com.android.gallery3d.data.MediaItem)",this,index,item);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.FilterSet$1.consume(int,com.android.gallery3d.data.MediaItem)",this);if (item.getMediaType() == mMediaType) {
                    if (index < 0 || index >= total) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.FilterSet.updateData()",this);return;}}
                    Path path = item.getPath();
                    buf[index] = path;
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.FilterSet$1.consume(int,com.android.gallery3d.data.MediaItem)",this,throwable);throw throwable;}
            }
        });

        for (int i = 0; i < total; i++) {
            if (buf[i] != null) {
                mPaths.add(buf[i]);
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.FilterSet.updateData()",this,throwable);throw throwable;}
    }

    @Override
    public int getSupportedOperations() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.FilterSet.getSupportedOperations()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.FilterSet.getSupportedOperations()",this);return SUPPORT_SHARE | SUPPORT_DELETE;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.FilterSet.getSupportedOperations()",this,throwable);throw throwable;}
    }

    @Override
    public void delete() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.FilterSet.delete()",this);try{ItemConsumer consumer = new ItemConsumer() {
            public void consume(int index, MediaItem item) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.FilterSet$2.consume(int,com.android.gallery3d.data.MediaItem)",this,index,item);try{if ((item.getSupportedOperations() & SUPPORT_DELETE) != 0) {
                    item.delete();
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.FilterSet$2.consume(int,com.android.gallery3d.data.MediaItem)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.FilterSet$2.consume(int,com.android.gallery3d.data.MediaItem)",this,throwable);throw throwable;}
            }
        };
        mDataManager.mapMediaItems(mPaths, consumer, 0);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.FilterSet.delete()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.FilterSet.delete()",this,throwable);throw throwable;}
    }
}
