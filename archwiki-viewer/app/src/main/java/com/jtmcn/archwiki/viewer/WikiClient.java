package com.jtmcn.archwiki.viewer;

import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jtmcn.archwiki.viewer.data.WikiPage;
import com.jtmcn.archwiki.viewer.tasks.Fetch;
import com.jtmcn.archwiki.viewer.tasks.FetchUrl;
import com.jtmcn.archwiki.viewer.utils.AndroidUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import static com.jtmcn.archwiki.viewer.Constants.ARCHWIKI_BASE;
import static com.jtmcn.archwiki.viewer.Constants.TEXT_HTML_MIME;
import static com.jtmcn.archwiki.viewer.Constants.UTF_8;

public class WikiClient extends WebViewClient implements FetchUrl.OnFinish<WikiPage> {
	public static final String TAG = WikiClient.class.getSimpleName();
	private final WebView webView;
	private final Stack<WikiPage> webpageStack = new Stack<>();
	private final ProgressBar progressBar;
	private final ActionBar actionBar;
	private Set<String> loadedUrls = new HashSet<>(); /*// this is used to see if we should restore the scroll position*/
	private String lastLoadedUrl = null; /*//https://stackoverflow.com/questions/11601134/android-webview-function-onpagefinished-is-called-twice*/

	public WikiClient(ProgressBar progressBar, ActionBar actionBar, WebView wikiViewer) {
		this.progressBar = progressBar;
		this.actionBar = actionBar;
		webView = wikiViewer;
	}

	/*
	 * Manage page history
	 */
	public void addHistory(WikiPage wikiPage) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiClient.addHistory(com.jtmcn.archwiki.viewer.data.WikiPage)",this,wikiPage);try{if (webpageStack.size() > 0) {
			Log.d(TAG, "Saving " + getCurrentWebPage().getPageTitle() + " at " + webView.getScrollY());
			getCurrentWebPage().setScrollPosition(webView.getScrollY());
		}
		webpageStack.push(wikiPage);
		Log.i(TAG, "Adding page " + wikiPage.getPageTitle() + ". Stack size= " + webpageStack.size());com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiClient.addHistory(com.jtmcn.archwiki.viewer.data.WikiPage)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiClient.addHistory(com.jtmcn.archwiki.viewer.data.WikiPage)",this,throwable);throw throwable;}
	}

	/**
	 * Loads the html from a {@link WikiPage} into the webview.
	 *
	 * @param wikiPage the page to be loaded.
	 */
	public void loadWikiHtml(WikiPage wikiPage) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiClient.loadWikiHtml(com.jtmcn.archwiki.viewer.data.WikiPage)",this,wikiPage);try{webView.loadDataWithBaseURL(
				wikiPage.getPageUrl(),
				wikiPage.getHtmlString(),
				TEXT_HTML_MIME,
				UTF_8,
				null
		);

		setSubtitle(wikiPage.getPageTitle());com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiClient.loadWikiHtml(com.jtmcn.archwiki.viewer.data.WikiPage)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiClient.loadWikiHtml(com.jtmcn.archwiki.viewer.data.WikiPage)",this,throwable);throw throwable;}
	}

	/**
	 * Intercept url when clicked. If it's part of the wiki load it here.
	 * If not, open the device's default browser.
	 *
	 * @param view webview being loaded into
	 * @param url  url being loaded
	 * @return true if should override url loading
	 */
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.WikiClient.shouldOverrideUrlLoading(android.webkit.WebView,java.lang.String)",this,view,url);try{/*// deprecated until min api 21 is used*/
		if (url.startsWith(ARCHWIKI_BASE)) {
			webView.stopLoading();
			Fetch.page(this, url, true);
			showProgress();

			{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.WikiClient.shouldOverrideUrlLoading(android.webkit.WebView,java.lang.String)",this);return false;}
		} else {
			AndroidUtils.openLink(url, view.getContext());
			{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.WikiClient.shouldOverrideUrlLoading(android.webkit.WebView,java.lang.String)",this);return true;}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.WikiClient.shouldOverrideUrlLoading(android.webkit.WebView,java.lang.String)",this,throwable);throw throwable;}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiClient.onPageFinished(android.webkit.WebView,java.lang.String)",this,view,url);try{super.onPageFinished(view, url);
		final WikiPage currentWebPage = getCurrentWebPage();
		Log.d(TAG, "Calling onPageFinished(view, " + currentWebPage.getPageTitle() + ")");
		/*// make sure we're loading the current page and that*/
		/*// this page's url doesn't have an anchor (only on first page load)*/
		if (url.equals(currentWebPage.getPageUrl()) && !url.equals(lastLoadedUrl)) {
			if (!isFirstLoad(currentWebPage)) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiClient$1.run()",this);try{int scrollY = currentWebPage.getScrollPosition();
						Log.d(TAG, "Restoring " + currentWebPage.getPageTitle() + " at " + scrollY);
						webView.setScrollY(scrollY);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiClient$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiClient$1.run()",this,throwable);throw throwable;}
					}
				}, 25);
			}

			lastLoadedUrl = url;
			hideProgress();
		}com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiClient.onPageFinished(android.webkit.WebView,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiClient.onPageFinished(android.webkit.WebView,java.lang.String)",this,throwable);throw throwable;}
	}

	private boolean isFirstLoad(WikiPage currentWebPage) {
		com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.WikiClient.isFirstLoad(com.jtmcn.archwiki.viewer.data.WikiPage)",this,currentWebPage);try{if (loadedUrls.contains(currentWebPage.getPageUrl())) {
			{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.WikiClient.isFirstLoad(com.jtmcn.archwiki.viewer.data.WikiPage)",this);return false;}
		} else {
			loadedUrls.add(currentWebPage.getPageUrl());
			{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.WikiClient.isFirstLoad(com.jtmcn.archwiki.viewer.data.WikiPage)",this);return true;}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.WikiClient.isFirstLoad(com.jtmcn.archwiki.viewer.data.WikiPage)",this,throwable);throw throwable;}
	}

	public void showProgress() {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiClient.showProgress()",this);try{progressBar.setVisibility(View.VISIBLE);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiClient.showProgress()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiClient.showProgress()",this,throwable);throw throwable;}
	}

	public void hideProgress() {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiClient.hideProgress()",this);try{progressBar.setVisibility(View.GONE);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiClient.hideProgress()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiClient.hideProgress()",this,throwable);throw throwable;}
	}

	public void setSubtitle(String title) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiClient.setSubtitle(java.lang.String)",this,title);try{if (actionBar != null) {
			actionBar.setSubtitle(title);
		}com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiClient.setSubtitle(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiClient.setSubtitle(java.lang.String)",this,throwable);throw throwable;}
	}

	/**
	 * Get the number of pages that are in the history.
	 *
	 * @return number of pages on the stack.
	 */
	public int getHistoryStackSize() {
		com.mijack.Xlog.logMethodEnter("int com.jtmcn.archwiki.viewer.WikiClient.getHistoryStackSize()",this);try{com.mijack.Xlog.logMethodExit("int com.jtmcn.archwiki.viewer.WikiClient.getHistoryStackSize()",this);return webpageStack.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.jtmcn.archwiki.viewer.WikiClient.getHistoryStackSize()",this,throwable);throw throwable;}
	}

	/**
	 * Go back to the last loaded page.
	 */
	public void goBackHistory() {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiClient.goBackHistory()",this);try{WikiPage removed = webpageStack.pop();
		loadedUrls.remove(removed.getPageUrl());
		Log.i(TAG, "Removing " + removed.getPageTitle() + " from stack");
		WikiPage newPage = webpageStack.peek();
		loadWikiHtml(newPage);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiClient.goBackHistory()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiClient.goBackHistory()",this,throwable);throw throwable;}
	}

	/**
	 * Returns null or the current page.
	 *
	 * @return The current page
	 */
	public WikiPage getCurrentWebPage() {
		com.mijack.Xlog.logMethodEnter("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.WikiClient.getCurrentWebPage()",this);try{com.mijack.Xlog.logMethodExit("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.WikiClient.getCurrentWebPage()",this);return webpageStack.size() == 0 ? null : webpageStack.peek();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.WikiClient.getCurrentWebPage()",this,throwable);throw throwable;}
	}

	@Override
	public void onFinish(WikiPage results) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiClient.onFinish(com.jtmcn.archwiki.viewer.data.WikiPage)",this,results);try{addHistory(results);
		loadWikiHtml(getCurrentWebPage());com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiClient.onFinish(com.jtmcn.archwiki.viewer.data.WikiPage)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiClient.onFinish(com.jtmcn.archwiki.viewer.data.WikiPage)",this,throwable);throw throwable;}
	}

	public void refreshPage() {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiClient.refreshPage()",this);try{lastLoadedUrl = null; /*// set to null if page should restore position, otherwise start at top of page*/
		WikiPage currentWebPage = getCurrentWebPage();
		if (currentWebPage != null) {
			final int scrollPosition = currentWebPage.getScrollPosition();

			String url = currentWebPage.getPageUrl();
			showProgress();
			Fetch.page(new FetchUrl.OnFinish<WikiPage>() {
				@Override
				public void onFinish(WikiPage wikiPage) {
					com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.WikiClient$2.onFinish(com.jtmcn.archwiki.viewer.data.WikiPage)",this,wikiPage);try{webpageStack.pop();
					webpageStack.push(wikiPage);
					wikiPage.setScrollPosition(scrollPosition);
					loadWikiHtml(wikiPage);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiClient$2.onFinish(com.jtmcn.archwiki.viewer.data.WikiPage)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiClient$2.onFinish(com.jtmcn.archwiki.viewer.data.WikiPage)",this,throwable);throw throwable;}
				}
			}, url, false);
		}com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.WikiClient.refreshPage()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.WikiClient.refreshPage()",this,throwable);throw throwable;}
	}
}