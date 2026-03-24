package com.example.todoapp.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.model.domain.TodoFilter
import com.example.todoapp.model.domain.TodoTag
import com.example.todoapp.model.repository.TodoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class TodoViewModel(
    private val repository: TodoRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {

    private val _filter = MutableStateFlow(TodoFilter.ALL)
    private val _inputText = MutableStateFlow("")
    private val _selectedTag = MutableStateFlow<TodoTag?>(null)
    private val _editingTodoId = MutableStateFlow<String?>(null)
    private val _editingText = MutableStateFlow("")

    val state: StateFlow<TodoScreenState> = combine(
        repository.getTodos(),
        _filter,
        _inputText,
        _selectedTag,
        combine(_editingTodoId, _editingText) { id, text -> id to text },
    ) { todos, filter, inputText, selectedTag, (editingId, editingText) ->
        val filteredTodos = when (filter) {
            TodoFilter.ALL -> todos
            TodoFilter.ACTIVE -> todos.filter { !it.isCompleted }
            TodoFilter.COMPLETED -> todos.filter { it.isCompleted }
        }
        TodoScreenState(
            todos = filteredTodos,
            inputText = inputText,
            filter = filter,
            completedCount = todos.count { it.isCompleted },
            totalCount = todos.size,
            selectedTag = selectedTag,
            editingTodoId = editingId,
            editingText = editingText,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = TodoScreenState(),
    )

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun onTagSelected(tag: TodoTag?) {
        _selectedTag.value = if (_selectedTag.value == tag) null else tag
    }

    fun addTodo() {
        val text = _inputText.value.trim()
        if (text.isBlank()) return
        viewModelScope.launch(dispatcher) {
            repository.addTodo(text = text, tag = _selectedTag.value)
            _inputText.value = ""
            _selectedTag.value = null
        }
    }

    fun deleteTodo(id: String) {
        viewModelScope.launch(dispatcher) {
            repository.deleteTodo(id)
        }
    }

    fun toggleTodo(id: String) {
        viewModelScope.launch(dispatcher) {
            repository.toggleTodo(id)
        }
    }

    fun setFilter(filter: TodoFilter) {
        _filter.value = filter
    }

    /** Начать редактирование задачи. */
    fun startEditing(id: String, currentText: String) {
        _editingTodoId.value = id
        _editingText.value = currentText
    }

    /** Обновить текст в поле редактирования. */
    fun onEditingTextChanged(text: String) {
        _editingText.value = text
    }

    /** Сохранить редактирование. */
    fun saveEditing() {
        val id = _editingTodoId.value ?: return
        val text = _editingText.value.trim()
        if (text.isBlank()) return
        viewModelScope.launch(dispatcher) {
            repository.updateTodoText(id, text)
            _editingTodoId.value = null
            _editingText.value = ""
        }
    }

    /** Отменить редактирование. */
    fun cancelEditing() {
        _editingTodoId.value = null
        _editingText.value = ""
    }
}