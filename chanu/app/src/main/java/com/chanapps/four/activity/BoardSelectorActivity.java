package com.chanapps.four.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import com.chanapps.four.component.TutorialOverlay;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.data.LastActivity;
import com.chanapps.four.service.NetworkProfileManager;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 10/7/13
 * Time: 8:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class BoardSelectorActivity extends BoardActivity implements ChanIdentifiedActivity {

    public static final String TAG = BoardSelectorActivity.class.getSimpleName();
    public static final boolean DEBUG = false;

    public static void startActivity(Context from) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.activity.BoardSelectorActivity.startActivity(android.content.Context)",from);try{startActivity(from, ChanBoard.defaultBoardCode(from), "");com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.BoardSelectorActivity.startActivity(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.activity.BoardSelectorActivity.startActivity(android.content.Context)",throwable);throw throwable;}
    }

    @Override
    public void switchBoard(String boardCode, String query) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.BoardSelectorActivity.switchBoard(java.lang.String,java.lang.String)",this,boardCode,query);try{if (ChanBoard.isTopBoard(boardCode)) {
            switchBoardInternal(boardCode, query);
        }
        else {
            Intent intent = BoardActivity.createIntent(this, boardCode, query);
            startActivity(intent);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.BoardSelectorActivity.switchBoard(java.lang.String,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.BoardSelectorActivity.switchBoard(java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    protected void activityChangeAsync() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.BoardSelectorActivity.activityChangeAsync()",this);try{final ChanIdentifiedActivity activity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.BoardSelectorActivity$1.run()",this);try{if (NetworkProfileManager.instance().getActivity() != activity) {
                    if (DEBUG) {Log.i(TAG, "boardSelector onResume() activityChange to /" + boardCode + "/");}
                    NetworkProfileManager.instance().activityChange(activity);
                    if (handler != null)
                        {handler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.BoardSelectorActivity$1$1.run()",this);try{new TutorialOverlay(layout, TutorialOverlay.Page.BOARD);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.BoardSelectorActivity$1$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.BoardSelectorActivity$1$1.run()",this,throwable);throw throwable;}
                            }
                        });}
                }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.BoardSelectorActivity$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.BoardSelectorActivity$1.run()",this,throwable);throw throwable;}
            }
        }).start();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.BoardSelectorActivity.activityChangeAsync()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.BoardSelectorActivity.activityChangeAsync()",this,throwable);throw throwable;}
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.BoardSelectorActivity.onConfigurationChanged(android.content.res.Configuration)",this,newConfig);try{super.onConfigurationChanged(newConfig);
        if (absListView != null)
            {viewPosition = absListView.getFirstVisiblePosition();}
        switchBoardInternal(boardCode, "");
        /*//if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)*/

        /*//createAbsListView();*/
        /*//if (absListView != null && absListView instanceof GridView) {*/
        /*//    ((GridView)absListView).setNumColumns(R.integer.BoardGridViewSmall_numColumns);*/
        /*//}*/
        /*// the views handle this already*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.BoardSelectorActivity.onConfigurationChanged(android.content.res.Configuration)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.BoardSelectorActivity.onConfigurationChanged(android.content.res.Configuration)",this,throwable);throw throwable;}
    }

    @Override
    public ChanActivityId getChanActivityId() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.ChanActivityId com.chanapps.four.activity.BoardSelectorActivity.getChanActivityId()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanActivityId com.chanapps.four.activity.BoardSelectorActivity.getChanActivityId()",this);return new ChanActivityId(LastActivity.BOARD_SELECTOR_ACTIVITY, boardCode, query);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.ChanActivityId com.chanapps.four.activity.BoardSelectorActivity.getChanActivityId()",this,throwable);throw throwable;}
    }

}
