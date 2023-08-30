package ru.sokolov_diplom.nework.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sokolov_diplom.nework.R
import ru.sokolov_diplom.nework.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.sokolov_diplom.nework.viewmodels.AuthViewModel
import ru.sokolov_diplom.nework.viewmodels.UserViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var hostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        hostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = hostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_posts, R.id.navigation_events)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_posts -> {
                    findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
                    true
                }

                R.id.navigation_events -> {
                    findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.action_posts_to_eventsFragment)
                    true
                }

                R.id.my_Profile -> {
                    lifecycleScope.launch {
                        authViewModel.state.value?.id?.let { userViewModel.getUserById(it).join() }
                    }
                    findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.action_posts_to_userProfileFragment)
                    true
                }

                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(hostFragment.navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
