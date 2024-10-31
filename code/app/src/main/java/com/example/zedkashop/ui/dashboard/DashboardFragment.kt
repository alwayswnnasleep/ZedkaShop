package com.example.zedkashop.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zedkashop.R
import com.example.zedkashop.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.title = "Каталог"

        // Список элементов для отображения
        val items = listOf(
            CatalogItem("Шлем", R.drawable.helmet),
            CatalogItem("Бронежилет", R.drawable.armor),
            CatalogItem("Обувь", R.drawable.foot),
            CatalogItem("Одежда", R.drawable.wear),
            CatalogItem("Разгрузочная система", R.drawable.razgruz)
        )

        // Настройка RecyclerView
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = CatalogAdapter(items) { catalogItem ->
            // Создание Bundle для передачи выбранной категории
            val bundle = Bundle().apply {
                putString("category", catalogItem.name) // Передаем выбранную категорию
            }

            // Навигация к ProductCatalogFragment с передачей данных
            findNavController().navigate(R.id.action_navigation_dashboard_to_productCatalogFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}