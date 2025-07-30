package com.example.messenger.domain.model

data class User(
    val id: Int,
    val name: String,
    val phone: String? = null
)