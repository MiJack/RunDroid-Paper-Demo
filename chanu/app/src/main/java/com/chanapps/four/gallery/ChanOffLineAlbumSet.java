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
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.chanapps.four.data.ChanFileStorage;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class ChanOffLineAlbumSet extends MediaSet {
	private static final String TAG = "ChanOffLineAlbumSet";
	
	private GalleryApp application;
	private String name;
	private List<ChanOffLineAlbum> boards = new ArrayList<ChanOffLineAlbum>();
	
	public ChanOffLineAlbumSet(Path path, GalleryApp application) {
		super(path, nextVersionNumber());
		this.application = application;
		this.name = "Cached images";
		
		loadData();
	}

	@Override
	public String getName() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.gallery.ChanOffLineAlbumSet.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.gallery.ChanOffLineAlbumSet.getName()",this);return name;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.gallery.ChanOffLineAlbumSet.getName()",this,throwable);throw throwable;}
	}
	
	@Override
    public MediaSet getSubMediaSet(int index) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaSet com.chanapps.four.gallery.ChanOffLineAlbumSet.getSubMediaSet(int)",this,index);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaSet com.chanapps.four.gallery.ChanOffLineAlbumSet.getSubMediaSet(int)",this);return boards.get(index);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaSet com.chanapps.four.gallery.ChanOffLineAlbumSet.getSubMediaSet(int)",this,throwable);throw throwable;}
    }

    @Override
    public int getSubMediaSetCount() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanOffLineAlbumSet.getSubMediaSetCount()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanOffLineAlbumSet.getSubMediaSetCount()",this);return boards.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanOffLineAlbumSet.getSubMediaSetCount()",this,throwable);throw throwable;}
    }

	@Override
	public long reload() {
		com.mijack.Xlog.logMethodEnter("long com.chanapps.four.gallery.ChanOffLineAlbumSet.reload()",this);try{int prevSize = boards.size();
		boards.clear();
		
		loadData();
        
		if (prevSize != boards.size()) {
			{com.mijack.Xlog.logMethodExit("long com.chanapps.four.gallery.ChanOffLineAlbumSet.reload()",this);return nextVersionNumber();}
		} else {
			{com.mijack.Xlog.logMethodExit("long com.chanapps.four.gallery.ChanOffLineAlbumSet.reload()",this);return mDataVersion;}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.gallery.ChanOffLineAlbumSet.reload()",this,throwable);throw throwable;}
	}

	private void loadData() {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanOffLineAlbumSet.loadData()",this);try{Context context = application.getAndroidContext();
        File cacheFolder = ChanFileStorage.getCacheDirectory(context);
        File[] dirs = cacheFolder.listFiles(new FilenameFilter() {
            public boolean accept(File directory, String fileName) {
                com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.gallery.ChanOffLineAlbumSet$1.accept(java.io.File,java.lang.String)",this,directory,fileName);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanOffLineAlbumSet$1.accept(java.io.File,java.lang.String)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanOffLineAlbumSet.loadData()",this);return new File(directory, fileName).isDirectory();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanOffLineAlbumSet$1.accept(java.io.File,java.lang.String)",this,throwable);throw throwable;}
            }
        });
        
        for (File dir : dirs) {
        	if (!folderContainsImages(dir)) {
        		continue;
        	}
        	Log.i(TAG, "Creating album object for folder " + dir.getAbsolutePath());
        	Path path = Path.fromString("/" + ChanOffLineSource.SOURCE_PREFIX + "/" + dir.getName());
        	if (path.getObject() == null) {
        		boards.add(new ChanOffLineAlbum(path, application, dir));
        	} else {
        		boards.add((ChanOffLineAlbum)path.getObject());
        	}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanOffLineAlbumSet.loadData()",this,throwable);throw throwable;}
	}
	
	private boolean folderContainsImages(File dir) {
		com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.gallery.ChanOffLineAlbumSet.folderContainsImages(java.io.File)",this,dir);try{File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File directory, String fileName) {
                com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.gallery.ChanOffLineAlbumSet$2.accept(java.io.File,java.lang.String)",this,directory,fileName);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanOffLineAlbumSet$2.accept(java.io.File,java.lang.String)",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanOffLineAlbumSet.folderContainsImages(java.io.File)",this);return !fileName.endsWith(".txt");}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanOffLineAlbumSet$2.accept(java.io.File,java.lang.String)",this,throwable);throw throwable;}
            }
        });
		{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanOffLineAlbumSet.folderContainsImages(java.io.File)",this);return files.length > 0;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanOffLineAlbumSet.folderContainsImages(java.io.File)",this,throwable);throw throwable;}
	}
}
