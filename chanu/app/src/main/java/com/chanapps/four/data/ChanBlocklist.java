package com.chanapps.four.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import com.chanapps.four.activity.SettingsActivity;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 2/2/13
 * Time: 9:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChanBlocklist {

    private static final String TAG = ChanBlocklist.class.getSimpleName();
    private static final boolean DEBUG = false;

    public enum BlockType {
        TEXT ("text", SettingsActivity.PREF_BLOCKLIST_TEXT),
        TRIPCODE ("tripcode", SettingsActivity.PREF_BLOCKLIST_TRIPCODE),
        NAME ("name", SettingsActivity.PREF_BLOCKLIST_NAME),
        EMAIL ("email", SettingsActivity.PREF_BLOCKLIST_EMAIL),
        ID ("id", SettingsActivity.PREF_BLOCKLIST_ID),
        THREAD ("thread", SettingsActivity.PREF_BLOCKLIST_THREAD);
        private String displayString;
        private String blockPref;
        BlockType(String s, String t) {
            displayString = s;
            blockPref = t;
        }
        public String displayString() {
            com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanBlocklist.displayString()",this);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanBlocklist.displayString()",this);return displayString;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBlocklist.displayString()",this,throwable);throw throwable;}
        }
        public String blockPref() {
            com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanBlocklist.blockPref()",this);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanBlocklist.blockPref()",this);return blockPref;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBlocklist.blockPref()",this,throwable);throw throwable;}
        }
    };

    private static Map<BlockType, Set<String>> blocklist;
    private static Pattern testPattern = null;

     private static void initBlocklist(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanBlocklist.initBlocklist(android.content.Context)",context);try{if (blocklist != null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBlocklist.initBlocklist(android.content.Context)");return;}}
        blocklist = new HashMap<BlockType, Set<String>>();
        synchronized (blocklist) {
            blocklist.clear();
            for (int i = 0; i < BlockType.values().length; i++) {
                BlockType blockType = BlockType.values()[i];
                Set<String> savedBlocks = PreferenceManager
                        .getDefaultSharedPreferences(context)
                        .getStringSet(blockType.blockPref(), new HashSet<String>());
                /*// copy to avoid android getStringSet bug*/
                Set<String> blocks = new HashSet<String>(savedBlocks.size());
                blocks.addAll(savedBlocks);
                blocklist.put(blockType, blocks);
            }
            compileTestPattern();
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanBlocklist.initBlocklist(android.content.Context)",throwable);throw throwable;}
    }

    public static List<Pair<String, BlockType>> getSorted(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.List com.chanapps.four.data.ChanBlocklist.getSorted(android.content.Context)",context);try{if (blocklist == null)
            {initBlocklist(context);}
        List<Pair<String, BlockType>> sorted = new ArrayList<Pair<String, BlockType>>();
        for (BlockType blockType : BlockType.values()) {
            Set<String> blocks = blocklist.get(blockType);
            if (DEBUG) {Log.i(TAG, "getSorted() type=" + blockType + " blocks=" + blocks);}
            if (blocks == null || blocks.isEmpty())
                {continue;}
            for (String block : blocks) {
                if (block != null && !block.isEmpty())
                    {sorted.add(new Pair<String, BlockType>(block, blockType));}
            }
        }
        Collections.sort(sorted, blocklistComparator);
        {com.mijack.Xlog.logStaticMethodExit("java.util.List com.chanapps.four.data.ChanBlocklist.getSorted(android.content.Context)");return sorted;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.List com.chanapps.four.data.ChanBlocklist.getSorted(android.content.Context)",throwable);throw throwable;}

    }

    protected static Comparator<Pair<String, BlockType>> blocklistComparator = new Comparator<Pair<String, BlockType>>() {
        @Override
        public int compare(Pair<String, BlockType> lhs, Pair<String, BlockType> rhs) {
            com.mijack.Xlog.logMethodEnter("int com.chanapps.four.data.ChanBlocklist$1.compare(android.util.Pair,android.util.Pair)",this,lhs,rhs);try{int comp1 = lhs.first.compareToIgnoreCase(rhs.first);
            if (comp1 != 0)
                {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanBlocklist$1.compare(android.util.Pair,android.util.Pair)",this);return comp1;}}
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanBlocklist$1.compare(android.util.Pair,android.util.Pair)",this);return lhs.second.compareTo(rhs.second);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.data.ChanBlocklist$1.compare(android.util.Pair,android.util.Pair)",this,throwable);throw throwable;}
        }
    };

    public static void removeAll(Context context, BlockType blockType, List<String> removeBlocks) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanBlocklist.removeAll(android.content.Context,java.util.BlockType,java.util.List)",context,blockType,removeBlocks);try{if (blocklist == null)
            {initBlocklist(context);}
        Set<String> blocks = blocklist.get(blockType);
        blocks.removeAll(removeBlocks);
        saveBlocklist(context, blockType);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBlocklist.removeAll(android.content.Context,java.util.BlockType,java.util.List)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanBlocklist.removeAll(android.content.Context,java.util.BlockType,java.util.List)",throwable);throw throwable;}
    }

    public static void remove(Context context, BlockType blockType, String removeBlock) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanBlocklist.remove(android.content.Context,java.util.BlockType,java.util.String)",context,blockType,removeBlock);try{if (blocklist == null)
            {initBlocklist(context);}
        Set<String> blocks = blocklist.get(blockType);
        saveBlocklist(context, blockType);
        List<String> removeBlocks = new ArrayList<String>(1);
        removeBlocks.add(removeBlock);
        blocks.removeAll(removeBlocks);
        removeAll(context, blockType, removeBlocks);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBlocklist.remove(android.content.Context,java.util.BlockType,java.util.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanBlocklist.remove(android.content.Context,java.util.BlockType,java.util.String)",throwable);throw throwable;}
    }

    public static void removeMatching(Context context, BlockType blockType, String substring) { com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanBlocklist.removeMatching(android.content.Context,java.util.BlockType,java.util.String)",context,blockType,substring);try{/*// substring non-regexp match*/
        if (substring == null || substring.isEmpty())
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBlocklist.removeMatching(android.content.Context,java.util.BlockType,java.util.String)");return;}}
        if (blocklist == null)
            {initBlocklist(context);}
        Set<String> blocks = blocklist.get(blockType);
        List<String> removeBlocks = new ArrayList<String>();
        for (String b : blocks) {
            if (b != null && b.contains(substring))
                {removeBlocks.add(b);}
        }
        blocks.removeAll(removeBlocks);
        saveBlocklist(context, blockType);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanBlocklist.removeMatching(android.content.Context,java.util.BlockType,java.util.String)",throwable);throw throwable;}
    }

    public static boolean hasMatching(Context context, BlockType blockType, String substring) { com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBlocklist.hasMatching(android.content.Context,java.util.BlockType,java.util.String)",context,blockType,substring);try{/*// substring non-regexp match*/
        if (substring == null || substring.isEmpty())
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.hasMatching(android.content.Context,java.util.BlockType,java.util.String)");return false;}}
        if (blocklist == null)
            {initBlocklist(context);}
        Set<String> blocks = blocklist.get(blockType);
        if (blocks == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.hasMatching(android.content.Context,java.util.BlockType,java.util.String)");return false;}}
        for (String b : blocks) {
            if (b != null && b.contains(substring))
                {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.hasMatching(android.content.Context,java.util.BlockType,java.util.String)");return true;}}
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.hasMatching(android.content.Context,java.util.BlockType,java.util.String)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBlocklist.hasMatching(android.content.Context,java.util.BlockType,java.util.String)",throwable);throw throwable;}
    }

    public static void add(Context context, BlockType blockType, String newBlock) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanBlocklist.add(android.content.Context,java.util.BlockType,java.util.String)",context,blockType,newBlock);try{if (blocklist == null)
            {initBlocklist(context);}
        Set<String> blocks = blocklist.get(blockType);
        List<String> newBlocks = new ArrayList<String>(1);
        newBlocks.add(newBlock);
        blocks.addAll(newBlocks);
        saveBlocklist(context, blockType);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBlocklist.add(android.content.Context,java.util.BlockType,java.util.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanBlocklist.add(android.content.Context,java.util.BlockType,java.util.String)",throwable);throw throwable;}
    }

    public static boolean contains(Context context, BlockType blockType, String block) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBlocklist.contains(android.content.Context,java.util.BlockType,java.util.String)",context,blockType,block);try{if (blocklist == null)
            {initBlocklist(context);}
        Set<String> typeList = blocklist.get(blockType);
        if (typeList == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.contains(android.content.Context,java.util.BlockType,java.util.String)");return false;}}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.contains(android.content.Context,java.util.BlockType,java.util.String)");return typeList.contains(block);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBlocklist.contains(android.content.Context,java.util.BlockType,java.util.String)",throwable);throw throwable;}
    }

    public static boolean isBlocked(Context context, ChanPost post) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanPost)",context,post);try{if (blocklist == null)
            {initBlocklist(context);}
        boolean simpleMatch =  contains(context, BlockType.THREAD, post.uniqueId())
                || contains(context, BlockType.TRIPCODE, post.trip)
                || contains(context, BlockType.NAME, post.name)
                || contains(context, BlockType.EMAIL, post.email)
                || contains(context, BlockType.ID, post.id)
                ;
        if (simpleMatch)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanPost)");return true;}}
        if (testPattern == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanPost)");return false;}}
        if (post.sub != null && testPattern.matcher(post.sub).find())
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanPost)");return true;}}
        if (post.com != null && testPattern.matcher(post.com).find())
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanPost)");return true;}}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanPost)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanPost)",throwable);throw throwable;}
    }

    public static boolean isBlocked(Context context, ChanThread thread) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanThread)",context,thread);try{if (isBlocked(context, (ChanPost)thread))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanThread)");return true;}}
        if (thread.lastReplies == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanThread)");return false;}}
        for (ChanPost post : thread.lastReplies)
            {if (isBlocked(context, post))
                {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanThread)");return true;}}}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanThread)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBlocklist.isBlocked(android.content.Context,java.util.ChanThread)",throwable);throw throwable;}
    }

    private static void saveBlocklist(Context context, BlockType blockType) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanBlocklist.saveBlocklist(android.content.Context,java.util.BlockType)",context,blockType);try{if (blockType == BlockType.TEXT) /*// precreate regex for efficient matching*/
            {compileTestPattern();}
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        Set<String> blocks = blocklist.get(blockType);
        Set<String> copy = new HashSet<String>(blocks.size());
        copy.addAll(blocks);
        if (DEBUG) {Log.i(TAG, "saveBlocklist() type=" + blockType + " blocks=" + copy);}
        editor.putStringSet(blockType.blockPref(), copy).apply();com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBlocklist.saveBlocklist(android.content.Context,java.util.BlockType)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanBlocklist.saveBlocklist(android.content.Context,java.util.BlockType)",throwable);throw throwable;}
    }

    private static void compileTestPattern() {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanBlocklist.compileTestPattern()");try{Set<String> blocks = blocklist.get(BlockType.TEXT);
        if (blocks.isEmpty()) {
            testPattern = null;
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBlocklist.compileTestPattern()");return;}
        }
        List<String> regexList = new ArrayList<String>();
        for (String block : blocks) {
            if (block == null || block.isEmpty())
                {continue;}
            String regex = block.replaceAll("[()|]", "");
            if (regex.isEmpty())
                {continue;}
            regexList.add(regex);
        }
        if (regexList.isEmpty()) {
            testPattern = null;
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBlocklist.compileTestPattern()");return;}
        }
        String regex = "(" + StringUtils.join(regexList, "|") + ")";
        testPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanBlocklist.compileTestPattern()",throwable);throw throwable;}
    }

    public static void save(Context context, List<Pair<String, BlockType>> newBlocks) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanBlocklist.save(android.content.Context,java.util.List)",context,newBlocks);try{if (blocklist == null)
            {initBlocklist(context);}
        synchronized (blocklist) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            for (BlockType blockType : BlockType.values()) /*// out with the old*/
                {blocklist.get(blockType).clear();}
            for (Pair<String, BlockType> block : newBlocks) /*// and in with the new*/
                {if (block.first != null && !block.first.isEmpty() && block.second != null)
                    {blocklist.get(block.second).add(block.first);}}
            for (BlockType blockType : BlockType.values()) {
                if (blockType == BlockType.TEXT) /*// precreate regex for efficient matching*/
                    {compileTestPattern();}
                Set<String> blocks = blocklist.get(blockType);
                if (DEBUG) {Log.i(TAG, "save() type=" + blockType + " blocks=" + blocks);}
                editor.putStringSet(blockType.blockPref(), blocks);
            }
            editor.apply();
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBlocklist.save(android.content.Context,java.util.List)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanBlocklist.save(android.content.Context,java.util.List)",throwable);throw throwable;}
    }

}
