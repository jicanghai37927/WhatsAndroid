package com.haiyunshan.record;

public class RecentEntity {

    public static final boolean put(String id) {
        int index = 0;

        RecentDataset ds = RecordManager.getInstance().getRecentDataset();

        {
            RecentEntry entry = ds.get(id);
            if (entry == null) {
                entry = new RecentEntry(id);
            } else {
                ds.remove(entry);
            }

            ds.add(index, entry);
            entry.setModified(System.currentTimeMillis());
        }

        {
            RecordManager.getInstance().save(ds);
        }

        return true;
    }

}
