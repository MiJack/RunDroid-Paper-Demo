package com.chanapps.four.component;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.gallery3d.ui.Log;
import com.chanapps.four.activity.R;
import com.chanapps.four.data.UserStatistics;
import com.chanapps.four.service.NetworkProfileManager;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 5/6/13
 * Time: 10:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class TutorialOverlay {

    protected static final String TAG = TutorialOverlay.class.getSimpleName();
    protected static final boolean DEBUG = false;
    protected static final boolean TEST_MODE = false;

    protected static final String SUBJECT_FONT = "fonts/Edmondsans-Regular.otf";
    private static Typeface subjectTypeface = null;

    public enum Page {
        /*//BOARDLIST,*/
        /*//POPULAR,*/
        /*//WATCHLIST,*/
        BOARD
        /*//,THREAD*/
        ;
    }

    protected UserStatistics.ChanFeature feature;
    protected Page page;
    protected View layout;
    protected ViewGroup tutorialOverlay;

    public TutorialOverlay(View layout, Page page) {
        this.layout =  layout;
        this.page = page;
        if (layout == null)
            {return;}
        tutorialOverlay = (ViewGroup)layout.findViewById(R.id.tutorial_overlay);
        if (tutorialOverlay == null)
            {return;}
        if (!TEST_MODE && !displayNextTipForPage(page)) {
            tutorialOverlay.setVisibility(View.GONE);
            return;
        }

        setSubjectTypeface();
        addButtonHandlers();
        tutorialOverlay.setVisibility(View.VISIBLE);
    }

    protected boolean displayNextTipForPage(Page page) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.component.TutorialOverlay.displayNextTipForPage(Page)",this,page);try{NetworkProfileManager manager = NetworkProfileManager.instance();
        if (manager == null) {
            if (DEBUG) {Log.i(TAG, "no network manager found");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.TutorialOverlay.displayNextTipForPage(Page)",this);return false;}
        }
        UserStatistics stats = manager.getUserStatistics();
        if (stats == null) {
            if (DEBUG) {Log.i(TAG, "no user statistics found");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.TutorialOverlay.displayNextTipForPage(Page)",this);return false;}
        }
        feature = stats.nextTipForPage(page);
        if (feature == null) {
            if (DEBUG) {Log.i(TAG, "no tutorial feature found");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.TutorialOverlay.displayNextTipForPage(Page)",this);return false;}
        }
        if (feature == UserStatistics.ChanFeature.NONE) {
            if (DEBUG) {Log.i(TAG, "NONE tutorial feature found");}
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.TutorialOverlay.displayNextTipForPage(Page)",this);return false;}
        }
        if (DEBUG) {Log.i(TAG, "found feature=" + feature);}
        NetworkProfileManager.instance().getUserStatistics().tipDisplayed(feature);
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.TutorialOverlay.displayNextTipForPage(Page)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.component.TutorialOverlay.displayNextTipForPage(Page)",this,throwable);throw throwable;}
    }

    protected void setSubjectTypeface() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.TutorialOverlay.setSubjectTypeface()",this);try{subjectTypeface = Typeface.createFromAsset(layout.getResources().getAssets(), SUBJECT_FONT);
        TextView subject = (TextView)tutorialOverlay.findViewById(R.id.tutorial_overlay_subject);
        if (subject != null && subjectTypeface != null)
            {subject.setTypeface(subjectTypeface);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.TutorialOverlay.setSubjectTypeface()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.TutorialOverlay.setSubjectTypeface()",this,throwable);throw throwable;}
    }

    protected void addButtonHandlers() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.TutorialOverlay.addButtonHandlers()",this);try{if (tutorialOverlay == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.TutorialOverlay.addButtonHandlers()",this);return;}}
        View tutorialOverlayDismiss = tutorialOverlay.findViewById(R.id.tutorial_overlay_dismiss);
        if (tutorialOverlayDismiss == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.TutorialOverlay.addButtonHandlers()",this);return;}}
        tutorialOverlayDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.TutorialOverlay$1.onClick(android.view.View)",this,v);try{tutorialOverlay.setVisibility(View.GONE);
                NetworkProfileManager.instance().getUserStatistics().tipDisplayed(feature);
                NetworkProfileManager.instance().getUserStatistics().disableTips();com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.TutorialOverlay$1.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.TutorialOverlay$1.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.TutorialOverlay.addButtonHandlers()",this,throwable);throw throwable;}
    }

}
