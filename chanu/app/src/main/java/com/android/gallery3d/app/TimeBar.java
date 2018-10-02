/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.gallery3d.app;

import com.android.gallery3d.common.Utils;
import com.chanapps.four.gallery3d.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * The time bar view, which includes the current and total time, the progress bar,
 * and the scrubber.
 */
public class TimeBar extends View {

  public interface Listener {
    void onScrubbingStart();
    void onScrubbingMove(int time);
    void onScrubbingEnd(int time);
  }

  /*// Padding around the scrubber to increase its touch target*/
  private static final int SCRUBBER_PADDING_IN_DP = 10;

  /*// The total padding, top plus bottom*/
  private static final int V_PADDING_IN_DP = 30;

  private static final int TEXT_SIZE_IN_DP = 14;

  private final Listener listener;

  /*// the bars we use for displaying the progress*/
  private final Rect progressBar;
  private final Rect playedBar;

  private final Paint progressPaint;
  private final Paint playedPaint;
  private final Paint timeTextPaint;

  private final Bitmap scrubber;
  private final int scrubberPadding; /*// adds some touch tolerance around the scrubber*/

  private int scrubberLeft;
  private int scrubberTop;
  private int scrubberCorrection;
  private boolean scrubbing;
  private boolean showTimes;
  private boolean showScrubber;

  private int totalTime;
  private int currentTime;

  private final Rect timeBounds;

  private int vPaddingInPx;

  public TimeBar(Context context, Listener listener) {
    super(context);
    this.listener = Utils.checkNotNull(listener);

    showTimes = true;
    showScrubber = true;

    progressBar = new Rect();
    playedBar = new Rect();

    progressPaint = new Paint();
    progressPaint.setColor(0xFF808080);
    playedPaint = new Paint();
    playedPaint.setColor(0xFFFFFFFF);

    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    float textSizeInPx = metrics.density * TEXT_SIZE_IN_DP;
    timeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    timeTextPaint.setColor(0xFFCECECE);
    timeTextPaint.setTextSize(textSizeInPx);
    timeTextPaint.setTextAlign(Paint.Align.CENTER);

    timeBounds = new Rect();
    timeTextPaint.getTextBounds("0:00:00", 0, 7, timeBounds);

    scrubber = BitmapFactory.decodeResource(getResources(), R.drawable.scrubber_knob);
    scrubberPadding = (int) (metrics.density * SCRUBBER_PADDING_IN_DP);

    vPaddingInPx = (int) (metrics.density * V_PADDING_IN_DP);
  }

  private void update() {
    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.TimeBar.update()",this);try{playedBar.set(progressBar);

    if (totalTime > 0) {
      playedBar.right =
          playedBar.left + (int) ((progressBar.width() * (long) currentTime) / totalTime);
    } else {
      playedBar.right = progressBar.left;
    }

    if (!scrubbing) {
      scrubberLeft = playedBar.right - scrubber.getWidth() / 2;
    }
    invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.TimeBar.update()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.TimeBar.update()",this,throwable);throw throwable;}
  }

  /**
   * @return the preferred height of this view, including invisible padding
   */
  public int getPreferredHeight() {
    com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.TimeBar.getPreferredHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.TimeBar.getPreferredHeight()",this);return timeBounds.height() + vPaddingInPx + scrubberPadding;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.TimeBar.getPreferredHeight()",this,throwable);throw throwable;}
  }

  /**
   * @return the height of the time bar, excluding invisible padding
   */
  public int getBarHeight() {
    com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.TimeBar.getBarHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.TimeBar.getBarHeight()",this);return timeBounds.height() + vPaddingInPx;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.TimeBar.getBarHeight()",this,throwable);throw throwable;}
  }

  public void setTime(int currentTime, int totalTime) {
    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.TimeBar.setTime(int,int)",this,currentTime,totalTime);try{if (this.currentTime == currentTime && this.totalTime == totalTime) {
        {com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.TimeBar.setTime(int,int)",this);return;}
    }
    this.currentTime = currentTime;
    this.totalTime = totalTime;
    update();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.TimeBar.setTime(int,int)",this,throwable);throw throwable;}
  }

  public void setShowTimes(boolean showTimes) {
    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.TimeBar.setShowTimes(boolean)",this,showTimes);try{this.showTimes = showTimes;
    requestLayout();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.TimeBar.setShowTimes(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.TimeBar.setShowTimes(boolean)",this,throwable);throw throwable;}
  }

  public void resetTime() {
    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.TimeBar.resetTime()",this);try{setTime(0, 0);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.TimeBar.resetTime()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.TimeBar.resetTime()",this,throwable);throw throwable;}
  }

  public void setShowScrubber(boolean showScrubber) {
    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.TimeBar.setShowScrubber(boolean)",this,showScrubber);try{this.showScrubber = showScrubber;
    if (!showScrubber && scrubbing) {
      listener.onScrubbingEnd(getScrubberTime());
      scrubbing = false;
    }
    requestLayout();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.TimeBar.setShowScrubber(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.TimeBar.setShowScrubber(boolean)",this,throwable);throw throwable;}
  }

  private boolean inScrubber(float x, float y) {
    com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.TimeBar.inScrubber(float,float)",this,x,y);try{int scrubberRight = scrubberLeft + scrubber.getWidth();
    int scrubberBottom = scrubberTop + scrubber.getHeight();
    {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.TimeBar.inScrubber(float,float)",this);return scrubberLeft - scrubberPadding < x && x < scrubberRight + scrubberPadding
        && scrubberTop - scrubberPadding < y && y < scrubberBottom + scrubberPadding;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.TimeBar.inScrubber(float,float)",this,throwable);throw throwable;}
  }

  private void clampScrubber() {
    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.TimeBar.clampScrubber()",this);try{int half = scrubber.getWidth() / 2;
    int max = progressBar.right - half;
    int min = progressBar.left - half;
    scrubberLeft = Math.min(max, Math.max(min, scrubberLeft));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.TimeBar.clampScrubber()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.TimeBar.clampScrubber()",this,throwable);throw throwable;}
  }

  private int getScrubberTime() {
    com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.TimeBar.getScrubberTime()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.TimeBar.getScrubberTime()",this);return (int) ((long) (scrubberLeft + scrubber.getWidth() / 2 - progressBar.left)
        * totalTime / progressBar.width());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.TimeBar.getScrubberTime()",this,throwable);throw throwable;}
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.TimeBar.onLayout(boolean,int,int,int,int)",this,changed,l,t,r,b);try{int w = r - l;
    int h = b - t;
    if (!showTimes && !showScrubber) {
      progressBar.set(0, 0, w, h);
    } else {
      int margin = scrubber.getWidth() / 3;
      if (showTimes) {
        margin += timeBounds.width();
      }
      int progressY = (h + scrubberPadding) / 2;
      scrubberTop = progressY - scrubber.getHeight() / 2 + 1;
      progressBar.set(
          getPaddingLeft() + margin, progressY,
          w - getPaddingRight() - margin, progressY + 4);
    }
    update();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.TimeBar.onLayout(boolean,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.TimeBar.onLayout(boolean,int,int,int,int)",this,throwable);throw throwable;}
  }

  @Override
  public void draw(Canvas canvas) {
    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.TimeBar.draw(android.graphics.Canvas)",this,canvas);try{super.draw(canvas);

    /*// draw progress bars*/
    canvas.drawRect(progressBar, progressPaint);
    canvas.drawRect(playedBar, playedPaint);

    /*// draw scrubber and timers*/
    if (showScrubber) {
      canvas.drawBitmap(scrubber, scrubberLeft, scrubberTop, null);
    }
    if (showTimes) {
      canvas.drawText(
          stringForTime(currentTime),
          timeBounds.width() / 2 + getPaddingLeft(),
          timeBounds.height() + vPaddingInPx / 2 + scrubberPadding + 1,
          timeTextPaint);
      canvas.drawText(
          stringForTime(totalTime),
          getWidth() - getPaddingRight() - timeBounds.width() / 2,
          timeBounds.height() + vPaddingInPx / 2 + scrubberPadding + 1,
          timeTextPaint);
    }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.TimeBar.draw(android.graphics.Canvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.TimeBar.draw(android.graphics.Canvas)",this,throwable);throw throwable;}
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {

    com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.TimeBar.onTouchEvent(android.view.MotionEvent)",this,event);try{if (showScrubber) {
      int x = (int) event.getX();
      int y = (int) event.getY();

      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          if (inScrubber(x, y)) {
            scrubbing = true;
            scrubberCorrection = x - scrubberLeft;
            listener.onScrubbingStart();
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.TimeBar.onTouchEvent(android.view.MotionEvent)",this);return true;}
          }
          break;
        case MotionEvent.ACTION_MOVE:
          if (scrubbing) {
            scrubberLeft = x - scrubberCorrection;
            clampScrubber();
            currentTime = getScrubberTime();
            listener.onScrubbingMove(currentTime);
            invalidate();
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.TimeBar.onTouchEvent(android.view.MotionEvent)",this);return true;}
          }
          break;
        case MotionEvent.ACTION_UP:
          if (scrubbing) {
            listener.onScrubbingEnd(getScrubberTime());
            scrubbing = false;
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.TimeBar.onTouchEvent(android.view.MotionEvent)",this);return true;}
          }
          break;
      }
    }
    {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.TimeBar.onTouchEvent(android.view.MotionEvent)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.TimeBar.onTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
  }

  private String stringForTime(long millis) {
    com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.app.TimeBar.stringForTime(long)",this,millis);try{int totalSeconds = (int) millis / 1000;
    int seconds = totalSeconds % 60;
    int minutes = (totalSeconds / 60) % 60;
    int hours = totalSeconds / 3600;
    if (hours > 0) {
      {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.app.TimeBar.stringForTime(long)",this);return String.format("%d:%02d:%02d", hours, minutes, seconds).toString();}
    } else {
      {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.app.TimeBar.stringForTime(long)",this);return String.format("%02d:%02d", minutes, seconds).toString();}
    }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.app.TimeBar.stringForTime(long)",this,throwable);throw throwable;}
  }

}
