package com.chanapps.four.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.chanapps.four.activity.R;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.mColorPicker.ColorPickerDialog;
import com.chanapps.four.service.FetchChanDataService;
import com.chanapps.four.service.FetchPopularThreadsService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/15/13
 * Time: 11:07 PM
 */
public abstract class AbstractWidgetConfigureActivity extends FragmentActivity {

    public static final String TAG = AbstractWidgetConfigureActivity.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static final long DELAY_BOARD_IMAGE_MS = 5 * 1000; /*// give board fetch time to finish*/

    protected int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    protected WidgetConf widgetConf;

    protected abstract int getContentViewLayout();

    protected abstract void setBoardImages();

    protected abstract String getWidgetType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(getContentViewLayout());
        appWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.e(TAG, "Invalid app widget id received, exiting configuration");
            finish();
        } else {
            if (DEBUG) {Log.i(TAG, "Configuring widget=" + appWidgetId);}
        }
        widgetConf = WidgetProviderUtils.loadWidgetConf(this, appWidgetId);
        if (widgetConf == null)
            {widgetConf = new WidgetConf(appWidgetId, getWidgetType());} /*// new widget or no config;*/
        setupSpinner();
        setupCheckboxes();
        addColorClickHandler();
        addDoneClickHandler();
        initWidgetLayoutState();
        AbstractWidgetConfigureActivity.this.setResult(Activity.RESULT_CANCELED);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.onResume()",this);try{super.onResume();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.onResume()",this,throwable);throw throwable;}
    }

    @Override
    protected void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.onPause()",this);try{super.onPause();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.onPause()",this,throwable);throw throwable;}
    }

    protected String[] spinnerArray() {
        com.mijack.Xlog.logMethodEnter("[android.widget.String com.chanapps.four.widget.AbstractWidgetConfigureActivity.spinnerArray()",this);try{List<ChanBoard> boards = ChanBoard.getNewThreadBoardsRespectingNSFW(this);
        String[] boardsArray = new String[boards.size() + 1];
        int i = 0;
        for (ChanBoard board : boards)
            {boardsArray[i++] = "/" + board.link + "/ " + board.name;}
        boardsArray[i++] = getString(R.string.board_watch);
        {com.mijack.Xlog.logMethodExit("[android.widget.String com.chanapps.four.widget.AbstractWidgetConfigureActivity.spinnerArray()",this);return boardsArray;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[android.widget.String com.chanapps.four.widget.AbstractWidgetConfigureActivity.spinnerArray()",this,throwable);throw throwable;}
    }

    protected ArrayAdapter<String> createSpinnerAdapter() {
        com.mijack.Xlog.logMethodEnter("android.widget.ArrayAdapter com.chanapps.four.widget.AbstractWidgetConfigureActivity.createSpinnerAdapter()",this);try{ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1, spinnerArray());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        {com.mijack.Xlog.logMethodExit("android.widget.ArrayAdapter com.chanapps.four.widget.AbstractWidgetConfigureActivity.createSpinnerAdapter()",this);return spinnerAdapter;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.ArrayAdapter com.chanapps.four.widget.AbstractWidgetConfigureActivity.createSpinnerAdapter()",this,throwable);throw throwable;}
    }

    protected void setupSpinner() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.setupSpinner()",this);try{Spinner spinner = (Spinner) findViewById(R.id.board_spinner);
        ArrayAdapter<String> spinnerAdapter = createSpinnerAdapter();
        spinner.setAdapter(spinnerAdapter);
        int position = 0;
        if (widgetConf.boardCode == null || widgetConf.boardCode.isEmpty()) {
            position = 0;
        } else {
            for (int i = 0; i < spinnerAdapter.getCount(); i++) {
                String boardText = (String) spinnerAdapter.getItem(i);
                if (ChanBoard.isVirtualBoard(widgetConf.boardCode)
                        && ChanBoard.WATCHLIST_BOARD_CODE.equals(widgetConf.boardCode)
                        && boardText.matches(getString(R.string.board_watch))) {
                    position = i;
                    break;
                }
                if (ChanBoard.isVirtualBoard(widgetConf.boardCode)
                        && ChanBoard.POPULAR_BOARD_CODE.equals(widgetConf.boardCode)
                        && boardText.matches(getString(R.string.board_popular))) {
                    position = i;
                    break;
                }
                if (ChanBoard.isVirtualBoard(widgetConf.boardCode)
                        && ChanBoard.LATEST_BOARD_CODE.equals(widgetConf.boardCode)
                        && boardText.matches(getString(R.string.board_latest))) {
                    position = i;
                    break;
                }
                if (ChanBoard.isVirtualBoard(widgetConf.boardCode)
                        && ChanBoard.LATEST_IMAGES_BOARD_CODE.equals(widgetConf.boardCode)
                        && boardText.matches(getString(R.string.board_latest_images))) {
                    position = i;
                    break;
                } else if (!ChanBoard.isVirtualBoard(widgetConf.boardCode)
                        && boardText.matches("/" + widgetConf.boardCode + "/.*")) {
                    position = i;
                    break;
                }
            }
        }
        spinner.setSelection(position, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$1.onItemSelected(android.widget.AdapterView,android.view.View,int,long)",this,parent,view,position,id);try{final Context context = AbstractWidgetConfigureActivity.this.getApplicationContext();
                updateWidgetConfWithSelectedBoard((String) parent.getItemAtPosition(position));
                boolean onDisk = ChanFileStorage.isBoardCachedOnDisk(context, widgetConf.boardCode);
                boolean freshFetch;
                if (onDisk) {
                    freshFetch = false;
                } else {
                    if (ChanBoard.WATCHLIST_BOARD_CODE.equals(widgetConf.boardCode)) {
                        freshFetch = false;
                    } else if (ChanBoard.isPopularBoard(widgetConf.boardCode)) {
                        if (DEBUG) {Log.i(TAG, "scheduling popular fetch for board=" + widgetConf.boardCode);}
                        freshFetch = FetchPopularThreadsService.schedulePopularFetchService(context, true, false);
                    } else if (ChanBoard.isVirtualBoard(widgetConf.boardCode)) {
                        if (DEBUG) {Log.i(TAG, "skipping fetch for non-popular virtual board=" + widgetConf.boardCode);}
                        freshFetch = false;
                    } else {
                        if (DEBUG) {Log.i(TAG, "scheduling fetch for board=" + widgetConf.boardCode);}
                        freshFetch = FetchChanDataService.scheduleBoardFetch(context, widgetConf.boardCode, true, false);
                    }
                }

                if (freshFetch) {
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$1$1.run()",this);try{setBoardImages();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$1$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$1$1.run()",this,throwable);throw throwable;}
                        }
                    }, DELAY_BOARD_IMAGE_MS);
                }com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$1.onItemSelected(android.widget.AdapterView,android.view.View,int,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$1.onItemSelected(android.widget.AdapterView,android.view.View,int,long)",this,throwable);throw throwable;}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$1.onNothingSelected(android.widget.AdapterView)",this,parent);try{updateWidgetConfWithSelectedBoard("");com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$1.onNothingSelected(android.widget.AdapterView)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$1.onNothingSelected(android.widget.AdapterView)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.setupSpinner()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.setupSpinner()",this,throwable);throw throwable;}
    }

    protected void setupCheckboxes() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.setupCheckboxes()",this);try{CheckBox roundedCorners = (CheckBox) findViewById(R.id.rounded_corners);
        CheckBox showBoardButton = (CheckBox) findViewById(R.id.show_board);
        CheckBox showRefreshButton = (CheckBox) findViewById(R.id.show_refresh);
        CheckBox showConfigureButton = (CheckBox) findViewById(R.id.show_configure);
        roundedCorners.setChecked(widgetConf.roundedCorners);
        showBoardButton.setChecked(widgetConf.showBoardTitle);
        showRefreshButton.setChecked(widgetConf.showRefreshButton);
        showConfigureButton.setChecked(widgetConf.showConfigureButton);
        roundedCorners.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$2.onCheckedChanged(android.widget.CompoundButton,boolean)",this,buttonView,isChecked);try{widgetConf.roundedCorners = isChecked;
                updateContainerBackgroundState();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$2.onCheckedChanged(android.widget.CompoundButton,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$2.onCheckedChanged(android.widget.CompoundButton,boolean)",this,throwable);throw throwable;}
            }
        });
        showBoardButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$3.onCheckedChanged(android.widget.CompoundButton,boolean)",this,buttonView,isChecked);try{widgetConf.showBoardTitle = isChecked;
                updateBoardTitleState();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$3.onCheckedChanged(android.widget.CompoundButton,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$3.onCheckedChanged(android.widget.CompoundButton,boolean)",this,throwable);throw throwable;}
            }
        });
        showRefreshButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$4.onCheckedChanged(android.widget.CompoundButton,boolean)",this,buttonView,isChecked);try{widgetConf.showRefreshButton = isChecked;
                updateRefreshButtonState();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$4.onCheckedChanged(android.widget.CompoundButton,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$4.onCheckedChanged(android.widget.CompoundButton,boolean)",this,throwable);throw throwable;}
            }
        });
        showConfigureButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$5.onCheckedChanged(android.widget.CompoundButton,boolean)",this,buttonView,isChecked);try{widgetConf.showConfigureButton = isChecked;
                updateConfigButtonState();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$5.onCheckedChanged(android.widget.CompoundButton,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$5.onCheckedChanged(android.widget.CompoundButton,boolean)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.setupCheckboxes()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.setupCheckboxes()",this,throwable);throw throwable;}
    }

    protected void addColorClickHandler() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.addColorClickHandler()",this);try{EditText backgroundColorButton = (EditText) findViewById(R.id.board_title_color);
        if (backgroundColorButton == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.addColorClickHandler()",this);return;}}
        backgroundColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$6.onClick(android.view.View)",this,v);try{final ColorPickerDialog d = new ColorPickerDialog(AbstractWidgetConfigureActivity.this,
                        widgetConf.boardTitleColor);
                d.setAlphaSliderVisible(true);
                d.setButton(DialogInterface.BUTTON_POSITIVE,
                        getString(R.string.thread_context_select),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$6$1.onClick(android.content.DialogInterface,int)",this,dialog,which);try{widgetConf.boardTitleColor = d.getColor();
                                updateBoardTitleState();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$6$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$6$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                            }
                        });
                d.setButton(DialogInterface.BUTTON_NEGATIVE,
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$6$2.onClick(android.content.DialogInterface,int)",this,dialog,which);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$6$2.onClick(android.content.DialogInterface,int)",this);}
                        });
                d.show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$6.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity$6.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.addColorClickHandler()",this,throwable);throw throwable;}
    }

    protected void updateWidgetConfWithSelectedBoard(String boardSpinnerLine) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateWidgetConfWithSelectedBoard(android.widget.String)",this,boardSpinnerLine);try{if (boardSpinnerLine == null || boardSpinnerLine.isEmpty())
            {boardSpinnerLine = "";}
        String boardCode;
        if (getString(R.string.board_watch).equals(boardSpinnerLine)) {
            boardCode = ChanBoard.WATCHLIST_BOARD_CODE;
        } else if (getString(R.string.board_popular).equals(boardSpinnerLine)) {
            boardCode = ChanBoard.POPULAR_BOARD_CODE;
        } else if (getString(R.string.board_latest).equals(boardSpinnerLine)) {
            boardCode = ChanBoard.LATEST_BOARD_CODE;
        } else if (getString(R.string.board_latest_images).equals(boardSpinnerLine)) {
            boardCode = ChanBoard.LATEST_IMAGES_BOARD_CODE;
        } else {
            Pattern p = Pattern.compile("/([^/]*)/.*");
            Matcher m = p.matcher(boardSpinnerLine);
            if (m.matches())
                {boardCode = m.group(1);}
            else
                {boardCode = ChanBoard.DEFAULT_BOARD_CODE;}
        }
        widgetConf.boardCode = boardCode;
        updateBoardTitleState();
        setBoardImages();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateWidgetConfWithSelectedBoard(android.widget.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateWidgetConfWithSelectedBoard(android.widget.String)",this,throwable);throw throwable;}
    }

    protected abstract void addDoneClickHandler();

    protected void initWidgetLayoutState() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.initWidgetLayoutState()",this);try{updateContainerBackgroundState();
        updateBoardTitleState();
        updateRefreshButtonState();
        updateConfigButtonState();
        setBoardImages();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.initWidgetLayoutState()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.initWidgetLayoutState()",this,throwable);throw throwable;}
    }

    protected void updateContainerBackgroundState() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateContainerBackgroundState()",this);try{int containerBackground = widgetConf.roundedCorners ? R.drawable.widget_rounded_background : 0;
        View container = findViewById(R.id.widget_preview);
        container.setBackgroundResource(containerBackground);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateContainerBackgroundState()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateContainerBackgroundState()",this,throwable);throw throwable;}
    }

    protected void updateBoardTitleState() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateBoardTitleState()",this);try{ChanBoard board = ChanBoard.getBoardByCode(this, widgetConf.boardCode);
        if (board == null)
            {board = ChanBoard.getBoardByCode(this, ChanBoard.DEFAULT_BOARD_CODE);}
        String boardTitle;
        if (WidgetConstants.WIDGET_TYPE_ONE_IMAGE.equals(widgetConf.widgetType))
            {boardTitle = "/" + board.link + "/";}
        else if (ChanBoard.isVirtualBoard(board.link))
            {boardTitle = board.getName(this);}
        else
            {boardTitle = board.getName(this) + " /" + board.link + "/";}
        int boardTitleColor = widgetConf.boardTitleColor;
        int boardTitleVisibility = widgetConf.showBoardTitle ? View.VISIBLE : View.GONE;
        TextView tv = (TextView) findViewById(R.id.board_title);
        tv.setText(boardTitle);
        tv.setTextColor(boardTitleColor);
        tv.setVisibility(boardTitleVisibility);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateBoardTitleState()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateBoardTitleState()",this,throwable);throw throwable;}
    }

    protected void updateRefreshButtonState() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateRefreshButtonState()",this);try{int refreshDrawable = widgetConf.showRefreshButton ? R.drawable.widget_refresh_button_selector : 0;
        ImageView refresh = (ImageView) findViewById(R.id.refresh_board);
        refresh.setImageResource(refreshDrawable);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateRefreshButtonState()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateRefreshButtonState()",this,throwable);throw throwable;}
    }

    protected void updateConfigButtonState() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateConfigButtonState()",this);try{int configureDrawable = widgetConf.showConfigureButton ? R.drawable.widget_configure_button_selector : 0;
        ImageView configure = (ImageView) findViewById(R.id.configure);
        configure.setImageResource(configureDrawable);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateConfigButtonState()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractWidgetConfigureActivity.updateConfigButtonState()",this,throwable);throw throwable;}
    }

    protected String[] boardThreadUrls(Context context, String boardCode, int numThreads) {
        com.mijack.Xlog.logMethodEnter("[android.widget.String com.chanapps.four.widget.AbstractWidgetConfigureActivity.boardThreadUrls(android.content.Context,android.widget.String,int)",this,context,boardCode,numThreads);try{String[] urls = new String[numThreads];
        ChanPost[] threads = WidgetProviderUtils.loadBestWidgetThreads(this, boardCode, numThreads);
        for (int i = 0; i < numThreads; i++) {
            ChanPost thread = threads[i];
            String url = ChanBoard.getBestWidgetImageUrl(context, thread, boardCode, i);
            urls[i] = url;
        }
        {com.mijack.Xlog.logMethodExit("[android.widget.String com.chanapps.four.widget.AbstractWidgetConfigureActivity.boardThreadUrls(android.content.Context,android.widget.String,int)",this);return urls;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[android.widget.String com.chanapps.four.widget.AbstractWidgetConfigureActivity.boardThreadUrls(android.content.Context,android.widget.String,int)",this,throwable);throw throwable;}
    }

}
