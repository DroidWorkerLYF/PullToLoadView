# PullToLoadView
##配置
```
compile 'com.droidworker:pulltoloadview:0.9.5'
```

##Why
下拉刷新的控件已经很多了，本没必要再造轮子，然而产品提了一个需求，感觉[Android-PullToRefresh](https://github.com/chrisbanes/Android-PullToRefresh)实现不便(而且被公司内部公共组件改的乱七八糟的)，所以试着再造个轮子。。。主要是实现underbar里的效果。当然很多都参考了**Android-PullToRefresh**，图用mindnote做的，没想画类图。。。
![尝试用mindnote,待更新](https://github.com/DroidWorkerLYF/PullToLoadView/blob/master/PullToLoadView.mindnode/QuickLook/Preview.jpg?raw=true)

##分析
如果使用**Android-PullToRefresh**:  

1. 正常使用场景，内容区域在Toolbar下方，不可能滚动到Toolbar的位置
2. 如果通过层次让Toolbar盖到内容上方，则需要设置padding，将内容挤下来，但是下拉刷新的header和内容的就会有较大的间隔。 
 
综上，还是决定写一个新的，虽然并不是什么很厉害炫酷的效果。

##演示gif

![下拉刷新](https://github.com/DroidWorkerLYF/PullToLoadView/blob/master/art/loadnew.gif?raw=true)

**underbar**  

![underbar](https://github.com/DroidWorkerLYF/PullToLoadView/blob/master/art/underbar.gif?raw=true)

demo里有完整的演示

##支持的模式  
* 下拉加载更新
* 下拉加载更新并且自动加载更多
* 上拉加载更多
* 上拉加载更多下拉加载更新
* 禁止
* 自动刷新

![模式](https://github.com/DroidWorkerLYF/PullToLoadView/blob/master/art/loadmode.png?raw=true)

##额外的
* 可以指定header，content，footer的layout resource id
* 可以自定义reset时，回滚时间
* 开始支持NestedScroll，目前PullToLoad的RecyclerView现在可以支持内容和下拉上拉的无缝切换

