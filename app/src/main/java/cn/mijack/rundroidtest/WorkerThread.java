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
        com.mijack.Xlog.logMethodEnter("void cn.mijack.rundroidtest.WorkerThread.run()",this);try{Message message = Message.obtain();
        message.what = 1;
        message.obj = "some text";
        handler.sendMessage(message);com.mijack.Xlog.logMethodExit("void cn.mijack.rundroidtest.WorkerThread.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void cn.mijack.rundroidtest.WorkerThread.run()",this,throwable);throw throwable;}
    }
}
