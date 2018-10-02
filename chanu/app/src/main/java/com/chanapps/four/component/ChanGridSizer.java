package com.chanapps.four.component;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

/**
 * Created with IntelliJ IDEA.
 * User: arley
 * Date: 11/21/12
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChanGridSizer {

    private static final String TAG = ChanGridSizer.class.getSimpleName();
    private static final boolean DEBUG = false;

    static public int dpToPx(Display d, int dp) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.component.ChanGridSizer.dpToPx(android.view.Display,int)",d,dp);try{DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.component.ChanGridSizer.dpToPx(android.view.Display,int)");return dpToPx(displayMetrics, dp);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.component.ChanGridSizer.dpToPx(android.view.Display,int)",throwable);throw throwable;}
    }
    static public int dpToPx(DisplayMetrics displayMetrics, int dp) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.component.ChanGridSizer.dpToPx(android.util.DisplayMetrics,int)",displayMetrics,dp);try{float dpf = dp;
        int pixels = (int)(displayMetrics.density * dpf + 0.5f);
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.component.ChanGridSizer.dpToPx(android.util.DisplayMetrics,int)");return pixels;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.component.ChanGridSizer.dpToPx(android.util.DisplayMetrics,int)",throwable);throw throwable;}
    }

    static public int getCalculatedWidth(DisplayMetrics d, int numColumns, int requestedHorizontalSpacing) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.component.ChanGridSizer.getCalculatedWidth(android.util.DisplayMetrics,int,int)",d,numColumns,requestedHorizontalSpacing);try{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.component.ChanGridSizer.getCalculatedWidth(android.util.DisplayMetrics,int,int)");return getCalculatedWidth(d.widthPixels, numColumns, requestedHorizontalSpacing);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.component.ChanGridSizer.getCalculatedWidth(android.util.DisplayMetrics,int,int)",throwable);throw throwable;}
    }

    static public int getCalculatedWidth(int widthPixels, int numColumns, int requestedHorizontalSpacing) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.component.ChanGridSizer.getCalculatedWidth(int,int,int)",widthPixels,numColumns,requestedHorizontalSpacing);try{int availableSpace = widthPixels - requestedHorizontalSpacing * (numColumns + 1);
        int columnWidth = availableSpace / numColumns;
        if (DEBUG) {Log.i(TAG, "sizeGridToDisplay availableSpace=" + availableSpace
                + " numColumns=" + numColumns
                + " columnWidth=" + columnWidth);}
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.component.ChanGridSizer.getCalculatedWidth(int,int,int)");return columnWidth;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.component.ChanGridSizer.getCalculatedWidth(int,int,int)",throwable);throw throwable;}
    }

}
