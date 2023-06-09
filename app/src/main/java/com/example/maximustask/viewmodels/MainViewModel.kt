package com.example.maximustask.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maximustask.models.CatFacts
import com.example.maximustask.repository.FactsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: FactsRepository) : ViewModel() {

    var currentFact: String? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {

            repository.getFacts()
        }
    }

    val facts: MutableLiveData<List<CatFacts>>
        get() = repository.facts

}