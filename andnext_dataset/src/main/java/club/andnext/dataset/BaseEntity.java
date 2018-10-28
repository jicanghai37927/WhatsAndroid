package club.andnext.dataset;

import com.google.gson.annotations.SerializedName;

public class BaseEntity {

    @SerializedName("id")
    protected String id;

    @SerializedName("created")
    protected long created;

    @SerializedName("modified")
    protected long modified;

    @SerializedName("deleted")
    protected long deleted;

    public BaseEntity() {
        this.created = System.currentTimeMillis();
        this.modified = this.created;
    }

    public String getId() {
        return id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public long getDeleted() {
        return deleted;
    }

    public void setDeleted(long deleted) {
        this.deleted = deleted;
    }
}
