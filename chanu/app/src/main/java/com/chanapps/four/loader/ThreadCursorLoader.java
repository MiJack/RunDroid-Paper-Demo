package com.chanapps.four.loader;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.chanapps.four.data.*;

public class ThreadCursorLoader extends BoardCursorLoader {

    private static final String TAG = ThreadCursorLoader.class.getSimpleName();
    private static final boolean DEBUG = false;

    protected SharedPreferences prefs;
    protected long threadNo;
    protected boolean showRelatedBoards;
    private boolean useFriendlyIds;

    protected ThreadCursorLoader(Context context) {
        super(context);
    }

    public ThreadCursorLoader(Context context, String boardName, long threadNo, String query, boolean showRelatedBoards) {
        this(context);
        this.context = context;
        this.boardName = boardName;
        this.threadNo = threadNo;
        this.query = query == null ? "" : query.toLowerCase().trim();
        this.showRelatedBoards = false; /*// not so nice design*/
        if (threadNo <= 0)
            {throw new ExceptionInInitializerError("Can't have zero threadNo in a thread cursor loader");}
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
        com.mijack.Xlog.logMethodEnter("android.database.Cursor com.chanapps.four.loader.ThreadCursorLoader.loadInBackground()",this);try{try {
            /*//useFriendlyIds = prefs.getBoolean(SettingsActivity.PREF_USE_FRIENDLY_IDS, true);*/
            useFriendlyIds = false;
            ChanBoard board = ChanFileStorage.loadBoardData(getContext(), boardName);
            if (DEBUG) {Log.i(TAG, "Loaded board from storage " + board);}
            ChanThread thread;
            try {
                thread = ChanFileStorage.loadThreadData(getContext(), boardName, threadNo);
                if (DEBUG) {Log.i(TAG, "Loaded thread from storage " + thread);}
            } catch (Exception e) {
                Log.e(TAG, "Couldn't load thread from storage " + boardName + "/" + threadNo, e);
                thread = null;
            }
            int isDead = thread != null && thread.isDead ? 1 : 0;
            if (DEBUG) {Log.i(TAG, "loadInBackground " + thread.board + "/" + thread.no + " num posts " + (thread.posts != null ? thread.posts.length : 0));}
            if (DEBUG) {Log.i(TAG, "Thread dead status for " + boardName + "/" + threadNo + " is " + isDead);}
            if (DEBUG) {Log.i(TAG, "Thread closed status for " + boardName + "/" + threadNo + " is closed=" + thread.closed);}

            int capacity = thread != null && thread.posts != null && (query == null || query.isEmpty()) ? thread.posts.length : 0;
            MatrixCursor matrixCursor = ChanPost.buildMatrixCursor(capacity);

            if (board != null && thread != null && thread.posts != null && thread.posts.length > 0) { /*// show loading for no thread data*/
                loadMatrixCursor(matrixCursor, board, thread);
                if (DEBUG) {Log.i(TAG, "Remaining to load:" + (thread.posts[0].replies - thread.posts.length));}
            }
            registerContentObserver(matrixCursor, mObserver);
            {com.mijack.Xlog.logMethodExit("android.database.Cursor com.chanapps.four.loader.ThreadCursorLoader.loadInBackground()",this);return matrixCursor;}
    	} catch (Exception e) {
    		Log.e(TAG, "loadInBackground", e);
    		{com.mijack.Xlog.logMethodExit("android.database.Cursor com.chanapps.four.loader.ThreadCursorLoader.loadInBackground()",this);return null;}
    	}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.database.Cursor com.chanapps.four.loader.ThreadCursorLoader.loadInBackground()",this,throwable);throw throwable;}
    }

    private void loadMatrixCursor(MatrixCursor matrixCursor, ChanBoard board, ChanThread thread) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.loader.ThreadCursorLoader.loadMatrixCursor(android.database.MatrixCursor,com.chanapps.four.data.ChanBoard,com.chanapps.four.data.ChanThread)",this,matrixCursor,board,thread);try{if (DEBUG) {Log.i(TAG, "Thread toplevel thumb=" + thread.tn_w + "x" + thread.tn_h + " full=" + thread.w + "x" + thread.h);}
        if (DEBUG) {Log.i(TAG, "Thread postlevel thumb=" + thread.posts[0].tn_w + "x" + thread.posts[0].tn_h + " full=" + thread.posts[0].w + "x" + thread.posts[0].h);}

        /*// first get the maps for thread references*/
        Map<Long, HashSet<Long>> backlinksMap = thread.backlinksMap();
        Map<Long, HashSet<Long>> repliesMap = thread.repliesMap(backlinksMap);
        Map<String, HashSet<Long>> sameIdsMap = thread.sameIdsMap();

        int i = 0;
        int numQueryMatches = 0;
        for (ChanPost post : thread.posts) {
            if (ChanBlocklist.isBlocked(context, post))
                {continue;}
            if (!post.matchesQuery(query))
                {continue;}
            if (!query.isEmpty())
                {numQueryMatches++;}
            post.isDead = thread.isDead; /*// inherit from parent*/
            post.closed = thread.closed; /*// inherit*/
            post.hidePostNumbers = false; /*// always show*/
            post.useFriendlyIds = useFriendlyIds;
            byte[] backlinksBlob = ChanPost.blobify(backlinksMap.get(post.no));
            byte[] repliesBlob = ChanPost.blobify(repliesMap.get(post.no));
            HashSet<Long> sameIds = sameIdsMap.get(post.id);
            byte[] sameIdsBlob = (sameIds != null && sameIds.size() > 1) ? ChanPost.blobify(sameIds) : null;
            matrixCursor.addRow(post.makeRow(context, query, i, backlinksBlob, repliesBlob, sameIdsBlob));
            i++;
        }

        if (thread.defData)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.loader.ThreadCursorLoader.loadMatrixCursor(android.database.MatrixCursor,com.chanapps.four.data.ChanBoard,com.chanapps.four.data.ChanThread)",this);return;}}

        if (!thread.isDead && (thread.posts != null && thread.replies > thread.posts.length))
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.loader.ThreadCursorLoader.loadMatrixCursor(android.database.MatrixCursor,com.chanapps.four.data.ChanBoard,com.chanapps.four.data.ChanThread)",this);return;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.loader.ThreadCursorLoader.loadMatrixCursor(android.database.MatrixCursor,com.chanapps.four.data.ChanBoard,com.chanapps.four.data.ChanThread)",this,throwable);throw throwable;}

    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.loader.ThreadCursorLoader.dump(com.chanapps.four.data.String,java.io.FileDescriptor,java.io.PrintWriter,[com.chanapps.four.data.String)",this,prefix,fd,writer,args);try{super.dump(prefix, fd, writer, args);
        writer.print(prefix); writer.print("boardName="); writer.println(boardName);
        writer.print(prefix); writer.print("threadNo="); writer.println(threadNo);
        writer.print(prefix); writer.print("mCursor="); writer.println(mCursor);com.mijack.Xlog.logMethodExit("void com.chanapps.four.loader.ThreadCursorLoader.dump(com.chanapps.four.data.String,java.io.FileDescriptor,java.io.PrintWriter,[com.chanapps.four.data.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.loader.ThreadCursorLoader.dump(com.chanapps.four.data.String,java.io.FileDescriptor,java.io.PrintWriter,[com.chanapps.four.data.String)",this,throwable);throw throwable;}
    }

}
