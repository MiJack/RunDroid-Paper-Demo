package com.chanapps.four.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import com.chanapps.four.data.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*//import android.content.AsyncTaskLoader;*/

public class PopularCursorLoader extends BoardCursorLoader {

    protected static final String TAG = PopularCursorLoader.class.getSimpleName();
    protected static final boolean DEBUG = false;

    public PopularCursorLoader(Context context) {
        super(context);
        this.context = context;
        this.boardName = "";
    }

    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
    	com.mijack.Xlog.logMethodEnter("android.database.Cursor com.chanapps.four.loader.PopularCursorLoader.loadInBackground()",this);try{if (DEBUG) {Log.i(TAG, "loadInBackground");}
        MatrixCursor matrixCursor = ChanThread.buildMatrixCursor(10);
        loadBoard(matrixCursor, ChanBoard.POPULAR_BOARD_CODE);
        loadBoard(matrixCursor, ChanBoard.LATEST_BOARD_CODE);
        loadBoard(matrixCursor, ChanBoard.LATEST_IMAGES_BOARD_CODE);
        addRecommendedBoardLink(matrixCursor);
        registerContentObserver(matrixCursor, mObserver);
        {com.mijack.Xlog.logMethodExit("android.database.Cursor com.chanapps.four.loader.PopularCursorLoader.loadInBackground()",this);return matrixCursor;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.database.Cursor com.chanapps.four.loader.PopularCursorLoader.loadInBackground()",this,throwable);throw throwable;}
    }

    protected void loadBoard(MatrixCursor matrixCursor, String boardCode) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.loader.PopularCursorLoader.loadBoard(android.database.MatrixCursor,com.chanapps.four.data.String)",this,matrixCursor,boardCode);try{ChanBoard board = ChanFileStorage.loadBoardData(getContext(), boardCode);
        if (DEBUG) {Log.i(TAG,
                "board threadcount=" + (board.threads != null ? board.threads.length : 0)
                        + "board loadedthreadcount=" + (board.loadedThreads != null ? board.loadedThreads.length : 0));}

        if (board == null || board.threads == null || board.threads.length == 0 || board.defData)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.loader.PopularCursorLoader.loadBoard(android.database.MatrixCursor,com.chanapps.four.data.String)",this);return;}}

        if (DEBUG) {Log.i(TAG, "Loading " + board.threads.length + " threads");}
        int i = 0;
        for (ChanThread thread : board.threads) {
            if (DEBUG) {Log.i(TAG, "Loading thread:" + thread.no);}
            if (ChanBlocklist.contains(context, ChanBlocklist.BlockType.TRIPCODE, thread.trip)
                    || ChanBlocklist.contains(context, ChanBlocklist.BlockType.NAME, thread.name)
                    || ChanBlocklist.contains(context, ChanBlocklist.BlockType.EMAIL, thread.email)
                    || ChanBlocklist.contains(context, ChanBlocklist.BlockType.ID, thread.id))
            {
                if (DEBUG) {Log.i(TAG, "Skipped thread: " + thread.no);}
                continue;
            }
            Object[] row = ChanThread.makeRow(context, thread, "", threadFlag(boardCode), false, false);
            matrixCursor.addRow(row);
            i++;
            if (DEBUG) {Log.v(TAG, "Added board row: " + Arrays.toString(row));}
        }
        if (DEBUG) {Log.i(TAG, "Loaded " + i + " threads");}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.loader.PopularCursorLoader.loadBoard(android.database.MatrixCursor,com.chanapps.four.data.String)",this,throwable);throw throwable;}
    }

    protected int threadFlag(String boardCode) {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.loader.PopularCursorLoader.threadFlag(com.chanapps.four.data.String)",this,boardCode);try{if (ChanBoard.POPULAR_BOARD_CODE.equals(boardCode))
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.loader.PopularCursorLoader.threadFlag(com.chanapps.four.data.String)",this);return ChanThread.THREAD_FLAG_POPULAR_THREAD;}}
        else if (ChanBoard.LATEST_BOARD_CODE.equals(boardCode))
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.loader.PopularCursorLoader.threadFlag(com.chanapps.four.data.String)",this);return ChanThread.THREAD_FLAG_LATEST_POST;}}
        else if (ChanBoard.LATEST_IMAGES_BOARD_CODE.equals(boardCode))
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.loader.PopularCursorLoader.threadFlag(com.chanapps.four.data.String)",this);return ChanThread.THREAD_FLAG_RECENT_IMAGE;}}
        else
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.loader.PopularCursorLoader.threadFlag(com.chanapps.four.data.String)",this);return 0;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.loader.PopularCursorLoader.threadFlag(com.chanapps.four.data.String)",this,throwable);throw throwable;}
    }

    protected void addRecommendedBoardLink(MatrixCursor matrixCursor) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.loader.PopularCursorLoader.addRecommendedBoardLink(android.database.MatrixCursor)",this,matrixCursor);try{String[] boardCodes = { "a", "v", "vg", "fit", "mu", "sp", "co", "g", "tv" }; /*// b s gif*/
        List<String> boardCodeList = new ArrayList<String>(Arrays.asList(boardCodes));
        Collections.shuffle(boardCodeList);
        for (String boardCode : boardCodeList) {
            ChanBoard board = ChanBoard.getBoardByCode(getContext(), boardCode);
            matrixCursor.addRow(board.makeRow(context, 0));
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.loader.PopularCursorLoader.addRecommendedBoardLink(android.database.MatrixCursor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.loader.PopularCursorLoader.addRecommendedBoardLink(android.database.MatrixCursor)",this,throwable);throw throwable;}
    }

}