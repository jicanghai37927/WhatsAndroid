package club.andnext.recyclerview.bridge;

import club.andnext.recyclerview.adapter.BuilderFilter;
import club.andnext.recyclerview.adapter.ViewHolderBuilder;

public abstract class BridgeFilter<T> extends BuilderFilter<T> {

    @Override
    public ViewHolderBuilder accept(T obj, ViewHolderBuilder[] array) {
        Class<? extends BridgeHolder> tag = this.getHolder(obj);
        if (tag == null) {
            return null;
        }

        for (ViewHolderBuilder d : array) {
            BridgeBuilder delegate = (BridgeBuilder)d;
            Class<? extends BridgeHolder> holder = delegate.holderClazz;

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

    public abstract Class<? extends BridgeHolder> getHolder(T obj);
}
