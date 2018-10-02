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
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaSet.ItemConsumer;
import com.android.gallery3d.data.MediaSource.PathId;
import com.android.gallery3d.picasasource.PicasaSource;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.WeakHashMap;

/*// DataManager manages all media sets and media items in the system.*/
/*//*/
/*// Each MediaSet and MediaItem has a unique 64 bits id. The most significant*/
/*// 32 bits represents its parent, and the least significant 32 bits represents*/
/*// the self id. For MediaSet the self id is is globally unique, but for*/
/*// MediaItem it's unique only relative to its parent.*/
/*//*/
/*// To make sure the id is the same when the MediaSet is re-created, a child key*/
/*// is provided to obtainSetId() to make sure the same self id will be used as*/
/*// when the parent and key are the same. A sequence of child keys is called a*/
/*// path. And it's used to identify a specific media set even if the process is*/
/*// killed and re-created, so child keys should be stable identifiers.*/

public class DataManager {
    public static final int INCLUDE_IMAGE = 1;
    public static final int INCLUDE_VIDEO = 2;
    public static final int INCLUDE_ALL = INCLUDE_IMAGE | INCLUDE_VIDEO;
    public static final int INCLUDE_LOCAL_ONLY = 4;
    public static final int INCLUDE_LOCAL_IMAGE_ONLY =
            INCLUDE_LOCAL_ONLY | INCLUDE_IMAGE;
    public static final int INCLUDE_LOCAL_VIDEO_ONLY =
            INCLUDE_LOCAL_ONLY | INCLUDE_VIDEO;
    public static final int INCLUDE_LOCAL_ALL_ONLY =
            INCLUDE_LOCAL_ONLY | INCLUDE_IMAGE | INCLUDE_VIDEO;

    /*// Any one who would like to access data should require this lock*/
    /*// to prevent concurrency issue.*/
    public static final Object LOCK = new Object();

    private static final String TAG = "DataManager";

    /*// This is the path for the media set seen by the user at top level.*/
    private static final String TOP_SET_PATH =
            "/combo/{/mtp,/local/all,/picasa/all}";
    private static final String TOP_IMAGE_SET_PATH =
            "/combo/{/mtp,/local/image,/picasa/image}";
    private static final String TOP_VIDEO_SET_PATH =
            "/combo/{/local/video,/picasa/video}";
    private static final String TOP_LOCAL_SET_PATH =
            "/local/all";
    private static final String TOP_LOCAL_IMAGE_SET_PATH =
            "/local/image";
    private static final String TOP_LOCAL_VIDEO_SET_PATH =
            "/local/video";

    public static final Comparator<MediaItem> sDateTakenComparator =
            new DateTakenComparator();

    private static class DateTakenComparator implements Comparator<MediaItem> {
        public int compare(MediaItem item1, MediaItem item2) {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.DataManager$DateTakenComparator.compare(com.android.gallery3d.data.MediaItem,com.android.gallery3d.data.MediaItem)",this,item1,item2);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.DataManager$DateTakenComparator.compare(com.android.gallery3d.data.MediaItem,com.android.gallery3d.data.MediaItem)",this);return -Utils.compare(item1.getDateInMs(), item2.getDateInMs());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.DataManager$DateTakenComparator.compare(com.android.gallery3d.data.MediaItem,com.android.gallery3d.data.MediaItem)",this,throwable);throw throwable;}
        }
    }

    private final Handler mDefaultMainHandler;

    private GalleryApp mApplication;
    private int mActiveCount = 0;

    private HashMap<Uri, NotifyBroker> mNotifierMap =
            new HashMap<Uri, NotifyBroker>();


    private HashMap<String, MediaSource> mSourceMap =
            new LinkedHashMap<String, MediaSource>();

    public DataManager(GalleryApp application) {
        mApplication = application;
        mDefaultMainHandler = new Handler(application.getMainLooper());
    }

    public synchronized void initializeSourceMap() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DataManager.initializeSourceMap()",this);try{if (!mSourceMap.isEmpty()) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DataManager.initializeSourceMap()",this);return;}}

        /*// the order matters, the UriSource must come last*/
        addSource(new LocalSource(mApplication));
        addSource(new PicasaSource(mApplication));
        addSource(new MtpSource(mApplication));
        addSource(new ComboSource(mApplication));
        addSource(new ClusterSource(mApplication));
        addSource(new FilterSource(mApplication));
        addSource(new UriSource(mApplication));

        if (mActiveCount > 0) {
            for (MediaSource source : mSourceMap.values()) {
                source.resume();
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DataManager.initializeSourceMap()",this,throwable);throw throwable;}
    }

    public String getTopSetPath(int typeBits) {

        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.DataManager.getTopSetPath(int)",this,typeBits);try{switch (typeBits) {
            case INCLUDE_IMAGE: {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.DataManager.getTopSetPath(int)",this);return TOP_IMAGE_SET_PATH;}
            case INCLUDE_VIDEO: {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.DataManager.getTopSetPath(int)",this);return TOP_VIDEO_SET_PATH;}
            case INCLUDE_ALL: {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.DataManager.getTopSetPath(int)",this);return TOP_SET_PATH;}
            case INCLUDE_LOCAL_IMAGE_ONLY: {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.DataManager.getTopSetPath(int)",this);return TOP_LOCAL_IMAGE_SET_PATH;}
            case INCLUDE_LOCAL_VIDEO_ONLY: {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.DataManager.getTopSetPath(int)",this);return TOP_LOCAL_VIDEO_SET_PATH;}
            case INCLUDE_LOCAL_ALL_ONLY: {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.DataManager.getTopSetPath(int)",this);return TOP_LOCAL_SET_PATH;}
            default: throw new IllegalArgumentException();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.DataManager.getTopSetPath(int)",this,throwable);throw throwable;}
    }

    /*// open for debug*/
    public void addSource(MediaSource source) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DataManager.addSource(com.android.gallery3d.data.MediaSource)",this,source);try{mSourceMap.put(source.getPrefix(), source);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DataManager.addSource(com.android.gallery3d.data.MediaSource)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DataManager.addSource(com.android.gallery3d.data.MediaSource)",this,throwable);throw throwable;}
    }

    public MediaObject peekMediaObject(Path path) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.DataManager.peekMediaObject(com.android.gallery3d.data.Path)",this,path);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.DataManager.peekMediaObject(com.android.gallery3d.data.Path)",this);return path.getObject();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.DataManager.peekMediaObject(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public MediaSet peekMediaSet(Path path) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.peekMediaSet(com.android.gallery3d.data.Path)",this,path);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.peekMediaSet(com.android.gallery3d.data.Path)",this);return (MediaSet) path.getObject();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.peekMediaSet(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public MediaObject getMediaObject(Path path) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.DataManager.getMediaObject(com.android.gallery3d.data.Path)",this,path);try{MediaObject obj = path.getObject();
        if (obj != null) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.DataManager.getMediaObject(com.android.gallery3d.data.Path)",this);return obj;}}

        MediaSource source = mSourceMap.get(path.getPrefix());
        if (source == null) {
            Log.w(TAG, "cannot find media source for path: " + path);
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.DataManager.getMediaObject(com.android.gallery3d.data.Path)",this);return null;}
        }

        MediaObject object = source.createMediaObject(path);
        if (object == null) {
            Log.w(TAG, "cannot create media object: " + path);
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.DataManager.getMediaObject(com.android.gallery3d.data.Path)",this);return object;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.DataManager.getMediaObject(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public MediaObject getMediaObject(String s) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.DataManager.getMediaObject(java.lang.String)",this,s);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.DataManager.getMediaObject(java.lang.String)",this);return getMediaObject(Path.fromString(s));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.DataManager.getMediaObject(java.lang.String)",this,throwable);throw throwable;}
    }

    public MediaSet getMediaSet(Path path) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.getMediaSet(com.android.gallery3d.data.Path)",this,path);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.getMediaSet(com.android.gallery3d.data.Path)",this);return (MediaSet) getMediaObject(path);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.getMediaSet(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public MediaSet getMediaSet(String s) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.getMediaSet(java.lang.String)",this,s);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.getMediaSet(java.lang.String)",this);return (MediaSet) getMediaObject(s);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.getMediaSet(java.lang.String)",this,throwable);throw throwable;}
    }

    public MediaSet[] getMediaSetsFromString(String segment) {
        com.mijack.Xlog.logMethodEnter("[com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.getMediaSetsFromString(java.lang.String)",this,segment);try{String[] seq = Path.splitSequence(segment);
        int n = seq.length;
        MediaSet[] sets = new MediaSet[n];
        for (int i = 0; i < n; i++) {
            sets[i] = getMediaSet(seq[i]);
        }
        {com.mijack.Xlog.logMethodExit("[com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.getMediaSetsFromString(java.lang.String)",this);return sets;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[com.android.gallery3d.data.MediaSet com.android.gallery3d.data.DataManager.getMediaSetsFromString(java.lang.String)",this,throwable);throw throwable;}
    }

    /*// Maps a list of Paths to MediaItems, and invoke consumer.consume()*/
    /*// for each MediaItem (may not be in the same order as the input list).*/
    /*// An index number is also passed to consumer.consume() to identify*/
    /*// the original position in the input list of the corresponding Path (plus*/
    /*// startIndex).*/
    public void mapMediaItems(ArrayList<Path> list, ItemConsumer consumer,
            int startIndex) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DataManager.mapMediaItems(java.util.ArrayList,com.android.gallery3d.data.MediaSet.ItemConsumer,int)",this,list,consumer,startIndex);try{HashMap<String, ArrayList<PathId>> map =
                new HashMap<String, ArrayList<PathId>>();

        /*// Group the path by the prefix.*/
        int n = list.size();
        for (int i = 0; i < n; i++) {
            Path path = list.get(i);
            String prefix = path.getPrefix();
            ArrayList<PathId> group = map.get(prefix);
            if (group == null) {
                group = new ArrayList<PathId>();
                map.put(prefix, group);
            }
            group.add(new PathId(path, i + startIndex));
        }

        /*// For each group, ask the corresponding media source to map it.*/
        for (Entry<String, ArrayList<PathId>> entry : map.entrySet()) {
            String prefix = entry.getKey();
            MediaSource source = mSourceMap.get(prefix);
            source.mapMediaItems(entry.getValue(), consumer);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DataManager.mapMediaItems(java.util.ArrayList,com.android.gallery3d.data.MediaSet.ItemConsumer,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DataManager.mapMediaItems(java.util.ArrayList,com.android.gallery3d.data.MediaSet.ItemConsumer,int)",this,throwable);throw throwable;}
    }

    /*// The following methods forward the request to the proper object.*/
    public int getSupportedOperations(Path path) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.DataManager.getSupportedOperations(com.android.gallery3d.data.Path)",this,path);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.DataManager.getSupportedOperations(com.android.gallery3d.data.Path)",this);return getMediaObject(path).getSupportedOperations();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.DataManager.getSupportedOperations(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public void delete(Path path) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DataManager.delete(com.android.gallery3d.data.Path)",this,path);try{getMediaObject(path).delete();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DataManager.delete(com.android.gallery3d.data.Path)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DataManager.delete(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public void rotate(Path path, int degrees) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DataManager.rotate(com.android.gallery3d.data.Path,int)",this,path,degrees);try{getMediaObject(path).rotate(degrees);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DataManager.rotate(com.android.gallery3d.data.Path,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DataManager.rotate(com.android.gallery3d.data.Path,int)",this,throwable);throw throwable;}
    }

    public Uri getContentUri(Path path) {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.data.DataManager.getContentUri(com.android.gallery3d.data.Path)",this,path);try{com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.data.DataManager.getContentUri(com.android.gallery3d.data.Path)",this);return getMediaObject(path).getContentUri();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.data.DataManager.getContentUri(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public int getMediaType(Path path) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.DataManager.getMediaType(com.android.gallery3d.data.Path)",this,path);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.DataManager.getMediaType(com.android.gallery3d.data.Path)",this);return getMediaObject(path).getMediaType();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.DataManager.getMediaType(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public MediaDetails getDetails(Path path) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.DataManager.getDetails(com.android.gallery3d.data.Path)",this,path);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.DataManager.getDetails(com.android.gallery3d.data.Path)",this);return getMediaObject(path).getDetails();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.DataManager.getDetails(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public void cache(Path path, int flag) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DataManager.cache(com.android.gallery3d.data.Path,int)",this,path,flag);try{getMediaObject(path).cache(flag);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DataManager.cache(com.android.gallery3d.data.Path,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DataManager.cache(com.android.gallery3d.data.Path,int)",this,throwable);throw throwable;}
    }

    public Path findPathByUri(Uri uri) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.Path com.android.gallery3d.data.DataManager.findPathByUri(android.net.Uri)",this,uri);try{if (uri == null) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.DataManager.findPathByUri(android.net.Uri)",this);return null;}}
        for (MediaSource source : mSourceMap.values()) {
            Path path = source.findPathByUri(uri);
            if (path != null) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.DataManager.findPathByUri(android.net.Uri)",this);return path;}}
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.DataManager.findPathByUri(android.net.Uri)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.Path com.android.gallery3d.data.DataManager.findPathByUri(android.net.Uri)",this,throwable);throw throwable;}
    }

    public Path getDefaultSetOf(Path item) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.Path com.android.gallery3d.data.DataManager.getDefaultSetOf(com.android.gallery3d.data.Path)",this,item);try{MediaSource source = mSourceMap.get(item.getPrefix());
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.DataManager.getDefaultSetOf(com.android.gallery3d.data.Path)",this);return source == null ? null : source.getDefaultSetOf(item);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.Path com.android.gallery3d.data.DataManager.getDefaultSetOf(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    /*// Returns number of bytes used by cached pictures currently downloaded.*/
    public long getTotalUsedCacheSize() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.DataManager.getTotalUsedCacheSize()",this);try{long sum = 0;
        for (MediaSource source : mSourceMap.values()) {
            sum += source.getTotalUsedCacheSize();
        }
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.DataManager.getTotalUsedCacheSize()",this);return sum;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.DataManager.getTotalUsedCacheSize()",this,throwable);throw throwable;}
    }

    /*// Returns number of bytes used by cached pictures if all pending*/
    /*// downloads and removals are completed.*/
    public long getTotalTargetCacheSize() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.DataManager.getTotalTargetCacheSize()",this);try{long sum = 0;
        for (MediaSource source : mSourceMap.values()) {
            sum += source.getTotalTargetCacheSize();
        }
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.DataManager.getTotalTargetCacheSize()",this);return sum;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.DataManager.getTotalTargetCacheSize()",this,throwable);throw throwable;}
    }

    public void registerChangeNotifier(Uri uri, ChangeNotifier notifier) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DataManager.registerChangeNotifier(android.net.Uri,com.android.gallery3d.data.ChangeNotifier)",this,uri,notifier);try{NotifyBroker broker = null;
        synchronized (mNotifierMap) {
            broker = mNotifierMap.get(uri);
            if (broker == null) {
                broker = new NotifyBroker(mDefaultMainHandler);
                mApplication.getContentResolver()
                        .registerContentObserver(uri, true, broker);
                mNotifierMap.put(uri, broker);
            }
        }
        broker.registerNotifier(notifier);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DataManager.registerChangeNotifier(android.net.Uri,com.android.gallery3d.data.ChangeNotifier)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DataManager.registerChangeNotifier(android.net.Uri,com.android.gallery3d.data.ChangeNotifier)",this,throwable);throw throwable;}
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DataManager.resume()",this);try{if (++mActiveCount == 1) {
            for (MediaSource source : mSourceMap.values()) {
                source.resume();
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DataManager.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DataManager.resume()",this,throwable);throw throwable;}
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DataManager.pause()",this);try{if (--mActiveCount == 0) {
            for (MediaSource source : mSourceMap.values()) {
                source.pause();
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DataManager.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DataManager.pause()",this,throwable);throw throwable;}
    }

    private static class NotifyBroker extends ContentObserver {
        private WeakHashMap<ChangeNotifier, Object> mNotifiers =
                new WeakHashMap<ChangeNotifier, Object>();

        public NotifyBroker(Handler handler) {
            super(handler);
        }

        public synchronized void registerNotifier(ChangeNotifier notifier) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DataManager$NotifyBroker.registerNotifier(com.android.gallery3d.data.ChangeNotifier)",this,notifier);try{mNotifiers.put(notifier, null);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DataManager$NotifyBroker.registerNotifier(com.android.gallery3d.data.ChangeNotifier)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DataManager$NotifyBroker.registerNotifier(com.android.gallery3d.data.ChangeNotifier)",this,throwable);throw throwable;}
        }

        @Override
        public synchronized void onChange(boolean selfChange) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.DataManager$NotifyBroker.onChange(boolean)",this,selfChange);try{for(ChangeNotifier notifier : mNotifiers.keySet()) {
                notifier.onChange(selfChange);
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.DataManager$NotifyBroker.onChange(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.DataManager$NotifyBroker.onChange(boolean)",this,throwable);throw throwable;}
        }
    }
}
