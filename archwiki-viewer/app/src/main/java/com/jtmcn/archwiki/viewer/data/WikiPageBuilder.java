package com.jtmcn.archwiki.viewer.data;

import static com.jtmcn.archwiki.viewer.Constants.LOCAL_CSS;

/**
 * Helps with creating a {@link WikiPage} by extracting content from the
 * html fetched from the ArchWiki.
 */
public class WikiPageBuilder {
	/*//NOTE: spaces are allowed in "<head>"/etc, but parsing this way should be fine*/
	public static final String HTML_HEAD_OPEN = "<head>";
	public static final String HTML_HEAD_CLOSE = "</head>";
	public static final String HTML_TITLE_OPEN = "<title>";
	public static final String HTML_TITLE_CLOSE = "</title>";
	public static final String HEAD_TO_INJECT = "<link rel='stylesheet' href='%s' />"
			+ "<meta name='viewport' content='width=device-width, initial-scale=1.0, user-scalable=no' />";
	public static final String DEFAULT_TITLE = " - ArchWiki";

	private WikiPageBuilder() {

	}

	/**
	 * Builds a page containing the title, url, and injects local css.
	 *
	 * @param stringUrl url to download.
	 * @param html      stringbuilder containing the html of the wikipage
	 * @return {@link WikiPage} containing downloaded page.
	 */
	public static WikiPage buildPage(String stringUrl, StringBuilder html) {
		com.mijack.Xlog.logStaticMethodEnter("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.data.WikiPageBuilder.buildPage(java.lang.String,java.lang.StringBuilder)",stringUrl,html);try{String pageTitle = getPageTitle(html);
		injectLocalCSS(html, LOCAL_CSS);
		{com.mijack.Xlog.logStaticMethodExit("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.data.WikiPageBuilder.buildPage(java.lang.String,java.lang.StringBuilder)");return new WikiPage(stringUrl, pageTitle, html.toString());}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.jtmcn.archwiki.viewer.data.WikiPage com.jtmcn.archwiki.viewer.data.WikiPageBuilder.buildPage(java.lang.String,java.lang.StringBuilder)",throwable);throw throwable;}
	}

	/**
	 * Finds the name of the page within the title block of the html.
	 * The returned string removes the " - ArchWiki" if found.
	 *
	 * @param htmlString The html of the page as a string.
	 * @return the extracted title from the page.
	 */
	public static String getPageTitle(StringBuilder htmlString) {
		com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPageBuilder.getPageTitle(java.lang.StringBuilder)",htmlString);try{int titleStart = (htmlString.indexOf(HTML_TITLE_OPEN) + HTML_TITLE_OPEN.length());
		int titleEnd = htmlString.indexOf(HTML_TITLE_CLOSE, titleStart);
		if (titleStart > 0 && titleEnd > titleStart) { /*// if there is an html title block*/
			String title = htmlString.substring(titleStart, titleEnd);
			{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPageBuilder.getPageTitle(java.lang.StringBuilder)");return title.replace(DEFAULT_TITLE, "");} /*// drop DEFAULT_TITLE from page title*/
		}
		/*//todo should be handled somewhere else when no title is found*/
		{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPageBuilder.getPageTitle(java.lang.StringBuilder)");return "No title found";}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.jtmcn.archwiki.viewer.data.WikiPageBuilder.getPageTitle(java.lang.StringBuilder)",throwable);throw throwable;}
	}

	/**
	 * Removes the contents within the head block of the html
	 * and replaces it with the a reference to a local css file.
	 *
	 * @param htmlString       The html of the page as a string.
	 * @param localCSSFilePath The path of the css file to inject.
	 * @return true if the block was successfully replaced.
	 */
	public static boolean injectLocalCSS(StringBuilder htmlString, String localCSSFilePath) {
		com.mijack.Xlog.logStaticMethodEnter("boolean com.jtmcn.archwiki.viewer.data.WikiPageBuilder.injectLocalCSS(java.lang.StringBuilder,java.lang.String)",htmlString,localCSSFilePath);try{int headStart = htmlString.indexOf(HTML_HEAD_OPEN) + HTML_HEAD_OPEN.length();
		int headEnd = htmlString.indexOf(HTML_HEAD_CLOSE, headStart);

		if (headStart > 0 && headEnd >= headStart) {
			String injectedHeadHtml = String.format(HEAD_TO_INJECT, localCSSFilePath);
			htmlString.replace(headStart, headEnd, injectedHeadHtml);
			{com.mijack.Xlog.logStaticMethodExit("boolean com.jtmcn.archwiki.viewer.data.WikiPageBuilder.injectLocalCSS(java.lang.StringBuilder,java.lang.String)");return true;}
		}

		{com.mijack.Xlog.logStaticMethodExit("boolean com.jtmcn.archwiki.viewer.data.WikiPageBuilder.injectLocalCSS(java.lang.StringBuilder,java.lang.String)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.jtmcn.archwiki.viewer.data.WikiPageBuilder.injectLocalCSS(java.lang.StringBuilder,java.lang.String)",throwable);throw throwable;}
	}
}