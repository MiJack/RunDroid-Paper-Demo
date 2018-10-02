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

package com.chanapps.four.mColorPicker;

import com.chanapps.four.activity.R;
import com.chanapps.four.mColorPicker.views.ColorPanelView;
import com.chanapps.four.mColorPicker.views.ColorPickerView;
import com.chanapps.four.mColorPicker.views.ColorPickerView.OnColorChangedListener;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class ColorPickerDialog extends AlertDialog implements
		ColorPickerView.OnColorChangedListener {

	private ColorPickerView mColorPicker;

	private ColorPanelView mOldColor;
	private ColorPanelView mNewColor;

	private OnColorChangedListener mListener;

	public ColorPickerDialog(Context context, int initialColor) {
		super(context);

		init(initialColor);
	}

	private void init(int color) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.ColorPickerDialog.init(int)",this,color);try{/*// To fight color branding.*/
		getWindow().setFormat(PixelFormat.RGBA_8888);

		setUp(color);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.ColorPickerDialog.init(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.ColorPickerDialog.init(int)",this,throwable);throw throwable;}

	}

	private void setUp(int color) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.ColorPickerDialog.setUp(int)",this,color);try{LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_color_picker, null);

		setView(layout);

		mColorPicker = (ColorPickerView) layout
				.findViewById(R.id.color_picker_view);
		mOldColor = (ColorPanelView) layout.findViewById(R.id.old_color_panel);
		mNewColor = (ColorPanelView) layout.findViewById(R.id.new_color_panel);

		((LinearLayout) mOldColor.getParent()).setPadding(Math
				.round(mColorPicker.getDrawingOffset()), 0, Math
				.round(mColorPicker.getDrawingOffset()), 0);

		mColorPicker.setOnColorChangedListener(this);

		mOldColor.setColor(color);
		mColorPicker.setColor(color, true);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.ColorPickerDialog.setUp(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.ColorPickerDialog.setUp(int)",this,throwable);throw throwable;}

	}

	@Override
	public void onColorChanged(int color) {

		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.ColorPickerDialog.onColorChanged(int)",this,color);try{mNewColor.setColor(color);

		if (mListener != null) {
			mListener.onColorChanged(color);
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.ColorPickerDialog.onColorChanged(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.ColorPickerDialog.onColorChanged(int)",this,throwable);throw throwable;}

	}

	public void setAlphaSliderVisible(boolean visible) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.mColorPicker.ColorPickerDialog.setAlphaSliderVisible(boolean)",this,visible);try{mColorPicker.setAlphaSliderVisible(visible);com.mijack.Xlog.logMethodExit("void com.chanapps.four.mColorPicker.ColorPickerDialog.setAlphaSliderVisible(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.mColorPicker.ColorPickerDialog.setAlphaSliderVisible(boolean)",this,throwable);throw throwable;}
	}

	public int getColor() {
		com.mijack.Xlog.logMethodEnter("int com.chanapps.four.mColorPicker.ColorPickerDialog.getColor()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.mColorPicker.ColorPickerDialog.getColor()",this);return mColorPicker.getColor();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.mColorPicker.ColorPickerDialog.getColor()",this,throwable);throw throwable;}
	}

}
