package com.chanapps.four.gallery;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import com.chanapps.four.activity.ChanActivityId;
import com.chanapps.four.activity.ChanIdentifiedService;
import com.chanapps.four.data.ChanFileStorage;

public class ChanOffLineImage extends MediaItem implements ChanIdentifiedService {
    private static final String TAG = "ChanOffLineImage";
    public static final boolean DEBUG  = false;
    
	private ChanActivityId activityId;
	private String name;
	private String dir;
    private File imageFile;
    private String contentType;
    private String ext;

    private int width = 0;
    private int height = 0;
    private long size = 0;

    private GalleryApp mApplication;

    public ChanOffLineImage(GalleryApp application, Path path, String dir, String image) {
        super(path, nextVersionNumber());
        Context context = application.getAndroidContext();
        File cacheFolder = ChanFileStorage.getCacheDirectory(context);
        init(application, dir, new File(cacheFolder, dir + "/" + image));
    }
    
    public ChanOffLineImage(GalleryApp application, Path path, String dir, File imageFile) {
        super(path, nextVersionNumber());
        init(application, dir, imageFile);
    }

	private void init(GalleryApp application, String dir, File imageFile) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanOffLineImage.init(com.android.gallery3d.app.GalleryApp,java.lang.String,java.io.File)",this,application,dir,imageFile);try{mApplication = application;
        activityId = new ChanActivityId(dir, 0, false);
        this.dir = dir;
        this.imageFile = imageFile;
        if (!this.imageFile.exists()) {
        	Log.e(TAG, "Initialized with not existing image! " + imageFile.getAbsolutePath(), new Exception());
        }
        String tmpExt = FilenameUtils.getExtension(imageFile.getName());
        ext = tmpExt == null || tmpExt.isEmpty() ? "jpg" : tmpExt;
        name = "Cached /" + dir + "/" + imageFile.getName();
        mApplication = Utils.checkNotNull(application);
        contentType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        size = imageFile.length();com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanOffLineImage.init(com.android.gallery3d.app.GalleryApp,java.lang.String,java.io.File)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanOffLineImage.init(com.android.gallery3d.app.GalleryApp,java.lang.String,java.io.File)",this,throwable);throw throwable;}
	}

    @Override
    public Job<Bitmap> requestImage(int type) {
    	com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanOffLineImage.requestImage(int)",this,type);try{if (DEBUG) {Log.i(TAG, "requestImage " + imageFile.getName() + " " + (type == TYPE_THUMBNAIL ? "TYPE_THUMBNAIL" : "TYPE_MICROTHUMBNAIL"));}
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanOffLineImage.requestImage(int)",this);return new BitmapJob(type);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanOffLineImage.requestImage(int)",this,throwable);throw throwable;}
    }

    @Override
    public Job<BitmapRegionDecoder> requestLargeImage() {
    	com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanOffLineImage.requestLargeImage()",this);try{if (DEBUG) {Log.i(TAG, "requestLargeImage " + imageFile.getName());}
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanOffLineImage.requestLargeImage()",this);return new RegionDecoderJob();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool.Job com.chanapps.four.gallery.ChanOffLineImage.requestLargeImage()",this,throwable);throw throwable;}
    }

    private class RegionDecoderJob implements Job<BitmapRegionDecoder> {
        public BitmapRegionDecoder run(JobContext jc) {
        	com.mijack.Xlog.logMethodEnter("android.graphics.BitmapRegionDecoder com.chanapps.four.gallery.ChanOffLineImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{if (DEBUG) {Log.i(TAG, "Large image exists " + imageFile.getAbsolutePath());}
    		try {
				{com.mijack.Xlog.logMethodExit("android.graphics.BitmapRegionDecoder com.chanapps.four.gallery.ChanOffLineImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return BitmapRegionDecoder.newInstance(imageFile.getAbsolutePath(), false);}
			} catch (IOException e) {
				Log.e(TAG, "BitmapRegionDecoder error for " + imageFile.getAbsolutePath(), e);
			}
            {com.mijack.Xlog.logMethodExit("android.graphics.BitmapRegionDecoder com.chanapps.four.gallery.ChanOffLineImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.BitmapRegionDecoder com.chanapps.four.gallery.ChanOffLineImage$RegionDecoderJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }
    
    private class BitmapJob implements Job<Bitmap> {
    	int type;
    	
        protected BitmapJob(int type) {
        	this.type = type;
        }

        public Bitmap run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{/*//Bitmap srcBmp = null;*/
    		try {
    			Bitmap bmp = getBitmap();
        		if (bmp != null && type == TYPE_MICROTHUMBNAIL) {
        			bmp = centerCrop(bmp);
        		}
        		{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return bmp;}
			} catch (Throwable e) {
				Log.e(TAG, "Bitmap docode error for " + imageFile.getName(), e);
				{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return null;}
			}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }

		private Bitmap getBitmap() throws IOException {
			com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.getBitmap()",this);try{Bitmap dstBmp = null;
			Options options = new Options();
			options.inPreferredConfig = Config.ARGB_8888;
			switch (type) {
			case TYPE_THUMBNAIL:
				options.inSampleSize = computeImageScale(200, 200);
				break;
			case TYPE_MICROTHUMBNAIL:
			default:
				options.inSampleSize = computeImageScale(100, 100);
			}

			InputStream imageStream;
			try {
			    imageStream = new BufferedInputStream(new FileInputStream(imageFile));
			}
			catch (Exception e) {
			    Log.e(TAG, "Couldn't load image file " + imageFile, e);
			    {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.getBitmap()",this);return null;}
			}

			try {
				if ("gif".equals(ext)) {
					GifDecoder decoder = new GifDecoder();
					int status = decoder.read(imageStream);
					Log.w(TAG, "Status " + (status == 0 ? "OK" : status == 1 ? "FORMAT_ERROR" : "OPEN_ERROR") + " for file " + imageFile.getName());
					if (status == 0) {
						dstBmp = decoder.getBitmap();
						width = dstBmp.getWidth();
						height = dstBmp.getHeight();
					} else if (status == 1) {
						IOUtils.closeQuietly(imageStream);
						imageStream = new BufferedInputStream(new FileInputStream(imageFile));
						dstBmp = BitmapFactory.decodeStream(imageStream, null, options);
						Log.w(TAG, imageFile.getName() + (dstBmp == null ? " not" : "") + " loaded via BitmapFactor");
					}
				} else {
					/*// center crop*/
					dstBmp = BitmapFactory.decodeStream(imageStream, null, options);
				}
			}
			catch (Exception e) {
			    Log.e(TAG, "Couldn't decode bitmap file " + imageFile, e);
			    {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.getBitmap()",this);return null;}
			}
			finally {
				IOUtils.closeQuietly(imageStream);
			}
			/*//return ensureGLCompatibleBitmap(bitmap);*/
			{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.getBitmap()",this);return dstBmp;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.getBitmap()",this,throwable);throw throwable;}
		}
        
        private Bitmap centerCrop(Bitmap srcBmp) {
			com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.centerCrop(android.graphics.Bitmap)",this,srcBmp);try{Bitmap dstBmp = null;
			if (srcBmp.getWidth() >= srcBmp.getHeight()) {
				dstBmp = Bitmap.createBitmap(srcBmp, srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2, 0,
						srcBmp.getHeight(), srcBmp.getHeight());
			} else {
				dstBmp = Bitmap.createBitmap(srcBmp, 0, srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
						srcBmp.getWidth(), srcBmp.getWidth());
			}
			{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.centerCrop(android.graphics.Bitmap)",this);return dstBmp;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.centerCrop(android.graphics.Bitmap)",this,throwable);throw throwable;}
        }

		private int computeImageScale(int targetWidth, int targetHeight) throws IOException {
			com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.computeImageScale(int,int)",this,targetWidth,targetHeight);try{/*// decode image size*/
			Options options = new Options();
			options.inJustDecodeBounds = true;

            InputStream imageStream;
            try {
                imageStream = new BufferedInputStream(new FileInputStream(imageFile));
            }
            catch (Exception e) {
                Log.e(TAG, "Couldn't open image file " + imageFile, e);
                {com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.computeImageScale(int,int)",this);return 1;}
            }
            if (imageStream == null)
                {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.computeImageScale(int,int)",this);return 1;}}

			try {
				BitmapFactory.decodeStream(imageStream, null, options);
			} catch(Exception e) {
                Log.e(TAG, "Couldn't decode image file " + imageFile, e);
                {com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.computeImageScale(int,int)",this);return 1;}
            }
            finally {
            	IOUtils.closeQuietly(imageStream);
			}

			int scale = 1;
			int imageWidth = width = options.outWidth;
			int imageHeight = height = options.outHeight;

			while (imageWidth / 2 >= targetWidth && imageHeight / 2 >= targetHeight) { /*// &&*/
				imageWidth /= 2;
				imageHeight /= 2;
				scale *= 2;
			}

			if (scale < 1) {
				scale = 1;
			}

			{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.computeImageScale(int,int)",this);return scale;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanOffLineImage$BitmapJob.computeImageScale(int,int)",this,throwable);throw throwable;}
		}
    }

    @Override
    public int getSupportedOperations() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanOffLineImage.getSupportedOperations()",this);try{int supported = SUPPORT_SETAS | SUPPORT_INFO;
        if (isSharable()) {supported |= SUPPORT_SHARE;}
        if ("jpg".equals(ext) || "jpeg".equals(ext) || "png".equals(ext)) {
            supported |= SUPPORT_FULL_IMAGE;
        }
        if (isAnimatedGif()) {
        	supported |= SUPPORT_ANIMATED_GIF;
        }
        if (DEBUG) {
        	StringBuffer buf = new StringBuffer();
        	buf.append((supported & SUPPORT_SETAS) > 0? "SUPPORT_SETAS " : "");
        	buf.append((supported & SUPPORT_SHARE) > 0? "SUPPORT_SHARE " : "");
        	buf.append((supported & SUPPORT_FULL_IMAGE) > 0? "SUPPORT_FULL_IMAGE " : "");
        	buf.append((supported & SUPPORT_ANIMATED_GIF) > 0? "SUPPORT_ANIMATED_GIF " : "");
        	Log.i(TAG, "Supported operations for " + this.name + " " + buf.toString());
        }
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanOffLineImage.getSupportedOperations()",this);return supported;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanOffLineImage.getSupportedOperations()",this,throwable);throw throwable;}
    }

    private boolean isSharable() {
    	com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.gallery.ChanOffLineImage.isSharable()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanOffLineImage.isSharable()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanOffLineImage.isSharable()",this,throwable);throw throwable;}
    }

    @Override
    public int getMediaType() {
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanOffLineImage.getMediaType()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanOffLineImage.getMediaType()",this);return MEDIA_TYPE_IMAGE;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanOffLineImage.getMediaType()",this,throwable);throw throwable;}
    }
    
    private boolean isAnimatedGif() {
    	com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.gallery.ChanOffLineImage.isAnimatedGif()",this);try{if ("gif".equals(ext)) {
    		if (width > 0 && height > 0) {
    			{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanOffLineImage.isAnimatedGif()",this);return size > width * height * 8 / 10;}
    		} else {
    			{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanOffLineImage.isAnimatedGif()",this);return size > 128000;}
    		}
    	}
    	{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.gallery.ChanOffLineImage.isAnimatedGif()",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.gallery.ChanOffLineImage.isAnimatedGif()",this,throwable);throw throwable;}
    }
    
    @Override
	public Uri getPlayUri() {
    	com.mijack.Xlog.logMethodEnter("android.net.Uri com.chanapps.four.gallery.ChanOffLineImage.getPlayUri()",this);try{com.mijack.Xlog.logMethodExit("android.net.Uri com.chanapps.four.gallery.ChanOffLineImage.getPlayUri()",this);return Uri.fromFile(imageFile);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.chanapps.four.gallery.ChanOffLineImage.getPlayUri()",this,throwable);throw throwable;}
	}

	@Override
    public Uri getContentUri() {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.chanapps.four.gallery.ChanOffLineImage.getContentUri()",this);try{com.mijack.Xlog.logMethodExit("android.net.Uri com.chanapps.four.gallery.ChanOffLineImage.getContentUri()",this);return getPlayUri();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.chanapps.four.gallery.ChanOffLineImage.getContentUri()",this,throwable);throw throwable;}
    }
	
	public void updateImageBounds(File imageFile) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanOffLineImage.updateImageBounds(java.io.File)",this,imageFile);try{/*// decode image size*/
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
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanOffLineImage.updateImageBounds(java.io.File)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanOffLineImage.updateImageBounds(java.io.File)",this,throwable);throw throwable;}
	}

    @Override
    public MediaDetails getDetails() {
    	com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaDetails com.chanapps.four.gallery.ChanOffLineImage.getDetails()",this);try{updateImageBounds(imageFile);
        MediaDetails details = super.getDetails();
        if (width != 0 && height != 0) {
            details.addDetail(MediaDetails.INDEX_WIDTH, width);
            details.addDetail(MediaDetails.INDEX_HEIGHT, height);
        }
        details.addDetail(MediaDetails.INDEX_PATH, this.imageFile.getAbsoluteFile());
        details.addDetail(MediaDetails.INDEX_MIMETYPE, contentType);
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.chanapps.four.gallery.ChanOffLineImage.getDetails()",this);return details;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaDetails com.chanapps.four.gallery.ChanOffLineImage.getDetails()",this,throwable);throw throwable;}
    }

    @Override
    public String getMimeType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.gallery.ChanOffLineImage.getMimeType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.gallery.ChanOffLineImage.getMimeType()",this);return contentType;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.gallery.ChanOffLineImage.getMimeType()",this,throwable);throw throwable;}
    }

    @Override
    protected void finalize() throws Throwable {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.gallery.ChanOffLineImage.finalize()",this);try{try {
        	mApplication = null;
        } finally {
            super.finalize();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.gallery.ChanOffLineImage.finalize()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.gallery.ChanOffLineImage.finalize()",this,throwable);throw throwable;}
    }

    @Override
    public int getWidth() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanOffLineImage.getWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanOffLineImage.getWidth()",this);return width;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanOffLineImage.getWidth()",this,throwable);throw throwable;}
    }

    @Override
    public int getHeight() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.gallery.ChanOffLineImage.getHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.gallery.ChanOffLineImage.getHeight()",this);return height;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.gallery.ChanOffLineImage.getHeight()",this,throwable);throw throwable;}
    }

	@Override
	public String getName() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.gallery.ChanOffLineImage.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.gallery.ChanOffLineImage.getName()",this);return name;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.gallery.ChanOffLineImage.getName()",this,throwable);throw throwable;}
	}

	@Override
	public ChanActivityId getChanActivityId() {
		com.mijack.Xlog.logMethodEnter("com.chanapps.four.activity.ChanActivityId com.chanapps.four.gallery.ChanOffLineImage.getChanActivityId()",this);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.activity.ChanActivityId com.chanapps.four.gallery.ChanOffLineImage.getChanActivityId()",this);return activityId;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.activity.ChanActivityId com.chanapps.four.gallery.ChanOffLineImage.getChanActivityId()",this,throwable);throw throwable;}
	}

	@Override
	public Context getApplicationContext() {
		com.mijack.Xlog.logMethodEnter("android.content.Context com.chanapps.four.gallery.ChanOffLineImage.getApplicationContext()",this);try{com.mijack.Xlog.logMethodExit("android.content.Context com.chanapps.four.gallery.ChanOffLineImage.getApplicationContext()",this);return mApplication != null ? mApplication.getAndroidContext() : null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.Context com.chanapps.four.gallery.ChanOffLineImage.getApplicationContext()",this,throwable);throw throwable;}
	}
}
