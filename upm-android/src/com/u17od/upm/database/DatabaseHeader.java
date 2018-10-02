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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DatabaseHeader extends FlatPackObject {

    private int majorVersion;
    private int minorVersion;
    private int patchVersion;
    
    
    public DatabaseHeader(InputStream is) throws IOException, ProblemReadingDatabaseFile {
        assemble(is);
    }
    
    
    public DatabaseHeader(int majorVersion, int minorVersion, int patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
    }
    
    
    public void flatPack(OutputStream os) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.DatabaseHeader.flatPack(java.io.OutputStream)",this,os);try{os.write(flatPack(String.valueOf(majorVersion)));
        os.write(flatPack(String.valueOf(minorVersion)));
        os.write(flatPack(String.valueOf(patchVersion)));com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.DatabaseHeader.flatPack(java.io.OutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.DatabaseHeader.flatPack(java.io.OutputStream)",this,throwable);throw throwable;}
    }

    
    private void assemble(InputStream is) throws IOException, ProblemReadingDatabaseFile {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.DatabaseHeader.assemble(java.io.InputStream)",this,is);try{majorVersion = getInt(is);
        minorVersion = getInt(is);
        patchVersion = getInt(is);com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.DatabaseHeader.assemble(java.io.InputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.DatabaseHeader.assemble(java.io.InputStream)",this,throwable);throw throwable;}
    }    

    public String getVersion() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.database.DatabaseHeader.getVersion()",this);try{StringBuffer buf = new StringBuffer();
        buf.append(majorVersion);
        buf.append('.');
        buf.append(minorVersion);
        buf.append('.');
        buf.append(patchVersion);
        {com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.database.DatabaseHeader.getVersion()",this);return buf.toString();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.database.DatabaseHeader.getVersion()",this,throwable);throw throwable;}
    }


    public int getMajorVersion() {
        com.mijack.Xlog.logMethodEnter("int com.u17od.upm.database.DatabaseHeader.getMajorVersion()",this);try{com.mijack.Xlog.logMethodExit("int com.u17od.upm.database.DatabaseHeader.getMajorVersion()",this);return majorVersion;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.u17od.upm.database.DatabaseHeader.getMajorVersion()",this,throwable);throw throwable;}
    }


    public void setMajorVersion(int majorVersion) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.DatabaseHeader.setMajorVersion(int)",this,majorVersion);try{this.majorVersion = majorVersion;com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.DatabaseHeader.setMajorVersion(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.DatabaseHeader.setMajorVersion(int)",this,throwable);throw throwable;}
    }


    public int getMinorVersion() {
        com.mijack.Xlog.logMethodEnter("int com.u17od.upm.database.DatabaseHeader.getMinorVersion()",this);try{com.mijack.Xlog.logMethodExit("int com.u17od.upm.database.DatabaseHeader.getMinorVersion()",this);return minorVersion;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.u17od.upm.database.DatabaseHeader.getMinorVersion()",this,throwable);throw throwable;}
    }


    public void setMinorVersion(int minorVersion) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.DatabaseHeader.setMinorVersion(int)",this,minorVersion);try{this.minorVersion = minorVersion;com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.DatabaseHeader.setMinorVersion(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.DatabaseHeader.setMinorVersion(int)",this,throwable);throw throwable;}
    }


    public int getPatchVersion() {
        com.mijack.Xlog.logMethodEnter("int com.u17od.upm.database.DatabaseHeader.getPatchVersion()",this);try{com.mijack.Xlog.logMethodExit("int com.u17od.upm.database.DatabaseHeader.getPatchVersion()",this);return patchVersion;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.u17od.upm.database.DatabaseHeader.getPatchVersion()",this,throwable);throw throwable;}
    }


    public void setPatchVersion(int patchVersion) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.DatabaseHeader.setPatchVersion(int)",this,patchVersion);try{this.patchVersion = patchVersion;com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.DatabaseHeader.setPatchVersion(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.DatabaseHeader.setPatchVersion(int)",this,throwable);throw throwable;}
    }

}
