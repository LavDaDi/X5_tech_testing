package com.example.todoapp.model.domain

import kotlinx.serialization.Serializable

@Serializable
data class TodoTag(
    val name: String,
    val color: Long,
) {
    companion object {
        // Предустановленные теги
        val WORK = TodoTag(name = "Работа", color = 0xFF4285F4)
        val STUDY = TodoTag(name = "Учёба", color = 0xFF9C27B0)
        val PERSONAL = TodoTag(name = "Личное", color = 0xFF4CAF50)
        val SHOPPING = TodoTag(name = "Покупки", color = 0xFFFF9800)
        val HEALTH = TodoTag(name = "Здоровье", color = 0xFFE91E63)

        val allTags = listOf(WORK, STUDY, PERSONAL, SHOPPING, HEALTH)
    }
}