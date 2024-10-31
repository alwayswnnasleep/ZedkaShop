package com.example.zedkashop.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.zedkashop.R
import com.example.zedkashop.ui.login.RegViewModel

class RegFragment : Fragment() {
    private lateinit var viewModel: RegViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        // Установка заголовка тулбара
        (activity as AppCompatActivity).supportActionBar?.title = "Регистрация" // или другой заголовок
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(RegViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_reg, container, false)

        val nameEditText = view.findViewById<EditText>(R.id.enterNameReg)
        val emailEditText = view.findViewById<EditText>(R.id.enterEmailReg)
        val passwordEditText = view.findViewById<EditText>(R.id.enterPasswordReg)
        val regButton = view.findViewById<Button>(R.id.regButton)
        val haveAccountText = view.findViewById<TextView>(R.id.haveAccount)
        val backButton = view.findViewById<ImageView>(R.id.ArrowBack)

        regButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Вызов метода signUp из ViewModel
                viewModel.signUp(name, email, password) { isSuccess, message ->
                    if (isSuccess) {
                        Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                        // Навигация или другие действия после успешной регистрации
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        haveAccountText.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_reg_to_authFragment2)
        }

        backButton.setOnClickListener {
            view.findNavController().navigate(R.id.navigation_profile)
        }

        return view
    }
}