package com.example.mvvm

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvm.bean.ArticleData
import com.example.mvvm.bean.LoadState
import com.example.mvvm.common.launch
import com.example.mvvm.common.loadState
import com.example.mvvm.net.Repository

class MainActivityVM: ViewModel() {
    val data = MutableLiveData<ArticleData>()

    fun getData(context: Context?) = launch({
        loadState.value = LoadState.Loading()
        data.value = Repository.getWXArticle(context)
        loadState.value = LoadState.Success()
    }, {
        loadState.value = LoadState.Fail()
    })
}