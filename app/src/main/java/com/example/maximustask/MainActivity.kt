package com.example.maximustask

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.maximustask.api.FactService
import com.example.maximustask.api.RetrofitHelper
import com.example.maximustask.databinding.ActivityMainBinding
import com.example.maximustask.models.CatFacts
import com.example.maximustask.repository.FactsRepository
import com.example.maximustask.viewmodels.MainViewModel
import com.example.maximustask.viewmodels.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factService = RetrofitHelper.getInstance().create(FactService::class.java)

        val repository = FactsRepository(factService)

        mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]

        mainViewModel.facts.observe(this) { facts ->
            val currentFact = mainViewModel.currentFact

            if (currentFact != null) {
                binding.factsTv.text = currentFact
            } else {
                setFact(facts)
            }

            binding.btnReload.setOnClickListener {
                setFact(facts)
            }
        }

    }

    private fun setFact(facts: List<CatFacts>) {
        val fact = facts[(facts.indices).random()].text
        binding.factsTv.text = fact
        mainViewModel.currentFact = fact
    }
}