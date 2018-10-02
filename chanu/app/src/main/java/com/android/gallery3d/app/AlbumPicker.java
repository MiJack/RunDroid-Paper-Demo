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

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.data.DataManager;

import android.content.Intent;
import android.os.Bundle;

public class AlbumPicker extends PickerActivity {

    public static final String KEY_ALBUM_PATH = "album-path";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.AlbumPicker.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setTitle(R.string.select_album);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Bundle data = extras == null ? new Bundle() : new Bundle(extras);

        data.putBoolean(Gallery.KEY_GET_ALBUM, true);
        data.putString(AlbumSetPage.KEY_MEDIA_PATH,
                getDataManager().getTopSetPath(DataManager.INCLUDE_IMAGE));
        getStateManager().startState(AlbumSetPage.class, data);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.AlbumPicker.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.AlbumPicker.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }
}
