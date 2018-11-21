package club.andnext.recyclerview.bridge;

import club.andnext.recyclerview.adapter.BuilderFilter;
import club.andnext.recyclerview.adapter.ViewHolderBuilder;

import java.util.List;

public abstract class BridgeFilter<T> extends BuilderFilter<T> {

    @Override
    public ViewHolderBuilder accept(T obj, List<ViewHolderBuilder> list) {
        Class<? extends BridgeViewHolder> tag = this.getHolder(obj);
        if (tag == null) {
            return null;
        }

        for (ViewHolderBuilder d : list) {
            BridgeBuilder delegate = (BridgeBuilder)d;
            Class<? extends BridgeViewHolder> holder = delegate.holderClazz;

            if (holder == tag) {
                return delegate;
            }
        }

        return null;
    }

    @Override
    public Class<? extends ViewHolderBuilder> getBuilder(T obj) {
        return null;
    }

    public abstract Class<? extends BridgeViewHolder> getHolder(T obj);
}
