```
    val binding: ActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater())
```

在 Fragment、ListView 或 RecyclerView 适配器中使用数据绑定项，您可能更愿意使用绑定类或 DataBindingUtil 类的 inflate() 方法，如以下代码示例所示：
```
    val listItemBinding = ListItemBinding.inflate(layoutInflater, viewGroup, false)
    // or
    val listItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item, viewGroup, false)
```

```
ObservableBoolean
ObservableByte
ObservableChar
ObservableShort
ObservableInt
ObservableLong
ObservableFloat
ObservableDouble
ObservableParcelable
```

```
ObservableField
```

> 自己的标准能用liveData就用liveData

## InverseBindingAdapter 和 BindingAdapter
BindingAdapter主要定义赋值给一个view，反过来，InverseBindingAdapter定义从一个view取值

双向数据绑定布局文件@{}变为@={}

原则一：能不用可观察变量尽量不要用。
原则二：多个变量会同时改变的情况尽量使用一个可观察变量进行包装。
原则三：data标签能少导入一个变量尽量少导入。
原则四：XML布局尽量少或者不使用过多的逻辑判断。
原则五：避免对一个数据进行多次绑定（有人通过这种方式刷新界面，这个其实和DataBinding的初衷违背了）。
原则六：严格遵守上述五条。


@InverseBindingMethods({@InverseBindingMethod(
type = android.widget.TextView.class,
attribute = "android:text",
event = "android:textAttrChanged",
method = "getText")})
public class MyTextViewBindingAdapters { ... }