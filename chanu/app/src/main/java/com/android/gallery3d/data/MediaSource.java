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

import com.android.gallery3d.data.MediaSet.ItemConsumer;

import android.net.Uri;

import java.util.ArrayList;

public abstract class MediaSource {
    private static final String TAG = "MediaSource";
    private String mPrefix;

    protected MediaSource(String prefix) {
        mPrefix = prefix;
    }

    public String getPrefix() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.MediaSource.getPrefix()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.MediaSource.getPrefix()",this);return mPrefix;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.MediaSource.getPrefix()",this,throwable);throw throwable;}
    }

    public Path findPathByUri(Uri uri) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.Path com.android.gallery3d.data.MediaSource.findPathByUri(android.net.Uri)",this,uri);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.MediaSource.findPathByUri(android.net.Uri)",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.Path com.android.gallery3d.data.MediaSource.findPathByUri(android.net.Uri)",this,throwable);throw throwable;}
    }

    public abstract MediaObject createMediaObject(Path path);

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSource.pause()",this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSource.pause()",this);}

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSource.resume()",this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSource.resume()",this);}

    public Path getDefaultSetOf(Path item) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.Path com.android.gallery3d.data.MediaSource.getDefaultSetOf(com.android.gallery3d.data.Path)",this,item);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.MediaSource.getDefaultSetOf(com.android.gallery3d.data.Path)",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.Path com.android.gallery3d.data.MediaSource.getDefaultSetOf(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public long getTotalUsedCacheSize() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.MediaSource.getTotalUsedCacheSize()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.MediaSource.getTotalUsedCacheSize()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.MediaSource.getTotalUsedCacheSize()",this,throwable);throw throwable;}
    }

    public long getTotalTargetCacheSize() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.MediaSource.getTotalTargetCacheSize()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.MediaSource.getTotalTargetCacheSize()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.MediaSource.getTotalTargetCacheSize()",this,throwable);throw throwable;}
    }

    public static class PathId {
        public PathId(Path path, int id) {
            this.path = path;
            this.id = id;
        }
        public Path path;
        public int id;
    }

    /*// Maps a list of Paths (all belong to this MediaSource) to MediaItems,*/
    /*// and invoke consumer.consume() for each MediaItem with the given id.*/
    /*//*/
    /*// This default implementation uses getMediaObject for each Path. Subclasses*/
    /*// may override this and provide more efficient implementation (like*/
    /*// batching the database query).*/
    public void mapMediaItems(ArrayList<PathId> list, ItemConsumer consumer) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaSource.mapMediaItems(java.util.ArrayList,com.android.gallery3d.data.MediaSet.ItemConsumer)",this,list,consumer);try{int n = list.size();
        for (int i = 0; i < n; i++) {
            PathId pid = list.get(i);
            MediaObject obj = pid.path.getObject();
            if (obj == null) {
                try {
                    obj = createMediaObject(pid.path);
                } catch (Throwable th) {
                    Log.w(TAG, "cannot create media object: " + pid.path, th);
                }
            }
            if (obj != null) {
                consumer.consume(pid.id, (MediaItem) obj);
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaSource.mapMediaItems(java.util.ArrayList,com.android.gallery3d.data.MediaSet.ItemConsumer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaSource.mapMediaItems(java.util.ArrayList,com.android.gallery3d.data.MediaSet.ItemConsumer)",this,throwable);throw throwable;}
    }
}
