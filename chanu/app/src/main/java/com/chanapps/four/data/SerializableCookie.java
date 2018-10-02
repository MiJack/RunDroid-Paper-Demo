package com.chanapps.four.data;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * A wrapper class around {@link Cookie} and/or {@link BasicClientCookie}
 * designed for use in {@link PersistentCookieStore}.
 */
public class SerializableCookie implements Serializable {
    private static final long serialVersionUID = 6374381828722046732L;

    private transient final Cookie cookie;
    private transient BasicClientCookie clientCookie;

    public SerializableCookie(Cookie cookie) {
        this.cookie = cookie;
    }

    public Cookie getCookie() {
        com.mijack.Xlog.logMethodEnter("org.apache.http.cookie.Cookie com.chanapps.four.data.SerializableCookie.getCookie()",this);try{Cookie bestCookie = cookie;
        if(clientCookie != null) {
            bestCookie = clientCookie;
        }
        {com.mijack.Xlog.logMethodExit("org.apache.http.cookie.Cookie com.chanapps.four.data.SerializableCookie.getCookie()",this);return bestCookie;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("org.apache.http.cookie.Cookie com.chanapps.four.data.SerializableCookie.getCookie()",this,throwable);throw throwable;}
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.SerializableCookie.writeObject(java.io.ObjectOutputStream)",this,out);try{out.writeObject(cookie.getName());
        out.writeObject(cookie.getValue());
        out.writeObject(cookie.getComment());
        out.writeObject(cookie.getDomain());
        out.writeObject(cookie.getExpiryDate());
        out.writeObject(cookie.getPath());
        out.writeInt(cookie.getVersion());
        out.writeBoolean(cookie.isSecure());com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.SerializableCookie.writeObject(java.io.ObjectOutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.SerializableCookie.writeObject(java.io.ObjectOutputStream)",this,throwable);throw throwable;}
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.SerializableCookie.readObject(java.io.ObjectInputStream)",this,in);try{String name = (String)in.readObject();
        String value = (String)in.readObject();
        clientCookie = new BasicClientCookie(name, value);
        clientCookie.setComment((String)in.readObject());
        clientCookie.setDomain((String)in.readObject());
        clientCookie.setExpiryDate((Date)in.readObject());
        clientCookie.setPath((String)in.readObject());
        clientCookie.setVersion(in.readInt());
        clientCookie.setSecure(in.readBoolean());com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.SerializableCookie.readObject(java.io.ObjectInputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.SerializableCookie.readObject(java.io.ObjectInputStream)",this,throwable);throw throwable;}
    }
}