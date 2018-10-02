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
package com.u17od.upm.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;


public class Util {

    /**
     * Left pad an integer to a given length with the given
     * character 
     * @param i The integer to pad
     * @param length The length to pad it to
     * @param c The character to do the padding with
     * @return A padded version of the integer
     */
    public static String lpad(int i, int length, char c) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.u17od.upm.util.Util.lpad(int,int,char)",i,length,c);try{StringBuffer buf = new StringBuffer(String.valueOf(i));
        while (buf.length() < length) {
            buf.insert(0, c);
        }
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.u17od.upm.util.Util.lpad(int,int,char)");return buf.toString();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.u17od.upm.util.Util.lpad(int,int,char)",throwable);throw throwable;}
    }


    public static byte[] getBytesFromFile(File file) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("[byte com.u17od.upm.util.Util.getBytesFromFile(java.io.File)",file);try{com.mijack.Xlog.logStaticMethodExit("[byte com.u17od.upm.util.Util.getBytesFromFile(java.io.File)");return getBytesFromFile(file, file.length());}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[byte com.u17od.upm.util.Util.getBytesFromFile(java.io.File)",throwable);throw throwable;}
    }

    public static byte[] getBytesFromFile(File file, long numBytesToRead) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("[byte com.u17od.upm.util.Util.getBytesFromFile(java.io.File,long)",file,numBytesToRead);try{InputStream is = new FileInputStream(file);
    
        /*// Create the byte array to hold the data*/
        byte[] bytes = new byte[(int) numBytesToRead];
    
        /*// Read in the bytes*/
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        /*// Ensure all the bytes have been read in*/
        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();

        {com.mijack.Xlog.logStaticMethodExit("[byte com.u17od.upm.util.Util.getBytesFromFile(java.io.File,long)");return bytes;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[byte com.u17od.upm.util.Util.getBytesFromFile(java.io.File,long)",throwable);throw throwable;}
    }

    public static Charset defaultCharset() {
        com.mijack.Xlog.logStaticMethodEnter("java.nio.charset.Charset com.u17od.upm.util.Util.defaultCharset()");try{com.mijack.Xlog.logStaticMethodExit("java.nio.charset.Charset com.u17od.upm.util.Util.defaultCharset()");return Charset.forName(
                new OutputStreamWriter(
                        new ByteArrayOutputStream()).getEncoding());}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.nio.charset.Charset com.u17od.upm.util.Util.defaultCharset()",throwable);throw throwable;}
    }

}
