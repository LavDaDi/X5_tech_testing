package com.example.todoapp.model.repository

import com.example.todoapp.model.domain.TodoItem
import com.example.todoapp.model.domain.TodoTag
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodos(): Flow<List<TodoItem>>
    suspend fun addTodo(text: String, tag: TodoTag? = null)
    suspend fun deleteTodo(id: String)
    suspend fun toggleTodo(id: String)
    suspend fun updateTodoText(id: String, newText: String)
}