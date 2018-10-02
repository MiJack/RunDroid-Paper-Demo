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

package com.chanapps.four.gallery;

import android.content.ContentProviderClient;
import android.net.Uri;
import android.util.Log;

import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSource;
import com.android.gallery3d.data.Path;

public class ChanOffLineSource extends MediaSource {
    private static final String TAG = "ChanOffLineSource";
    public static final String KEY_BUCKET_ID = "bucketId";
    
    public static final String SOURCE_PREFIX = "chan-offline";

    private GalleryApp mApplication;

    private ContentProviderClient mClient;

    public ChanOffLineSource(GalleryApp context) {
        super(SOURCE_PREFIX);
        mApplication = context;
    }

    @Override
    public MediaObject createMediaObject(Path path) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanOffLineSource.createMediaObject(com.android.gallery3d.data.Path)",this,path);try{if (SOURCE_PREFIX.equals(path.getPrefix())) {
        	String[] elems = path.split();
        	if (elems.length == 1) {
        		{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanOffLineSource.createMediaObject(com.android.gallery3d.data.Path)",this);return new ChanOffLineAlbumSet(path, mApplication);}
        	} else if (elems.length == 2) {
        		{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanOffLineSource.createMediaObject(com.android.gallery3d.data.Path)",this);return new ChanOffLineAlbum(path, mApplication, elems[1]);}
        	} else if (elems.length == 3) {
        		{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanOffLineSource.createMediaObject(com.android.gallery3d.data.Path)",this);return new ChanOffLineImage(mApplication, path, elems[1], elems[2]);}
        	}
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanOffLineSource.createMediaObject(com.android.gallery3d.data.Path)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanOffLineSource.createMediaObject(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    @Override
    public Path findPathByUri(Uri uri) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.Path com.chanapps.four.gallery.ChanOffLineSource.findPathByUri(android.net.Uri)",this,uri);try{try {
        	String uriStr = uri != null ? uri.toString() : "";
        	if (uriStr.startsWith("/" + SOURCE_PREFIX + "/")) {
        		{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.chanapps.four.gallery.ChanOffLineSource.findPathByUri(android.net.Uri)",this);return Path.fromString(uriStr);}
        	}
        } catch (Exception e) {
            Log.w(TAG, "uri: " + uri.toString(), e);
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.chanapps.four.gallery.ChanOffLineSource.findPathByUri(android.net.Uri)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.Path com.chanapps.four.gallery.ChanOffLineSource.findPathByUri(android.net.Uri)",this,throwable);throw throwable;}
    }

    @Override
    public void pause() {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanOffLineSource.pause()",this);try{if (mClient != null) {
    		mClient.release();
    		mClient = null;
    	}com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanOffLineSource.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanOffLineSource.pause()",this,throwable);throw throwable;}
    }
}
