package com.jtmcn.archwiki.viewer.data;

/**
 * Wrapper for a downloaded wiki page which holds the title and html.
 */
public class WikiPage {
	private final String pageUrl;
	private final String pageTitle;
	private final String htmlString;
	private int scrollPosition = 0;

	/**
	 * Store the url, title, and html of a page on the wiki.
	 *
	 * @param pageUrl    the string url on the wiki.
	 * @param pageTitle  the title of the page on the wiki.
	 * @param htmlString the html which should be shown to represent the page.
	 */
	public WikiPage(String pageUrl, String pageTitle, String htmlString) {
		this.pageUrl = pageUrl;
		this.pageTitle = pageTitle;
		this.htmlString = htmlString;
	}

	public String getPageUrl() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.getPageUrl()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.getPageUrl()",this);return pageUrl;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.getPageUrl()",this,throwable);throw throwable;}
	}

	public String getPageTitle() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.getPageTitle()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.getPageTitle()",this);return pageTitle;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.getPageTitle()",this,throwable);throw throwable;}
	}

	public String getHtmlString() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.getHtmlString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.getHtmlString()",this);return htmlString;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.getHtmlString()",this,throwable);throw throwable;}
	}

	@Override
	public String toString() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.toString()",this);try{StringBuilder sb = new StringBuilder("WikiPage{");
		sb.append("title='").append(pageTitle).append('\'');
		sb.append('}');
		{com.mijack.Xlog.logMethodExit("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.toString()",this);return sb.toString();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPage.toString()",this,throwable);throw throwable;}
	}

	public int getScrollPosition() {
		com.mijack.Xlog.logMethodEnter("int com.jtmcn.archwiki.viewer.data.WikiPage.getScrollPosition()",this);try{com.mijack.Xlog.logMethodExit("int com.jtmcn.archwiki.viewer.data.WikiPage.getScrollPosition()",this);return scrollPosition;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.jtmcn.archwiki.viewer.data.WikiPage.getScrollPosition()",this,throwable);throw throwable;}
	}

	public void setScrollPosition(int scrollPosition) {
		com.mijack.Xlog.logMethodEnter("void com.jtmcn.archwiki.viewer.data.WikiPage.setScrollPosition(int)",this,scrollPosition);try{this.scrollPosition = scrollPosition;com.mijack.Xlog.logMethodExit("void com.jtmcn.archwiki.viewer.data.WikiPage.setScrollPosition(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.jtmcn.archwiki.viewer.data.WikiPage.setScrollPosition(int)",this,throwable);throw throwable;}
	}

	@Override
	public boolean equals(Object o) {
		com.mijack.Xlog.logMethodEnter("boolean com.jtmcn.archwiki.viewer.data.WikiPage.equals(java.lang.Object)",this,o);try{if (this == o) {{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.data.WikiPage.equals(java.lang.Object)",this);return true;}}
		if (!(o instanceof WikiPage)) {{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.data.WikiPage.equals(java.lang.Object)",this);return false;}}

		WikiPage wikiPage = (WikiPage) o;

		{com.mijack.Xlog.logMethodExit("boolean com.jtmcn.archwiki.viewer.data.WikiPage.equals(java.lang.Object)",this);return getPageUrl() != null ? getPageUrl().equals(wikiPage.getPageUrl()) : wikiPage.getPageUrl() == null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.data.WikiPage.equals(java.lang.Object)",this,throwable);throw throwable;}

	}

	@Override
	public int hashCode() {
		com.mijack.Xlog.logMethodEnter("int com.jtmcn.archwiki.viewer.data.WikiPage.hashCode()",this);try{com.mijack.Xlog.logMethodExit("int com.jtmcn.archwiki.viewer.data.WikiPage.hashCode()",this);return getPageUrl() != null ? getPageUrl().hashCode() : 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.jtmcn.archwiki.viewer.data.WikiPage.hashCode()",this,throwable);throw throwable;}
	}
}
