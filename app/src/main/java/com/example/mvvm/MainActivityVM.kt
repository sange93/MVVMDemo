package com.example.mvvm

import androidx.lifecycle.MutableLiveData
import com.example.mvvm.base.BaseViewModel
import com.example.mvvm.bean.Article
import com.example.mvvm.bean.LoadState
import com.example.mvvm.bean.WxArticleBean
import com.example.mvvm.bean.ZipData
import com.example.mvvm.common.launch
import com.example.mvvm.net.Repository
import kotlinx.coroutines.async

/**
 * Created by dumingwei on 2021/10/31
 *
 * Desc: 具体的ViewModel类
 */
class MainActivityVM : BaseViewModel() {

    private val TAG: String = "MainActivityVM"

    val data = MutableLiveData<Article>()

    val listWxArticle: MutableLiveData<List<WxArticleBean>> = MutableLiveData()

    val zipData = MutableLiveData<ZipData>()


    /**
     * 单个请求，data是一个 json object
     */
    fun getData() = launch({
        loadState.value = LoadState.Loading()
        data.value = Repository.getArticle()
        loadState.value = LoadState.Success()
    }, {
        loadState.value = LoadState.Fail()
    })

    /**
     * 单个请求，data是一个 json Array
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
     * 合并两个请求
     */
    fun getDataTogether() = launch(
            {
                loadState.value = LoadState.Loading()

                //注释
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