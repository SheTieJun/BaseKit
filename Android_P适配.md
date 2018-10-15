#### P 适配的问题
1. 全面屏检查
ArmsUtils.checkIsNotchScreen(context)

2. 前台 Service
```
在 Android P 中，如果 targeSdkVersion 升级到 28，
使用前台 Service 必须要申请 FOREGROUND_SERVICE 权限，
如果没有申请该权限，系统会抛出 SecurityException，
该权限为普通权限，申请自动授予应用。
```

#### P的新特性

1. HEIF 图片格式支持
2. Android P 增加对凹口屏幕的支持