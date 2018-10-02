package com.chanapps.four.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.chanapps.four.component.*;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.data.LastActivity;
import com.chanapps.four.fragment.SettingsFragment;

/**
 * User: mpop
 * Date: 11/20/12
 * Time: 11:50 PM
 */
public class SettingsActivity extends Activity implements ChanIdentifiedActivity, ThemeSelector.ThemeActivity {

    public static final String TAG = SettingsActivity.class.getSimpleName();
    private static final boolean DEBUG = false;

    public static final String PREF_SHOW_NSFW_BOARDS = "pref_show_nsfw_boards";
    public static final String PREF_NOTIFICATIONS = "pref_notifications";
    /*//public static final String PREF_START_WITH_FAVORITES = "pref_start_with_favorites";*/
    /*//public static final String PREF_USE_FRIENDLY_IDS = "pref_use_friendly_ids";*/
    public static final String PREF_USE_HTTPS = "pref_use_https";
    public static final String PREF_BACKGROUND_DATA_ON_MOBILE = "pref_background_data_on_mobile";
    public static final String PREF_DOWNLOAD_NOMEDIA = "pref_download_nomedia";
    public static final String PREF_USE_CATALOG = "pref_use_catalog";
    public static final String PREF_USE_VOLUME_SCROLL = "pref_use_volume_scroll";
    public static final String PREF_USE_ABBREV_BOARDS = "pref_use_abbrev_boards";
    public static final String PREF_HIDE_LAST_REPLIES = "pref_hide_last_replies";
    public static final String PREF_BOARD_SORT_TYPE = "pref_board_sort_type";
    public static final String PREF_FONT_SIZE = "pref_font_size";
    public static final String PREF_AUTOUPDATE_THREADS = "pref_autoupdate_threads";
    public static final String PREF_THEME = "pref_theme";
    public static final String PREF_AUTOLOAD_IMAGES = "pref_autoload_images";
    public static final String PREF_DOWNLOAD_IMAGES = "pref_download_images";
    public static final String PREF_USER_NAME = "pref_user_name";
    public static final String PREF_USER_EMAIL = "pref_user_email";
    public static final String PREF_USER_PASSWORD = "pref_user_password";
    public static final String PREF_CACHE_SIZE = "pref_cache_size";
    public static final String PREF_CLEAR_CACHE = "pref_clear_cache";
    /*//public static final String PREF_CLEAR_WATCHLIST = "pref_clear_watchlist";*/
    /*//public static final String PREF_CLEAR_FAVORITES = "pref_clear_favorites";*/
    public static final String PREF_RESET_TO_DEFAULTS = "pref_reset_to_defaults";
    public static final String PREF_BLOCKLIST_BUTTON = "pref_blocklist_button";
    public static final String PREF_ABOUT = "pref_about";
    public static final String PREF_DOWNLOAD_LOCATION = "pref_download_location";
    public static final String PREF_PASS_TOKEN = "pref_pass_token";
    public static final String PREF_PASS_PIN = "pref_pass_pin";
    public static final String PREF_PASS_ENABLED = "pref_pass_enabled";
    public static final String PREF_WIDGET_BOARDS = "prefWidgetBoards";
    public static final String PREF_BLOCKLIST_TRIPCODE = "prefBlocklistTripcode";
    public static final String PREF_BLOCKLIST_NAME = "prefBlocklistName";
    public static final String PREF_BLOCKLIST_EMAIL = "prefBlocklistEmail";
    public static final String PREF_BLOCKLIST_ID = "prefBlocklistId";
    public static final String PREF_BLOCKLIST_THREAD = "prefBlocklistThread";
    public static final String PREF_BLOCKLIST_TEXT = "prefBlocklistText";
    public static final String PREF_AUTOMATICALLY_MANAGE_WATCHLIST = "pref_automatically_manage_watchlist";
    public static final String PREF_USE_FAST_SCROLL = "pref_use_fast_scroll";
    public static final String PREF_SHARE_IMAGE_URL = "pref_share_image_url";
    public static final String PREF_FORCE_ENGLISH = "pref_force_english";

    static public boolean shouldLoadThumbs(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.activity.SettingsActivity.shouldLoadThumbs(android.content.Context)",context);try{String autoloadType = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_AUTOLOAD_IMAGES,
                        context.getString(R.string.pref_autoload_images_default_value));
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.activity.SettingsActivity.shouldLoadThumbs(android.content.Context)");return !(context.getString(R.string.pref_autoload_images_nothumbs_value).equals(autoloadType));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.activity.SettingsActivity.shouldLoadThumbs(android.content.Context)",throwable);throw throwable;}
    }

    public static enum DownloadImages {STANDARD, ALL_IN_ONE, PER_BOARD, PER_THREAD};

    protected int themeId;
    protected ThemeSelector.ThemeReceiver broadcastThemeReceiver;

    public static boolean startActivity(final Activity from) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.activity.SettingsActivity.startActivity(android.app.Activity)",from);try{Intent intent = new Intent(from, SettingsActivity.class);
        from.startActivity(intent);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.activity.SettingsActivity.startActivity(android.app.Activity)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.activity.SettingsActivity.startActivity(android.app.Activity)",throwable);throw throwable;}
    }

    public static Intent createIntent(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("android.content.Intent com.chanapps.four.activity.SettingsActivity.createIntent(android.content.Context)",context);try{com.mijack.Xlog.logStaticMethodExit("android.content.Intent com.chanapps.four.activity.SettingsActivity.createIntent(android.content.Context)");return new Intent(context, SettingsActivity.class);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.content.Intent com.chanapps.four.activity.SettingsActivity.createIntent(android.content.Context)",throwable);throw throwable;}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        broadcastThemeReceiver = new ThemeSelector.ThemeReceiver(this);
        broadcastThemeReceiver.register();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SettingsActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public int getThemeId() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.activity.SettingsActivity.getThemeId()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.activity.SettingsActivity.getThemeId()",this);return themeId;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.activity.SettingsActivity.getThemeId()",this,throwable);throw throwable;}
    }

    @Override
    public void setThemeId(int themeId) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.setThemeId(int)",this,themeId);try{this.themeId = themeId;com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.setThemeId(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SettingsActivity.setThemeId(int)",this,throwable);throw throwable;}
    }

    @Override
    public void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.onStart()",this);try{super.onStart();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SettingsActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    public void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.onStop()",this);try{super.onStop();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SettingsActivity.onStop()",this,throwable);throw throwable;}
    }

    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.onResume()",this);try{super.onResume();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SettingsActivity.onResume()",this,throwable);throw throwable;}
    }

    @Override
    protected void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.onPause()",this);try{super.onPause();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SettingsActivity.onPause()",this,throwable);throw throwable;}
    }

    @Override
    protected void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.onDestroy()",this);try{super.onDestroy();
        broadcastThemeReceiver.unregister();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SettingsActivity.onDestroy()",this,throwable);throw throwable;}
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.onSaveInstanceState(android.os.Bundle)",this,bundle);try{ActivityDispatcher.store(this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SettingsActivity.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
	public ChanActivityId getChanActivityId() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.ChanActivityId com.chanapps.four.activity.SettingsActivity.getChanActivityId()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ChanActivityId com.chanapps.four.activity.SettingsActivity.getChanActivityId()",this);return new ChanActivityId(LastActivity.SETTINGS_ACTIVITY);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.ChanActivityId com.chanapps.four.activity.SettingsActivity.getChanActivityId()",this,throwable);throw throwable;}
	}

	@Override
	public Handler getChanHandler() {
		com.mijack.Xlog.logMethodEnter("android.os.Handler com.chanapps.four.activity.SettingsActivity.getChanHandler()",this);try{com.mijack.Xlog.logMethodExit("android.os.Handler com.chanapps.four.activity.SettingsActivity.getChanHandler()",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Handler com.chanapps.four.activity.SettingsActivity.getChanHandler()",this,throwable);throw throwable;}
	}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.refresh()",this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.refresh()",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.closeSearch()",this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.closeSearch()",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.setProgress(boolean)",this,on);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.setProgress(boolean)",this);}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.SettingsActivity.onCreateOptionsMenu(android.view.Menu)",this,menu);try{MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.SettingsActivity.onCreateOptionsMenu(android.view.Menu)",this);return super.onCreateOptionsMenu(menu);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.SettingsActivity.onCreateOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.SettingsActivity.onOptionsItemSelected(android.view.MenuItem)",this,item);try{switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                /*//BoardActivity.startDefaultActivity(this);*/
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.SettingsActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.global_rules_menu:
                (new StringResourceDialog(this,
                        R.layout.board_rules_dialog,
                        R.string.global_rules_menu,
                        R.string.global_rules_detail))
                        .show();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.SettingsActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.web_menu:
                String url = ChanBoard.boardUrl(this, null);
                ActivityDispatcher.launchUrlInBrowser(this, url);
            default:
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.SettingsActivity.onOptionsItemSelected(android.view.MenuItem)",this);return super.onOptionsItemSelected(item);}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.SettingsActivity.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    @Override
    public void onBackPressed() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.onBackPressed()",this);try{if (DEBUG) {Log.i(TAG, "onBackPressed()");}
        navigateUp();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.onBackPressed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SettingsActivity.onBackPressed()",this,throwable);throw throwable;}
    }

    protected void navigateUp() { com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.navigateUp()",this);try{/*// either pop off stack, or go up to all boards*/
        if (DEBUG) {Log.i(TAG, "navigateUp()");}
        Pair<Integer, ActivityManager.RunningTaskInfo> p = ActivityDispatcher.safeGetRunningTasks(this);
        int numTasks = p.first;
        ActivityManager.RunningTaskInfo task = p.second;
        if (task != null) {
            if (DEBUG) {Log.i(TAG, "navigateUp() top=" + task.topActivity + " base=" + task.baseActivity);}
            if (task.baseActivity != null
                    && !getClass().getName().equals(task.baseActivity.getClassName())) {
                if (DEBUG) {Log.i(TAG, "navigateUp() using finish instead of intents with me="
                        + getClass().getName() + " base=" + task.baseActivity.getClassName());}
                finish();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.navigateUp()",this);return;}
            }
            else if (task.baseActivity != null && numTasks >= 2) {
                if (DEBUG) {Log.i(TAG, "navigateUp() using finish as task has at least one parent, size=" + numTasks);}
                finish();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.navigateUp()",this);return;}
            }
        }
        /*// otherwise go back to the default board page*/
        Intent intent = BoardActivity.createIntent(this, ChanBoard.defaultBoardCode(this), "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.SettingsActivity.navigateUp()",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.SettingsActivity.switchBoard(com.chanapps.four.component.String,com.chanapps.four.component.String)",this,boardCode,query);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.SettingsActivity.switchBoard(com.chanapps.four.component.String,com.chanapps.four.component.String)",this);}

}

