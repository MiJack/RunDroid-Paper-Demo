package com.jtmcn.archwiki.viewer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ProgressBar;

import com.jtmcn.archwiki.viewer.data.SearchResult;
import com.jtmcn.archwiki.viewer.data.SearchResultsBuilder;
import com.jtmcn.archwiki.viewer.data.WikiPage;
import com.jtmcn.archwiki.viewer.tasks.Fetch;
import com.jtmcn.archwiki.viewer.tasks.FetchUrl;
import com.jtmcn.archwiki.viewer.utils.AndroidUtils;
import com.jtmcn.archwiki.viewer.utils.SettingsUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FetchUrl.OnFinish<List<SearchResult>> {
	public static final String TAG = MainActivity.class.getSimpleName();
	@BindView(R.id.wiki_view) WikiView wikiViewer;
	@BindView(R.id.toolbar) Toolbar toolbar;
	private ShareActionProvider shareActionProvider;
	private SearchView searchView;
	private MenuItem searchMenuItem;
	private List<SearchResult> currentSuggestions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.MainActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		setSupportActionBar(toolbar);

		ProgressBar progressBar = ButterKnife.findById(this, R.id.progress_bar);
		wikiViewer.buildView(progressBar, getSupportActionBar());

		handleIntent(getIntent());com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.MainActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.MainActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
	}

	@Override
	protected void onResume() {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.MainActivity.onResume()",this);try{super.onResume();
		updateWebSettings();com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.MainActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.MainActivity.onResume()",this,throwable);throw throwable;}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.MainActivity.onNewIntent(android.content.Intent)",this,intent);try{handleIntent(intent);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.MainActivity.onNewIntent(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.MainActivity.onNewIntent(android.content.Intent)",this,throwable);throw throwable;}
	}

	private void handleIntent(Intent intent) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.MainActivity.handleIntent(android.content.Intent)",this,intent);try{if (intent == null) {
			{com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.MainActivity.handleIntent(android.content.Intent)",this);return;}
		}

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			wikiViewer.passSearch(query);
			hideSearchView();
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			final String url = intent.getDataString();
			wikiViewer.wikiClient.shouldOverrideUrlLoading(wikiViewer, url);
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.MainActivity.handleIntent(android.content.Intent)",this,throwable);throw throwable;}
	}

	/**
	 * Update the font size used in the webview.
	 */
	public void updateWebSettings() {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.MainActivity.updateWebSettings()",this);try{WebSettings webSettings = wikiViewer.getSettings();
		int fontSize = SettingsUtils.getFontSize(this);

		/*//todo this setting should be changed to a slider, remove deprecated call*/
		/*// deprecated method must be used until Android API 14*/
		/*// https://developer.android.com/reference/android/webkit/WebSettings.TextSize.html#NORMAL*/
		switch (fontSize) {
			case 0:
				webSettings.setTextSize(WebSettings.TextSize.SMALLEST); /*//50%*/
				break;
			case 1:
				webSettings.setTextSize(WebSettings.TextSize.SMALLER); /*//75%*/
				break;
			case 2:
				webSettings.setTextSize(WebSettings.TextSize.NORMAL); /*//100%*/
				break;
			case 3:
				webSettings.setTextSize(WebSettings.TextSize.LARGER); /*//150%*/
				break;
			case 4:
				webSettings.setTextSize(WebSettings.TextSize.LARGEST); /*//200%*/
				break;
		}com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.MainActivity.updateWebSettings()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.MainActivity.updateWebSettings()",this,throwable);throw throwable;}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.MainActivity.onPrepareOptionsMenu(android.view.Menu)",this,menu);try{SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchMenuItem = menu.findItem(R.id.menu_search);
		searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
		searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.MainActivity$1.onFocusChange(android.support.v7.widget.SearchView,boolean)",this,v,hasFocus);try{if (!hasFocus) {
					hideSearchView();
				}com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.MainActivity$1.onFocusChange(android.support.v7.widget.SearchView,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.MainActivity$1.onFocusChange(android.support.v7.widget.SearchView,boolean)",this,throwable);throw throwable;}
			}
		});
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.MainActivity$2.onQueryTextSubmit(java.lang.String)",this,query);try{wikiViewer.passSearch(query);
				{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity.onPrepareOptionsMenu(android.view.Menu)",this);{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity$2.onQueryTextSubmit(java.lang.String)",this);return false;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.MainActivity$2.onQueryTextSubmit(java.lang.String)",this,throwable);throw throwable;}
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.MainActivity$2.onQueryTextChange(java.lang.String)",this,newText);try{if (newText.isEmpty()) {
					setCursorAdapter(new ArrayList<SearchResult>());
					{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity.onPrepareOptionsMenu(android.view.Menu)",this);{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity$2.onQueryTextChange(java.lang.String)",this);return true;}}
				} else {
					String searchUrl = SearchResultsBuilder.getSearchQuery(newText);
					Fetch.search(MainActivity.this, searchUrl);
					{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity.onPrepareOptionsMenu(android.view.Menu)",this);{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity$2.onQueryTextChange(java.lang.String)",this);return true;}}
				}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.MainActivity$2.onQueryTextChange(java.lang.String)",this,throwable);throw throwable;}
			}
		});

		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionSelect(int position) {
				com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.MainActivity$3.onSuggestionSelect(int)",this,position);try{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity$3.onSuggestionSelect(int)",this);{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity.onPrepareOptionsMenu(android.view.Menu)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.MainActivity$3.onSuggestionSelect(int)",this,throwable);throw throwable;}
			}

			@Override
			public boolean onSuggestionClick(int position) {
				com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.MainActivity$3.onSuggestionClick(int)",this,position);try{SearchResult searchResult = currentSuggestions.get(position);
				Log.d(TAG, "Opening '" + searchResult.getPageName() + "' from search suggestion.");
				wikiViewer.wikiClient.shouldOverrideUrlLoading(wikiViewer, searchResult.getPageUrl());
				hideSearchView();
				{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity.onPrepareOptionsMenu(android.view.Menu)",this);{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity$3.onSuggestionClick(int)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.MainActivity$3.onSuggestionClick(int)",this,throwable);throw throwable;}
			}
		});
		{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity.onPrepareOptionsMenu(android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.MainActivity.onPrepareOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
	}

	public void hideSearchView() {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.MainActivity.hideSearchView()",this);try{searchMenuItem.collapseActionView();
		wikiViewer.requestFocus(); /*//pass control back to the wikiview*/com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.MainActivity.hideSearchView()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.MainActivity.hideSearchView()",this,throwable);throw throwable;}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.MainActivity.onCreateOptionsMenu(android.view.Menu)",this,menu);try{getMenuInflater().inflate(R.menu.menu, menu);
		MenuItem share = menu.findItem(R.id.menu_share);
		shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);
		{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity.onCreateOptionsMenu(android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.MainActivity.onCreateOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.MainActivity.onOptionsItemSelected(android.view.MenuItem)",this,item);try{switch (item.getItemId()) {
			case R.id.menu_share:
				WikiPage wikiPage = wikiViewer.getCurrentWebPage();
				if (wikiPage != null) {
					Intent intent = AndroidUtils.shareText(wikiPage.getPageTitle(), wikiPage.getPageUrl(), this);
					shareActionProvider.setShareIntent(intent);
				}
				break;
			case R.id.refresh:
				wikiViewer.onRefresh();
				break;
			case R.id.menu_settings:
				startActivity(new Intent(this, PreferencesActivity.class));
				break;
			case R.id.exit:
				finish();
				break;
		}
		{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.MainActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.MainActivity.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
	}


	@Override
	public void onFinish(List<SearchResult> results) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.MainActivity.onFinish(java.util.ArrayList)",this,results);try{currentSuggestions = results;
		setCursorAdapter(currentSuggestions);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.MainActivity.onFinish(java.util.ArrayList)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.MainActivity.onFinish(java.util.ArrayList)",this,throwable);throw throwable;}
	}

	private void setCursorAdapter(List<SearchResult> currentSuggestions) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.MainActivity.setCursorAdapter(java.util.ArrayList)",this,currentSuggestions);try{searchView.setSuggestionsAdapter(
				SearchResultsAdapter.getCursorAdapter(this, currentSuggestions)
		);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.MainActivity.setCursorAdapter(java.util.ArrayList)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.MainActivity.setCursorAdapter(java.util.ArrayList)",this,throwable);throw throwable;}
	}
}