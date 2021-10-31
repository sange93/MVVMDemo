package com.example.mvvm.net

import com.example.mvvm.base.NetResponse
import com.example.mvvm.bean.Article
import com.example.mvvm.bean.WxArticleBean
import retrofit2.http.GET

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