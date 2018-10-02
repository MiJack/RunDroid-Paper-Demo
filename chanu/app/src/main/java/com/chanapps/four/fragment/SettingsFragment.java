package com.chanapps.four.fragment;

import java.io.File;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.chanapps.four.activity.AboutActivity;
import com.chanapps.four.activity.R;
import com.chanapps.four.activity.SettingsActivity;
import com.chanapps.four.activity.StartupActivity;
import com.chanapps.four.data.ChanFileStorage;


/**
 * Created with IntelliJ IDEA.
 * User: arley
 * Date: 11/22/12
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsFragment
        extends PreferenceFragment
/*//        implements SharedPreferences.OnSharedPreferenceChangeListener*/
{

    protected static final boolean DEBUG = false;
    protected static final String TAG = SettingsFragment.class.getSimpleName();

    protected static final int STARTUP_INTENT = 0x80134148; /*// magic number*/
    protected static final int RESTART_DELAY_MS = 250;

    protected Preference downloadLocationButton;

    public Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.SettingsFragment.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        initPreferenceScreen();
/*//        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.SettingsFragment.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    public void initPreferenceScreen() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.SettingsFragment.initPreferenceScreen()",this);try{addPreferencesFromResource(R.xml.preferences);
        /*
        Preference blocklistButton = findPreference(SettingsActivity.PREF_BLOCKLIST_BUTTON);
        blocklistButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                List<Pair<String, ChanBlocklist.BlockType>> blocks = ChanBlocklist.getSorted(getActivity());
                (new BlocklistViewAllDialogFragment(blocks)).show(getFragmentManager(),TAG);
                return true;
            }
        });
        */
        Preference resetPrefsButton = findPreference(SettingsActivity.PREF_RESET_TO_DEFAULTS);
        resetPrefsButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
     com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.SettingsFragment$1.onPreferenceClick(android.preference.Preference)",this,preference);try{           (new ResetPreferencesDialogFragment(SettingsFragment.this))
                        .show(getFragmentManager(), SettingsFragment.TAG);
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment.initPreferenceScreen()",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.SettingsFragment$1.onPreferenceClick(android.preference.Preference)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.SettingsFragment$1.onPreferenceClick(android.preference.Preference)",this,throwable);throw throwable;}
            }
        });

        Preference clearCacheButton = findPreference(SettingsActivity.PREF_CLEAR_CACHE);
        clearCacheButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.SettingsFragment$2.onPreferenceClick(android.preference.Preference)",this,preference);try{(new ClearCacheDialogFragment(SettingsFragment.this))
                        .show(getFragmentManager(), SettingsFragment.TAG);
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment.initPreferenceScreen()",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.SettingsFragment$2.onPreferenceClick(android.preference.Preference)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.SettingsFragment$2.onPreferenceClick(android.preference.Preference)",this,throwable);throw throwable;}
            }
        });
        /*
        Preference clearWatchlistButton = findPreference(SettingsActivity.PREF_CLEAR_WATCHLIST);
        clearWatchlistButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                (new WatchlistClearDialogFragment())
                        .show(getFragmentManager(), WatchlistClearDialogFragment.TAG);
                return true;
            }
        });

        Preference clearFavoritesButton = findPreference(SettingsActivity.PREF_CLEAR_FAVORITES);
        clearFavoritesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                (new FavoritesClearDialogFragment())
                        .show(getFragmentManager(), FavoritesClearDialogFragment.TAG);
                return true;
            }
        });
        */
        Preference aboutButton = findPreference(SettingsActivity.PREF_ABOUT);
        aboutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.SettingsFragment$3.onPreferenceClick(android.preference.Preference)",this,preference);try{Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment.initPreferenceScreen()",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.SettingsFragment$3.onPreferenceClick(android.preference.Preference)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.SettingsFragment$3.onPreferenceClick(android.preference.Preference)",this,throwable);throw throwable;}
            }
        });

        downloadLocationButton = findPreference(SettingsActivity.PREF_DOWNLOAD_LOCATION);
        downloadLocationButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.SettingsFragment$4.onPreferenceClick(android.preference.Preference)",this,preference);try{File downloadFolder = ChanFileStorage.getDownloadFolder(getActivity(), null, 0, true);
                DialogChooseDirectory d = new DialogChooseDirectory(getActivity(), chooseDirectoryHandler,
                        downloadFolder.getAbsolutePath());
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment.initPreferenceScreen()",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.SettingsFragment$4.onPreferenceClick(android.preference.Preference)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.SettingsFragment$4.onPreferenceClick(android.preference.Preference)",this,throwable);throw throwable;}
            }
        });
        File downloadFolder = ChanFileStorage.getDownloadFolder(getActivity(), null, 0, true);
        downloadLocationButton.setSummary(downloadFolder.getAbsolutePath());

        Preference forceEnglish = findPreference(SettingsActivity.PREF_FORCE_ENGLISH);

        forceEnglish.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.SettingsFragment$5.onPreferenceChange(android.preference.Preference,java.lang.Object)",this,preference,newValue);try{/*//Toast.makeText(getActivity(), "onChange", Toast.LENGTH_SHORT).show();*/
                restartApp();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment.initPreferenceScreen()",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.SettingsFragment$5.onPreferenceChange(android.preference.Preference,java.lang.Object)",this);return true;}}  /*//To change body of implemented methods use File | Settings | File Templates.*/}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.SettingsFragment$5.onPreferenceChange(android.preference.Preference,java.lang.Object)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.SettingsFragment.initPreferenceScreen()",this,throwable);throw throwable;}

    };


        /*
        Preference.OnPreferenceClickListener namesListener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //NetworkProfileManager.instance().getUserStatistics().featureUsed(UserStatistics.ChanFeature.SETTINGS_NAMES);
                return false;
            }
        };
        Preference name = findPreference(SettingsActivity.PREF_USER_NAME);
        Preference email = findPreference(SettingsActivity.PREF_USER_EMAIL);
        Preference password = findPreference(SettingsActivity.PREF_USER_PASSWORD);
        name.setOnPreferenceClickListener(namesListener);
        email.setOnPreferenceClickListener(namesListener);
        password.setOnPreferenceClickListener(namesListener);
        */

        /*
        Preference.OnPreferenceChangeListener abbrevBoardsListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (DEBUG) Log.i(TAG, "onPrefrenceChange pref=" + preference.getKey() + " newVal=" + newValue);
                Boolean b = newValue instanceof Boolean ? (Boolean) newValue : null;
                BoardActivity.updateAbbrev(getActivity(), b != null ? b : false);
                return true;
            }
        };
        Preference abbrevBoards = findPreference(SettingsActivity.PREF_USE_ABBREV_BOARDS);
        abbrevBoards.setOnPreferenceChangeListener(abbrevBoardsListener);
        */
        /*
        Preference.OnPreferenceChangeListener fastScrollBoardsListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (DEBUG) Log.i(TAG, "onPrefrenceChange pref=" + preference.getKey() + " newVal=" + newValue);
                Boolean b = newValue instanceof Boolean ? (Boolean) newValue : null;
                BoardActivity.updateFastScroll(getActivity(), b != null ? b : false);
                return true;
            }
        };
        Preference fastScrollBoards = findPreference(SettingsActivity.PREF_USE_FAST_SCROLL);
        fastScrollBoards.setOnPreferenceChangeListener(fastScrollBoardsListener);
        */
        /*
        Preference.OnPreferenceChangeListener catalogListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (DEBUG) Log.i(TAG, "onPrefrenceChange pref=" + preference.getKey() + " newVal=" + newValue);
                Boolean b = newValue instanceof Boolean ? (Boolean) newValue : null;
                BoardActivity.updateCatalog(getActivity(), b != null ? b : false);
                return true;
            }
        };
        Preference catalog = findPreference(SettingsActivity.PREF_USE_CATALOG);
        catalog.setOnPreferenceChangeListener(catalogListener);
        */
        /*
        Preference.OnPreferenceChangeListener hideLastRepliesListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (DEBUG) Log.i(TAG, "onPrefrenceChange pref=" + preference.getKey() + " newVal=" + newValue);
                Boolean b = newValue instanceof Boolean ? (Boolean) newValue : null;
                BoardActivity.updateHideLastReplies(getActivity(), b != null ? b : false);
                return true;
            }
        };
        Preference hideLastReplies = findPreference(SettingsActivity.PREF_HIDE_LAST_REPLIES);
        hideLastReplies.setOnPreferenceChangeListener(hideLastRepliesListener);
        */
        /*
        Preference.OnPreferenceChangeListener nsfwListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                BoardActivity.refreshAllBoards();
                return true;
            }
        };
        Preference showNsfw = findPreference(SettingsActivity.PREF_SHOW_NSFW_BOARDS);
        showNsfw.setOnPreferenceChangeListener(nsfwListener);
        */
        /*
        Preference.OnPreferenceChangeListener themeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SettingsActivity activity = (SettingsActivity)getActivity();
                activity.recreate();
                return true;
            }
        };
        Preference theme = findPreference(SettingsActivity.PREF_THEME);
        theme.setOnPreferenceChangeListener(themeListener);
        */

    private void restartApp() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.SettingsFragment.restartApp()",this);try{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment.restartApp()",this);new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.SettingsFragment$6.run()",this);try{Context context = getActivity();
                if (context == null) {
                    {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment$6.run()",this);return;}
                }
                Intent mStartActivity = new Intent(context, StartupActivity.class);
                int mPendingIntentId = STARTUP_INTENT;
                PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + RESTART_DELAY_MS, mPendingIntent);
                System.exit(0);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.SettingsFragment$6.run()",this,throwable);throw throwable;}
            }
        }, RESTART_DELAY_MS);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.SettingsFragment.restartApp()",this,throwable);throw throwable;}
    }

    /*
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        boolean passEnabled = prefs.getBoolean(SettingsActivity.PREF_PASS_ENABLED, false);
        int stringId = validatePass(prefs);
        boolean passInvalid = stringId > 0;
        Toast.makeText(getActivity(), "key: " + key + " invalid:" + passInvalid, Toast.LENGTH_SHORT).show();
        if (SettingsActivity.PREF_PASS_TOKEN.equals(key) && passEnabled && passInvalid) {
            disablePass(prefs, stringId);
        }
        else if (SettingsActivity.PREF_PASS_PIN.equals(key) && passEnabled && passInvalid) {
            disablePass(prefs, stringId);
        }
        else if (SettingsActivity.PREF_PASS_ENABLED.equals(key) && passEnabled && passInvalid) {
            disablePass(prefs, stringId);
        }
    }

    private int validatePass(SharedPreferences prefs) {
        String token = prefs.getString(SettingsActivity.PREF_PASS_TOKEN, null);
        String pin = prefs.getString(SettingsActivity.PREF_PASS_PIN, null);
        if ("".equals(token) || token.length() != 10) {
            return R.string.pref_pass_token_invalid;
        }
        else if ("".equals(pin) || pin.length() != 6) {
            return R.string.pref_pass_pin_invalid;
        }
        else {
            return 0;
        }
    }

    private void disablePass(SharedPreferences prefs, int stringId) {
        prefs.edit().putBoolean(SettingsActivity.PREF_PASS_ENABLED, false).commit();
        if (getActivity() != null)
            Toast.makeText(getActivity(), stringId, Toast.LENGTH_SHORT).show();
        ensureHandler().sendEmptyMessageDelayed(0, 1000);
    }
*/

    public Handler ensureHandler() {
        com.mijack.Xlog.logMethodEnter("android.os.Handler com.chanapps.four.fragment.SettingsFragment.ensureHandler()",this);try{if (handler == null)
            {handler = new ReloadPrefsHandler(this);}
        {com.mijack.Xlog.logMethodExit("android.os.Handler com.chanapps.four.fragment.SettingsFragment.ensureHandler()",this);return handler;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Handler com.chanapps.four.fragment.SettingsFragment.ensureHandler()",this,throwable);throw throwable;}
    }

    protected class ReloadPrefsHandler extends Handler {
        SettingsFragment fragment;
        public ReloadPrefsHandler() {}
        public ReloadPrefsHandler(SettingsFragment fragment) {
            this.fragment = fragment;
        }
        @Override
        public void handleMessage(Message msg) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.SettingsFragment$ReloadPrefsHandler.handleMessage(android.os.Message)",this,msg);try{((BaseAdapter)fragment.getPreferenceScreen().getRootAdapter()).notifyDataSetInvalidated();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment$ReloadPrefsHandler.handleMessage(android.os.Message)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.SettingsFragment$ReloadPrefsHandler.handleMessage(android.os.Message)",this,throwable);throw throwable;}
        }
    }

    @Override
    public void onAttach(Activity activity) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.SettingsFragment.onAttach(android.app.Activity)",this,activity);try{super.onAttach(activity);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment.onAttach(android.app.Activity)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.SettingsFragment.onAttach(android.app.Activity)",this,throwable);throw throwable;}
    }

    @Override
    public void onDetach() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.SettingsFragment.onDetach()",this);try{super.onDetach();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment.onDetach()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.SettingsFragment.onDetach()",this,throwable);throw throwable;}
    }

    protected DialogChooseDirectory.Result chooseDirectoryHandler = new DialogChooseDirectory.Result() {
        @Override
        public void onChooseDirectory(String dir) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.SettingsFragment$7.onChooseDirectory(java.lang.String)",this,dir);try{try {
                File file = new File(dir);
                if (file.exists() && file.isDirectory() && file.canWrite()) {
                    String msg = String.format(getString(R.string.pref_download_location_set), file.getAbsolutePath());
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    getPreferenceManager()
                            .getSharedPreferences()
                            .edit()
                            .putString(SettingsActivity.PREF_DOWNLOAD_LOCATION, file.getAbsolutePath())
                            .commit();
                    downloadLocationButton.setSummary(file.getAbsolutePath());
                }
                else {
                    Toast.makeText(getActivity(), R.string.pref_download_location_error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.pref_download_location_error, Toast.LENGTH_SHORT).show();
            }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.SettingsFragment$7.onChooseDirectory(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.SettingsFragment$7.onChooseDirectory(java.lang.String)",this,throwable);throw throwable;}
        }
    };

}
