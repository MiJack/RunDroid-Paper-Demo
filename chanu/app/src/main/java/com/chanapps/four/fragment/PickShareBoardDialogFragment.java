package com.chanapps.four.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.chanapps.four.activity.PostReplyShareActivity;
import com.chanapps.four.activity.R;
import com.chanapps.four.data.ChanBoard;

import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class PickShareBoardDialogFragment extends ListDialogFragment {

    public static final String TAG = PickShareBoardDialogFragment.class.getSimpleName();

    private static final boolean DEBUG = false;

    private String[] boards;
    private Handler activityHandler;

    private void initBoards(Context context) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PickShareBoardDialogFragment.initBoards(android.content.Context)",this,context);try{List<ChanBoard> chanBoards = ChanBoard.getNewThreadBoardsRespectingNSFW(context);
        boards = new String[chanBoards.size()];
        int i = 0;
        for (ChanBoard chanBoard : chanBoards) {
            String boardCode = chanBoard.link;
            String boardName = chanBoard.getName(context);
            String boardLine = "/" + boardCode + " " + boardName;
            boards[i] = boardLine;
            i++;
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PickShareBoardDialogFragment.initBoards(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PickShareBoardDialogFragment.initBoards(android.content.Context)",this,throwable);throw throwable;}
    }

    public PickShareBoardDialogFragment() {
        super();
        activityHandler = null;
    }

    public PickShareBoardDialogFragment(Handler handler) {
        super();
        activityHandler = handler;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.Dialog com.chanapps.four.fragment.PickShareBoardDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{initBoards(getActivity());
        {com.mijack.Xlog.logMethodExit("android.app.Dialog com.chanapps.four.fragment.PickShareBoardDialogFragment.onCreateDialog(android.os.Bundle)",this);return createListDialog(R.string.new_thread_menu, R.string.new_thread_menu,
                R.string.post_reply_share_error,
                boards, new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PickShareBoardDialogFragment$1.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this,parent,view,position,id);try{String boardLine = boards[position];
                        String boardCode = boardLine.substring(1, boardLine.indexOf(' '));
                        if (DEBUG) {Log.i(TAG, "Picked board=" + boardCode);}
                        Bundle b = new Bundle();
                        b.putString(ChanBoard.BOARD_CODE, boardCode);
                        Message msg = Message.obtain(activityHandler, PostReplyShareActivity.PICK_BOARD);
                        msg.setData(b);
                        msg.sendToTarget();
                        dismiss();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PickShareBoardDialogFragment$1.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PickShareBoardDialogFragment$1.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this,throwable);throw throwable;}
                    }
                }, new Dialog.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PickShareBoardDialogFragment$2.onCancel(android.content.DialogInterface)",this,dialog);try{Message.obtain(activityHandler, PostReplyShareActivity.POST_CANCELLED).sendToTarget();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PickShareBoardDialogFragment$2.onCancel(android.content.DialogInterface)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PickShareBoardDialogFragment$2.onCancel(android.content.DialogInterface)",this,throwable);throw throwable;}

                    }

                });}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.Dialog com.chanapps.four.fragment.PickShareBoardDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

}
