/*
 * Copyright (c) 2016-2018. Vijai Chandra Prasad R.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses
 */

package com.orpheusdroid.screenrecorder.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.webkit.WebView;

import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.PrivacyPolicy.onCreate(android.os.Bundle)",this,savedInstanceState);try{String theme = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.preference_theme_key), Const.PREFS_LIGHT_THEME);
        switch (theme){
            case Const.PREFS_WHITE_THEME:
                setTheme(R.style.AppTheme_White);
                break;
            case Const.PREFS_DARK_THEME:
                setTheme(R.style.AppTheme_Dark);
                break;
            case Const.PREFS_BLACK_THEME:
                setTheme(R.style.AppTheme_Black);
                break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ((WebView) findViewById(R.id.wv_privacy_policy)).loadUrl("file:///android_asset/privacy_policy.html");com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.PrivacyPolicy.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.PrivacyPolicy.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.ui.PrivacyPolicy.onOptionsItemSelected(android.view.MenuItem)",this,item);try{switch (item.getItemId()) {
            /*// Respond to the action bar's Up/Home button*/
            case android.R.id.home:
                /*//finish this activity and return to parent activity*/
                this.finish();
                {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.ui.PrivacyPolicy.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.ui.PrivacyPolicy.onOptionsItemSelected(android.view.MenuItem)",this);return super.onOptionsItemSelected(item);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.ui.PrivacyPolicy.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }
}
