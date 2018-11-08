package com.haiyunshan.whatsnote.article;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import club.andnext.recyclerview.bridge.BridgeHolder;
import com.haiyunshan.article.DocumentEntity;

public abstract class ComposeViewHolder<E extends DocumentEntity> extends BridgeHolder<E> {

    protected E entity;

    ComposeArticleFragment parent;

    public ComposeViewHolder(ComposeArticleFragment f, View itemView) {
        super(itemView);

        this.parent = f;
    }

    @Override
    public abstract int getLayoutResourceId();

    @Override
    public abstract void onViewCreated(@NonNull View view);

    @Override
    @CallSuper
    public void onBind(E item, int position) {
        this.entity = item;
    }

    @Override
    @CallSuper
    public void onViewAttachedToWindow() {

    }

    @Override
    @CallSuper
    public void onViewDetachedFromWindow() {
        save();
    }

    void remove() {
        parent.remove(this);
    }

    abstract void save();

    E getEntity() {
        return entity;
    }
}
