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

package com.android.gallery3d.gadget;

import com.chanapps.four.gallery3d.R;
import com.chanapps.four.widget.PhotoAppWidgetConfigure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class WidgetTypeChooser extends Activity {

    private OnCheckedChangeListener mListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.WidgetTypeChooser$1.onCheckedChanged(android.widget.RadioGroup,int)",this,group,checkedId);try{Intent data = new Intent()
                    .putExtra(PhotoAppWidgetConfigure.KEY_WIDGET_TYPE, checkedId);
            setResult(RESULT_OK, data);
            finish();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.WidgetTypeChooser$1.onCheckedChanged(android.widget.RadioGroup,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.WidgetTypeChooser$1.onCheckedChanged(android.widget.RadioGroup,int)",this,throwable);throw throwable;}
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.WidgetTypeChooser.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setTitle(R.string.widget_type);
        setContentView(R.layout.choose_widget_type);
        RadioGroup rg = (RadioGroup) findViewById(R.id.widget_type);
        rg.setOnCheckedChangeListener(mListener);

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.WidgetTypeChooser$2.onClick(android.view.View)",this,v);try{setResult(RESULT_CANCELED);
                finish();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.WidgetTypeChooser$2.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.WidgetTypeChooser$2.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.WidgetTypeChooser.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.WidgetTypeChooser.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }
}
