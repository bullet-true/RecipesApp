package com.ifedorov

import android.app.Application
import com.ifedorov.recipesapp.di.AppContainer

class RecipeApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)
    }
}