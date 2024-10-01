package com.example.zedkashop.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.zedkashop.R
import com.example.zedkashop.databinding.FragmentAuthBinding
import com.example.zedkashop.ui.login.AuthViewModel
import com.example.zedkashop.ui.login.RegViewModel
import org.w3c.dom.Text

class RegFragment : Fragment() {
    private lateinit var viewModel: RegViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(RegViewModel::class.java)
        var view = inflater.inflate(R.layout.fragment_reg,container,false)
        view.findViewById<EditText>(R.id.enterPasswordReg)
        view.findViewById<EditText>(R.id.enterNameReg)
        view.findViewById<EditText>(R.id.enterEmailReg)
        view.findViewById<Button>(R.id.regButton).setOnClickListener {
            val email = view.findViewById<android.widget.EditText>(com.example.zedkashop.R.id.enterEmailReg).text.toString().trim()
            val password = view.findViewById<android.widget.EditText>(com.example.zedkashop.R.id.enterPasswordReg).text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Вызов метода signUp из ViewModel
                viewModel.signUp(email, password)
            } else {
                android.widget.Toast.makeText(context, "Пожалуйста, введите email и пароль", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
        view.findViewById<TextView>(R.id.regText)
        view.findViewById<TextView>(R.id.haveAccount).setOnClickListener{
            view.findNavController().navigate(R.id.action_navigation_reg_to_authFragment22)
        }
        view.findViewById<ImageView>(R.id.ArrowBack).setOnClickListener(){
            view.findNavController().navigate(R.id.action_global_navigation_profile)
        }
        return view


    }


}