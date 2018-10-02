package com.android.gallery3d.photoeditor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.chanapps.four.gallery3d.R;

/**
 * Alert dialog builder that builds a simple Yes/No/Cancel dialog.
 */
public class YesNoCancelDialogBuilder extends AlertDialog.Builder {

    public YesNoCancelDialogBuilder(Context context, final Runnable yes, final Runnable no,
            int messageId) {
        super(context);
        setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.YesNoCancelDialogBuilder$1.onClick(android.content.DialogInterface,int)",this,dialog,which);try{yes.run();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.YesNoCancelDialogBuilder$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.YesNoCancelDialogBuilder$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
            }
        })
        .setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.YesNoCancelDialogBuilder$2.onClick(android.content.DialogInterface,int)",this,dialog,which);try{no.run();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.YesNoCancelDialogBuilder$2.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.YesNoCancelDialogBuilder$2.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.YesNoCancelDialogBuilder$3.onClick(android.content.DialogInterface,int)",this,dialog,which);try{/*// no-op*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.YesNoCancelDialogBuilder$3.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.YesNoCancelDialogBuilder$3.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
            }
        }).setMessage(messageId);
    }
}
