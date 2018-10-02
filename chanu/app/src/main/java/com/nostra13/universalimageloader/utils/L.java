/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.utils;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.util.Log;

/**
 * "Less-word" analog of Android {@link Log logger}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.6.4
 */
public final class L {

	private static final String LOG_FORMAT = "%1$s\n%2$s";

	private L() {
	}

	public static void d(String message, Object... args) {
		com.mijack.Xlog.logStaticMethodEnter("void com.nostra13.universalimageloader.utils.L.d(java.lang.String,[java.lang.Object)",message,args);try{log(Log.DEBUG, null, message, args);com.mijack.Xlog.logStaticMethodExit("void com.nostra13.universalimageloader.utils.L.d(java.lang.String,[java.lang.Object)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.nostra13.universalimageloader.utils.L.d(java.lang.String,[java.lang.Object)",throwable);throw throwable;}
	}

	public static void i(String message, Object... args) {
		com.mijack.Xlog.logStaticMethodEnter("void com.nostra13.universalimageloader.utils.L.i(java.lang.String,[java.lang.Object)",message,args);try{log(Log.INFO, null, message, args);com.mijack.Xlog.logStaticMethodExit("void com.nostra13.universalimageloader.utils.L.i(java.lang.String,[java.lang.Object)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.nostra13.universalimageloader.utils.L.i(java.lang.String,[java.lang.Object)",throwable);throw throwable;}
	}

	public static void w(String message, Object... args) {
		com.mijack.Xlog.logStaticMethodEnter("void com.nostra13.universalimageloader.utils.L.w(java.lang.String,[java.lang.Object)",message,args);try{log(Log.WARN, null, message, args);com.mijack.Xlog.logStaticMethodExit("void com.nostra13.universalimageloader.utils.L.w(java.lang.String,[java.lang.Object)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.nostra13.universalimageloader.utils.L.w(java.lang.String,[java.lang.Object)",throwable);throw throwable;}
	}

	public static void e(Throwable ex) {
		com.mijack.Xlog.logStaticMethodEnter("void com.nostra13.universalimageloader.utils.L.e(java.lang.Throwable)",ex);try{log(Log.ERROR, ex, null);com.mijack.Xlog.logStaticMethodExit("void com.nostra13.universalimageloader.utils.L.e(java.lang.Throwable)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.nostra13.universalimageloader.utils.L.e(java.lang.Throwable)",throwable);throw throwable;}
	}

	public static void e(String message, Object... args) {
		com.mijack.Xlog.logStaticMethodEnter("void com.nostra13.universalimageloader.utils.L.e(java.lang.String,[java.lang.Object)",message,args);try{log(Log.ERROR, null, message, args);com.mijack.Xlog.logStaticMethodExit("void com.nostra13.universalimageloader.utils.L.e(java.lang.String,[java.lang.Object)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.nostra13.universalimageloader.utils.L.e(java.lang.String,[java.lang.Object)",throwable);throw throwable;}
	}

	public static void e(Throwable ex, String message, Object... args) {
		com.mijack.Xlog.logStaticMethodEnter("void com.nostra13.universalimageloader.utils.L.e(java.lang.Throwable,java.lang.String,[java.lang.Object)",ex,message,args);try{log(Log.ERROR, ex, message, args);com.mijack.Xlog.logStaticMethodExit("void com.nostra13.universalimageloader.utils.L.e(java.lang.Throwable,java.lang.String,[java.lang.Object)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.nostra13.universalimageloader.utils.L.e(java.lang.Throwable,java.lang.String,[java.lang.Object)",throwable);throw throwable;}
	}

	private static void log(int priority, Throwable ex, String message, Object... args) {
		com.mijack.Xlog.logStaticMethodEnter("void com.nostra13.universalimageloader.utils.L.log(int,java.lang.Throwable,java.lang.String,[java.lang.Object)",priority,ex,message,args);try{if (args.length > 0) {
			message = String.format(message, args);
		}

		String log;
		if (ex == null) {
			log = message;
		} else {
			String logMessage = message == null ? ex.getMessage() : message;
			String logBody = Log.getStackTraceString(ex);
			log = String.format(LOG_FORMAT, logMessage, logBody);
		}
		Log.println(priority, ImageLoader.TAG, log);com.mijack.Xlog.logStaticMethodExit("void com.nostra13.universalimageloader.utils.L.log(int,java.lang.Throwable,java.lang.String,[java.lang.Object)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.nostra13.universalimageloader.utils.L.log(int,java.lang.Throwable,java.lang.String,[java.lang.Object)",throwable);throw throwable;}
	}
}