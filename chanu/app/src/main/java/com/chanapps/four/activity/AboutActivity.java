package com.chanapps.four.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.gallery3d.ui.Log;
import com.chanapps.four.component.*;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.data.LastActivity;
import com.chanapps.four.fragment.AboutFragment;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 4/14/13
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class AboutActivity extends Activity implements ChanIdentifiedActivity, ThemeSelector.ThemeActivity {

    protected static final boolean DEBUG = false;
    public static final String TAG = AboutActivity.class.getSimpleName();
    public static final String PREF_PURCHASE_CATEGORY = "pref_about_developer_category";
    public static final int PURCHASE_REQUEST_CODE = 1987;

    protected int themeId;
    protected ThemeSelector.ThemeReceiver broadcastThemeReceiver;
    protected Handler handler;

    public static boolean startActivity(final Activity from) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.activity.AboutActivity.startActivity(android.app.Activity)",from);try{Intent intent = new Intent(from, AboutActivity.class);
        from.startActivity(intent);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.activity.AboutActivity.startActivity(android.app.Activity)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.activity.AboutActivity.startActivity(android.app.Activity)",throwable);throw throwable;}
    }

    public static Intent createIntent(Context from) {
        com.mijack.Xlog.logStaticMethodEnter("android.content.Intent com.chanapps.four.activity.AboutActivity.createIntent(android.content.Context)",from);try{com.mijack.Xlog.logStaticMethodExit("android.content.Intent com.chanapps.four.activity.AboutActivity.createIntent(android.content.Context)");return new Intent(from, AboutActivity.class);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.content.Intent com.chanapps.four.activity.AboutActivity.createIntent(android.content.Context)",throwable);throw throwable;}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        broadcastThemeReceiver = new ThemeSelector.ThemeReceiver(this);
        broadcastThemeReceiver.register();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new AboutFragment()).commit();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AboutActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public int getThemeId() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.activity.AboutActivity.getThemeId()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.activity.AboutActivity.getThemeId()",this);return themeId;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.activity.AboutActivity.getThemeId()",this,throwable);throw throwable;}
    }

    @Override
    public void setThemeId(int themeId) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.setThemeId(int)",this,themeId);try{this.themeId = themeId;com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.setThemeId(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AboutActivity.setThemeId(int)",this,throwable);throw throwable;}
    }

    @Override
    protected void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.onStart()",this);try{super.onStart();
        handler = new Handler();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AboutActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    protected void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.onStop()",this);try{super.onStop();
        handler = null;com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AboutActivity.onStop()",this,throwable);throw throwable;}
    }

    @Override
    protected void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.onPause()",this);try{super.onPause();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AboutActivity.onPause()",this,throwable);throw throwable;}
    }

    @Override
    protected void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.onDestroy()",this);try{super.onDestroy();
        broadcastThemeReceiver.unregister();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AboutActivity.onDestroy()",this,throwable);throw throwable;}
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.onSaveInstanceState(android.os.Bundle)",this,bundle);try{ActivityDispatcher.store(this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AboutActivity.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public ChanActivityId getChanActivityId() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.ChanActivityId com.chanapps.four.activity.AboutActivity.getChanActivityId()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.ChanActivityId com.chanapps.four.activity.AboutActivity.getChanActivityId()",this);return new ChanActivityId(LastActivity.ABOUT_ACTIVITY);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.ChanActivityId com.chanapps.four.activity.AboutActivity.getChanActivityId()",this,throwable);throw throwable;}
    }

    @Override
    public Handler getChanHandler() {
        com.mijack.Xlog.logMethodEnter("android.os.Handler com.chanapps.four.activity.AboutActivity.getChanHandler()",this);try{com.mijack.Xlog.logMethodExit("android.os.Handler com.chanapps.four.activity.AboutActivity.getChanHandler()",this);return handler;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Handler com.chanapps.four.activity.AboutActivity.getChanHandler()",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.refresh()",this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.refresh()",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.closeSearch()",this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.closeSearch()",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.setProgress(boolean)",this,on);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.setProgress(boolean)",this);}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.AboutActivity.onCreateOptionsMenu(android.view.Menu)",this,menu);try{MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AboutActivity.onCreateOptionsMenu(android.view.Menu)",this);return super.onCreateOptionsMenu(menu);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.AboutActivity.onCreateOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.AboutActivity.onOptionsItemSelected(android.view.MenuItem)",this,item);try{switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                /*// BoardSelectorActivity.startDefaultActivity(this);*/
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AboutActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.global_rules_menu:
                (new StringResourceDialog(this,
                        R.layout.board_rules_dialog,
                        R.string.global_rules_menu,
                        R.string.global_rules_detail))
                        .show();
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AboutActivity.onOptionsItemSelected(android.view.MenuItem)",this);return true;}
            case R.id.web_menu:
                String url = ChanBoard.boardUrl(this, null);
                ActivityDispatcher.launchUrlInBrowser(this, url);
            default:
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AboutActivity.onOptionsItemSelected(android.view.MenuItem)",this);return super.onOptionsItemSelected(item);}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.AboutActivity.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.onActivityResult(int,int,android.content.Intent)",this,requestCode,resultCode,data);try{if (DEBUG) {Log.i(TAG, "onActivityResult requestCode=" + requestCode + " resultCode=" + resultCode + " data=" + data);}
        if (requestCode != PURCHASE_REQUEST_CODE)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.onActivityResult(int,int,android.content.Intent)",this);return;}}
        if (resultCode != RESULT_OK) {
            Log.e(TAG, "Error while processing purchase request resultCode=" + resultCode);
            Toast.makeText(this, R.string.purchase_error, Toast.LENGTH_SHORT).show();
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.onActivityResult(int,int,android.content.Intent)",this);return;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AboutActivity.onActivityResult(int,int,android.content.Intent)",this,throwable);throw throwable;}
    }

    @Override
    public void onBackPressed() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.onBackPressed()",this);try{if (DEBUG) {android.util.Log.i(TAG, "onBackPressed()");}
        navigateUp();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.onBackPressed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AboutActivity.onBackPressed()",this,throwable);throw throwable;}
    }

    protected void navigateUp() { com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.navigateUp()",this);try{/*// either pop off stack, or go up to all boards*/
        if (DEBUG) {android.util.Log.i(TAG, "navigateUp()");}
        Pair<Integer, ActivityManager.RunningTaskInfo> p = ActivityDispatcher.safeGetRunningTasks(this);
        int numTasks = p.first;
        ActivityManager.RunningTaskInfo task = p.second;
        if (task != null) {
            if (DEBUG) {android.util.Log.i(TAG, "navigateUp() top=" + task.topActivity + " base=" + task.baseActivity);}
            if (task.baseActivity != null
                    && !getClass().getName().equals(task.baseActivity.getClassName())) {
                if (DEBUG) {android.util.Log.i(TAG, "navigateUp() using finish instead of intents with me="
                        + getClass().getName() + " base=" + task.baseActivity.getClassName());}
                finish();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.navigateUp()",this);return;}
            }
            else if (task.baseActivity != null && numTasks >= 2) {
                if (DEBUG) {android.util.Log.i(TAG, "navigateUp() using finish as task has at least one parent, size=" + numTasks);}
                finish();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.navigateUp()",this);return;}
            }
        }
        /*// otherwise go back to the default board page*/
        Intent intent = BoardActivity.createIntent(this, ChanBoard.defaultBoardCode(this), "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AboutActivity.navigateUp()",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AboutActivity.switchBoard(com.chanapps.four.component.String,com.chanapps.four.component.String)",this,boardCode,query);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AboutActivity.switchBoard(com.chanapps.four.component.String,com.chanapps.four.component.String)",this);}

}
