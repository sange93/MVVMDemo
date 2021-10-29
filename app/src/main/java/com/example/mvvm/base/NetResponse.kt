package com.example.mvvm.base

/**
 * Created by dumingwei on 2021/10/29.
 *
 * Desc:
 */
data class NetResponse<T>(
        var ret: Int = 0,
        var msg: String? = "",
        var data: T? = null,
        var throwable: Throwable? = null
) {

    fun success(): Boolean {
        return ret == 0
    }

    fun failed():Boolean{
        return ret!=0
    }
}

data class ErrorResponse(
        var ret: Int = 0,
        var msg: String? = "",
        var throwable: Throwable? = null
)