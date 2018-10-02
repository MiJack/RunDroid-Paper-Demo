package com.chanapps.four.widget;

/**
 * User: mpop
 * Date: 11/22/12
 * Time: 11:30 PM
 */
public class BoardCoverFlowCardWidgetProvider extends AbstractBoardWidgetProvider {

    public static final String TAG = BoardCoverFlowCardWidgetProvider.class.getSimpleName();
    public static final int MAX_THREADS = 30;

    @Override
    protected String getWidgetType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.widget.BoardCoverFlowCardWidgetProvider.getWidgetType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.widget.BoardCoverFlowCardWidgetProvider.getWidgetType()",this);return WidgetConstants.WIDGET_TYPE_COVER_FLOW_CARD;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.widget.BoardCoverFlowCardWidgetProvider.getWidgetType()",this,throwable);throw throwable;}
    }

}