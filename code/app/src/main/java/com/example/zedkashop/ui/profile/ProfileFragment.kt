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

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Инициализация SharedPreferences
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

        // Инициализация кнопки и установка слушателя нажатий
        val openDialogButton: Button = view.findViewById(R.id.addproductbutton)
        openDialogButton.setOnClickListener {
            showLicenseDialog()
        }

        // Обработка нажатия кнопки выхода
        view.findViewById<ImageView>(R.id.logOut).setOnClickListener {
            // Выход из аккаунта
            viewModel.signOut()
            with(sharedPreferences.edit()) {
                remove("isLoggedIn")
                apply()
            }
            view.findNavController().navigate(R.id.action_navigation_profile_to_auth_navigation)
        }

        return view
    }

    private fun showLicenseDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_license, null)
        val licenseInput: EditText = dialogView.findViewById(R.id.licenseInput)

        builder.setView(dialogView)
            .setTitle("Введите лицензию")
            .setPositiveButton("OK") { dialog, _ ->
                val license = licenseInput.text.toString()
                // Проверка лицензии
                if (license == "lowe321") {
                    // Если лицензия верна, переходим на AddProductFragment
                    view?.findNavController()?.navigate(R.id.action_navigation_profile_to_addProductFragment)
                } else {
                    // Вывод сообщения об ошибке
                    Toast.makeText(requireContext(), "Неверная лицензия", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}