## APT使用 KotlinPoet
1. 文件创建`FileSpec`
2. 类创建`TypeSpec`
3. 方法创建`FunSpec`
   1. 构造函数`FunSpec.constructorBuilder()`
   2. 扩展函数`FunSpec.builder("square").receiver(Int::class)`
   3. 静态方法`addAnnotation(JvmStatic::class)`
   4. Lambda `LambdaTypeName`
4. 属性创建 `ParameterSpec` `addParameter("info", String::class)`
   1. 默认值  `ParameterSpec.builder("android", String::class).defaultValue("\"pie\"")`
   2. 可以空 `.copy(nullable = true）`
   3. var `.mutable()`
5. 泛型 `TypeVariableName("K")`
6. 继承类，实现接口
   1. 继承 `addSuperclass`
   2. 实实现 `addSuperinterface`
7. 类型创建->枚举`addEnumConstant`
8. 内部类 `TypeSpec.anonymousClassBuilder()`
9. 添加注解`.addAnnotation(JvmStatic::class)`


## Element 相关
  package com.zac4j;  // PackageElement

     public class Foo {  // TypeElement
       private int a;  // VariableElement
       private Foo other;  // VariableElement
       public Foo() {}  // ExecutableElement
       public void setA(    // ExecutableElement
                        int newA  // VariableElement
                        ) {}
     }

1. 包名 `PackageElement`
2. 类 `TypeElement`
   1. 类上的泛型 `TypeElement.getTypeParameters()`
3. 属性``VariableElement
4. 方法`ExecutableElement`

常用的方法

`TypeMirror `  =>  `asType()`
> 返回一个TypeMirror是元素的类型信息，包括包名，类(或方法，或参数)名/类型，在生成动态代码的时候，我们往往需要知道变量/方法参数的类型，以便写入正确的类型声明

<A extends Annotation> A `getAnnotation(Class<A> annotationType)`
> 根据传入的注解类型获取该元素上的注解

List<? extends Element> `getEnclosedElements()`
> 返回该元素直接包含的子元素,通常对一个PackageElement而言，它可以包含TypeElement；对于一个TypeElement而言，它可能包含属性VariableElement，方法ExecutableElement,

Element` getEnclosingElement()`
> 返回包含该element的父element


Set<Modifier> `getModifiers()`
> 返回修饰词 final 等

`getQualifiedName()`
> 获取完整的名字

`getReturnType()`

`getParameters()`
