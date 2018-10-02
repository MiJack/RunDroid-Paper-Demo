/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//httpclient/src/java/org/apache/commons/httpclient/methods/multipart/StringPart.java,v 1.11 2004/04/18 23:51:37 jsdever Exp $
 * $Revision: 480424 $
 * $Date: 2006-11-29 06:56:49 +0100 (Wed, 29 Nov 2006) $
 *
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package com.chanapps.four.multipartmime;

import java.io.OutputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;
import android.util.Log;

/**
 * Simple string parameter for a multipart post
 *
 * @author <a href="mailto:mattalbright@yahoo.com">Matthew Albright</a>
 * @author <a href="mailto:jsdever@apache.org">Jeff Dever</a>
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 *
 * @since 2.0
 */
public class StringPart extends PartBase {

    /** Log object for this class. */
    public static final String TAG = StringPart.class.getSimpleName();
    private static final boolean DEBUG = false;

    /** Default content encoding of string parameters. */
    public static final String DEFAULT_CONTENT_TYPE = "text/plain";

    /** Default transfer encoding of string parameters*/
    public static final String DEFAULT_TRANSFER_ENCODING = "8bit";

    /** Contents of this StringPart. */
    private byte[] content;
    
    /** The String value of this part. */
    private String value;

    public String getValue() { com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.multipartmime.StringPart.getValue()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.multipartmime.StringPart.getValue()",this);return value;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.multipartmime.StringPart.getValue()",this,throwable);throw throwable;} }
    /**
     * Constructor.
     *
     * @param name The name of the part
     * @param value the string to post
     * @param charset the charset to be used to encode the string, if <code>null</code> 
     */
    public StringPart(String name, String value, String charset) {
        
        super(
            name,
            DEFAULT_CONTENT_TYPE,
            charset == null ? DEFAULT_CHARSET : charset,
            DEFAULT_TRANSFER_ENCODING
        );
        if (value == null) {
            throw new IllegalArgumentException("Value may not be null");
        }
        if (value.indexOf(0) != -1) {
            /*// See RFC 2048, 2.8. "8bit Data"*/
            throw new IllegalArgumentException("NULs may not be present in string parts");
        }
        this.value = value;
    }

    /**
     * Constructor.
     *
     * @param name The name of the part
     * @param value the string to post
     */
    public StringPart(String name, String value) {
        this(name, value, null);
    }

    /**
     * Gets the content in bytes.  Bytes are lazily created to allow the charset to be changed
     * after the part is created.
     * 
     * @return the content in bytes
     */
    private byte[] getContent() {
        com.mijack.Xlog.logMethodEnter("[byte com.chanapps.four.multipartmime.StringPart.getContent()",this);try{if (content == null) {
            content = EncodingUtils.getBytes(value, getCharSet());
        }
        {com.mijack.Xlog.logMethodExit("[byte com.chanapps.four.multipartmime.StringPart.getContent()",this);return content;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[byte com.chanapps.four.multipartmime.StringPart.getContent()",this,throwable);throw throwable;}
    }
    
    /**
     * Writes the data to the given OutputStream.
     * @param out the OutputStream to write to
     * @throws IOException if there is a write error
     */
    @Override
    protected void sendData(OutputStream out) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.multipartmime.StringPart.sendData(java.io.OutputStream)",this,out);try{if (DEBUG) {Log.d(TAG, "enter sendData(OutputStream)");}
        out.write(getContent());com.mijack.Xlog.logMethodExit("void com.chanapps.four.multipartmime.StringPart.sendData(java.io.OutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.multipartmime.StringPart.sendData(java.io.OutputStream)",this,throwable);throw throwable;}
    }
    
    /**
     * Return the length of the data.
     * @return The length of the data.
     * @see Part#lengthOfData()
     */
    @Override
    protected long lengthOfData() {
        com.mijack.Xlog.logMethodEnter("long com.chanapps.four.multipartmime.StringPart.lengthOfData()",this);try{if (DEBUG) {Log.d(TAG, "enter lengthOfData()");}
        {com.mijack.Xlog.logMethodExit("long com.chanapps.four.multipartmime.StringPart.lengthOfData()",this);return getContent().length;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.multipartmime.StringPart.lengthOfData()",this,throwable);throw throwable;}
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.httpclient.methods.multipart.BasePart#setCharSet(java.lang.String)
     */
    @Override
    public void setCharSet(String charSet) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.multipartmime.StringPart.setCharSet(java.lang.String)",this,charSet);try{super.setCharSet(charSet);
        this.content = null;com.mijack.Xlog.logMethodExit("void com.chanapps.four.multipartmime.StringPart.setCharSet(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.multipartmime.StringPart.setCharSet(java.lang.String)",this,throwable);throw throwable;}
    }

}
