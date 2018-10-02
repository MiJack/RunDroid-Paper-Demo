package com.phikal.regex.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.phikal.regex.R;
import com.phikal.regex.games.Game;
import com.phikal.regex.models.Progress;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import static com.phikal.regex.Util.CHAR_BAR_ON;
import static com.phikal.regex.Util.FEEDBACK_ON;
import static com.phikal.regex.Util.MODE;
import static com.phikal.regex.Util.notif;

public class SettingsActivity extends Activity {

    SharedPreferences prefs;
    Game g;
    Progress p;

    TextView roundsText, difficultyText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.SettingsActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /*// find views*/
        roundsText = (TextView) findViewById(R.id.round);
        difficultyText = (TextView) findViewById(R.id.diff);
        final Button resetButton = (Button) findViewById(R.id.reset);
        final Button charmButton = (Button) findViewById(R.id.charm);
        final Button notifButton = (Button) findViewById(R.id.notif);
        Spinner gameSpinner = (Spinner) findViewById(R.id.mode_selector);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        stats();

        /*// reset when pressed twice*/
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.SettingsActivity$1.onClick(android.view.View)",this,v);try{if (v.getTag() != null && (Boolean) v.getTag()) {
                    resetButton.setText(R.string.clear);
                    p.clear();
                    v.setTag(false);
                    GameActivity.reload = true;
                    stats();
                } else {
                    resetButton.setText(R.string.confirm);
                    v.setTag(true);
                }com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.SettingsActivity$1.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.SettingsActivity$1.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });

        /*// turn character bar on or off*/
        charmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.SettingsActivity$2.onClick(android.view.View)",this,v);try{prefs.edit().putBoolean(CHAR_BAR_ON,
                    !prefs.getBoolean(CHAR_BAR_ON, true))
                    .apply();
            charmButton.setText(prefs.getBoolean(CHAR_BAR_ON, true) ?
                    R.string.char_off :
                    R.string.char_on);
            notif(SettingsActivity.this);com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.SettingsActivity$2.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.SettingsActivity$2.onClick(android.view.View)",this,throwable);throw throwable;}
        }});
        charmButton.setText(prefs.getBoolean(CHAR_BAR_ON, true) ?
                R.string.char_off :
                R.string.char_on);

        /*// turn notifications on or off*/
        notifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.SettingsActivity$3.onClick(android.view.View)",this,v);try{prefs.edit().putBoolean(FEEDBACK_ON,
                    !prefs.getBoolean(FEEDBACK_ON, true))
                    .apply();
            notifButton.setText(prefs.getBoolean(FEEDBACK_ON, false) ?
                    R.string.notif_off :
                    R.string.notif_on);
            notif(SettingsActivity.this);com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.SettingsActivity$3.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.SettingsActivity$3.onClick(android.view.View)",this,throwable);throw throwable;}
        }});
        notifButton.setText(prefs.getBoolean(FEEDBACK_ON, false) ?
                R.string.notif_off :
                R.string.notif_on);

        /*// game mode selector*/
        gameSpinner.setAdapter(new ArrayAdapter<Game>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList(Game.values())) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                com.mijack.Xlog.logMethodEnter("android.view.View com.phikal.regex.activities.SettingsActivity$4.getView(int,@Nullable View,@NonNull ViewGroup)",this,position,convertView,parent);try{if (convertView != null) {{com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.SettingsActivity.onCreate(android.os.Bundle)",this);{com.mijack.Xlog.logMethodExit("android.view.View com.phikal.regex.activities.SettingsActivity$4.getView(int,@Nullable View,@NonNull ViewGroup)",this);return convertView;}}}
                TextView v = new TextView(getContext());
                v.setText(getString(Game.values()[position].name));
                {com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.SettingsActivity.onCreate(android.os.Bundle)",this);{com.mijack.Xlog.logMethodExit("android.view.View com.phikal.regex.activities.SettingsActivity$4.getView(int,@Nullable View,@NonNull ViewGroup)",this);return v;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.phikal.regex.activities.SettingsActivity$4.getView(int,@Nullable View,@NonNull ViewGroup)",this,throwable);throw throwable;}
            }
        });
        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.SettingsActivity$5.onItemSelected(android.widget.AdapterView,android.view.View,int,long)",this,parent,view,i,l);try{Game game = (Game) parent.getItemAtPosition(i);
                prefs.edit().putString(MODE, game.name()).apply();
                GameActivity.reload = true;
                stats();com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.SettingsActivity$5.onItemSelected(android.widget.AdapterView,android.view.View,int,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.SettingsActivity$5.onItemSelected(android.widget.AdapterView,android.view.View,int,long)",this,throwable);throw throwable;}
            }

            {com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.SettingsActivity$5.onNothingSelected(android.widget.AdapterView)",this,adapterView);com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.SettingsActivity$5.onNothingSelected(android.widget.AdapterView)",this);}
        });
        gameSpinner.setSelection(g.ordinal());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.SettingsActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void stats() {
        com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.SettingsActivity.stats()",this);try{Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {locale = getResources().getConfiguration().getLocales().get(0);}
        else
            /*//noinspection deprecation*/
            {locale = getResources().getConfiguration().locale;}

        String modeName = prefs.getString(MODE, Game.DEFAULT_GAME.name());
        g = Game.valueOf(modeName);
        p = g.getProgress(getApplicationContext());

        /*// display progress*/
        roundsText.setText(String.valueOf(p.getRound()));
        difficultyText.setText(String.format(locale, "%.2f%%", p.getDifficutly() * 100));com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.SettingsActivity.stats()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.SettingsActivity.stats()",this,throwable);throw throwable;}
    }
}
