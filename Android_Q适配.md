### Android Q 适配

#### 沙盒机制

`Context.getExternalFilesDir()`下的文件夹。
比如要存储一张图片,则应放在`Context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)`中。


#### 定位权限

Android Q 引入了新的位置权限 ACCESS_BACKGROUND_LOCATION。


#### 设备唯一标识符

```
((TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId()
```

#### 非SDK接口介绍
非SDK接口限制就是某些SDK中的私用方法，如private方法，你通过Java反射等方法获取并调用了。那么这些调用将在target>=P或target>=Q的设备上被限制使用，当你使用了这些方法后，会报错: