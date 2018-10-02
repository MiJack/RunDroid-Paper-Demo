/**
 * 
 */
package com.chanapps.four.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import com.chanapps.four.activity.R;
import com.chanapps.four.data.*;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 * Copied from SDK.
 *
 * An easy adapter to map columns from a cursor to TextViews or ImageViews
 * defined in an XML file. You can specify which columns you want, which
 * views you want to display the columns, and the XML file that defines
 * the appearance of these views.
 *
 */
abstract public class AbstractBoardCursorAdapter extends ResourceCursorAdapter {
	protected static final String TAG = AbstractBoardCursorAdapter.class.getSimpleName();
	protected static final boolean DEBUG = false;
    protected static final String ID_COL = "_id";

    /**
     * A list of columns containing the data to bind to the UI.
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    /*//protected int[] mFrom;*/
    /**
     * A list of View ids representing the views to which the data must be bound.
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    /*//protected int[] mTo;*/

    protected ViewBinder mViewBinder;
    protected Context context;
    protected LayoutInflater mInflater;
    protected String groupBoardCode;

    /*//protected String[] mOriginalFrom;*/

    public AbstractBoardCursorAdapter(Context context, ViewBinder viewBinder) {
        super(context, 0, null, 0);
        this.context = context;
        mViewBinder = viewBinder;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setGroupBoardCode(String board) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.setGroupBoardCode(com.chanapps.four.data.String)",this,board);try{groupBoardCode = board;com.mijack.Xlog.logMethodExit("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.setGroupBoardCode(com.chanapps.four.data.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.setGroupBoardCode(com.chanapps.four.data.String)",this,throwable);throw throwable;}
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.bindView(android.view.View,android.content.Context,android.database.Cursor)",this,view,context,cursor);try{final int pos = cursor == null ? -1 : cursor.getPosition();
        if (DEBUG) {Log.i(TAG, "bindView() for pos=" + pos);}
        final ViewBinder binder = mViewBinder;
        Object tag = view.getTag();
        if (tag != null && tag instanceof Long) {
            long viewPostId = (Long)tag;
            long cursorPostId = cursor.getLong(cursor.getColumnIndex(ID_COL));
            if (viewPostId == cursorPostId) { /*// prevent flickering caused by redrawing when not needed*/
                if (DEBUG) {Log.i(TAG, "view already set, updating pos=" + cursor.getPosition());}
                updateView(view, cursor, pos);
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.bindView(android.view.View,android.content.Context,android.database.Cursor)",this);return;}
            }
        }
        if (isBlocked(cursor)) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params != null)
                {params.height = 0;}
            view.setVisibility(View.GONE);
        }
        if (binder != null)
            {binder.setViewValue(view, cursor, 0);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.bindView(android.view.View,android.content.Context,android.database.Cursor)",this,throwable);throw throwable;}
    }

    protected boolean isHeader(Cursor c) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.adapter.AbstractBoardCursorAdapter.isHeader(android.database.Cursor)",this,c);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.adapter.AbstractBoardCursorAdapter.isHeader(android.database.Cursor)",this);return (c.getInt(c.getColumnIndex(ChanThread.THREAD_FLAGS)) & ChanThread.THREAD_FLAG_HEADER) > 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.adapter.AbstractBoardCursorAdapter.isHeader(android.database.Cursor)",this,throwable);throw throwable;}
    }

    protected boolean isBlocked(Cursor c) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.adapter.AbstractBoardCursorAdapter.isBlocked(android.database.Cursor)",this,c);try{String board = c.getString(c.getColumnIndex(ChanThread.THREAD_BOARD_CODE));
        long no = c.getLong(c.getColumnIndex(ChanThread.THREAD_NO));
        final String uniqueId = ChanThread.uniqueId(board, no, 0);
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.adapter.AbstractBoardCursorAdapter.isBlocked(android.database.Cursor)",this);return ChanBlocklist.contains(context, ChanBlocklist.BlockType.THREAD, uniqueId);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.adapter.AbstractBoardCursorAdapter.isBlocked(android.database.Cursor)",this,throwable);throw throwable;}
    }

    protected boolean isOffWatchlist(Cursor c) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.adapter.AbstractBoardCursorAdapter.isOffWatchlist(android.database.Cursor)",this,c);try{if (!ChanBoard.WATCHLIST_BOARD_CODE.equals(groupBoardCode))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.adapter.AbstractBoardCursorAdapter.isOffWatchlist(android.database.Cursor)",this);return false;}}
        String board = c.getString(c.getColumnIndex(ChanThread.THREAD_BOARD_CODE));
        long no = c.getLong(c.getColumnIndex(ChanThread.THREAD_NO));
        final ChanThread thread = ChanFileStorage.loadThreadData(context, board, no);
        boolean isWatched = ChanFileStorage.isThreadWatched(context, thread);
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.adapter.AbstractBoardCursorAdapter.isOffWatchlist(android.database.Cursor)",this);return !isWatched;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.adapter.AbstractBoardCursorAdapter.isOffWatchlist(android.database.Cursor)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.updateView(android.view.View,android.database.Cursor,int)",this,view,cursor,pos);com.mijack.Xlog.logMethodExit("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.updateView(android.view.View,android.database.Cursor,int)",this);}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.chanapps.four.adapter.AbstractBoardCursorAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,position,convertView,parent);try{if (DEBUG) {Log.i(TAG, "Getting view for pos=" + position);}
        int tag = getItemViewType(position);
        if (convertView == null || (Integer)convertView.getTag(R.id.VIEW_TAG_TYPE) != tag) {
            convertView = newView(parent, tag, position);
            if (DEBUG) {Log.i(TAG, "Created new view=" + convertView);}
        }
        else {
            if (DEBUG) {Log.i(TAG, "Reusing existing view=" + convertView);}
        }
        if (DEBUG) {Log.i(TAG, "Binding pos=" + position + " to view=" + convertView);}
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        bindView(convertView, context, getCursor());
        {com.mijack.Xlog.logMethodExit("android.view.View com.chanapps.four.adapter.AbstractBoardCursorAdapter.getView(int,android.view.View,android.view.ViewGroup)",this);return convertView;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.chanapps.four.adapter.AbstractBoardCursorAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,throwable);throw throwable;}
    }

    abstract protected View newView(ViewGroup parent, int tag, int position);

    /**
     * Create a map from an array of strings to an array of column-id integers in mCursor.
     * If mCursor is null, the array will be discarded.
     * 
     * @param from the Strings naming the columns of interest
     */
    private void findColumns(Cursor c, String[] from) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.findColumns(android.database.Cursor,[com.chanapps.four.data.String)",this,c,from);try{/*//if (c != null) {*/
            /*//int i;*/
            /*//int count = from.length;*/
            /*//if (mFrom == null || mFrom.length != count) {*/
            /*//    mFrom = new int[count];*/
            /*//}*/
            /*//for (i = 0; i < count; i++) {*/
            /*//    mFrom[i] = c.getColumnIndexOrThrow(from[i]);*/
            /*//}*/
        /*//} else {*/
        /*//    mFrom = null;*/
        /*//}*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.findColumns(android.database.Cursor,[com.chanapps.four.data.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.findColumns(android.database.Cursor,[com.chanapps.four.data.String)",this,throwable);throw throwable;}
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        com.mijack.Xlog.logMethodEnter("android.database.Cursor com.chanapps.four.adapter.AbstractBoardCursorAdapter.swapCursor(android.database.Cursor)",this,c);try{/*// super.swapCursor() will notify observers before we have*/
        /*// a valid mapping, make sure we have a mapping before this*/
        /*// happens*/
        /*//if (mFrom == null) {*/
        /*//    findColumns(getCursor(), mOriginalFrom);*/
        /*//}*/
        Cursor res = super.swapCursor(c);
        /*// rescan columns in case cursor layout is different*/
        /*//findColumns(c, mOriginalFrom);*/
        {com.mijack.Xlog.logMethodExit("android.database.Cursor com.chanapps.four.adapter.AbstractBoardCursorAdapter.swapCursor(android.database.Cursor)",this);return res;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.database.Cursor com.chanapps.four.adapter.AbstractBoardCursorAdapter.swapCursor(android.database.Cursor)",this,throwable);throw throwable;}
    }

    @Override
    public void changeCursor(Cursor c) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.changeCursor(android.database.Cursor)",this,c);try{/*// super.swapCursor() will notify observers before we have*/
        /*// a valid mapping, make sure we have a mapping before this*/
        /*// happens*/
        /*//if (mFrom == null) {*/
        /*//    findColumns(getCursor(), mOriginalFrom);*/
        /*//}*/
        super.changeCursor(c);
        /*// rescan columns in case cursor layout is different*/
        /*//findColumns(c, mOriginalFrom);*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.changeCursor(android.database.Cursor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.changeCursor(android.database.Cursor)",this,throwable);throw throwable;}
    }

    /**
     * Change the cursor and change the column-to-view mappings at the same time.
     *  
     * @param c The database cursor.  Can be null if the cursor is not available yet.
     * @param from A list of column names representing the data to bind to the UI.  Can be null 
     *            if the cursor is not available yet.
     * @param to The views that should display column in the "from" parameter.
     *            These should all be TextViews. The first N views in this list
     *            are given the values of the first N columns in the from
     *            parameter.  Can be null if the cursor is not available yet.
     */
    public void changeCursorAndColumns(Cursor c, String[] from, int[] to) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.changeCursorAndColumns(android.database.Cursor,[com.chanapps.four.data.String,[int)",this,c,from,to);try{/*//mOriginalFrom = from;*/
        /*//mTo = to;*/
        /*// super.changeCursor() will notify observers before we have*/
        /*// a valid mapping, make sure we have a mapping before this*/
        /*// happens*/
        /*//if (mFrom == null) {*/
        /*//    findColumns(getCursor(), mOriginalFrom);*/
        /*//}*/
        super.changeCursor(c);
        /*//findColumns(c, mOriginalFrom);*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.changeCursorAndColumns(android.database.Cursor,[com.chanapps.four.data.String,[int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.adapter.AbstractBoardCursorAdapter.changeCursorAndColumns(android.database.Cursor,[com.chanapps.four.data.String,[int)",this,throwable);throw throwable;}
    }

    public static interface ViewBinder {
        /**
         * Binds the Cursor column defined by the specified index to the specified view.
         *
         * When binding is handled by this ViewBinder, this method must return true.
         * If this method returns false, SimpleCursorAdapter will attempts to handle
         * the binding on its own.
         *
         * @param view the view to bind the data to
         * @param cursor the cursor to get the data from
         * @param columnIndex the column at which the data can be found in the cursor
         *
         * @return true if the data was bound to the view, false otherwise
         */
        boolean setViewValue(View view, Cursor cursor, int columnIndex);
    }

}

