package com.chanapps.four.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.chanapps.four.activity.R;
import com.chanapps.four.viewer.BoardViewHolder;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 2/4/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class BoardSmallCursorAdapter extends AbstractBoardCursorAdapter {

    protected static final int TYPE_GRID_ITEM = 0;
    protected static final int TYPE_MAX_COUNT = 1;

    public BoardSmallCursorAdapter(Context context, ViewBinder viewBinder) {
        super(context, viewBinder);
    }

    @Override
    public int getItemViewType(int position) {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.adapter.BoardSmallCursorAdapter.getItemViewType(int)",this,position);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.adapter.BoardSmallCursorAdapter.getItemViewType(int)",this);return TYPE_GRID_ITEM;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.adapter.BoardSmallCursorAdapter.getItemViewType(int)",this,throwable);throw throwable;}
    }

    @Override
    protected View newView(ViewGroup parent, int tag, int position) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.chanapps.four.adapter.BoardSmallCursorAdapter.newView(android.view.ViewGroup,int,int)",this,parent,tag,position);try{if (DEBUG) {Log.d(TAG, "Creating " + tag + " layout for " + position);}
        View v = mInflater.inflate(R.layout.board_grid_item_small, parent, false);
        BoardViewHolder viewHolder = new BoardViewHolder(v);
        v.setTag(R.id.VIEW_TAG_TYPE, tag);
        v.setTag(R.id.VIEW_HOLDER, viewHolder);
        {com.mijack.Xlog.logMethodExit("android.view.View com.chanapps.four.adapter.BoardSmallCursorAdapter.newView(android.view.ViewGroup,int,int)",this);return v;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.chanapps.four.adapter.BoardSmallCursorAdapter.newView(android.view.ViewGroup,int,int)",this,throwable);throw throwable;}
    }

    @Override
    public int getViewTypeCount() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.adapter.BoardSmallCursorAdapter.getViewTypeCount()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.adapter.BoardSmallCursorAdapter.getViewTypeCount()",this);return TYPE_MAX_COUNT;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.adapter.BoardSmallCursorAdapter.getViewTypeCount()",this,throwable);throw throwable;}
    }
}
