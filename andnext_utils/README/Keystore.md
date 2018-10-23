# 修改release.keystore为debug.keystore

生产项目中，我们会创建自己的keystore文件，用于最终的发布版本，使用另外一个keystore文件进行调试。

当在同一部手机中安装release和debug版本时，会出现keystore不匹配的情况。

所以，我们需要将release.keystore修改为debug.keystore，以保证可以覆盖安装。

* 修改要点

```bash
# alias必须是androiddebugkey
# store和key的密码必须是android
```

1. 修改store的密码

```bash
keytool -storepasswd -keystore ./debug.keystore 
```

2. 修改alias

```bash
keytool -changealias -keystore ./debug.keystore -alias {原来的alias} -destalias androiddebugkey
```

3. 修改key的密码

```bash
keytool -keypasswd -keystore ./debug.keystore -alias androiddebugkey 
```