package com.example.zedkashop.ui.addingproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zedkashop.R

class AddProductFragment : Fragment() {
    private lateinit var viewModel: AddProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_add_product, container, false)


        val placePhoto: ImageView = view.findViewById(R.id.placePhoto)
        val enterName: EditText = view.findViewById(R.id.enterName)
        val enterPrice: EditText = view.findViewById(R.id.enterPrice)
        val enterDescription: EditText = view.findViewById(R.id.enterDescription)
        val chooseConsumer: Spinner = view.findViewById(R.id.chooseConsumer)


        return view
    }
}