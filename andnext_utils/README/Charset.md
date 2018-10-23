# Android识别文本编码

## 一、使用方式Usage

* 下载andnext_utils模块

下载地址：https://github.com/jicanghai37927/WhatsAndroid/tree/master/andnext_utils



* CharsetUtils获取文本编码

```Java
String getCharset(@NonNull byte[] bytes, String defaultCharset)
String getCharset(@NonNull byte[] bytes, int length, String defaultCharset)
String getCharset(@NonNull byte[] bytes, int offset, int length, String defaultCharset)
```



## 二、 代码解析

实际的代码解析工作由`UniversalDetector`来完成。

```Java
static final String getCharset(UniversalDetector detector, byte[] bytes, int offset, int length) {
        detector.handleData(bytes, offset, length);
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();

        detector.reset();

        return encoding;
    }
```

`UniversalDetector`是Mozilla的编码识别库。GitHub上可以找到Java的实现版本。



## 三、参考资料

- juniversalchardet:    

https://github.com/albfernandez/juniversalchardet  

https://mvnrepository.com/artifact/com.github.albfernandez/juniversalchardet

