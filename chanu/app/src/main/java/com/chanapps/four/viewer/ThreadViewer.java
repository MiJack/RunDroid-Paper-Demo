package com.chanapps.four.viewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.*;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.gallery3d.ui.Log;
import com.chanapps.four.activity.GalleryViewActivity;
import com.chanapps.four.activity.R;
import com.chanapps.four.activity.SettingsActivity;
import com.chanapps.four.component.ThreadImageExpander;
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.data.ChanThread;
import com.chanapps.four.data.FontSize;
import com.chanapps.four.gallery.ChanImage;
import com.chanapps.four.loader.ChanImageLoader;
import com.chanapps.four.service.NetworkProfileManager;
import com.chanapps.four.service.profile.NetworkProfile;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.xml.sax.XMLReader;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA. User: johnarleyburns Date: 5/10/13 Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unchecked")
/*// for spoiler handler stuff*/
public class ThreadViewer {

    /*// public static final double MAX_HEADER_SCALE = 1.5;*/
    public static final double MAX_HEADER_SCALE = 2.0;
    public static final String SUBJECT_FONT = "fonts/Roboto-BoldCondensed.ttf";

    private static final String TAG = ThreadViewer.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static DisplayMetrics displayMetrics = null;
    private static Typeface subjectTypeface = null;
    private static int cardPaddingPx = 0;
    private static int actionBarHeightPx = 0;
    private static ImageLoader imageLoader = null;
    private static DisplayImageOptions expandedDisplayImageOptions = null;
    private static DisplayImageOptions thumbDisplayImageOptions = null;
    private static int stub;
    private static int boardTabletViewWidthPx = 0;
    private static int fragmentMarginWidthPx = 0;
    private static int fragmentMarginHeightPx = 0;

    public static void initStatics(Context context, boolean isDark) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.initStatics(android.content.Context,boolean)",context,isDark);try{imageLoader = ChanImageLoader.getInstance(context);
        stub = isDark ? R.drawable.stub_image_background_dark
                : R.drawable.stub_image_background;
        Resources res = context.getResources();
        cardPaddingPx = res
                .getDimensionPixelSize(R.dimen.BoardGridView_spacing);

        final TypedArray styledAttributes = context.getTheme()
                .obtainStyledAttributes(
                        new int[] { android.R.attr.actionBarSize });
        actionBarHeightPx = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        boardTabletViewWidthPx = res
                .getDimensionPixelSize(R.dimen.BoardGridViewTablet_layout_width);
        fragmentMarginWidthPx = res
                .getDimensionPixelSize(R.dimen.dialogFragmentMarginWidth);
        fragmentMarginHeightPx = res
                .getDimensionPixelSize(R.dimen.dialogFragmentMarginHeight);
        displayMetrics = res.getDisplayMetrics();
        subjectTypeface = Typeface.createFromAsset(res.getAssets(),
                SUBJECT_FONT);
        expandedDisplayImageOptions = createExpandedDisplayImageOptions(null);
        thumbDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc()
                .cacheInMemory()
                .imageScaleType(ImageScaleType.NONE)
                .displayer(new FadeInBitmapDisplayer(100))
                /*//.resetViewBeforeLoading()*/
                /*//.showStubImage(stub)*/
                .build();com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.initStatics(android.content.Context,boolean)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.initStatics(android.content.Context,boolean)",throwable);throw throwable;}
    }

    private static DisplayImageOptions createExpandedDisplayImageOptions(
            ImageSize imageSize) {
        com.mijack.Xlog.logStaticMethodEnter("com.nostra13.universalimageloader.core.DisplayImageOptions com.chanapps.four.viewer.ThreadViewer.createExpandedDisplayImageOptions(com.nostra13.universalimageloader.core.assist.ImageSize)",imageSize);try{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.core.DisplayImageOptions com.chanapps.four.viewer.ThreadViewer.createExpandedDisplayImageOptions(com.nostra13.universalimageloader.core.assist.ImageSize)");return new DisplayImageOptions.Builder()
                .cacheOnDisc()
                .cacheInMemory()
                .imageSize(imageSize)
                .displayer(new FadeInBitmapDisplayer(100))
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showStubImage(stub).resetViewBeforeLoading().build();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.nostra13.universalimageloader.core.DisplayImageOptions com.chanapps.four.viewer.ThreadViewer.createExpandedDisplayImageOptions(com.nostra13.universalimageloader.core.assist.ImageSize)",throwable);throw throwable;}
    }

    public static boolean setViewValue(final View view, final Cursor cursor,
            String groupBoardCode, boolean showContextMenu, int columnWidth,
            int columnHeight, View.OnClickListener thumbOnClickListener,
            SpannableOnClickListener backlinkOnClickListener,
            View.OnClickListener commentsOnClickListener,
            View.OnClickListener imagesOnClickListener,
            View.OnClickListener repliesOnClickListener,
            View.OnClickListener sameIdOnClickListener,
            View.OnClickListener exifOnClickListener,
            View.OnClickListener overflowListener,
            View.OnClickListener expandedImageListener,
            View.OnLongClickListener startActionModeListener,
            View.OnClickListener goToThreadUrlListener) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.setViewValue(android.view.View,android.database.Cursor,android.text.style.String,boolean,int,int,android.text.style.View.OnClickListener,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnLongClickListener,android.text.style.View.OnClickListener)",view,cursor,groupBoardCode,showContextMenu,columnWidth,columnHeight,thumbOnClickListener,backlinkOnClickListener,commentsOnClickListener,imagesOnClickListener,repliesOnClickListener,sameIdOnClickListener,exifOnClickListener,overflowListener,expandedImageListener,startActionModeListener,goToThreadUrlListener);try{if (imageLoader == null)
            {throw new IllegalStateException(
                    "Must call initStatics() before calling setViewValue()");}
        int flagIdx = cursor.getColumnIndex(ChanPost.POST_FLAGS);
        int flags = flagIdx >= 0 ? cursor.getInt(flagIdx) : -1;
        if (flags < 0) /*// we are on board list*/
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setViewValue(android.view.View,android.database.Cursor,android.text.style.String,boolean,int,int,android.text.style.View.OnClickListener,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnLongClickListener,android.text.style.View.OnClickListener)");return BoardViewer.setViewValue(view, cursor, groupBoardCode,
                    columnWidth, columnHeight, null, null, 0, null);}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setViewValue(android.view.View,android.database.Cursor,android.text.style.String,boolean,int,int,android.text.style.View.OnClickListener,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnLongClickListener,android.text.style.View.OnClickListener)");return setListItemView(view, cursor, flags, showContextMenu,
                    thumbOnClickListener, backlinkOnClickListener,
                    commentsOnClickListener, imagesOnClickListener,
                    repliesOnClickListener, sameIdOnClickListener,
                    exifOnClickListener,
                    /*// postReplyListener,*/
                    overflowListener, expandedImageListener,
                    startActionModeListener, goToThreadUrlListener);}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.setViewValue(android.view.View,android.database.Cursor,android.text.style.String,boolean,int,int,android.text.style.View.OnClickListener,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnLongClickListener,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    protected static boolean setListItemView(final View view,
            final Cursor cursor,
            int flags,
            boolean showContextMenu,
            View.OnClickListener thumbOnClickListener,
            SpannableOnClickListener backlinkOnClickListener,
            View.OnClickListener commentsOnClickListener,
            View.OnClickListener imagesOnClickListener,
            View.OnClickListener repliesOnClickListener,
            View.OnClickListener sameIdOnClickListener,
            View.OnClickListener exifOnClickListener,
            /*// View.OnClickListener postReplyListener,*/
            View.OnClickListener overflowListener,
            View.OnClickListener expandedImageListener,
            final View.OnLongClickListener startActionModeListener,
            View.OnClickListener goToThreadUrlListener) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.setListItemView(android.view.View,android.database.Cursor,int,boolean,android.text.style.View.OnClickListener,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnLongClickListener,android.text.style.View.OnClickListener)",view,cursor,flags,showContextMenu,thumbOnClickListener,backlinkOnClickListener,commentsOnClickListener,imagesOnClickListener,repliesOnClickListener,sameIdOnClickListener,exifOnClickListener,overflowListener,expandedImageListener,startActionModeListener,goToThreadUrlListener);try{ThreadViewHolder viewHolder = (ThreadViewHolder) view
                .getTag(R.id.VIEW_HOLDER);
        setItem(viewHolder, cursor, flags, showContextMenu,
                commentsOnClickListener, imagesOnClickListener,
                repliesOnClickListener, overflowListener);
        /*// setImageWrapper(viewHolder, flags);*/
        /*// if ((flags & ChanPost.FLAG_HAS_IMAGE) > 0) {*/
        bindThumbnailExpandTarget(viewHolder.list_item_image_expansion_target,
                thumbOnClickListener);
        bindThumbnailExpandTarget(viewHolder.list_item_image_header,
                thumbOnClickListener);
        /*// }*/

        if ((flags & ChanPost.FLAG_IS_HEADER) > 0)
            {setHeaderImage(viewHolder, cursor, flags, expandedImageListener,
                    showContextMenu);}
        else
            {setImage(viewHolder, cursor, flags, expandedImageListener,
                    showContextMenu);}
        setCountryFlag(viewHolder, cursor, flags);
        setHeaderValue(viewHolder, cursor, sameIdOnClickListener);
        setSubject(viewHolder, cursor, flags, backlinkOnClickListener);
        setSubjectIcons(viewHolder, flags);
        setText(viewHolder, cursor, flags, backlinkOnClickListener,
                exifOnClickListener);
        setImageExifValue(viewHolder);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setListItemView(android.view.View,android.database.Cursor,int,boolean,android.text.style.View.OnClickListener,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnLongClickListener,android.text.style.View.OnClickListener)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.setListItemView(android.view.View,android.database.Cursor,int,boolean,android.text.style.View.OnClickListener,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnLongClickListener,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    static protected boolean setItem(ThreadViewHolder viewHolder,
            Cursor cursor,
            int flags,
            boolean showContextMenu,
            /*// View.OnClickListener backlinkOnClickListener,*/
            View.OnClickListener commentsOnClickListener,
            View.OnClickListener imagesOnClickListener,
            View.OnClickListener repliesOnClickListener,
            View.OnClickListener overflowListener) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.setItem(android.text.style.ThreadViewHolder,android.database.Cursor,int,boolean,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener)",viewHolder,cursor,flags,showContextMenu,commentsOnClickListener,imagesOnClickListener,repliesOnClickListener,overflowListener);try{ViewGroup item = viewHolder.list_item;
        long postId = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
        item.setTag(postId);
        item.setTag(R.id.THREAD_VIEW_IS_EXIF_EXPANDED, Boolean.FALSE);

        if ((flags & ChanPost.FLAG_IS_HEADER) > 0)
            {displayHeaderCountFields(viewHolder, cursor, showContextMenu,
                    commentsOnClickListener, imagesOnClickListener,
                    repliesOnClickListener);}
        else
            {displayItemCountFields(viewHolder, cursor, showContextMenu,
                    repliesOnClickListener);}
        viewHolder.list_item.setOnClickListener(null);
        viewHolder.list_item.setClickable(false);

        View overflow = viewHolder.list_item_header_bar_overflow_wrapper;
        if (overflow != null) {
            if (showContextMenu) {
                overflow.setOnClickListener(overflowListener);
                overflow.setVisibility(View.VISIBLE);
                if (viewHolder.list_item_right_menu_spacer != null)
                    {viewHolder.list_item_right_menu_spacer
                            .setVisibility(View.GONE);}
            } else {
                overflow.setOnClickListener(null);
                overflow.setVisibility(View.GONE);
                if (viewHolder.list_item_right_menu_spacer != null)
                    {viewHolder.list_item_right_menu_spacer
                            .setVisibility(View.VISIBLE);}
            }
        }
        item.setVisibility(View.VISIBLE);
        /*
         * if (cursor.getPosition() == 1) { int top =
         * item.getResources().getDimensionPixelSize
         * (R.dimen.ThreadListLayout_paddingBottom);
         * item.setPadding(item.getPaddingLeft(), top, item.getPaddingRight(),
         * item.getPaddingBottom()); } else {
         * item.setPadding(item.getPaddingLeft(), 0, item.getPaddingRight(),
         * item.getPaddingBottom()); }
         */
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setItem(android.text.style.ThreadViewHolder,android.database.Cursor,int,boolean,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.setItem(android.text.style.ThreadViewHolder,android.database.Cursor,int,boolean,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    static public void setHeaderNumRepliesImages(ThreadViewHolder viewHolder,
            Cursor cursor, View.OnClickListener commentsOnClickListener,
            View.OnClickListener imagesOnClickListener) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.setHeaderNumRepliesImages(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener)",viewHolder,cursor,commentsOnClickListener,imagesOnClickListener);try{int r = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_NUM_REPLIES));
        int i = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_NUM_IMAGES));
        TextView numReplies = viewHolder.list_item_num_replies_text;
        TextView numImages = viewHolder.list_item_num_images_text;
        TextView numRepliesLabel = viewHolder.list_item_num_replies_label;
        TextView numImagesLabel = viewHolder.list_item_num_images_label;
        View cmtWrapper = viewHolder.list_item_num_replies;
        View cmtWrapperTop = viewHolder.list_item_num_replies_top;
        View imgWrapper = viewHolder.list_item_num_images;
        View imgWrapperTop = viewHolder.list_item_num_images_top;
        if (numReplies != null) {
            FontSize.sizeTextView(numReplies);
            numReplies.setText(String.valueOf(r));
        }
        if (numImages != null) {
            FontSize.sizeTextView(numImages);
            numImages.setText(String.valueOf(i));
        }
        if (numRepliesLabel != null) {
            FontSize.sizeTextView(numRepliesLabel);
            numRepliesLabel.setText(numRepliesLabel.getResources()
                    .getQuantityString(R.plurals.thread_num_replies_label, r));
        }
        if (numImagesLabel != null) {
            FontSize.sizeTextView(numImagesLabel);
            numImagesLabel.setText(numImagesLabel.getResources()
                    .getQuantityString(R.plurals.thread_num_images_label, i));
        }

        int n = r >= 0 && cursor.getCount() > 1 ? r : 0;
        setWrapperListener(cmtWrapper, commentsOnClickListener, n);
        setWrapperListener(cmtWrapperTop, commentsOnClickListener, n);
        setWrapperListener(imgWrapper, imagesOnClickListener, i);
        setWrapperListener(imgWrapperTop, imagesOnClickListener, i);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.setHeaderNumRepliesImages(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.setHeaderNumRepliesImages(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    static protected void setWrapperListener(View v, View.OnClickListener l,
            int n) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.setWrapperListener(android.view.View,android.text.style.View.OnClickListener,int)",v,l,n);try{if (v == null) {
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.setWrapperListener(android.view.View,android.text.style.View.OnClickListener,int)");return;}
        }
        if (l == null || n <= 0) {
            v.setOnClickListener(null);
            v.setClickable(false);
        }
        v.setOnClickListener(l);
        v.setClickable(true);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.setWrapperListener(android.view.View,android.text.style.View.OnClickListener,int)",throwable);throw throwable;}
    }

    static protected void displayHeaderCountFields(ThreadViewHolder viewHolder,
            Cursor cursor, boolean showContextMenu,
            View.OnClickListener commentsOnClickListener,
            View.OnClickListener imagesOnClickListener,
            View.OnClickListener repliesOnClickListener) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.displayHeaderCountFields(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener)",viewHolder,cursor,showContextMenu,commentsOnClickListener,imagesOnClickListener,repliesOnClickListener);try{displayHeaderBarAgoNo(viewHolder, cursor);
        setHeaderNumRepliesImages(viewHolder, cursor, commentsOnClickListener,
                imagesOnClickListener);
        displayNumDirectReplies(viewHolder, cursor, showContextMenu,
                repliesOnClickListener);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.displayHeaderCountFields(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.displayHeaderCountFields(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    static protected void displayHeaderBarAgoNo(ThreadViewHolder viewHolder,
            Cursor cursor) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.displayHeaderBarAgoNo(android.text.style.ThreadViewHolder,android.database.Cursor)",viewHolder,cursor);try{String dateText = cursor.getString(cursor
                .getColumnIndex(ChanPost.POST_DATE_TEXT));
        TextView ago = viewHolder.list_item_header_bar_ago;
        if (ago != null) {
            String sep = ago.getResources().getString(
                    R.string.list_item_ago_date_separator);
            if (sep != null && !sep.isEmpty())
                {sep += " ";}
            FontSize.sizeTextView(ago);
            ago.setText(sep + dateText);
        }
        long postNo = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
        TextView no = viewHolder.list_item_header_bar_no;
        if (no != null) {
            FontSize.sizeTextView(no);
            no.setText(String.valueOf(postNo));
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.displayHeaderBarAgoNo(android.text.style.ThreadViewHolder,android.database.Cursor)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.displayHeaderBarAgoNo(android.text.style.ThreadViewHolder,android.database.Cursor)",throwable);throw throwable;}
    }

    static protected void displayItemCountFields(ThreadViewHolder viewHolder,
            Cursor cursor, boolean showContextMenu,
            /*// View.OnClickListener backlinkOnClickListener,*/
            View.OnClickListener repliesOnClickListener) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.displayItemCountFields(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener)",viewHolder,cursor,showContextMenu,repliesOnClickListener);try{displayHeaderBarAgoNo(viewHolder, cursor);
        /*// n += displayNumRefs(item, cursor, backlinkOnClickListener);*/
        displayNumDirectReplies(viewHolder, cursor, showContextMenu,
                repliesOnClickListener); /*// , true);*/com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.displayItemCountFields(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.displayItemCountFields(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    static public int displayNumDirectReplies(ThreadViewHolder viewHolder,
            Cursor cursor, boolean showContextMenu,
            View.OnClickListener repliesOnClickListener) { com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.viewer.ThreadViewer.displayNumDirectReplies(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener)",viewHolder,cursor,showContextMenu,repliesOnClickListener);try{/*// ,*/
        /*// boolean markVisibility) {*/
        View wrapper = viewHolder.list_item_num_direct_replies;
        if (wrapper == null)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.viewer.ThreadViewer.displayNumDirectReplies(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener)");return 0;}}
        if (!showContextMenu) {
            wrapper.setVisibility(View.GONE);
            {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.viewer.ThreadViewer.displayNumDirectReplies(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener)");return 0;}
        }
        TextView numDirectReplies = viewHolder.list_item_num_direct_replies_text;
        if (numDirectReplies == null)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.viewer.ThreadViewer.displayNumDirectReplies(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener)");return 0;}}

        int directReplies = numDirectReplies(cursor);
        FontSize.sizeTextView(numDirectReplies);
        numDirectReplies.setText(String.valueOf(directReplies));
        if (directReplies > 0) {
            wrapper.setOnClickListener(repliesOnClickListener);
            wrapper.setVisibility(View.VISIBLE);
        } else {
            wrapper.setOnClickListener(null);
            wrapper.setVisibility(View.GONE);
        }
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.viewer.ThreadViewer.displayNumDirectReplies(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener)");return directReplies;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.viewer.ThreadViewer.displayNumDirectReplies(android.text.style.ThreadViewHolder,android.database.Cursor,boolean,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    static protected int numDirectReplies(Cursor cursor) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.viewer.ThreadViewer.numDirectReplies(android.database.Cursor)",cursor);try{byte[] b = cursor.getBlob(cursor
                .getColumnIndex(ChanPost.POST_REPLIES_BLOB));
        if (b == null || b.length == 0)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.viewer.ThreadViewer.numDirectReplies(android.database.Cursor)");return 0;}}
        HashSet<?> links = ChanPost.parseBlob(b);
        if (links == null || links.size() <= 0)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.viewer.ThreadViewer.numDirectReplies(android.database.Cursor)");return 0;}}
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.viewer.ThreadViewer.numDirectReplies(android.database.Cursor)");return links.size();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.viewer.ThreadViewer.numDirectReplies(android.database.Cursor)",throwable);throw throwable;}
    }

    static private boolean setHeaderValue(ThreadViewHolder viewHolder,
            final Cursor cursor,
            /*// View.OnClickListener repliesOnClickListener,*/
            View.OnClickListener sameIdOnClickListener) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderValue(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener)",viewHolder,cursor,sameIdOnClickListener);try{TextView tv = viewHolder.list_item_header;
        if (tv == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderValue(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener)");return false;}}
        String text = cursor.getString(cursor
                .getColumnIndex(ChanPost.POST_HEADLINE_TEXT));
        if (text == null || text.isEmpty()) {
            tv.setVisibility(View.GONE);
            tv.setText("");
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderValue(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener)");return true;}
        }
        /*// if (repliesOnClickListener != null || sameIdOnClickListener != null)*/
        /*// tv.setMovementMethod(LinkMovementMethod.getInstance());*/
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(
                Html.fromHtml(text));
        /*// if (repliesOnClickListener != null)*/
        /*// addLinkedSpans(spannable, REPLY_PATTERN, repliesOnClickListener);*/
        if (cursor.getBlob(cursor.getColumnIndex(ChanPost.POST_SAME_IDS_BLOB)) != null
                && sameIdOnClickListener != null) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            addLinkedSpans(spannable, ID_PATTERN, sameIdOnClickListener);
        }
        FontSize.sizeTextView(tv);
        tv.setText(spannable);
        tv.setVisibility(View.VISIBLE);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderValue(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderValue(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    static private boolean setSubject(ThreadViewHolder viewHolder,
            final Cursor cursor, int flags,
            SpannableOnClickListener backlinkOnClickListener) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.setSubject(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.SpannableOnClickListener)",viewHolder,cursor,flags,backlinkOnClickListener);try{TextView tv = viewHolder.list_item_subject;
        if (tv == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setSubject(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.SpannableOnClickListener)");return false;}}
        if ((flags & ChanPost.FLAG_HAS_SUBJECT) == 0) {
            tv.setText("");
            tv.setVisibility(View.GONE);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setSubject(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.SpannableOnClickListener)");return true;}
        }
        String text = cursor.getString(cursor
                .getColumnIndex(ChanPost.POST_SUBJECT_TEXT));
        if (DEBUG)
            {Log.v(TAG, "setSubject text=" + text);}
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(
                Html.fromHtml(text, null, spoilerTagHandler));
        if (spannable.length() > 0) {
            if ((flags & ChanPost.FLAG_IS_HEADER) > 0) {
                tv.setTypeface(subjectTypeface);
            } else {
                FontSize.sizeTextView(tv);
            }
            tv.setText(spannable);
            if (backlinkOnClickListener != null) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                addLinkedSpans(spannable, POST_PATTERN, backlinkOnClickListener);
            }
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setText("");
            tv.setVisibility(View.GONE);
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setSubject(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.SpannableOnClickListener)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.setSubject(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.SpannableOnClickListener)",throwable);throw throwable;}
    }

    static public boolean setSubjectIcons(ThreadViewHolder viewHolder, int flags) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.setSubjectIcons(android.text.style.ThreadViewHolder,int)",viewHolder,flags);try{if (viewHolder.list_item_dead_icon != null)
            {viewHolder.list_item_dead_icon
                    .setVisibility((flags & ChanPost.FLAG_IS_DEAD) > 0 ? View.VISIBLE
                            : View.GONE);}
        if (viewHolder.list_item_closed_icon != null)
            {viewHolder.list_item_closed_icon
                    .setVisibility((flags & ChanPost.FLAG_IS_CLOSED) > 0 ? View.VISIBLE
                            : View.GONE);}
        if (viewHolder.list_item_sticky_icon != null)
            {viewHolder.list_item_sticky_icon
                    .setVisibility((flags & ChanPost.FLAG_IS_STICKY) > 0 ? View.VISIBLE
                            : View.GONE);}
        if (DEBUG)
            {Log.d(TAG, "setSubjectIcons()" + " dead="
                    + ((flags & ChanPost.FLAG_IS_DEAD) > 0) + " closed="
                    + ((flags & ChanPost.FLAG_IS_CLOSED) > 0) + " sticky="
                    + ((flags & ChanPost.FLAG_IS_STICKY) > 0));}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setSubjectIcons(android.text.style.ThreadViewHolder,int)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.setSubjectIcons(android.text.style.ThreadViewHolder,int)",throwable);throw throwable;}
    }

    static private boolean setText(ThreadViewHolder viewHolder,
            final Cursor cursor, int flags,
            final SpannableOnClickListener backlinkOnClickListener,
            final View.OnClickListener exifOnClickListener) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.setText(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener)",viewHolder,cursor,flags,backlinkOnClickListener,exifOnClickListener);try{TextView tv = viewHolder.list_item_text;
        if (tv == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setText(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener)");return false;}}
        if ((flags & (ChanPost.FLAG_HAS_TEXT | ChanPost.FLAG_HAS_EXIF)) == 0) {
            tv.setVisibility(View.GONE);
            tv.setText("");
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setText(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener)");return true;}
        }

        String text = cursor.getString(cursor
                .getColumnIndex(ChanPost.POST_TEXT));
        String exifText = tv.getContext().getResources()
                .getString(R.string.exif);
        if ((flags & ChanPost.FLAG_HAS_EXIF) > 0 && exifOnClickListener != null)
            {text += (text.isEmpty() ? "" : " ") + "<b>" + exifText + "</b>";}

        String html = markupHtml(text);
        /*// if (DEBUG) Log.i(TAG, "text before replace:" + text);*/
        /*// if (DEBUG) Log.i(TAG, "text after  replace:" + html);*/
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(
                Html.fromHtml(html, null, spoilerTagHandler));
        if (spannable.length() == 0) {
            tv.setVisibility(View.GONE);
            tv.setText("");
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setText(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener)");return true;}
        }

        if (backlinkOnClickListener != null || exifOnClickListener != null)
            {tv.setMovementMethod(LinkMovementMethod.getInstance());}
        if ((flags & ChanPost.FLAG_HAS_EXIF) > 0 && exifOnClickListener != null)
            {addExifSpan(tv, spannable, exifOnClickListener);}
        if (backlinkOnClickListener != null)
            {addLinkedSpans(spannable, POST_PATTERN, backlinkOnClickListener);}

        /*// if (DEBUG) Log.v(TAG, "setText spannable=" + spannable + " len=" +*/
        /*// spannable.length());*/
        FontSize.sizeTextView(tv);
        tv.setText(spannable);
        tv.setVisibility(View.VISIBLE);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setText(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.setText(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.SpannableOnClickListener,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    static private void addExifSpan(TextView tv, Spannable spannable,
            final View.OnClickListener listener) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.addExifSpan(android.text.style.TextView,android.text.style.Spannable,android.text.style.View.OnClickListener)",tv,spannable,listener);try{if (listener == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.addExifSpan(android.text.style.TextView,android.text.style.Spannable,android.text.style.View.OnClickListener)");return;}}
        if (tv == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.addExifSpan(android.text.style.TextView,android.text.style.Spannable,android.text.style.View.OnClickListener)");return;}}
        ClickableSpan exif = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$1.onClick(android.view.View)",this,widget);try{listener.onClick(widget);com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$1.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$1.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        };
        String exifText = tv.getContext().getResources()
                .getString(R.string.exif);
        spannable.setSpan(exif, spannable.length() - exifText.length(),
                spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.addExifSpan(android.text.style.TextView,android.text.style.Spannable,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    static private final String QUOTE_RE = "((?<!<b|/|<br|u|>>|^>)>[^<>]+?(?=<br/?>)|(?<!<b|/|<br|u|>>|^>)>[^<>]+)";
    static private final String QUOTE_RE_REPLACE = "<font color=\"#7a9441\">$1</font>"; /*// #7a9441*/
    static private final Pattern POST_PATTERN = Pattern
            .compile("(>>(\\d+))( \\(OP\\))?");
    /*// static private final Pattern REPLY_PATTERN =*/
    /*// Pattern.compile("(1 Reply|\\d+ Replies)");*/
    static private final Pattern ID_PATTERN = Pattern
            .compile("Id: ([A-Za-z0-9+./_:!-]+)");

    static private void addLinkedSpans(Spannable spannable, Pattern pattern,
            final View.OnClickListener listener) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.addLinkedSpans(android.text.style.Spannable,java.util.regex.Pattern,android.text.style.View.OnClickListener)",spannable,pattern,listener);try{if (listener == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.addLinkedSpans(android.text.style.Spannable,java.util.regex.Pattern,android.text.style.View.OnClickListener)");return;}}
        Matcher m = pattern.matcher(spannable);
        while (m.find()) {
            ClickableSpan popup = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$2.onClick(android.view.View)",this,widget);try{listener.onClick(widget);com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$2.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$2.onClick(android.view.View)",this,throwable);throw throwable;}
                }
            };
            spannable.setSpan(popup, m.start(1), m.end(1),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.addLinkedSpans(android.text.style.Spannable,java.util.regex.Pattern,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    static private void addLinkedSpans(Spannable spannable, Pattern pattern,
            final SpannableOnClickListener listener) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.addLinkedSpans(android.text.style.Spannable,java.util.regex.Pattern,android.text.style.SpannableOnClickListener)",spannable,pattern,listener);try{if (listener == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.addLinkedSpans(android.text.style.Spannable,java.util.regex.Pattern,android.text.style.SpannableOnClickListener)");return;}}
        Matcher m = pattern.matcher(spannable);
        while (m.find()) {
            String post = m.group(2);
            long postNo = -1;
            try {
                postNo = Long.parseLong(post);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Exception parsing long: " + post);
            }
            final long finalPostNo = postNo;
            ClickableSpan popup = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$3.onClick(android.view.View)",this,widget);try{listener.onClick(widget, finalPostNo);com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$3.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$3.onClick(android.view.View)",this,throwable);throw throwable;}
                }
            };
            spannable.setSpan(popup, m.start(1), m.end(1),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.addLinkedSpans(android.text.style.Spannable,java.util.regex.Pattern,android.text.style.SpannableOnClickListener)",throwable);throw throwable;}
    }

    static public String markupHtml(String in) {
        com.mijack.Xlog.logStaticMethodEnter("android.text.style.String com.chanapps.four.viewer.ThreadViewer.markupHtml(android.text.style.String)",in);try{com.mijack.Xlog.logStaticMethodExit("android.text.style.String com.chanapps.four.viewer.ThreadViewer.markupHtml(android.text.style.String)");return in.replaceAll(QUOTE_RE, QUOTE_RE_REPLACE);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.text.style.String com.chanapps.four.viewer.ThreadViewer.markupHtml(android.text.style.String)",throwable);throw throwable;}
    }

    static private boolean setImageExifValue(ThreadViewHolder viewHolder) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.setImageExifValue(android.text.style.ThreadViewHolder)",viewHolder);try{TextView tv = viewHolder.list_item_exif_text;
        if (tv == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setImageExifValue(android.text.style.ThreadViewHolder)");return false;}}
        FontSize.sizeTextView(tv);
        tv.setText("");
        tv.setVisibility(View.GONE);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setImageExifValue(android.text.style.ThreadViewHolder)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.setImageExifValue(android.text.style.ThreadViewHolder)",throwable);throw throwable;}
    }

    /*
     * static private boolean setImageWrapper(ThreadViewHolder viewHolder, final
     * int flags) { View v = viewHolder.list_item_image_wrapper; if (v == null)
     * return false; if ((flags & ChanPost.FLAG_HAS_IMAGE) > 0) {
     * v.setOnClickListener(new View.OnClickListener() {
     * 
     * @Override public void onClick(View v) { toggleExpandedImage(viewHolder);
     * } }); v.setVisibility(View.VISIBLE); } else { v.setVisibility(View.GONE);
     * } return true; }
     */

    static private boolean setHeaderImage(ThreadViewHolder viewHolder,
            final Cursor cursor, int flags,
            View.OnClickListener expandedImageListener, boolean showContextMenu) {
  com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)",viewHolder,cursor,flags,expandedImageListener,showContextMenu);try{      if (viewHolder.list_item_image_header == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)");return false;}}
        if (!SettingsActivity
                .shouldLoadThumbs(viewHolder.list_item_image_header
                        .getContext()))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)");return showExpandableThumb(viewHolder, viewHolder.list_item_image);}}
        displayHeaderImage(viewHolder, cursor, flags, false, showContextMenu); /*// make*/
                                                                               /*// sure*/
                                                                               /*// it's*/
                                                                               /*// always*/
                                                                               /*// displayed*/
        if (displayCachedExpandedImage(viewHolder, cursor,
                expandedImageListener, showContextMenu)) {
            viewHolder.list_item_image_header.setVisibility(View.GONE);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)");return true;}
        }
        boolean isDead = (flags & ChanPost.FLAG_IS_DEAD) > 0;
        if (!isDead
                && prefetchExpandedImage(viewHolder, cursor,
                        expandedImageListener, showContextMenu)) {
            viewHolder.list_item_image_header.setVisibility(View.GONE);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)");return true;}
        }
        viewHolder.list_item_image_header.setVisibility(View.VISIBLE);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.setHeaderImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)",throwable);throw throwable;}
    }

    static private boolean showExpandableThumb(ThreadViewHolder viewHolder,
            ImageView thumb) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.showExpandableThumb(android.text.style.ThreadViewHolder,android.text.style.ImageView)",viewHolder,thumb);try{if (viewHolder.list_item_image_expanded_wrapper != null)
            {viewHolder.list_item_image_expanded_wrapper
                    .setVisibility(View.GONE);}
        thumb.setVisibility(View.GONE);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.showExpandableThumb(android.text.style.ThreadViewHolder,android.text.style.ImageView)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.showExpandableThumb(android.text.style.ThreadViewHolder,android.text.style.ImageView)",throwable);throw throwable;}
    }

    static private boolean setImage(ThreadViewHolder viewHolder,
            final Cursor cursor, int flags,
            View.OnClickListener expandedImageListener, boolean showContextMenu) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.setImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)",viewHolder,cursor,flags,expandedImageListener,showContextMenu);try{if (viewHolder.list_item_image == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)");return false;}}
        /*// setSpinnerTarget(viewHolder.list_item_image_spinner,*/
        /*// expandedImageListener, showContextMenu);*/
        if (!SettingsActivity.shouldLoadThumbs(viewHolder.list_item_image
                .getContext()))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)");return showExpandableThumb(viewHolder, viewHolder.list_item_image);}}
        /*// display thumb and also expand if available*/
        displayNonHeaderImage(viewHolder.list_item_image,
                viewHolder.list_item_image_expansion_target, cursor);
        if (displayCachedExpandedImage(viewHolder, cursor,
                expandedImageListener, showContextMenu))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)");return true;}}
        boolean isDead = (flags & ChanPost.FLAG_IS_DEAD) > 0;
        if (!isDead
                && prefetchExpandedImage(viewHolder, cursor,
                        expandedImageListener, showContextMenu))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)");return true;}}
        /*
         * we haven't expanded the image at this point, so collapse if it's
         * still being shown from previous view
         */
        if (viewHolder.list_item_image_expanded_wrapper != null)
            {viewHolder.list_item_image_expanded_wrapper
                    .setVisibility(View.GONE);}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.setImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,android.text.style.View.OnClickListener,boolean)",throwable);throw throwable;}
    }

    static private void setSpinnerTarget(ImageView spinner,
            View.OnClickListener expandedImageListener, boolean showContextMenu) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.setSpinnerTarget(android.text.style.ImageView,android.text.style.View.OnClickListener,boolean)",spinner,expandedImageListener,showContextMenu);try{if (spinner == null || expandedImageListener == null
                || !showContextMenu) {
            if (spinner != null)
                {spinner.setVisibility(View.GONE);}
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.setSpinnerTarget(android.text.style.ImageView,android.text.style.View.OnClickListener,boolean)");return;}
        }
        spinner.setOnClickListener(expandedImageListener);
        spinner.setVisibility(View.VISIBLE);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.setSpinnerTarget(android.text.style.ImageView,android.text.style.View.OnClickListener,boolean)",throwable);throw throwable;}
    }

    static private void sizeView(View view, Point imageSize) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.sizeView(android.view.View,android.graphics.Point)",view,imageSize);try{ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.width = imageSize.x;
            params.height = imageSize.y;
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.sizeView(android.view.View,android.graphics.Point)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.sizeView(android.view.View,android.graphics.Point)",throwable);throw throwable;}
    }

    static private Point sizeHeaderImage(Cursor cursor, boolean showContextMenu) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Point com.chanapps.four.viewer.ThreadViewer.sizeHeaderImage(android.database.Cursor,boolean)",cursor,showContextMenu);try{int tn_w = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_TN_W));
        int tn_h = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_TN_H));
        int flags = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_FLAGS));
        if ((flags & ChanPost.FLAG_HAS_SPOILER) > 0 || tn_w <= 0 || tn_h <= 0) { /*// don't*/
                                                                                 /*// size*/
                                                                                 /*// based*/
                                                                                 /*// on*/
                                                                                 /*// hidden*/
                                                                                 /*// image,*/
                                                                                 /*// size*/
                                                                                 /*// based*/
                                                                                 /*// on*/
                                                                                 /*// filler*/
                                                                                 /*// image*/
            tn_w = 250;
            tn_h = 250;
        }
        /*// if (tn_w == 0 || tn_h == 0) // we don't have height and width, so*/
        /*// just show unscaled image*/
        /*// return displayHeaderImageAtDefaultSize(iv, url);*/
        /*// scale image*/
        /*// if ((flags & ChanPost.FLAG_IS_HEADER) > 0) {*/
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Point com.chanapps.four.viewer.ThreadViewer.sizeHeaderImage(android.database.Cursor,boolean)");return sizeHeaderImage(tn_w, tn_h, showContextMenu);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Point com.chanapps.four.viewer.ThreadViewer.sizeHeaderImage(android.database.Cursor,boolean)",throwable);throw throwable;}
    }

    static private boolean displayHeaderImage(ThreadViewHolder viewHolder,
            Cursor cursor, int flags, boolean visible, boolean showContextMenu) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.displayHeaderImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,boolean,boolean)",viewHolder,cursor,flags,visible,showContextMenu);try{Point imageSize = sizeHeaderImage(cursor, showContextMenu);
        sizeView(viewHolder.list_item_image_header, imageSize);
        ThreadImageExpander.setImageDimensions(viewHolder, imageSize);
        if (DEBUG)
            {Log.i(TAG, "displayHeaderImage() size=" + imageSize.x + "x"
                    + imageSize.y);}
        /*// }*/
        /*// else {*/
        /*// imageSize = sizeItemImage(tn_w, tn_h);*/
        /*// if (params != null) {*/
        /*// params.width = imageSize.x;*/
        /*// params.height = imageSize.y;*/
        /*// }*/
        /*// }*/
        ImageSize displayImageSize = new ImageSize(imageSize.x, imageSize.y);
        DisplayImageOptions options = createExpandedDisplayImageOptions(displayImageSize);

        /*// display image*/
        viewHolder.list_item_image_expanded_wrapper.setVisibility(View.VISIBLE);
        if (visible)
            {viewHolder.list_item_image_header.setVisibility(View.VISIBLE);}
        /*// ImageLoadingListener listener = ((flags & ChanPost.FLAG_IS_AD) > 0) ?*/
        /*// adImageLoadingListener : null;*/
        String url = cursor.getString(cursor
                .getColumnIndex(ChanPost.POST_IMAGE_URL));
        if (DEBUG)
            {Log.w(TAG, "displayHeaderImage() url=" + url);}
        /*// imageLoader.displayImage(url, iv, options, listener);*/
        imageLoader.displayImage(url, viewHolder.list_item_image_header,
                options, visible ? thumbLoadingListener
                        : invisibleThumbLoadingListener);

        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.displayHeaderImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,boolean,boolean)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.displayHeaderImage(android.text.style.ThreadViewHolder,android.database.Cursor,int,boolean,boolean)",throwable);throw throwable;}
    }

    /*
     * static private boolean hideNoImage(ThreadViewHolder viewHolder, int
     * flags) { if ((flags & ChanPost.FLAG_HAS_IMAGE) == 0) {
     * toggleExpandedImage(viewHolder); return true; } else {
     * iv.setVisibility(View.VISIBLE); if (spinner != null)
     * spinner.setVisibility(View.VISIBLE); return false; } }
     */

    static private boolean prefetchExpandedImage(ThreadViewHolder viewHolder,
            final Cursor cursor,
            final View.OnClickListener expandedImageListener,
            boolean showContextMenu) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.prefetchExpandedImage(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,boolean)",viewHolder,cursor,expandedImageListener,showContextMenu);try{if (viewHolder.list_item == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.prefetchExpandedImage(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,boolean)");return false;}}
        if (!shouldAutoload(viewHolder.list_item.getContext(), cursor))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.prefetchExpandedImage(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,boolean)");return false;}}
        ThreadImageExpander expander = (new ThreadImageExpander(viewHolder,
                cursor, true, stub, expandedImageListener, showContextMenu));
        expander.displayImage();
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.prefetchExpandedImage(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,boolean)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.prefetchExpandedImage(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,boolean)",throwable);throw throwable;}
    }

    static private boolean shouldAutoload(Context context, final Cursor cursor) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.shouldAutoload(android.content.Context,android.database.Cursor)",context,cursor);try{SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String autoloadType = prefs.getString(
                SettingsActivity.PREF_AUTOLOAD_IMAGES,
                context.getString(R.string.pref_autoload_images_auto_value));
        long resto = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_RESTO));
        if (context.getString(R.string.pref_autoload_images_nothumbs_value)
                .equals(autoloadType))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.shouldAutoload(android.content.Context,android.database.Cursor)");return false;}}
        else if (context.getString(R.string.pref_autoload_images_never_value)
                .equals(autoloadType))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.shouldAutoload(android.content.Context,android.database.Cursor)");return false;}}
        else if (context.getString(R.string.pref_autoload_images_always_value)
                .equals(autoloadType))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.shouldAutoload(android.content.Context,android.database.Cursor)");return true;}}
        /*// else if*/
        /*// (context.getString(R.string.pref_autoload_images_auto_value).equals(autoloadType))*/
        /*// return shouldAutoloadBySizeAndNetwork(cursor);*/
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.shouldAutoload(android.content.Context,android.database.Cursor)");return shouldAutoloadBySizeTypeAndNetwork(cursor);}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.shouldAutoload(android.content.Context,android.database.Cursor)",throwable);throw throwable;}
    }

    static private boolean shouldAutoloadBySizeTypeAndNetwork(
            final Cursor cursor) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.shouldAutoloadBySizeTypeAndNetwork(android.database.Cursor)",cursor);try{/*// int fsize =*/
        /*// cursor.getInt(cursor.getColumnIndex(ChanPost.POST_FSIZE));*/
        /*// int maxAutoloadFSize =*/
        /*// NetworkProfileManager.instance().getCurrentProfile().getFetchParams().maxAutoLoadFSize;*/
        /*// if (DEBUG) Log.i(TAG,*/
        /*// "prefetchExpandedImage auto-expanding since fsize=" + fsize + " < " +*/
        /*// maxAutoloadFSize);*/
        /*// return (fsize <= maxAutoloadFSize);*/
        boolean onWifi = NetworkProfileManager.instance().getCurrentProfile()
                .getConnectionType() == NetworkProfile.Type.WIFI;
        boolean isVideo = isVideo(cursor);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.shouldAutoloadBySizeTypeAndNetwork(android.database.Cursor)");return onWifi || isVideo;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.shouldAutoloadBySizeTypeAndNetwork(android.database.Cursor)",throwable);throw throwable;}
    }

    static private boolean isVideo(final Cursor cursor) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.isVideo(android.database.Cursor)",cursor);try{String postExt = cursor.getString(cursor
                .getColumnIndex(ChanThread.POST_EXT));
        int fsize = cursor.getInt(cursor.getColumnIndex(ChanThread.POST_FSIZE));
        int postW = cursor.getInt(cursor.getColumnIndex(ChanThread.POST_W));
        int postH = cursor.getInt(cursor.getColumnIndex(ChanThread.POST_H));
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.isVideo(android.database.Cursor)");return ChanImage.isVideo(postExt, fsize, postW, postH);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.isVideo(android.database.Cursor)",throwable);throw throwable;}
    }

    static private boolean displayCachedExpandedImage(
            ThreadViewHolder viewHolder, final Cursor cursor,
            final View.OnClickListener expandedImageListener,
            boolean showContextMenu) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.displayCachedExpandedImage(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,boolean)",viewHolder,cursor,expandedImageListener,showContextMenu);try{File file = fullSizeImageFile(viewHolder.list_item.getContext(), cursor); /*// try*/
                                                                                  /*// for*/
                                                                                  /*// full*/
                                                                                  /*// size*/
                                                                                  /*// first*/
        if (file == null) {
            /*// toggleExpandedImage(viewHolder);*/
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.displayCachedExpandedImage(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,boolean)");return false;}
        }

        if (DEBUG)
            {Log.i(TAG,
                    "displayCachedExpandedImage() expanded file="
                            + file.getAbsolutePath());}
        ThreadImageExpander expander = (new ThreadImageExpander(viewHolder,
                cursor, false, stub, expandedImageListener, showContextMenu));
        expander.displayImage();
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.displayCachedExpandedImage(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,boolean)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.displayCachedExpandedImage(android.text.style.ThreadViewHolder,android.database.Cursor,android.text.style.View.OnClickListener,boolean)",throwable);throw throwable;}
    }

    static private void bindThumbnailExpandTarget(final View wrapper,
            View.OnClickListener thumbOnClickListener) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.bindThumbnailExpandTarget(android.view.View,android.text.style.View.OnClickListener)",wrapper,thumbOnClickListener);try{if (wrapper == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.bindThumbnailExpandTarget(android.view.View,android.text.style.View.OnClickListener)");return;}}
        if (thumbOnClickListener != null) {
            wrapper.setOnClickListener(thumbOnClickListener);
            if (wrapper instanceof FrameLayout)
                {((FrameLayout) wrapper).setForeground(wrapper.getResources()
                        .getDrawable(R.drawable.thread_list_selector_bg));}
        } else {
            wrapper.setOnClickListener(null);
            if (wrapper instanceof FrameLayout)
                {((FrameLayout) wrapper).setForeground(wrapper.getResources()
                        .getDrawable(R.drawable.null_selector_bg));}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.bindThumbnailExpandTarget(android.view.View,android.text.style.View.OnClickListener)",throwable);throw throwable;}
    }

    static private boolean displayNonHeaderImage(final ImageView iv,
            final ViewGroup wrapper, final Cursor cursor) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.displayNonHeaderImage(android.text.style.ImageView,android.view.ViewGroup,android.database.Cursor)",iv,wrapper,cursor);try{String url = cursor.getString(cursor
                .getColumnIndex(ChanPost.POST_IMAGE_URL));
        if (url != null && url.equals(iv.getTag(R.id.IMG_URL))) {
            iv.setVisibility(View.VISIBLE);
        } else if (url != null && !url.isEmpty()) {
            if (DEBUG)
                {Log.i(TAG, "setImage url=" + url);}
            iv.setVisibility(View.GONE);
            imageLoader.displayImage(url, iv, thumbDisplayImageOptions,
                    thumbLoadingListener);
        } else {
            if (DEBUG)
                {Log.i(TAG, "setImage null image");}
            iv.setVisibility(View.GONE);
            iv.setImageBitmap(null);
            if (wrapper != null) {
                wrapper.setOnClickListener(null);
                if (wrapper instanceof FrameLayout)
                    {((FrameLayout) wrapper).setForeground(wrapper
                            .getResources().getDrawable(
                                    R.drawable.null_selector_bg));}
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.displayNonHeaderImage(android.text.style.ImageView,android.view.ViewGroup,android.database.Cursor)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.displayNonHeaderImage(android.text.style.ImageView,android.view.ViewGroup,android.database.Cursor)",throwable);throw throwable;}
    }

    static private ImageLoadingListener invisibleThumbLoadingListener = new ImageLoadingListener() {
        {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$4.onLoadingStarted(android.text.style.String,android.view.View)",this,imageUri,view);com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$4.onLoadingStarted(android.text.style.String,android.view.View)",this);}

        @Override
        public void onLoadingFailed(String imageUri, View view,
                FailReason failReason) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$4.onLoadingFailed(android.text.style.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this,imageUri,view,failReason);try{if (view != null)
                {view.setTag(R.id.IMG_URL, null);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$4.onLoadingFailed(android.text.style.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$4.onLoadingFailed(android.text.style.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this,throwable);throw throwable;}
        }

        @Override
        public void onLoadingComplete(String imageUri, View view,
                Bitmap loadedImage) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$4.onLoadingComplete(android.text.style.String,android.view.View,android.graphics.Bitmap)",this,imageUri,view,loadedImage);try{if (view != null) {
                view.setTag(R.id.IMG_URL, imageUri);
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$4.onLoadingComplete(android.text.style.String,android.view.View,android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$4.onLoadingComplete(android.text.style.String,android.view.View,android.graphics.Bitmap)",this,throwable);throw throwable;}
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$4.onLoadingCancelled(android.text.style.String,android.view.View)",this,imageUri,view);try{if (view != null)
                {view.setTag(R.id.IMG_URL, null);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$4.onLoadingCancelled(android.text.style.String,android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$4.onLoadingCancelled(android.text.style.String,android.view.View)",this,throwable);throw throwable;}
        }
    };
    static private ImageLoadingListener thumbLoadingListener = new ImageLoadingListener() {
        {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$5.onLoadingStarted(android.text.style.String,android.view.View)",this,imageUri,view);com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$5.onLoadingStarted(android.text.style.String,android.view.View)",this);}

        @Override
        public void onLoadingFailed(String imageUri, View view,
                FailReason failReason) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$5.onLoadingFailed(android.text.style.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this,imageUri,view,failReason);try{if (view != null)
                {view.setTag(R.id.IMG_URL, null);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$5.onLoadingFailed(android.text.style.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$5.onLoadingFailed(android.text.style.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this,throwable);throw throwable;}
        }

        @Override
        public void onLoadingComplete(String imageUri, View view,
                Bitmap loadedImage) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$5.onLoadingComplete(android.text.style.String,android.view.View,android.graphics.Bitmap)",this,imageUri,view,loadedImage);try{if (view != null) {
                view.setTag(R.id.IMG_URL, imageUri);
                view.setVisibility(View.VISIBLE);
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$5.onLoadingComplete(android.text.style.String,android.view.View,android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$5.onLoadingComplete(android.text.style.String,android.view.View,android.graphics.Bitmap)",this,throwable);throw throwable;}
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$5.onLoadingCancelled(android.text.style.String,android.view.View)",this,imageUri,view);try{if (view != null)
                {view.setTag(R.id.IMG_URL, null);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$5.onLoadingCancelled(android.text.style.String,android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$5.onLoadingCancelled(android.text.style.String,android.view.View)",this,throwable);throw throwable;}
        }
    };

    /*
     * static private boolean displayHeaderImageAtDefaultSize(final ImageView
     * iv, String url) { ViewGroup.LayoutParams params = iv.getLayoutParams();
     * if (params != null) { params.width = itemThumbWidth; params.height =
     * itemThumbMaxHeight; } iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
     * iv.setVisibility(View.VISIBLE); imageLoader.displayImage(url, iv,
     * expandedDisplayImageOptions); return true; }
     */

    public static Point sizeHeaderImage(final int tn_w, final int tn_h,
            boolean showContextMenu) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Point com.chanapps.four.viewer.ThreadViewer.sizeHeaderImage(int,int,boolean)",tn_w,tn_h,showContextMenu);try{Point baseBox = new Point(tn_w, tn_h);
        Point scaledBox = new Point((int) (tn_w * MAX_HEADER_SCALE),
                (int) (tn_h * MAX_HEADER_SCALE));
        Point cardBox = new Point(cardMaxImageWidth(showContextMenu),
                cardMaxImageHeight(showContextMenu));
        /*// baseBox <= scaleBox <= cardBox;*/

        /*// Point scaledBox = new Point(baseBox.x, baseBox.y);*/
        if (scaledBox.x > cardBox.x) { /*// downscale to fix x in card*/
            double scale = (double) cardBox.x / (double) scaledBox.x;
            scaledBox.x = (int) (scale * scaledBox.x);
            scaledBox.y = (int) (scale * scaledBox.y);
        }
        if (scaledBox.y > cardBox.y) { /*// downscale to fit y in card*/
            double scale = (double) cardBox.y / (double) scaledBox.y;
            scaledBox.x = (int) (scale * scaledBox.x);
            scaledBox.y = (int) (scale * scaledBox.y);
        }

        /*
         * Point imageSize = new Point(); double aspectRatio = (double) tn_w /
         * (double) tn_h; if (aspectRatio < 1) { // tall image, restrict by
         * height double desiredHeight = Math.min(cardMaxImageHeight(), (tn_h *
         * MAX_HEADER_SCALE)); // prevent excessive scaling imageSize.x =
         * (int)(aspectRatio * desiredHeight); imageSize.y = (int)desiredHeight;
         * } else { double desiredWidth = Math.min(cardMaxImageWidth(), (tn_w *
         * MAX_HEADER_SCALE)); // prevent excessive scaling imageSize.x =
         * (int)desiredWidth; // restrict by width normally imageSize.y =
         * (int)(desiredWidth / aspectRatio); }
         */

        if (DEBUG)
            {Log.v(TAG, "Image size input=" + tn_w + "x" + tn_h + " box="
                    + cardBox.x + "x" + cardBox.y + " output=" + scaledBox.x
                    + "x" + scaledBox.y);}
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Point com.chanapps.four.viewer.ThreadViewer.sizeHeaderImage(int,int,boolean)");return scaledBox;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Point com.chanapps.four.viewer.ThreadViewer.sizeHeaderImage(int,int,boolean)",throwable);throw throwable;}
    }

    public static int cardMaxImageWidth(boolean showContextMenu) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.viewer.ThreadViewer.cardMaxImageWidth(boolean)",showContextMenu);try{int naiveMax;
        /*// if (displayMetrics.widthPixels < displayMetrics.heightPixels) //*/
        /*// portrait*/
        naiveMax = displayMetrics.widthPixels - cardPaddingPx - cardPaddingPx;
        /*// else // landscape*/
        /*// naiveMax = displayMetrics.widthPixels - cardPaddingPx -*/
        /*// cardPaddingPx;*/
        /*// naiveMax = displayMetrics.widthPixels / 2 - cardPaddingPx -*/
        /*// cardPaddingPx;*/
        naiveMax -= boardTabletViewWidthPx;
        if (!showContextMenu) /*// when on fragment*/
            {naiveMax -= fragmentMarginWidthPx;}
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.viewer.ThreadViewer.cardMaxImageWidth(boolean)");return naiveMax;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.viewer.ThreadViewer.cardMaxImageWidth(boolean)",throwable);throw throwable;}
    }

    public static int cardMaxImageHeight(boolean showContextMenu) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.viewer.ThreadViewer.cardMaxImageHeight(boolean)",showContextMenu);try{int naiveMax;
        /*// if (displayMetrics.widthPixels < displayMetrics.heightPixels) //*/
        /*// portrait*/
        naiveMax = displayMetrics.heightPixels - cardPaddingPx - cardPaddingPx
                - actionBarHeightPx;
        /*// else // landscape*/
        /*// naiveMax = displayMetrics.heightPixels - cardPaddingPx -*/
        /*// cardPaddingPx;*/
        /*// naiveMax = displayMetrics.heightPixels - cardPaddingPx -*/
        /*// cardPaddingPx;*/
        /*// naiveMax -= boardTabletViewWidthPx;*/
        if (!showContextMenu) /*// when on fragment*/
            {naiveMax -= fragmentMarginHeightPx;}
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.viewer.ThreadViewer.cardMaxImageHeight(boolean)");return naiveMax;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.viewer.ThreadViewer.cardMaxImageHeight(boolean)",throwable);throw throwable;}
    }

    /*
     * public static Point sizeItemImage(int tn_w, int tn_h) { Point imageSize =
     * new Point(); double aspectRatio = (double) tn_w / (double) tn_h; if
     * (aspectRatio < 0.5) { // tall image, restrict by height imageSize.x =
     * (int) (aspectRatio * (double) itemThumbMaxHeight); imageSize.y =
     * itemThumbMaxHeight; } else { imageSize.x = itemThumbWidth; // restrict by
     * width normally imageSize.y = (int) ((double) itemThumbWidth /
     * aspectRatio); } //if (DEBUG) Log.i(TAG, "Item Input size=" + tn_w + "x" +
     * tn_h + " output size=" + imageSize.x + "x" + imageSize.y); return
     * imageSize; }
     */

    static private boolean setCountryFlag(ThreadViewHolder viewHolder,
            final Cursor cursor, int flags) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.setCountryFlag(android.text.style.ThreadViewHolder,android.database.Cursor,int)",viewHolder,cursor,flags);try{ImageView iv = viewHolder.list_item_country_flag;
        if (iv == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setCountryFlag(android.text.style.ThreadViewHolder,android.database.Cursor,int)");return false;}}
        if ((flags & ChanPost.FLAG_HAS_COUNTRY) > 0) {
            iv.setImageBitmap(null);
            iv.setVisibility(View.VISIBLE);
            String url = cursor.getString(cursor
                    .getColumnIndex(ChanPost.POST_COUNTRY_URL));
            imageLoader.displayImage(url, iv, expandedDisplayImageOptions);
        } else {
            iv.setImageBitmap(null);
            iv.setVisibility(View.GONE);
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.setCountryFlag(android.text.style.ThreadViewHolder,android.database.Cursor,int)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.setCountryFlag(android.text.style.ThreadViewHolder,android.database.Cursor,int)",throwable);throw throwable;}
    }

    static private final Html.TagHandler spoilerTagHandler = new Html.TagHandler() {
        static private final String SPOILER_TAG = "s";

        class SpoilerSpan extends ClickableSpan {
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
            public void onClick(View widget) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$$SpoilerSpan.onClick(android.view.View)",this,widget);try{if (!(widget instanceof TextView))
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$$SpoilerSpan.onClick(android.view.View)",this);return;}}
                TextView tv = (TextView) widget;
                CharSequence cs = tv.getText();
                if (!(cs instanceof Spannable))
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$$SpoilerSpan.onClick(android.view.View)",this);return;}}
                Spannable s = (Spannable) cs;
                Object[] spans = s.getSpans(start, end, this.getClass());
                if (spans == null || spans.length == 0)
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$$SpoilerSpan.onClick(android.view.View)",this);return;}}
                if (DEBUG)
                    {Log.i(TAG, "Found " + spans.length + " spans");}
                blackout = false;
                widget.invalidate();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$$SpoilerSpan.onClick(android.view.View)",this,throwable);throw throwable;}
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$$SpoilerSpan.updateDrawState(android.text.style.TextPaint)",this,ds);try{if (blackout) {
                    int textColor = ds.getColor();
                    ds.bgColor = textColor;
                }com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$$SpoilerSpan.updateDrawState(android.text.style.TextPaint)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$$SpoilerSpan.updateDrawState(android.text.style.TextPaint)",this,throwable);throw throwable;}
            }
        }

        class SpanFactory {
            public Class getSpanClass() {
                com.mijack.Xlog.logMethodEnter("android.text.style.Class com.chanapps.four.viewer.ThreadViewer$$SpanFactory.getSpanClass()",this);try{com.mijack.Xlog.logMethodExit("android.text.style.Class com.chanapps.four.viewer.ThreadViewer$$SpanFactory.getSpanClass()",this);return SpoilerSpan.class;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.text.style.Class com.chanapps.four.viewer.ThreadViewer$$SpanFactory.getSpanClass()",this,throwable);throw throwable;}
            }

            public Object getSpan(final int start, final int end) {
                com.mijack.Xlog.logMethodEnter("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$$SpanFactory.getSpan(int,int)",this,start,end);try{com.mijack.Xlog.logMethodExit("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$$SpanFactory.getSpan(int,int)",this);return new SpoilerSpan(start, end);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$$SpanFactory.getSpan(int,int)",this,throwable);throw throwable;}
            }
        }

        SpanFactory spanFactory = new SpanFactory();

        public void handleTag(boolean opening, String tag, Editable output,
                XMLReader xmlReader) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$6.handleTag(boolean,android.text.style.String,android.text.style.Editable,org.xml.sax.XMLReader)",this,opening,tag,output,xmlReader);try{if (SPOILER_TAG.equals(tag))
                {handleSpoiler(opening, output);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$6.handleTag(boolean,android.text.style.String,android.text.style.Editable,org.xml.sax.XMLReader)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$6.handleTag(boolean,android.text.style.String,android.text.style.Editable,org.xml.sax.XMLReader)",this,throwable);throw throwable;}
        }

        private void handleSpoiler(boolean opening, Editable output) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$6.handleSpoiler(boolean,android.text.style.Editable)",this,opening,output);try{if (opening)
                {handleSpoilerOpen(output);}
            else
                {handleSpoilerClose(output);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$6.handleSpoiler(boolean,android.text.style.Editable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$6.handleSpoiler(boolean,android.text.style.Editable)",this,throwable);throw throwable;}
        }

        private void handleSpoilerOpen(Editable output) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$6.handleSpoilerOpen(android.text.style.Editable)",this,output);try{if (DEBUG)
                {Log.i(TAG, "handleSpoilerOpen(" + output + ")");}
            int len = output.length();
            output.setSpan(spanFactory.getSpan(len, len), len, len,
                    Spannable.SPAN_MARK_MARK);com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$6.handleSpoilerOpen(android.text.style.Editable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$6.handleSpoilerOpen(android.text.style.Editable)",this,throwable);throw throwable;}
        }

        private void handleSpoilerClose(Editable output) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$6.handleSpoilerClose(android.text.style.Editable)",this,output);try{if (DEBUG)
                {Log.i(TAG, "handleSpoilerClose(" + output + ")");}
            int len = output.length();
            Object obj = getFirst(output, spanFactory.getSpanClass());
            int start = output.getSpanStart(obj);
            output.removeSpan(obj);
            if (start >= 0 && len >= 0 && start != len) {
                output.setSpan(spanFactory.getSpan(start, len), start, len,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (DEBUG)
                    {Log.i(TAG, "setSpan(" + start + ", " + len + ")");}
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$6.handleSpoilerClose(android.text.style.Editable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$6.handleSpoilerClose(android.text.style.Editable)",this,throwable);throw throwable;}
        }

        private Object getFirst(Editable text, Class kind) {
            com.mijack.Xlog.logMethodEnter("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$6.getFirst(android.text.style.Editable,android.text.style.Class)",this,text,kind);try{Object[] objs = text.getSpans(0, text.length(), kind);
            if (objs.length == 0)
                {{com.mijack.Xlog.logMethodExit("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$6.getFirst(android.text.style.Editable,android.text.style.Class)",this);return null;}}
            if (DEBUG)
                {Log.i(TAG, "Found " + objs.length + " matching spans");}
            for (int i = 0; i < objs.length; i++) {
                Object span = objs[i];
                if (text.getSpanFlags(span) == Spannable.SPAN_MARK_MARK)
                    {{com.mijack.Xlog.logMethodExit("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$6.getFirst(android.text.style.Editable,android.text.style.Class)",this);return span;}}
            }
            {com.mijack.Xlog.logMethodExit("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$6.getFirst(android.text.style.Editable,android.text.style.Class)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$6.getFirst(android.text.style.Editable,android.text.style.Class)",this,throwable);throw throwable;}
        }

        private Object getLast(Editable text, Class kind) {
            com.mijack.Xlog.logMethodEnter("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$6.getLast(android.text.style.Editable,android.text.style.Class)",this,text,kind);try{Object[] objs = text.getSpans(0, text.length(), kind);
            if (objs.length == 0)
                {{com.mijack.Xlog.logMethodExit("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$6.getLast(android.text.style.Editable,android.text.style.Class)",this);return null;}}
            for (int i = objs.length - 1; i >= 0; i--) {
                Object span = objs[i];
                if (text.getSpanFlags(span) == Spannable.SPAN_MARK_MARK)
                    {{com.mijack.Xlog.logMethodExit("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$6.getLast(android.text.style.Editable,android.text.style.Class)",this);return span;}}
            }
            {com.mijack.Xlog.logMethodExit("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$6.getLast(android.text.style.Editable,android.text.style.Class)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.text.style.Object com.chanapps.four.viewer.ThreadViewer$6.getLast(android.text.style.Editable,android.text.style.Class)",this,throwable);throw throwable;}
        }
    };

    public static void clearBigImageView(final ImageView v) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.clearBigImageView(android.text.style.ImageView)",v);try{new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$7.run()",this);try{Drawable d = v.getDrawable();
                if (d != null && d instanceof BitmapDrawable) {
                    BitmapDrawable bd = (BitmapDrawable) d;
                    Bitmap b = bd.getBitmap();
                    if (b != null)
                        {b.recycle();}
                }com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$7.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$7.run()",this,throwable);throw throwable;}
            }
        }).start();
        v.setImageBitmap(null);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.clearBigImageView(android.text.style.ImageView)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.clearBigImageView(android.text.style.ImageView)",throwable);throw throwable;}
    }

    public static File fullSizeImageFile(Context context, Cursor cursor) {
        com.mijack.Xlog.logStaticMethodEnter("java.io.File com.chanapps.four.viewer.ThreadViewer.fullSizeImageFile(android.content.Context,android.database.Cursor)",context,cursor);try{String boardCode = cursor.getString(cursor
                .getColumnIndex(ChanPost.POST_BOARD_CODE));
        long postNo = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
        String ext = cursor.getString(cursor.getColumnIndex(ChanPost.POST_EXT));
        Uri uri = ChanFileStorage.getHiddenLocalImageUri(context, boardCode,
                postNo, ext);
        File localImage = new File(URI.create(uri.toString()));
        if (localImage != null && localImage.exists() && localImage.canRead()
                && localImage.length() > 0)
            {{com.mijack.Xlog.logStaticMethodExit("java.io.File com.chanapps.four.viewer.ThreadViewer.fullSizeImageFile(android.content.Context,android.database.Cursor)");return localImage;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("java.io.File com.chanapps.four.viewer.ThreadViewer.fullSizeImageFile(android.content.Context,android.database.Cursor)");return null;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.io.File com.chanapps.four.viewer.ThreadViewer.fullSizeImageFile(android.content.Context,android.database.Cursor)",throwable);throw throwable;}
    }

    public static View.OnClickListener createCommentsOnClickListener(
            final AbsListView absListView, final Handler handler) {
        com.mijack.Xlog.logStaticMethodEnter("android.text.style.View.OnClickListener com.chanapps.four.viewer.ThreadViewer.createCommentsOnClickListener(android.text.style.AbsListView,android.os.Handler)",absListView,handler);try{com.mijack.Xlog.logStaticMethodExit("android.text.style.View.OnClickListener com.chanapps.four.viewer.ThreadViewer.createCommentsOnClickListener(android.text.style.AbsListView,android.os.Handler)");return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$8.onClick(android.view.View)",this,v);try{jumpToBottom(absListView, handler);com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$8.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$8.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        };}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.text.style.View.OnClickListener com.chanapps.four.viewer.ThreadViewer.createCommentsOnClickListener(android.text.style.AbsListView,android.os.Handler)",throwable);throw throwable;}
    }

    public static View.OnClickListener createImagesOnClickListener(
            final Context context, final String boardCode, final long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("android.text.style.View.OnClickListener com.chanapps.four.viewer.ThreadViewer.createImagesOnClickListener(android.content.Context,android.text.style.String,long)",context,boardCode,threadNo);try{com.mijack.Xlog.logStaticMethodExit("android.text.style.View.OnClickListener com.chanapps.four.viewer.ThreadViewer.createImagesOnClickListener(android.content.Context,android.text.style.String,long)");return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$9.onClick(android.view.View)",this,v);try{GalleryViewActivity.startAlbumViewActivity(context, boardCode,
                        threadNo);com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$9.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$9.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        };}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.text.style.View.OnClickListener com.chanapps.four.viewer.ThreadViewer.createImagesOnClickListener(android.content.Context,android.text.style.String,long)",throwable);throw throwable;}
    }

    public static void jumpToTop(final AbsListView absListView,
            final Handler handler) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.jumpToTop(android.text.style.AbsListView,android.os.Handler)",absListView,handler);try{if (absListView == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.jumpToTop(android.text.style.AbsListView,android.os.Handler)");return;}}
        Adapter adapter = absListView.getAdapter();
        if (handler != null && adapter != null && adapter.getCount() > 0)
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$10.run()",this);try{absListView.setSelection(0);com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$10.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$10.run()",this,throwable);throw throwable;}
                }
            });}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.jumpToTop(android.text.style.AbsListView,android.os.Handler)",throwable);throw throwable;}
    }

    public static void jumpToBottom(final AbsListView absListView,
            final Handler handler) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.viewer.ThreadViewer.jumpToBottom(android.text.style.AbsListView,android.os.Handler)",absListView,handler);try{if (absListView == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.viewer.ThreadViewer.jumpToBottom(android.text.style.AbsListView,android.os.Handler)");return;}}
        Adapter adapter = absListView.getAdapter();
        final int n = adapter == null ? -1 : (adapter.getCount() - 1);
        if (DEBUG)
            {android.util.Log.i(TAG, "jumping to item n=" + n);}
        if (handler != null && n >= 0)
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.viewer.ThreadViewer$11.run()",this);try{absListView.setSelection(n);com.mijack.Xlog.logMethodExit("void com.chanapps.four.viewer.ThreadViewer$11.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer$11.run()",this,throwable);throw throwable;}
                }
            });}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.viewer.ThreadViewer.jumpToBottom(android.text.style.AbsListView,android.os.Handler)",throwable);throw throwable;}
    }

    public static boolean toggleExpandedImage(ThreadViewHolder viewHolder) { com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImage(android.text.style.ThreadViewHolder)",viewHolder);try{/*// returns*/
                                                                             /*// true*/
                                                                             /*// iff*/
                                                                             /*// expanded*/
                                                                             /*// image*/
                                                                             /*// will*/
                                                                             /*// be*/
                                                                             /*// shown*/
        if (viewHolder == null || viewHolder.list_item_image_wrapper == null
                || viewHolder.list_item_image_expanded_wrapper == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImage(android.text.style.ThreadViewHolder)");return false;}}
        if (viewHolder.isWebView)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImage(android.text.style.ThreadViewHolder)");return toggleExpandedWebView(viewHolder);}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImage(android.text.style.ThreadViewHolder)");return toggleExpandedImageView(viewHolder);}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImage(android.text.style.ThreadViewHolder)",throwable);throw throwable;}
    }

    private static boolean toggleExpandedWebView(ThreadViewHolder viewHolder) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedWebView(android.text.style.ThreadViewHolder)",viewHolder);try{if (viewHolder.list_item_image_header != null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedWebView(android.text.style.ThreadViewHolder)");return toggleExpandedWebViewHeader(viewHolder);}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedWebView(android.text.style.ThreadViewHolder)");return toggleExpandedWebViewItem(viewHolder);}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedWebView(android.text.style.ThreadViewHolder)",throwable);throw throwable;}
    }

    private static boolean toggleExpandedWebViewHeader(
            ThreadViewHolder viewHolder) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedWebViewHeader(android.text.style.ThreadViewHolder)",viewHolder);try{toggleExpandedWebViewItem(viewHolder);
        viewHolder.list_item_image_expanded_click_effect
                .setVisibility(View.GONE);
        boolean wasHidden = true;
        if (viewHolder.list_item_image_expanded_webview.getVisibility() == View.VISIBLE) {
            ViewGroup.LayoutParams params = viewHolder.list_item_image_header
                    .getLayoutParams();
            if (params != null) {
                Point imageSize = new Point(params.width, params.height);
                sizeView(viewHolder.list_item_image_header, imageSize);
                ThreadImageExpander.setImageDimensions(viewHolder, imageSize);
                if (DEBUG)
                    {Log.i(TAG, "sizedHeader " + params.width + "x"
                            + params.height);}
            }
            wasHidden = false;
        }
        viewHolder.list_item_image_header.setVisibility(View.VISIBLE);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedWebViewHeader(android.text.style.ThreadViewHolder)");return wasHidden;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedWebViewHeader(android.text.style.ThreadViewHolder)",throwable);throw throwable;}
    }

    private static boolean toggleExpandedWebViewItem(ThreadViewHolder viewHolder) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedWebViewItem(android.text.style.ThreadViewHolder)",viewHolder);try{if (viewHolder.list_item_image_expanded_wrapper.getVisibility() == View.VISIBLE) {
            viewHolder.list_item_image_expanded_wrapper
                    .setVisibility(View.GONE);
            /*// viewHolder.list_item_image_expanded.setVisibility(View.GONE);*/
            viewHolder.list_item_image_expanded_webview
                    .setVisibility(View.GONE);
            viewHolder.list_item_image_wrapper.setVisibility(View.VISIBLE);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedWebViewItem(android.text.style.ThreadViewHolder)");return false;}
        } else {
            /*// viewHolder.list_item_image_expanded.setVisibility(View.GONE);*/
            viewHolder.list_item_image_expanded_webview
                    .setVisibility(View.VISIBLE);
            viewHolder.list_item_image_expanded_wrapper
                    .setVisibility(View.VISIBLE);
            viewHolder.list_item_image_wrapper.setVisibility(View.GONE);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedWebViewItem(android.text.style.ThreadViewHolder)");return true;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedWebViewItem(android.text.style.ThreadViewHolder)",throwable);throw throwable;}
    }

    private static boolean toggleExpandedImageView(ThreadViewHolder viewHolder) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageView(android.text.style.ThreadViewHolder)",viewHolder);try{if (viewHolder.list_item_image_header != null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageView(android.text.style.ThreadViewHolder)");return toggleExpandedImageViewHeader(viewHolder);}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageView(android.text.style.ThreadViewHolder)");return toggleExpandedImageViewItem(viewHolder);}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageView(android.text.style.ThreadViewHolder)",throwable);throw throwable;}
    }

    private static boolean toggleExpandedImageViewHeader(
            ThreadViewHolder viewHolder) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageViewHeader(android.text.style.ThreadViewHolder)",viewHolder);try{if (viewHolder.list_item_image_expanded_wrapper.getVisibility() == View.VISIBLE) {
            toggleExpandedImageViewItem(viewHolder);
            viewHolder.list_item_image_expanded_click_effect
                    .setVisibility(View.GONE);
            ViewGroup.LayoutParams params = viewHolder.list_item_image_header
                    .getLayoutParams();
            if (params != null) {
                Point imageSize = new Point(params.width, params.height);
                sizeView(viewHolder.list_item_image_header, imageSize);
                ThreadImageExpander.setImageDimensions(viewHolder, imageSize);
                if (DEBUG)
                    {Log.i(TAG, "sizedHeader " + params.width + "x"
                            + params.height);}
            }
            viewHolder.list_item_image_header.setVisibility(View.VISIBLE);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageViewHeader(android.text.style.ThreadViewHolder)");return false;}
        } else {
            toggleExpandedImageViewItem(viewHolder);
            viewHolder.list_item_image_expanded_click_effect
                    .setVisibility(View.GONE);
            viewHolder.list_item_image_header.setVisibility(View.VISIBLE);
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageViewHeader(android.text.style.ThreadViewHolder)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageViewHeader(android.text.style.ThreadViewHolder)",throwable);throw throwable;}
    }

    private static boolean toggleExpandedImageViewItem(
            ThreadViewHolder viewHolder) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageViewItem(android.text.style.ThreadViewHolder)",viewHolder);try{if (viewHolder.list_item_image_expanded_wrapper.getVisibility() == View.VISIBLE) {
            viewHolder.list_item_image_expanded_wrapper
                    .setVisibility(View.GONE);
            /*// viewHolder.list_item_image_expanded.setVisibility(View.GONE);*/
            viewHolder.list_item_image_expanded_webview
                    .setVisibility(View.GONE);
            viewHolder.list_item_image_wrapper.setVisibility(View.VISIBLE);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageViewItem(android.text.style.ThreadViewHolder)");return false;}
        } else {
            /*// viewHolder.list_item_image_expanded.setVisibility(View.VISIBLE);*/
            viewHolder.list_item_image_expanded_webview
                    .setVisibility(View.GONE);
            viewHolder.list_item_image_expanded_wrapper
                    .setVisibility(View.VISIBLE);
            viewHolder.list_item_image_wrapper.setVisibility(View.GONE);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageViewItem(android.text.style.ThreadViewHolder)");return true;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.viewer.ThreadViewer.toggleExpandedImageViewItem(android.text.style.ThreadViewHolder)",throwable);throw throwable;}
    }

}
