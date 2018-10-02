package com.chanapps.four.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.chanapps.four.component.ActivityDispatcher;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.fragment.PickShareBoardDialogFragment;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 2/21/13
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class PostReplyShareActivity extends PostReplyActivity implements ChanIdentifiedActivity {

    public static final boolean DEBUG = false;
    public static final int PICK_BOARD = 0x011;
    public static final int POST_CANCELLED = 0x12;

    protected Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.PostReplyShareActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        ensureHandler();
        Intent intent = getIntent();
        String type = intent.getType();
        Uri imageUri = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (Intent.ACTION_SEND.equals(intent.getAction())
                && type != null
                && type.startsWith("image/")
                && !"".equals(imageUri))
        {
            handleSendImage(imageUri);
        }
        else {
            Toast.makeText(this, R.string.post_reply_share_error, Toast.LENGTH_SHORT).show();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.PostReplyShareActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.PostReplyShareActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    protected void handleSendImage(Uri imageUri) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.PostReplyShareActivity.handleSendImage(android.net.Uri)",this,imageUri);try{this.imageUri = imageUri;
        new PickShareBoardDialogFragment(handler).show(getFragmentManager(), PickShareBoardDialogFragment.TAG);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.PostReplyShareActivity.handleSendImage(android.net.Uri)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.PostReplyShareActivity.handleSendImage(android.net.Uri)",this,throwable);throw throwable;}
    }

    @Override
    protected synchronized Handler ensureHandler() {
        com.mijack.Xlog.logMethodEnter("android.os.Handler com.chanapps.four.activity.PostReplyShareActivity.ensureHandler()",this);try{if (handler == null && ActivityDispatcher.onUIThread())
            {handler = new ShareHandler();}
        {com.mijack.Xlog.logMethodExit("android.os.Handler com.chanapps.four.activity.PostReplyShareActivity.ensureHandler()",this);return handler;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Handler com.chanapps.four.activity.PostReplyShareActivity.ensureHandler()",this,throwable);throw throwable;}
    }

    public class ShareHandler extends Handler {

        public ShareHandler() {}

        @Override
        public void handleMessage(Message msg) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.PostReplyShareActivity$ShareHandler.handleMessage(android.os.Message)",this,msg);try{try {
                super.handleMessage(msg);
                switch (msg.what) {
                    case PICK_BOARD:
                        Bundle b = msg.getData();
                        if (b == null) {
                            Toast.makeText(PostReplyShareActivity.this, R.string.post_reply_share_error, Toast.LENGTH_SHORT).show();
                            finish();
                            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.PostReplyShareActivity$ShareHandler.handleMessage(android.os.Message)",this);return;}
                        }
                        boardCode = b.getString(ChanBoard.BOARD_CODE);
                        if ("".equals(boardCode)) {
                            Toast.makeText(PostReplyShareActivity.this, R.string.post_reply_share_error, Toast.LENGTH_SHORT).show();
                            finish();
                            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.PostReplyShareActivity$ShareHandler.handleMessage(android.os.Message)",this);return;}
                        }
                        setViews();
                        break;
                    case POST_FINISHED:
                        /*// go to board to see our new post*/
                        finish();
                        if (!"".equals(boardCode))
                            {BoardActivity.startActivity(PostReplyShareActivity.this, boardCode, "");}
                    case POST_CANCELLED:
                    default:
                        finish();
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Couldn't handle message " + msg, e);
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.PostReplyShareActivity$ShareHandler.handleMessage(android.os.Message)",this,throwable);throw throwable;}
        }
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.PostReplyShareActivity.closeSearch()",this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.PostReplyShareActivity.closeSearch()",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.PostReplyShareActivity.setProgress(boolean)",this,on);com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.PostReplyShareActivity.setProgress(boolean)",this);}

    @Override
    public void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.PostReplyShareActivity.onStart()",this);try{super.onStart();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.PostReplyShareActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.PostReplyShareActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    public void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.PostReplyShareActivity.onStop()",this);try{super.onStop();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.PostReplyShareActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.PostReplyShareActivity.onStop()",this,throwable);throw throwable;}
    }

}
