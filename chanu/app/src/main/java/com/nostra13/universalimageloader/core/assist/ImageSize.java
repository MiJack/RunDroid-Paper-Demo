/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core.assist;

/**
 * Present width and height values
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class ImageSize {

	private static final int TO_STRING_MAX_LENGHT = 9; /*// "9999x9999".length()*/
	private static final String SEPARATOR = "x";

	private final int width;
	private final int height;

	public ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public ImageSize(int width, int height, int rotation) {
		if (rotation % 180 == 0) {
			this.width = width;
			this.height = height;
		} else {
			this.width = height;
			this.height = width;
		}
	}

	public int getWidth() {
		com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.core.assist.ImageSize.getWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.core.assist.ImageSize.getWidth()",this);return width;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.core.assist.ImageSize.getWidth()",this,throwable);throw throwable;}
	}

	public int getHeight() {
		com.mijack.Xlog.logMethodEnter("int com.nostra13.universalimageloader.core.assist.ImageSize.getHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.nostra13.universalimageloader.core.assist.ImageSize.getHeight()",this);return height;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.nostra13.universalimageloader.core.assist.ImageSize.getHeight()",this,throwable);throw throwable;}
	}

	/** Scales down dimensions in <b>sampleSize</b> times. Returns new object. */
	public ImageSize scaleDown(int sampleSize) {
		com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.assist.ImageSize com.nostra13.universalimageloader.core.assist.ImageSize.scaleDown(int)",this,sampleSize);try{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.assist.ImageSize com.nostra13.universalimageloader.core.assist.ImageSize.scaleDown(int)",this);return new ImageSize(width / sampleSize, height / sampleSize);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.assist.ImageSize com.nostra13.universalimageloader.core.assist.ImageSize.scaleDown(int)",this,throwable);throw throwable;}
	}

	/** Scales dimensions according to incoming scale. Returns new object. */
	public ImageSize scale(float scale) {
		com.mijack.Xlog.logMethodEnter("com.nostra13.universalimageloader.core.assist.ImageSize com.nostra13.universalimageloader.core.assist.ImageSize.scale(float)",this,scale);try{com.mijack.Xlog.logMethodExit("com.nostra13.universalimageloader.core.assist.ImageSize com.nostra13.universalimageloader.core.assist.ImageSize.scale(float)",this);return new ImageSize((int) (width * scale), (int) (height * scale));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.nostra13.universalimageloader.core.assist.ImageSize com.nostra13.universalimageloader.core.assist.ImageSize.scale(float)",this,throwable);throw throwable;}
	}

	@Override
	public String toString() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.nostra13.universalimageloader.core.assist.ImageSize.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.nostra13.universalimageloader.core.assist.ImageSize.toString()",this);return new StringBuilder(TO_STRING_MAX_LENGHT).append(width).append(SEPARATOR).append(height).toString();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.nostra13.universalimageloader.core.assist.ImageSize.toString()",this,throwable);throw throwable;}
	}
}
