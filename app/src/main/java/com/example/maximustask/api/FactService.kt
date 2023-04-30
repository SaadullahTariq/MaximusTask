package com.example.maximustask.api

import com.example.maximustask.models.CatFacts
import retrofit2.Response
import retrofit2.http.GET

interface FactService {

    @GET("/facts")
    suspend fun getFact() : Response<CatFacts>

}