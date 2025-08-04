package com.bachld.android

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bachld.android.databinding.ActivityMainBinding
import com.bachld.android.ui.doanfragment.DoAnFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_trang_chu, R.id.navigation_do_an, R.id.navigation_sinh_vien, R.id.navigation_hoi_dong, R.id.navigation_thong_tin
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnItemReselectedListener { item ->
            if (item.itemId == R.id.navigation_do_an) {
                // Lấy fragment DoAnFragment (tab đồ án)
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                val fragments = navHostFragment?.childFragmentManager?.fragments
                val doAnFragment = fragments?.find { it is DoAnFragment } as? DoAnFragment
                doAnFragment?.resetToThongTinDoAn()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed() // Quay lại fragment trước đó trong backstack
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}