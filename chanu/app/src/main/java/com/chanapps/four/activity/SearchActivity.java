package com.chanapps.four.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import com.android.gallery3d.ui.Log;
import com.chanapps.four.service.NetworkProfileManager;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 5/1/13
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchActivity extends Activity {

    protected static final String TAG = SearchActivity.class.getSimpleName();
    protected static final boolean DEBUG = false;

    protected String query;

    public static void createSearchView(final Activity activity, MenuItem searchMenuItem) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.activity.SearchActivity.createSearchView(android.app.Activity,android.view.MenuItem)",activity,searchMenuItem);try{try {
            SearchManager searchManager = (SearchManager)activity.getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView)searchMenuItem.getActionView();
            if (searchView == null)
                {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.SearchActivity.createSearchView(android.app.Activity,android.view.MenuItem)");return;}}
            searchView.setSubmitButtonEnabled(true);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SearchActivity$1.onFocusChange(android.view.View,boolean)",this,v,hasFocus);try{/*//if (activity != null)*/
                    /*//    activity.disableAutoRefresh();*/
                    if (DEBUG) {Log.i(TAG, "onFocusChange()");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SearchActivity$1.onFocusChange(android.view.View,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SearchActivity$1.onFocusChange(android.view.View,boolean)",this,throwable);throw throwable;}
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.SearchActivity$2.onQueryTextSubmit(java.lang.String)",this,query);try{if (DEBUG) {android.util.Log.i(TAG, "SearchView.onQueryTextSubmit");}
                    Intent intent = new Intent(activity, SearchActivity.class);
                    intent.putExtra(SearchManager.QUERY, query);
                    intent.setAction(Intent.ACTION_SEARCH);
                    activity.startActivity(intent);
                    {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.SearchActivity.createSearchView(android.app.Activity,android.view.MenuItem)");{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.SearchActivity$2.onQueryTextSubmit(java.lang.String)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.SearchActivity$2.onQueryTextSubmit(java.lang.String)",this,throwable);throw throwable;}
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.SearchActivity$2.onQueryTextChange(java.lang.String)",this,newText);try{if (DEBUG) {android.util.Log.i(TAG, "SearchView.onQueryTextChange");}
                    {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.activity.SearchActivity.createSearchView(android.app.Activity,android.view.MenuItem)");{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.SearchActivity$2.onQueryTextChange(java.lang.String)",this);return false;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.SearchActivity$2.onQueryTextChange(java.lang.String)",this,throwable);throw throwable;}
                }
            });
        }
        catch (Exception e) {
            Log.e(TAG, "Exception creating search view", e);
        }
        ActionBar actionBar = activity.getActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.app_icon_actionbar);
        actionBar.setLogo(R.drawable.app_icon_actionbar);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.activity.SearchActivity.createSearchView(android.app.Activity,android.view.MenuItem)",throwable);throw throwable;}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SearchActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        if (DEBUG) {Log.i(TAG, "onCreate query=" + getIntent().getStringExtra(SearchManager.QUERY));}
        handleIntent(getIntent());
        getActionBar().setDisplayUseLogoEnabled(true);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SearchActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SearchActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void onNewIntent(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SearchActivity.onNewIntent(android.content.Intent)",this,intent);try{if (DEBUG) {Log.i(TAG, "onNewIntent");}
        handleIntent(intent);
        getActionBar().setDisplayUseLogoEnabled(true);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SearchActivity.onNewIntent(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SearchActivity.onNewIntent(android.content.Intent)",this,throwable);throw throwable;}
    }

    protected void handleIntent(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SearchActivity.handleIntent(android.content.Intent)",this,intent);try{query = intent.getStringExtra(SearchManager.QUERY);
        finish();
        if (DEBUG) {Log.i(TAG, "handleIntent action=" + intent.getAction() + " q=" + query);}
        if (!Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.e(TAG, "handleIntent invalid action");
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SearchActivity.handleIntent(android.content.Intent)",this);return;}
        }
        if (DEBUG) {Log.i(TAG, "handleIntent q=" + query);}
        final ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
        if (activity == null || activity.getChanActivityId() == null) {
            Log.e(TAG, "Null chan activity or activity id, exiting search");
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SearchActivity.handleIntent(android.content.Intent)",this);return;}
        }
        if (DEBUG) {Log.i(TAG, "handleIntent closing search");}
        activity.closeSearch();
        doSearch();
        {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SearchActivity.handleIntent(android.content.Intent)",this);return;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SearchActivity.handleIntent(android.content.Intent)",this,throwable);throw throwable;}
    }

    protected void doSearch() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SearchActivity.doSearch()",this);try{final ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
        String boardCode = activity.getChanActivityId().boardCode;
        if (boardCode == null || boardCode.isEmpty()) {
            Log.e(TAG, "No boardCode supplied with activity id, exiting search");
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SearchActivity.doSearch()",this);return;}
        }

        long threadNo = activity.getChanActivityId().threadNo;
        if (threadNo <= 0) {
            if (DEBUG) {Log.i(TAG, "handleIntent start search /" + boardCode + "/" + " q=" + query);}
            BoardActivity.startActivity(this, boardCode, query);
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SearchActivity.doSearch()",this);return;}
        }

        if (DEBUG) {Log.i(TAG, "handleIntent start search /" + boardCode + "/" + threadNo + " q=" + query);}
        ThreadActivity.startActivity(this, boardCode, threadNo, query);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SearchActivity.doSearch()",this,throwable);throw throwable;}
    }

    @Override
    public void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SearchActivity.onStart()",this);try{super.onStart();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SearchActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SearchActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    public void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SearchActivity.onStop()",this);try{super.onStop();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SearchActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SearchActivity.onStop()",this,throwable);throw throwable;}
    }

}

