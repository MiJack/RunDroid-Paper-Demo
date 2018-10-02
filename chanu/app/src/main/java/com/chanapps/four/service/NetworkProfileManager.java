/**
 * 
 */
package com.chanapps.four.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.chanapps.four.activity.*;
import com.chanapps.four.component.ActivityDispatcher;
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.data.FetchParams;
import com.chanapps.four.data.LastActivity;
import com.chanapps.four.data.UserStatistics;
import com.chanapps.four.service.profile.MobileProfile;
import com.chanapps.four.service.profile.NetworkProfile;
import com.chanapps.four.service.profile.NetworkProfile.Failure;
import com.chanapps.four.service.profile.NoConnectionProfile;
import com.chanapps.four.service.profile.WifiProfile;

import java.lang.reflect.Field;

/**
 * Class manages network profile switching.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class NetworkProfileManager {
	private static final String TAG = NetworkProfileManager.class.getSimpleName();
	private static final boolean DEBUG = false;
	
	private static NetworkProfileManager instance;
	
	public static NetworkProfileManager instance() {
		com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.service.NetworkProfileManager com.chanapps.four.service.NetworkProfileManager.instance()");try{if (instance == null) {
			instance = new NetworkProfileManager();
		}
		{com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.service.NetworkProfileManager com.chanapps.four.service.NetworkProfileManager.instance()");return instance;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.service.NetworkProfileManager com.chanapps.four.service.NetworkProfileManager.instance()",throwable);throw throwable;}
	}
	
	private NetworkProfileManager() {
	}
	
	private NetworkBroadcastReceiver receiver;
	private ChanActivityId currentActivityId;
	private ChanIdentifiedActivity currentActivity;
	private NetworkProfile activeProfile = null;
	private UserStatistics userStats = null;
	
	private WifiProfile wifiProfile = new WifiProfile();
	private NoConnectionProfile noConnectionProfile = new NoConnectionProfile();
	private MobileProfile mobileProfile = new MobileProfile();
	
	public ChanActivityId getActivityId () {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.NetworkProfileManager.getActivityId()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.NetworkProfileManager.getActivityId()",this);return currentActivityId;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.NetworkProfileManager.getActivityId()",this,throwable);throw throwable;}
	}
	
	/**
	 * Returns current network profile.
	 * CALL ONLY GET METHODS.
	 */
	public NetworkProfile getCurrentProfile() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.service.profile.NetworkProfile com.chanapps.four.service.NetworkProfileManager.getCurrentProfile()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.service.profile.NetworkProfile com.chanapps.four.service.NetworkProfileManager.getCurrentProfile()",this);return activeProfile == null ? noConnectionProfile : activeProfile;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.service.profile.NetworkProfile com.chanapps.four.service.NetworkProfileManager.getCurrentProfile()",this,throwable);throw throwable;}
	}
	
	public ChanIdentifiedActivity getActivity() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.ChanIdentifiedActivity com.chanapps.four.service.NetworkProfileManager.getActivity()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanIdentifiedActivity com.chanapps.four.service.NetworkProfileManager.getActivity()",this);return currentActivity;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.ChanIdentifiedActivity com.chanapps.four.service.NetworkProfileManager.getActivity()",this,throwable);throw throwable;}
	}
	
	public FetchParams getFetchParams() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.data.FetchParams com.chanapps.four.service.NetworkProfileManager.getFetchParams()",this);try{if (activeProfile != null) {
			{com.mijack.Xlog.logMethodExit("com.chanapps.four.data.FetchParams com.chanapps.four.service.NetworkProfileManager.getFetchParams()",this);return activeProfile.getFetchParams();}
		} else {
			{com.mijack.Xlog.logMethodExit("com.chanapps.four.data.FetchParams com.chanapps.four.service.NetworkProfileManager.getFetchParams()",this);return noConnectionProfile.getFetchParams();}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.data.FetchParams com.chanapps.four.service.NetworkProfileManager.getFetchParams()",this,throwable);throw throwable;}
	}
	
	public UserStatistics getUserStatistics() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.data.UserStatistics com.chanapps.four.service.NetworkProfileManager.getUserStatistics()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.data.UserStatistics com.chanapps.four.service.NetworkProfileManager.getUserStatistics()",this);return userStats;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.data.UserStatistics com.chanapps.four.service.NetworkProfileManager.getUserStatistics()",this,throwable);throw throwable;}
	}

    protected void forceMenuKey(Context context) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.forceMenuKey(android.content.Context)",this,context);try{try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            /*// Ignore*/
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.forceMenuKey(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.forceMenuKey(android.content.Context)",this,throwable);throw throwable;}
    }

    private static boolean initialized = false;

    synchronized public void ensureInitialized(ChanIdentifiedActivity newActivity) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.ensureInitialized(com.chanapps.four.activity.ChanIdentifiedActivity)",this,newActivity);try{if (!initialized) {
            initialized = true;
            if (DEBUG) {Log.i(TAG, "ensureInitialized not initialized, initializing newActivity=" + newActivity.getChanActivityId());}

            forceMenuKey(newActivity.getBaseContext()); /*// i think it's nicer*/

            if (receiver == null) {
                /*// we need to register network changes receiver*/
                receiver = new NetworkBroadcastReceiver();
                newActivity.getBaseContext().getApplicationContext()
                        .registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                if (DEBUG) {Log.i(TAG, ConnectivityManager.CONNECTIVITY_ACTION + " receiver registered");}
            }

            NetworkBroadcastReceiver.checkNetwork(newActivity.getBaseContext());

            activeProfile.onApplicationStart(newActivity.getBaseContext());
            if (DEBUG) {Log.i(TAG, "ensureInitialized complete newActivity=" + newActivity.getChanActivityId());}
            /*//if (DEBUG) Log.i(TAG, "ensureInitialized initializing dispatching newActivity=" + newActivity.getChanActivityId());*/
            /*//ActivityDispatcher.dispatch(newActivity);*/
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.ensureInitialized(com.chanapps.four.activity.ChanIdentifiedActivity)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.ensureInitialized(com.chanapps.four.activity.ChanIdentifiedActivity)",this,throwable);throw throwable;}
    }

    public void startLastActivity(Context context) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.startLastActivity(android.content.Context)",this,context);try{LastActivity activity = currentActivityId != null && currentActivityId.activity != null
                ? currentActivityId.activity
                : null;
        if (activity == null) {
            if (DEBUG) {Log.i(TAG, "startLastActivity() starting default all boards activity");}
            BoardSelectorActivity.startActivity(context);
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.startLastActivity(android.content.Context)",this);return;}
        }

        if (DEBUG) {Log.i(TAG, "startLastActivity() starting last activity id=" + currentActivityId);}
        switch(activity) {
            case BOARD_SELECTOR_ACTIVITY:
                BoardSelectorActivity.startActivity(context, currentActivityId);
                break;
            case BOARD_ACTIVITY:
                BoardActivity.startActivity(context, currentActivityId);
                break;
            case THREAD_ACTIVITY:
                ThreadActivity.startActivity(context, currentActivityId);
                break;
            case GALLERY_ACTIVITY:
                GalleryViewActivity.startActivity(context, currentActivityId);
                break;
            case POST_REPLY_ACTIVITY:
                PostReplyActivity.startActivity(context, currentActivityId);
                break;
            case SETTINGS_ACTIVITY:
            case PURCHASE_ACTIVITY:
            case ABOUT_ACTIVITY:
            default:
                BoardSelectorActivity.startActivity(context);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.startLastActivity(android.content.Context)",this,throwable);throw throwable;}
    }

    public void activityChange(final ChanIdentifiedActivity newActivity) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.activityChange(com.chanapps.four.activity.ChanIdentifiedActivity)",this,newActivity);try{if (DEBUG) {Log.i(TAG, "activityChange to newActivityId=" + newActivity.getChanActivityId() + " receiver=" + receiver
                + " lastActivity=" + currentActivity);}

        ensureInitialized(newActivity);
        ActivityDispatcher.store(newActivity);

        final ChanActivityId lastActivity = currentActivityId;
        currentActivityId = newActivity.getChanActivityId();
		currentActivity = newActivity;

		if (userStats == null) {
            userStats = ChanFileStorage.loadUserStats(newActivity.getBaseContext());
        }
        userStats.registerActivity(newActivity);

        switch(currentActivityId.activity) {
            case BOARD_SELECTOR_ACTIVITY:
                break;
            case BOARD_ACTIVITY:
                /*// NOTE: moved refresh logic to board activity*/
               /*// if (lastActivity == null || !currentActivityId.boardCode.equals(lastActivity.boardCode)) {*/
                /*//    activeProfile.onBoardSelected(newActivity.getBaseContext(), currentActivityId.boardCode);*/
                /*//}*/
                break;
            case THREAD_ACTIVITY:
                /*// NOTE: moved refresh logic to thread activity*/
                /*// now with fragments, we only need to load the board at this level*/
                /*//if (lastActivity == null || !currentActivityId.boardCode.equals(lastActivity.boardCode)) {*/
                /*//    activeProfile.onBoardSelected(newActivity.getBaseContext(), currentActivityId.boardCode);*/
                /*//}*/
/*
                if (lastActivity == null || !currentActivityId.boardCode.equals(lastActivity.boardCode)
                        || currentActivityId.threadNo != lastActivity.threadNo)
                    activeProfile.onThreadSelected(newActivity.getBaseContext(), currentActivityId.boardCode, currentActivityId.threadNo);
*/
                break;
            case GALLERY_ACTIVITY:
                activeProfile.onFullImageLoading(newActivity.getBaseContext(), currentActivityId.boardCode, currentActivityId.threadNo, currentActivityId.postNo);
                break;
            case POST_REPLY_ACTIVITY:
                break;
            case SETTINGS_ACTIVITY:
                break;
            case PURCHASE_ACTIVITY:
                break;
            case ABOUT_ACTIVITY:
                break;
            default:
                Log.e(TAG, "Not handled activity type: " + currentActivityId.activity, new Exception("Check stack trace!"));
                activeProfile.onApplicationStart(newActivity.getBaseContext());
        }
        if (DEBUG) {Log.i(TAG, "activityChange finished currentActivityId=" + currentActivityId);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.activityChange(com.chanapps.four.activity.ChanIdentifiedActivity)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.activityChange(com.chanapps.four.activity.ChanIdentifiedActivity)",this,throwable);throw throwable;}
    }
	
	public void manualRefresh(ChanIdentifiedActivity newActivity) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.manualRefresh(com.chanapps.four.activity.ChanIdentifiedActivity)",this,newActivity);try{/*//NetworkProfileManager.instance().getUserStatistics().featureUsed(UserStatistics.ChanFeature.MANUAL_REFRESH);*/
        if (DEBUG) {Log.i(TAG, "manualRefresh " + newActivity.getChanActivityId());}
		if (newActivity == null) {
			{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.manualRefresh(com.chanapps.four.activity.ChanIdentifiedActivity)",this);return;}
		}
		currentActivityId = newActivity.getChanActivityId();
		currentActivity = newActivity;
		
		if (activeProfile == null) {
			NetworkBroadcastReceiver.checkNetwork(newActivity.getBaseContext());
		}
        if (DEBUG) {Log.i(TAG, "activeProfile=" + activeProfile);}
        switch(currentActivityId.activity) {
            case BOARD_SELECTOR_ACTIVITY:
                activeProfile.onBoardRefreshed(newActivity.getBaseContext(), newActivity.getChanHandler(), currentActivityId.boardCode);
                break;
            case BOARD_ACTIVITY:
                activeProfile.onBoardRefreshed(newActivity.getBaseContext(), newActivity.getChanHandler(), currentActivityId.boardCode);
                break;
            case THREAD_ACTIVITY:
                activeProfile.onThreadRefreshed(newActivity.getBaseContext(), newActivity.getChanHandler(), currentActivityId.boardCode, currentActivityId.threadNo);
                break;
            case GALLERY_ACTIVITY:
                activeProfile.onFullImageLoading(newActivity.getBaseContext(), currentActivityId.boardCode, currentActivityId.threadNo, currentActivityId.postNo);
                break;
            case POST_REPLY_ACTIVITY:
                break;
            case SETTINGS_ACTIVITY:
                break;
            default:
                Log.e(TAG, "Not handled activity type: " + currentActivityId.activity, new Exception("Check stack trace!"));
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.manualRefresh(com.chanapps.four.activity.ChanIdentifiedActivity)",this,throwable);throw throwable;}
    }
	
	/**
	 * Replaces currently viewed data with the one fetched recently
	 */
	public void updateViewData(ChanIdentifiedActivity newActivity) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.updateViewData(com.chanapps.four.activity.ChanIdentifiedActivity)",this,newActivity);try{if (DEBUG) {Log.i(TAG, "updateViewData " + newActivity.getChanActivityId(), new Exception("updateViewData"));}
		if (newActivity == null) {
			{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.updateViewData(com.chanapps.four.activity.ChanIdentifiedActivity)",this);return;}
		}
		currentActivityId = newActivity.getChanActivityId();
		currentActivity = newActivity;
		
		if (activeProfile == null) {
			NetworkBroadcastReceiver.checkNetwork(newActivity.getBaseContext());
		}

        switch(currentActivityId.activity) {
            case BOARD_SELECTOR_ACTIVITY:
            case BOARD_ACTIVITY:
                activeProfile.onUpdateViewData(newActivity.getBaseContext(), newActivity.getChanHandler(), currentActivityId.boardCode);
                break;
            default:
                /*// we only support update view data for board view*/
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.updateViewData(com.chanapps.four.activity.ChanIdentifiedActivity)",this,throwable);throw throwable;}
	}
	public void finishedImageDownload(ChanIdentifiedService service, int time, int size) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.finishedImageDownload(com.chanapps.four.activity.ChanIdentifiedService,int,int)",this,service,time,size);try{service = checkService(service);
		if (activeProfile == null) {
			NetworkBroadcastReceiver.checkNetwork(service.getApplicationContext());
		}
		activeProfile.onImageDownloadSuccess(service.getApplicationContext(), time, size);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.finishedImageDownload(com.chanapps.four.activity.ChanIdentifiedService,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.finishedImageDownload(com.chanapps.four.activity.ChanIdentifiedService,int,int)",this,throwable);throw throwable;}
	}

	public void finishedFetchingData(ChanIdentifiedService service, int time, int size) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.finishedFetchingData(com.chanapps.four.activity.ChanIdentifiedService,int,int)",this,service,time,size);try{service = checkService(service);
		if (activeProfile == null) {
			NetworkBroadcastReceiver.checkNetwork(service.getApplicationContext());
		}
		activeProfile.onDataFetchSuccess(service, time, size);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.finishedFetchingData(com.chanapps.four.activity.ChanIdentifiedService,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.finishedFetchingData(com.chanapps.four.activity.ChanIdentifiedService,int,int)",this,throwable);throw throwable;}
	}
	
	public void failedFetchingData(ChanIdentifiedService service, Failure failure) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.failedFetchingData(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.service.profile.NetworkProfile.Failure)",this,service,failure);try{if (DEBUG) {Log.i(TAG, "failedFetchingData service=" + service);}
		service = checkService(service);
		if (activeProfile == null) {
			NetworkBroadcastReceiver.checkNetwork(service.getApplicationContext());
		}
		activeProfile.onDataFetchFailure(service, failure);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.failedFetchingData(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.service.profile.NetworkProfile.Failure)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.failedFetchingData(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.service.profile.NetworkProfile.Failure)",this,throwable);throw throwable;}
	}
	
	public void finishedParsingData(ChanIdentifiedService service) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.finishedParsingData(com.chanapps.four.activity.ChanIdentifiedService)",this,service);try{service = checkService(service);
		if (activeProfile == null) {
			NetworkBroadcastReceiver.checkNetwork(service.getApplicationContext());
		}
		activeProfile.onDataParseSuccess(service);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.finishedParsingData(com.chanapps.four.activity.ChanIdentifiedService)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.finishedParsingData(com.chanapps.four.activity.ChanIdentifiedService)",this,throwable);throw throwable;}
	}
	
	public void failedParsingData(ChanIdentifiedService service, Failure failure) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.failedParsingData(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.service.profile.NetworkProfile.Failure)",this,service,failure);try{service = checkService(service);
		if (activeProfile == null) {
			NetworkBroadcastReceiver.checkNetwork(service.getApplicationContext());
		}
		activeProfile.onDataFetchFailure(service, failure);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.failedParsingData(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.service.profile.NetworkProfile.Failure)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.failedParsingData(com.chanapps.four.activity.ChanIdentifiedService,com.chanapps.four.service.profile.NetworkProfile.Failure)",this,throwable);throw throwable;}
	}
	
	private ChanIdentifiedService checkService(ChanIdentifiedService service) {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.ChanIdentifiedService com.chanapps.four.service.NetworkProfileManager.checkService(com.chanapps.four.activity.ChanIdentifiedService)",this,service);try{if (service == null) {
			{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanIdentifiedService com.chanapps.four.service.NetworkProfileManager.checkService(com.chanapps.four.activity.ChanIdentifiedService)",this);return new ChanIdentifiedService() {
				@Override
				public ChanActivityId getChanActivityId() {
					com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.NetworkProfileManager$1.getChanActivityId()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.NetworkProfileManager$1.getChanActivityId()",this);{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanIdentifiedService com.chanapps.four.service.NetworkProfileManager.checkService(com.chanapps.four.activity.ChanIdentifiedService)",this);return getActivity().getChanActivityId();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.NetworkProfileManager$1.getChanActivityId()",this,throwable);throw throwable;}
				}
				
				@Override
				public Context getApplicationContext() {
					com.mijack.Xlog.logMethodEnter("android.content.Context com.chanapps.four.service.NetworkProfileManager$1.getApplicationContext()",this);try{com.mijack.Xlog.logMethodExit("android.content.Context com.chanapps.four.service.NetworkProfileManager$1.getApplicationContext()",this);{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanIdentifiedService com.chanapps.four.service.NetworkProfileManager.checkService(com.chanapps.four.activity.ChanIdentifiedService)",this);return getActivity().getBaseContext();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.Context com.chanapps.four.service.NetworkProfileManager$1.getApplicationContext()",this,throwable);throw throwable;}
				}
			};}
		} else {
			{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanIdentifiedService com.chanapps.four.service.NetworkProfileManager.checkService(com.chanapps.four.activity.ChanIdentifiedService)",this);return service;}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.ChanIdentifiedService com.chanapps.four.service.NetworkProfileManager.checkService(com.chanapps.four.activity.ChanIdentifiedService)",this,throwable);throw throwable;}
	}
	
	public void changeNetworkProfile(NetworkProfile.Type type) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.changeNetworkProfile(com.chanapps.four.activity.NetworkProfile.Type)",this,type);try{changeNetworkProfile(type, null);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.changeNetworkProfile(com.chanapps.four.activity.NetworkProfile.Type)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.changeNetworkProfile(com.chanapps.four.activity.NetworkProfile.Type)",this,throwable);throw throwable;}
	}
	
	public void changeNetworkProfile(NetworkProfile.Type type, String subType) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.changeNetworkProfile(com.chanapps.four.activity.NetworkProfile.Type,com.chanapps.four.activity.String)",this,type,subType);try{switch(type) {
		case NO_CONNECTION:
			if (activeProfile != noConnectionProfile) {
				if (activeProfile != null && currentActivity != null && currentActivity != null) {
					activeProfile.onProfileDeactivated(currentActivity.getBaseContext());
				}
				activeProfile = noConnectionProfile;
				if (DEBUG) {Log.i(TAG, "Setting " + type + " profile");}
				if (currentActivity != null) {
					activeProfile.onProfileActivated(currentActivity.getBaseContext());
				}
			}
			break;
		case WIFI:
			if (activeProfile != wifiProfile) {
				if (activeProfile != null && currentActivity != null && currentActivity != null) {
					activeProfile.onProfileDeactivated(currentActivity.getBaseContext());
				}
				activeProfile = wifiProfile;
				if (DEBUG) {Log.i(TAG, "Setting " + type + " profile");}
				if (currentActivity != null) {
					activeProfile.onProfileActivated(currentActivity.getBaseContext());
				}
			}
			break;
		case MOBILE:
			if (activeProfile != mobileProfile) {
				if (activeProfile != null && currentActivity != null && currentActivity != null) {
					activeProfile.onProfileDeactivated(currentActivity.getBaseContext());
				}
				activeProfile = mobileProfile;
				if (DEBUG) {Log.i(TAG, "Setting " + type + " profile");}
				if (currentActivity != null) {
					activeProfile.onProfileActivated(currentActivity.getBaseContext());
				}
			}
			break;
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.changeNetworkProfile(com.chanapps.four.activity.NetworkProfile.Type,com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.changeNetworkProfile(com.chanapps.four.activity.NetworkProfile.Type,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
	}
	
	public static class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager$NetworkBroadcastReceiver.onReceive(android.content.Context,android.content.Intent)",this,context,intent);try{String action = intent.getAction();
            if (DEBUG) {Log.i(TAG, "Connection change action: " + action);}
/*// can't do this because extra is unreliable, returns true even when mobile data is up*/
/*//            if (intent.getBooleanExtra("EXTRA_NO_CONNECTIVITY", false)) {*/
/*//            	if (DEBUG) Log.i(TAG, "Disconnected from any network");*/
/*//            	NetworkProfileManager.instance().changeNetworkProfile(NetworkProfile.Type.NO_CONNECTION);*/
/*//            } else {*/
            	checkNetwork(context);
/*//            }*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager$NetworkBroadcastReceiver.onReceive(android.content.Context,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager$NetworkBroadcastReceiver.onReceive(android.content.Context,android.content.Intent)",this,throwable);throw throwable;}
        }
        
        public static void checkNetwork(Context context) {
            com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.service.NetworkProfileManager$NetworkBroadcastReceiver.checkNetwork(android.content.Context)",context);try{NetworkInfo activeNetwork = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
            	if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
        			if (DEBUG) {Log.i(TAG, "Connected to Wifi");}
        			NetworkProfileManager.instance().changeNetworkProfile(NetworkProfile.Type.WIFI);
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                	String networkType = activeNetwork.getSubtypeName();
            		if (DEBUG) {Log.i(TAG, "Connected to mobile " + networkType);}
            		NetworkProfileManager.instance().changeNetworkProfile(NetworkProfile.Type.MOBILE, networkType);
                } else {
                	if (DEBUG) {Log.i(TAG, "Connected to other type of network " + activeNetwork.getType());}
                	NetworkProfileManager.instance().changeNetworkProfile(NetworkProfile.Type.MOBILE);
                }
            } else {
            	if (DEBUG) {Log.i(TAG, "Not connected or connecting");}
            	NetworkProfileManager.instance().changeNetworkProfile(NetworkProfile.Type.NO_CONNECTION);
            }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.service.NetworkProfileManager$NetworkBroadcastReceiver.checkNetwork(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager$NetworkBroadcastReceiver.checkNetwork(android.content.Context)",throwable);throw throwable;}
        }
	}
	
	public void makeToast(final String text) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.makeToast(com.chanapps.four.activity.String)",this,text);try{makeToast(text, Toast.LENGTH_SHORT);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.makeToast(com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.makeToast(com.chanapps.four.activity.String)",this,throwable);throw throwable;}
	}

    public void makeToast(final String text, final int length) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.makeToast(com.chanapps.four.activity.String,int)",this,text,length);try{final ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
        if (activity != null) {
            Handler handler = activity.getChanHandler();
            if (handler != null) {
                handler.postDelayed(new Runnable() {
                    public void run() {
                    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager$2.run()",this);try{try {
                    		if (DEBUG) {Log.w(TAG, "Calling toast with '" + text + "'");}
                    		Toast.makeText(activity.getBaseContext(), text, length).show();
                    	} catch (Exception e) {
                    		Log.e(TAG, "Error creating toast '" + text + "'", e);
                    	}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager$2.run()",this,throwable);throw throwable;}
                    }
                }, 300);
            } else {
                if (DEBUG) {Log.w(TAG, "Null handler for " + activity);}
            }
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.makeToast(com.chanapps.four.activity.String,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.makeToast(com.chanapps.four.activity.String,int)",this,throwable);throw throwable;}
    }

    public void makeToast(final int id) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager.makeToast(int)",this,id);try{final ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
        if (activity != null) {
            Handler handler = activity.getChanHandler();
            if (handler != null) {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.NetworkProfileManager$3.run()",this);try{if (DEBUG) {Log.w(TAG, "Calling toast with '" + id + "'");}
                        Toast.makeText(activity.getBaseContext(), id, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager$3.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager$3.run()",this,throwable);throw throwable;}
                    }
                }, 300);
            } else {
                if (DEBUG) {Log.w(TAG, "Null handler for " + activity);}
            }
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.NetworkProfileManager.makeToast(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.NetworkProfileManager.makeToast(int)",this,throwable);throw throwable;}
    }

    public static boolean isConnected() {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.service.NetworkProfileManager.isConnected()");try{NetworkProfile profile = NetworkProfileManager.instance().getCurrentProfile();
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.service.NetworkProfileManager.isConnected()");return profile.getConnectionType() != NetworkProfile.Type.NO_CONNECTION;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.service.NetworkProfileManager.isConnected()",throwable);throw throwable;}
    }

}
