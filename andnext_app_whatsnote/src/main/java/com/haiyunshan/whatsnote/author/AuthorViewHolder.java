package com.haiyunshan.whatsnote.author;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.author.entity.AuthorEntity;
import org.joda.time.LocalDate;

public class AuthorViewHolder extends BridgeViewHolder<AuthorEntity> {

    public static final int LAYOUT_RES_ID = R.layout.layout_share_author;

    ImageView portraitView;
    TextView nameView;
    TextView dateView;

    ImageView codeView;
    TextView descView;

    public AuthorViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        this.portraitView = view.findViewById(R.id.iv_portrait);
        this.nameView = view.findViewById(R.id.tv_name);
        this.dateView = view.findViewById(R.id.tv_date);

        this.codeView = view.findViewById(R.id.iv_code);
        this.descView = view.findViewById(R.id.tv_desc);

    }

    @Override
    public void onBind(AuthorEntity item, int position) {
        {
            Glide.with(itemView)
                    .load(item.getPortrait())
                    .apply(RequestOptions.circleCropTransform())
                    .into(portraitView);
        }

        {
            nameView.setText(item.getName());
        }

        {
            LocalDate date = LocalDate.now();
            String text = String.format("%1$d 年 %2$d 月 %3$d 日", date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
            dateView.setText(text);
        }

        {
            Glide.with(itemView)
                    .load(item.getQRCode())
                    .into(codeView);
        }

        {
            descView.setText(item.getDesc());
        }
    }

}
