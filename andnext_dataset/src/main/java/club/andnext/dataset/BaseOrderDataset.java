package club.andnext.dataset;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BaseOrderDataset<E extends BaseEntry> extends BaseDataset<E> {

    @SerializedName("order_list")
    List<String> orderList;

    transient boolean reverse = false;

    public BaseOrderDataset(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public void add(int index, E entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E entry) {
        boolean r = super.add(entry);

        if (r) {

            if (orderList == null) {
                orderList = new ArrayList<>();
            }

            if (reverse) {
                orderList.add(0, entry.getId());
            } else {
                orderList.add(entry.getId());
            }
        }

        return r;
    }

    public List<String> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<String> orderList) {
        this.orderList = orderList;
    }
}
