package com.haringeymobile.ukweather.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.haringeymobile.ukweather.R;

/**
 * A fragment to provide settings for the app.
 */
@SuppressLint("NewApi")
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.settings.SettingsFragment.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.settings.SettingsFragment.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.settings.SettingsFragment.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

}
