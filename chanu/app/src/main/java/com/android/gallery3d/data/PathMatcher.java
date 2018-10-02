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

import java.util.ArrayList;
import java.util.HashMap;

public class PathMatcher {
    public static final int NOT_FOUND = -1;

    private ArrayList<String> mVariables = new ArrayList<String>();
    private Node mRoot = new Node();

    public PathMatcher() {
        mRoot = new Node();
    }

    public void add(String pattern, int kind) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.PathMatcher.add(java.lang.String,int)",this,pattern,kind);try{String[] segments = Path.split(pattern);
        Node current = mRoot;
        for (int i = 0; i < segments.length; i++) {
            current = current.addChild(segments[i]);
        }
        current.setKind(kind);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.PathMatcher.add(java.lang.String,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.PathMatcher.add(java.lang.String,int)",this,throwable);throw throwable;}
    }

    public int match(Path path) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.PathMatcher.match(com.android.gallery3d.data.Path)",this,path);try{String[] segments = path.split();
        mVariables.clear();
        Node current = mRoot;
        for (int i = 0; i < segments.length; i++) {
            Node next = current.getChild(segments[i]);
            if (next == null) {
                next = current.getChild("*");
                if (next != null) {
                    mVariables.add(segments[i]);
                } else {
                    {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.PathMatcher.match(com.android.gallery3d.data.Path)",this);return NOT_FOUND;}
                }
            }
            current = next;
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.PathMatcher.match(com.android.gallery3d.data.Path)",this);return current.getKind();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.PathMatcher.match(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public String getVar(int index) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.PathMatcher.getVar(int)",this,index);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.PathMatcher.getVar(int)",this);return mVariables.get(index);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.PathMatcher.getVar(int)",this,throwable);throw throwable;}
    }

    public int getIntVar(int index) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.PathMatcher.getIntVar(int)",this,index);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.PathMatcher.getIntVar(int)",this);return Integer.parseInt(mVariables.get(index));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.PathMatcher.getIntVar(int)",this,throwable);throw throwable;}
    }

    public long getLongVar(int index) {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.PathMatcher.getLongVar(int)",this,index);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.PathMatcher.getLongVar(int)",this);return Long.parseLong(mVariables.get(index));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.PathMatcher.getLongVar(int)",this,throwable);throw throwable;}
    }

    private static class Node {
        private HashMap<String, Node> mMap;
        private int mKind = NOT_FOUND;

        Node addChild(String segment) {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.PathMatcher$Node com.android.gallery3d.data.PathMatcher$Node.addChild(java.lang.String)",this,segment);try{if (mMap == null) {
                mMap = new HashMap<String, Node>();
            } else {
                Node node = mMap.get(segment);
                if (node != null) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.PathMatcher$Node com.android.gallery3d.data.PathMatcher$Node.addChild(java.lang.String)",this);return node;}}
            }

            Node n = new Node();
            mMap.put(segment, n);
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.PathMatcher$Node com.android.gallery3d.data.PathMatcher$Node.addChild(java.lang.String)",this);return n;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.PathMatcher$Node com.android.gallery3d.data.PathMatcher$Node.addChild(java.lang.String)",this,throwable);throw throwable;}
        }

        Node getChild(String segment) {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.PathMatcher$Node com.android.gallery3d.data.PathMatcher$Node.getChild(java.lang.String)",this,segment);try{if (mMap == null) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.PathMatcher$Node com.android.gallery3d.data.PathMatcher$Node.getChild(java.lang.String)",this);return null;}}
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.PathMatcher$Node com.android.gallery3d.data.PathMatcher$Node.getChild(java.lang.String)",this);return mMap.get(segment);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.PathMatcher$Node com.android.gallery3d.data.PathMatcher$Node.getChild(java.lang.String)",this,throwable);throw throwable;}
        }

        void setKind(int kind) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.PathMatcher$Node.setKind(int)",this,kind);try{mKind = kind;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.PathMatcher$Node.setKind(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.PathMatcher$Node.setKind(int)",this,throwable);throw throwable;}
        }

        int getKind() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.PathMatcher$Node.getKind()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.PathMatcher$Node.getKind()",this);return mKind;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.PathMatcher$Node.getKind()",this,throwable);throw throwable;}
        }
    }
}
