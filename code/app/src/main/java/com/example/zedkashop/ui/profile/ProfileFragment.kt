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
import androidx.navigation.findNavController
import com.example.zedkashop.R
import com.example.zedkashop.ui.base.BaseFragment

class ProfileFragment : BaseFragment() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Инициализация SharedPreferences и других компонентов
        // ...

        // Инициализация кнопок для навигации
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

    override fun onResume() {
        super.onResume()
        setActionBarTitle("Профиль")
    }
}