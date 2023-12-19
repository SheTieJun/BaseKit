## Kotlin contract

contract 为开发者解决了**编译器不够智能**的问题，这样可以使代码更简练，更加通俗易懂。但是这个智能的做法是通过开发者主观代码告诉编译器的，编译器无条件地遵守这个约定，这也就为开发者提出了
额外的要求，那就是**一定要确保 contract 的正确性**，不然将会导致很多不可控制的错误，甚至是**崩溃**。
 

```kotlin
  contract { returns(true) implies (foldFeature != null) }
```

