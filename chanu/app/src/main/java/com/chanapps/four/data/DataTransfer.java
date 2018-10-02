/**
 * 
 */
package com.chanapps.four.data;

import java.util.Date;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class DataTransfer {
	public DataTransfer (int duration, int size) {
		time = new Date();
		this.duration = duration;
		this.size = size;
		this.dataRate = size / duration;
		this.failed = false;
	}
	public DataTransfer() {
		time = new Date();
		failed = true;
	}
	public Date time;
	/** Fetch time in ms */
	public int duration;
	/** Fetched data size in bytes */
	public int size;
	/** Download rate B/ms, same as kB/s */
	public double dataRate;
	public boolean failed;
	
	public String toString() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.DataTransfer.toString()",this);try{if (failed) {
			{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.DataTransfer.toString()",this);return "failed at " + time;}
		} else {
			{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.DataTransfer.toString()",this);return "size " + size + "b during " + duration + "ms at " + dataRate + "kB/s on " + time;}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.DataTransfer.toString()",this,throwable);throw throwable;}
	}
}
