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

package com.android.gallery3d.photoeditor.actions;

import android.content.Context;
import android.util.AttributeSet;

import com.android.gallery3d.photoeditor.filters.SepiaFilter;

/**
 * An action handling sepia effect.
 */
public class SepiaAction  extends EffectAction {

    public SepiaAction(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void doBegin() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.SepiaAction.doBegin()",this);try{notifyFilterChanged(new SepiaFilter(), true);
        notifyDone();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.SepiaAction.doBegin()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.SepiaAction.doBegin()",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.SepiaAction.doEnd()",this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.SepiaAction.doEnd()",this);}
}
