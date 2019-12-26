package com.example.mvvm.net

import com.example.mvvm.MyApplication
import com.example.mvvm.common.LogUtil
import com.example.mvvm.common.gson.MGson
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 接口请求工厂
 * @author ssq
 */
object ApiFactory {
    // 日志拦截器
    private val mLoggingInterceptor: Interceptor by lazy { LoggingInterceptor() }
    // OkHttpClient客户端
    private val mClient: OkHttpClient by lazy { newClient() }

    /**
     * 创建API Service接口实例
     */
    fun <T> createService(baseUrl: String, clazz: Class<T>): T =
        Retrofit.Builder().baseUrl(baseUrl).client(mClient)
            .addConverterFactory(GsonConverterFactory.create(MGson.getInstance()))
            .build().create(clazz)

    /**
     * OkHttpClient客户端
     */
    private fun newClient(): OkHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(30, TimeUnit.SECONDS)// 连接时间：30s超时
        readTimeout(10, TimeUnit.SECONDS)// 读取时间：10s超时
        writeTimeout(10, TimeUnit.SECONDS)// 写入时间：10s超时
        if (MyApplication.isDebugMode) addInterceptor(mLoggingInterceptor)// 仅debug模式启用日志过滤器
    }.build()

    /**
     * 日志拦截器
     */
    private class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder = StringBuilder()
            val startTime = System.nanoTime()
            val response: Response = with(chain.request()) {
                builder.append(method + "\n")
                builder.append("Sending request\n$url")
                if (method == "POST") {
                    builder.append("?")
                    when (val body = body) {
                        is FormBody -> {
                            for (j in 0 until body.size) {
                                builder.append(body.name(j) + "=" + body.value(j))
                                if (j != body.size - 1) {
                                    builder.append("&")
                                }
                            }
                        }
//                        is MultipartBody -> {}
                    }
                }
                builder.append("\n").append(headers)
                LogUtil.v(builder.toString())
                chain.proceed(this)
            }
            builder.clear()
            builder.append("Received response in " + (System.nanoTime() - startTime) / 1e6 + "ms\n")
            builder.append("code" + response.code + "\n")
            LogUtil.v(builder.toString())
            return response
        }
    }
}