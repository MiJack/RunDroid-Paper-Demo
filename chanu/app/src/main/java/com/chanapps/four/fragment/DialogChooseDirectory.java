package com.chanapps.four.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.chanapps.four.activity.R;
import com.chanapps.four.component.ThemeSelector;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 12/5/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class DialogChooseDirectory implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener
{
    public interface Result
    {
        void onChooseDirectory( String dir );
    }

    List<File> m_entries = new ArrayList< File >();
    File m_currentDir;
    Context m_context;
    AlertDialog m_alertDialog;
    ListView m_list;
    Result m_result = null;

    public class DirAdapter extends ArrayAdapter< File >
    {
        public DirAdapter( int resid )
        {
            super( m_context, resid, m_entries );
        }

        /*// This function is called to show each view item*/
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            com.mijack.Xlog.logMethodEnter("android.view.View com.chanapps.four.fragment.DialogChooseDirectory$DirAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,position,convertView,parent);try{TextView textview = (TextView) super.getView( position, convertView, parent );

            if ( m_entries.get(position) == null )
            {
                textview.setText( ".." );
                /*//textview.setCompoundDrawablesWithIntrinsicBounds( m_context.getResources().getDrawable( R.drawable.collections_collection ), null, null, null );*/
            }
            else
            {
                textview.setText( m_entries.get(position).getName() );
                int iconId = ThemeSelector.instance(getContext()).isDark()
                        ? R.drawable.collections_collection_light
                        : R.drawable.collections_collection;
                textview.setCompoundDrawablesWithIntrinsicBounds(
                        m_context.getResources().getDrawable(iconId), null, null, null );
            }

            {com.mijack.Xlog.logMethodExit("android.view.View com.chanapps.four.fragment.DialogChooseDirectory$DirAdapter.getView(int,android.view.View,android.view.ViewGroup)",this);return textview;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.chanapps.four.fragment.DialogChooseDirectory$DirAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,throwable);throw throwable;}
        }
    }

    private void listDirs()
    {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.DialogChooseDirectory.listDirs()",this);try{m_entries.clear();

        /*// Get files*/
        File[] files = m_currentDir.listFiles();

        /*// Add the ".." entry*/
        if ( m_currentDir.getParent() != null )
            {m_entries.add( new File("..") );}

        if ( files != null )
        {
            for ( File file : files )
            {
                if ( !file.isDirectory() )
                    {continue;}

                m_entries.add( file );
            }
        }

        Collections.sort(m_entries, new Comparator<File>() {
            public int compare(File f1, File f2) {
                com.mijack.Xlog.logMethodEnter("int com.chanapps.four.fragment.DialogChooseDirectory$1.compare(java.io.File,java.io.File)",this,f1,f2);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.fragment.DialogChooseDirectory$1.compare(java.io.File,java.io.File)",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.DialogChooseDirectory.listDirs()",this);return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.fragment.DialogChooseDirectory$1.compare(java.io.File,java.io.File)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.DialogChooseDirectory.listDirs()",this,throwable);throw throwable;}
    }

    public DialogChooseDirectory( Context ctx, Result res, String startDir )
    {
        m_context = ctx;
        m_result = res;

        if ( startDir != null )
            {m_currentDir = new File( startDir );}
        else
            {m_currentDir = Environment.getExternalStorageDirectory();}

        listDirs();
        DirAdapter adapter = new DirAdapter(ITEM_LAYOUT_ID);

        AlertDialog.Builder builder = new AlertDialog.Builder( ctx );
        builder.setTitle(m_currentDir.getAbsolutePath());
        builder.setAdapter( adapter, this );

        builder.setPositiveButton(R.string.dialog_choose, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.DialogChooseDirectory$2.onClick(android.content.DialogInterface,int)",this,dialog,id);try{if ( m_result != null )
                    {m_result.onChooseDirectory( m_currentDir.getAbsolutePath() );}
                dialog.dismiss();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.DialogChooseDirectory$2.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.DialogChooseDirectory$2.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.DialogChooseDirectory$3.onClick(android.content.DialogInterface,int)",this,dialog,id);try{dialog.cancel();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.DialogChooseDirectory$3.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.DialogChooseDirectory$3.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
            }
        });

        m_alertDialog = builder.create();
        m_list = m_alertDialog.getListView();
        m_list.setOnItemClickListener( this );
        m_alertDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View list, int pos, long id )
    {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.DialogChooseDirectory.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this,arg0,list,pos,id);try{if ( pos < 0 || pos >= m_entries.size() )
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.DialogChooseDirectory.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this);return;}}

        if ( m_entries.get( pos ).getName().equals( ".." ) )
            {m_currentDir = m_currentDir.getParentFile();}
        else
            {m_currentDir = m_entries.get( pos );}

        String title = m_currentDir.getAbsolutePath();
        m_alertDialog.setTitle(title);
        listDirs();
        DirAdapter adapter = new DirAdapter(ITEM_LAYOUT_ID);
        m_list.setAdapter( adapter );}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.DialogChooseDirectory.onItemClick(android.widget.AdapterView,android.view.View,int,long)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.DialogChooseDirectory.onClick(android.content.DialogInterface,int)",this,dialog,which);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.DialogChooseDirectory.onClick(android.content.DialogInterface,int)",this);}

    protected static final int ITEM_LAYOUT_ID = android.R.layout.simple_list_item_1; /*// listitem_row_textview;*/

}