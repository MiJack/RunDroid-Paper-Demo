package com.chanapps.four.data;

import java.util.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.chanapps.four.activity.R;
import com.chanapps.four.activity.SettingsActivity;
import com.chanapps.four.component.AlphanumComparator;
import com.chanapps.four.component.URLFormatComponent;
import com.chanapps.four.service.FetchChanDataService;
import com.chanapps.four.service.NetworkProfileManager;

public class ChanBoard {

	public static final String TAG = ChanBoard.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final int NUM_DEFAULT_IMAGES_PER_BOARD = 3;
    private static final int NUM_RELATED_BOARDS = 3;

    public static final String BOARD_CODE = "boardCode";
    public static final String ALL_BOARDS_BOARD_CODE = BoardType.ALL_BOARDS.boardCode();
    public static final String POPULAR_BOARD_CODE = BoardType.POPULAR.boardCode();
    public static final String LATEST_BOARD_CODE = BoardType.LATEST.boardCode();
    public static final String LATEST_IMAGES_BOARD_CODE = BoardType.LATEST_IMAGES.boardCode();
    public static final String WATCHLIST_BOARD_CODE = BoardType.WATCHLIST.boardCode();
    public static final String FAVORITES_BOARD_CODE = BoardType.FAVORITES.boardCode();
    public static final String META_JAPANESE_CULTURE_BOARD_CODE = BoardType.JAPANESE_CULTURE.boardCode();
    public static final String META_INTERESTS_BOARD_CODE = BoardType.INTERESTS.boardCode();
    public static final String META_CREATIVE_BOARD_CODE = BoardType.CREATIVE.boardCode();
    public static final String META_OTHER_BOARD_CODE = BoardType.OTHER.boardCode();
    public static final String META_ADULT_BOARD_CODE = BoardType.ADULT.boardCode();
    public static final String META_MISC_BOARD_CODE = BoardType.MISC.boardCode();

    public static final String[] VIRTUAL_BOARDS = { ALL_BOARDS_BOARD_CODE, POPULAR_BOARD_CODE, LATEST_BOARD_CODE,
            LATEST_IMAGES_BOARD_CODE, WATCHLIST_BOARD_CODE, FAVORITES_BOARD_CODE,
            META_JAPANESE_CULTURE_BOARD_CODE, META_INTERESTS_BOARD_CODE,
            META_CREATIVE_BOARD_CODE, META_OTHER_BOARD_CODE,
            META_ADULT_BOARD_CODE, META_MISC_BOARD_CODE };
    public static final String[] META_BOARDS = { ALL_BOARDS_BOARD_CODE,
            META_JAPANESE_CULTURE_BOARD_CODE, META_INTERESTS_BOARD_CODE,
            META_CREATIVE_BOARD_CODE, META_OTHER_BOARD_CODE,
            META_ADULT_BOARD_CODE, META_MISC_BOARD_CODE };
    public static final String[] POPULAR_BOARDS = { POPULAR_BOARD_CODE, LATEST_BOARD_CODE, LATEST_IMAGES_BOARD_CODE };
    public static final String[] TOP_BOARDS = { ALL_BOARDS_BOARD_CODE, FAVORITES_BOARD_CODE, WATCHLIST_BOARD_CODE };

    private static final Set<String> removedBoards = new HashSet<String>();
    private static final String[] REMOVED_BOARDS = { "q" };
    static {
        removedBoards.clear();
        for (String boardCode : REMOVED_BOARDS)
            {removedBoards.add(boardCode);}
    }
    public static boolean isRemoved(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.isRemoved(java.util.String)",boardCode);try{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isRemoved(java.util.String)");return removedBoards.contains(boardCode);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isRemoved(java.util.String)",throwable);throw throwable;}
    }

    public static final String DEFAULT_BOARD_CODE = "a";
    public static final String PAGE = "pageNo";
    public static final String BOARD_CATALOG = "boardCatalog";

    public String board;
    public String name;
    public String link;
    public int iconId;
    public int no;
	public BoardType boardType;
    public boolean workSafe;
    public boolean classic;
    public boolean textOnly;
	public ChanPost stickyPosts[] = new ChanPost[0];
	public ChanThread threads[] = new ChanThread[0];
	public ChanThread loadedThreads[] = new ChanThread[0];
	public int newThreads = 0;
	public int updatedThreads = 0;
    public long lastFetched;
    public long lastSwapped;
    public boolean defData = false;

    private static List<ChanBoard> boards = new ArrayList<ChanBoard>();
    private static List<ChanBoard> safeBoards = new ArrayList<ChanBoard>();
    private static Map<BoardType, List<ChanBoard>> boardsByType = new HashMap<BoardType, List<ChanBoard>>();
    private static Map<String, ChanBoard> boardByCode = new HashMap<String, ChanBoard>();
    private static Map<String, List<ChanBoard>> relatedBoards = new HashMap<String, List<ChanBoard>>();

    protected static Map<String, int[]> boardDrawables = new HashMap<String, int[]>();

    public ChanBoard() {
        /*// public default constructor for Jackson*/
    }

    private ChanBoard(BoardType boardType, String name, String link, int iconId,
                      boolean workSafe, boolean classic, boolean textOnly) {
        this.boardType = boardType;
        this.name = name;
        this.link = link;
        this.iconId = iconId;
        this.workSafe = workSafe;
        this.classic = classic;
        this.textOnly = textOnly;
    }

    public static boolean boardNeedsRefresh(Context context, String boardCode, boolean forceRefresh) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.boardNeedsRefresh(android.content.Context,java.util.String,boolean)",context,boardCode,forceRefresh);try{ChanBoard board = ChanFileStorage.loadBoardData(context, boardCode);
        if (board == null || board.defData)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.boardNeedsRefresh(android.content.Context,java.util.String,boolean)");return true;}}
        else if (board.threads == null || board.threads.length == 0)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.boardNeedsRefresh(android.content.Context,java.util.String,boolean)");return true;}}
        else if (board.threads[0] == null || board.threads[0].defData)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.boardNeedsRefresh(android.content.Context,java.util.String,boolean)");return true;}}
        else if (!board.isCurrent())
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.boardNeedsRefresh(android.content.Context,java.util.String,boolean)");return true;}}
        else if (forceRefresh)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.boardNeedsRefresh(android.content.Context,java.util.String,boolean)");return true;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.boardNeedsRefresh(android.content.Context,java.util.String,boolean)");return false;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.boardNeedsRefresh(android.content.Context,java.util.String,boolean)",throwable);throw throwable;}
    }

    public ChanBoard copy() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.data.ChanBoard com.chanapps.four.data.ChanBoard.copy()",this);try{ChanBoard copy = new ChanBoard(this.boardType, this.name, this.link, this.iconId,
                this.workSafe, this.classic, this.textOnly);
        {com.mijack.Xlog.logMethodExit("com.chanapps.four.data.ChanBoard com.chanapps.four.data.ChanBoard.copy()",this);return copy;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.data.ChanBoard com.chanapps.four.data.ChanBoard.copy()",this,throwable);throw throwable;}
    }

    public String toString() {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanBoard.toString()",this);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanBoard.toString()",this);return "Board " + link + " page: " + no + ", stickyPosts: " + stickyPosts.length
                + ", threads: " + threads.length + ", newThreads: " + loadedThreads.length;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBoard.toString()",this,throwable);throw throwable;}
    }

    public static List<ChanBoard> getBoards(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.List com.chanapps.four.data.ChanBoard.getBoards(android.content.Context)",context);try{initBoards(context);
		{com.mijack.Xlog.logStaticMethodExit("java.util.List com.chanapps.four.data.ChanBoard.getBoards(android.content.Context)");return new ArrayList<ChanBoard>(boards);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.List com.chanapps.four.data.ChanBoard.getBoards(android.content.Context)",throwable);throw throwable;}
	}

    public static List<ChanBoard> getBoardsRespectingNSFW(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.List com.chanapps.four.data.ChanBoard.getBoardsRespectingNSFW(android.content.Context)",context);try{initBoards(context);
        {com.mijack.Xlog.logStaticMethodExit("java.util.List com.chanapps.four.data.ChanBoard.getBoardsRespectingNSFW(android.content.Context)");return new ArrayList<ChanBoard>(showNSFW(context) ? boards : safeBoards);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.List com.chanapps.four.data.ChanBoard.getBoardsRespectingNSFW(android.content.Context)",throwable);throw throwable;}
    }

    public static List<ChanBoard> getNewThreadBoardsRespectingNSFW(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.List com.chanapps.four.data.ChanBoard.getNewThreadBoardsRespectingNSFW(android.content.Context)",context);try{initBoards(context);
        List<ChanBoard> source = new ArrayList<ChanBoard>(showNSFW(context) ? boards : safeBoards);
        List<ChanBoard> filtered = new ArrayList<ChanBoard>();
        for (ChanBoard b : source)
            {if (!b.isVirtualBoard())
                {filtered.add(b);}}
        {com.mijack.Xlog.logStaticMethodExit("java.util.List com.chanapps.four.data.ChanBoard.getNewThreadBoardsRespectingNSFW(android.content.Context)");return filtered;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.List com.chanapps.four.data.ChanBoard.getNewThreadBoardsRespectingNSFW(android.content.Context)",throwable);throw throwable;}
    }

    public static List<ChanBoard> getPickFavoritesBoardsRespectingNSFW(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.List com.chanapps.four.data.ChanBoard.getPickFavoritesBoardsRespectingNSFW(android.content.Context)",context);try{List<ChanBoard> source = getNewThreadBoardsRespectingNSFW(context);
        List<ChanBoard> filtered = new ArrayList<ChanBoard>();
        ChanBoard board = ChanFileStorage.loadBoardData(context, FAVORITES_BOARD_CODE);
        if (board == null || board.defData || board.threads == null)
            {{com.mijack.Xlog.logStaticMethodExit("java.util.List com.chanapps.four.data.ChanBoard.getPickFavoritesBoardsRespectingNSFW(android.content.Context)");return source;}}
        ChanPost[] threads = board.threads;
        Set<String> boardCodes = new HashSet<String>(threads.length);
        for (ChanPost thread : threads)
            {boardCodes.add(thread.board);}
        for (ChanBoard b : source)
            {if (!boardCodes.contains(b.link))
                {filtered.add(b);}}
        {com.mijack.Xlog.logStaticMethodExit("java.util.List com.chanapps.four.data.ChanBoard.getPickFavoritesBoardsRespectingNSFW(android.content.Context)");return filtered;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.List com.chanapps.four.data.ChanBoard.getPickFavoritesBoardsRespectingNSFW(android.content.Context)",throwable);throw throwable;}
    }

    public static boolean showNSFW(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.showNSFW(android.content.Context)",context);try{SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.showNSFW(android.content.Context)");return prefs.getBoolean(SettingsActivity.PREF_SHOW_NSFW_BOARDS, false);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.showNSFW(android.content.Context)",throwable);throw throwable;}
    }

    public static List<ChanBoard> getBoardsByType(Context context, BoardType boardType) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.List com.chanapps.four.data.ChanBoard.getBoardsByType(android.content.Context,java.util.BoardType)",context,boardType);try{initBoards(context);
        /*//boolean showNSFW = showNSFW(context);*/
        /*//if (BoardType.ALL_BOARDS == boardType && showNSFW)*/
        /*//    return new ArrayList<ChanBoard>(boards);*/
        /*//else if (BoardType.ALL_BOARDS == boardType && !showNSFW)*/
        /*//    return new ArrayList<ChanBoard>(safeBoards);*/
        /*//else*/
            {com.mijack.Xlog.logStaticMethodExit("java.util.List com.chanapps.four.data.ChanBoard.getBoardsByType(android.content.Context,java.util.BoardType)");return new ArrayList<ChanBoard>(boardsByType.get(boardType));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.List com.chanapps.four.data.ChanBoard.getBoardsByType(android.content.Context,java.util.BoardType)",throwable);throw throwable;}
	}

	public static ChanBoard getBoardByCode(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.data.ChanBoard com.chanapps.four.data.ChanBoard.getBoardByCode(android.content.Context,java.util.String)",context,boardCode);try{initBoards(context);
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.data.ChanBoard com.chanapps.four.data.ChanBoard.getBoardByCode(android.content.Context,java.util.String)");return boardByCode.get(boardCode);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.data.ChanBoard com.chanapps.four.data.ChanBoard.getBoardByCode(android.content.Context,java.util.String)",throwable);throw throwable;}
	}

    public static boolean isWorksafe(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.isWorksafe(android.content.Context,java.util.String)",context,boardCode);try{initBoards(context);
        ChanBoard board = getBoardByCode(context, boardCode);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isWorksafe(android.content.Context,java.util.String)");return safeBoards.contains(board);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isWorksafe(android.content.Context,java.util.String)",throwable);throw throwable;}
    }

    public static synchronized void initBoards(Context ctx) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanBoard.initBoards(android.content.Context)",ctx);try{if (boards != null && boards.size() > 0) {
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBoard.initBoards(android.content.Context)");return;}
        }

        if (DEBUG) {Log.i(TAG, "Initializing boards");}
        boards = new ArrayList<ChanBoard>();
        safeBoards = new ArrayList<ChanBoard>();
        boardsByType = new HashMap<BoardType, List<ChanBoard>>();
        boardByCode = new HashMap<String, ChanBoard>();
        relatedBoards = new HashMap<String, List<ChanBoard>>();

        String[][] boardCodesByType = BoardInitializer.initBoardCodes(ctx);

        for (String[] boardCodesForType : boardCodesByType) {
            BoardType boardType = BoardType.valueOf(boardCodesForType[0]);
            List<ChanBoard> boardsForType = new ArrayList<ChanBoard>();
            for (int i = 1; i < boardCodesForType.length; i+=2) {
                String boardCode = boardCodesForType[i];
                String boardName = boardCodesForType[i+1];
                boolean workSafe = !(boardType == BoardType.ADULT || boardType == BoardType.MISC);
                int iconId = getImageResourceId(boardCode, 0, 0);
                ChanBoard b = new ChanBoard(boardType, boardName, boardCode, iconId, workSafe, true, false);
                if (DEBUG) {Log.i(TAG, "Added board /" + boardCode + "/ " + boardName);}
                boardsForType.add(b);
                if (!boardByCode.containsKey(b.link)) {
                    boards.add(b);
                    if (workSafe)
                        {safeBoards.add(b);}
                    boardByCode.put(boardCode, b);
                }
            }
            boardsByType.put(boardType, boardsForType);
            if (DEBUG) {Log.i(TAG, "Put boardsByType(" + boardType.boardCode() + ") as " + Arrays.toString(boardsForType.toArray()));}
        }


        final AlphanumComparator comparator = new AlphanumComparator();
        /*//Soft all boards*/
        Collections.sort(boards, new Comparator<ChanBoard>() {
            @Override
            public int compare(ChanBoard lhs, ChanBoard rhs)
            {
                com.mijack.Xlog.logMethodEnter("int com.chanapps.four.data.ChanBoard$1.compare(com.chanapps.four.data.ChanBoard,com.chanapps.four.data.ChanBoard)",this,lhs,rhs);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanBoard$1.compare(com.chanapps.four.data.ChanBoard,com.chanapps.four.data.ChanBoard)",this);{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBoard.initBoards(android.content.Context)");return comparator.compare(lhs.link, rhs.link);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.data.ChanBoard$1.compare(com.chanapps.four.data.ChanBoard,com.chanapps.four.data.ChanBoard)",this,throwable);throw throwable;}
            }
        });

        /*//Sort safe board*/
        Collections.sort(safeBoards, new Comparator<ChanBoard>()
        {
            @Override
            public int compare(ChanBoard lhs, ChanBoard rhs)
            {
                com.mijack.Xlog.logMethodEnter("int com.chanapps.four.data.ChanBoard$2.compare(com.chanapps.four.data.ChanBoard,com.chanapps.four.data.ChanBoard)",this,lhs,rhs);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanBoard$2.compare(com.chanapps.four.data.ChanBoard,com.chanapps.four.data.ChanBoard)",this);{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBoard.initBoards(android.content.Context)");return comparator.compare(lhs.link, rhs.link);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.data.ChanBoard$2.compare(com.chanapps.four.data.ChanBoard,com.chanapps.four.data.ChanBoard)",this,throwable);throw throwable;}
            }
        });

        /* Commented out unused code
        String[][] relatedBoardCodes = BoardInitializer.initRelatedBoards();
        for (String[] relatedBoardCodeArray : relatedBoardCodes) {
            String boardCode = relatedBoardCodeArray[0];
            List<ChanBoard> relatedBoardList = new ArrayList<ChanBoard>();
            for (int i = 1; i < relatedBoardCodeArray.length; i++) {
                String relatedBoardCode = relatedBoardCodeArray[i];
                ChanBoard relatedBoard = boardByCode.get(relatedBoardCode);
                relatedBoardList.add(relatedBoard);
            }
            //Related Init
            relatedBoards.put(boardCode, relatedBoardList);
            if (DEBUG) Log.i(TAG, "Initialized /" + boardCode + "/ with " + relatedBoardList.size() + " related boards");
        }
        */

        boardDrawables = BoardInitializer.initBoardDrawables();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanBoard.initBoards(android.content.Context)",throwable);throw throwable;}
    }

    public static int imagelessStickyDrawableId(String boardCode, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.data.ChanBoard.imagelessStickyDrawableId(java.util.String,long)",boardCode,threadNo);try{if (boardCode.equals("s") && threadNo == 12370429)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanBoard.imagelessStickyDrawableId(java.util.String,long)");return R.drawable.s_2;}}
        else if (boardCode.equals("s") && threadNo == 9112225)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanBoard.imagelessStickyDrawableId(java.util.String,long)");return R.drawable.s_9112225;}}
        else if (boardCode.equals("gif") && threadNo == 5404329)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanBoard.imagelessStickyDrawableId(java.util.String,long)");return R.drawable.gif_5405329;}}
        else if (boardCode.equals("gif") && threadNo == 5412288)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanBoard.imagelessStickyDrawableId(java.util.String,long)");return R.drawable.gif_5412288;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanBoard.imagelessStickyDrawableId(java.util.String,long)");return 0;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.data.ChanBoard.imagelessStickyDrawableId(java.util.String,long)",throwable);throw throwable;}
    }

    public static String getIndexedImageDrawableUrl(String boardCode, int index) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanBoard.getIndexedImageDrawableUrl(java.util.String,int)",boardCode,index);try{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.getIndexedImageDrawableUrl(java.util.String,int)");return "drawable://" + getImageResourceId(boardCode, 0, index);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBoard.getIndexedImageDrawableUrl(java.util.String,int)",throwable);throw throwable;}
    }

    public static int getRandomImageResourceId(String boardCode, long postNo) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.data.ChanBoard.getRandomImageResourceId(java.util.String,long)",boardCode,postNo);try{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanBoard.getRandomImageResourceId(java.util.String,long)");return ChanBoard.getImageResourceId(boardCode, postNo, (int)(postNo % NUM_DEFAULT_IMAGES_PER_BOARD));}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.data.ChanBoard.getRandomImageResourceId(java.util.String,long)",throwable);throw throwable;}
    }

    protected static final int STUB_IMAGE_ID = R.drawable.stub_image;

    public static int getImageResourceId(String boardCode, long postNo, int index) { com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.data.ChanBoard.getImageResourceId(java.util.String,long,int)",boardCode,postNo,index);try{/*// allows special-casing first (usually sticky) and multiple*/
        int imageId = imagelessStickyDrawableId(boardCode, postNo);
        if (imageId > 0)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanBoard.getImageResourceId(java.util.String,long,int)");return imageId;}}
        int[] imageIds = boardDrawables.get(boardCode);
        if (imageIds == null || imageIds.length == 0)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanBoard.getImageResourceId(java.util.String,long,int)");return STUB_IMAGE_ID;}}
        if (index >= 0 && index < 3)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanBoard.getImageResourceId(java.util.String,long,int)");return imageIds[index];}}
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanBoard.getImageResourceId(java.util.String,long,int)");return imageIds[0];}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.data.ChanBoard.getImageResourceId(java.util.String,long,int)",throwable);throw throwable;}
    }

    public String getDescription(Context context) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanBoard.getDescription(android.content.Context)",this,context);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanBoard.getDescription(android.content.Context)",this);return getDescription(context, link);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBoard.getDescription(android.content.Context)",this,throwable);throw throwable;}
    }

    public static String getDescription(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanBoard.getDescription(android.content.Context,java.util.String)",context,boardCode);try{String stringName = "board_desc_" + boardCode;
        try {
            int id = context.getResources().getIdentifier(stringName, "string", context.getPackageName());
            {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.getDescription(android.content.Context,java.util.String)");return context.getString(id);}
        }
        catch (Exception e) {
            Log.e(TAG, "Couldn't find board description for boardCode=" + boardCode);
            {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.getDescription(android.content.Context,java.util.String)");return "";}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBoard.getDescription(android.content.Context,java.util.String)",throwable);throw throwable;}
    }

    public String getName(Context context) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanBoard.getName(android.content.Context)",this,context);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanBoard.getName(android.content.Context)",this);return getName(context, link);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBoard.getName(android.content.Context)",this,throwable);throw throwable;}
    }

    public static String getName(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanBoard.getName(android.content.Context,java.util.String)",context,boardCode);try{String stringName = "board_" + boardCode;
        try {
            int id = context.getResources().getIdentifier(stringName, "string", context.getPackageName());
            {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.getName(android.content.Context,java.util.String)");return context.getString(id);}
        }
        catch (Exception e) {
            Log.e(TAG, "Couldn't find board description for boardCode=" + boardCode);
            {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.getName(android.content.Context,java.util.String)");return "";}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBoard.getName(android.content.Context,java.util.String)",throwable);throw throwable;}
    }


    public static void preloadUncachedBoards(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanBoard.preloadUncachedBoards(android.content.Context)",context);try{List<ChanBoard> boards = ChanBoard.getBoards(context);
        for (ChanBoard board : boards) {
            if (!board.isMetaBoard() && !ChanFileStorage.isBoardCachedOnDisk(context, board.link)) { /*// if user never visited board before*/
                if (DEBUG) {Log.i(TAG, "Starting load service for uncached board " + board.link);}
                FetchChanDataService.scheduleBoardFetch(context, board.link, false, true);
                break; /*// don't schedule more than one per call to avoid overloading*/
            }
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanBoard.preloadUncachedBoards(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanBoard.preloadUncachedBoards(android.content.Context)",throwable);throw throwable;}
    }

    static private Set<String> spoilerBoardCodes = new HashSet<String>();
    static public boolean hasSpoiler(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.hasSpoiler(java.util.String)",boardCode);try{if (spoilerBoardCodes.isEmpty()) {
            synchronized (spoilerBoardCodes) {
                String[] spoilers = { "a", "m", "u", "v", "vg", "r9k", "co", "jp", "lit", "mlp", "tg", "tv", "vp" };
                for (int i = 0; i < spoilers.length; i++)
                    {spoilerBoardCodes.add(spoilers[i]);}
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.hasSpoiler(java.util.String)");return spoilerBoardCodes.contains(boardCode);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.hasSpoiler(java.util.String)",throwable);throw throwable;}
    }

    static public boolean hasName(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.hasName(java.util.String)",boardCode);try{if (boardCode.equals("b") || boardCode.equals("soc") || boardCode.equals("q"))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.hasName(java.util.String)");return false;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.hasName(java.util.String)");return true;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.hasName(java.util.String)",throwable);throw throwable;}
    }

    static public boolean hasSubject(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.hasSubject(java.util.String)",boardCode);try{if (boardCode.equals("b") || boardCode.equals("soc"))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.hasSubject(java.util.String)");return false;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.hasSubject(java.util.String)");return true;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.hasSubject(java.util.String)",throwable);throw throwable;}
    }

    static public boolean requiresThreadSubject(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.requiresThreadSubject(java.util.String)",boardCode);try{if (boardCode.equals("q"))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.requiresThreadSubject(java.util.String)");return true;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.requiresThreadSubject(java.util.String)");return false;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.requiresThreadSubject(java.util.String)",throwable);throw throwable;}
    }

    static public boolean requiresThreadImage(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.requiresThreadImage(java.util.String)",boardCode);try{if (boardCode.equals("q"))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.requiresThreadImage(java.util.String)");return false;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.requiresThreadImage(java.util.String)");return true;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.requiresThreadImage(java.util.String)",throwable);throw throwable;}
    }

    static public boolean allowsBump(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.allowsBump(java.util.String)",boardCode);try{if (boardCode.equals("q"))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.allowsBump(java.util.String)");return false;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.allowsBump(java.util.String)");return true;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.allowsBump(java.util.String)",throwable);throw throwable;}
    }

    /*
    /i - lots of stuff
    */

    static public final Map<String, Integer> spoilerImageCount = new HashMap<String, Integer>();
    static public final Random spoilerGenerator = new Random();

    static public String spoilerThumbnailUrl(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanBoard.spoilerThumbnailUrl(android.content.Context,java.util.String)",context,boardCode);try{if (spoilerImageCount.isEmpty()) {
            spoilerImageCount.put("m", 4);
            spoilerImageCount.put("co", 5);
            spoilerImageCount.put("tg", 3);
            spoilerImageCount.put("tv", 5);
        }
        int spoilerImages = spoilerImageCount.containsKey(boardCode) ? spoilerImageCount.get(boardCode) : 1;
        if (spoilerImages > 1) {
            int spoilerImageNum = spoilerGenerator.nextInt(spoilerImages) + 1;
            {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.spoilerThumbnailUrl(android.content.Context,java.util.String)");return String.format(
                    URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_SPOILER_NUMBERED_IMAGE_URL_FORMAT), boardCode, spoilerImageNum);}
        }
        else {
            {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.spoilerThumbnailUrl(android.content.Context,java.util.String)");return String.format(
                    URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_SPOILER_IMAGE_URL_FORMAT), boardCode);}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBoard.spoilerThumbnailUrl(android.content.Context,java.util.String)",throwable);throw throwable;}
    }

    static public boolean isAsciiOnlyBoard(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.isAsciiOnlyBoard(java.util.String)",boardCode);try{if (boardCode.equals("q") || boardCode.equals("r9k") || boardCode.equals("news"))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isAsciiOnlyBoard(java.util.String)");return true;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isAsciiOnlyBoard(java.util.String)");return false;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isAsciiOnlyBoard(java.util.String)",throwable);throw throwable;}
    }

    public Object[] makeRow(Context context) { com.mijack.Xlog.logMethodEnter("[java.util.Object com.chanapps.four.data.ChanBoard.makeRow(android.content.Context)",this,context);try{/*// for board selector*/
        {com.mijack.Xlog.logMethodExit("[java.util.Object com.chanapps.four.data.ChanBoard.makeRow(android.content.Context)",this);return makeRow(context, 0);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.util.Object com.chanapps.four.data.ChanBoard.makeRow(android.content.Context)",this,throwable);throw throwable;}
    }

    public Object[] makeRow(Context context, long threadNo) { com.mijack.Xlog.logMethodEnter("[java.util.Object com.chanapps.four.data.ChanBoard.makeRow(android.content.Context,long)",this,context,threadNo);try{/*// for board selector*/
        {com.mijack.Xlog.logMethodExit("[java.util.Object com.chanapps.four.data.ChanBoard.makeRow(android.content.Context,long)",this);return ChanThread.makeBoardRow(context, link, getName(context), getRandomImageResourceId(link, threadNo), 0);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.util.Object com.chanapps.four.data.ChanBoard.makeRow(android.content.Context,long)",this,throwable);throw throwable;}
    }

    public Object[] makeHeaderRow(Context context) { com.mijack.Xlog.logMethodEnter("[java.util.Object com.chanapps.four.data.ChanBoard.makeHeaderRow(android.content.Context)",this,context);try{/*// for board selector*/
        {com.mijack.Xlog.logMethodExit("[java.util.Object com.chanapps.four.data.ChanBoard.makeHeaderRow(android.content.Context)",this);return ChanThread.makeHeaderRow(context, this);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.util.Object com.chanapps.four.data.ChanBoard.makeHeaderRow(android.content.Context)",this,throwable);throw throwable;}
    }

    private int findThreadPos(long threadNo) {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.data.ChanBoard.findThreadPos(long)",this,threadNo);try{/*// find position of thread in list*/
        int threadPos = -1;
        for (int i = 0; i < threads.length; i++) {
            ChanPost thread = threads[i];
            if (thread != null && thread.no == threadNo) {
                threadPos = i;
                break;
            }
        }
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanBoard.findThreadPos(long)",this);return threadPos;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.data.ChanBoard.findThreadPos(long)",this,throwable);throw throwable;}
    }

    public void updateCountersAfterLoad(Context context) {
    	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.ChanBoard.updateCountersAfterLoad(android.content.Context)",this,context);try{if (loadedThreads.length == 0) {
    		{com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanBoard.updateCountersAfterLoad(android.content.Context)",this);return;}
    	}
    	Map<Long, ChanPost> currentThreads = new HashMap<Long, ChanPost>();
    	for (ChanPost thread : threads) {
    		currentThreads.put(thread.no, thread);
    	}
    	this.newThreads = 0;
    	this.updatedThreads = 0;
        ChanThread firstNewThread = null;
    	for (ChanThread newThread : loadedThreads) {
    		if (currentThreads.containsKey(newThread.no)) {
    			ChanPost currentPost = currentThreads.get(newThread.no);
    			if (currentPost.replies != newThread.replies) {
    				updatedThreads++;
    			}
    		} else {
                if (firstNewThread == null)
                    {firstNewThread = newThread;}
    			newThreads++;
    		}
    	}
        /*//if (newThreads > 0 && isFavoriteBoard(context, link))*/
        /*//    NotificationComponent.notifyNewThreads(context, link, newThreads, firstNewThread);*/
        if (DEBUG) {Log.i(TAG, "Updated board " + name + ", " + newThreads + " new threads, " + updatedThreads + " updated threads.");}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.ChanBoard.updateCountersAfterLoad(android.content.Context)",this,throwable);throw throwable;}
    }

    public boolean isVirtualBoard() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanBoard.isVirtualBoard()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.isVirtualBoard()",this);return isVirtualBoard(link);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isVirtualBoard()",this,throwable);throw throwable;}
    }

    public static boolean isVirtualBoard(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.isVirtualBoard(java.util.String)",boardCode);try{for (String virtualBoardCode : VIRTUAL_BOARDS)
            {if (virtualBoardCode.equals(boardCode))
                {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isVirtualBoard(java.util.String)");return true;}}}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isVirtualBoard(java.util.String)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isVirtualBoard(java.util.String)",throwable);throw throwable;}
    }

    public static boolean isTopBoard(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.isTopBoard(java.util.String)",boardCode);try{for (String code : TOP_BOARDS)
            {if (code.equals(boardCode))
                {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isTopBoard(java.util.String)");return true;}}}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isTopBoard(java.util.String)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isTopBoard(java.util.String)",throwable);throw throwable;}
    }

    public boolean isTopBoard() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanBoard.isTopBoard()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.isTopBoard()",this);return isTopBoard(link);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isTopBoard()",this,throwable);throw throwable;}
    }

    public boolean isMetaBoard() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanBoard.isMetaBoard()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.isMetaBoard()",this);return isMetaBoard(link);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isMetaBoard()",this,throwable);throw throwable;}
    }

    public static boolean isMetaBoard(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.isMetaBoard(java.util.String)",boardCode);try{for (String metaBoardCode : META_BOARDS)
            {if (metaBoardCode.equals(boardCode))
                {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isMetaBoard(java.util.String)");return true;}}}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isMetaBoard(java.util.String)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isMetaBoard(java.util.String)",throwable);throw throwable;}
    }

    public boolean isPopularBoard() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanBoard.isPopularBoard()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.isPopularBoard()",this);return isPopularBoard(link);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isPopularBoard()",this,throwable);throw throwable;}
    }

    public static boolean isPopularBoard(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.isPopularBoard(java.util.String)",boardCode);try{for (String popularBoardCode : POPULAR_BOARDS)
            {if (popularBoardCode.equals(boardCode))
                {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isPopularBoard(java.util.String)");return true;}}}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isPopularBoard(java.util.String)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isPopularBoard(java.util.String)",throwable);throw throwable;}
    }

    private static final String[] fastBoards = { "a", "b", "v", "vr" };
    private static final Set<String> fastBoardSet = new HashSet<String>(Arrays.asList(fastBoards));

    public boolean isFastBoard() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanBoard.isFastBoard()",this);try{if (link == null)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.isFastBoard()",this);return false;}}
        if (fastBoardSet.contains(link))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.isFastBoard()",this);return true;}}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.isFastBoard()",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isFastBoard()",this,throwable);throw throwable;}
    }

    public static String getBestWidgetImageUrl(Context context, ChanPost thread, String backupBoardCode, int i) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanBoard.getBestWidgetImageUrl(android.content.Context,java.util.ChanPost,java.util.String,int)",context,thread,backupBoardCode,i);try{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.getBestWidgetImageUrl(android.content.Context,java.util.ChanPost,java.util.String,int)");return (thread != null && thread.tim > 0)
                ? thread.thumbnailUrl(context)
                : ChanBoard.getIndexedImageDrawableUrl(
                thread != null ? thread.board : backupBoardCode,
                i);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBoard.getBestWidgetImageUrl(android.content.Context,java.util.ChanPost,java.util.String,int)",throwable);throw throwable;}
    }
    /*
    public List<ChanBoard> relatedBoards(Context context) {
        return relatedBoards(context, 0);
    }
    */
    public List<ChanBoard> relatedBoards(Context context, long threadNo) {
        com.mijack.Xlog.logMethodEnter("java.util.List com.chanapps.four.data.ChanBoard.relatedBoards(android.content.Context,long)",this,context,threadNo);try{initBoards(context);
        if (isVirtualBoard())
            {{com.mijack.Xlog.logMethodExit("java.util.List com.chanapps.four.data.ChanBoard.relatedBoards(android.content.Context,long)",this);return new ArrayList<ChanBoard>();}}

        List<ChanBoard> boards = relatedBoards.get(link);
        if (DEBUG) {Log.i(TAG, "Found " + (boards == null ? 0 : boards.size()) + " related boards for /" + link + "/");}
        if (boards == null)
            {{com.mijack.Xlog.logMethodExit("java.util.List com.chanapps.four.data.ChanBoard.relatedBoards(android.content.Context,long)",this);return new ArrayList<ChanBoard>();}}

        boolean showAdult = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(SettingsActivity.PREF_SHOW_NSFW_BOARDS, false);
        List<ChanBoard> filteredBoards = new ArrayList<ChanBoard>();
        for (ChanBoard board : boards) {
            if (board != null && (board.workSafe || showAdult))
                {filteredBoards.add(board);}
        }

        if (threadNo <= 0)
            {Collections.shuffle(filteredBoards);}
        else
            {Collections.rotate(filteredBoards, (int)threadNo);} /*// preserve order*/
        List<ChanBoard> boardList = new ArrayList<ChanBoard>(NUM_RELATED_BOARDS);
        int j = 0;
        for (ChanBoard relatedBoard : filteredBoards) {
            if (j >= NUM_RELATED_BOARDS)
                {break;}
            if (!link.equals(relatedBoard.link)) {
                boardList.add(relatedBoard);
                j++;
            }
        }
        {com.mijack.Xlog.logMethodExit("java.util.List com.chanapps.four.data.ChanBoard.relatedBoards(android.content.Context,long)",this);return boardList;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.List com.chanapps.four.data.ChanBoard.relatedBoards(android.content.Context,long)",this,throwable);throw throwable;}
    }

    public static boolean boardHasData(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.boardHasData(android.content.Context,java.util.String)",context,boardCode);try{ChanBoard board = ChanFileStorage.loadBoardData(context, boardCode);
        boolean hasData = board != null && board.hasData();
        if (DEBUG) {Log.i(TAG, "boardHasData() /" + boardCode + "/ hasData=" + hasData + " board=" + board);}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.boardHasData(android.content.Context,java.util.String)");return hasData;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.boardHasData(android.content.Context,java.util.String)",throwable);throw throwable;}
    }

    public boolean hasData() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanBoard.hasData()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.hasData()",this);return !defData
                && threads != null
                && threads.length > 0
                && threads[0] != null
                && !threads[0].defData;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.hasData()",this,throwable);throw throwable;}
    }

    public boolean hasNewBoardData() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanBoard.hasNewBoardData()",this);try{if (defData)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.hasNewBoardData()",this);return false;}}
        if (newThreads > 0)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.hasNewBoardData()",this);return true;}}
        if (updatedThreads > 0)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.hasNewBoardData()",this);return true;}}
        if (loadedThreads != null && loadedThreads.length > 0)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.hasNewBoardData()",this);return true;}}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.hasNewBoardData()",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.hasNewBoardData()",this,throwable);throw throwable;}
    }

    protected final int MAX_THREADS_BEFORE_SWAP = 20;

    public boolean shouldSwapThreads() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanBoard.shouldSwapThreads()",this);try{if (loadedThreads == null || loadedThreads.length == 0)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.shouldSwapThreads()",this);return false;}}
        if (threads == null || threads.length == 0)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.shouldSwapThreads()",this);return true;}}
        if (threads[0] == null || threads[0].defData || threads[0].no <= 0)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.shouldSwapThreads()",this);return true;}}
        if (threads.length > MAX_THREADS_BEFORE_SWAP)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.shouldSwapThreads()",this);return true;}}
        if (!isSwapCurrent())
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.shouldSwapThreads()",this);return true;}}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.shouldSwapThreads()",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.shouldSwapThreads()",this,throwable);throw throwable;}
    }

    private boolean isSwapCurrent() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanBoard.isSwapCurrent()",this);try{long diff = Math.abs(new Date().getTime() - lastSwapped);
        boolean swapCurrent;
        if (lastSwapped <= 0)
            {swapCurrent = false;}
        else if (diff > SWAP_DELAY_MS)
            {swapCurrent = false;}
        else
            {swapCurrent = true;}
        if (DEBUG) {Log.i(TAG, "isSwapCurrent /" + link + "/ lastSwapped=" + lastSwapped + " diff=" + diff + " return=" + swapCurrent);}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.isSwapCurrent()",this);return swapCurrent;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isSwapCurrent()",this,throwable);throw throwable;}
    }

    public void swapLoadedThreads() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.ChanBoard.swapLoadedThreads()",this);try{boolean hasNew = hasNewBoardData();
        if (DEBUG) {Log.i(TAG, "swapLoadedThreads() hasNew=" + hasNew);}
        if (hasNew) {
            synchronized (this) {
                threads = loadedThreads;
                loadedThreads = new ChanThread[0];
                newThreads = 0;
                updatedThreads = 0;
                lastSwapped = (new Date()).getTime();
            }
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanBoard.swapLoadedThreads()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.ChanBoard.swapLoadedThreads()",this,throwable);throw throwable;}
    }

    public static boolean isFavoriteBoard(final Context context, final String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.isFavoriteBoard(android.content.Context,java.util.String)",context,boardCode);try{ChanBoard favorites = ChanFileStorage.loadBoardData(context, ChanBoard.FAVORITES_BOARD_CODE);
        if (favorites == null || !favorites.hasData())
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isFavoriteBoard(android.content.Context,java.util.String)");return false;}}
        for (ChanThread thread : favorites.threads) {
            if (boardCode.equals(thread.board))
                {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isFavoriteBoard(android.content.Context,java.util.String)");return true;}}
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isFavoriteBoard(android.content.Context,java.util.String)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isFavoriteBoard(android.content.Context,java.util.String)",throwable);throw throwable;}
    }

    public static String boardUrl(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanBoard.boardUrl(android.content.Context,java.util.String)",context,boardCode);try{if (boardCode == null || boardCode.isEmpty() || isVirtualBoard(boardCode))
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.boardUrl(android.content.Context,java.util.String)");return URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_FRONTPAGE_URL);}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.boardUrl(android.content.Context,java.util.String)");return String.format(URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_WEB_BOARD_URL_FORMAT), boardCode);}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBoard.boardUrl(android.content.Context,java.util.String)",throwable);throw throwable;}
    }

    public int getThreadIndex(String boardCode, long threadNo) {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.data.ChanBoard.getThreadIndex(java.util.String,long)",this,boardCode,threadNo);try{if (DEBUG) {Log.i(TAG, "getThreadIndex /" + boardCode + "/" + threadNo);}
        if (defData)
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanBoard.getThreadIndex(java.util.String,long)",this);return -1;}}
        if (threads == null)
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanBoard.getThreadIndex(java.util.String,long)",this);return -1;}}
        int index = -1;
        ChanPost thread;
        for (int i = 0; i < threads.length; i++) {
            if ((thread = threads[i]) == null)
                {continue;}
            if (thread.board == null)
                {continue;}
            if (!thread.board.equals(boardCode))
                {continue;}
            if (thread.no != threadNo)
                {continue;}
            index = i;
            break;
        }
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanBoard.getThreadIndex(java.util.String,long)",this);return index;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.data.ChanBoard.getThreadIndex(java.util.String,long)",this,throwable);throw throwable;}
    }

    public boolean isCurrent() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanBoard.isCurrent()",this);try{FetchParams params = NetworkProfileManager.instance().getCurrentProfile().getFetchParams();
        long now = new Date().getTime();
        long interval = Math.abs(now - lastFetched);
        boolean current;
        if (lastFetched <= 0)
            {current = false;}
        else if (interval > params.refreshDelay)
            {current = false;}
        else
            {current = true;}
        if (DEBUG) {Log.i(TAG, "isCurrent() /" + link + "/"
                + " lastFetched=" + lastFetched
                + " interval=" + interval
                + " refreshDelay=" + params.refreshDelay
                + " current=" + current
        );}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanBoard.isCurrent()",this);return current;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isCurrent()",this,throwable);throw throwable;}
    }

    protected static final long SWAP_DELAY_MS = 300000L;

    public String refreshMessage() {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanBoard.refreshMessage()",this);try{StringBuffer msg = new StringBuffer();
        if (newThreads > 0) {
            msg.append("" + newThreads + " new thread");
            if (newThreads > 1) /*// + updatedThreads > 1) {*/
                {msg.append("s");}
        }
        else if (updatedThreads > 0) {
            msg.append("" + updatedThreads + " updated thread");
            if (updatedThreads > 1) /*// + updatedThreads > 1) {*/
                {msg.append("s");}
        }
        {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanBoard.refreshMessage()",this);return msg.toString();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBoard.refreshMessage()",this,throwable);throw throwable;}
    }

    public static ChanThread makeFavoritesThread(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.ChanThread com.chanapps.four.data.ChanBoard.makeFavoritesThread(android.content.Context,java.util.String)",context,boardCode);try{ChanBoard board = ChanBoard.getBoardByCode(context, boardCode);
        ChanThread thread = new ChanThread();
        thread.board = boardCode;
        thread.no = 0;
        thread.sub = getName(context, boardCode);
        thread.com = getDescription(context, boardCode);
        {com.mijack.Xlog.logStaticMethodExit("java.util.ChanThread com.chanapps.four.data.ChanBoard.makeFavoritesThread(android.content.Context,java.util.String)");return thread;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.ChanThread com.chanapps.four.data.ChanBoard.makeFavoritesThread(android.content.Context,java.util.String)",throwable);throw throwable;}
    }


    public static boolean hasFavorites(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.hasFavorites(android.content.Context)",context);try{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.hasFavorites(android.content.Context)");return boardHasData(context, ChanBoard.FAVORITES_BOARD_CODE);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.hasFavorites(android.content.Context)",throwable);throw throwable;}
    }

    public static boolean hasWatchlist(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.hasWatchlist(android.content.Context)",context);try{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.hasWatchlist(android.content.Context)");return boardHasData(context, ChanBoard.WATCHLIST_BOARD_CODE);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.hasWatchlist(android.content.Context)",throwable);throw throwable;}
    }

    public static String defaultBoardCode(final Context context) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanBoard.defaultBoardCode(android.content.Context)",context);try{/*//if (hasWatchlist(context))*/
        /*//    return ChanBoard.WATCHLIST_BOARD_CODE;*/
        /*//else*/
        if (hasFavorites(context))
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.defaultBoardCode(android.content.Context)");return ChanBoard.FAVORITES_BOARD_CODE;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanBoard.defaultBoardCode(android.content.Context)");return ChanBoard.ALL_BOARDS_BOARD_CODE;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanBoard.defaultBoardCode(android.content.Context)",throwable);throw throwable;}
    }

    public static boolean isPersistentBoard(final String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanBoard.isPersistentBoard(java.util.String)",boardCode);try{if (WATCHLIST_BOARD_CODE.equals(boardCode))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isPersistentBoard(java.util.String)");return true;}}
        else if (FAVORITES_BOARD_CODE.equals(boardCode))
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isPersistentBoard(java.util.String)");return true;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanBoard.isPersistentBoard(java.util.String)");return false;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanBoard.isPersistentBoard(java.util.String)",throwable);throw throwable;}
    }

}
