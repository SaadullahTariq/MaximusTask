package com.example.maximustask.repository

import androidx.lifecycle.MutableLiveData
import com.example.maximustask.api.FactService
import com.example.maximustask.models.CatFacts

class FactsRepository(private val factService: FactService) {

    private val factLiveData = MutableLiveData<List<CatFacts>>()

    val facts: MutableLiveData<List<CatFacts>>
        get() = factLiveData

    suspend fun getFacts() {
        val result = factService.getFact()

        if (result.body() != null) {
            factLiveData.postValue(result.body())
        }
    }

}