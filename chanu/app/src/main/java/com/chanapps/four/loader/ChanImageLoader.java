package com.chanapps.four.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.chanapps.four.component.ChanGridSizer;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 3/14/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChanImageLoader {

    static private final String TAG = ChanImageLoader.class.getSimpleName();
    static private final boolean DEBUG = false;
    
    static private final int FULL_SCREEN_IMAGE_PADDING_DP = 8;
    static private final int MAX_MEMORY_WIDTH = 125;
    static private final int MAX_MEMORY_HEIGHT = 125;

    static private ImageLoader imageLoader = null;

    static public synchronized ImageLoader getInstance(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.nostra13.universalimageloader.core.ImageLoader com.chanapps.four.loader.ChanImageLoader.getInstance(android.content.Context)",context);try{if (imageLoader == null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(displayMetrics);
            int padding = ChanGridSizer.dpToPx(displayMetrics, FULL_SCREEN_IMAGE_PADDING_DP);
            final int maxWidth = ChanGridSizer.dpToPx(displayMetrics, displayMetrics.widthPixels) - 2 * padding;
            final int maxHeight = ChanGridSizer.dpToPx(displayMetrics, displayMetrics.heightPixels) - 2 * padding;
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(
                    new ImageLoaderConfiguration
                            .Builder(context)
                            /*//.memoryCacheExtraOptions(MAX_MEMORY_WIDTH, MAX_MEMORY_HEIGHT)*/
                            .discCacheExtraOptions(maxWidth, maxHeight, Bitmap.CompressFormat.JPEG, 85)
                                    /*//.imageDownloader(new ExtendedImageDownloader(context))*/
                                    /*//.threadPriority(Thread.MIN_PRIORITY+1)*/
                            .threadPoolSize(5)
                            .discCacheFileNameGenerator(new FileNameGenerator() {
                                @Override
                                public String generate(String imageUri) {
                                    com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.loader.ChanImageLoader$1.generate(java.lang.String)",this,imageUri);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.loader.ChanImageLoader$1.generate(java.lang.String)",this);{com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.core.ImageLoader com.chanapps.four.loader.ChanImageLoader.getInstance(android.content.Context)");return String.valueOf(Math.abs(imageUri.hashCode())) + ".jpg";}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.loader.ChanImageLoader$1.generate(java.lang.String)",this,throwable);throw throwable;}
                                }
                            })
                            .build());
        }
        {com.mijack.Xlog.logStaticMethodExit("com.nostra13.universalimageloader.core.ImageLoader com.chanapps.four.loader.ChanImageLoader.getInstance(android.content.Context)");return imageLoader;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.nostra13.universalimageloader.core.ImageLoader com.chanapps.four.loader.ChanImageLoader.getInstance(android.content.Context)",throwable);throw throwable;}
    }

}
