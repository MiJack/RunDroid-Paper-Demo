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

public class Revision extends FlatPackObject {

    private int revision;
    
    
    public Revision() {
        revision = 0;
    }
    
    
    public int increment() {
        com.mijack.Xlog.logMethodEnter("int com.u17od.upm.database.Revision.increment()",this);try{com.mijack.Xlog.logMethodExit("int com.u17od.upm.database.Revision.increment()",this);return ++revision;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.u17od.upm.database.Revision.increment()",this,throwable);throw throwable;}
    }
    
    public Revision(InputStream is) throws IOException, ProblemReadingDatabaseFile {
        revision = getInt(is);
    }

    
    public void flatPack(OutputStream os) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.Revision.flatPack(java.io.OutputStream)",this,os);try{os.write(flatPack(String.valueOf(revision)));com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.Revision.flatPack(java.io.OutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.Revision.flatPack(java.io.OutputStream)",this,throwable);throw throwable;}
    }

    
    public int getRevision() {
        com.mijack.Xlog.logMethodEnter("int com.u17od.upm.database.Revision.getRevision()",this);try{com.mijack.Xlog.logMethodExit("int com.u17od.upm.database.Revision.getRevision()",this);return revision;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.u17od.upm.database.Revision.getRevision()",this,throwable);throw throwable;}
    }
    
    
    public void setRevision(int revision) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.database.Revision.setRevision(int)",this,revision);try{this.revision = revision;com.mijack.Xlog.logMethodExit("void com.u17od.upm.database.Revision.setRevision(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.database.Revision.setRevision(int)",this,throwable);throw throwable;}
    }

}
