## Binder 机制

### Linux 下传统的IPC
内核空间：系统内核工作区域
用户空间：应用程序工作区域

传统的IPC，进行通信需要进行2次copy，半双工，只能一个方向，当双方互相通信的时候，需要建立2管道


copy_from_user()
copy_to_user()

### Binder IPC
内存映射：用户空间的部分空间于内存空间，是指这部分空间无论是应用程序修改还是内核空间对这部分修改都会修改到这部分的内存

一次完整的Binder IPC
1. Binder 驱动在内核空间开辟一个`数据接受缓存区`
2. 接着开一个`内核缓存区`，是这2个区域进行映射关系，同时`内核中的数据缓存区`和`接受进程用户空间地址`的映射关系
3. 发送方用过系统调用copy_from_user()把数据copy到内存缓存区，因为映射的关系，所有相当于数据进入了接受进程的的用户空间

模型：C/S
client 和Server 都是在用户空间  Service Manager 也是在用户空间
Binder驱动在内核空间

### 为什么AMS通过socket与Zygote进程进行通信，那么为什么不使用binder呢？
Zygote是通过fork生成进程的，而多线程是不允许使用fork，可能造成死锁，
同时Binder又是多线程的，所以就干脆不用Binder转而使用socket了
Zygote的Java框架层中，会创建一个Server端的Socket，这个Socket用来等待AMS来请求Zygote来创建新的应用程序进程

### 跨进程通信方式
共享内存 （ 最快 ）
管道（copy2次）
Binder 驱动 （1次，通过内存映射）
Socket（2次）
AIDL ,Messenger （都是基于Binder）
文件