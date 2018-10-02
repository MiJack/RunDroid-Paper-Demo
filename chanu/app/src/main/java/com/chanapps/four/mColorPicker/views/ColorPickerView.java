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
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Displays a color picker to the user and allow them
 * to select a color. A slider for the alpha channel is
 * also available. Enable it by setting 
 * setAlphaSliderVisible(boolean) to true.
 * @author Daniel Nilsson
 */
public class ColorPickerView extends View{

	public interface OnColorChangedListener{
		public void onColorChanged(int color);		
	}
		
	private final static int	PANEL_SAT_VAL = 0;
	private final static int	PANEL_HUE = 1;
	private final static int	PANEL_ALPHA = 2;
	
	/**
	 * The width in pixels of the border 
	 * surrounding all color panels.
	 */
	private final static float	BORDER_WIDTH_PX = 1;
	
	/**
	 * The width in dp of the hue panel.
	 */
	private float 		HUE_PANEL_WIDTH = 30f;	
	/**
	 * The height in dp of the alpha panel 
	 */
	private float		ALPHA_PANEL_HEIGHT = 20f;
	/**
	 * The distance in dp between the different
	 * color panels.
	 */
	private float 		PANEL_SPACING = 10f;	
	/**
	 * The radius in dp of the color palette tracker circle.
	 */
	private float 		PALETTE_CIRCLE_TRACKER_RADIUS = 5f;
	/**
	 * The dp which the tracker of the hue or alpha panel
	 * will extend outside of its bounds.
	 */
	private float		RECTANGLE_TRACKER_OFFSET = 2f;
	
	
	private static float mDensity = 1f;
	
	private OnColorChangedListener	mListener;
	
	private Paint 		mSatValPaint;
	private Paint		mSatValTrackerPaint;
	
	private Paint		mHuePaint;
	private Paint		mHueTrackerPaint;
	
	private Paint		mAlphaPaint;
	private Paint		mAlphaTextPaint;
	
	private Paint		mBorderPaint;
		
	private Shader		mValShader;
	private Shader		mSatShader;
	private Shader		mHueShader;
	private Shader		mAlphaShader;
	
	private int			mAlpha = 0xff;
	private float		mHue = 360f;
	private float 		mSat = 0f;
	private float 		mVal = 0f;
	
	private String		mAlphaSliderText = "Alpha";	
	private int 		mSliderTrackerColor = 0xff1c1c1c;
	private int 		mBorderColor = 0xff6E6E6E;
	private boolean		mShowAlphaPanel = false;
	
	/*
	 * To remember which panel that has the "focus" when 
	 * processing hardware button data.
	 */
	private int			mLastTouchedPanel = PANEL_SAT_VAL;
	
	/**
	 * Offset from the edge we must have or else
	 * the finger tracker will get clipped when
	 * it is drawn outside of the view.
	 */
	private float 		mDrawingOffset;
	

	/*
	 * Distance form the edges of the view 
	 * of where we are allowed to draw.
	 */	
	private RectF	mDrawingRect;
			
	private RectF	mSatValRect;
	private RectF 	mHueRect;
	private RectF	mAlphaRect;
	
	private AlphaPatternDrawable	mAlphaPattern;
	
	private Point	mStartTouchPoint = null;
	
	
	public ColorPickerView(Context context){
		this(context, null);
	}
	
	public ColorPickerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
		
	private void init(){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.init()",this);try{mDensity = getContext().getResources().getDisplayMetrics().density;
		PALETTE_CIRCLE_TRACKER_RADIUS *= mDensity;		
		RECTANGLE_TRACKER_OFFSET *= mDensity;
		HUE_PANEL_WIDTH *= mDensity;
		ALPHA_PANEL_HEIGHT *= mDensity;
		PANEL_SPACING = PANEL_SPACING * mDensity;
		
		mDrawingOffset = calculateRequiredOffset();
		
		initPaintTools();
		
		/*//Needed for receiving trackball motion events.*/
		setFocusable(true);
		setFocusableInTouchMode(true);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.init()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.init()",this,throwable);throw throwable;}
	}
	
	private void initPaintTools(){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.initPaintTools()",this);try{
		mSatValPaint = new Paint();
		mSatValTrackerPaint = new Paint();
		mHuePaint = new Paint();
		mHueTrackerPaint = new Paint();
		mAlphaPaint = new Paint();
		mAlphaTextPaint = new Paint();
		mBorderPaint = new Paint();
		
		
		mSatValTrackerPaint.setStyle(Style.STROKE);
		mSatValTrackerPaint.setStrokeWidth(2f * mDensity);
		mSatValTrackerPaint.setAntiAlias(true);
		
		mHueTrackerPaint.setColor(mSliderTrackerColor);
		mHueTrackerPaint.setStyle(Style.STROKE);
		mHueTrackerPaint.setStrokeWidth(2f * mDensity);
		mHueTrackerPaint.setAntiAlias(true);
		
		mAlphaTextPaint.setColor(0xff1c1c1c);
		mAlphaTextPaint.setTextSize(14f * mDensity);
		mAlphaTextPaint.setAntiAlias(true);
		mAlphaTextPaint.setTextAlign(Align.CENTER);
		mAlphaTextPaint.setFakeBoldText(true);
	
		com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.initPaintTools()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.initPaintTools()",this,throwable);throw throwable;}
	}
	
	private float calculateRequiredOffset(){		
		com.mijack.Xlog.logMethodEnter("float com.chanapps.four.mColorPicker.views.ColorPickerView.calculateRequiredOffset()",this);try{float offset = Math.max(PALETTE_CIRCLE_TRACKER_RADIUS, RECTANGLE_TRACKER_OFFSET);
		offset = Math.max(offset, BORDER_WIDTH_PX * mDensity);
		
		{com.mijack.Xlog.logMethodExit("float com.chanapps.four.mColorPicker.views.ColorPickerView.calculateRequiredOffset()",this);return offset * 1.5f;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.chanapps.four.mColorPicker.views.ColorPickerView.calculateRequiredOffset()",this,throwable);throw throwable;}	
	}
	
	private int[] buildHueColorArray(){
		com.mijack.Xlog.logMethodEnter("[int com.chanapps.four.mColorPicker.views.ColorPickerView.buildHueColorArray()",this);try{
		int[] hue = new int[361];
		
		int count = 0;
		for(int i = hue.length -1; i >= 0; i--, count++){
			hue[count] = Color.HSVToColor(new float[]{i, 1f, 1f});
		}
		
		{com.mijack.Xlog.logMethodExit("[int com.chanapps.four.mColorPicker.views.ColorPickerView.buildHueColorArray()",this);return hue;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[int com.chanapps.four.mColorPicker.views.ColorPickerView.buildHueColorArray()",this,throwable);throw throwable;}
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.onDraw(android.graphics.Canvas)",this,canvas);try{
		if(mDrawingRect.width() <= 0 || mDrawingRect.height() <= 0) {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.onDraw(android.graphics.Canvas)",this);return;}}
		
		drawSatValPanel(canvas);	
		drawHuePanel(canvas);
		drawAlphaPanel(canvas);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.onDraw(android.graphics.Canvas)",this,throwable);throw throwable;}
		
	}
	
	private void drawSatValPanel(Canvas canvas){

		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.drawSatValPanel(android.graphics.Canvas)",this,canvas);try{final RectF	rect = mSatValRect;
				
		if(BORDER_WIDTH_PX > 0){			
			mBorderPaint.setColor(mBorderColor);
			canvas.drawRect(mDrawingRect.left, mDrawingRect.top, rect.right + BORDER_WIDTH_PX, rect.bottom + BORDER_WIDTH_PX, mBorderPaint);		
		}
			
		if (mValShader == null) {
			mValShader = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, 
					0xffffffff, 0xff000000, TileMode.CLAMP);
		}
		
		int rgb = Color.HSVToColor(new float[]{mHue,1f,1f});
	
		mSatShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top, 
				0xffffffff, rgb, TileMode.CLAMP);
		ComposeShader mShader = new ComposeShader(mValShader, mSatShader, PorterDuff.Mode.MULTIPLY);
		mSatValPaint.setShader(mShader);
		
		canvas.drawRect(rect, mSatValPaint);
	
		Point p = satValToPoint(mSat, mVal);
			
		mSatValTrackerPaint.setColor(0xff000000);
		canvas.drawCircle(p.x, p.y, PALETTE_CIRCLE_TRACKER_RADIUS - 1f * mDensity, mSatValTrackerPaint);
				
		mSatValTrackerPaint.setColor(0xffdddddd);
		canvas.drawCircle(p.x, p.y, PALETTE_CIRCLE_TRACKER_RADIUS, mSatValTrackerPaint);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.drawSatValPanel(android.graphics.Canvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.drawSatValPanel(android.graphics.Canvas)",this,throwable);throw throwable;}
			
	}
	
	private void drawHuePanel(Canvas canvas){
	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.drawHuePanel(android.graphics.Canvas)",this,canvas);try{
		final RectF rect = mHueRect;
		
		if(BORDER_WIDTH_PX > 0){
			mBorderPaint.setColor(mBorderColor);
			canvas.drawRect(rect.left - BORDER_WIDTH_PX, 
					rect.top - BORDER_WIDTH_PX, 
					rect.right + BORDER_WIDTH_PX, 
					rect.bottom + BORDER_WIDTH_PX, 
					mBorderPaint);		
		}

		if (mHueShader == null) {
			mHueShader = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, buildHueColorArray(), null, TileMode.CLAMP);
			mHuePaint.setShader(mHueShader);
		}
	
		canvas.drawRect(rect, mHuePaint);
		
		float rectHeight = 4 * mDensity / 2;
				
		Point p = hueToPoint(mHue);
				
		RectF r = new RectF();
		r.left = rect.left - RECTANGLE_TRACKER_OFFSET;
		r.right = rect.right + RECTANGLE_TRACKER_OFFSET;
		r.top = p.y - rectHeight;
		r.bottom = p.y + rectHeight;
		
		
		canvas.drawRoundRect(r, 2, 2, mHueTrackerPaint);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.drawHuePanel(android.graphics.Canvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.drawHuePanel(android.graphics.Canvas)",this,throwable);throw throwable;}
		
	}
	
	private void drawAlphaPanel(Canvas canvas){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.drawAlphaPanel(android.graphics.Canvas)",this,canvas);try{
		if(!mShowAlphaPanel || mAlphaRect == null || mAlphaPattern == null) {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.drawAlphaPanel(android.graphics.Canvas)",this);return;}}
		
		final RectF rect = mAlphaRect;
		
		if(BORDER_WIDTH_PX > 0){
			mBorderPaint.setColor(mBorderColor);
			canvas.drawRect(rect.left - BORDER_WIDTH_PX, 
					rect.top - BORDER_WIDTH_PX, 
					rect.right + BORDER_WIDTH_PX, 
					rect.bottom + BORDER_WIDTH_PX, 
					mBorderPaint);		
		}
		
		
		mAlphaPattern.draw(canvas);
		
		float[] hsv = new float[]{mHue,mSat,mVal};
		int color = Color.HSVToColor(hsv);
		int acolor = Color.HSVToColor(0, hsv);
		
		mAlphaShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top, 
				color, acolor, TileMode.CLAMP);
		
		
		mAlphaPaint.setShader(mAlphaShader);
		
		canvas.drawRect(rect, mAlphaPaint);
		
		if(mAlphaSliderText != null && mAlphaSliderText!= ""){
			canvas.drawText(mAlphaSliderText, rect.centerX(), rect.centerY() + 4 * mDensity, mAlphaTextPaint);
		}
		
		float rectWidth = 4 * mDensity / 2;
		
		Point p = alphaToPoint(mAlpha);
				
		RectF r = new RectF();
		r.left = p.x - rectWidth;
		r.right = p.x + rectWidth;
		r.top = rect.top - RECTANGLE_TRACKER_OFFSET;
		r.bottom = rect.bottom + RECTANGLE_TRACKER_OFFSET;
		
		canvas.drawRoundRect(r, 2, 2, mHueTrackerPaint);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.drawAlphaPanel(android.graphics.Canvas)",this,throwable);throw throwable;}
		
	}
	
	
	private Point hueToPoint(float hue){
		com.mijack.Xlog.logMethodEnter("android.graphics.Point com.chanapps.four.mColorPicker.views.ColorPickerView.hueToPoint(float)",this,hue);try{
		final RectF rect = mHueRect;
		final float height = rect.height();
		
		Point p = new Point();
			
		p.y = (int) (height - (hue * height / 360f) + rect.top);
		p.x = (int) rect.left;
		
		{com.mijack.Xlog.logMethodExit("android.graphics.Point com.chanapps.four.mColorPicker.views.ColorPickerView.hueToPoint(float)",this);return p;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Point com.chanapps.four.mColorPicker.views.ColorPickerView.hueToPoint(float)",this,throwable);throw throwable;}		
	}
	
	private Point satValToPoint(float sat, float val){
		com.mijack.Xlog.logMethodEnter("android.graphics.Point com.chanapps.four.mColorPicker.views.ColorPickerView.satValToPoint(float,float)",this,sat,val);try{
		final RectF rect = mSatValRect;
		final float height = rect.height();
		final float width = rect.width();
		
		Point p = new Point();
		
		p.x = (int) (sat * width + rect.left);
		p.y = (int) ((1f - val) * height + rect.top);
		
		{com.mijack.Xlog.logMethodExit("android.graphics.Point com.chanapps.four.mColorPicker.views.ColorPickerView.satValToPoint(float,float)",this);return p;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Point com.chanapps.four.mColorPicker.views.ColorPickerView.satValToPoint(float,float)",this,throwable);throw throwable;}
	}
	
	private Point alphaToPoint(int alpha){
		com.mijack.Xlog.logMethodEnter("android.graphics.Point com.chanapps.four.mColorPicker.views.ColorPickerView.alphaToPoint(int)",this,alpha);try{
		final RectF rect = mAlphaRect;
		final float width = rect.width();
		
		Point p = new Point();
		
		p.x = (int) (width - (alpha * width / 0xff) + rect.left);
		p.y = (int) rect.top;
		
		{com.mijack.Xlog.logMethodExit("android.graphics.Point com.chanapps.four.mColorPicker.views.ColorPickerView.alphaToPoint(int)",this);return p;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Point com.chanapps.four.mColorPicker.views.ColorPickerView.alphaToPoint(int)",this,throwable);throw throwable;}
	
	}
	
	private float[] pointToSatVal(float x, float y){
	com.mijack.Xlog.logMethodEnter("[float com.chanapps.four.mColorPicker.views.ColorPickerView.pointToSatVal(float,float)",this,x,y);try{
		final RectF rect = mSatValRect;
		float[] result = new float[2];
		
		float width = rect.width();
		float height = rect.height();
		
		if (x < rect.left){
			x = 0f;
		}
		else if(x > rect.right){
			x = width;
		}
		else{
			x = x - rect.left;
		}
				
		if (y < rect.top){
			y = 0f;
		}
		else if(y > rect.bottom){
			y = height;
		}
		else{
			y = y - rect.top;
		}
		
			
		result[0] = 1.f / width * x;
		result[1] = 1.f - (1.f / height * y);
		
		{com.mijack.Xlog.logMethodExit("[float com.chanapps.four.mColorPicker.views.ColorPickerView.pointToSatVal(float,float)",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[float com.chanapps.four.mColorPicker.views.ColorPickerView.pointToSatVal(float,float)",this,throwable);throw throwable;}	
	}
	
	private float pointToHue(float y){		
		com.mijack.Xlog.logMethodEnter("float com.chanapps.four.mColorPicker.views.ColorPickerView.pointToHue(float)",this,y);try{
		final RectF rect = mHueRect;
		
		float height = rect.height();
		
		if (y < rect.top){
			y = 0f;
		}
		else if(y > rect.bottom){
			y = height;
		}
		else{
			y = y - rect.top;
		}
		
		{com.mijack.Xlog.logMethodExit("float com.chanapps.four.mColorPicker.views.ColorPickerView.pointToHue(float)",this);return 360f - (y * 360f / height);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.chanapps.four.mColorPicker.views.ColorPickerView.pointToHue(float)",this,throwable);throw throwable;}
	}
	
	private int pointToAlpha(int x){
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.mColorPicker.views.ColorPickerView.pointToAlpha(int)",this,x);try{
		final RectF rect = mAlphaRect;
		final int width = (int) rect.width();
		
		if(x < rect.left){
			x = 0;
		}
		else if(x > rect.right){
			x = width;
		}
		else{
			x = x - (int)rect.left;
		}
		
		{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPickerView.pointToAlpha(int)",this);return 0xff - (x * 0xff / width);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.mColorPicker.views.ColorPickerView.pointToAlpha(int)",this,throwable);throw throwable;}
		
	}
	
			
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.onTrackballEvent(android.view.MotionEvent)",this,event);try{
		float x = event.getX();
		float y = event.getY();
		
		boolean update = false;
		
		
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			
			switch(mLastTouchedPanel){
			
			case PANEL_SAT_VAL:
				
				float sat, val;
				
				sat = mSat + x/50f;
				val = mVal - y/50f; 
				
				if(sat < 0f){
					sat = 0f;
				}
				else if(sat > 1f){
					sat = 1f;
				}
				
				if(val < 0f){
					val = 0f;
				}
				else if(val > 1f){
					val = 1f;
				}
				
				mSat = sat;
				mVal = val;
				
				update = true;
				
				break;
				
			case PANEL_HUE:
				
				float hue = mHue - y * 10f;
				
				if(hue < 0f){
					hue = 0f;
				}
				else if(hue > 360f){
					hue = 360f;
				}
				
				mHue = hue;
						
				update = true;
				
				break;
				
			case PANEL_ALPHA:
				
				if(!mShowAlphaPanel || mAlphaRect == null){
					update = false;
				}
				else{
					
					int alpha = (int) (mAlpha - x*10);
					
					if(alpha < 0){
						alpha = 0;
					}
					else if(alpha > 0xff){
						alpha = 0xff;
					}
					
					mAlpha = alpha;
					
					
					update = true;
				}
				
				break;
			}
			
			
		}
		
		
		if(update){
			
			if(mListener != null){
				mListener.onColorChanged(Color.HSVToColor(mAlpha, new float[]{mHue, mSat, mVal}));
			}
			
			invalidate();
			{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.onTrackballEvent(android.view.MotionEvent)",this);return true;}
		}
		
	
		{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.onTrackballEvent(android.view.MotionEvent)",this);return super.onTrackballEvent(event);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.onTrackballEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
	}
		
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.onTouchEvent(android.view.MotionEvent)",this,event);try{
		boolean update = false;
				
		switch(event.getAction()){
		
		case MotionEvent.ACTION_DOWN:
			
			mStartTouchPoint = new Point((int)event.getX(), (int)event.getY());
			
			update = moveTrackersIfNeeded(event);
		
			break;
						
		case MotionEvent.ACTION_MOVE:
			
			update = moveTrackersIfNeeded(event);
		
			break;
			
		case MotionEvent.ACTION_UP:
			
			mStartTouchPoint = null;
					
			update = moveTrackersIfNeeded(event);
			
			break;
	
		}
		
		if(update){
			
			if(mListener != null){
				mListener.onColorChanged(Color.HSVToColor(mAlpha, new float[]{mHue, mSat, mVal}));
			}
			
			invalidate();
			{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.onTouchEvent(android.view.MotionEvent)",this);return true;}
		}
		
	
		{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.onTouchEvent(android.view.MotionEvent)",this);return super.onTouchEvent(event);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.onTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
	}
		
	private boolean moveTrackersIfNeeded(MotionEvent event){
		com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.moveTrackersIfNeeded(android.view.MotionEvent)",this,event);try{
		if(mStartTouchPoint == null) {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.moveTrackersIfNeeded(android.view.MotionEvent)",this);return false;}}
		
		boolean update = false;
		
		int startX = mStartTouchPoint.x;
		int startY = mStartTouchPoint.y;
		
		
		if(mHueRect.contains(startX, startY)){
			mLastTouchedPanel = PANEL_HUE;
			
			mHue = pointToHue(event.getY());
						
			update = true;
		}
		else if(mSatValRect.contains(startX, startY)){
							
			mLastTouchedPanel = PANEL_SAT_VAL;
			
			float[] result = pointToSatVal(event.getX(), event.getY());
			
			mSat = result[0];
			mVal = result[1];

			update = true;
		}
		else if(mAlphaRect != null && mAlphaRect.contains(startX, startY)){
			
			mLastTouchedPanel = PANEL_ALPHA;
			
			mAlpha = pointToAlpha((int)event.getX());
			
			update = true;
		}
		
		
		{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.moveTrackersIfNeeded(android.view.MotionEvent)",this);return update;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.mColorPicker.views.ColorPickerView.moveTrackersIfNeeded(android.view.MotionEvent)",this,throwable);throw throwable;}
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.onMeasure(int,int)",this,widthMeasureSpec,heightMeasureSpec);try{
		int width = 0;
		int height = 0;
			
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		int widthAllowed = MeasureSpec.getSize(widthMeasureSpec);
		int heightAllowed = MeasureSpec.getSize(heightMeasureSpec);
		
		
		widthAllowed = chooseWidth(widthMode, widthAllowed);
		heightAllowed = chooseHeight(heightMode, heightAllowed);
		
		
		if(!mShowAlphaPanel){
			height = (int) (widthAllowed - PANEL_SPACING - HUE_PANEL_WIDTH);
			
			/*//If calculated height (based on the width) is more than the allowed height.*/
			if(height > heightAllowed){
				height = heightAllowed;
				width = (int) (height + PANEL_SPACING + HUE_PANEL_WIDTH);
			}
			else{
				width = widthAllowed;
			}
		}
		else{
			
			width = (int) (heightAllowed - ALPHA_PANEL_HEIGHT + HUE_PANEL_WIDTH);
			
			if(width > widthAllowed){
				width = widthAllowed;
				height = (int) (widthAllowed - HUE_PANEL_WIDTH + ALPHA_PANEL_HEIGHT);
			}
			else{
				height = heightAllowed;
			}
			
			
		}
		
	
		setMeasuredDimension(width, height);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.onMeasure(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.onMeasure(int,int)",this,throwable);throw throwable;}
	}
	
	private int chooseWidth(int mode, int size){
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.mColorPicker.views.ColorPickerView.chooseWidth(int,int)",this,mode,size);try{if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPickerView.chooseWidth(int,int)",this);return size;}
		} else { /*// (mode == MeasureSpec.UNSPECIFIED)*/
			{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPickerView.chooseWidth(int,int)",this);return getPrefferedWidth();}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.mColorPicker.views.ColorPickerView.chooseWidth(int,int)",this,throwable);throw throwable;} 
	}
	
	private int chooseHeight(int mode, int size){
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.mColorPicker.views.ColorPickerView.chooseHeight(int,int)",this,mode,size);try{if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPickerView.chooseHeight(int,int)",this);return size;}
		} else { /*// (mode == MeasureSpec.UNSPECIFIED)*/
			{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPickerView.chooseHeight(int,int)",this);return getPrefferedHeight();}
		}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.mColorPicker.views.ColorPickerView.chooseHeight(int,int)",this,throwable);throw throwable;} 
	}
	
	private int getPrefferedWidth(){
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.mColorPicker.views.ColorPickerView.getPrefferedWidth()",this);try{
		int width = getPrefferedHeight();
		
		if(mShowAlphaPanel){
			width -= (PANEL_SPACING + ALPHA_PANEL_HEIGHT);
		}
		
		
		{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPickerView.getPrefferedWidth()",this);return (int) (width + HUE_PANEL_WIDTH + PANEL_SPACING);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.mColorPicker.views.ColorPickerView.getPrefferedWidth()",this,throwable);throw throwable;}
		
	}
	
	private int getPrefferedHeight(){
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.mColorPicker.views.ColorPickerView.getPrefferedHeight()",this);try{
		int height = (int)(200 * mDensity);
		
		if(mShowAlphaPanel){
			height += PANEL_SPACING + ALPHA_PANEL_HEIGHT;
		}
		
		{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPickerView.getPrefferedHeight()",this);return height;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.mColorPicker.views.ColorPickerView.getPrefferedHeight()",this,throwable);throw throwable;}
	}
	
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.onSizeChanged(int,int,int,int)",this,w,h,oldw,oldh);try{super.onSizeChanged(w, h, oldw, oldh);
		
		mDrawingRect = new RectF();		
		mDrawingRect.left = mDrawingOffset + getPaddingLeft();
		mDrawingRect.right  = w - mDrawingOffset - getPaddingRight();
		mDrawingRect.top = mDrawingOffset + getPaddingTop();
		mDrawingRect.bottom = h - mDrawingOffset - getPaddingBottom();
		
		setUpSatValRect();
		setUpHueRect();
		setUpAlphaRect();com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.onSizeChanged(int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.onSizeChanged(int,int,int,int)",this,throwable);throw throwable;}
	}
	
	private void setUpSatValRect(){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.setUpSatValRect()",this);try{
		final RectF	dRect = mDrawingRect;		
		float panelSide = dRect.height() - BORDER_WIDTH_PX * 2;
		
		if(mShowAlphaPanel){
			panelSide -= PANEL_SPACING + ALPHA_PANEL_HEIGHT;
		}
		
		float left = dRect.left + BORDER_WIDTH_PX;
		float top = dRect.top + BORDER_WIDTH_PX;
		float bottom = top + panelSide;
		float right = left + panelSide;
		
		mSatValRect = new RectF(left,top, right, bottom);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.setUpSatValRect()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.setUpSatValRect()",this,throwable);throw throwable;}
	}
	
	private void setUpHueRect(){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.setUpHueRect()",this);try{final RectF	dRect = mDrawingRect;		
		
		float left = dRect.right - HUE_PANEL_WIDTH + BORDER_WIDTH_PX;
		float top = dRect.top + BORDER_WIDTH_PX;
		float bottom = dRect.bottom - BORDER_WIDTH_PX - (mShowAlphaPanel ? (PANEL_SPACING + ALPHA_PANEL_HEIGHT) : 0);
		float right = dRect.right - BORDER_WIDTH_PX;
		
		mHueRect = new RectF(left, top, right, bottom);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.setUpHueRect()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.setUpHueRect()",this,throwable);throw throwable;}
	}

	private void setUpAlphaRect(){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.setUpAlphaRect()",this);try{
		if(!mShowAlphaPanel) {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.setUpAlphaRect()",this);return;}}
		
		final RectF	dRect = mDrawingRect;		
		
		float left = dRect.left + BORDER_WIDTH_PX;
		float top = dRect.bottom - ALPHA_PANEL_HEIGHT + BORDER_WIDTH_PX;
		float bottom = dRect.bottom - BORDER_WIDTH_PX;
		float right = dRect.right - BORDER_WIDTH_PX;
		
		mAlphaRect = new RectF(left, top, right, bottom);	
		
	
		mAlphaPattern = new AlphaPatternDrawable((int) (5 * mDensity));
		mAlphaPattern.setBounds(Math.round(mAlphaRect.left), Math
				.round(mAlphaRect.top), Math.round(mAlphaRect.right), Math
				.round(mAlphaRect.bottom));
		
		}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.setUpAlphaRect()",this,throwable);throw throwable;}
		
	}
	
	
	/**
	 * Set a OnColorChangedListener to get notified when the color
	 * selected by the user has changed.
	 * @param listener
	 */
	public void setOnColorChangedListener(OnColorChangedListener listener){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.setOnColorChangedListener(OnColorChangedListener)",this,listener);try{mListener = listener;com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.setOnColorChangedListener(OnColorChangedListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.setOnColorChangedListener(OnColorChangedListener)",this,throwable);throw throwable;}
	}
	
	/**
	 * Set the color of the border surrounding all panels.
	 * @param color
	 */
	public void setBorderColor(int color){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.setBorderColor(int)",this,color);try{mBorderColor = color;
		invalidate();com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.setBorderColor(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.setBorderColor(int)",this,throwable);throw throwable;}
	}
	
	/**
	 * Get the color of the border surrounding all panels.
	 */
	public int getBorderColor(){
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.mColorPicker.views.ColorPickerView.getBorderColor()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPickerView.getBorderColor()",this);return mBorderColor;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.mColorPicker.views.ColorPickerView.getBorderColor()",this,throwable);throw throwable;}
	}
	
	/**
	 * Get the current color this view is showing.
	 * @return the current color.
	 */
	public int getColor(){
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.mColorPicker.views.ColorPickerView.getColor()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPickerView.getColor()",this);return Color.HSVToColor(mAlpha, new float[]{mHue,mSat,mVal});}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.mColorPicker.views.ColorPickerView.getColor()",this,throwable);throw throwable;}
	}
	
	/**
	 * Set the color the view should show.
	 * @param color The color that should be selected.
	 */
	public void setColor(int color){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.setColor(int)",this,color);try{setColor(color, false);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.setColor(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.setColor(int)",this,throwable);throw throwable;}
	}
	
	/**
	 * Set the color this view should show.
	 * @param color The color that should be selected.
	 * @param callback If you want to get a callback to
	 * your OnColorChangedListener.
	 */
	public void setColor(int color, boolean callback){
	com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.setColor(int,boolean)",this,color,callback);try{
		int alpha = Color.alpha(color);
		int red = Color.red(color);
		int blue = Color.blue(color);
		int green = Color.green(color);
		
		float[] hsv = new float[3];
		
		Color.RGBToHSV(red, green, blue, hsv);

		mAlpha = alpha;
		mHue = hsv[0];
		mSat = hsv[1];
		mVal = hsv[2];
		
		if(callback && mListener != null){			
			mListener.onColorChanged(Color.HSVToColor(mAlpha, new float[]{mHue, mSat, mVal}));				
		}
		
		invalidate();com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.setColor(int,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.setColor(int,boolean)",this,throwable);throw throwable;}
	}
	
	/**
	 * Get the drawing offset of the color picker view.
	 * The drawing offset is the distance from the side of
	 * a panel to the side of the view minus the padding.
	 * Useful if you want to have your own panel below showing
	 * the currently selected color and want to align it perfectly.
	 * @return The offset in pixels.
	 */
	public float getDrawingOffset(){
		com.mijack.Xlog.logMethodEnter("float com.chanapps.four.mColorPicker.views.ColorPickerView.getDrawingOffset()",this);try{com.mijack.Xlog.logMethodExit("float com.chanapps.four.mColorPicker.views.ColorPickerView.getDrawingOffset()",this);return mDrawingOffset;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.chanapps.four.mColorPicker.views.ColorPickerView.getDrawingOffset()",this,throwable);throw throwable;}
	}
	
	/**
	 * Set if the user is allowed to adjust the alpha panel. Default is false.
	 * If it is set to false no alpha will be set.
	 * @param visible
	 */
	public void setAlphaSliderVisible(boolean visible){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.setAlphaSliderVisible(boolean)",this,visible);try{
		if(mShowAlphaPanel != visible){
			mShowAlphaPanel = visible;
			
			/*
			 * Reset all shader to force a recreation. 
			 * Otherwise they will not look right after 
			 * the size of the view has changed.
			 */
			mValShader = null;
			mSatShader = null;
			mHueShader = null;
			mAlphaShader = null;;
			
			requestLayout();
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.setAlphaSliderVisible(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.setAlphaSliderVisible(boolean)",this,throwable);throw throwable;}
		
	}
	
	public void setSliderTrackerColor(int color){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.setSliderTrackerColor(int)",this,color);try{mSliderTrackerColor = color;

		mHueTrackerPaint.setColor(mSliderTrackerColor);		
		
		invalidate();com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.setSliderTrackerColor(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.setSliderTrackerColor(int)",this,throwable);throw throwable;}
	}
	
	public int getSliderTrackerColor(){
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.mColorPicker.views.ColorPickerView.getSliderTrackerColor()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.views.ColorPickerView.getSliderTrackerColor()",this);return mSliderTrackerColor;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.mColorPicker.views.ColorPickerView.getSliderTrackerColor()",this,throwable);throw throwable;}
	}
	
	/**
	 * Set the text that should be shown in the 
	 * alpha slider. Set to null to disable text.
	 * @param res string resource id.
	 */
	public void setAlphaSliderText(int res){		
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.setAlphaSliderText(int)",this,res);try{String text = getContext().getString(res);
		setAlphaSliderText(text);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.setAlphaSliderText(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.setAlphaSliderText(int)",this,throwable);throw throwable;}
	}
	
	/**
	 * Set the text that should be shown in the 
	 * alpha slider. Set to null to disable text.
	 * @param text Text that should be shown.
	 */
	public void setAlphaSliderText(String text){
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.views.ColorPickerView.setAlphaSliderText(java.lang.String)",this,text);try{mAlphaSliderText = text;
		invalidate();com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.views.ColorPickerView.setAlphaSliderText(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.views.ColorPickerView.setAlphaSliderText(java.lang.String)",this,throwable);throw throwable;}
	}

	/**
	 * Get the current value of the text
	 * that will be shown in the alpha
	 * slider.
	 * @return
	 */
	public String getAlphaSliderText(){
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.mColorPicker.views.ColorPickerView.getAlphaSliderText()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.mColorPicker.views.ColorPickerView.getAlphaSliderText()",this);return mAlphaSliderText;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.mColorPicker.views.ColorPickerView.getAlphaSliderText()",this,throwable);throw throwable;}
	}
}
