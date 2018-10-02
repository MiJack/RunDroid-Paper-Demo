package com.chanapps.four.fragment;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import com.chanapps.four.activity.*;
import com.chanapps.four.component.ActivityDispatcher;
import com.chanapps.four.component.URLFormatComponent;
import com.chanapps.four.service.NetworkProfileManager;
import com.chanapps.four.task.AuthorizePassTask;


/**
 * Created with IntelliJ IDEA.
 * User: arley
 * Date: 11/22/12
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class PassSettingsFragment extends PreferenceFragment
{

    public static String TAG = PassSettingsFragment.class.getSimpleName();

    public static String PREF_PASS_AUTH_BUTTON = "pref_pass_auth_button";
    public static String PREF_PASS_PURCHASE_BUTTON = "pref_pass_purchase_button";
    public static String PREF_PASS_CLOSE_BUTTON = "pref_pass_close_button";

    protected DialogInterface.OnDismissListener dismissListener;
    protected SharedPreferences prefs;
    protected Preference authButton;
    protected Preference purchaseButton;
    protected Preference closeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PassSettingsFragment.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pass_preferences);

        authButton = findPreference(PREF_PASS_AUTH_BUTTON);
        authButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.PassSettingsFragment$1.onPreferenceClick(android.preference.Preference)",this,preference);try{authorizePass();
                PassSettingsFragment.this.dismiss();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PassSettingsFragment.onCreate(android.os.Bundle)",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.PassSettingsFragment$1.onPreferenceClick(android.preference.Preference)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.PassSettingsFragment$1.onPreferenceClick(android.preference.Preference)",this,throwable);throw throwable;}
            }
        });
        updateAuthorizeVisibility();

        purchaseButton = findPreference(PREF_PASS_PURCHASE_BUTTON);
        purchaseButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.PassSettingsFragment$2.onPreferenceClick(android.preference.Preference)",this,preference);try{/*// we're cheating*/
                ActivityDispatcher.launchUrlInBrowser(getActivity(),
                        URLFormatComponent.getUrl(getActivity(), URLFormatComponent.CHAN_PASS_PURCHASE_URL));
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PassSettingsFragment.onCreate(android.os.Bundle)",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.PassSettingsFragment$2.onPreferenceClick(android.preference.Preference)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.PassSettingsFragment$2.onPreferenceClick(android.preference.Preference)",this,throwable);throw throwable;}
            }
        });

        closeButton = findPreference(PREF_PASS_CLOSE_BUTTON);
        closeButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.PassSettingsFragment$3.onPreferenceClick(android.preference.Preference)",this,preference);try{ensurePrefs().edit().putBoolean(SettingsActivity.PREF_PASS_ENABLED, false).commit();
                PassSettingsFragment.this.dismiss();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PassSettingsFragment.onCreate(android.os.Bundle)",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.PassSettingsFragment$3.onPreferenceClick(android.preference.Preference)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.PassSettingsFragment$3.onPreferenceClick(android.preference.Preference)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PassSettingsFragment.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    public void show(FragmentTransaction transaction, String tag) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PassSettingsFragment.show(android.app.FragmentTransaction,com.chanapps.four.activity.String)",this,transaction,tag);try{transaction.add(this, tag);
        transaction.commit();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PassSettingsFragment.show(android.app.FragmentTransaction,com.chanapps.four.activity.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PassSettingsFragment.show(android.app.FragmentTransaction,com.chanapps.four.activity.String)",this,throwable);throw throwable;}
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PassSettingsFragment.setOnDismissListener(com.chanapps.four.activity.DialogInterface.OnDismissListener)",this,dismissListener);try{this.dismissListener = dismissListener;com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PassSettingsFragment.setOnDismissListener(com.chanapps.four.activity.DialogInterface.OnDismissListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PassSettingsFragment.setOnDismissListener(com.chanapps.four.activity.DialogInterface.OnDismissListener)",this,throwable);throw throwable;}
    }

    public void dismiss() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PassSettingsFragment.dismiss()",this);try{FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
        if (dismissListener != null)
            {dismissListener.onDismiss(null);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PassSettingsFragment.dismiss()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PassSettingsFragment.dismiss()",this,throwable);throw throwable;}
    }

    public SharedPreferences ensurePrefs() {
        com.mijack.Xlog.logMethodEnter("android.content.SharedPreferences com.chanapps.four.fragment.PassSettingsFragment.ensurePrefs()",this);try{if (prefs == null)
            {prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());}
        {com.mijack.Xlog.logMethodExit("android.content.SharedPreferences com.chanapps.four.fragment.PassSettingsFragment.ensurePrefs()",this);return prefs;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.SharedPreferences com.chanapps.four.fragment.PassSettingsFragment.ensurePrefs()",this,throwable);throw throwable;}
    }

    private boolean isPassEnabled() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.PassSettingsFragment.isPassEnabled()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.PassSettingsFragment.isPassEnabled()",this);return ensurePrefs().getBoolean(SettingsActivity.PREF_PASS_ENABLED, false);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.PassSettingsFragment.isPassEnabled()",this,throwable);throw throwable;}
    }

    private boolean isPassAvailable() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.PassSettingsFragment.isPassAvailable()",this);try{switch (NetworkProfileManager.instance().getCurrentProfile().getConnectionType()) {
            case WIFI:
            case MOBILE:
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.PassSettingsFragment.isPassAvailable()",this);return true;}
            case NO_CONNECTION:
            default:
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.PassSettingsFragment.isPassAvailable()",this);return false;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.PassSettingsFragment.isPassAvailable()",this,throwable);throw throwable;}
    }

    private void updateAuthorizeVisibility() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PassSettingsFragment.updateAuthorizeVisibility()",this);try{if (isPassAvailable())
            {authButton.setEnabled(true);}
        else
            {authButton.setEnabled(false);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PassSettingsFragment.updateAuthorizeVisibility()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PassSettingsFragment.updateAuthorizeVisibility()",this,throwable);throw throwable;}
    }

    private void authorizePass() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PassSettingsFragment.authorizePass()",this);try{String passToken = ensurePrefs().getString(SettingsActivity.PREF_PASS_TOKEN, "");
        String passPIN = ensurePrefs().getString(SettingsActivity.PREF_PASS_PIN, "");
        AuthorizePassTask authorizePassTask = new AuthorizePassTask((ChanIdentifiedActivity)getActivity(), passToken, passPIN);
        AuthorizingPassDialogFragment passDialogFragment = new AuthorizingPassDialogFragment(authorizePassTask);
        passDialogFragment.show(getFragmentManager(), AuthorizingPassDialogFragment.TAG);
        if (!authorizePassTask.isCancelled()) {
            authorizePassTask.execute(passDialogFragment);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PassSettingsFragment.authorizePass()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PassSettingsFragment.authorizePass()",this,throwable);throw throwable;}
    }

}
