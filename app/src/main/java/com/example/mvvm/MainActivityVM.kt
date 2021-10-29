package com.example.mvvm

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mvvm.base.BaseViewModel
import com.example.mvvm.bean.Article
import com.example.mvvm.bean.ArticleData
import com.example.mvvm.bean.LoadState
import com.example.mvvm.common.launch
import com.example.mvvm.net.Repository
import kotlinx.coroutines.async

class MainActivityVM : BaseViewModel() {

    private val TAG: String = "MainActivityVM"

    val data = MutableLiveData<ArticleData>()
    val article = MutableLiveData<Article>()

    fun getData(context: Context?) = launch({
        loadState.value = LoadState.Loading()
        article.value = Repository.getArticle()
        loadState.value = LoadState.Success()
    }, {
        loadState.value = LoadState.Fail()
    })


    fun getDataTogether() = launch({
        loadState.value = LoadState.Loading()

        val current = System.currentTimeMillis()
        Log.i(TAG, "getDataTogether: 开始并发请求两个接口 $current")
        val article1Deferred = async {

            val current1 = System.currentTimeMillis()

            Log.i(TAG, "getDataTogether: 开始请求article1：${current1}")
            val deferred = Repository.getArticle()

            Log.i(TAG, "getDataTogether: 请求article1结束，耗时：${System.currentTimeMillis() - current1}毫秒")

            return@async deferred

        }
        val article2Deferred = async {
            val current2 = System.currentTimeMillis()
            Log.i(TAG, "getDataTogether: 开始请求article2：${current2}")
            val deferred = Repository.getArticleTwo()

            Log.i(TAG, "getDataTogether: 请求article2结束，耗时：${System.currentTimeMillis() - current2}毫秒")
            return@async deferred
        }

        val article2: Article? = try {
            article2Deferred.await()
        } catch (e: Exception) {
            Log.i(TAG, "getDataTogether: article2 抛出异常 ${e.message}")
            throw e
        }

        val article1: Article? = try {
            article1Deferred.await()
        } catch (e: Exception) {
            Log.i(TAG, "getDataTogether: article1 抛出异常")
            throw e
        }
        val zipResult = Article(2, article1?.datas ?: article2?.datas, 0, false, 0, 100, 200)

        article.value = zipResult

        Log.i(TAG, "getDataTogether: 并发请求两个接口结束，耗时：${System.currentTimeMillis() - current}毫秒")

        loadState.value = LoadState.Success()
    }, {
        loadState.value = LoadState.Fail()
    })
}