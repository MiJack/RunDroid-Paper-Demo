package com.chanapps.four.activity;

import android.app.ActivityManager;

import android.util.Pair;
import com.android.gallery3d.app.*;
import com.chanapps.four.component.ActivityDispatcher;
import com.chanapps.four.data.*;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.GLRootView;
import com.chanapps.four.data.LastActivity;
import com.chanapps.four.service.NetworkProfileManager;
import com.chanapps.four.service.ThreadImageDownloadService;
import com.chanapps.four.service.profile.NetworkProfile;

import java.util.regex.Pattern;

public class GalleryViewActivity extends AbstractGalleryActivity implements ChanIdentifiedActivity {

    public static final String TAG = "GalleryViewActivity";

    public static final String BOARD_CODE = "boardCode";
    public static final String THREAD_NO = "threadNo";
    public static final String POST_NO = "postNo";

    private static final boolean DEBUG = false;

    public static final String VIEW_TYPE = "viewType";

    public enum ViewType {
        PHOTO_VIEW,
        ALBUM_VIEW,
        OFFLINE_ALBUM_VIEW,
        OFFLINE_ALBUMSET_VIEW
    }

    public static final int PROGRESS_REFRESH_MSG = 0;
	public static final int FINISHED_DOWNLOAD_MSG = 2;
	public static final int DOWNLOAD_ERROR_MSG = 3;
	public static final int UPDATE_POSTNO_MSG = 4;

    private ViewType viewType = ViewType.PHOTO_VIEW; /*// default single image view*/
    private String boardCode = null;
    private long threadNo = 0;
    private long postNo = 0;
    private ChanPost post = null;
    private LayoutInflater inflater;
    protected Handler handler;
    private Handler postHandler;
    private GalleryActionBar actionBar;
    private String title;
    private ChanThread thread;
    private ProgressBar progressBar;

    public static void startActivity(Context from, ChanActivityId aid) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.startActivity(android.content.Context,com.android.gallery3d.app.ChanActivityId)",from,aid);try{startActivity(from, aid.boardCode, aid.threadNo, aid.postNo);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.GalleryViewActivity.startActivity(android.content.Context,com.android.gallery3d.app.ChanActivityId)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.startActivity(android.content.Context,com.android.gallery3d.app.ChanActivityId)",throwable);throw throwable;}
    }

    public static void startActivity(Context from, String boardCode, long threadNo, long postId) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.startActivity(android.content.Context,com.android.gallery3d.app.String,long,long)",from,boardCode,threadNo,postId);try{Intent intent = createIntent(from, boardCode, threadNo, postId, ViewType.PHOTO_VIEW);
        if (DEBUG) {Log.i(TAG, "Starting full screen image viewer for: " + boardCode + "/" + threadNo + "/" + postId);}
        from.startActivity(intent);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.GalleryViewActivity.startActivity(android.content.Context,com.android.gallery3d.app.String,long,long)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.startActivity(android.content.Context,com.android.gallery3d.app.String,long,long)",throwable);throw throwable;}
    }

    public static void startActivity(Context from, AdapterView<?> adapterView, View view, int position, long id) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.startActivity(android.content.Context,android.widget.AdapterView,android.view.View,int,long)",from,adapterView,view,position,id);try{Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        final long postId = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
        final String boardCode = cursor.getString(cursor.getColumnIndex(ChanPost.POST_BOARD_CODE));
        final long resto = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_RESTO));
        final long threadNo = resto <= 0 ? postId : resto;
        startActivity(from, boardCode, threadNo, postId);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.GalleryViewActivity.startActivity(android.content.Context,android.widget.AdapterView,android.view.View,int,long)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.startActivity(android.content.Context,android.widget.AdapterView,android.view.View,int,long)",throwable);throw throwable;}
    }

    public static Intent createIntent(Context from, String boardCode, long threadNo, long postId, ViewType viewType) {
        com.mijack.Xlog.logStaticMethodEnter("android.content.Intent com.chanapps.four.activity.GalleryViewActivity.createIntent(android.content.Context,com.android.gallery3d.app.String,long,long,com.android.gallery3d.app.ViewType)",from,boardCode,threadNo,postId,viewType);try{if (DEBUG) {Log.i(TAG, "createIntent() viewType=" + viewType);}
        Intent intent = new Intent(from, GalleryViewActivity.class);
        intent.putExtra(VIEW_TYPE, viewType.toString());
        intent.putExtra(BOARD_CODE, boardCode);
        intent.putExtra(THREAD_NO, threadNo);
        intent.putExtra(POST_NO, postId);
        {com.mijack.Xlog.logStaticMethodExit("android.content.Intent com.chanapps.four.activity.GalleryViewActivity.createIntent(android.content.Context,com.android.gallery3d.app.String,long,long,com.android.gallery3d.app.ViewType)");return intent;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.content.Intent com.chanapps.four.activity.GalleryViewActivity.createIntent(android.content.Context,com.android.gallery3d.app.String,long,long,com.android.gallery3d.app.ViewType)",throwable);throw throwable;}
    }

    public static void startAlbumViewActivity(Context from, String boardCode, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.startAlbumViewActivity(android.content.Context,com.android.gallery3d.app.String,long)",from,boardCode,threadNo);try{/*//NetworkProfileManager.instance().getUserStatistics().featureUsed(UserStatistics.ChanFeature.GALLERY_VIEW);*/
        if (DEBUG) {Log.i(TAG, "Starting gallery folder viewer for: " + boardCode + "/" + threadNo);}
        from.startActivity(getAlbumViewIntent(from, boardCode, threadNo));com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.GalleryViewActivity.startAlbumViewActivity(android.content.Context,com.android.gallery3d.app.String,long)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.startAlbumViewActivity(android.content.Context,com.android.gallery3d.app.String,long)",throwable);throw throwable;}
    }
    
    public static void startOfflineAlbumViewActivity(Context from, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.startOfflineAlbumViewActivity(android.content.Context,com.android.gallery3d.app.String)",from,boardCode);try{/*//if (boardCode == null || boardCode.isEmpty())*/
        /*//    NetworkProfileManager.instance().getUserStatistics().featureUsed(UserStatistics.ChanFeature.ALL_CACHED_IMAGES);*/
        /*//else*/
        /*//    NetworkProfileManager.instance().getUserStatistics().featureUsed(UserStatistics.ChanFeature.CACHED_BOARD_IMAGES);*/
        if (DEBUG) {Log.i(TAG, "Starting offline gallery viewer for " + (boardCode != null ? "board " + boardCode : "whole cache"));}
        from.startActivity(getOfflineAlbumViewIntent(from, boardCode));com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.GalleryViewActivity.startOfflineAlbumViewActivity(android.content.Context,com.android.gallery3d.app.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.startOfflineAlbumViewActivity(android.content.Context,com.android.gallery3d.app.String)",throwable);throw throwable;}
    }

    public static Intent getAlbumViewIntent(Context from, String boardCode, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("android.content.Intent com.chanapps.four.activity.GalleryViewActivity.getAlbumViewIntent(android.content.Context,com.android.gallery3d.app.String,long)",from,boardCode,threadNo);try{Intent intent = new Intent(from, GalleryViewActivity.class);
        intent.putExtra(BOARD_CODE, boardCode);
        intent.putExtra(THREAD_NO, threadNo);
        intent.putExtra(VIEW_TYPE, ViewType.ALBUM_VIEW.toString());
        {com.mijack.Xlog.logStaticMethodExit("android.content.Intent com.chanapps.four.activity.GalleryViewActivity.getAlbumViewIntent(android.content.Context,com.android.gallery3d.app.String,long)");return intent;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.content.Intent com.chanapps.four.activity.GalleryViewActivity.getAlbumViewIntent(android.content.Context,com.android.gallery3d.app.String,long)",throwable);throw throwable;}
    }

    public static Intent getOfflineAlbumViewIntent(Context from, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("android.content.Intent com.chanapps.four.activity.GalleryViewActivity.getOfflineAlbumViewIntent(android.content.Context,com.android.gallery3d.app.String)",from,boardCode);try{Intent intent = new Intent(from, GalleryViewActivity.class);
        if (boardCode == null || boardCode.isEmpty()) {
        	intent.putExtra(VIEW_TYPE, ViewType.OFFLINE_ALBUMSET_VIEW.toString());
        } else {
        	intent.putExtra(BOARD_CODE, boardCode);
        	intent.putExtra(VIEW_TYPE, ViewType.OFFLINE_ALBUM_VIEW.toString());
        }
        {com.mijack.Xlog.logStaticMethodExit("android.content.Intent com.chanapps.four.activity.GalleryViewActivity.getOfflineAlbumViewIntent(android.content.Context,com.android.gallery3d.app.String)");return intent;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.content.Intent com.chanapps.four.activity.GalleryViewActivity.getOfflineAlbumViewIntent(android.content.Context,com.android.gallery3d.app.String)",throwable);throw throwable;}
    }

    private void loadChanPostData() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.loadChanPostData()",this);try{post = null;
        try {
            thread = ChanFileStorage.loadThreadData(getBaseContext(), boardCode, threadNo);
            if (thread != null) {
                for (ChanPost post : thread.posts) {
                    if (post.no == postNo) {
                        this.post = post;
                        break;
                    }
                }
            }
        } catch (Exception e) {
			Log.e(TAG, "Error load post data. " + e.getMessage(), e);
		}
        if (post == null) {
            post = new ChanPost();
            post.no = postNo;
            post.resto = threadNo;
            post.board = boardCode;
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.loadChanPostData()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.loadChanPostData()",this,throwable);throw throwable;}
    }

    @Override
    protected void onCreate(Bundle bundle){
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.onCreate(android.os.Bundle)",this,bundle);try{super.onCreate(bundle);
        if (DEBUG) {Log.i(TAG, "onCreate");}
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        actionBar = new GalleryActionBar(this);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.gallery_layout);
        progressBar = (ProgressBar)findViewById(R.id.full_screen_progress_bar);
        if (bundle != null)
            {onRestoreInstanceState(bundle);}
        else
            {setFromIntent(getIntent());}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void onNewIntent(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.onNewIntent(android.content.Intent)",this,intent);try{setIntent(intent);
        setFromIntent(intent);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.onNewIntent(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.onNewIntent(android.content.Intent)",this,throwable);throw throwable;}
    }

    protected ViewType topViewType() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.topViewType()",this);try{ActivityState state = getStateManager().getTopState();
        if (DEBUG) {Log.i(TAG, "onSaveInstanceState activityState=" + state);}
        ViewType v;
        if (state instanceof PhotoPage)
            {v = ViewType.PHOTO_VIEW;}
        else if (state instanceof AlbumPage)
            {v = ViewType.ALBUM_VIEW;}
        else if (state instanceof AlbumSetPage)
            {v = ViewType.OFFLINE_ALBUMSET_VIEW;}
        else
            {v = ViewType.ALBUM_VIEW;}
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.topViewType()",this);return v;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.topViewType()",this,throwable);throw throwable;}
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.onRestoreInstanceState(android.os.Bundle)",this,bundle);try{viewType = ViewType.valueOf(bundle.getString(VIEW_TYPE));
        boardCode = bundle.getString(ChanBoard.BOARD_CODE);
        threadNo = bundle.getLong(ChanThread.THREAD_NO, 0);
        postNo = bundle.getLong(ChanPost.POST_NO, 0);
        getStateManager().restoreFromState(bundle);
        if (DEBUG) {Log.i(TAG, "onRestoreInstanceState() restoring from /" + boardCode + "/" + threadNo + "#p" + postNo);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.onRestoreInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.onRestoreInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    protected static final String MEDIA_ITEM_PATH = "media-item-path";
    protected static final String PATH_PATTERN_STR = "/.*\\/([0-9]+)$/";
    protected static final Pattern PATH_PATTERN = Pattern.compile(PATH_PATTERN_STR);

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.onSaveInstanceState(android.os.Bundle)",this,bundle);try{postNo = topPostNo();
        viewType = topViewType();
        getStateManager().saveState(bundle);

        bundle.putString(VIEW_TYPE, viewType.toString());
        bundle.putString(ChanBoard.BOARD_CODE, boardCode);
        bundle.putLong(ChanThread.THREAD_NO, threadNo);
        bundle.putLong(ChanPost.POST_NO, postNo);
        if (DEBUG) {Log.i(TAG, "onSaveInstanceState() saved to /" + boardCode + "/" + threadNo + "#p" + postNo);}
        ActivityDispatcher.store(this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    protected long topPostNo() {
        com.mijack.Xlog.logMethodEnter("long com.chanapps.four.activity.GalleryViewActivity.topPostNo()",this);try{ActivityState state = getStateManager().getTopState();
        if (state == null)
            {{com.mijack.Xlog.logMethodExit("long com.chanapps.four.activity.GalleryViewActivity.topPostNo()",this);return 0;}}
        Bundle b = state.getData();
        String path = (String)b.get(MEDIA_ITEM_PATH);
        if (path == null || path.isEmpty())
            {{com.mijack.Xlog.logMethodExit("long com.chanapps.four.activity.GalleryViewActivity.topPostNo()",this);return 0;}}
        int i = path.lastIndexOf("/");
        if (i == -1 || (++i + 1) >= path.length())
            {{com.mijack.Xlog.logMethodExit("long com.chanapps.four.activity.GalleryViewActivity.topPostNo()",this);return 0;}}
        String postNoStr = path.substring(i);
        {com.mijack.Xlog.logMethodExit("long com.chanapps.four.activity.GalleryViewActivity.topPostNo()",this);return Long.valueOf(postNoStr);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.activity.GalleryViewActivity.topPostNo()",this,throwable);throw throwable;}
    }

    protected void setFromIntent(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.setFromIntent(android.content.Intent)",this,intent);try{String s = intent.getStringExtra(VIEW_TYPE);
        if (DEBUG) {Log.i(TAG, "setFromIntent viewType=" + s);}
        if (s != null && !s.isEmpty())
            {viewType = ViewType.valueOf(s);}
        else
            {viewType = ViewType.OFFLINE_ALBUMSET_VIEW;}
        boardCode = intent.getStringExtra(ChanBoard.BOARD_CODE);
        threadNo = intent.getLongExtra(ChanThread.THREAD_NO, 0);
        postNo = intent.getLongExtra(ChanPost.POST_NO, 0);
        if (DEBUG) {Log.i(TAG, "setFromIntent() loaded /" + boardCode + "/" + threadNo + "#" + postNo
                + " viewType=" + viewType.toString());}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.setFromIntent(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.setFromIntent(android.content.Intent)",this,throwable);throw throwable;}
    }

    @Override
	protected void onStart() {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.onStart()",this);try{super.onStart();
		if (DEBUG) {Log.i(TAG, "onStart");}
        postHandler = new Handler();
        loadDataAsync();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    protected void onStop () {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.onStop()",this);try{super.onStop();
    	if (DEBUG) {Log.i(TAG, "onStop");}
        postHandler = null;com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.onStop()",this,throwable);throw throwable;}
    }

    @Override
	protected void onResume () {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.onResume()",this);try{super.onResume();
		if (DEBUG) {Log.i(TAG, "onResume");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.onResume()",this,throwable);throw throwable;}
	}

    @Override
	protected void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.onPause()",this);try{try {
            super.onPause();
        }
        catch (Exception e) {
            Log.e(TAG, "onPause() gallery state exception", e);
        }
        if (DEBUG) {Log.i(TAG, "onPause");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.onPause()",this,throwable);throw throwable;}
    }

    @Override
    public void onBackPressed() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.onBackPressed()",this);try{if (DEBUG) {Log.i(TAG, "onBackPressed()");}
        /*// send the back event to the top sub-state*/
        GLRoot root = getGLRoot();
        root.lockRenderThread();
        try {
        	if (DEBUG) {Log.i(TAG, "Gallery state stack: " + getStateManager().getStackDescription());}
            getStateManager().compactActivityStateStack();
        	if (getStateManager().getStateCount() > 1) {
        		getStateManager().onBackPressed();
        	} else {
                finish();
        		/*//navigateUp();*/
        	}
        }
        catch (Exception e) {
            Log.e(TAG, "onBackPressed() exception", e);
            finish();
        }
        catch (Error e) {
            Log.e(TAG, "onBackPressed() error (probably assertion error)", e);
            finish();
        }
        finally {
            root.unlockRenderThread();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.onBackPressed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.onBackPressed()",this,throwable);throw throwable;}
    }
    
    @Override
	protected void onDestroy () {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.onDestroy()",this);try{super.onDestroy();
		if (DEBUG) {Log.i(TAG, "onDestroy");}
        GLRoot root = getGLRoot();
        if (root != null) {
	        root.lockRenderThread();
	        try {
	            getStateManager().destroy();
	        } finally {
	            root.unlockRenderThread();
	        }
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.onDestroy()",this,throwable);throw throwable;}
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.onConfigurationChanged(android.content.res.Configuration)",this,newConfig);try{super.onConfigurationChanged(newConfig);
    	prepareGalleryView();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.onConfigurationChanged(android.content.res.Configuration)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.onConfigurationChanged(android.content.res.Configuration)",this,throwable);throw throwable;}
    }

    private void loadDataAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.loadDataAsync()",this);try{setProgressBar(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity$1.run()",this);try{delayLoop();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity$1.run()",this,throwable);throw throwable;}
            }
        }).start();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.loadDataAsync()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.loadDataAsync()",this,throwable);throw throwable;}
    }

    private void delayLoop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.delayLoop()",this);try{loadChanPostData();
        loadActionBarTitle();
        final long delayMs = calcDelayMs();
        if (DEBUG) {Log.i(TAG, "loadDataAsync() delayMs=" + delayMs);}
        if (delayMs <= 0) {
            displayGallery(); /*// load now*/
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.delayLoop()",this);return;}
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity$2.run()",this);try{try {
                    Thread.sleep(delayMs);
                    delayLoop();
                }
                catch (InterruptedException e) {
                    delayLoop();
                }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity$2.run()",this,throwable);throw throwable;}
            }
        }).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.delayLoop()",this,throwable);throw throwable;}
    }

    private void displayGallery() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.displayGallery()",this);try{if (postHandler != null)
            {postHandler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity$3.run()",this);try{setActionBarTitle();
                    setProgressBar(false);
                    prepareGalleryView();
                    activityChangeAsync();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity$3.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity$3.run()",this,throwable);throw throwable;}
                }
            });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.displayGallery()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.displayGallery()",this,throwable);throw throwable;}
    }

    protected void activityChangeAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.activityChangeAsync()",this);try{final ChanIdentifiedActivity activity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity$4.run()",this);try{if (NetworkProfileManager.instance().getActivity() != activity) {
                    if (DEBUG) {Log.i(TAG, "onResume() activityChange to " + activity.getChanActivityId() );}
                    NetworkProfileManager.instance().activityChange(activity);
                }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity$4.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity$4.run()",this,throwable);throw throwable;}
            }
        }).start();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.activityChangeAsync()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.activityChangeAsync()",this,throwable);throw throwable;}
    }

    private void setProgressBar(boolean on) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.setProgressBar(boolean)",this,on);try{if (progressBar != null) {
            if (DEBUG) {Log.i(TAG, "setProgressBar(" + on + ")");}
            if (on)
                {progressBar.setVisibility(View.VISIBLE);}
            else
                {progressBar.setVisibility(View.GONE);}
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.setProgressBar(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.setProgressBar(boolean)",this,throwable);throw throwable;}
    }

    private long calcDelayMs() {
        com.mijack.Xlog.logMethodEnter("long com.chanapps.four.activity.GalleryViewActivity.calcDelayMs()",this);try{boolean loaded;
        if (thread == null || thread.defData)
            {loaded = false;}
        else if (thread.isDead)
            {loaded = true;}
        else if (thread.posts == null || thread.posts.length == 0)
            {loaded = false;}
        else if (thread.posts[0] == null || thread.posts[0].defData)
            {loaded = false;}
        else if (thread.posts.length < thread.posts[0].images)
            {loaded = false;}
        else
            {loaded = true;}

        if (DEBUG) {Log.i(TAG, "calcDelayMs() loaded=" + loaded + " tryCount=" + loadTryCount + " thread=" + thread.toString());}

        if (++loadTryCount > MAX_LOAD_TRIES) {
            loadTryCount = 0;
            {com.mijack.Xlog.logMethodExit("long com.chanapps.four.activity.GalleryViewActivity.calcDelayMs()",this);return 0;}
        }
        else if (loaded)
            {{com.mijack.Xlog.logMethodExit("long com.chanapps.four.activity.GalleryViewActivity.calcDelayMs()",this);return 0;}}
        else if (NetworkProfileManager.instance().getCurrentProfile().getConnectionHealth() == NetworkProfile.Health.NO_CONNECTION)
            {{com.mijack.Xlog.logMethodExit("long com.chanapps.four.activity.GalleryViewActivity.calcDelayMs()",this);return 0;}}
        else
            {{com.mijack.Xlog.logMethodExit("long com.chanapps.four.activity.GalleryViewActivity.calcDelayMs()",this);return NetworkProfileManager.instance().getFetchParams().readTimeout / 10;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.activity.GalleryViewActivity.calcDelayMs()",this,throwable);throw throwable;}
    }

    private static final int MAX_LOAD_TRIES = 5;
    private int loadTryCount = 0;

    private void prepareGalleryView() {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.prepareGalleryView()",this);try{handler = new ProgressHandler(this);
    	View contentView = inflater.inflate(R.layout.gallery_layout,
    			(ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content), false);
    	setContentView(contentView);
    	super.mGLRootView = (GLRootView) contentView.findViewById(R.id.gl_root_view);

    	Bundle data = new Bundle();
    	try {
	    	switch(viewType) {
	    	case PHOTO_VIEW:
	    		data.putString(PhotoPage.KEY_MEDIA_SET_PATH, 
	    				Path.fromString("/chan/" + boardCode + "/" + threadNo).toString());
	    		data.putString(PhotoPage.KEY_MEDIA_ITEM_PATH, 
	    				Path.fromString("/chan/" + boardCode + "/" + threadNo + "/" + postNo).toString());
                if (DEBUG) {Log.i(TAG, "starting photo state");}
	    		getStateManager().startState(PhotoPage.class, data);
	    		break;
	    	case ALBUM_VIEW:
                data.putString(AlbumPage.KEY_MEDIA_PATH,
	    				Path.fromString("/chan/" + boardCode + "/" + threadNo).toString());
                if (DEBUG) {Log.i(TAG, "starting album state");}
                getStateManager().startState(AlbumPage.class, data);
	    		break;
	    	case OFFLINE_ALBUM_VIEW:
                data.putString(AlbumPage.KEY_MEDIA_PATH,
	    				Path.fromString("/chan-offline/" + boardCode).toString());
                if (DEBUG) {Log.i(TAG, "starting offline album state");}
                getStateManager().startState(AlbumPage.class, data);
	    		break;
	    	case OFFLINE_ALBUMSET_VIEW:
	    		data.putString(AlbumSetPage.KEY_MEDIA_PATH,
	    				Path.fromString("/chan-offline").toString());
                if (DEBUG) {Log.i(TAG, "starting offline albumset state");}
                getStateManager().startState(AlbumSetPage.class, data);
	    		break;
	    	}
    	} catch (Error e) {
    		Log.e(TAG, "Error initializing gallery, navagiting up, viewType: " + viewType + ", board: " + boardCode + ", threadNo: " + threadNo, e);
    		navigateUp();
    	} catch (Exception e) {
    		Log.e(TAG, "Execption initializing gallery, navigating up, viewType: " + viewType + ", board: " + boardCode + ", threadNo: " + threadNo, e);
    		navigateUp();
    	}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.prepareGalleryView()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.prepareGalleryView()",this,throwable);throw throwable;}
    }

    @Override
    public void setContentView(int resId) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.setContentView(int)",this,resId);try{super.setContentView(resId);
        super.mGLRootView = (GLRootView) findViewById(R.id.gl_root_view);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.setContentView(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.setContentView(int)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.GalleryViewActivity.onOptionsItemSelected(android.view.MenuItem)",this,item);try{GLRoot root = getGLRoot();
        root.lockRenderThread();
        try {
        switch (item.getItemId()) {
            case android.R.id.home:
            	/*//if (DEBUG) Log.i(TAG, "Gallery state stack: " + getStateManager().getStackDescription());*/
            	/*//getStateManager().compactActivityStateStack();*/
            	/*//if (getStateManager().getStateCount() > 1) {*/
            	/*//	getStateManager().onBackPressed();*/
            	/*//} else {*/
            		navigateUp();
            	/*//}*/
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.GalleryViewActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.download_all_images_to_gallery_menu:
                ThreadImageDownloadService.startDownloadViaThreadMenu(getBaseContext(), boardCode, threadNo, new long[]{});
                Toast.makeText(this, R.string.download_all_images_notice, Toast.LENGTH_SHORT).show();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.GalleryViewActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            /*//case R.id.view_image_gallery_menu:*/
            /*//    GalleryViewActivity.startAlbumViewActivity(this, boardCode, threadNo);*/
            /*//    return true;*/
            case R.id.offline_board_view_menu:
            	GalleryViewActivity.startOfflineAlbumViewActivity(this, boardCode);
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.GalleryViewActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.offline_chan_view_menu:
            	GalleryViewActivity.startOfflineAlbumViewActivity(this, null);
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.GalleryViewActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.web_menu:
                String url;
                if (postNo > 0)
                    {url = ChanPost.postUrl(this, boardCode, threadNo, postNo);}
                else
                    {url = ChanThread.threadUrl(this, boardCode, threadNo);}
                ActivityDispatcher.launchUrlInBrowser(this, url);
            default:
            	{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.GalleryViewActivity.onOptionsItemSelected(android.view.MenuItem)",this);return getStateManager().itemSelected(item);}
        }        
	    } finally {
	        root.unlockRenderThread();
	    }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.GalleryViewActivity.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    private void navigateUp() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.navigateUp()",this);try{Pair<Integer, ActivityManager.RunningTaskInfo> p = ActivityDispatcher.safeGetRunningTasks(this);
        int numTasks = p.first;
        ActivityManager.RunningTaskInfo task = p.second;
        if (task != null) {
            if (DEBUG) {Log.i(TAG, "navigateUp() top=" + task.topActivity + " base=" + task.baseActivity);}
            if (task.baseActivity != null && !this.getClass().getName().equals(task.baseActivity.getClassName())) {
                if (DEBUG) {Log.i(TAG, "navigateUp() using finish instead of intents with me="
                        + this.getClass().getName() + " base=" + task.baseActivity.getClassName());}
                finish();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.navigateUp()",this);return;}
            }
        }

    	Intent intent = null;
    	switch(viewType) {
    	case PHOTO_VIEW:
    		intent = new Intent(this, ThreadActivity.class);
            intent.putExtra(BOARD_CODE, boardCode);
            intent.putExtra(THREAD_NO, threadNo);
            intent.putExtra(ChanPost.POST_NO, postNo);
    		break;
    	case ALBUM_VIEW:
    		intent = new Intent(this, ThreadActivity.class);
            intent.putExtra(BOARD_CODE, boardCode);
            intent.putExtra(THREAD_NO, threadNo);
    		break;
    	case OFFLINE_ALBUM_VIEW:
    		intent = new Intent(this, BoardActivity.class);
            intent.putExtra(BOARD_CODE, boardCode);
    		break;
    	case OFFLINE_ALBUMSET_VIEW:
            if (boardCode == null || boardCode.isEmpty())
                {boardCode = ChanBoard.defaultBoardCode(this);}
    		intent = BoardActivity.createIntent(this, boardCode, "");
    		intent.putExtra(ActivityDispatcher.IGNORE_DISPATCH, true);
            break;
    	}
        startActivity(intent);
        finish();
        /*//NavUtils.navigateUpTo(this, upIntent);*/}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.navigateUp()",this,throwable);throw throwable;}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.GalleryViewActivity.onCreateOptionsMenu(android.view.Menu)",this,menu);try{getStateManager().createOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gallery_view_menu, menu);
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.GalleryViewActivity.onCreateOptionsMenu(android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.GalleryViewActivity.onCreateOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
    }

    private void loadActionBarTitle() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.loadActionBarTitle()",this);try{title = "";
        if (boardCode != null && !boardCode.isEmpty()) {
            if (DEBUG) {Log.i(TAG, "about to load board data for action bar board=" + boardCode);}
            ChanBoard board = ChanFileStorage.loadBoardData(getApplicationContext(), boardCode);
            if (board == null) {
                board = ChanBoard.getBoardByCode(getApplicationContext(), boardCode);
            }
            String rawTitle = ChanBoard.getName(getApplicationContext(), boardCode);
            title = (rawTitle == null ? "Board" : rawTitle) + " /" + boardCode + "/";
            /*
            if (threadNo > 0) {
                String threadTitle = "";
                ChanThread thread = ChanFileStorage.loadThreadData(getApplicationContext(), boardCode, threadNo);
                if (thread != null)
                    threadTitle = thread.threadSubject(getApplicationContext());
                if (threadTitle.isEmpty())
                    threadTitle = "Thread " + threadNo;
                title += TITLE_SEPARATOR + threadTitle;
            }
            */
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.loadActionBarTitle()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.loadActionBarTitle()",this,throwable);throw throwable;}
    }

    private void setActionBarTitle() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.setActionBarTitle()",this);try{if (DEBUG) {Log.i(TAG, "setting action bar based on viewType=" + viewType);}
        if (getActionBar() == null) {
            if (DEBUG) {Log.i(TAG, "Action bar was null");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.setActionBarTitle()",this);return;}
        }
        switch(viewType) {
            case OFFLINE_ALBUMSET_VIEW:
                getActionBar().setTitle(R.string.offline_chan_view_menu);
                break;
            case OFFLINE_ALBUM_VIEW:
            case PHOTO_VIEW:
            case ALBUM_VIEW:
            default:
                getActionBar().setTitle(title);
        }
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (DEBUG) {Log.i(TAG, "Set action bar");}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity.setActionBarTitle()",this,throwable);throw throwable;}
    }

    private class ProgressHandler extends Handler {
    	GalleryViewActivity activity;
    	ProgressHandler(GalleryViewActivity activity) {
    		super();
    		this.activity = activity;
    	}
    	
    	@Override
        public void handleMessage(Message msg) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity$ProgressHandler.handleMessage(android.os.Message)",this,msg);try{super.handleMessage(msg);
            
            if (msg.what == PROGRESS_REFRESH_MSG) {
	            int localFileSize = msg.arg1 + 1;
	            int totalFileSize = msg.arg2;
	            if (DEBUG) {Log.i(TAG, "handle message: updating progress bar " + localFileSize);}
	            
	            if (progressBar != null) {
		            if (localFileSize != totalFileSize) {
			            progressBar.setVisibility(ProgressBar.VISIBLE);
			    		progressBar.setProgress(localFileSize);
			    		progressBar.setMax(totalFileSize);
		            } else {
		            	progressBar.setVisibility(ProgressBar.INVISIBLE);
		            }
	            }
            } else if (msg.what == UPDATE_POSTNO_MSG) {
	            String postNo = (String)msg.obj;
	            activity.postNo = Long.parseLong(postNo);
            	if (DEBUG) {Log.w(TAG, "Updated last viewed image: " + activity.postNo);}
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity$ProgressHandler.handleMessage(android.os.Message)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.GalleryViewActivity$ProgressHandler.handleMessage(android.os.Message)",this,throwable);throw throwable;}
            
        }
    }

	@Override
    public GalleryActionBar getGalleryActionBar() {
    	com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.GalleryActionBar com.chanapps.four.activity.GalleryViewActivity.getGalleryActionBar()",this);try{if (actionBar == null) {
    		actionBar = new GalleryActionBar(this);
    		setActionBarTitle();
    	}
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.GalleryActionBar com.chanapps.four.activity.GalleryViewActivity.getGalleryActionBar()",this);return actionBar;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.GalleryActionBar com.chanapps.four.activity.GalleryViewActivity.getGalleryActionBar()",this,throwable);throw throwable;}
    }
    
    @Override
	public ChanActivityId getChanActivityId() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.ChanActivityId com.chanapps.four.activity.GalleryViewActivity.getChanActivityId()",this);try{ViewType type;
        try {
            type = currentViewType();
        }
        catch (AssertionError e) {
            if (DEBUG) {Log.i(TAG, "getChanActivityId() /" + boardCode + "/" + threadNo + " buggered out on view type error, defaulting", e);}
            type = defaultViewType();
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ChanActivityId com.chanapps.four.activity.GalleryViewActivity.getChanActivityId()",this);return new ChanActivityId(LastActivity.GALLERY_ACTIVITY, boardCode, threadNo, postNo, type);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.ChanActivityId com.chanapps.four.activity.GalleryViewActivity.getChanActivityId()",this,throwable);throw throwable;}
    }

    protected ViewType defaultViewType() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.defaultViewType()",this);try{if (boardCode == null || boardCode.isEmpty())
            {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.defaultViewType()",this);return ViewType.OFFLINE_ALBUMSET_VIEW;}}
        else if (threadNo <= 0)
            {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.defaultViewType()",this);return ViewType.OFFLINE_ALBUM_VIEW;}}
        else if (postNo <= 0)
            {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.defaultViewType()",this);return ViewType.ALBUM_VIEW;}}
        else
            {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.defaultViewType()",this);return ViewType.PHOTO_VIEW;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.defaultViewType()",this,throwable);throw throwable;}
    }

    protected ViewType currentViewType() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.currentViewType()",this);try{ActivityState activityState = getStateManager().getTopState();
        ViewType t;
        if (activityState == null)
            {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.currentViewType()",this);return ViewType.OFFLINE_ALBUMSET_VIEW;}}
        if (activityState instanceof PhotoPage)
            {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.currentViewType()",this);return ViewType.PHOTO_VIEW;}}
        if (activityState instanceof AlbumPage) {
            Bundle data = activityState.getData();
            if (data == null)
                {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.currentViewType()",this);return ViewType.OFFLINE_ALBUM_VIEW;}}
            String path = data.getString(AlbumPage.KEY_MEDIA_PATH);
            if (path == null || path.isEmpty())
                {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.currentViewType()",this);return ViewType.OFFLINE_ALBUM_VIEW;}}
            if (path.matches("/chan-offline/.*"))
                {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.currentViewType()",this);return ViewType.OFFLINE_ALBUM_VIEW;}}
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.currentViewType()",this);return ViewType.ALBUM_VIEW;}
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.currentViewType()",this);return ViewType.OFFLINE_ALBUMSET_VIEW;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.app.ViewType com.chanapps.four.activity.GalleryViewActivity.currentViewType()",this,throwable);throw throwable;}
    }
    
	@Override
	public Handler getChanHandler() {
		com.mijack.Xlog.logMethodEnter("android.os.Handler com.chanapps.four.activity.GalleryViewActivity.getChanHandler()",this);try{com.mijack.Xlog.logMethodExit("android.os.Handler com.chanapps.four.activity.GalleryViewActivity.getChanHandler()",this);return handler;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Handler com.chanapps.four.activity.GalleryViewActivity.getChanHandler()",this,throwable);throw throwable;}
	}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.refresh()",this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.refresh()",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.closeSearch()",this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.closeSearch()",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.setProgress(boolean)",this,on);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.setProgress(boolean)",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.GalleryViewActivity.switchBoard(com.android.gallery3d.app.String,com.android.gallery3d.app.String)",this,boardCode,query);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.GalleryViewActivity.switchBoard(com.android.gallery3d.app.String,com.android.gallery3d.app.String)",this);}

    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= 18) {
            if (hasFocus) {
                int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
                int newUiOptions = uiOptions;
                newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
                newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                newUiOptions ^= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                newUiOptions ^= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                //newUiOptions ^= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
            }
        }
    }
    */

}
