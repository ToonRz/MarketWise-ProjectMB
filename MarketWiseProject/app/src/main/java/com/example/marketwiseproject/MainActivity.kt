package com.example.marketwiseproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.example.marketwiseproject.databinding.ActivityMainBinding
import com.example.marketwiseproject.services.PriceAlertWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // ✅ แก้ตรงนี้ - ใช้ NavHostFragment แทน findNavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_crypto,
                R.id.navigation_stocks
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)

        setupPriceAlertWorker()
    }

    private fun setupPriceAlertWorker() {
        val workRequest = PeriodicWorkRequestBuilder<PriceAlertWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PriceAlert",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }


    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}