package club.andnext.recyclerview.bridge;

import android.content.Context;

import club.andnext.recyclerview.adapter.ClazzAdapter;
import club.andnext.recyclerview.adapter.ClazzAdapterProvider;

public class BridgeAdapter extends ClazzAdapter {

    public BridgeAdapter(Context context, ClazzAdapterProvider provider) {
        super(context, provider);
    }

    public BridgeAdapter bind(Class<?> clazz, BridgeBuilder... builders) {
        super.bind(clazz, builders);

        return this;
    }

}
