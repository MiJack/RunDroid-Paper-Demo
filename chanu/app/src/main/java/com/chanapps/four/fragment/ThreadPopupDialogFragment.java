package com.chanapps.four.fragment;

import android.app.*;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.chanapps.four.activity.R;
import com.chanapps.four.activity.ThreadActivity;
import com.chanapps.four.adapter.AbstractBoardCursorAdapter;
import com.chanapps.four.adapter.ThreadCursorAdapter;
import com.chanapps.four.adapter.ThreadSingleItemCursorAdapter;
import com.chanapps.four.component.ThemeSelector;
import com.chanapps.four.component.ThreadViewable;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.data.ChanThread;
import com.chanapps.four.loader.ChanImageLoader;
import com.chanapps.four.loader.ThreadCursorLoader;
import com.chanapps.four.viewer.ThreadListener;
import com.chanapps.four.viewer.ThreadViewer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import java.util.HashSet;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class ThreadPopupDialogFragment extends DialogFragment implements ThreadViewable
{
    public static final String TAG = ThreadPopupDialogFragment.class.getSimpleName();
    public static final boolean DEBUG = false;

    public static final String LAST_POSITION = "lastPosition";
    public static final String POPUP_TYPE = "popupType";

    static public enum PopupType {
        SELF,
        BACKLINKS,
        REPLIES,
        SAME_ID
    }

    protected String boardCode;
    protected long threadNo;
    protected long postNo;
    protected int pos;
    protected PopupType popupType;

    protected Cursor cursor;

    protected AbstractBoardCursorAdapter adapter;
    protected AbsListView absListView;
    protected View layout;
    protected Handler handler;
    protected ThreadListener threadListener;
    protected Fragment parent;
    protected String query;

    public ThreadPopupDialogFragment() {
        super();
        if (DEBUG) {Log.i(TAG, "ThreadPopupDialogFragment()");}
    }

    /*//public ThreadPopupDialogFragment(Fragment parent, String boardCode, long threadNo, long postNo, int pos, PopupType popupType, String query) {*/
    public ThreadPopupDialogFragment(Fragment parent, String boardCode, long threadNo, long postNo, PopupType popupType, String query) {
        super();
        this.parent = parent;
        this.boardCode = boardCode;
        this.threadNo = threadNo;
        this.postNo = postNo;
        this.pos = -1;
        this.popupType = popupType;
        this.query = query;
        if (DEBUG) {Log.i(TAG, "ThreadPopupDialogFragment() /" + boardCode + "/" + threadNo + "#p" + postNo + " pos=" + pos + " query=" + query);}
    }

    protected void inflateLayout() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.inflateLayout()",this);try{LayoutInflater inflater = getActivity().getLayoutInflater();
        if (popupType == PopupType.SELF)
            {layout = inflater.inflate(R.layout.thread_single_popup_dialog_fragment, null);}
        else
            {layout = inflater.inflate(R.layout.thread_popup_dialog_fragment, null);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.inflateLayout()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.inflateLayout()",this,throwable);throw throwable;}
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.widget.Dialog com.chanapps.four.fragment.ThreadPopupDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{if (savedInstanceState != null && savedInstanceState.containsKey(ChanBoard.BOARD_CODE)) {
            boardCode = savedInstanceState.getString(ChanBoard.BOARD_CODE);
            threadNo = savedInstanceState.getLong(ChanThread.THREAD_NO);
            postNo = savedInstanceState.getLong(ChanPost.POST_NO);
            /*//pos = savedInstanceState.getInt(LAST_POSITION);*/
            popupType = PopupType.valueOf(savedInstanceState.getString(POPUP_TYPE));
            query = savedInstanceState.getString(SearchManager.QUERY);
            if (DEBUG) {Log.i(TAG, "onCreateDialog() /" + boardCode + "/" + threadNo + " restored from bundle");}
        }
        else {
            if (DEBUG) {Log.i(TAG, "onCreateDialog() /" + boardCode + "/" + threadNo + " null bundle");}
        }
        if (popupType == null)
            {popupType = PopupType.SELF;}
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflateLayout();
        init();
        setStyle(STYLE_NO_TITLE, 0);
        if (DEBUG) {Log.i(TAG, "creating dialog");}
        Dialog dialog = builder
                .setView(layout)
                .create();
        dialog.setCanceledOnTouchOutside(true);
        {com.mijack.Xlog.logMethodExit("android.widget.Dialog com.chanapps.four.fragment.ThreadPopupDialogFragment.onCreateDialog(android.os.Bundle)",this);return dialog;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.Dialog com.chanapps.four.fragment.ThreadPopupDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

    protected String popupTitle() {
        com.mijack.Xlog.logMethodEnter("android.widget.String com.chanapps.four.fragment.ThreadPopupDialogFragment.popupTitle()",this);try{switch (popupType) {
            case BACKLINKS:
                {com.mijack.Xlog.logMethodExit("android.widget.String com.chanapps.four.fragment.ThreadPopupDialogFragment.popupTitle()",this);return getString(R.string.thread_backlinks);}
            case REPLIES:
                {com.mijack.Xlog.logMethodExit("android.widget.String com.chanapps.four.fragment.ThreadPopupDialogFragment.popupTitle()",this);return getString(R.string.thread_replies);}
            case SAME_ID:
                {com.mijack.Xlog.logMethodExit("android.widget.String com.chanapps.four.fragment.ThreadPopupDialogFragment.popupTitle()",this);return getString(R.string.thread_same_id);}
            default:
            case SELF:
                {com.mijack.Xlog.logMethodExit("android.widget.String com.chanapps.four.fragment.ThreadPopupDialogFragment.popupTitle()",this);return getString(R.string.thread_post);}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.String com.chanapps.four.fragment.ThreadPopupDialogFragment.popupTitle()",this,throwable);throw throwable;}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onSaveInstanceState(android.os.Bundle)",this,outState);try{if (DEBUG) {Log.i(TAG, "onSaveInstanceState /" + boardCode + "/" + threadNo);}
        outState.putString(ChanBoard.BOARD_CODE, boardCode);
        outState.putLong(ChanThread.THREAD_NO, threadNo);
        outState.putLong(ChanPost.POST_NO, postNo);
        /*//outState.putInt(LAST_POSITION, pos);*/
        outState.putString(POPUP_TYPE, popupType.toString());
        outState.putString(SearchManager.QUERY, query);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onActivityCreated(android.os.Bundle)",this,bundle);try{super.onActivityCreated(bundle);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onActivityCreated(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onActivityCreated(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onStart()",this);try{super.onStart();
        if (handler == null)
            {handler = new Handler();}
        loadAdapter();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onStart()",this,throwable);throw throwable;}
    }

    @Override
    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onResume()",this);try{super.onResume();
        if (handler == null)
            {handler = new Handler();}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onResume()",this,throwable);throw throwable;}
    }

    protected void loadAdapter() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadAdapter()",this);try{ThreadActivity activity = (ThreadActivity)getActivity();
        if (activity == null) {
            if (DEBUG) {Log.i(TAG, "loadAdapter /" + boardCode + "/" + threadNo + " null activity, exiting");}
            dismiss();
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadAdapter()",this);return;}
        }
        ThreadFragment fragment = activity.getCurrentFragment();
        if (fragment == null) {
            if (DEBUG) {Log.i(TAG, "loadAdapter /" + boardCode + "/" + threadNo + " null fragment, exiting");}
            dismiss();
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadAdapter()",this);return;}
        }
        if (DEBUG) {Log.i(TAG, "loadAdapter /" + boardCode + "/" + threadNo + " fragment=" + fragment + " query=" + query);}

        ResourceCursorAdapter fragmentAdapter;
        if (query == null || query.isEmpty()) { /*// load directly from fragment for empty queries*/
            if ((fragmentAdapter = fragment.getAdapter()) == null) {
                if (DEBUG) {Log.i(TAG, "loadAdapter /" + boardCode + "/" + threadNo + " null adapter, exiting");}
                dismiss();
            }
            else {
                if (DEBUG) {Log.i(TAG, "loadAdapter /" + boardCode + "/" + threadNo
                        + " loading empty query cursor async count=" + fragmentAdapter.getCount());}
                cursor = fragmentAdapter.getCursor();
                loadCursorAsync();
            }
        }
        else { /*// load from callback for non-empty queries*/
            loadCursorFromFragmentCallback(fragment);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadAdapter()",this,throwable);throw throwable;}
    }

    protected static final int CURSOR_LOADER_ID = 0x19; /*// arbitrary*/
    
    protected void loadCursorFromFragmentCallback(ThreadFragment fragment) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadCursorFromFragmentCallback(android.widget.ThreadFragment)",this,fragment);try{if (DEBUG) {Log.i(TAG, "loadAdapter /" + boardCode + "/" + threadNo + " doing cursor loader callback");}
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, loaderCallbacks);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadCursorFromFragmentCallback(android.widget.ThreadFragment)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadCursorFromFragmentCallback(android.widget.ThreadFragment)",this,throwable);throw throwable;}
    }

    protected LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            com.mijack.Xlog.logMethodEnter("android.support.v4.content.Loader com.chanapps.four.fragment.ThreadPopupDialogFragment$1.onCreateLoader(int,android.os.Bundle)",this,id,args);try{if (DEBUG) {Log.i(TAG, "onCreateLoader /" + boardCode + "/" + threadNo + " id=" + id);}
            {com.mijack.Xlog.logMethodExit("android.support.v4.content.Loader com.chanapps.four.fragment.ThreadPopupDialogFragment$1.onCreateLoader(int,android.os.Bundle)",this);return new ThreadCursorLoader(parent.getActivity(), boardCode, threadNo, "", false);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.support.v4.content.Loader com.chanapps.four.fragment.ThreadPopupDialogFragment$1.onCreateLoader(int,android.os.Bundle)",this,throwable);throw throwable;}
        }
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment$1.onLoadFinished(android.support.v4.content.Loader,android.database.Cursor)",this,loader,data);try{if (DEBUG) {Log.i(TAG, "onLoadFinished /" + boardCode + "/" + threadNo + " id=" + loader.getId()
                    + " count=" + (data == null ? 0 : data.getCount()) + " loader=" + loader);}
            int count = data == null ? 0 : data.getCount();
            Log.i(TAG, "loadAdapter /" + boardCode + "/" + threadNo + " callback returned " + count + " rows");
            cursor = data;
            loadCursorAsync();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment$1.onLoadFinished(android.support.v4.content.Loader,android.database.Cursor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment$1.onLoadFinished(android.support.v4.content.Loader,android.database.Cursor)",this,throwable);throw throwable;}
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment$1.onLoaderReset(android.support.v4.content.Loader)",this,loader);try{if (DEBUG) {Log.i(TAG, "onLoaderReset /" + boardCode + "/" + threadNo + " id=" + loader.getId());}
            /*//adapter.swapCursor(null);*/
            adapter.changeCursor(null);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment$1.onLoaderReset(android.support.v4.content.Loader)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment$1.onLoaderReset(android.support.v4.content.Loader)",this,throwable);throw throwable;}
        }
    };

    protected void loadCursorAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadCursorAsync()",this);try{if (cursor == null) {
            if (DEBUG) {Log.i(TAG, "loadAdapter /" + boardCode + "/" + threadNo + " null cursor, exiting");}
            dismiss();
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadCursorAsync()",this);return;}
        }
        if (cursor.getCount() == 0) {
            if (DEBUG) {Log.i(TAG, "loadAdapter /" + boardCode + "/" + threadNo + " empty cursor, exiting");}
            dismiss();
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadCursorAsync()",this);return;}
        }
        if (DEBUG) {Log.i(TAG, "loadAdapter /" + boardCode + "/" + threadNo + " fragment cursor size=" + cursor.getCount());}
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment$2.run()",this);try{/*//if (pos == 0 && postNo != threadNo && cursor.moveToFirst()) { // on multi-jump the original position is invalid, so re-scan position*/
                pos = -1;
                if (cursor.moveToFirst()) { /*// on multi-jump the original position is invalid, so re-scan position*/
                    while (!cursor.isAfterLast()) {
                        long id = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
                        if (id == postNo) {
                            pos = cursor.getPosition();
                            break;
                        }
                        cursor.moveToNext();
                    }
                }
                if (pos == -1) {
                    Log.e(TAG, "Couldn't find post position in cursor");
                    {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadCursorAsync()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment$2.run()",this);return;}}
                }
                final Cursor detailCursor = detailsCursor();
                if (DEBUG) {Log.i(TAG, "loadAdapter /" + boardCode + "/" + threadNo + " detail cursor size=" + detailCursor.getCount());}
                if (handler != null)
                    {handler.post(new Runnable() {
                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment$2$1.run()",this);try{adapter.swapCursor(detailCursor);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment$2$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment$2$1.run()",this,throwable);throw throwable;}
                        }
                    });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment$2.run()",this,throwable);throw throwable;}
            }
        }).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.loadCursorAsync()",this,throwable);throw throwable;}
    }

    @Override
    public void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onPause()",this);try{super.onPause();
        handler = null;com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onPause()",this,throwable);throw throwable;}
    }

    @Override
    public void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onStop()",this);try{super.onStop();
        handler = null;com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.onStop()",this,throwable);throw throwable;}
    }

    protected void createAdapter() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.createAdapter()",this);try{if (popupType == PopupType.SELF) {
            adapter = new ThreadSingleItemCursorAdapter(getActivity(), viewBinder, true, new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment$3.run()",this);try{dismiss();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment$3.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment$3.run()",this,throwable);throw throwable;}
                }
            });
        }
        else {
        adapter = new ThreadCursorAdapter(getActivity(), viewBinder, true, new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment$4.run()",this);try{dismiss();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment$4.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment$4.run()",this,throwable);throw throwable;}
            }
        });
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.createAdapter()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.createAdapter()",this,throwable);throw throwable;}
    }

    protected void init() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.init()",this);try{createAdapter();
        absListView = (ListView) layout.findViewById(R.id.thread_popup_list_view);
        absListView.setAdapter(adapter);
        absListView.setOnItemClickListener(itemListener);
        ImageLoader imageLoader = ChanImageLoader.getInstance(getActivity().getApplicationContext());
        absListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true));
        threadListener = new ThreadListener(this, ThemeSelector.instance(getActivity().getApplicationContext()).isDark());com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.init()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.init()",this,throwable);throw throwable;}
    }

    protected AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment$5.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this,parent,view,position,id);try{if (DEBUG) {Log.i(TAG, "onItemClick() pos=" + position + " postNo=" + id);}
            try {
                dismiss();
            }
            catch (IllegalStateException e) {
                Log.e(TAG, "Can't dismiss previous fragment", e);
            }
            Activity activity = getActivity();
            if (activity == null || !(activity instanceof ThreadActivity)) {
                if (DEBUG) {Log.i(TAG, "onItemClick() no activity");}
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment$5.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this);return;}
            }
            ThreadFragment fragment = ((ThreadActivity) activity).getCurrentFragment();
            if (fragment == null) {
                if (DEBUG) {Log.i(TAG, "onItemClick() no thread fragment");}
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment$5.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this);return;}
            }
            if (DEBUG) {Log.i(TAG, "onItemClick() scrolling to postNo=" + id);}
            fragment.scrollToPostAsync(id);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment$5.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this,throwable);throw throwable;}
        }
    };

    @Override
    public AbsListView getAbsListView() {
        com.mijack.Xlog.logMethodEnter("android.widget.AbsListView com.chanapps.four.fragment.ThreadPopupDialogFragment.getAbsListView()",this);try{com.mijack.Xlog.logMethodExit("android.widget.AbsListView com.chanapps.four.fragment.ThreadPopupDialogFragment.getAbsListView()",this);return absListView;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.AbsListView com.chanapps.four.fragment.ThreadPopupDialogFragment.getAbsListView()",this,throwable);throw throwable;}
    }

    @Override
    public ResourceCursorAdapter getAdapter() {
        com.mijack.Xlog.logMethodEnter("android.widget.ResourceCursorAdapter com.chanapps.four.fragment.ThreadPopupDialogFragment.getAdapter()",this);try{Activity activity = getActivity();
        if (activity == null || !(activity instanceof ThreadActivity)) {
            if (DEBUG) {Log.i(TAG, "getAdapter() no activity");}
            {com.mijack.Xlog.logMethodExit("android.widget.ResourceCursorAdapter com.chanapps.four.fragment.ThreadPopupDialogFragment.getAdapter()",this);return adapter;}
        }
        ThreadFragment fragment = ((ThreadActivity) activity).getCurrentFragment();
        if (fragment == null) {
            if (DEBUG) {Log.i(TAG, "getAdapter() no thread fragment");}
            {com.mijack.Xlog.logMethodExit("android.widget.ResourceCursorAdapter com.chanapps.four.fragment.ThreadPopupDialogFragment.getAdapter()",this);return adapter;}
        }
        ResourceCursorAdapter fragmentAdapter = fragment.getAdapter();
        if (fragmentAdapter == null) {
            if (DEBUG) {Log.i(TAG, "getAdapter() no thread fragment adapter");}
            {com.mijack.Xlog.logMethodExit("android.widget.ResourceCursorAdapter com.chanapps.four.fragment.ThreadPopupDialogFragment.getAdapter()",this);return adapter;}
        }
        if (query != null && !query.isEmpty()) {
            if (DEBUG) {Log.i(TAG, "getAdapter() has query so returing adpter");}
            {com.mijack.Xlog.logMethodExit("android.widget.ResourceCursorAdapter com.chanapps.four.fragment.ThreadPopupDialogFragment.getAdapter()",this);return adapter;}
        }
        if (DEBUG) {Log.i(TAG, "getAdapter() returning fragment adapter");}
        {com.mijack.Xlog.logMethodExit("android.widget.ResourceCursorAdapter com.chanapps.four.fragment.ThreadPopupDialogFragment.getAdapter()",this);return fragmentAdapter;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.ResourceCursorAdapter com.chanapps.four.fragment.ThreadPopupDialogFragment.getAdapter()",this,throwable);throw throwable;}
    }

    @Override
    public Handler getHandler() {
        com.mijack.Xlog.logMethodEnter("android.os.Handler com.chanapps.four.fragment.ThreadPopupDialogFragment.getHandler()",this);try{com.mijack.Xlog.logMethodExit("android.os.Handler com.chanapps.four.fragment.ThreadPopupDialogFragment.getHandler()",this);return handler;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Handler com.chanapps.four.fragment.ThreadPopupDialogFragment.getHandler()",this,throwable);throw throwable;}
    }

    protected Cursor detailsCursor() {
        com.mijack.Xlog.logMethodEnter("android.database.Cursor com.chanapps.four.fragment.ThreadPopupDialogFragment.detailsCursor()",this);try{MatrixCursor matrixCursor = ChanPost.buildMatrixCursor(0);
        if (pos == -1) {
            Log.e(TAG, "Error: invalid pos position pos=" + -1);
            {com.mijack.Xlog.logMethodExit("android.database.Cursor com.chanapps.four.fragment.ThreadPopupDialogFragment.detailsCursor()",this);return matrixCursor;}
        }
        switch (popupType) {
            case BACKLINKS:
                addBlobRows(matrixCursor, ChanPost.POST_BACKLINKS_BLOB);
                break;
            case REPLIES:
                addBlobRows(matrixCursor, ChanPost.POST_REPLIES_BLOB);
                break;
            case SAME_ID:
                addBlobRows(matrixCursor, ChanPost.POST_SAME_IDS_BLOB);
                break;
            case SELF:
                addSelfRow(matrixCursor);
                break;
        }
        {com.mijack.Xlog.logMethodExit("android.database.Cursor com.chanapps.four.fragment.ThreadPopupDialogFragment.detailsCursor()",this);return matrixCursor;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.database.Cursor com.chanapps.four.fragment.ThreadPopupDialogFragment.detailsCursor()",this,throwable);throw throwable;}
    }

    protected int addBlobRows(MatrixCursor matrixCursor, String columnName) {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.fragment.ThreadPopupDialogFragment.addBlobRows(android.database.MatrixCursor,android.widget.String)",this,matrixCursor,columnName);try{if (DEBUG) {Log.i(TAG, "addBlobRows() /" + boardCode + "/" + threadNo + " pos=" + pos + " popupType=" + popupType + " columnName=" + columnName);}
        if (!cursor.moveToPosition(pos)) {
            if (DEBUG) {Log.i(TAG, "addBlobRows() /" + boardCode + "/" + threadNo + " pos=" + pos + " could not move to position");}
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.fragment.ThreadPopupDialogFragment.addBlobRows(android.database.MatrixCursor,android.widget.String)",this);return 0;}
        }
        byte[] b = cursor.getBlob(cursor.getColumnIndex(columnName));
        if (b == null || b.length == 0) {
            if (DEBUG) {Log.i(TAG, "addBlobRows() /" + boardCode + "/" + threadNo + " pos=" + pos + " no blob found for columnName=" + columnName);}
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.fragment.ThreadPopupDialogFragment.addBlobRows(android.database.MatrixCursor,android.widget.String)",this);return 0;}
        }
        HashSet<?> links = ChanPost.parseBlob(b);
        if (links == null || links.size() <= 0) {
            if (DEBUG) {Log.i(TAG, "addBlobRows() /" + boardCode + "/" + threadNo + " pos=" + pos + " no links found in blob");}
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.fragment.ThreadPopupDialogFragment.addBlobRows(android.database.MatrixCursor,android.widget.String)",this);return 0;}
        }
        int count = links.size();
        if (DEBUG) {Log.i(TAG, "addBlobRows() /" + boardCode + "/" + threadNo + " pos=" + pos + " found links count=" + count);}
        if (!cursor.moveToFirst()) {
            if (DEBUG) {Log.i(TAG, "addBlobRows() /" + boardCode + "/" + threadNo + " pos=" + pos + " could not move to first");}
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.fragment.ThreadPopupDialogFragment.addBlobRows(android.database.MatrixCursor,android.widget.String)",this);return 0;}
        }
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
            if (DEBUG) {Log.d(TAG, "addBlobRows() /" + boardCode + "/" + threadNo + " pos=" + pos + " checking pos=" + cursor.getPosition() + " id=" + id);}
            if (links.contains(id)) {
                if (DEBUG) {Log.d(TAG, "addBlobRows() /" + boardCode + "/" + threadNo + " pos=" + pos + " found link at pos=" + cursor.getPosition());}
                Object[] row = ChanPost.extractPostRow(cursor);
                if (row != null)
                    {matrixCursor.addRow(row);}
            }
            if (!cursor.moveToNext())
                {break;}
        }
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.fragment.ThreadPopupDialogFragment.addBlobRows(android.database.MatrixCursor,android.widget.String)",this);return count;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.fragment.ThreadPopupDialogFragment.addBlobRows(android.database.MatrixCursor,android.widget.String)",this,throwable);throw throwable;}
    }

    protected void addSelfRow(MatrixCursor matrixCursor) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.addSelfRow(android.database.MatrixCursor)",this,matrixCursor);try{if (!cursor.moveToPosition(pos)) {
            if (DEBUG) {Log.i(TAG, "addSelfRow() /" + boardCode + "/" + threadNo + " could not move to pos=" + pos);}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.addSelfRow(android.database.MatrixCursor)",this);return;}
        }
        Object[] row = ChanPost.extractPostRow(cursor);
        if (row == null) {
            if (DEBUG) {Log.i(TAG, "addSelfRow() /" + boardCode + "/" + threadNo + " null row from pos=" + pos);}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.addSelfRow(android.database.MatrixCursor)",this);return;}
        }
        if (DEBUG) {Log.i(TAG, "addSelfRow() /" + boardCode + "/" + threadNo + " loaded row pos=" + pos);}
        matrixCursor.addRow(row);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.addSelfRow(android.database.MatrixCursor)",this,throwable);throw throwable;}
    }

    protected AbstractBoardCursorAdapter.ViewBinder viewBinder = new AbstractBoardCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.ThreadPopupDialogFragment$6.setViewValue(android.view.View,android.database.Cursor,int)",this,view,cursor,columnIndex);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.ThreadPopupDialogFragment$6.setViewValue(android.view.View,android.database.Cursor,int)",this);return ThreadViewer.setViewValue(view, cursor, boardCode,
                    false,
                    0,
                    0,
                    null, /*//threadListener.thumbOnClickListener,*/
                    threadListener.backlinkOnClickListener,
                    null,
                    null,
                    threadListener.repliesOnClickListener,
                    null, /*//threadListener.sameIdOnClickListener,*/
                    null, /*//threadListener.exifOnClickListener,*/
                    null,
                    threadListener.expandedImageListener,
                    null,
                    null
            );}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.ThreadPopupDialogFragment$6.setViewValue(android.view.View,android.database.Cursor,int)",this,throwable);throw throwable;}
        }
    };

    @Override
    public void showDialog(String boardCode, long threadNo, long postNo, int pos,
                           ThreadPopupDialogFragment.PopupType popupType) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ThreadPopupDialogFragment.showDialog(android.widget.String,long,long,int,android.widget.ThreadPopupDialogFragment.PopupType)",this,boardCode,threadNo,postNo,pos,popupType);try{Activity activity = getActivity();
        if (activity == null || !(activity instanceof ThreadActivity)) {
            if (DEBUG) {Log.i(TAG, "onItemClick() no activity");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.showDialog(android.widget.String,long,long,int,android.widget.ThreadPopupDialogFragment.PopupType)",this);return;}
        }
        ThreadFragment fragment = ((ThreadActivity) activity).getCurrentFragment();
        if (fragment == null) {
            if (DEBUG) {Log.i(TAG, "onItemClick() no thread fragment");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ThreadPopupDialogFragment.showDialog(android.widget.String,long,long,int,android.widget.ThreadPopupDialogFragment.PopupType)",this);return;}
        }
        if (DEBUG) {Log.i(TAG, "onItemClick() scrolling to postNo=" + postNo);}
        dismiss();
        /*//(new ThreadPopupDialogFragment(fragment, boardCode, threadNo, postNo, pos, popupType, query))*/
        (new ThreadPopupDialogFragment(fragment, boardCode, threadNo, postNo, popupType, query))
                .show(getFragmentManager(), ThreadPopupDialogFragment.TAG);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ThreadPopupDialogFragment.showDialog(android.widget.String,long,long,int,android.widget.ThreadPopupDialogFragment.PopupType)",this,throwable);throw throwable;}
    }

}
