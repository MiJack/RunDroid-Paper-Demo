package com.chanapps.four.adapter;

import android.content.Context;
import android.database.Cursor;
import com.chanapps.four.data.ChanThread;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 12/21/12
 * Time: 12:14 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractThreadCursorAdapter extends AbstractBoardCursorAdapter {

    protected static final String TAG = AbstractThreadCursorAdapter.class.getSimpleName();
    protected static final boolean DEBUG = false;

    public AbstractThreadCursorAdapter(Context context, ViewBinder viewBinder) {
        super(context, viewBinder);
    }

    @Override
    protected boolean isHeader(Cursor c) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.adapter.AbstractThreadCursorAdapter.isHeader(android.database.Cursor)",this,c);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.adapter.AbstractThreadCursorAdapter.isHeader(android.database.Cursor)",this);return (c.getInt(c.getColumnIndex(ChanThread.POST_FLAGS)) & ChanThread.FLAG_IS_HEADER) > 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.adapter.AbstractThreadCursorAdapter.isHeader(android.database.Cursor)",this,throwable);throw throwable;}
    }

    @Override
    protected boolean isBlocked(Cursor c) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.adapter.AbstractThreadCursorAdapter.isBlocked(android.database.Cursor)",this,c);try{{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.adapter.AbstractThreadCursorAdapter.isBlocked(android.database.Cursor)",this);return false;} /*// post blocking not supported*/
        /*
        String board = c.getString(c.getColumnIndex(ChanThread.POST_BOARD_CODE));
        long no = c.getLong(c.getColumnIndex(ChanThread.POST_NO));
        long resto = c.getLong(c.getColumnIndex(ChanThread.POST_RESTO));
        final String uniqueId = ChanThread.uniqueId(board, no, resto);
        return ChanBlocklist.contains(context, ChanBlocklist.BlockType.THREAD, uniqueId);
        */}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.adapter.AbstractThreadCursorAdapter.isBlocked(android.database.Cursor)",this,throwable);throw throwable;}
    }

    @Override
    protected boolean isOffWatchlist(Cursor c) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.adapter.AbstractThreadCursorAdapter.isOffWatchlist(android.database.Cursor)",this,c);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.adapter.AbstractThreadCursorAdapter.isOffWatchlist(android.database.Cursor)",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.adapter.AbstractThreadCursorAdapter.isOffWatchlist(android.database.Cursor)",this,throwable);throw throwable;}
    }

}