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

public class DatabaseOptions extends FlatPackObject {

    private String remoteLocation;
    private String authDBEntry;

    
    public DatabaseOptions() {
        remoteLocation = "";
        authDBEntry = "";
    }
    
    
    public DatabaseOptions(InputStream is) throws IOException, ProblemReadingDatabaseFile {
        remoteLocation = getString(is);
        authDBEntry = getString(is);
    }
    
    
    public void setRemoteLocation(String remoteLocation) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.DatabaseOptions.setRemoteLocation(java.lang.String)",this,remoteLocation);try{if (remoteLocation == null) {
            remoteLocation = "";
        }
        this.remoteLocation = remoteLocation;com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.DatabaseOptions.setRemoteLocation(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.DatabaseOptions.setRemoteLocation(java.lang.String)",this,throwable);throw throwable;}
    }
    
    
    public String getRemoteLocation() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.database.DatabaseOptions.getRemoteLocation()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.database.DatabaseOptions.getRemoteLocation()",this);return remoteLocation;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.database.DatabaseOptions.getRemoteLocation()",this,throwable);throw throwable;}
    }
    
    
    public void flatPack(OutputStream os) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.DatabaseOptions.flatPack(java.io.OutputStream)",this,os);try{os.write(flatPack(remoteLocation));
        os.write(flatPack(authDBEntry));com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.DatabaseOptions.flatPack(java.io.OutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.DatabaseOptions.flatPack(java.io.OutputStream)",this,throwable);throw throwable;}
    }


    public String getAuthDBEntry() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.database.DatabaseOptions.getAuthDBEntry()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.database.DatabaseOptions.getAuthDBEntry()",this);return authDBEntry;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.database.DatabaseOptions.getAuthDBEntry()",this,throwable);throw throwable;}
    }


    public void setAuthDBEntry(String authDBEntry) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.DatabaseOptions.setAuthDBEntry(java.lang.String)",this,authDBEntry);try{if (authDBEntry == null) {
            authDBEntry = "";
        }
        this.authDBEntry = authDBEntry;com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.DatabaseOptions.setAuthDBEntry(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.DatabaseOptions.setAuthDBEntry(java.lang.String)",this,throwable);throw throwable;}
    }

}
