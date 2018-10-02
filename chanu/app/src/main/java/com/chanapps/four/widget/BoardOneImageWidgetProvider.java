package com.chanapps.four.widget;

/**
 * User: mpop
 * Date: 11/22/12
 * Time: 11:30 PM
 */
public class BoardOneImageWidgetProvider extends AbstractBoardWidgetProvider {

    public static final String TAG = BoardOneImageWidgetProvider.class.getSimpleName();

    public static final int MAX_THREADS = 1;

    @Override
    protected String getWidgetType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.widget.BoardOneImageWidgetProvider.getWidgetType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.widget.BoardOneImageWidgetProvider.getWidgetType()",this);return WidgetConstants.WIDGET_TYPE_ONE_IMAGE;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.widget.BoardOneImageWidgetProvider.getWidgetType()",this,throwable);throw throwable;}
    }
}