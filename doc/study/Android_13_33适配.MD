1. 通知:
   <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>

```kotlin
fun checkNotificationEnable(context: FragmentActivity, isRequest: Boolean = true): Boolean {
   if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
      return true
   }
   if (isRequest) {
      //如果目标版本是低于12
      if (context.applicationInfo.targetSdkVersion < VERSION_CODES.TIRAMISU) {
         //但是手机>= 13
         if (VERSION.SDK_INT >= 33) {
            //是否已经创建了渠道，这里是利用创建渠道来创建通知
            val channel = NotificationManagerCompat.from(context).notificationChannels.find { it.id == "活动通知" }
            if (channel == null) {
               val channelId = "活动通知"
               val channelName: CharSequence = "活动通知"
               val channelDescription = "用于消息通知"
               val channelImportance = NotificationManager.IMPORTANCE_HIGH
               val notificationChannel = NotificationChannel(channelId, channelName, channelImportance)
               notificationChannel.description = channelDescription
               notificationChannel.enableVibration(false)
               notificationChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
               notificationChannel.setSound(null, null)
               notificationChannel.enableLights(false)
               NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
            } else {
               goSettingNotification(context)
            }
         } else {
            goSettingNotification(context)
         }
      } else {
         if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            context.hasPermission(permissions = arrayOf(permission.POST_NOTIFICATIONS))
         } else {
            goSettingNotification(context)
         }
      }
   }
   return false
}

```

2. 存储权限细分:
``` 
permission.READ_MEDIA_VIDEO
permission.READ_MEDIA_IMAGES
permission.READ_MEDIA_AUDIO
```
