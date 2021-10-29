
###ApiService

ApiService层Retrofit的请求接口层

### Repository 层

调用 ApiService层Retrofit的请求接口层 的方法，请求数据，并把统一格式数据NetResponse转换为具体类型数据，如果NetResponse ret不为0，抛出异常。


### ViewModel层

调用Repository 层的方法，获取具体数据，捕获异常，并统一处理异常，比如弹出登录弹窗等等。

```kotlin
fun ViewModel.launch(
        block: suspend CoroutineScope.() -> Unit,
        onError: (e: Throwable) -> Unit = {},
        onComplete: () -> Unit = {}
) {
    viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                run {
                    //注释1处，这里统一处理错误，比如弹出登录弹窗等等
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

### View 层


View 层调用ViewModel层的方法即可。
