package com.example.mvvm.net

/**
 * 网络服务类
 * @author ssq
 */
object NetworkService {
    // 请求根地址
    private const val BASE_URL = "https://www.wanandroid.com/"
    // 接口API服务(挂起)
    val api by lazy { ApiFactory.createService(BASE_URL, ApiService::class.java) }
}