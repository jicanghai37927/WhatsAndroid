package club.andnext.syntaxprettify;

import prettify.parser.Prettify;
import syntaxhighlight.Style;
import syntaxhighlight.Theme;

import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HighlighterTheme extends Theme {

    Theme mTheme;
    HashMap<String, String> mKeyMap;

    public HighlighterTheme(Theme theme) {
        this.mTheme = theme;

        this.mKeyMap = new HashMap<>();
        mKeyMap.put(Prettify.PR_STRING, "string");
        mKeyMap.put(Prettify.PR_KEYWORD, "keyword");
        mKeyMap.put(Prettify.PR_COMMENT, "comments");
        mKeyMap.put(Prettify.PR_TYPE, "variable");
        mKeyMap.put(Prettify.PR_LITERAL, "value");
        mKeyMap.put(Prettify.PR_PUNCTUATION, "");
        mKeyMap.put(Prettify.PR_PLAIN, "plain");
        mKeyMap.put(Prettify.PR_TAG, "");
        mKeyMap.put(Prettify.PR_DECLARATION, "");
        mKeyMap.put(Prettify.PR_SOURCE, "");
        mKeyMap.put(Prettify.PR_ATTRIB_NAME, "");
        mKeyMap.put(Prettify.PR_ATTRIB_VALUE, "");
        mKeyMap.put(Prettify.PR_NOCODE, "");
    }

    @Override
    public Style getPlain() {
        return mTheme.getPlain();
    }

    @Override
    public SimpleAttributeSet getStylesAttributeSet(String styleKeys) {
        return mTheme.getStylesAttributeSet(styleKeys);
    }

    @Override
    public Style getStyle(String key) {
        String k = mKeyMap.get(key);
        key = (k == null || k.isEmpty())? key: k;

        return mTheme.getStyle(key);
    }

    @Override
    public Map<String, Style> getStyles() {
        return mTheme.getStyles();
    }

    @Override
    public Font getFont() {
        return mTheme.getFont();
    }

    @Override
    public Color getBackground() {
        return mTheme.getBackground();
    }

    @Override
    public Color getHighlightedBackground() {
        return mTheme.getHighlightedBackground();
    }

    @Override
    public Color getGutterText() {
        return mTheme.getGutterText();
    }

    @Override
    public Color getGutterBorderColor() {
        return mTheme.getGutterBorderColor();
    }

    @Override
    public int getGutterBorderWidth() {
        return mTheme.getGutterBorderWidth();
    }

    @Override
    public Font getGutterTextFont() {
        return mTheme.getGutterTextFont();
    }

    @Override
    public int getGutterTextPaddingLeft() {
        return mTheme.getGutterTextPaddingLeft();
    }

    @Override
    public int getGutterTextPaddingRight() {
        return mTheme.getGutterTextPaddingRight();
    }

    @Override
    public String toString() {
        return mTheme.toString();
    }
}
