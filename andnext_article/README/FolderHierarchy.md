# WhatsNote多层目录结构设计

## 一、整体结构

整体结构由三部分组成

* 目录
* 笔记
* 记录——笔记及目录的所属关系。

目录和笔记为原子数据，目录与目录、笔记与笔记、目录与笔记之间没有层级关系，分开保存在2个集合当中。

记录将目录与笔记联系到一起，建立层级及所属关系。



## 二、数据设计

* 记录

```java
/** 记录集合 */
RecordDataset extends BaseDataset

```

```java
/** 记录 */
RecordEntity extends BaseEntity
	String parent; 	// 父类ID
	String type; 	// 类型，Folder或者Note
	ArrayList<String> orderList; // 顺序列表
```



* 目录与笔记的基类

```java
/** 抽象基类，目录与笔记的共同属性 */
FileEntity extends BaseEntity
	String name; // 名称
	String desc; // 描述
	String alias; // 自动生成的名称
```



* 目录

```java
/** 目录集合 */
FolderDataset extends BaseDataset
```

```Java
/** 目录 */
FolderEntity extends FileEntity
	
```



* 笔记

```java
/** 笔记集合 */
NoteDataset extends BaseDataset
```

```java
/** 笔记 */
NoteEntity extends FileEntity
	ArrayList<String> tagList; // 标签列表
```



## 三、笔记管理者

`NoteManager`用来管理`RecordDataset`，`FolderDataset`，`NoteDataset`。通过`NoteManager`，我们可以访问并管理所有的数据信息，目录、笔记、层级结构。

* `NoteManager`的核心属性

```java
RecordDataset recordDs; // 记录集合
File recordFile; 

FolderDataset folderDs; // 目录集合
File folderFile; 

NoteDataset	noteDs; 	// 笔记集合
File noteFile; 
```

* `NoteManager`的核心方法

1. 读取指定目录数据集合

```java
public List<FileEntity> get(String parent); 
```

2. 创建目录及笔记

```java
public FolderEntity createFolder(String parent); // 创建目录
public NoteEntity createNote(String parent); // 创建笔记
```

3. 删除目录及笔记

```java
public void remove(FolderEntity entity); 
public void remove(NoteEntity entity); 
```

4. 保存数据

```java
public void save(); 
```

5. 功能方法

```java
public String getFolderName(String name); // 获取目录名称
public String getNoteName(String name); //	获取笔记名称
```

6. 内部方法

```java
RecordDataset getRecordDataset(); 
FolderDataset getFolderDataset(); 
NoteDataset getNoteDataset(); 
```



