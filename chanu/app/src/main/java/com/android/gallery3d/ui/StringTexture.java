/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;

/*// StringTexture is a texture shows the content of a specified String.*/
/*//*/
/*// To create a StringTexture, use the newInstance() method and specify*/
/*// the String, the font size, and the color.*/
class StringTexture extends CanvasTexture {
    private final String mText;
    private final TextPaint mPaint;
    private final FontMetricsInt mMetrics;

    private StringTexture(String text, TextPaint paint,
            FontMetricsInt metrics, int width, int height) {
        super(width, height);
        mText = text;
        mPaint = paint;
        mMetrics = metrics;
    }

    public static TextPaint getDefaultPaint(float textSize, int color) {
        com.mijack.Xlog.logStaticMethodEnter("android.text.TextPaint com.android.gallery3d.ui.StringTexture.getDefaultPaint(float,int)",textSize,color);try{TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setShadowLayer(2f, 0f, 0f, Color.BLACK);
        {com.mijack.Xlog.logStaticMethodExit("android.text.TextPaint com.android.gallery3d.ui.StringTexture.getDefaultPaint(float,int)");return paint;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.text.TextPaint com.android.gallery3d.ui.StringTexture.getDefaultPaint(float,int)",throwable);throw throwable;}
    }

    public static StringTexture newInstance(
            String text, float textSize, int color) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.ui.StringTexture com.android.gallery3d.ui.StringTexture.newInstance(java.lang.String,float,int)",text,textSize,color);try{com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.ui.StringTexture com.android.gallery3d.ui.StringTexture.newInstance(java.lang.String,float,int)");return newInstance(text, getDefaultPaint(textSize, color));}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.ui.StringTexture com.android.gallery3d.ui.StringTexture.newInstance(java.lang.String,float,int)",throwable);throw throwable;}
    }

    public static StringTexture newInstance(
            String text, float textSize, int color,
            float lengthLimit, boolean isBold) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.ui.StringTexture com.android.gallery3d.ui.StringTexture.newInstance(java.lang.String,float,int,float,boolean)",text,textSize,color,lengthLimit,isBold);try{TextPaint paint = getDefaultPaint(textSize, color);
        if (isBold) {
            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        text = TextUtils.ellipsize(
                text, paint, lengthLimit, TextUtils.TruncateAt.END).toString();
        {com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.ui.StringTexture com.android.gallery3d.ui.StringTexture.newInstance(java.lang.String,float,int,float,boolean)");return newInstance(text, paint);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.ui.StringTexture com.android.gallery3d.ui.StringTexture.newInstance(java.lang.String,float,int,float,boolean)",throwable);throw throwable;}
    }

    private static StringTexture newInstance(String text, TextPaint paint) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.ui.StringTexture com.android.gallery3d.ui.StringTexture.newInstance(java.lang.String,android.text.TextPaint)",text,paint);try{FontMetricsInt metrics = paint.getFontMetricsInt();
        int width = (int) Math.ceil(paint.measureText(text));
        int height = metrics.bottom - metrics.top;
        /*// The texture size needs to be at least 1x1.*/
        if (width <= 0) {width = 1;}
        if (height <= 0) {height = 1;}
        {com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.ui.StringTexture com.android.gallery3d.ui.StringTexture.newInstance(java.lang.String,android.text.TextPaint)");return new StringTexture(text, paint, metrics, width, height);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.ui.StringTexture com.android.gallery3d.ui.StringTexture.newInstance(java.lang.String,android.text.TextPaint)",throwable);throw throwable;}
    }

    @Override
    protected void onDraw(Canvas canvas, Bitmap backing) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.StringTexture.onDraw(android.graphics.Canvas,android.graphics.Bitmap)",this,canvas,backing);try{canvas.translate(0, -mMetrics.ascent);
        canvas.drawText(mText, 0, 0, mPaint);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.StringTexture.onDraw(android.graphics.Canvas,android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.StringTexture.onDraw(android.graphics.Canvas,android.graphics.Bitmap)",this,throwable);throw throwable;}
    }
}
