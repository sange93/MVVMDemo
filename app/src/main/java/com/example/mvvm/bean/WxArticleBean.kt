package com.example.mvvm.bean

/**
 * Created by dumingwei on 2021/10/31
 *
 * Desc:
 */
data class WxArticleBean(
        var courseId: Int = 0,
        var id: Int = 0,
        var name: String? = null,
        var order: Int = 0,
        var parentChapterId: Int = 0,
        var isUserControlSetTop: Boolean = false,
        var visible: Int = 0
)