/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chanapps.four.service;

import java.net.HttpURLConnection;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Base service class based on IntentService class
 */
public abstract class BaseChanService extends Service {
	private static final String TAG = BaseChanService.class.getSimpleName();
    private static final boolean DEBUG = false;

    protected static int NON_PRIORITY_MESSAGE = 99;
    protected static int PRIORITY_MESSAGE = 100;
    
    protected static final int MAX_NON_PRIORITY_MESSAGES = 10;
    protected static final int MAX_PRIORITY_MESSAGES = 0;
    public static final String CLEAR_FETCH_QUEUE = "clearFetchQueue";
    public static final String PRIORITY_MESSAGE_FETCH = "priorityFetch";
    public static final String BACKGROUND_LOAD = "backgroundLoad";

    protected int nonPriorityMessageCounter = 0;
    protected int priorityMessageCounter = 0;

    protected volatile Looper mServiceLooper;
    protected volatile ServiceHandler mServiceHandler;
    private String mName;

    protected void toastUI(final String string) {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.BaseChanService.toastUI(java.lang.String)",this,string);try{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.BaseChanService.toastUI(java.lang.String)",this);try {
	        Handler handler = NetworkProfileManager.instance().getActivity().getChanHandler();
	        if (handler == null) {
	        	return;
	        }
	        handler.post(new Runnable() {
	            public void run() {
	                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.BaseChanService$1.run()",this);try{Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.BaseChanService$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.BaseChanService$1.run()",this,throwable);throw throwable;}
	            }
	        });
    	} catch (Exception e) {
    		/*// we don't want to log that*/
    	}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.BaseChanService.toastUI(java.lang.String)",this,throwable);throw throwable;}
    }

    protected final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
        	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.BaseChanService$ServiceHandler.handleMessage(android.os.Message)",this,msg);try{Intent intent = (Intent)msg.obj;
        	if (intent != null && intent.getIntExtra(PRIORITY_MESSAGE_FETCH, 0) == 1) {
        		synchronized(this) {
        			priorityMessageCounter--;
        			if (priorityMessageCounter < 0) {
        				priorityMessageCounter = 0;
        			}
        		}
        	} else {
        		synchronized(this) {
        			nonPriorityMessageCounter--;
        			if (nonPriorityMessageCounter < 0) {
        				nonPriorityMessageCounter = 0;
        			}
        		}
        	}
            if (intent != null)
                {onHandleIntent(intent);}
            stopSelf(msg.arg1);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.BaseChanService$ServiceHandler.handleMessage(android.os.Message)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.BaseChanService$ServiceHandler.handleMessage(android.os.Message)",this,throwable);throw throwable;}
        }        
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseChanService(String name) {
        super();
        mName = name;
    }

    @Override
    public void onCreate() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.BaseChanService.onCreate()",this);try{super.onCreate();
        HandlerThread thread = new HandlerThread("ChanService[" + mName + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.BaseChanService.onCreate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.BaseChanService.onCreate()",this,throwable);throw throwable;}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	com.mijack.Xlog.logMethodEnter("int com.chanapps.four.service.BaseChanService.onStartCommand(android.content.Intent,int,int)",this,intent,flags,startId);try{if (nonPriorityMessageCounter > MAX_NON_PRIORITY_MESSAGES) {
    		if (DEBUG) {Log.i(TAG, "Clearing chan fetch service message queue from non priority messages (" + nonPriorityMessageCounter + ")");}
        	mServiceHandler.removeMessages(NON_PRIORITY_MESSAGE);
        	synchronized(this) {
        		nonPriorityMessageCounter = 0;
        	}
    	}
    	if (priorityMessageCounter > MAX_PRIORITY_MESSAGES) {
    		if (DEBUG) {Log.i(TAG, "Clearing chan fetch service message queue from priority messages (" + priorityMessageCounter + ")");}
        	mServiceHandler.removeMessages(PRIORITY_MESSAGE);
        	synchronized(this) {
        		priorityMessageCounter = 0;
        	}
    	}
        if (intent != null && intent.getIntExtra(CLEAR_FETCH_QUEUE, 0) == 1) {
            if (DEBUG) {Log.i(TAG, "Clearing chan fetch service message queue");}
        	mServiceHandler.removeMessages(NON_PRIORITY_MESSAGE);
        	synchronized(this) {
        		nonPriorityMessageCounter = 0;
        	}
        	mServiceHandler.removeMessages(PRIORITY_MESSAGE);
        	synchronized(this) {
        		priorityMessageCounter = 0;
        	}
        	{com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.BaseChanService.onStartCommand(android.content.Intent,int,int)",this);return START_NOT_STICKY;}
        }
        
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        if (intent != null && intent.getIntExtra(PRIORITY_MESSAGE_FETCH, 0) == 1) {
        	msg.what = PRIORITY_MESSAGE;
        	mServiceHandler.sendMessageAtFrontOfQueue(msg);
        	synchronized(this) {
        		priorityMessageCounter++;
        	}
        } else {
        	msg.what = NON_PRIORITY_MESSAGE;
        	mServiceHandler.sendMessage(msg);
        	synchronized(this) {
        		nonPriorityMessageCounter++;
        	}
        }
        
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.BaseChanService.onStartCommand(android.content.Intent,int,int)",this);return START_NOT_STICKY;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.service.BaseChanService.onStartCommand(android.content.Intent,int,int)",this,throwable);throw throwable;}
    }
	
    /*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }
    */

    @Override
    public void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.BaseChanService.onDestroy()",this);try{mServiceLooper.quit();com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.BaseChanService.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.BaseChanService.onDestroy()",this,throwable);throw throwable;}
    }

    /**
     * Unless you provide binding for your service, you don't need to implement this
     * method, because the default implementation returns null. 
     * @see android.app.Service#onBind
     */
    @Override
    public IBinder onBind(Intent intent) {
        com.mijack.Xlog.logMethodEnter("android.os.IBinder com.chanapps.four.service.BaseChanService.onBind(android.content.Intent)",this,intent);try{com.mijack.Xlog.logMethodExit("android.os.IBinder com.chanapps.four.service.BaseChanService.onBind(android.content.Intent)",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.IBinder com.chanapps.four.service.BaseChanService.onBind(android.content.Intent)",this,throwable);throw throwable;}
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(Intent)}.
     */
    protected abstract void onHandleIntent(Intent intent);

	protected void closeConnection(HttpURLConnection tc) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.BaseChanService.closeConnection(java.net.HttpURLConnection)",this,tc);try{if (tc != null) {
			try {
		        tc.disconnect();
			} catch (Exception e) {
				Log.e(TAG, "Error closing connection", e);
			} finally {
                tc = null;
            }
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.BaseChanService.closeConnection(java.net.HttpURLConnection)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.BaseChanService.closeConnection(java.net.HttpURLConnection)",this,throwable);throw throwable;}
	}
	
}
