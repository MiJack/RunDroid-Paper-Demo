/**
 * 
 */
package com.chanapps.four.service.profile;

import java.util.Date;
import java.util.Stack;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.chanapps.four.activity.*;
import com.chanapps.four.data.*;
import com.chanapps.four.service.BoardParserService;
import com.chanapps.four.service.NetworkProfileManager;
import com.chanapps.four.service.ThreadParserService;
import com.chanapps.four.widget.WidgetProviderUtils;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public abstract class AbstractNetworkProfile implements NetworkProfile {
	private static final String TAG = "AbstractNetworkProfile";
	private static final boolean DEBUG = false;
	
	protected int usageCounter = 0;
	
	private static final int MAX_STORED_DATATRANSFERS = 5;
	private static final int MAX_DATATRANSFER_INACTIVITY = 600000;  /*// 10 min*/
	private Stack<DataTransfer> dataTransfers = new Stack<DataTransfer>();
	
	private Health currentHealth = null;
	/*
	 *               HEALTH  ----->   REFRESH_DELAY, FORCE_REFRESH_DELAY, READ_TIMEOUT, CONNECT_TIMEOUT
	 */
	private static final FetchParams DEFAULT_FETCH_PARAMS = new FetchParams(600L, 3L, 3, 3, 0, 10);
	
	@Override
	public FetchParams getFetchParams() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.FetchParams com.chanapps.four.service.profile.AbstractNetworkProfile.getFetchParams()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.FetchParams com.chanapps.four.service.profile.AbstractNetworkProfile.getFetchParams()",this);return DEFAULT_FETCH_PARAMS;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.FetchParams com.chanapps.four.service.profile.AbstractNetworkProfile.getFetchParams()",this,throwable);throw throwable;}
	}

	protected synchronized void checkDataTransfer() {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.checkDataTransfer()",this);try{if (dataTransfers.size() > 0) {
			if (new Date().getTime() - dataTransfers.get(0).time.getTime() > MAX_DATATRANSFER_INACTIVITY) {
				dataTransfers.clear();
			}
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.checkDataTransfer()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.checkDataTransfer()",this,throwable);throw throwable;}
	}
	
	protected synchronized void storeDataTransfer(int time, int size) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.storeDataTransfer(int,int)",this,time,size);try{DataTransfer transfer = new DataTransfer(time, size);
		dataTransfers.push(transfer);
		if (DEBUG) {Log.i(TAG, "Storing transfer " + transfer);}
		if (dataTransfers.size() > MAX_STORED_DATATRANSFERS) {
			dataTransfers.setSize(MAX_STORED_DATATRANSFERS);
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.storeDataTransfer(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.storeDataTransfer(int,int)",this,throwable);throw throwable;}
	}
	
	protected synchronized void storeFailedDataTransfer() {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.storeFailedDataTransfer()",this);try{DataTransfer transfer = new DataTransfer();
		dataTransfers.push(transfer);
		if (DEBUG) {Log.i(TAG, "Storing transfer " + transfer);}
		if (dataTransfers.size() > MAX_STORED_DATATRANSFERS) {
			dataTransfers.setSize(MAX_STORED_DATATRANSFERS);
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.storeFailedDataTransfer()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.storeFailedDataTransfer()",this,throwable);throw throwable;}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Health getConnectionHealth() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.Health com.chanapps.four.service.profile.AbstractNetworkProfile.getConnectionHealth()",this);try{double rateSum = 0.0;
		int rateNum = 0;
		int failures = 0;
		Stack<DataTransfer> clonedTransfers = (Stack<DataTransfer>)dataTransfers.clone();
		if (clonedTransfers.size() < 2) {
			Health defaultHealth = getDefaultConnectionHealth();
			if (currentHealth != defaultHealth) {
                if (DEBUG) {Log.i(TAG, "Less than 2 transfers, changing default health to " + defaultHealth);}
                currentHealth = defaultHealth;
            }
			{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.Health com.chanapps.four.service.profile.AbstractNetworkProfile.getConnectionHealth()",this);return defaultHealth;}
		}
		for (DataTransfer transfer : clonedTransfers) {
			if (transfer.failed) {
				rateNum++;
				rateSum /= 2.0;
				failures++;
			} else {
				rateSum += transfer.dataRate;
				rateNum++;
			}
		}
		if (failures > 2) {
			if (currentHealth != Health.BAD) {
                if (DEBUG) {Log.i(TAG, "More than 2 failures, switching to BAD from " + currentHealth);}
                makeToast(R.string.network_profile_health_bad);
                currentHealth = Health.BAD;
            }
			{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.Health com.chanapps.four.service.profile.AbstractNetworkProfile.getConnectionHealth()",this);return currentHealth;}
		}
		double avgRate = rateSum / rateNum;
		if (avgRate > 200) {
			currentHealth = Health.PERFECT;
		} else if (avgRate > 50) {
			currentHealth = Health.GOOD;
		} else if (avgRate > 10) {
			currentHealth = Health.SLOW;
		} else {
			currentHealth = Health.VERY_SLOW;
		}
		/*//if (DEBUG) Log.i(TAG, "Avg rate " + avgRate + " kB/s, setting health " + currentHealth);*/
		{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.Health com.chanapps.four.service.profile.AbstractNetworkProfile.getConnectionHealth()",this);return currentHealth;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.Health com.chanapps.four.service.profile.AbstractNetworkProfile.getConnectionHealth()",this,throwable);throw throwable;}
	}
	
	@Override
	public Health getDefaultConnectionHealth() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.Health com.chanapps.four.service.profile.AbstractNetworkProfile.getDefaultConnectionHealth()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.Health com.chanapps.four.service.profile.AbstractNetworkProfile.getDefaultConnectionHealth()",this);return Health.GOOD;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.Health com.chanapps.four.service.profile.AbstractNetworkProfile.getDefaultConnectionHealth()",this,throwable);throw throwable;}
	}

	@Override
	public void onApplicationStart(Context context) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onApplicationStart(android.content.Context)",this,context);try{if (DEBUG) {Log.d(TAG, "onApplicationStart called");}
		usageCounter++;
        NetworkProfileManager.NetworkBroadcastReceiver.checkNetwork(context);
        if (DEBUG) {Log.i(TAG, "onApplicationStart scheduling global alarm");}
        WidgetProviderUtils.scheduleGlobalAlarm(context);
        /*
        Health health = getConnectionHealth();
        if (health != Health.NO_CONNECTION && health != Health.BAD && health != Health.VERY_SLOW && health != Health.SLOW) {
            WidgetProviderUtils.asyncUpdateWidgetsAndWatchlist(context); // this also schedules alarm
        } else {
            makeHealthStatusToast(context, health);
            WidgetProviderUtils.scheduleGlobalAlarm(context);
        }
        */com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onApplicationStart(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onApplicationStart(android.content.Context)",this,throwable);throw throwable;}
    }

	@Override
	public void onBoardSelectorSelected(Context context, String boardCode) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardSelectorSelected(android.content.Context,com.chanapps.four.activity.String)",this,context,boardCode);try{if (DEBUG) {Log.d(TAG, "onBoardSelectorSelected called");}
		usageCounter++;com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardSelectorSelected(android.content.Context,com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardSelectorSelected(android.content.Context,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
	}

	@Override
	public void onBoardSelectorRefreshed(Context context, Handler handler, String boardCode) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardSelectorRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,context,handler,boardCode);try{if (DEBUG) {Log.d(TAG, "onBoardSelectorRefreshed called");}
		usageCounter++;com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardSelectorRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardSelectorRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
	}

	@Override
	public void onBoardSelected(Context context, String board) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardSelected(android.content.Context,com.chanapps.four.activity.String)",this,context,board);try{if (DEBUG) {Log.d(TAG, "onBoardSelected called with board: " + board);}
		usageCounter++;com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardSelected(android.content.Context,com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardSelected(android.content.Context,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
	}

	@Override
	public void onBoardRefreshed(Context context, Handler handler, String board) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,context,handler,board);try{if (DEBUG) {Log.d(TAG, "onBoardRefreshed called with board: " + board);}
		usageCounter++;com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onBoardRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
	}

	@Override
	public void onUpdateViewData(Context baseContext, Handler chanHandler, String board) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onUpdateViewData(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,baseContext,chanHandler,board);try{if (DEBUG) {Log.d(TAG, "onUpdateViewData called with board: " + board);}
		usageCounter++;com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onUpdateViewData(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onUpdateViewData(android.content.Context,android.os.Handler,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
	}

	@Override
	public void onThreadSelected(Context context, String board, long threadId) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onThreadSelected(android.content.Context,com.chanapps.four.activity.String,long)",this,context,board,threadId);try{if (DEBUG) {Log.d(TAG, "onThreadSelected called with board: " + board + " threadId: " + threadId);}
		usageCounter++;com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onThreadSelected(android.content.Context,com.chanapps.four.activity.String,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onThreadSelected(android.content.Context,com.chanapps.four.activity.String,long)",this,throwable);throw throwable;}
	}

	@Override
	public void onThreadRefreshed(Context context, Handler handler, String board, long threadId) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onThreadRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String,long)",this,context,handler,board,threadId);try{if (DEBUG) {Log.d(TAG, "onThreadRefreshed called with board: " + board + " threadId: " + threadId);}
		usageCounter++;com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onThreadRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onThreadRefreshed(android.content.Context,android.os.Handler,com.chanapps.four.activity.String,long)",this,throwable);throw throwable;}
	}

	@Override
	public void onFullImageLoading(Context context, String board, long threadId, long postId) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onFullImageLoading(android.content.Context,com.chanapps.four.activity.String,long,long)",this,context,board,threadId,postId);try{if (DEBUG) {Log.d(TAG, "onFullImageLoading called with board: " + board + " threadId: " + threadId + " postId: " + postId);}
		usageCounter++;com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onFullImageLoading(android.content.Context,com.chanapps.four.activity.String,long,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onFullImageLoading(android.content.Context,com.chanapps.four.activity.String,long,long)",this,throwable);throw throwable;}
	}
	
	@Override
	public void onProfileActivated(Context context) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onProfileActivated(android.content.Context)",this,context);try{if (DEBUG) {Log.d(TAG, "onProfileActivated called");}
        checkDataTransfer();com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onProfileActivated(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onProfileActivated(android.content.Context)",this,throwable);throw throwable;}
	}

	@Override
	public void onProfileDeactivated(Context context) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onProfileDeactivated(android.content.Context)",this,context);try{if (DEBUG) {Log.d(TAG, "onProfileDeactivated called");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onProfileDeactivated(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onProfileDeactivated(android.content.Context)",this,throwable);throw throwable;}
	}

	@Override
	public void onDataFetchSuccess(ChanIdentifiedService service, int time, int size) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataFetchSuccess(com.chanapps.four.activity.ChanIdentifiedService,int,int)",this,service,time,size);try{if (DEBUG) {Log.i(TAG, "finishedFetchingData called for " + service + " " + size + " bytes during " + time + "ms");}
		
		storeDataTransfer(time, size);
		
		ChanActivityId data = service.getChanActivityId();
        if (DEBUG) {Log.i(TAG, "fetchData success for /" + data.boardCode + "/" + data.threadNo + "/" + data.postNo + " priority=" + data.priority);}

        if (ChanBoard.isVirtualBoard(data.boardCode)) {
            /*// skip since fetch&parse steps happen together for virtual boards*/
        } else if (data.threadNo == 0) {
			/*// board fetching*/
            BoardParserService.startService(service.getApplicationContext(), data.boardCode, data.pageNo, data.priority, data.secondaryThreadNo);
            /*//CleanUpService.startService(service.getApplicationContext());*/
		} else if (data.postNo == 0) {
			/*// thread fetching*/
            ThreadParserService.startService(service.getApplicationContext(), data.boardCode, data.threadNo, data.priority);
		} else {
			/*// image fetching*/
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataFetchSuccess(com.chanapps.four.activity.ChanIdentifiedService,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataFetchSuccess(com.chanapps.four.activity.ChanIdentifiedService,int,int)",this,throwable);throw throwable;}
	}

	@Override
	public void onDataFetchFailure(ChanIdentifiedService service, Failure failure) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataFetchFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this,service,failure);try{if (DEBUG) {Log.d(TAG, "failedFetchingData called for " + service);}
		storeFailedDataTransfer();
        final ChanActivityId data = service.getChanActivityId();
        if (data == null || (data.threadNo > 0 && data.postNo > 0)) { /*// ignore post/image fetch failures*/
            if (DEBUG) {Log.i(TAG, "null data or image fetch failure, ignoring");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataFetchFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this);return;}
        }
        final ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
        if (activity == null) {
            if (DEBUG) {Log.i(TAG, "null activity failure, ignoring");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataFetchFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this);return;}
        }
        Handler handler = activity.getChanHandler();
        if (handler == null) {
            if (DEBUG) {Log.i(TAG, "null handler failure, ignoring");}
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataFetchFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this);return;}
        }
        switch (failure) {
            case DEAD_THREAD:
                if (DEBUG) {Log.i(TAG, "refreshig after dead thread");}
                if (activity instanceof ThreadActivity)
                    {handler.post(new Runnable() {
                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile$1.run()",this);try{ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
                            ((ThreadActivity)activity)
                                    .refreshFragment(data.boardCode, data.threadNo, null);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile$1.run()",this,throwable);throw throwable;}
                        }
                    });}
                break;
            case THREAD_UNMODIFIED:
                if (DEBUG) {Log.i(TAG, "stopping after unmodified thread");}
                /*//postStopMessage(handler, R.string.mobile_profile_fetch_unmodified);*/
                postStopMessage(handler, null);
                break;
            case NETWORK:
            case MISSING_DATA:
            case WRONG_DATA:
            case CORRUPT_DATA:
            default:
                if (DEBUG) {Log.i(TAG, "stopping after generic failure");}
                /*//postStopMessage(handler, R.string.mobile_profile_fetch_failure);*/
                postStopMessage(handler, null);
                break;
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataFetchFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this,throwable);throw throwable;}
    }

	@Override
	public void onDataParseSuccess(ChanIdentifiedService service) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataParseSuccess(com.chanapps.four.activity.ChanIdentifiedService)",this,service);try{if (DEBUG) {Log.d(TAG, "finishedParsingData called for " + service);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataParseSuccess(com.chanapps.four.activity.ChanIdentifiedService)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataParseSuccess(com.chanapps.four.activity.ChanIdentifiedService)",this,throwable);throw throwable;}
	}

	@Override
	public void onDataParseFailure(ChanIdentifiedService service, Failure failure) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataParseFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this,service,failure);try{if (DEBUG) {Log.d(TAG, "failedParsingData called for " + service);}
	    onDataFetchFailure(service, failure);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataParseFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onDataParseFailure(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.activity.Failure)",this,throwable);throw throwable;}
    }
	
	@Override
	public void onImageDownloadSuccess(Context context, int time, int size) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.onImageDownloadSuccess(android.content.Context,int,int)",this,context,time,size);try{storeDataTransfer(time, size);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.onImageDownloadSuccess(android.content.Context,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.onImageDownloadSuccess(android.content.Context,int,int)",this,throwable);throw throwable;}
	}

	protected void makeToast(final String text) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.makeToast(com.chanapps.four.activity.String)",this,text);try{NetworkProfileManager.instance().makeToast(text);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.makeToast(com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.makeToast(com.chanapps.four.activity.String)",this,throwable);throw throwable;}
	}

	protected void makeToast(final int id) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.makeToast(int)",this,id);try{NetworkProfileManager.instance().makeToast(id);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.makeToast(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.makeToast(int)",this,throwable);throw throwable;}
	}

    protected void startProgress(Handler handler) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.startProgress(android.os.Handler)",this,handler);try{if (handler == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.startProgress(android.os.Handler)",this);return;}}
        handler.post(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile$2.run()",this);try{NetworkProfileManager.instance().getActivity().setProgress(true);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile$2.run()",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.startProgress(android.os.Handler)",this,throwable);throw throwable;}
    }

    protected void postStopMessage(Handler handler, final String string) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.postStopMessage(android.os.Handler,com.chanapps.four.activity.String)",this,handler,string);try{if (handler == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.postStopMessage(android.os.Handler,com.chanapps.four.activity.String)",this);return;}}
        handler.post(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile$3.run()",this);try{ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
                activity.setProgress(false);
                if (string != null && !string.isEmpty())
                    {Toast.makeText(activity.getBaseContext(), string, Toast.LENGTH_SHORT).show();}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile$3.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile$3.run()",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.postStopMessage(android.os.Handler,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
    }

    protected void postStopMessage(Handler handler, final int stringId) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.postStopMessage(android.os.Handler,int)",this,handler,stringId);try{if (handler == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.postStopMessage(android.os.Handler,int)",this);return;}}
        handler.post(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile$4.run()",this);try{ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
                activity.setProgress(false);
                if (stringId > 0)
                    {Toast.makeText(activity.getBaseContext(), stringId, Toast.LENGTH_SHORT).show();}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile$4.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile$4.run()",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.postStopMessage(android.os.Handler,int)",this,throwable);throw throwable;}
    }

    protected void postStopMessageWithRefresh(Handler handler, final int stringId) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.postStopMessageWithRefresh(android.os.Handler,int)",this,handler,stringId);try{if (handler == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.postStopMessageWithRefresh(android.os.Handler,int)",this);return;}}
        handler.post(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile$5.run()",this);try{ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
                if (stringId > 0)
                    {Toast.makeText(activity.getBaseContext(), stringId, Toast.LENGTH_SHORT).show();}
                activity.refresh();com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile$5.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile$5.run()",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.postStopMessageWithRefresh(android.os.Handler,int)",this,throwable);throw throwable;}
    }

    protected void makeHealthStatusToast(Context context, Health health) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.AbstractNetworkProfile.makeHealthStatusToast(android.content.Context,com.chanapps.four.activity.Health)",this,context,health);try{Handler handler = NetworkProfileManager.instance().getActivity() != null
                ? NetworkProfileManager.instance().getActivity().getChanHandler()
                : null;
        if (handler != null)
            {postStopMessage(handler,
                    String.format(context.getString(R.string.mobile_profile_health_status),
                            health.toString().toLowerCase().replaceAll("_", " ")));}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.AbstractNetworkProfile.makeHealthStatusToast(android.content.Context,com.chanapps.four.activity.Health)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.AbstractNetworkProfile.makeHealthStatusToast(android.content.Context,com.chanapps.four.activity.Health)",this,throwable);throw throwable;}
    }

}
