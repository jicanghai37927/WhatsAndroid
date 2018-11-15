package club.andnext.dataset;

import com.google.gson.annotations.SerializedName;

public class BaseEntry {

    @SerializedName("id")
    protected String id;

    @SerializedName("created")
    protected String created;

    @SerializedName("modified")
    protected String modified;

    @SerializedName("deleted")
    protected String deleted;

    public BaseEntry() {

    }

    public String getId() {
        return id;
    }

    public String getCreated() {
        return created == null? "": created;
    }

    public void setCreated(String utc) {
        this.created = utc;
    }

    public String getModified() {
        return modified == null? "": modified;
    }

    public void setModified(String utc) {
        this.modified = utc;
    }

    public String getDeleted() {
        return deleted == null? "": deleted;
    }

    public void setDeleted(String utc) {
        this.deleted = utc;
    }
}
