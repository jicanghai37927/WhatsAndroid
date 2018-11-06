package club.andnext.recyclerview.section;

import androidx.recyclerview.widget.ListUpdateCallback;
import club.andnext.recyclerview.adapter.ClazzAdapterProvider;

import java.util.ArrayList;

public class SectionList implements ClazzAdapterProvider {

    ArrayList<Section> list;

    SectionList.Callback callback;

    public SectionList(SectionList.Callback callback) {
        this.list = new ArrayList<>();

        this.callback = callback;
    }

    @Override
    public Object get(int position) {
        Object obj = null;

        for (Section s : list) {
            if (position == 0) {
                obj = s.getUserObject();
                break;
            }

            position -= 1;
            if (s.isExpand() && position < s.size()) {
                obj = s.get(position);
                break;
            }

            if (s.isExpand()) {
                position -= s.size();
            }
        }

        return obj;
    }

    @Override
    public int size() {
        int size = 0;
        for (Section s : list) {
            size += 1;

            if (s.isExpand()) {
                size += s.size();
            }
        }

        return size;
    }

    public boolean setExpand(Object userObject, boolean value) {
        Section section = getSection(userObject);
        if ((section == null) || !(section.isExpand() ^ value)) {
            return false;
        }

        boolean expand = value;
        if (expand) {
            this.expand(section);
        } else {
            this.collapse(section);
        }

        section.setExpand(value);
        return true;
    }

    public boolean isExpand(Object userObject) {
        Section section = getSection(userObject);
        if ((section == null)) {
            return false;
        }

        return section.isExpand();
    }

    public Section add(Object userObject, boolean expand, ClazzAdapterProvider provider) {
        Section section = new Section(userObject, expand, provider);
        this.add(section);

        return section;
    }

    public void notifyInserted(Object userObject, int position) {
        this.notifyInserted(userObject, position, 1);
    }

    public void notifyInserted(Object userObject, int position, int count) {
        Section section = getSection(userObject);
        if (section == null || !section.isExpand()) {
            return;
        }

        int pos = positionOf(section);
        pos += 1;
        pos += position;

        callback.onInserted(pos, count);
    }

    public void notifyRemoved(Object userObject, int position) {
        this.notifyRemoved(userObject, position, 1);
    }

    public void notifyRemoved(Object userObject, int position, int count) {
        Section section = getSection(userObject);
        if (section == null || !section.isExpand()) {
            return;
        }

        int pos = positionOf(section);
        pos += 1;
        pos += position;

        callback.onRemoved(pos, count);
    }

    Section getSection(Object userObject) {
        if (userObject == null) {
            return null;
        }

        for (Section s : list) {
            if (s.getUserObject() == userObject) {
                return s;
            }
        }

        return null;
    }

    void add(Section section) {
        int position = list.size();

        list.add(section);

        int count = 1;
        if (section.isExpand()) {
            count += section.size();
        }

        callback.onInserted(position, count);
    }

    void expand(Section section) {
        int position = positionOf(section) + 1;
        int count = section.size();

        callback.onInserted(position, count);

    }

    void collapse(Section section) {
        int position = positionOf(section) + 1;
        int count = section.size();

        callback.onRemoved(position, count);
    }

    int positionOf(Section section) {
        int index = 0;

        for (Section s : list) {
            if (s == section) {
                break;
            }

            index += 1;
            if (s.isExpand()) {
                index += s.size();
            }
        }

        return index;
    }

    /**
     *
     * @param
     */
    private static class Section {

        boolean expand;

        Object userObject;
        ClazzAdapterProvider provider;

        public Section(Object userObject, boolean expand, ClazzAdapterProvider provider) {
            this.expand = expand;

            this.userObject = userObject;
            this.provider = provider;
        }

        public Object getUserObject() {
            return userObject;
        }

        void setExpand(boolean value) {
            if (!(expand ^ value)) {
                return;
            }

            this.expand = value;
        }

        boolean isExpand() {
            return expand;
        }

        Object get(int index) {
            return provider.get(index);
        }

        int size() {
            return provider.size();
        }

    }

    /**
     *
     */
    public static abstract class Callback implements ListUpdateCallback {

    }

}
