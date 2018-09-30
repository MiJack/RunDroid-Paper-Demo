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
            if (msg.what == 1) {
                Toast.makeText(getBaseContext(), "get one message", Toast.LENGTH_SHORT).show();
            }
        }
    };
//    Handler newHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 1) {
//                Toast.makeText(MainActivity.this, "get one message", Toast.LENGTH_SHORT).show();
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                doHandleButton1();
                return;
            case R.id.button2:
                doHandleButton2();
                return;
//            case R.id.button4:
//                doHandleButton4();
//                return;
            case R.id.button3:
                doHandleButton3();
                return;

        }
    }


    public void doHandleButton1() {
        int fibonacci = doFibonacci(4);
        Toast.makeText(this, "fibonacci: " + fibonacci, Toast.LENGTH_SHORT).show();
    }

    private int doFibonacci(int i) {
        if (i < 0) {
            return -1;
        }
        if (i == 1 || i == 0) {
            return 1;
        }
        return doFibonacci(i - 1) + doFibonacci(i - 2);

    }

    public void doHandleButton2() {
        Intent intent = new Intent(this, NewActivity.class);
        startActivity(intent);
    }

    public void doHandleButton3() {
        Thread workerThread =new WorkerThread(handler);
        workerThread.start();

    }


//    public void doHandleButton4() {
//        Message message = Message.obtain(handler);
//        message.what = 1;
//        newHandler.sendMessage(message);
//    }


}
