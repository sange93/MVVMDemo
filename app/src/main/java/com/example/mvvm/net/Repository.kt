package com.example.mvvm.net

import com.example.mvvm.base.NetResponse
import com.example.mvvm.bean.Article
import com.example.mvvm.bean.WxArticleBean
import com.example.mvvm.common.ApiException

/**
 * 接口资源
 * @author ssq
 */
object Repository {


    suspend fun getArticle(): Article? {
        return NetworkService.api.getArticle().let { processData(it) }
    }

    suspend fun getWXArticleList(): List<WxArticleBean>? =
            NetworkService.api.getWXArticleList().let {
                processData(it)
            }


    private fun <T> processData(netResponse: NetResponse<T>): T? {
        if (netResponse.success()) {
            return netResponse.data
        } else {
            throw ApiException(netResponse.ret, netResponse.msg ?: "未知错误")
        }
    }

}