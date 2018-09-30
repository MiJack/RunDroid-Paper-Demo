package cn.mijack.rundroidtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    Button button1;
    Button button2;
    Button button4;
    Button button3;
    TextView textView;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            com.mijack.Xlog.logMethodEnter("void cn.mijack.rundroidtest.MainActivity$1.handleMessage(android.os.Message)",this,msg);try{if (msg.what == 1) {
                Toast.makeText(getBaseContext(), "get one message", Toast.LENGTH_SHORT).show();
            }com.mijack.Xlog.logMethodExit("void cn.mijack.rundroidtest.MainActivity$1.handleMessage(android.os.Message)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void cn.mijack.rundroidtest.MainActivity$1.handleMessage(android.os.Message)",this,throwable);throw throwable;}
        }
    };
/*//    Handler newHandler = new Handler() {*/
/*//        @Override*/
/*//        public void handleMessage(Message msg) {*/
/*//            if (msg.what == 1) {*/
/*//                Toast.makeText(MainActivity.this, "get one message", Toast.LENGTH_SHORT).show();*/
/*//            }*/
/*//        }*/
/*//    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void cn.mijack.rundroidtest.MainActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);com.mijack.Xlog.logMethodExit("void cn.mijack.rundroidtest.MainActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void cn.mijack.rundroidtest.MainActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}


    }

    @Override
    public void onClick(View view) {
        com.mijack.Xlog.logMethodEnter("void cn.mijack.rundroidtest.MainActivity.onClick(android.view.View)",this,view);try{switch (view.getId()) {
            case R.id.button1:
                doHandleButton1();
                {com.mijack.Xlog.logMethodExit("void cn.mijack.rundroidtest.MainActivity.onClick(android.view.View)",this);return;}
            case R.id.button2:
                doHandleButton2();
                {com.mijack.Xlog.logMethodExit("void cn.mijack.rundroidtest.MainActivity.onClick(android.view.View)",this);return;}
/*//            case R.id.button4:*/
/*//                doHandleButton4();*/
/*//                return;*/
            case R.id.button3:
                doHandleButton3();
                {com.mijack.Xlog.logMethodExit("void cn.mijack.rundroidtest.MainActivity.onClick(android.view.View)",this);return;}

        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void cn.mijack.rundroidtest.MainActivity.onClick(android.view.View)",this,throwable);throw throwable;}
    }


    public void doHandleButton1() {
        com.mijack.Xlog.logMethodEnter("void cn.mijack.rundroidtest.MainActivity.doHandleButton1()",this);try{int fibonacci = doFibonacci(4);
        Toast.makeText(this, "fibonacci: " + fibonacci, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void cn.mijack.rundroidtest.MainActivity.doHandleButton1()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void cn.mijack.rundroidtest.MainActivity.doHandleButton1()",this,throwable);throw throwable;}
    }

    private int doFibonacci(int i) {
        com.mijack.Xlog.logMethodEnter("int cn.mijack.rundroidtest.MainActivity.doFibonacci(int)",this,i);try{if (i < 0) {
            {com.mijack.Xlog.logMethodExit("int cn.mijack.rundroidtest.MainActivity.doFibonacci(int)",this);return -1;}
        }
        if (i == 1 || i == 0) {
            {com.mijack.Xlog.logMethodExit("int cn.mijack.rundroidtest.MainActivity.doFibonacci(int)",this);return 1;}
        }
        {com.mijack.Xlog.logMethodExit("int cn.mijack.rundroidtest.MainActivity.doFibonacci(int)",this);return doFibonacci(i - 1) + doFibonacci(i - 2);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int cn.mijack.rundroidtest.MainActivity.doFibonacci(int)",this,throwable);throw throwable;}

    }

    public void doHandleButton2() {
        com.mijack.Xlog.logMethodEnter("void cn.mijack.rundroidtest.MainActivity.doHandleButton2()",this);try{Intent intent = new Intent(this, NewActivity.class);
        startActivity(intent);com.mijack.Xlog.logMethodExit("void cn.mijack.rundroidtest.MainActivity.doHandleButton2()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void cn.mijack.rundroidtest.MainActivity.doHandleButton2()",this,throwable);throw throwable;}
    }

    public void doHandleButton3() {
        com.mijack.Xlog.logMethodEnter("void cn.mijack.rundroidtest.MainActivity.doHandleButton3()",this);try{Thread workerThread =new WorkerThread(handler);
        workerThread.start();com.mijack.Xlog.logMethodExit("void cn.mijack.rundroidtest.MainActivity.doHandleButton3()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void cn.mijack.rundroidtest.MainActivity.doHandleButton3()",this,throwable);throw throwable;}

    }


/*//    public void doHandleButton4() {*/
/*//        Message message = Message.obtain(handler);*/
/*//        message.what = 1;*/
/*//        newHandler.sendMessage(message);*/
/*//    }*/


}
