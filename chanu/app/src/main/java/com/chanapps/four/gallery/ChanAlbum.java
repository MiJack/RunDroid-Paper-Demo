/**
 * 
 */
package com.chanapps.four.gallery;

import java.util.ArrayList;
import java.util.List;

import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.Log;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.data.ChanThread;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class ChanAlbum extends MediaSet {

    private static final String TAG = ChanAlbum.class.getSimpleName();
    private static final boolean DEBUG = false;

	private GalleryApp application;
	private String name;
	private String board;
	private long threadNo;
	private List<ChanPost> posts = new ArrayList<ChanPost>();
	
	public ChanAlbum(Path path, GalleryApp application, ChanThread thread) {
		super(path, nextVersionNumber());
        if (thread == null) { /*// in case something went wrong*/
            this.board = ChanBoard.DEFAULT_BOARD_CODE;
            ChanBoard board = ChanBoard.getBoardByCode(application.getAndroidContext(), this.board);
            String rawName = ChanBoard.getName(application.getAndroidContext(), this.board);
            this.name = (rawName == null ? "" : rawName + " ")
                    + "/" + this.board + "/";
            this.threadNo = 0;
            return;
        }
		this.application = application;
		this.board = thread.board;
		this.name = "/" + board + "/" + thread.no;
		this.threadNo = thread.no;
        if (DEBUG) {Log.i(TAG, "ChanAlbum thread=" + thread);}
		for (ChanPost post : thread.posts) {
			if (post.tim != 0) {
				post.isDead = thread.isDead;
                posts.add(post);
			}
		}
	}

	@Override
	public String getName() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.gallery.ChanAlbum.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.gallery.ChanAlbum.getName()",this);return name;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.gallery.ChanAlbum.getName()",this,throwable);throw throwable;}
	}

	@Override
    public ArrayList<MediaItem> getMediaItem(int start, int count) {
		com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.chanapps.four.gallery.ChanAlbum.getMediaItem(int,int)",this,start,count);try{ArrayList<MediaItem> result = new ArrayList<MediaItem>();
		for (int i = 0; i < count; i++) {
			if (i + start < posts.size()) {
				ChanPost post = posts.get(i + start);
				Path path = Path.fromString("/chan/" + post.board + "/" + threadNo + "/" + post.no);
				if (path.getObject() == null) {
					result.add(new ChanImage(application, path, post));
				} else {
					result.add((MediaItem)path.getObject());
				}
			}
		}
		{com.mijack.Xlog.logMethodExit("java.util.ArrayList com.chanapps.four.gallery.ChanAlbum.getMediaItem(int,int)",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.chanapps.four.gallery.ChanAlbum.getMediaItem(int,int)",this,throwable);throw throwable;}
	}
	
	@Override
    public int getMediaItemCount() {
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanAlbum.getMediaItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanAlbum.getMediaItemCount()",this);return posts.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanAlbum.getMediaItemCount()",this,throwable);throw throwable;}
	}
	
	@Override
    public boolean isLeafAlbum() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.gallery.ChanAlbum.isLeafAlbum()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanAlbum.isLeafAlbum()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanAlbum.isLeafAlbum()",this,throwable);throw throwable;}
    }

	@Override
	public long reload() {
        com.mijack.Xlog.logMethodEnter("long com.chanapps.four.gallery.ChanAlbum.reload()",this);try{if (application == null)
            {{com.mijack.Xlog.logMethodExit("long com.chanapps.four.gallery.ChanAlbum.reload()",this);return mDataVersion;}}
		ChanThread thread = ChanFileStorage.loadThreadData(application.getAndroidContext(), board, threadNo);
		int prevSize = posts.size();
		posts.clear();
		for (ChanPost post : thread.posts) {
			if (post.tim != 0) {
				posts.add(post);
			}
		}
		if (prevSize != posts.size()) {
			{com.mijack.Xlog.logMethodExit("long com.chanapps.four.gallery.ChanAlbum.reload()",this);return nextVersionNumber();}
		} else {
			{com.mijack.Xlog.logMethodExit("long com.chanapps.four.gallery.ChanAlbum.reload()",this);return mDataVersion;}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.gallery.ChanAlbum.reload()",this,throwable);throw throwable;}
	}
}
