package cn.mijack.rundroidtest;

import android.os.Handler;
import android.os.Message;

/**
 * @author Mi&Jack
 * @since 2018/9/19
 */
public class WorkerThread extends Thread {
    Handler handler;

    public WorkerThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        Message message = Message.obtain();
        message.what = 1;
        message.obj = "some text";
        handler.sendMessage(message);
    }
}
