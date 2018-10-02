package com.chanapps.four.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

/**
 * A persistent cookie store which implements the Apache HttpClient
 * {@link CookieStore} interface. Cookies are stored and will persist on the
 * user's device between application sessions since they are serialized and
 * stored in {@link SharedPreferences}.
 * <p>
 * Instances of this class are designed to be used with
 * AsyncHttpClient#setCookieStore, but can also be used with a
 * regular old apache HttpClient/HttpContext if you prefer.
 */
public class PersistentCookieStore implements CookieStore {
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String COOKIE_NAME_STORE = "names";
    private static final String COOKIE_NAME_PREFIX = "cookie_";

    private final ConcurrentHashMap<String, Cookie> cookies;
    private final SharedPreferences cookiePrefs;

    /**
     * Construct a persistent cookie store.
     */
    public PersistentCookieStore(Context context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        cookies = new ConcurrentHashMap<String, Cookie>();

        /*// Load any previously stored cookies into the store*/
        String storedCookieNames = cookiePrefs.getString(COOKIE_NAME_STORE, null);
        if(storedCookieNames != null) {
            String[] cookieNames = TextUtils.split(storedCookieNames, ",");
            for(String name : cookieNames) {
                String encodedCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                if(encodedCookie != null) {
                    Cookie decodedCookie = decodeCookie(encodedCookie);
                    if(decodedCookie != null) {
                        cookies.put(name, decodedCookie);
                    }
                }
            }

            /*// Clear out expired cookies*/
            clearExpired(new Date());
        }
    }

    @Override
    public void addCookie(Cookie cookie) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.PersistentCookieStore.addCookie(org.apache.http.cookie.Cookie)",this,cookie);try{String name = cookie.getName();

        /*// Save cookie into local store, or remove if expired*/
        if(!cookie.isExpired(new Date())) {
            cookies.put(name, cookie);
        } else {
            cookies.remove(name);
        }

        /*// Save cookie into persistent store*/
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putString(COOKIE_NAME_STORE, TextUtils.join(",", cookies.keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableCookie(cookie)));
        prefsWriter.commit();com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.PersistentCookieStore.addCookie(org.apache.http.cookie.Cookie)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.PersistentCookieStore.addCookie(org.apache.http.cookie.Cookie)",this,throwable);throw throwable;}
    }

    @Override
    public void clear() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.PersistentCookieStore.clear()",this);try{/*// Clear cookies from local store*/
        cookies.clear();

        /*// Clear cookies from persistent store*/
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        for(String name : cookies.keySet()) {
            prefsWriter.remove(COOKIE_NAME_PREFIX + name);
        }
        prefsWriter.remove(COOKIE_NAME_STORE);
        prefsWriter.commit();com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.PersistentCookieStore.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.PersistentCookieStore.clear()",this,throwable);throw throwable;}
    }

    @Override
    public boolean clearExpired(Date date) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.PersistentCookieStore.clearExpired(java.util.Date)",this,date);try{boolean clearedAny = false;
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();

        for(ConcurrentHashMap.Entry<String, Cookie> entry : cookies.entrySet()) {
            String name = entry.getKey();
            Cookie cookie = entry.getValue();
            if(cookie.isExpired(date)) {
                /*// Clear cookies from local store*/
                cookies.remove(name);

                /*// Clear cookies from persistent store*/
                prefsWriter.remove(COOKIE_NAME_PREFIX + name);

                /*// We've cleared at least one*/
                clearedAny = true;
            }
        }

        /*// Update names in persistent store*/
        if(clearedAny) {
            prefsWriter.putString(COOKIE_NAME_STORE, TextUtils.join(",", cookies.keySet()));
        }
        prefsWriter.commit();

        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.PersistentCookieStore.clearExpired(java.util.Date)",this);return clearedAny;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.PersistentCookieStore.clearExpired(java.util.Date)",this,throwable);throw throwable;}
    }

    @Override
    public List<Cookie> getCookies() {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.chanapps.four.data.PersistentCookieStore.getCookies()",this);try{com.mijack.Xlog.logMethodExit("java.util.ArrayList com.chanapps.four.data.PersistentCookieStore.getCookies()",this);return new ArrayList<Cookie>(cookies.values());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.chanapps.four.data.PersistentCookieStore.getCookies()",this,throwable);throw throwable;}
    }


    /*//*/
    /*// Cookie serialization/deserialization*/
    /*//*/

    protected String encodeCookie(SerializableCookie cookie) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.PersistentCookieStore.encodeCookie(com.chanapps.four.data.SerializableCookie)",this,cookie);try{ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (Exception e) {
            {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.PersistentCookieStore.encodeCookie(com.chanapps.four.data.SerializableCookie)",this);return null;}
        }

        {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.PersistentCookieStore.encodeCookie(com.chanapps.four.data.SerializableCookie)",this);return byteArrayToHexString(os.toByteArray());}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.PersistentCookieStore.encodeCookie(com.chanapps.four.data.SerializableCookie)",this,throwable);throw throwable;}
    }

    public String dump() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.PersistentCookieStore.dump()",this);try{StringBuilder s = new StringBuilder();
        for (Cookie cookie : getCookies()) {
            SerializableCookie serializableCookie = new SerializableCookie(cookie);
            String c = encodeCookie(serializableCookie);
            s.append("".equals(s) ? "" : ", ").append(c);
        }
        {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.PersistentCookieStore.dump()",this);return s.toString();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.PersistentCookieStore.dump()",this,throwable);throw throwable;}
    }

    protected static final String TAG = PersistentCookieStore.class.getSimpleName();

    protected Cookie decodeCookie(String cookieStr) {
        com.mijack.Xlog.logMethodEnter("org.apache.http.cookie.Cookie com.chanapps.four.data.PersistentCookieStore.decodeCookie(java.lang.String)",this,cookieStr);try{byte[] bytes = hexStringToByteArray(cookieStr);
        InputStream is = new BufferedInputStream(new ByteArrayInputStream(bytes));
        Cookie cookie = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            cookie = ((SerializableCookie)ois.readObject()).getCookie();
        } catch (Exception e) {
            Log.e(TAG, "Exception decoding cookie=" + cookieStr, e);
        }

        {com.mijack.Xlog.logMethodExit("org.apache.http.cookie.Cookie com.chanapps.four.data.PersistentCookieStore.decodeCookie(java.lang.String)",this);return cookie;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("org.apache.http.cookie.Cookie com.chanapps.four.data.PersistentCookieStore.decodeCookie(java.lang.String)",this,throwable);throw throwable;}
    }

    /*// Using some super basic byte array <-> hex conversions so we don't have*/
    /*// to rely on any large Base64 libraries. Can be overridden if you like!*/
    protected String byteArrayToHexString(byte[] b) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.PersistentCookieStore.byteArrayToHexString([byte)",this,b);try{StringBuffer sb = new StringBuffer(b.length * 2);
        for (byte element : b) {
            int v = element & 0xff;
            if(v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.PersistentCookieStore.byteArrayToHexString([byte)",this);return sb.toString().toUpperCase();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.PersistentCookieStore.byteArrayToHexString([byte)",this,throwable);throw throwable;}
    }

    protected byte[] hexStringToByteArray(String s) {
        com.mijack.Xlog.logMethodEnter("[byte com.chanapps.four.data.PersistentCookieStore.hexStringToByteArray(java.lang.String)",this,s);try{int len = s.length();
        byte[] data = new byte[len / 2];
        for(int i=0; i<len; i+=2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        {com.mijack.Xlog.logMethodExit("[byte com.chanapps.four.data.PersistentCookieStore.hexStringToByteArray(java.lang.String)",this);return data;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[byte com.chanapps.four.data.PersistentCookieStore.hexStringToByteArray(java.lang.String)",this,throwable);throw throwable;}
    }
}
