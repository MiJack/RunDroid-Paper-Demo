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
import android.content.res.Resources;

import java.util.ArrayList;

public class SizeClustering extends Clustering {
    private static final String TAG = "SizeClustering";

    private Context mContext;
    private ArrayList<Path>[] mClusters;
    private String[] mNames;
    private long mMinSizes[];

    private static final long MEGA_BYTES = 1024L*1024;
    private static final long GIGA_BYTES = 1024L*1024*1024;

    private static final long[] SIZE_LEVELS = {
        0,
        1 * MEGA_BYTES,
        10 * MEGA_BYTES,
        100 * MEGA_BYTES,
        1 * GIGA_BYTES,
        2 * GIGA_BYTES,
        4 * GIGA_BYTES,
    };

    public SizeClustering(Context context) {
        mContext = context;
    }

    @Override
    public void run(MediaSet baseSet) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.SizeClustering.run(com.android.gallery3d.data.MediaSet)",this,baseSet);try{final ArrayList<Path>[] group =
                (ArrayList<Path>[]) new ArrayList[SIZE_LEVELS.length];
        baseSet.enumerateTotalMediaItems(new MediaSet.ItemConsumer() {
            public void consume(int index, MediaItem item) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.SizeClustering$1.consume(int,com.android.gallery3d.data.MediaItem)",this,index,item);try{/*// Find the cluster this item belongs to.*/
                long size = item.getSize();
                int i;
                for (i = 0; i < SIZE_LEVELS.length - 1; i++) {
                    if (size < SIZE_LEVELS[i + 1]) {
                        break;
                    }
                }

                ArrayList<Path> list = group[i];
                if (list == null) {
                    list = new ArrayList<Path>();
                    group[i] = list;
                }
                list.add(item.getPath());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.SizeClustering$1.consume(int,com.android.gallery3d.data.MediaItem)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.SizeClustering$1.consume(int,com.android.gallery3d.data.MediaItem)",this,throwable);throw throwable;}
            }
        });

        int count = 0;
        for (int i = 0; i < group.length; i++) {
            if (group[i] != null) {
                count++;
            }
        }

        mClusters = (ArrayList<Path>[]) new ArrayList[count];
        mNames = new String[count];
        mMinSizes = new long[count];

        Resources res = mContext.getResources();
        int k = 0;
        /*// Go through group in the reverse order, so the group with the largest*/
        /*// size will show first.*/
        for (int i = group.length - 1; i >= 0; i--) {
            if (group[i] == null) {continue;}

            mClusters[k] = group[i];
            if (i == 0) {
                mNames[k] = String.format(
                        res.getString(R.string.size_below), getSizeString(i + 1));
            } else if (i == group.length - 1) {
                mNames[k] = String.format(
                        res.getString(R.string.size_above), getSizeString(i));
            } else {
                String minSize = getSizeString(i);
                String maxSize = getSizeString(i + 1);
                mNames[k] = String.format(
                        res.getString(R.string.size_between), minSize, maxSize);
            }
            mMinSizes[k] = SIZE_LEVELS[i];
            k++;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.SizeClustering.run(com.android.gallery3d.data.MediaSet)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.SizeClustering.run(com.android.gallery3d.data.MediaSet)",this,throwable);throw throwable;}
    }

    private String getSizeString(int index) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.SizeClustering.getSizeString(int)",this,index);try{long bytes = SIZE_LEVELS[index];
        if (bytes >= GIGA_BYTES) {
            {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.SizeClustering.getSizeString(int)",this);return (bytes / GIGA_BYTES) + "GB";}
        } else {
            {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.SizeClustering.getSizeString(int)",this);return (bytes / MEGA_BYTES) + "MB";}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.SizeClustering.getSizeString(int)",this,throwable);throw throwable;}
    }

    @Override
    public int getNumberOfClusters() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.SizeClustering.getNumberOfClusters()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.SizeClustering.getNumberOfClusters()",this);return mClusters.length;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.SizeClustering.getNumberOfClusters()",this,throwable);throw throwable;}
    }

    @Override
    public ArrayList<Path> getCluster(int index) {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.data.SizeClustering.getCluster(int)",this,index);try{com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.SizeClustering.getCluster(int)",this);return mClusters[index];}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.SizeClustering.getCluster(int)",this,throwable);throw throwable;}
    }

    @Override
    public String getClusterName(int index) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.SizeClustering.getClusterName(int)",this,index);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.SizeClustering.getClusterName(int)",this);return mNames[index];}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.SizeClustering.getClusterName(int)",this,throwable);throw throwable;}
    }

    public long getMinSize(int index) {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.SizeClustering.getMinSize(int)",this,index);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.SizeClustering.getMinSize(int)",this);return mMinSizes[index];}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.SizeClustering.getMinSize(int)",this,throwable);throw throwable;}
    }
}
