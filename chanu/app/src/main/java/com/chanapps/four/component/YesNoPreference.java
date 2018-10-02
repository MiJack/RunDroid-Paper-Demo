/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.chanapps.four.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import com.chanapps.four.activity.BoardActivity;
import com.chanapps.four.activity.R;

/**
 * The {@link YesNoPreference} is a preference to show a dialog with Yes and No
 * buttons.
 * <p>
 * This preference will store a boolean into the SharedPreferences.
 */
public class YesNoPreference extends DialogPreference {
    private boolean mWasPositiveResult;
    
    public YesNoPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public YesNoPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.yesNoPreferenceStyle);
    }
    
    public YesNoPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.YesNoPreference.onDialogClosed(boolean)",this,positiveResult);try{super.onDialogClosed(positiveResult);

        if (callChangeListener(positiveResult)) {
            setValue(positiveResult);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.YesNoPreference.onDialogClosed(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.YesNoPreference.onDialogClosed(boolean)",this,throwable);throw throwable;}
    }

    /**
     * Sets the value of this preference, and saves it to the persistent store
     * if required.
     * 
     * @param value The value of the preference.
     */
    public void setValue(boolean value) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.YesNoPreference.setValue(boolean)",this,value);try{mWasPositiveResult = value;
        
        persistBoolean(value);
        
        notifyDependencyChange(!value);
        setSummary(value);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.YesNoPreference.setValue(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.YesNoPreference.setValue(boolean)",this,throwable);throw throwable;}
    }

    protected void setSummary(boolean value) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.YesNoPreference.setSummary(boolean)",this,value);try{int id = value ? R.string.pref_show_nsfw_boards_summ_on : R.string.pref_show_nsfw_boards_summ_off;
        super.setSummary(id);
        BoardActivity.refreshAllBoards(getContext());com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.YesNoPreference.setSummary(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.YesNoPreference.setSummary(boolean)",this,throwable);throw throwable;}
    }

    /**
     * Gets the value of this preference.
     * 
     * @return The value of the preference.
     */
    public boolean getValue() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.component.YesNoPreference.getValue()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.YesNoPreference.getValue()",this);return mWasPositiveResult;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.component.YesNoPreference.getValue()",this,throwable);throw throwable;}
    }
    
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        com.mijack.Xlog.logMethodEnter("java.lang.Object com.chanapps.four.component.YesNoPreference.onGetDefaultValue(android.content.res.TypedArray,int)",this,a,index);try{com.mijack.Xlog.logMethodExit("java.lang.Object com.chanapps.four.component.YesNoPreference.onGetDefaultValue(android.content.res.TypedArray,int)",this);return a.getBoolean(index, false);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Object com.chanapps.four.component.YesNoPreference.onGetDefaultValue(android.content.res.TypedArray,int)",this,throwable);throw throwable;}
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.YesNoPreference.onSetInitialValue(boolean,java.lang.Object)",this,restorePersistedValue,defaultValue);try{setValue(restorePersistedValue ? getPersistedBoolean(mWasPositiveResult) :
            (Boolean) defaultValue);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.YesNoPreference.onSetInitialValue(boolean,java.lang.Object)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.YesNoPreference.onSetInitialValue(boolean,java.lang.Object)",this,throwable);throw throwable;}
    }

    @Override
    public boolean shouldDisableDependents() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.component.YesNoPreference.shouldDisableDependents()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.YesNoPreference.shouldDisableDependents()",this);return !mWasPositiveResult || super.shouldDisableDependents();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.component.YesNoPreference.shouldDisableDependents()",this,throwable);throw throwable;}
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        com.mijack.Xlog.logMethodEnter("android.os.Parcelable com.chanapps.four.component.YesNoPreference.onSaveInstanceState()",this);try{final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            /*// No need to save instance state since it's persistent*/
            {com.mijack.Xlog.logMethodExit("android.os.Parcelable com.chanapps.four.component.YesNoPreference.onSaveInstanceState()",this);return superState;}
        }
        
        final SavedState myState = new SavedState(superState);
        myState.wasPositiveResult = getValue();
        {com.mijack.Xlog.logMethodExit("android.os.Parcelable com.chanapps.four.component.YesNoPreference.onSaveInstanceState()",this);return myState;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Parcelable com.chanapps.four.component.YesNoPreference.onSaveInstanceState()",this,throwable);throw throwable;}
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.YesNoPreference.onRestoreInstanceState(android.os.Parcelable)",this,state);try{if (!state.getClass().equals(SavedState.class)) {
            /*// Didn't save state for us in onSaveInstanceState*/
            super.onRestoreInstanceState(state);
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.YesNoPreference.onRestoreInstanceState(android.os.Parcelable)",this);return;}
        }
         
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setValue(myState.wasPositiveResult);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.YesNoPreference.onRestoreInstanceState(android.os.Parcelable)",this,throwable);throw throwable;}
    }
    
    private static class SavedState extends BaseSavedState {
        boolean wasPositiveResult;
        
        public SavedState(Parcel source) {
            super(source);
            wasPositiveResult = source.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.YesNoPreference$SavedState.writeToParcel(android.os.Parcel,int)",this,dest,flags);try{super.writeToParcel(dest, flags);
            dest.writeInt(wasPositiveResult ? 1 : 0);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.YesNoPreference$SavedState.writeToParcel(android.os.Parcel,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.YesNoPreference$SavedState.writeToParcel(android.os.Parcel,int)",this,throwable);throw throwable;}
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                com.mijack.Xlog.logMethodEnter("com.chanapps.four.component.YesNoPreference$SavedState com.chanapps.four.component.YesNoPreference$SavedState$1.createFromParcel(android.os.Parcel)",this,in);try{com.mijack.Xlog.logMethodExit("com.chanapps.four.component.YesNoPreference$SavedState com.chanapps.four.component.YesNoPreference$SavedState$1.createFromParcel(android.os.Parcel)",this);return new SavedState(in);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.component.YesNoPreference$SavedState com.chanapps.four.component.YesNoPreference$SavedState$1.createFromParcel(android.os.Parcel)",this,throwable);throw throwable;}
            }

            public SavedState[] newArray(int size) {
                com.mijack.Xlog.logMethodEnter("[com.chanapps.four.component.YesNoPreference$SavedState com.chanapps.four.component.YesNoPreference$SavedState$1.newArray(int)",this,size);try{com.mijack.Xlog.logMethodExit("[com.chanapps.four.component.YesNoPreference$SavedState com.chanapps.four.component.YesNoPreference$SavedState$1.newArray(int)",this);return new SavedState[size];}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[com.chanapps.four.component.YesNoPreference$SavedState com.chanapps.four.component.YesNoPreference$SavedState$1.newArray(int)",this,throwable);throw throwable;}
            }
        };
    }

    public void show() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.YesNoPreference.show()",this);try{showDialog(null);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.YesNoPreference.show()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.YesNoPreference.show()",this,throwable);throw throwable;}
    }
}
