package com.haringeymobile.ukweather.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Retrieves JSON data from URL.
 */
public class JsonFetcher {

    public static final int HTTP_STATUS_CODE_OK = 200;
    /**
     * Maximum time to wait while connecting (in miliseconds).
     */
    public static final int TIMEOUT = 4500;
    public static final String GET = "GET";

    /**
     * Extracts JSON data from the provided web page as an {@link java.io.InputStream},
     * and converts the stream to a string.
     *
     * @param url a web address for the JSON data
     * @return a string representing the JSON data, or null if there are
     * connection problems
     * @throws java.io.IOException if there are connection problems
     */
    public String getJsonString(URL url) throws IOException {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.JsonFetcher.getJsonString(java.net.URL)",this,url);try{StringBuilder stringBuilder = null;
        InputStream inputStream = null;
        try {
            HttpURLConnection connection = getConnection(url);
            inputStream = connection.getInputStream();
            stringBuilder = readData(inputStream);
            connection.disconnect();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.JsonFetcher.getJsonString(java.net.URL)",this);return stringBuilder.toString();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.JsonFetcher.getJsonString(java.net.URL)",this,throwable);throw throwable;}
    }

    /**
     * Opens and sets the network connection.
     *
     * @param url a web address for the JSON data
     * @return a connection pointing to a JSON data resource
     * @throws java.io.IOException if there are connection problems
     */
    private HttpURLConnection getConnection(URL url) throws IOException {
        com.mijack.Xlog.logMethodEnter("java.net.HttpURLConnection com.haringeymobile.ukweather.data.JsonFetcher.getConnection(java.net.URL)",this,url);try{HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(TIMEOUT);
        connection.setConnectTimeout(TIMEOUT * 2);
        connection.setRequestMethod(GET);
        connection.setDoInput(true);
        connection.connect();
        {com.mijack.Xlog.logMethodExit("java.net.HttpURLConnection com.haringeymobile.ukweather.data.JsonFetcher.getConnection(java.net.URL)",this);return connection;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.net.HttpURLConnection com.haringeymobile.ukweather.data.JsonFetcher.getConnection(java.net.URL)",this,throwable);throw throwable;}
    }

    /**
     * Fetches data from the provided input stream.
     *
     * @param inputStream readable source of bytes
     * @return a set of characters representing the extracted data
     * @throws java.io.IOException in case of some stream reading problems
     */
    private StringBuilder readData(InputStream inputStream) throws IOException {
        com.mijack.Xlog.logMethodEnter("java.lang.StringBuilder com.haringeymobile.ukweather.data.JsonFetcher.readData(java.io.InputStream)",this,inputStream);try{BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }
        {com.mijack.Xlog.logMethodExit("java.lang.StringBuilder com.haringeymobile.ukweather.data.JsonFetcher.readData(java.io.InputStream)",this);return stringBuilder;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.StringBuilder com.haringeymobile.ukweather.data.JsonFetcher.readData(java.io.InputStream)",this,throwable);throw throwable;}
    }
}
