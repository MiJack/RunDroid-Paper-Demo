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
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.data.ChanThread;

public class ChanSource extends MediaSource {
    private static final String TAG = "ChanSource";
    private static final boolean DEBUG = false;
    public static final String KEY_BUCKET_ID = "bucketId";

    private GalleryApp mApplication;

    private ContentProviderClient mClient;

    public ChanSource(GalleryApp context) {
        super("chan");
        mApplication = context;
    }

    @Override
    public MediaObject createMediaObject(Path path) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanSource.createMediaObject(com.android.gallery3d.data.Path)",this,path);try{if ("chan".equals(path.getPrefix())) {
        	String[] elems = path.split();
        	String board = elems[1];
        	String threadStr = elems[2];
    		long threadNo = Long.parseLong(threadStr);
            /*//ChanThread thread = ChanFileStorage.getCachedThreadData(mApplication.getAndroidContext(), board, threadNo);*/
            /*// really should not be on main thread, but gallery not yet setup to async load thread data on start without crashing*/
            ChanThread thread = ChanFileStorage.loadThreadData(mApplication.getAndroidContext(), board, threadNo);
            if (DEBUG) {Log.i(TAG, "createMediaObject() path=" + path + " found cached thread=" + thread);}
        	if (thread == null) {
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanSource.createMediaObject(com.android.gallery3d.data.Path)",this);return null;}
            }
            else if (elems.length == 3) {
        		{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanSource.createMediaObject(com.android.gallery3d.data.Path)",this);return new ChanAlbum(path, mApplication, thread);}
        	} else {
        		long postNo = Long.parseLong(elems[3]);
        		for (ChanPost post : thread.posts) {
        			if (post.no == postNo) {
        				{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanSource.createMediaObject(com.android.gallery3d.data.Path)",this);return new ChanImage(mApplication, path, post);}
        			}
        		}
        	}
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanSource.createMediaObject(com.android.gallery3d.data.Path)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaObject com.chanapps.four.gallery.ChanSource.createMediaObject(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    @Override
    public Path findPathByUri(Uri uri) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.Path com.chanapps.four.gallery.ChanSource.findPathByUri(android.net.Uri)",this,uri);try{try {
        	String uriStr = uri != null ? uri.toString() : "";
        	if (uriStr.startsWith("/chan/")) {
        		{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.chanapps.four.gallery.ChanSource.findPathByUri(android.net.Uri)",this);return Path.fromString(uriStr);}
        	}
        } catch (Exception e) {
            Log.w(TAG, "uri: " + uri.toString(), e);
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.chanapps.four.gallery.ChanSource.findPathByUri(android.net.Uri)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.Path com.chanapps.four.gallery.ChanSource.findPathByUri(android.net.Uri)",this,throwable);throw throwable;}
    }

    @Override
    public void pause() {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanSource.pause()",this);try{if (mClient != null) {
    		mClient.release();
    		mClient = null;
    	}com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanSource.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanSource.pause()",this,throwable);throw throwable;}
    }
}
