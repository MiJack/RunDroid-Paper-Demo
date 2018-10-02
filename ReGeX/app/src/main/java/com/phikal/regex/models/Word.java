package com.phikal.regex.models;

public abstract class Word {
    private Matches match;

    public Matches getMatch() {
        com.mijack.Xlog.logMethodEnter("Matches com.phikal.regex.models.Word.getMatch()",this);try{com.mijack.Xlog.logMethodExit("Matches com.phikal.regex.models.Word.getMatch()",this);return match;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Matches com.phikal.regex.models.Word.getMatch()",this,throwable);throw throwable;}
    }

    public void setMatch(Matches match) {
        com.mijack.Xlog.logMethodEnter("void com.phikal.regex.models.Word.setMatch(Matches)",this,match);try{this.match = match;com.mijack.Xlog.logMethodExit("void com.phikal.regex.models.Word.setMatch(Matches)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.models.Word.setMatch(Matches)",this,throwable);throw throwable;}
    }

    public abstract String getString();

    public enum Matches {FULL, HALF, NONE, ANTI_HALF, ANTI_FULL}
}
