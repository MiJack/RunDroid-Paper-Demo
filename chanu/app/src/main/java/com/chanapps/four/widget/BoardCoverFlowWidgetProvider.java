package com.chanapps.four.widget;

/**
 * User: mpop
 * Date: 11/22/12
 * Time: 11:30 PM
 */
public class BoardCoverFlowWidgetProvider extends AbstractBoardWidgetProvider {

    public static final String TAG = BoardCoverFlowWidgetProvider.class.getSimpleName();
    public static final int MAX_THREADS = 20;

    @Override
    protected String getWidgetType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.widget.BoardCoverFlowWidgetProvider.getWidgetType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.widget.BoardCoverFlowWidgetProvider.getWidgetType()",this);return WidgetConstants.WIDGET_TYPE_COVER_FLOW;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.widget.BoardCoverFlowWidgetProvider.getWidgetType()",this,throwable);throw throwable;}
    }

}