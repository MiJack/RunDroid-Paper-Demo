package com.jtmcn.archwiki.viewer.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import static com.jtmcn.archwiki.viewer.Constants.ARCHWIKI_BASE;

/**
 * Provides a simple interface to make queries against
 * and parse data from the arch wiki for searches.
 */
public class SearchResultsBuilder {
	public static final String SEARCH_URL = ARCHWIKI_BASE + "api.php?action=opensearch" +
			"&format=json&formatversion=2&namespace=0&limit=%d" +
			"&suggest=true&search=%s";
	private static final int DEFAULT_LIMIT = 10;

	private SearchResultsBuilder() {

	}

	/**
	 * Builds a string url to fetch search results.
	 *
	 * @param query the text to search for.
	 * @return a url to fetch.
	 */
	public static String getSearchQuery(String query) {
		com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.getSearchQuery(java.lang.String)",query);try{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.getSearchQuery(java.lang.String)");return getSearchQuery(query, DEFAULT_LIMIT);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.getSearchQuery(java.lang.String)",throwable);throw throwable;}
	}

	/**
	 * Builds a string url to fetch search results.
	 *
	 * @param query the text to search for.
	 * @param limit the maximum number of results to retrieve.
	 * @return a url to fetch.
	 */
	public static String getSearchQuery(String query, int limit) {
		com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.getSearchQuery(java.lang.String,int)",query,limit);try{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.getSearchQuery(java.lang.String,int)");return String.format(SEARCH_URL, limit, query);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.getSearchQuery(java.lang.String,int)",throwable);throw throwable;}
	}

	/**
	 * Builds a {@link List<SearchResult>} from the result of fetching with {@link #getSearchQuery(String, int)}.
	 *
	 * @param jsonResult the string returned from the query.
	 * @return a parsed list of the results.
	 */
	public static List<SearchResult> parseSearchResults(String jsonResult) {
		com.mijack.Xlog.logStaticMethodEnter("java.util.ArrayList com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.parseSearchResults(java.lang.String)",jsonResult);try{JsonParser jsonParser = new JsonParser();
		JsonElement jsonRoot = jsonParser.parse(jsonResult);
		List<SearchResult> toReturn = new ArrayList<>();
		if (jsonRoot.isJsonArray()) {
			JsonArray jsonArray = jsonRoot.getAsJsonArray();
			if (jsonArray.size() == 4) {
				String[] listOfPageTitles = getJsonArrayAsStringArray(jsonArray.get(1).getAsJsonArray());
				String[] listOfPageUrls = getJsonArrayAsStringArray(jsonArray.get(3).getAsJsonArray());
				for (int i = 0; i < listOfPageTitles.length; i++) {
					toReturn.add(new SearchResult(listOfPageTitles[i], listOfPageUrls[i]));
				}
			}
		}
		{com.mijack.Xlog.logStaticMethodExit("java.util.ArrayList com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.parseSearchResults(java.lang.String)");return toReturn;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.ArrayList com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.parseSearchResults(java.lang.String)",throwable);throw throwable;}
	}

	/**
	 * Convert a {@link JsonArray} into an array of strings.
	 *
	 * @param jsonArray the array to be parsed.
	 * @return the string array which was parsed.
	 */
	private static String[] getJsonArrayAsStringArray(JsonArray jsonArray) {
		com.mijack.Xlog.logStaticMethodEnter("[java.lang.String com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.getJsonArrayAsStringArray(com.google.gson.JsonArray)",jsonArray);try{String[] s2 = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			s2[i] = jsonArray.get(i).getAsString();
		}
		{com.mijack.Xlog.logStaticMethodExit("[java.lang.String com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.getJsonArrayAsStringArray(com.google.gson.JsonArray)");return s2;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[java.lang.String com.jtmcn.archwiki.viewer.data.SearchResultsBuilder.getJsonArrayAsStringArray(com.google.gson.JsonArray)",throwable);throw throwable;}
	}
}
