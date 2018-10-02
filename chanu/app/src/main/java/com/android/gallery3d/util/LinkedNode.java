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

package com.android.gallery3d.util;


public class LinkedNode {
    public LinkedNode mPrev;
    public LinkedNode mNext;

    public LinkedNode() {
        mPrev = mNext = this;
    }

    public void insert(LinkedNode node) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.LinkedNode.insert(com.android.gallery3d.util.LinkedNode)",this,node);try{node.mNext = mNext;
        mNext.mPrev = node;
        node.mPrev = this;
        mNext = node;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.LinkedNode.insert(com.android.gallery3d.util.LinkedNode)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.LinkedNode.insert(com.android.gallery3d.util.LinkedNode)",this,throwable);throw throwable;}
    }

    public void remove() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.LinkedNode.remove()",this);try{if (mNext == this) {throw new IllegalStateException();}
        mPrev.mNext = mNext;
        mNext.mPrev = mPrev;
        mPrev = mNext = null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.LinkedNode.remove()",this,throwable);throw throwable;}
    }

    @SuppressWarnings("unchecked")
    public static class List<T extends LinkedNode> {
        private LinkedNode mHead = new LinkedNode();

        public void insertFirst(T node) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.insertFirst(T)",this,node);try{mHead.insert(node);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.insertFirst(T)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.insertFirst(T)",this,throwable);throw throwable;}
        }

        public void insertLast(T node) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.insertLast(T)",this,node);try{mHead.mPrev.insert(node);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.insertLast(T)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.insertLast(T)",this,throwable);throw throwable;}
        }

        public T getFirst() {
            com.mijack.Xlog.logMethodEnter("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.getFirst()",this);try{com.mijack.Xlog.logMethodExit("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.getFirst()",this);return (T) (mHead.mNext == mHead ? null : mHead.mNext);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.getFirst()",this,throwable);throw throwable;}
        }

        public T getLast() {
            com.mijack.Xlog.logMethodEnter("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.getLast()",this);try{com.mijack.Xlog.logMethodExit("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.getLast()",this);return (T) (mHead.mPrev == mHead ? null : mHead.mPrev);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.getLast()",this,throwable);throw throwable;}
        }

        public T nextOf(T node) {
            com.mijack.Xlog.logMethodEnter("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.nextOf(T)",this,node);try{com.mijack.Xlog.logMethodExit("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.nextOf(T)",this);return (T) (node.mNext == mHead ? null : node.mNext);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.nextOf(T)",this,throwable);throw throwable;}
        }

        public T previousOf(T node) {
            com.mijack.Xlog.logMethodEnter("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.previousOf(T)",this,node);try{com.mijack.Xlog.logMethodExit("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.previousOf(T)",this);return (T) (node.mPrev == mHead ? null : node.mPrev);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("T com.android.gallery3d.util.LinkedNode$List<T extends LinkedNode>.previousOf(T)",this,throwable);throw throwable;}
        }

    }

    public static <T extends LinkedNode> List<T> newList() {
        com.mijack.Xlog.logStaticMethodEnter("List com.android.gallery3d.util.LinkedNode.newList()");try{com.mijack.Xlog.logStaticMethodExit("List com.android.gallery3d.util.LinkedNode.newList()");return new List<T>();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("List com.android.gallery3d.util.LinkedNode.newList()",throwable);throw throwable;}
    }
}
