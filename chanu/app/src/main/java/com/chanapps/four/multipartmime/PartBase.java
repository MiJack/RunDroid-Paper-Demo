/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//httpclient/src/java/org/apache/commons/httpclient/methods/multipart/PartBase.java,v 1.5 2004/04/18 23:51:37 jsdever Exp $
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


/**
 * Provides setters and getters for the basic Part properties.
 * 
 * @author Michael Becke
 */
public abstract class PartBase extends Part {

    /** Default charset of string parameters*/
    public static final String ASCII_CHARSET = "US-ASCII";
    public static final String UTF8_CHARSET = "UTF-8";
    public static final String DEFAULT_CHARSET = UTF8_CHARSET;
    /** Name of the file part. */
    private String name;
        
    /** Content type of the file part. */
    private String contentType;

    /** Content encoding of the file part. */
    private String charSet;
    
    /** The transfer encoding. */
    private String transferEncoding;

    /**
     * Constructor.
     * 
     * @param name The name of the part
     * @param contentType The content type, or <code>null</code>
     * @param charSet The character encoding, or <code>null</code> 
     * @param transferEncoding The transfer encoding, or <code>null</code>
     */
    public PartBase(String name, String contentType, String charSet, String transferEncoding) {

        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        this.name = name;
        this.contentType = contentType;
        this.charSet = charSet;
        this.transferEncoding = transferEncoding;
    }

    /**
     * Returns the name.
     * @return The name.
     * @see Part#getName()
     */
    @Override
    public String getName() { 
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.multipartmime.PartBase.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.multipartmime.PartBase.getName()",this);return this.name;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.multipartmime.PartBase.getName()",this,throwable);throw throwable;} 
    }

    /**
     * Returns the content type of this part.
     * @return String The name.
     */
    @Override
    public String getContentType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.multipartmime.PartBase.getContentType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.multipartmime.PartBase.getContentType()",this);return this.contentType;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.multipartmime.PartBase.getContentType()",this,throwable);throw throwable;}
    }

    /**
     * Return the character encoding of this part.
     * @return String The name.
     */
    @Override
    public String getCharSet() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.multipartmime.PartBase.getCharSet()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.multipartmime.PartBase.getCharSet()",this);return this.charSet;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.multipartmime.PartBase.getCharSet()",this,throwable);throw throwable;}
    }

    /**
     * Returns the transfer encoding of this part.
     * @return String The name.
     */
    @Override
    public String getTransferEncoding() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.multipartmime.PartBase.getTransferEncoding()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.multipartmime.PartBase.getTransferEncoding()",this);return transferEncoding;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.multipartmime.PartBase.getTransferEncoding()",this,throwable);throw throwable;}
    }

    /**
     * Sets the character encoding.
     * 
     * @param charSet the character encoding, or <code>null</code> to exclude the character 
     * encoding header
     */
    public void setCharSet(String charSet) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.multipartmime.PartBase.setCharSet(java.lang.String)",this,charSet);try{this.charSet = charSet;com.mijack.Xlog.logMethodExit("void com.chanapps.four.multipartmime.PartBase.setCharSet(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.multipartmime.PartBase.setCharSet(java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Sets the content type.
     * 
     * @param contentType the content type, or <code>null</code> to exclude the content type header
     */
    public void setContentType(String contentType) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.multipartmime.PartBase.setContentType(java.lang.String)",this,contentType);try{this.contentType = contentType;com.mijack.Xlog.logMethodExit("void com.chanapps.four.multipartmime.PartBase.setContentType(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.multipartmime.PartBase.setContentType(java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Sets the part name.
     * 
     * @param name
     */
    public void setName(String name) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.multipartmime.PartBase.setName(java.lang.String)",this,name);try{if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        this.name = name;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.multipartmime.PartBase.setName(java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Sets the transfer encoding.
     * 
     * @param transferEncoding the transfer encoding, or <code>null</code> to exclude the 
     * transfer encoding header
     */
    public void setTransferEncoding(String transferEncoding) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.multipartmime.PartBase.setTransferEncoding(java.lang.String)",this,transferEncoding);try{this.transferEncoding = transferEncoding;com.mijack.Xlog.logMethodExit("void com.chanapps.four.multipartmime.PartBase.setTransferEncoding(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.multipartmime.PartBase.setTransferEncoding(java.lang.String)",this,throwable);throw throwable;}
    }

}
