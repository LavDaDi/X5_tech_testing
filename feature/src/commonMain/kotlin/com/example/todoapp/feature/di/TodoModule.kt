package com.example.todoapp.feature.di

import com.example.todoapp.feature.TodoViewModel
import com.example.todoapp.model.repository.InMemoryTodoRepository
import com.example.todoapp.model.repository.TodoRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val todoModule = module {
    single<TodoRepository> { InMemoryTodoRepository() }
    viewModel { TodoViewModel(repository = get()) }
}
