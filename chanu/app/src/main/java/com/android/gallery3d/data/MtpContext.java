package com.android.gallery3d.data;

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.util.GalleryUtils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.mtp.MtpObjectInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MtpContext implements MtpClient.Listener {
    private static final String TAG = "MtpContext";

    public static final String NAME_IMPORTED_FOLDER = "Imported";

    private ScannerClient mScannerClient;
    private Context mContext;
    private MtpClient mClient;

    private static final class ScannerClient implements MediaScannerConnectionClient {
        ArrayList<String> mPaths = new ArrayList<String>();
        MediaScannerConnection mScannerConnection;
        boolean mConnected;
        Object mLock = new Object();

        public ScannerClient(Context context) {
            mScannerConnection = new MediaScannerConnection(context, this);
        }

        public void scanPath(String path) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpContext$ScannerClient.scanPath(java.lang.String)",this,path);try{synchronized (mLock) {
                if (mConnected) {
                    mScannerConnection.scanFile(path, null);
                } else {
                    mPaths.add(path);
                    mScannerConnection.connect();
                }
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpContext$ScannerClient.scanPath(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpContext$ScannerClient.scanPath(java.lang.String)",this,throwable);throw throwable;}
        }

        @Override
        public void onMediaScannerConnected() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpContext$ScannerClient.onMediaScannerConnected()",this);try{synchronized (mLock) {
                mConnected = true;
                if (!mPaths.isEmpty()) {
                    for (String path : mPaths) {
                        mScannerConnection.scanFile(path, null);
                    }
                    mPaths.clear();
                }
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpContext$ScannerClient.onMediaScannerConnected()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpContext$ScannerClient.onMediaScannerConnected()",this,throwable);throw throwable;}
        }

        {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpContext$ScannerClient.onScanCompleted(java.lang.String,android.net.Uri)",this,path,uri);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpContext$ScannerClient.onScanCompleted(java.lang.String,android.net.Uri)",this);}
    }

    public MtpContext(Context context) {
        mContext = context;
        mScannerClient = new ScannerClient(context);
        mClient = new MtpClient(mContext);
    }

    public void pause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpContext.pause()",this);try{mClient.removeListener(this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpContext.pause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpContext.pause()",this,throwable);throw throwable;}
    }

    public void resume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpContext.resume()",this);try{mClient.addListener(this);
        notifyDirty();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpContext.resume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpContext.resume()",this,throwable);throw throwable;}
    }

    public void deviceAdded(android.mtp.MtpDevice device) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpContext.deviceAdded(android.mtp.MtpDevice)",this,device);try{notifyDirty();
        showToast(R.string.camera_connected);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpContext.deviceAdded(android.mtp.MtpDevice)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpContext.deviceAdded(android.mtp.MtpDevice)",this,throwable);throw throwable;}
    }

    public void deviceRemoved(android.mtp.MtpDevice device) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpContext.deviceRemoved(android.mtp.MtpDevice)",this,device);try{notifyDirty();
        showToast(R.string.camera_disconnected);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpContext.deviceRemoved(android.mtp.MtpDevice)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpContext.deviceRemoved(android.mtp.MtpDevice)",this,throwable);throw throwable;}
    }

    private void notifyDirty() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpContext.notifyDirty()",this);try{mContext.getContentResolver().notifyChange(Uri.parse("mtp://"), null);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpContext.notifyDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpContext.notifyDirty()",this,throwable);throw throwable;}
    }

    private void showToast(final int msg) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MtpContext.showToast(int)",this,msg);try{Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MtpContext.showToast(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MtpContext.showToast(int)",this,throwable);throw throwable;}
    }

    public MtpClient getMtpClient() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MtpClient com.android.gallery3d.data.MtpContext.getMtpClient()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MtpClient com.android.gallery3d.data.MtpContext.getMtpClient()",this);return mClient;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MtpClient com.android.gallery3d.data.MtpContext.getMtpClient()",this,throwable);throw throwable;}
    }

    public boolean copyFile(String deviceName, MtpObjectInfo objInfo) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.MtpContext.copyFile(java.lang.String,android.mtp.MtpObjectInfo)",this,deviceName,objInfo);try{if (GalleryUtils.hasSpaceForSize(objInfo.getCompressedSize())) {
            File dest = Environment.getExternalStorageDirectory();
            dest = new File(dest, NAME_IMPORTED_FOLDER);
            dest.mkdirs();
            String destPath = new File(dest, objInfo.getName()).getAbsolutePath();
            int objectId = objInfo.getObjectHandle();
            if (mClient.importFile(deviceName, objectId, destPath)) {
                mScannerClient.scanPath(destPath);
                {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MtpContext.copyFile(java.lang.String,android.mtp.MtpObjectInfo)",this);return true;}
            }
        } else {
            Log.w(TAG, "No space to import " + objInfo.getName() +
                    " whose size = " + objInfo.getCompressedSize());
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MtpContext.copyFile(java.lang.String,android.mtp.MtpObjectInfo)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.MtpContext.copyFile(java.lang.String,android.mtp.MtpObjectInfo)",this,throwable);throw throwable;}
    }

    public boolean copyAlbum(String deviceName, String albumName,
            List<MtpObjectInfo> children) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.MtpContext.copyAlbum(java.lang.String,java.lang.String,java.util.ArrayList)",this,deviceName,albumName,children);try{File dest = Environment.getExternalStorageDirectory();
        dest = new File(dest, albumName);
        dest.mkdirs();
        int success = 0;
        for (MtpObjectInfo child : children) {
            if (!GalleryUtils.hasSpaceForSize(child.getCompressedSize())) {continue;}

            File importedFile = new File(dest, child.getName());
            String path = importedFile.getAbsolutePath();
            if (mClient.importFile(deviceName, child.getObjectHandle(), path)) {
                mScannerClient.scanPath(path);
                success++;
            }
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MtpContext.copyAlbum(java.lang.String,java.lang.String,java.util.ArrayList)",this);return success == children.size();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.MtpContext.copyAlbum(java.lang.String,java.lang.String,java.util.ArrayList)",this,throwable);throw throwable;}
    }
}
