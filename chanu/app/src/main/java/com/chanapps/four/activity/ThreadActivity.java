package com.chanapps.four.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.widget.*;

import com.chanapps.four.adapter.AbstractBoardCursorAdapter;
import com.chanapps.four.adapter.BoardCursorAdapter;
import com.chanapps.four.adapter.BoardNarrowCursorAdapter;
import com.chanapps.four.component.*;
import com.chanapps.four.data.*;
import com.chanapps.four.data.LastActivity;
import com.chanapps.four.fragment.*;
import com.chanapps.four.loader.BoardCursorLoader;
import com.chanapps.four.loader.ChanImageLoader;
import com.chanapps.four.service.FetchChanDataService;
import com.chanapps.four.service.NetworkProfileManager;
import com.chanapps.four.service.profile.NetworkProfile;
import com.chanapps.four.viewer.BoardViewer;
import com.chanapps.four.viewer.ThreadViewer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * Created with IntelliJ IDEA.
 * User: arley
 * Date: 11/27/12
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */

public class ThreadActivity
        /*//extends AbstractBoardSpinnerActivity*/
        extends AbstractDrawerActivity
        implements ChanIdentifiedActivity
{

    public static final String TAG = ThreadActivity.class.getSimpleName();
    public static final boolean DEBUG = false;

    public static final String BOARD_CODE = "boardCode";
    public static final String THREAD_NO = "threadNo";
    public static final String POST_NO = "postNo";
    protected static final int OFFSCREEN_PAGE_LIMIT = 1;
    protected static final int LOADER_ID = 1;
    protected static final String FIRST_VISIBLE_BOARD_POSITION = "firstVisibleBoardPosition";
    protected static final String FIRST_VISIBLE_BOARD_POSITION_OFFSET = "firstVisibleBoardPositionOffset";
    protected static final String UPDATE_FAST_SCROLL_ACTION = "updateFastScrollAction";
    protected static final String OPTION_ENABLE = "optionEnable";

    protected ThreadPagerAdapter mAdapter;
    protected ControllableViewPager mPager;
    protected Handler handler;
    protected String query = "";
    protected MenuItem searchMenuItem;
    protected long postNo; /*// for direct jumps from latest post / recent images*/
    protected PullToRefreshAttacher mPullToRefreshAttacher;
    protected boolean narrowTablet;
    protected View layout;

    /*//tablet layout*/
    protected AbstractBoardCursorAdapter adapterBoardsTablet;
    protected AbsListView boardGrid;
    protected int firstVisibleBoardPosition = -1;
    protected int firstVisibleBoardPositionOffset = -1;
    protected boolean tabletTestDone = false;
    protected int columnWidth = 0;
    protected int columnHeight = 0;

    public static void startActivity(Context from, ChanActivityId aid) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.activity.ThreadActivity.startActivity(com.chanapps.four.component.Context,com.chanapps.four.component.ChanActivityId)",from,aid);try{startActivity(from, aid.boardCode, aid.threadNo, aid.postNo, aid.text);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.ThreadActivity.startActivity(com.chanapps.four.component.Context,com.chanapps.four.component.ChanActivityId)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.startActivity(com.chanapps.four.component.Context,com.chanapps.four.component.ChanActivityId)",throwable);throw throwable;}
    }

    public static void  startActivity(Context from, String boardCode, long threadNo, String query) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.activity.ThreadActivity.startActivity(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,com.chanapps.four.component.String)",from,boardCode,threadNo,query);try{startActivity(from, boardCode, threadNo, 0, query);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.ThreadActivity.startActivity(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,com.chanapps.four.component.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.startActivity(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,com.chanapps.four.component.String)",throwable);throw throwable;}
    }

    public static void startActivity(Context from, String boardCode, long threadNo, long postNo, String query) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.activity.ThreadActivity.startActivity(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,long,com.chanapps.four.component.String)",from,boardCode,threadNo,postNo,query);try{if (DEBUG) {Log.i(TAG, "startActivity /" + boardCode + "/" + threadNo + "#p" + postNo + " q=" + query);}
        if (threadNo <= 0) {
            BoardActivity.startActivity(from, boardCode, query);
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.ThreadActivity.startActivity(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,long,com.chanapps.four.component.String)");return;}
        }
        if (from instanceof ThreadActivity) { /*// switch thread instead of launching activity*/
            ((ThreadActivity)from).switchThreadInternal(boardCode, threadNo, postNo, query);
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.ThreadActivity.startActivity(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,long,com.chanapps.four.component.String)");return;}
        }
        if (postNo <= 0 || postNo == threadNo)
            {from.startActivity(createIntent(from, boardCode, threadNo, query));}
        else
            {from.startActivity(createIntent(from, boardCode, threadNo, postNo, query));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.startActivity(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,long,com.chanapps.four.component.String)",throwable);throw throwable;}
    }

    public static Intent createIntent(Context context, final String boardCode, final long threadNo, String query) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.component.Intent com.chanapps.four.activity.ThreadActivity.createIntent(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,com.chanapps.four.component.String)",context,boardCode,threadNo,query);try{com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.component.Intent com.chanapps.four.activity.ThreadActivity.createIntent(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,com.chanapps.four.component.String)");return createIntent(context, boardCode, threadNo, 0, query);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.component.Intent com.chanapps.four.activity.ThreadActivity.createIntent(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,com.chanapps.four.component.String)",throwable);throw throwable;}
    }

    public static Intent createIntent(Context context, final String boardCode,
                                      final long threadNo, final long postNo, String query) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.component.Intent com.chanapps.four.activity.ThreadActivity.createIntent(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,long,com.chanapps.four.component.String)",context,boardCode,threadNo,postNo,query);try{Intent intent = new Intent(context, ThreadActivity.class);
        intent.putExtra(BOARD_CODE, boardCode);
        intent.putExtra(THREAD_NO, threadNo);
        intent.putExtra(POST_NO, postNo);
        intent.putExtra(SearchManager.QUERY, query);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.component.Intent com.chanapps.four.activity.ThreadActivity.createIntent(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,long,com.chanapps.four.component.String)");return intent;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.component.Intent com.chanapps.four.activity.ThreadActivity.createIntent(com.chanapps.four.component.Context,com.chanapps.four.component.String,long,long,com.chanapps.four.component.String)",throwable);throw throwable;}
    }

    @Override
    public boolean isSelfDrawerMenu(String boardAsMenu) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.ThreadActivity.isSelfDrawerMenu(com.chanapps.four.component.String)",this,boardAsMenu);try{if (boardAsMenu == null || boardAsMenu.isEmpty())
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.isSelfDrawerMenu(com.chanapps.four.component.String)",this);return false;}}
        if (boardAsMenu.matches("/" + boardCode + "/" + threadNo + ".*") && (query == null || query.isEmpty()))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.isSelfDrawerMenu(com.chanapps.four.component.String)",this);return true;}}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.isSelfDrawerMenu(com.chanapps.four.component.String)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.ThreadActivity.isSelfDrawerMenu(com.chanapps.four.component.String)",this,throwable);throw throwable;}
    }

    @Override
    protected void createViews(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.createViews(android.os.Bundle)",this,bundle);try{if (bundle != null)
            {onRestoreInstanceState(bundle);}
        else
            {setFromIntent(getIntent());}
        if (DEBUG) {Log.i(TAG, "createViews() /" + boardCode + "/" + threadNo + "#p" + postNo + " q=" + query);}
        if (boardCode == null || boardCode.isEmpty())
            {boardCode = ChanBoard.defaultBoardCode(this);}
        if (threadNo <= 0)
            {redirectToBoard();}

        FrameLayout contentFrame = (FrameLayout)findViewById(R.id.content_frame);
        if (contentFrame.getChildCount() > 0)
            {contentFrame.removeAllViews();}
        layout = getLayoutInflater().inflate(R.layout.thread_activity_layout, null);
        contentFrame.addView(layout);

        try {
            mPullToRefreshAttacher = new PullToRefreshAttacher(this, new PullToRefreshAttacher.Options());
        }
        catch (OutOfMemoryError e) {
            Log.e(TAG, "createViews() couldn't load pull to refresh, out of memory");
            mPullToRefreshAttacher = null;
        }
        ThreadViewer.initStatics(getApplicationContext(), ThemeSelector.instance(getApplicationContext()).isDark());

        narrowTablet = getResources().getBoolean(R.bool.narrow_tablet);
        setupReceivers();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.createViews(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.createViews(android.os.Bundle)",this,throwable);throw throwable;}
    }

    protected void setupReceivers() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.setupReceivers()",this);try{LocalBroadcastManager.getInstance(this).registerReceiver(onUpdateFastScrollReceived,
                new IntentFilter(UPDATE_FAST_SCROLL_ACTION));com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.setupReceivers()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.setupReceivers()",this,throwable);throw throwable;}
    }
    
    protected void teardownReceivers() {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.teardownReceivers()",this);try{LocalBroadcastManager.getInstance(this).unregisterReceiver(onUpdateFastScrollReceived);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.teardownReceivers()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.teardownReceivers()",this,throwable);throw throwable;}
    }

    protected void createPager(final ChanBoard board) { com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.createPager(com.chanapps.four.component.ChanBoard)",this,board);try{/*// must be called on UI thread*/
        if (DEBUG) {Log.i(TAG, "createPager /" + (board == null ? null : board.link) + "/ q=" + query);}
        if (onTablet())
            {getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);} /*// board loader for tablet view*/
        if (query != null && !query.isEmpty()) {
            if (mPager != null)
                {mPager.removeAllViews();}
            mPager = null;
            mAdapter = null;
        }
        else if (mPager != null && mAdapter != null && mAdapter.getBoardCode() != null
                && mAdapter.getBoardCode().equals(board.link)) {
            if (DEBUG) {Log.i(TAG, "createPager() pager already exists, exiting");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.createPager(com.chanapps.four.component.ChanBoard)",this);return;}
        }
        if (mAdapter == null)
            {mAdapter = new ThreadPagerAdapter(getSupportFragmentManager());}
        mAdapter.setBoard(board);
        mAdapter.setQuery(query);
        mAdapter.notifyDataSetChanged();
        if (mPager == null)
            {mPager = (ControllableViewPager) findViewById(R.id.pager);}
        try {
            mPager.setAdapter(mAdapter);
            boolean pagingEnabled = query == null || query.isEmpty();
            mPager.setPagingEnabled(pagingEnabled);
        }
        catch (IllegalStateException e) {
            Log.e(TAG, "Error: pager state exception", e);
            Toast.makeText(ThreadActivity.this, R.string.thread_couldnt_create_pager, Toast.LENGTH_SHORT);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.createPager(com.chanapps.four.component.ChanBoard)",this,throwable);throw throwable;}
    }

    public void showThread(long threadNo) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.showThread(long)",this,threadNo);try{this.threadNo = threadNo;
        syncPagerAsync();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.showThread(long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.showThread(long)",this,throwable);throw throwable;}
    }

    protected void syncPagerOnHandler(final ChanBoard board) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.syncPagerOnHandler(com.chanapps.four.component.ChanBoard)",this,board);try{if (handler != null)
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$1.run()",this);try{syncPager(board);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$1.run()",this,throwable);throw throwable;}
                }
            });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.syncPagerOnHandler(com.chanapps.four.component.ChanBoard)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.syncPagerOnHandler(com.chanapps.four.component.ChanBoard)",this,throwable);throw throwable;}
    }

    protected void syncPager(final ChanBoard board) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.syncPager(com.chanapps.four.component.ChanBoard)",this,board);try{int pos = board.getThreadIndex(boardCode, threadNo);
        if (mAdapter != null && pos >= 0 && pos < mAdapter.getCount()) { /*// found it*/
            if (DEBUG) {Log.i(TAG, "syncPager /" + boardCode + "/" + threadNo + " setting pos=" + pos);}
            if (pos == mPager.getCurrentItem()) /*// it's already selected, do nothing*/
                {;}
            else
                {mPager.setCurrentItem(pos, false);} /*// select the item*/
        }
        else { /*// we didn't find it, default to 0th thread*/
            if (DEBUG) {Log.i(TAG, "syncPager /" + boardCode + "/" + threadNo + " not found pos=" + pos + " defaulting to zero");}
            pos = 0;
            if (mPager != null)
                {mPager.setCurrentItem(pos, false);} /*// select the item*/
            ThreadFragment fragment = getCurrentFragment();
            if (fragment != null) {
                ChanActivityId activityId = fragment.getChanActivityId();
                boardCode = activityId.boardCode;
                threadNo = activityId.threadNo;
                Toast.makeText(getActivityContext(), R.string.thread_not_found, Toast.LENGTH_SHORT).show();
            }
        }
        /*//setProgress(false);*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.syncPager(com.chanapps.four.component.ChanBoard)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.syncPager(com.chanapps.four.component.ChanBoard)",this,throwable);throw throwable;}
    }

    protected void redirectToBoard() { com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.redirectToBoard()",this);try{/*// backup in case we are missing stuff*/
        Log.e(TAG, "Empty board code, redirecting to board /" + boardCode + "/");
        Intent intent = BoardActivity.createIntent(this, boardCode, "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.redirectToBoard()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.redirectToBoard()",this,throwable);throw throwable;}
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.onSaveInstanceState(android.os.Bundle)",this,bundle);try{super.onSaveInstanceState(bundle);
        bundle.putString(ChanBoard.BOARD_CODE, boardCode);
        bundle.putLong(ChanThread.THREAD_NO, threadNo);
        bundle.putString(SearchManager.QUERY, query);
        int boardPos = !onTablet() ? -1 : boardGrid.getFirstVisiblePosition();
        View boardView = !onTablet() ? null : boardGrid.getChildAt(0);
        int boardOffset = boardView == null ? 0 : boardView.getTop();
        bundle.putInt(FIRST_VISIBLE_BOARD_POSITION, boardPos);
        bundle.putInt(FIRST_VISIBLE_BOARD_POSITION_OFFSET, boardOffset);
        if (DEBUG) {Log.i(TAG, "onSaveInstanceState /" + boardCode + "/" + threadNo);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.onRestoreInstanceState(android.os.Bundle)",this,bundle);try{super.onRestoreInstanceState(bundle);
        boardCode = bundle.getString(ChanBoard.BOARD_CODE);
        threadNo = bundle.getLong(ChanThread.THREAD_NO, 0);
        query = bundle.getString(SearchManager.QUERY);
        firstVisibleBoardPosition = bundle.getInt(FIRST_VISIBLE_BOARD_POSITION);
        firstVisibleBoardPositionOffset = bundle.getInt(FIRST_VISIBLE_BOARD_POSITION_OFFSET);
        if (DEBUG) {Log.i(TAG, "onRestoreInstanceState /" + boardCode + "/" + threadNo);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.onRestoreInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.onRestoreInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void onNewIntent(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.onNewIntent(com.chanapps.four.component.Intent)",this,intent);try{if (DEBUG) {Log.i(TAG, "onNewIntent begin /" + intent.getStringExtra(ChanBoard.BOARD_CODE) + "/"
                + intent.getLongExtra(ChanThread.THREAD_NO, -1)
                + "#p" + intent.getLongExtra(ChanThread.POST_NO, -1)
                + " q=" + intent.getStringExtra(SearchManager.QUERY));}
        switchThreadInternal(intent.getStringExtra(ChanBoard.BOARD_CODE),
                intent.getLongExtra(ChanThread.THREAD_NO, -1),
                intent.getLongExtra(ChanThread.POST_NO, -1),
                intent.getStringExtra(SearchManager.QUERY));
        if (DEBUG) {Log.i(TAG, "onNewIntent end /" + boardCode + "/" + threadNo + " q=" + query);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.onNewIntent(com.chanapps.four.component.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.onNewIntent(com.chanapps.four.component.Intent)",this,throwable);throw throwable;}
    }

    public void setFromIntent(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.setFromIntent(com.chanapps.four.component.Intent)",this,intent);try{Uri data = intent.getData();
        if (data == null || intent.hasExtra(ChanBoard.BOARD_CODE)) {
            boardCode = intent.getStringExtra(ChanBoard.BOARD_CODE);
            threadNo = intent.getLongExtra(ChanThread.THREAD_NO, 0);
            postNo = intent.getLongExtra(ChanThread.POST_NO, 0);
            query = intent.getStringExtra(SearchManager.QUERY);
        }
        else {
            List<String> params = data.getPathSegments();
            String uriBoardCode = params.get(0);
            String uriThreadNo = params.get(1);
            if (ChanBoard.getBoardByCode(this, uriBoardCode) != null && uriThreadNo != null) {
                boardCode = uriBoardCode;
                threadNo = Long.valueOf(uriThreadNo);
                postNo = 0;
                query = "";
                if (DEBUG) {Log.i(TAG, "loaded /" + boardCode + "/" + threadNo + " from url intent");}
            }
            else {
                boardCode = ChanBoard.DEFAULT_BOARD_CODE;
                threadNo = 0;
                postNo = 0;
                query = "";
                if (DEBUG) {Log.e(TAG, "Received invalid boardCode=" + uriBoardCode + " from url intent, using default board");}
            }
        }
        if (DEBUG) {Log.i(TAG, "setFromIntent /" + boardCode + "/" + threadNo + "#p" + postNo + " q=" + query);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.setFromIntent(com.chanapps.four.component.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.setFromIntent(com.chanapps.four.component.Intent)",this,throwable);throw throwable;}
    }

    @Override
    protected void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.onStart()",this);try{super.onStart();
        if (DEBUG) {Log.i(TAG, "onStart /" + boardCode + "/" + threadNo);}
        if (getIntent() != null) {
            if (DEBUG) {Log.i(TAG, "onStart intent=/" + getIntent().getStringExtra(BOARD_CODE) + "/" + getIntent().getLongExtra(THREAD_NO, 0));}
        }
        if (handler == null)
            {handler = new Handler();}
        NetworkProfileManager.instance().activityChange(this);
        if (onTablet())
            {createAbsListView();}
        createPagerAsync();
        loadDrawerArray();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.onStart()",this,throwable);throw throwable;}
    }

    protected void createPagerAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.createPagerAsync()",this);try{final ChanIdentifiedActivity activity = this;
        if (mPager != null) {
            if (DEBUG) {Log.i(TAG, "createPagerAsync() pager already exists, exiting");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.createPagerAsync()",this);return;}
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$2.run()",this);try{NetworkProfile.Health health = NetworkProfileManager.instance().getCurrentProfile().getConnectionHealth();
                final ChanBoard board = BoardCursorLoader.loadBoardSorted(ThreadActivity.this, boardCode);
                if (mAdapter != null && mAdapter.getCount() > 0 && board.hasData() && board.isCurrent()) {
                    if (DEBUG) {Log.i(TAG, "createPagerAsync() /" + boardCode + "/" + threadNo + " adapter already loaded, skipping");}
                }
                else if (board.hasData()) { /*// && board.isCurrent()) {*/
                    if (DEBUG) {Log.i(TAG, "createPagerAsync() /" + boardCode + "/" + threadNo + " board has current data, loading");}
                    if (handler != null)
                        {handler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$2$1.run()",this);try{createPager(board);
                                syncPager(board);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$2$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$2$1.run()",this,throwable);throw throwable;}
                            }
                        });}
                }
                else if (board.hasData() &&
                        (health == NetworkProfile.Health.NO_CONNECTION
                        ))
                {
                    if (DEBUG) {Log.i(TAG, "createPagerAsync() /" + boardCode + "/" + threadNo + " board has old data but connection " + health + ", loading");}
                    if (handler != null)
                        {handler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$2$2.run()",this);try{createPager(board);
                                syncPager(board);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$2$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$2$2.run()",this,throwable);throw throwable;}
                            }
                        });}
                }
                else if (health == NetworkProfile.Health.NO_CONNECTION) {
                    if (DEBUG) {Log.i(TAG, "createPagerAsync() /" + boardCode + "/" + threadNo + " no board data and connection is down");}
                    if (handler != null)
                        {handler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$2$3.run()",this);try{Toast.makeText(getApplicationContext(), R.string.board_no_connection_load, Toast.LENGTH_SHORT).show();
                                /*//setProgress(false);*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$2$3.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$2$3.run()",this,throwable);throw throwable;}
                            }
                        });}
                }

                else {
                    if (DEBUG) {Log.i(TAG, "createPagerAsync() /" + boardCode + "/" + threadNo + " no board data, priority refreshing board");}
                    if (handler != null)
                        {handler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$2$4.run()",this);try{/*//setProgress(true);*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$2$4.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$2$4.run()",this,throwable);throw throwable;}
                            }
                        });}
                    FetchChanDataService.scheduleBoardFetch(ThreadActivity.this, boardCode, true, false);
                    refreshing = true;
                }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$2.run()",this,throwable);throw throwable;}
            }
        }).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.createPagerAsync()",this,throwable);throw throwable;}
    }

    public boolean refreshing = false;

    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.onResume()",this);try{super.onResume();
        if (DEBUG) {Log.i(TAG, "onResume /" + boardCode + "/" + threadNo + " q=" + query);}
        if (handler == null)
            {handler = new Handler();}
        if (redisplayPagerOnResume) {
            redisplayPagerOnResume = false;
            redisplayPager();
        }
        else {
            resumePager();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.onResume()",this,throwable);throw throwable;}
    }

    protected void resumePager() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.resumePager()",this);try{/*
        if (query != null && !query.isEmpty()) {
            if (DEBUG) Log.i(TAG, "resumePager /" + boardCode + "/" + threadNo + " q=" + query + " performing query");
            redisplayPager(boardCode, threadNo, "");
        }
        else
        */
        /*
        if (query != null && !query.isEmpty()) {
            if (DEBUG) Log.i(TAG, "resumePager /" + boardCode + "/" + threadNo + " awaiting query callback");
        }
        else
        */
        if (mAdapter != null && mAdapter.getCount() > 0) {
            if (DEBUG) {Log.i(TAG, "resumePager /" + boardCode + "/" + threadNo + " setting current item pager count=" + mAdapter.getCount());}
            syncPagerAsync();
        }
        else {
            if (DEBUG) {Log.i(TAG, "resumePager /" + boardCode + "/" + threadNo + " pager not loaded, awaiting callback");}
        }
        if (onTablet()
                && !getSupportLoaderManager().hasRunningLoaders()
                && (adapterBoardsTablet == null || adapterBoardsTablet.getCount() == 0)) {
            if (DEBUG) {Log.i(TAG, "resumePager calling restartLoader");}
            getSupportLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks); /*// board loader for tablet view*/
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.resumePager()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.resumePager()",this,throwable);throw throwable;}
    }

    protected void syncPagerAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.syncPagerAsync()",this);try{if (DEBUG) {Log.i(TAG, "syncPagerAsync() /" + boardCode + "/" + threadNo + " selecting current item in pager");}
        final ChanIdentifiedActivity activity = this;
        ThreadFragment fragment = getCurrentFragment();
        if (fragment != null) {
            ChanActivityId aid = fragment.getChanActivityId();
            boolean onThread = aid != null && boardCode != null && boardCode.equals(aid.boardCode) && threadNo == aid.threadNo;
            boolean sameQuery = aid != null && ((query == null && aid.text == null) || (query != null && query.equals(aid.text)));
            if (onThread && sameQuery) {
                if (DEBUG) {Log.i(TAG, "syncPagerAsync() already on thread with idential query, exiting");}
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.syncPagerAsync()",this);return;}
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$3.run()",this);try{if (NetworkProfileManager.instance().getActivity() != activity) {
                    if (DEBUG) {Log.i(TAG, "syncPagerAsync() storing activity change");}
                    NetworkProfileManager.instance().activityChange(activity);
                }
                ChanBoard board = BoardCursorLoader.loadBoardSorted(ThreadActivity.this, boardCode);
                int idx = board.getThreadIndex(boardCode, threadNo);
                if (idx == -1) {
                    if (DEBUG) {Log.i(TAG, "syncPagerAsync() thread not in board, waiting for board refresh");}
                }
                else {
                    if (DEBUG) {Log.i(TAG, "syncPagerAsync() set current item to thread");}
                    syncPagerOnHandler(board);
                }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$3.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$3.run()",this,throwable);throw throwable;}
            }
        }).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.syncPagerAsync()",this,throwable);throw throwable;}
    }

    @Override
    protected void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.onPause()",this);try{super.onPause();
        if (DEBUG) {Log.i(TAG, "onPause /" + boardCode + "/" + threadNo);}
        handler = null;
        ThreadFragment fragment = getCurrentFragment();
        ChanActivityId activityId = fragment == null ? null : fragment.getChanActivityId();
        if (activityId != null
                && activityId.boardCode != null
                && !activityId.boardCode.isEmpty()
                && activityId.threadNo > 0
                ) { /*// different activity*/
            /*// only change if thread doesn't exist in board*/
            /*//if (board.getThreadIndex(boardCode, threadNo) == -1) {*/
                boardCode = activityId.boardCode;
                threadNo = activityId.threadNo;
                if (DEBUG) {Log.i(TAG, "onPause save default thread to /" + boardCode + "/" + threadNo);}
            /*//}*/
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.onPause()",this,throwable);throw throwable;}
    }

    @Override
    protected void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.onStop()",this);try{super.onStop();
        if (DEBUG) {Log.i(TAG, "onStop /" + boardCode + "/" + threadNo);}
        handler = null;
        if (onTablet()) {
            if (DEBUG) {Log.i(TAG, "onStop calling destroyLoader");}
            getSupportLoaderManager().destroyLoader(LOADER_ID);
        }
        setProgress(false);
        teardownReceivers();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.onStop()",this,throwable);throw throwable;}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.ThreadActivity.onCreateOptionsMenu(com.chanapps.four.component.Menu)",this,menu);try{/*// menu creation handled at fragment level instead*/
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.onCreateOptionsMenu(com.chanapps.four.component.Menu)",this);return super.onCreateOptionsMenu(menu);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.ThreadActivity.onCreateOptionsMenu(com.chanapps.four.component.Menu)",this,throwable);throw throwable;}
    }

    public void createSearchView(Menu menu) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.createSearchView(com.chanapps.four.component.Menu)",this,menu);try{searchMenuItem = menu.findItem(R.id.search_menu);
        if (searchMenuItem != null)
            {SearchActivity.createSearchView(this, searchMenuItem);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.createSearchView(com.chanapps.four.component.Menu)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.createSearchView(com.chanapps.four.component.Menu)",this,throwable);throw throwable;}
    }

    public ChanActivityId getChanActivityId() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.ChanActivityId com.chanapps.four.activity.ThreadActivity.getChanActivityId()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ChanActivityId com.chanapps.four.activity.ThreadActivity.getChanActivityId()",this);return new ChanActivityId(LastActivity.THREAD_ACTIVITY, boardCode, threadNo, postNo, query);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.ChanActivityId com.chanapps.four.activity.ThreadActivity.getChanActivityId()",this,throwable);throw throwable;}
    }

    public void setChanActivityId(ChanActivityId aid) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.setChanActivityId(com.chanapps.four.component.ChanActivityId)",this,aid);try{boardCode = aid.boardCode;
        threadNo = aid.threadNo;
        postNo = aid.postNo;
        query = aid.text;com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.setChanActivityId(com.chanapps.four.component.ChanActivityId)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.setChanActivityId(com.chanapps.four.component.ChanActivityId)",this,throwable);throw throwable;}
    }

    @Override
    public void refresh() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.refresh()",this);try{refreshing = false;
        invalidateOptionsMenu(); /*// in case spinner needs to be reset*/
        ThreadFragment fragment = getCurrentFragment();
        if (fragment != null)
            {fragment.onRefresh();}
        if (handler != null && onTablet())
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$4.run()",this);try{if (DEBUG) {Log.i(TAG, "refreshBoard() restarting loader");}
                    getSupportLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$4.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$4.run()",this,throwable);throw throwable;}
                }
            });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.refresh()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.refresh()",this,throwable);throw throwable;}
    }

    public void refreshFragment(final String boardCode, final long threadNo, final String message) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.refreshFragment(com.chanapps.four.component.String,long,com.chanapps.four.component.String)",this,boardCode,threadNo,message);try{refreshing = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$5.run()",this);try{refreshFragmentSync(boardCode, threadNo, message);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$5.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$5.run()",this,throwable);throw throwable;}
            }
        }).start();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.refreshFragment(com.chanapps.four.component.String,long,com.chanapps.four.component.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.refreshFragment(com.chanapps.four.component.String,long,com.chanapps.four.component.String)",this,throwable);throw throwable;}
    }

    protected boolean redisplayPagerOnResume = false;

    protected void redisplayPager() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.redisplayPager()",this);try{redisplayPager(boardCode, threadNo, query, "");com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.redisplayPager()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.redisplayPager()",this,throwable);throw throwable;}
    }

    protected void redisplayPager(final String boardCode, final long threadNo, final String query, final String message) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.redisplayPager(com.chanapps.four.component.String,long,com.chanapps.four.component.String,com.chanapps.four.component.String)",this,boardCode,threadNo,query,message);try{if (DEBUG) {Log.i(TAG, "redisplayPager() /" + boardCode + "/" + threadNo + " q=" + query + " handler=" + handler);}
        final ChanBoard fragmentBoard = BoardCursorLoader.loadBoardSorted(ThreadActivity.this, boardCode);
        if (fragmentBoard.defData)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.redisplayPager(com.chanapps.four.component.String,long,com.chanapps.four.component.String,com.chanapps.four.component.String)",this);return;}}
        if (handler == null) {
            redisplayPagerOnResume = true;
        }
        else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$6.run()",this);try{if (DEBUG) {Log.i(TAG, "redisplayPager() /" + boardCode + "/" + threadNo + " q=" + query + " recreating pager on handler");}
                    if (onTablet()) {
                        createAbsListView();
                        getSupportLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks); /*// board loader for tablet view*/
                    }
                    createPager(fragmentBoard);
                    syncPager(fragmentBoard);
                    refreshAllDisplayedFragments(boardCode, threadNo, query, message);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$6.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$6.run()",this,throwable);throw throwable;}
                }
            });
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.redisplayPager(com.chanapps.four.component.String,long,com.chanapps.four.component.String,com.chanapps.four.component.String)",this,throwable);throw throwable;}
    }

    public void restartLoader() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.restartLoader()",this);try{getSupportLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks); /*// board loader for tablet view*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.restartLoader()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.restartLoader()",this,throwable);throw throwable;}
    }

    public void refreshFragmentSync(final String boardCode, final long threadNo, final String message) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.refreshFragmentSync(com.chanapps.four.component.String,long,com.chanapps.four.component.String)",this,boardCode,threadNo,message);try{if (DEBUG) {Log.i(TAG, "refreshFragment /" + boardCode + "/" + threadNo + " message=" + message);}
        if (hasValidPager())
            {refreshAllDisplayedFragments(boardCode, threadNo, query, message);}
        else
            {redisplayPager(boardCode, threadNo, query, message);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.refreshFragmentSync(com.chanapps.four.component.String,long,com.chanapps.four.component.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.refreshFragmentSync(com.chanapps.four.component.String,long,com.chanapps.four.component.String)",this,throwable);throw throwable;}
    }

    protected boolean hasValidPager() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.ThreadActivity.hasValidPager()",this);try{if (mPager == null) {
            if (DEBUG) {Log.i(TAG, "refreshFragment /" + boardCode + "/" + threadNo + " skipping, null pager");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.hasValidPager()",this);return false;}
        }
        if (mAdapter == null) {
            if (DEBUG) {Log.i(TAG, "refreshFragment /" + boardCode + "/" + threadNo + " skipping, null adapter");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.hasValidPager()",this);return false;}
        }
        if (!boardCode.equals(mAdapter.getBoardCode()))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.hasValidPager()",this);return false;}}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.hasValidPager()",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.ThreadActivity.hasValidPager()",this,throwable);throw throwable;}
    }

    protected void refreshAllDisplayedFragments(final String boardCode, final long threadNo, final String query, final String message) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.refreshAllDisplayedFragments(com.chanapps.four.component.String,long,com.chanapps.four.component.String,com.chanapps.four.component.String)",this,boardCode,threadNo,query,message);try{if (query != null && !query.isEmpty()) {
            if (DEBUG) {Log.i(TAG, "refreshAllDisplayedFragments() query present, exiting");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.refreshAllDisplayedFragments(com.chanapps.four.component.String,long,com.chanapps.four.component.String,com.chanapps.four.component.String)",this);return;}
        }
        if (handler != null)
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$7.run()",this);try{int current = mPager.getCurrentItem();
                    int delta = mPager.getOffscreenPageLimit();
                    boolean found = false;
                    for (int i = current - delta; i < current + delta + 1; i++) {
                        String msg = i == current ? message : null;
                        if (refreshFragmentAtPosition(boardCode, threadNo, i, msg))
                            {found = true;}
                    }
                    /*//if (!found)*/
                    /*//    setProgress(false);*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$7.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$7.run()",this,throwable);throw throwable;}
                }
            });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.refreshAllDisplayedFragments(com.chanapps.four.component.String,long,com.chanapps.four.component.String,com.chanapps.four.component.String)",this,throwable);throw throwable;}
    }

    protected void setCurrentItemAsync(final int pos, final boolean smooth) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.setCurrentItemAsync(int,boolean)",this,pos,smooth);try{if (handler != null)
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$8.run()",this);try{mPager.setCurrentItem(pos, smooth);
                    ThreadFragment fragment2;
                    if ((fragment2 = getFragmentAtPosition(pos)) != null
                            && fragment2.getChanActivityId() != null
                            && fragment2.getChanActivityId().threadNo > 0)
                        {fragment2.refreshThread(null);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$8.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$8.run()",this,throwable);throw throwable;}
                }
            });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.setCurrentItemAsync(int,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.setCurrentItemAsync(int,boolean)",this,throwable);throw throwable;}
    }


    protected boolean refreshFragmentAtPosition(String boardCode, long threadNo, int pos, String message) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.ThreadActivity.refreshFragmentAtPosition(com.chanapps.four.component.String,long,int,com.chanapps.four.component.String)",this,boardCode,threadNo,pos,message);try{ThreadFragment fragment;
        ChanActivityId data;
        if (pos < 0 || pos >= mAdapter.getCount()) {
            if (DEBUG) {Log.i(TAG, "refreshFragmentAtPosition /" + boardCode + "/" + threadNo + " pos=" + pos
                    + " out of bounds, skipping");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.refreshFragmentAtPosition(com.chanapps.four.component.String,long,int,com.chanapps.four.component.String)",this);return false;}
        }
        if ((fragment = getFragmentAtPosition(pos)) == null) {
            if (DEBUG) {Log.i(TAG, "refreshFragmentAtPosition /" + boardCode + "/" + threadNo + " pos=" + pos
                    + " null fragment at position, skipping");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.refreshFragmentAtPosition(com.chanapps.four.component.String,long,int,com.chanapps.four.component.String)",this);return false;}
        }
        if ((data = fragment.getChanActivityId()) == null) {
            if (DEBUG) {Log.i(TAG, "refreshFragmentAtPosition /" + boardCode + "/" + threadNo + " pos=" + pos
                    + " null getChanActivityId(), skipping");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.refreshFragmentAtPosition(com.chanapps.four.component.String,long,int,com.chanapps.four.component.String)",this);return false;}
        }
        if (data.boardCode == null || !data.boardCode.equals(boardCode) || data.threadNo != threadNo) {
            if (DEBUG) {Log.i(TAG, "refreshFragmentAtPosition /" + boardCode + "/" + threadNo + " pos=" + pos
                    + " unmatching data=/" + data.boardCode + "/" + data.threadNo + ", skipping");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.refreshFragmentAtPosition(com.chanapps.four.component.String,long,int,com.chanapps.four.component.String)",this);return false;}
        }
        fragment.setQuery("");
        if (DEBUG) {Log.i(TAG, "refreshFragmentAtPosition /" + boardCode + "/" + threadNo + " pos=" + pos + " refreshing");}
        fragment.refreshThread(message);
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.refreshFragmentAtPosition(com.chanapps.four.component.String,long,int,com.chanapps.four.component.String)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.ThreadActivity.refreshFragmentAtPosition(com.chanapps.four.component.String,long,int,com.chanapps.four.component.String)",this,throwable);throw throwable;}
    }

    @Override
    public void closeSearch() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.closeSearch()",this);try{if (DEBUG) {Log.i(TAG, "closeSearch /" + boardCode + "/" + threadNo + " q=" + query);}
        if (searchMenuItem != null)
            {searchMenuItem.collapseActionView();}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.closeSearch()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.closeSearch()",this,throwable);throw throwable;}
    }

    @Override
    public Handler getChanHandler() {
        com.mijack.Xlog.logMethodEnter("android.os.Handler com.chanapps.four.activity.ThreadActivity.getChanHandler()",this);try{ThreadFragment fragment = getCurrentFragment();
        {com.mijack.Xlog.logMethodExit("android.os.Handler com.chanapps.four.activity.ThreadActivity.getChanHandler()",this);return fragment == null ? handler : fragment.getHandler();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Handler com.chanapps.four.activity.ThreadActivity.getChanHandler()",this,throwable);throw throwable;}
    }

    @Override
    protected void createActionBar() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.createActionBar()",this);try{super.createActionBar();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.createActionBar()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.createActionBar()",this,throwable);throw throwable;}
    }

    protected Activity getActivity() {
        com.mijack.Xlog.logMethodEnter("android.app.Activity com.chanapps.four.activity.ThreadActivity.getActivity()",this);try{com.mijack.Xlog.logMethodExit("android.app.Activity com.chanapps.four.activity.ThreadActivity.getActivity()",this);return this;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.Activity com.chanapps.four.activity.ThreadActivity.getActivity()",this,throwable);throw throwable;}
    }

    protected Context getActivityContext() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.Context com.chanapps.four.activity.ThreadActivity.getActivityContext()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.Context com.chanapps.four.activity.ThreadActivity.getActivityContext()",this);return this;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.Context com.chanapps.four.activity.ThreadActivity.getActivityContext()",this,throwable);throw throwable;}
    }

    protected ChanIdentifiedActivity getChanActivity() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.ChanIdentifiedActivity com.chanapps.four.activity.ThreadActivity.getChanActivity()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ChanIdentifiedActivity com.chanapps.four.activity.ThreadActivity.getChanActivity()",this);return this;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.ChanIdentifiedActivity com.chanapps.four.activity.ThreadActivity.getChanActivity()",this,throwable);throw throwable;}
    }

    public ThreadFragment getCurrentFragment() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getCurrentFragment()",this);try{if (mPager == null)
            {{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getCurrentFragment()",this);return null;}}
        int i = mPager.getCurrentItem();
        {com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getCurrentFragment()",this);return getFragmentAtPosition(i);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getCurrentFragment()",this,throwable);throw throwable;}
    }

    protected ThreadFragment getFragmentAtPosition(int pos) {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getFragmentAtPosition(int)",this,pos);try{if (mAdapter == null)
            {{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getFragmentAtPosition(int)",this);return null;}}
        else
            {{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getFragmentAtPosition(int)",this);return mAdapter.getCachedItem(pos);}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getFragmentAtPosition(int)",this,throwable);throw throwable;}
    }

    public class ThreadPagerAdapter extends FragmentStatePagerAdapter {
        protected String boardCode;
        protected ChanBoard board;
        protected String query;
        protected int count;
        protected Map<Integer,WeakReference<ThreadFragment>> fragments
                = new HashMap<Integer, WeakReference<ThreadFragment>>();
        public ThreadPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public void setBoard(ChanBoard board) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.setBoard(com.chanapps.four.component.ChanBoard)",this,board);try{if (board == null || board.threads == null)
                {throw new UnsupportedOperationException("can't start pager with null board or null threads");}
            this.boardCode = board.link;
            this.board = board;
            this.count = board.threads == null ? 0 : board.threads.length;
            super.notifyDataSetChanged();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.setBoard(com.chanapps.four.component.ChanBoard)",this,throwable);throw throwable;}
        }
        public String getBoardCode() {
            com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.String com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getBoardCode()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.String com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getBoardCode()",this);return this.boardCode;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.String com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getBoardCode()",this,throwable);throw throwable;}
        }
        public void setQuery(String query) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.setQuery(com.chanapps.four.component.String)",this,query);try{this.query = query;com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.setQuery(com.chanapps.four.component.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.setQuery(com.chanapps.four.component.String)",this,throwable);throw throwable;}
        }
        @Override
        public void notifyDataSetChanged() {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.notifyDataSetChanged()",this);try{board = BoardCursorLoader.loadBoardSorted(ThreadActivity.this, boardCode);
            count = board.threads.length;
            super.notifyDataSetChanged();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.notifyDataSetChanged()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.notifyDataSetChanged()",this,throwable);throw throwable;}
        }
        @Override
        public int getCount() {
            com.mijack.Xlog.logMethodEnter("int com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getCount()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getCount()",this);return count;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getCount()",this,throwable);throw throwable;}
        }
        @Override
        public Fragment getItem(int pos) {
            com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.Fragment com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getItem(int)",this,pos);try{if (pos < count)
                {{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.Fragment com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getItem(int)",this);return createFragment(pos);}}
            else
                {{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.Fragment com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getItem(int)",this);return null;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.Fragment com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getItem(int)",this,throwable);throw throwable;}
        }
        protected Fragment createFragment(int pos) {
            com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.Fragment com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.createFragment(int)",this,pos);try{/*// get thread*/
            ChanPost thread = board.threads[pos];
            String boardCode = thread.board;
            long threadNo = thread.no;
            long postNo = (boardCode != null
                    && boardCode.equals(ThreadActivity.this.boardCode)
                    && threadNo == ThreadActivity.this.threadNo)
                    && ThreadActivity.this.postNo > 0
                    ? ThreadActivity.this.postNo
                    : -1;
            String query = this.query;
            /*// make fragment*/
            ThreadFragment fragment = new ThreadFragment();
            Bundle bundle = new Bundle();
            bundle.putString(BOARD_CODE, boardCode);
            bundle.putLong(THREAD_NO, threadNo);
            bundle.putLong(POST_NO, postNo);
            bundle.putString(SearchManager.QUERY, query);
            if (DEBUG) {Log.i(TAG, "createFragment /" + boardCode + "/" + threadNo + "#p" + postNo + " q=" + query);}
            fragment.setArguments(bundle);
            fragment.setHasOptionsMenu(true);
            {com.mijack.Xlog.logMethodExit("com.chanapps.four.component.Fragment com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.createFragment(int)",this);return fragment;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.Fragment com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.createFragment(int)",this,throwable);throw throwable;}
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.Object com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.instantiateItem(com.chanapps.four.component.ViewGroup,int)",this,container,position);try{Object object = super.instantiateItem(container, position);
            fragments.put(position, new WeakReference<ThreadFragment>((ThreadFragment)object));
            {com.mijack.Xlog.logMethodExit("com.chanapps.four.component.Object com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.instantiateItem(com.chanapps.four.component.ViewGroup,int)",this);return object;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.Object com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.instantiateItem(com.chanapps.four.component.ViewGroup,int)",this,throwable);throw throwable;}
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.destroyItem(com.chanapps.four.component.ViewGroup,int,com.chanapps.four.component.Object)",this,container,position,object);try{super.destroyItem(container, position, object);
            fragments.remove(position);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.destroyItem(com.chanapps.four.component.ViewGroup,int,com.chanapps.four.component.Object)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.destroyItem(com.chanapps.four.component.ViewGroup,int,com.chanapps.four.component.Object)",this,throwable);throw throwable;}
        }
        public ThreadFragment getCachedItem(int position) {
            com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getCachedItem(int)",this,position);try{WeakReference<ThreadFragment> ref = fragments.get(position);
            if (ref == null)
                {{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getCachedItem(int)",this);return null;}}
            else
                {{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getCachedItem(int)",this);return ref.get();}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.getCachedItem(int)",this,throwable);throw throwable;}
        }
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.setPrimaryItem(com.chanapps.four.component.ViewGroup,int,com.chanapps.four.component.Object)",this,container,position,object);try{ThreadFragment fragment = (ThreadFragment)object;
            if (primaryItem != fragment && fragment.getChanActivityId().threadNo > 0) {
                if (DEBUG) {Log.i(TAG, "setPrimaryItem pos=" + position + " obj=" + fragment
                        + " rebinding mPullToRefreshAttacher");}
                if (primaryItem != null)
                    {primaryItem.setPullToRefreshAttacher(null);}
                primaryItem = fragment;
                primaryItem.fetchIfNeeded(handler);
                fragment.setPullToRefreshAttacher(mPullToRefreshAttacher);
                if (onTablet()) {
                    int first = boardGrid.getFirstVisiblePosition();
                    int last = boardGrid.getLastVisiblePosition();
                    boolean positionVisible = first <= position && position <= last;
                    if (!positionVisible) {
                        if (DEBUG) {Log.i(TAG, "scrolling to pos=" + position + " not in range [" + first + "," + last + "]");}
                        final int toPos = position;
                        boardGrid.post(new Runnable() {
                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter$1.run()",this);try{boardGrid.setSelection(toPos);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter$1.run()",this,throwable);throw throwable;}
                            }
                        });
                    }
                    else {
                        if (DEBUG) {Log.i(TAG, "not scrolling to pos=" + position + " in range [" + first + "," + last + "]");}
                    }
                }
            }
            super.setPrimaryItem(container, position, object);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.setPrimaryItem(com.chanapps.four.component.ViewGroup,int,com.chanapps.four.component.Object)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$ThreadPagerAdapter.setPrimaryItem(com.chanapps.four.component.ViewGroup,int,com.chanapps.four.component.Object)",this,throwable);throw throwable;}
        }
        protected ThreadFragment primaryItem = null;
    }

    public ThreadFragment getPrimaryItem() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getPrimaryItem()",this);try{if (mAdapter == null)
            {{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getPrimaryItem()",this);return null;}}
        else
            {{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getPrimaryItem()",this);return mAdapter.primaryItem;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.ThreadFragment com.chanapps.four.activity.ThreadActivity.getPrimaryItem()",this,throwable);throw throwable;}
    }

    public void setProgressForFragment(String boardCode, long threadNo, boolean on) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.setProgressForFragment(com.chanapps.four.component.String,long,boolean)",this,boardCode,threadNo,on);try{ThreadFragment fragment = getCurrentFragment();
        if (fragment == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.setProgressForFragment(com.chanapps.four.component.String,long,boolean)",this);return;}}
        ChanActivityId data = fragment.getChanActivityId();
        if (data == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.setProgressForFragment(com.chanapps.four.component.String,long,boolean)",this);return;}}
        if (data.boardCode == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.setProgressForFragment(com.chanapps.four.component.String,long,boolean)",this);return;}}
        if (!data.boardCode.equals(boardCode))
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.setProgressForFragment(com.chanapps.four.component.String,long,boolean)",this);return;}}
        if (data.threadNo != threadNo)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.setProgressForFragment(com.chanapps.four.component.String,long,boolean)",this);return;}}
        setProgress(on);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.setProgressForFragment(com.chanapps.four.component.String,long,boolean)",this,throwable);throw throwable;}
    }

    @Override
    public void setProgress(boolean on) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.setProgress(boolean)",this,on);try{if (DEBUG) {Log.i(TAG, "setProgress(" + on + ")");}
        if (mPullToRefreshAttacher != null) {
            if (DEBUG) {Log.i(TAG, "mPullToRefreshAttacher.setRefreshing(" + on + ")");}
            mPullToRefreshAttacher.setRefreshing(on);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.setProgress(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.setProgress(boolean)",this,throwable);throw throwable;}
    }

    protected void initTablet() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.initTablet()",this);try{if (!tabletTestDone) {
            boardGrid = (AbsListView)findViewById(R.id.board_grid_view_tablet);
            tabletTestDone = true;
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.initTablet()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.initTablet()",this,throwable);throw throwable;}
    }

    public boolean onTablet() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.ThreadActivity.onTablet()",this);try{initTablet();
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.onTablet()",this);return boardGrid != null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.ThreadActivity.onTablet()",this,throwable);throw throwable;}
    }

    protected void createAbsListView() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.createAbsListView()",this);try{initTablet();
        if (adapterBoardsTablet != null && adapterBoardsTablet.getCount() > 0) {
            if (DEBUG) {Log.i(TAG, "createAbsListView() /" + boardCode + "/" + threadNo + " adapter already loaded, skipping");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.createAbsListView()",this);return;}
        }
        if (DEBUG) {Log.i(TAG, "createAbsListView() /" + boardCode + "/" + threadNo + " creating adapter");}
        ImageLoader imageLoader = ChanImageLoader.getInstance(getActivityContext());
        columnWidth = ChanGridSizer.getCalculatedWidth(
                getResources().getDimensionPixelSize(R.dimen.BoardGridViewTablet_image_width),
                1,
                getResources().getDimensionPixelSize(R.dimen.BoardGridView_spacing));
        columnHeight = 2 * columnWidth;
        if (narrowTablet)
            {adapterBoardsTablet = new BoardNarrowCursorAdapter(this, viewBinder);}
        else
            {adapterBoardsTablet = new BoardCursorAdapter(this, viewBinder);}
        adapterBoardsTablet.setGroupBoardCode(boardCode);
        boardGrid.setAdapter(adapterBoardsTablet);
        boardGrid.setOnItemClickListener(boardGridListener);
        boardGrid.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true));
        boardGrid.setFastScrollEnabled(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(SettingsActivity.PREF_USE_FAST_SCROLL, false));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.createAbsListView()",this,throwable);throw throwable;}
    }

    protected void onBoardsTabletLoadFinished(Cursor data) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.onBoardsTabletLoadFinished(android.database.Cursor)",this,data);try{if (boardGrid == null)
            {createAbsListView();}
        /*//this.adapterBoardsTablet.swapCursor(data);*/
        this.adapterBoardsTablet.changeCursor(data);
        /*// retry load if maybe data wasn't there yet*/
        if (data != null && data.getCount() < 1 && handler != null) {
            if (DEBUG) {Log.i(TAG, "onBoardsTabletLoadFinished threadNo=" + threadNo + " data count=0");}
            NetworkProfile.Health health = NetworkProfileManager.instance().getCurrentProfile().getConnectionHealth();
            if (health == NetworkProfile.Health.NO_CONNECTION || health == NetworkProfile.Health.BAD) {
                String msg = String.format(getString(R.string.mobile_profile_health_status),
                        health.toString().toLowerCase().replaceAll("_", " "));
                Toast.makeText(getActivityContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }
        else if (firstVisibleBoardPosition >= 0) {
            if (DEBUG) {Log.i(TAG, "onBoardsTabletLoadFinished threadNo=" + threadNo + " firstVisibleBoardPosition=" + firstVisibleBoardPosition);}
            boardGrid.setSelection(firstVisibleBoardPosition);
            firstVisibleBoardPosition = -1;
            firstVisibleBoardPositionOffset = -1;
        }
        else if (threadNo > 0) {
            Cursor cursor = adapterBoardsTablet.getCursor();
            cursor.moveToPosition(-1);
            boolean found = false;
            int pos = 0;
            while (cursor.moveToNext()) {
                long threadNoAtPos = cursor.getLong(cursor.getColumnIndex(ChanThread.THREAD_NO));
                if (threadNoAtPos == threadNo) {
                    found = true;
                    break;
                }
                pos++;
            }
            if (found) {
                if (DEBUG) {Log.i(TAG, "onBoardsTabletLoadFinished threadNo=" + threadNo + " pos=" + pos);}
                final int selectedPos = pos;
                if (handler != null)
                    {handler.post(new Runnable() {
                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$9.run()",this);try{boardGrid.setSelection(selectedPos);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$9.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$9.run()",this,throwable);throw throwable;}
                        }
                    });}
            }
            else {
                if (DEBUG) {Log.i(TAG, "onBoardsTabletLoadFinished threadNo=" + threadNo + " thread not found");}
            }
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.onBoardsTabletLoadFinished(android.database.Cursor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.onBoardsTabletLoadFinished(android.database.Cursor)",this,throwable);throw throwable;}
    }

    protected LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            com.mijack.Xlog.logMethodEnter("android.support.v4.content.Loader com.chanapps.four.activity.ThreadActivity$10.onCreateLoader(int,android.os.Bundle)",this,id,args);try{if (DEBUG) {Log.i(TAG, "onCreateLoader /" + boardCode + "/ id=" + id);}
            BoardSortType sortType = BoardSortType.loadFromPrefs(ThreadActivity.this);
            {com.mijack.Xlog.logMethodExit("android.support.v4.content.Loader com.chanapps.four.activity.ThreadActivity$10.onCreateLoader(int,android.os.Bundle)",this);return new BoardCursorLoader(getActivityContext(), boardCode, "", true, false, sortType);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.support.v4.content.Loader com.chanapps.four.activity.ThreadActivity$10.onCreateLoader(int,android.os.Bundle)",this,throwable);throw throwable;}
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$10.onLoadFinished(android.support.v4.content.Loader,android.database.Cursor)",this,loader,data);try{if (DEBUG) {Log.i(TAG, "onLoadFinished /" + boardCode + "/ id=" + loader.getId()
                    + " count=" + (data == null ? 0 : data.getCount()) + " loader=" + loader);}
            onBoardsTabletLoadFinished(data);
            refreshing = false;com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$10.onLoadFinished(android.support.v4.content.Loader,android.database.Cursor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$10.onLoadFinished(android.support.v4.content.Loader,android.database.Cursor)",this,throwable);throw throwable;}
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$10.onLoaderReset(android.support.v4.content.Loader)",this,loader);try{if (DEBUG) {Log.i(TAG, "onLoaderReset /" + boardCode + "/ id=" + loader.getId());}
            adapterBoardsTablet.changeCursor(null);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$10.onLoaderReset(android.support.v4.content.Loader)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$10.onLoaderReset(android.support.v4.content.Loader)",this,throwable);throw throwable;}
        }
    };

    protected AbstractBoardCursorAdapter.ViewBinder viewBinder = new AbstractBoardCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.ThreadActivity$11.setViewValue(com.chanapps.four.component.View,android.database.Cursor,int)",this,view,cursor,columnIndex);try{int options = narrowTablet ? 0 : BoardViewer.CATALOG_GRID;
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity$11.setViewValue(com.chanapps.four.component.View,android.database.Cursor,int)",this);return BoardViewer.setViewValue(view, cursor, boardCode, columnWidth, columnHeight, null, null, options, null);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.ThreadActivity$11.setViewValue(com.chanapps.four.component.View,android.database.Cursor,int)",this,throwable);throw throwable;}
        }
    };

    protected AdapterView.OnItemClickListener boardGridListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$12.onItemClick(com.chanapps.four.component.AdapterView,com.chanapps.four.component.View,int,long)",this,parent,view,position,id);try{Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            int flags = cursor.getInt(cursor.getColumnIndex(ChanThread.THREAD_FLAGS));
            final String title = cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_SUBJECT));
            final String desc = cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_TEXT));
            if ((flags & ChanThread.THREAD_FLAG_BOARD) > 0) {
                final String boardLink = cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_BOARD_CODE));
                BoardActivity.startActivity(getActivityContext(), boardLink, "");
            }
            else {
                final String boardLink = cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_BOARD_CODE));
                final long threadNoLink = cursor.getLong(cursor.getColumnIndex(ChanThread.THREAD_NO));
                if (boardCode.equals(boardLink) && threadNo == threadNoLink) { /*// already on this, do nothing*/
                } else if (boardCode.equals(boardLink)) { /*// just redisplay right tab*/
                    showThread(threadNoLink);
                } else {
                    ThreadActivity.startActivity(getActivityContext(), boardLink, threadNoLink, "");
                }
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$12.onItemClick(com.chanapps.four.component.AdapterView,com.chanapps.four.component.View,int,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$12.onItemClick(com.chanapps.four.component.AdapterView,com.chanapps.four.component.View,int,long)",this,throwable);throw throwable;}
        }
    };

    public void notifyBoardChanged() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.notifyBoardChanged()",this);try{if (DEBUG) {Log.i(TAG, "notifyBoardChanged() /" + boardCode + "/");}
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$13.run()",this);try{final ChanBoard board = BoardCursorLoader.loadBoardSorted(ThreadActivity.this, boardCode);
                if (board.defData) {
                    if (DEBUG) {Log.i(TAG, "notifyBoardChanged() /" + boardCode + "/ couldn't load board, exiting");}
                    {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.notifyBoardChanged()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$13.run()",this);return;}}
                }
                if (mPager != null && mPager.getAdapter() != null && mAdapter != null && mAdapter.getCount() > 0) {
                    if (DEBUG) {Log.i(TAG, "notifyBoardChanged() /" + boardCode + "/ pager already filled, restarting loader");}
                    if (handler != null)
                        {handler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$13$1.run()",this);try{if (onTablet())
                                    {getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);} /*// board loader for tablet view*/
                                mAdapter.setQuery(query);
                                mAdapter.setBoard(board);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$13$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$13$1.run()",this,throwable);throw throwable;}
                            }
                        });}
                }
                else {
                    if (DEBUG) {Log.i(TAG, "notifyBoardChanged() /" + boardCode + "/ creating pager");}
                    if (handler != null)
                        {handler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$13$2.run()",this);try{if (onTablet())
                                    {createAbsListView();}
                                createPager(board);
                                syncPager(board);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$13$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$13$2.run()",this,throwable);throw throwable;}
                            }
                        });}
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$13.run()",this,throwable);throw throwable;}
            }
        }).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.notifyBoardChanged()",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.ThreadActivity.onOptionsItemSelected(com.chanapps.four.component.MenuItem)",this,item);try{if (mDrawerToggle.onOptionsItemSelected(item))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.onOptionsItemSelected(com.chanapps.four.component.MenuItem)",this);return true;}}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.onOptionsItemSelected(com.chanapps.four.component.MenuItem)",this);return super.onOptionsItemSelected(item);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.ThreadActivity.onOptionsItemSelected(com.chanapps.four.component.MenuItem)",this,throwable);throw throwable;}
    }

    private boolean warnedAboutNetworkDown = false;

    public boolean warnedAboutNetworkDown() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.ThreadActivity.warnedAboutNetworkDown()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.warnedAboutNetworkDown()",this);return warnedAboutNetworkDown;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.ThreadActivity.warnedAboutNetworkDown()",this,throwable);throw throwable;}
    }

    public void warnedAboutNetworkDown(boolean set) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.warnedAboutNetworkDown(boolean)",this,set);try{warnedAboutNetworkDown = set;com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.warnedAboutNetworkDown(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.warnedAboutNetworkDown(boolean)",this,throwable);throw throwable;}
    }

    @Override
    public void onBackPressed() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.onBackPressed()",this);try{if (query != null && !query.isEmpty()) {
            if (DEBUG) {Log.i(TAG, "onBackPressed with query, refreshing activity");}
            switchThreadInternal(boardCode, threadNo, postNo, "");
        }
        else {
            if (DEBUG) {Log.i(TAG, "onBackPressed without query, navigating up");}
            navigateUp();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.onBackPressed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.onBackPressed()",this,throwable);throw throwable;}
    }

    public void navigateUp() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.navigateUp()",this);try{Pair<Integer, ActivityManager.RunningTaskInfo> p = ActivityDispatcher.safeGetRunningTasks(this);
        int numTasks = p.first;
        ActivityManager.RunningTaskInfo task = p.second;
        if (numTasks == 0 || (numTasks == 1 && task != null && task.topActivity != null && task.topActivity.getClassName().equals(getClass().getName()))) {
            if (DEBUG) {Log.i(TAG, "no valid up task found, creating new one");}
            Intent intent = BoardActivity.createIntent(getActivityContext(), boardCode, "");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        finish();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.navigateUp()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.navigateUp()",this,throwable);throw throwable;}
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.ThreadActivity.dispatchKeyEvent(com.chanapps.four.component.KeyEvent)",this,event);try{if (DEBUG) {Log.i(TAG, "dispatchKeyEvent event=" + event.toString());}
        ThreadFragment fragment = getCurrentFragment();
        if (fragment == null) {
            if (DEBUG) {Log.i(TAG, "dispatchKeyEvent current fragment is null, ignoring");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.dispatchKeyEvent(com.chanapps.four.component.KeyEvent)",this);return super.dispatchKeyEvent(event);}
        }
        AbsListView absListView = fragment.getAbsListView();
        boolean handled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(SettingsActivity.PREF_USE_VOLUME_SCROLL, false)
            && ListViewKeyScroller.dispatchKeyEvent(event, absListView);
        if (handled)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.dispatchKeyEvent(com.chanapps.four.component.KeyEvent)",this);return true;}}
        else
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.ThreadActivity.dispatchKeyEvent(com.chanapps.four.component.KeyEvent)",this);return super.dispatchKeyEvent(event);}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.ThreadActivity.dispatchKeyEvent(com.chanapps.four.component.KeyEvent)",this,throwable);throw throwable;}
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.ActionBarDrawerToggle com.chanapps.four.activity.ThreadActivity.getDrawerToggle()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ActionBarDrawerToggle com.chanapps.four.activity.ThreadActivity.getDrawerToggle()",this);return mDrawerToggle;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.ActionBarDrawerToggle com.chanapps.four.activity.ThreadActivity.getDrawerToggle()",this,throwable);throw throwable;}
    }

    protected BroadcastReceiver onUpdateFastScrollReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$14.onReceive(com.chanapps.four.component.Context,com.chanapps.four.component.Intent)",this,context,intent);try{final boolean receivedEnable = intent != null
                    && intent.getAction().equals(UPDATE_FAST_SCROLL_ACTION)
                    && intent.hasExtra(OPTION_ENABLE)
                    ? intent.getBooleanExtra(OPTION_ENABLE, false)
                    : true;
            if (DEBUG) {Log.i(TAG, "onUpdateFastScrollReceived /" + boardCode + "/ received=/" + receivedEnable + "/");}
            final Handler gridHandler = handler != null ? handler : new Handler();
            if (gridHandler != null)
                {gridHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity$14$1.run()",this);try{if (boardGrid != null)
                            {boardGrid.setFastScrollEnabled(receivedEnable);}
                        ThreadFragment fragment = getPrimaryItem();
                        if (fragment != null)
                            {fragment.onUpdateFastScroll(receivedEnable);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$14$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$14$1.run()",this,throwable);throw throwable;}
                    }
                });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity$14.onReceive(com.chanapps.four.component.Context,com.chanapps.four.component.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity$14.onReceive(com.chanapps.four.component.Context,com.chanapps.four.component.Intent)",this,throwable);throw throwable;}
        }
    };

    public static void updateFastScroll(Context context, boolean enabled) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.activity.ThreadActivity.updateFastScroll(com.chanapps.four.component.Context,boolean)",context,enabled);try{Intent intent = new Intent(BoardActivity.UPDATE_FAST_SCROLL_ACTION);
        intent.putExtra(OPTION_ENABLE, enabled);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.ThreadActivity.updateFastScroll(com.chanapps.four.component.Context,boolean)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.updateFastScroll(com.chanapps.four.component.Context,boolean)",throwable);throw throwable;}
    }

    @Override
    public void switchBoard(String boardCode, String query) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.switchBoard(com.chanapps.four.component.String,com.chanapps.four.component.String)",this,boardCode,query);try{Intent intent = BoardActivity.createIntent(this, boardCode, query);
        if (!this.boardCode.equals(boardCode)) {
            startActivity(intent);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.switchBoard(com.chanapps.four.component.String,com.chanapps.four.component.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.switchBoard(com.chanapps.four.component.String,com.chanapps.four.component.String)",this,throwable);throw throwable;}
    }

    protected void switchThreadInternal(String boardCode, long threadNo, long postNo, String query) { com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.ThreadActivity.switchThreadInternal(com.chanapps.four.component.String,long,long,com.chanapps.four.component.String)",this,boardCode,threadNo,postNo,query);try{/*// for when we are already in this class*/
        if (DEBUG) {Log.i(TAG, "switchThreadInternal begin /" + boardCode + "/" + threadNo + "#p" + postNo + " q=" + query);}
        String oldBoardCode = this.boardCode;
        String oldQuery = this.query != null ? this.query : "";
        String checkQuery = query != null ? query : "";
        Intent intent = createIntent(this, boardCode, threadNo, postNo, query);
        setIntent(intent);
        setFromIntent(intent);
        NetworkProfileManager.instance().activityChange(this);

        /* move spinner to right board */
        mIgnoreMode = true;
        selectActionBarNavigationItem();

        if (!oldBoardCode.equals(boardCode)) { /*// recreate pager*/
            if (DEBUG) {Log.i(TAG, "switchThreadInternal new board redisplayPager() /" + boardCode + "/" + threadNo + "#p" + postNo + " q=" + query);}
            redisplayPager(boardCode, threadNo, query, "");
        }
        else if (!oldQuery.equals(query)) {
            if (DEBUG) {Log.i(TAG, "switchThreadInternal new query redisplayPager() /" + boardCode + "/" + threadNo + "#p" + postNo + " q=" + query);}
            redisplayPager(boardCode, threadNo, query, "");
        }
        else {
            if (DEBUG) {Log.i(TAG, "switchThreadInternal showThread() /" + boardCode + "/" + threadNo + "#p" + postNo + " q=" + query);}
            showThread(threadNo);
        }
        if (DEBUG) {Log.i(TAG, "switchThreadInternal end /" + boardCode + "/" + threadNo + "#p" + postNo + " q=" + query);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.ThreadActivity.switchThreadInternal(com.chanapps.four.component.String,long,long,com.chanapps.four.component.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.ThreadActivity.switchThreadInternal(com.chanapps.four.component.String,long,long,com.chanapps.four.component.String)",this,throwable);throw throwable;}
    }


}
