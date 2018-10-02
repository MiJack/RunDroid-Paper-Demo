package com.phikal.regex.games.match;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;

import com.phikal.regex.R;
import com.phikal.regex.games.Game;
import com.phikal.regex.models.Collumn;
import com.phikal.regex.models.Input;
import com.phikal.regex.models.Progress;
import com.phikal.regex.models.Task;
import com.phikal.regex.models.Word;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class MatchTask extends Task implements Serializable {

    private Collection<MatchWord> allWords = new ArrayList<>();
    private MatchInput input;
    private List<Collumn> collumns;

    MatchTask(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) {
        super(ctx, g, p, pc);
        this.input = new MatchInput(getContext());
        this.collumns = Arrays.<Collumn>asList(
                new MatchCollumn(getContext(), true),
                new MatchCollumn(getContext(), false)
        );
    }

    protected abstract List<MatchWord> genWords(boolean match);

    @Override
    public List<Collumn> getCollumns() {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.phikal.regex.games.match.MatchTask.getCollumns()",this);try{com.mijack.Xlog.logMethodExit("java.util.ArrayList com.phikal.regex.games.match.MatchTask.getCollumns()",this);return collumns;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.phikal.regex.games.match.MatchTask.getCollumns()",this,throwable);throw throwable;}
    }

    @Override
    public Input getInput() {
        com.mijack.Xlog.logMethodEnter("com.phikal.regex.models.Input com.phikal.regex.games.match.MatchTask.getInput()",this);try{com.mijack.Xlog.logMethodExit("com.phikal.regex.models.Input com.phikal.regex.games.match.MatchTask.getInput()",this);return input;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.phikal.regex.models.Input com.phikal.regex.games.match.MatchTask.getInput()",this,throwable);throw throwable;}
    }

    protected class MatchWord extends Word implements Serializable {
        private final boolean match;
        private final String word;

        MatchWord(@NonNull String word,
                  boolean match) {
            this.word = word;
            this.match = match;
        }

        @Override
        public String getString() {
            com.mijack.Xlog.logMethodEnter("java.lang.String com.phikal.regex.games.match.MatchTask$MatchWord.getString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.phikal.regex.games.match.MatchTask$MatchWord.getString()",this);return word;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.phikal.regex.games.match.MatchTask$MatchWord.getString()",this,throwable);throw throwable;}
        }

    }

    protected class MatchCollumn implements Collumn, Serializable {
        private Context ctx;
        private List<MatchWord> words = null;
        private boolean match;

        MatchCollumn(Context ctx, boolean match) {
            this.ctx = ctx;
            this.match = match;
        }

        @Override
        public String getHeader() {
            com.mijack.Xlog.logMethodEnter("java.lang.String com.phikal.regex.games.match.MatchTask$MatchCollumn.getHeader()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.phikal.regex.games.match.MatchTask$MatchCollumn.getHeader()",this);return ctx.getString(match ? R.string.match : R.string.dmactch);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.phikal.regex.games.match.MatchTask$MatchCollumn.getHeader()",this,throwable);throw throwable;}
        }

        @Override
        public List<? extends Word> getWords() {
            com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.phikal.regex.games.match.MatchTask$MatchCollumn.getWords()",this);try{if (words == null) {
                words = genWords(match);
                allWords.addAll(words);
            }
            {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.phikal.regex.games.match.MatchTask$MatchCollumn.getWords()",this);return words;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.phikal.regex.games.match.MatchTask$MatchCollumn.getWords()",this,throwable);throw throwable;}
        }
    }

    protected class MatchInput extends Input implements Serializable {
        Context ctx;

        MatchInput(Context ctx) {
            this.ctx = ctx;
        }

        public void afterTextChanged(Editable pat) {

            com.mijack.Xlog.logMethodEnter("void com.phikal.regex.games.match.MatchTask$MatchInput.afterTextChanged(android.text.Editable)",this,pat);try{Input.Response res = Input.Response.OK;

            int maxLength = (int) ((0.8 * Math.pow(getProgress().getDifficutly(), 1.5) + 0.2) * 24);
            int charsLeft = maxLength - pat.length();

            pat.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(maxLength)
            });

            try {
                Pattern p = Pattern.compile(pat.toString());

                boolean allMatch = true, match;
                for (MatchWord w : allWords) {
                    match = p.matcher(w.word).matches();
                    allMatch &= match ^ !w.match;
                    w.setMatch(match ? (w.match ? Word.Matches.FULL : Word.Matches.ANTI_FULL) :
                            Word.Matches.NONE);
                }

                if (allMatch && pat.length() > 0) {
                    getProgressCallback().progress(getProgress()
                            .next(maxLength/pat.length()));
                }
            } catch (PatternSyntaxException pse) {
                for (MatchWord w : allWords)
                    {w.setMatch(Word.Matches.NONE);}
                res = Input.Response.ERROR;
            }

            updateStatus(res, String.valueOf(charsLeft));com.mijack.Xlog.logMethodExit("void com.phikal.regex.games.match.MatchTask$MatchInput.afterTextChanged(android.text.Editable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.games.match.MatchTask$MatchInput.afterTextChanged(android.text.Editable)",this,throwable);throw throwable;}
        }
    }
}
