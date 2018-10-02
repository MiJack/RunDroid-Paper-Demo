package com.chanapps.four.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.chanapps.four.activity.R;
import com.chanapps.four.data.ChanThread;
import com.chanapps.four.viewer.BoardViewHolder;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 2/4/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class BoardNarrowCursorAdapter extends AbstractBoardCursorAdapter {

    protected static final int TYPE_GRID_HEADER = 0;
    protected static final int TYPE_GRID_ITEM = 1;
    protected static final int TYPE_MAX_COUNT = 2;

    public BoardNarrowCursorAdapter(Context context, ViewBinder viewBinder) {
        super(context, viewBinder);
    }

    @Override
    public int getItemViewType(int position) {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.adapter.BoardNarrowCursorAdapter.getItemViewType(int)",this,position);try{Cursor c = getCursor();
        if (c != null
                && c.moveToPosition(position)
                && (c.getInt(c.getColumnIndex(ChanThread.THREAD_FLAGS)) & ChanThread.THREAD_FLAG_HEADER) > 0)
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.adapter.BoardNarrowCursorAdapter.getItemViewType(int)",this);return TYPE_GRID_HEADER;}}
        else
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.adapter.BoardNarrowCursorAdapter.getItemViewType(int)",this);return TYPE_GRID_ITEM;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.adapter.BoardNarrowCursorAdapter.getItemViewType(int)",this,throwable);throw throwable;}
    }

    @Override
    protected View newView(ViewGroup parent, int tag, int position) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.chanapps.four.adapter.BoardNarrowCursorAdapter.newView(android.view.ViewGroup,int,int)",this,parent,tag,position);try{if (DEBUG) {Log.d(TAG, "Creating " + tag + " layout for " + position);}
        int layoutId = getItemViewType(position) == TYPE_GRID_HEADER
                ? R.layout.board_grid_header_narrow
                : R.layout.board_grid_item_narrow;
        View v = mInflater.inflate(layoutId, parent, false);
        BoardViewHolder viewHolder = new BoardViewHolder(v);
        v.setTag(R.id.VIEW_TAG_TYPE, tag);
        v.setTag(R.id.VIEW_HOLDER, viewHolder);
        {com.mijack.Xlog.logMethodExit("android.view.View com.chanapps.four.adapter.BoardNarrowCursorAdapter.newView(android.view.ViewGroup,int,int)",this);return v;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.chanapps.four.adapter.BoardNarrowCursorAdapter.newView(android.view.ViewGroup,int,int)",this,throwable);throw throwable;}
    }

    @Override
    public int getViewTypeCount() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.adapter.BoardNarrowCursorAdapter.getViewTypeCount()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.adapter.BoardNarrowCursorAdapter.getViewTypeCount()",this);return TYPE_MAX_COUNT;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.adapter.BoardNarrowCursorAdapter.getViewTypeCount()",this,throwable);throw throwable;}
    }
}
