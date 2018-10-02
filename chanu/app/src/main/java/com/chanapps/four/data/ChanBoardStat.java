/**
 * 
 */
package com.chanapps.four.data;

import java.util.Calendar;
import java.util.Date;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class ChanBoardStat {
	public String board;
	public int usage;
	public long lastUsage;
	
	public ChanBoardStat() {
		this.usage = 0;
	}
	public ChanBoardStat(String board) {
		this.board = board;
		this.usage = 0;
	}
	public long use() {
		com.mijack.Xlog.logMethodEnter("long com.chanapps.four.data.ChanBoardStat.use()",this);try{usage++;
		lastUsage = Calendar.getInstance().getTimeInMillis();
		{com.mijack.Xlog.logMethodExit("long com.chanapps.four.data.ChanBoardStat.use()",this);return lastUsage;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.data.ChanBoardStat.use()",this,throwable);throw throwable;}
	}
	
	public String toString() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.ChanBoardStat.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.ChanBoardStat.toString()",this);return "board " + board + " used " + usage + " last at " + new Date(lastUsage);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.ChanBoardStat.toString()",this,throwable);throw throwable;}
	}
}
