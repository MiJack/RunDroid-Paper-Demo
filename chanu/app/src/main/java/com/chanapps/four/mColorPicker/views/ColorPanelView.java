/*
 * Copyright (C) 2010 Daniel Nilsson
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


package com.chanapps.four.mColorPicker.views;

import com.chanapps.four.mColorPicker.drawables.AlphaPatternDrawable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * This class draws a panel which which will be filled with a color which can be set.
 * It can be used to show the currently selected color which you will get from
 * the {@link ColorPickerView}.
 * @author Daniel Nilsson
 *
 */
public class ColorPanelView extends View{

	/**
	 * The width in pixels of the border 
	 * surrounding the color panel.
	 */
	private final static float	BORDER_WIDTH_PX = 1;
	
	private static float mDensity = 1f;
	
	private int 		mBorderColor = 0xff6E6E6E;
	private int 		mColor = 0xff000000;
	
	private Paint		mBorderPaint;
	private Paint		mColorPaint;
	
	private RectF		mDrawingRect;
	private RectF		mColorRect;

	private AlphaPatternDrawable mAlphaPattern;
	
	
	public ColorPanelView(Context context){
		this(context, null);
	}
	
	public ColorPanelView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public ColorPanelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}
	
	private void init(){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPanelView.init()",this);try{mBorderPaint = new Paint();
		mColorPaint = new Paint();
		mDensity = getContext().getResources().getDisplayMetrics().density;com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPanelView.init()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPanelView.init()",this,throwable);throw throwable;}
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPanelView.onDraw(android.graphics.Canvas)",this,canvas);try{
		final RectF	rect = mColorRect;
				
		if(BORDER_WIDTH_PX > 0){
			mBorderPaint.setColor(mBorderColor);
			canvas.drawRect(mDrawingRect, mBorderPaint);		
		}
		
		if(mAlphaPattern != null){
			mAlphaPattern.draw(canvas);
		}
					
		mColorPaint.setColor(mColor);
		
		canvas.drawRect(rect, mColorPaint);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPanelView.onDraw(android.graphics.Canvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPanelView.onDraw(android.graphics.Canvas)",this,throwable);throw throwable;}
	}
		
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPanelView.onMeasure(int,int)",this,widthMeasureSpec,heightMeasureSpec);try{
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		setMeasuredDimension(width, height);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPanelView.onMeasure(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPanelView.onMeasure(int,int)",this,throwable);throw throwable;}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPanelView.onSizeChanged(int,int,int,int)",this,w,h,oldw,oldh);try{super.onSizeChanged(w, h, oldw, oldh);
		
		mDrawingRect = new RectF();		
		mDrawingRect.left =  getPaddingLeft();
		mDrawingRect.right  = w - getPaddingRight();
		mDrawingRect.top = getPaddingTop();
		mDrawingRect.bottom = h - getPaddingBottom();
		
		setUpColorRect();com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPanelView.onSizeChanged(int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPanelView.onSizeChanged(int,int,int,int)",this,throwable);throw throwable;}
		
	}
	
	private void setUpColorRect(){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPanelView.setUpColorRect()",this);try{final RectF	dRect = mDrawingRect;		
		
		float left = dRect.left + BORDER_WIDTH_PX;
		float top = dRect.top + BORDER_WIDTH_PX;
		float bottom = dRect.bottom - BORDER_WIDTH_PX;
		float right = dRect.right - BORDER_WIDTH_PX;
		
		mColorRect = new RectF(left,top, right, bottom);
		
		mAlphaPattern = new AlphaPatternDrawable((int)(5 * mDensity));
		
		mAlphaPattern.setBounds(Math.round(mColorRect.left), 
				Math.round(mColorRect.top), 
				Math.round(mColorRect.right), 
				Math.round(mColorRect.bottom));com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPanelView.setUpColorRect()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPanelView.setUpColorRect()",this,throwable);throw throwable;}
		
	}
	
	/**
	 * Set the color that should be shown by this view.
	 * @param color
	 */
	public void setColor(int color){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPanelView.setColor(int)",this,color);try{mColor = color;
		invalidate();com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPanelView.setColor(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPanelView.setColor(int)",this,throwable);throw throwable;}
	}
	
	/**
	 * Get the color currently show by this view.
	 * @return
	 */
	public int getColor(){
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.mColorPicker.views.ColorPanelView.getColor()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPanelView.getColor()",this);return mColor;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.mColorPicker.views.ColorPanelView.getColor()",this,throwable);throw throwable;}
	}
	
	/**
	 * Set the color of the border surrounding the panel.
	 * @param color
	 */
	public void setBorderColor(int color){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPanelView.setBorderColor(int)",this,color);try{mBorderColor = color;
		invalidate();com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPanelView.setBorderColor(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPanelView.setBorderColor(int)",this,throwable);throw throwable;}
	}

	/**
	 * Get the color of the border surrounding the panel.
	 */
	public int getBorderColor(){
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.mColorPicker.views.ColorPanelView.getBorderColor()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPanelView.getBorderColor()",this);return mBorderColor;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.mColorPicker.views.ColorPanelView.getBorderColor()",this,throwable);throw throwable;}
	}
	
}
