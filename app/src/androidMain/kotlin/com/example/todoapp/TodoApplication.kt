package com.example.todoapp

import android.app.Application
import com.example.todoapp.feature.di.todoModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

internal class TodoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TodoApplication)
            modules(todoModule)
        }
    }
}
