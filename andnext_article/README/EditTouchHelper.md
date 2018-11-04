# 完美解决RecyclerView + EditText的输入问题

## 一、实现效果

* 问题描述

当EditText与RecyclerView结合使用，实现富文本编辑器，因为EditText的高度使用了WRAP_CONTENT的方式。导致用户必须点击EditText才能触发输入法。问题不大，体验不佳。理想的方式应该点击整个RecyclerView都能触发输入法。

* 实现效果

EditText的控件大小以图中红线位置为边界。顶部有24dp的Margin。并且实现用户滑动到输入法区域关闭输入法的功能。

![EditTouchHelper](./EditTouchHelper.gif)

## 二、下载地址

演示APK：

EditTouchHelper代码：

演示项目工程：



## 三、使用方式



之前实现过一个版本，通过继承`RecyclerView`，重载`dispatchTouchEvent`方法的方式实现。使用继承方式过于暴力，非常不优雅。

当前版本的实现通过`OnItemTouchListener`，动态添加到`RecyclerView`的方式实现，比前个版本优雅许多。