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
import android.media.effect.EffectFactory;

import com.android.gallery3d.photoeditor.Photo;

/**
 * Rotate filter applied to the image.
 */
public class RotateFilter extends Filter {

    private float degrees;

    public void setAngle(float degrees) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.filters.RotateFilter.setAngle(float)",this,degrees);try{this.degrees = degrees;
        validate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.filters.RotateFilter.setAngle(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.filters.RotateFilter.setAngle(float)",this,throwable);throw throwable;}
    }

    @Override
    public void process(Photo src, Photo dst) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.filters.RotateFilter.process(com.android.gallery3d.photoeditor.Photo,com.android.gallery3d.photoeditor.Photo)",this,src,dst);try{if (degrees % 180 != 0) {
            dst.changeDimension(src.height(), src.width());
        }
        Effect effect = getEffect(EffectFactory.EFFECT_ROTATE);
        effect.setParameter("angle", (int) degrees);
        effect.apply(src.texture(), src.width(), src.height(), dst.texture());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.filters.RotateFilter.process(com.android.gallery3d.photoeditor.Photo,com.android.gallery3d.photoeditor.Photo)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.filters.RotateFilter.process(com.android.gallery3d.photoeditor.Photo,com.android.gallery3d.photoeditor.Photo)",this,throwable);throw throwable;}
    }
}
