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

import com.chanapps.four.gallery3d.R;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class TagClustering extends Clustering {
    @SuppressWarnings("unused")
    private static final String TAG = "TagClustering";

    private ArrayList<ArrayList<Path>> mClusters;
    private String[] mNames;
    private String mUntaggedString;

    public TagClustering(Context context) {
        mUntaggedString = context.getResources().getString(R.string.untagged);
    }

    @Override
    public void run(MediaSet baseSet) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.TagClustering.run(com.android.gallery3d.data.MediaSet)",this,baseSet);try{final TreeMap<String, ArrayList<Path>> map =
                new TreeMap<String, ArrayList<Path>>();
        final ArrayList<Path> untagged = new ArrayList<Path>();

        baseSet.enumerateTotalMediaItems(new MediaSet.ItemConsumer() {
            public void consume(int index, MediaItem item) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.TagClustering$1.consume(int,com.android.gallery3d.data.MediaItem)",this,index,item);try{Path path = item.getPath();

                String[] tags = item.getTags();
                if (tags == null || tags.length == 0) {
                    untagged.add(path);
                    {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.TagClustering.run(com.android.gallery3d.data.MediaSet)",this);{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.TagClustering$1.consume(int,com.android.gallery3d.data.MediaItem)",this);return;}}
                }
                for (int j = 0; j < tags.length; j++) {
                    String key = tags[j];
                    ArrayList<Path> list = map.get(key);
                    if (list == null) {
                        list = new ArrayList<Path>();
                        map.put(key, list);
                    }
                    list.add(path);
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.TagClustering$1.consume(int,com.android.gallery3d.data.MediaItem)",this,throwable);throw throwable;}
            }
        });

        int m = map.size();
        mClusters = new ArrayList<ArrayList<Path>>();
        mNames = new String[m + ((untagged.size() > 0) ? 1 : 0)];
        int i = 0;
        for (Map.Entry<String, ArrayList<Path>> entry : map.entrySet()) {
            mNames[i++] = entry.getKey();
            mClusters.add(entry.getValue());
        }
        if (untagged.size() > 0) {
            mNames[i++] = mUntaggedString;
            mClusters.add(untagged);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.TagClustering.run(com.android.gallery3d.data.MediaSet)",this,throwable);throw throwable;}
    }

    @Override
    public int getNumberOfClusters() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.TagClustering.getNumberOfClusters()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.TagClustering.getNumberOfClusters()",this);return mClusters.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.TagClustering.getNumberOfClusters()",this,throwable);throw throwable;}
    }

    @Override
    public ArrayList<Path> getCluster(int index) {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.data.TagClustering.getCluster(int)",this,index);try{com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.TagClustering.getCluster(int)",this);return mClusters.get(index);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.TagClustering.getCluster(int)",this,throwable);throw throwable;}
    }

    @Override
    public String getClusterName(int index) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.TagClustering.getClusterName(int)",this,index);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.TagClustering.getClusterName(int)",this);return mNames[index];}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.TagClustering.getClusterName(int)",this,throwable);throw throwable;}
    }
}
