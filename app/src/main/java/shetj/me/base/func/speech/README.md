# Android 语音识别功能实现

本模块实现了Android平台上的语音识别（ASR - Automatic Speech Recognition）功能，提供了三种不同的实现方式，从基础到高级，满足不同的应用场景需求。

## 功能特点

- 基于Android原生SpeechRecognizer API实现
- 支持中文语音识别
- 提供三种不同的实现方式：基础、简易和持续监听
- 支持语音命令处理
- 错误处理和状态反馈

## 实现方式

### 1. 基础语音识别 (SpeechRecognizerActivity)

直接使用Android的SpeechRecognizer API实现的基础语音识别功能，适合简单场景。

- 实现了完整的RecognitionListener接口
- 提供开始/停止识别按钮
- 显示识别结果和错误信息
- 支持基本的语音命令处理

### 2. 简易语音识别 (SimpleSpeechActivity)

使用SpeechRecognizerManager封装的语音识别功能，更易于集成到其他界面中。

- 封装了SpeechRecognizer的复杂性
- 提供简化的监听器接口
- 更易于在其他Activity中复用
- 支持基本的语音命令处理

### 3. 持续语音识别 (ContinuousSpeechActivity)

使用前台服务实现的持续语音识别功能，可在后台持续监听语音命令。

- 使用前台服务持续监听
- 自动重启识别过程
- 显示识别历史记录
- 支持命令监听器接口
- 适合需要持续监听语音命令的场景

## 使用方法

### 权限要求

使用语音识别功能需要以下权限：

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" /> <!-- 用于持续监听 -->
```

### 基础用法

1. 启动SpeechRecognitionEntryActivity，选择需要的语音识别实现方式
2. 点击"开始识别"按钮开始语音识别
3. 说出要识别的内容
4. 查看识别结果

### 集成到其他Activity

如果要在自己的Activity中集成语音识别功能，推荐使用SpeechRecognizerManager：

```kotlin
// 初始化语音识别管理器
val speechManager = SpeechRecognizerManager(context)
speechManager.initialize()
speechManager.setListener(object : SpeechRecognizerManager.SpeechRecognitionListener {
    override fun onReadyForSpeech() {
        // 准备好开始说话
    }
    
    override fun onResults(results: ArrayList<String>) {
        // 处理识别结果
        val recognizedText = results[0]
        // 处理识别到的文本
    }
    
    // 实现其他必要的回调方法...
})

// 开始识别
speechManager.startListening()

// 停止识别
speechManager.stopListening()

// 释放资源
speechManager.destroy()
```

### 持续监听语音命令

如果需要持续监听语音命令，可以使用SpeechRecognitionService：

```kotlin
// 启动服务
val serviceIntent = Intent(context, SpeechRecognitionService::class.java)
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    context.startForegroundService(serviceIntent)
} else {
    context.startService(serviceIntent)
}

// 绑定服务
context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

// 服务连接
val serviceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as SpeechRecognitionService.SpeechBinder
        val speechService = binder.getService()
        
        // 设置命令监听器
        speechService.setCommandListener(object : SpeechRecognitionService.OnCommandListener {
            override fun onCommandRecognized(command: String) {
                // 处理识别到的命令
            }
        })
        
        // 开始持续监听
        speechService.startContinuousListening()
    }
    
    override fun onServiceDisconnected(name: ComponentName?) {
        // 处理服务断开连接
    }
}
```

## 注意事项

1. Android的语音识别功能依赖于Google服务，在某些设备上可能无法正常工作
2. 语音识别需要网络连接
3. 持续监听会消耗更多的电量
4. 在Android 10及以上版本，前台服务需要在通知中显示
5. 在某些设备上，可能需要处理特定的错误情况

## 扩展和优化

- 支持离线语音识别
- 添加自定义唤醒词功能
- 优化电量消耗
- 支持更多语言
- 集成自然语言处理功能