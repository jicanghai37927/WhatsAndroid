# 必须掌握的Android开源库

## 1. Glide

非常优秀的图片加载图。

相比与Fresco，个人更喜欢Glide一点。虽然Fresco提供的`SimpleDraweeView`使用起来很简单，但还是更喜欢Glide的设计。

目前，**WhatsNote**编辑器使用Glide实现了图片加载功能。

* Glide项目地址：https://github.com/bumptech/glide

* Glide中文使用指南：https://muyangmin.github.io/glide-docs-cn/



## 2. overscroll-decor

OverScroll回弹效果库。

虽然Android的OverScroll阴影效果也是一种非常优秀的边界提醒。但个人还是更偏向于iOS的回弹效果，更加直观。使用`overscroll-decor`可以非常容易实现iOS的回弹效果。

作者最近一次的更新时间是在`4 Jul 2017`，还没有迁移到`AndroidX`。

目前，Fork了一个版本迁移到`AndroidX`，还在测试中，尚未应用于**WhatsNote**。

* overscroll-decor项目地址：https://github.com/EverythingMe/overscroll-decor



## 3. subsampling-scale-image-view

 显示大图必备的ImageView。

使用`BitmapRegionDecoder`加载图片，有效解决了加载大图时出现`OutOfMemoryError`错误的问题。并且支持手势缩放，惯性滑动，……

目前，**WhatsNote**用来预览图片文件。

* subsampling-scale-image-view项目地址：https://github.com/davemorrissey/subsampling-scale-image-view



## 4. gson

Google出品的JSON序列化/反序列化工具。

极大简化了JSON的解析工作。

目前，**WhatsNote**使用gson解析所有的JSON数据。

* gson项目地址：https://github.com/google/gson



## 5. prettytime

时间格式化工具库。

优雅地显示时间。

目前，**WhatsNote**使用prettytime显示所有时间信息。

* prettytime项目地址：https://github.com/ocpsoft/prettytime
* 官方网站：http://www.ocpsoft.org/prettytime/



## 6. marked

markdown解析器。

markdown格式数据高效解析为html格式。

目前，**WhatsNote**使用marked实现预览markdown文档。

* marked项目地址：https://github.com/markedjs/marked



## 7. highlight.js

语法高亮。

支持185种语言，89种样式。

目前，**WhatsNote**使用highlight.js实现markdown代码内容的语法高亮。

* highlight.js官方网站：https://highlightjs.org/



## 8. github-markdown-css

github风格的markdown样式。

**WhatsNote**项目托管在GitHub上，非常喜欢GitHub的README的markdown风格。所以**WhatsNote**的markdown预览风格使用的便是github样式。

目前，**WhatsNote**采用github-markdown-css样式预览markdown文档。

* github-markdown-css项目地址：https://github.com/sindresorhus/github-markdown-css



## 未完，待续



