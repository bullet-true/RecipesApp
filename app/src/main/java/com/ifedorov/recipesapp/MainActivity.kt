package com.ifedorov.recipesapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
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

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<CategoriesListFragment>(R.id.mainContainer)
            }
        }

        binding.btnCategory.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.mainContainer)

            if (currentFragment !is CategoriesListFragment) {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<CategoriesListFragment>(R.id.mainContainer)
                    addToBackStack(null)
                }
            }
        }

        binding.btnFavorite.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.mainContainer)

            if (currentFragment !is FavoritesFragment) {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<FavoritesFragment>(R.id.mainContainer)
                    addToBackStack(null)
                }
            }
        }
    }
}