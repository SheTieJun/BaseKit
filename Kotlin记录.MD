##### 修饰词
`internal` // 在同一module中可见

`private `只在该类(以及它的成员)中可见

`protected` 和 `private `一样但在子类中也可见

`internal` 在本模块的所有可以访问到声明区域的均可以访问该类的所有 internal 成员 ( internal — any client inside this module who sees the declaring class sees its internal members;)

`public` 任何地方可见 (public — any client who sees the declaring class sees its public members.)+

##### 扩展
1. 函数扩展