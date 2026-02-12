package com.example.todofirebase.model

import androidx.compose.ui.graphics.Color

enum class TodoPriority(val color: Color, val label: String) {
    HIGH(Color(0xFFFFCDD2), "Alta"),    // Vermelho claro
    MEDIUM(Color(0xFFFFF9C4), "Média"), // Amarelo claro
    LOW(Color(0xFFC8E6C9), "Baixa")     // Verde claro
}

data class TodoItem(
    val id: Long = System.currentTimeMillis(), // ID único baseado no tempo
    val text: String,
    val isDone: Boolean = false,
    val priority: TodoPriority = TodoPriority.LOW
)