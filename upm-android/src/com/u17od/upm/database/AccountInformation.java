/*
 * Universal Password Manager
 * Copyright (C) 2005-2011 Adrian Smith
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
package com.u17od.upm.database;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;


public class AccountInformation extends FlatPackObject {

    private String accountName;
    private String userId;
    private String password;
    private String url;
    private String notes;


    public AccountInformation() {
        accountName = "";
        userId = "";
        password = "";
        url = "";
        notes = "";
    }
    
    
    public AccountInformation(String accountName, String userId,
            String password, String url, String notes) {
        this.accountName = accountName;
        this.userId = userId;
        this.password = password;
        this.url = url;
        this.notes = notes;
    }


    public AccountInformation(InputStream is) throws IOException, ProblemReadingDatabaseFile {
        assemble(is, Charset.forName("UTF-8"));
    }


    public AccountInformation(InputStream is, Charset charset) throws IOException, ProblemReadingDatabaseFile {
        assemble(is, charset);
    }
    
    
    public void flatPack(OutputStream os) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.AccountInformation.flatPack(java.io.OutputStream)",this,os);try{os.write(flatPack(accountName));
        os.write(flatPack(userId));
        os.write(flatPack(password));
        os.write(flatPack(url));
        os.write(flatPack(notes));com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.AccountInformation.flatPack(java.io.OutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.AccountInformation.flatPack(java.io.OutputStream)",this,throwable);throw throwable;}
    }

    private void assemble(InputStream is, Charset charset) throws IOException, ProblemReadingDatabaseFile {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.AccountInformation.assemble(java.io.InputStream,java.nio.charset.Charset)",this,is,charset);try{accountName = getString(is, charset);
        userId = getString(is, charset);
        password = getString(is, charset);
        url = getString(is, charset);
        notes = getString(is, charset);com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.AccountInformation.assemble(java.io.InputStream,java.nio.charset.Charset)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.AccountInformation.assemble(java.io.InputStream,java.nio.charset.Charset)",this,throwable);throw throwable;}
    }
    
    public String getAccountName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.database.AccountInformation.getAccountName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.database.AccountInformation.getAccountName()",this);return accountName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.database.AccountInformation.getAccountName()",this,throwable);throw throwable;}
    }

    public void setAccountName(String accountName) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.AccountInformation.setAccountName(java.lang.String)",this,accountName);try{this.accountName = accountName;com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.AccountInformation.setAccountName(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.AccountInformation.setAccountName(java.lang.String)",this,throwable);throw throwable;}
    }

    public String getNotes() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.database.AccountInformation.getNotes()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.database.AccountInformation.getNotes()",this);return notes;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.database.AccountInformation.getNotes()",this,throwable);throw throwable;}
    }

    public void setNotes(String notes) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.AccountInformation.setNotes(java.lang.String)",this,notes);try{this.notes = notes;com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.AccountInformation.setNotes(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.AccountInformation.setNotes(java.lang.String)",this,throwable);throw throwable;}
    }

    public String getPassword() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.database.AccountInformation.getPassword()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.database.AccountInformation.getPassword()",this);return password;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.database.AccountInformation.getPassword()",this,throwable);throw throwable;}
    }

    public void setPassword(String password) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.AccountInformation.setPassword(java.lang.String)",this,password);try{this.password = password;com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.AccountInformation.setPassword(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.AccountInformation.setPassword(java.lang.String)",this,throwable);throw throwable;}
    }

    public String getUrl() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.database.AccountInformation.getUrl()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.database.AccountInformation.getUrl()",this);return url;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.database.AccountInformation.getUrl()",this,throwable);throw throwable;}
    }

    public void setUrl(String url) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.AccountInformation.setUrl(java.lang.String)",this,url);try{this.url = url;com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.AccountInformation.setUrl(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.AccountInformation.setUrl(java.lang.String)",this,throwable);throw throwable;}
    }

    public String getUserId() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.database.AccountInformation.getUserId()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.database.AccountInformation.getUserId()",this);return userId;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.database.AccountInformation.getUserId()",this,throwable);throw throwable;}
    }

    public void setUserId(String userId) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.AccountInformation.setUserId(java.lang.String)",this,userId);try{this.userId = userId;com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.AccountInformation.setUserId(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.AccountInformation.setUserId(java.lang.String)",this,throwable);throw throwable;}
    }

}
