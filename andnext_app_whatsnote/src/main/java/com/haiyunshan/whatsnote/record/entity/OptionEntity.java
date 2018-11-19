package com.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import com.haiyunshan.whatsnote.WhatsApp;
import com.haiyunshan.whatsnote.record.dataset.OptionDataset;

public class OptionEntity extends BaseEntitySet<OptionEntity> {

    public static final String SECTION_ENTRANCE = "entrance";
    public static final String SECTION_FAVORITE = "favorite";
    public static final String SECTION_TAG      = "tag";

    SortEntity sort;        //
    SortEntity recentSort;  //

    OptionDataset entry;    //

    OptionEntity(Context context, OptionDataset entry) {
        super(context);

        this.entry = entry;
    }

    public SortEntity getSort() {
        if (sort == null) {
            String id = entry.getSortId();
            String order = entry.getSortOrder();

            this.sort = SortFactory.create(context, id);
            if (order.equals("desc")) {
                sort.setReverse(true);
            }
        }

        return new SortEntity(sort);
    }

    public void setSort(SortEntity sort) {

        if ((this.sort != null) && (this.sort.equals(sort))) {
            return;
        }

        {
            this.sort = sort;

            entry.setSortId(sort.getId());
            entry.setSortOrder(sort.isReverse() ? "desc" : "asc");
        }

        {
            save();
        }
    }

    public SortEntity getRecentSort() {

        if (recentSort == null) {

            String id = entry.getRecentSortId();
            String order = entry.getRecentSortOrder();

            this.recentSort = SortFactory.create(context, id);
            if (order.equals("desc")) {
                recentSort.setReverse(true);
            }
        }

        return new SortEntity(recentSort);
    }

    public void setRecentSort(SortEntity sort) {

        if ((this.recentSort != null) && (this.recentSort.equals(sort))) {
            return;
        }

        {
            this.recentSort = sort;

            entry.setRecentSortId(sort.getId());
            entry.setRecentSortOrder(sort.isReverse() ? "desc" : "asc");
        }

        {
            save();
        }
    }

    public boolean isSectionExpand(String section) {
        return entry.getSectionList().indexOf(section) >= 0;
    }

    public void setSectionExpand(String section, boolean value) {
        entry.getSectionList().remove(section);

        if (value) {
            entry.getSectionList().add(section);
        }

        {
            save();
        }
    }

    @Override
    public void save() {
        getManager().save(entry);
    }

    public static final OptionEntity obtain() {
        return OptionFactory.obtain(WhatsApp.getContext());
    }
}
