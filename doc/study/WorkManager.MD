## WorkManager

## 长时间运行的 worker
- 如果您的应用以 Android 10（API 级别 29）或更高版本为目标平台， 并且包含需要位置信息访问权限的长时间运行的 worker，请指明该 worker 使用前台服务类型 location。
- 如果您的应用以 Android 11（API 级别 30）或更高版本为目标平台，并且包含需要访问摄像头或麦克风的长时间运行的 worker，请分别声明 camera 或 microphone 前台服务类型。
- 如果您的应用以 Android 14（API 级别 34）或更高版本为目标平台，您必须为所有长时间运行的 worker 指定前台服务类型。从 Android 14（API 级别 34）开始，您必须为每个前台服务声明适当的服务类型。

