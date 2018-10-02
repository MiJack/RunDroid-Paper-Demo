/**
 * 
 */
package com.chanapps.four.gallery;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.chanapps.four.data.ChanFileStorage;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class ChanOffLineAlbum extends MediaSet {
	private static final String TAG = "ChanOffLineAlbum";
	private static final boolean DEBUG = false;
	
	private GalleryApp application;
	private String name;
	private File dir;
	private List<ChanOffLineImage> images = new ArrayList<ChanOffLineImage>();
	
	public ChanOffLineAlbum(Path path, GalleryApp application, String dir) {
		super(path, nextVersionNumber());
		Context context = application.getAndroidContext();
        File cacheFolder = ChanFileStorage.getCacheDirectory(context);
        this.dir = new File(cacheFolder, dir);
        this.application = application;
		this.name = "Cached /" + dir;
		
		loadData();
	}
	
	public ChanOffLineAlbum(Path path, GalleryApp application, File dir) {
		super(path, nextVersionNumber());
		this.application = application;
		this.dir = dir;
		this.name = "Cached /" + dir.getName();
		
		loadData();
	}

	@Override
	public String getName() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.gallery.ChanOffLineAlbum.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.gallery.ChanOffLineAlbum.getName()",this);return name;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.gallery.ChanOffLineAlbum.getName()",this,throwable);throw throwable;}
	}
	
	public String getDirName() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.gallery.ChanOffLineAlbum.getDirName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.gallery.ChanOffLineAlbum.getDirName()",this);return dir != null ? dir.getName() : "null";}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.gallery.ChanOffLineAlbum.getDirName()",this,throwable);throw throwable;}
	}

	@Override
    public ArrayList<MediaItem> getMediaItem(int start, int count) {
		com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.chanapps.four.gallery.ChanOffLineAlbum.getMediaItem(int,int)",this,start,count);try{ArrayList<MediaItem> result = new ArrayList<MediaItem>();
		for (int i = 0; i < count; i++) {
			if (i + start < images.size()) {
				ChanOffLineImage post = images.get(i + start);
				result.add(post);
			}
		}
		{com.mijack.Xlog.logMethodExit("java.util.ArrayList com.chanapps.four.gallery.ChanOffLineAlbum.getMediaItem(int,int)",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.chanapps.four.gallery.ChanOffLineAlbum.getMediaItem(int,int)",this,throwable);throw throwable;}
	}
	
	@Override
    public int getMediaItemCount() {
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanOffLineAlbum.getMediaItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanOffLineAlbum.getMediaItemCount()",this);return images.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanOffLineAlbum.getMediaItemCount()",this,throwable);throw throwable;}
	}
	
	@Override
    public boolean isLeafAlbum() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.gallery.ChanOffLineAlbum.isLeafAlbum()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanOffLineAlbum.isLeafAlbum()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanOffLineAlbum.isLeafAlbum()",this,throwable);throw throwable;}
    }

	@Override
	public long reload() {
		com.mijack.Xlog.logMethodEnter("long com.chanapps.four.gallery.ChanOffLineAlbum.reload()",this);try{int prevSize = images.size();
		images.clear();
		
        loadData();
        
		if (prevSize != images.size()) {
			{com.mijack.Xlog.logMethodExit("long com.chanapps.four.gallery.ChanOffLineAlbum.reload()",this);return nextVersionNumber();}
		} else {
			{com.mijack.Xlog.logMethodExit("long com.chanapps.four.gallery.ChanOffLineAlbum.reload()",this);return mDataVersion;}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.gallery.ChanOffLineAlbum.reload()",this,throwable);throw throwable;}
	}

	private void loadData() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanOffLineAlbum.loadData()",this);try{if (dir == null) {
            Log.e(TAG, "loadData() null directory, exiting");
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanOffLineAlbum.loadData()",this);return;}
        }
		Log.i(TAG, "Loading data from " + dir.getAbsolutePath());
		File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File directory, String fileName) {
            	com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.gallery.ChanOffLineAlbum$1.accept(java.io.File,java.lang.String)",this,directory,fileName);try{if (DEBUG) {Log.d(TAG, "Checking file " + directory.getAbsolutePath() + "/" + fileName);}
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanOffLineAlbum.loadData()",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanOffLineAlbum$1.accept(java.io.File,java.lang.String)",this);return !fileName.endsWith(".txt");}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanOffLineAlbum$1.accept(java.io.File,java.lang.String)",this,throwable);throw throwable;}
            }
        });
        if (files == null || files.length == 0) {
            Log.e(TAG, "loadData() exiting, no gallery images found in dir=" + dir);
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanOffLineAlbum.loadData()",this);return;}
        }
        String dirName = dir.getName();
        for (File file : files) {
        	Path path = Path.fromString("/" + ChanOffLineSource.SOURCE_PREFIX + "/" + dir.getName() + "/" + file.getName());
        	if (path.getObject() == null) {
        		images.add(new ChanOffLineImage(application, path, dirName, file));
        	} else {
        		images.add((ChanOffLineImage)path.getObject());
        	}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanOffLineAlbum.loadData()",this,throwable);throw throwable;}
	}
}
