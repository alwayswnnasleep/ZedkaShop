package com.example.zedkashop.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zedkashop.databinding.FragmentCartBinding
import com.example.zedkashop.ui.base.BaseFragment


class CartFragment : BaseFragment() {

    private var _binding: FragmentCartBinding? = null


    private val binding get() = _binding!!
    override fun onResume() {
        super.onResume()
        setActionBarTitle("Корзина")
    }

}