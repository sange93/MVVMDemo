package com.example.mvvm

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mvvm.bean.Article
import com.example.mvvm.bean.LoadState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG: String = "MainActivity"

    private lateinit var mViewModel: MainActivityVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 实例化ViewModel
        mViewModel = ViewModelProviders.of(this).get(MainActivityVM::class.java)
        // 观察数据
        mViewModel.article.observe(this, Observer { showData(it) })
        mViewModel.loadState.observe(this, Observer { changeLoadState(it) })
        // 获取数据
        btGetData.setOnClickListener { mViewModel.getDataTogether() }
    }

    /**
     * 加载状态变化
     */
    private fun changeLoadState(loadState: LoadState) {
        pbProgress.visibility = when (loadState) {
            is LoadState.Loading -> {
                tvData.text = ""
                View.VISIBLE
            }
            else -> View.GONE
        }
    }

    /**
     * 显示数据
     */
    private fun showData(data: Article) {
        Log.i(TAG, "showData: $data")
        tvData.text = "第0个数据，${data.datas?.get(0)}"
    }
}
