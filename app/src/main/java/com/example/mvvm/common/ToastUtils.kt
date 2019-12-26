package com.example.mvvm.common

import android.widget.Toast
import com.example.mvvm.MyApplication

object ToastUtils {

    fun showShort(msg: String){
        Toast.makeText(MyApplication.instance,msg,Toast.LENGTH_SHORT).show()
    }
}