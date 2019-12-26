package com.example.mvvm.net

import android.content.Context
import com.example.mvvm.bean.ArticleData
import com.example.mvvm.bean.BaseBean
import com.example.mvvm.common.ApiException

/**
 * 接口资源
 * @author ssq
 */
object Repository {

    /**
     * 预处理数据(错误)
     * @param context 跳至登录页的上下文
     */
    private suspend fun <T : BaseBean> preprocessData(baseBean: T, context: Context? = null): T =
        if (baseBean.errorCode == 0) {// 成功
            // 返回数据
            baseBean
        } else {// 失败
            // 验证登录是否过期
//        validateCode(context, baseBean.code)
            // 抛出接口异常
            throw ApiException(baseBean.errorCode, baseBean.errorMsg)
        }

    /**
     * 获取我的信息（主页——我的页面）
     *
     * @param context 跳至登录页的上下文
     */
    suspend fun getWXArticle(context: Context? = null): ArticleData =
        NetworkService.api.getWXArticle().let {
            preprocessData(it, context)
        }
}