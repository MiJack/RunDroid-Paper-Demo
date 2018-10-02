package com.chanapps.four.viewer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.*;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.chanapps.four.activity.R;
import com.chanapps.four.activity.SettingsActivity;
import com.chanapps.four.component.LetterSpacingTextView;
import com.chanapps.four.component.ThemeSelector;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.data.ChanThread;
import com.chanapps.four.loader.ChanImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.xml.sax.XMLReader;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 5/10/13
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unchecked")
public class BoardViewer {

    public static final int CATALOG_GRID = 0x01;
    public static final int ABBREV_BOARDS = 0x02;
    public static final int HIDE_LAST_REPLIES = 0x04;

    private static String TAG = BoardViewer.class.getSimpleName();
    private static boolean DEBUG = false;
    public static final String SUBJECT_FONT = "fonts/Roboto-BoldCondensed.ttf";

    private static ImageLoader imageLoader;
    private static DisplayImageOptions displayImageOptions;
    private static Typeface subjectTypeface;

    protected static final int NUM_BOARD_CODE_COLORS = 5;

    public static void initStatics(Context context, boolean isDark) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.BoardViewer.initStatics(android.content.Context,boolean)",context,isDark);try{Resources res = context.getResources();
        subjectTypeface = Typeface.createFromAsset(res.getAssets(), SUBJECT_FONT);
        imageLoader = ChanImageLoader.getInstance(context);
        /*//int stub = isDark*/
        /*//        ? R.drawable.stub_image_background_dark*/
        /*//        : R.drawable.stub_image_background;*/
        displayImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.NONE)
                .cacheInMemory()
                .cacheOnDisc()
                .displayer(new FadeInBitmapDisplayer(100))
                .build();com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.initStatics(android.content.Context,boolean)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer.initStatics(android.content.Context,boolean)",throwable);throw throwable;}
    }

    public static boolean setViewValue(View view, Cursor cursor, String groupBoardCode,
                                       int columnWidth, int columnHeight,
                                       View.OnClickListener overlayListener,
                                       View.OnClickListener overflowListener,
                                       int options,
                                       Typeface titleTypeface)
    {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.setViewValue(android.view.View,android.database.Cursor,android.text.String,int,int,android.text.View.OnClickListener,android.text.View.OnClickListener,int,android.graphics.Typeface)",view,cursor,groupBoardCode,columnWidth,columnHeight,overlayListener,overflowListener,options,titleTypeface);try{if (imageLoader == null)
            {throw new IllegalStateException("Must call initStatics() before calling setViewValue()");}

        int flags = cursor.getInt(cursor.getColumnIndex(ChanThread.THREAD_FLAGS));
        boolean isDark = ThemeSelector.instance(view.getContext()).isDark();
        BoardViewHolder viewHolder = (BoardViewHolder)view.getTag(R.id.VIEW_HOLDER);
        setItem(viewHolder, overlayListener, overflowListener, flags, options);
        setSubject(viewHolder, cursor, flags);
        setSubjectLarge(viewHolder, cursor, flags, subjectTypeface);
        setInfo(viewHolder, cursor, groupBoardCode, flags, options);
        setNumReplies(viewHolder, cursor, groupBoardCode, flags, options);
        setCountryFlag(viewHolder, cursor);
        setIcons(viewHolder, flags, isDark);
        setImage(viewHolder, cursor, groupBoardCode, flags, columnWidth, columnHeight, options, titleTypeface);
        clearLastReplyImages(viewHolder);
        setLastReplies(viewHolder, cursor, options);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setViewValue(android.view.View,android.database.Cursor,android.text.String,int,int,android.text.View.OnClickListener,android.text.View.OnClickListener,int,android.graphics.Typeface)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.setViewValue(android.view.View,android.database.Cursor,android.text.String,int,int,android.text.View.OnClickListener,android.text.View.OnClickListener,int,android.graphics.Typeface)",throwable);throw throwable;}
    }

    protected static void clearLastReplyImages(BoardViewHolder viewHolder) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.BoardViewer.clearLastReplyImages(android.text.BoardViewHolder)",viewHolder);try{if (viewHolder.grid_item_thread_thumb_1 != null)
            {viewHolder.grid_item_thread_thumb_1.setVisibility(View.GONE);}
        if (viewHolder.grid_item_thread_thumb_2 != null)
            {viewHolder.grid_item_thread_thumb_2.setVisibility(View.GONE);}
        if (viewHolder.grid_item_thread_thumb_3 != null)
            {viewHolder.grid_item_thread_thumb_3.setVisibility(View.GONE);}
        if (viewHolder.grid_item_thread_thumb_4 != null)
            {viewHolder.grid_item_thread_thumb_4.setVisibility(View.GONE);}
        if (viewHolder.grid_item_thread_thumb_5 != null)
            {viewHolder.grid_item_thread_thumb_5.setVisibility(View.GONE);}com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.clearLastReplyImages(android.text.BoardViewHolder)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer.clearLastReplyImages(android.text.BoardViewHolder)",throwable);throw throwable;}
    }

    protected static boolean setItem(BoardViewHolder viewHolder,
                                     View.OnClickListener overlayListener,
                                     View.OnClickListener overflowListener,
                                     int flags,
                                     int options) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.setItem(android.text.BoardViewHolder,android.text.View.OnClickListener,android.text.View.OnClickListener,int,int)",viewHolder,overlayListener,overflowListener,flags,options);try{boolean isHeader = (flags & ChanThread.THREAD_FLAG_HEADER) > 0;

        View overflow = viewHolder.grid_item_overflow_icon;
        if (overflow != null) {
            if ((flags & ChanThread.THREAD_FLAG_HEADER) > 0) {
                overflow.setVisibility(View.GONE);
            }
            else if ((options & ABBREV_BOARDS) > 0) {
                overflow.setVisibility(View.GONE);
            }
            else if (overflowListener == null) {
                overflow.setVisibility(View.GONE);
            }
            else {
                overflow.setOnClickListener(overflowListener);
                overflow.setVisibility(View.VISIBLE);
            }
        }

        ViewGroup overlay = viewHolder.grid_item;
        /*//ViewGroup overlay = viewHolder.grid_item_thread;*/
        if (overlay != null) {
            if (overlayListener != null && !isHeader) {
                overlay.setOnClickListener(overlayListener);
                overlay.setClickable(true);
            }
            else {
                overlay.setClickable(false);
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setItem(android.text.BoardViewHolder,android.text.View.OnClickListener,android.text.View.OnClickListener,int,int)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.setItem(android.text.BoardViewHolder,android.text.View.OnClickListener,android.text.View.OnClickListener,int,int)",throwable);throw throwable;}
    }
    
    protected static String getBoardAbbrev(Context context, Cursor cursor, String groupBoardCode) {
        com.mijack.Xlog.logStaticMethodEnter("android.text.String com.chanapps.four.viewer.BoardViewer.getBoardAbbrev(android.content.Context,android.database.Cursor,android.text.String)",context,cursor,groupBoardCode);try{String threadAbbrev = "";
        String boardCode = cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_BOARD_CODE));
        if (boardCode != null && !boardCode.isEmpty() && !boardCode.equals(groupBoardCode)) {
            ChanBoard board = ChanBoard.getBoardByCode(context, boardCode);
            String name = board == null ? null : board.getName(context);
            if (ChanBoard.WATCHLIST_BOARD_CODE.equals(groupBoardCode))
                {threadAbbrev += "";} /*// "/" + boardCode + "/";*/
            else if (name != null)
                {threadAbbrev += name;} /*//"/" + boardCode + "/ " + name;*/
            else
                {threadAbbrev += "";} /*// "/" + boardCode + "/";*/
        }
        {com.mijack.Xlog.logStaticMethodExit("android.text.String com.chanapps.four.viewer.BoardViewer.getBoardAbbrev(android.content.Context,android.database.Cursor,android.text.String)");return threadAbbrev;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.text.String com.chanapps.four.viewer.BoardViewer.getBoardAbbrev(android.content.Context,android.database.Cursor,android.text.String)",throwable);throw throwable;}
    }

    protected static boolean setSubject(final BoardViewHolder viewHolder, final Cursor cursor, final int flags) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.setSubject(android.text.BoardViewHolder,android.database.Cursor,int)",viewHolder,cursor,flags);try{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setSubject(android.text.BoardViewHolder,android.database.Cursor,int)");return setSubject(viewHolder.grid_item_thread_subject,
                cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_SUBJECT)),
                cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_TEXT)),
                flags);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.setSubject(android.text.BoardViewHolder,android.database.Cursor,int)",throwable);throw throwable;}
    }

    protected static boolean setSubject(final TextView tv, final String s, final String t, final int flags) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.setSubject(android.widget.TextView,android.text.String,android.text.String,int)",tv,s,t,flags);try{if (tv == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setSubject(android.widget.TextView,android.text.String,android.text.String,int)");return false;}}
        if ((flags & ChanThread.THREAD_FLAG_HEADER) > 0) {
            tv.setVisibility(View.GONE);
            tv.setText("");
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setSubject(android.widget.TextView,android.text.String,android.text.String,int)");return true;}
        }
        String u = (s != null && !s.isEmpty() ? "<b>" + s + "</b>" : "")
                + (s != null && t != null && !s.isEmpty() && !t.isEmpty() ? "<br/>" : "")
                + (t != null && !t.isEmpty() ? t : "");
        if (DEBUG) {Log.i(TAG, "setSubject tv=" + tv + " u=" + u);}
        if (!u.isEmpty()) {
            String html = ThreadViewer.markupHtml(u);
            Spannable spannable = Spannable.Factory.getInstance().newSpannable(Html.fromHtml(html, null, spoilerTagHandler));
            
            tv.setText(spannable);
            tv.setVisibility(View.VISIBLE);
        }
        else {
            tv.setVisibility(View.GONE);
            tv.setText("");
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setSubject(android.widget.TextView,android.text.String,android.text.String,int)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.setSubject(android.widget.TextView,android.text.String,android.text.String,int)",throwable);throw throwable;}
    }

    protected static boolean setSubjectLarge(BoardViewHolder viewHolder, Cursor cursor, int flags, Typeface subjectTypeface) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.setSubjectLarge(android.text.BoardViewHolder,android.database.Cursor,int,android.graphics.Typeface)",viewHolder,cursor,flags,subjectTypeface);try{TextView tv = viewHolder.grid_item_thread_subject_header;
        if (tv == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setSubjectLarge(android.text.BoardViewHolder,android.database.Cursor,int,android.graphics.Typeface)");return false;}}
        if ((flags & ChanThread.THREAD_FLAG_HEADER) == 0) {
            tv.setVisibility(View.GONE);
            tv.setText("");
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setSubjectLarge(android.text.BoardViewHolder,android.database.Cursor,int,android.graphics.Typeface)");return true;}
        }
        String u = cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_SUBJECT));
        if (DEBUG) {Log.i(TAG, "setSubjectLarge tv=" + tv + " u=" + u);}
        if (u != null && !u.isEmpty()) {
            tv.setText(u);
            tv.setTypeface(subjectTypeface);
            tv.setVisibility(View.VISIBLE);
        }
        else {
            tv.setVisibility(View.GONE);
            tv.setText("");
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setSubjectLarge(android.text.BoardViewHolder,android.database.Cursor,int,android.graphics.Typeface)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.setSubjectLarge(android.text.BoardViewHolder,android.database.Cursor,int,android.graphics.Typeface)",throwable);throw throwable;}
    }

    protected static boolean setImage(BoardViewHolder viewHolder, Cursor cursor, String groupBoardCode,
                                      int flags, int columnWidth, int columnHeight, int options, Typeface titleTypeface) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.setImage(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int,int,int,android.graphics.Typeface)",viewHolder,cursor,groupBoardCode,flags,columnWidth,columnHeight,options,titleTypeface);try{if (DEBUG) {Log.i(TAG, "setImage()");}
        ImageView iv = viewHolder.grid_item_thread_thumb;
        if (iv == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setImage(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int,int,int,android.graphics.Typeface)");return false;}}
        boolean isWideHeader = (flags & ChanThread.THREAD_FLAG_HEADER) > 0 && (options & CATALOG_GRID) == 0;
        if (DEBUG) {Log.i(TAG, "setImage() isWideHeader=" + isWideHeader + " no=" + cursor.getLong(cursor.getColumnIndex(ChanThread.THREAD_NO)));}
        if (isWideHeader) {
            iv.setImageBitmap(null);
            iv.setVisibility(View.GONE);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setImage(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int,int,int,android.graphics.Typeface)");return true;}
        }

        String boardCode = cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_BOARD_CODE));
        if ((options & ABBREV_BOARDS) > 0) {
            if (DEBUG) {Log.i(TAG, "setImage() abbrevBoards");}
            iv.setImageBitmap(null);
            iv.setVisibility(View.GONE);
            displayBoardCode(viewHolder, cursor, boardCode, groupBoardCode, titleTypeface, flags, options);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setImage(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int,int,int,android.graphics.Typeface)");return true;}
        }
        displayBoardCode(viewHolder, cursor, boardCode, groupBoardCode, titleTypeface, flags, options);

        sizeImage(iv, viewHolder.grid_item, columnWidth, columnHeight, options);
        String url = imageUrl(iv, boardCode, groupBoardCode, cursor, flags, options);
        displayImage(iv, url);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setImage(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int,int,int,android.graphics.Typeface)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.setImage(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int,int,int,android.graphics.Typeface)",throwable);throw throwable;}
    }

    protected static boolean displayImage(final ImageView iv, final String url) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.displayImage(android.widget.ImageView,android.text.String)",iv,url);try{if (DEBUG) {Log.i(TAG, "displayImage()");}
        if (!SettingsActivity.shouldLoadThumbs(iv.getContext())) {
            iv.setVisibility(View.GONE);
            iv.setImageBitmap(null);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.displayImage(android.widget.ImageView,android.text.String)");return true;}
        }
        if (url == null || url.isEmpty()) {
            iv.setVisibility(View.GONE);
            iv.setImageDrawable(null);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.displayImage(android.widget.ImageView,android.text.String)");return false;}
        }
        int drawHash = iv.getDrawable() == null ? 0 : iv.getDrawable().hashCode();
        int tagHash = iv.getTag(R.id.IMG_HASH) == null ? 0 : (Integer)iv.getTag(R.id.IMG_HASH);
        String tagUrl = (String)iv.getTag(R.id.IMG_URL);
        if (DEBUG) {Log.i(TAG, "displayImage() checking url=" + url + " tagUrl=" + tagUrl + " drawHash=" + drawHash + " tagHash=" + tagHash);}
        if (drawHash != 0 && drawHash == tagHash && tagUrl != null && !tagUrl.isEmpty() && url.equals(tagUrl)) {
            if (DEBUG) {Log.i(TAG, "displayImage() skipping url=" + url + " drawable=" + iv.getDrawable());}
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.displayImage(android.widget.ImageView,android.text.String)");return true;}
        }
        iv.setVisibility(View.GONE);
        imageLoader.displayImage(url, iv, displayImageOptions, thumbLoadingListener);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.displayImage(android.widget.ImageView,android.text.String)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.displayImage(android.widget.ImageView,android.text.String)",throwable);throw throwable;}
    }
    
    protected static final float BOARD_CODE_LETTER_SPACING = 0.65f;

    protected static void displayBoardCode(BoardViewHolder viewHolder,
                                           Cursor cursor,
                                           String boardCode, String groupBoardCode,
                                           Typeface titleTypeface,
                                           int flags,
                                           int options) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.BoardViewer.displayBoardCode(android.text.BoardViewHolder,android.database.Cursor,android.text.String,android.text.String,android.graphics.Typeface,int,int)",viewHolder,cursor,boardCode,groupBoardCode,titleTypeface,flags,options);try{if (DEBUG) {Log.i(TAG, "displayBoardCode");}
        if ((options & ABBREV_BOARDS) > 0) {
            if (viewHolder.grid_item_board_code != null) {
                viewHolder.grid_item_board_code.setText("");
                viewHolder.grid_item_board_code.setVisibility(View.GONE);
            }
            if (viewHolder.grid_item_bottom_frame != null)
                {viewHolder.grid_item_bottom_frame.setVisibility(View.GONE);}
            displayNicelyFormattedBoardCode(titleTypeface, boardCode, viewHolder.grid_item_thread_subject_header_abbr);
            colorBoardFrame(boardCode, viewHolder.grid_item_thread_subject_header_abbr);
        }
        else if (groupBoardCode != null
                && !ChanBoard.isVirtualBoard(groupBoardCode)
                && (flags & ChanThread.THREAD_FLAG_HEADER) > 0
                && (options & CATALOG_GRID) > 0
                && viewHolder.grid_item_thread_subject_header_abbr != null) { /*// grid header*/
            if (viewHolder.grid_item_board_code != null) {
                viewHolder.grid_item_board_code.setText("");
                viewHolder.grid_item_board_code.setVisibility(View.VISIBLE);
            }
            if (viewHolder.grid_item_bottom_frame != null)
                {viewHolder.grid_item_bottom_frame.setVisibility(View.VISIBLE);}
            displayNicelyFormattedBoardCode(titleTypeface, boardCode, viewHolder.grid_item_thread_subject_header_abbr);
            /*//colorBoardFrame(boardCode, viewHolder.grid_item_thumb_frame);*/
        }
        else if (boardCode == null || boardCode.equals(groupBoardCode)) {
            if (viewHolder.grid_item_thread_subject_header_abbr != null)
                {viewHolder.grid_item_thread_subject_header_abbr.setText("");}
            if (viewHolder.grid_item_board_code != null) {
                viewHolder.grid_item_board_code.setText("");
                viewHolder.grid_item_board_code.setVisibility(View.VISIBLE);
            }
            if (viewHolder.grid_item_bottom_frame != null)
                {viewHolder.grid_item_bottom_frame.setVisibility(View.VISIBLE);}
        }
        else {
            if (viewHolder.grid_item_thread_subject_header_abbr != null)
                {viewHolder.grid_item_thread_subject_header_abbr.setText("");}
            if (viewHolder.grid_item_board_code != null)
                {viewHolder.grid_item_board_code.setVisibility(View.VISIBLE);}
            displayNicelyFormattedBoardCode(titleTypeface, boardCode, viewHolder.grid_item_board_code);
            if (viewHolder.grid_item_bottom_frame != null)
                {viewHolder.grid_item_bottom_frame.setVisibility(View.VISIBLE);}
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.displayBoardCode(android.text.BoardViewHolder,android.database.Cursor,android.text.String,android.text.String,android.graphics.Typeface,int,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer.displayBoardCode(android.text.BoardViewHolder,android.database.Cursor,android.text.String,android.text.String,android.graphics.Typeface,int,int)",throwable);throw throwable;}
    }

    protected static void displayNicelyFormattedBoardCode(Typeface titleTypeface, String boardCode, TextView tv) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.BoardViewer.displayNicelyFormattedBoardCode(android.graphics.Typeface,android.text.String,android.widget.TextView)",titleTypeface,boardCode,tv);try{String boardCodeTitle = "/" + boardCode + "/";
        if (DEBUG) {Log.i(TAG, "displayBoardCode() boardCodeTitle=" + boardCodeTitle);}
        if (tv == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.displayNicelyFormattedBoardCode(android.graphics.Typeface,android.text.String,android.widget.TextView)");return;}}
        if (titleTypeface != null)
            {tv.setTypeface(titleTypeface);}
        if (tv instanceof LetterSpacingTextView)
            {((LetterSpacingTextView) tv).setTextSpacing(BOARD_CODE_LETTER_SPACING);}
        tv.setText(boardCodeTitle);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer.displayNicelyFormattedBoardCode(android.graphics.Typeface,android.text.String,android.widget.TextView)",throwable);throw throwable;}
    }

    protected static String imageUrl(ImageView iv, String boardCode, String groupBoardCode,
                                     Cursor cursor, int flags, int options) {
        com.mijack.Xlog.logStaticMethodEnter("android.text.String com.chanapps.four.viewer.BoardViewer.imageUrl(android.widget.ImageView,android.text.String,android.text.String,android.database.Cursor,int,int)",iv,boardCode,groupBoardCode,cursor,flags,options);try{String url;
        long threadNo = cursor.getLong(cursor.getColumnIndex(ChanThread.THREAD_NO));
        if (DEBUG) {Log.i(TAG, "imageUrl() /" + boardCode + "/" + threadNo);}
        if (groupBoardCode != null
                && !ChanBoard.isVirtualBoard(groupBoardCode)
                && (flags & ChanThread.THREAD_FLAG_HEADER) > 0
                && (options & CATALOG_GRID) > 0) { /*// grid header*/
            /*//url = "drawable://" + R.drawable.transparent;*/
            int drawableId = ThemeSelector.instance(iv.getContext()).isDark()
                    ? R.drawable.bg_222
                    : R.drawable.bg_f4f4f4;
            url = "drawable://" + drawableId;
        }
        /*//else if (threadNo <= 0) {*/
        /*//    if (DEBUG) Log.i(TAG, "setImage() /" + boardCode + "/" + threadNo + " displaying board code instead of image");*/
        /*//    url = null;*/
        /*//}*/
        else {
            url = cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_THUMBNAIL_URL));
            if (DEBUG) {Log.i(TAG, "imageUrl() /" + boardCode + "/ url=" + url);}
            int i = (Long.valueOf(threadNo % 3)).intValue();
            String defaultUrl = ChanBoard.getIndexedImageDrawableUrl(boardCode, i);
            iv.setTag(R.id.BOARD_GRID_VIEW_DEFAULT_DRAWABLE, defaultUrl);
            if (url == null || url.isEmpty())
                {url = ChanBoard.getIndexedImageDrawableUrl(boardCode, i);}
        }
        {com.mijack.Xlog.logStaticMethodExit("android.text.String com.chanapps.four.viewer.BoardViewer.imageUrl(android.widget.ImageView,android.text.String,android.text.String,android.database.Cursor,int,int)");return url;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.text.String com.chanapps.four.viewer.BoardViewer.imageUrl(android.widget.ImageView,android.text.String,android.text.String,android.database.Cursor,int,int)",throwable);throw throwable;}
    }

    protected static void sizeImage(ImageView iv, View item, int columnWidth, int columnHeight, int options) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.BoardViewer.sizeImage(android.widget.ImageView,android.view.View,int,int,int)",iv,item,columnWidth,columnHeight,options);try{/*//iv.setVisibility(View.VISIBLE);*/
        if ((options & CATALOG_GRID) > 0) {
            /*
            ViewParent parent = iv.getParent();
            if (parent != null && parent instanceof View) {
                View v = (View)parent;
                ViewGroup.LayoutParams params = v.getLayoutParams();
                if (columnWidth > 0 && params != null) {
                    params.width = columnWidth; // force square
                    params.height = columnWidth; // force square
                }
            }
            */
            ViewGroup.LayoutParams params = iv.getLayoutParams();
            if (columnWidth > 0 && params != null) {
                params.width = columnWidth; /*// force square*/
                params.height = columnWidth; /*// force square*/
            }
            /*
            ViewGroup.LayoutParams params2 = item.getLayoutParams();
            if (columnWidth > 0 && params2 != null) {
                params2.width = columnWidth; // force rectangle
                params2.height = (int)((double)columnWidth * 1.62d); // force rectangle
            }
            */
        }
        /*
        else {
            ViewGroup.LayoutParams params = iv.getLayoutParams();
            if (columnWidth > 0 && params != null) {
                params.width = columnWidth; // force square
                params.height = columnWidth; // force square
            }
        }
        */com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.sizeImage(android.widget.ImageView,android.view.View,int,int,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer.sizeImage(android.widget.ImageView,android.view.View,int,int,int)",throwable);throw throwable;}
    }


    protected static int colorIndex = -1;
    /*
    protected static void displayBoardCode(ImageView iv, TextView tv, String boardCode) {
        int idx = (colorIndex = (colorIndex + 1) % NUM_BOARD_CODE_COLORS);
        int color;
        switch (colorIndex) {
            default:
            case 0: color = R.color.PaletteBoardColor0; break;
            case 1: color = R.color.PaletteBoardColor1; break;
            case 2: color = R.color.PaletteBoardColor2; break;
            case 3: color = R.color.PaletteBoardColor3; break;
            case 4: color = R.color.PaletteBoardColor4; break;

            case 5: color = R.color.PaletteBoardColor5; break;
            case 6: color = R.color.PaletteBoardColor6; break;
            case 7: color = R.color.PaletteBoardColor7; break;
            case 8: color = R.color.PaletteBoardColor8; break;
            case 9: color = R.color.PaletteBoardColor9; break;
            case 10: color = R.color.PaletteBoardColor10; break;
        }
        if (DEBUG) Log.i(TAG, "setImage() displaying board code /" + boardCode + "/ color index=" + idx + " color=" + color);
        iv.setImageResource(color);
        if (tv != null)
            tv.setText("/" + boardCode + "/");
    }
    */
    protected static void colorBoardFrame(String boardCode, TextView v) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.BoardViewer.colorBoardFrame(android.text.String,android.widget.TextView)",boardCode,v);try{if (v == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.colorBoardFrame(android.text.String,android.widget.TextView)");return;}}
        if (boardCode == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.colorBoardFrame(android.text.String,android.widget.TextView)");return;}}
        /*//int colorIndex = boardCode.hashCode() % NUM_BOARD_CODE_COLORS;*/
        colorIndex = (colorIndex + 1) % NUM_BOARD_CODE_COLORS;
        int colorId = pickColor(colorIndex);
        if (DEBUG) {Log.i(TAG, "colorBoardFrame /" + boardCode + "/ idx=" + colorIndex + " id=" + colorId);}
        v.setTextColor(v.getResources().getColor(R.color.PaletteBoardTextColor));
        v.setBackgroundColor(v.getResources().getColor(colorId));
        /*//v.getBackground().setColorFilter(v.getResources().getColor(colorId), PorterDuff.Mode.DARKEN);*/
        /*//frame.setBackgroundColor(colorId);*/
        /*//frame.setBackgroundResource(colorId);*/}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer.colorBoardFrame(android.text.String,android.widget.TextView)",throwable);throw throwable;}
    }

    protected static int pickColor(int colorIndex) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.viewer.BoardViewer.pickColor(int)",colorIndex);try{int color;
        switch (colorIndex) {
            default:
            case 0: color = R.color.PaletteBoardColor0; break;
            case 1: color = R.color.PaletteBoardColor1; break;
            case 2: color = R.color.PaletteBoardColor2; break;
            case 3: color = R.color.PaletteBoardColor3; break;
            case 4: color = R.color.PaletteBoardColor4; break;

            case 5: color = R.color.PaletteBoardColor5; break;
            case 6: color = R.color.PaletteBoardColor6; break;
            case 7: color = R.color.PaletteBoardColor7; break;
            case 8: color = R.color.PaletteBoardColor8; break;
            case 9: color = R.color.PaletteBoardColor9; break;
            case 10: color = R.color.PaletteBoardColor10; break;
        }
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.viewer.BoardViewer.pickColor(int)");return color;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.viewer.BoardViewer.pickColor(int)",throwable);throw throwable;}
    }

    protected static ImageLoadingListener thumbLoadingListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.BoardViewer$1.onLoadingStarted(android.text.String,android.view.View)",this,imageUri,view);try{if (view != null && view instanceof ImageView) {
                ((ImageView)view).setImageDrawable(null);
                view.setTag(R.id.IMG_URL, null);
                view.setTag(R.id.IMG_HASH, null);
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.BoardViewer$1.onLoadingStarted(android.text.String,android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer$1.onLoadingStarted(android.text.String,android.view.View)",this,throwable);throw throwable;}
        }
        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.BoardViewer$1.onLoadingFailed(android.text.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this,imageUri,view,failReason);try{if (DEBUG) {Log.e(TAG, "Loading failed uri=" + imageUri + " reason=" + failReason.getType());}
            /*//displayDefaultItem(imageUri, view);*/
            if (view != null) {
                view.setTag(R.id.IMG_URL, null);
                view.setTag(R.id.IMG_HASH, null);
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.BoardViewer$1.onLoadingFailed(android.text.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer$1.onLoadingFailed(android.text.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this,throwable);throw throwable;}
        }
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.BoardViewer$1.onLoadingComplete(android.text.String,android.view.View,android.graphics.Bitmap)",this,imageUri,view,loadedImage);try{if (view != null && view instanceof ImageView) {
                ImageView iv = (ImageView)view;
                iv.setTag(R.id.IMG_URL, imageUri);
                iv.setTag(R.id.IMG_HASH, iv.getDrawable() == null ? null : iv.getDrawable().hashCode());
                view.setVisibility(View.VISIBLE);
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.BoardViewer$1.onLoadingComplete(android.text.String,android.view.View,android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer$1.onLoadingComplete(android.text.String,android.view.View,android.graphics.Bitmap)",this,throwable);throw throwable;}
        }
        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.BoardViewer$1.onLoadingCancelled(android.text.String,android.view.View)",this,imageUri,view);try{if (DEBUG) {Log.e(TAG, "Loading cancelled uri=" + imageUri);}
            if (view != null) {
                view.setTag(R.id.IMG_URL, null);
                view.setTag(R.id.IMG_HASH, null);
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.BoardViewer$1.onLoadingCancelled(android.text.String,android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer$1.onLoadingCancelled(android.text.String,android.view.View)",this,throwable);throw throwable;}
        }
    };

    protected static boolean setCountryFlag(BoardViewHolder viewHolder, Cursor cursor) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.setCountryFlag(android.text.BoardViewHolder,android.database.Cursor)",viewHolder,cursor);try{ImageView iv = viewHolder.grid_item_country_flag;
        String url = cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_COUNTRY_FLAG_URL));
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setCountryFlag(android.text.BoardViewHolder,android.database.Cursor)");return setCountryFlag(iv, url);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.setCountryFlag(android.text.BoardViewHolder,android.database.Cursor)",throwable);throw throwable;}
    }

    protected static boolean setCountryFlag(final ImageView iv, final String url) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.setCountryFlag(android.widget.ImageView,android.text.String)",iv,url);try{if (iv == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setCountryFlag(android.widget.ImageView,android.text.String)");return false;}}
        if (!SettingsActivity.shouldLoadThumbs(iv.getContext())) {
            iv.setVisibility(View.GONE);
            iv.setImageBitmap(null);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setCountryFlag(android.widget.ImageView,android.text.String)");return true;}
        }
        if (url == null || url.isEmpty()) {
            iv.setVisibility(View.GONE);
            imageLoader.displayImage(url, iv, displayImageOptions, thumbLoadingListener);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setCountryFlag(android.widget.ImageView,android.text.String)");return true;}
        }

        iv.setVisibility(View.GONE);
        iv.setImageDrawable(null);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setCountryFlag(android.widget.ImageView,android.text.String)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.setCountryFlag(android.widget.ImageView,android.text.String)",throwable);throw throwable;}
    }

    protected static boolean setInfo(BoardViewHolder viewHolder, Cursor cursor, String groupBoardCode, int flags, int options) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.setInfo(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int)",viewHolder,cursor,groupBoardCode,flags,options);try{if ((flags & ChanThread.THREAD_FLAG_HEADER) > 0) {
            if (viewHolder.grid_item_thread_info != null)
                {viewHolder.grid_item_thread_info.setText("");}
            if (viewHolder.grid_item_thread_info_header != null)
                {viewHolder.grid_item_thread_info_header.setText(
                        Html.fromHtml(cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_HEADLINE))));}
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setInfo(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int)");return true;}
        }

        if (viewHolder.grid_item_thread_info_header != null)
            {viewHolder.grid_item_thread_info_header.setText("");}

        TextView tv = viewHolder.grid_item_thread_info;
        if (tv == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setInfo(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int)");return false;}}
        if (cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_BOARD_CODE)).equals(groupBoardCode)) {
            tv.setText("");
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setInfo(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int)");return true;}
        }

        String s = (flags & ChanThread.THREAD_FLAG_BOARD) == 0
                ? getBoardAbbrev(tv.getContext(), cursor, groupBoardCode)
                : "";
        String t = cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_HEADLINE));
        if (t == null)
            {t = "";}
        /*//String u = s + (s.isEmpty() || t.isEmpty() ? "" : "<br/>") + t;*/
        String u = s + (s.isEmpty() || t.isEmpty() ? "" : " ") + t;
        if (!u.isEmpty())
            {tv.setText(Html.fromHtml(u));}
        else
            {tv.setText("");}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setInfo(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.setInfo(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int)",throwable);throw throwable;}
    }

    protected static boolean setNumReplies(BoardViewHolder viewHolder, Cursor cursor, String groupBoardCode,
                                           int flags, int options) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.setNumReplies(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int)",viewHolder,cursor,groupBoardCode,flags,options);try{TextView numReplies = viewHolder.grid_item_num_replies_text;
        TextView numImages = viewHolder.grid_item_num_images_text;
        TextView numRepliesLabel = viewHolder.grid_item_num_replies_label;
        TextView numImagesLabel = viewHolder.grid_item_num_images_label;
        TextView numRepliesLabelAbbr = viewHolder.grid_item_num_replies_label_abbr;
        TextView numImagesLabelAbbr = viewHolder.grid_item_num_images_label_abbr;
        ImageView numRepliesImg = viewHolder.grid_item_num_replies_img;
        ImageView numImagesImg = viewHolder.grid_item_num_images_img;

        if ((flags & ChanThread.THREAD_FLAG_HEADER) > 0
                || (ChanBoard.isVirtualBoard(groupBoardCode) && !ChanBoard.WATCHLIST_BOARD_CODE.equals(groupBoardCode))
                || ((options & ABBREV_BOARDS) > 0)
            ) {
            if (numReplies != null)
                {numReplies.setVisibility(View.GONE);}
            if (numImages != null)
                {numImages.setVisibility(View.GONE);}
            if (numRepliesLabel != null)
                {numRepliesLabel.setVisibility(View.GONE);}
            if (numImagesLabel != null)
                {numImagesLabel.setVisibility(View.GONE);}
            if (numRepliesLabelAbbr != null)
                {numRepliesLabelAbbr.setVisibility(View.GONE);}
            if (numImagesLabelAbbr != null)
                {numImagesLabelAbbr.setVisibility(View.GONE);}
            if (numRepliesImg != null)
                {numRepliesImg.setVisibility(View.GONE);}
            if (numImagesImg != null)
                {numImagesImg.setVisibility(View.GONE);}
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setNumReplies(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int)");return true;}
        }

        int r = cursor.getInt(cursor.getColumnIndex(ChanThread.THREAD_NUM_REPLIES));
        int i = cursor.getInt(cursor.getColumnIndex(ChanThread.THREAD_NUM_IMAGES));
        if (numRepliesLabel != null) {
            numRepliesLabel.setText(numRepliesLabel.getResources().getQuantityString(R.plurals.thread_num_replies_label, r));
            numRepliesLabel.setVisibility(View.VISIBLE);
        }
        if (numImagesLabel != null) {
            numImagesLabel.setText(numImagesLabel.getResources().getQuantityString(R.plurals.thread_num_images_label, i));
            numImagesLabel.setVisibility(View.VISIBLE);
        }
        if (numRepliesLabelAbbr != null) {
            numRepliesLabelAbbr.setVisibility(View.VISIBLE);
        }
        if (numImagesLabelAbbr != null) {
            numImagesLabelAbbr.setVisibility(View.VISIBLE);
        }
        if (numReplies != null) {
            numReplies.setText(r >= 0 ? String.valueOf(r) : "");
            numReplies.setVisibility(View.VISIBLE);
        }
        if (numImages != null) {
            numImages.setText(i >= 0 ? String.valueOf(i) : "");
            numImages.setVisibility(View.VISIBLE);
        }
        if (numRepliesImg != null)
            {numRepliesImg.setVisibility(View.VISIBLE);}
        if (numImagesImg != null)
            {numImagesImg.setVisibility(View.VISIBLE);}
        /*
        if (r >= 0) {
            numReplies.setText(String.valueOf(r));
            viewHolder.grid_item_num_replies_text.setVisibility(View.VISIBLE);
            //viewHolder.grid_item_num_replies_img.setVisibility(View.VISIBLE);
        }
        else {
            numReplies.setText("");
            //viewHolder.grid_item_num_replies_text.setVisibility(View.GONE);
            //viewHolder.grid_item_num_replies_img.setVisibility(View.GONE);
        }

        if (i >= 0) {
            numImages.setText(String.valueOf(i));
            //viewHolder.grid_item_num_images_text.setVisibility(View.VISIBLE);
            //viewHolder.grid_item_num_images_img.setVisibility(View.VISIBLE);
        }
        else {
            numImages.setText("");
            //viewHolder.grid_item_num_images_text.setVisibility(View.GONE);
            //viewHolder.grid_item_num_images_img.setVisibility(View.GONE);
        }
        */
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setNumReplies(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.setNumReplies(android.text.BoardViewHolder,android.database.Cursor,android.text.String,int,int)",throwable);throw throwable;}
    }

    protected static final int DRAWABLE_ALPHA_LIGHT = 0xaa;
    protected static final int DRAWABLE_ALPHA_DARK = 0xee;

    @TargetApi(16)
    protected static void setAlpha(ImageView iv, int alpha) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.BoardViewer.setAlpha(android.widget.ImageView,int)",iv,alpha);try{if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            {deprecatedSetAlpha(iv, alpha);}
        else
            {iv.setImageAlpha(alpha);}com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.setAlpha(android.widget.ImageView,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer.setAlpha(android.widget.ImageView,int)",throwable);throw throwable;}
    }

    @SuppressWarnings("deprecation")
    protected static void deprecatedSetAlpha(ImageView v, int a) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.BoardViewer.deprecatedSetAlpha(android.widget.ImageView,int)",v,a);try{v.setAlpha(a);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.deprecatedSetAlpha(android.widget.ImageView,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer.deprecatedSetAlpha(android.widget.ImageView,int)",throwable);throw throwable;}
    }

    protected static boolean setIcons(BoardViewHolder viewHolder, int flags, boolean isDark) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.setIcons(android.text.BoardViewHolder,int,boolean)",viewHolder,flags,isDark);try{int alpha = isDark ? DRAWABLE_ALPHA_DARK : DRAWABLE_ALPHA_LIGHT;
        if (viewHolder.grid_item_dead_icon != null) {
            viewHolder.grid_item_dead_icon.setVisibility((flags & ChanThread.THREAD_FLAG_DEAD) > 0 ? View.VISIBLE : View.GONE);
            setAlpha(viewHolder.grid_item_dead_icon, alpha);
        }
        if (viewHolder.grid_item_closed_icon != null) {
            viewHolder.grid_item_closed_icon.setVisibility((flags & ChanThread.THREAD_FLAG_CLOSED) > 0 ? View.VISIBLE : View.GONE);
            setAlpha(viewHolder.grid_item_closed_icon, alpha);
        }
        if (viewHolder.grid_item_sticky_icon != null) {
            viewHolder.grid_item_sticky_icon.setVisibility((flags & ChanThread.THREAD_FLAG_STICKY) > 0 ? View.VISIBLE : View.GONE);
            setAlpha(viewHolder.grid_item_sticky_icon, alpha);
        }
        if (DEBUG)
            {Log.i(TAG, "setSubjectIcons()"
                    + " dead=" + ((flags & ChanThread.THREAD_FLAG_DEAD) > 0)
                    + " closed=" + ((flags & ChanThread.THREAD_FLAG_CLOSED) > 0)
                    + " sticky=" + ((flags & ChanThread.THREAD_FLAG_STICKY) > 0)
            );}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.setIcons(android.text.BoardViewHolder,int,boolean)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.setIcons(android.text.BoardViewHolder,int,boolean)",throwable);throw throwable;}
    }

    /* similar to ThreadViewer version, but not clickable */
    static private final Html.TagHandler spoilerTagHandler = new Html.TagHandler() {
        static private final String SPOILER_TAG = "s";
        class SpoilerSpan extends CharacterStyle {
            private int start = 0;
            private int end = 0;
            private boolean blackout = true;
            public SpoilerSpan() {
                super();
            }
            public SpoilerSpan(int start, int end) {
                this();
                this.start = start;
                this.end = end;
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.BoardViewer$$SpoilerSpan.updateDrawState(android.text.TextPaint)",this,ds);try{if (blackout) {
                    int textColor = ds.getColor();
                    ds.bgColor = textColor;
                }com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.BoardViewer$$SpoilerSpan.updateDrawState(android.text.TextPaint)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer$$SpoilerSpan.updateDrawState(android.text.TextPaint)",this,throwable);throw throwable;}
            }
        }
        class SpanFactory {
            public Class getSpanClass() { com.mijack.Xlog.logMethodEnter("android.text.Class com.chanapps.four.viewer.BoardViewer$$SpanFactory.getSpanClass()",this);try{com.mijack.Xlog.logMethodExit("android.text.Class com.chanapps.four.viewer.BoardViewer$$SpanFactory.getSpanClass()",this);return SpoilerSpan.class;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.text.Class com.chanapps.four.viewer.BoardViewer$$SpanFactory.getSpanClass()",this,throwable);throw throwable;} }
            public Object getSpan(final int start, final int end) { com.mijack.Xlog.logMethodEnter("android.text.Object com.chanapps.four.viewer.BoardViewer$$SpanFactory.getSpan(int,int)",this,start,end);try{com.mijack.Xlog.logMethodExit("android.text.Object com.chanapps.four.viewer.BoardViewer$$SpanFactory.getSpan(int,int)",this);return new SpoilerSpan(start, end);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.text.Object com.chanapps.four.viewer.BoardViewer$$SpanFactory.getSpan(int,int)",this,throwable);throw throwable;} }
        }
        SpanFactory spanFactory = new SpanFactory();
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.BoardViewer$2.handleTag(boolean,android.text.String,android.text.Editable,org.xml.sax.XMLReader)",this,opening,tag,output,xmlReader);try{if (SPOILER_TAG.equals(tag))
                {handleSpoiler(opening, output);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.BoardViewer$2.handleTag(boolean,android.text.String,android.text.Editable,org.xml.sax.XMLReader)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer$2.handleTag(boolean,android.text.String,android.text.Editable,org.xml.sax.XMLReader)",this,throwable);throw throwable;}
        }
        private void handleSpoiler(boolean opening, Editable output) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.BoardViewer$2.handleSpoiler(boolean,android.text.Editable)",this,opening,output);try{if (opening)
                {handleSpoilerOpen(output);}
            else
                {handleSpoilerClose(output);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.BoardViewer$2.handleSpoiler(boolean,android.text.Editable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer$2.handleSpoiler(boolean,android.text.Editable)",this,throwable);throw throwable;}
        }
        private void handleSpoilerOpen(Editable output) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.BoardViewer$2.handleSpoilerOpen(android.text.Editable)",this,output);try{if (DEBUG) {Log.i(TAG, "handleSpoilerOpen(" + output + ")");}
            int len = output.length();
            output.setSpan(spanFactory.getSpan(len, len), len, len, Spannable.SPAN_MARK_MARK);com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.BoardViewer$2.handleSpoilerOpen(android.text.Editable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer$2.handleSpoilerOpen(android.text.Editable)",this,throwable);throw throwable;}
        }
        private void handleSpoilerClose(Editable output) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.BoardViewer$2.handleSpoilerClose(android.text.Editable)",this,output);try{if (DEBUG) {Log.i(TAG, "handleSpoilerClose(" + output + ")");}
            int len = output.length();
            Object obj = getFirst(output, spanFactory.getSpanClass());
            int start = output.getSpanStart(obj);
            output.removeSpan(obj);
            if (start >= 0 && len >= 0 && start != len)  {
                output.setSpan(spanFactory.getSpan(start, len), start, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (DEBUG) {Log.i(TAG, "setSpan(" + start + ", " + len + ")");}
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.BoardViewer$2.handleSpoilerClose(android.text.Editable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer$2.handleSpoilerClose(android.text.Editable)",this,throwable);throw throwable;}
        }
        private Object getFirst(Editable text, Class kind) {
            com.mijack.Xlog.logMethodEnter("android.text.Object com.chanapps.four.viewer.BoardViewer$2.getFirst(android.text.Editable,android.text.Class)",this,text,kind);try{Object[] objs = text.getSpans(0, text.length(), kind);
            if (objs.length == 0)
                {{com.mijack.Xlog.logMethodExit("android.text.Object com.chanapps.four.viewer.BoardViewer$2.getFirst(android.text.Editable,android.text.Class)",this);return null;}}
            if (DEBUG) {Log.i(TAG, "Found " + objs.length + " matching spans");}
            for (int i = 0; i < objs.length; i++) {
                Object span = objs[i];
                if (text.getSpanFlags(span) == Spannable.SPAN_MARK_MARK)
                    {{com.mijack.Xlog.logMethodExit("android.text.Object com.chanapps.four.viewer.BoardViewer$2.getFirst(android.text.Editable,android.text.Class)",this);return span;}}
            }
            {com.mijack.Xlog.logMethodExit("android.text.Object com.chanapps.four.viewer.BoardViewer$2.getFirst(android.text.Editable,android.text.Class)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.text.Object com.chanapps.four.viewer.BoardViewer$2.getFirst(android.text.Editable,android.text.Class)",this,throwable);throw throwable;}
        }
        private Object getLast(Editable text, Class kind) {
            com.mijack.Xlog.logMethodEnter("android.text.Object com.chanapps.four.viewer.BoardViewer$2.getLast(android.text.Editable,android.text.Class)",this,text,kind);try{Object[] objs = text.getSpans(0, text.length(), kind);
            if (objs.length == 0)
                {{com.mijack.Xlog.logMethodExit("android.text.Object com.chanapps.four.viewer.BoardViewer$2.getLast(android.text.Editable,android.text.Class)",this);return null;}}
            for (int i = objs.length - 1; i >= 0; i--) {
                Object span = objs[i];
                if (text.getSpanFlags(span) == Spannable.SPAN_MARK_MARK)
                    {{com.mijack.Xlog.logMethodExit("android.text.Object com.chanapps.four.viewer.BoardViewer$2.getLast(android.text.Editable,android.text.Class)",this);return span;}}
            }
            {com.mijack.Xlog.logMethodExit("android.text.Object com.chanapps.four.viewer.BoardViewer$2.getLast(android.text.Editable,android.text.Class)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.text.Object com.chanapps.four.viewer.BoardViewer$2.getLast(android.text.Editable,android.text.Class)",this,throwable);throw throwable;}
        }
    };

    protected static void setLastReplies(final BoardViewHolder viewHolder, final Cursor cursor, final int options) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.BoardViewer.setLastReplies(android.text.BoardViewHolder,android.database.Cursor,int)",viewHolder,cursor,options);try{boolean[] isSet = new boolean[5];
        try {
            if ((options & CATALOG_GRID) > 0)
                {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.setLastReplies(android.text.BoardViewHolder,android.database.Cursor,int)");return;}}
            if ((options & HIDE_LAST_REPLIES) > 0)
                {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.setLastReplies(android.text.BoardViewHolder,android.database.Cursor,int)");return;}}
            String boardCode = cursor.getString(cursor.getColumnIndex(ChanThread.THREAD_BOARD_CODE));
            if (boardCode == null || boardCode.isEmpty())
                {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.setLastReplies(android.text.BoardViewHolder,android.database.Cursor,int)");return;}}
            byte[] b = cursor.getBlob(cursor.getColumnIndex(ChanThread.THREAD_LAST_REPLIES_BLOB));
            if (DEBUG) {Log.i(TAG, "lastRepliesBlob=" + b);}
            if (b == null)
                {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.setLastReplies(android.text.BoardViewHolder,android.database.Cursor,int)");return;}}
            ChanPost[] lastReplies = ChanThread.parseLastRepliesBlob(b);
            if (lastReplies == null)
                {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.setLastReplies(android.text.BoardViewHolder,android.database.Cursor,int)");return;}}
            if (DEBUG) {Log.i(TAG, "lastReplies len=" + lastReplies.length);}
            if (lastReplies.length == 0)
                {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.BoardViewer.setLastReplies(android.text.BoardViewHolder,android.database.Cursor,int)");return;}}
            isSet[0] = lastReplies.length < 1
                    ? false
                    : displayLastReply(viewHolder.grid_item_thread_subject_1,
                    viewHolder.grid_item_thread_thumb_1,
                    viewHolder.grid_item_country_flag_1,
                    lastReplies[0], boardCode);
            isSet[1] = lastReplies.length < 2
                    ? false
                    : displayLastReply(viewHolder.grid_item_thread_subject_2,
                    viewHolder.grid_item_thread_thumb_2,
                    viewHolder.grid_item_country_flag_2,
                    lastReplies[1], boardCode);
            isSet[2] = lastReplies.length < 3
                    ? false
                    : displayLastReply(viewHolder.grid_item_thread_subject_3,
                    viewHolder.grid_item_thread_thumb_3,
                    viewHolder.grid_item_country_flag_3,
                    lastReplies[2], boardCode);
            isSet[3] = lastReplies.length < 4
                    ? false
                    : displayLastReply(viewHolder.grid_item_thread_subject_4,
                    viewHolder.grid_item_thread_thumb_4,
                    viewHolder.grid_item_country_flag_4,
                    lastReplies[3], boardCode);
            isSet[4] = lastReplies.length < 5
                    ? false
                    : displayLastReply(viewHolder.grid_item_thread_subject_5,
                    viewHolder.grid_item_thread_thumb_5,
                    viewHolder.grid_item_country_flag_5,
                    lastReplies[4], boardCode);
        }
        catch (Exception e) {
            Log.e(TAG, "Exception reading lastRepliesBlob", e);
        }
        finally {
            /*
            if (viewHolder.grid_item_thread_1 != null)
                viewHolder.grid_item_thread_1.setVisibility(isSet[0] ? View.VISIBLE : View.GONE);
            if (viewHolder.grid_item_thread_2 != null)
                viewHolder.grid_item_thread_2.setVisibility(isSet[1] ? View.VISIBLE : View.GONE);
            if (viewHolder.grid_item_thread_3 != null)
                viewHolder.grid_item_thread_3.setVisibility(isSet[2] ? View.VISIBLE : View.GONE);
            if (viewHolder.grid_item_thread_4 != null)
                viewHolder.grid_item_thread_4.setVisibility(isSet[3] ? View.VISIBLE : View.GONE);
            if (viewHolder.grid_item_thread_5 != null)
                viewHolder.grid_item_thread_5.setVisibility(isSet[4] ? View.VISIBLE : View.GONE);
                */
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.BoardViewer.setLastReplies(android.text.BoardViewHolder,android.database.Cursor,int)",throwable);throw throwable;}
    }

    protected static boolean displayLastReply(final TextView subject, final ImageView thumb, final ImageView countryFlag,
                                      final ChanPost post, final String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.BoardViewer.displayLastReply(android.widget.TextView,android.widget.ImageView,android.widget.ImageView,com.chanapps.four.data.ChanPost,android.text.String)",subject,thumb,countryFlag,post,boardCode);try{String[] textComponents = post == null ? new String[]{ "", "" } : post.textComponents("");
        String countryFlagUrl = post == null ? null : post.lastReplyCountryFlagUrl(countryFlag.getContext(), boardCode);
        String thumbUrl = post == null ? null : post.lastReplyThumbnailUrl(thumb.getContext(), boardCode);
        if (subject != null)
            {setSubject(subject, textComponents[0], textComponents[1], 0);}
        if (countryFlag != null)
            {setCountryFlag(countryFlag, countryFlagUrl);}
        if (thumb != null)
            {displayImage(thumb, thumbUrl);}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.BoardViewer.displayLastReply(android.widget.TextView,android.widget.ImageView,android.widget.ImageView,com.chanapps.four.data.ChanPost,android.text.String)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.BoardViewer.displayLastReply(android.widget.TextView,android.widget.ImageView,android.widget.ImageView,com.chanapps.four.data.ChanPost,android.text.String)",throwable);throw throwable;}
    }

}