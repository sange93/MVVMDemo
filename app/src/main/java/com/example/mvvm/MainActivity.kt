package com.example.mvvm

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mvvm.bean.ArticleData
import com.example.mvvm.bean.LoadState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mViewModel: MainActivityVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 实例化ViewModel
        mViewModel = ViewModelProviders.of(this).get(MainActivityVM::class.java)
        // 观察数据
        mViewModel.data.observe(this, Observer { showData(it) })
        mViewModel.loadState.observe(this, Observer { changeLoadState(it) })
        // 获取数据
        btGetData.setOnClickListener { mViewModel.getData(this) }
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
    private fun showData(data: ArticleData) {
        if (data.data.isEmpty()) return
        tvData.text = "id: ${data.data[0].id} name ${data.data[0].name}"
    }
}
