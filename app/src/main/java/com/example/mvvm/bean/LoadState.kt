package com.example.mvvm.bean

/**
 * 加载状态
 * @author ssq
 * sealed 关键字表示此类仅内部继承
 */
sealed class LoadState(val msg: String) {
    /**
     * 加载中
     */
    class Loading(msg: String = ""): LoadState(msg)

    /**
     * 成功
     */
    class Success(msg: String = ""): LoadState(msg)

    /**
     * 失败
     */
    class Fail(msg: String = ""): LoadState(msg)
}