/*
 * Copyright (C) 2009 The Android Open Source Project
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
import com.android.gallery3d.app.Gallery;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WidgetClickHandler extends Activity {
    private static final String TAG = "PhotoAppWidgetClickHandler";

    private boolean isValidDataUri(Uri dataUri) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.gadget.WidgetClickHandler.isValidDataUri(android.net.Uri)",this,dataUri);try{if (dataUri == null) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.gadget.WidgetClickHandler.isValidDataUri(android.net.Uri)",this);return false;}}
        try {
            AssetFileDescriptor f = getContentResolver()
                    .openAssetFileDescriptor(dataUri, "r");
            f.close();
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.gadget.WidgetClickHandler.isValidDataUri(android.net.Uri)",this);return true;}
        } catch (Throwable e) {
            Log.w(TAG, "cannot open uri: " + dataUri, e);
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.gadget.WidgetClickHandler.isValidDataUri(android.net.Uri)",this);return false;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.gadget.WidgetClickHandler.isValidDataUri(android.net.Uri)",this,throwable);throw throwable;}
    }

    @Override
    protected void onCreate(Bundle savedState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.WidgetClickHandler.onCreate(android.os.Bundle)",this,savedState);try{super.onCreate(savedState);
        Intent intent = getIntent();
        if (isValidDataUri(intent.getData())) {
            startActivity(new Intent(Intent.ACTION_VIEW, intent.getData()));
        } else {
            Toast.makeText(this,
                    R.string.no_such_item, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, Gallery.class));
        }
        finish();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.WidgetClickHandler.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.WidgetClickHandler.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }
}
