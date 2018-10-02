package com.jtmcn.archwiki.viewer.tasks;

import android.os.AsyncTask;

import com.jtmcn.archwiki.viewer.data.SearchResult;
import com.jtmcn.archwiki.viewer.data.SearchResultsBuilder;
import com.jtmcn.archwiki.viewer.data.WikiPage;
import com.jtmcn.archwiki.viewer.data.WikiPageBuilder;

import java.util.List;

/**
 * Wrapper for {@link FetchUrl} which gives an easy to use interface
 * for fetching {@link SearchResult} and {@link WikiPage}.
 */
public class Fetch {
	public static final FetchUrl.FetchUrlMapper<List<SearchResult>> SEARCH_RESULTS_MAPPER =
			new FetchUrl.FetchUrlMapper<List<SearchResult>>() {
				@Override
				public List<SearchResult> mapTo(String url, StringBuilder stringBuilder) {
					com.mijack.Xlog.logMethodEnter("java.util.List com.jtmcn.archwiki.viewer.tasks.Fetch$1.mapTo(java.lang.String,java.lang.StringBuilder)",this,url,stringBuilder);try{com.mijack.Xlog.logMethodExit("java.util.List com.jtmcn.archwiki.viewer.tasks.Fetch$1.mapTo(java.lang.String,java.lang.StringBuilder)",this);return SearchResultsBuilder.parseSearchResults(stringBuilder.toString());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.List com.jtmcn.archwiki.viewer.tasks.Fetch$1.mapTo(java.lang.String,java.lang.StringBuilder)",this,throwable);throw throwable;}
				}
			};

	public static final FetchUrl.FetchUrlMapper<WikiPage> WIKIPAGE_MAPPER =
			new FetchUrl.FetchUrlMapper<WikiPage>() {
				@Override
				public WikiPage mapTo(String url, StringBuilder sb) {
					com.mijack.Xlog.logMethodEnter("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.tasks.Fetch$2.mapTo(java.lang.String,java.lang.StringBuilder)",this,url,sb);try{com.mijack.Xlog.logMethodExit("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.tasks.Fetch$2.mapTo(java.lang.String,java.lang.StringBuilder)",this);return WikiPageBuilder.buildPage(url, sb);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.tasks.Fetch$2.mapTo(java.lang.String,java.lang.StringBuilder)",this,throwable);throw throwable;}
				}
			};

	private Fetch() {

	}

	/**
	 * Fetches a {@link List<SearchResult>} from the url.
	 *
	 * @param onFinish The listener called when search results are ready.
	 * @param url      The url to fetch the search results from.
	 * @return the async task fetching the data.
	 */
	public static AsyncTask<String, Void, List<SearchResult>> search(
			FetchUrl.OnFinish<List<SearchResult>> onFinish,
			String url
	) {
		com.mijack.Xlog.logStaticMethodEnter("android.os.AsyncTask com.jtmcn.archwiki.viewer.tasks.Fetch.search(FetchUrl.OnFinish,java.lang.String)",onFinish,url);try{com.mijack.Xlog.logStaticMethodExit("android.os.AsyncTask com.jtmcn.archwiki.viewer.tasks.Fetch.search(FetchUrl.OnFinish,java.lang.String)");return new FetchUrl<>(onFinish, SEARCH_RESULTS_MAPPER).execute(url);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.os.AsyncTask com.jtmcn.archwiki.viewer.tasks.Fetch.search(FetchUrl.OnFinish,java.lang.String)",throwable);throw throwable;}
	}

	/**
	 * Fetches a {@link WikiPage} from the url.
	 *
	 * @param onFinish The listener called when the page is ready.
	 * @param url      The url to fetch the page from.
	 * @return the async task fetching the data.
	 */
	public static AsyncTask<String, Void, WikiPage> page(
			FetchUrl.OnFinish<WikiPage> onFinish,
			String url,
			boolean caching
	) {
		com.mijack.Xlog.logStaticMethodEnter("android.os.AsyncTask com.jtmcn.archwiki.viewer.tasks.Fetch.page(FetchUrl.OnFinish,java.lang.String,boolean)",onFinish,url,caching);try{com.mijack.Xlog.logStaticMethodExit("android.os.AsyncTask com.jtmcn.archwiki.viewer.tasks.Fetch.page(FetchUrl.OnFinish,java.lang.String,boolean)");return new FetchUrl<>(onFinish, WIKIPAGE_MAPPER).execute(url);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.os.AsyncTask com.jtmcn.archwiki.viewer.tasks.Fetch.page(FetchUrl.OnFinish,java.lang.String,boolean)",throwable);throw throwable;}
	}

}
