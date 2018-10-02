package com.phikal.regex.models;

import android.content.Context;

import com.phikal.regex.games.Game;

import java.io.Serializable;
import java.util.List;

public abstract class Task implements Serializable {
    private final Progress p;
    private final Game g;
    private final Progress.ProgressCallback pc;
    private transient final Context ctx;

    public Task(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) {
        this.ctx = ctx;
        this.g = g;
        this.p = p;
        this.pc = pc;
    }

    public Progress.ProgressCallback getProgressCallback() {
        com.mijack.Xlog.logMethodEnter("Progress.ProgressCallback com.phikal.regex.models.Task.getProgressCallback()",this);try{com.mijack.Xlog.logMethodExit("Progress.ProgressCallback com.phikal.regex.models.Task.getProgressCallback()",this);return pc;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Progress.ProgressCallback com.phikal.regex.models.Task.getProgressCallback()",this,throwable);throw throwable;}
    }

    public Progress getProgress() {
        com.mijack.Xlog.logMethodEnter("com.phikal.regex.models.Progress com.phikal.regex.models.Task.getProgress()",this);try{com.mijack.Xlog.logMethodExit("com.phikal.regex.models.Progress com.phikal.regex.models.Task.getProgress()",this);return p;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.phikal.regex.models.Progress com.phikal.regex.models.Task.getProgress()",this,throwable);throw throwable;}
    }

    public Game getGame() {
        com.mijack.Xlog.logMethodEnter("com.phikal.regex.games.Game com.phikal.regex.models.Task.getGame()",this);try{com.mijack.Xlog.logMethodExit("com.phikal.regex.games.Game com.phikal.regex.models.Task.getGame()",this);return g;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.phikal.regex.games.Game com.phikal.regex.models.Task.getGame()",this,throwable);throw throwable;}
    }

    public Context getContext() {
        com.mijack.Xlog.logMethodEnter("android.content.Context com.phikal.regex.models.Task.getContext()",this);try{com.mijack.Xlog.logMethodExit("android.content.Context com.phikal.regex.models.Task.getContext()",this);return ctx;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.Context com.phikal.regex.models.Task.getContext()",this,throwable);throw throwable;}
    }

    public abstract List<Collumn> getCollumns();

    public abstract Input getInput();
}
