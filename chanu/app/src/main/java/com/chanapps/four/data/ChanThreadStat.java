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
public class ChanThreadStat {
	public String board;
	public long no;
	public int usage;
	public long lastUsage;
	
	public ChanThreadStat() {
		this.usage = 0;
	}
	public ChanThreadStat(String board, long threadNo) {
		this.board = board;
		this.no = threadNo;
		this.usage = 0;
	}
	public long use() {
		com.mijack.Xlog.logMethodEnter("long com.chanapps.four.data.ChanThreadStat.use()",this);try{usage++;
		lastUsage = Calendar.getInstance().getTimeInMillis();
		{com.mijack.Xlog.logMethodExit("long com.chanapps.four.data.ChanThreadStat.use()",this);return lastUsage;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.data.ChanThreadStat.use()",this,throwable);throw throwable;}
	}
	
	public String toString() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.ChanThreadStat.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.ChanThreadStat.toString()",this);return "thread " + board + "/" + no + " used " + usage + " last " + new Date(lastUsage);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.ChanThreadStat.toString()",this,throwable);throw throwable;}
	}
}
