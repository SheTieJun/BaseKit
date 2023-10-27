#### P 适配的问题  9.0
1. 全面屏检查
ArmsUtils.checkIsNotchScreen(context)

2. 前台 Service
```
在 Android P 中，如果 targeSdkVersion 升级到 28，
使用前台 Service 必须要申请 FOREGROUND_SERVICE 权限，
如果没有申请该权限，系统会抛出 SecurityException，
该权限为普通权限，申请自动授予应用。
```

3. 限制了明文流量的网络请求

> CLEARTEXT communication to life.115.com not permitted by network security policy
问题原因： Android P 限制了明文流量的网络请求，非加密的流量请求都会被系统禁止掉
```
解决方案：
在资源文件新建xml目录，新建文件
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>


清单文件配置：
    <application
        android:networkSecurityConfig="@xml/network_security_config">
        <!--9.0加的，哦哦-->
        <uses-library
            android:name="org.apache.http.legacy"
            android:required="false" />
    </application>
```

#### P的新特性

1. HEIF 图片格式支持
2. Android P 增加对凹口屏幕的支持


