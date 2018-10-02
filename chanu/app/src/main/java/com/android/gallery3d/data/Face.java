/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.gallery3d.data;

import com.android.gallery3d.common.Utils;

public class Face implements Comparable<Face> {
    private String mName;
    private String mPersonId;

    public Face(String name, String personId) {
        mName = name;
        mPersonId = personId;
        Utils.assertTrue(mName != null && mPersonId != null);
    }

    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.Face.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.Face.getName()",this);return mName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.Face.getName()",this,throwable);throw throwable;}
    }

    public String getPersonId() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.Face.getPersonId()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.Face.getPersonId()",this);return mPersonId;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.Face.getPersonId()",this,throwable);throw throwable;}
    }

    @Override
    public boolean equals(Object obj) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.Face.equals(java.lang.Object)",this,obj);try{if (obj instanceof Face) {
            Face face = (Face) obj;
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.Face.equals(java.lang.Object)",this);return mPersonId.equals(face.mPersonId);}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.Face.equals(java.lang.Object)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.Face.equals(java.lang.Object)",this,throwable);throw throwable;}
    }

    @Override
    public int hashCode() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.Face.hashCode()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.Face.hashCode()",this);return mPersonId.hashCode();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.Face.hashCode()",this,throwable);throw throwable;}
    }

    public int compareTo(Face another) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.Face.compareTo(com.android.gallery3d.data.Face)",this,another);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.Face.compareTo(com.android.gallery3d.data.Face)",this);return mPersonId.compareTo(another.mPersonId);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.Face.compareTo(com.android.gallery3d.data.Face)",this,throwable);throw throwable;}
    }
}
