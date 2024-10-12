package com.example.zedkashop.ui.login

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

class AuthFragment : BaseFragment() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Проверяем состояние авторизации при создании фрагмента
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            // Если пользователь уже авторизован, переходим на профиль
            view?.findNavController()?.navigate(R.id.authFragment2)
        }
    }
    override fun onResume() {
        super.onResume()
        setActionBarTitle("Вход в аккаунт")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        val emailEditText = view.findViewById<EditText>(R.id.enterEmailAuth)
        val passwordEditText = view.findViewById<EditText>(R.id.enterPasswordAuth)
        val authButton = view.findViewById<Button>(R.id.authButton)
        val haveAccountTextView = view.findViewById<TextView>(R.id.haveAccount)
        val backButton = view.findViewById<ImageView>(R.id.ArrowBack)

        authButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Вызов метода входа с передачей email
                viewModel.signIn(email, password)
            } else {
                Toast.makeText(requireContext(), "Пожалуйста, введите email и пароль", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                val email = emailEditText.text.toString().trim() // Получаем email здесь
                // Сохраняем состояние авторизации
                with(sharedPreferences.edit()) {
                    putBoolean("isLoggedIn", true)
                    putString("userEmail", email) // Сохраняем email
                    apply()
                }
                view.findNavController().navigate(R.id.action_global_navigation_profile)
            } else {
                Toast.makeText(requireContext(), "Ошибка входа. Проверьте email и пароль.", Toast.LENGTH_SHORT).show()
            }
        }

        haveAccountTextView.setOnClickListener {
            view.findNavController().navigate(R.id.authFragment2)
        }

        backButton.setOnClickListener {
            view.findNavController().navigate(R.id.authFragment2)
        }

        return view
    }

    // Метод для выхода из приложения и очистки состояния авторизации
    fun signOut() {
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            apply()
        }

    }
}