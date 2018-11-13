# WhatsNote的移动笔记功能

## 一、实现效果

![WhatsNote的移动笔记功能](./MoveNotes.gif)

[演示APK](https://raw.githubusercontent.com/jicanghai37927/WhatsAndroid/master/andnext_app_whatsnote/release/andnext_app_whatsnote-release.apk)

[TargetFolderFragment.java](https://github.com/jicanghai37927/WhatsAndroid/blob/master/andnext_app_whatsnote/src/main/java/com/haiyunshan/whatsnote/record/TargetFolderFragment.java)

[演示项目工程](https://github.com/jicanghai37927/WhatsAndroid)



## 二、功能需求

为**WhatsNote**增加笔记整理功能，移动笔记到指定目录。



## 三、设计要点

* 采用树形结构，便于选定目标文件夹；
* 移动文件夹时，不能移动到自身及子文件夹内；
* 折叠树形节点时，保持子节点的展开状态，便于选定目标文件夹；



## 四、实现过程

