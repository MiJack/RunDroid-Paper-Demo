package net.bitplane.android.microphone;

import java.nio.ByteBuffer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class MicrophoneService extends Service implements OnSharedPreferenceChangeListener {
	
	private static final String APP_TAG = "Microphone";
	private static final int mSampleRate = 44100;
	private static final int mFormat     = AudioFormat.ENCODING_PCM_16BIT;
	
	private AudioTrack              mAudioOutput;
	private AudioRecord             mAudioInput;
	private int                     mInBufferSize;
	private int                     mOutBufferSize;
	SharedPreferences               mSharedPreferences;
	private static boolean          mActive = false;
	private NotificationManager     mNotificationManager;
	private MicrophoneReceiver      mBroadcastReceiver;
	
	private class MicrophoneReceiver extends BroadcastReceiver {
	    /*// Turn the mic off when things get loud*/
	    @Override 
	    public void onReceive(Context context, Intent intent) {
	        com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneService$MicrophoneReceiver.onReceive(android.content.Context,android.app.PendingIntent)",this,context,intent);try{String action = intent.getAction();
	    	if (action != null && action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
	    	
	    		SharedPreferences prefs = context.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE);
	    	
	    		SharedPreferences.Editor e = prefs.edit();
	    		e.putBoolean("active", false);
	    		e.commit();
	    	}com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService$MicrophoneReceiver.onReceive(android.content.Context,android.app.PendingIntent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneService$MicrophoneReceiver.onReceive(android.content.Context,android.app.PendingIntent)",this,throwable);throw throwable;}
	   }
	}   
	
	@Override
	public IBinder onBind(Intent intent) {
		com.mijack.Xlog.logMethodEnter("android.os.IBinder net.bitplane.android.microphone.MicrophoneService.onBind(android.app.PendingIntent)",this,intent);try{/*// TODO Auto-generated method stub*/
		{com.mijack.Xlog.logMethodExit("android.os.IBinder net.bitplane.android.microphone.MicrophoneService.onBind(android.app.PendingIntent)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.IBinder net.bitplane.android.microphone.MicrophoneService.onBind(android.app.PendingIntent)",this,throwable);throw throwable;}
	}
	
    @Override
    public void onCreate() {
    	com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneService.onCreate()",this);try{
    	Log.d(APP_TAG, "Creating mic service");
    	
    	/*// notification service*/
/*//    	mNotificationManager  = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/
    	
    	mBroadcastReceiver = new MicrophoneReceiver();
    	
    	/*// create input and output streams*/
        mInBufferSize  = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, mFormat);
        mOutBufferSize = AudioTrack.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, mFormat);
        mAudioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, mSampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, mFormat, mInBufferSize);
        mAudioOutput = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, mFormat, mOutBufferSize, AudioTrack.MODE_STREAM);
    	
    	/*// listen for preference changes*/
    	mSharedPreferences = getSharedPreferences(APP_TAG, MODE_PRIVATE);
    	mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    	mActive = mSharedPreferences.getBoolean("active", false);
    	    	
    	if (mActive)
    		{record();}com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService.onCreate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneService.onCreate()",this,throwable);throw throwable;}
    }
    
    @Override
    public void onDestroy() {
    	com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneService.onDestroy()",this);try{Log.d(APP_TAG, "Stopping mic service"); 
    	
    	/*// close the service*/
    	SharedPreferences.Editor e = mSharedPreferences.edit();
    	e.putBoolean("active", false);
    	e.commit();
    	
    	/*// disable the listener*/
    	mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    	
    	mAudioInput.release();
    	mAudioOutput.release();com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneService.onDestroy()",this,throwable);throw throwable;}
    }
    
	@Override
    public void onStart(Intent intent, int startId) {
		com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneService.onStart(android.app.PendingIntent,int)",this,intent,startId);try{super.onStart(intent, startId);
		Log.d(APP_TAG, "Service sent intent");
		
		/*// if this is a stop request, cancel the recording*/
		if (intent != null && intent.getAction() != null) {
			if (intent.getAction().equals("net.bitplane.android.microphone.STOP")) {
				Log.d(APP_TAG, "Cancelling recording via notification click");
				SharedPreferences.Editor e = mSharedPreferences.edit();
	        	e.putBoolean("active", false);
	        	e.commit();
			}
		}com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService.onStart(android.app.PendingIntent,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneService.onStart(android.app.PendingIntent,int)",this,throwable);throw throwable;}
	}
    
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneService.onSharedPreferenceChanged(android.content.SharedPreferences,java.lang.String)",this,sharedPreferences,key);try{/*// intercept the preference change.*/
		
		if (!key.equals("active"))
			{{com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService.onSharedPreferenceChanged(android.content.SharedPreferences,java.lang.String)",this);return;}}
		
		boolean bActive = sharedPreferences.getBoolean("active", false);
		
		Log.d(APP_TAG, "Mic state changing (from " + mActive + " to " + bActive + ")"); 
		
		if (bActive != mActive) {
		
			mActive = bActive;
			
			if (mActive)
				{record();}
			
			if (!mActive){;}
/*//				mNotificationManager.cancel(0);*/
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneService.onSharedPreferenceChanged(android.content.SharedPreferences,java.lang.String)",this,throwable);throw throwable;}
	}
	
	public void record() {
		com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneService.record()",this);try{Thread t = new Thread() {
			public void run() {
				com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneService$1.run()",this);try{
/*//				Context       context             = getApplicationContext();*/
/*//				CharSequence  titleText           = getString(R.string.mic_active);*/
/*//				CharSequence  statusText          = getString(R.string.cancel_mic);*/
/*//		        long          when                = System.currentTimeMillis();*/
/*//		        Intent        cancelIntent        = new Intent();*/
/*//		        cancelIntent.setAction("net.bitplane.android.microphone.STOP");*/
/*//		        cancelIntent.setData(Uri.parse("null://null"));*/
/*//		        cancelIntent.setFlags(cancelIntent.getFlags() | Notification.FLAG_AUTO_CANCEL);*/
/*//		        PendingIntent pendingCancelIntent = PendingIntent.getService(context, 0, cancelIntent, 0);*/
/*//		        Notification notification         = new Notification(R.drawable.status, titleText, when);*/
/*//				notification.setLatestEventInfo(context, titleText, statusText, pendingCancelIntent);*/
/*//				mNotificationManager.notify(0, notification);*/
				
				/*// allow the */
				registerReceiver(mBroadcastReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
				
				Log.d(APP_TAG, "Entered record loop");
				
				recordLoop();
				
				Log.d(APP_TAG, "Record loop finished");com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneService$1.run()",this,throwable);throw throwable;}
			}
			
			private void recordLoop() {
				com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneService$1.recordLoop()",this);try{if ( mAudioOutput.getState() != AudioTrack.STATE_INITIALIZED || mAudioInput.getState() != AudioTrack.STATE_INITIALIZED) {
					Log.d(APP_TAG, "Can't start. Race condition?");
				}
				else {
					
					try {
					
						try { mAudioOutput.play(); }          catch (Exception e) { Log.e(APP_TAG, "Failed to start playback"); {com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService.record()",this);{com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService$1.recordLoop()",this);return;}} }
						try { mAudioInput.startRecording(); } catch (Exception e) { Log.e(APP_TAG, "Failed to start recording"); mAudioOutput.stop(); {com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService.record()",this);{com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService$1.recordLoop()",this);return;}} }
						
						try {
							
					        ByteBuffer bytes = ByteBuffer.allocateDirect(mInBufferSize);
					        int o = 0;
					        byte b[] = new byte[mInBufferSize];
					        while(mActive) {
					        	o = mAudioInput.read(bytes, mInBufferSize);
					        	bytes.get(b);
					        	bytes.rewind();
					        	mAudioOutput.write(b, 0, o);
					        }
					        
					        Log.d(APP_TAG, "Finished recording");
						}
						catch (Exception e) {
							Log.d(APP_TAG, "Error while recording, aborting.");
						}
			        
				        try { mAudioOutput.stop(); } catch (Exception e) { Log.e(APP_TAG, "Can't stop playback"); mAudioInput.stop(); {com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService.record()",this);{com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService$1.recordLoop()",this);return;}} }
				        try { mAudioInput.stop();  } catch (Exception e) { Log.e(APP_TAG, "Can't stop recording"); {com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService.record()",this);{com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneService$1.recordLoop()",this);return;}} }
					}
					catch (Exception e) {
						Log.d(APP_TAG, "Error somewhere in record loop.");				
					}
				}
				/*// cancel notification and receiver*/
/*//				mNotificationManager.cancel(0);*/
				try {
					unregisterReceiver(mBroadcastReceiver);
				} catch (IllegalArgumentException e) { Log.e(APP_TAG, "Receiver wasn't registered: " + e.toString()); }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneService$1.recordLoop()",this,throwable);throw throwable;}
			}
		};
		
		t.start();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneService.record()",this,throwable);throw throwable;}
		
	}
}
