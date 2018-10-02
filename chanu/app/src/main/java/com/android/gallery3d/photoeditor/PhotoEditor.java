/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.photoeditor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;

import com.chanapps.four.gallery3d.R;

/**
 * Main activity of the photo editor that opens a photo and prepares tools for photo editing.
 */
public class PhotoEditor extends Activity {

    private Uri sourceUri;
    private Uri saveUri;
    private FilterStack filterStack;
    private ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setContentView(R.layout.photoeditor_main);

        Intent intent = getIntent();
        if (Intent.ACTION_EDIT.equalsIgnoreCase(intent.getAction())) {
            sourceUri = intent.getData();
        }

        actionBar = (ActionBar) findViewById(R.id.action_bar);
        filterStack = new FilterStack((PhotoView) findViewById(R.id.photo_view),
                new FilterStack.StackListener() {

                    @Override
                    public void onStackChanged(boolean canUndo, boolean canRedo) {
                        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$1.onStackChanged(boolean,boolean)",this,canUndo,canRedo);try{actionBar.updateButtons(canUndo, canRedo);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$1.onStackChanged(boolean,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$1.onStackChanged(boolean,boolean)",this,throwable);throw throwable;}
                    }
        });

        EffectsBar effectsBar = (EffectsBar) findViewById(R.id.effects_bar);
        effectsBar.initialize(filterStack);

        actionBar.setClickRunnable(R.id.undo_button, createUndoRedoRunnable(true, effectsBar));
        actionBar.setClickRunnable(R.id.redo_button, createUndoRedoRunnable(false, effectsBar));
        actionBar.setClickRunnable(R.id.save_button, createSaveRunnable(effectsBar));
        actionBar.setClickRunnable(R.id.share_button, createShareRunnable(effectsBar));
        actionBar.setClickRunnable(R.id.action_bar_back, createBackRunnable(effectsBar));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private SpinnerProgressDialog createProgressDialog() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.photoeditor.SpinnerProgressDialog com.android.gallery3d.photoeditor.PhotoEditor.createProgressDialog()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.photoeditor.SpinnerProgressDialog com.android.gallery3d.photoeditor.PhotoEditor.createProgressDialog()",this);return SpinnerProgressDialog.show((ViewGroup) findViewById(R.id.toolbar));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.photoeditor.SpinnerProgressDialog com.android.gallery3d.photoeditor.PhotoEditor.createProgressDialog()",this,throwable);throw throwable;}
    }

    private void openPhoto() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor.openPhoto()",this);try{final SpinnerProgressDialog progressDialog = createProgressDialog();
        LoadScreennailTask.Callback callback = new LoadScreennailTask.Callback() {

            @Override
            public void onComplete(final Bitmap result) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$2.onComplete(android.graphics.Bitmap)",this,result);try{filterStack.setPhotoSource(result, new OnDoneCallback() {

                    @Override
                    public void onDone() {
                        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$2$1.onDone()",this);try{progressDialog.dismiss();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$2$1.onDone()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$2$1.onDone()",this,throwable);throw throwable;}
                    }
                });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$2.onComplete(android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$2.onComplete(android.graphics.Bitmap)",this,throwable);throw throwable;}
            }
        };
        new LoadScreennailTask(this, callback).execute(sourceUri);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor.openPhoto()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor.openPhoto()",this,throwable);throw throwable;}
    }

    private Runnable createUndoRedoRunnable(final boolean undo, final EffectsBar effectsBar) {
        com.mijack.Xlog.logMethodEnter("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createUndoRedoRunnable(boolean,com.android.gallery3d.photoeditor.EffectsBar)",this,undo,effectsBar);try{com.mijack.Xlog.logMethodExit("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createUndoRedoRunnable(boolean,com.android.gallery3d.photoeditor.EffectsBar)",this);return new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$3.run()",this);try{effectsBar.exit(new Runnable() {

                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$3$1.run()",this);try{final SpinnerProgressDialog progressDialog = createProgressDialog();
                        OnDoneCallback callback = new OnDoneCallback() {

                            @Override
                            public void onDone() {
                                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$3$1$1.onDone()",this);try{progressDialog.dismiss();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$3$1$1.onDone()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$3$1$1.onDone()",this,throwable);throw throwable;}
                            }
                        };
                        if (undo) {
                            filterStack.undo(callback);
                        } else {
                            filterStack.redo(callback);
                        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$3$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$3$1.run()",this,throwable);throw throwable;}
                    }
                });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$3.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$3.run()",this,throwable);throw throwable;}
            }
        };}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createUndoRedoRunnable(boolean,com.android.gallery3d.photoeditor.EffectsBar)",this,throwable);throw throwable;}
    }

    private Runnable createSaveRunnable(final EffectsBar effectsBar) {
        com.mijack.Xlog.logMethodEnter("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createSaveRunnable(com.android.gallery3d.photoeditor.EffectsBar)",this,effectsBar);try{com.mijack.Xlog.logMethodExit("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createSaveRunnable(com.android.gallery3d.photoeditor.EffectsBar)",this);return new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$4.run()",this);try{effectsBar.exit(new Runnable() {

                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$4$1.run()",this);try{final SpinnerProgressDialog progressDialog = createProgressDialog();
                        filterStack.saveBitmap(new OnDoneBitmapCallback() {

                            @Override
                            public void onDone(Bitmap bitmap) {
                                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$4$1$1.onDone(android.graphics.Bitmap)",this,bitmap);try{SaveCopyTask.Callback callback = new SaveCopyTask.Callback() {

                                    @Override
                                    public void onComplete(Uri result) {
                                        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$4$1$1$1.onComplete(android.net.Uri)",this,result);try{progressDialog.dismiss();
                                        actionBar.updateSave(result == null);
                                        saveUri = result;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$4$1$1$1.onComplete(android.net.Uri)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$4$1$1$1.onComplete(android.net.Uri)",this,throwable);throw throwable;}
                                    }
                                };
                                new SaveCopyTask(PhotoEditor.this, sourceUri, callback).execute(
                                        bitmap);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$4$1$1.onDone(android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$4$1$1.onDone(android.graphics.Bitmap)",this,throwable);throw throwable;}
                            }
                        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$4$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$4$1.run()",this,throwable);throw throwable;}
                    }
                });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$4.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$4.run()",this,throwable);throw throwable;}
            }
        };}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createSaveRunnable(com.android.gallery3d.photoeditor.EffectsBar)",this,throwable);throw throwable;}
    }

    private Runnable createShareRunnable(final EffectsBar effectsBar) {
        com.mijack.Xlog.logMethodEnter("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createShareRunnable(com.android.gallery3d.photoeditor.EffectsBar)",this,effectsBar);try{com.mijack.Xlog.logMethodExit("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createShareRunnable(com.android.gallery3d.photoeditor.EffectsBar)",this);return new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$5.run()",this);try{effectsBar.exit(new Runnable() {

                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$5$1.run()",this);try{if (saveUri != null) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_STREAM, saveUri);
                            intent.setType("image/*");
                            startActivity(intent);
                        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$5$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$5$1.run()",this,throwable);throw throwable;}
                    }
                });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$5.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$5.run()",this,throwable);throw throwable;}
            }
        };}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createShareRunnable(com.android.gallery3d.photoeditor.EffectsBar)",this,throwable);throw throwable;}
    }

    private Runnable createBackRunnable(final EffectsBar effectsBar) {
        com.mijack.Xlog.logMethodEnter("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createBackRunnable(com.android.gallery3d.photoeditor.EffectsBar)",this,effectsBar);try{com.mijack.Xlog.logMethodExit("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createBackRunnable(com.android.gallery3d.photoeditor.EffectsBar)",this);return new Runnable() {

            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$6.run()",this);try{/*// Exit effects or go back to the previous activity on pressing back button.*/
                if (!effectsBar.exit(null)) {
                    /*// Pop-up a dialog to save unsaved photo.*/
                    if (actionBar.canSave()) {
                        new YesNoCancelDialogBuilder(PhotoEditor.this, new Runnable() {

                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$6$1.run()",this);try{actionBar.clickSave();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$6$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$6$1.run()",this,throwable);throw throwable;}
                            }
                        }, new Runnable() {

                            @Override
                            public void run() {
                                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor$6$2.run()",this);try{finish();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$6$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$6$2.run()",this,throwable);throw throwable;}
                            }
                        }, R.string.save_photo).show();
                    } else {
                        finish();
                    }
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor$6.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor$6.run()",this,throwable);throw throwable;}
            }
        };}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Runnable com.android.gallery3d.photoeditor.PhotoEditor.createBackRunnable(com.android.gallery3d.photoeditor.EffectsBar)",this,throwable);throw throwable;}
    }

    @Override
    public void onBackPressed() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor.onBackPressed()",this);try{actionBar.clickBack();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor.onBackPressed()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor.onBackPressed()",this,throwable);throw throwable;}
    }

    @Override
    protected void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor.onPause()",this);try{/*// TODO: Close running progress dialogs as all pending operations will be paused.*/
        super.onPause();
        filterStack.onPause();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor.onPause()",this,throwable);throw throwable;}
    }

    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.PhotoEditor.onResume()",this);try{super.onResume();
        filterStack.onResume();
        openPhoto();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.PhotoEditor.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.PhotoEditor.onResume()",this,throwable);throw throwable;}
    }
}
