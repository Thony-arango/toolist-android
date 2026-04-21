package com.toolist.app.domain.model

data class Category(
    val id: String,
    val name: String,
    val icon: String,           // emoji o identificador de ícono
    val isSystem: Boolean,
    val productCount: Int,
)
