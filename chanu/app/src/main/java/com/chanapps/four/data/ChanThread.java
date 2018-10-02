package com.chanapps.four.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import android.content.Context;
import android.database.MatrixCursor;
import android.util.Log;

import com.chanapps.four.activity.R;
import com.chanapps.four.component.URLFormatComponent;
import com.chanapps.four.service.NetworkProfileManager;

public class ChanThread extends ChanPost {

    private static final String TAG = ChanThread.class.getSimpleName();
    private static final boolean DEBUG = false;
    public static final double MAX_THUMBNAIL_PX = 250;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingLongDeserializer.class)
    public long lastFetched = 0;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingBooleanDeserializer.class)
    public boolean loadedFromBoard = false;

	public ChanPost posts[] = new ChanPost[0];

    @JsonProperty("last_replies")
    public ChanPost[] lastReplies = new ChanPost[0];

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int viewPosition = -1;
    public int viewOffset = 0;

    public static final String THREAD_COMPOSITE_ID = "_id";
    public static final String THREAD_BOARD_CODE = "threadBoardCode";
    public static final String THREAD_NO = "threadNo";
    public static final String THREAD_SUBJECT = "threadSub";
    public static final String THREAD_HEADLINE = "threadHeadline";
    public static final String THREAD_TEXT = "threadText";
    public static final String THREAD_THUMBNAIL_URL = "threadThumb";
    public static final String THREAD_COUNTRY_FLAG_URL = "threadFlag";
    public static final String THREAD_CLICK_URL = "threadClick";
    public static final String THREAD_NUM_REPLIES = "threadNumReplies";
    public static final String THREAD_NUM_IMAGES = "threadNumImages";
    public static final String THREAD_TN_W = "threadThumbWidth";
    public static final String THREAD_TN_H = "threadThumbHeight";
    public static final String THREAD_JUMP_TO_POST_NO = "threadJumpToPostNo";
    public static final String THREAD_FLAGS = "threadFlags";
    public static final String THREAD_NUM_LAST_REPLIES = "numLastReplies";
    public static final String THREAD_LAST_REPLIES_BLOB = "lastRepliesBlob";

    public static final int THREAD_FLAG_DEAD = 0x001;
    public static final int THREAD_FLAG_CLOSED = 0x002;
    public static final int THREAD_FLAG_STICKY = 0x004;
    public static final int THREAD_FLAG_BOARD = 0x010;
    public static final int THREAD_FLAG_BOARD_TYPE = 0x010;
    public static final int THREAD_FLAG_POPULAR_THREAD = 0x080;
    public static final int THREAD_FLAG_LATEST_POST = 0x100;
    public static final int THREAD_FLAG_RECENT_IMAGE = 0x200;
    public static final int THREAD_FLAG_HEADER = 0x400;

    private static final String[] THREAD_COLUMNS = {
            THREAD_COMPOSITE_ID,
            THREAD_BOARD_CODE,
            THREAD_NO,
            THREAD_SUBJECT,
            THREAD_HEADLINE,
            THREAD_TEXT,
            THREAD_THUMBNAIL_URL,
            THREAD_COUNTRY_FLAG_URL,
            THREAD_CLICK_URL,
            THREAD_NUM_REPLIES,
            THREAD_NUM_IMAGES,
            THREAD_TN_W,
            THREAD_TN_H,
            THREAD_JUMP_TO_POST_NO,
            THREAD_NUM_LAST_REPLIES,
            THREAD_LAST_REPLIES_BLOB,
            THREAD_FLAGS
    };

    public static MatrixCursor buildMatrixCursor(int capacity) {
        com.mijack.Xlog.logStaticMethodEnter("android.database.MatrixCursor com.chanapps.four.data.ChanThread.buildMatrixCursor(int)",capacity);try{com.mijack.Xlog.logStaticMethodExit("android.database.MatrixCursor com.chanapps.four.data.ChanThread.buildMatrixCursor(int)");return new MatrixCursor(THREAD_COLUMNS, capacity);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.database.MatrixCursor com.chanapps.four.data.ChanThread.buildMatrixCursor(int)",throwable);throw throwable;}
    }

    private static int threadFlags(ChanPost post) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.data.ChanThread.threadFlags(com.chanapps.four.data.ChanPost)",post);try{int flags = 0;
        if (post.isDead)
            {flags |= THREAD_FLAG_DEAD;}
        if (post.closed > 0)
            {flags |= THREAD_FLAG_CLOSED;}
        if (post.sticky > 0)
            {flags |= THREAD_FLAG_STICKY;}
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanThread.threadFlags(com.chanapps.four.data.ChanPost)");return flags;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.data.ChanThread.threadFlags(com.chanapps.four.data.ChanPost)",throwable);throw throwable;}
    }

    public static Object[] makeRow(Context context, ChanThread thread, String query, int extraFlags,
                                   boolean showNumReplies, boolean abbrev) {
        com.mijack.Xlog.logStaticMethodEnter("[java.lang.Object com.chanapps.four.data.ChanThread.makeRow(android.content.Context,com.chanapps.four.data.ChanThread,java.lang.String,int,boolean,boolean)",context,thread,query,extraFlags,showNumReplies,abbrev);try{String id = thread.board + "/" + thread.no;
        String[] textComponents = thread.textComponents(query);
        byte[] lastRepliesBlob = blobifyLastReplies(thread.lastReplies);
        if (DEBUG) {Log.i(TAG, "makeRow /" + thread.board + "/" + thread.no + " lastRepliesBlob=" + lastRepliesBlob);}
        {com.mijack.Xlog.logStaticMethodExit("[java.lang.Object com.chanapps.four.data.ChanThread.makeRow(android.content.Context,com.chanapps.four.data.ChanThread,java.lang.String,int,boolean,boolean)");return new Object[] {
                id.hashCode(),
                thread.board,
                thread.no,
                textComponents[0],
                thread.headline(context, query, true, null, showNumReplies, abbrev),
                textComponents[1],
                thread.thumbnailUrl(context),
                thread.countryFlagUrl(context),
                "",
                thread.replies,
                thread.images,
                thread.tn_w > 0 ? thread.tn_w : MAX_THUMBNAIL_PX,
                thread.tn_h > 0 ? thread.tn_h : MAX_THUMBNAIL_PX,
                thread.jumpToPostNo,
                thread.lastReplies == null ? 0 : thread.lastReplies.length,
                lastRepliesBlob,
                threadFlags(thread) | extraFlags
        };}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[java.lang.Object com.chanapps.four.data.ChanThread.makeRow(android.content.Context,com.chanapps.four.data.ChanThread,java.lang.String,int,boolean,boolean)",throwable);throw throwable;}
    }

    public static Object[] makeBoardRow(Context context, String boardCode, String boardName, int boardImageResourceId, int extraFlags) {
        com.mijack.Xlog.logStaticMethodEnter("[java.lang.Object com.chanapps.four.data.ChanThread.makeBoardRow(android.content.Context,java.lang.String,java.lang.String,int,int)",context,boardCode,boardName,boardImageResourceId,extraFlags);try{com.mijack.Xlog.logStaticMethodExit("[java.lang.Object com.chanapps.four.data.ChanThread.makeBoardRow(android.content.Context,java.lang.String,java.lang.String,int,int)");return new Object[] {
                boardCode.hashCode(),
                boardCode,
                0,
                boardName,
                ChanBoard.getDescription(context, boardCode),
                "",
                boardImageResourceId > 0 ? "drawable://" + boardImageResourceId : "",
                "",
                "",
                0,
                0,
                MAX_THUMBNAIL_PX,
                MAX_THUMBNAIL_PX,
                0,
                0,
                null,
                THREAD_FLAG_BOARD | extraFlags
        };}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[java.lang.Object com.chanapps.four.data.ChanThread.makeBoardRow(android.content.Context,java.lang.String,java.lang.String,int,int)",throwable);throw throwable;}
    }

    public static Object[] makeHeaderRow(Context context, ChanBoard board) {
        com.mijack.Xlog.logStaticMethodEnter("[java.lang.Object com.chanapps.four.data.ChanThread.makeHeaderRow(android.content.Context,com.chanapps.four.data.ChanBoard)",context,board);try{String boardCode = board.link;
        String boardName = "/" + boardCode + "/ " + board.getName(context);
        /*//String safeText = context.getString(board.isWorksafe(context, boardCode) ? R.string.board_type_worksafe : R.string.board_type_adult);*/
        String dateText = String.format(context.getString(R.string.board_last_updated),
                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(board.lastFetched)));
        /*//String description = board.getDescription(context) + "<br/>"*/
        /*//        + safeText + "<br/>"*/
        /*//        + dateText;*/
        String description = dateText;
        {com.mijack.Xlog.logStaticMethodExit("[java.lang.Object com.chanapps.four.data.ChanThread.makeHeaderRow(android.content.Context,com.chanapps.four.data.ChanBoard)");return new Object[] {
                boardCode.hashCode(),
                boardCode,
                0,
                boardName,
                description,
                "",
                "",
                "",
                "",
                0,
                0,
                MAX_THUMBNAIL_PX,
                MAX_THUMBNAIL_PX,
                0,
                0,
                null,
                THREAD_FLAG_BOARD | THREAD_FLAG_HEADER
        };}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[java.lang.Object com.chanapps.four.data.ChanThread.makeHeaderRow(android.content.Context,com.chanapps.four.data.ChanBoard)",throwable);throw throwable;}
    }

    public static boolean threadNeedsRefresh(Context context, String boardCode, long threadNo, boolean forceRefresh) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh(android.content.Context,java.lang.String,long,boolean)",context,boardCode,threadNo,forceRefresh);try{ChanThread thread = ChanFileStorage.loadThreadData(context, boardCode, threadNo);
        if (thread == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh(android.content.Context,java.lang.String,long,boolean)");return true;}}
        if (forceRefresh)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh(android.content.Context,java.lang.String,long,boolean)");return true;}}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh(android.content.Context,java.lang.String,long,boolean)");return thread.threadNeedsRefresh();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh(android.content.Context,java.lang.String,long,boolean)",throwable);throw throwable;}
    }
    
    public boolean threadNeedsRefresh() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh()",this);try{if (isDead)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh()",this);return false;}}
        else if (defData)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh()",this);return true;}}
        else if (posts == null || posts.length == 0)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh()",this);return true;}}
        else if (posts[0] == null || posts[0].defData)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh()",this);return true;}}
        else if (posts.length < replies)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh()",this);return true;}}
        else if (!isCurrent())
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh()",this);return true;}}
        else
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh()",this);return false;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanThread.threadNeedsRefresh()",this,throwable);throw throwable;}
    }

    public String toString() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.ChanThread.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.ChanThread.toString()",this);return "Thread " + no + ", defData:" + defData + " dead:" + isDead + ", images: " + images
                + " com: " + com + ", sub:" + sub + ", replies: " + replies + ", posts.length: " + posts.length
				+ (posts.length > 0
                    ? ", posts[0].no: " + posts[0].no + ", posts[0].replies: " + posts[0].replies
                    + ", posts[0].images: " + posts[0].images + ", posts[0].defData: " + posts[0].defData
                    + ", posts[0].isDead: " + posts[0].isDead
                    : "")
				+ ", tn_w: " + tn_w + " tn_h: " + tn_h;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.ChanThread.toString()",this,throwable);throw throwable;}
	}
	
    public void mergePosts(List<ChanPost> newPosts) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.ChanThread.mergePosts(java.util.List)",this,newPosts);try{Map<Long,ChanPost> postMap = new HashMap<Long,ChanPost>(this.posts.length);
        for (ChanPost post : this.posts)
            {postMap.put(post.no, post);}
        for (ChanPost newPost: newPosts)
            {postMap.put(newPost.no, newPost);} /*// overwrite any existing posts*/
        ChanPost[] postArray = postMap.values().toArray(new ChanPost[0]);
        Arrays.sort(postArray, new Comparator<ChanPost>() {
            @Override
            public int compare(ChanPost lhs, ChanPost rhs) {
                com.mijack.Xlog.logMethodEnter("int com.chanapps.four.data.ChanThread$1.compare(com.chanapps.four.data.ChanPost,com.chanapps.four.data.ChanPost)",this,lhs,rhs);try{if (lhs.no == rhs.no)
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanThread.mergePosts(java.util.List)",this);{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanThread$1.compare(com.chanapps.four.data.ChanPost,com.chanapps.four.data.ChanPost)",this);return 0;}}}
                else if (lhs.no < rhs.no)
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanThread.mergePosts(java.util.List)",this);{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanThread$1.compare(com.chanapps.four.data.ChanPost,com.chanapps.four.data.ChanPost)",this);return -1;}}}
                else
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanThread.mergePosts(java.util.List)",this);{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanThread$1.compare(com.chanapps.four.data.ChanPost,com.chanapps.four.data.ChanPost)",this);return 1;}}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.data.ChanThread$1.compare(com.chanapps.four.data.ChanPost,com.chanapps.four.data.ChanPost)",this,throwable);throw throwable;}
            }
        });
        this.posts = postArray; /*// swap*/}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.ChanThread.mergePosts(java.util.List)",this,throwable);throw throwable;}
    }

    public Map<Long, HashSet<Long>> backlinksMap() {
        com.mijack.Xlog.logMethodEnter("java.util.HashMap com.chanapps.four.data.ChanThread.backlinksMap()",this);try{Map<Long, HashSet<Long>> backlinksMap = new HashMap<Long, HashSet<Long>>();
        for (ChanPost post : posts) {
            HashSet<Long> backlinks = post.backlinks();
            if (backlinks != null && !backlinks.isEmpty())
                {backlinksMap.put(post.no, backlinks);}
        }
        {com.mijack.Xlog.logMethodExit("java.util.HashMap com.chanapps.four.data.ChanThread.backlinksMap()",this);return backlinksMap;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.HashMap com.chanapps.four.data.ChanThread.backlinksMap()",this,throwable);throw throwable;}
    }

    public Map<Long, HashSet<Long>> repliesMap(Map<Long, HashSet<Long>> backlinksMap) {
        com.mijack.Xlog.logMethodEnter("java.util.HashMap com.chanapps.four.data.ChanThread.repliesMap(java.util.HashMap)",this,backlinksMap);try{Map<Long, HashSet<Long>> repliesMap = new HashMap<Long, HashSet<Long>>();
        for (Long laterPostNo : backlinksMap.keySet()) {
            for (Long originalPostNo : backlinksMap.get(laterPostNo)) {
                HashSet<Long> replies = repliesMap.get(originalPostNo);
                if (replies == null) {
                    replies = new HashSet<Long>();
                    repliesMap.put(originalPostNo, replies);
                }
                replies.add(laterPostNo);
            }
        }
        {com.mijack.Xlog.logMethodExit("java.util.HashMap com.chanapps.four.data.ChanThread.repliesMap(java.util.HashMap)",this);return repliesMap;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.HashMap com.chanapps.four.data.ChanThread.repliesMap(java.util.HashMap)",this,throwable);throw throwable;}
    }

    public Map<String, HashSet<Long>> sameIdsMap() {
        com.mijack.Xlog.logMethodEnter("java.util.HashMap com.chanapps.four.data.ChanThread.sameIdsMap()",this);try{Map<String, HashSet<Long>> sameIdsMap = new HashMap<String, HashSet<Long>>();
        for (ChanPost post : posts) {
            if (post.id == null || post.id.isEmpty() || post.id.equals(ChanPost.SAGE_POST_ID))
                {continue;}
            HashSet<Long> sameIds = sameIdsMap.get(post.id);
            if (sameIds == null) {
                sameIds = new HashSet<Long>();
                sameIdsMap.put(post.id, sameIds);
            }
            sameIds.add(post.no);
        }
        {com.mijack.Xlog.logMethodExit("java.util.HashMap com.chanapps.four.data.ChanThread.sameIdsMap()",this);return sameIdsMap;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.HashMap com.chanapps.four.data.ChanThread.sameIdsMap()",this,throwable);throw throwable;}
    }
    
    public ChanThread cloneForWatchlist() {
    	com.mijack.Xlog.logMethodEnter("com.chanapps.four.data.ChanThread com.chanapps.four.data.ChanThread.cloneForWatchlist()",this);try{ChanThread t = new ChanThread();
    	t.no = no;
    	t.board = board;
    	t.closed = closed;
    	t.created = created;
    	t.omitted_images = omitted_images;
    	t.omitted_posts = omitted_posts;
    	t.resto = resto;
        t.jumpToPostNo = jumpToPostNo;
    	t.defData = false;

    	if (posts.length > 0 && posts[0] != null) {
        	t.replies = posts[0].replies;
        	t.images = posts[0].images;
	    	t.bumplimit = posts[0].bumplimit;
	    	t.capcode = posts[0].capcode;
	    	t.com = posts[0].com;
	    	t.country = posts[0].country;
	    	t.country_name = posts[0].country_name;
	    	t.email = posts[0].email;
	    	t.ext = posts[0].ext;
	    	t.filedeleted = posts[0].filedeleted;
	    	t.filename = posts[0].filename;
	    	t.fsize = posts[0].fsize;
	    	t.h = posts[0].h;
	    	t.hideAllText = posts[0].hideAllText;
	    	t.hidePostNumbers = posts[0].hidePostNumbers;
	    	t.id = posts[0].id;
	    	t.now = posts[0].now;
	    	t.spoiler = posts[0].spoiler;
	    	t.sticky = posts[0].sticky;
	    	t.sub = posts[0].sub;
	    	t.tim = posts[0].tim;
	    	t.tn_h = posts[0].tn_h;
	    	t.tn_w = posts[0].tn_w;
	    	t.trip = posts[0].trip;
	    	t.useFriendlyIds = posts[0].useFriendlyIds;
	    	t.w = posts[0].w;
            t.jumpToPostNo = posts[0].jumpToPostNo;
    	}
    	
    	{com.mijack.Xlog.logMethodExit("com.chanapps.four.data.ChanThread com.chanapps.four.data.ChanThread.cloneForWatchlist()",this);return t;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.data.ChanThread com.chanapps.four.data.ChanThread.cloneForWatchlist()",this,throwable);throw throwable;}
    }

    public static String threadUrl(Context context, String boardCode, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.chanapps.four.data.ChanThread.threadUrl(android.content.Context,java.lang.String,long)",context,boardCode,threadNo);try{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.chanapps.four.data.ChanThread.threadUrl(android.content.Context,java.lang.String,long)");return String.format(URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_WEB_THREAD_URL_FORMAT), boardCode, threadNo);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.chanapps.four.data.ChanThread.threadUrl(android.content.Context,java.lang.String,long)",throwable);throw throwable;}
    }

    public boolean isCurrent() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanThread.isCurrent()",this);try{FetchParams params = NetworkProfileManager.instance().getCurrentProfile().getFetchParams();
        if (defData)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.isCurrent()",this);return false;}}
        else if (lastFetched <= 0)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.isCurrent()",this);return false;}}
        else if (Math.abs(new Date().getTime() - lastFetched) > params.refreshDelay)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.isCurrent()",this);return false;}}
        else
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.isCurrent()",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanThread.isCurrent()",this,throwable);throw throwable;}
    }

    public static byte[] blobifyLastReplies(ChanPost[] list) {
        com.mijack.Xlog.logStaticMethodEnter("[byte com.chanapps.four.data.ChanThread.blobifyLastReplies([com.chanapps.four.data.ChanPost)",list);try{if (list == null || list.length == 0)
            {{com.mijack.Xlog.logStaticMethodExit("[byte com.chanapps.four.data.ChanThread.blobifyLastReplies([com.chanapps.four.data.ChanPost)");return null;}}
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(list);
            {com.mijack.Xlog.logStaticMethodExit("[byte com.chanapps.four.data.ChanThread.blobifyLastReplies([com.chanapps.four.data.ChanPost)");return baos.toByteArray();}
        }
        catch (IOException e) {
            Log.e(TAG, "Couldn't serialize list=" + list, e);
        }
        {com.mijack.Xlog.logStaticMethodExit("[byte com.chanapps.four.data.ChanThread.blobifyLastReplies([com.chanapps.four.data.ChanPost)");return null;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[byte com.chanapps.four.data.ChanThread.blobifyLastReplies([com.chanapps.four.data.ChanPost)",throwable);throw throwable;}
    }

    public static ChanPost[] parseLastRepliesBlob(final byte[] b) {
        com.mijack.Xlog.logStaticMethodEnter("[com.chanapps.four.data.ChanPost com.chanapps.four.data.ChanThread.parseLastRepliesBlob([byte)",b);try{if (b == null || b.length == 0)
            {{com.mijack.Xlog.logStaticMethodExit("[com.chanapps.four.data.ChanPost com.chanapps.four.data.ChanThread.parseLastRepliesBlob([byte)");return null;}}
        try {
            InputStream bais = new BufferedInputStream(new ByteArrayInputStream(b));
            ObjectInputStream ois = new ObjectInputStream(bais);
            ChanPost[] list = (ChanPost[])ois.readObject();
            {com.mijack.Xlog.logStaticMethodExit("[com.chanapps.four.data.ChanPost com.chanapps.four.data.ChanThread.parseLastRepliesBlob([byte)");return list;}
        }
        catch (Exception e) {
            Log.e(TAG, "Couldn't deserialize blob=" + b);
        }
        {com.mijack.Xlog.logStaticMethodExit("[com.chanapps.four.data.ChanPost com.chanapps.four.data.ChanThread.parseLastRepliesBlob([byte)");return null;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[com.chanapps.four.data.ChanPost com.chanapps.four.data.ChanThread.parseLastRepliesBlob([byte)",throwable);throw throwable;}
    }

    public boolean matchesQuery(String query) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanThread.matchesQuery(java.lang.String)",this,query);try{if (query == null || query.isEmpty())
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.matchesQuery(java.lang.String)",this);return true;}}
        if (super.matchesQuery(query))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.matchesQuery(java.lang.String)",this);return true;}}
        if (lastReplies != null) {
            for (ChanPost p : lastReplies) {
                if (p.matchesQuery(query))
                    {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.matchesQuery(java.lang.String)",this);return true;}}
            }
        }
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanThread.matchesQuery(java.lang.String)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanThread.matchesQuery(java.lang.String)",this,throwable);throw throwable;}
    }

}