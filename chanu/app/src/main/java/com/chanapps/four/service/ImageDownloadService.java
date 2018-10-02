/**
 * 
 */
package com.chanapps.four.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;

import com.chanapps.four.data.*;
import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chanapps.four.activity.ChanActivityId;
import com.chanapps.four.activity.ChanIdentifiedActivity;
import com.chanapps.four.activity.ChanIdentifiedService;
import com.chanapps.four.activity.GalleryViewActivity;
import com.chanapps.four.service.profile.NetworkProfile.Failure;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class ImageDownloadService extends BaseChanService implements ChanIdentifiedService {
	private static final String TAG = ImageDownloadService.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final int MIN_DOWNLOAD_PROGRESS_UPDATE = 300;
	private static final int IMAGE_BUFFER_SIZE = 20480;
    public static final String IMAGE_PATH = "imagePath";
    public static final String IMAGE_URL = "imageUrl";

    public static void startService(Context context, String board, long threadNo, long postNo, String url, String targetFile) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.service.ImageDownloadService.startService(android.content.Context,com.chanapps.four.data.String,long,long,com.chanapps.four.data.String,com.chanapps.four.data.String)",context,board,threadNo,postNo,url,targetFile);try{if (DEBUG) {Log.i(TAG, "Start image download service for " + url);}
        Intent intent = new Intent(context, ImageDownloadService.class);
        intent.putExtra(ChanBoard.BOARD_CODE, board);
        intent.putExtra(ChanThread.THREAD_NO, threadNo);
        intent.putExtra(ChanPost.POST_NO, postNo);
        intent.putExtra(IMAGE_URL, url);
        intent.putExtra(IMAGE_PATH, targetFile);
        context.startService(intent);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.service.ImageDownloadService.startService(android.content.Context,com.chanapps.four.data.String,long,long,com.chanapps.four.data.String,com.chanapps.four.data.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.service.ImageDownloadService.startService(android.content.Context,com.chanapps.four.data.String,long,long,com.chanapps.four.data.String,com.chanapps.four.data.String)",throwable);throw throwable;}
    }

    public static void cancelService(Context context, String url) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.service.ImageDownloadService.cancelService(android.content.Context,com.chanapps.four.data.String)",context,url);try{if (DEBUG) {Log.i(TAG, "Cancelling image download service for " + url);}
        Intent intent = new Intent(context, ImageDownloadService.class);
        intent.putExtra(CLEAR_FETCH_QUEUE, 1);
        intent.putExtra(IMAGE_URL, url);
        context.startService(intent);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.service.ImageDownloadService.cancelService(android.content.Context,com.chanapps.four.data.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.service.ImageDownloadService.cancelService(android.content.Context,com.chanapps.four.data.String)",throwable);throw throwable;}
    }

    public ImageDownloadService() {
   		super("imagedownload");
   	}

    protected ImageDownloadService(String name) {
   		super(name);
   	}
    
    private String imageUrl = null;
    private String targetImagePath = null;
    private String board = null;
    private long threadNo = 0;
    private long postNo = 0;
    private boolean stopDownload = false;
	
	@Override
	protected void onHandleIntent(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.ImageDownloadService.onHandleIntent(android.content.Intent)",this,intent);try{long startTime = Calendar.getInstance().getTimeInMillis();
        InputStream in = null;
        OutputStream out = null;
        HttpURLConnection conn = null;
		try {
			stopDownload = false;
			board = intent.getStringExtra(ChanBoard.BOARD_CODE);
			threadNo = intent.getLongExtra(ChanThread.THREAD_NO, 0);
			postNo = intent.getLongExtra(ChanPost.POST_NO, 0);
			imageUrl = intent.getStringExtra(IMAGE_URL);
			targetImagePath = intent.getStringExtra(IMAGE_PATH);
			if (DEBUG) {Log.i(TAG, "Handling image download service for " + imageUrl);}
			
			File targetFile = new File(URI.create(targetImagePath));
			if (targetFile.exists()) {
				targetFile.delete();
			}
			
			conn = (HttpURLConnection)new URL(imageUrl).openConnection();
			FetchParams fetchParams = NetworkProfileManager.instance().getFetchParams();
			/*// we need to double read timeout as file might be large*/
			conn.setReadTimeout(fetchParams.readTimeout * 2);
			conn.setConnectTimeout(fetchParams.connectTimeout);
			
			in = conn.getInputStream();
			out = new FileOutputStream(targetFile);
			byte[] buffer = new byte[IMAGE_BUFFER_SIZE];
			int len = -1;
			int fileLength = 0;
			long lastNotify = startTime;
			while ((len = in.read(buffer)) != -1) {
			    out.write(buffer, 0, len);
			    fileLength += len;
			    if (Calendar.getInstance().getTimeInMillis() - lastNotify > MIN_DOWNLOAD_PROGRESS_UPDATE) {
			    	notifyDownloadProgress(fileLength);
			    	lastNotify = Calendar.getInstance().getTimeInMillis();
			    }
			    if (stopDownload || Thread.interrupted()) {
			        throw new InterruptedException("Download interrupted");
			    }
			}
			
			long endTime = Calendar.getInstance().getTimeInMillis();
			NetworkProfileManager.instance().finishedImageDownload(this, (int)(endTime - startTime), fileLength);
            if (DEBUG) {Log.i(TAG, "Stored image " + imageUrl + " to file "
            		+ targetFile.getAbsolutePath() + " in " + (endTime - startTime) + "ms.");}
            
		    notifyDownloadFinished(fileLength);			    	
		} catch (Exception e) {
            Log.e(TAG, "Error in image download service", e);
            NetworkProfileManager.instance().failedFetchingData(this, Failure.NETWORK);
            notifyDownloadError();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			closeConnection(conn);
			imageUrl = null;
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.ImageDownloadService.onHandleIntent(android.content.Intent)",this,throwable);throw throwable;}
	}

	private void notifyDownloadProgress(int fileLength) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.ImageDownloadService.notifyDownloadProgress(int)",this,fileLength);try{ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
		if (activity != null && activity.getChanActivityId().activity == LastActivity.GALLERY_ACTIVITY) {
			Handler handler = activity.getChanHandler();
			if (handler != null) {
				Message msg = handler.obtainMessage(GalleryViewActivity.PROGRESS_REFRESH_MSG, fileLength, 0);
				handler.sendMessage(msg);
			}
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.ImageDownloadService.notifyDownloadProgress(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.ImageDownloadService.notifyDownloadProgress(int)",this,throwable);throw throwable;}
	}
	
	private void notifyDownloadFinished(int fileLength) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.ImageDownloadService.notifyDownloadFinished(int)",this,fileLength);try{ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
		if (activity != null && activity.getChanActivityId().activity == LastActivity.GALLERY_ACTIVITY) {
			Handler handler = activity.getChanHandler();
			if (handler != null) {
				Message msg = handler.obtainMessage(GalleryViewActivity.FINISHED_DOWNLOAD_MSG, fileLength, 0);
				handler.sendMessage(msg);
			}
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.ImageDownloadService.notifyDownloadFinished(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.ImageDownloadService.notifyDownloadFinished(int)",this,throwable);throw throwable;}
	}
	
	private void notifyDownloadError() {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.ImageDownloadService.notifyDownloadError()",this);try{ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
		if (activity != null && activity.getChanActivityId().activity == LastActivity.GALLERY_ACTIVITY) {
			Handler handler = activity.getChanHandler();
			if (handler != null) {
				Message msg = handler.obtainMessage(GalleryViewActivity.DOWNLOAD_ERROR_MSG);
				handler.sendMessage(msg);
			}
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.ImageDownloadService.notifyDownloadError()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.ImageDownloadService.notifyDownloadError()",this,throwable);throw throwable;}
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.service.ImageDownloadService.onStartCommand(android.content.Intent,int,int)",this,intent,flags,startId);try{if (intent != null && intent.getIntExtra(CLEAR_FETCH_QUEUE, 0) == 1) {
            if (DEBUG) {Log.i(TAG, "Clearing chan fetch service message queue");}
        	mServiceHandler.removeMessages(PRIORITY_MESSAGE);
        	synchronized(this) {
        		priorityMessageCounter = 0;
        	}
        	if (imageUrl != null && imageUrl.equals(intent.getStringExtra(IMAGE_URL))) {
        		stopDownload = true;
        	}
        	{com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.ImageDownloadService.onStartCommand(android.content.Intent,int,int)",this);return START_NOT_STICKY;}
        }

    	mServiceHandler.removeMessages(PRIORITY_MESSAGE);
    	synchronized(this) {
    		priorityMessageCounter = 0;
    	}

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
    	msg.what = PRIORITY_MESSAGE;
    	mServiceHandler.sendMessageAtFrontOfQueue(msg);
    	synchronized(this) {
    		priorityMessageCounter++;
    	}
        
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.ImageDownloadService.onStartCommand(android.content.Intent,int,int)",this);return START_NOT_STICKY;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.service.ImageDownloadService.onStartCommand(android.content.Intent,int,int)",this,throwable);throw throwable;}
    }
	
	@Override
	public ChanActivityId getChanActivityId() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.ImageDownloadService.getChanActivityId()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.ImageDownloadService.getChanActivityId()",this);return new ChanActivityId(null, board, threadNo, postNo);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.ChanActivityId com.chanapps.four.service.ImageDownloadService.getChanActivityId()",this,throwable);throw throwable;}
	}
}
