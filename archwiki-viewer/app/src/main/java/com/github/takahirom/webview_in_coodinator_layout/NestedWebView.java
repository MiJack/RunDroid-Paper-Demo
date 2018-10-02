/*
 * Copyright (C) 2015 takahirom
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

package com.github.takahirom.webview_in_coodinator_layout;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class NestedWebView extends WebView implements NestedScrollingChild {
	private final int[] mScrollOffset = new int[2];
	private final int[] mScrollConsumed = new int[2];
	private int mLastY;
	private int mNestedOffsetY;
	private NestedScrollingChildHelper mChildHelper;

	public NestedWebView(Context context) {
		this(context, null);
	}

	public NestedWebView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.webViewStyle);
	}

	public NestedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mChildHelper = new NestedScrollingChildHelper(this);
		setNestedScrollingEnabled(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		com.mijack.Xlog.logMethodEnter("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.onTouchEvent(android.view.MotionEvent)",this,ev);try{boolean returnValue = false;

		MotionEvent event = MotionEvent.obtain(ev);
		final int action = MotionEventCompat.getActionMasked(event);
		if (action == MotionEvent.ACTION_DOWN) {
			mNestedOffsetY = 0;
		}
		int eventY = (int) event.getY();
		event.offsetLocation(0, mNestedOffsetY);
		switch (action) {
			case MotionEvent.ACTION_MOVE:
				int deltaY = mLastY - eventY;
				/*// NestedPreScroll*/
				if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
					deltaY -= mScrollConsumed[1];
					mLastY = eventY - mScrollOffset[1];
					event.offsetLocation(0, -mScrollOffset[1]);
					mNestedOffsetY += mScrollOffset[1];
				}
				returnValue = super.onTouchEvent(event);

				/*// NestedScroll*/
				if (dispatchNestedScroll(0, mScrollOffset[1], 0, deltaY, mScrollOffset)) {
					event.offsetLocation(0, mScrollOffset[1]);
					mNestedOffsetY += mScrollOffset[1];
					mLastY -= mScrollOffset[1];
				}
				break;
			case MotionEvent.ACTION_DOWN:
				returnValue = super.onTouchEvent(event);
				mLastY = eventY;
				/*// start NestedScroll*/
				startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				returnValue = super.onTouchEvent(event);
				/*// end NestedScroll*/
				stopNestedScroll();
				break;
		}
		{com.mijack.Xlog.logMethodExit("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.onTouchEvent(android.view.MotionEvent)",this);return returnValue;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.onTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
	}

	@Override
	public boolean isNestedScrollingEnabled() {
		com.mijack.Xlog.logMethodEnter("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.isNestedScrollingEnabled()",this);try{com.mijack.Xlog.logMethodExit("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.isNestedScrollingEnabled()",this);return mChildHelper.isNestedScrollingEnabled();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.isNestedScrollingEnabled()",this,throwable);throw throwable;}
	}

	/*// Nested Scroll implements*/
	@Override
	public void setNestedScrollingEnabled(boolean enabled) {
		com.mijack.Xlog.logMethodEnter("void com.github.takahirom.webview_in_coodinator_layout.NestedWebView.setNestedScrollingEnabled(boolean)",this,enabled);try{mChildHelper.setNestedScrollingEnabled(enabled);com.mijack.Xlog.logMethodExit("void com.github.takahirom.webview_in_coodinator_layout.NestedWebView.setNestedScrollingEnabled(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.github.takahirom.webview_in_coodinator_layout.NestedWebView.setNestedScrollingEnabled(boolean)",this,throwable);throw throwable;}
	}

	@Override
	public boolean startNestedScroll(int axes) {
		com.mijack.Xlog.logMethodEnter("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.startNestedScroll(int)",this,axes);try{com.mijack.Xlog.logMethodExit("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.startNestedScroll(int)",this);return mChildHelper.startNestedScroll(axes);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.startNestedScroll(int)",this,throwable);throw throwable;}
	}

	@Override
	public void stopNestedScroll() {
		com.mijack.Xlog.logMethodEnter("void com.github.takahirom.webview_in_coodinator_layout.NestedWebView.stopNestedScroll()",this);try{mChildHelper.stopNestedScroll();com.mijack.Xlog.logMethodExit("void com.github.takahirom.webview_in_coodinator_layout.NestedWebView.stopNestedScroll()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.github.takahirom.webview_in_coodinator_layout.NestedWebView.stopNestedScroll()",this,throwable);throw throwable;}
	}

	@Override
	public boolean hasNestedScrollingParent() {
		com.mijack.Xlog.logMethodEnter("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.hasNestedScrollingParent()",this);try{com.mijack.Xlog.logMethodExit("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.hasNestedScrollingParent()",this);return mChildHelper.hasNestedScrollingParent();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.hasNestedScrollingParent()",this,throwable);throw throwable;}
	}

	@Override
	public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
										int[] offsetInWindow) {
		com.mijack.Xlog.logMethodEnter("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedScroll(int,int,int,int,[int)",this,dxConsumed,dyConsumed,dxUnconsumed,dyUnconsumed,offsetInWindow);try{com.mijack.Xlog.logMethodExit("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedScroll(int,int,int,int,[int)",this);return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedScroll(int,int,int,int,[int)",this,throwable);throw throwable;}
	}

	@Override
	public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
		com.mijack.Xlog.logMethodEnter("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedPreScroll(int,int,[int,[int)",this,dx,dy,consumed,offsetInWindow);try{com.mijack.Xlog.logMethodExit("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedPreScroll(int,int,[int,[int)",this);return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedPreScroll(int,int,[int,[int)",this,throwable);throw throwable;}
	}

	@Override
	public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
		com.mijack.Xlog.logMethodEnter("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedFling(float,float,boolean)",this,velocityX,velocityY,consumed);try{com.mijack.Xlog.logMethodExit("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedFling(float,float,boolean)",this);return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedFling(float,float,boolean)",this,throwable);throw throwable;}
	}

	@Override
	public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
		com.mijack.Xlog.logMethodEnter("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedPreFling(float,float)",this,velocityX,velocityY);try{com.mijack.Xlog.logMethodExit("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedPreFling(float,float)",this);return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.github.takahirom.webview_in_coodinator_layout.NestedWebView.dispatchNestedPreFling(float,float)",this,throwable);throw throwable;}
	}

}