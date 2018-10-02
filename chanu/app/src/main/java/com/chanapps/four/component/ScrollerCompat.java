package com.chanapps.four.component;
/*
 * Copyright (C) 2012 The Android Open Source Project
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

import android.content.Context;
import android.widget.Scroller;

/**
 * Provides access to new {@link android.widget.Scroller Scroller} APIs when available.
 *
 * <p>This class provides a platform version-independent mechanism for obeying the
 * current device's preferred scroll physics and fling behavior. It offers a subset of
 * the APIs from Scroller or OverScroller.</p>
 */
public class ScrollerCompat {
    Scroller mScroller;

    static class ScrollerCompatImplICS2 extends ScrollerCompat {
        public ScrollerCompatImplICS2(Context context) {
            super(context);
        }

        @Override
        public float getCurrVelocity() {
            com.mijack.Xlog.logMethodEnter("float com.chanapps.four.component.ScrollerCompat$ScrollerCompatImplICS2.getCurrVelocity()",this);try{com.mijack.Xlog.logMethodExit("float com.chanapps.four.component.ScrollerCompat$ScrollerCompatImplICS2.getCurrVelocity()",this);return mScroller == null ? 0 : mScroller.getCurrVelocity();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.chanapps.four.component.ScrollerCompat$ScrollerCompatImplICS2.getCurrVelocity()",this,throwable);throw throwable;}
        }
    }

    public static ScrollerCompat from(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.component.ScrollerCompat com.chanapps.four.component.ScrollerCompat.from(android.content.Context)",context);try{if (android.os.Build.VERSION.SDK_INT >= 14) {
            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.component.ScrollerCompat com.chanapps.four.component.ScrollerCompat.from(android.content.Context)");return new ScrollerCompatImplICS2(context);}
        }
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.component.ScrollerCompat com.chanapps.four.component.ScrollerCompat.from(android.content.Context)");return new ScrollerCompat(context);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.component.ScrollerCompat com.chanapps.four.component.ScrollerCompat.from(android.content.Context)",throwable);throw throwable;}
    }

    ScrollerCompat(Context context) {
        mScroller = new Scroller(context);
    }

    /**
     * Returns whether the scroller has finished scrolling.
     *
     * @return True if the scroller has finished scrolling, false otherwise.
     */
    public boolean isFinished() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.component.ScrollerCompat.isFinished()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.ScrollerCompat.isFinished()",this);return mScroller.isFinished();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.component.ScrollerCompat.isFinished()",this,throwable);throw throwable;}
    }

    /**
     * Returns how long the scroll event will take, in milliseconds.
     *
     * @return The duration of the scroll in milliseconds.
     */
    public int getDuration() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.component.ScrollerCompat.getDuration()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.component.ScrollerCompat.getDuration()",this);return mScroller.getDuration();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.component.ScrollerCompat.getDuration()",this,throwable);throw throwable;}
    }

    /**
     * Returns the current X offset in the scroll.
     *
     * @return The new X offset as an absolute distance from the origin.
     */
    public int getCurrX() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.component.ScrollerCompat.getCurrX()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.component.ScrollerCompat.getCurrX()",this);return mScroller.getCurrX();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.component.ScrollerCompat.getCurrX()",this,throwable);throw throwable;}
    }

    /**
     * Returns the current Y offset in the scroll.
     *
     * @return The new Y offset as an absolute distance from the origin.
     */
    public int getCurrY() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.component.ScrollerCompat.getCurrY()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.component.ScrollerCompat.getCurrY()",this);return mScroller.getCurrY();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.component.ScrollerCompat.getCurrY()",this,throwable);throw throwable;}
    }

    /**
     * Returns the current velocity.
     *
     * TODO: Approximate a sane result for older platform versions. Right now
     * this will return 0 for platforms earlier than ICS. This is acceptable
     * at the moment only since it is only used for EdgeEffect, which is also only
     * present in ICS+, and ScrollerCompat is not public.
     *
     * @return The original velocity less the deceleration. Result may be
     * negative.
     */
    public float getCurrVelocity() {
        com.mijack.Xlog.logMethodEnter("float com.chanapps.four.component.ScrollerCompat.getCurrVelocity()",this);try{com.mijack.Xlog.logMethodExit("float com.chanapps.four.component.ScrollerCompat.getCurrVelocity()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.chanapps.four.component.ScrollerCompat.getCurrVelocity()",this,throwable);throw throwable;}
    }

    /**
     * Call this when you want to know the new location.  If it returns true,
     * the animation is not yet finished.  loc will be altered to provide the
     * new location.
     */
    public boolean computeScrollOffset() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.component.ScrollerCompat.computeScrollOffset()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.ScrollerCompat.computeScrollOffset()",this);return mScroller.computeScrollOffset();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.component.ScrollerCompat.computeScrollOffset()",this,throwable);throw throwable;}
    }

    /**
     * Start scrolling by providing a starting point and the distance to travel.
     * The scroll will use the default value of 250 milliseconds for the
     * duration.
     *
     * @param startX Starting horizontal scroll offset in pixels. Positive
     *        numbers will scroll the content to the left.
     * @param startY Starting vertical scroll offset in pixels. Positive numbers
     *        will scroll the content up.
     * @param dx Horizontal distance to travel. Positive numbers will scroll the
     *        content to the left.
     * @param dy Vertical distance to travel. Positive numbers will scroll the
     *        content up.
     */
    public void startScroll(int startX, int startY, int dx, int dy) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.ScrollerCompat.startScroll(int,int,int,int)",this,startX,startY,dx,dy);try{mScroller.startScroll(startX, startY, dx, dy);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.ScrollerCompat.startScroll(int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.ScrollerCompat.startScroll(int,int,int,int)",this,throwable);throw throwable;}
    }

    /**
     * Start scrolling by providing a starting point and the distance to travel.
     *
     * @param startX Starting horizontal scroll offset in pixels. Positive
     *        numbers will scroll the content to the left.
     * @param startY Starting vertical scroll offset in pixels. Positive numbers
     *        will scroll the content up.
     * @param dx Horizontal distance to travel. Positive numbers will scroll the
     *        content to the left.
     * @param dy Vertical distance to travel. Positive numbers will scroll the
     *        content up.
     * @param duration Duration of the scroll in milliseconds.
     */
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.ScrollerCompat.startScroll(int,int,int,int,int)",this,startX,startY,dx,dy,duration);try{mScroller.startScroll(startX, startY, dx, dy, duration);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.ScrollerCompat.startScroll(int,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.ScrollerCompat.startScroll(int,int,int,int,int)",this,throwable);throw throwable;}
    }

    /**
     * Start scrolling based on a fling gesture. The distance travelled will
     * depend on the initial velocity of the fling.
     *
     * @param startX Starting point of the scroll (X)
     * @param startY Starting point of the scroll (Y)
     * @param velocityX Initial velocity of the fling (X) measured in pixels per
     *        second.
     * @param velocityY Initial velocity of the fling (Y) measured in pixels per
     *        second
     * @param minX Minimum X value. The scroller will not scroll past this
     *        point.
     * @param maxX Maximum X value. The scroller will not scroll past this
     *        point.
     * @param minY Minimum Y value. The scroller will not scroll past this
     *        point.
     * @param maxY Maximum Y value. The scroller will not scroll past this
     *        point.
     */
    public void fling(int startX, int startY, int velocityX, int velocityY,
                      int minX, int maxX, int minY, int maxY) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.ScrollerCompat.fling(int,int,int,int,int,int,int,int)",this,startX,startY,velocityX,velocityY,minX,maxX,minY,maxY);try{mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.ScrollerCompat.fling(int,int,int,int,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.ScrollerCompat.fling(int,int,int,int,int,int,int,int)",this,throwable);throw throwable;}
    }

    /**
     * Stops the animation. Contrary to {@link #forceFinished(boolean)},
     * aborting the animating cause the scroller to move to the final x and y
     * position
     */
    public void abortAnimation() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.ScrollerCompat.abortAnimation()",this);try{mScroller.abortAnimation();com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.ScrollerCompat.abortAnimation()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.ScrollerCompat.abortAnimation()",this,throwable);throw throwable;}
    }
}