package com.u17od.upm.dropbox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Singleton instance of {@link DbxClientV2} and friends
 */
public class DropboxClientFactory {

    private static DbxClientV2 sDbxClient;
    private static String accessToken;

    public static void init(String accessToken) {
        com.mijack.Xlog.logStaticMethodEnter("void com.u17od.upm.dropbox.DropboxClientFactory.init(java.lang.String)",accessToken);try{if (sDbxClient == null || DropboxClientFactory.accessToken != accessToken) {
            DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("upm")
                    .withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
                    .build();

            sDbxClient = new DbxClientV2(requestConfig, accessToken);
            DropboxClientFactory.accessToken = accessToken;
        }com.mijack.Xlog.logStaticMethodExit("void com.u17od.upm.dropbox.DropboxClientFactory.init(java.lang.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.u17od.upm.dropbox.DropboxClientFactory.init(java.lang.String)",throwable);throw throwable;}
    }

    public static DbxClientV2 getClient() {
        com.mijack.Xlog.logStaticMethodEnter("com.dropbox.core.v2.DbxClientV2 com.u17od.upm.dropbox.DropboxClientFactory.getClient()");try{if (sDbxClient == null) {
            throw new IllegalStateException("Client not initialized.");
        }
        {com.mijack.Xlog.logStaticMethodExit("com.dropbox.core.v2.DbxClientV2 com.u17od.upm.dropbox.DropboxClientFactory.getClient()");return sDbxClient;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.dropbox.core.v2.DbxClientV2 com.u17od.upm.dropbox.DropboxClientFactory.getClient()",throwable);throw throwable;}
    }
}
