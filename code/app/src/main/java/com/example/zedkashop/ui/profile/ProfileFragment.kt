package com.example.zedkashop.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.zedkashop.R
import com.example.zedkashop.ui.base.BaseFragment

class ProfileFragment : BaseFragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Инициализация ViewModel
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Проверка состояния авторизации
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            // Если пользователь не авторизован, переходим в AuthFragment
            view.post {
                view.findNavController().navigate(R.id.action_navigation_profile_to_auth_navigation)
            }
            return view
        }

        // Извлечение email пользователя из SharedPreferences
        val userEmail = sharedPreferences.getString("userEmail", "Нет email")
        val userEmailTextView: TextView = view.findViewById(R.id.userEmail)
        userEmailTextView.text = userEmail // Установка email в TextView

        // Обработка нажатия кнопки выхода
        view.findViewById<ImageView>(R.id.logOut).setOnClickListener {
            viewModel.signOut()
            with(sharedPreferences.edit()) {
                remove("isLoggedIn")
                apply()
            }
            view.findNavController().navigate(R.id.action_navigation_profile_to_auth_navigation)
        }

        val btnPurchaseHistory: Button = view.findViewById(R.id.btnPurchaseHistory)
        val btnViewHistory: Button = view.findViewById(R.id.btnViewHistory)

        btnPurchaseHistory.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_purchaseHistoryFragment)
        }

        btnViewHistory.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_viewHistoryFragment)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        setActionBarTitle("Профиль")
    }
}