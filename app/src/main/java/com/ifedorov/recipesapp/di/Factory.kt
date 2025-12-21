package com.ifedorov.recipesapp.di

interface Factory<T> {
    fun create(): T
}