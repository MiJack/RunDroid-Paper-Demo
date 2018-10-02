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

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.u17od.upm.database.AccountInformation;
import com.u17od.upm.database.PasswordDatabase;

public class AccountsList extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.AccountsList.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);com.mijack.Xlog.logMethodExit("void com.u17od.upm.AccountsList.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.AccountsList.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.AccountsList.onCreateContextMenu(android.view.ContextMenu,android.view.View,android.view.ContextMenu.ContextMenuInfo)",this,menu,v,menuInfo);try{super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_context_menu, menu);com.mijack.Xlog.logMethodExit("void com.u17od.upm.AccountsList.onCreateContextMenu(android.view.ContextMenu,android.view.View,android.view.ContextMenu.ContextMenuInfo)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.AccountsList.onCreateContextMenu(android.view.ContextMenu,android.view.View,android.view.ContextMenu.ContextMenuInfo)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.u17od.upm.AccountsList.onContextItemSelected(android.view.MenuItem)",this,item);try{AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.edit_account:
            editAccount(getAccount(info.targetView));
            {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.AccountsList.onContextItemSelected(android.view.MenuItem)",this);return true;}
        case R.id.copy_username:
            setClipboardText(getUsername(getAccount(info.targetView)));
            {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.AccountsList.onContextItemSelected(android.view.MenuItem)",this);return true;}
        case R.id.copy_password:
            setClipboardText(getPassword(getAccount(info.targetView)));
            {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.AccountsList.onContextItemSelected(android.view.MenuItem)",this);return true;}
        case R.id.launch_url:
            launchURL(getURL(getAccount(info.targetView)));
            {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.AccountsList.onContextItemSelected(android.view.MenuItem)",this);return true;}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.AccountsList.onContextItemSelected(android.view.MenuItem)",this);return super.onContextItemSelected(item);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.u17od.upm.AccountsList.onContextItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    private void setClipboardText(String text) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.AccountsList.setClipboardText(java.lang.String)",this,text);try{ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setText(text);com.mijack.Xlog.logMethodExit("void com.u17od.upm.AccountsList.setClipboardText(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.AccountsList.setClipboardText(java.lang.String)",this,throwable);throw throwable;}
    }

    private AccountInformation getAccount(View listviewItem) {
        com.mijack.Xlog.logMethodEnter("com.u17od.upm.database.AccountInformation com.u17od.upm.AccountsList.getAccount(android.view.View)",this,listviewItem);try{com.mijack.Xlog.logMethodExit("com.u17od.upm.database.AccountInformation com.u17od.upm.AccountsList.getAccount(android.view.View)",this);return getPasswordDatabase().getAccount(((TextView) listviewItem).getText().toString());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.u17od.upm.database.AccountInformation com.u17od.upm.AccountsList.getAccount(android.view.View)",this,throwable);throw throwable;}
    }

    private String getUsername(AccountInformation account) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.AccountsList.getUsername(com.u17od.upm.database.AccountInformation)",this,account);try{com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.AccountsList.getUsername(com.u17od.upm.database.AccountInformation)",this);return new String(account.getUserId());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.AccountsList.getUsername(com.u17od.upm.database.AccountInformation)",this,throwable);throw throwable;}
    }

    private String getURL(AccountInformation account) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.AccountsList.getURL(com.u17od.upm.database.AccountInformation)",this,account);try{com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.AccountsList.getURL(com.u17od.upm.database.AccountInformation)",this);return new String(account.getUrl());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.AccountsList.getURL(com.u17od.upm.database.AccountInformation)",this,throwable);throw throwable;}
    }

    private String getPassword(AccountInformation account) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.AccountsList.getPassword(com.u17od.upm.database.AccountInformation)",this,account);try{com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.AccountsList.getPassword(com.u17od.upm.database.AccountInformation)",this);return new String(account.getPassword());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.AccountsList.getPassword(com.u17od.upm.database.AccountInformation)",this,throwable);throw throwable;}
    }

    private void launchURL(String uriString) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.AccountsList.launchURL(java.lang.String)",this,uriString);try{if (uriString == null || uriString.equals("")) {
            UIUtilities.showToast(this, R.string.no_uri, true);
        } else {
            Uri uri = Uri.parse(uriString);
            if (uri.getScheme() == null) {
                uri = Uri.parse("http://" + uriString);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
            startActivity(intent); 
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.AccountsList.launchURL(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.AccountsList.launchURL(java.lang.String)",this,throwable);throw throwable;}
    }

    private void viewAccount(AccountInformation ai) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.AccountsList.viewAccount(com.u17od.upm.database.AccountInformation)",this,ai);try{/*// Pass the AccountInformation object o the AccountDetails Activity by*/
        /*// way of a static variable on that class. I really don't like this but*/
        /*// it seems like the best way of doing it*/
        /*// @see http://developer.android.com/guide/appendix/faq/framework.html#3*/
        ViewAccountDetails.account = ai;

        Intent i = new Intent(AccountsList.this, ViewAccountDetails.class);
        startActivityForResult(i, ViewAccountDetails.VIEW_ACCOUNT_REQUEST_CODE);com.mijack.Xlog.logMethodExit("void com.u17od.upm.AccountsList.viewAccount(com.u17od.upm.database.AccountInformation)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.AccountsList.viewAccount(com.u17od.upm.database.AccountInformation)",this,throwable);throw throwable;}
    }

    private void editAccount(AccountInformation ai) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.AccountsList.editAccount(com.u17od.upm.database.AccountInformation)",this,ai);try{if (Utilities.isSyncRequired(this)) {
            UIUtilities.showToast(this, R.string.sync_required);
        } else {
            if (ai != null) {
                Intent i = new Intent(AccountsList.this, AddEditAccount.class);
                i.putExtra(AddEditAccount.MODE, AddEditAccount.EDIT_MODE);
                i.putExtra(AddEditAccount.ACCOUNT_TO_EDIT, ai.getAccountName());
                startActivityForResult(i, AddEditAccount.EDIT_ACCOUNT_REQUEST_CODE);
            }
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.AccountsList.editAccount(com.u17od.upm.database.AccountInformation)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.AccountsList.editAccount(com.u17od.upm.database.AccountInformation)",this,throwable);throw throwable;}
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.AccountsList.onListItemClick(android.widget.ListView,android.view.View,int,long)",this,l,v,position,id);try{/*// Get the name of the account the user selected*/
        TextView itemSelected = (TextView) v;
        viewAccount(getPasswordDatabase().getAccount(itemSelected.getText().toString()));com.mijack.Xlog.logMethodExit("void com.u17od.upm.AccountsList.onListItemClick(android.widget.ListView,android.view.View,int,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.AccountsList.onListItemClick(android.widget.ListView,android.view.View,int,long)",this,throwable);throw throwable;}
    }

    protected PasswordDatabase getPasswordDatabase() {
        com.mijack.Xlog.logMethodEnter("com.u17od.upm.database.PasswordDatabase com.u17od.upm.AccountsList.getPasswordDatabase()",this);try{com.mijack.Xlog.logMethodExit("com.u17od.upm.database.PasswordDatabase com.u17od.upm.AccountsList.getPasswordDatabase()",this);return ((UPMApplication) getApplication()).getPasswordDatabase();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.u17od.upm.database.PasswordDatabase com.u17od.upm.AccountsList.getPasswordDatabase()",this,throwable);throw throwable;}
    }

}
