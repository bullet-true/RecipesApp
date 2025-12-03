package com.ifedorov.recipesapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.google.gson.Gson
import com.ifedorov.recipesapp.databinding.ActivityMainBinding
import com.ifedorov.recipesapp.model.Category
import java.net.HttpURLConnection
import java.net.URL

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
            }

            val navController = findNavController(R.id.nav_host_fragment)
            if (navController.currentDestination?.id != R.id.categoriesListFragment) {
                navController.navigate(R.id.categoriesListFragment, null, options)
            }
        }

        binding.btnFavorite.setOnClickListener {
            val options = navOptions {
                anim {
                    enter = R.anim.from_right
                    exit = R.anim.to_left
                    popEnter = R.anim.from_left
                    popExit = R.anim.to_right
                }
            }

            val navController = findNavController(R.id.nav_host_fragment)
            if (navController.currentDestination?.id != R.id.favoritesFragment) {
                navController.navigate(R.id.favoritesFragment, null, options)
            }
        }

        Log.i("!!!", "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")

        val thread = Thread {
            Log.i("!!!", "Выполняю запрос на потоке: ${Thread.currentThread().name}")

            val url = URL("https://recipes.androidsprint.ru/api/category")
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            val jsonBody = connection.getInputStream().use { inputStream ->
                inputStream.bufferedReader().use { reader ->
                    reader.readText()
                }
            }

            Log.i("!!!", "Response code: ${connection.responseCode}")
            Log.i("!!!", "Response message: ${connection.responseMessage}")
            Log.i("!!!", "Body: $jsonBody")

            val gson = Gson()
            val categories = gson.fromJson(jsonBody, Array<Category>::class.java)

            categories.forEach { category ->
                Log.i("!!!", category.title)
            }

            connection.disconnect()
        }
        thread.start()
    }
}