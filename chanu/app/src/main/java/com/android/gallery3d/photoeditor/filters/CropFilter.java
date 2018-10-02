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

import android.graphics.RectF;
import android.media.effect.Effect;
import android.media.effect.EffectFactory;

import com.android.gallery3d.photoeditor.Photo;

/**
 * Crop filter applied to the image.
 */
public class CropFilter extends Filter {

    private RectF bounds;

    /**
     * The rect coordinates used here should range from 0 to 1.
     */
    public void setCropBounds(RectF bounds) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.filters.CropFilter.setCropBounds(android.graphics.RectF)",this,bounds);try{this.bounds = bounds;
        validate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.filters.CropFilter.setCropBounds(android.graphics.RectF)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.filters.CropFilter.setCropBounds(android.graphics.RectF)",this,throwable);throw throwable;}
    }

    @Override
    public void process(Photo src, Photo dst) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.filters.CropFilter.process(com.android.gallery3d.photoeditor.Photo,com.android.gallery3d.photoeditor.Photo)",this,src,dst);try{dst.changeDimension(Math.round(bounds.width() * src.width()),
                Math.round(bounds.height() * src.height()));

        Effect effect = getEffect(EffectFactory.EFFECT_CROP);
        effect.setParameter("xorigin", Math.round(bounds.left * src.width()));
        effect.setParameter("yorigin", Math.round(bounds.top * src.height()));
        effect.setParameter("width", dst.width());
        effect.setParameter("height", dst.height());
        effect.apply(src.texture(), src.width(), src.height(), dst.texture());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.filters.CropFilter.process(com.android.gallery3d.photoeditor.Photo,com.android.gallery3d.photoeditor.Photo)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.filters.CropFilter.process(com.android.gallery3d.photoeditor.Photo,com.android.gallery3d.photoeditor.Photo)",this,throwable);throw throwable;}
    }
}
