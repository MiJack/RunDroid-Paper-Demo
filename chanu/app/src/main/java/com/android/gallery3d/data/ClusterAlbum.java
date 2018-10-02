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

public class ClusterAlbum extends MediaSet implements ContentListener {
    private static final String TAG = "ClusterAlbum";
    private ArrayList<Path> mPaths = new ArrayList<Path>();
    private String mName = "";
    private DataManager mDataManager;
    private MediaSet mClusterAlbumSet;

    public ClusterAlbum(Path path, DataManager dataManager,
            MediaSet clusterAlbumSet) {
        super(path, nextVersionNumber());
        mDataManager = dataManager;
        mClusterAlbumSet = clusterAlbumSet;
        mClusterAlbumSet.addContentListener(this);
    }

    void setMediaItems(ArrayList<Path> paths) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ClusterAlbum.setMediaItems(java.util.ArrayList)",this,paths);try{mPaths = paths;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ClusterAlbum.setMediaItems(java.util.ArrayList)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ClusterAlbum.setMediaItems(java.util.ArrayList)",this,throwable);throw throwable;}
    }

    ArrayList<Path> getMediaItems() {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.data.ClusterAlbum.getMediaItems()",this);try{com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.ClusterAlbum.getMediaItems()",this);return mPaths;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.ClusterAlbum.getMediaItems()",this,throwable);throw throwable;}
    }

    public void setName(String name) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ClusterAlbum.setName(java.lang.String)",this,name);try{mName = name;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ClusterAlbum.setName(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ClusterAlbum.setName(java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.ClusterAlbum.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.ClusterAlbum.getName()",this);return mName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.ClusterAlbum.getName()",this,throwable);throw throwable;}
    }

    @Override
    public int getMediaItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.ClusterAlbum.getMediaItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.ClusterAlbum.getMediaItemCount()",this);return mPaths.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.ClusterAlbum.getMediaItemCount()",this,throwable);throw throwable;}
    }

    @Override
    public ArrayList<MediaItem> getMediaItem(int start, int count) {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.data.ClusterAlbum.getMediaItem(int,int)",this,start,count);try{com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.ClusterAlbum.getMediaItem(int,int)",this);return getMediaItemFromPath(mPaths, start, count, mDataManager);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.ClusterAlbum.getMediaItem(int,int)",this,throwable);throw throwable;}
    }

    public static ArrayList<MediaItem> getMediaItemFromPath(
            ArrayList<Path> paths, int start, int count,
            DataManager dataManager) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.ArrayList com.android.gallery3d.data.ClusterAlbum.getMediaItemFromPath(java.util.ArrayList,int,int,com.android.gallery3d.data.DataManager)",paths,start,count,dataManager);try{if (start >= paths.size()) {
            {com.mijack.Xlog.logStaticMethodExit("java.util.ArrayList com.android.gallery3d.data.ClusterAlbum.getMediaItemFromPath(java.util.ArrayList,int,int,com.android.gallery3d.data.DataManager)");return new ArrayList<MediaItem>();}
        }
        int end = Math.min(start + count, paths.size());
        ArrayList<Path> subset = new ArrayList<Path>(paths.subList(start, end));
        final MediaItem[] buf = new MediaItem[end - start];
        ItemConsumer consumer = new ItemConsumer() {
            public void consume(int index, MediaItem item) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ClusterAlbum$1.consume(int,com.android.gallery3d.data.MediaItem)",this,index,item);try{buf[index] = item;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ClusterAlbum$1.consume(int,com.android.gallery3d.data.MediaItem)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ClusterAlbum$1.consume(int,com.android.gallery3d.data.MediaItem)",this,throwable);throw throwable;}
            }
        };
        dataManager.mapMediaItems(subset, consumer, 0);
        ArrayList<MediaItem> result = new ArrayList<MediaItem>(end - start);
        for (int i = 0; i < buf.length; i++) {
            result.add(buf[i]);
        }
        {com.mijack.Xlog.logStaticMethodExit("java.util.ArrayList com.android.gallery3d.data.ClusterAlbum.getMediaItemFromPath(java.util.ArrayList,int,int,com.android.gallery3d.data.DataManager)");return result;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.ClusterAlbum.getMediaItemFromPath(java.util.ArrayList,int,int,com.android.gallery3d.data.DataManager)",throwable);throw throwable;}
    }

    @Override
    protected int enumerateMediaItems(ItemConsumer consumer, int startIndex) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.ClusterAlbum.enumerateMediaItems(ItemConsumer,int)",this,consumer,startIndex);try{mDataManager.mapMediaItems(mPaths, consumer, startIndex);
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.ClusterAlbum.enumerateMediaItems(ItemConsumer,int)",this);return mPaths.size();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.ClusterAlbum.enumerateMediaItems(ItemConsumer,int)",this,throwable);throw throwable;}
    }

    @Override
    public int getTotalMediaItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.ClusterAlbum.getTotalMediaItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.ClusterAlbum.getTotalMediaItemCount()",this);return mPaths.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.ClusterAlbum.getTotalMediaItemCount()",this,throwable);throw throwable;}
    }

    @Override
    public long reload() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.ClusterAlbum.reload()",this);try{if (mClusterAlbumSet.reload() > mDataVersion) {
            mDataVersion = nextVersionNumber();
        }
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.ClusterAlbum.reload()",this);return mDataVersion;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.ClusterAlbum.reload()",this,throwable);throw throwable;}
    }

    public void onContentDirty() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ClusterAlbum.onContentDirty()",this);try{notifyContentChanged();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ClusterAlbum.onContentDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ClusterAlbum.onContentDirty()",this,throwable);throw throwable;}
    }

    @Override
    public int getSupportedOperations() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.ClusterAlbum.getSupportedOperations()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.ClusterAlbum.getSupportedOperations()",this);return SUPPORT_SHARE | SUPPORT_DELETE | SUPPORT_INFO;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.ClusterAlbum.getSupportedOperations()",this,throwable);throw throwable;}
    }

    @Override
    public void delete() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ClusterAlbum.delete()",this);try{ItemConsumer consumer = new ItemConsumer() {
            public void consume(int index, MediaItem item) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ClusterAlbum$2.consume(int,com.android.gallery3d.data.MediaItem)",this,index,item);try{if ((item.getSupportedOperations() & SUPPORT_DELETE) != 0) {
                    item.delete();
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ClusterAlbum$2.consume(int,com.android.gallery3d.data.MediaItem)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ClusterAlbum$2.consume(int,com.android.gallery3d.data.MediaItem)",this,throwable);throw throwable;}
            }
        };
        mDataManager.mapMediaItems(mPaths, consumer, 0);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ClusterAlbum.delete()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ClusterAlbum.delete()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isLeafAlbum() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.ClusterAlbum.isLeafAlbum()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.ClusterAlbum.isLeafAlbum()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.ClusterAlbum.isLeafAlbum()",this,throwable);throw throwable;}
    }
}
