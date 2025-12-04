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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("ActivityMainBinding can't be null")

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(10)

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

        threadPool.submit {
            Log.i("!!!", "Выполняю запрос на потоке: ${Thread.currentThread().name}")

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val request: Request = Request.Builder()
                .url("https://recipes.androidsprint.ru/api/category")
                .build()

            val jsonBody = client.newCall(request).execute().use { response ->
                Log.i("!!!", "Response code: ${response.code}")
                Log.i("!!!", "Response message: ${response.message}")
                response.body?.string() ?: ""
            }

            Log.i("!!!", "Body: $jsonBody")

            val gson = Gson()
            val categories = gson.fromJson(jsonBody, Array<Category>::class.java)

            categories.forEach { category ->
                Log.i("!!!", category.title)
            }

            val categoriesIds = categories.map { it.id }

            categoriesIds.forEach { id ->
                threadPool.submit {
                    val request: Request = Request.Builder()
                        .url("https://recipes.androidsprint.ru/api/category/$id/recipes")
                        .build()

                    val threadName = Thread.currentThread().name
                    Log.i("!!!", "Выполняю запрос для ID = $id в пуле потоков: $threadName")

                    client.newCall(request).execute().use { response ->
                        Log.i("!!!", "Id: $id, recipe list : ${response.body?.string()}")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        threadPool.shutdown()
    }
}