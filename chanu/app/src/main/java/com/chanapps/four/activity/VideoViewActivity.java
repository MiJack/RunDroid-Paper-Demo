/**
 * 
 */
package com.chanapps.four.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;

import com.chanapps.four.service.ImageDownloadService;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class VideoViewActivity extends Activity {
	public static final String TAG = "VideoView";
    private static final boolean DEBUG = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.VideoViewActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.video_view_layout);
    	WebView myWebView = (WebView) findViewById(R.id.video_view);
    	myWebView.getRootView().setBackgroundColor(0xffffff);
    	getWindow().getDecorView().setBackgroundColor(0xffffff);

    	setActionBarTitle();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.VideoViewActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.VideoViewActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }
    
    @Override
	protected void onResume () {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.VideoViewActivity.onResume()",this);try{super.onResume();
    	play();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.VideoViewActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.VideoViewActivity.onResume()",this,throwable);throw throwable;}
    }
    
    private void setActionBarTitle() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.VideoViewActivity.setActionBarTitle()",this);try{if (getActionBar() != null) {
            getActionBar().setTitle(getIntent().getStringExtra(Intent.EXTRA_TITLE));
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.VideoViewActivity.setActionBarTitle()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.VideoViewActivity.setActionBarTitle()",this,throwable);throw throwable;}
    }
    
    private void play() {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.VideoViewActivity.play()",this);try{try {
	    	WebView myWebView = (WebView) findViewById(R.id.video_view);
	    	myWebView.getRootView().setBackgroundColor(0xffffff);
	    	myWebView.setBackgroundColor(0xffffff);
	    	myWebView.getSettings().setJavaScriptEnabled(false);
	    	myWebView.getSettings().setBuiltInZoomControls(false);
	    	String html = "<html><body bgcolor=\"black\"><center><img src=\"" + getIntent().getStringExtra(ImageDownloadService.IMAGE_URL)
	    			+ "\"></img></body></html>";
	    	myWebView.loadDataWithBaseURL("/", html, "text/html", "UTF-8", null);
    	} catch (Throwable e) {
    		Log.e(TAG, "Web view loading error", e);
    	}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.VideoViewActivity.play()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.VideoViewActivity.play()",this,throwable);throw throwable;}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.VideoViewActivity.onOptionsItemSelected(android.view.MenuItem)",this,item);try{switch (item.getItemId()) {
            case android.R.id.home:
            	onBackPressed();
        }
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.VideoViewActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.VideoViewActivity.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    @Override
    public void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.VideoViewActivity.onStart()",this);try{super.onStart();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.VideoViewActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.VideoViewActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    public void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.VideoViewActivity.onStop()",this);try{super.onStop();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.VideoViewActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.VideoViewActivity.onStop()",this,throwable);throw throwable;}
    }

}
