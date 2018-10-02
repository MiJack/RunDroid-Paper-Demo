package com.phikal.regex.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import static com.phikal.regex.Util.COUNT;
import static com.phikal.regex.Util.PROGRESS;

public class Progress {

    private static final float Q = 0.01f;

    private String name;
    private transient Context ctx;
    private double difficulty;
    private int rounds;

    public Progress(Context ctx, String name) {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(ctx);

        this.difficulty = pm.getFloat(name + PROGRESS, Q);
        this.rounds = pm.getInt(name + COUNT, 1);

        this.ctx = ctx;
        this.name = name;
        Log.d("progress", name + ": " + difficulty + "/" + rounds);
    }

    private Progress(String name, Context ctx, double difficulty, int rounds) {
        this.name = name;
        this.ctx = ctx;
        this.difficulty = difficulty;
        this.rounds = rounds;
    }

    public Progress next(double factor) {
        com.mijack.Xlog.logMethodEnter("com.phikal.regex.models.Progress com.phikal.regex.models.Progress.next(double)",this,factor);try{com.mijack.Xlog.logMethodExit("com.phikal.regex.models.Progress com.phikal.regex.models.Progress.next(double)",this);return new Progress(name, ctx,
                difficulty + factor * Q * Math.pow(1 / (Q + 1), rounds),
                rounds + 1);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.phikal.regex.models.Progress com.phikal.regex.models.Progress.next(double)",this,throwable);throw throwable;}
    }

    public double getDifficutly() {
        com.mijack.Xlog.logMethodEnter("double com.phikal.regex.models.Progress.getDifficutly()",this);try{com.mijack.Xlog.logMethodExit("double com.phikal.regex.models.Progress.getDifficutly()",this);return difficulty;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.phikal.regex.models.Progress.getDifficutly()",this,throwable);throw throwable;}
    }

    public int getRound() {
        com.mijack.Xlog.logMethodEnter("int com.phikal.regex.models.Progress.getRound()",this);try{com.mijack.Xlog.logMethodExit("int com.phikal.regex.models.Progress.getRound()",this);return rounds;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.phikal.regex.models.Progress.getRound()",this,throwable);throw throwable;}
    }

    public void clear() {
        com.mijack.Xlog.logMethodEnter("void com.phikal.regex.models.Progress.clear()",this);try{PreferenceManager.getDefaultSharedPreferences(ctx).edit()
                .remove(name + PROGRESS)
                .remove(name + COUNT)
                .apply();com.mijack.Xlog.logMethodExit("void com.phikal.regex.models.Progress.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.models.Progress.clear()",this,throwable);throw throwable;}
    }

    public interface ProgressCallback {
        void progress(Progress p);
    }
}
