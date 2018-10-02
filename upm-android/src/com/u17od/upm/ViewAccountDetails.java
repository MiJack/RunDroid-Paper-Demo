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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.u17od.upm.database.AccountInformation;
import com.u17od.upm.database.PasswordDatabase;

public class ViewAccountDetails extends Activity {

    public static AccountInformation account;

    private static final int CONFIRM_DELETE_DIALOG = 0;
    public static final int VIEW_ACCOUNT_REQUEST_CODE = 224;

    private int editAccountResultCode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.ViewAccountDetails.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        if (Utilities.VERSION.SDK_INT >= Utilities.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.view_account_details);com.mijack.Xlog.logMethodExit("void com.u17od.upm.ViewAccountDetails.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.ViewAccountDetails.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    /**
     * This method is called when returning from the edit activity. Since the
     * account details may have been changed we should repopulate the view 
     */
    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.ViewAccountDetails.onResume()",this);try{super.onResume();
        /*// If the account is null then finish (may be null because activity was*/
        /*// recreated since it was last visible*/
        if (account == null) {
            finish();
        } else {
            populateView();
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.ViewAccountDetails.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.ViewAccountDetails.onResume()",this,throwable);throw throwable;}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean com.u17od.upm.ViewAccountDetails.onCreateOptionsMenu(android.view.Menu)",this,menu);try{super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account, menu);
        {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.ViewAccountDetails.onCreateOptionsMenu(android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.u17od.upm.ViewAccountDetails.onCreateOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.u17od.upm.ViewAccountDetails.onOptionsItemSelected(android.view.MenuItem)",this,item);try{boolean optionConsumed = false;

        switch (item.getItemId()) {
        case R.id.edit:
            if (Utilities.isSyncRequired(this)) {
                UIUtilities.showToast(this, R.string.sync_required);
            } else {
                Intent i = new Intent(ViewAccountDetails.this, AddEditAccount.class);
                i.putExtra(AddEditAccount.MODE, AddEditAccount.EDIT_MODE);
                i.putExtra(AddEditAccount.ACCOUNT_TO_EDIT, account.getAccountName());
                startActivityForResult(i, AddEditAccount.EDIT_ACCOUNT_REQUEST_CODE);
            }
            break;
        case R.id.delete:
            if (Utilities.isSyncRequired(this)) {
                UIUtilities.showToast(this, R.string.sync_required);
            } else {
                showDialog(CONFIRM_DELETE_DIALOG);
            }
            break;
        }

        {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.ViewAccountDetails.onOptionsItemSelected(android.view.MenuItem)",this);return optionConsumed;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.u17od.upm.ViewAccountDetails.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.u17od.upm.ViewAccountDetails.onCreateDialog(int)",this,id);try{Dialog dialog = null;

        switch(id) {
        case CONFIRM_DELETE_DIALOG:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?")
                .setTitle("Confirm Delete")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.ViewAccountDetails$1.onClick(android.content.DialogInterface,int)",this,dialog,id);try{getPasswordDatabase().deleteAccount(account.getAccountName());
                        final String accountName = account.getAccountName();

                        new SaveDatabaseAsyncTask(ViewAccountDetails.this, new Callback() {
                            @Override
                            public void execute() {
                                com.mijack.Xlog.logMethodEnter("void com.u17od.upm.ViewAccountDetails$1$1.execute()",this);try{String message = String.format(getString(R.string.account_deleted), accountName);
                                Toast.makeText(ViewAccountDetails.this, message, Toast.LENGTH_SHORT).show();
                                /*// Set this flag so that when we're returned to the FullAccountList*/
                                /*// activity the list is refreshed*/
                                ViewAccountDetails.this.setResult(AddEditAccount.EDIT_ACCOUNT_RESULT_CODE_TRUE);
                                finish();com.mijack.Xlog.logMethodExit("void com.u17od.upm.ViewAccountDetails$1$1.execute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.ViewAccountDetails$1$1.execute()",this,throwable);throw throwable;}
                            }
                        }).execute(getPasswordDatabase());com.mijack.Xlog.logMethodExit("void com.u17od.upm.ViewAccountDetails$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.ViewAccountDetails$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.ViewAccountDetails$2.onClick(android.content.DialogInterface,int)",this,dialog,id);try{dialog.cancel();com.mijack.Xlog.logMethodExit("void com.u17od.upm.ViewAccountDetails$2.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.ViewAccountDetails$2.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                });
            dialog = builder.create();
        }

        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.u17od.upm.ViewAccountDetails.onCreateDialog(int)",this);return dialog;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.u17od.upm.ViewAccountDetails.onCreateDialog(int)",this,throwable);throw throwable;}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.ViewAccountDetails.onActivityResult(int,int,android.content.Intent)",this,requestCode,resultCode,intent);try{switch(requestCode) {
            case AddEditAccount.EDIT_ACCOUNT_REQUEST_CODE:
                editAccountResultCode = resultCode;
                break;
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.ViewAccountDetails.onActivityResult(int,int,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.ViewAccountDetails.onActivityResult(int,int,android.content.Intent)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.u17od.upm.ViewAccountDetails.onKeyDown(int,android.view.KeyEvent)",this,keyCode,event);try{/*// If the back button is pressed pass back the edit account flag*/
        /*// This is used to indicate if the list of account names on */
        /*// FullAccountList needs to be refreshed*/
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(editAccountResultCode);
        }
        {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.ViewAccountDetails.onKeyDown(int,android.view.KeyEvent)",this);return super.onKeyDown(keyCode, event);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.u17od.upm.ViewAccountDetails.onKeyDown(int,android.view.KeyEvent)",this,throwable);throw throwable;}
    } 

    private void populateView() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.ViewAccountDetails.populateView()",this);try{TextView accountNameTextView = (TextView) findViewById(R.id.account_name);
        accountNameTextView.setText(account.getAccountName());

        TextView accountUseridTextView = (TextView) findViewById(R.id.account_userid);
        accountUseridTextView.setText(new String(account.getUserId()));

        TextView accountPasswordTextView = (TextView) findViewById(R.id.account_password);
        accountPasswordTextView.setText(new String(account.getPassword()));

        TextView accountURLTextView = (TextView) findViewById(R.id.account_url);
        accountURLTextView.setText(new String(account.getUrl()));

        TextView accountNotesTextView = (TextView) findViewById(R.id.account_notes);
        accountNotesTextView.setText(new String(account.getNotes()));com.mijack.Xlog.logMethodExit("void com.u17od.upm.ViewAccountDetails.populateView()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.ViewAccountDetails.populateView()",this,throwable);throw throwable;}
    }

    private PasswordDatabase getPasswordDatabase() {
        com.mijack.Xlog.logMethodEnter("com.u17od.upm.database.PasswordDatabase com.u17od.upm.ViewAccountDetails.getPasswordDatabase()",this);try{com.mijack.Xlog.logMethodExit("com.u17od.upm.database.PasswordDatabase com.u17od.upm.ViewAccountDetails.getPasswordDatabase()",this);return ((UPMApplication) getApplication()).getPasswordDatabase();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.u17od.upm.database.PasswordDatabase com.u17od.upm.ViewAccountDetails.getPasswordDatabase()",this,throwable);throw throwable;}
    }

}
