package com.chanapps.four.service.profile;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import com.chanapps.four.data.FetchParams;


public class WifiProfile extends MobileProfile {
	private static final Map<Health, FetchParams> REFRESH_TIME = new HashMap<Health, FetchParams> ();
	
	static {
		/* Mapping between connection health and fetch params
		 *               HEALTH  ----->   REFRESH_DELAY, FORCE_REFRESH_DELAY, READ_TIMEOUT, CONNECT_TIMEOUT
		 */
		REFRESH_TIME.put(Health.BAD,       new FetchParams(600L,  3L, 15, 10, 0, 0));
		REFRESH_TIME.put(Health.VERY_SLOW, new FetchParams(600L,  3L, 15, 10, 100000, 5));
		REFRESH_TIME.put(Health.SLOW,      new FetchParams(600L,  3L, 15,  7, 250000, 10));
		REFRESH_TIME.put(Health.GOOD,      new FetchParams(600L,  3L, 10,  4, 500000, 15));
		REFRESH_TIME.put(Health.PERFECT,   new FetchParams(600L,  3L,  6,  3, 5000000, 20));
	}
	
	@Override
	public Type getConnectionType() {
		com.mijack.Xlog.logMethodEnter("Type com.chanapps.four.service.profile.WifiProfile.getConnectionType()",this);try{com.mijack.Xlog.logMethodExit("Type com.chanapps.four.service.profile.WifiProfile.getConnectionType()",this);return Type.WIFI;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Type com.chanapps.four.service.profile.WifiProfile.getConnectionType()",this,throwable);throw throwable;}
	}

	@Override
	public FetchParams getFetchParams() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.data.FetchParams com.chanapps.four.service.profile.WifiProfile.getFetchParams()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.data.FetchParams com.chanapps.four.service.profile.WifiProfile.getFetchParams()",this);return REFRESH_TIME.get(getConnectionHealth());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.data.FetchParams com.chanapps.four.service.profile.WifiProfile.getFetchParams()",this,throwable);throw throwable;}
	}

    @Override
    public void onBoardSelected(Context context, String boardCode) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.profile.WifiProfile.onBoardSelected(android.content.Context,java.lang.String)",this,context,boardCode);try{super.onBoardSelected(context, boardCode);
        /*// seems to overload phone on wifi*/
        /*
        NetworkProfileManager.NetworkBroadcastReceiver.checkNetwork(context);
        Health health = getConnectionHealth();
        if (health == Health.GOOD || health == Health.PERFECT) {
            ChanBoard board = ChanFileStorage.loadBoardData(context, boardCode);
            int threadPrefechCounter = health == Health.GOOD ? 3 : 7;
            if (board != null) {
                for(ChanPost thread : board.threads) {
                    if (threadPrefechCounter <= 0) {
                        break;
                    }
                    if (thread.closed == 0 && thread.sticky == 0 && thread.replies > 5 && thread.images > 1) {
                        threadPrefechCounter--;
                        FetchChanDataService.scheduleThreadFetch(context, boardCode, thread.no, false, false);
                    }
                }
            }
        }
        */com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.profile.WifiProfile.onBoardSelected(android.content.Context,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.profile.WifiProfile.onBoardSelected(android.content.Context,java.lang.String)",this,throwable);throw throwable;}

    }

}
