package com.chanapps.four.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/22/13
 * Time: 11:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChanPostlist {

    public static final String USER_POSTS = "userPosts";

    public static void addPost(Context context, String boardCode, long threadNo, long postNo, String password) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanPostlist.addPost(android.content.Context,java.lang.String,long,long,java.lang.String)",context,boardCode,threadNo,postNo,password);try{SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> posts = prefs.getStringSet(USER_POSTS, new HashSet<String>());
        String post = getSerializedPost(boardCode, threadNo, postNo, password);
        posts.add(post);
        prefs.edit().putStringSet(USER_POSTS, posts).commit();com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanPostlist.addPost(android.content.Context,java.lang.String,long,long,java.lang.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanPostlist.addPost(android.content.Context,java.lang.String,long,long,java.lang.String)",throwable);throw throwable;}
    }

    private static String getSerializedPost(String boardCode, long threadNo, long postNo, String password) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.chanapps.four.data.ChanPostlist.getSerializedPost(java.lang.String,long,long,java.lang.String)",boardCode,threadNo,postNo,password);try{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.chanapps.four.data.ChanPostlist.getSerializedPost(java.lang.String,long,long,java.lang.String)");return boardCode + "/" + threadNo + "/" + postNo + "/" + password;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.chanapps.four.data.ChanPostlist.getSerializedPost(java.lang.String,long,long,java.lang.String)",throwable);throw throwable;}
    }
}
