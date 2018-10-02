package com.chanapps.four.widget;

/**
 * User: mpop
 * Date: 11/22/12
 * Time: 11:30 PM
 */
public class BoardWidgetProvider extends AbstractBoardWidgetProvider {

    public static final String TAG = BoardWidgetProvider.class.getSimpleName();

    public static final int MAX_THREADS = 3;

    @Override
    protected String getWidgetType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.widget.BoardWidgetProvider.getWidgetType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.widget.BoardWidgetProvider.getWidgetType()",this);return WidgetConstants.WIDGET_TYPE_BOARD;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.widget.BoardWidgetProvider.getWidgetType()",this,throwable);throw throwable;}
    }
}