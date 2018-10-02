package com.chanapps.four.viewer;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import com.chanapps.four.activity.GalleryViewActivity;
import com.chanapps.four.activity.R;
import com.chanapps.four.activity.ThreadActivity;
import com.chanapps.four.component.ThreadExpandExifOnClickListener;
import com.chanapps.four.component.ThreadImageExpander;
import com.chanapps.four.component.ThreadViewable;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.fragment.ThreadPopupDialogFragment;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 5/21/13
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadListener {

    private static final String TAG = ThreadListener.class.getSimpleName();
    private static final boolean DEBUG = false;

    private ThreadViewable threadViewable;
    private boolean isDark;

    public ThreadListener(ThreadViewable threadViewable, boolean isDark) {
        this.threadViewable = threadViewable;
        this.isDark = isDark;
    }

    private final SpannableOnClickListener createPopupListener(final ThreadPopupDialogFragment.PopupType popupType) {
        com.mijack.Xlog.logMethodEnter("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this,popupType);try{{com.mijack.Xlog.logMethodExit("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this);return new SpannableOnClickListener() {
            private int pos;
            private String boardCode;
            private long threadNo;
            private long postNo;
            private void locatePost(View v) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadListener$1.locatePost(android.view.View)",this,v);try{pos = -1;
                if (threadViewable == null)
                    {{com.mijack.Xlog.logMethodExit("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$1.locatePost(android.view.View)",this);return;}}}
                if (threadViewable.getAbsListView() == null)
                    {{com.mijack.Xlog.logMethodExit("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$1.locatePost(android.view.View)",this);return;}}}
                if (threadViewable.getAdapter() == null)
                    {{com.mijack.Xlog.logMethodExit("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$1.locatePost(android.view.View)",this);return;}}}
                Cursor cursor = threadViewable.getAdapter().getCursor();
                pos = threadViewable.getAbsListView().getPositionForView(v);
                if (DEBUG) {Log.i(TAG, "locatePost() no cursorId, current pos=" + pos);}
                if (cursor.moveToPosition(pos)) {
                    postNo = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
                    boardCode = cursor.getString(cursor.getColumnIndex(ChanPost.POST_BOARD_CODE));
                    threadNo = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_RESTO));
                    if (threadNo <= 0)
                        {threadNo = postNo;}
                }
                else {
                    pos = -1;
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadListener$1.locatePost(android.view.View)",this,throwable);throw throwable;}
            }
            private void locatePost(View v, long cursorId) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadListener$1.locatePost(android.view.View,long)",this,v,cursorId);try{if (threadViewable == null)
                    {{com.mijack.Xlog.logMethodExit("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$1.locatePost(android.view.View,long)",this);return;}}}
                if (threadViewable.getAdapter() == null)
                    {{com.mijack.Xlog.logMethodExit("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$1.locatePost(android.view.View,long)",this);return;}}}
                Cursor cursor = threadViewable.getAdapter().getCursor();
                postNo = cursorId;
                if (DEBUG) {Log.i(TAG, "locatePost() looking for postNo=" + postNo
                        + " out of " + cursor.getCount() + " cursor items");}
                /*// not efficient, but won't be more than a few hundred or thousand results*/
                final int col = cursor.getColumnIndex(ChanPost.POST_ID);
                pos = -1;
                cursor.moveToPosition(-1);
                while (cursor.moveToNext())
                    {if (cursor.getLong(col) == postNo) {
                        pos = cursor.getPosition();
                        break;
                    }}
                if (cursor.moveToPosition(pos)) {
                    boardCode = cursor.getString(cursor.getColumnIndex(ChanPost.POST_BOARD_CODE));
                    threadNo = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_RESTO));
                    if (threadNo <= 0)
                        {threadNo = postNo;}
                }
                else {
                    pos = -1;
                }
                if (DEBUG) {Log.i(TAG, "locatePost() cursorId=" + cursorId + " found pos=" + pos);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadListener$1.locatePost(android.view.View,long)",this,throwable);throw throwable;}
            }
            private void launchThread(Activity activity, long threadNo) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadListener$1.launchThread(android.app.Activity,long)",this,activity,threadNo);try{if (threadViewable == null)
                    {{com.mijack.Xlog.logMethodExit("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$1.launchThread(android.app.Activity,long)",this);return;}}}
                if (threadViewable.getAdapter() == null)
                    {{com.mijack.Xlog.logMethodExit("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$1.launchThread(android.app.Activity,long)",this);return;}}}
                Cursor cursor = threadViewable.getAdapter().getCursor();
                if (cursor.moveToFirst()) {
                    boardCode = cursor.getString(cursor.getColumnIndex(ChanPost.POST_BOARD_CODE));
                    if (DEBUG) {Log.i(TAG, "locatePost() launching thread activity /" + boardCode + "/" + threadNo);}
                    ThreadActivity.startActivity(activity, boardCode, threadNo, "");
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadListener$1.launchThread(android.app.Activity,long)",this,throwable);throw throwable;}
            }
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadListener$1.onClick(android.view.View)",this,v);try{if (threadViewable == null)
                    {{com.mijack.Xlog.logMethodExit("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$1.onClick(android.view.View)",this);return;}}}
                locatePost(v);
                if (DEBUG) {Log.i(TAG, "popupListener clicked pos=" + pos + " type=" + popupType);}
                if (pos >= 0)
                    {threadViewable.showDialog(boardCode, threadNo, postNo, pos, popupType);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadListener$1.onClick(android.view.View)",this,throwable);throw throwable;}
            }
            @Override
            public void onClick(View v, long cursorId) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadListener$1.onClick(android.view.View,long)",this,v,cursorId);try{if (threadViewable == null)
                    {{com.mijack.Xlog.logMethodExit("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$1.onClick(android.view.View,long)",this);return;}}}
                locatePost(v, cursorId);
                if (DEBUG) {Log.i(TAG, "popupListener clicked pos=" + pos + " type=" + popupType + " popup postNo=" + postNo);}
                if (pos >= 0)
                    {threadViewable.showDialog(boardCode, threadNo, postNo, pos, ThreadPopupDialogFragment.PopupType.SELF);}
                else if (v.getContext() instanceof Activity)
                    {launchThread((Activity)v.getContext(), cursorId);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadListener$1.onClick(android.view.View,long)",this,throwable);throw throwable;}
            }
        };}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("SpannableOnClickListener com.chanapps.four.viewer.ThreadListener.createPopupListener(ThreadPopupDialogFragment.PopupType)",this,throwable);throw throwable;}
    }

    public final SpannableOnClickListener backlinkOnClickListener = createPopupListener(ThreadPopupDialogFragment.PopupType.SELF);
    /*//public final View.OnClickListener backlinkOnClickListener = createPopupListener(ThreadPopupDialogFragment.PopupType.BACKLINKS);*/
    public final View.OnClickListener repliesOnClickListener = createPopupListener(ThreadPopupDialogFragment.PopupType.REPLIES);
    public final View.OnClickListener sameIdOnClickListener = createPopupListener(ThreadPopupDialogFragment.PopupType.SAME_ID);

    public final View.OnClickListener thumbOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadListener$2.onClick(android.view.View)",this,v);try{if (threadViewable == null || threadViewable.getAbsListView() == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$2.onClick(android.view.View)",this);return;}}
            int pos = threadViewable.getAbsListView().getPositionForView(v);
            if (DEBUG) {Log.i(TAG, "received item click pos: " + pos);}

            View itemView = null;
            for (int i = 0; i < threadViewable.getAbsListView().getChildCount(); i++) {
                View child = threadViewable.getAbsListView().getChildAt(i);
                if (threadViewable.getAbsListView().getPositionForView(child) == pos) {
                    itemView = child;
                    break;
                }
            }
            if (DEBUG) {Log.i(TAG, "found itemView=" + itemView);}
            if (itemView == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$2.onClick(android.view.View)",this);return;}}
            ThreadViewHolder viewHolder = (ThreadViewHolder)itemView.getTag(R.id.VIEW_HOLDER);
            if (DEBUG) {Log.i(TAG, "found viewHolder=" + itemView);}
            if (viewHolder == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$2.onClick(android.view.View)",this);return;}}
            if (viewHolder.list_item_image_header != null
                    && viewHolder.list_item_image_expanded_wrapper.getVisibility() == View.VISIBLE
                    && (
                    /*//viewHolder.list_item_image_expanded.getVisibility() == View.VISIBLE ||*/
                    viewHolder.list_item_image_expanded_webview.getVisibility() == View.VISIBLE
                        )
                    ) {
                if (DEBUG) {Log.i(TAG, "header already expanded, collapsing");}
                ThreadViewer.toggleExpandedImage(viewHolder);
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$2.onClick(android.view.View)",this);return;}
            }
            else if (viewHolder.list_item_image_header == null
                    && viewHolder.list_item_image_expanded_wrapper.getVisibility() == View.VISIBLE) { /*// hide and return*/
                if (DEBUG) {Log.i(TAG, "non-header already expanded, collapsing");}
                ThreadViewer.toggleExpandedImage(viewHolder);
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$2.onClick(android.view.View)",this);return;}
            }

            if (threadViewable == null || threadViewable.getAdapter() == null) {
                if (DEBUG) {Log.i(TAG, "no thread viewable or no adapter, exiting");}
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$2.onClick(android.view.View)",this);return;}
            }
            Cursor cursor = threadViewable.getAdapter().getCursor();
            cursor.moveToPosition(pos);
            final int flags = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_FLAGS));
            if (DEBUG) {Log.i(TAG, "clicked flags=" + flags);}
            if ((flags & ChanPost.FLAG_HAS_IMAGE) == 0) {
                if (DEBUG) {Log.i(TAG, "no image found in cursor, exiting");}
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$2.onClick(android.view.View)",this);return;}
            }

            if (DEBUG) {Log.i(TAG, "expanding pos: " + pos);}
            ThreadImageExpander expander = (new ThreadImageExpander(viewHolder, cursor,
                    true,
                    isDark
                            ? R.drawable.stub_image_background_dark
                            : R.drawable.stub_image_background,
                    expandedImageListener,
                    true
                    )
            );
            expander.displayImage();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadListener$2.onClick(android.view.View)",this,throwable);throw throwable;}
        }
    };

    public View.OnClickListener expandedImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadListener$3.onClick(android.view.View)",this,v);try{if (threadViewable == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$3.onClick(android.view.View)",this);return;}}
            if (threadViewable.getAbsListView() == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$3.onClick(android.view.View)",this);return;}}
            int pos = -1;
            try {
                pos = threadViewable.getAbsListView().getPositionForView(v);
            }
            catch (Exception e)  {
                Log.e(TAG, "Exception getting thread viewable for view = " + v, e);
                pos = -1;
            }
            if (pos < 0)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$3.onClick(android.view.View)",this);return;}}
            Cursor cursor = threadViewable.getAdapter().getCursor();
            if (!cursor.moveToPosition(pos))
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$3.onClick(android.view.View)",this);return;}}
            String boardCode = cursor.getString(cursor.getColumnIndex(ChanPost.POST_BOARD_CODE));
            long postNo = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
            long threadNo = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_RESTO));
            if (threadNo <= 0)
                {threadNo = postNo;}
            /*//if (postNo == threadNo)*/
            /*//    postNo = 0;*/
            if (DEBUG) {Log.i(TAG, "expandImageListener /" + boardCode + "/" + threadNo + "#p" + postNo);}
            if (postNo > 0)
                {GalleryViewActivity.startActivity(v.getContext(), boardCode, threadNo, postNo);}
            else
                {GalleryViewActivity.startAlbumViewActivity(v.getContext(), boardCode, threadNo);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadListener$3.onClick(android.view.View)",this,throwable);throw throwable;}
        }
    };

    public final View.OnClickListener exifOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadListener$4.onClick(android.view.View)",this,v);try{if (threadViewable == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$4.onClick(android.view.View)",this);return;}}
            if (threadViewable.getAbsListView() == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$4.onClick(android.view.View)",this);return;}}
            int pos = threadViewable.getAbsListView().getPositionForView(v);
            if (DEBUG) {Log.i(TAG, "received item click pos: " + pos);}

            View itemView = null;
            for (int i = 0; i < threadViewable.getAbsListView().getChildCount(); i++) {
                View child = threadViewable.getAbsListView().getChildAt(i);
                if (threadViewable.getAbsListView().getPositionForView(child) == pos) {
                    itemView = child;
                    break;
                }
            }
            if (DEBUG) {Log.i(TAG, "found itemView=" + itemView);}
            if (itemView == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$4.onClick(android.view.View)",this);return;}}
            if ((Boolean) itemView.getTag(R.id.THREAD_VIEW_IS_EXIF_EXPANDED))
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$4.onClick(android.view.View)",this);return;}}

            Cursor cursor = threadViewable.getAdapter() == null ? null : threadViewable.getAdapter().getCursor();
            if (cursor == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$4.onClick(android.view.View)",this);return;}}
            if (!cursor.moveToPosition(pos))
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadListener$4.onClick(android.view.View)",this);return;}}
            final int flags = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_FLAGS));
            if (DEBUG) {Log.i(TAG, "clicked flags=" + flags);}
            if ((flags & (ChanPost.FLAG_HAS_EXIF)) > 0) {
                (new ThreadExpandExifOnClickListener(
                        threadViewable.getAbsListView(), cursor, threadViewable.getHandler()))
                        .onClick(itemView);
                itemView.setTag(R.id.THREAD_VIEW_IS_EXIF_EXPANDED, Boolean.TRUE);
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadListener$4.onClick(android.view.View)",this,throwable);throw throwable;}
        }
    };

}
