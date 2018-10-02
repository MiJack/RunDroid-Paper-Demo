package com.chanapps.four.data;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;
import android.util.Log;

import com.chanapps.four.activity.R;
import com.chanapps.four.component.URLFormatComponent;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

public class ChanPost implements Serializable {

	public static final String TAG = ChanPost.class.getSimpleName();
    private static final boolean DEBUG = false;

    /*//public static final String HEADLINE_BOARDLEVEL_DELIMITER = "<br/>";*/
    public static final String HEADLINE_BOARDLEVEL_DELIMITER = " ";
    public static final String HEADLINE_THREADLEVEL_DELIMITER = "<br/>";
    /*//public static final String HEADLINE_THREADLEVEL_DELIMITER = " &middot; ";*/
    private static final int MIN_LINE = 30;
    private static final int MAX_LINE = 40;
    private static final int MAX_THREAD_SUBJECT_LEN = 100;
    private static final int MIN_SUBJECT_LEN = 2;
    private static final int MAX_SUBJECT_LEN = 50; /*// 4chan enforces 100*/

    public static final String POST_NO = "postNo";
    public static final String POST_ID = "_id";
    public static final String POST_BOARD_CODE = "boardCode";
    public static final String POST_NAME = "name";
    public static final String POST_EMAIL = "email";
    public static final String POST_TIM = "tim";
    public static final String POST_EXT = "ext";
    public static final String POST_W = "w";
    public static final String POST_H = "h";
    public static final String POST_TN_W = "tn_w";
    public static final String POST_TN_H = "tn_h";
    public static final String POST_RESTO = "resto";
    public static final String POST_HEADLINE_TEXT = "headlineText"; /*// we construct and filter this*/
    public static final String POST_NUM_REPLIES = "numReplies";
    public static final String POST_NUM_IMAGES = "numImages";
    public static final String POST_SUBJECT_TEXT = "subjectText"; /*// we construct and filter this // NOT USED*/
    public static final String POST_TEXT = "text"; /*// we construct and filter this*/
    public static final String POST_DATE_TEXT = "dateText"; /*// we construct and filter this*/
    public static final String POST_IMAGE_URL = "imageUrl"; /*// we construct this from board and tim*/
    public static final String POST_FULL_IMAGE_URL = "fullImageUrlrl"; /*// we construct this from board and tim*/
    public static final String POST_COUNTRY_URL = "countryUrl"; /*// we construct this from the country code*/
    private static final String POST_SPOILER_SUBJECT = "spoilerSubject";
    private static final String POST_SPOILER_TEXT = "spoilerText";
    public static final String POST_EXIF_TEXT = "exifText";
    public static final String POST_USER_ID = "id";
    public static final String POST_TRIPCODE = "trip";
    public static final String POST_THUMBNAIL_ID = "postThumbnailId";
    public static final String POST_BACKLINKS_BLOB = "backlinksBlob";
    public static final String POST_REPLIES_BLOB = "repliesBlob";
    public static final String POST_SAME_IDS_BLOB = "sameIdsBlob";
    public static final String POST_FSIZE = "fileSize";
    public static final String POST_FLAGS = "postFlags";
    public static final int FLAG_HAS_IMAGE = 0x001;
    public static final int FLAG_HAS_SUBJECT = 0x002;
    public static final int FLAG_HAS_TEXT = 0x004;
    public static final int FLAG_HAS_SPOILER = 0x008;
    public static final int FLAG_HAS_EXIF = 0x010;
    public static final int FLAG_HAS_COUNTRY = 0x020;
    public static final int FLAG_IS_DEAD = 0x040;
    public static final int FLAG_IS_CLOSED = 0x080;
    public static final int FLAG_IS_HEADER = 0x2000;
    public static final int FLAG_NO_EXPAND = 0x8000;
    public static final int FLAG_HAS_HEAD  = 0x10000;
    public static final int FLAG_IS_STICKY = 0x20000;

    private static final String HIGHLIGHT_COLOR = "#aaa268";
    private static final String LINK_COLOR = "#33b5e5";

    public static String planifyText(String text) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanPost.planifyText(java.util.String)",text);try{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.planifyText(java.util.String)");return text.replaceAll("<br/?>", "\n").replaceAll("<[^>]*>", "");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.planifyText(java.util.String)",throwable);throw throwable;}
    }

    public static String join(List<String> list, String delimiter) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanPost.join(java.util.List,java.util.String)",list,delimiter);try{StringBuilder text = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first) {
                text.append(item);
                first = false;
                continue;
            }
            text.append(delimiter + item);
        }
        {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.join(java.util.List,java.util.String)");return text.toString();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.join(java.util.List,java.util.String)",throwable);throw throwable;}
    }

    public static int countLines(String s) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.data.ChanPost.countLines(java.util.String)",s);try{if (s == null || s.isEmpty())
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanPost.countLines(java.util.String)");return 0;}}
        int i = 1;
        int idx = -1;
        while ((idx = s.indexOf('\n', idx + 1)) != -1) {
            i++;
        }
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanPost.countLines(java.util.String)");return i;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.data.ChanPost.countLines(java.util.String)",throwable);throw throwable;}
    }

    private int postFlags(String subject, String text, String exifText, String headline) {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.data.ChanPost.postFlags(java.util.String,java.util.String,java.util.String,java.util.String)",this,subject,text,exifText,headline);try{int flags = 0;
        if (tim > 0)
            {flags |= FLAG_HAS_IMAGE;}
        if (subject != null && !subject.isEmpty())
            {flags |= FLAG_HAS_SUBJECT;}
        if (text != null && !text.isEmpty())
            {flags |= FLAG_HAS_TEXT;}
        if (spoiler > 0)
            {flags |= FLAG_HAS_SPOILER;}
        if (exifText != null && !exifText.isEmpty())
            {flags |= FLAG_HAS_EXIF;}
        if (country != null && !country.isEmpty())
            {flags |= FLAG_HAS_COUNTRY;}
        if (isDead)
            {flags |= FLAG_IS_DEAD;}
        if (closed > 0)
            {flags |= FLAG_IS_CLOSED;}
        if (sticky > 0)
            {flags |= FLAG_IS_STICKY;}
        if (headline != null && !headline.isEmpty())
            {flags |= FLAG_HAS_HEAD;}
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanPost.postFlags(java.util.String,java.util.String,java.util.String,java.util.String)",this);return flags;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.data.ChanPost.postFlags(java.util.String,java.util.String,java.util.String,java.util.String)",this,throwable);throw throwable;}
    }

    private static final String[] POST_COLUMNS = {
            POST_ID,
            POST_BOARD_CODE,
            POST_RESTO,
            POST_IMAGE_URL,
            POST_FULL_IMAGE_URL,
            POST_COUNTRY_URL,
            POST_HEADLINE_TEXT,
            POST_NUM_REPLIES,
            POST_NUM_IMAGES,
            POST_SUBJECT_TEXT,
            POST_TEXT,
            POST_DATE_TEXT,
            POST_TN_W,
            POST_TN_H,
            POST_W,
            POST_H,
            POST_TIM,
            POST_SPOILER_SUBJECT,
            POST_SPOILER_TEXT,
            POST_EXIF_TEXT,
            POST_USER_ID,
            POST_TRIPCODE,
            POST_NAME,
            POST_EMAIL,
            POST_THUMBNAIL_ID,
            POST_EXT,
            POST_BACKLINKS_BLOB,
            POST_REPLIES_BLOB,
            POST_SAME_IDS_BLOB,
            POST_FSIZE,
            POST_FLAGS
    };

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String board;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingLongDeserializer.class)
    public long no = -1;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int sticky = 0;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int closed = 0;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String now;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String trip;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String id;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String capcode;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String country;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String country_name;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String email;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingDateDeserializer.class)
    public Date created;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingLongDeserializer.class)
    public long time = -1;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String name;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String sub;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String com;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingLongDeserializer.class)
    public long tim = 0;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String filename;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingStringDeserializer.class)
    public String ext;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int w = 0;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int h = 0;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int tn_w = 0;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int tn_h = 0;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int fsize = -1;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingLongDeserializer.class)
    public long resto = -1;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int replies = -1;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int images = -1;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int omitted_posts = -1;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int omitted_images = -1;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int bumplimit = 0;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int imagelimit = 0;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int spoiler = 0;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingIntegerDeserializer.class)
    public int filedeleted = 0;

    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingBooleanDeserializer.class)
    public boolean isDead = false;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingBooleanDeserializer.class)
    public boolean defData = false;

    /*// settings from prefs*/
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingBooleanDeserializer.class)
    public boolean hideAllText = false; /*// no longer used*/
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingBooleanDeserializer.class)
    public boolean hidePostNumbers = true;
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingBooleanDeserializer.class)
    public boolean useFriendlyIds = true;

    /*// to support latest posts and recent images direct jump to post number*/
    @JsonDeserialize(using=JacksonNonBlockingObjectMapperFactory.NonBlockingLongDeserializer.class)
    public long jumpToPostNo = 0;

    public static final String quoteText(String in, long resto) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanPost.quoteText(java.util.String,long)",in,resto);try{if (in == null || in.isEmpty())
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.quoteText(java.util.String,long)");return "";}}
        String s = in.replaceAll("<br/>", "\n");
        String o = "> ";
        int l = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\n') {
                o += "\n> ";
                l = 2;
            }
            else if (l < MIN_LINE) {
                o += c;
                l++;
            }
            else if (l > MAX_LINE) {
                o += "\n> " + c;
                l = 3;
            }
            else if (c == ' ') {
                o += "\n> ";
                l = 2;
            }
            else {
                o+= c;
                l++;
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.quoteText(java.util.String,long)");return o.replaceAll("> >", ">>").replaceAll("\n", "<br/>");}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.quoteText(java.util.String,long)",throwable);throw throwable;}
    }

    public String combinedSubCom() {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.combinedSubCom()",this);try{String[] textComponents = textComponents("");
        String s = textComponents[0];
        String t = textComponents[1];
        String u = (s != null && !s.isEmpty() ? "<b>" + s + "</b><br/>" : "")
                + t;
        {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.combinedSubCom()",this);return u;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.combinedSubCom()",this,throwable);throw throwable;}
    }

    private String cleanSubject(String subject) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.cleanSubject(java.util.String)",this,subject);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.cleanSubject(java.util.String)",this);return subject
                .trim()
                .replaceFirst("^(<br/?>)+", "")
                .replaceAll("(<br/?>)(<br/?>)+", "$1")
                .replaceFirst("(<br/?>)+$", "")
                .trim();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.cleanSubject(java.util.String)",this,throwable);throw throwable;}
    }

    private String cleanMessage(String message) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.cleanMessage(java.util.String)",this,message);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.cleanMessage(java.util.String)",this);return message
                .trim()
                .replaceFirst("^(<br/?>)+", "")
                .replaceFirst("(<br/?>)+$", "")
                .trim();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.cleanMessage(java.util.String)",this,throwable);throw throwable;}
    }

    private String highlightOP(String text) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.highlightOP(java.util.String)",this,text);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.highlightOP(java.util.String)",this);return text.replaceAll(">>" + resto,
                ">>"
                        + resto
                        + "<font color=\"" + LINK_COLOR + "\"><u> (OP)</u></font>");}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.highlightOP(java.util.String)",this,throwable);throw throwable;}
    }

    public String[] textComponents(String query) {
        com.mijack.Xlog.logMethodEnter("[java.util.String com.chanapps.four.data.ChanPost.textComponents(java.util.String)",this,query);try{String subText = sanitizeText(sub, false);
        String comText = sanitizeText(com, false);
        String subject = subText != null ? subText : "";
        String message = comText != null ? comText : "";

        if (resto > 0) {
            if (DEBUG) {Log.v(TAG, "default combinedSubCom=" + subject + " message=" + message);}
            String msg = highlightOP(message);
            {com.mijack.Xlog.logMethodExit("[java.util.String com.chanapps.four.data.ChanPost.textComponents(java.util.String)",this);return highlightComponents(cleanSubject(subject), cleanMessage(msg), query);}
        }

        if (!subject.isEmpty() || message.isEmpty()) { /*// we have a combinedSubCom or can't extract from message*/
            if (DEBUG) {Log.v(TAG, "provided combinedSubCom=" + subject + " message=" + message);}
            {com.mijack.Xlog.logMethodExit("[java.util.String com.chanapps.four.data.ChanPost.textComponents(java.util.String)",this);return highlightComponents(cleanSubject(subject), cleanMessage(message), query);}
        }
        if (comText.length() <= MAX_SUBJECT_LEN) { /*// just make message the combinedSubCom*/
            subject = cleanSubject(message);
            message = "";
            if (DEBUG) {Log.v(TAG, "made message the combinedSubCom=" + subject + " message=" + message);}
            {com.mijack.Xlog.logMethodExit("[java.util.String com.chanapps.four.data.ChanPost.textComponents(java.util.String)",this);return highlightComponents(subject, message, query);}
        }

        /*
        // start combinedSubCom extraction process
        String[] terminators = { "\r", "\n", "<br/>", "<br>", ". ", "! ", "? ", "; ", ": ", ", " };
        message = cleanMessage(message);
        for (String terminator : terminators) {
            int i = message.indexOf(terminator);
            if (i > MIN_SUBJECT_LEN && i < MAX_SUBJECT_LEN) { // extract the combinedSubCom
                int len = terminator.length();
                combinedSubCom = cleanSubject(message.substring(0, i + len));
                message = cleanMessage(message.substring(i + len));
                if (DEBUG) Log.v(TAG, "extracted combinedSubCom=" + combinedSubCom + " message=" + message);
                return highlightComponents(combinedSubCom, message, query);
            }
        }

        // cutoff
        int i = MAX_SUBJECT_LEN - 1; // start cut at max len
        while (!Character.isWhitespace(comText.charAt(i)) && i > 0)
            i--; // rewind until we reach a whitespace character
        if (i > MIN_SUBJECT_LEN) { // we found a suitable cutoff point
            combinedSubCom = cleanSubject(comText.substring(0, i));
            message = cleanMessage(comText.substring(i + 1));
            if (DEBUG) Log.v(TAG, "cutoff combinedSubCom=" + combinedSubCom + " message=" + message);
            return highlightComponents(combinedSubCom, message, query);
        }
        */
        /*// default*/
        if (DEBUG) {Log.v(TAG, "default combinedSubCom=" + subject + " message=" + message);}
        {com.mijack.Xlog.logMethodExit("[java.util.String com.chanapps.four.data.ChanPost.textComponents(java.util.String)",this);return highlightComponents(cleanSubject(subject), cleanMessage(message), query);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.util.String com.chanapps.four.data.ChanPost.textComponents(java.util.String)",this,throwable);throw throwable;}
    }

    private String[] highlightComponents(String subject, String message, String query) {
        com.mijack.Xlog.logMethodEnter("[java.util.String com.chanapps.four.data.ChanPost.highlightComponents(java.util.String,java.util.String,java.util.String)",this,subject,message,query);try{com.mijack.Xlog.logMethodExit("[java.util.String com.chanapps.four.data.ChanPost.highlightComponents(java.util.String,java.util.String,java.util.String)",this);return new String[] { highlightComponent(subject, query), highlightComponent(message, query) };}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.util.String com.chanapps.four.data.ChanPost.highlightComponents(java.util.String,java.util.String,java.util.String)",this,throwable);throw throwable;}
    }

    private String highlightComponent(String component, String query) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.highlightComponent(java.util.String,java.util.String)",this,component,query);try{if (query.isEmpty())
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.highlightComponent(java.util.String,java.util.String)",this);return component;}}
        String regex = "(?i)(" + query + ")";
        String replace = "<b><font color=\"" + HIGHLIGHT_COLOR + "\">$1</font></b>";
        {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.highlightComponent(java.util.String,java.util.String)",this);return component.replaceAll(regex, replace);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.highlightComponent(java.util.String,java.util.String)",this,throwable);throw throwable;}
    }

    public String threadSubject(Context context) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.threadSubject(android.content.Context)",this,context);try{String subText = sanitizeText(sub, false);
        if (subText != null && !subText.isEmpty())
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.threadSubject(android.content.Context)",this);return subText;}}
        String comText = sanitizeText(com, false);
        if (comText != null && !comText.isEmpty())
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.threadSubject(android.content.Context)",this);return comText.substring(0, Math.min(comText.length(), MAX_THREAD_SUBJECT_LEN));}} /*// always shorter than this since only one line*/
        if (name != null && !name.isEmpty() && !name.equalsIgnoreCase("anonymous"))
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.threadSubject(android.content.Context)",this);return name;}}
        if (email != null && !email.isEmpty() && !email.equalsIgnoreCase("sage"))
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.threadSubject(android.content.Context)",this);return email;}}
        {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.threadSubject(android.content.Context)",this);return "";}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.threadSubject(android.content.Context)",this,throwable);throw throwable;}
    }

    public String drawerSubject(Context context) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.drawerSubject(android.content.Context)",this,context);try{String threadSuffix = no > 0 ? String.valueOf(no) : "";
        String prefix = "/" + board + "/" + threadSuffix + " ";
        String subText = sanitizeText(sub, false);
        String comText = sanitizeText(com, false);
        String suffix;
        if (subText != null && !subText.isEmpty())
            {suffix = subText;}
        else if (comText != null && !comText.isEmpty())
            {suffix = comText.substring(0, Math.min(comText.length(), MAX_THREAD_SUBJECT_LEN));} /*// always shorter than this since only one line*/
        else
            {suffix = "";}
        suffix = suffix.replaceAll("<[^>]*>", "");
        {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.drawerSubject(android.content.Context)",this);return prefix + (suffix.length() > 0 ? " " : "") + suffix;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.drawerSubject(android.content.Context)",this,throwable);throw throwable;}
    }

    private String sanitizeText(String text, boolean collapseNewlines) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.sanitizeText(java.util.String,boolean)",this,text,collapseNewlines);try{if (text == null || text.isEmpty())
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.sanitizeText(java.util.String,boolean)",this);return "";}}

        long start = System.currentTimeMillis();

        if (hidePostNumbers)
            {text = text.replaceAll("<a[^>]*class=\"quotelink\">[^<]*</a>", "");}
        else
            {text = text.replaceAll("<a[^>]*class=\"quotelink\">([^<]*)</a>", "$1");}

        text = text
                .replaceAll("<span[^>]*class=\"abbr\"[^>]*>.*</span>", "")    /*// exif reference*/
                .replaceAll("<table[^>]*class=\"exif\"[^>]*>.*</table>", "");  /*// exif info*/
        text = textViewFilter(text, collapseNewlines);

        long end = System.currentTimeMillis();
        if (DEBUG) {Log.v(TAG, "Regexp: " + (end - start) + "ms");}

        {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.sanitizeText(java.util.String,boolean)",this);return text;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.sanitizeText(java.util.String,boolean)",this,throwable);throw throwable;}
    }

    public String exifText() {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.exifText()",this);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.exifText()",this);return exifText(com);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.exifText()",this,throwable);throw throwable;}
    }

    protected static final Pattern EXIF_PATTERN = Pattern.compile(".*<table[^>]*class=\"exif\"[^>]*>(.*)</table>.*");

    private static final String exifText(String text) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanPost.exifText(java.util.String)",text);try{if (text == null || text.isEmpty())
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.exifText(java.util.String)");return null;}}
        Matcher m = EXIF_PATTERN.matcher(text);
        if (!m.matches())
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.exifText(java.util.String)");return null;}}
        String g = m.group(1);
        if (g == null || g.isEmpty())
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.exifText(java.util.String)");return null;}}
        String s = g.replaceAll("<tr[^>]*><td colspan=\"2\"[^>]*><b>([^<]*)</b></td></tr>", "$1\n");
        String t = s.replaceAll("<tr[^>]*><td[^>]*>([^<]*)</td><td[^>]*>([^<]*)</td></tr>", "$1: $2\n");
        {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.exifText(java.util.String)");return textViewFilter(t);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.exifText(java.util.String)",throwable);throw throwable;}
    }

    private static final String textViewFilter(String s) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanPost.textViewFilter(java.util.String)",s);try{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.textViewFilter(java.util.String)");return textViewFilter(s, false);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.textViewFilter(java.util.String)",throwable);throw throwable;}
    }

    private static final String textViewFilter(String s, boolean collapseNewlines) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanPost.textViewFilter(java.util.String,boolean)",s,collapseNewlines);try{String t = s
                .replaceAll("<br */?>", "\n")
                .replaceAll("<[^s/][^>]+>", "") /*// preserve <s> tags*/
                .replaceAll("<s[^>]+>", "")
                .replaceAll("</[^s][^>]*>", "")
                .replaceAll("</s[^>]+>", "")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .replaceAll("&quot;", "\"")
                .replaceAll("&#0*39;", "'")
                .replaceAll("&#0*44;", ",")
                .replaceAll("&#[0-9abcdef]*;", "")
                .replaceFirst("^\n+", "")
                .replaceFirst("\n+$", "");
        if (collapseNewlines)
            {t = t.replaceAll("\n+", " ");}
        else
            {t = t.replaceAll("\n", "<br/>");}
        {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.textViewFilter(java.util.String,boolean)");return t.trim();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.textViewFilter(java.util.String,boolean)",throwable);throw throwable;}
    }

    public String toString() {
		com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.toString()",this);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.toString()",this);return "/" + board + "/" + (resto == 0 ? no : resto + "#p" + no) + " sub=" + sub + " com=" + com + " size=" + tn_w + "x" + tn_h;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.toString()",this,throwable);throw throwable;}
	}

    public String thumbnailUrl(Context context) { com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.thumbnailUrl(android.content.Context)",this,context);try{/*// thumbnail with fallback*/
        int stickyId = ChanBoard.imagelessStickyDrawableId(board, no);
        if (stickyId > 0)
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.thumbnailUrl(android.content.Context)",this);return "drawable://" + stickyId;}}
        else if (spoiler > 0)
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.thumbnailUrl(android.content.Context)",this);return ChanBoard.spoilerThumbnailUrl(context, board);}}
        else if (tim > 0 && filedeleted == 0) /*// && tn_w > 2 && tn_h > 2)*/
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.thumbnailUrl(android.content.Context)",this);return String.format(URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_THUMBS_URL_FORMAT), board, tim);}}
        else if (resto <= 0) /*// thread default*/
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.thumbnailUrl(android.content.Context)",this);return "drawable://" + ChanBoard.getRandomImageResourceId(board, no);}}
        else
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.thumbnailUrl(android.content.Context)",this);return "";}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.thumbnailUrl(android.content.Context)",this,throwable);throw throwable;}
    }

    public String lastReplyThumbnailUrl(final Context context, final String board) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.lastReplyThumbnailUrl(android.content.Context,java.util.String)",this,context,board);try{if (spoiler > 0)
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.lastReplyThumbnailUrl(android.content.Context,java.util.String)",this);return ChanBoard.spoilerThumbnailUrl(context, board);}}
        else if (tim > 0 && filedeleted == 0) /*// && tn_w > 2 && tn_h > 2)*/
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.lastReplyThumbnailUrl(android.content.Context,java.util.String)",this);return String.format(URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_THUMBS_URL_FORMAT), board, tim);}}
        else
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.lastReplyThumbnailUrl(android.content.Context,java.util.String)",this);return "";}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.lastReplyThumbnailUrl(android.content.Context,java.util.String)",this,throwable);throw throwable;}
    }

    public int thumbnailId() { com.mijack.Xlog.logMethodEnter("int com.chanapps.four.data.ChanPost.thumbnailId()",this);try{/*// for resource types*/
        int stickyId = ChanBoard.imagelessStickyDrawableId(board, no);
        if (stickyId > 0)
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanPost.thumbnailId()",this);return stickyId;}}
        else if (spoiler > 0)
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanPost.thumbnailId()",this);return 0;}}
        else if (tim > 0 && filedeleted == 0 && tn_w > 2 && tn_h > 2)
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanPost.thumbnailId()",this);return 0;}}
        else if (resto <= 0) /*// thread default*/
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanPost.thumbnailId()",this);return ChanBoard.getRandomImageResourceId(board, no);}}
        else
            {{com.mijack.Xlog.logMethodExit("int com.chanapps.four.data.ChanPost.thumbnailId()",this);return 0;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.data.ChanPost.thumbnailId()",this,throwable);throw throwable;}
    }

    public String imageUrl(Context context) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.imageUrl(android.content.Context)",this,context);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.imageUrl(android.content.Context)",this);return imageUrl(context, board, tim, ext);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.imageUrl(android.content.Context)",this,throwable);throw throwable;}
   	}

    protected static String imageUrl(Context context, String board, long tim, String ext) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanPost.imageUrl(android.content.Context,java.util.String,long,java.util.String)",context,board,tim,ext);try{if (tim != 0) {
            {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.imageUrl(android.content.Context,java.util.String,long,java.util.String)");return String.format(URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_IMAGE_URL_FORMAT), board, tim, ext);}
        }
        {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.imageUrl(android.content.Context,java.util.String,long,java.util.String)");return null;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.imageUrl(android.content.Context,java.util.String,long,java.util.String)",throwable);throw throwable;}
    }

   	public String imageName() {
   		com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.imageName()",this);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.imageName()",this);return no + ext;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.imageName()",this,throwable);throw throwable;}
   	}

    public String lastReplyCountryFlagUrl(Context context, String boardCode) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.lastReplyCountryFlagUrl(android.content.Context,java.util.String)",this,context,boardCode);try{if (country != null && !country.isEmpty())
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.lastReplyCountryFlagUrl(android.content.Context,java.util.String)",this);return countryFlagUrl(context, boardCode, country);}}
        else
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.lastReplyCountryFlagUrl(android.content.Context,java.util.String)",this);return null;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.lastReplyCountryFlagUrl(android.content.Context,java.util.String)",this,throwable);throw throwable;}
    }

    public String countryFlagUrl(Context context) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.countryFlagUrl(android.content.Context)",this,context);try{if (country != null && !country.isEmpty())
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.countryFlagUrl(android.content.Context)",this);return countryFlagUrl(context, board, country);}}
        else
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.countryFlagUrl(android.content.Context)",this);return null;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.countryFlagUrl(android.content.Context)",this,throwable);throw throwable;}
    }

    public String countryFlagUrl(Context context, String boardCode, String countryCode) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.countryFlagUrl(android.content.Context,java.util.String,java.util.String)",this,context,boardCode,countryCode);try{if (boardCode.equals("pol"))
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.countryFlagUrl(android.content.Context,java.util.String,java.util.String)",this);return String.format(URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_POL_COUNTRY_IMAGE_URL_FORMAT),
                    countryCode.toLowerCase());}}
        else
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.countryFlagUrl(android.content.Context,java.util.String,java.util.String)",this);return String.format(URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_COUNTRY_IMAGE_URL_FORMAT),
                    countryCode.toLowerCase());}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.countryFlagUrl(android.content.Context,java.util.String,java.util.String)",this,throwable);throw throwable;}
    }

    public String dateText(Context context) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.dateText(android.content.Context)",this,context);try{long timeMs = time > 0 ? 1000 * time : tim;
        if (timeMs <= 0)
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.dateText(android.content.Context)",this);return "";}}
        Date postDate = new Date();
        postDate.setTime(timeMs);
        Calendar postCal = Calendar.getInstance();
        postCal.setTime(postDate);
        Calendar nowCal = Calendar.getInstance();

        if (postCal.get(Calendar.YEAR) != nowCal.get(Calendar.YEAR)) {
            {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.dateText(android.content.Context)",this);return "" + postCal.get(Calendar.YEAR);}
        }
        else if (postCal.get(Calendar.DAY_OF_YEAR) != nowCal.get(Calendar.DAY_OF_YEAR)) {
            {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.dateText(android.content.Context)",this);return postCal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                    + " "
                    + postCal.get(Calendar.DAY_OF_MONTH);}
        }
        else {
            {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.dateText(android.content.Context)",this);return timeString(context, postCal);}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.dateText(android.content.Context)",this,throwable);throw throwable;}
    }

    protected static final String[] TWELVE_HOUR_CLOCK_COUNTRY_CODES = {
            "AL",
            "SG",
            "TW",
            "AU",
            "BZ",
            "CA",
            "JM",
            "NZ",
            "PH",
            "TT",
            "ZA",
            "US",
            "ZW",
            "GR",
            "MY",
            "KP",
            "KR",
            "MX"
    };
    protected static final Set<String> TWELVE_HOUR_CLOCK_COUNTRY_CODES_SET
            = new HashSet<String>(Arrays.asList(TWELVE_HOUR_CLOCK_COUNTRY_CODES));

    protected String timeString(Context context, Calendar postCal) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.timeString(android.content.Context,java.util.Calendar)",this,context,postCal);try{if (TWELVE_HOUR_CLOCK_COUNTRY_CODES_SET.contains(context.getResources().getConfiguration().locale.getCountry()))
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.timeString(android.content.Context,java.util.Calendar)",this);return twelveHourString(postCal);}}
        else
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.timeString(android.content.Context,java.util.Calendar)",this);return twentyFourHourString(postCal);}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.timeString(android.content.Context,java.util.Calendar)",this,throwable);throw throwable;}
    }

    protected String twelveHourString(Calendar postCal) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.twelveHourString(java.util.Calendar)",this,postCal);try{int hour = postCal.get(Calendar.HOUR_OF_DAY);
        int min = postCal.get(Calendar.MINUTE);
        {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.twelveHourString(java.util.Calendar)",this);return (hour == 0 ? 12 : hour)
                + ":"
                + (min < 10 ? "0" : "")
                + min
                + " "
                + postCal.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault());}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.twelveHourString(java.util.Calendar)",this,throwable);throw throwable;}
    }

    protected String twentyFourHourString(Calendar postCal) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.twentyFourHourString(java.util.Calendar)",this,postCal);try{int hour = postCal.get(Calendar.HOUR_OF_DAY);
        int min = postCal.get(Calendar.MINUTE);
        {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.twentyFourHourString(java.util.Calendar)",this);return hour
                + ":"
                + (min < 10 ? "0" : "")
                + min;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.twentyFourHourString(java.util.Calendar)",this,throwable);throw throwable;}
    }

    public String imageDimensions() {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.imageDimensions()",this);try{if (fsize > 0) {
            int kbSize = (fsize / 1024) + 1;
            String size = (kbSize > 1000) ? (kbSize / 1000) + "MB" : kbSize + "KB";
            String fileinfo;
            if (filename != null && !filename.isEmpty()) {
                fileinfo = " ~ " + filename;
                if (ext != null && !ext.isEmpty())
                    {fileinfo += ext;}
            }
            else {
                fileinfo = "";
            }
            {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.imageDimensions()",this);return w + "x" + h + " ~ " + size + fileinfo;}
        }
        {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.imageDimensions()",this);return "";}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.imageDimensions()",this,throwable);throw throwable;}
    }

    public String headline(Context context, String query, boolean boardLevel, byte[] repliesBlob,
                           boolean showNumReplies, boolean abbrev) {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.headline(android.content.Context,java.util.String,boolean,[byte,boolean,boolean)",this,context,query,boardLevel,repliesBlob,showNumReplies,abbrev);try{List<String> items = new ArrayList<String>();
        if (!boardLevel) {
            if (email != null && !email.isEmpty() && email.equals("sage"))
                {items.add("sage");}
            if (id != null && !id.isEmpty() && id.equals(SAGE_POST_ID))
                {items.add("sage");}
            if (id != null && !id.isEmpty())
                {items.add("Id: " + formattedUserId());}
            if (name != null && !name.isEmpty() && !name.equals("Anonymous"))
                {items.add(name);}
            if (trip != null && !trip.isEmpty())
                {items.add(formattedUserTrip());}
            if (email != null && !email.isEmpty() && !email.equals("sage"))
                {items.add(email);}
            if (country_name != null && !country_name.isEmpty())
                {items.add(country_name);}
            if (fsize > 0)
                {items.add(imageDimensions());}
            /*
            if (resto == 0) {
                String threadUrl = ChanThread.threadUrl(board, no);
                String threadRef = "<a href=\"" + threadUrl + "\">" + threadUrl + "</a>";
                items.add(threadRef);
            }
            */
        }
        /*//if (boardLevel && resto <= 0) {*/
            String s = threadInfoLine(context, boardLevel, showNumReplies, abbrev);
            if (!s.isEmpty())
                {items.add(s);}
        /*//}*/
        /*
        if (repliesBlob != null && repliesBlob.length > 0) { // don't show text for threads
            HashSet<Long> hashSet = (HashSet<Long>)parseBlob(repliesBlob);
            int n = hashSet != null ? hashSet.size() : 0;
            if (n > 0) {
                String s = hashSet.size() + (n == 1 ? " Reply" : " Replies");
                items.add(s);
            }
        }
        */
        String delim = boardLevel ? HEADLINE_BOARDLEVEL_DELIMITER : HEADLINE_THREADLEVEL_DELIMITER;
        String component = join(items, delim);
        {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.headline(android.content.Context,java.util.String,boolean,[byte,boolean,boolean)",this);return highlightComponent(component, query);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.headline(android.content.Context,java.util.String,boolean,[byte,boolean,boolean)",this,throwable);throw throwable;}
    }

    public String threadInfoLine(Context context, boolean boardLevel, boolean showNumReplies, boolean abbrev)
    {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.threadInfoLine(android.content.Context,boolean,boolean,boolean)",this,context,boardLevel,showNumReplies,abbrev);try{if (sticky > 0 && replies == 0)
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.threadInfoLine(android.content.Context,boolean,boolean,boolean)",this);return context.getString(R.string.thread_is_sticky) + (closed > 0 ? " " + context.getString(R.string.thread_is_closed) : "");}}
        String text = "";
        if (!boardLevel && resto == 0) {
            if (imagelimit == 1)
                {text += " " + context.getString(R.string.thread_has_imagelimit);}
            if (bumplimit == 1)
                {text += " " + context.getString(R.string.thread_has_bumplimit);}
            if (sticky > 0)
                {text += " " + context.getString(R.string.thread_is_sticky);}
            if (closed > 0)
                {text += " " + context.getString(R.string.thread_is_closed);}
        }
        {com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.threadInfoLine(android.content.Context,boolean,boolean,boolean)",this);return text.trim();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.threadInfoLine(android.content.Context,boolean,boolean,boolean)",this,throwable);throw throwable;}
    }

    public void mergeIntoThreadList(List<ChanPost> threads) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.ChanPost.mergeIntoThreadList(java.util.List)",this,threads);try{boolean exists = false;
        for (ChanPost existingThread : threads) {
            if (this.no == existingThread.no) {
                exists = true;
                existingThread.copyUpdatedInfoFields(this);
                break;
            }
        }
        if (!exists) {
            threads.add(this);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanPost.mergeIntoThreadList(java.util.List)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.ChanPost.mergeIntoThreadList(java.util.List)",this,throwable);throw throwable;}
    }

    public void copyUpdatedInfoFields(ChanThread from) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.ChanPost.copyUpdatedInfoFields(java.util.ChanThread)",this,from);try{if (from != null && from.posts != null && from.posts.length > 0 && from.posts[0] != null)
            {copyUpdatedInfoFields(from.posts[0]);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanPost.copyUpdatedInfoFields(java.util.ChanThread)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.ChanPost.copyUpdatedInfoFields(java.util.ChanThread)",this,throwable);throw throwable;}
    }

    public void copyUpdatedInfoFields(ChanPost from) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.ChanPost.copyUpdatedInfoFields(com.chanapps.four.data.ChanPost)",this,from);try{isDead = from.isDead;
        bumplimit = from.bumplimit;
        imagelimit = from.imagelimit;
        images = from.images;
        omitted_images = from.omitted_images;
        omitted_posts = from.omitted_posts;
        replies = from.replies;
        /*
        tn_w = from.tn_w;
        tn_h = from.tn_h;
        sub = from.sub;
        com = from.com;
        sticky = from.sticky;
        */
        closed = from.closed;
        /*
        spoiler = from.spoiler;
        now = from.now;
        trip = from.trip;
        id = from.id;
        capcode = from.capcode;
        country = from.country;
        country_name = from.country_name;
        email = from.email;
        created = from.created;
        time = from.time;
        tim = from.tim;
        filename = from.filename;
        ext = from.ext;
        w = from.w;
        h = from.h;
        fsize = from.fsize;
        */
        filedeleted = from.filedeleted;com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanPost.copyUpdatedInfoFields(com.chanapps.four.data.ChanPost)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.ChanPost.copyUpdatedInfoFields(com.chanapps.four.data.ChanPost)",this,throwable);throw throwable;}
    }

    public boolean refersTo(long postNo) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanPost.refersTo(long)",this,postNo);try{if (postNo <= 0 || com == null || com.isEmpty())
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.refersTo(long)",this);return false;}}
        boolean matches = com.indexOf("#p" + postNo + "\"") >= 0;
        if (DEBUG) {Log.i(TAG, "Matching postNo=" + postNo + " is " + matches + " against com=" + com);}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.refersTo(long)",this);return matches;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanPost.refersTo(long)",this,throwable);throw throwable;}
    }

    protected static final Pattern BACKLINK_PATTERN = Pattern.compile("#p(\\d+)\"");

    protected HashSet<Long> backlinks() {
        com.mijack.Xlog.logMethodEnter("java.util.HashSet com.chanapps.four.data.ChanPost.backlinks()",this);try{HashSet<Long> backlinks = null;
        if (com != null && !com.isEmpty()) {
            Matcher m = BACKLINK_PATTERN.matcher(com);
            while (m.find()) {
                if (backlinks == null)
                    {backlinks = new HashSet<Long>();}
                backlinks.add(Long.valueOf(m.group(1)));
            }
        }
        {com.mijack.Xlog.logMethodExit("java.util.HashSet com.chanapps.four.data.ChanPost.backlinks()",this);return backlinks;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.HashSet com.chanapps.four.data.ChanPost.backlinks()",this,throwable);throw throwable;}
    }

    public static byte[] blobify(HashSet<?> hashSet) {
        com.mijack.Xlog.logStaticMethodEnter("[byte com.chanapps.four.data.ChanPost.blobify(java.util.HashSet)",hashSet);try{if (hashSet == null || hashSet.isEmpty())
            {{com.mijack.Xlog.logStaticMethodExit("[byte com.chanapps.four.data.ChanPost.blobify(java.util.HashSet)");return null;}}
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(hashSet);
            {com.mijack.Xlog.logStaticMethodExit("[byte com.chanapps.four.data.ChanPost.blobify(java.util.HashSet)");return baos.toByteArray();}
        }
        catch (IOException e) {
            Log.e(TAG, "Couldn't serialize set=" + hashSet, e);
        }
        {com.mijack.Xlog.logStaticMethodExit("[byte com.chanapps.four.data.ChanPost.blobify(java.util.HashSet)");return null;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[byte com.chanapps.four.data.ChanPost.blobify(java.util.HashSet)",throwable);throw throwable;}
    }

    public static HashSet<?> parseBlob(final byte[] b) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.HashSet com.chanapps.four.data.ChanPost.parseBlob([byte)",b);try{if (b == null || b.length == 0)
            {{com.mijack.Xlog.logStaticMethodExit("java.util.HashSet com.chanapps.four.data.ChanPost.parseBlob([byte)");return null;}}
        try {
            InputStream bais =new BufferedInputStream(new ByteArrayInputStream(b));
            ObjectInputStream ois = new ObjectInputStream(bais);
            HashSet<?> hashSet = (HashSet<?>)ois.readObject();
            {com.mijack.Xlog.logStaticMethodExit("java.util.HashSet com.chanapps.four.data.ChanPost.parseBlob([byte)");return hashSet;}
        }
        catch (Exception e) {
            Log.e(TAG, "Couldn't deserialize blob=" + b);
        }
        {com.mijack.Xlog.logStaticMethodExit("java.util.HashSet com.chanapps.four.data.ChanPost.parseBlob([byte)");return null;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.HashSet com.chanapps.four.data.ChanPost.parseBlob([byte)",throwable);throw throwable;}
    }

    public static final String SAGE_POST_ID = "Heaven";
    private static final String[] NAMES = {
            "Aries",
            "Bian",
            "Chikage",
            "Dragon",
            "Eki",
            "Fidel",
            "Goku",
            "Hotaru",
            "Ideki",
            "Judo",
            "Kendo",
            "Lima",
            "Moto",
            "Noko",
            "Oni",
            "Piku",
            "Queen",
            "Radium",
            "Sensei",
            "Totoro",
            "Usagi",
            "Virgo",
            "Waka",
            "Xi",
            "Yoto",
            "Zulu",

            "Akira",
            "Balrog",
            "Chidori",
            "Diva",
            "Endo",
            "Fap",
            "Godo",
            "Hero",
            "Ichigo",
            "Joro",
            "Kai",
            "Li",
            "Mini",
            "Naruto",
            "Opa",
            "Pei",
            "Quest",
            "Rune",
            "Shura",
            "Tetsuo",
            "Unit",
            "Victor",
            "Wiki",
            "Xenu",
            "Yolo",
            "Zolan",

            "One",
            "Two",
            "Three",
            "Four",
            "Five",
            "Six",
            "Seven",
            "Eight",
            "Nine",
            "Ten",

            "Plus",
            "Slash"

    };
    private static final String[] NAMES_2 = {
            "Arctic",
            "Brain",
            "Chimp",
            "Duck",
            "Elf",
            "Frog",
            "Gimp",
            "Hippy",
            "Imp",
            "Jumper",
            "Kitchen",
            "Lamp",
            "Mittens",
            "Night",
            "Owl",
            "Phantom",
            "Quack",
            "Rocket",
            "Storm",
            "Thunder",
            "Urchin",
            "Vampire",
            "Whale",
            "Xerxes",
            "Yuppie",
            "Zebra",

            "Ape",
            "Banana",
            "Crown",
            "Dread",
            "Eel",
            "Factor",
            "General",
            "Hound",
            "Ink",
            "Jack",
            "Killer",
            "Loader",
            "Master",
            "Nasty",
            "Onion",
            "Paste",
            "Quitter",
            "Rim",
            "Stampede",
            "Tent",
            "Unicorn",
            "Vox",
            "War",
            "Xtender",
            "Yogi",
            "Zoo",

            "Ten",
            "Twenty",
            "Thirty",
            "Fourty",
            "Fifty",
            "Sixty",
            "Seventy",
            "Eighty",
            "Ninety",
            "Hundred",

            "Minus",
            "Dot"

    };
    private static final String BASE_64_CODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz" + "0123456789" + "+/";
    private static final Map<Character, String> nameMap = new HashMap<Character, String>();
    private static final Map<Character, String> nameMap2 = new HashMap<Character, String>();

    private static void initNameMap() {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanPost.initNameMap()");try{for (int i = 0; i < NAMES.length; i++) {
            String s = NAMES[i];
            char c = BASE_64_CODE.charAt(i);
            if (DEBUG) {Log.i(TAG, "Putting into map " + c + ", " + s);}
            nameMap.put(c, s);
        }
        for (int i = 0; i < NAMES_2.length; i++) {
            String s = NAMES_2[i];
            char c = BASE_64_CODE.charAt(i);
            if (DEBUG) {Log.i(TAG, "Putting into map2 " + c + ", " + s);}
            nameMap2.put(c, s);
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanPost.initNameMap()");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanPost.initNameMap()",throwable);throw throwable;}
    }

    public String formattedUserId() {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.formattedUserId()",this);try{if (id == null)
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserId()",this);return "";}}
        else
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserId()",this);return formattedUserId(id, useFriendlyIds);}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.formattedUserId()",this,throwable);throw throwable;}
    }

    public String formattedUserTrip() {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.formattedUserTrip()",this);try{if (trip == null)
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserTrip()",this);return "";}}
        else
            {{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserTrip()",this);return formattedUserTrip(trip, useFriendlyIds);}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.formattedUserTrip()",this,throwable);throw throwable;}
    }

    public static String formattedUserTrip(String trip, boolean useFriendlyIds) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanPost.formattedUserTrip(java.util.String,boolean)",trip,useFriendlyIds);try{if (trip == null)
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserTrip(java.util.String,boolean)");return "";}}
        if (!useFriendlyIds)
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserTrip(java.util.String,boolean)");return trip;}}
        if (trip.charAt(0) == '!' && trip.charAt(1) == '!')
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserTrip(java.util.String,boolean)");return "!!" + formattedUserId(trip.substring(2), useFriendlyIds);}}
        if (trip.charAt(0) == '!')
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserTrip(java.util.String,boolean)");return "!" + formattedUserId(trip.substring(1), useFriendlyIds);}}
        {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserTrip(java.util.String,boolean)");return trip;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.formattedUserTrip(java.util.String,boolean)",throwable);throw throwable;}
    }

    public static String formattedUserId(String id, boolean useFriendlyIds) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanPost.formattedUserId(java.util.String,boolean)",id,useFriendlyIds);try{if (!useFriendlyIds)
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserId(java.util.String,boolean)");return id;}}
        if (id.equalsIgnoreCase(SAGE_POST_ID))
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserId(java.util.String,boolean)");return id;}}
        if (id.equalsIgnoreCase("Admin") || id.equalsIgnoreCase("Mod") || id.equalsIgnoreCase("Developer"))
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserId(java.util.String,boolean)");return id;}}
        if (DEBUG) {Log.d(TAG, "Initial: " + id);}

        synchronized (nameMap) {
            if (nameMap.isEmpty()) {
                initNameMap();
            }
        }

        String newId = nameMap.get(id.charAt(0)) + nameMap2.get(id.charAt(1)) + "." + id.substring(2);
        if (DEBUG) {Log.i(TAG, "Final: " + newId);}
        {com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.formattedUserId(java.util.String,boolean)");return newId;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.formattedUserId(java.util.String,boolean)",throwable);throw throwable;}
    }

    public void clearImageInfo() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.ChanPost.clearImageInfo()",this);try{w = 0;
        h = 0;
        tn_w = 0;
        tn_h = 0;
        tim = 0;
        fsize = -1;
        filename = null;
        ext = null;com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanPost.clearImageInfo()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.ChanPost.clearImageInfo()",this,throwable);throw throwable;}
    }

    public boolean matchesQuery(String query) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this,query);try{if (query == null || query.isEmpty())
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this);return true;}}
        /*// should use StringUtils.containsIgnoreCase*/
        if (no != 0 && Long.toString(no).contains(query))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this);return true;}}
        if (id != null && id.toLowerCase().contains(query))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this);return true;}}
        if (name != null && name.toLowerCase().contains(query))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this);return true;}}
        if (trip != null && trip.toLowerCase().contains(query))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this);return true;}}
        if (email != null && email.toLowerCase().contains(query))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this);return true;}}
        if (country_name != null && country_name.toLowerCase().contains(query))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this);return true;}}
        if (sub != null && sub.toLowerCase().contains(query))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this);return true;}}
        if (com != null && com.toLowerCase().contains(query))
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this);return true;}}
        if (DEBUG) {Log.i(TAG, "skipping post not matching query: " + no + " " + sub + " " + com);}
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanPost.matchesQuery(java.util.String)",this,throwable);throw throwable;}
    }

    public static MatrixCursor buildMatrixCursor(int capacity) {
        com.mijack.Xlog.logStaticMethodEnter("android.database.MatrixCursor com.chanapps.four.data.ChanPost.buildMatrixCursor(int)",capacity);try{try {
            {com.mijack.Xlog.logStaticMethodExit("android.database.MatrixCursor com.chanapps.four.data.ChanPost.buildMatrixCursor(int)");return new MatrixCursor(POST_COLUMNS, capacity);}
        }
        catch (OutOfMemoryError e) {
            Log.e(TAG, "Couldn't allocate cursor size=" + capacity, e);
        }
        {com.mijack.Xlog.logStaticMethodExit("android.database.MatrixCursor com.chanapps.four.data.ChanPost.buildMatrixCursor(int)");return new MatrixCursor(POST_COLUMNS);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.database.MatrixCursor com.chanapps.four.data.ChanPost.buildMatrixCursor(int)",throwable);throw throwable;}
    }

    public Object[] makeRow(Context context, String query, int i, byte[] backlinksBlob, byte[] repliesBlob, byte[] sameIdsBlob) {
        com.mijack.Xlog.logMethodEnter("[java.util.Object com.chanapps.four.data.ChanPost.makeRow(android.content.Context,java.util.String,int,[byte,[byte,[byte)",this,context,query,i,backlinksBlob,repliesBlob,sameIdsBlob);try{String[] textComponents = textComponents(query);
        String exifText = exifText();
        String headline = headline(context, query, false, repliesBlob, false, false);
        int flags = postFlags(textComponents[0], textComponents[1], exifText, headline);
        if (resto == 0)
            {flags |= FLAG_IS_HEADER;}
        {com.mijack.Xlog.logMethodExit("[java.util.Object com.chanapps.four.data.ChanPost.makeRow(android.content.Context,java.util.String,int,[byte,[byte,[byte)",this);return new Object[] {
                no,
                board,
                resto,
                thumbnailUrl(context),
                imageUrl(context),
                countryFlagUrl(context),
                headline,
                replies,
                images,
                textComponents[0],
                textComponents[1],
                dateText(context),
                tn_w,
                tn_h,
                w,
                h,
                tim,
                null,
                null,
                exifText(),
                id,
                trip,
                name,
                email,
                thumbnailId(),
                ext,
                backlinksBlob,
                repliesBlob,
                sameIdsBlob,
                fsize,
                flags
        };}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.util.Object com.chanapps.four.data.ChanPost.makeRow(android.content.Context,java.util.String,int,[byte,[byte,[byte)",this,throwable);throw throwable;}
    }

    /*
    public static Object[] makeTitleRow(String boardCode, String title) {
        return makeTitleRow(boardCode, title, "");
    }

    public static Object[] makeTitleRow(String boardCode, String title, String desc) {
        String subject = title;
        return new Object[] {
                title.hashCode(),
                boardCode,
                0,
                "",
                "",
                "",
                "",
                0,
                0,
                subject,
                desc,
                "",
                0,
                0,
                -1,
                -1,
                0,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                null,
                null,
                null,
                0,
                FLAG_HAS_SUBJECT | FLAG_IS_TITLE
        };
    }

    public static final int MIN_TOKEN_LENGTH = 5;

    public Set<String> keywords() {
        String text = (sub == null ? "" : sub) + (com == null ? "" : com) + headline("", false, null, false);
        String stripped = text.replaceAll("<[^>]*>|\\W+", " ");
        String[] tokens = stripped.split("\\s+");
        if (DEBUG) Log.v(TAG, "threadNo=" + no + " tokens=" + Arrays.toString(tokens));
        Set<String> tokenSet = new HashSet<String>();
        for (String token : tokens) {
            if (token.length() > MIN_TOKEN_LENGTH || token.matches("[A-Z]+")) // all uppercase abbreviations
                tokenSet.add(token.toLowerCase());
        }
        if (DEBUG) Log.v(TAG, "threadNo=" + no + " keywords=" + tokenSet);
        return tokenSet;
    }

    public int keywordRelevance(Set<String> keywords) {
        Set<String> tokenSet = keywords();
        tokenSet.retainAll(keywords);
        int relevancy = tokenSet.size();
        if (DEBUG && relevancy > 0) Log.v(TAG, "relevancy=" + relevancy + " matching keywords=" + tokenSet);
        return relevancy;
    }
    */
    public static Object[] extractPostRow(Cursor cursor) {
        com.mijack.Xlog.logStaticMethodEnter("[java.util.Object com.chanapps.four.data.ChanPost.extractPostRow(android.database.Cursor)",cursor);try{int flagIdx = cursor.getColumnIndex(POST_FLAGS);
        int postNoIdx = cursor.getColumnIndex(POST_ID);
        int restoIdx = cursor.getColumnIndex(POST_RESTO);
        int timIdx = cursor.getColumnIndex(POST_TIM);
        int c = cursor.getColumnCount();
        Object[] o = new Object[c];
        try {
            for (int i = 0; i < c; i++) {
                if (i == flagIdx) {
                    int flags = cursor.getInt(flagIdx);
                    flags |= FLAG_NO_EXPAND;
                    o[i] = flags;
                    continue;
                }
                if (i == postNoIdx || i == restoIdx || i == timIdx) {
                    o[i] = cursor.getLong(i);
                    continue;
                }
                int type = cursor.getType(i);
                switch (type) {
                    case Cursor.FIELD_TYPE_BLOB: o[i] = cursor.getBlob(i); break;
                    case Cursor.FIELD_TYPE_FLOAT: o[i] = cursor.getFloat(i); break;
                    case Cursor.FIELD_TYPE_INTEGER: o[i] = cursor.getInt(i); break;
                    case Cursor.FIELD_TYPE_STRING: o[i] = cursor.getString(i); break;
                    case Cursor.FIELD_TYPE_NULL:
                    default: o[i] = null; break;
                }
            }
        }
        catch (CursorIndexOutOfBoundsException e) {
            Log.e(TAG, "Cursor index out of bounds, returning null");
            {com.mijack.Xlog.logStaticMethodExit("[java.util.Object com.chanapps.four.data.ChanPost.extractPostRow(android.database.Cursor)");return null;}
        }
        {com.mijack.Xlog.logStaticMethodExit("[java.util.Object com.chanapps.four.data.ChanPost.extractPostRow(android.database.Cursor)");return o;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[java.util.Object com.chanapps.four.data.ChanPost.extractPostRow(android.database.Cursor)",throwable);throw throwable;}
    }
    
    public void updateThreadData(ChanThread t) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.ChanPost.updateThreadData(java.util.ChanThread)",this,t);try{isDead = t.isDead;
    	closed = t.closed;
    	omitted_images = t.omitted_images;
    	omitted_posts = t.omitted_posts;
    	
    	if (t.posts.length > 0 && t.posts[0] != null) {
            if (t.posts[0].isDead)
                {isDead = t.isDead;}
        	replies = t.posts[0].replies;
        	images = t.posts[0].images;
	    	bumplimit = t.posts[0].bumplimit;
	    	capcode = t.posts[0].capcode;
	    	com = t.posts[0].com;
	    	country = t.posts[0].country;
	    	country_name = t.posts[0].country_name;
	    	email = t.posts[0].email;
	    	ext = t.posts[0].ext;
	    	filedeleted = t.posts[0].filedeleted;
	    	filename = t.posts[0].filename;
	    	fsize = t.posts[0].fsize;
	    	h = t.posts[0].h;
	    	hideAllText = t.posts[0].hideAllText;
	    	hidePostNumbers = t.posts[0].hidePostNumbers;
	    	id = t.posts[0].id;
	    	now = t.posts[0].now;
	    	spoiler = t.posts[0].spoiler;
	    	sticky = t.posts[0].sticky;
	    	sub = t.posts[0].sub;
	    	tim = t.posts[0].tim;
	    	tn_h = t.posts[0].tn_h;
	    	tn_w = t.posts[0].tn_w;
	    	trip = t.posts[0].trip;
	    	useFriendlyIds = t.posts[0].useFriendlyIds;
	    	w = t.posts[0].w;
    	}com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanPost.updateThreadData(java.util.ChanThread)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.ChanPost.updateThreadData(java.util.ChanThread)",this,throwable);throw throwable;}    	
    }

    public void updateThreadDataWithPost(ChanPost t) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.ChanPost.updateThreadDataWithPost(com.chanapps.four.data.ChanPost)",this,t);try{isDead = t.isDead;
    	closed = t.closed;
    	omitted_images = t.omitted_images;
    	omitted_posts = t.omitted_posts;
    	
    	replies = t.replies;
    	images = t.images;
    	bumplimit = t.bumplimit;
    	capcode = t.capcode;
    	com = t.com;
    	country = t.country;
    	country_name = t.country_name;
    	email = t.email;
    	ext = t.ext;
    	filedeleted = t.filedeleted;
    	filename = t.filename;
    	fsize = t.fsize;
    	h = t.h;
    	hideAllText = t.hideAllText;
    	hidePostNumbers = t.hidePostNumbers;
    	id = t.id;
    	now = t.now;
    	spoiler = t.spoiler;
    	sticky = t.sticky;
    	sub = t.sub;
    	tim = t.tim;
    	tn_h = t.tn_h;
    	tn_w = t.tn_w;
    	trip = t.trip;
    	useFriendlyIds = t.useFriendlyIds;
    	w = t.w;com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanPost.updateThreadDataWithPost(com.chanapps.four.data.ChanPost)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.ChanPost.updateThreadDataWithPost(com.chanapps.four.data.ChanPost)",this,throwable);throw throwable;}
    }

    public static String postUrl(Context context, String boardCode, long threadNo, long postNo) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanPost.postUrl(android.content.Context,java.util.String,long,long)",context,boardCode,threadNo,postNo);try{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.postUrl(android.content.Context,java.util.String,long,long)");return String.format(URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_WEB_POST_URL_FORMAT), boardCode, threadNo, postNo);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.postUrl(android.content.Context,java.util.String,long,long)",throwable);throw throwable;}
    }

    public String uniqueId() {
        com.mijack.Xlog.logMethodEnter("java.util.String com.chanapps.four.data.ChanPost.uniqueId()",this);try{com.mijack.Xlog.logMethodExit("java.util.String com.chanapps.four.data.ChanPost.uniqueId()",this);return uniqueId(board, no, resto);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.uniqueId()",this,throwable);throw throwable;}
    }

    public static String uniqueId(String board, long no, long resto) {
        com.mijack.Xlog.logStaticMethodEnter("java.util.String com.chanapps.four.data.ChanPost.uniqueId(java.util.String,long,long)",board,no,resto);try{if (no <= 0)
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.uniqueId(java.util.String,long,long)");return "/" + board + "/";}}
        else if (resto <= 0)
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.uniqueId(java.util.String,long,long)");return "/" + board + "/" + no;}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("java.util.String com.chanapps.four.data.ChanPost.uniqueId(java.util.String,long,long)");return "/" + board + "/" + resto + "#p" + no;}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.util.String com.chanapps.four.data.ChanPost.uniqueId(java.util.String,long,long)",throwable);throw throwable;}
    }

}
