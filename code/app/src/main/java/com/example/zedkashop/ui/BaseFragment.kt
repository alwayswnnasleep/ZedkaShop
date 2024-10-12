package com.example.zedkashop.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    // Этот метод вызывается, когда фрагмент становится видимым
    override fun onResume() {
        super.onResume()
        showActionBar()
    }

    // Метод для показа ActionBar
    protected fun showActionBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    // Метод для скрытия ActionBar
    protected fun hideActionBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    // Метод для установки заголовка ActionBar
    protected fun setActionBarTitle(title: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = title
    }
}