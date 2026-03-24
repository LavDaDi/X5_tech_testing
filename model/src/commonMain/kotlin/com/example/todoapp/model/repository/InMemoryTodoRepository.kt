package com.example.todoapp.model.repository

import com.example.todoapp.model.domain.TodoItem
import kotlinx.coroutines.flow.Flow
import com.example.todoapp.model.domain.TodoTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryTodoRepository : TodoRepository {

    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())

    override fun getTodos(): Flow<List<TodoItem>> = _todos.asStateFlow()

    override suspend fun addTodo(text: String, tag: TodoTag?) {
        val item = TodoItem.create(text, tag)
        _todos.update { current -> current + item }
    }

    override suspend fun deleteTodo(id: String) {
        _todos.update { current -> current.filter { it.id != id } }
    }

    override suspend fun toggleTodo(id: String) {
        _todos.update { current ->
            current.map { item ->
                if (item.id == id) item.copy(isCompleted = !item.isCompleted) else item
            }
        }
    }

    override suspend fun updateTodoText(id: String, newText: String) {
        if (newText.isBlank()) return
        _todos.update { current ->
            current.map { item ->
                if (item.id == id) item.copy(text = newText.trim()) else item
            }
        }
    }
}
