package com.chanapps.four.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.chanapps.four.activity.*;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.service.NetworkProfileManager;

import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class PickNewThreadBoardDialogFragment extends ListDialogFragment {

    public static final String TAG = PickNewThreadBoardDialogFragment.class.getSimpleName();

    private static final boolean DEBUG = false;

    private String[] boards;
    private Handler activityHandler;

    private void initBoards(Context context) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PickNewThreadBoardDialogFragment.initBoards(android.content.Context)",this,context);try{List<ChanBoard> chanBoards = ChanBoard.getNewThreadBoardsRespectingNSFW(context);
        boards = new String[chanBoards.size()];
        int i = 0;
        for (ChanBoard chanBoard : chanBoards) {
            String boardCode = chanBoard.link;
            String boardName = chanBoard.getName(context);
            String boardLine = "/" + boardCode + " " + boardName;
            boards[i] = boardLine;
            i++;
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PickNewThreadBoardDialogFragment.initBoards(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PickNewThreadBoardDialogFragment.initBoards(android.content.Context)",this,throwable);throw throwable;}
    }

    public PickNewThreadBoardDialogFragment(){}

    public PickNewThreadBoardDialogFragment(Handler handler) {
        activityHandler = handler;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.Dialog com.chanapps.four.fragment.PickNewThreadBoardDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{initBoards(getActivity());
        {com.mijack.Xlog.logMethodExit("android.app.Dialog com.chanapps.four.fragment.PickNewThreadBoardDialogFragment.onCreateDialog(android.os.Bundle)",this);return createListDialog(R.string.new_thread_menu, R.string.new_thread_menu,
                R.string.post_reply_new_thread_error,
                boards, new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PickNewThreadBoardDialogFragment$1.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this,parent,view,position,id);try{String boardLine = boards[position];
                        String boardCode = boardLine.substring(1, boardLine.indexOf(' '));
                        if (DEBUG) {Log.i(TAG, "Picked board=" + boardCode);}
                        ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
                        dismiss();
                        PostReplyActivity.startActivity((Activity) activity, boardCode, 0, 0, "", "");com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PickNewThreadBoardDialogFragment$1.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PickNewThreadBoardDialogFragment$1.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this,throwable);throw throwable;}
                    }
                }, new Dialog.OnCancelListener() {
                    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PickNewThreadBoardDialogFragment$2.onCancel(android.content.DialogInterface)",this,dialog);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PickNewThreadBoardDialogFragment$2.onCancel(android.content.DialogInterface)",this);}
                });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.Dialog com.chanapps.four.fragment.PickNewThreadBoardDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

}
