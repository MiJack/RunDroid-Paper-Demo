package com.chanapps.four.gallery;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import com.chanapps.four.activity.ChanActivityId;
import com.chanapps.four.activity.ChanIdentifiedService;
import com.chanapps.four.activity.R;
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.data.FetchParams;
import com.chanapps.four.service.NetworkProfileManager;
import com.chanapps.four.service.profile.NetworkProfile;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;

public class ChanImage extends MediaItem implements ChanIdentifiedService {
    private static final String TAG = "ChanImage";
    public static final boolean DEBUG  = false;
    
    private static final int MIN_DOWNLOAD_PROGRESS_UPDATE = 300;
	private static final int IMAGE_BUFFER_SIZE = 20480;
	
	private static final int THUMBNAIL_TARGET_SIZE = 640;
    private static final int MICROTHUMBNAIL_TARGET_SIZE = 200;

	private final ChanActivityId activityId;
	private final String name;
    private final String url;
    private final String thumbUrl;
    private final String localImagePath;
    private final String contentType;
    private final int w, h;
    private final int tn_h, tn_w;
    private final int fsize;
    private final String ext;
    private final boolean isDead;
    private final String sub;
    private final String com;

    private int width;
    private int height;

    private GalleryApp mApplication;

    public ChanImage(GalleryApp application, Path path, ChanPost post) {
        super(path, nextVersionNumber());
        mApplication = application;
        /*//activityId = new ChanActivityId(post.board, post.resto != 0 ? post.resto : post.no, post.no, false);*/
        activityId = new ChanActivityId(null, post.board, post.resto != 0 ? post.resto : post.no, post.no);
        url = post.imageUrl(getApplicationContext());
        thumbUrl = post.thumbnailUrl(getApplicationContext());
        tn_h = post.tn_h;
        tn_w = post.tn_w;
        w = post.w;
        h = post.h;
        fsize = post.fsize;
        ext = post.ext;
        isDead = post.isDead;
        sub = post.sub;
        com = post.com;
        name = "/" + post.board + "/" + (post.resto != 0 ? post.resto : post.no);
        localImagePath = ChanFileStorage.getBoardCacheDirectory(mApplication.getAndroidContext(), post.board) + "/" + post.imageName();
        mApplication = Utils.checkNotNull(application);
        String extNoDot = post.ext != null && post.ext.startsWith(".") ? post.ext.substring(1) : post.ext;
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extNoDot);
        if (mimeType != null) {
            contentType = mimeType;
        }
        else {
            contentType = pickMimeType(extNoDot);
        }
    }

    private String pickMimeType(String ext) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.gallery.ChanImage.pickMimeType(java.lang.String)",this,ext);try{if ("webm".equals(ext)) {
            {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.gallery.ChanImage.pickMimeType(java.lang.String)",this);return "video/webm";}
        }
        else {
            {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.gallery.ChanImage.pickMimeType(java.lang.String)",this);return "image/" + ext;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.gallery.ChanImage.pickMimeType(java.lang.String)",this,throwable);throw throwable;}
    }

    public static boolean isCallable(Context context, Intent intent) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.gallery.ChanImage.isCallable(android.content.Context,android.content.Intent)",context,intent);try{List<ResolveInfo> list = context.getPackageManager() == null
                ? null
                : context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.gallery.ChanImage.isCallable(android.content.Context,android.content.Intent)");return list != null && list.size() > 0;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanImage.isCallable(android.content.Context,android.content.Intent)",throwable);throw throwable;}
    }

    public static void startViewer(Activity activity, Uri uri, String mimeType) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.gallery.ChanImage.startViewer(android.app.Activity,android.net.Uri,java.lang.String)",activity,uri,mimeType);try{if (activity == null) {
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.gallery.ChanImage.startViewer(android.app.Activity,android.net.Uri,java.lang.String)");return;}
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setDataAndType(uri, mimeType);
        if (!isCallable(activity, intent)) {
            Log.e(TAG, "no handler for mimeType=" + mimeType + " url=" + uri);
            Toast.makeText(activity, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.gallery.ChanImage.startViewer(android.app.Activity,android.net.Uri,java.lang.String)");return;}
        }
        activity.startActivity(intent);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.gallery.ChanImage.startViewer(android.app.Activity,android.net.Uri,java.lang.String)",throwable);throw throwable;}
    }

    public static String videoMimeType(String ext) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.chanapps.four.gallery.ChanImage.videoMimeType(java.lang.String)",ext);try{String mimeType;
        if (ext == null || ext.isEmpty()) {
            mimeType = "video/*";
        }
        else {
            mimeType = "video/" + ext.replaceFirst("\\.", "");
        }
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.chanapps.four.gallery.ChanImage.videoMimeType(java.lang.String)");return mimeType;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.chanapps.four.gallery.ChanImage.videoMimeType(java.lang.String)",throwable);throw throwable;}
    }

    @Override
    public Job<Bitmap> requestImage(int type) {
    	com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanImage.requestImage(int)",this,type);try{if (DEBUG) {Log.i(TAG, "requestImage " + thumbUrl + " " + (type == TYPE_THUMBNAIL ? "TYPE_THUMBNAIL" : "TYPE_MICROTHUMBNAIL"));}
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanImage.requestImage(int)",this);return new BitmapJob(type);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanImage.requestImage(int)",this,throwable);throw throwable;}
    }

    @Override
    public Job<BitmapRegionDecoder> requestLargeImage() {
    	com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanImage.requestLargeImage()",this);try{if (DEBUG) {Log.i(TAG, "requestLargeImage " + this.url);}
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanImage.requestLargeImage()",this);return new RegionDecoderJob();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanImage.requestLargeImage()",this,throwable);throw throwable;}
    }

    private class RegionDecoderJob implements Job<BitmapRegionDecoder> {
        public BitmapRegionDecoder run(JobContext jc) {
        	com.mijack.Xlog.logMethodEnter("android.graphics.BitmapRegionDecoder com.chanapps.four.gallery.ChanImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{File localImageFile = new File(localImagePath);
        	if (!localImageFile.exists() && !isDead) {
        		downloadFullImage();
        	}

        	if (localImageFile.exists()) {
	        	if (DEBUG) {
	        		Log.i(TAG, "Large image exists, local: " + localImagePath + " url: " + url);
	        		FileInputStream fis = null;
	        		try {
	        			fis = new FileInputStream(localImageFile);
	        			byte buffer[] = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	        			int actualSize = fis.read(buffer, 0, 10);
	        			Log.i(TAG, "Magic code for url: " + url + " hex: " + toHexString(buffer));
	        		} catch (Exception e) {
	        			Log.e(TAG, "Error while getting magic number", e);
	        		} finally {
	        			IOUtils.closeQuietly(fis);
	        		}
	        	}
	        	
	    		try {
					{com.mijack.Xlog.logMethodExit("android.graphics.BitmapRegionDecoder com.chanapps.four.gallery.ChanImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return BitmapRegionDecoder.newInstance(localImagePath, false);}
				} catch (IOException e) {
					Log.e(TAG, "BitmapRegionDecoder error for " + localImagePath, e);
				}
        	}
            {com.mijack.Xlog.logMethodExit("android.graphics.BitmapRegionDecoder com.chanapps.four.gallery.ChanImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.BitmapRegionDecoder com.chanapps.four.gallery.ChanImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }
    
	protected void downloadFullImage() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanImage.downloadFullImage()",this);try{long startTime = Calendar.getInstance().getTimeInMillis();
        InputStream in = null;
        OutputStream out = null;
        HttpURLConnection conn = null;
		try {
			if (DEBUG) {Log.i(TAG, "Handling image download service for " + url);}
			
			File targetFile = new File(localImagePath);
			
			conn = (HttpURLConnection)new URL(url).openConnection();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                Log.e(TAG, "downloadFullImage() no longer exists url=" + url);
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanImage.downloadFullImage()",this);return;}
            }
            FetchParams fetchParams = NetworkProfileManager.instance().getFetchParams();
            /*// we need to double read timeout as file might be large*/
			conn.setReadTimeout(fetchParams.readTimeout * 2);
			conn.setConnectTimeout(fetchParams.connectTimeout);
            /*//conn.setRequestProperty("connection", "close"); // prevent keep-alive on big images*/

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
			}
			long endTime = Calendar.getInstance().getTimeInMillis();
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            closeConnection(conn);

            NetworkProfileManager.instance().finishedImageDownload(this, (int)(endTime - startTime), fileLength);
            if (DEBUG) {Log.i(TAG, "Stored image " + url + " to file "
            		+ targetFile.getAbsolutePath() + " in " + (endTime - startTime) + "ms.");}
            
		    /*//notifyDownloadFinished(fileLength);*/
		} catch (Exception e) {
            Log.e(TAG, "Error in image download service url=" + url, e);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            closeConnection(conn);
            NetworkProfileManager.instance().failedFetchingData(this, NetworkProfile.Failure.NETWORK);
            /*//notifyDownloadError();*/
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			closeConnection(conn);
            notifyDownloadProgress(fsize);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanImage.downloadFullImage()",this,throwable);throw throwable;}
	}
	
	private void notifyDownloadProgress(int fileLength) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanImage.notifyDownloadProgress(int)",this,fileLength);try{/*
		ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
		if (activity != null && activity.getChanActivityId().activity == LastActivity.GALLERY_ACTIVITY) {
			Handler handler = activity.getChanHandler();
			if (handler != null) {
				Message msg = handler.obtainMessage(GalleryViewActivity.PROGRESS_REFRESH_MSG, fileLength, 0);
				handler.sendMessage(msg);
			}
		}
		*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanImage.notifyDownloadProgress(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanImage.notifyDownloadProgress(int)",this,throwable);throw throwable;}
	}

	protected void closeConnection(HttpURLConnection tc) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanImage.closeConnection(java.net.HttpURLConnection)",this,tc);try{if (tc != null) {
			try {
		        tc.disconnect();
            } catch (Exception e) {
				Log.e(TAG, "Error closing connection", e);
			}
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanImage.closeConnection(java.net.HttpURLConnection)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanImage.closeConnection(java.net.HttpURLConnection)",this,throwable);throw throwable;}
	}

    private class BitmapJob implements Job<Bitmap> {
        private int type;

        protected BitmapJob(int type) {
            this.type = type;
        }

        public Bitmap run(JobContext jc) {
        	com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{try {
        		Bitmap bmp = getBitmap();
        		if (bmp != null && type == TYPE_MICROTHUMBNAIL) {
        			bmp = centerCrop(bmp);
        		}
        		if (DEBUG) {Log.w(TAG, "Bitmap loaded for " + name);}
        		{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return bmp;}
        	} catch (Throwable e) {
				Log.e(TAG, "Bitmap docode error for " + localImagePath, e);
				{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}
			}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }

		private Bitmap getBitmap() {
			com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.getBitmap()",this);try{Bitmap bitmap = null;
			if (type == TYPE_THUMBNAIL) {
				bitmap = downloadFullImageAsThumb();
				if (bitmap != null) {
					{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.getBitmap()",this);return bitmap;}
				}
			}
			{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.getBitmap()",this);return downloadThumbnail();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.getBitmap()",this,throwable);throw throwable;}
		}

        private Bitmap centerCrop(Bitmap srcBmp) {
			com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.centerCrop(android.graphics.Bitmap)",this,srcBmp);try{Bitmap dstBmp = null;
			if (srcBmp.getWidth() >= srcBmp.getHeight()) {
				dstBmp = Bitmap.createBitmap(srcBmp, srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2, 0,
						srcBmp.getHeight(), srcBmp.getHeight());
			} else {
				dstBmp = Bitmap.createBitmap(srcBmp, 0, srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
						srcBmp.getWidth(), srcBmp.getWidth());
			}
			{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.centerCrop(android.graphics.Bitmap)",this);return dstBmp;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.centerCrop(android.graphics.Bitmap)",this,throwable);throw throwable;}
        }
        
        private Bitmap downloadThumbnail() {
			com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.downloadThumbnail()",this);try{File thumbFile = ImageLoader.getInstance().getDiscCache().get(thumbUrl);
            Bitmap bitmap = null;
            try {
	            if (!thumbFile.exists()) {
	            	saveImageOnDisc(thumbFile);
	            }
	            
	            Options options = getBitmapOptions(thumbFile);
        		bitmap = BitmapFactory.decodeFile(thumbFile.getAbsolutePath(), options);
            } catch (Exception e) {
        		Log.e(TAG, "Error loading/transforming thumbnail", e);
        		thumbFile.delete();
        	}
			{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.downloadThumbnail()",this);return bitmap;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.downloadThumbnail()",this,throwable);throw throwable;}
		}

        private Bitmap downloadFullImageAsThumb() {
            com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.downloadFullImageAsThumb()",this);try{Bitmap bitmap = null;
            InputStream io = null;
            try {
            	File localImageFile = new File(localImagePath);
            	if (localImageFile.exists()) {
            		if (DEBUG) {Log.w(TAG, "Expected size: " + fsize + ", onDisk: " + localImageFile.length() + ", path: " + localImageFile.getAbsolutePath());}
            	}
            	if ((!localImageFile.exists() || localImageFile.length() < (fsize / 2) ) && !isDead) {
            		downloadFullImage();
            	}

            	if (".gif".equals(ext)) {
            		GifDecoder decoder = new GifDecoder();
            		io = new BufferedInputStream(new FileInputStream(localImageFile));
            		int status = decoder.read(io);
            		if (DEBUG) {Log.w(TAG, "Status " + (status == 0 ? "OK" : status == 1 ? "FORMAT_ERROR" : "OPEN_ERROR") + " for file " + localImageFile.getName());}
            		if (status == 0) {
            			bitmap = decoder.getBitmap();
            			if (DEBUG) {Log.w(TAG, "Gif size w:" + bitmap.getWidth() + " h:" + bitmap.getWidth());}
            		} else if (status == 1) {
            			Options options = getBitmapOptions(localImageFile);
            			IOUtils.closeQuietly(io);
            			io = new BufferedInputStream(new FileInputStream(localImageFile));
            			bitmap = BitmapFactory.decodeStream(io, null, options);
            			if (DEBUG) {Log.w(TAG, localImageFile.getName() + (bitmap == null ? " not" : "") + " loaded via BitmapFactory");}
            		}
            	} else if (localImageFile.exists()) {
            		Options options = getBitmapOptions(localImageFile);
            		bitmap = BitmapFactory.decodeFile(localImageFile.getAbsolutePath(), options);
            	}
            } catch (Throwable e) {
        		Log.e(TAG, "Error loading/transforming full image", e);
        	} finally {
        		IOUtils.closeQuietly(io);
        	}
			{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.downloadFullImageAsThumb()",this);return bitmap;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.chanapps.four.gallery.ChanImage$BitmapJob.downloadFullImageAsThumb()",this,throwable);throw throwable;}
		}
		
		private Options getBitmapOptions(File thumbFile) throws IOException {
			com.mijack.Xlog.logMethodEnter("android.graphics.BitmapFactory.Options com.chanapps.four.gallery.ChanImage$BitmapJob.getBitmapOptions(java.io.File)",this,thumbFile);try{Options options = new Options();
			options.inPreferredConfig = Config.ARGB_8888;
			switch (type) {
			case TYPE_THUMBNAIL:
				options.inSampleSize = computeImageScale(thumbFile, THUMBNAIL_TARGET_SIZE, THUMBNAIL_TARGET_SIZE);
				break;
			case TYPE_MICROTHUMBNAIL:
			default:
				options.inSampleSize = computeImageScale(thumbFile, MICROTHUMBNAIL_TARGET_SIZE, MICROTHUMBNAIL_TARGET_SIZE);
			}
			{com.mijack.Xlog.logMethodExit("android.graphics.BitmapFactory.Options com.chanapps.four.gallery.ChanImage$BitmapJob.getBitmapOptions(java.io.File)",this);return options;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.BitmapFactory.Options com.chanapps.four.gallery.ChanImage$BitmapJob.getBitmapOptions(java.io.File)",this,throwable);throw throwable;}
		}
        
    	private void saveImageOnDisc(File targetFile) throws URISyntaxException, IOException {
			com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanImage$BitmapJob.saveImageOnDisc(java.io.File)",this,targetFile);try{FetchParams fetchParams = NetworkProfileManager.instance().getFetchParams();
			URLConnectionImageDownloader downloader = new URLConnectionImageDownloader(mApplication.getAndroidContext(), fetchParams.connectTimeout, fetchParams.readTimeout);
    		InputStream is = null;
    		OutputStream os = null;
    		try {
    			is = new BufferedInputStream(downloader.getStreamFromNetwork(thumbUrl, null));
    			os = new BufferedOutputStream(new FileOutputStream(targetFile));
				IOUtils.copy(is, os);
    		} finally {
				IOUtils.closeQuietly(os);
    			IOUtils.closeQuietly(is);
    		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanImage$BitmapJob.saveImageOnDisc(java.io.File)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanImage$BitmapJob.saveImageOnDisc(java.io.File)",this,throwable);throw throwable;}
    	}
    }

    @Override
    public int getSupportedOperations() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanImage.getSupportedOperations()",this);try{int supported = SUPPORT_SETAS | SUPPORT_INFO;
        if (isSharable()) {supported |= SUPPORT_SHARE;}
        if (".jpg".equals(ext) || ".jpeg".equals(ext) || ".png".equals(ext)) {
            supported |= SUPPORT_FULL_IMAGE;
        }
        if (isAnimatedGif()) {
        	supported |= SUPPORT_ANIMATED_GIF;
        }
        else if (".gif".equals(ext)) {
            supported |= SUPPORT_FULL_IMAGE;
        }
        else if (isVideo()) {
            supported |= SUPPORT_PLAY;
        }
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanImage.getSupportedOperations()",this);return supported;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanImage.getSupportedOperations()",this,throwable);throw throwable;}
    }

    private boolean isSharable() {
    	com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.gallery.ChanImage.isSharable()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanImage.isSharable()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanImage.isSharable()",this,throwable);throw throwable;}
    }

    @Override
    public int getMediaType() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanImage.getMediaType()",this);try{if (isVideo()) {
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanImage.getMediaType()",this);return MEDIA_TYPE_VIDEO;}
        }
        else {
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanImage.getMediaType()",this);return MEDIA_TYPE_IMAGE;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanImage.getMediaType()",this,throwable);throw throwable;}
    }
    
    private boolean isAnimatedGif() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.gallery.ChanImage.isAnimatedGif()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanImage.isAnimatedGif()",this);return isAnimatedGif(ext, fsize, w, h);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanImage.isAnimatedGif()",this,throwable);throw throwable;}
    }

    static public boolean isAnimatedGif(String ext, int fsize, int w, int h) {
    	com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.gallery.ChanImage.isAnimatedGif(java.lang.String,int,int,int)",ext,fsize,w,h);try{if (".gif".equals(ext)) {
    		if (fsize > 0) {
    			{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.gallery.ChanImage.isAnimatedGif(java.lang.String,int,int,int)");return fsize > w * h * 8 / 10;}
    		}
    	}
    	{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.gallery.ChanImage.isAnimatedGif(java.lang.String,int,int,int)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanImage.isAnimatedGif(java.lang.String,int,int,int)",throwable);throw throwable;}
    }

    private boolean isVideo() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.gallery.ChanImage.isVideo()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanImage.isVideo()",this);return isVideo(ext, fsize, w, h);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanImage.isVideo()",this,throwable);throw throwable;}
    }

    static public boolean isVideo(String ext, int fsize, int w, int h) {
    	com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.gallery.ChanImage.isVideo(java.lang.String,int,int,int)",ext,fsize,w,h);try{if (".webm".equals(ext)) {
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.gallery.ChanImage.isVideo(java.lang.String,int,int,int)");return fsize > 0 && w > 0 && h > 0;}
    	}
    	{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.gallery.ChanImage.isVideo(java.lang.String,int,int,int)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanImage.isVideo(java.lang.String,int,int,int)",throwable);throw throwable;}
    }

    @Override
	public Uri getPlayUri() {
    	com.mijack.Xlog.logMethodEnter("android.net.Uri com.chanapps.four.gallery.ChanImage.getPlayUri()",this);try{File localFile = new File (localImagePath);
    	if (isVideo()) {
            {com.mijack.Xlog.logMethodExit("android.net.Uri com.chanapps.four.gallery.ChanImage.getPlayUri()",this);return Uri.parse(url);}
        }
        else if (localFile.exists() && isAnimatedGif()) {
    		{com.mijack.Xlog.logMethodExit("android.net.Uri com.chanapps.four.gallery.ChanImage.getPlayUri()",this);return Uri.fromFile(localFile);}
    	} else {
    		{com.mijack.Xlog.logMethodExit("android.net.Uri com.chanapps.four.gallery.ChanImage.getPlayUri()",this);return null;}
    	}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.chanapps.four.gallery.ChanImage.getPlayUri()",this,throwable);throw throwable;}
	}

	@Override
    public Uri getContentUri() {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.chanapps.four.gallery.ChanImage.getContentUri()",this);try{File localFile = new File (localImagePath);
        if (localFile.exists()) {
            {com.mijack.Xlog.logMethodExit("android.net.Uri com.chanapps.four.gallery.ChanImage.getContentUri()",this);return Uri.fromFile(localFile);}
        } else {
            {com.mijack.Xlog.logMethodExit("android.net.Uri com.chanapps.four.gallery.ChanImage.getContentUri()",this);return null;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.chanapps.four.gallery.ChanImage.getContentUri()",this,throwable);throw throwable;}
    }

    @Override
    public MediaDetails getDetails() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaDetails com.chanapps.four.gallery.ChanImage.getDetails()",this);try{MediaDetails details = super.getDetails();
        if (sub != null && !sub.isEmpty())
            {details.addDetail(MediaDetails.INDEX_TITLE, Html.fromHtml("<b>" + sub + "</b>"));}
        else
            {details.addDetail(MediaDetails.INDEX_TITLE, name);}
        if (com != null && !com.isEmpty())
            {details.addDetail(MediaDetails.INDEX_DESCRIPTION, Html.fromHtml(com));}
        else
            {details.addDetail(MediaDetails.INDEX_DESCRIPTION, "");}
        if (width != 0 && height != 0) {
            details.addDetail(MediaDetails.INDEX_WIDTH, w);
            details.addDetail(MediaDetails.INDEX_HEIGHT, h);
        }
        details.addDetail(MediaDetails.INDEX_PATH, this.url);
        details.addDetail(MediaDetails.INDEX_MIMETYPE, contentType);
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.chanapps.four.gallery.ChanImage.getDetails()",this);return details;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaDetails com.chanapps.four.gallery.ChanImage.getDetails()",this,throwable);throw throwable;}
    }

    @Override
    public String getMimeType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.gallery.ChanImage.getMimeType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.gallery.ChanImage.getMimeType()",this);return contentType;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.gallery.ChanImage.getMimeType()",this,throwable);throw throwable;}
    }

    @Override
    protected void finalize() throws Throwable {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanImage.finalize()",this);try{try {
        	mApplication = null;
        } finally {
            super.finalize();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanImage.finalize()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanImage.finalize()",this,throwable);throw throwable;}
    }

    @Override
    public int getWidth() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanImage.getWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanImage.getWidth()",this);return w;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanImage.getWidth()",this,throwable);throw throwable;}
    }

    @Override
    public int getHeight() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanImage.getHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanImage.getHeight()",this);return h;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanImage.getHeight()",this,throwable);throw throwable;}
    }

	@Override
	public String getName() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.gallery.ChanImage.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.gallery.ChanImage.getName()",this);return name;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.gallery.ChanImage.getName()",this,throwable);throw throwable;}
	}

	@Override
	public ChanActivityId getChanActivityId() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.ChanActivityId com.chanapps.four.gallery.ChanImage.getChanActivityId()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanActivityId com.chanapps.four.gallery.ChanImage.getChanActivityId()",this);return activityId;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.ChanActivityId com.chanapps.four.gallery.ChanImage.getChanActivityId()",this,throwable);throw throwable;}
	}

	@Override
	public Context getApplicationContext() {
		com.mijack.Xlog.logMethodEnter("android.content.Context com.chanapps.four.gallery.ChanImage.getApplicationContext()",this);try{com.mijack.Xlog.logMethodExit("android.content.Context com.chanapps.four.gallery.ChanImage.getApplicationContext()",this);return mApplication != null ? mApplication.getAndroidContext() : null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.Context com.chanapps.four.gallery.ChanImage.getApplicationContext()",this,throwable);throw throwable;}
	}
	
	public int computeImageScale(File imageFile, int targetWidth, int targetHeight) throws IOException {
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanImage.computeImageScale(java.io.File,int,int)",this,imageFile,targetWidth,targetHeight);try{/*// decode image size*/
		Options options = new Options();
		options.inJustDecodeBounds = true;
		InputStream imageStream = new BufferedInputStream(new FileInputStream(imageFile));
		try {
			BitmapFactory.decodeStream(imageStream, null, options);
			int scale = 1;
			int imageWidth = options.outWidth;
			int imageHeight = options.outHeight;

			while (imageWidth / 2 >= targetWidth && imageHeight / 2 >= targetHeight) { /*// &&*/
				imageWidth /= 2;
				imageHeight /= 2;
				scale *= 2;
			}

			if (scale < 1) {
				scale = 1;
			}

			{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanImage.computeImageScale(java.io.File,int,int)",this);return scale;}
		} finally {
			IOUtils.closeQuietly(imageStream);
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanImage.computeImageScale(java.io.File,int,int)",this,throwable);throw throwable;}
	}
	
	public void updateImageBounds(File imageFile) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanImage.updateImageBounds(java.io.File)",this,imageFile);try{/*// decode image size*/
		Options options = new Options();
		options.inJustDecodeBounds = true;
		InputStream imageStream = null;
		try {
			imageStream = new BufferedInputStream(new FileInputStream(imageFile));
			BitmapFactory.decodeStream(imageStream, null, options);
			width = options.outWidth;
			height = options.outHeight;
		} catch (Exception e) {
			Log.e(TAG, "Error while decoding image bounds", e);
		} finally {
			IOUtils.closeQuietly(imageStream);
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanImage.updateImageBounds(java.io.File)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanImage.updateImageBounds(java.io.File)",this,throwable);throw throwable;}
	}

	public static String toHexString(byte[] magicNumber) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.chanapps.four.gallery.ChanImage.toHexString([byte)",magicNumber);try{StringBuffer buf = new StringBuffer();
        for (byte b : magicNumber) {
        	buf.append("0x").append(Integer.toHexString((0xF0 & b) >>> 4)).append(Integer.toHexString(0x0F & b)).append(" ");
        }
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.chanapps.four.gallery.ChanImage.toHexString([byte)");return buf.toString();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.chanapps.four.gallery.ChanImage.toHexString([byte)",throwable);throw throwable;}
	}

	public int getW() {
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanImage.getW()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanImage.getW()",this);return w;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanImage.getW()",this,throwable);throw throwable;}
	}
	
	public int getH() {
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanImage.getH()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanImage.getH()",this);return h;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanImage.getH()",this,throwable);throw throwable;}
	}
}
