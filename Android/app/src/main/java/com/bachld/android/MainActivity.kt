package com.bachld.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bachld.android.core.Session
import com.bachld.android.core.UserPrefs   // ✅ thêm: để lấy role từ cache
import com.bachld.android.databinding.ActivityMainBinding
import com.bachld.android.ui.view.doan.DoAnFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navView: BottomNavigationView = binding.navView

        Log.d("token", "token: ${Session.getTokenSync()}")

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_auth,
                R.id.navigation_trang_chu,
                R.id.navigation_do_an,
                R.id.navigation_hoi_dong,
                R.id.navigation_thong_tin
            )
        )

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                val loggedIn = Session.isLoggedIn() // suspend
                val graph = navController.navInflater.inflate(R.navigation.nav_root)
                graph.setStartDestination(if (loggedIn) R.id.nav_sinh_vien else R.id.nav_auth)
                navController.graph = graph

                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)

                val role = UserPrefs(this@MainActivity).getCached()?.role?.lowercase()
                Log.d("MainActivity", "Role: $role")
                val isGiangVien = role == "giang_vien" || role == "truong_bo_mon" || role == "tro_ly_khoa"
                if (loggedIn) {
                    applyRoleUI(isGiangVien)
                }
            }
        } else {
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }

        val topLevelTabsSV = setOf(
            R.id.navigation_trang_chu,
            R.id.navigation_do_an,
            R.id.navigation_hoi_dong,
            R.id.navigation_thong_tin
        )
        val topLevelTabsGV = setOf(
            R.id.gv_navigation_trang_chu,
            R.id.gv_navigation_de_tai,
            R.id.gv_navigation_thong_tin,
            R.id.gv_navigation_de_cuong
        )
        val homeChildrenSV = setOf(R.id.navigation_thong_bao_detail)
        val homeChildrenGV = setOf(R.id.navigation_thong_bao_detail)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navView.isVisible =
                destination.id in topLevelTabsSV ||
                        destination.id in topLevelTabsGV ||
                        destination.id in homeChildrenSV ||
                        destination.id in homeChildrenGV

            if (destination.id in homeChildrenSV) {
                binding.navView.menu.findItem(R.id.navigation_trang_chu)?.isChecked = true
            }
            if (destination.id in homeChildrenGV) {
                binding.navView.menu.findItem(R.id.gv_navigation_trang_chu)?.isChecked = true
            }

            if (destination.id == R.id.dangNhapFragment || destination.id == R.id.nav_auth) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }

        navView.setOnItemReselectedListener { item ->
            if (item.itemId == R.id.navigation_do_an) {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                val fragments = navHostFragment?.childFragmentManager?.fragments
                val doAnFragment = fragments?.find { it is DoAnFragment } as? DoAnFragment
                doAnFragment?.resetToThongTinDoAn()
            }
        }
    }

    fun applyRoleUI(isGiangVien: Boolean) {
        val navView = binding.navView
        val graph = navController.navInflater.inflate(R.navigation.nav_root)
        graph.setStartDestination(if (isGiangVien) R.id.nav_giang_vien else R.id.nav_sinh_vien)
        navController.setGraph(graph, null)

        val topLevelSV = setOf(
            R.id.navigation_trang_chu, R.id.navigation_do_an,
            R.id.navigation_hoi_dong, R.id.navigation_thong_tin
        )
        val topLevelGV = setOf(
            R.id.gv_navigation_trang_chu, R.id.gv_navigation_de_tai,
            R.id.gv_navigation_thong_tin
        )
        appBarConfiguration = AppBarConfiguration(if (isGiangVien) topLevelGV else topLevelSV)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.menu.clear()
        navView.inflateMenu(if (isGiangVien) R.menu.bottom_nav_giang_vien else R.menu.bottom_nav_menu)
        navView.setupWithNavController(navController)
    }

    // Bảo đảm nút Up trên ActionBar popBackStack đúng
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
