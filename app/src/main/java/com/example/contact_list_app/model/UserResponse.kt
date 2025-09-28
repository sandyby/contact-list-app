package com.example.contact_list_app.model

data class UserResponse(
    val users: List<User>
)

data class User(
    val firstName: String,
    val lastName: String,
    val phone: String
)