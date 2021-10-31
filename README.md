本篇文章中使用到的接口来自[wanandroid](https://links.jianshu.com/go?to=https%3A%2F%2Fwanandroid.com%2Fblog%2Fshow%2F2)提供的公开接口。


### Model层，负责网络请求


网络请求返回结果通常有两种样式

样式1：data是一个JSONObject

```json
{
  "data": {},
  "errorCode": 0,
  "errorMsg": ""
}
```

样式2：data是一个JSONArray

```
{
  "data": [],
  "errorCode": 0,
  "errorMsg": ""
}
```
首先定义通用的响应类

```
class NetResponse<T> {
    var data: T? = null
    var errorMsg = ""
    var errorCode = 0
    fun success() = errorCode == 0
}
```

网络接口

```kotlin
/**
 * 网络服务接口(协程)
 */
interface ApiService {

    @GET("article/list/1/json")
    suspend fun getArticle(): NetResponse<Article>

    /**
     * 获取公众号列表
     */
    @GET("wxarticle/chapters/json")
    suspend fun getWXArticleList(): NetResponse<List<WxArticleBean>>
}
```

数据仓库：调用网络接口发起网络请求

```
object Repository {


    suspend fun getArticle(): Article? {
        return NetworkService.api.getArticle().let { processData(it) }
    }

    suspend fun getWXArticleList(): List<WxArticleBean>? =
            NetworkService.api.getWXArticleList().let {
                processData(it)
            }


    //统一处理NetResponse
    private fun <T> processData(netResponse: NetResponse<T>): T? {
        if (netResponse.success()) {
            return netResponse.data
        } else {
            throw ApiException(netResponse.ret, netResponse.msg ?: "未知错误")
        }
    }
}
```

### ViewModel层

VM层响应View层的请求，调用M层数据仓库的方法发起网络请求，并将请求结果以LiveData的方式暴露给View层。

给ViewModel类添加一个扩展方法，用来请求网络。

```kotlin
fun ViewModel.launch(
        block: suspend CoroutineScope.() -> Unit,
        onError: (e: Throwable) -> Unit = {},
        onComplete: () -> Unit = {}
) {
    viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                run {
                    //注释1处，这里统一处理错误，比如在这里弹出登录界面
                    Log.i("ViewModel.launch", "launch: 统一处理异常 ${throwable.message}")
                    ExceptionUtil.catchException(throwable)
                    onError(throwable)

                }
            }
    ) {
        try {
            block.invoke(this)
        } finally {
            onComplete()
        }
    }
}
```

注释1处，这里使用`CoroutineExceptionHandler`统一处理错误，比如在这里弹出登录界面。ExceptionUtil也会对异常进行预处理。最后将异常回调给onError方法。

注意：这里感觉`ExceptionUtil.catchException`方法预处理异常后，应该返回一个处理过的异常回调给onError方法。



ViewModel基类

```kotlin
abstract class BaseViewModel : ViewModel() {
    // 加载状态
    val loadState = MutableLiveData<LoadState>()
}
```

ViewModel基类处理加载状态，也是以LiveData的方式暴露数据给View层。

具体的ViewModel类

```kotlin

class MainActivityVM : BaseViewModel() {

    private val TAG: String = "MainActivityVM"

    //注释1处，以LiveData的方式暴露数据给View层。
    val data = MutableLiveData<Article>()
    val listWxArticle: MutableLiveData<List<WxArticleBean>> = MutableLiveData()
    val zipData = MutableLiveData<ZipData>()

    /**
     * 注释2处，单个请求，data是一个 json object
     */
    fun getData() = launch({
        loadState.value = LoadState.Loading()
        data.value = Repository.getArticle()
        loadState.value = LoadState.Success()
    }, {
        loadState.value = LoadState.Fail()
    })

    /**
     * 注释3处，单个请求，data是一个 json Array
     */
    fun getDataList() = launch(
            {
                loadState.value = LoadState.Loading()
                listWxArticle.value = Repository.getWXArticleList()
                loadState.value = LoadState.Success()
            },
            {
                loadState.value = LoadState.Fail()
            }
    )


     /**
      * 注释4处，合并两个请求
      */

    fun getDataTogether() = launch(
            {
                loadState.value = LoadState.Loading()

                //注释5处
                val article1Deferred = async { Repository.getArticle() }
                val article2Deferred = async { Repository.getWXArticleList() }

                val article: Article? = article1Deferred.await()
                val articleList: List<WxArticleBean>? = article2Deferred.await()

                val zipResult = ZipData(article, articleList)

                zipData.value = zipResult
                loadState.value = LoadState.Success()
            },
            {
                loadState.value = LoadState.Fail()
            }
    )
    
}
```

注释1处，以LiveData的方式暴露数据给View层。
注释2处，单个请求，data是一个 JsonObject。
注释3处，单个请求，data是一个 JsonArray。
注释4处，合并两个请求。

注释5处，注意一下，一定要先启动两个asyn协程，再去await。这样才是并发的。如果像下面这种写法，就是顺序请求了，不是并发。

```kotlin
val article1Deferred = async { Repository.getArticle() }
val article: Article? = article1Deferred.await()

val article2Deferred = async { Repository.getWXArticleList() }
val articleList: List<WxArticleBean>? = article2Deferred.await()

```
这种写法，第一个协程任务结束之后才会启动第二个协程任务，这个就是顺序的了。

### View层

View层响应用户操作，发起网络请求。


```kotlin
class MainActivity : AppCompatActivity() {

    private val TAG: String = "MainActivity"

    private lateinit var mViewModel: MainActivityVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 实例化ViewModel
        mViewModel = ViewModelProviders.of(this).get(MainActivityVM::class.java)

        // 观察数据
        //loading状态
        mViewModel.loadState.observe(this, Observer { changeLoadState(it) })
        mViewModel.data.observe(this, Observer { showData(it) })
        mViewModel.listWxArticle.observe(this, Observer { showListData(it) })
        mViewModel.zipData.observe(this, Observer { showZipData(it) })

        // 获取数据
        btGetData.setOnClickListener { mViewModel.getData() }
        btGetDataList.setOnClickListener { mViewModel.getDataList() }
        btZipData.setOnClickListener { mViewModel.getDataTogether() }
    }

    /**
     * 加载状态变化
     */
    private fun changeLoadState(loadState: LoadState) {
        pbProgress.visibility = when (loadState) {
            is LoadState.Loading -> {
                tvData.text = ""
                View.VISIBLE
            }
            else -> View.GONE
        }
    }
    
    /**
     * 显示数据
     */
    private fun showData(data: Article) {
        Log.i(TAG, "showData: $data")
        tvData.text = "第0个数据，${data.datas?.get(0)}"
    }

    private fun showListData(data: List<WxArticleBean>?) {
        Log.i(TAG, "showData: $data")
        tvData.text = "第0个数据，${data?.get(0)}"
    }

    /**
     * 显示数据
     */
    private fun showZipData(data: ZipData) {
        Log.i(TAG, "showZipData: $data")
        tvData.text = "第0个数据，${data.article?.datas?.get(0)}\n，第一个数据${data.articleList?.get(0)}"
    }
}
```

参考链接

* [Android:玩转网络请求架构 Retrofit+Kotlin协程简单使用(MVVM架构模式)](https://blog.csdn.net/sange77/article/details/103959389)
