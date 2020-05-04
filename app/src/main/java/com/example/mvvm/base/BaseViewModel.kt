package com.example.mvvm.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvm.bean.LoadState

/**
 * ViewModel 基类
 * @author ssq
 */
abstract class BaseViewModel : ViewModel() {
    // 加载状态
    val loadState = MutableLiveData<LoadState>()
}