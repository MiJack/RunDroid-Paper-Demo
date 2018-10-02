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

import android.content.Context;
import android.location.Address;
import android.os.Handler;
import android.os.Looper;

import com.android.gallery3d.app.GalleryActivity;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ReverseGeocoder;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

public class DetailsAddressResolver {
    private AddressResolvingListener mListener;
    private final GalleryActivity mContext;
    private Future<Address> mAddressLookupJob;
    private final Handler mHandler;

    private class AddressLookupJob implements Job<Address> {
        private double[] mLatlng;

        protected AddressLookupJob(double[] latlng) {
            mLatlng = latlng;
        }

        public Address run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("android.location.Address com.android.gallery3d.ui.DetailsAddressResolver$AddressLookupJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{ReverseGeocoder geocoder = new ReverseGeocoder(mContext.getAndroidContext());
            {com.mijack.Xlog.logMethodExit("android.location.Address com.android.gallery3d.ui.DetailsAddressResolver$AddressLookupJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return geocoder.lookupAddress(mLatlng[0], mLatlng[1], true);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.location.Address com.android.gallery3d.ui.DetailsAddressResolver$AddressLookupJob.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }

    public interface AddressResolvingListener {
        public void onAddressAvailable(String address);
    }

    public DetailsAddressResolver(GalleryActivity context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public String resolveAddress(double[] latlng, AddressResolvingListener listener) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.ui.DetailsAddressResolver.resolveAddress([double,AddressResolvingListener)",this,latlng,listener);try{mListener = listener;
        mAddressLookupJob = mContext.getThreadPool().submit(
                new AddressLookupJob(latlng),
                new FutureListener<Address>() {
                    public void onFutureDone(final Future<Address> future) {
                        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DetailsAddressResolver$1.onFutureDone(com.android.gallery3d.util.Future)",this,future);try{mAddressLookupJob = null;
                        if (!future.isCancelled()) {
                            mHandler.post(new Runnable() {
                                public void run() {
                                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DetailsAddressResolver$1$1.run()",this);try{updateLocation(future.get());com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DetailsAddressResolver$1$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DetailsAddressResolver$1$1.run()",this,throwable);throw throwable;}
                                }
                            });
                        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DetailsAddressResolver$1.onFutureDone(com.android.gallery3d.util.Future)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DetailsAddressResolver$1.onFutureDone(com.android.gallery3d.util.Future)",this,throwable);throw throwable;}
                    }
                });
        {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.ui.DetailsAddressResolver.resolveAddress([double,AddressResolvingListener)",this);return GalleryUtils.formatLatitudeLongitude("(%f,%f)", latlng[0], latlng[1]);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.ui.DetailsAddressResolver.resolveAddress([double,AddressResolvingListener)",this,throwable);throw throwable;}
    }

    private void updateLocation(Address address) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DetailsAddressResolver.updateLocation(android.location.Address)",this,address);try{if (address != null) {
            Context context = mContext.getAndroidContext();
            String parts[] = {
                address.getAdminArea(),
                address.getSubAdminArea(),
                address.getLocality(),
                address.getSubLocality(),
                address.getThoroughfare(),
                address.getSubThoroughfare(),
                address.getPremises(),
                address.getPostalCode(),
                address.getCountryName()
            };

            String addressText = "";
            for (int i = 0; i < parts.length; i++) {
                if (parts[i] == null || parts[i].isEmpty()) {continue;}
                if (!addressText.isEmpty()) {
                    addressText += ", ";
                }
                addressText += parts[i];
            }
            String text = String.format("%s : %s", DetailsHelper.getDetailsName(
                    context, MediaDetails.INDEX_LOCATION), addressText);
            mListener.onAddressAvailable(text);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DetailsAddressResolver.updateLocation(android.location.Address)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DetailsAddressResolver.updateLocation(android.location.Address)",this,throwable);throw throwable;}
    }

    public void cancel() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DetailsAddressResolver.cancel()",this);try{if (mAddressLookupJob != null) {
            mAddressLookupJob.cancel();
            mAddressLookupJob = null;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DetailsAddressResolver.cancel()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DetailsAddressResolver.cancel()",this,throwable);throw throwable;}
    }
}
