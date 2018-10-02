package com.chanapps.four.component;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 9/24/13
 * Time: 8:18 PM
 * To change this template use File | Settings | File Templates.
 */
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class LetterSpacingTextView extends TextView {

    private float textSpacing = TextSpacing.NORMAL;
    private CharSequence originalText = "";


    public LetterSpacingTextView(Context context) {
        super(context);
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public float getTextSpacing() {
        com.mijack.Xlog.logMethodEnter("float com.chanapps.four.component.LetterSpacingTextView.getTextSpacing()",this);try{com.mijack.Xlog.logMethodExit("float com.chanapps.four.component.LetterSpacingTextView.getTextSpacing()",this);return textSpacing;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.chanapps.four.component.LetterSpacingTextView.getTextSpacing()",this,throwable);throw throwable;}
    }

    public void setTextSpacing(float textSpacing) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.LetterSpacingTextView.setTextSpacing(float)",this,textSpacing);try{this.textSpacing = textSpacing;
        applyLetterSpacing();com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.LetterSpacingTextView.setTextSpacing(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.LetterSpacingTextView.setTextSpacing(float)",this,throwable);throw throwable;}
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.LetterSpacingTextView.setText(java.lang.CharSequence,BufferType)",this,text,type);try{originalText = text;
        applyLetterSpacing();com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.LetterSpacingTextView.setText(java.lang.CharSequence,BufferType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.LetterSpacingTextView.setText(java.lang.CharSequence,BufferType)",this,throwable);throw throwable;}
    }

    @Override
    public CharSequence getText() {
        com.mijack.Xlog.logMethodEnter("java.lang.CharSequence com.chanapps.four.component.LetterSpacingTextView.getText()",this);try{com.mijack.Xlog.logMethodExit("java.lang.CharSequence com.chanapps.four.component.LetterSpacingTextView.getText()",this);return originalText;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.CharSequence com.chanapps.four.component.LetterSpacingTextView.getText()",this,throwable);throw throwable;}
    }

    private void applyLetterSpacing() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.LetterSpacingTextView.applyLetterSpacing()",this);try{StringBuilder builder = new StringBuilder();
        for(int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if(i+1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if(builder.toString().length() > 1) {
            for(int i = 1; i < builder.toString().length(); i+=2) {
                finalText.setSpan(new ScaleXSpan((textSpacing+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.LetterSpacingTextView.applyLetterSpacing()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.LetterSpacingTextView.applyLetterSpacing()",this,throwable);throw throwable;}
    }

    public class TextSpacing {
        public final static float NORMAL = 0;
    }
}
