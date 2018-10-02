package com.chanapps.four.activity;

import android.app.ActionBar;
import android.content.*;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import com.chanapps.four.component.SendFeedback;
import com.chanapps.four.component.ThemeSelector;
import com.chanapps.four.data.BoardType;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.service.NetworkProfileManager;
import com.chanapps.four.viewer.BoardViewer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class
        AbstractBoardSpinnerActivity
        extends FragmentActivity
        implements ChanIdentifiedActivity,
        ThemeSelector.ThemeActivity
{
    protected static final String TAG = AbstractBoardSpinnerActivity.class.getSimpleName();
    protected static final boolean DEBUG = false;
    protected static final boolean DEVELOPER_MODE = false;

    protected static final String THREAD_PATTERN = "/([a-z0-9]+)/([0-9]+).*";
    protected static final String BOARD_PATTERN = "/([a-z0-9]+)/.*";
    protected static final Pattern threadPattern = Pattern.compile(THREAD_PATTERN);
    protected static final Pattern boardPattern = Pattern.compile(BOARD_PATTERN);

    protected String boardCode;
    protected long threadNo = 0;
    protected int themeId;
    protected ThemeSelector.ThemeReceiver broadcastThemeReceiver;

    protected boolean mShowNSFW = false;
    protected boolean mIgnoreMode = false;

    protected ActionBar actionBar;
    protected String[] mSpinnerArray;
    protected ArrayAdapter<String> mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onCreate(android.os.Bundle)",this,bundle);try{if (DEBUG) {Log.v(TAG, "onCreate");}
        if (DEVELOPER_MODE) {
            if (DEBUG) {Log.i(TAG, "onCreate enabling developer mode");}
            /*// only enable in development for UI-thread / mem leak testing*/
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    /*//.detectDiskReads()*/
                    /*//.detectDiskWrites()*/
                    /*//.detectNetwork()   // or .detectAll() for all detectable problems*/
                    .detectAll()
                    .penaltyLog()
                    /*//.penaltyDeath()*/
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    /*//.detectLeakedSqlLiteObjects()*/
                    /*//.detectLeakedClosableObjects()*/
                    .detectAll()
                    .penaltyLog()
                    /*//.penaltyDeath()*/
                    .build());
            if (DEBUG) {Log.i(TAG, "onCreate developer mode enabled");}
        }
        super.onCreate(bundle);

        BoardViewer.initStatics(getApplicationContext(), ThemeSelector.instance(getApplicationContext()).isDark());

        NetworkProfileManager.instance().ensureInitialized(this);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); /*// for spinning action bar*/
        broadcastThemeReceiver = new ThemeSelector.ThemeReceiver(this);
        broadcastThemeReceiver.register();
        setContentView(activityLayout());
        mShowNSFW = ChanBoard.showNSFW(getApplicationContext());
        createActionBar();
        createPreViews();
        createViews(bundle);
        if (DEBUG) {Log.v(TAG, "onCreate complete");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public int getThemeId() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.activity.AbstractBoardSpinnerActivity.getThemeId()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.activity.AbstractBoardSpinnerActivity.getThemeId()",this);return themeId;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.activity.AbstractBoardSpinnerActivity.getThemeId()",this,throwable);throw throwable;}
    }

    @Override
    public void setThemeId(int themeId) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.setThemeId(int)",this,themeId);try{this.themeId = themeId;com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.setThemeId(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.setThemeId(int)",this,throwable);throw throwable;}
    }

    protected int activityLayout() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.activity.AbstractBoardSpinnerActivity.activityLayout()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.activity.AbstractBoardSpinnerActivity.activityLayout()",this);return R.layout.board_spinner_activity_layout;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.activity.AbstractBoardSpinnerActivity.activityLayout()",this,throwable);throw throwable;}
    }

    protected void createActionBar() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.createActionBar()",this);try{actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        if (DEBUG) {Log.i(TAG, "createActionBar()");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.createActionBar()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.createActionBar()",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.createPreViews()",this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.createPreViews()",this);}

    abstract protected void createViews(Bundle bundle);

    protected void setAdapters() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.setAdapters()",this);try{if (DEBUG) {Log.i(TAG, "setSpinnerAdapter() begin this=" + this);}
        initSpinnerArray();
        mSpinnerAdapter = new ArrayAdapter<String>(actionBar.getThemedContext(),
                android.R.layout.simple_spinner_item, android.R.id.text1, mSpinnerArray);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /*//mIgnoreMode = true;*/
        /*//selectActionBarNavigationItem();*/
        /*//bindSpinnerListener();*/
        /*//mIgnoreMode = false;*/
        /*//if (DEBUG) Log.i(TAG, "setSpinnerAdapter() before bind listener");*/
        /*//if (DEBUG) Log.i(TAG, "setSpinnerAdapter() after bind listener");*/
        if (DEBUG) {Log.i(TAG, "setSpinnerAdapter() end");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.setAdapters()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.setAdapters()",this,throwable);throw throwable;}
    }

    protected boolean allAdaptersSet() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.allAdaptersSet()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.allAdaptersSet()",this);return mSpinnerAdapter != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.allAdaptersSet()",this,throwable);throw throwable;}
    }

    protected void initSpinnerArray() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.initSpinnerArray()",this);try{List<ChanBoard> boards = ChanBoard.getNewThreadBoardsRespectingNSFW(this);
        String[] boardsArray = new String[boards.size() + 1];
        int i = 0;
        boardsArray[i++] = getString(R.string.board_select);
        for (ChanBoard board : boards)
            {boardsArray[i++] = " /" + board.link + "/ " + board.name;}
        mSpinnerArray = boardsArray;com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.initSpinnerArray()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.initSpinnerArray()",this,throwable);throw throwable;}
    }

    protected void bindSpinnerListener() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.bindSpinnerListener()",this);try{actionBar.setListNavigationCallbacks(mSpinnerAdapter, spinnerNavigationListener);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.bindSpinnerListener()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.bindSpinnerListener()",this,throwable);throw throwable;}
    }

    protected void unbindSpinnerListener() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.unbindSpinnerListener()",this);try{actionBar.setListNavigationCallbacks(mSpinnerAdapter, null);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.unbindSpinnerListener()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.unbindSpinnerListener()",this,throwable);throw throwable;}
    }

    @Override
    protected void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onStart()",this);try{super.onStart();
        checkNSFW();
        mIgnoreMode = true;
        bindSpinnerListener();
        selectActionBarNavigationItem();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onStart()",this,throwable);throw throwable;}
    }

    protected void checkNSFW() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.checkNSFW()",this);try{boolean newShowNSFW = ChanBoard.showNSFW(getApplicationContext());
        if (newShowNSFW != mShowNSFW)
            {mShowNSFW = newShowNSFW;}
        setAdapters();
        /*
        if (newShowNSFW != mShowNSFW) {
            mShowNSFW = newShowNSFW;
            setAdapters();
        }
        else if (!allAdaptersSet()) {
            setAdapters();
        }
        else {
            setAdapters();
        }
        */com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.checkNSFW()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.checkNSFW()",this,throwable);throw throwable;}
    }

    @Override
    protected void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onStop()",this);try{super.onStop();
        /*//unbindSpinnerListener();*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onStop()",this,throwable);throw throwable;}
    }

    @Override
    protected void onNewIntent(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onNewIntent(android.content.Intent)",this,intent);try{super.onNewIntent(intent);
        setIntent(intent);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onNewIntent(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onNewIntent(android.content.Intent)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.refresh()",this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.refresh()",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.closeSearch()",this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.closeSearch()",this);}

    @Override
    public void setProgress(final boolean on) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.setProgress(boolean)",this,on);try{/*
        Handler handler = getChanHandler();
        if (handler != null)
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setProgressBarIndeterminateVisibility(on);
                }
            });
            */com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.setProgress(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.setProgress(boolean)",this,throwable);throw throwable;}
    }

    abstract public boolean isSelfDrawerMenu(String boardAsMenu);

    abstract protected void closeDrawer();

    private ActionBar.OnNavigationListener spinnerNavigationListener = new ActionBar.OnNavigationListener() {
        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity$1.onNavigationItemSelected(int,long)",this,itemPosition,itemId);try{if (mIgnoreMode) {
                mIgnoreMode = false;
                {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity$1.onNavigationItemSelected(int,long)",this);return true;}
            }
            String item = mSpinnerAdapter.getItem(itemPosition);
            if (DEBUG) {Log.i(TAG, "spinnerNavigationListener pos=" + itemPosition + " item=[" + item + "] this=" + this + " calling handleSelectItem");}
            boolean handle = handleSelectItem(item);
            if (handle) {
                closeDrawer();
            }
            if (DEBUG) {Log.i(TAG, "spinnerNavigationListener pos=" + itemPosition + " item=[" + item + "] returned handleSelectItem=" + handle);}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity$1.onNavigationItemSelected(int,long)",this);return handle;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity$1.onNavigationItemSelected(int,long)",this,throwable);throw throwable;}
        }
    };

    protected boolean handleSelectItem(String boardAsMenu) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.handleSelectItem(android.content.String)",this,boardAsMenu);try{boardAsMenu = boardAsMenu.trim();
        if (DEBUG) {Log.i(TAG, "handleSelectItem boardAsMenu=" + boardAsMenu);}
        if (isSelfDrawerMenu(boardAsMenu))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.handleSelectItem(android.content.String)",this);return false;}}
        if (matchForMenu(boardAsMenu))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.handleSelectItem(android.content.String)",this);return true;}}
        if (matchForBoardType(boardAsMenu))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.handleSelectItem(android.content.String)",this);return true;}}
        if (matchForThread(boardAsMenu))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.handleSelectItem(android.content.String)",this);return true;}}
        if (matchForBoard(boardAsMenu))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.handleSelectItem(android.content.String)",this);return true;}}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.handleSelectItem(android.content.String)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.handleSelectItem(android.content.String)",this,throwable);throw throwable;}
    }

    protected boolean matchForMenu(String boardAsMenu) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForMenu(android.content.String)",this,boardAsMenu);try{if (getString(R.string.board_select).equals(boardAsMenu))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForMenu(android.content.String)",this);return false;}}
        if (getString(R.string.send_feedback_menu).equals(boardAsMenu))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForMenu(android.content.String)",this);return SendFeedback.email(this);}}
        if (getString(R.string.settings_menu).equals(boardAsMenu))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForMenu(android.content.String)",this);return SettingsActivity.startActivity(this);}}
        if (getString(R.string.about_activity).equals(boardAsMenu))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForMenu(android.content.String)",this);return AboutActivity.startActivity(this);}}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForMenu(android.content.String)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForMenu(android.content.String)",this,throwable);throw throwable;}
    }

    protected boolean matchForBoardType(String boardAsMenu) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForBoardType(android.content.String)",this,boardAsMenu);try{BoardType boardType = BoardType.valueOfDrawerString(this, boardAsMenu);
        if (boardType == null)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForBoardType(android.content.String)",this);return false;}}
        String boardTypeCode = boardType.boardCode();
        if (boardTypeCode.equals(boardCode)) {
            if (DEBUG) {Log.i(TAG, "matched existing board code, exiting");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForBoardType(android.content.String)",this);return false;}
        }
        if (DEBUG) {Log.i(TAG, "matched board type /" + boardTypeCode + "/ this=" + this + " switching board");}
        switchBoard(boardTypeCode, "");
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForBoardType(android.content.String)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForBoardType(android.content.String)",this,throwable);throw throwable;}
    }

    protected boolean matchForThread(String boardAsMenu) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForThread(android.content.String)",this,boardAsMenu);try{/*// try to match board*/
        Matcher m = threadPattern.matcher(boardAsMenu);
        if (!m.matches()) {
            if (DEBUG) {Log.i(TAG, "thread matched nothing, bailing");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForThread(android.content.String)",this);return false;}
        }
        String boardCodeForJump = m.group(1);
        long threadNoForJump;
        try {
            threadNoForJump = m.group(2) == null ? -1 : Long.valueOf(m.group(2));
        }
        catch (NumberFormatException e) {
            if (DEBUG) {Log.i(TAG, "matched non-number thread, bailing");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForThread(android.content.String)",this);return false;}
        }
        if (boardCodeForJump == null || boardCodeForJump.isEmpty()) {
            if (DEBUG) {Log.i(TAG, "null thread board match, bailing");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForThread(android.content.String)",this);return false;}
        }
        if (threadNoForJump <= 0) {
            if (DEBUG) {Log.i(TAG, "bad thread match, bailing");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForThread(android.content.String)",this);return false;}
        }
        if (boardCodeForJump.equals(boardCode) && threadNoForJump == threadNo) {
            if (DEBUG) {Log.i(TAG, "matched same thread, no jump done");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForThread(android.content.String)",this);return false;}
        }
        if (DEBUG) {Log.i(TAG, "matched thread /" + boardCodeForJump + "/" + threadNoForJump + ", starting");}

        if (DEBUG) {Log.i(TAG, "starting /" + boardCodeForJump + "/" + threadNoForJump + " from this=" + this);}
        ThreadActivity.startActivity(this, boardCodeForJump, threadNoForJump, "");
        /*//mIgnoreMode = false;*/
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForThread(android.content.String)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForThread(android.content.String)",this,throwable);throw throwable;}
    }

    protected boolean matchForBoard(String boardAsMenu) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForBoard(android.content.String)",this,boardAsMenu);try{/*// try to match board*/
        Matcher m = boardPattern.matcher(boardAsMenu);
        if (!m.matches()) {
            if (DEBUG) {Log.i(TAG, "board matched nothing, bailing");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForBoard(android.content.String)",this);return false;}
        }
        String boardCodeForJump = m.group(1);
        if (boardCodeForJump == null || boardCodeForJump.isEmpty()) {
            if (DEBUG) {Log.i(TAG, "null board match, bailing");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForBoard(android.content.String)",this);return false;}
        }
        if (boardCodeForJump.equals(boardCode) && !(this instanceof ThreadActivity)) {/*// && threadNo <= 0) { // && threadNo <= 0) {*/
            if (DEBUG) {Log.i(TAG, "matched same board code, no jump done");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForBoard(android.content.String)",this);return false;}
        }
        /*
        if (!startActivityIfNeeded(intent, -1)) {
            if (DEBUG) Log.i(TAG, "startActivityIfNeeded /" + boardCodeForJump + "/ returned true, finishing current activity");
        }
        else {
            if (DEBUG) Log.i(TAG, "startActivityIfNeeded /" + boardCodeForJump + "/ returned false, switching board activity");
            switchBoard(boardCode, "");
        }
        */
        if (DEBUG) {Log.i(TAG, "matched board /" + boardCodeForJump + "/ this=" + this + " switching board");}
        switchBoard(boardCodeForJump, "");

        /*
        startActivity(intent);
        if ((this instanceof BoardSelectorActivity) || (this instanceof BoardActivity)) // don't finish single task actv
            return true;
        finish();
        */
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForBoard(android.content.String)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.activity.AbstractBoardSpinnerActivity.matchForBoard(android.content.String)",this,throwable);throw throwable;}
    }

    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onResume()",this);try{super.onResume();
        if (DEBUG) {Log.i(TAG, "onResume /" + boardCode + "/ this=" + this);}
        if (DEBUG) {Log.i(TAG, "onCreate isTaskRoot()=" + isTaskRoot() + " intent=" + getIntent());}
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
                    intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
            }
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onResume()",this,throwable);throw throwable;}
    }

    @Override
    protected void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onDestroy()",this);try{super.onDestroy();
        broadcastThemeReceiver.unregister();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.onDestroy()",this,throwable);throw throwable;}
    }

    protected void selectActionBarNavigationItem() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.selectActionBarNavigationItem()",this);try{if (DEBUG) {Log.i(TAG, "selectActionBarNavigationItem /" + boardCode + "/ begin");}
        int pos = -1;
        for (int i = 0; i < mSpinnerAdapter.getCount(); i++) {
            String boardText = mSpinnerAdapter.getItem(i);
            BoardType type = BoardType.valueOfDrawerString(this, boardText);
            if (type != null && type.boardCode().equals(boardCode)) {
                pos = i;
                break;
            }
            else if (boardText.matches(" ?/" + boardCode + "/.*")) {
                pos = i;
                break;
            }
        }
        if (pos >= 0) {
            String boardText = mSpinnerAdapter.getItem(pos);
            if (DEBUG) {Log.i(TAG, "selectActionBarNavigationItem /" + boardCode + "/ found pos=" + pos + " text=" + boardText);}
        }
        else {
            pos = 0;
            if (DEBUG) {Log.i(TAG, "selectActionBarNavigationItem /" + boardCode + "/ not found defaulted pos=" + pos);}
        }
        if (actionBar.getSelectedNavigationIndex() != pos) {
            actionBar.setSelectedNavigationItem(pos);
        }
        else {
            mIgnoreMode = false;
        }
        if (DEBUG) {Log.i(TAG, "selectActionBarNavigationItem /" + boardCode + "/ set pos=" + pos + " end");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.selectActionBarNavigationItem()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.selectActionBarNavigationItem()",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.switchBoard(android.content.String,android.content.String)",this,boardCode,query);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.AbstractBoardSpinnerActivity.switchBoard(android.content.String,android.content.String)",this);}

    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= 18) {
            if (hasFocus) {
                int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
                int newUiOptions = uiOptions;
                newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
                newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                newUiOptions ^= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                newUiOptions ^= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                //newUiOptions ^= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
            }
        }
    }
    */

}
