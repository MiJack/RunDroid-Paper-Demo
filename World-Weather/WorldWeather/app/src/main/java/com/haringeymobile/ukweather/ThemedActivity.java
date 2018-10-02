package com.haringeymobile.ukweather;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.haringeymobile.ukweather.settings.SettingsActivity;
import com.haringeymobile.ukweather.utils.MiscMethods;

import static com.haringeymobile.ukweather.settings.SettingsActivity.LANGUAGE_DEFAULT;

/**
 * A base activity for all other app's activities, that sets the app theme upon creation.
 */
public abstract class ThemedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.ThemedActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{setAppTheme();
        super.onCreate(savedInstanceState);
        setTitle(getActivityLabelResourceId());com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.ThemedActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.ThemedActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    protected int getActivityLabelResourceId() {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.ThemedActivity.getActivityLabelResourceId()",this);try{int labelRes;
        try {
            labelRes = getPackageManager().getActivityInfo(getComponentName(), 0).labelRes;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException();
        }
        {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.ThemedActivity.getActivityLabelResourceId()",this);return labelRes;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.ThemedActivity.getActivityLabelResourceId()",this,throwable);throw throwable;}
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.ThemedActivity.onPostCreate(@Nullable Bundle)",this,savedInstanceState);try{super.onPostCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String appLanguage = preferences.getString(SettingsActivity.
                PREF_APP_LANGUAGE, LANGUAGE_DEFAULT);
        if (!appLanguage.equals(LANGUAGE_DEFAULT)) {
            MiscMethods.updateLocale(appLanguage, getResources());
            resetActionBarTitle();
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.ThemedActivity.onPostCreate(@Nullable Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.ThemedActivity.onPostCreate(@Nullable Bundle)",this,throwable);throw throwable;}
    }

    protected void resetActionBarTitle() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.ThemedActivity.resetActionBarTitle()",this);try{ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            int labelRes = getActivityLabelResourceId();
            if (labelRes > 0) {
                actionBar.setTitle(labelRes);
            }
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.ThemedActivity.resetActionBarTitle()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.ThemedActivity.resetActionBarTitle()",this,throwable);throw throwable;}
    }

    /**
     * Sets the user preferred (or default, if no preference was set) app theme.
     */
    private void setAppTheme() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.ThemedActivity.setAppTheme()",this);try{Resources res = getResources();
        String appThemeValue = PreferenceManager.getDefaultSharedPreferences(this).getString(
                SettingsActivity.PREF_APP_THEME,
                res.getString(R.string.pref_app_theme_default));

        int themeResourceId = getThemeResourceId(appThemeValue);
        setTheme(themeResourceId);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.ThemedActivity.setAppTheme()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.ThemedActivity.setAppTheme()",this,throwable);throw throwable;}
    }

    /**
     * Obtains the resource id for the app theme.
     *
     * @param appThemeValue one of the values defined in {@link R.array#pref_theme_values}
     * @return app theme resource id
     */
    private int getThemeResourceId(String appThemeValue) {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.ThemedActivity.getThemeResourceId(java.lang.String)",this,appThemeValue);try{int theme;
        switch (appThemeValue) {
            case "0":
                theme = R.style.AppThemePink;
                break;
            case "1":
                theme = R.style.AppThemeRed;
                break;
            case "2":
                theme = R.style.AppThemePurple;
                break;
            case "3":
                theme = R.style.AppThemeDeepPurple;
                break;
            case "4":
                theme = R.style.AppThemeIndigo;
                break;
            case "5":
                theme = R.style.AppThemeBlue;
                break;
            case "6":
                theme = R.style.AppThemeCyan;
                break;
            case "7":
                theme = R.style.AppThemeTeal;
                break;
            case "8":
                theme = R.style.AppThemeGreen;
                break;
            case "9":
                theme = R.style.AppThemeBrown;
                break;
            case "10":
                theme = R.style.AppThemeBlueGray;
                break;
            default:
                throw new IllegalArgumentException("Unsupported value: " + appThemeValue + ". " +
                        "(The " + "value must be defined in xml: res->values" +
                        "->strings_for_settings->pref_theme_values");
        }
        {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.ThemedActivity.getThemeResourceId(java.lang.String)",this);return theme;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.ThemedActivity.getThemeResourceId(java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    public void onBackPressed() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.ThemedActivity.onBackPressed()",this);try{super.onBackPressed();
        playAnimation();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.ThemedActivity.onBackPressed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.ThemedActivity.onBackPressed()",this,throwable);throw throwable;}
    }

    /**
     * Plays the activity transition animation.
     */
    @SuppressLint("PrivateResource")
    protected void playAnimation() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.ThemedActivity.playAnimation()",this);try{overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.ThemedActivity.playAnimation()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.ThemedActivity.playAnimation()",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.ThemedActivity.onOptionsItemSelected(android.view.MenuItem)",this,item);try{switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                playAnimation();
                {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.ThemedActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.ThemedActivity.onOptionsItemSelected(android.view.MenuItem)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.ThemedActivity.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

}