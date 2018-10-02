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


import android.os.Process;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread factory that creates threads with a given thread priority.
 */
public class PriorityThreadFactory implements ThreadFactory {

    private final int mPriority;
    private final AtomicInteger mNumber = new AtomicInteger();
    private final String mName;

    public PriorityThreadFactory(String name, int priority) {
        mName = name;
        mPriority = priority;
    }

    public Thread newThread(Runnable r) {
        com.mijack.Xlog.logMethodEnter("java.lang.Thread com.android.gallery3d.util.PriorityThreadFactory.newThread(java.lang.Runnable)",this,r);try{com.mijack.Xlog.logMethodExit("java.lang.Thread com.android.gallery3d.util.PriorityThreadFactory.newThread(java.lang.Runnable)",this);return new Thread(r, mName + '-' + mNumber.getAndIncrement()) {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.PriorityThreadFactory$1.run()",this);try{Process.setThreadPriority(mPriority);
                super.run();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.PriorityThreadFactory$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.PriorityThreadFactory$1.run()",this,throwable);throw throwable;}
            }
        };}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Thread com.android.gallery3d.util.PriorityThreadFactory.newThread(java.lang.Runnable)",this,throwable);throw throwable;}
    }

}
