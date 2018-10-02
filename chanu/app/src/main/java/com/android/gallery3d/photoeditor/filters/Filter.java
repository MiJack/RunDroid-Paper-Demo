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

package com.android.gallery3d.photoeditor.filters;

import android.media.effect.Effect;
import android.media.effect.EffectContext;

import com.android.gallery3d.photoeditor.Photo;

import java.util.HashMap;

/**
 * Image filter for photo editing; most of its methods must be called from a single GL thread except
 * validate()/isValid() that are called from UI thread.
 */
public abstract class Filter {

    /*// TODO: This should be set in MFF instead.*/
    private static final int DEFAULT_TILE_SIZE = 640;

    private static final HashMap<Filter, Effect> effects = new HashMap<Filter, Effect>();
    private static EffectContext context;

    private boolean isValid;

    /**
     * Filter context should be released before the current GL context is lost.
     */
    public static void releaseContext() {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.photoeditor.filters.Filter.releaseContext()");try{if (context != null) {
            /*// Release all effects created with the releasing context.*/
            for (Effect effect : effects.values()) {
                effect.release();
            }
            effects.clear();
            context.release();
            context = null;
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.photoeditor.filters.Filter.releaseContext()");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.photoeditor.filters.Filter.releaseContext()",throwable);throw throwable;}
    }

    public void release() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.filters.Filter.release()",this);try{Effect effect = effects.remove(this);
        if (effect != null) {
            effect.release();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.filters.Filter.release()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.filters.Filter.release()",this,throwable);throw throwable;}
    }

    protected Effect getEffect(String name) {
        com.mijack.Xlog.logMethodEnter("android.media.effect.Effect com.android.gallery3d.photoeditor.filters.Filter.getEffect(java.lang.String)",this,name);try{Effect effect = effects.get(this);
        if (effect == null) {
            if (context == null) {
                context = EffectContext.createWithCurrentGlContext();
            }
            effect = context.getFactory().createEffect(name);
            effect.setParameter("tile_size", DEFAULT_TILE_SIZE);
            effects.put(this, effect);
        }
        {com.mijack.Xlog.logMethodExit("android.media.effect.Effect com.android.gallery3d.photoeditor.filters.Filter.getEffect(java.lang.String)",this);return effect;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.media.effect.Effect com.android.gallery3d.photoeditor.filters.Filter.getEffect(java.lang.String)",this,throwable);throw throwable;}
    }

    protected void validate() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.filters.Filter.validate()",this);try{isValid = true;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.filters.Filter.validate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.filters.Filter.validate()",this,throwable);throw throwable;}
    }

    /**
     * Some filters, e.g. lighting filters, are initially invalid until set up with parameters while
     * others, e.g. Sepia or Posterize filters, are initially valid without parameters.
     */
    public boolean isValid() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.filters.Filter.isValid()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.filters.Filter.isValid()",this);return isValid;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.filters.Filter.isValid()",this,throwable);throw throwable;}
    }

    /**
     * Processes the source bitmap and matrix and output the destination bitmap and matrix.
     *
     * @param src source photo as the input.
     * @param dst destination photo having the same dimension as source photo as the output.
     */
    public abstract void process(Photo src, Photo dst);
}
