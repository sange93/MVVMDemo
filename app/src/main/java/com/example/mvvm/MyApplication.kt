package com.example.mvvm

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.multidex.MultiDexApplication

/**
 * 基础Application
 * @author ssq
 */
class MyApplication : MultiDexApplication() {
    companion object {
        // app实例
        lateinit var instance: MyApplication
        // 是否为debug模式
        var isDebugMode: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        //处理Application的onCreate多次调用问题,通过进程的名称来区分执行哪些具体逻辑。
        if ("com.example.mvvm" == getProcessName(this, android.os.Process.myPid())) {
            initDebug()
        }
    }

    /**
     * 获取是否debug版本
     */
    private fun initDebug() {
        isDebugMode = instance.applicationInfo != null && instance.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    /**
     * 获取进程名
     */
    private fun getProcessName(cxt: Context, pid: Int): String {
        //获取ActivityManager对象
        val am = cxt.getSystemService(Context.ACTIVITY_SERVICE)
        if (am is ActivityManager) {
            //在运行的进程
            val runningApps = am.runningAppProcesses
            for (processInfo in runningApps) {
                if (processInfo.pid == pid) {
                    return processInfo.processName
                }
            }
        }
        return ""
    }
}