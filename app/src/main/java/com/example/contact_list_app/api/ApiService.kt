package com.example.contact_list_app.api

import com.example.contact_list_app.model.UserResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("users")
    fun getUsers(): Call<UserResponse>
}