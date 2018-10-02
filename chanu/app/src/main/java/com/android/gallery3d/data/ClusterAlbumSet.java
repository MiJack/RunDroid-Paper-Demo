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

import com.android.gallery3d.app.GalleryApp;

import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashSet;

public class ClusterAlbumSet extends MediaSet implements ContentListener {
    private static final String TAG = "ClusterAlbumSet";
    private GalleryApp mApplication;
    private MediaSet mBaseSet;
    private int mKind;
    private ArrayList<ClusterAlbum> mAlbums = new ArrayList<ClusterAlbum>();
    private boolean mFirstReloadDone;

    public ClusterAlbumSet(Path path, GalleryApp application,
            MediaSet baseSet, int kind) {
        super(path, INVALID_DATA_VERSION);
        mApplication = application;
        mBaseSet = baseSet;
        mKind = kind;
        baseSet.addContentListener(this);
    }

    @Override
    public MediaSet getSubMediaSet(int index) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.ClusterAlbumSet.getSubMediaSet(int)",this,index);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.ClusterAlbumSet.getSubMediaSet(int)",this);return mAlbums.get(index);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.ClusterAlbumSet.getSubMediaSet(int)",this,throwable);throw throwable;}
    }

    @Override
    public int getSubMediaSetCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.ClusterAlbumSet.getSubMediaSetCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.ClusterAlbumSet.getSubMediaSetCount()",this);return mAlbums.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.ClusterAlbumSet.getSubMediaSetCount()",this,throwable);throw throwable;}
    }

    @Override
    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.ClusterAlbumSet.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.ClusterAlbumSet.getName()",this);return mBaseSet.getName();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.ClusterAlbumSet.getName()",this,throwable);throw throwable;}
    }

    @Override
    public long reload() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.ClusterAlbumSet.reload()",this);try{if (mBaseSet.reload() > mDataVersion) {
            if (mFirstReloadDone) {
                updateClustersContents();
            } else {
                updateClusters();
                mFirstReloadDone = true;
            }
            mDataVersion = nextVersionNumber();
        }
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.ClusterAlbumSet.reload()",this);return mDataVersion;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.ClusterAlbumSet.reload()",this,throwable);throw throwable;}
    }

    public void onContentDirty() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ClusterAlbumSet.onContentDirty()",this);try{notifyContentChanged();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ClusterAlbumSet.onContentDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ClusterAlbumSet.onContentDirty()",this,throwable);throw throwable;}
    }

    private void updateClusters() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ClusterAlbumSet.updateClusters()",this);try{mAlbums.clear();
        Clustering clustering;
        Context context = mApplication.getAndroidContext();
        switch (mKind) {
            case ClusterSource.CLUSTER_ALBUMSET_TIME:
                clustering = new TimeClustering(context);
                break;
            case ClusterSource.CLUSTER_ALBUMSET_LOCATION:
                clustering = new LocationClustering(context);
                break;
            case ClusterSource.CLUSTER_ALBUMSET_TAG:
                clustering = new TagClustering(context);
                break;
            case ClusterSource.CLUSTER_ALBUMSET_FACE:
                clustering = new FaceClustering(context);
                break;
            default: /* CLUSTER_ALBUMSET_SIZE */
                clustering = new SizeClustering(context);
                break;
        }

        clustering.run(mBaseSet);
        int n = clustering.getNumberOfClusters();
        DataManager dataManager = mApplication.getDataManager();
        for (int i = 0; i < n; i++) {
            Path childPath;
            String childName = clustering.getClusterName(i);
            if (mKind == ClusterSource.CLUSTER_ALBUMSET_TAG) {
                childPath = mPath.getChild(Uri.encode(childName));
            } else if (mKind == ClusterSource.CLUSTER_ALBUMSET_SIZE) {
                long minSize = ((SizeClustering) clustering).getMinSize(i);
                childPath = mPath.getChild(minSize);
            } else {
                childPath = mPath.getChild(i);
            }
            ClusterAlbum album = (ClusterAlbum) dataManager.peekMediaObject(
                        childPath);
            if (album == null) {
                album = new ClusterAlbum(childPath, dataManager, this);
            }
            album.setMediaItems(clustering.getCluster(i));
            album.setName(childName);
            mAlbums.add(album);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ClusterAlbumSet.updateClusters()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ClusterAlbumSet.updateClusters()",this,throwable);throw throwable;}
    }

    private void updateClustersContents() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ClusterAlbumSet.updateClustersContents()",this);try{final HashSet<Path> existing = new HashSet<Path>();
        mBaseSet.enumerateTotalMediaItems(new MediaSet.ItemConsumer() {
            public void consume(int index, MediaItem item) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ClusterAlbumSet$1.consume(int,com.android.gallery3d.data.MediaItem)",this,index,item);try{existing.add(item.getPath());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ClusterAlbumSet$1.consume(int,com.android.gallery3d.data.MediaItem)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ClusterAlbumSet$1.consume(int,com.android.gallery3d.data.MediaItem)",this,throwable);throw throwable;}
            }
        });

        int n = mAlbums.size();

        /*// The loop goes backwards because we may remove empty albums from*/
        /*// mAlbums.*/
        for (int i = n - 1; i >= 0; i--) {
            ArrayList<Path> oldPaths = mAlbums.get(i).getMediaItems();
            ArrayList<Path> newPaths = new ArrayList<Path>();
            int m = oldPaths.size();
            for (int j = 0; j < m; j++) {
                Path p = oldPaths.get(j);
                if (existing.contains(p)) {
                    newPaths.add(p);
                }
            }
            mAlbums.get(i).setMediaItems(newPaths);
            if (newPaths.isEmpty()) {
                mAlbums.remove(i);
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ClusterAlbumSet.updateClustersContents()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ClusterAlbumSet.updateClustersContents()",this,throwable);throw throwable;}
    }
}
