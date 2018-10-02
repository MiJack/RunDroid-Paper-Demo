package com.chanapps.four.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.chanapps.four.activity.R;
import com.chanapps.four.component.ActivityDispatcher;
import com.chanapps.four.component.URLFormatComponent;

/**
 * Created with IntelliJ IDEA.
 * User: arley
 * Date: 11/22/12
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseFragment extends PreferenceFragment
{
    protected static final boolean DEBUG = false;
    protected static String TAG = PurchaseFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PurchaseFragment.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.purchase_preferences);

        linkPreference("pref_about_store_chanapps",
                URLFormatComponent.getUrl(getActivity(), URLFormatComponent.SKREENED_CHANU_STORE_URL));
        emailPreference("pref_about_contact_us", getString(R.string.pref_about_contact_email));com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PurchaseFragment.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PurchaseFragment.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    protected void linkPreference(final String pref, final String url) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PurchaseFragment.linkPreference(java.lang.String,java.lang.String)",this,pref,url);try{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PurchaseFragment.linkPreference(java.lang.String,java.lang.String)",this);findPreference(pref).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.PurchaseFragment$1.onPreferenceClick(android.preference.Preference)",this,preference);try{ActivityDispatcher.launchUrlInBrowser(getActivity(), url);
                        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.PurchaseFragment$1.onPreferenceClick(android.preference.Preference)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.PurchaseFragment$1.onPreferenceClick(android.preference.Preference)",this,throwable);throw throwable;}
                    }
                });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PurchaseFragment.linkPreference(java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    protected void emailPreference(final String pref, final String email) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PurchaseFragment.emailPreference(java.lang.String,java.lang.String)",this,pref,email);try{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PurchaseFragment.emailPreference(java.lang.String,java.lang.String)",this);findPreference(pref).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.PurchaseFragment$2.onPreferenceClick(android.preference.Preference)",this,preference);try{Uri uri = Uri.fromParts("mailto", email, null);
                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        getActivity().startActivity(
                                Intent.createChooser(intent, getString(R.string.pref_about_send_email)));
                        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.PurchaseFragment$2.onPreferenceClick(android.preference.Preference)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.PurchaseFragment$2.onPreferenceClick(android.preference.Preference)",this,throwable);throw throwable;}
                    }
                });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PurchaseFragment.emailPreference(java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }
}
