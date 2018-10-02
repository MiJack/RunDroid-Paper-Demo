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

package com.android.gallery3d.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.app.GalleryActivity;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.ui.DetailsAddressResolver.AddressResolvingListener;
import com.android.gallery3d.ui.DetailsHelper.CloseListener;
import com.android.gallery3d.ui.DetailsHelper.DetailsSource;
import com.android.gallery3d.ui.DetailsHelper.DetailsViewContainer;

import java.util.ArrayList;
import java.util.Map.Entry;

public class DialogDetailsView implements DetailsViewContainer {
    @SuppressWarnings("unused")
    private static final String TAG = "DialogDetailsView";

    private final GalleryActivity mContext;
    private DetailsAdapter mAdapter;
    private MediaDetails mDetails;
    private final DetailsSource mSource;
    private int mIndex;
    private Dialog mDialog;
    private DialogInterface.OnClickListener mClickListener;
    private int mClickListenerStringId;
    private CloseListener mCloseListener;

    public DialogDetailsView(GalleryActivity activity, DetailsSource source) {
        mContext = activity;
        mSource = source;
    }

    public void show() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DialogDetailsView.show()",this);try{reloadDetails(mSource.getIndex());
        mDialog.show();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView.show()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DialogDetailsView.show()",this,throwable);throw throwable;}
    }

    public void hide() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DialogDetailsView.hide()",this);try{mDialog.hide();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView.hide()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DialogDetailsView.hide()",this,throwable);throw throwable;}
    }

    public void reloadDetails(int indexHint) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DialogDetailsView.reloadDetails(int)",this,indexHint);try{int index = mSource.findIndex(indexHint);
        if (index == -1) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView.reloadDetails(int)",this);return;}}
        MediaDetails details = mSource.getDetails();
        if (details != null) {
            if (mIndex == index && mDetails == details) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView.reloadDetails(int)",this);return;}}
            mIndex = index;
            mDetails = details;
            setDetails(details);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DialogDetailsView.reloadDetails(int)",this,throwable);throw throwable;}
    }

    public boolean isVisible() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.DialogDetailsView.isVisible()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.DialogDetailsView.isVisible()",this);return mDialog.isShowing();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.DialogDetailsView.isVisible()",this,throwable);throw throwable;}
    }

    private void setDetails(MediaDetails details) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DialogDetailsView.setDetails(com.android.gallery3d.data.MediaDetails)",this,details);try{mAdapter = new DetailsAdapter(details);
        String defaultTitle = String.format(
                mContext.getAndroidContext().getString(R.string.details_title),
                mIndex + 1, mSource.size());
        Object o = details.getDetail(MediaDetails.INDEX_TITLE);
        /*//Log.e("PhotoPage", "index_title: " + o);*/
        String s = o != null ? o.toString() : null;
        String title = s != null && !s.isEmpty() ? s : defaultTitle;
        ListView detailsList = (ListView) LayoutInflater.from(mContext.getAndroidContext()).inflate(
                R.layout.details_list, null, false);
        detailsList.setAdapter(mAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext)
            .setView(detailsList)
            .setTitle(title);
        if (mClickListener != null) {
            builder.setPositiveButton(mClickListenerStringId, mClickListener)
                    .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DialogDetailsView$1.onClick(android.content.DialogInterface,int)",this,dialog,whichButton);try{mDialog.dismiss();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DialogDetailsView$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                        }
                    });
        }
        else {
            builder.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DialogDetailsView$2.onClick(android.content.DialogInterface,int)",this,dialog,whichButton);try{mDialog.dismiss();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView$2.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DialogDetailsView$2.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                }
            });
        }
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);

        mDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DialogDetailsView$3.onDismiss(android.content.DialogInterface)",this,dialog);try{if (mCloseListener != null) {
                    mCloseListener.onClose();
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView$3.onDismiss(android.content.DialogInterface)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DialogDetailsView$3.onDismiss(android.content.DialogInterface)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView.setDetails(com.android.gallery3d.data.MediaDetails)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DialogDetailsView.setDetails(com.android.gallery3d.data.MediaDetails)",this,throwable);throw throwable;}
    }

    private class DetailsAdapter extends BaseAdapter implements AddressResolvingListener {
        private final ArrayList<String> mItems;
        private int mLocationIndex;

        public DetailsAdapter(MediaDetails details) {
            Context context = mContext.getAndroidContext();
            mItems = new ArrayList<String>(details.size());
            mLocationIndex = -1;
            setDetails(context, details);
        }

        private void setDetails(Context context, MediaDetails details) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.setDetails(android.content.Context,com.android.gallery3d.data.MediaDetails)",this,context,details);try{for (Entry<Integer, Object> detail : details) {
                String value;
                switch (detail.getKey()) {
                    case MediaDetails.INDEX_LOCATION: {
                        double[] latlng = (double[]) detail.getValue();
                        mLocationIndex = mItems.size();
                        value = DetailsHelper.resolveAddress(mContext, latlng, this);
                        break;
                    }
                    case MediaDetails.INDEX_SIZE: {
                        value = Formatter.formatFileSize(
                                context, (Long) detail.getValue());
                        break;
                    }
                    case MediaDetails.INDEX_WHITE_BALANCE: {
                        value = "1".equals(detail.getValue())
                                ? context.getString(R.string.manual)
                                : context.getString(R.string.auto);
                        break;
                    }
                    case MediaDetails.INDEX_FLASH: {
                        MediaDetails.FlashState flash =
                                (MediaDetails.FlashState) detail.getValue();
                        /*// TODO: camera doesn't fill in the complete values, show more information*/
                        /*// when it is fixed.*/
                        if (flash.isFlashFired()) {
                            value = context.getString(R.string.flash_on);
                        } else {
                            value = context.getString(R.string.flash_off);
                        }
                        break;
                    }
                    case MediaDetails.INDEX_EXPOSURE_TIME: {
                        value = (String) detail.getValue();
                        double time = Double.valueOf(value);
                        if (time < 1.0f) {
                            value = String.format("1/%d", (int) (0.5f + 1 / time));
                        } else {
                            int integer = (int) time;
                            time -= integer;
                            value = String.valueOf(integer) + "''";
                            if (time > 0.0001) {
                                value += String.format(" 1/%d", (int) (0.5f + 1 / time));
                            }
                        }
                        break;
                    }
                    default: {
                        Object valueObj = detail.getValue();
                        /*// This shouldn't happen, log its key to help us diagnose the problem.*/
                        Utils.assertTrue(valueObj != null, "%s's value is Null",
                                DetailsHelper.getDetailsName(context, detail.getKey()));
                        value = valueObj.toString();
                    }
                }
                int key = detail.getKey();
                if (details.hasUnit(key)) {
                    value = String.format("%s : %s %s", DetailsHelper.getDetailsName(
                            context, key), value, context.getString(details.getUnit(key)));
                } else if (MediaDetails.INDEX_TITLE == key) {
                    value = null;
                } else if (MediaDetails.INDEX_DESCRIPTION == key) {
                    value = String.format("%s", value);
                } else if (MediaDetails.INDEX_MIMETYPE == key) {
                    value = null;
                } else if (MediaDetails.INDEX_PATH == key) {
                    value = null;
                } else {
                    value = String.format("%s : %s", DetailsHelper.getDetailsName(
                            context, key), value);
                }
                if (value != null)
                    {mItems.add(value);}
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.setDetails(android.content.Context,com.android.gallery3d.data.MediaDetails)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.setDetails(android.content.Context,com.android.gallery3d.data.MediaDetails)",this,throwable);throw throwable;}
        }

        @Override
        public boolean areAllItemsEnabled() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.areAllItemsEnabled()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.areAllItemsEnabled()",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.areAllItemsEnabled()",this,throwable);throw throwable;}
        }

        @Override
        public boolean isEnabled(int position) {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.isEnabled(int)",this,position);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.isEnabled(int)",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.isEnabled(int)",this,throwable);throw throwable;}
        }

        public int getCount() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getCount()",this);return mItems.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getCount()",this,throwable);throw throwable;}
        }

        public Object getItem(int position) {
            com.mijack.Xlog.logMethodEnter("java.lang.Object com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getItem(int)",this,position);try{com.mijack.Xlog.logMethodExit("java.lang.Object com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getItem(int)",this);return mDetails.getDetail(position);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Object com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getItem(int)",this,throwable);throw throwable;}
        }

        public long getItemId(int position) {
            com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getItemId(int)",this,position);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getItemId(int)",this);return position;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getItemId(int)",this,throwable);throw throwable;}
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            com.mijack.Xlog.logMethodEnter("android.view.View com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,position,convertView,parent);try{TextView tv;
            if (convertView == null) {
                tv = (TextView) LayoutInflater.from(mContext.getAndroidContext()).inflate(
                        R.layout.details, parent, false);
            } else {
                tv = (TextView) convertView;
            }
            tv.setText(mItems.get(position));
            {com.mijack.Xlog.logMethodExit("android.view.View com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getView(int,android.view.View,android.view.ViewGroup)",this);return tv;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,throwable);throw throwable;}
        }

        public void onAddressAvailable(String address) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.onAddressAvailable(java.lang.String)",this,address);try{mItems.set(mLocationIndex, address);
            notifyDataSetChanged();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.onAddressAvailable(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DialogDetailsView$DetailsAdapter.onAddressAvailable(java.lang.String)",this,throwable);throw throwable;}
        }
    }

    public void setCloseListener(CloseListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DialogDetailsView.setCloseListener(com.android.gallery3d.ui.DetailsHelper.CloseListener)",this,listener);try{mCloseListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView.setCloseListener(com.android.gallery3d.ui.DetailsHelper.CloseListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DialogDetailsView.setCloseListener(com.android.gallery3d.ui.DetailsHelper.CloseListener)",this,throwable);throw throwable;}
    }

    public void setClickListener(int stringId, DialogInterface.OnClickListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DialogDetailsView.setClickListener(int,DialogInterface.OnClickListener)",this,stringId,listener);try{mClickListenerStringId = stringId;
        mClickListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DialogDetailsView.setClickListener(int,DialogInterface.OnClickListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DialogDetailsView.setClickListener(int,DialogInterface.OnClickListener)",this,throwable);throw throwable;}
    }
}
