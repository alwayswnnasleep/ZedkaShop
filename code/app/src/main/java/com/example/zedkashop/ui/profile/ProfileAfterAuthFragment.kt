package com.example.zedkashop.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.zedkashop.R
import com.example.zedkashop.databinding.FragmentProfileBinding

import com.example.zedkashop.ui.login.RegViewModel

class ProfileAfterAuthFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view = inflater.inflate(R.layout.fragment_profileafterauth,container,false)
        view.findViewById<ImageView>(R.id.profilePhoto)
        view.findViewById<ImageView>(R.id.logOut).setOnClickListener(){
            viewModel.signOut()
        }
        return view
    }


}