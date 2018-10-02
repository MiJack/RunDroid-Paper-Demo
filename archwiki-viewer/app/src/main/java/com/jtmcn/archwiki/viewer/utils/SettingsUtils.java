package com.jtmcn.archwiki.viewer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jtmcn.archwiki.viewer.PreferencesActivity;

/**
 * Created by kevin on 6/7/2017.
 */

public class SettingsUtils {

	public static int getFontSize(Context context) {
		com.mijack.Xlog.logStaticMethodEnter("int com.jtmcn.archwiki.viewer.utils.SettingsUtils.getFontSize(android.content.Context)",context);try{SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		/*// https://stackoverflow.com/questions/11346916/listpreference-use-string-array-as-entry-and-integer-array-as-entry-values-does*/
		/*// the value of this preference must be parsed as a string*/
		String fontSizePref = prefs.getString(PreferencesActivity.KEY_TEXT_SIZE, "2");
		{com.mijack.Xlog.logStaticMethodExit("int com.jtmcn.archwiki.viewer.utils.SettingsUtils.getFontSize(android.content.Context)");return Integer.valueOf(fontSizePref);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.jtmcn.archwiki.viewer.utils.SettingsUtils.getFontSize(android.content.Context)",throwable);throw throwable;}

	}
}
