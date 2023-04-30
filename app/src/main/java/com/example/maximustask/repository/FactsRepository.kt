package com.example.maximustask.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.maximustask.api.FactService
import com.example.maximustask.models.CatFacts

class FactsRepository(private val factService: FactService) {

    private val factLiveData = MutableLiveData<CatFacts>()

    val facts : LiveData<CatFacts>
    get() = factLiveData

    suspend fun getFacts(){
        val result = factService.getFact()

        if (result.body() != null){
            factLiveData.postValue(result.body())
        }
    }

}