```
    dataBinding {
        enabled = true
    }
```
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


