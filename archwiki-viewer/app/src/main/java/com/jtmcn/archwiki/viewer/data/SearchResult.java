package com.jtmcn.archwiki.viewer.data;

/**
 * A page on the wiki which only knows the name and url.
 */
public class SearchResult {
	private final String pageName;
	private final String pageUrl;

	/**
	 * Create a search result.
	 *
	 * @param pageName the name of the page as shown on the wiki.
	 * @param pageUrl  the string url on the wiki.
	 */
	public SearchResult(String pageName, String pageUrl) {
		this.pageName = pageName;
		this.pageUrl = pageUrl;
	}

	public String getPageName() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResult.getPageName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResult.getPageName()",this);return pageName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResult.getPageName()",this,throwable);throw throwable;}
	}

	public String getPageUrl() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResult.getPageUrl()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResult.getPageUrl()",this);return pageUrl;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResult.getPageUrl()",this,throwable);throw throwable;}
	}

	@Override
	public String toString() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResult.toString()",this);try{StringBuilder sb = new StringBuilder("SearchResult{");
		sb.append("title='").append(pageName).append('\'');
		sb.append(", url='").append(pageUrl).append('\'');
		sb.append('}');
		{com.mijack.Xlog.logMethodExit("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResult.toString()",this);return sb.toString();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.jtmcn.archwiki.viewer.data.SearchResult.toString()",this,throwable);throw throwable;}
	}
}
