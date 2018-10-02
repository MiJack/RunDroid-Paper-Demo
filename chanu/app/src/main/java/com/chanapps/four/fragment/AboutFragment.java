package com.chanapps.four.fragment;

import java.text.DateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

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
public class AboutFragment extends PreferenceFragment
{
    protected static final boolean DEBUG = false;
    protected static String TAG = AboutFragment.class.getSimpleName();
    protected static final String VERSION_DATE_FORMAT = "yyyy.MM.dd";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.AboutFragment.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.about_preferences);

        versionPreference("pref_about_application_version");
        linkPreference("pref_about_icon", URLFormatComponent.getUrl(getActivity(), URLFormatComponent.MARKET_CORP_URL));
        linkPreference("pref_about_application_version", URLFormatComponent.getUrl(getActivity(), URLFormatComponent.MARKET_APP_URL));
        linkPreference("pref_about_application_rate", URLFormatComponent.getUrl(getActivity(), URLFormatComponent.MARKET_APP_URL));
        linkPreference("pref_about_application_gplus", URLFormatComponent.getUrl(getActivity(), URLFormatComponent.GOOGLE_PLUS_CHANU_URL));
        linkPreference("pref_about_data_4chan", URLFormatComponent.getUrl(getActivity(), URLFormatComponent.GITHUB_CHAN_API_URL));
        linkPreference("pref_about_data_uil", URLFormatComponent.getUrl(getActivity(), URLFormatComponent.GITHUB_UIL_URL));
        linkPreference("pref_about_data_pulltorefresh", URLFormatComponent.getUrl(getActivity(), URLFormatComponent.GITHUB_ABPTR_URL));
        linkPreference("pref_about_data_color", URLFormatComponent.getUrl(getActivity(), URLFormatComponent.GOOGLE_CODE_COLOR_PICKER_URL));
        linkPreference("pref_about_store_chanapps", URLFormatComponent.getUrl(getActivity(), URLFormatComponent.SKREENED_CHANU_STORE_URL));
        linkPreference("pref_about_translations_de", URLFormatComponent.getUrl(getActivity(), URLFormatComponent.GERMAN_TRANSLATOR_URL));com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.AboutFragment.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.AboutFragment.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    protected void versionPreference(final String pref) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.AboutFragment.versionPreference(java.lang.String)",this,pref);try{if (DEBUG) {Log.i(TAG, "versionPreference");}
        Preference p = findPreference(pref);
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            ApplicationInfo appInfo = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), 0);
            
            String version = info.versionName;
            
            ZipFile zf = new ZipFile(appInfo.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            
            Date d = new Date(time);
            
            zf.close();
            
            String dateStr = DateFormat.getDateInstance(DateFormat.MEDIUM).format(d);
            String title = String.format(getString(R.string.pref_about_application_version), version);
            String summary = String.format(getString(R.string.pref_about_application_version_sum), dateStr);
            p.setTitle(title);
            p.setSummary(summary);
            if (DEBUG) {Log.i(TAG, "set version title=" + title + " summary=" + summary);}
        }
        catch (Exception e) {
            Log.e(TAG, "Exception setting version preference", e);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.AboutFragment.versionPreference(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.AboutFragment.versionPreference(java.lang.String)",this,throwable);throw throwable;}
    }

    protected void linkPreference(final String pref, final String url) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.AboutFragment.linkPreference(java.lang.String,java.lang.String)",this,pref,url);try{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.AboutFragment.linkPreference(java.lang.String,java.lang.String)",this);findPreference(pref).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.AboutFragment$1.onPreferenceClick(android.preference.Preference)",this,preference);try{ActivityDispatcher.launchUrlInBrowser(getActivity(), url);
                        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.AboutFragment$1.onPreferenceClick(android.preference.Preference)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.AboutFragment$1.onPreferenceClick(android.preference.Preference)",this,throwable);throw throwable;}
                    }
                });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.AboutFragment.linkPreference(java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    protected void intentOrLinkPreference(final String pref, final String googlePlusId, final String url) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.AboutFragment.intentOrLinkPreference(java.lang.String,java.lang.String,java.lang.String)",this,pref,googlePlusId,url);try{findPreference(pref).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.AboutFragment$2.onPreferenceClick(android.preference.Preference)",this,preference);try{if (getActivity() == null)
                            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.AboutFragment.intentOrLinkPreference(java.lang.String,java.lang.String,java.lang.String)",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.AboutFragment$2.onPreferenceClick(android.preference.Preference)",this);return true;}}}

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setClassName("com.google.android.apps.plus", "com.google.android.apps.plus.phone.UrlGatewayActivity");
                        i.putExtra("customAppUri", googlePlusId);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            {startActivity(i);}
                        else
                            {ActivityDispatcher.launchUrlInBrowser(getActivity(), url);}
                        {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.AboutFragment.intentOrLinkPreference(java.lang.String,java.lang.String,java.lang.String)",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.AboutFragment$2.onPreferenceClick(android.preference.Preference)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.AboutFragment$2.onPreferenceClick(android.preference.Preference)",this,throwable);throw throwable;}
                    }
                });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.AboutFragment.intentOrLinkPreference(java.lang.String,java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

}
