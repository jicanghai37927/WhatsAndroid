# 修改release.keystore为debug.keystore

修改store的密码
keytool -storepasswd -keystore ./debug.keystore 

修改alias
keytool -changealias -keystore ./debug.keystore -alias {原来的alias} -destalias androiddebugkey
 
修改key的密码
keytool -keypasswd -keystore ./debug.keystore -alias androiddebugkey 
