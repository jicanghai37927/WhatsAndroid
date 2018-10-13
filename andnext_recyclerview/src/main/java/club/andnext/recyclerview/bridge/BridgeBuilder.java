package club.andnext.recyclerview.bridge;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import club.andnext.recyclerview.adapter.ViewHolderBuilder;

public class BridgeBuilder<T, VH extends BridgeHolder> extends ViewHolderBuilder {

    Class<? extends BridgeHolder> holderClazz;
    Object[] args;
    int resId;

    ViewHolderBuilder<T, VH> delegate;

    public BridgeBuilder(ViewHolderBuilder delegate) {
        this.delegate = delegate;
    }

    public BridgeBuilder(@NonNull Class<? extends BridgeHolder> clz, @NonNull int resId, Object... args) {
        this.holderClazz = clz;
        this.resId = resId;
        this.args = args;

        this.delegate = null;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull Context context, @NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        if (delegate != null) {
            return delegate.onCreateView(context, inflater, container);
        }

        return inflater.inflate(resId, container, false);
    }

    @NonNull
    @Override
    public BridgeHolder onCreateViewHolder(@NonNull View view) {
        if (delegate != null) {
            VH holder = delegate.onCreateViewHolder(view);
            holder.onViewCreated(view);

            return holder;
        }

        BridgeHolder holder = null;

        {
            Object[] args = new Object[this.args.length + 1];
            System.arraycopy(this.args, 0, args, 0, this.args.length);
            args[this.args.length] = view;

            Class<?>[] classArray = new Class[args.length];
            for (int i = 0, size = this.args.length; i < size; i++) {
                classArray[i] = args[i].getClass();
            }

            classArray[this.args.length] = View.class;

            try {
                Constructor<? extends BridgeHolder> constructor = holderClazz.getConstructor(classArray);

                holder = constructor.newInstance(args);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (holder != null) {
            if (holder.getLayoutResourceId() != this.resId) {
                throw new IllegalArgumentException(holderClazz.getName() + "has different layout resource id. check it!");
            }

            holder.onViewCreated(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Object item, int position) {
        VH viewHolder = (VH)holder;
        viewHolder.onBind(item, position);
    }

}
