package com.example.mvvm.bean

data class Article(
    var curPage: Int?, // 2
    var datas: List<Data>?,
    var offset: Int?, // 20
    var over: Boolean?, // false
    var pageCount: Int?, // 422
    var size: Int?, // 20
    var total: Int? // 8434
) {
    data class Data(
        var apkLink: String?,
        var audit: Int?, // 1
        var author: String?,
        var canEdit: Boolean?, // false
        var chapterId: Int?, // 267
        var chapterName: String?, // handler
        var collect: Boolean?, // false
        var courseId: Int?, // 13
        var desc: String?,
        var descMd: String?,
        var envelopePic: String?,
        var fresh: Boolean?, // false
        var id: Int?, // 13373
        var link: String?, // https://juejin.im/post/5d4e6af7e51d4561ba48fdb0
        var niceDate: String?, // 2020-05-12 00:27
        var niceShareDate: String?, // 2020-05-11 22:02
        var origin: String?,
        var prefix: String?,
        var projectLink: String?,
        var publishTime: Long?, // 1589214421000
        var selfVisible: Int?, // 0
        var shareDate: Long?, // 1589205762000
        var shareUser: String?, // wangzhengyi
        var superChapterId: Int?, // 10
        var superChapterName: String?, // 四大组件
        var tags: List<Tag?>?,
        var title: String?, // 揭秘 Android 同步消息屏障：target==null？
        var type: Int?, // 0
        var userId: Int?, // 38889
        var visible: Int?, // 1
        var zan: Int? // 0
    ) {
        data class Tag(
            var name: String?, // 项目
            var url: String? // /project/list/1?cid=402
        )
    }
}