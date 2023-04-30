package com.example.maximustask

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.maximustask.api.FactService
import com.example.maximustask.api.RetrofitHelper
import com.example.maximustask.repository.FactsRepository
import com.example.maximustask.viewmodels.MainViewModel
import com.example.maximustask.viewmodels.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val factService = RetrofitHelper.getInstance().create(FactService::class.java)

        val repository = FactsRepository(factService)

        mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]

        mainViewModel.facts.observe(this) {facts ->
            facts.forEach {
                Log.d("MaximusTaskData", it.fact)
            }
        }

    }
}