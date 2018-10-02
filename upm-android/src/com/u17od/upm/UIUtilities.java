/*
 * Universal Password Manager
 * Copyright (c) 2010-2011 Adrian Smith
 *
 * This file is part of Universal Password Manager.
 *   
 * Universal Password Manager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Universal Password Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Universal Password Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.u17od.upm;

import android.content.Context;
import android.widget.Toast;

public class UIUtilities {

    public static void showToast(Context context, int id) {
        com.mijack.Xlog.logStaticMethodEnter("void com.u17od.upm.UIUtilities.showToast(android.content.Context,int)",context,id);try{showToast(context, id, false);com.mijack.Xlog.logStaticMethodExit("void com.u17od.upm.UIUtilities.showToast(android.content.Context,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.u17od.upm.UIUtilities.showToast(android.content.Context,int)",throwable);throw throwable;}
    }

    public static void showToast(Context context, int id, boolean longToast) {
        com.mijack.Xlog.logStaticMethodEnter("void com.u17od.upm.UIUtilities.showToast(android.content.Context,int,boolean)",context,id,longToast);try{Toast.makeText(context, id, longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();com.mijack.Xlog.logStaticMethodExit("void com.u17od.upm.UIUtilities.showToast(android.content.Context,int,boolean)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.u17od.upm.UIUtilities.showToast(android.content.Context,int,boolean)",throwable);throw throwable;}
    }

    public static void showToast(Context context, String message, boolean longToast) {
        com.mijack.Xlog.logStaticMethodEnter("void com.u17od.upm.UIUtilities.showToast(android.content.Context,java.lang.String,boolean)",context,message,longToast);try{Toast.makeText(context, message, longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();com.mijack.Xlog.logStaticMethodExit("void com.u17od.upm.UIUtilities.showToast(android.content.Context,java.lang.String,boolean)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.u17od.upm.UIUtilities.showToast(android.content.Context,java.lang.String,boolean)",throwable);throw throwable;}
    }

}
