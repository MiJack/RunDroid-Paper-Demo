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

package com.android.gallery3d.ui;

import com.android.gallery3d.app.GalleryActivity;
import com.android.gallery3d.common.Utils;

import java.util.HashMap;
import java.util.WeakHashMap;

public class PositionRepository {
    private static final WeakHashMap<GalleryActivity, PositionRepository>
            sMap = new WeakHashMap<GalleryActivity, PositionRepository>();

    public static class Position implements Cloneable {
        public float x;
        public float y;
        public float z;
        public float theta;
        public float alpha;

        public Position() {
        }

        public Position(float x, float y, float z) {
            this(x, y, z, 0f, 1f);
        }

        public Position(float x, float y, float z, float ftheta, float alpha) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.theta = ftheta;
            this.alpha = alpha;
        }

        @Override
        public Position clone() {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PositionRepository$Position com.android.gallery3d.ui.PositionRepository$Position.clone()",this);try{try {
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PositionRepository$Position com.android.gallery3d.ui.PositionRepository$Position.clone()",this);return (Position) super.clone();}
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(); /*// we do support clone.*/
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PositionRepository$Position com.android.gallery3d.ui.PositionRepository$Position.clone()",this,throwable);throw throwable;}
        }

        public void set(Position another) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PositionRepository$Position.set(com.android.gallery3d.ui.PositionRepository$Position)",this,another);try{x = another.x;
            y = another.y;
            z = another.z;
            theta = another.theta;
            alpha = another.alpha;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PositionRepository$Position.set(com.android.gallery3d.ui.PositionRepository$Position)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PositionRepository$Position.set(com.android.gallery3d.ui.PositionRepository$Position)",this,throwable);throw throwable;}
        }

        public void set(float x, float y, float z, float ftheta, float alpha) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PositionRepository$Position.set(float,float,float,float,float)",this,x,y,z,ftheta,alpha);try{this.x = x;
            this.y = y;
            this.z = z;
            this.theta = ftheta;
            this.alpha = alpha;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PositionRepository$Position.set(float,float,float,float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PositionRepository$Position.set(float,float,float,float,float)",this,throwable);throw throwable;}
        }

        @Override
        public boolean equals(Object object) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.PositionRepository$Position.equals(java.lang.Object)",this,object);try{if (!(object instanceof Position)) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PositionRepository$Position.equals(java.lang.Object)",this);return false;}}
            Position position = (Position) object;
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.PositionRepository$Position.equals(java.lang.Object)",this);return x == position.x && y == position.y && z == position.z
                    && theta == position.theta
                    && alpha == position.alpha;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.PositionRepository$Position.equals(java.lang.Object)",this,throwable);throw throwable;}
        }

        public static void interpolate(
                Position source, Position target, Position output, float progress) {
            com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.ui.PositionRepository$Position.interpolate(com.android.gallery3d.ui.PositionRepository$Position,com.android.gallery3d.ui.PositionRepository$Position,com.android.gallery3d.ui.PositionRepository$Position,float)",source,target,output,progress);try{if (progress < 1f) {
                output.set(
                        Utils.interpolateScale(source.x, target.x, progress),
                        Utils.interpolateScale(source.y, target.y, progress),
                        Utils.interpolateScale(source.z, target.z, progress),
                        Utils.interpolateAngle(source.theta, target.theta, progress),
                        Utils.interpolateScale(source.alpha, target.alpha, progress));
            } else {
                output.set(target);
            }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.ui.PositionRepository$Position.interpolate(com.android.gallery3d.ui.PositionRepository$Position,com.android.gallery3d.ui.PositionRepository$Position,com.android.gallery3d.ui.PositionRepository$Position,float)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.ui.PositionRepository$Position.interpolate(com.android.gallery3d.ui.PositionRepository$Position,com.android.gallery3d.ui.PositionRepository$Position,com.android.gallery3d.ui.PositionRepository$Position,float)",throwable);throw throwable;}
        }
    }

    public static PositionRepository getInstance(GalleryActivity activity) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.ui.PositionRepository com.android.gallery3d.ui.PositionRepository.getInstance(com.android.gallery3d.app.GalleryActivity)",activity);try{PositionRepository repository = sMap.get(activity);
        if (repository == null) {
            repository = new PositionRepository();
            sMap.put(activity, repository);
        }
        {com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.ui.PositionRepository com.android.gallery3d.ui.PositionRepository.getInstance(com.android.gallery3d.app.GalleryActivity)");return repository;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.ui.PositionRepository com.android.gallery3d.ui.PositionRepository.getInstance(com.android.gallery3d.app.GalleryActivity)",throwable);throw throwable;}
    }

    private HashMap<Long, Position> mData = new HashMap<Long, Position>();
    private int mOffsetX;
    private int mOffsetY;
    private Position mTempPosition = new Position();

    public Position get(Long identity) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.PositionRepository$Position com.android.gallery3d.ui.PositionRepository.get(java.lang.Long)",this,identity);try{Position position = mData.get(identity);
        if (position == null) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PositionRepository$Position com.android.gallery3d.ui.PositionRepository.get(java.lang.Long)",this);return null;}}
        mTempPosition.set(position);
        position = mTempPosition;
        position.x -= mOffsetX;
        position.y -= mOffsetY;
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.PositionRepository$Position com.android.gallery3d.ui.PositionRepository.get(java.lang.Long)",this);return position;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.PositionRepository$Position com.android.gallery3d.ui.PositionRepository.get(java.lang.Long)",this,throwable);throw throwable;}
    }

    public void setOffset(int offsetX, int offsetY) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PositionRepository.setOffset(int,int)",this,offsetX,offsetY);try{mOffsetX = offsetX;
        mOffsetY = offsetY;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PositionRepository.setOffset(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PositionRepository.setOffset(int,int)",this,throwable);throw throwable;}
    }

    public void putPosition(Long identity, Position position) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PositionRepository.putPosition(java.lang.Long,com.android.gallery3d.ui.PositionRepository$Position)",this,identity,position);try{Position clone = position.clone();
        clone.x += mOffsetX;
        clone.y += mOffsetY;
        mData.put(identity, clone);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PositionRepository.putPosition(java.lang.Long,com.android.gallery3d.ui.PositionRepository$Position)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PositionRepository.putPosition(java.lang.Long,com.android.gallery3d.ui.PositionRepository$Position)",this,throwable);throw throwable;}
    }

    public void clear() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.PositionRepository.clear()",this);try{mData.clear();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.PositionRepository.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.PositionRepository.clear()",this,throwable);throw throwable;}
    }
}
