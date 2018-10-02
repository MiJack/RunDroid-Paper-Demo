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

import android.graphics.PointF;
import android.media.effect.Effect;
import android.media.effect.EffectFactory;

import com.android.gallery3d.photoeditor.Photo;

import java.util.Vector;

/**
 * Red-eye removal filter applied to the image.
 */
public class RedEyeFilter extends Filter {

    private final Vector<PointF> redeyes = new Vector<PointF>();

    /**
     * The point coordinates used here should range from 0 to 1.
     */
    public void addRedEyePosition(PointF point) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.filters.RedEyeFilter.addRedEyePosition(android.graphics.PointF)",this,point);try{redeyes.add(point);
        validate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.filters.RedEyeFilter.addRedEyePosition(android.graphics.PointF)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.filters.RedEyeFilter.addRedEyePosition(android.graphics.PointF)",this,throwable);throw throwable;}
    }

    @Override
    public void process(Photo src, Photo dst) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.filters.RedEyeFilter.process(com.android.gallery3d.photoeditor.Photo,com.android.gallery3d.photoeditor.Photo)",this,src,dst);try{Effect effect = getEffect(EffectFactory.EFFECT_REDEYE);
        float[] centers = new float[redeyes.size() * 2];
        int i = 0;
        for (PointF eye : redeyes) {
            centers[i++] = eye.x;
            centers[i++] = eye.y;
        }
        effect.setParameter("centers", centers);
        effect.apply(src.texture(), src.width(), src.height(), dst.texture());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.filters.RedEyeFilter.process(com.android.gallery3d.photoeditor.Photo,com.android.gallery3d.photoeditor.Photo)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.filters.RedEyeFilter.process(com.android.gallery3d.photoeditor.Photo,com.android.gallery3d.photoeditor.Photo)",this,throwable);throw throwable;}
    }
}
