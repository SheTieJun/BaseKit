#### Android 8.0

1. 安卓8.0中PHONE权限组新增两个权限
```
ANSWER_PHONE_CALLS：允许您的应用通过编程方式接听呼入电话。要在您的应用中处理呼入电话，您可以使用 acceptRingingCall() 函数。
READ_PHONE_NUMBERS ：权限允许您的应用读取设备中存储的电话号码。

```

2. 通知适配


3. 安装APK
```
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>

private static final int REQUEST_CODE_UNKNOWN_APP = 100;

    private void installAPK(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (hasInstallPermission) {
                //安装应用
            } else {
                //跳转至“安装未知应用”权限界面，引导用户开启权限
                Uri selfPackageUri = Uri.parse("package:" + this.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, selfPackageUri);
                startActivityForResult(intent, REQUEST_CODE_UNKNOWN_APP);
            }
        }else {
            //安装应用
        }

    }

    //接收“安装未知应用”权限的开启结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UNKNOWN_APP) {
            installAPK();
        }
    }

```

4. 可能SharedPreferences闪退

5. SecurityException的闪退

        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.FileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/base_file_path"/>
        </provider>

6. 静态广播无法正常接收 使用动态广播代替静态广播

7. Caused by: java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
   解决方案(1)：
   问题原因： Android 8.0 非全屏透明页面不允许设置方向(8.1以上谷歌就修复去掉了这个限制)

   解决方案：android:windowIsTranslucent设置为false
