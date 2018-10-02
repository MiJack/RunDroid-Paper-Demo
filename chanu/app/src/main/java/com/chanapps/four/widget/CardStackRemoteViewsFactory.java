package com.chanapps.four.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.chanapps.four.activity.R;
import com.chanapps.four.activity.ThreadActivity;
import com.chanapps.four.data.ChanPost;
import java.util.ArrayList;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: johnarleyburns
* Date: 8/26/13
* Time: 11:18 AM
* To change this template use File | Settings | File Templates.
*/
public class CardStackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = CardStackRemoteViewsFactory.class.getSimpleName();
    private static final boolean DEBUG = false;

    private Context context;
    private int appWidgetId;
    private WidgetConf widgetConf;
    private List<ChanPost> threads = new ArrayList<ChanPost>();

    public CardStackRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        initWidget();
    }

    private void initWidget() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.CardStackRemoteViewsFactory.initWidget()",this);try{widgetConf = WidgetProviderUtils.loadWidgetConf(context, appWidgetId);
        if (widgetConf == null)
            {widgetConf = new WidgetConf(appWidgetId);} /*// new widget or no config;*/
        threads = WidgetProviderUtils.viableThreads(context, widgetConf.boardCode, BoardCoverFlowCardWidgetProvider.MAX_THREADS);
        if (DEBUG) {Log.i(TAG, "initWidget() threadCount=" + threads.size());}com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.CardStackRemoteViewsFactory.initWidget()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.CardStackRemoteViewsFactory.initWidget()",this,throwable);throw throwable;}
    }

    @Override
    public void onCreate() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.CardStackRemoteViewsFactory.onCreate()",this);try{if (DEBUG) {Log.i(TAG, "onCreate() id=" + widgetConf.appWidgetId + " /" + widgetConf.boardCode + "/");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.CardStackRemoteViewsFactory.onCreate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.CardStackRemoteViewsFactory.onCreate()",this,throwable);throw throwable;}
    }

    @Override
    public void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.CardStackRemoteViewsFactory.onDestroy()",this);try{if (DEBUG) {Log.i(TAG, "onDestroy() id=" + widgetConf.appWidgetId + " /" + widgetConf.boardCode + "/");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.CardStackRemoteViewsFactory.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.CardStackRemoteViewsFactory.onDestroy()",this,throwable);throw throwable;}
    }

    @Override
    public int getCount() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.widget.CardStackRemoteViewsFactory.getCount()",this);try{if (DEBUG) {Log.i(TAG, "getCount() id=" + widgetConf.appWidgetId + " /" + widgetConf.boardCode + "/ count=" + threads.size());}
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.widget.CardStackRemoteViewsFactory.getCount()",this);return threads.size();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.widget.CardStackRemoteViewsFactory.getCount()",this,throwable);throw throwable;}
    }

    @Override
    public RemoteViews getViewAt(int i) {
        com.mijack.Xlog.logMethodEnter("android.widget.RemoteViews com.chanapps.four.widget.CardStackRemoteViewsFactory.getViewAt(int)",this,i);try{RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_coverflowcard_item);
        ChanPost thread = i >= 0 && i < threads.size() ? threads.get(i) : null;
        if (thread != null) {
            if (DEBUG) {Log.i(TAG, "getViewAt() id=" + widgetConf.appWidgetId + " /" + widgetConf.boardCode + "/ pos=" + i
                + " set thread no=" + thread.no);}
            setThreadView(views, thread, i);
        }
        else {
            if (DEBUG) {Log.i(TAG, "getViewAt() id=" + widgetConf.appWidgetId + " /" + widgetConf.boardCode + "/ pos=" + i
                    + " no thread found at position");}
        }
        {com.mijack.Xlog.logMethodExit("android.widget.RemoteViews com.chanapps.four.widget.CardStackRemoteViewsFactory.getViewAt(int)",this);return views;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.RemoteViews com.chanapps.four.widget.CardStackRemoteViewsFactory.getViewAt(int)",this,throwable);throw throwable;}
    }

    @Override
    public RemoteViews getLoadingView() {
        com.mijack.Xlog.logMethodEnter("android.widget.RemoteViews com.chanapps.four.widget.CardStackRemoteViewsFactory.getLoadingView()",this);try{com.mijack.Xlog.logMethodExit("android.widget.RemoteViews com.chanapps.four.widget.CardStackRemoteViewsFactory.getLoadingView()",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.RemoteViews com.chanapps.four.widget.CardStackRemoteViewsFactory.getLoadingView()",this,throwable);throw throwable;}
    }

    @Override
    public int getViewTypeCount() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.widget.CardStackRemoteViewsFactory.getViewTypeCount()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.widget.CardStackRemoteViewsFactory.getViewTypeCount()",this);return 1;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.widget.CardStackRemoteViewsFactory.getViewTypeCount()",this,throwable);throw throwable;}
    }

    @Override
    public long getItemId(int position) {
        com.mijack.Xlog.logMethodEnter("long com.chanapps.four.widget.CardStackRemoteViewsFactory.getItemId(int)",this,position);try{com.mijack.Xlog.logMethodExit("long com.chanapps.four.widget.CardStackRemoteViewsFactory.getItemId(int)",this);return position;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.widget.CardStackRemoteViewsFactory.getItemId(int)",this,throwable);throw throwable;}
    }

    @Override
    public boolean hasStableIds() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.widget.CardStackRemoteViewsFactory.hasStableIds()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.widget.CardStackRemoteViewsFactory.hasStableIds()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.widget.CardStackRemoteViewsFactory.hasStableIds()",this,throwable);throw throwable;}
    }

    @Override
    public void onDataSetChanged() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.CardStackRemoteViewsFactory.onDataSetChanged()",this);try{if (DEBUG) {Log.i(TAG, "onDataSetChanged() id=" + widgetConf.appWidgetId + " /" + widgetConf.boardCode + "/");}
        initWidget();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.CardStackRemoteViewsFactory.onDataSetChanged()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.CardStackRemoteViewsFactory.onDataSetChanged()",this,throwable);throw throwable;}
    }

    private void setThreadView(RemoteViews views, ChanPost thread, int position) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setThreadView(android.widget.RemoteViews,com.chanapps.four.data.ChanPost,int)",this,views,thread,position);try{setImage(views, thread, position);
        setSubCom(views, thread);
        setInfo(views, thread);
        setCountryFlag(views, thread);
        setClickTarget(views, thread);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setThreadView(android.widget.RemoteViews,com.chanapps.four.data.ChanPost,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setThreadView(android.widget.RemoteViews,com.chanapps.four.data.ChanPost,int)",this,throwable);throw throwable;}
    }

    private void setImage(RemoteViews views, ChanPost thread, int position) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setImage(android.widget.RemoteViews,com.chanapps.four.data.ChanPost,int)",this,views,thread,position);try{String url = thread.thumbnailUrl(context);
        WidgetProviderUtils.safeSetRemoteViewThumbnail(context, widgetConf, views, R.id.widget_coverflowcard_image, url, position);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setImage(android.widget.RemoteViews,com.chanapps.four.data.ChanPost,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setImage(android.widget.RemoteViews,com.chanapps.four.data.ChanPost,int)",this,throwable);throw throwable;}
    }

    private void setSubCom(RemoteViews views, ChanPost thread) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setSubCom(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this,views,thread);try{String subject = thread.combinedSubCom();
        if (subject != null && !subject.isEmpty())
            {views.setTextViewText(R.id.widget_coverflowcard_subject, Html.fromHtml(subject));}com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setSubCom(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setSubCom(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this,throwable);throw throwable;}
    }

    private void setInfo(RemoteViews views, ChanPost thread) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setInfo(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this,views,thread);try{String headline = thread.threadInfoLine(context, true, true, true);
        if (headline != null && !headline.isEmpty())
            {views.setTextViewText(R.id.widget_coverflowcard_info, Html.fromHtml(headline));}com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setInfo(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setInfo(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this,throwable);throw throwable;}

    }

    private void setCountryFlag(RemoteViews views, ChanPost thread) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setCountryFlag(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this,views,thread);try{String url = thread.countryFlagUrl(context);
        if (url == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setCountryFlag(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this);return;}}
        boolean isCached = WidgetProviderUtils.safeSetRemoteViewThumbnail(context, widgetConf, views, R.id.widget_coverflowcard_flag, url, -1);
        if (!isCached) {
            WidgetProviderUtils.asyncDownloadAndCacheUrl(context, url, urlDownloadCallback);
            if (DEBUG) {Log.i(TAG, "getViewAt() url=" + url + " no file, downloading country flag");}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setCountryFlag(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this,throwable);throw throwable;}
    }

    private Runnable urlDownloadCallback = new Runnable() {
        @Override
        public void run() {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.CardStackRemoteViewsFactory$1.run()",this);try{AppWidgetManager
                    .getInstance(context)
                    .notifyAppWidgetViewDataChanged(widgetConf.appWidgetId, R.id.widget_board_coverflow_container);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.CardStackRemoteViewsFactory$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.CardStackRemoteViewsFactory$1.run()",this,throwable);throw throwable;}
        }
    };

    private void setClickTarget(RemoteViews views, ChanPost thread) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setClickTarget(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this,views,thread);try{Bundle extras = new Bundle();
        extras.putLong(ThreadActivity.THREAD_NO, thread.no);
        Intent intent = new Intent();
        intent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_coverflowcard_frame, intent);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setClickTarget(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.CardStackRemoteViewsFactory.setClickTarget(android.widget.RemoteViews,com.chanapps.four.data.ChanPost)",this,throwable);throw throwable;}
    }

}
