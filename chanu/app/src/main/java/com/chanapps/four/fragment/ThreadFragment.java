package com.chanapps.four.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ResourceCursorAdapter;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.chanapps.four.activity.BoardActivity;
import com.chanapps.four.activity.ChanActivityId;
import com.chanapps.four.activity.ChanIdentifiedActivity;
import com.chanapps.four.activity.GalleryViewActivity;
import com.chanapps.four.activity.PostReplyActivity;
import com.chanapps.four.activity.R;
import com.chanapps.four.activity.SettingsActivity;
import com.chanapps.four.activity.ThreadActivity;
import com.chanapps.four.adapter.AbstractBoardCursorAdapter;
import com.chanapps.four.adapter.ThreadCursorAdapter;
import com.chanapps.four.component.ActivityDispatcher;
import com.chanapps.four.component.PreferenceDialogs;
import com.chanapps.four.component.ThemeSelector;
import com.chanapps.four.component.ThreadViewable;
import com.chanapps.four.component.URLFormatComponent;
import com.chanapps.four.data.ChanBlocklist;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.data.ChanThread;
import com.chanapps.four.data.LastActivity;
import com.chanapps.four.loader.ChanImageLoader;
import com.chanapps.four.loader.ThreadCursorLoader;
import com.chanapps.four.service.FetchChanDataService;
import com.chanapps.four.service.NetworkProfileManager;
import com.chanapps.four.service.ThreadImageDownloadService;
import com.chanapps.four.service.profile.NetworkProfile;
import com.chanapps.four.viewer.ThreadListener;
import com.chanapps.four.viewer.ThreadViewer;
import com.chanapps.four.widget.WidgetProviderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * Created with IntelliJ IDEA.
 * User: arley
 * Date: 11/27/12
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadFragment extends Fragment implements ThreadViewable
{

    public static final String TAG = ThreadFragment.class.getSimpleName();
    public static final String BOARD_CODE = "boardCode";
    public static final String THREAD_NO = "threadNo";
    public static final String POST_NO = "postNo";

    protected static final int DRAWABLE_ALPHA_LIGHT = 0xc2;
    protected static final int DRAWABLE_ALPHA_DARK = 0xee;

    public static final boolean DEBUG = false;

    public static final int MAX_HTTP_GET_URL_LEN = 1000;
    protected static final int LOADER_ID = 0;

    protected String boardCode;
    protected long threadNo;

    protected AbstractBoardCursorAdapter adapter;
    protected AbstractBoardCursorAdapter fullAdapter; /*// only used for search*/
    protected View layout;
    protected AbsListView absListView;
    protected Handler handler;
    protected String query = "";
    protected long postNo; /*// for direct jumps from latest post / recent images*/
    protected String imageUrl;
    protected boolean shouldPlayThread = false;
    protected ShareActionProvider shareActionProviderOP = null;
    /*//protected ShareActionProvider shareActionProvider = null;*/
    protected Map<String, Uri> checkedImageUris = new HashMap<String, Uri>(); /*// used for tracking what's in the media store*/
    protected ActionMode actionMode = null;
    protected PullToRefreshAttacher mPullToRefreshAttacher;
    protected View boardTitleBar;
    protected View boardSearchResultsBar;
    protected ThreadListener threadListener;
    protected boolean progressVisible = false;
    protected Menu menu = null;
    protected View.OnClickListener commentsOnClickListener = null;
    protected View.OnClickListener imagesOnClickListener = null;
    protected boolean firstLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.chanapps.four.fragment.ThreadFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,inflater,viewGroup,bundle);try{if (bundle == null)
            {bundle = getArguments();}
        boardCode = bundle.getString(BOARD_CODE);
        threadNo = bundle.getLong(THREAD_NO);
        postNo = bundle.getLong(POST_NO);
        query = bundle.getString(SearchManager.QUERY);
        if (DEBUG) {Log.i(TAG, "onCreateView /" + boardCode + "/" + threadNo + "#p" + postNo + " q=" + query);}
        int layoutId = query != null && !query.isEmpty() ? R.layout.thread_list_layout_search : R.layout.thread_list_layout;
        layout = inflater.inflate(layoutId, viewGroup, false);
        createAbsListView();

        if (threadNo > 0)
            {getLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);}
        else
            if (DEBUG) {Log.i(TAG, "onCreateView /" + boardCode + "/" + threadNo + "#p" + postNo
                    + " no thread found, skipping loader");}

        boardTitleBar = layout.findViewById(R.id.board_title_bar);
        boardSearchResultsBar = layout.findViewById(R.id.board_search_results_bar);
        setHasOptionsMenu(true);
        {com.mijack.Xlog.logMethodExit("android.view.View com.chanapps.four.fragment.ThreadFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this);return layout;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.chanapps.four.fragment.ThreadFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,throwable);throw throwable;}
    }

    protected PullToRefreshAttacher.OnRefreshListener pullToRefreshListener = new PullToRefreshAttacher.OnRefreshListener() {
        @Override
        public void onRefreshStarted(View view) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$1.onRefreshStarted(android.view.View)",this,view);try{if (DEBUG) {Log.i(TAG, "pullToRefreshListener.onRefreshStarted()");}
            manualRefresh();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$1.onRefreshStarted(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$1.onRefreshStarted(android.view.View)",this,throwable);throw throwable;}
        }
    };

    protected boolean onTablet() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment.onTablet()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onTablet()",this);return getActivity() != null && ((ThreadActivity)getActivity()).onTablet();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment.onTablet()",this,throwable);throw throwable;}
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.onSaveInstanceState(android.os.Bundle)",this,savedInstanceState);try{super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(ChanBoard.BOARD_CODE, boardCode);
        savedInstanceState.putLong(ChanThread.THREAD_NO, threadNo);
        savedInstanceState.putString(SearchManager.QUERY, query);
        /*//int pos = absListView == null ? -1 : absListView.getFirstVisiblePosition();*/
        /*//View view = absListView == null ? null : absListView.getChildAt(0);*/
        /*//int offset = view == null ? 0 : view.getTop();*/
        if (DEBUG) {Log.i(TAG, "onSaveInstanceState /" + boardCode + "/" + threadNo);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onViewStateRestored(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.onViewStateRestored(android.os.Bundle)",this,bundle);try{super.onViewStateRestored(bundle);
        if (bundle == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.onViewStateRestored(android.os.Bundle)",this);return;}}
        boardCode = bundle.getString(ChanBoard.BOARD_CODE);
        threadNo = bundle.getLong(ChanThread.THREAD_NO, 0);
        query = bundle.getString(SearchManager.QUERY);
        if (DEBUG) {Log.i(TAG, "onViewStateRestored /" + boardCode + "/" + threadNo);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.onViewStateRestored(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.onStart()",this);try{super.onStart();
        if (DEBUG) {Log.i(TAG, "onStart /" + boardCode + "/" + threadNo);}
        if (handler == null)
            {handler = new Handler();}
        threadListener = new ThreadListener(this, ThemeSelector.instance(getActivity().getApplicationContext()).isDark());
        commentsOnClickListener = ThreadViewer.createCommentsOnClickListener(absListView, handler);
        imagesOnClickListener = ThreadViewer.createImagesOnClickListener(getActivityContext(), boardCode, threadNo);

        if (threadNo > 0 && (adapter == null || adapter.getCount() <= 1)) { /*// <= 0*/
            ThreadActivity activity = (ThreadActivity)getActivity();
            if (activity == null) {
                if (DEBUG) {Log.i(TAG, "onStart /" + boardCode + "/" + threadNo + " activity null, skipping loader");}
            }
            else if (activity.refreshing) {
                restartIfDeadAsync();
            }
            else if (!getLoaderManager().hasRunningLoaders()) {
                if (DEBUG) {Log.i(TAG, "onStart /" + boardCode + "/" + threadNo + " no data and no running loaders, restarting loader");}
                /*//getLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);*/
            }
        }
        else {
            if (DEBUG) {Log.i(TAG, "onStart /" + boardCode + "/" + threadNo + " no thread found, skipping loader");}
        }
        scheduleAutoUpdate();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.onStart()",this,throwable);throw throwable;}
    }

    protected void restartIfDeadAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.restartIfDeadAsync()",this);try{new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$2.run()",this);try{ChanThread thread = ChanFileStorage.loadThreadData(getActivityContext(), boardCode, threadNo);
                final boolean isDead = thread != null && thread.isDead;
                if (handler != null)
                    {handler.post(new Runnable() {
                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$2$1.run()",this);try{if (isDead) {
                                if (DEBUG) {Log.i(TAG, "onStart /" + boardCode + "/" + threadNo + " dead thread, restarting loader");}
                                getLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);
                            }
                            else {
                                if (DEBUG) {Log.i(TAG, "onStart /" + boardCode + "/" + threadNo + " activity refreshing, skipping loader");}
                            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$2$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$2$1.run()",this,throwable);throw throwable;}
                        }
                    });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$2.run()",this,throwable);throw throwable;}
            }
        }).start();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.restartIfDeadAsync()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.restartIfDeadAsync()",this,throwable);throw throwable;}
    }

    @Override
    public AbsListView getAbsListView() {
        com.mijack.Xlog.logMethodEnter("android.widget.AbsListView com.chanapps.four.fragment.ThreadFragment.getAbsListView()",this);try{com.mijack.Xlog.logMethodExit("android.widget.AbsListView com.chanapps.four.fragment.ThreadFragment.getAbsListView()",this);return absListView;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.AbsListView com.chanapps.four.fragment.ThreadFragment.getAbsListView()",this,throwable);throw throwable;}
    }

    @Override
    public ResourceCursorAdapter getAdapter() {
        com.mijack.Xlog.logMethodEnter("android.widget.ResourceCursorAdapter com.chanapps.four.fragment.ThreadFragment.getAdapter()",this);try{com.mijack.Xlog.logMethodExit("android.widget.ResourceCursorAdapter com.chanapps.four.fragment.ThreadFragment.getAdapter()",this);return adapter;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.ResourceCursorAdapter com.chanapps.four.fragment.ThreadFragment.getAdapter()",this,throwable);throw throwable;}
    }

    @Override
    public Handler getHandler() {
        com.mijack.Xlog.logMethodEnter("android.os.Handler com.chanapps.four.fragment.ThreadFragment.getHandler()",this);try{com.mijack.Xlog.logMethodExit("android.os.Handler com.chanapps.four.fragment.ThreadFragment.getHandler()",this);return handler;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Handler com.chanapps.four.fragment.ThreadFragment.getHandler()",this,throwable);throw throwable;}
    }

    @Override
    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.onResume()",this);try{super.onResume();
        if (DEBUG) {Log.i(TAG, "onResume /" + boardCode + "/" + threadNo);}
        if (handler == null)
            {handler = new Handler();}
        scheduleAutoUpdate();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.onResume()",this,throwable);throw throwable;}
    }

    @Override
    public void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.onPause()",this);try{super.onPause();
        if (DEBUG) {Log.i(TAG, "onPause /" + boardCode + "/" + threadNo);}
        saveViewPositionAsync();
        if (handler != null)
            {handler.removeCallbacks(autoUpdateRunnable);} /*// deschedule any current updates*/
        handler = null;com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.onPause()",this,throwable);throw throwable;}
    }

    @Override
    public void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.onStop()",this);try{super.onStop();
        if (DEBUG) {Log.i(TAG, "onStop /" + boardCode + "/" + threadNo);}
        handler = null;com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.onStop()",this,throwable);throw throwable;}
    }

    protected boolean warnedAboutNetworkDown() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment.warnedAboutNetworkDown()",this);try{ThreadActivity activity = (ThreadActivity)getActivity();
        if (activity == null)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.warnedAboutNetworkDown()",this);return false;}}
        else
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.warnedAboutNetworkDown()",this);return activity.warnedAboutNetworkDown();}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment.warnedAboutNetworkDown()",this,throwable);throw throwable;}
    }

    protected void warnedAboutNetworkDown(boolean set) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.warnedAboutNetworkDown(boolean)",this,set);try{ThreadActivity activity = (ThreadActivity)getActivity();
        if (activity == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.warnedAboutNetworkDown(boolean)",this);return;}}
        else
            {activity.warnedAboutNetworkDown(set);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.warnedAboutNetworkDown(boolean)",this,throwable);throw throwable;}
    }

    public void fetchIfNeeded(final Handler activityHandler) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.fetchIfNeeded(android.os.Handler)",this,activityHandler);try{if (DEBUG) {Log.i(TAG, "fetchIfNeeded() /" + boardCode + "/" + threadNo);}
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$3.run()",this);try{ChanThread thread = ChanFileStorage.loadThreadData(getActivityContext(), boardCode, threadNo);
                if (thread == null) {
                    if (DEBUG) {Log.i(TAG, "fetchIfNeeded() /" + boardCode + "/" + threadNo + " null thread, exiting");}
                    {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.fetchIfNeeded(android.os.Handler)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$3.run()",this);return;}}
                }
                if (thread.isDead) {
                    if (DEBUG) {Log.i(TAG, "fetchIfNeeded() /" + boardCode + "/" + threadNo + " dead thread, exiting");}
                    {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.fetchIfNeeded(android.os.Handler)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$3.run()",this);return;}}
                }
                if (query != null && !query.isEmpty()) {
                    if (DEBUG) {Log.i(TAG, "fetchIfNeeded() /" + boardCode + "/" + threadNo + " query present, exiting");}
                    {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.fetchIfNeeded(android.os.Handler)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$3.run()",this);return;}}
                }
                final int replies = thread.replies;
                if (DEBUG) {Log.i(TAG, "fetchIfNeeded() /" + boardCode + "/" + threadNo + " checking thread replies=" + thread.replies);}
                if (activityHandler != null)
                    {activityHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$3$1.run()",this);try{if (replies < 0 || replies > absListView.getCount() - 1) {
                                if (DEBUG) {Log.i(TAG, "fetchIfNeeded() /" + boardCode + "/" + threadNo + " should fetch more, trying");}
                                tryFetchThread();
                            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$3$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$3$1.run()",this,throwable);throw throwable;}
                        }
                    });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$3.run()",this,throwable);throw throwable;}
            }
        }).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.fetchIfNeeded(android.os.Handler)",this,throwable);throw throwable;}
    }

    protected void tryFetchThread() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.tryFetchThread()",this);try{if (DEBUG) {Log.i(TAG, "tryFetchThread /" + boardCode + "/" + threadNo);}
        if (handler == null) {
            if (DEBUG) {Log.i(TAG, "tryFetchThread not in foreground, exiting");}
            setProgressAsync(false);
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.tryFetchThread()",this);return;}
        }
        NetworkProfile.Health health = NetworkProfileManager.instance().getCurrentProfile().getConnectionHealth();
        if (health == NetworkProfile.Health.NO_CONNECTION) { /*// || health == NetworkProfile.Health.BAD) {*/
            if (DEBUG) {Log.i(TAG, "tryFetchThread no connection, exiting");}
            final Context context = getActivityContext();
            if (handler != null && context != null && !warnedAboutNetworkDown()) {
                warnedAboutNetworkDown(true);
                final String msg = String.format(getString(R.string.mobile_profile_health_status),
                        health.toString().toLowerCase().replaceAll("_", " "));
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$4.run()",this);try{Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$4.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$4.run()",this,throwable);throw throwable;}
                    }
                });
            }
            setProgressAsync(false);
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.tryFetchThread()",this);return;}
        }
        else {
            warnedAboutNetworkDown(false);
        }
        ThreadActivity activity = (ThreadActivity)getActivity();
        ThreadFragment primary = activity == null ? null : activity.getPrimaryItem();
        if (primary == null || primary != this) {
            if (DEBUG) {Log.i(TAG, "tryFetchThread exiting since non-primary item this=" + this + " is not primary=" + primary);}
            setProgressAsync(false);
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.tryFetchThread()",this);return;}
        }
        if (DEBUG) {Log.i(TAG, "tryFetchThread clearing fetch chan data service queue");}
        FetchChanDataService.clearServiceQueue(getActivityContext());
        if (DEBUG) {Log.i(TAG, "tryFetchThread calling fetch chan data service for /" + boardCode + "/" + threadNo);}
        boolean fetchScheduled = FetchChanDataService.scheduleThreadFetch(getActivityContext(), boardCode, threadNo, true, false);
        if (fetchScheduled) {
            if (DEBUG) {Log.i(TAG, "tryFetchThread scheduled fetch");}
            setProgressAsync(true);
        }
        else {
            if (DEBUG) {Log.i(TAG, "tryFetchThread couldn't fetch");}
            setProgressAsync(false);
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.tryFetchThread()",this);return;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.tryFetchThread()",this,throwable);throw throwable;}
    }
    
    protected void setProgressAsync(final boolean on) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.setProgressAsync(boolean)",this,on);try{if (handler != null)
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$5.run()",this);try{setProgress(on);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$5.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$5.run()",this,throwable);throw throwable;}
                }
            });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.setProgressAsync(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.setProgressAsync(boolean)",this,throwable);throw throwable;}
    }

    protected void onThreadLoadFinished(Cursor data) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.onThreadLoadFinished(android.database.Cursor)",this,data);try{adapter.swapCursor(data);
        setupShareActionProviderOPMenu(menu);
        selectCurrentThreadAsync();
        if (firstLoad) {
            firstLoad = false;
            loadViewPositionAsync();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.onThreadLoadFinished(android.database.Cursor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.onThreadLoadFinished(android.database.Cursor)",this,throwable);throw throwable;}
    }

    protected void selectCurrentThreadAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.selectCurrentThreadAsync()",this);try{new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$6.run()",this);try{final ChanThread thread = ChanFileStorage.loadThreadData(getActivityContext(), boardCode, threadNo);
                selectCurrentThread(thread);
                scheduleAutoUpdate();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$6.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$6.run()",this,throwable);throw throwable;}
            }
        }).start();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.selectCurrentThreadAsync()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.selectCurrentThreadAsync()",this,throwable);throw throwable;}
    }

    protected static final int AUTOUPDATE_THREAD_DELAY_MS = 30000;

    protected void scheduleAutoUpdate() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.scheduleAutoUpdate()",this);try{if (DEBUG) {Log.i(TAG, "scheduleAutoUpdate() checking /" + boardCode + "/" + threadNo + " q=" + query);}
        Context context = getActivityContext();
        if (context == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.scheduleAutoUpdate()",this);return;}}
        boolean autoUpdate = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(SettingsActivity.PREF_AUTOUPDATE_THREADS, true);
        if (!autoUpdate) {
            if (DEBUG) {Log.i(TAG, "scheduleAutoUpdate() autoupdate disabled, exiting /" + boardCode + "/" + threadNo);}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.scheduleAutoUpdate()",this);return;}
        }
        if (query != null && !query.isEmpty()) {
            if (DEBUG) {Log.i(TAG, "scheduleAutoUpdate() query is present, exiting /" + boardCode + "/" + threadNo);}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.scheduleAutoUpdate()",this);return;}
        }
        if (getActivity() != null && ((ThreadActivity)getActivity()).getCurrentFragment() != this) {
            if (DEBUG) {Log.i(TAG, "scheduleAutoUpdate() not current fragment, exiting /" + boardCode + "/" + threadNo);}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.scheduleAutoUpdate()",this);return;}
        }
        ChanThread thread = ChanFileStorage.loadThreadData(context, boardCode, threadNo);
        if (thread == null || thread.isDead) {
            if (DEBUG) {Log.i(TAG, "scheduleAutoUpdate() dead thread, exiting /" + boardCode + "/" + threadNo);}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.scheduleAutoUpdate()",this);return;}
        }
        if (handler != null)
            {handler.removeCallbacks(autoUpdateRunnable);} /*// deschedule any current updates*/
        if (handler != null)
            {handler.postDelayed(autoUpdateRunnable, AUTOUPDATE_THREAD_DELAY_MS);}
        if (handler == null) {
            if (DEBUG) {Log.i(TAG, "scheduleAutoUpdate() null handler exiting /" + boardCode + "/" + threadNo);}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.scheduleAutoUpdate()",this,throwable);throw throwable;}
    }

    protected final Runnable autoUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$7.run()",this);try{if (DEBUG) {Log.i(TAG, "autoUpdateRunnable preparing refresh /" + boardCode + "/" + threadNo);}
            if (NetworkProfileManager.instance().getActivityId() != getChanActivityId()) {
                if (DEBUG) {Log.i(TAG, "autoUpdateRunnable no longer foreground, cancelling update /" + boardCode + "/" + threadNo);}
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$7.run()",this);return;}
            }
            if (handler == null) {
                if (DEBUG) {Log.i(TAG, "autoUpdateRunnable null handler, cancelling update /" + boardCode + "/" + threadNo);}
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$7.run()",this);return;}
            }
            if (DEBUG) {Log.i(TAG, "autoUpdateRunnable manually refreshing /" + boardCode + "/" + threadNo);}
            manualRefresh();
            if (DEBUG) {Log.i(TAG, "autoUpdateRunnable scheduling next auto refresh /" + boardCode + "/" + threadNo);}
            scheduleAutoUpdate();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$7.run()",this,throwable);throw throwable;}
        }
    };

    protected static final int FROM_BOARD_THREAD_ADAPTER_COUNT = 5; /*// thread header + related title + 3 related boards*/

    protected void setProgressFromThreadState(final ChanThread thread) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.setProgressFromThreadState(com.chanapps.four.data.ChanThread)",this,thread);try{if (DEBUG) {Log.i(TAG, "setProgressFromThreadState /" + boardCode + "/" + threadNo + " listViewCount=" + (absListView == null ? 0 : absListView.getCount()));}
        ThreadActivity activity = getActivity() instanceof ThreadActivity ? (ThreadActivity)getActivity() : null;
        if (activity == null) {
            if (DEBUG) {Log.i(TAG, "setProgressFromThreadState /" + boardCode + "/" + threadNo + " not attached to activity, exiting");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.setProgressFromThreadState(com.chanapps.four.data.ChanThread)",this);return;}
        }
        else if (activity.getCurrentFragment() != this) {
            if (DEBUG) {Log.i(TAG, "setProgressFromThreadState /" + boardCode + "/" + threadNo + " not the current fragment, exiting");}
        }
        else if (!NetworkProfileManager.isConnected()) {
            if (DEBUG) {Log.i(TAG, "setProgressFromThreadState /" + boardCode + "/" + threadNo + " no connection, setting load finished for thread=" + thread);}
            setProgress(false);
        }
        else if (thread.isDead) {
            if (DEBUG) {Log.i(TAG, "setProgressFromThreadState /" + boardCode + "/" + threadNo + " dead thread, setting load finished for thread=" + thread);}
            setProgress(false);
        }
        else if (thread != null && thread.posts != null && thread.posts.length == 1 && thread.posts[0].replies > 0
                && absListView != null && absListView.getCount() <= FROM_BOARD_THREAD_ADAPTER_COUNT) {
            if (DEBUG) {Log.i(TAG, "setProgressFromThreadState /" + boardCode + "/" + threadNo + " thread not fully loaded, awaiting load thread=" + thread);}
        }
        else if (!thread.defData
                && thread.posts != null && thread.posts.length > 0
                && thread.posts[0] != null && !thread.posts[0].defData && thread.posts[0].replies >= 0) { /*// post is loaded*/
            if (DEBUG) {Log.i(TAG, "setProgressFromThreadState /" + boardCode + "/" + threadNo + " thread loaded, setting load finished for thread=" + thread);}
            setProgress(false);
        }
        else {
            if (DEBUG) {Log.i(TAG, "setProgressFromThreadState /" + boardCode + "/" + threadNo + " thread not yet loaded, awaiting load thread=" + thread);}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.setProgressFromThreadState(com.chanapps.four.data.ChanThread)",this,throwable);throw throwable;}    
    }

    public void scrollToPostAsync(final long scrollToPostNo) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.scrollToPostAsync(long)",this,scrollToPostNo);try{if (DEBUG) {Log.i(TAG, "scrollToPostAsync() postNo=" + scrollToPostNo);}
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$8.run()",this);try{scrollToPost(scrollToPostNo, null);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$8.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$8.run()",this,throwable);throw throwable;}
            }
        }).start();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.scrollToPostAsync(long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.scrollToPostAsync(long)",this,throwable);throw throwable;}
    }

    protected void scrollToPost(final long scrollToPostNo, final Runnable uiCallback) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.scrollToPost(long,java.lang.Runnable)",this,scrollToPostNo,uiCallback);try{if (DEBUG) {Log.i(TAG, "scrollToPost() postNo=" + scrollToPostNo + " begin");}
        if (adapter == null) {
            if (DEBUG) {Log.i(TAG, "scrollToPost() postNo=" + scrollToPostNo + " null adapter, exiting");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.scrollToPost(long,java.lang.Runnable)",this);return;}
        }
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(-1);
        boolean found = false;
        int pos = 0;
        while (cursor.moveToNext()) {
            long postNoAtPos = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
            if (postNoAtPos == scrollToPostNo) {
                found = true;
                break;
            }
            pos++;
        }
        final boolean hasPost = found;
        final int postPos = pos;
        if (!hasPost) {
            if (DEBUG) {Log.i(TAG, "scrollToPost() didn't find postNo=" + scrollToPostNo);}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.scrollToPost(long,java.lang.Runnable)",this);return;}
        }
        if (DEBUG) {Log.i(TAG, "scrollToPost() found postNo=" + scrollToPostNo + " at pos=" + pos);}

        if (handler == null) {
            if (DEBUG) {Log.i(TAG, "scrollToPost() postNo=" + scrollToPostNo + " null handler, skipping highlight");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.scrollToPost(long,java.lang.Runnable)",this);return;}
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$9.run()",this);try{if (absListView == null) {
                    if (DEBUG) {Log.i(TAG, "scrollToPost() postNo=" + scrollToPostNo + " null list view, exiting");}
                    {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.scrollToPost(long,java.lang.Runnable)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$9.run()",this);return;}}
                }

                if (DEBUG) {Log.i(TAG, "scrollToPost() postNo=" + scrollToPostNo + " scrolling to pos=" + postPos + " on UI thread");}

                /*//(new ScrollerRunnable(absListView)).start(postPos);*/
                /*//absListView.smoothScrollToPosition(postPos);*/
                absListView.requestFocusFromTouch();
                absListView.setSelection(postPos);
                /*//if (uiCallback != null)*/
                /*//    uiCallback.run();*/}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$9.run()",this,throwable);throw throwable;}
            }
        }, 100);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.scrollToPost(long,java.lang.Runnable)",this,throwable);throw throwable;}

    }

    protected void selectCurrentThread(final ChanThread thread) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.selectCurrentThread(com.chanapps.four.data.ChanThread)",this,thread);try{if (DEBUG) {Log.i(TAG, "onThreadLoadFinished /" + boardCode + "/" + threadNo + " thread=" + thread);}
        if (query != null && !query.isEmpty()) {
            if (handler != null)
                {handler.post(new Runnable() {
                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$10.run()",this);try{displaySearchTitle();
                        setProgressFromThreadState(thread);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$10.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$10.run()",this,throwable);throw throwable;}
                    }
                });}
        }
        else if (thread.isDead) {
            if (DEBUG) {Log.i(TAG, "onThreadLoadFinished /" + boardCode + "/" + threadNo + " dead thread, redisplaying");}
            if (handler != null)
                {handler.post(new Runnable() {
                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$11.run()",this);try{/*//absListView.invalidateViews();*/
                        displaySearchTitle();
                        setProgressFromThreadState(thread);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$11.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$11.run()",this,throwable);throw throwable;}
                    }
                });}
        }
        else {
            if (DEBUG) {Log.i(TAG, "onThreadLoadFinished /" + boardCode + "/" + threadNo + " setting spinner from thread state");}
            if (handler != null)
                {handler.post(new Runnable() {
                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$12.run()",this);try{displaySearchTitle();
                        setProgressFromThreadState(thread);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$12.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$12.run()",this,throwable);throw throwable;}
                    }
                });}
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.selectCurrentThread(com.chanapps.four.data.ChanThread)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.selectCurrentThread(com.chanapps.four.data.ChanThread)",this,throwable);throw throwable;}
    }

    protected void setProgress(boolean on) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.setProgress(boolean)",this,on);try{progressVisible = on;
        ThreadActivity activity = (ThreadActivity)getActivity();
        if (activity != null) {
        	activity.setProgressForFragment(boardCode, threadNo, on);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.setProgress(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.setProgress(boolean)",this,throwable);throw throwable;}
    }
    
    protected void createAbsListView() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.createAbsListView()",this);try{ImageLoader imageLoader = ChanImageLoader.getInstance(getActivityContext());
        absListView = (ListView) layout.findViewById(R.id.thread_list_view);
        adapter = new ThreadCursorAdapter(getActivity(), viewBinder, true, null);
        absListView.setAdapter(adapter);
        absListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        absListView.setOnCreateContextMenuListener(this);
        absListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true));
        absListView.setFastScrollEnabled(PreferenceManager
                .getDefaultSharedPreferences(getActivity()).getBoolean(SettingsActivity.PREF_USE_FAST_SCROLL, false));com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.createAbsListView()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.createAbsListView()",this,throwable);throw throwable;}
    }

    public void setPullToRefreshAttacher(PullToRefreshAttacher mPullToRefreshAttacher) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.setPullToRefreshAttacher(uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher)",this,mPullToRefreshAttacher);try{this.mPullToRefreshAttacher = mPullToRefreshAttacher;
        if (mPullToRefreshAttacher == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.setPullToRefreshAttacher(uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher)",this);return;}}
        if (absListView != null)
            {mPullToRefreshAttacher.setRefreshableView(absListView, pullToRefreshListener);}
        new Thread(setPullToRefreshEnabledAsync).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.setPullToRefreshAttacher(uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher)",this,throwable);throw throwable;}
    }

    private Runnable setPullToRefreshEnabledAsync = new Runnable() {
        @Override
        public void run() {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$13.run()",this);try{Context context = getActivityContext();
            if (context == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$13.run()",this);return;}}
            ChanThread thread = ChanFileStorage.loadThreadData(context, boardCode, threadNo);
            boolean enabled;
            if (thread != null && thread.isDead)
                {enabled = false;}
            else
                {enabled = true;}
            final boolean isEnabled = enabled;
            if (handler != null)
                {handler.post(new Runnable() {
                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$13$1.run()",this);try{if (mPullToRefreshAttacher != null)
                            {mPullToRefreshAttacher.setEnabled(isEnabled);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$13$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$13$1.run()",this,throwable);throw throwable;}
                    }
                });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$13.run()",this,throwable);throw throwable;}
        }
    };

    private String replyText(long postNos[]) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.fragment.ThreadFragment.replyText(long)",this,postNos[]);try{StringBuilder replyText = new StringBuilder();
        for (long postNo : postNos) {
            replyText.append(">>").append(postNo).append("\n");
        }
        {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.fragment.ThreadFragment.replyText(long)",this);return replyText.toString();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.fragment.ThreadFragment.replyText(long)",this,throwable);throw throwable;}
    }

    private void postReply(String replyText, String quotesText) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.postReply(java.lang.String,java.lang.String)",this,replyText,quotesText);try{PostReplyActivity.startActivity(getActivityContext(), boardCode, threadNo, 0,
                ChanPost.planifyText(replyText),
                ChanPost.planifyText(quotesText));com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.postReply(java.lang.String,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.postReply(java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    protected boolean isThreadPlayable() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment.isThreadPlayable()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.isThreadPlayable()",this);return adapter != null
                && adapter.getCount() > 0
                && !getLoaderManager().hasRunningLoaders()
                && !progressVisible;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment.isThreadPlayable()",this,throwable);throw throwable;}
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.onPrepareOptionsMenu(android.view.Menu)",this,menu);try{this.menu = menu;
        MenuItem playMenuItem = menu.findItem(R.id.play_thread_menu);
        if (playMenuItem != null)
            {synchronized (this) {
                if (isThreadPlayable()) {
                    playMenuItem.setIcon(shouldPlayThread ? R.drawable.av_stop : R.drawable.av_play);
                    playMenuItem.setTitle(shouldPlayThread ? R.string.play_thread_stop_menu : R.string.play_thread_menu);
                    playMenuItem.setVisible(true);
                }
                else {
                    playMenuItem.setVisible(false);
                }
            }}
        setDeadStatusAsync();
        setWatchMenuAsync();
        setupShareActionProviderOPMenu(menu);
        if (getActivity() != null) {
            ((ThreadActivity)getActivity()).createSearchView(menu);
        }
        super.onPrepareOptionsMenu(menu);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.onPrepareOptionsMenu(android.view.Menu)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.onPrepareOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
    }

    protected void setDeadStatusAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.setDeadStatusAsync()",this);try{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.setDeadStatusAsync()",this);new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$14.run()",this);try{final boolean undead = undead();
                if (handler != null)
                    {handler.post(new Runnable() {
                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$14$1.run()",this);try{if (menu == null)
                                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$14.run()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$14$1.run()",this);return;}}}
                            MenuItem item;
                            /*//if ((item = menu.findItem(R.id.refresh_menu)) != null)*/
                            /*//    item.setVisible(undead);*/
                            if (mPullToRefreshAttacher != null)
                                {mPullToRefreshAttacher.setEnabled(undead);}
                            if ((item = menu.findItem(R.id.post_reply_all_menu)) != null)
                                {item.setVisible(undead);}
                            /*//if ((item = menu.findItem(R.id.web_menu)) != null)*/
                            /*//    item.setVisible(undead);*/}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$14$1.run()",this,throwable);throw throwable;}
                        }
                    });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$14.run()",this,throwable);throw throwable;}
            }
        }).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.setDeadStatusAsync()",this,throwable);throw throwable;}
    }

    protected void setWatchMenuAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.setWatchMenuAsync()",this);try{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.setWatchMenuAsync()",this);new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$15.run()",this);try{final ChanThread thread = ChanFileStorage.loadThreadData(getActivityContext(), boardCode, threadNo);
                final boolean watched = ChanFileStorage.isThreadWatched(getActivityContext(), thread);
                if (handler != null)
                    {handler.post(new Runnable() {
                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$15$1.run()",this);try{if (menu == null)
                                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$15.run()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$15$1.run()",this);return;}}}
                            MenuItem item;
                            if ((item = menu.findItem(R.id.watch_thread_menu)) != null)
                                {item.setVisible(!watched);}
                            if ((item = menu.findItem(R.id.watch_remove_thread_menu)) != null)
                                {item.setVisible(watched);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$15$1.run()",this,throwable);throw throwable;}
                        }
                    });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$15.run()",this,throwable);throw throwable;}
            }
        }).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.setWatchMenuAsync()",this,throwable);throw throwable;}
    }

    protected void setupShareActionProviderOPMenu(final Menu menu) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.setupShareActionProviderOPMenu(android.view.Menu)",this,menu);try{updateSharedIntentOP(shareActionProviderOP);
        if (menu == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.setupShareActionProviderOPMenu(android.view.Menu)",this);return;}}
        MenuItem shareItem = menu.findItem(R.id.thread_share_menu);
        shareActionProviderOP = shareItem == null ? null : (ShareActionProvider) shareItem.getActionProvider();
        if (DEBUG) {Log.i(TAG, "setupShareActionProviderOP() shareActionProviderOP=" + shareActionProviderOP);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.setupShareActionProviderOPMenu(android.view.Menu)",this,throwable);throw throwable;}
    }

    protected boolean undead() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment.undead()",this);try{ChanThread thread = ChanFileStorage.loadThreadData(getActivity(), boardCode, threadNo);
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.undead()",this);return !(thread != null && thread.isDead);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment.undead()",this,throwable);throw throwable;}
    }

    protected void navigateUp() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.navigateUp()",this);try{Activity activity = getActivity();
        if (activity == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.navigateUp()",this);return;}}
        if (activity instanceof ThreadActivity)
            {((ThreadActivity)activity).navigateUp();}
        else if (activity instanceof BoardActivity)
            {((BoardActivity)activity).navigateUp();}
        else
            {activity.finish();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.navigateUp()",this,throwable);throw throwable;}
    }

    protected void setActivityIdToFragment() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.setActivityIdToFragment()",this);try{if (!(getActivity() instanceof ThreadActivity))
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.setActivityIdToFragment()",this);return;}}
        ThreadActivity ta = (ThreadActivity)getActivity();
        ta.setChanActivityId(getChanActivityId());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.setActivityIdToFragment()",this,throwable);throw throwable;}
    }

    protected void manualRefresh() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.manualRefresh()",this);try{if (handler != null)
            {handler.removeCallbacks(autoUpdateRunnable);} /*// deschedule autoupdates while refreshing*/
        setProgress(true);
        setActivityIdToFragment();
        NetworkProfileManager.instance().manualRefresh(getChanActivity());com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.manualRefresh()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.manualRefresh()",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this,item);try{ThreadActivity a = getActivity() != null && getActivity() instanceof ThreadActivity ? (ThreadActivity)getActivity() : null;
        ActionBarDrawerToggle t = a != null ? a.getDrawerToggle() : null;
        if (t != null && t.onOptionsItemSelected(item))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}}
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateUp();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.refresh_menu:
                manualRefresh();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.view_image_gallery_menu:
                GalleryViewActivity.startAlbumViewActivity(getActivityContext(), boardCode, threadNo);
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.watch_thread_menu:
                addToWatchlist();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.watch_remove_thread_menu:
                removeFromWatchlist();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.scroll_to_top_menu:
                jumpToTop();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.scroll_to_bottom_menu:
                jumpToBottom();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.post_reply_all_menu:
                postReply("", selectQuoteText(0));
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.download_all_images_to_gallery_menu:
                ThreadImageDownloadService.startDownloadViaThreadMenu(getActivityContext(), boardCode, threadNo, new long[]{});
                Toast.makeText(getActivityContext(), R.string.download_all_images_notice, Toast.LENGTH_SHORT).show();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.play_thread_menu:
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return playThreadMenu();}
            case R.id.web_menu:
                String url = ChanThread.threadUrl(getActivityContext(), boardCode, threadNo);
                ActivityDispatcher.launchUrlInBrowser(getActivityContext(), url);
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.font_size_menu:
                new PreferenceDialogs(getActivity()).showFontSizeDialog();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.autoload_images_menu:
                new PreferenceDialogs(getActivity()).showAutoloadImagesDialog();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.theme_menu:
                new PreferenceDialogs(getActivity()).showThemeDialog();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.use_volume_scroll_menu:
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                boolean pref = prefs.getBoolean(SettingsActivity.PREF_USE_VOLUME_SCROLL, false);
                pref = !pref;
                prefs.edit().putBoolean(SettingsActivity.PREF_USE_VOLUME_SCROLL, pref).apply();
                getActivity().recreate();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.use_fast_scroll_menu:
                prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                pref = prefs.getBoolean(SettingsActivity.PREF_USE_FAST_SCROLL, false);
                pref = !pref;
                prefs.edit().putBoolean(SettingsActivity.PREF_USE_FAST_SCROLL, pref).apply();
                if (absListView != null)
                    {absListView.setFastScrollEnabled(pref);}
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.blocklist_menu:
                List<Pair<String, ChanBlocklist.BlockType>> blocks = ChanBlocklist.getSorted(getActivity());
                (new BlocklistViewAllDialogFragment(blocks, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$16.onDismiss(android.content.DialogInterface)",this,dialog);try{getLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);
                        if (onTablet() && getActivity() != null)
                            {((ThreadActivity)getActivity()).restartLoader();}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$16.onDismiss(android.content.DialogInterface)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$16.onDismiss(android.content.DialogInterface)",this,throwable);throw throwable;}
                    }
                })).show(getActivity().getFragmentManager(), TAG);
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            default:
                ThreadActivity activity = (ThreadActivity)getActivity();
                if (activity != null)
                    {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return activity.onOptionsItemSelected(item);}}
                else
                    {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this);return super.onOptionsItemSelected(item);}}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    protected void addToWatchlist() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.addToWatchlist()",this);try{addToWatchlist(getActivityContext(), handler, boardCode, threadNo);
        setWatchMenuAsync();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.addToWatchlist()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.addToWatchlist()",this,throwable);throw throwable;}
    }

    protected void removeFromWatchlist() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.removeFromWatchlist()",this);try{removeFromWatchlist(getActivityContext(), handler, boardCode, threadNo);
        setWatchMenuAsync();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.removeFromWatchlist()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.removeFromWatchlist()",this,throwable);throw throwable;}
    }

    public static void addToWatchlist(final Context context, final Handler handler,
                                      final String boardCode, final long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.fragment.ThreadFragment.addToWatchlist(android.content.Context,android.os.Handler,java.lang.String,long)",context,handler,boardCode,threadNo);try{new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$17.run()",this);try{int msgId;
                try {
                    final ChanThread thread = ChanFileStorage.loadThreadData(context, boardCode, threadNo);
                    if (thread == null) {
                        Log.e(TAG, "Couldn't add null thread /" + boardCode + "/" + threadNo + " to watchlist");
                        msgId = R.string.thread_not_added_to_watchlist;
                    }
                    else {
                        ChanFileStorage.addWatchedThread(context, thread);
                        BoardActivity.refreshWatchlist(context);
                        WidgetProviderUtils.scheduleGlobalAlarm(context); /*// insure watchlist is updated*/
                        msgId = R.string.thread_added_to_watchlist;
                        if (DEBUG) {Log.i(TAG, "Added /" + boardCode + "/" + threadNo + " to watchlist");}
                    }
                }
                catch (IOException e) {
                    msgId = R.string.thread_not_added_to_watchlist;
                    Log.e(TAG, "Exception adding /" + boardCode + "/" + threadNo + " to watchlist", e);
                }
                final int stringId = msgId;
                if (handler != null)
                    {handler.post(new Runnable() {
                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$17$1.run()",this);try{Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$17$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$17$1.run()",this,throwable);throw throwable;}
                        }
                    });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$17.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$17.run()",this,throwable);throw throwable;}
            }
        }).start();com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.fragment.ThreadFragment.addToWatchlist(android.content.Context,android.os.Handler,java.lang.String,long)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.addToWatchlist(android.content.Context,android.os.Handler,java.lang.String,long)",throwable);throw throwable;}
    }

    public static void removeFromWatchlist(final Context context, final Handler handler,
                                      final String boardCode, final long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.fragment.ThreadFragment.removeFromWatchlist(android.content.Context,android.os.Handler,java.lang.String,long)",context,handler,boardCode,threadNo);try{new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$18.run()",this);try{int msgId;
                try {
                    final ChanThread thread = ChanFileStorage.loadThreadData(context, boardCode, threadNo);
                    if (thread == null) {
                        Log.e(TAG, "Couldn't remove thread /" + boardCode + "/" + threadNo + " from watchlist");
                        msgId = R.string.thread_watchlist_not_deleted_thread;
                    }
                    else {
                        boolean isDead = thread.isDead;
                        ChanFileStorage.deleteWatchedThread(context, thread);
                        BoardActivity.refreshWatchlist(context);
                        if (isDead)
                            {BoardActivity.updateBoard(context, boardCode);}
                        msgId = R.string.thread_deleted_from_watchlist;
                        if (DEBUG) {Log.i(TAG, "Deleted /" + boardCode + "/" + threadNo + " from watchlist");}
                    }
                }
                catch (IOException e) {
                    msgId = R.string.thread_watchlist_not_deleted_thread;
                    Log.e(TAG, "Exception deleting /" + boardCode + "/" + threadNo + " from watchlist", e);
                }
                final int stringId = msgId;
                /*
                if (handler != null)
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
                        }
                    });
                    */com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$18.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$18.run()",this,throwable);throw throwable;}
            }
        }).start();com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.fragment.ThreadFragment.removeFromWatchlist(android.content.Context,android.os.Handler,java.lang.String,long)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.removeFromWatchlist(android.content.Context,android.os.Handler,java.lang.String,long)",throwable);throw throwable;}
    }

    public ChanActivityId getChanActivityId() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.ChanActivityId com.chanapps.four.fragment.ThreadFragment.getChanActivityId()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanActivityId com.chanapps.four.fragment.ThreadFragment.getChanActivityId()",this);return new ChanActivityId(LastActivity.THREAD_ACTIVITY, boardCode, threadNo, postNo, query);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.ChanActivityId com.chanapps.four.fragment.ThreadFragment.getChanActivityId()",this,throwable);throw throwable;}
    }

    protected String selectText(SparseBooleanArray postPos) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.fragment.ThreadFragment.selectText(android.util.SparseBooleanArray)",this,postPos);try{String text = "";
        for (int i = 0; i < absListView.getCount(); i++) {
            if (!postPos.get(i))
                {continue;}
            Cursor cursor = (Cursor) adapter.getItem(i);
            if (cursor == null)
                {continue;}
            String subject = cursor.getString(cursor.getColumnIndex(ChanPost.POST_SUBJECT_TEXT));
            String message = cursor.getString(cursor.getColumnIndex(ChanPost.POST_TEXT));
            text = subject
                    + (!subject.isEmpty() && !message.isEmpty() ? "<br/>" : "")
                    + message;
            if (DEBUG) {Log.i(TAG, "selectText() raw text=" + text);}
            break;
        }
        text = text.replaceAll("(</?br/?>)+", "\n").replaceAll("<[^>]*>", "");
        if (DEBUG) {Log.i(TAG, "selectText() returning filtered text=" + text);}
        {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.fragment.ThreadFragment.selectText(android.util.SparseBooleanArray)",this);return text;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.fragment.ThreadFragment.selectText(android.util.SparseBooleanArray)",this,throwable);throw throwable;}
    }

    protected String selectQuoteText(SparseBooleanArray postPos) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.fragment.ThreadFragment.selectQuoteText(android.util.SparseBooleanArray)",this,postPos);try{for (int i = 0; i < absListView.getCount(); i++) {
            if (!postPos.get(i))
                {continue;}
            {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.fragment.ThreadFragment.selectQuoteText(android.util.SparseBooleanArray)",this);return selectQuoteText(i);}
        }
        {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.fragment.ThreadFragment.selectQuoteText(android.util.SparseBooleanArray)",this);return "";}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.fragment.ThreadFragment.selectQuoteText(android.util.SparseBooleanArray)",this,throwable);throw throwable;}
    }

    protected String selectQuoteText(int i) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.fragment.ThreadFragment.selectQuoteText(int)",this,i);try{Cursor cursor = adapter.getCursor();
        if (cursor == null)
            {{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.fragment.ThreadFragment.selectQuoteText(int)",this);return "";}}
        cursor.moveToPosition(i);
        long postNo = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
        long resto = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_RESTO));
        String t = cursor.getString(cursor.getColumnIndex(ChanPost.POST_SUBJECT_TEXT));
        String u = cursor.getString(cursor.getColumnIndex(ChanPost.POST_TEXT));
        String itemText = (t == null ? "" : t)
                + (t != null && u != null && !t.isEmpty() && !u.isEmpty() ? "<br/>" : "")
                + (u == null ? "" : u);
        if (itemText == null)
            {itemText = "";}
        String postPrefix = ">>" + postNo + "\n";
        String text = postPrefix + ChanPost.quoteText(itemText, resto);
        if (DEBUG) {Log.i(TAG, "Selected itemText=" + itemText + " resulting quoteText=" + text);}
        {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.fragment.ThreadFragment.selectQuoteText(int)",this);return text;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.fragment.ThreadFragment.selectQuoteText(int)",this,throwable);throw throwable;}
    }

    protected void copyToClipboard(String text) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.copyToClipboard(java.lang.String)",this,text);try{android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivityContext().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(
                getActivityContext().getString(R.string.app_name),
                ChanPost.planifyText(text));
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivityContext(), R.string.copy_text_complete, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.copyToClipboard(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.copyToClipboard(java.lang.String)",this,throwable);throw throwable;}
    }

    protected View.OnLongClickListener startActionModeListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment$19.onLongClick(android.view.View)",this,v);try{int pos = absListView.getPositionForView(v);
            Cursor cursor = adapter.getCursor();
            if (cursor.moveToPosition(pos))
                {postNo = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));}
            if (DEBUG) {Log.i(TAG, "on long click for pos=" + pos + " postNo=" + postNo);}

            View itemView = null;
            for (int i = 0; i < absListView.getChildCount(); i++) {
                View child = absListView.getChildAt(i);
                if (absListView.getPositionForView(child) == pos) {
                    itemView = child;
                    break;
                }
            }
            if (DEBUG) {Log.i(TAG, "found itemView=" + itemView);}
            if (itemView == null)
                {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$19.onLongClick(android.view.View)",this);return false;}}

            /*//absListView.setItemChecked(pos, true);*/

            if (actionMode == null) {
                if (DEBUG) {Log.i(TAG, "starting action mode...");}
                getActivity().startActionMode(actionModeCallback);
                if (DEBUG) {Log.i(TAG, "started action mode");}
            }
            else {
                if (DEBUG) {Log.i(TAG, "action mode already started, updating share intent");}
                /*//updateSharedIntent(shareActionProvider, absListView.getCheckedItemPositions());*/
            }
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$19.onLongClick(android.view.View)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment$19.onLongClick(android.view.View)",this,throwable);throw throwable;}
        }
    };

    protected Set<Pair<String, ChanBlocklist.BlockType>> extractBlocklist(SparseBooleanArray postPos) {
        com.mijack.Xlog.logMethodEnter("java.util.HashSet com.chanapps.four.fragment.ThreadFragment.extractBlocklist(android.util.SparseBooleanArray)",this,postPos);try{Set<Pair<String, ChanBlocklist.BlockType>> blocklist = new HashSet<Pair<String, ChanBlocklist.BlockType>>();
        if (adapter == null)
            {{com.mijack.Xlog.logMethodExit("java.util.HashSet com.chanapps.four.fragment.ThreadFragment.extractBlocklist(android.util.SparseBooleanArray)",this);return blocklist;}}
        Cursor cursor = adapter.getCursor();
        if (cursor == null)
            {{com.mijack.Xlog.logMethodExit("java.util.HashSet com.chanapps.four.fragment.ThreadFragment.extractBlocklist(android.util.SparseBooleanArray)",this);return blocklist;}}

        for (int i = 0; i < adapter.getCount(); i++) {
            if (!postPos.get(i))
                {continue;}
            if (!cursor.moveToPosition(i))
                {continue;}
            String tripcode = cursor.getString(cursor.getColumnIndex(ChanPost.POST_TRIPCODE));
            if (tripcode != null && !tripcode.isEmpty())
                {blocklist.add(new Pair<String, ChanBlocklist.BlockType>(tripcode, ChanBlocklist.BlockType.TRIPCODE));}

            String name = cursor.getString(cursor.getColumnIndex(ChanPost.POST_NAME));
            if (name != null && !name.isEmpty() && !name.equals("Anonymous"))
                {blocklist.add(new Pair<String, ChanBlocklist.BlockType>(tripcode, ChanBlocklist.BlockType.NAME));}

            String email = cursor.getString(cursor.getColumnIndex(ChanPost.POST_EMAIL));
            if (email != null && !email.isEmpty() && !email.equals("sage"))
                {blocklist.add(new Pair<String, ChanBlocklist.BlockType>(tripcode, ChanBlocklist.BlockType.EMAIL));}

            String userId = cursor.getString(cursor.getColumnIndex(ChanPost.POST_USER_ID));
            if (userId != null && !userId.isEmpty() && !userId.equals("Heaven"))
                {blocklist.add(new Pair<String, ChanBlocklist.BlockType>(tripcode, ChanBlocklist.BlockType.ID));}
        }

        {com.mijack.Xlog.logMethodExit("java.util.HashSet com.chanapps.four.fragment.ThreadFragment.extractBlocklist(android.util.SparseBooleanArray)",this);return blocklist;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.HashSet com.chanapps.four.fragment.ThreadFragment.extractBlocklist(android.util.SparseBooleanArray)",this,throwable);throw throwable;}
    }

    protected boolean translatePosts(SparseBooleanArray postPos) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment.translatePosts(android.util.SparseBooleanArray)",this,postPos);try{final Locale locale = getResources().getConfiguration().locale;
        final String localeCode = locale.getLanguage();
        final String text = selectText(postPos);
        final String strippedText = text.replaceAll("<br/?>", "\n").replaceAll("<[^>]*>", "").trim();
        if (DEBUG) {Log.i(TAG, "translatePosts() translating text=" + strippedText);}
        String escaped;
        try {
            escaped = URLEncoder.encode(strippedText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Unsupported encoding utf-8? You crazy!", e);
            escaped = strippedText;
        }
        if (escaped.isEmpty()) {
            Toast.makeText(getActivityContext(), R.string.translate_no_text, Toast.LENGTH_SHORT);
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.translatePosts(android.util.SparseBooleanArray)",this);return true;}
        }
        String translateUrl = String.format(
                URLFormatComponent.getUrl(getActivityContext(), URLFormatComponent.GOOGLE_TRANSLATE_URL_FORMAT),
                localeCode, localeCode, escaped);
        if (translateUrl.length() > MAX_HTTP_GET_URL_LEN)
            {translateUrl = translateUrl.substring(0, MAX_HTTP_GET_URL_LEN);}
        if (DEBUG) {Log.i(TAG, "translatePosts() launching url=" + translateUrl);}
        ActivityDispatcher.launchUrlInBrowser(getActivityContext(), translateUrl);
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.translatePosts(android.util.SparseBooleanArray)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment.translatePosts(android.util.SparseBooleanArray)",this,throwable);throw throwable;}
    }

    protected boolean playThreadMenu() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment.playThreadMenu()",this);try{/*//NetworkProfileManager.instance().getUserStatistics().featureUsed(ChanFeature.PLAY_THREAD);*/
        synchronized (this) {
            shouldPlayThread = !shouldPlayThread; /*// user clicked, invert play status*/
            getActivity().invalidateOptionsMenu();
            if (!shouldPlayThread) {
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.playThreadMenu()",this);return false;}
            }
            if (!canPlayThread()) {
                shouldPlayThread = false;
                Toast.makeText(getActivityContext(), R.string.thread_no_start_play, Toast.LENGTH_SHORT).show();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.playThreadMenu()",this);return false;}
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$20.run()",this);try{if (handler != null)
                        {handler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$20$1.run()",this);try{/*//absListView.setFastScrollEnabled(false);*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$20$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$20$1.run()",this,throwable);throw throwable;}
                            }
                        });}
                    while (true) {
                        synchronized (this) {
                            if (!canPlayThread())
                                {break;}
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$20$2.run()",this);try{if (absListView == null || adapter == null)
                                        {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.playThreadMenu()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$20.run()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$20$2.run()",this);return;}}}}
                                    absListView.smoothScrollBy(2, 25);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$20$2.run()",this,throwable);throw throwable;}
                                }
                                /*
                                private void expandVisibleItem(int first, int pos) {
                                    View listItem = absListView.getChildAt(pos - first);
                                    View image = listItem == null ? null : listItem.findViewById(R.id.list_item_image);
                                    Cursor cursor = adapter.getCursor();
                                    //if (DEBUG) Log.i(TAG, "pos=" + pos + " listItem=" + listItem + " expandButton=" + expandButton);
                                    if (listItem != null
                                            && image != null
                                            && image.getVisibility() == View.VISIBLE
                                            && image.getHeight() > 0
                                            && cursor.moveToPosition(pos))
                                    {
                                        long id = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
                                        absListView.performItemClick(image, pos, id);
                                    }
                                }
                                */
                            });
                        }
                        try {
                            Thread.sleep(25);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    synchronized (this) {
                        shouldPlayThread = false;
                    }
                    if (handler != null)
                        {handler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$20$3.run()",this);try{/*//absListView.setFastScrollEnabled(true);*/
                                getActivity().invalidateOptionsMenu();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$20$3.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$20$3.run()",this,throwable);throw throwable;}
                            }
                        });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$20.run()",this,throwable);throw throwable;}
                }
            }).start();
        }
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.playThreadMenu()",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment.playThreadMenu()",this,throwable);throw throwable;}
    }

    protected boolean canPlayThread() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment.canPlayThread()",this);try{if (shouldPlayThread == false)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.canPlayThread()",this);return false;}}
        if (absListView == null || adapter == null || adapter.getCount() <= 0)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.canPlayThread()",this);return false;}}
        /*//if (absListView.getLastVisiblePosition() == adapter.getCount() - 1)*/
        /*//    return false; // stop*/
        /*//It is scrolled all the way down here*/
        if (absListView.getLastVisiblePosition() >= absListView.getAdapter().getCount() - 1)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.canPlayThread()",this);return false;}}
        if (handler == null)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.canPlayThread()",this);return false;}}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment.canPlayThread()",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment.canPlayThread()",this,throwable);throw throwable;}
    }

    private void setShareIntent(final ShareActionProvider provider, final Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.setShareIntent(android.widget.ShareActionProvider,android.content.Intent)",this,provider,intent);try{if (ActivityDispatcher.onUIThread())
            {synchronized (this) {
                if (provider != null && intent != null)
                    {provider.setShareIntent(intent);}
            }}
        else if (handler != null)
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$21.run()",this);try{synchronized (this) {
                        if (provider != null && intent != null)
                            {provider.setShareIntent(intent);}
                    }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$21.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$21.run()",this,throwable);throw throwable;}
                }
            });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.setShareIntent(android.widget.ShareActionProvider,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.setShareIntent(android.widget.ShareActionProvider,android.content.Intent)",this,throwable);throw throwable;}
    }

    protected void updateSharedIntent(ShareActionProvider provider, SparseBooleanArray postPos) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.updateSharedIntent(android.widget.ShareActionProvider,android.util.SparseBooleanArray)",this,provider,postPos);try{if (postPos == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.updateSharedIntent(android.widget.ShareActionProvider,android.util.SparseBooleanArray)",this);return;}}
        if (DEBUG) {Log.i(TAG, "updateSharedIntent() checked count=" + postPos.size());}
        if (postPos.size() < 1)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.updateSharedIntent(android.widget.ShareActionProvider,android.util.SparseBooleanArray)",this);return;}}
        Cursor cursor = adapter.getCursor();
        if (cursor == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.updateSharedIntent(android.widget.ShareActionProvider,android.util.SparseBooleanArray)",this);return;}}

        /*// construct paths and add files*/
        /*//ArrayList<String> paths = new ArrayList<String>();*/
        /*//long firstPost = -1;*/
        /*//ImageLoader imageLoader = ChanImageLoader.getInstance(getActivityContext());*/
        String url = null;
        for (int i = 0; i < cursor.getCount(); i++) {
            if (!postPos.get(i))
                {continue;}
            if (!cursor.moveToPosition(i))
                {continue;}
            /*//if (firstPost == -1)*/
            /*//    firstPost = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));*/
            /*//File file = ThreadViewer.fullSizeImageFile(getActivityContext(), cursor); // try for full size first*/
            /*//if (file == null) { // if can't find it, fall back to thumbnail*/
            url = cursor.getString(cursor.getColumnIndex(ChanPost.POST_IMAGE_URL)); /*// thumbnail*/
            if (url != null && !url.isEmpty())
                {break;}
                /*//if (DEBUG) Log.i(TAG, "Couldn't find full image, falling back to thumbnail=" + url);*/
                /*//file = (url == null || url.isEmpty()) ? null : imageLoader.getDiscCache().get(url);*/
            /*//}*/
            /*//if (file == null || !file.exists() || !file.canRead() || file.length() <= 0)*/
            /*//    continue;*/
            /*//paths.add(file.getAbsolutePath());*/
        }
        if (url == null || url.isEmpty())
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.updateSharedIntent(android.widget.ShareActionProvider,android.util.SparseBooleanArray)",this);return;}}

        /*// set share text*/
        /*//if (DEBUG) Log.i(TAG, "updateSharedIntent() found postNo=" + firstPost + " for threadNo=" + threadNo);*/
        /*
        String linkUrl = (firstPost > 0 && firstPost != threadNo)
                ? ChanPost.postUrl(getActivityContext(), boardCode, threadNo, firstPost)
                : ChanThread.threadUrl(getActivityContext(), boardCode, threadNo);
        */
        /*// create intent*/
        Intent intent;
        intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        setShareIntent(provider, intent);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.updateSharedIntent(android.widget.ShareActionProvider,android.util.SparseBooleanArray)",this,throwable);throw throwable;}
    }

    protected void updateSharedIntentOP(ShareActionProvider provider) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.updateSharedIntentOP(android.widget.ShareActionProvider)",this,provider);try{String url = ChanThread.threadUrl(getActivityContext(), boardCode, threadNo);
        Intent intent;
        intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        setShareIntent(provider, intent);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.updateSharedIntentOP(android.widget.ShareActionProvider)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.updateSharedIntentOP(android.widget.ShareActionProvider)",this,throwable);throw throwable;}
    }

    protected void asyncUpdateSharedIntent(ArrayList<String> pathList) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.asyncUpdateSharedIntent(java.util.ArrayList)",this,pathList);try{String[] paths = new String[pathList.size()];
        String[] types = new String[pathList.size()];
        for (int i = 0; i < pathList.size(); i++) {
            paths[i] = pathList.get(i);
            types[i] = "image/jpeg";
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.asyncUpdateSharedIntent(java.util.ArrayList)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.asyncUpdateSharedIntent(java.util.ArrayList)",this,throwable);throw throwable;}
    }

    public void onRefresh() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.onRefresh()",this);try{if (DEBUG) {Log.i(TAG, "onRefresh() /" + boardCode + "/" + threadNo);}
        if (getActivity() != null)
            {getActivity().invalidateOptionsMenu();} /*// in case spinner needs to be reset*/
        refreshThread();
        if (actionMode != null)
            {actionMode.finish();}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.onRefresh()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.onRefresh()",this,throwable);throw throwable;}
    }

    public void refreshThread() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.refreshThread()",this);try{refreshThread(null);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.refreshThread()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.refreshThread()",this,throwable);throw throwable;}
    }

    public void refreshThread(final String message) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.refreshThread(java.lang.String)",this,message);try{new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$22.run()",this);try{ChanThread thread = ChanFileStorage.loadThreadData(getActivityContext(), boardCode, threadNo);
                if (DEBUG) {Log.i(TAG, "refreshThread /" + boardCode + "/" + threadNo + " checking status");}
                if (thread != null && thread.isDead) {
                    if (DEBUG) {Log.i(TAG, "refreshThread /" + boardCode + "/" + threadNo + " found dead thread");}
                }
                if (handler != null && getActivity() != null && getActivity().getLoaderManager() != null) {
                    if (DEBUG) {Log.i(TAG, "refreshThread /" + boardCode + "/" + threadNo + " scheduling handler post");}
                    if (handler != null)
                        {handler.post(new Runnable() {
                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$22$1.run()",this);try{if (DEBUG) {Log.i(TAG, "refreshThread /" + boardCode + "/" + threadNo + " restarting loader");}
                            if (getActivity() != null && getActivity().getLoaderManager() != null) {
                                getLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);
                                if (message != null && !message.isEmpty())
                                    {Toast.makeText(getActivityContext(), message, Toast.LENGTH_SHORT).show();}
                            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$22$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$22$1.run()",this,throwable);throw throwable;}
                        }
                    });}
                }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$22.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$22.run()",this,throwable);throw throwable;}
            }
        }).start();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.refreshThread(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.refreshThread(java.lang.String)",this,throwable);throw throwable;}
    }

    protected View.OnClickListener overflowListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$23.onClick(android.view.View)",this,v);try{if (v == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$23.onClick(android.view.View)",this);return;}}
            int pos = -1;
            SparseBooleanArray checked;
            synchronized (this) {
                if (absListView == null)
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$23.onClick(android.view.View)",this);return;}}
                pos = absListView == null ? -1 : absListView.getPositionForView(v);
                if (absListView != null && pos >= 0) {
                    absListView.setItemChecked(pos, true);
                    postNo = absListView == null ? -1 : absListView.getItemIdAtPosition(pos);
                }
                checked = absListView == null ? null : absListView.getCheckedItemPositions();
            }
            if (pos == -1)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$23.onClick(android.view.View)",this);return;}}
            /*//updateSharedIntent(shareActionProvider, checked);*/
            PopupMenu popup = new PopupMenu(getActivityContext(), v);
            Cursor cursor = adapter.getCursor();
            boolean hasImage = cursor != null
                    && (cursor.getInt(cursor.getColumnIndex(ChanPost.POST_FLAGS)) & ChanPost.FLAG_HAS_IMAGE) > 0;
                boolean isHeader = pos == 0;
                int menuId;
            if (!undead())
                {menuId = R.menu.thread_dead_context_menu;}
            else if (isHeader)
                {menuId = R.menu.thread_header_context_menu;}
            else if (hasImage)
                {menuId = R.menu.thread_image_context_menu;}
            else
                {menuId = R.menu.thread_text_context_menu;}
            popup.inflate(menuId);
            popup.setOnMenuItemClickListener(popupListener);
            popup.setOnDismissListener(popupDismissListener);
            MenuItem shareItem = popup.getMenu().findItem(R.id.thread_context_share_action_menu);
            /*//shareActionProvider = shareItem == null ? null : (ShareActionProvider) shareItem.getActionProvider();*/
            /*//if (DEBUG) Log.i(TAG, "overflowListener.onClick() popup called shareActionProvider=" + shareActionProvider);*/
            popup.show();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$23.onClick(android.view.View)",this,throwable);throw throwable;}
        }
    };

    protected PopupMenu.OnMenuItemClickListener popupListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment$24.onMenuItemClick(android.view.MenuItem)",this,item);try{long[] postNos = absListView.getCheckedItemIds();
            SparseBooleanArray postPos = absListView.getCheckedItemPositions();
            if (postNos.length == 0) {
                Toast.makeText(getActivityContext(), R.string.thread_no_posts_selected, Toast.LENGTH_SHORT).show();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$24.onMenuItemClick(android.view.MenuItem)",this);return false;}
            }
            switch (item.getItemId()) {
                case R.id.post_reply_all_menu:
                    if (DEBUG) {Log.i(TAG, "Post nos: " + Arrays.toString(postNos));}
                    postReply(replyText(postNos), selectQuoteText(postPos));
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$24.onMenuItemClick(android.view.MenuItem)",this);return true;}
                case R.id.copy_text_menu:
                    String selectText = selectText(postPos);
                    copyToClipboard(selectText);
                    /*//(new SelectTextDialogFragment(text)).show(getFragmentManager(), SelectTextDialogFragment.TAG);*/
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$24.onMenuItemClick(android.view.MenuItem)",this);return true;}
                case R.id.download_images_to_gallery_menu:
                    ThreadImageDownloadService.startDownloadViaThreadMenu(
                            getActivityContext(), boardCode, threadNo, postNos);
                    Toast.makeText(getActivityContext(), R.string.download_all_images_notice, Toast.LENGTH_SHORT).show();
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$24.onMenuItemClick(android.view.MenuItem)",this);return true;}
                case R.id.translate_posts_menu:
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$24.onMenuItemClick(android.view.MenuItem)",this);return translatePosts(postPos);}
                case R.id.delete_posts_menu:
                    (new DeletePostDialogFragment(boardCode, threadNo, postNos))
                            .show(getFragmentManager(), DeletePostDialogFragment.TAG);
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$24.onMenuItemClick(android.view.MenuItem)",this);return true;}
                case R.id.report_posts_menu:
                    (new ReportPostDialogFragment(boardCode, threadNo, postNos))
                            .show(getFragmentManager(), ReportPostDialogFragment.TAG);
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$24.onMenuItemClick(android.view.MenuItem)",this);return true;}
                case R.id.web_menu:
                    String url = ChanPost.postUrl(getActivityContext(), boardCode, threadNo, postNos[0]);
                    ActivityDispatcher.launchUrlInBrowser(getActivityContext(), url);
                default:
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$24.onMenuItemClick(android.view.MenuItem)",this);return false;}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment$24.onMenuItemClick(android.view.MenuItem)",this,throwable);throw throwable;}
        }
    };

    protected PopupMenu.OnDismissListener popupDismissListener = new PopupMenu.OnDismissListener() {
        {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$25.onDismiss(android.widget.PopupMenu)",this,menu);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$25.onDismiss(android.widget.PopupMenu)",this);}
    };

    protected LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            com.mijack.Xlog.logMethodEnter("android.support.v4.content.Loader com.chanapps.four.fragment.ThreadFragment$26.onCreateLoader(int,android.os.Bundle)",this,id,args);try{if (DEBUG) {Log.i(TAG, "onCreateLoader /" + boardCode + "/" + threadNo + " q=" + query + " id=" + id);}
            setProgress(true);
            boolean showRelatedBoards;
            if (onTablet())
                {showRelatedBoards = false;}
            else {showRelatedBoards = true;}
            {com.mijack.Xlog.logMethodExit("android.support.v4.content.Loader com.chanapps.four.fragment.ThreadFragment$26.onCreateLoader(int,android.os.Bundle)",this);return new ThreadCursorLoader(getActivityContext(), boardCode, threadNo, query, showRelatedBoards);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.support.v4.content.Loader com.chanapps.four.fragment.ThreadFragment$26.onCreateLoader(int,android.os.Bundle)",this,throwable);throw throwable;}
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$26.onLoadFinished(android.support.v4.content.Loader,android.database.Cursor)",this,loader,data);try{if (DEBUG) {Log.i(TAG, "onLoadFinished /" + boardCode + "/" + threadNo + " id=" + loader.getId()
                    + " count=" + (data == null ? 0 : data.getCount()) + " loader=" + loader);}
            onThreadLoadFinished(data);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$26.onLoadFinished(android.support.v4.content.Loader,android.database.Cursor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$26.onLoadFinished(android.support.v4.content.Loader,android.database.Cursor)",this,throwable);throw throwable;}
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$26.onLoaderReset(android.support.v4.content.Loader)",this,loader);try{if (DEBUG) {Log.i(TAG, "onLoaderReset /" + boardCode + "/" + threadNo + " id=" + loader.getId());}
            /*//adapter.swapCursor(null);*/
            adapter.changeCursor(null);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$26.onLoaderReset(android.support.v4.content.Loader)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$26.onLoaderReset(android.support.v4.content.Loader)",this,throwable);throw throwable;}
        }
    };

    protected Context getActivityContext() {
        com.mijack.Xlog.logMethodEnter("android.content.Context com.chanapps.four.fragment.ThreadFragment.getActivityContext()",this);try{com.mijack.Xlog.logMethodExit("android.content.Context com.chanapps.four.fragment.ThreadFragment.getActivityContext()",this);return getActivity();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.Context com.chanapps.four.fragment.ThreadFragment.getActivityContext()",this,throwable);throw throwable;}
    }

    protected ChanIdentifiedActivity getChanActivity() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.ChanIdentifiedActivity com.chanapps.four.fragment.ThreadFragment.getChanActivity()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanIdentifiedActivity com.chanapps.four.fragment.ThreadFragment.getChanActivity()",this);return (ChanIdentifiedActivity)getActivity();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.ChanIdentifiedActivity com.chanapps.four.fragment.ThreadFragment.getChanActivity()",this,throwable);throw throwable;}
    }
    
    protected ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment$27.onCreateActionMode(android.view.ActionMode,android.view.Menu)",this,mode,menu);try{if (DEBUG) {Log.i(TAG, "onCreateActionMode");}
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.thread_text_context_menu, menu);
            MenuItem shareItem = menu.findItem(R.id.thread_context_share_action_menu);
            /*//if (shareItem != null) {*/
            /*//    shareActionProvider = (ShareActionProvider) shareItem.getActionProvider();*/
            /*//} else {*/
            /*//    shareActionProvider = null;*/
            /*//}*/
            mode.setTitle(R.string.thread_context_select);
            actionMode = mode;
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$27.onCreateActionMode(android.view.ActionMode,android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment$27.onCreateActionMode(android.view.ActionMode,android.view.Menu)",this,throwable);throw throwable;}
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment$27.onPrepareActionMode(android.view.ActionMode,android.view.Menu)",this,mode,menu);try{if (DEBUG) {Log.i(TAG, "onPrepareActionMode");}
            /*//updateSharedIntent(shareActionProvider, absListView.getCheckedItemPositions());*/
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$27.onPrepareActionMode(android.view.ActionMode,android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment$27.onPrepareActionMode(android.view.ActionMode,android.view.Menu)",this,throwable);throw throwable;}
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment$27.onActionItemClicked(android.view.ActionMode,android.view.MenuItem)",this,mode,item);try{long[] postNos = absListView.getCheckedItemIds();
            SparseBooleanArray postPos = absListView.getCheckedItemPositions();
            if (postNos.length == 0) {
                Toast.makeText(getActivityContext(), R.string.thread_no_posts_selected, Toast.LENGTH_SHORT).show();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$27.onActionItemClicked(android.view.ActionMode,android.view.MenuItem)",this);return false;}
            }

            switch (item.getItemId()) {
                case R.id.post_reply_all_menu:
                    if (DEBUG) {Log.i(TAG, "Post nos: " + Arrays.toString(postNos));}
                    postReply(replyText(postNos), selectQuoteText(postPos));
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$27.onActionItemClicked(android.view.ActionMode,android.view.MenuItem)",this);return true;}
                /*
                case R.id.post_reply_all_quote_menu:
                    String quotesText = selectQuoteText(postPos);
                    postReply(quotesText);
                    return true;
                */
                case R.id.copy_text_menu:
                    String selectText = selectText(postPos);
                    copyToClipboard(selectText);
                    /*//(new SelectTextDialogFragment(text)).show(getFragmentManager(), SelectTextDialogFragment.TAG);*/
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$27.onActionItemClicked(android.view.ActionMode,android.view.MenuItem)",this);return true;}
                case R.id.download_images_to_gallery_menu:
                    ThreadImageDownloadService.startDownloadViaThreadMenu(
                            getActivityContext(), boardCode, threadNo, postNos);
                    Toast.makeText(getActivityContext(), R.string.download_all_images_notice, Toast.LENGTH_SHORT).show();
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$27.onActionItemClicked(android.view.ActionMode,android.view.MenuItem)",this);return true;}
                case R.id.translate_posts_menu:
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$27.onActionItemClicked(android.view.ActionMode,android.view.MenuItem)",this);return translatePosts(postPos);}
                case R.id.delete_posts_menu:
                    (new DeletePostDialogFragment(boardCode, threadNo, postNos))
                            .show(getFragmentManager(), DeletePostDialogFragment.TAG);
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$27.onActionItemClicked(android.view.ActionMode,android.view.MenuItem)",this);return true;}
                case R.id.report_posts_menu:
                    (new ReportPostDialogFragment(boardCode, threadNo, postNos))
                            .show(getFragmentManager(), ReportPostDialogFragment.TAG);
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$27.onActionItemClicked(android.view.ActionMode,android.view.MenuItem)",this);return true;}
                case R.id.web_menu:
                    String url = ChanPost.postUrl(getActivityContext(), boardCode, threadNo, postNos[0]);
                    ActivityDispatcher.launchUrlInBrowser(getActivityContext(), url);
                default:
                    {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$27.onActionItemClicked(android.view.ActionMode,android.view.MenuItem)",this);return false;}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment$27.onActionItemClicked(android.view.ActionMode,android.view.MenuItem)",this,throwable);throw throwable;}
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$27.onDestroyActionMode(android.view.ActionMode)",this,mode);try{SparseBooleanArray positions = absListView.getCheckedItemPositions();
            if (DEBUG) {Log.i(TAG, "onDestroyActionMode checked size=" + positions.size());}
            for (int i = 0; i < absListView.getCount(); i++) {
                if (positions.get(i)) {
                    absListView.setItemChecked(i, false);
                }
            }
            actionMode = null;com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$27.onDestroyActionMode(android.view.ActionMode)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$27.onDestroyActionMode(android.view.ActionMode)",this,throwable);throw throwable;}
        }
    };

    protected MediaScannerConnection.OnScanCompletedListener mediaScannerListener
            = new MediaScannerConnection.MediaScannerConnectionClient()
    {
        {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$28.onMediaScannerConnected()",this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$28.onMediaScannerConnected()",this);}
        @Override
        public void onScanCompleted(String path, Uri uri) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$28.onScanCompleted(java.lang.String,android.net.Uri)",this,path,uri);try{if (DEBUG) {Log.i(TAG, "Scan completed for path=" + path + " result uri=" + uri);}
            if (uri == null)
                {uri = Uri.parse(path);}
            checkedImageUris.put(path, uri);
            /*//updateSharedIntent(shareActionProvider, absListView.getCheckedItemPositions());*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$28.onScanCompleted(java.lang.String,android.net.Uri)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$28.onScanCompleted(java.lang.String,android.net.Uri)",this,throwable);throw throwable;}
        }
    };

    @Override
    public void showDialog(String boardCode, long threadNo, long postNo, int pos, ThreadPopupDialogFragment.PopupType popupType) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.showDialog(java.lang.String,long,long,int,ThreadPopupDialogFragment.PopupType)",this,boardCode,threadNo,postNo,pos,popupType);try{if (DEBUG) {Log.i(TAG, "showDialog /" + boardCode + "/" + threadNo + "#p" + postNo + " pos=" + pos);}
        /*//(new ThreadPopupDialogFragment(this, boardCode, threadNo, postNo, pos, popupType, query))*/
        (new ThreadPopupDialogFragment(this, boardCode, threadNo, postNo, popupType, query))
                .show(getFragmentManager(), ThreadPopupDialogFragment.TAG);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.showDialog(java.lang.String,long,long,int,ThreadPopupDialogFragment.PopupType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.showDialog(java.lang.String,long,long,int,ThreadPopupDialogFragment.PopupType)",this,throwable);throw throwable;}
    }

    public String toString() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.fragment.ThreadFragment.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.fragment.ThreadFragment.toString()",this);return "ThreadFragment[] " + getChanActivityId().toString();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.fragment.ThreadFragment.toString()",this,throwable);throw throwable;}
    }

    protected View.OnClickListener goToThreadUrlListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$29.onClick(android.view.View)",this,view);try{if (getActivityContext() != null) {
                String url = ChanThread.threadUrl(getActivityContext(), boardCode, threadNo);
                ActivityDispatcher.launchUrlInBrowser(getActivityContext(), url);
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$29.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$29.onClick(android.view.View)",this,throwable);throw throwable;}
        }
    };

    protected AbstractBoardCursorAdapter.ViewBinder viewBinder = new AbstractBoardCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadFragment$30.setViewValue(android.view.View,android.database.Cursor,int)",this,view,cursor,columnIndex);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadFragment$30.setViewValue(android.view.View,android.database.Cursor,int)",this);return ThreadViewer.setViewValue(view, cursor, boardCode,
                    true,
                    0,
                    0,
                    threadListener.thumbOnClickListener,
                    threadListener.backlinkOnClickListener,
                    commentsOnClickListener,
                    imagesOnClickListener,
                    threadListener.repliesOnClickListener,
                    threadListener.sameIdOnClickListener,
                    threadListener.exifOnClickListener,
                    overflowListener,
                    threadListener.expandedImageListener,
                    startActionModeListener,
                    goToThreadUrlListener
            );}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadFragment$30.setViewValue(android.view.View,android.database.Cursor,int)",this,throwable);throw throwable;}
        }
    };

    protected void displaySearchTitle() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.displaySearchTitle()",this);try{if (getActivity() == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.displaySearchTitle()",this);return;}}
        displayTitleBar(getString(R.string.search_results_title), R.drawable.search, R.drawable.search_light);
        displayResultsBar();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.displaySearchTitle()",this,throwable);throw throwable;}
    }

    protected void displayResultsBar() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.displayResultsBar()",this);try{if (boardSearchResultsBar == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.displayResultsBar()",this);return;}}
        if (query == null || query.isEmpty()) {
            boardSearchResultsBar.setVisibility(View.GONE);
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.displayResultsBar()",this);return;}
        }
        int resultsId = adapter != null && adapter.getCount() > 0
                ? R.string.thread_search_results
                : R.string.thread_search_no_results;
        String results = String.format(getString(resultsId), query);
        TextView searchResultsTextView = (TextView)boardSearchResultsBar.findViewById(R.id.board_search_results_text);
        searchResultsTextView.setText(results);
        boardSearchResultsBar.setVisibility(View.VISIBLE);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.displayResultsBar()",this,throwable);throw throwable;}
    }

    @TargetApi(16)
    protected void displayTitleBar(String title, int lightIconId, int darkIconId) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.displayTitleBar(java.lang.String,int,int)",this,title,lightIconId,darkIconId);try{if (boardTitleBar == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.displayTitleBar(java.lang.String,int,int)",this);return;}}
        if (query == null || query.isEmpty()) {
            boardTitleBar.setVisibility(View.GONE);
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.displayTitleBar(java.lang.String,int,int)",this);return;}
        }
        TextView boardTitle = (TextView)boardTitleBar.findViewById(R.id.board_title_text);
        ImageView boardIcon = (ImageView)boardTitleBar.findViewById(R.id.board_title_icon);
        if (boardTitle == null || boardIcon == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.displayTitleBar(java.lang.String,int,int)",this);return;}}
        boardTitle.setText(title);
        boolean isDark = ThemeSelector.instance(getActivity().getApplicationContext()).isDark();
        int drawableId = isDark ? lightIconId : darkIconId;
        int alpha = isDark ? DRAWABLE_ALPHA_DARK : DRAWABLE_ALPHA_LIGHT;
        if (drawableId > 0) {
            boardIcon.setImageResource(drawableId);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                {deprecatedSetAlpha(boardIcon, alpha);}
            else
                {boardIcon.setImageAlpha(alpha);}
        }
        boardTitleBar.setVisibility(View.VISIBLE);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.displayTitleBar(java.lang.String,int,int)",this,throwable);throw throwable;}
    }

    @SuppressWarnings("deprecation")
    protected void deprecatedSetAlpha(ImageView v, int a) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.deprecatedSetAlpha(android.widget.ImageView,int)",this,v,a);try{v.setAlpha(a);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.deprecatedSetAlpha(android.widget.ImageView,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.deprecatedSetAlpha(android.widget.ImageView,int)",this,throwable);throw throwable;}
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.onCreateOptionsMenu(android.view.Menu,android.view.MenuInflater)",this,menu,menuInflater);try{menuInflater.inflate(R.menu.thread_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.onCreateOptionsMenu(android.view.Menu,android.view.MenuInflater)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.onCreateOptionsMenu(android.view.Menu,android.view.MenuInflater)",this,throwable);throw throwable;}
    }

    protected void jumpToTop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.jumpToTop()",this);try{ThreadViewer.jumpToTop(absListView, handler);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.jumpToTop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.jumpToTop()",this,throwable);throw throwable;}
    }

    protected void jumpToBottom() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.jumpToBottom()",this);try{ThreadViewer.jumpToBottom(absListView, handler);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.jumpToBottom()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.jumpToBottom()",this,throwable);throw throwable;}
    }

    public String getQuery() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.fragment.ThreadFragment.getQuery()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.fragment.ThreadFragment.getQuery()",this);return query;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.fragment.ThreadFragment.getQuery()",this,throwable);throw throwable;}
    }

    public void setQuery(String query) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.setQuery(java.lang.String)",this,query);try{this.query = query;com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.setQuery(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.setQuery(java.lang.String)",this,throwable);throw throwable;}
    }

    public void onUpdateFastScroll(final boolean enabled) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.onUpdateFastScroll(boolean)",this,enabled);try{final Handler gridHandler = handler != null ? handler : new Handler();
        if (gridHandler != null)
            {gridHandler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$31.run()",this);try{if (absListView != null)
                        {absListView.setFastScrollEnabled(enabled);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$31.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$31.run()",this,throwable);throw throwable;}
                }
            });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.onUpdateFastScroll(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.onUpdateFastScroll(boolean)",this,throwable);throw throwable;}
    }

    protected void loadViewPositionAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.loadViewPositionAsync()",this);try{new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$32.run()",this);try{Context c = getActivityContext();
                if (c == null)
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.loadViewPositionAsync()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$32.run()",this);return;}}}
                ChanThread thread = ChanFileStorage.loadThreadData(c, boardCode, threadNo);
                if (thread == null)
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.loadViewPositionAsync()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$32.run()",this);return;}}}
                final int firstVisiblePosition = thread.viewPosition;
                final int firstVisibleOffset = thread.viewOffset;
                if (firstVisiblePosition >= 0 && handler != null)
                    {handler.post(new Runnable() {
                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$32$1.run()",this);try{if (DEBUG) {Log.i(TAG, "loaded view position /" + boardCode + "/" + threadNo
                                    + " pos=" + firstVisiblePosition + " offset=" + firstVisibleOffset);}
                            if (absListView == null)
                                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.loadViewPositionAsync()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$32.run()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$32$1.run()",this);return;}}}}
                            if (absListView instanceof ListView) {
                                ((ListView)absListView).setSelectionFromTop(firstVisiblePosition, firstVisibleOffset);
                            }
                            else {
                                absListView.requestFocusFromTouch();
                                absListView.setSelection(firstVisiblePosition);
                            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$32$1.run()",this,throwable);throw throwable;}
                        }
                    });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$32.run()",this,throwable);throw throwable;}
            }
        }).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.loadViewPositionAsync()",this,throwable);throw throwable;}
    }

    protected void saveViewPositionAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment.saveViewPositionAsync()",this);try{if (absListView == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.saveViewPositionAsync()",this);return;}}
        final int firstVisiblePosition = absListView.getFirstVisiblePosition();
        final int firstVisibleOffset = absListView.getChildAt(firstVisiblePosition) == null
                ? 0
                : absListView.getChildAt(firstVisiblePosition).getTop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadFragment$33.run()",this);try{Context c = getActivityContext();
                if (c == null)
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.saveViewPositionAsync()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$33.run()",this);return;}}}
                ChanThread thread = ChanFileStorage.loadThreadData(c, boardCode, threadNo);
                if (thread == null)
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment.saveViewPositionAsync()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadFragment$33.run()",this);return;}}}
                thread.viewPosition = firstVisiblePosition;
                thread.viewOffset = firstVisibleOffset;
                try {
                    ChanFileStorage.storeThreadData(c, thread);
                    if (DEBUG) {Log.i(TAG, "saved view position /" + boardCode + "/" + threadNo
                            + " pos=" + firstVisiblePosition + " offset=" + firstVisibleOffset);}
                }
                catch (IOException e) {
                    Log.e(TAG, "Exception saving thread view position /" + boardCode + "/" + threadNo);
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment$33.run()",this,throwable);throw throwable;}
            }
        }).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadFragment.saveViewPositionAsync()",this,throwable);throw throwable;}
    }

}
