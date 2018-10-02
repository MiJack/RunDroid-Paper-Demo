/**
 * 
 */
package com.chanapps.four.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chanapps.four.activity.ChanActivityId;
import com.chanapps.four.activity.ChanIdentifiedService;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.data.ChanThread;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class BoardThreadsParserService extends BaseChanService implements ChanIdentifiedService {
    protected static final String TAG = "BoardThreadsParserService";
    private static final boolean DEBUG = false;
	
    private String boardCode;
    private boolean boardCatalog;
    private int pageNo;
    private boolean priority;
    private ChanBoard board;

    public static void startService(Context context, String boardCode, int pageNo) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.service.BoardThreadsParserService.startService(android.content.Context,java.lang.String,int)",context,boardCode,pageNo);try{if (DEBUG) {Log.i(TAG, "Start board load service for board=" + boardCode + " page=" + pageNo + " priority=" + false );}
        Intent intent = new Intent(context, BoardThreadsParserService.class);
        intent.putExtra(ChanBoard.BOARD_CODE, boardCode);
        intent.putExtra(ChanBoard.BOARD_CATALOG, pageNo == -1 ? 1 : 0);
        intent.putExtra(ChanBoard.PAGE, pageNo);
        context.startService(intent);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.service.BoardThreadsParserService.startService(android.content.Context,java.lang.String,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.service.BoardThreadsParserService.startService(android.content.Context,java.lang.String,int)",throwable);throw throwable;}
    }

    public static void startServiceWithPriority(Context context, String boardCode, int pageNo) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.service.BoardThreadsParserService.startServiceWithPriority(android.content.Context,java.lang.String,int)",context,boardCode,pageNo);try{if (DEBUG) {Log.i(TAG, "Start board load service for board=" + boardCode + " page=" + pageNo + " priority=" + true );}
        Intent intent = new Intent(context, BoardThreadsParserService.class);
        intent.putExtra(ChanBoard.BOARD_CODE, boardCode);
        intent.putExtra(ChanBoard.BOARD_CATALOG, pageNo == -1 ? 1 : 0);
        intent.putExtra(ChanBoard.PAGE, pageNo);
        context.startService(intent);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.service.BoardThreadsParserService.startServiceWithPriority(android.content.Context,java.lang.String,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.service.BoardThreadsParserService.startServiceWithPriority(android.content.Context,java.lang.String,int)",throwable);throw throwable;}
    }

    public BoardThreadsParserService() {
   		super("boardThreads");
   	}

    protected BoardThreadsParserService(String name) {
   		super(name);
   	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.BoardThreadsParserService.onHandleIntent(android.content.Intent)",this,intent);try{boardCode = intent.getStringExtra(ChanBoard.BOARD_CODE);
		boardCatalog = intent.getIntExtra(ChanBoard.BOARD_CATALOG, 0) == 1;
		pageNo = boardCatalog ? -1 : intent.getIntExtra(ChanBoard.PAGE, 0);
        priority = intent.getIntExtra(PRIORITY_MESSAGE_FETCH, 0) > 0;
		if (DEBUG) {Log.i(TAG, "Handling board=" + boardCode + " page=" + pageNo);}

        long startTime = Calendar.getInstance().getTimeInMillis();
		try {
            Context context = getBaseContext();

        	board = ChanFileStorage.loadBoardData(getBaseContext(), boardCode);
        	if (board.defData) {
        		/*// at this point valid board object should be available*/
        		{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.BoardThreadsParserService.onHandleIntent(android.content.Intent)",this);return;}
        	}
        	
            File boardFile = ChanFileStorage.getBoardFile(context, boardCode, pageNo);
            if (boardCatalog) {
            	/*// there's no point in parsing catalog for threads*/
            	parseBoardCatalog(new BufferedReader(new FileReader(boardFile)));
            } else {
            	parseBoard(new BufferedReader(new FileReader(boardFile)));
            }
            boardFile.delete();

            if (DEBUG) {Log.i(TAG, "Parsed board " + boardCode + " page " + pageNo
            		+ " in " + (Calendar.getInstance().getTimeInMillis() - startTime) + "ms");}
            startTime = Calendar.getInstance().getTimeInMillis();
        } catch (Exception e) {
            Log.e(TAG, "Board parsing error", e);
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.BoardThreadsParserService.onHandleIntent(android.content.Intent)",this,throwable);throw throwable;}
	}

    private void parseBoard(BufferedReader in) throws IOException {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.BoardThreadsParserService.parseBoard(java.io.BufferedReader)",this,in);try{List<ChanPost> threads = new ArrayList<ChanPost>();

        ObjectMapper mapper = BoardParserService.getJsonMapper();
        JsonNode rootNode = mapper.readValue(in, JsonNode.class);
        for (JsonNode threadValue : rootNode.path("threads")) { /*// iterate over threads*/
            ChanThread thread = null;
            List<ChanPost> posts = new ArrayList<ChanPost>();
            boolean first = true;
            for (JsonNode postValue : threadValue.path("posts")) { /*// first object is the thread post*/
                try {
                    ChanPost post = mapper.readValue(postValue, ChanPost.class);
                    if (post != null) {
                        if (post.board == null || post.board.isEmpty())
                            {post.board = boardCode;}
                        if (first) {
                            thread = ChanFileStorage.loadThreadData(getBaseContext(), post.board, post.no);
                            /*// if thread was not stored create a new object*/
                            if (thread == null || thread.defData) {
                                thread = new ChanThread();
                                thread.board = post.board;
                                thread.lastFetched = 0;
                                thread.no = post.no;
                                /*// note we don't set the lastUpdated here because we didn't pull the full thread yet*/
                            } else if (board != null && board.lastFetched < thread.lastFetched) {
                                /*// do not update thread if was fetched later than board*/
                                break;
                            }
                            post.mergeIntoThreadList(threads);
                            first = false;
                        }
                        posts.add(post);
                    }
                }
                catch (JsonMappingException e) { /*// if we have just one error, try and recover*/
                    Log.e(TAG, "Couldn't parseBoard threadValue for board=" + boardCode, e);
                }

            }
            if (thread != null) {
                thread.mergePosts(posts);
                if (DEBUG) {Log.i(TAG, "After parseBoard calling storeThreadData for /" + thread.board + "/" + thread.no);}
                ChanFileStorage.storeThreadData(getBaseContext(), thread);
            }
        }
        if (DEBUG) {Log.i(TAG, "Stored " + threads.size() + " threads for board " + boardCode);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.BoardThreadsParserService.parseBoard(java.io.BufferedReader)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.BoardThreadsParserService.parseBoard(java.io.BufferedReader)",this,throwable);throw throwable;}
    }

    private void parseBoardCatalog(BufferedReader in) throws IOException {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.BoardThreadsParserService.parseBoardCatalog(java.io.BufferedReader)",this,in);try{int updatedThreads = 0;
    	try {
	        ObjectMapper mapper = BoardParserService.getJsonMapper();
	        JsonParser jp = new MappingJsonFactory().createJsonParser(in);
	    	BoardParserService.configureJsonParser(jp);
	    	jp.nextToken(); /*// will return JsonToken.START_ARRAY*/
	    	while (jp.nextToken() != JsonToken.END_ARRAY) {
        		jp.nextToken(); /*// should be JsonToken.START_OBJECT*/
        		JsonNode pageNode = jp.readValueAsTree();
    	        for (JsonNode threadValue : pageNode.path("threads")) { /*// iterate over threads*/
                    try {
                        ChanPost post = mapper.readValue(threadValue, ChanPost.class);
                        if (post != null) {
                            if (post.board == null || post.board.isEmpty())
                                {post.board = boardCode;}
                            ChanThread thread = ChanFileStorage.loadThreadData(getBaseContext(), post.board, post.no);
                            if (thread == null || thread.defData) {
                                thread = new ChanThread();
                                if (thread != null) {
                                    thread.board = post.board;
                                    thread.lastFetched = 0;
                                    thread.no = post.no;
                                    thread.posts = new ChanPost[]{post};
                                    if (DEBUG) {Log.i(TAG, "After parseBoardCatalog calling storeThreadData for /" + thread.board + "/" + thread.no);}
                                    ChanFileStorage.storeThreadData(getBaseContext(), thread);
                                    updatedThreads++;
                                }
                            }
                        }
                    }
                    catch (JsonMappingException e) { /*// if we have just one error, try and recover*/
                        Log.e(TAG, "Couldn't parseBoardCatalog threadValue for board=" + boardCode, e);
                    }
                }
	    	}
	    	jp.close();
		} catch (Exception e) {
			/*// we don't care about parse exceptions here*/
			/*// error is thrown at the BoardParserService level*/
		}
    	
        if (DEBUG) {Log.i(TAG, "Updated " + updatedThreads + " threads for board " + boardCode);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.BoardThreadsParserService.parseBoardCatalog(java.io.BufferedReader)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.BoardThreadsParserService.parseBoardCatalog(java.io.BufferedReader)",this,throwable);throw throwable;}
    }

    @Override
	public ChanActivityId getChanActivityId() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.BoardThreadsParserService.getChanActivityId()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.BoardThreadsParserService.getChanActivityId()",this);return new ChanActivityId(boardCode, pageNo, priority);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.BoardThreadsParserService.getChanActivityId()",this,throwable);throw throwable;}
	}

    @Override
    public String toString() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.service.BoardThreadsParserService.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.service.BoardThreadsParserService.toString()",this);return "BoardThreadsParserService : " + getChanActivityId();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.service.BoardThreadsParserService.toString()",this,throwable);throw throwable;}
    }

}
