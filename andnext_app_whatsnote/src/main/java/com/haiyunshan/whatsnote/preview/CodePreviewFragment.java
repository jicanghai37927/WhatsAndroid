package com.haiyunshan.whatsnote.preview;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import club.andnext.overscroll.HorizontalOverScrollHelper;
import club.andnext.overscroll.OverScrollHelper;
import club.andnext.utils.IntBuffer;
import com.haiyunshan.preview.PreviewEntity;
import com.haiyunshan.whatsnote.R;
import prettify.PrettifyParser;
import prettify.parser.Prettify;
import prettify.theme.ThemeSonsOfObsidian;
import syntaxhighlight.ParseResult;
import syntaxhighlight.Parser;
import syntaxhighlight.Style;
import syntaxhighlight.Theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CodePreviewFragment extends BasePreviewFragment {

    NestedScrollView verticalScrollView;
    HorizontalScrollView horizontalScrollView;

    TextView lineView;
    View gutterView;
    TextView codeView;

    CodePage codePage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_code_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        {
            this.verticalScrollView = view.findViewById(R.id.vertical_scroll);
            OverScrollHelper.attach(verticalScrollView);

            this.horizontalScrollView = view.findViewById(R.id.horizontal_scroll);
            HorizontalOverScrollHelper.attach(horizontalScrollView);
        }

        {
            this.lineView = view.findViewById(R.id.tv_line);
            this.gutterView = view.findViewById(R.id.view_gutter);
            this.codeView = view.findViewById(R.id.tv_code);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PreviewEntity entity = this.getEntity();
        this.codePage = new CodePage(this, entity);

        {
            lineView.setText(codePage.buildLines());
            codeView.setText(codePage.buildCode());
        }


        {
            Theme theme = codePage.theme;

            {
                this.getView().setBackgroundColor(theme.getBackground().getRGB());
            }

            {
                lineView.setTextColor(theme.getPlain().getColor().getRGB());
            }

            {
                gutterView.setBackgroundColor(theme.getGutterBorderColor().getRGB());
            }

            {
                codeView.setHorizontallyScrolling(true);
                codeView.setTextColor(theme.getPlain().getColor().getRGB());
                codeView.setHighlightColor(theme.getHighlightedBackground().getRGB());
            }
        }
    }

    private static final class CodePage {

        Theme theme;

        String text;
        SpannableString code;
        IntBuffer lines;

        PreviewEntity entity;
        CodePreviewFragment parent;

        public CodePage(CodePreviewFragment parent, PreviewEntity entity) {
            this.parent = parent;
            this.entity = entity;

            this.theme = new ThemeSonsOfObsidian();
        }

        public Activity getContext() {
            return parent.getActivity();
        }

        String buildText() {
            if (text != null) {
                return text;
            }

            String str = entity.getText(getContext());
            this.text = str;

            return text;
        }

        CharSequence buildLines() {
            if (lines == null) {
                this.lines = new IntBuffer();

                String str = this.buildText();

                char c = '\n';
                int pos = 0;
                while (true) {
                    pos = str.indexOf(c, pos);
                    if (pos < 0) {
                        break;
                    }

                    lines.write(pos);

                    pos += 1;
                }
            }

            {
                int count = lines.size();
                StringBuilder sb = new StringBuilder(count * 4);

                for (int i = 1; i <= count; i++) {
                    sb.append(i);
                    sb.append('\n');
                }

                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }

                return sb.toString();
            }
        }

        Spanned buildCode() {
            if (code != null) {
                return code;
            }

            String text = this.buildText();
            List<ParseResult> list;

            {


                Parser parser = new PrettifyParser();
                list = parser.parse(entity.getExtension(), text);
                list = (list == null) ? new ArrayList<ParseResult>() : list;

                // 剔除puctuation、plain，没必要Span
                {
                    int size = list.size();
                    for (int i = (size - 1); i >= 0; i--) {
                        ParseResult r = list.get(i);
                        String key = r.getStyleKeys().get(0);
                        if (key.equalsIgnoreCase(Prettify.PR_PUNCTUATION)) {
                            list.remove(i);
                            continue;
                        }

                        if (key.equalsIgnoreCase(Prettify.PR_PLAIN)) {
                            list.remove(i);
                            continue;
                        }
                    }
                }

                // 排个序
                {
                    Collections.sort(list, new Comparator<ParseResult>() {
                        @Override
                        public int compare(ParseResult o1, ParseResult o2) {
                            return (o1.getOffset() - o2.getOffset());
                        }
                    });
                }
            }

            {
                SpannableString spannable = new SpannableString(text);
                this.code = spannable;

                for (ParseResult r : list) {
                    String key = r.getStyleKeys().get(0);
                    Style s = theme.getStyle(key);
                    if (s == null) {
                        continue;
                    }

                    int start = r.getOffset();
                    int end = start + r.getLength();
                    int flags = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;

                    // 文本色
                    {
                        int color = s.getColor().getRGB();
                        ForegroundColorSpan span = new ForegroundColorSpan(color);
                        spannable.setSpan(span, start, end, flags);
                    }

                    // 加粗、斜体
                    {
                        int style = 0;
                        if (s.isBold() && s.isItalic()) {
                            style = Typeface.BOLD_ITALIC;
                        } else if (s.isBold()) {
                            style = Typeface.BOLD;
                        } else if (s.isItalic()) {
                            style = Typeface.ITALIC;
                        }

                        if (style != 0) {
                            StyleSpan span = new StyleSpan(style);
                            spannable.setSpan(span, start, end, flags);
                        }
                    }

                    // 下划线
                    if (s.isUnderline()) {
                        UnderlineSpan span = new UnderlineSpan();
                        spannable.setSpan(span, start, end, flags);
                    }
                }
            }

            return code;
        }
    }
}
