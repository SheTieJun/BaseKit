## Detekt

## 相关命令
`./gradlew detektBaseline` 来生成一个 baseline.xml 基线文件。
`./gradlew detektGenerateConfig` 会生成 config/detekt/detekt.yml 文件
`./gradlew detekt` 会执行检查，如果有问题会输出到控制台。

detekt.yml ：

| 规则大类           | 说明                               |
|:---------------|:---------------------------------|
| comments       | 与注释、文档有关的规范检查                    |
| complexity     | 检查代码复杂度，复杂度过高的代码不利于维护            |
| coroutines     | 与协程有关的规范检查                       |
| empty-blocks   | 空代码块检查，空代码应该尽量避免                 |
| exceptions     | 与异常抛出和捕获有关的规范检查                  |
| formatting     | 格式化问题，detekt直接引用的 ktlint 的格式化规则集 |
| naming         | 类名、变量命名相关的规范检查                   |
| performance    | 检查潜在的性能问题                        |
| potentail-bugs | 检查潜在的BUG                         |
| style          | 统一团队的代码风格，也包括一些由 Detekt 定义的格式化问题 |


## 忽略规则
1.在类名或者方法名前添加诸如@Suppress("LargeClass")这样的注解，可以有针对性地忽略这个文件中的某些告警。
2.在文件头添加@file:Suppress("TooManyFunctions"),则可以直接告诉detekt不扫描这个文件中的这类问题。
