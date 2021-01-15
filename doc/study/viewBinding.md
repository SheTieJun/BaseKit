## ViewBinding

[include](https://blog.csdn.net/fly_with_24/article/details/104337067)

### 新增BaseBindingActivity 和 BaseBindingFragment
Activity
```
    override fun initViewBinding(): ActivityMainBinding {
       return ActivityMainBinding.inflate(layoutInflater)
    }
```

Fragment
```
    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBlankBinding {
        return FragmentBlankBinding.inflate(layoutInflater,container,false)
    }

```