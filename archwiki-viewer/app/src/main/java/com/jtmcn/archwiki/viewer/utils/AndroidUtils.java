package com.jtmcn.archwiki.viewer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;

import com.jtmcn.archwiki.viewer.R;

import static com.jtmcn.archwiki.viewer.Constants.TEXT_PLAIN_MIME;

/**
 * Utilities class for Android specific actions.
 */
public class AndroidUtils {

	private AndroidUtils() {

	}

	/**
	 * Creates an intent to prompt the user for sharing text.
	 *
	 * @param title    The name of the text being stored.
	 * @param url      The url to be shared.
	 * @param activity The current activity.
	 */
	public static Intent shareText(String title, String url, Activity activity) {
		com.mijack.Xlog.logStaticMethodEnter("android.content.Intent com.jtmcn.archwiki.viewer.utils.AndroidUtils.shareText(java.lang.String,java.lang.String,android.app.Activity)",title,url,activity);try{com.mijack.Xlog.logStaticMethodExit("android.content.Intent com.jtmcn.archwiki.viewer.utils.AndroidUtils.shareText(java.lang.String,java.lang.String,android.app.Activity)");return ShareCompat.IntentBuilder.from(activity)
				.setSubject(title)
				.setText(url)
				.setStream(Uri.parse(url))
				.setType(TEXT_PLAIN_MIME)
				.setChooserTitle(R.string.share)
				.getIntent();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.content.Intent com.jtmcn.archwiki.viewer.utils.AndroidUtils.shareText(java.lang.String,java.lang.String,android.app.Activity)",throwable);throw throwable;}
	}

	/**
	 * Creates an intent to open a link.
	 *
	 * @param url     The url to be opened.
	 * @param context The context needed to start the intent.
	 */
	public static void openLink(String url, Context context) {
		com.mijack.Xlog.logStaticMethodEnter("void com.jtmcn.archwiki.viewer.utils.AndroidUtils.openLink(java.lang.String,android.content.Context)",url,context);try{Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(intent);com.mijack.Xlog.logStaticMethodExit("void com.jtmcn.archwiki.viewer.utils.AndroidUtils.openLink(java.lang.String,android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.utils.AndroidUtils.openLink(java.lang.String,android.content.Context)",throwable);throw throwable;}
	}
}
