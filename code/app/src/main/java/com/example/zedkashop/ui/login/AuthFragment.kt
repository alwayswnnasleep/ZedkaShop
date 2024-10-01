package com.example.zedkashop.ui.login

import android.os.Bundle
import android.util.Log
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

class AuthFragment : Fragment() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        view.findViewById<Button>(R.id.authButton).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.enterEmailAuth).text.toString().trim()
            val password = view.findViewById<EditText>(R.id.enterPasswordAuth).text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.signIn(email, password)
            } else {
                Toast.makeText(context, "Пожалуйста, введите email и пароль", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                // Переход на профиль
                view.findNavController().navigate(R.id.action_global_profileAfterAuthFragment)
            } else {
                Toast.makeText(context, "Ошибка входа. Проверьте email и пароль.", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<TextView>(R.id.haveAccount).setOnClickListener {
            view.findNavController().navigate(R.id.action_authFragment2_to_navigation_reg)
        }

        view.findViewById<ImageView>(R.id.ArrowBack).setOnClickListener {
            view.findNavController().navigate(R.id.action_global_navigation_profile)
        }

        return view
    }
}