/*
 * Universal Password Manager
 * Copyright (c) 2010-2011 Adrian Smith
 *
 * This file is part of Universal Password Manager.
 *   
 * Universal Password Manager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Universal Password Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Universal Password Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.u17od.upm;

import java.util.ArrayList;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class SearchResults extends AccountsList {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SearchResults.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);
        registerForContextMenu(getListView());com.mijack.Xlog.logMethodExit("void com.u17od.upm.SearchResults.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SearchResults.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onSearchRequested() {
        com.mijack.Xlog.logMethodEnter("boolean com.u17od.upm.SearchResults.onSearchRequested()",this);try{/*// Returning false here means that if the user can't initiate a search*/
        /*// while on the SearchResults page*/
        {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.SearchResults.onSearchRequested()",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.u17od.upm.SearchResults.onSearchRequested()",this,throwable);throw throwable;}
    }

    @Override
    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SearchResults.onResume()",this);try{super.onResume();
        /*// If the pw database is null then just close the activity.*/
        if (getPasswordDatabase() == null) {
            finish();
        } else {
            doSearch();
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SearchResults.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SearchResults.onResume()",this,throwable);throw throwable;}
    }

    private void doSearch() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SearchResults.doSearch()",this);try{final Intent queryIntent = getIntent();
        final String queryAction = queryIntent.getAction();
        if (Intent.ACTION_SEARCH.equals(queryAction)) {
            filterAccountsList(queryIntent.getStringExtra(SearchManager.QUERY));
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SearchResults.doSearch()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SearchResults.doSearch()",this,throwable);throw throwable;}
    }

    private void filterAccountsList(String textToFilterOn) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SearchResults.filterAccountsList(java.lang.String)",this,textToFilterOn);try{ArrayList<String> allAccountNames = getPasswordDatabase().getAccountNames(); 
        ArrayList<String> filteredAccountNames = new ArrayList<String>();
        String textToFilterOnLC = textToFilterOn.toLowerCase();
        
        /*// Loop through all the accounts and pick out those that match the search string*/
        for (String accountName : allAccountNames) {
            if (accountName.toLowerCase().indexOf(textToFilterOnLC) > -1) {
                filteredAccountNames.add(accountName);
            }
        }

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filteredAccountNames));com.mijack.Xlog.logMethodExit("void com.u17od.upm.SearchResults.filterAccountsList(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SearchResults.filterAccountsList(java.lang.String)",this,throwable);throw throwable;}
    }

}
