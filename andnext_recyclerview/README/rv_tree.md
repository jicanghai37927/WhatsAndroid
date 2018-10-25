# 基于RecyclerView的树形结构

## 一、使用方式Usage

* 实现效果

![树形结构](./rv_tree_demo.gif)

[演示代码](https://github.com/jicanghai37927/WhatsAndroid/blob/master/andnext_app_whatsandroid/src/main/java/com/haiyunshan/demo/recyclerview/TreeRVDemoFragment.java)



* 导入相关模块

`andnext_recyclerview`基于RecyclerView的封装，包含了本文中的树形实现；

`andnext_overscroll`RecyclerView的OverScroll依赖；

`andnext_java`部分代码依赖于JavaSE；

下载地址：https://github.com/jicanghai37927/WhatsAndroid



* 实例化TreeList

TreeList参考了SortedList的实现，如果有使用过SortedList，那么上手会非常容易。TreeList内部实现了树形结构的展开和折叠，所以提供了TreeListAdapterCallback来通知数据变化。

```Java
this.treeList = new TreeList(new TreeListAdapterCallback(adapter) {
    @Override
    public void onInserted(int position, int count) {
        super.onInserted(position, count);

        recyclerView.smoothScrollToPosition(position);
    }
});
```



* 构建树形结构

通过`TreeList`的`add(Object parent, Object child)`构建树形结构，`parent = null`时添加到`TreeList`的根节点。

```Java
treeList.add(null, new FolderHeader());
```

通过遍历将数据添加到`TreeList`中。

```Java
AreaDataset dataset = GsonUtils.fromJson(getActivity(), "dataset/area_ds.json", AreaDataset.class);
this.buildTree(treeList, dataset, null);
```

```Java
void buildTree(TreeList tree, AreaDataset ds, AreaDataset.AreaEntity parent) {
    List<AreaDataset.AreaEntity> list = ds.getChildren(parent == null? "": parent.getId(), null);

    for (AreaDataset.AreaEntity e : list) {
        tree.add(parent, e);
    }

    for (AreaDataset.AreaEntity e : list) {
        this.buildTree(tree, ds, e);
    }
}
```



* 适配到RecyclerView.Adapter

通过`TreeList`的`get()`与`size()`接口，可以获取到当前可见的节点数据。

```Java
this.adapter = new BridgeAdapter(getActivity(), new BridgeAdapterProvider() {
    @Override
    public Object get(int position) {
        return treeList.get(position);
    }

    @Override
    public int size() {
        return treeList.size();
    }
});
```



## 二、设计理念

功能设计上要求不需要调整既有的代码结构，仅仅是扩展功能，所有设计时作了以下几点限制：

* 不依赖于RecyclerView

* 不依赖于Adapter
* 不依赖于ViewHolder
* 不依赖于Object

所以使用`TreeList`，不需要调整现有的任何代码。

只需要稍微修改`RecyclerView.Adapter`的代码即可无缝对接。



## 三、代码解析

* 树形数据结构

```Java
package javax.swing.tree;
	TreeNode.java
	MutableTreeNode.java
	DefaultMutableTreeNode.java
```

直接拷贝了一份JavaSE的代码，不需要重新造轮子。

* `TreeList`

```Java
package club.andnext.recyclerview.tree;
	TreeList.java // Tree->List转换
	TreeListAdapterCallback.java // 回调接口，通知节点变化
```

* TreeList的主要属性

```Java
Node root; // all nodes

ArrayList<Node> expandList; // expanded nodes

ArrayList<Node> nodeList; // visible nodes

TreeList.Callback callback; // RecyclerView.Adapter callback to notify changed
```

* TreeList的主要方法

第一层：构建并显示数据内容

```Java
public void add(Object parent, Object child); // 构建树形结构

public Object get(int index); // 获取可见节点数据
public int size(); // 可见节点数

public int getChildCount(Object obj); // 获取节点的child个数
public boolean isLeaf(Object obj); // 判断是否叶子节点
public boolean isExpand(Object obj); // 判断是否展开

public int getLevel(Object obj); // 当前数据所处level
public int getLevel(); // 最大level
```

第二层：展开、折叠数据节点

```Java
public boolean setExpand(Object obj, boolean expand); // 设置展开或折叠
```

第三层：添加、删除节点

```Java
public void add(Object parent, Object child); // 添加数据

public void remove(Object obj); // 删除数据
```

