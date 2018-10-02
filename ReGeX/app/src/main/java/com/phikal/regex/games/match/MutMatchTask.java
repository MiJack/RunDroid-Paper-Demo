package com.phikal.regex.games.match;

import android.content.Context;

import com.phikal.regex.games.Game;
import com.phikal.regex.models.Progress;

import static com.phikal.regex.Util.*;

public class MutMatchTask extends SimpleMatchTask {

    private static final double GAMMA = (1 - Math.sqrt(5)) / 2 + 1;

    private String mutateOn = null;

    MutMatchTask(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) {
        super(ctx, g, p, pc);
    }

    @Override
    String randString() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.phikal.regex.games.match.MutMatchTask.randString()",this);try{if (mutateOn == null) {
            mutateOn = super.randString();
        }
        char[] c = mutateOn.toCharArray();

        for (int i = 0; i < c.length; i++) {
            double chance = (getProgress().getDifficutly() * getProgress().getDifficutly()) * (1 - GAMMA) + GAMMA;
            if (rnd.nextDouble() > chance)
                {c[i] = CHARS[rnd.nextInt(CHARS.length)];}
        }

        {com.mijack.Xlog.logMethodExit("java.lang.String com.phikal.regex.games.match.MutMatchTask.randString()",this);return String.valueOf(c);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.phikal.regex.games.match.MutMatchTask.randString()",this,throwable);throw throwable;}
    }
}
