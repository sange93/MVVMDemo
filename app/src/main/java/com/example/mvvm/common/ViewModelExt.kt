package com.example.mvvm.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvm.base.ErrorResponse
import com.example.mvvm.base.NetResponse
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

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
                    Log.i("ViewModel.launch", "launch: 统一处理异常 ${throwable.message}")
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


fun <T> ViewModel.launch(
        block: suspend CoroutineScope.() -> NetResponse<T>?,
        onSuccess: ((data: T?) -> Unit)? = null,
        onError: ((coroutineContext: CoroutineContext, e: ErrorResponse) -> Unit)? = null
) {
    viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
        //todo 处理未知错误
        onError?.invoke(coroutineContext, ErrorResponse(-1, throwable.message, throwable))
    }) {
        val response: NetResponse<T>? = try {
            block()
        } catch (e: Exception) {
            //构造一个错误的NetResponse
            NetResponse(-1, e.message, null, e)
        }

        response?.let {
            if (it.success()) {
                onSuccess?.invoke(it.data)
            } else {
                if (it.ret == -10086) {
                    //todo 统一错误处理，比如弹出登录框
                    ToastUtils.showShort("网络请求出错，${it.msg}")
                    //CustomToast.showFailToast("弹出登录弹窗")
                }
                onError?.invoke(coroutineContext, ErrorResponse(it.ret, it.msg, it.throwable))
            }
        }
    }
}


fun <T, K> ViewModel.launch(
        tBlock: suspend CoroutineScope.() -> NetResponse<T>?,
        kBlock: suspend CoroutineScope.() -> NetResponse<K>?,
        onSuccess: ((tData: T?, kData: K?) -> Unit)? = null,
        onError: ((coroutineContext: CoroutineContext, e: ErrorResponse) -> Unit)? = null
) {
    viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
        //todo 处理未知错误
        onError?.invoke(coroutineContext, ErrorResponse(-1, throwable.message, throwable))
    }) {

        val tResponseDeferred: Deferred<NetResponse<out T>?> = async {
            Log.i("launch", "开始第一个请求")
            val deferred = tBlock()
            Log.i("launch", "第一个请求结束")
            return@async deferred
        }
        val kResponseDeferred: Deferred<NetResponse<out K>?> = async {
            Log.i("launch", "开始第2个请求")
            val deferred = kBlock()
            Log.i("launch", "第2个请求结束")
            return@async deferred
        }

        val tResponse: NetResponse<out T>? = try {
            tResponseDeferred.await()
        } catch (e: Exception) {
            NetResponse(-1, e.message, null, e)

        }

        val kResponse: NetResponse<out K>? = try {
            kResponseDeferred.await()
        } catch (e: Exception) {
            NetResponse(-1, e.message, null, e)
        }

        Log.i("launch", "开始合并两个请求")

        if (tResponse != null && kResponse != null && tResponse.success() && kResponse.success()) {
            onSuccess?.invoke(tResponse.data, kResponse.data)
        } else {
            if (tResponse == null && kResponse == null) {
                onError?.invoke(coroutineContext, ErrorResponse(-1, "合并请求失败，两个响应都为null"))
            } else if (tResponse != null) {
                if (tResponse.success()) {
                    onError?.invoke(coroutineContext, ErrorResponse(-1, "合并请求失败，kBlock请求结果为null"))
                } else {
                    if (tResponse.ret == 10086) {
                        ToastUtils.showShort("统一处理网络请求出错，${tResponse.msg}")
                    }
                    onError?.invoke(coroutineContext, ErrorResponse(-1, "合并请求失败，tBlock请求失败，kBlock请求结果为null"))
                }
            } else if (kResponse != null) {
                if (kResponse.success()) {
                    onError?.invoke(coroutineContext, ErrorResponse(-1, "合并请求失败，tBlock请求结果为null"))
                } else {
                    if (kResponse.ret == 10086) {
                        ToastUtils.showShort("统一处理网络请求出错，${kResponse.msg}")
                    }
                    onError?.invoke(coroutineContext, ErrorResponse(-1, "合并请求失败，kBlock请求失败，tBlock请求结果为null"))
                }
            }
        }
    }

}