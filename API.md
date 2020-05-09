#### MyActivity

> 所有的 Activity 必须继承至 MyActivity，如果使用 MVP 请继承至 MvpActivity，已经处理了 Activity 多重启动的问题，[详情可点击此处查看](https://www.jianshu.com/p/579f1f118161)

> 获取 Context 或者 Activity

	public Context getContext()

    public <A extends BaseActivity> A getActivity()

> startActivity 方法优化

    public void startActivity(Class<? extends Activity> clazz)

> startActivityForResult 方法优化

    public void startActivityForResult(Intent intent, ActivityCallback callback)

    public void startActivityForResult(Intent intent, @Nullable Bundle options, ActivityCallback callback)

> setResult 方法优化

    public void finishResult(int resultCode)

    public void finishResult(int resultCode, Intent data)

> Activity 标题

    public void setTitle(int titleId) 

    public void setTitle(CharSequence title)

	public CharSequence getTitle()

> Toast 方法

    public void toast(CharSequence s)

    public void toast(int id)

    public void toast(Object object)

> Handler 方法

	public static Handler getHandler()

> TitleBar 方法

	public TitleBar getTitleBar()

> TitleBar 监听方法（需要被重写）

	// 标题栏左项被点击了，默认返回
    public void onLeftClick(View v)

	// 标题栏中间项被点击了
    public void onTitleClick(View v)

	// 标题栏右项被点击了
    public void onRightClick(View v)

#### MyFragment

> 获取Activity，防止出现 getActivity() 为空

    public FragmentActivity getFragmentActivity()

> 是否进行了懒加载

    protected boolean isLazyLoad()

> 当前 Fragment 是否可见

    public boolean isFragmentVisible()

> 跟 Activity 的同名方法效果一样

    protected void onRestart()

> 根据资源 id 获取一个 View 对象

    protected <T extends View> T findViewById(@IdRes int id)

    protected <T extends View> T findActivityViewById(@IdRes int id)

> 跳转到其他Activity

    public void startActivity(Class<? extends Activity> clazz)

> 销毁当前 Fragment 所在的 Activity

    public void finish()

> 获取系统服务

    public Object getSystemService(@NonNull String name)

> Fragment返回键被按下时回调（只做预留方法，没有效果）

    public boolean onKeyDown(int keyCode, KeyEvent event)

> Toast 方法

    public void toast(CharSequence s)

    public void toast(int id)

    public void toast(Object object)

##### MyRecyclerViewAdapter

> 获取 RecyclerView 或者 Context

	public RecyclerView getRecyclerView()
	
	public Context getContext()

> 布局摆放器（可以被重载，由于 RecyclerView 不能没有设置 LayoutManager，这里设置了默认的）

    protected RecyclerView.LayoutManager getDefaultLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

> 分页逻辑预留方法

    public int getPageNumber()

    public void setPageNumber(int pageNumber)

    public boolean isLastPage()

    public void setLastPage(boolean lastPage)

> 标记方法

    public Object getTag()

    public void setTag(Object tag)

> 操作数据集合

	public void setData(List<T> data)
	
	public List<T> getData()
	
	public void addData(List<T> data)
	
	public void clearData()

> 操作单个数据

	public T getItem(int position)

	public void setItem(int position, T item)

	public void addItem(T item)

	public void addItem(int position, T item)

	public void removeItem(T item)

	public void removeItem(int position)

> MyRecyclerViewAdapter.ViewHolder 方法

	public final View  getItemView()

	public final <V extends View> V findViewById(@IdRes int id)

	public final ViewHolder setText(@IdRes int id, @StringRes int id)

	public final ViewHolder setText(@IdRes int id, String text)
	
	public final ViewHolder setVisibility(@IdRes int id, int visibility)
	
	public final ViewHolder setColor(@IdRes int id, @ColorInt int color)
	
	public final ViewHolder setImage(@IdRes int id, @DrawableRes int drawableId)

> 监听方法（必须在 RecyclerView.setAdapter 之前调用）

	public void setOnItemClickListener(OnItemClickListener listener)

	public void setOnChildClickListener(@IdRes int id, OnChildClickListener listener)

	public void setOnItemLongClickListener(OnItemLongClickListener listener)
	
	public void setOnChildLongClickListener(@IdRes int id, OnChildLongClickListener listener)

	public void setOnScrollingListener(OnScrollingListener listener)

> MyListViewAdapter 和 MyRecyclerViewAdapter 差不多，只不过没有上面这些监听方法，因为 ListView 本身已经自带这些了

