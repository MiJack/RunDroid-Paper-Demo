package com.jtmcn.archwiki.viewer;

import android.content.Context;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.widget.ProgressBar;

import com.github.takahirom.webview_in_coodinator_layout.NestedWebView;
import com.jtmcn.archwiki.viewer.data.WikiPage;

import static com.jtmcn.archwiki.viewer.Constants.ARCHWIKI_MAIN;
import static com.jtmcn.archwiki.viewer.Constants.ARCHWIKI_SEARCH_URL;

public class WikiView extends NestedWebView implements SwipeRefreshLayout.OnRefreshListener {
	public static final String TAG = WikiView.class.getSimpleName();
	WikiClient wikiClient;
	private Context context;

	public WikiView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !isInEditMode()) {
			/*//this allows the webview to inject the css (otherwise it blocks it for security reasons)*/
			getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
	}

	/**
	 * Initializes the wiki client and loads the main page.
	 */
	public void buildView(ProgressBar progressBar, ActionBar actionBar) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiView.buildView(android.widget.ProgressBar,android.support.v7.app.ActionBar)",this,progressBar,actionBar);try{wikiClient = new WikiClient(progressBar, actionBar, this);
		setWebViewClient(wikiClient);
		wikiClient.shouldOverrideUrlLoading(this, ARCHWIKI_MAIN);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiView.buildView(android.widget.ProgressBar,android.support.v7.app.ActionBar)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiView.buildView(android.widget.ProgressBar,android.support.v7.app.ActionBar)",this,throwable);throw throwable;}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.WikiView.onKeyDown(int,android.view.KeyEvent)",this,keyCode,event);try{if (keyCode == KeyEvent.KEYCODE_BACK && wikiClient.getHistoryStackSize() > 1) {
			Log.i(TAG, "Loading previous page.");
			Log.d(TAG, "Position on page currently at " + getScrollY());
			wikiClient.goBackHistory();
			{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.WikiView.onKeyDown(int,android.view.KeyEvent)",this);return true;}
		} else {
			Log.d(TAG, "Passing up button press.");
			{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.WikiView.onKeyDown(int,android.view.KeyEvent)",this);return super.onKeyDown(keyCode, event);}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.WikiView.onKeyDown(int,android.view.KeyEvent)",this,throwable);throw throwable;}
	}

	/**
	 * Performs a search against the wiki.
	 *
	 * @param query the text to search for.
	 */
	public void passSearch(String query) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiView.passSearch(java.lang.String)",this,query);try{Log.d(TAG, "Searching for " + query);
		String searchUrl = String.format(ARCHWIKI_SEARCH_URL, query);
		wikiClient.shouldOverrideUrlLoading(this, searchUrl);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiView.passSearch(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiView.passSearch(java.lang.String)",this,throwable);throw throwable;}
	}

	/**
	 * Returns the current {@link WikiPage} being shown or null.
	 *
	 * @return current wiki page being shown.
	 */
	public WikiPage getCurrentWebPage() {
		com.mijack.Xlog.logMethodEnter("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.WikiView.getCurrentWebPage()",this);try{com.mijack.Xlog.logMethodExit("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.WikiView.getCurrentWebPage()",this);return wikiClient.getCurrentWebPage();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.WikiView.getCurrentWebPage()",this,throwable);throw throwable;}
	}

	@Override
	public void onRefresh() {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiView.onRefresh()",this);try{wikiClient.refreshPage();
		stopLoading();com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiView.onRefresh()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiView.onRefresh()",this,throwable);throw throwable;}
	}
}
