package com.chanapps.four.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.chanapps.four.activity.R;
import com.chanapps.four.loader.ChanImageLoader;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/15/13
 * Time: 11:07 PM
 */
public class WidgetConfigureCoverFlowActivity extends AbstractWidgetConfigureActivity {

    public static final String TAG = WidgetConfigureCoverFlowActivity.class.getSimpleName();
    private static final boolean DEBUG = false;
    protected static final int MAX_CONFIG_THREADS = 6;

    private StackView stackView = null;
    private View emptyView = null;
    private String[] urls = {};
    private BaseAdapter adapter = null;
    private LayoutInflater inflater = null;

    @Override
    protected int getContentViewLayout() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.getContentViewLayout()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.getContentViewLayout()",this);return R.layout.widget_configure_coverflow_layout;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.getContentViewLayout()",this,throwable);throw throwable;}
    }

    @Override
    protected void setBoardImages() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.setBoardImages()",this);try{final Context context = this;
        final Handler handler = new Handler();
        if (DEBUG) {Log.i(TAG, "setBoardImages() /" + widgetConf.boardCode + "/");}
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$1.run()",this);try{urls = boardThreadUrls(context, widgetConf.boardCode, MAX_CONFIG_THREADS);
                if (DEBUG) {Log.i(TAG, "setBoardImages() /" + widgetConf.boardCode + "/ found " + urls.length
                        + " urls=" + Arrays.toString(urls));}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$1$1.run()",this);try{if (emptyView == null)
                            {emptyView = findViewById(R.id.stack_view_empty);}
                        if (emptyView != null)
                            {emptyView.setVisibility(View.VISIBLE);}
                        if (stackView == null)
                            {stackView = (StackView)findViewById(R.id.stack_view_coverflow);}
                        if (stackView == null)
                            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.setBoardImages()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$1.run()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$1$1.run()",this);return;}}}}
                        if (adapter == null) {
                            if (DEBUG) {Log.i(TAG, "setBoardImages() /" + widgetConf.boardCode + "/ stackView.setAdapter");}
                            adapter = createAdapter();
                            stackView.setAdapter(adapter);
                        }
                        else {
                            adapter.notifyDataSetChanged();
                        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$1$1.run()",this,throwable);throw throwable;}
                    }
                });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$1.run()",this,throwable);throw throwable;}
            }
        }).start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.setBoardImages()",this,throwable);throw throwable;}
    }

    @Override
    protected String getWidgetType() {
        com.mijack.Xlog.logMethodEnter("android.widget.String com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.getWidgetType()",this);try{com.mijack.Xlog.logMethodExit("android.widget.String com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.getWidgetType()",this);return WidgetConstants.WIDGET_TYPE_COVER_FLOW;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.String com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.getWidgetType()",this,throwable);throw throwable;}
    }

    protected Class getWidgetProviderClass() {
        com.mijack.Xlog.logMethodEnter("android.widget.Class com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.getWidgetProviderClass()",this);try{com.mijack.Xlog.logMethodExit("android.widget.Class com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.getWidgetProviderClass()",this);return BoardCoverFlowWidgetProvider.class;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.Class com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.getWidgetProviderClass()",this,throwable);throw throwable;}
    }

    @Override
    protected void addDoneClickHandler() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.addDoneClickHandler()",this);try{Button doneButton = (Button) findViewById(R.id.done);
        if (doneButton == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.addDoneClickHandler()",this);return;}}
        final WidgetConfigureCoverFlowActivity activity = this;
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$2.onClick(android.view.View)",this,v);try{if (DEBUG)
                    {Log.i(TAG, "Configured widget=" + appWidgetId + " configuring for board=" + widgetConf.boardCode);}
                WidgetProviderUtils.storeWidgetConf(activity, widgetConf);
                Intent intent = new Intent();
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                intent.putExtra(WidgetProviderUtils.WIDGET_PROVIDER_UTILS, activity.getWidgetType());
                activity.setResult(Activity.RESULT_OK, intent);
                Intent updateWidget = new Intent(activity, getWidgetProviderClass());
                updateWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids = {appWidgetId};
                updateWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                updateWidget.putExtra(WidgetProviderUtils.WIDGET_PROVIDER_UTILS, activity.getWidgetType());
                activity.sendBroadcast(updateWidget);
                activity.finish();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$2.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$2.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.addDoneClickHandler()",this,throwable);throw throwable;}
    }

    protected BaseAdapter createAdapter() {
        com.mijack.Xlog.logMethodEnter("android.widget.BaseAdapter com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.createAdapter()",this);try{com.mijack.Xlog.logMethodExit("android.widget.BaseAdapter com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.createAdapter()",this);return new CoverflowStackAdapter(this, R.layout.widget_coverflow_item, R.id.image_coverflow_item);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.BaseAdapter com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.createAdapter()",this,throwable);throw throwable;}
    }

    protected class CoverflowStackAdapter extends BaseAdapter {
        protected Context context;
        protected int layoutId;
        protected int imageId;

        public CoverflowStackAdapter(Context context, int layoutId, int imageId) {
            this.context = context;
            this.layoutId = layoutId;
            this.imageId = imageId;
        }

        @Override
        public int getCount() {
            com.mijack.Xlog.logMethodEnter("int com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getCount()",this);try{if (DEBUG) {Log.i(TAG, "getCount()=" + urls.length);}
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getCount()",this);return urls.length;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getCount()",this,throwable);throw throwable;}
        }

        @Override
        public Object getItem(int position) {
            com.mijack.Xlog.logMethodEnter("android.widget.Object com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getItem(int)",this,position);try{com.mijack.Xlog.logMethodExit("android.widget.Object com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getItem(int)",this);return urls[position];}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.Object com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getItem(int)",this,throwable);throw throwable;}
        }

        @Override
        public long getItemId(int position) {
            com.mijack.Xlog.logMethodEnter("long com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getItemId(int)",this,position);try{com.mijack.Xlog.logMethodExit("long com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getItemId(int)",this);return (new String(widgetConf.boardCode + "/" + position)).hashCode();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getItemId(int)",this,throwable);throw throwable;}
        }

        @Override
        public boolean hasStableIds() {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.hasStableIds()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.hasStableIds()",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.hasStableIds()",this,throwable);throw throwable;}
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            com.mijack.Xlog.logMethodEnter("android.view.View com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,position,view,parent);try{if (DEBUG) {Log.i(TAG, "getView() pos=" + position + " url=" + urls[position]);}
            if (view == null) {
                if (inflater == null)
                    {inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);}
                view = inflater.inflate(layoutId, parent, false);
            }
            ImageView imageView = (ImageView) view.findViewById(imageId);
            imageView.setImageDrawable(null);
            ChanImageLoader.getInstance(context).displayImage(urls[position], imageView);
            if (emptyView != null && emptyView.getVisibility() != View.GONE)
                {emptyView.setVisibility(View.GONE);}
            {com.mijack.Xlog.logMethodExit("android.view.View com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getView(int,android.view.View,android.view.ViewGroup)",this);return view;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.chanapps.four.widget.WidgetConfigureCoverFlowActivity$CoverflowStackAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,throwable);throw throwable;}
        }
    }

    @Override
    public void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.onStart()",this);try{super.onStart();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    public void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.onStop()",this);try{super.onStop();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureCoverFlowActivity.onStop()",this,throwable);throw throwable;}
    }

}
