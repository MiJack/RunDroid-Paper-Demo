package com.chanapps.four.service.profile;

import android.content.Context;
import android.os.Handler;

import android.widget.Toast;
import com.chanapps.four.activity.*;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.data.LastActivity;
import com.chanapps.four.service.FetchChanDataService;
import com.chanapps.four.service.NetworkProfileManager;

public class NoConnectionProfile extends AbstractNetworkProfile {

	@Override
	public Type getConnectionType() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.Type com.chanapps.four.service.profile.NoConnectionProfile.getConnectionType()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.Type com.chanapps.four.service.profile.NoConnectionProfile.getConnectionType()",this);return Type.NO_CONNECTION;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.Type com.chanapps.four.service.profile.NoConnectionProfile.getConnectionType()",this,throwable);throw throwable;}
	}

	@Override
	public Health getConnectionHealth() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.Health com.chanapps.four.service.profile.NoConnectionProfile.getConnectionHealth()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.Health com.chanapps.four.service.profile.NoConnectionProfile.getConnectionHealth()",this);return Health.NO_CONNECTION;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.Health com.chanapps.four.service.profile.NoConnectionProfile.getConnectionHealth()",this,throwable);throw throwable;}
	}
	
	@Override
	public void onProfileActivated(Context context) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile.onProfileActivated(android.content.Context)",this,context);try{super.onProfileActivated(context);
		
		FetchChanDataService.clearServiceQueue(context);
		
		makeToast(R.string.no_connection_profile);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile.onProfileActivated(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile.onProfileActivated(android.content.Context)",this,throwable);throw throwable;}
	}

	@Override
	public void onApplicationStart(Context context) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile.onApplicationStart(android.content.Context)",this,context);try{super.onApplicationStart(context);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile.onApplicationStart(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile.onApplicationStart(android.content.Context)",this,throwable);throw throwable;}
	}

    @Override
    public void onBoardSelectorRefreshed(final Context context, Handler handler, String boardCode) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile.onBoardSelectorRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,context,handler,boardCode);try{super.onBoardSelectorRefreshed(context, handler, boardCode);
        if (handler != null)
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile$1.run()",this);try{ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
                    activity.setProgress(false);
                    Toast.makeText(activity.getBaseContext(), R.string.board_offline_refresh, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile$1.run()",this,throwable);throw throwable;}
                }
            });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile.onBoardSelectorRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile.onBoardSelectorRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
    }

    @Override
    public void onBoardRefreshed(final Context context, Handler handler, String boardCode) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile.onBoardRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,context,handler,boardCode);try{super.onBoardRefreshed(context, handler, boardCode);
        if (ChanFileStorage.hasNewBoardData(context, boardCode))
            {onUpdateViewData(context, handler, boardCode);}
        else if (handler != null)
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile$2.run()",this);try{ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
                    activity.setProgress(false);
                    Toast.makeText(activity.getBaseContext(), R.string.board_offline_refresh, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile$2.run()",this,throwable);throw throwable;}
                }
            });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile.onBoardRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile.onBoardRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
    }

    @Override
    public void onThreadRefreshed(Context context, Handler handler, String boardCode, long threadNo) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile.onThreadRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String,long)",this,context,handler,boardCode,threadNo);try{super.onThreadRefreshed(context, handler, boardCode, threadNo);
        if (handler != null)
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile$3.run()",this);try{ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
                    activity.setProgress(false);
                    Toast.makeText(activity.getBaseContext(), R.string.board_offline_refresh, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile$3.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile$3.run()",this,throwable);throw throwable;}
                }
            });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile.onThreadRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile.onThreadRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String,long)",this,throwable);throw throwable;}
    }

    @Override
    public void onUpdateViewData(Context baseContext, Handler handler, String boardCode) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile.onUpdateViewData(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,baseContext,handler,boardCode);try{super.onUpdateViewData(baseContext, handler, boardCode);

        final ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
        ChanActivityId currentActivityId = NetworkProfileManager.instance().getActivityId();

        String refreshText = null;
        ChanBoard board = ChanFileStorage.loadBoardData(baseContext, boardCode);
        if (board != null && board.hasNewBoardData()) {
            refreshText = board.refreshMessage();
            board.swapLoadedThreads();
        }
        final String refreshMessage = refreshText;

        boolean boardActivity = currentActivityId != null
                && currentActivityId.boardCode != null
                && currentActivityId.boardCode.equals(boardCode);

        if (boardActivity
                && currentActivityId.activity == LastActivity.BOARD_ACTIVITY
                && currentActivityId.threadNo == 0 && handler != null)
            {handler.post(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile$4.run()",this);try{/*//((BoardActivity)activity).refresh(refreshMessage);*/
                    ((BoardActivity)activity).refresh();com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile$4.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile$4.run()",this,throwable);throw throwable;}
                }
            });}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile.onUpdateViewData(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile.onUpdateViewData(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
    }

    @Override
	public void onBoardSelected(Context context, String board) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile.onBoardSelected(android.content.Context,com.chanapps.four.activity.String)",this,context,board);try{super.onBoardSelected(context, board);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile.onBoardSelected(android.content.Context,com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile.onBoardSelected(android.content.Context,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
    }

	@Override
	public void onThreadSelected(Context context, String board, long threadId) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile.onThreadSelected(android.content.Context,com.chanapps.four.activity.String,long)",this,context,board,threadId);try{super.onThreadSelected(context, board, threadId);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile.onThreadSelected(android.content.Context,com.chanapps.four.activity.String,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile.onThreadSelected(android.content.Context,com.chanapps.four.activity.String,long)",this,throwable);throw throwable;}
    }

    @Override
    public void onDataFetchFailure(ChanIdentifiedService service, Failure failure) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile.onDataFetchFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this,service,failure);try{super.onDataFetchFailure(service, failure);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile.onDataFetchFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile.onDataFetchFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this,throwable);throw throwable;}
    }

    @Override
    public void onDataParseFailure(ChanIdentifiedService service, Failure failure) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile.onDataParseFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this,service,failure);try{super.onDataParseFailure(service, failure);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile.onDataParseFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile.onDataParseFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this,throwable);throw throwable;}
    }

    @Override
	public void onDataParseSuccess(ChanIdentifiedService service) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.NoConnectionProfile.onDataParseSuccess(com.chanapps.four.activity.ChanIdentifiedService)",this,service);try{ChanActivityId data = service.getChanActivityId();
		ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
		ChanActivityId currentActivityId = NetworkProfileManager.instance().getActivityId();
		
		if (data.threadNo == 0) {
			/*// board fetching*/
			boolean boardActivity = currentActivityId != null
					&& currentActivityId.boardCode != null
					&& currentActivityId.boardCode.equals(data.boardCode);
			if (boardActivity && currentActivityId.activity == LastActivity.BOARD_ACTIVITY
					&& currentActivityId.threadNo == 0) {
				/*// user is on the board page, we need to be reloaded it*/
				Handler handler = activity.getChanHandler();
				if (handler != null) {
					handler.sendEmptyMessage(0);
				}
			}
		} else if (data.postNo == 0) {
			/*// thread fetching*/
			boolean threadActivity = currentActivityId != null && currentActivityId.boardCode != null
					&& currentActivityId.boardCode.equals(data.boardCode)
					&& currentActivityId.threadNo == data.threadNo;
			if (currentActivityId != null && threadActivity && currentActivityId.activity == LastActivity.THREAD_ACTIVITY
					&& currentActivityId.postNo == 0) {
				/*// user is on the thread page, we need to reload it*/
				Handler handler = activity.getChanHandler();
				if (handler != null) {
					handler.sendEmptyMessage(0);
                    if (data.threadUpdateMessage != null)
                        {Toast.makeText(activity.getBaseContext(), data.threadUpdateMessage, Toast.LENGTH_SHORT).show();}
				}
			}
		} else {
			/*// image fetching*/
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.NoConnectionProfile.onDataParseSuccess(com.chanapps.four.activity.ChanIdentifiedService)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.NoConnectionProfile.onDataParseSuccess(com.chanapps.four.activity.ChanIdentifiedService)",this,throwable);throw throwable;}
	}
}
