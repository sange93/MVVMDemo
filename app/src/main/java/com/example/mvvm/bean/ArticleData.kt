package com.example.mvvm.bean

/**
 * 公众号
 * @author ssq
 */
class ArticleData: BaseBean() {
//    data: [{children: [], courseId: 13, id: 408, name: "鸿洋", order: 190000, parentChapterId: 407,…},…]
//    errorCode: 0
//    errorMsg: ""
    var data = arrayListOf<Chapters>()
}

class Chapters{
//    children: []
//    courseId: 13
//    id: 408
//    name: "鸿洋"
//    order: 190000
//    parentChapterId: 407
//    userControlSetTop: false
//    visible: 1
    var courseId = ""
    var id = ""
    var name =  ""
    var order = 0
}