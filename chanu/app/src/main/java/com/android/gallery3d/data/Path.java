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

package com.android.gallery3d.data;

import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.IdentityCache;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Path {
    private static final String TAG = "Path";
    private static Path sRoot = new Path(null, "ROOT");

    private final Path mParent;
    private final String mSegment;
    private WeakReference<MediaObject> mObject;
    private IdentityCache<String, Path> mChildren;

    private Path(Path parent, String segment) {
        mParent = parent;
        mSegment = segment;
    }

	public Path getChild(String segment) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getChild(java.lang.String)",this,segment);try{synchronized (Path.class) {
            if (mChildren == null) {
                mChildren = new IdentityCache<String, Path>();
            } else {
                Path p = mChildren.get(segment);
                if (p != null) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getChild(java.lang.String)",this);return p;}}
            }

            Path p = new Path(this, segment);
            mChildren.put(segment, p);
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getChild(java.lang.String)",this);return p;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getChild(java.lang.String)",this,throwable);throw throwable;}
    }

    public Path getParent() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getParent()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getParent()",this);synchronized (Path.class) {
            return mParent;
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getParent()",this,throwable);throw throwable;}
    }

    public Path getChild(int segment) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getChild(int)",this,segment);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getChild(int)",this);return getChild(String.valueOf(segment));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getChild(int)",this,throwable);throw throwable;}
    }

    public Path getChild(long segment) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getChild(long)",this,segment);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getChild(long)",this);return getChild(String.valueOf(segment));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.getChild(long)",this,throwable);throw throwable;}
    }

    public void setObject(MediaObject object) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.Path.setObject(com.android.gallery3d.data.MediaObject)",this,object);try{synchronized (Path.class) {
            Utils.assertTrue(mObject == null || mObject.get() == null);
            mObject = new WeakReference<MediaObject>(object);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.Path.setObject(com.android.gallery3d.data.MediaObject)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.Path.setObject(com.android.gallery3d.data.MediaObject)",this,throwable);throw throwable;}
    }

    public MediaObject getObject() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.Path.getObject()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.Path.getObject()",this);synchronized (Path.class) {
            return (mObject == null) ? null : mObject.get();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaObject com.android.gallery3d.data.Path.getObject()",this,throwable);throw throwable;}
    }

    @Override
    public String toString() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.Path.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.Path.toString()",this);synchronized (Path.class) {
            StringBuilder sb = new StringBuilder();
            String[] segments = split();
            for (int i = 0; i < segments.length; i++) {
                sb.append("/");
                sb.append(segments[i]);
            }
            return sb.toString();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.Path.toString()",this,throwable);throw throwable;}
    }

    public static Path fromString(String s) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.fromString(java.lang.String)",s);try{com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.fromString(java.lang.String)");synchronized (Path.class) {
            String[] segments = split(s);
            Path current = sRoot;
            for (int i = 0; i < segments.length; i++) {
                current = current.getChild(segments[i]);
            }
            return current;
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.data.Path com.android.gallery3d.data.Path.fromString(java.lang.String)",throwable);throw throwable;}
    }

    public String[] split() {
        com.mijack.Xlog.logMethodEnter("[java.lang.String com.android.gallery3d.data.Path.split()",this);try{com.mijack.Xlog.logMethodExit("[java.lang.String com.android.gallery3d.data.Path.split()",this);synchronized (Path.class) {
            int n = 0;
            for (Path p = this; p != sRoot; p = p.mParent) {
                n++;
            }
            String[] segments = new String[n];
            int i = n - 1;
            for (Path p = this; p != sRoot; p = p.mParent) {
                segments[i--] = p.mSegment;
            }
            return segments;
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.lang.String com.android.gallery3d.data.Path.split()",this,throwable);throw throwable;}
    }

    public static String[] split(String s) {
        com.mijack.Xlog.logStaticMethodEnter("[java.lang.String com.android.gallery3d.data.Path.split(java.lang.String)",s);try{int n = s.length();
        if (n == 0) {{com.mijack.Xlog.logStaticMethodExit("[java.lang.String com.android.gallery3d.data.Path.split(java.lang.String)");return new String[0];}}
        if (s.charAt(0) != '/') {
            throw new RuntimeException("malformed path:" + s);
        }
        ArrayList<String> segments = new ArrayList<String>();
        int i = 1;
        while (i < n) {
            int brace = 0;
            int j;
            for (j = i; j < n; j++) {
                char c = s.charAt(j);
                if (c == '{') {++brace;}
                else if (c == '}') {--brace;}
                else if (brace == 0 && c == '/') {break;}
            }
            if (brace != 0) {
                throw new RuntimeException("unbalanced brace in path:" + s);
            }
            segments.add(s.substring(i, j));
            i = j + 1;
        }
        String[] result = new String[segments.size()];
        segments.toArray(result);
        {com.mijack.Xlog.logStaticMethodExit("[java.lang.String com.android.gallery3d.data.Path.split(java.lang.String)");return result;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[java.lang.String com.android.gallery3d.data.Path.split(java.lang.String)",throwable);throw throwable;}
    }

    /*// Splits a string to an array of strings.*/
    /*// For example, "{foo,bar,baz}" -> {"foo","bar","baz"}.*/
    public static String[] splitSequence(String s) {
        com.mijack.Xlog.logStaticMethodEnter("[java.lang.String com.android.gallery3d.data.Path.splitSequence(java.lang.String)",s);try{int n = s.length();
        if (s.charAt(0) != '{' || s.charAt(n-1) != '}') {
            throw new RuntimeException("bad sequence: " + s);
        }
        ArrayList<String> segments = new ArrayList<String>();
        int i = 1;
        while (i < n - 1) {
            int brace = 0;
            int j;
            for (j = i; j < n - 1; j++) {
                char c = s.charAt(j);
                if (c == '{') {++brace;}
                else if (c == '}') {--brace;}
                else if (brace == 0 && c == ',') {break;}
            }
            if (brace != 0) {
                throw new RuntimeException("unbalanced brace in path:" + s);
            }
            segments.add(s.substring(i, j));
            i = j + 1;
        }
        String[] result = new String[segments.size()];
        segments.toArray(result);
        {com.mijack.Xlog.logStaticMethodExit("[java.lang.String com.android.gallery3d.data.Path.splitSequence(java.lang.String)");return result;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[java.lang.String com.android.gallery3d.data.Path.splitSequence(java.lang.String)",throwable);throw throwable;}
    }

    public String getPrefix() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.Path.getPrefix()",this);try{synchronized (Path.class) {
            Path current = this;
            if (current == sRoot) {{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.Path.getPrefix()",this);return "";}}
            while (current.mParent != sRoot) {
                current = current.mParent;
            }
            {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.Path.getPrefix()",this);return current.mSegment;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.Path.getPrefix()",this,throwable);throw throwable;}
    }

    public String getSuffix() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.Path.getSuffix()",this);try{/*// We don't need lock because mSegment is final.*/
        {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.Path.getSuffix()",this);return mSegment;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.Path.getSuffix()",this,throwable);throw throwable;}
    }

    public String getSuffix(int level) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.Path.getSuffix(int)",this,level);try{/*// We don't need lock because mSegment and mParent are final.*/
        Path p = this;
        while (level-- != 0) {
            p = p.mParent;
        }
        {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.Path.getSuffix(int)",this);return p.mSegment;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.Path.getSuffix(int)",this,throwable);throw throwable;}
    }

    /*// Below are for testing/debugging only*/
    static void clearAll() {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.data.Path.clearAll()");try{synchronized (Path.class) {
            sRoot = new Path(null, "");
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.data.Path.clearAll()");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.data.Path.clearAll()",throwable);throw throwable;}
    }

    static void dumpAll() {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.data.Path.dumpAll()");try{dumpAll(sRoot, "", "");com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.data.Path.dumpAll()");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.data.Path.dumpAll()",throwable);throw throwable;}
    }

    static void dumpAll(Path p, String prefix1, String prefix2) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.data.Path.dumpAll(com.android.gallery3d.data.Path,java.lang.String,java.lang.String)",p,prefix1,prefix2);try{synchronized (Path.class) {
            MediaObject obj = p.getObject();
            Log.d(TAG, prefix1 + p.mSegment + ":"
                    + (obj == null ? "null" : obj.getClass().getSimpleName()));
            if (p.mChildren != null) {
                ArrayList<String> childrenKeys = p.mChildren.keys();
                int i = 0, n = childrenKeys.size();
                for (String key : childrenKeys) {
                    Path child = p.mChildren.get(key);
                    if (child == null) {
                        ++i;
                        continue;
                    }
                    Log.d(TAG, prefix2 + "|");
                    if (++i < n) {
                        dumpAll(child, prefix2 + "+-- ", prefix2 + "|   ");
                    } else {
                        dumpAll(child, prefix2 + "+-- ", prefix2 + "    ");
                    }
                }
            }
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.data.Path.dumpAll(com.android.gallery3d.data.Path,java.lang.String,java.lang.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.data.Path.dumpAll(com.android.gallery3d.data.Path,java.lang.String,java.lang.String)",throwable);throw throwable;}
    }

    @Override
	public boolean equals(Object o) {
    	com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.Path.equals(java.lang.Object)",this,o);try{if (o != null && o instanceof Path) {
    		Path op = (Path)o;
    		if (mParent != null) {
    			{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.Path.equals(java.lang.Object)",this);return mSegment.equals(op.mSegment) && mParent.equals(op.mParent);}
    		} else {
    			{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.Path.equals(java.lang.Object)",this);return mSegment.equals(op.mSegment);}
    		}
    	} else {
    		{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.Path.equals(java.lang.Object)",this);return false;}
    	}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.Path.equals(java.lang.Object)",this,throwable);throw throwable;}
	}
}
