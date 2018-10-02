package com.chanapps.four.component;

import android.database.Cursor;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.chanapps.four.activity.R;
import com.chanapps.four.data.ChanPost;

/**
* Created with IntelliJ IDEA.
* User: johnarleyburns
* Date: 4/9/13
* Time: 10:28 AM
* To change this template use File | Settings | File Templates.
*/
public class ThreadExpandExifOnClickListener implements View.OnClickListener {

    private static final String TAG = ThreadExpandExifOnClickListener.class.getSimpleName();
    private static final boolean DEBUG = false;

    private AbsListView absListView = null;
    private Handler handler = null;
    private TextView itemExifView;
    private int listPosition = 0;
    private int flags;
    private String exifText;

    public ThreadExpandExifOnClickListener(final AbsListView absListView, final Cursor cursor, final Handler handler) {
        this.absListView = absListView;
        this.handler = handler;
        exifText = cursor.getString(cursor.getColumnIndex(ChanPost.POST_EXIF_TEXT));
        listPosition = cursor.getPosition();
        flags = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_FLAGS));
    }

    private void collapseExif() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.ThreadExpandExifOnClickListener.collapseExif()",this);try{if (DEBUG) {Log.i(TAG, "collapsed pos=" + listPosition);}
        if (itemExifView != null)
            {itemExifView.setVisibility(View.GONE);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.ThreadExpandExifOnClickListener.collapseExif()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.ThreadExpandExifOnClickListener.collapseExif()",this,throwable);throw throwable;}
    }

    @Override
    public void onClick(View v) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.ThreadExpandExifOnClickListener.onClick(android.view.View)",this,v);try{if (DEBUG) {Log.i(TAG, "expanding pos=" + listPosition);}
        itemExifView = (TextView)v.findViewById(R.id.list_item_exif_text);
        if ((flags & ChanPost.FLAG_HAS_EXIF) > 0)
            {expandExif();}com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.ThreadExpandExifOnClickListener.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.ThreadExpandExifOnClickListener.onClick(android.view.View)",this,throwable);throw throwable;}
    }


    private boolean shouldExpandExif() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.component.ThreadExpandExifOnClickListener.shouldExpandExif()",this);try{if (itemExifView != null && itemExifView.getVisibility() != View.GONE) {
            if (DEBUG) {Log.i(TAG, "Exif already expanded, skipping");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.ThreadExpandExifOnClickListener.shouldExpandExif()",this);return false;}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.ThreadExpandExifOnClickListener.shouldExpandExif()",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.component.ThreadExpandExifOnClickListener.shouldExpandExif()",this,throwable);throw throwable;}
    }

    private void expandExif() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.ThreadExpandExifOnClickListener.expandExif()",this);try{if (!shouldExpandExif()) {
            collapseExif();
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.ThreadExpandExifOnClickListener.expandExif()",this);return;}
        }
        if (itemExifView.getVisibility() == View.VISIBLE) {
            itemExifView.setVisibility(View.GONE);
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.ThreadExpandExifOnClickListener.expandExif()",this);return;}
        }
        if (DEBUG) {Log.i(TAG, "Expanding exifText=" + exifText);}
        if (itemExifView != null && exifText != null && !exifText.isEmpty()) {
            itemExifView.setText(Html.fromHtml(exifText));
            itemExifView.setVisibility(View.VISIBLE);
            if (absListView != null && handler != null)
                {handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.ThreadExpandExifOnClickListener$1.run()",this);try{absListView.smoothScrollBy(250, 250);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.ThreadExpandExifOnClickListener$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.ThreadExpandExifOnClickListener$1.run()",this,throwable);throw throwable;}
                    }
                }, 250);} /*// give time for EXIF data to appear*/
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.ThreadExpandExifOnClickListener.expandExif()",this,throwable);throw throwable;}
    }

}
