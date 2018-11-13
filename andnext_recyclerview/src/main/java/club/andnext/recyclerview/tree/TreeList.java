package club.andnext.recyclerview.tree;

import androidx.recyclerview.widget.ListUpdateCallback;
import club.andnext.recyclerview.adapter.ClazzAdapterProvider;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;

public class TreeList implements ClazzAdapterProvider {

    Node root; // all nodes

    boolean keepExpand = false; // keep child expand state
    ArrayList<Node> expandList; // expanded nodes

    ArrayList<Node> nodeList; // visible nodes

    TreeList.Callback callback; // RecyclerView.Adapter callback to notify changed

    Node lastNode; // cache last node to save time for index

    public TreeList(TreeList.Callback callback) {
        this.root = new Node(null);

        this.expandList = new ArrayList<>();
        expandList.add(root); // root node always expanded

        this.nodeList = new ArrayList<>();

        this.lastNode = null;
        this.callback = callback;
    }

    @Override
    public Object get(int index) {
        return nodeList.get(index).getUserObject();
    }

    @Override
    public int size() {
        return nodeList.size();
    }

    public void setKeepExpand(boolean value) {
        this.keepExpand = value;
    }

    public boolean isKeepExpand() {
        return keepExpand;
    }

    public boolean isLeaf(Object obj) {
        Node node = this.getNode(obj);
        return node.isLeaf();
    }

    public boolean isExpand(Object obj) {
        Node node = this.getNode(obj);
        return (expandList.indexOf(node) >= 0);
    }

    public int getChildCount(Object obj) {
        Node node = this.getNode(obj);
        if (node == null) {
            return 0;
        }

        return node.getChildCount();
    }

    public int getLevel(Object obj) {
        Node node = this.getNode(obj);
        if (node == null) {
            return -1;
        }

        int level = node.getLevel();
        level -= 1; // - root level

        return level;
    }

    public int getLevel() {
        int level = 0;

        for (Node n : nodeList) {
            int value = n.getLevel();
            level = (value > level)? value: level;
        }

        level -= 1; // - root level
        return level;
    }

    public boolean setExpand(Object obj, boolean expand) {
        Node node = this.getNode(obj);

        int index = expandList.indexOf(node);
        if (index >= 0) {
            if (!expand) {
                collapse(node);
            }
        } else {
            if (expand) {
                expand(node);
            }
        }

        return (expandList.indexOf(node) >= 0);
    }

    public void add(Object parent, Object child) {

        Node childNode = this.getNode(child);
        if (childNode != null) {
            return;
        }

        childNode = new Node(child);

        Node parentNode = null;
        if (lastNode != null && lastNode.getUserObject() == parent) {
            parentNode = lastNode;
        }

        if (parentNode == null) {
            parentNode = getNode(parent);
        }

        if (parentNode != null) {
            parentNode.add(childNode);
        }

        if (parentNode == null) {
            return;
        }

        {
            this.lastNode = parentNode;
        }

        int position = -1;
        int count = 1;

        //
        if (expandList.indexOf(parentNode) >= 0) {
            position = this.lastIndexOfDescendant(parentNode);
            position = (position >= 0)? (position + 1): position;

            if (position < 0) {
                position = nodeList.indexOf(parentNode);

                position = (position >= 0) ? (position + 1) : position;
            }

            position = (position < 0)? 0: position;

            if (position >= 0) {
                nodeList.add(position, childNode);
            }
        }

        //
        {
            int index = nodeList.indexOf(parentNode);
            if (index >= 0) {
                callback.onChanged(index, 1, null);
            }
        }

        //
        if (position >= 0 && count > 0) {
            callback.onInserted(position, count);
        }
    }

    public void remove(Object obj) {
        if (obj == null) {
            return;
        }

        Node node = this.getNode(obj);
        if (node == null) {
            return;
        }

        TreeNode parent;
        int position = nodeList.indexOf(node);
        int count = -1;

        // 删除可见节点
        {
            int begin = indexOfDescendant(node);
            int end = lastIndexOfDescendant(node);
            if (begin >= 0 && end >= 0) {
                for (int i = end; i >= begin; i--) {
                    nodeList.remove(i);
                }

                position = begin;
                count = end - begin + 1;
            }
        }

        // 删除展开节点
        {
            for (int i = expandList.size() - 1; i >= 0; i--) {
                Node n = expandList.get(i);
                if (n.isNodeAncestor(node)) {
                    expandList.remove(i);
                }
            }
        }

        // 从树形结构中移除
        {
            parent = node.getParent();
            if (parent.isLeaf() && (parent.getParent() != root)) {
                expandList.remove(parent);
            }

            node.removeFromParent();
        }

        //
        {
            int index = nodeList.indexOf(parent);
            if (index >= 0) {
                callback.onChanged(index, 1, null);
            }
        }

        //
        if (position >= 0 && count > 0) {
            callback.onRemoved(position, count);
        }

    }

    public int indexOf(Object userObject) {
        if (userObject == null) {
            return -1;
        }

        for (int i = 0, size = nodeList.size(); i < size; i++) {
            if (nodeList.get(i).getUserObject() == userObject) {
                return i;
            }
        }

        return -1;
    }

    void expand(Node node) {
        if (node.isLeaf()) {
            return;
        }

        int position = nodeList.indexOf(node);
        int count = nodeList.size();

        // 添加到展开节点
        {
            expandList.add(node);
        }

        // 添加到可见节点
        {
            int index = position;
            if (index >= 0) {
                expand(index, node);
            }
        }

        //
        {
            count = nodeList.size() - count;
            callback.onInserted(position + 1, count);
        }

    }

    int expand(int index, Node node) {
        if (expandList.indexOf(node) < 0) {
            return index;
        }

        Enumeration<Node> enumeration = node.children();
        while (enumeration.hasMoreElements()) {
            Node n = enumeration.nextElement();

            index += 1;
            nodeList.add(index, n);
            index = expand(index, n);
        }

        return index;
    }

    void collapse(Node node) {
        if (node.isLeaf()) {
            return;
        }

        int position = -1;
        int count = -1;

        // 删除所有可见节点
        {
            int begin = indexOfDescendant(node);
            int end = lastIndexOfDescendant(node);
            if (begin >= 0 && end >= 0 && end > begin) {
                for (int i = end; i > begin; i--) {
                    nodeList.remove(i);
                }

                position = begin + 1;
                count = end - begin;
            }

        }

        // 删除所有展开节点
        if (isKeepExpand()) {
            expandList.remove(node);
        } else {
            for (int i = expandList.size() - 1; i >= 0; i--) {
                Node n = expandList.get(i);
                if (n.isNodeAncestor(node)) {
                    expandList.remove(i);
                }
            }
        }

        //
        if (position >= 0 && count > 0) {
            callback.onRemoved(position, count);
        }

    }

    int indexOfDescendant(Node ancestor) {
        int size = nodeList.size();
        for (int i = 0; i < size; i++) {
            Node n = nodeList.get(i);
            if (n.isNodeAncestor(ancestor)) {
                return i;
            }
        }

        return -1;
    }

    int lastIndexOfDescendant(Node ancestor) {
        int size = nodeList.size();
        for (int i = size - 1; i >= 0; i--) {
            Node n = nodeList.get(i);
            if (n.isNodeAncestor(ancestor)) {
                return i;
            }
        }

        return -1;
    }

    int lastIndexOfChild(Node parent) {
        int size = nodeList.size();
        for (int i = size - 1; i >= 0; i--) {
            Node n = nodeList.get(i);
            if (parent.isNodeChild(n)) {
                return i;
            }
        }

        return -1;
    }

    Node getNode(Object userObject) {
        return getNode(root, userObject);
    }

    Node getNode(Node node, Object userObject) {
        if (node.getUserObject() == userObject) {
            return node;
        }

        Enumeration<Node> enumeration = node.children();
        while (enumeration.hasMoreElements()) {
            Node result = getNode(enumeration.nextElement(), userObject);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     *
     */
    private static class Node extends DefaultMutableTreeNode {

        public Node(Object userObject) {
            super(userObject);
        }

    }

    /**
     *
     */
    public static abstract class Callback implements ListUpdateCallback {

    }

}
