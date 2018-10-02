package com.jtmcn.archwiki.viewer.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.jtmcn.archwiki.viewer.utils.NetworkUtils;

import java.io.IOException;

/**
 * Fetches a url, maps it to a {@link Result}, and returns it.
 *
 * @param <Result> The type which the fetched url's text will be mapped to.
 */
public class FetchUrl<Result> extends AsyncTask<String, Void, Result> {
	private static final String TAG = FetchUrl.class.getSimpleName();
	private final OnFinish<Result> onFinish;
	private final FetchUrlMapper<Result> mapper;
	private final boolean caching;

	public FetchUrl(OnFinish<Result> onFinish, FetchUrlMapper<Result> mapper) {
		this(onFinish, mapper, true);
	}

	/**
	 * Fetches the first url and notifies the {@link OnFinish} listener.
	 *
	 * @param onFinish The function to be called when the result is ready.
	 * @param mapper   The function to map from the url and downloaded page to the desired type.
	 * @param caching  Whether or not to use cached results
	 */
	public FetchUrl(OnFinish<Result> onFinish, FetchUrlMapper<Result> mapper, boolean caching) {
		this.onFinish = onFinish;
		this.mapper = mapper;
		this.caching = caching;
	}

	@Override
	protected Result doInBackground(String... params) {
		com.mijack.Xlog.logMethodEnter("Result com.jtmcn.archwiki.viewer.tasks.FetchUrl<Result>.doInBackground([java.lang.String)",this,params);try{if (params.length >= 1) {
			String url = params[0];
			StringBuilder toAdd = getItem(url);
			{com.mijack.Xlog.logMethodExit("Result com.jtmcn.archwiki.viewer.tasks.FetchUrl<Result>.doInBackground([java.lang.String)",this);return mapper.mapTo(url, toAdd);}
		}
		{com.mijack.Xlog.logMethodExit("Result com.jtmcn.archwiki.viewer.tasks.FetchUrl<Result>.doInBackground([java.lang.String)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Result com.jtmcn.archwiki.viewer.tasks.FetchUrl<Result>.doInBackground([java.lang.String)",this,throwable);throw throwable;}
	}

	@Override
	protected void onPostExecute(Result values) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.tasks.FetchUrl<Result>.onPostExecute(Result)",this,values);try{super.onPostExecute(values);
		if (onFinish != null) {
			onFinish.onFinish(values);
		}com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.tasks.FetchUrl<Result>.onPostExecute(Result)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.tasks.FetchUrl<Result>.onPostExecute(Result)",this,throwable);throw throwable;}
	}

	/**
	 * Fetches a url and returns what was downloaded or null
	 *
	 * @param url to query
	 */
	private StringBuilder getItem(String url) {
		com.mijack.Xlog.logMethodEnter("java.lang.StringBuilder com.jtmcn.archwiki.viewer.tasks.FetchUrl<Result>.getItem(java.lang.String)",this,url);try{StringBuilder toReturn;
		try {
			toReturn = NetworkUtils.fetchURL(url, caching);
		} catch (IOException e) { /*//network exception*/
			Log.w(TAG, "Could not connect to: " + url, e);
			toReturn = new StringBuilder();
		}

		{com.mijack.Xlog.logMethodExit("java.lang.StringBuilder com.jtmcn.archwiki.viewer.tasks.FetchUrl<Result>.getItem(java.lang.String)",this);return toReturn;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.StringBuilder com.jtmcn.archwiki.viewer.tasks.FetchUrl<Result>.getItem(java.lang.String)",this,throwable);throw throwable;}
	}

	/**
	 * A listener which is called when {@link Result} is ready.
	 *
	 * @param <Result> the type of object which has been created.
	 */
	public interface OnFinish<Result> {
		void onFinish(Result result);
	}

	/**
	 * Maps the url and fetched text to {@link R}
	 *
	 * @param <R> The type which the text will be mapped to.
	 */
	public interface FetchUrlMapper<R> {
		R mapTo(String url, StringBuilder sb);
	}
}
