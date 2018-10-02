/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.gallery3d.util;

import com.android.gallery3d.common.BlobCache;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReverseGeocoder {
    private static final String TAG = "ReverseGeocoder";
    public static final int EARTH_RADIUS_METERS = 6378137;
    public static final int LAT_MIN = -90;
    public static final int LAT_MAX = 90;
    public static final int LON_MIN = -180;
    public static final int LON_MAX = 180;
    private static final int MAX_COUNTRY_NAME_LENGTH = 8;
    /*// If two points are within 20 miles of each other, use*/
    /*// "Around Palo Alto, CA" or "Around Mountain View, CA".*/
    /*// instead of directly jumping to the next level and saying*/
    /*// "California, US".*/
    private static final int MAX_LOCALITY_MILE_RANGE = 20;

    private static final String GEO_CACHE_FILE = "rev_geocoding";
    private static final int GEO_CACHE_MAX_ENTRIES = 1000;
    private static final int GEO_CACHE_MAX_BYTES = 500 * 1024;
    private static final int GEO_CACHE_VERSION = 0;

    public static class SetLatLong {
        /*// The latitude and longitude of the min latitude point.*/
        public double mMinLatLatitude = LAT_MAX;
        public double mMinLatLongitude;
        /*// The latitude and longitude of the max latitude point.*/
        public double mMaxLatLatitude = LAT_MIN;
        public double mMaxLatLongitude;
        /*// The latitude and longitude of the min longitude point.*/
        public double mMinLonLatitude;
        public double mMinLonLongitude = LON_MAX;
        /*// The latitude and longitude of the max longitude point.*/
        public double mMaxLonLatitude;
        public double mMaxLonLongitude = LON_MIN;
    }

    private Context mContext;
    private Geocoder mGeocoder;
    private BlobCache mGeoCache;
    private ConnectivityManager mConnectivityManager;
    private static Address sCurrentAddress; /*// last known address*/

    public ReverseGeocoder(Context context) {
        mContext = context;
        mGeocoder = new Geocoder(mContext);
        mGeoCache = CacheManager.getCache(context, GEO_CACHE_FILE,
                GEO_CACHE_MAX_ENTRIES, GEO_CACHE_MAX_BYTES,
                GEO_CACHE_VERSION);
        mConnectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public String computeAddress(SetLatLong set) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this,set);try{/*// The overall min and max latitudes and longitudes of the set.*/
        double setMinLatitude = set.mMinLatLatitude;
        double setMinLongitude = set.mMinLatLongitude;
        double setMaxLatitude = set.mMaxLatLatitude;
        double setMaxLongitude = set.mMaxLatLongitude;
        if (Math.abs(set.mMaxLatLatitude - set.mMinLatLatitude)
                < Math.abs(set.mMaxLonLongitude - set.mMinLonLongitude)) {
            setMinLatitude = set.mMinLonLatitude;
            setMinLongitude = set.mMinLonLongitude;
            setMaxLatitude = set.mMaxLonLatitude;
            setMaxLongitude = set.mMaxLonLongitude;
        }
        Address addr1 = lookupAddress(setMinLatitude, setMinLongitude, true);
        Address addr2 = lookupAddress(setMaxLatitude, setMaxLongitude, true);
        if (addr1 == null)
            {addr1 = addr2;}
        if (addr2 == null)
            {addr2 = addr1;}
        if (addr1 == null || addr2 == null) {
            {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this);return null;}
        }

        /*// Get current location, we decide the granularity of the string based*/
        /*// on this.*/
        LocationManager locationManager =
                (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        List<String> providers = locationManager.getAllProviders();
        for (int i = 0; i < providers.size(); ++i) {
            String provider = providers.get(i);
            location = (provider != null) ? locationManager.getLastKnownLocation(provider) : null;
            if (location != null)
                {break;}
        }
        String currentCity = "";
        String currentAdminArea = "";
        String currentCountry = Locale.getDefault().getCountry();
        if (location != null) {
            Address currentAddress = lookupAddress(
                    location.getLatitude(), location.getLongitude(), true);
            if (currentAddress == null) {
                currentAddress = sCurrentAddress;
            } else {
                sCurrentAddress = currentAddress;
            }
            if (currentAddress != null && currentAddress.getCountryCode() != null) {
                currentCity = checkNull(currentAddress.getLocality());
                currentCountry = checkNull(currentAddress.getCountryCode());
                currentAdminArea = checkNull(currentAddress.getAdminArea());
            }
        }

        String closestCommonLocation = null;
        String addr1Locality = checkNull(addr1.getLocality());
        String addr2Locality = checkNull(addr2.getLocality());
        String addr1AdminArea = checkNull(addr1.getAdminArea());
        String addr2AdminArea = checkNull(addr2.getAdminArea());
        String addr1CountryCode = checkNull(addr1.getCountryCode());
        String addr2CountryCode = checkNull(addr2.getCountryCode());

        if (currentCity.equals(addr1Locality) || currentCity.equals(addr2Locality)) {
            String otherCity = currentCity;
            if (currentCity.equals(addr1Locality)) {
                otherCity = addr2Locality;
                if (otherCity.length() == 0) {
                    otherCity = addr2AdminArea;
                    if (!currentCountry.equals(addr2CountryCode)) {
                        otherCity += " " + addr2CountryCode;
                    }
                }
                addr2Locality = addr1Locality;
                addr2AdminArea = addr1AdminArea;
                addr2CountryCode = addr1CountryCode;
            } else {
                otherCity = addr1Locality;
                if (otherCity.length() == 0) {
                    otherCity = addr1AdminArea;
                    if (!currentCountry.equals(addr1CountryCode)) {
                        otherCity += " " + addr1CountryCode;
                    }
                }
                addr1Locality = addr2Locality;
                addr1AdminArea = addr2AdminArea;
                addr1CountryCode = addr2CountryCode;
            }
            closestCommonLocation = valueIfEqual(addr1.getAddressLine(0), addr2.getAddressLine(0));
            if (closestCommonLocation != null && !("null".equals(closestCommonLocation))) {
                if (!currentCity.equals(otherCity)) {
                    closestCommonLocation += " - " + otherCity;
                }
                {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this);return closestCommonLocation;}
            }

            /*// Compare thoroughfare (street address) next.*/
            closestCommonLocation = valueIfEqual(addr1.getThoroughfare(), addr2.getThoroughfare());
            if (closestCommonLocation != null && !("null".equals(closestCommonLocation))) {
                {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this);return closestCommonLocation;}
            }
        }

        /*// Compare the locality.*/
        closestCommonLocation = valueIfEqual(addr1Locality, addr2Locality);
        if (closestCommonLocation != null && !("".equals(closestCommonLocation))) {
            String adminArea = addr1AdminArea;
            String countryCode = addr1CountryCode;
            if (adminArea != null && adminArea.length() > 0) {
                if (!countryCode.equals(currentCountry)) {
                    closestCommonLocation += ", " + adminArea + " " + countryCode;
                } else {
                    closestCommonLocation += ", " + adminArea;
                }
            }
            {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this);return closestCommonLocation;}
        }

        /*// If the admin area is the same as the current location, we hide it and*/
        /*// instead show the city name.*/
        if (currentAdminArea.equals(addr1AdminArea) && currentAdminArea.equals(addr2AdminArea)) {
            if ("".equals(addr1Locality)) {
                addr1Locality = addr2Locality;
            }
            if ("".equals(addr2Locality)) {
                addr2Locality = addr1Locality;
            }
            if (!"".equals(addr1Locality)) {
                if (addr1Locality.equals(addr2Locality)) {
                    closestCommonLocation = addr1Locality + ", " + currentAdminArea;
                } else {
                    closestCommonLocation = addr1Locality + " - " + addr2Locality;
                }
                {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this);return closestCommonLocation;}
            }
        }

        /*// Just choose one of the localities if within a MAX_LOCALITY_MILE_RANGE*/
        /*// mile radius.*/
        float[] distanceFloat = new float[1];
        Location.distanceBetween(setMinLatitude, setMinLongitude,
                setMaxLatitude, setMaxLongitude, distanceFloat);
        int distance = (int) GalleryUtils.toMile(distanceFloat[0]);
        if (distance < MAX_LOCALITY_MILE_RANGE) {
            /*// Try each of the points and just return the first one to have a*/
            /*// valid address.*/
            closestCommonLocation = getLocalityAdminForAddress(addr1, true);
            if (closestCommonLocation != null) {
                {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this);return closestCommonLocation;}
            }
            closestCommonLocation = getLocalityAdminForAddress(addr2, true);
            if (closestCommonLocation != null) {
                {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this);return closestCommonLocation;}
            }
        }

        /*// Check the administrative area.*/
        closestCommonLocation = valueIfEqual(addr1AdminArea, addr2AdminArea);
        if (closestCommonLocation != null && !("".equals(closestCommonLocation))) {
            String countryCode = addr1CountryCode;
            if (!countryCode.equals(currentCountry)) {
                if (countryCode != null && countryCode.length() > 0) {
                    closestCommonLocation += " " + countryCode;
                }
            }
            {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this);return closestCommonLocation;}
        }

        /*// Check the country codes.*/
        closestCommonLocation = valueIfEqual(addr1CountryCode, addr2CountryCode);
        if (closestCommonLocation != null && !("".equals(closestCommonLocation))) {
            {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this);return closestCommonLocation;}
        }
        /*// There is no intersection, let's choose a nicer name.*/
        String addr1Country = addr1.getCountryName();
        String addr2Country = addr2.getCountryName();
        if (addr1Country == null)
            {addr1Country = addr1CountryCode;}
        if (addr2Country == null)
            {addr2Country = addr2CountryCode;}
        if (addr1Country == null || addr2Country == null)
            {{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this);return null;}}
        if (addr1Country.length() > MAX_COUNTRY_NAME_LENGTH || addr2Country.length() > MAX_COUNTRY_NAME_LENGTH) {
            closestCommonLocation = addr1CountryCode + " - " + addr2CountryCode;
        } else {
            closestCommonLocation = addr1Country + " - " + addr2Country;
        }
        {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this);return closestCommonLocation;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.util.ReverseGeocoder.computeAddress(com.android.gallery3d.util.ReverseGeocoder$SetLatLong)",this,throwable);throw throwable;}
    }

    private String checkNull(String locality) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.util.ReverseGeocoder.checkNull(java.lang.String)",this,locality);try{if (locality == null)
            {{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.checkNull(java.lang.String)",this);return "";}}
        if (locality.equals("null"))
            {{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.checkNull(java.lang.String)",this);return "";}}
        {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.checkNull(java.lang.String)",this);return locality;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.util.ReverseGeocoder.checkNull(java.lang.String)",this,throwable);throw throwable;}
    }

    private String getLocalityAdminForAddress(final Address addr, final boolean approxLocation) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.util.ReverseGeocoder.getLocalityAdminForAddress(android.location.Address,boolean)",this,addr,approxLocation);try{if (addr == null)
            {{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.getLocalityAdminForAddress(android.location.Address,boolean)",this);return "";}}
        String localityAdminStr = addr.getLocality();
        if (localityAdminStr != null && !("null".equals(localityAdminStr))) {
            if (approxLocation) {
                /*// TODO: Uncomment these lines as soon as we may translations*/
                /*// for Res.string.around.*/
                /*// localityAdminStr =*/
                /*// mContext.getResources().getString(Res.string.around) + " " +*/
                /*// localityAdminStr;*/
            }
            String adminArea = addr.getAdminArea();
            if (adminArea != null && adminArea.length() > 0) {
                localityAdminStr += ", " + adminArea;
            }
            {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.getLocalityAdminForAddress(android.location.Address,boolean)",this);return localityAdminStr;}
        }
        {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.getLocalityAdminForAddress(android.location.Address,boolean)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.util.ReverseGeocoder.getLocalityAdminForAddress(android.location.Address,boolean)",this,throwable);throw throwable;}
    }

    public Address lookupAddress(final double latitude, final double longitude,
            boolean useCache) {
        com.mijack.Xlog.logMethodEnter("android.location.Address com.android.gallery3d.util.ReverseGeocoder.lookupAddress(double,double,boolean)",this,latitude,longitude,useCache);try{try {
            long locationKey = (long) (((latitude + LAT_MAX) * 2 * LAT_MAX
                    + (longitude + LON_MAX)) * EARTH_RADIUS_METERS);
            byte[] cachedLocation = null;
            if (useCache && mGeoCache != null) {
                cachedLocation = mGeoCache.lookup(locationKey);
            }
            Address address = null;
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (cachedLocation == null || cachedLocation.length == 0) {
                if (networkInfo == null || !networkInfo.isConnected()) {
                    {com.mijack.Xlog.logMethodExit("android.location.Address com.android.gallery3d.util.ReverseGeocoder.lookupAddress(double,double,boolean)",this);return null;}
                }
                List<Address> addresses = mGeocoder.getFromLocation(latitude, longitude, 1);
                if (!addresses.isEmpty()) {
                    address = addresses.get(0);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(bos);
                    Locale locale = address.getLocale();
                    writeUTF(dos, locale.getLanguage());
                    writeUTF(dos, locale.getCountry());
                    writeUTF(dos, locale.getVariant());

                    writeUTF(dos, address.getThoroughfare());
                    int numAddressLines = address.getMaxAddressLineIndex();
                    dos.writeInt(numAddressLines);
                    for (int i = 0; i < numAddressLines; ++i) {
                        writeUTF(dos, address.getAddressLine(i));
                    }
                    writeUTF(dos, address.getFeatureName());
                    writeUTF(dos, address.getLocality());
                    writeUTF(dos, address.getAdminArea());
                    writeUTF(dos, address.getSubAdminArea());

                    writeUTF(dos, address.getCountryName());
                    writeUTF(dos, address.getCountryCode());
                    writeUTF(dos, address.getPostalCode());
                    writeUTF(dos, address.getPhone());
                    writeUTF(dos, address.getUrl());

                    dos.flush();
                    if (mGeoCache != null) {
                        mGeoCache.insert(locationKey, bos.toByteArray());
                    }
                    dos.close();
                }
            } else {
                /*// Parsing the address from the byte stream.*/
                DataInputStream dis = new DataInputStream(
                        new ByteArrayInputStream(cachedLocation));
                String language = readUTF(dis);
                String country = readUTF(dis);
                String variant = readUTF(dis);
                Locale locale = null;
                if (language != null) {
                    if (country == null) {
                        locale = new Locale(language);
                    } else if (variant == null) {
                        locale = new Locale(language, country);
                    } else {
                        locale = new Locale(language, country, variant);
                    }
                }
                if (!locale.getLanguage().equals(Locale.getDefault().getLanguage())) {
                    dis.close();
                    {com.mijack.Xlog.logMethodExit("android.location.Address com.android.gallery3d.util.ReverseGeocoder.lookupAddress(double,double,boolean)",this);return lookupAddress(latitude, longitude, false);}
                }
                address = new Address(locale);

                address.setThoroughfare(readUTF(dis));
                int numAddressLines = dis.readInt();
                for (int i = 0; i < numAddressLines; ++i) {
                    address.setAddressLine(i, readUTF(dis));
                }
                address.setFeatureName(readUTF(dis));
                address.setLocality(readUTF(dis));
                address.setAdminArea(readUTF(dis));
                address.setSubAdminArea(readUTF(dis));

                address.setCountryName(readUTF(dis));
                address.setCountryCode(readUTF(dis));
                address.setPostalCode(readUTF(dis));
                address.setPhone(readUTF(dis));
                address.setUrl(readUTF(dis));
                dis.close();
            }
            {com.mijack.Xlog.logMethodExit("android.location.Address com.android.gallery3d.util.ReverseGeocoder.lookupAddress(double,double,boolean)",this);return address;}
        } catch (Exception e) {
            /*// Ignore.*/
        }
        {com.mijack.Xlog.logMethodExit("android.location.Address com.android.gallery3d.util.ReverseGeocoder.lookupAddress(double,double,boolean)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.location.Address com.android.gallery3d.util.ReverseGeocoder.lookupAddress(double,double,boolean)",this,throwable);throw throwable;}
    }

    private String valueIfEqual(String a, String b) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.util.ReverseGeocoder.valueIfEqual(java.lang.String,java.lang.String)",this,a,b);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.valueIfEqual(java.lang.String,java.lang.String)",this);return (a != null && b != null && a.equalsIgnoreCase(b)) ? a : null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.util.ReverseGeocoder.valueIfEqual(java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    public static final void writeUTF(DataOutputStream dos, String string) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.ReverseGeocoder.writeUTF(java.io.DataOutputStream,java.lang.String)",dos,string);try{if (string == null) {
            dos.writeUTF("");
        } else {
            dos.writeUTF(string);
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.ReverseGeocoder.writeUTF(java.io.DataOutputStream,java.lang.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.ReverseGeocoder.writeUTF(java.io.DataOutputStream,java.lang.String)",throwable);throw throwable;}
    }

    public static final String readUTF(DataInputStream dis) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.util.ReverseGeocoder.readUTF(java.io.DataInputStream)",dis);try{String retVal = dis.readUTF();
        if (retVal.length() == 0)
            {{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.readUTF(java.io.DataInputStream)");return null;}}
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.util.ReverseGeocoder.readUTF(java.io.DataInputStream)");return retVal;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.util.ReverseGeocoder.readUTF(java.io.DataInputStream)",throwable);throw throwable;}
    }
}
