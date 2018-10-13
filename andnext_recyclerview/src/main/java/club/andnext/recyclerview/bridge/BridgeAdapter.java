package club.andnext.recyclerview.bridge;

import android.content.Context;

import club.andnext.recyclerview.adapter.ClazzAdapter;

public class BridgeAdapter extends ClazzAdapter {

    public BridgeAdapter(Context context, BridgeAdapterProvider provider) {
        super(context, provider);
    }

    public BridgeAdapter bind(Class<?> clazz, BridgeBuilder... builders) {
        super.bind(clazz, builders);

        return this;
    }

}
