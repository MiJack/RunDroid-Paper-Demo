package net.bitplane.android.microphone;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;

public class MicrophoneActivity extends Activity implements OnSharedPreferenceChangeListener, OnClickListener {
	
	private static final String APP_TAG         = "Microphone";
	private static final int    ABOUT_DIALOG_ID = 0;
	
	SharedPreferences mSharedPreferences;
	boolean           mActive = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        
        Log.d(APP_TAG, "Opening mic activity");
        
        /*// listen for preference changes*/
    	mSharedPreferences = getSharedPreferences(APP_TAG, MODE_PRIVATE);
    	mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    	
    	/*// listen for preference changes*/
    	mSharedPreferences = getSharedPreferences(APP_TAG, MODE_PRIVATE);
    	mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        
    	mActive = mSharedPreferences.getBoolean("active", false);
    	if (mActive)
    		{startService(new Intent(this, MicrophoneService.class));}
    	
    	setContentView(R.layout.main);
    	
    	ImageButton b = (ImageButton)findViewById(R.id.RecordButton);
    	b.setOnClickListener(this);
    	b.setImageBitmap(BitmapFactory.decodeResource(getResources(), mActive ? R.drawable.red : R.drawable.mic));
    	
        int lastVersion = mSharedPreferences.getInt("lastVersion", 0);
        int thisVersion = -1;
        try {
        	thisVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
        	e.printStackTrace();
        }
        
        if (lastVersion != thisVersion) {
        	SharedPreferences.Editor e = mSharedPreferences.edit();
        	e.putInt("lastVersion", thisVersion);
        	e.commit();
        	showDialog(ABOUT_DIALOG_ID);
        }com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    	
    }
    
    @Override
    public void onDestroy() {
    	com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneActivity.onDestroy()",this);try{super.onDestroy();
    	
    	Log.d(APP_TAG, "Closing mic activity");
    	
    	mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneActivity.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneActivity.onDestroy()",this,throwable);throw throwable;}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean net.bitplane.android.microphone.MicrophoneActivity.onCreateOptionsMenu(android.view.Menu)",this,menu);try{MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        {com.mijack.Xlog.logMethodExit("boolean net.bitplane.android.microphone.MicrophoneActivity.onCreateOptionsMenu(android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean net.bitplane.android.microphone.MicrophoneActivity.onCreateOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean net.bitplane.android.microphone.MicrophoneActivity.onOptionsItemSelected(android.view.MenuItem)",this,item);try{/*// Handle item selection*/
        switch (item.getItemId()) {
        case R.id.about:
        	showDialog(ABOUT_DIALOG_ID);
        	{com.mijack.Xlog.logMethodExit("boolean net.bitplane.android.microphone.MicrophoneActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
        default:
            {com.mijack.Xlog.logMethodExit("boolean net.bitplane.android.microphone.MicrophoneActivity.onOptionsItemSelected(android.view.MenuItem)",this);return super.onOptionsItemSelected(item);}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean net.bitplane.android.microphone.MicrophoneActivity.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    @Override 
    public Dialog onCreateDialog(int id) {
    	com.mijack.Xlog.logMethodEnter("android.app.AlertDialog net.bitplane.android.microphone.MicrophoneActivity.onCreateDialog(int)",this,id);try{Dialog dialog = null;
    	switch (id) {
    	case ABOUT_DIALOG_ID:
    		Builder b = new Builder(this);
    		b.setTitle(getString(R.string.about));
    		
    		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    		View aboutView = inflater.inflate(R.layout.about, (ViewGroup)findViewById(R.id.AboutWebView));
    		
    		b.setView(aboutView);
    		
    		String data = "";
    		
    		InputStream in = getApplicationContext().getResources().openRawResource(R.raw.about);
    		try {
	    		int ch;
	    		StringBuffer buf = new StringBuffer();
	    		while( ( ch = in.read() ) != -1 ){
	    			buf.append( (char)ch );
	    		}
	    		data = buf.toString();
    		}
    		catch (IOException e) {
    			/*// this is fucking silly. do something nicer than this shit method*/
    		}
    		
    		WebView wv = (WebView)aboutView.findViewById(R.id.AboutWebView);
    		wv.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
    		
    		dialog = b.create();
    		
    		break;
    	}
    	{com.mijack.Xlog.logMethodExit("android.app.AlertDialog net.bitplane.android.microphone.MicrophoneActivity.onCreateDialog(int)",this);return dialog;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog net.bitplane.android.microphone.MicrophoneActivity.onCreateDialog(int)",this,throwable);throw throwable;}
    }
    
	public void onClick(View v) {
		com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneActivity.onClick(android.view.View)",this,v);try{if (v.getId() == R.id.RecordButton) {
			SharedPreferences.Editor e = mSharedPreferences.edit();
			e.putBoolean("active", !mActive);
			e.commit();
		}com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneActivity.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneActivity.onClick(android.view.View)",this,throwable);throw throwable;}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneActivity.onSharedPreferenceChanged(android.content.SharedPreferences,java.lang.String)",this,sharedPreferences,key);try{/*// intercept the preference change.*/
		
		if (key.equals("active")) {
			boolean bActive = sharedPreferences.getBoolean("active", false);

			if (bActive != mActive) {
				if (bActive) {
					startService(new Intent(this, MicrophoneService.class));
				}
				else {
					stopService(new Intent(this, MicrophoneService.class));
				}
				mActive = bActive;
				runOnUiThread(	new Runnable() {
									public void run() {
										com.mijack.Xlog.logMethodEnter("void net.bitplane.android.microphone.MicrophoneActivity$1.run()",this);try{ImageButton b = (ImageButton)findViewById(R.id.RecordButton);
										b.setImageBitmap(BitmapFactory.decodeResource(getResources(), mActive ? R.drawable.red : R.drawable.mic));com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneActivity$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneActivity$1.run()",this,throwable);throw throwable;}						
									}
								});
			}
		}com.mijack.Xlog.logMethodExit("void net.bitplane.android.microphone.MicrophoneActivity.onSharedPreferenceChanged(android.content.SharedPreferences,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void net.bitplane.android.microphone.MicrophoneActivity.onSharedPreferenceChanged(android.content.SharedPreferences,java.lang.String)",this,throwable);throw throwable;}
		
	}
    
}