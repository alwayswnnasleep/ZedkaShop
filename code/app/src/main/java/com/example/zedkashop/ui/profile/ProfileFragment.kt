package com.example.zedkashop.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.zedkashop.R

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Инициализация ViewModel
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Проверка состояния авторизации после создания view
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            // Если пользователь не авторизован, переходим в AuthFragment
            view.post {
                view.findNavController().navigate(R.id.action_navigation_profile_to_navigation)
            }
            return view
        }

        view.findViewById<ImageView>(R.id.profilePhoto)
        view.findViewById<ImageView>(R.id.logOut).setOnClickListener {
            // Выход из аккаунта
            viewModel.signOut()


            with(sharedPreferences.edit()) {
                remove("isLoggedIn")

                apply()
            }


            view.findNavController().navigate(R.id.action_navigation_profile_to_navigation)
        }

        return view
    }
}