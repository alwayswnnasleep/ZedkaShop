package com.example.zedkashop.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.zedkashop.R

class ProfileFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: ProfileViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        // Установка заголовка тулбара
        (activity as AppCompatActivity).supportActionBar?.title = "Профиль" // или другой заголовок
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            view.post {
                view.findNavController().navigate(R.id.action_navigation_profile_to_auth_navigation)
            }
            return view
        }

        val userEmail = sharedPreferences.getString("userEmail", "Нет email")
        val userEmailTextView: TextView = view.findViewById(R.id.userEmail)
        userEmailTextView.text = userEmail

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
            view.findNavController().navigate(R.id.action_navigation_profile_to_viewPurchaseHistoryFragment)
        }

        btnViewHistory.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_viewHistoryFragment)
        }

        return view
    }
}