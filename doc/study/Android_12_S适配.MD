## Android S 12

## 通知需要 PendingIntent.FLAG_IMMUTABLE Or FLAG_MUTABLE

1. 对于以 Android 12 为目标平台的应用，所有通知都是可展开的。通常，这意味着，如果您使用的是 setCustomContentView，则还需要使用 setBigCustomContentView，以确保收起状态和展开状态保持一致。

通过调用 ACTION_MANAGE_STORAGE intent 操作检查可用空间。
如果设备上的可用空间不足，请提示用户同意让您的应用清除所有缓存。为此，请调用 ACTION_CLEAR_APP_CACHE intent 操作。


