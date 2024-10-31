package com.example.zedkashop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.zedkashop.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ZedkaShop)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация тулбара
        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView

        // Получаем NavHostFragment и его NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Настройка NavController с BottomNavigationView
        navView.setupWithNavController(navController)

        // Настройка Action Bar с NavController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Слушатель навигации для управления отображением стрелки назад
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Показываем кнопку "Назад" только для определенных фрагментов
            when (destination.id) {
                R.id.productDetailFragment -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true) // Показываем стрелку назад
                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false) // Скрываем стрелку назад
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}