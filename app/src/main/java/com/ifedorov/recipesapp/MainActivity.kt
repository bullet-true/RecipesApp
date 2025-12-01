package com.ifedorov.recipesapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.ifedorov.recipesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("ActivityMainBinding can't be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnCategory.setOnClickListener {
            val options = navOptions {
                anim {
                    enter = R.anim.from_left
                    exit = R.anim.to_right
                    popEnter = R.anim.from_right
                    popExit = R.anim.to_left
                }
                launchSingleTop = true
            }

            findNavController(R.id.nav_host_fragment)
                .navigate(R.id.categoriesListFragment, null, options)
        }

        binding.btnFavorite.setOnClickListener {
            val options = navOptions {
                anim {
                    enter = R.anim.from_right
                    exit = R.anim.to_left
                    popEnter = R.anim.from_left
                    popExit = R.anim.to_right
                }
                launchSingleTop = true
            }

            findNavController(R.id.nav_host_fragment)
                .navigate(R.id.favoritesFragment, null, options)
        }
    }
}