package com.example.l6z1

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface INewtonAPI {
    @GET("{operation}/{expression}")
    fun getResponse(@Path("operation", encoded = true) operation: String,
                    @Path("expression", encoded = true) expression: String) : Call<Result>
}