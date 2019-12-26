package com.example.mvvm.net

import com.example.mvvm.bean.ArticleData
import com.example.mvvm.bean.BaseBean
import retrofit2.http.FieldMap
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * 网络服务接口(协程)
 * @author ssq
 * @JvmSuppressWildcards 用来注解类和方法，使得被标记元素的泛型参数不会被编译成通配符?
 */
@JvmSuppressWildcards
interface ApiService {

    /**
     * 通用异步请求 只需要解析BaseBean
     */
//    @FormUrlEncoded
    @POST("wxarticle/chapters")
    suspend fun request(@FieldMap map: Map<String, Any>): BaseBean

    /**
     * 获取公众号列表
     */
//    @FormUrlEncoded
    @GET("wxarticle/chapters/json")
    suspend fun getWXArticle(): ArticleData
}