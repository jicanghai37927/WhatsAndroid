package club.andnext.dataset;

import com.google.gson.annotations.SerializedName;

public class BaseEntry {

    @SerializedName("id")
    protected String id;

    public BaseEntry() {

    }

    public String getId() {
        return id;
    }

}
