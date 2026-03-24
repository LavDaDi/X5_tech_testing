package com.example.todoapp.model.domain

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class TodoItem(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false,
    val tag: TodoTag? = null,
) {
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(text: String, tag: TodoTag? = null,): TodoItem = TodoItem(
            id = Uuid.random().toString(),
            text = text,
            tag = tag,
        )
    }
}
