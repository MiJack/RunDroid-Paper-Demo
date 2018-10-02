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

import java.io.IOException;
import java.security.GeneralSecurityException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.u17od.upm.crypto.InvalidPasswordException;
import com.u17od.upm.database.PasswordDatabase;
import com.u17od.upm.database.ProblemReadingDatabaseFile;

public class CreateNewDatabase extends Activity implements OnClickListener {

    private static final int GENERIC_ERROR_DIALOG = 1;

    public static final int MIN_PASSWORD_LENGTH = 6;

    private EditText password1;
    private EditText password2;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.CreateNewDatabase.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setContentView(R.layout.new_master_password_dialog);
        
        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);
        Button createDatabaseButton = (Button) findViewById(R.id.create_database_button);
        createDatabaseButton.setOnClickListener(this);

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.u17od.upm.CreateNewDatabase$1.onClick(android.view.View)",this,v);try{finish();com.mijack.Xlog.logMethodExit("void com.u17od.upm.CreateNewDatabase$1.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.CreateNewDatabase$1.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.u17od.upm.CreateNewDatabase.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.CreateNewDatabase.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private UPMApplication getUPMApplication() {
        com.mijack.Xlog.logMethodEnter("com.u17od.upm.UPMApplication com.u17od.upm.CreateNewDatabase.getUPMApplication()",this);try{com.mijack.Xlog.logMethodExit("com.u17od.upm.UPMApplication com.u17od.upm.CreateNewDatabase.getUPMApplication()",this);return (UPMApplication) getApplication();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.u17od.upm.UPMApplication com.u17od.upm.CreateNewDatabase.getUPMApplication()",this,throwable);throw throwable;}
    }

    @Override
    public void onClick(View v) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.CreateNewDatabase.onClick(android.view.View)",this,v);try{if (!password1.getText().toString().equals(password2.getText().toString())) {
            Toast toast = Toast.makeText(CreateNewDatabase.this, R.string.passwords_dont_match, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        } else if (password1.getText().length() < MIN_PASSWORD_LENGTH) {
            String passwordTooShortResStr = getString(R.string.password_too_short);
            String resultsText = String.format(passwordTooShortResStr, MIN_PASSWORD_LENGTH);
            Toast.makeText(this, resultsText, Toast.LENGTH_SHORT).show();
        } else {
            try {
                /*// Create a new database and then launch the AccountsList activity*/
                String password = password1.getText().toString();
                final PasswordDatabase passwordDatabase = new PasswordDatabase(Utilities.getDatabaseFile(this), password.toCharArray());

                new SaveDatabaseAsyncTask(this, new Callback() {
                    @Override
                    public void execute() {
                        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.CreateNewDatabase$2.execute()",this);try{/*// Make the database available to the rest of the application by */
                        /*// putting a reference to it on the application*/
                        getUPMApplication().setPasswordDatabase(passwordDatabase);

                        /*// Ask the BackupManager to backup the database using*/
                        /*// Google's cloud backup service.*/
                        Log.i("CreateNewDatabase", "Calling BackupManager().dataChanged()");
                        getUPMApplication().getBackupManager().dataChanged();

                        setResult(RESULT_OK);
                        finish();com.mijack.Xlog.logMethodExit("void com.u17od.upm.CreateNewDatabase$2.execute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.CreateNewDatabase$2.execute()",this,throwable);throw throwable;}
                    }
                }).execute(passwordDatabase);

            } catch (IOException e) {
                Log.e("CreateNewDatabase", "Error encountered while creating a new database", e);
                showDialog(GENERIC_ERROR_DIALOG);
            } catch (GeneralSecurityException e) {
                Log.e("CreateNewDatabase", "Error encountered while creating a new database", e);
                showDialog(GENERIC_ERROR_DIALOG);
            } catch (ProblemReadingDatabaseFile e) {
                Log.e("CreateNewDatabase", "Error encountered while creating a new database", e);
                showDialog(GENERIC_ERROR_DIALOG);
            } catch (InvalidPasswordException e) {
                Log.e("CreateNewDatabase", "Error encountered while creating a new database", e);
                showDialog(GENERIC_ERROR_DIALOG);
            }
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.CreateNewDatabase.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.CreateNewDatabase.onClick(android.view.View)",this,throwable);throw throwable;}
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.u17od.upm.CreateNewDatabase.onCreateDialog(int)",this,id);try{Dialog dialog = null;

        switch(id) {
            case GENERIC_ERROR_DIALOG:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.generic_error)
                    .setNeutralButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            com.mijack.Xlog.logMethodEnter("void com.u17od.upm.CreateNewDatabase$3.onClick(android.content.DialogInterface,int)",this,dialog,which);try{finish();com.mijack.Xlog.logMethodExit("void com.u17od.upm.CreateNewDatabase$3.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.CreateNewDatabase$3.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                        }
                });
                dialog = builder.create();
                break;
        }
        
        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.u17od.upm.CreateNewDatabase.onCreateDialog(int)",this);return dialog;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.u17od.upm.CreateNewDatabase.onCreateDialog(int)",this,throwable);throw throwable;}
    }

}
