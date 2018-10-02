package com.phikal.regex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.phikal.regex.R;
import com.phikal.regex.adapters.WordAdapter;
import com.phikal.regex.games.Game;
import com.phikal.regex.models.Collumn;
import com.phikal.regex.models.Input;
import com.phikal.regex.models.Progress;
import com.phikal.regex.models.Task;

import java.util.ArrayList;
import java.util.List;

import static com.phikal.regex.Util.CHARS;
import static com.phikal.regex.Util.CHAR_BAR_ON;
import static com.phikal.regex.Util.COUNT;
import static com.phikal.regex.Util.CURRENT_INPUT;
import static com.phikal.regex.Util.CURRENT_TASK;
import static com.phikal.regex.Util.MODE;
import static com.phikal.regex.Util.PROGRESS;
import static com.phikal.regex.Util.VERSION;
import static com.phikal.regex.Util.notif;

public class GameActivity extends Activity {

    static boolean reload = false;

    SharedPreferences prefs;
    private Game game;
    private Task task;

    LinearLayout colums;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
  com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.GameActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{      super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        /*// find or generate task*/
        game = Game.valueOf(prefs.getString(MODE, Game.DEFAULT_GAME.name()));

        try {
            if (savedInstanceState != null)
                {task = (Task) savedInstanceState.getSerializable(CURRENT_TASK);}
            if (task == null)
                {task = game.nextTask(getApplicationContext(), new Progress.ProgressCallback() {
                    @Override
                    public void progress(Progress p) {
                        com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.GameActivity$1.progress(com.phikal.regex.models.Progress)",this,p);try{prefs.edit()
                                .putFloat(game.name() + PROGRESS, (float) p.getDifficutly())
                                .putInt(game.name() + COUNT, p.getRound())
                                .apply();
                        input.getEditableText().clear();
                        task = null;com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.GameActivity$1.progress(com.phikal.regex.models.Progress)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.GameActivity$1.progress(com.phikal.regex.models.Progress)",this,throwable);throw throwable;}

                    }
                });}
        } catch (ClassCastException cce) {
            new AlertDialog.Builder(getApplicationContext())
                    .setMessage(cce.getMessage())
                    .create();
            {com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.GameActivity.onCreate(android.os.Bundle)",this);return;}
        }

        assert task != null;
        assert task.getCollumns() != null;
        assert task.getInput() != null;

        /*// find and setup views*/
        colums = (LinearLayout) findViewById(R.id.columns);
        RelativeLayout input_box = (RelativeLayout) findViewById(R.id.input_box);
        final Button status = (Button) input_box.findViewById(R.id.status);
        input = (EditText) input_box.findViewById(R.id.input);
        ImageButton settings = (ImageButton) input_box.findViewById(R.id.settings);
        LinearLayout charmb = (LinearLayout) findViewById(R.id.chars);

        colums.setWeightSum(task.getCollumns().size());
        LayoutInflater inf = LayoutInflater.from(getApplicationContext());
        final List<WordAdapter> adapters = new ArrayList<>(task.getCollumns().size());
        for (Collumn c : task.getCollumns()) {
            View v = inf.inflate(R.layout.column_layout, colums, false);

            TextView colName = (TextView) v.findViewById(R.id.col_name);
            ListView colList = (ListView) v.findViewById(R.id.col_list);

            colName.setText(c.getHeader());

            WordAdapter wa = new WordAdapter(getApplicationContext(), c);
            colList.setAdapter(wa);
            adapters.add(wa);

            colums.addView(v);
        }

        input.setHint(getString(game.name));
        input.addTextChangedListener(task.getInput());

        task.getInput().setStatusCallback(new Input.StatusCallback() {
            @Override
            public void status(Input.Response resp, String msg) {
                com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.GameActivity$2.status(Input.Response,java.lang.String)",this,resp,msg);try{int res;
                switch (resp) {
                    case ERROR:
                        res = R.color.orange;
                        break;
                    default:
                    case OK:
                        res = R.color.text;
                }
                status.setTextColor(ContextCompat.getColor(GameActivity.this, res));
                status.setText(msg);
                for (WordAdapter wa : adapters)
                    {wa.notifyDataSetChanged();}com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.GameActivity$2.status(Input.Response,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.GameActivity$2.status(Input.Response,java.lang.String)",this,throwable);throw throwable;}
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View $) {
                com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.GameActivity$3.onClick(android.view.View)",this,$);try{Intent i = new Intent(GameActivity.this, SettingsActivity.class);
                GameActivity.this.startActivity(i);com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.GameActivity$3.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.GameActivity$3.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });
        settings.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View $) {
                com.mijack.Xlog.logMethodEnter("boolean com.phikal.regex.activities.GameActivity$4.onLongClick(android.view.View)",this,$);try{task = null;
                notif(GameActivity.this);
                GameActivity.this.recreate();
                {com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.GameActivity.onCreate(android.os.Bundle)",this);{com.mijack.Xlog.logMethodExit("boolean com.phikal.regex.activities.GameActivity$4.onLongClick(android.view.View)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.phikal.regex.activities.GameActivity$4.onLongClick(android.view.View)",this,throwable);throw throwable;}
            }
        });

        for (final String c : CHARS) {
            TextView v = new TextView(getApplicationContext());

            v.setText(c);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View $) {
                    com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.GameActivity$5.onClick(android.view.View)",this,$);try{input.append(c);com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.GameActivity$5.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.GameActivity$5.onClick(android.view.View)",this,throwable);throw throwable;}
                }
            });
            v.setHeight(getResources().getDimensionPixelSize(R.dimen.std));
            v.setWidth(getResources().getDimensionPixelSize(R.dimen.std));
            v.setGravity(Gravity.CENTER);

            charmb.addView(v);
        }

        input.setText(savedInstanceState == null ? "" :
                savedInstanceState.getString(CURRENT_INPUT, ""));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.GameActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.GameActivity.onStart()",this);try{super.onStart();
        try {
            String vers = prefs.getString(VERSION, null);
            String cvers = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (vers == null)
                {startActivity(new Intent(getApplicationContext(), HelloActivity.class));}
            prefs.edit().putString(VERSION, cvers).apply();
        } catch (PackageManager.NameNotFoundException nnfe) {
            /*// ignore error*/
        }com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.GameActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.GameActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.GameActivity.onResume()",this);try{super.onResume();
        if (reload) {
            reload = false;
            task = null;
            recreate();
        }

        boolean show = prefs.getBoolean(CHAR_BAR_ON, true);
        if (!show)
            {findViewById(R.id.chars).setVisibility(View.GONE);}

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0,
                (int) getResources().getDimension(
                        show ? R.dimen.dstd : R.dimen.std));
        colums.setLayoutParams(params);

        input.requestFocus();com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.GameActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.GameActivity.onResume()",this,throwable);throw throwable;}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.GameActivity.onSaveInstanceState(android.os.Bundle)",this,outState);try{super.onSaveInstanceState(outState);
        outState.putSerializable(CURRENT_TASK, task);
        if (input != null)
            {outState.putString(CURRENT_INPUT, input.getText().toString());}
        if (task != null)
            {outState.putSerializable(CURRENT_TASK, task);}com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.GameActivity.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.GameActivity.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }
}