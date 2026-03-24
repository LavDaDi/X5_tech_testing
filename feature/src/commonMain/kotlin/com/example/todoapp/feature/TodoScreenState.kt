package com.example.todoapp.feature

import com.example.todoapp.model.domain.TodoFilter
import com.example.todoapp.model.domain.TodoItem
import com.example.todoapp.model.domain.TodoTag

internal data class TodoScreenState(
    val todos: List<TodoItem> = emptyList(),
    val inputText: String = "",
    val filter: TodoFilter = TodoFilter.ALL,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    /** Выбранный тег для новой задачи. */
    val selectedTag: TodoTag? = null,
    /** ID задачи, которая сейчас редактируется (null = не редактируем). */
    val editingTodoId: String? = null,
    /** Текст в поле редактирования. */
    val editingText: String = "",
)