package com.example.todoapp.feature

import com.example.todoapp.model.domain.TodoFilter
import com.example.todoapp.model.domain.TodoItem
import com.example.todoapp.model.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class TodoViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        repository: TodoRepository = FakeTodoRepository(),
    ): TodoViewModel = TodoViewModel(
        repository = repository,
        dispatcher = testDispatcher,
    )

    @Test
    fun should_add_new_todo_item_to_list() = runTest {
        val viewModel = createViewModel()
        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.state.collect {}
        }

        viewModel.onInputTextChanged("Buy groceries")
        viewModel.addTodo()

        val state = viewModel.state.value
        assertEquals(1, state.totalCount)
        assertEquals("Buy groceries", state.todos.first().text)
        assertFalse(state.todos.first().isCompleted)
        assertEquals("", state.inputText)

        collectJob.cancel()
    }

    @Test
    fun should_toggle_todo_completion_status() = runTest {
        val viewModel = createViewModel()
        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.state.collect {}
        }

        viewModel.onInputTextChanged("Test task")
        viewModel.addTodo()

        val todoId = viewModel.state.value.todos.first().id
        assertFalse(viewModel.state.value.todos.first().isCompleted)

        viewModel.toggleTodo(todoId)
        assertTrue(viewModel.state.value.todos.first().isCompleted)

        viewModel.toggleTodo(todoId)
        assertFalse(viewModel.state.value.todos.first().isCompleted)

        collectJob.cancel()
    }

    @Test
    fun should_filter_todos_by_status() = runTest {
        val viewModel = createViewModel()
        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.state.collect {}
        }

        viewModel.onInputTextChanged("Task 1")
        viewModel.addTodo()
        viewModel.onInputTextChanged("Task 2")
        viewModel.addTodo()
        viewModel.onInputTextChanged("Task 3")
        viewModel.addTodo()

        val firstTodoId = viewModel.state.value.todos.first().id
        viewModel.toggleTodo(firstTodoId)

        viewModel.setFilter(TodoFilter.COMPLETED)
        assertEquals(1, viewModel.state.value.todos.size)
        assertTrue(viewModel.state.value.todos.all { it.isCompleted })

        viewModel.setFilter(TodoFilter.ACTIVE)
        assertEquals(2, viewModel.state.value.todos.size)
        assertTrue(viewModel.state.value.todos.none { it.isCompleted })

        viewModel.setFilter(TodoFilter.ALL)
        assertEquals(3, viewModel.state.value.todos.size)

        collectJob.cancel()
    }

    @Test
    fun should_calculate_correct_statistics() = runTest {
        val viewModel = createViewModel()
        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.state.collect {}
        }

        viewModel.onInputTextChanged("Task 1")
        viewModel.addTodo()
        viewModel.onInputTextChanged("Task 2")
        viewModel.addTodo()
        viewModel.onInputTextChanged("Task 3")
        viewModel.addTodo()

        assertEquals(3, viewModel.state.value.totalCount)
        assertEquals(0, viewModel.state.value.completedCount)

        val firstId = viewModel.state.value.todos[0].id
        val secondId = viewModel.state.value.todos[1].id
        viewModel.toggleTodo(firstId)
        viewModel.toggleTodo(secondId)

        assertEquals(3, viewModel.state.value.totalCount)
        assertEquals(2, viewModel.state.value.completedCount)

        collectJob.cancel()
    }

    @Test
    fun should_delete_todo_item() = runTest {
        val viewModel = createViewModel()
        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.state.collect {}
        }

        viewModel.onInputTextChanged("Task to delete")
        viewModel.addTodo()
        assertEquals(1, viewModel.state.value.totalCount)

        val todoId = viewModel.state.value.todos.first().id
        viewModel.deleteTodo(todoId)

        assertEquals(0, viewModel.state.value.totalCount)
        assertTrue(viewModel.state.value.todos.isEmpty())

        collectJob.cancel()
    }

    @Test
    fun should_not_add_blank_todo() = runTest {
        val viewModel = createViewModel()
        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.state.collect {}
        }

        viewModel.onInputTextChanged("   ")
        viewModel.addTodo()

        assertEquals(0, viewModel.state.value.totalCount)

        collectJob.cancel()
    }
}

private class FakeTodoRepository : TodoRepository {

    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())

    override fun getTodos(): Flow<List<TodoItem>> = _todos.asStateFlow()

    override suspend fun addTodo(text: String) {
        val item = TodoItem.create(text)
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
}
