# Compose Annotation 注解的相关解释使用

## 1. @Composable

@Composable是一个注释，告诉 Compose 编译器这个函数是一个 Composable 函数。Compose 编译器会将这个函数转换为一个树状结构，然后根据这个树状结构来构建 UI。

## 2. @Preview

@Preview是一个注释，告诉 Compose 编译器这个函数是一个预览函数。Compose 编译器会将这个函数转换为一个预览，然后在 Android Studio 中显示出来。

## 3. @Model

@Model是一个注释，告诉 Compose 编译器这个类是一个模型类。Compose 编译器会将这个类转换为一个模型，然后在 Compose 运行时中使用。

## 4. @Immutable 和 @Stable

- @Immutable是一个注释，告诉 Compose 编译器该对象对于优化来说是不可变的，因此如果不使用它，可能会触发不必要的重新组合。

- @Stable是另一个注释，告诉 Compose 编译器这个对象可能会改变，但是当它改变时，Compose 运行时会收到通知。