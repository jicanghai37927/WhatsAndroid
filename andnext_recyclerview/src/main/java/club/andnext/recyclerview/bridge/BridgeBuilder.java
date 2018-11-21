package club.andnext.recyclerview.bridge;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import club.andnext.recyclerview.adapter.ViewHolderBuilder;

public class BridgeBuilder<T, VH extends BridgeViewHolder> extends ViewHolderBuilder {

    Class<? extends BridgeViewHolder> holderClazz;
    Object[] parameters;
    Class<?>[] parameterTypes;

    int resId;

    ViewHolderBuilder<T, VH> delegate;

    public BridgeBuilder(ViewHolderBuilder delegate) {
        this.delegate = delegate;
    }

    public BridgeBuilder(@NonNull Class<? extends BridgeViewHolder> clz, @NonNull int resId, Object... parameters) {
        this.holderClazz = clz;
        this.resId = resId;
        this.parameters = parameters;

        this.delegate = null;
    }

    public BridgeBuilder setParameterTypes(Class<?>... parameterTypes) {
        this.parameterTypes = parameterTypes;

        return this;
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
    public BridgeViewHolder onCreateViewHolder(@NonNull View view) {
        if (delegate != null) {
            VH holder = delegate.onCreateViewHolder(view);
            holder.onViewCreated(view);

            return holder;
        }

        BridgeViewHolder holder = null;

        {
            Object[] args = new Object[this.parameters.length + 1];
            System.arraycopy(this.parameters, 0, args, 0, this.parameters.length);
            args[this.parameters.length] = view;

            Class<?>[] classArray = new Class[args.length];
            if (parameterTypes != null) {
                System.arraycopy(parameterTypes, 0, classArray, 0, parameterTypes.length);
            } else {
                for (int i = 0, size = this.parameters.length; i < size; i++) {
                    classArray[i] = args[i].getClass();
                }
            }

            classArray[this.parameters.length] = View.class;
            try {

                Constructor<? extends BridgeViewHolder> constructor = holderClazz.getConstructor(classArray);
                constructor.setAccessible(true);

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

        if (holder == null) {
            throw new IllegalArgumentException(holderClazz.getName() + " should have public constructor and match params.");
        }

        if (holder != null) {
            if (holder.getLayoutResourceId() != this.resId) {
                throw new IllegalArgumentException(holderClazz.getName() + " has different layout resource id. check it!");
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, Object item, int position, @NonNull List payloads) {
        VH viewHolder = (VH)holder;
        viewHolder.onBind(item, position, payloads);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        VH viewHolder = (VH)holder;
        viewHolder.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        VH viewHolder = (VH)holder;
        viewHolder.onViewDetachedFromWindow();
    }
}
