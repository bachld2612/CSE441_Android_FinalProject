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

        // Khai báo các destination top-level (các tab + auth)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_auth,
                R.id.navigation_trang_chu,
                R.id.navigation_do_an,
                R.id.navigation_sinh_vien,
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

                // Bind sau khi set graph để Up button/back stack hoạt động chính xác
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)
            }
        } else {
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }

        val topLevelTabs = setOf(
            R.id.navigation_trang_chu,
            R.id.navigation_do_an,
            R.id.navigation_sinh_vien,
            R.id.navigation_hoi_dong,
            R.id.navigation_thong_tin
        )

        // Những màn con của Trang chủ cần giữ BottomNav và highlight Trang chủ
        val homeChildren = setOf(
            R.id.navigation_thong_bao_detail
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Hiển thị BottomNav khi ở tab top-level hoặc màn con của Trang chủ
            binding.navView.isVisible = destination.id in topLevelTabs || destination.id in homeChildren

            // Giữ tab Trang chủ được chọn khi ở màn con
            if (destination.id in homeChildren) {
                binding.navView.menu.findItem(R.id.navigation_trang_chu)?.isChecked = true
            }

            // Ẩn/hiện ActionBar cho màn auth
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

    // Bảo đảm nút Up trên ActionBar popBackStack đúng
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
