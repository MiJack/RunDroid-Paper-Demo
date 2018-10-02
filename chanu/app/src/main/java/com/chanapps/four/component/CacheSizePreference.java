/**
 * 
 */
package com.chanapps.four.component;

import java.io.File;

import com.chanapps.four.activity.R;
import com.chanapps.four.data.ChanFileStorage;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 * 
 */
public class CacheSizePreference extends Preference implements OnSeekBarChangeListener {
	private static final String TAG = "CacheSizePreference";
	private static final boolean DEBUG = false;
    public static final int MIN_VALUE = 32;
    public static final int MAX_VALUE = 1024;
    public static final int DEFAULT_VALUE = 128;
    public static final float MAX_CACHE_PERCENT = 0.1f;

	private static final String ANDROIDNS = "http://schemas.android.com/apk/res/android";
	private static final String CHANAPPS = "http://chanapps.com";

	private int maxValue = MAX_VALUE;
	private int minValue = MIN_VALUE;
	private int cacheSize = 1;
	private int currentValue = DEFAULT_VALUE;
	private String unitsLeft = "";
	private String unitsRight = "";
	private SeekBar seekBar;

	private TextView statusText;

	public CacheSizePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPreference(context, attrs);
	}

	public CacheSizePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPreference(context, attrs);
	}

	private void initPreference(Context context, AttributeSet attrs) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CacheSizePreference.initPreference(android.content.Context,android.util.AttributeSet)",this,context,attrs);try{setValuesFromXml(attrs);
		seekBar = new SeekBar(context, attrs);
		seekBar.setMax(maxValue - minValue);
		seekBar.setOnSeekBarChangeListener(this);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CacheSizePreference.initPreference(android.content.Context,android.util.AttributeSet)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CacheSizePreference.initPreference(android.content.Context,android.util.AttributeSet)",this,throwable);throw throwable;}
	}

	private void setValuesFromXml(AttributeSet attrs) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CacheSizePreference.setValuesFromXml(android.util.AttributeSet)",this,attrs);try{maxValue = attrs.getAttributeIntValue(ANDROIDNS, "max", 1024);
		minValue = attrs.getAttributeIntValue(CHANAPPS, "min", DEFAULT_VALUE);
		if (DEBUG) {Log.i(TAG, "Cache loaded from xml, min: " + minValue + " max: " + maxValue);}
		try {
			File cacheFolder = ChanFileStorage.getCacheDirectory(getContext());
			long totalSpace = cacheFolder.getTotalSpace() / (1024*1024);
            long maxCache = Math.round((float)totalSpace * MAX_CACHE_PERCENT);
			maxValue = (int)maxCache;
			if (maxValue < minValue) {
				minValue = maxValue / 2 < DEFAULT_VALUE ? DEFAULT_VALUE : maxValue / 2;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error while getting cache size", e);
		}

		unitsLeft = getAttributeStringValue(attrs, CHANAPPS, "unitsLeft", "");
		String units = getAttributeStringValue(attrs, CHANAPPS, "units", "");
		unitsRight = getAttributeStringValue(attrs, CHANAPPS, "unitsRight", units);

		try {
			String newInterval = attrs.getAttributeValue(CHANAPPS, "cacheSize");
			if (newInterval != null) {
				cacheSize = Integer.parseInt(newInterval);
			}
			if (cacheSize <= minValue) {
				cacheSize = DEFAULT_VALUE;
			}
		} catch (Exception e) {
			Log.e(TAG, "Invalid cacheSize value", e);
		}

		if (DEBUG) {Log.i(TAG, "Cache value set to " + cacheSize);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CacheSizePreference.setValuesFromXml(android.util.AttributeSet)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CacheSizePreference.setValuesFromXml(android.util.AttributeSet)",this,throwable);throw throwable;}
	}

	private String getAttributeStringValue(AttributeSet attrs, String namespace, String name, String defaultValue) {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.component.CacheSizePreference.getAttributeStringValue(android.util.AttributeSet,java.lang.String,java.lang.String,java.lang.String)",this,attrs,namespace,name,defaultValue);try{String value = attrs.getAttributeValue(namespace, name);
		if (value == null) {
			value = defaultValue;
		}

		{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.component.CacheSizePreference.getAttributeStringValue(android.util.AttributeSet,java.lang.String,java.lang.String,java.lang.String)",this);return value;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.component.CacheSizePreference.getAttributeStringValue(android.util.AttributeSet,java.lang.String,java.lang.String,java.lang.String)",this,throwable);throw throwable;}
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		com.mijack.Xlog.logMethodEnter("android.view.View com.chanapps.four.component.CacheSizePreference.onCreateView(android.view.ViewGroup)",this,parent);try{RelativeLayout layout = null;
		try {
			LayoutInflater mInflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (RelativeLayout) mInflater.inflate(
					R.layout.cache_size_preference, parent, false);
		} catch (Exception e) {
			Log.e(TAG, "Error creating seek bar preference", e);
		}

		{com.mijack.Xlog.logMethodExit("android.view.View com.chanapps.four.component.CacheSizePreference.onCreateView(android.view.ViewGroup)",this);return layout;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.chanapps.four.component.CacheSizePreference.onCreateView(android.view.ViewGroup)",this,throwable);throw throwable;}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBindView(View view) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CacheSizePreference.onBindView(android.view.View)",this,view);try{super.onBindView(view);

		try {
			/*// move our seekbar to the new view we've been given*/
			ViewParent oldContainer = seekBar.getParent();
			ViewGroup newContainer = (ViewGroup) view
					.findViewById(R.id.seekBarPrefBarContainer);

			if (oldContainer != newContainer) {
				/*// remove the seekbar from the old view*/
				if (oldContainer != null) {
					((ViewGroup) oldContainer).removeView(seekBar);
				}
				/*// remove the existing seekbar (there may not be one) and add*/
				/*// ours*/
				newContainer.removeAllViews();
				newContainer.addView(seekBar,
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
			}
		} catch (Exception ex) {
			Log.e(TAG, "Error binding view: " + ex.toString(), ex);
		}

		updateView(view);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CacheSizePreference.onBindView(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CacheSizePreference.onBindView(android.view.View)",this,throwable);throw throwable;}
	}

	/**
	 * Update a SeekBarPreference view with our current state
	 * 
	 * @param view
	 */
	protected void updateView(View view) {

		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CacheSizePreference.updateView(android.view.View)",this,view);try{try {
			RelativeLayout layout = (RelativeLayout) view;

			statusText = (TextView) layout.findViewById(R.id.seekBarPrefValue);
			statusText.setText(String.valueOf(currentValue));
			statusText.setMinimumWidth(30);

			seekBar.setProgress(currentValue - minValue);

			TextView unitsRightText = (TextView) layout.findViewById(R.id.seekBarPrefUnitsRight);
			unitsRightText.setText(unitsRight);

			TextView unitsLeftText = (TextView) layout.findViewById(R.id.seekBarPrefUnitsLeft);
			unitsLeftText.setText(unitsLeft);
		} catch (Exception e) {
			Log.e(TAG, "Error updating seek bar preference", e);
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CacheSizePreference.updateView(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CacheSizePreference.updateView(android.view.View)",this,throwable);throw throwable;}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CacheSizePreference.onProgressChanged(android.widget.SeekBar,int,boolean)",this,seekBar,progress,fromUser);try{int newValue = progress + minValue;

		if (newValue > maxValue)
			{newValue = maxValue;}
		else if (newValue < minValue)
			{newValue = minValue;}

		/*// change rejected, revert to the previous value*/
		if (!callChangeListener(newValue)) {
			seekBar.setProgress(currentValue - minValue);
			{com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CacheSizePreference.onProgressChanged(android.widget.SeekBar,int,boolean)",this);return;}
		}

		/*// change accepted, store it*/
		currentValue = newValue;
		statusText.setText(String.valueOf(newValue));
		persistInt(newValue);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CacheSizePreference.onProgressChanged(android.widget.SeekBar,int,boolean)",this,throwable);throw throwable;}

	}

	{com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CacheSizePreference.onStartTrackingTouch(android.widget.SeekBar)",this,seekBar);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CacheSizePreference.onStartTrackingTouch(android.widget.SeekBar)",this);}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CacheSizePreference.onStopTrackingTouch(android.widget.SeekBar)",this,seekBar);try{notifyChanged();com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CacheSizePreference.onStopTrackingTouch(android.widget.SeekBar)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CacheSizePreference.onStopTrackingTouch(android.widget.SeekBar)",this,throwable);throw throwable;}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray ta, int index) {
		com.mijack.Xlog.logMethodEnter("java.lang.Object com.chanapps.four.component.CacheSizePreference.onGetDefaultValue(android.content.res.TypedArray,int)",this,ta,index);try{int defaultValue = ta.getInt(index, DEFAULT_VALUE);
		{com.mijack.Xlog.logMethodExit("java.lang.Object com.chanapps.four.component.CacheSizePreference.onGetDefaultValue(android.content.res.TypedArray,int)",this);return defaultValue;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Object com.chanapps.four.component.CacheSizePreference.onGetDefaultValue(android.content.res.TypedArray,int)",this,throwable);throw throwable;}
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CacheSizePreference.onSetInitialValue(boolean,java.lang.Object)",this,restoreValue,defaultValue);try{if (restoreValue) {
			currentValue = getPersistedInt(currentValue);
		} else {
			int temp = DEFAULT_VALUE;
			persistInt(temp);
			currentValue = temp;
		}com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CacheSizePreference.onSetInitialValue(boolean,java.lang.Object)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CacheSizePreference.onSetInitialValue(boolean,java.lang.Object)",this,throwable);throw throwable;}
	}
}
