package com.jtmcn.archwiki.viewer;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * The {@link PreferenceActivity} to change settings for the application.
 */
public class PreferencesActivity extends AppCompatActivity {
	public static final String KEY_TEXT_SIZE = "textSize";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.PreferencesActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.prefs, false);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.PreferencesActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.PreferencesActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.PreferencesActivity.onPostCreate(@Nullable Bundle)",this,savedInstanceState);try{super.onPostCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);

		getFragmentManager().beginTransaction()
				.replace(R.id.settings_content, new ApplicationPreferenceFragment())
				.commit();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle(R.string.menu_settings);

		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.PreferencesActivity.onPostCreate(@Nullable Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.PreferencesActivity.onPostCreate(@Nullable Bundle)",this,throwable);throw throwable;}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.PreferencesActivity.onOptionsItemSelected(android.view.MenuItem)",this,item);try{switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}
		{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.PreferencesActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.PreferencesActivity.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
	}

	/**
	 * Loads the activities preferences into the fragment.
	 */
	public static class ApplicationPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle bundle) {
			com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.PreferencesActivity$ApplicationPreferenceFragment.onCreate(android.os.Bundle)",this,bundle);try{super.onCreate(bundle);
			addPreferencesFromResource(R.xml.prefs);com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.PreferencesActivity$ApplicationPreferenceFragment.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.PreferencesActivity$ApplicationPreferenceFragment.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
		}
	}
}
