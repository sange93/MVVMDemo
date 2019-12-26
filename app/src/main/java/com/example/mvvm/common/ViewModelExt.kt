package com.example.mvvm.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvm.bean.ExceptionUtil
import com.example.mvvm.bean.LoadState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * ViewModel扩展方法：启动协程
 * @param block 协程逻辑
 * @param onError 错误回调方法
 * @param onComplete 完成回调方法
 */
fun ViewModel.launch(
    block: suspend CoroutineScope.() -> Unit,
    onError: (e: Throwable) -> Unit = {},
    onComplete: () -> Unit = {}
) {
    viewModelScope.launch(
        CoroutineExceptionHandler { _, throwable ->
            run {
                // 这里统一处理错误
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

/**
 * ViewModel扩展属性：加载状态
 */
val ViewModel.loadState: MutableLiveData<LoadState>
    get() = MutableLiveData()