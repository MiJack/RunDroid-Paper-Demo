package cn.mijack.rundroidtest;

import android.app.Activity;
import android.os.Bundle;

public class NewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void cn.mijack.rundroidtest.NewActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);com.mijack.Xlog.logMethodExit("void cn.mijack.rundroidtest.NewActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void cn.mijack.rundroidtest.NewActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }
}
