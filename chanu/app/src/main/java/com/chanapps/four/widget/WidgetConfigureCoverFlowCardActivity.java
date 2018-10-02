package com.chanapps.four.widget;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.chanapps.four.activity.R;
import com.chanapps.four.data.ChanBoard;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/15/13
 * Time: 11:07 PM
 */
public class WidgetConfigureCoverFlowCardActivity extends WidgetConfigureCoverFlowActivity {

    public static final String TAG = WidgetConfigureCoverFlowCardActivity.class.getSimpleName();
    private static final boolean DEBUG = false;

    @Override
    protected int getContentViewLayout() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.getContentViewLayout()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.getContentViewLayout()",this);return R.layout.widget_configure_coverflowcard_layout;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.getContentViewLayout()",this,throwable);throw throwable;}
    }

    @Override
    protected Class getWidgetProviderClass() {
        com.mijack.Xlog.logMethodEnter("java.lang.Class com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.getWidgetProviderClass()",this);try{com.mijack.Xlog.logMethodExit("java.lang.Class com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.getWidgetProviderClass()",this);return BoardCoverFlowCardWidgetProvider.class;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Class com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.getWidgetProviderClass()",this,throwable);throw throwable;}
    }

    @Override
    protected String getWidgetType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.getWidgetType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.getWidgetType()",this);return WidgetConstants.WIDGET_TYPE_COVER_FLOW_CARD;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.getWidgetType()",this,throwable);throw throwable;}
    }

    @Override
    protected BaseAdapter createAdapter() {
        com.mijack.Xlog.logMethodEnter("android.widget.BaseAdapter com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.createAdapter()",this);try{com.mijack.Xlog.logMethodExit("android.widget.BaseAdapter com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.createAdapter()",this);return new CardCoverflowStackAdapter(this, R.layout.widget_coverflowcard_item, R.id.widget_coverflowcard_image);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.BaseAdapter com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.createAdapter()",this,throwable);throw throwable;}
    }

    protected class CardCoverflowStackAdapter extends CoverflowStackAdapter {

        public CardCoverflowStackAdapter(Context context, int layoutId, int imageId) {
            super(context, layoutId, imageId);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            com.mijack.Xlog.logMethodEnter("android.view.View com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity$CardCoverflowStackAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,position,view,parent);try{View item = super.getView(position, view, parent);
            ChanBoard board = ChanBoard.getBoardByCode(context, widgetConf.boardCode);
            TextView sub = (TextView)item.findViewById(R.id.widget_coverflowcard_subject);
            String html = "<b>" + board.getName(context) + "</b><br/>" + board.getDescription(context);
            sub.setText(Html.fromHtml(html));
            {com.mijack.Xlog.logMethodExit("android.view.View com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity$CardCoverflowStackAdapter.getView(int,android.view.View,android.view.ViewGroup)",this);return item;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity$CardCoverflowStackAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,throwable);throw throwable;}
        }

    }

    @Override
    public void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.onStart()",this);try{super.onStart();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    public void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.onStop()",this);try{super.onStop();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureCoverFlowCardActivity.onStop()",this,throwable);throw throwable;}
    }

}
