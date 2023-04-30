package com.example.maximustask

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.maximustask.api.FactService
import com.example.maximustask.api.RetrofitHelper
import com.example.maximustask.databinding.ActivityMainBinding
import com.example.maximustask.models.CatFacts
import com.example.maximustask.repository.FactsRepository
import com.example.maximustask.viewmodels.MainViewModel
import com.example.maximustask.viewmodels.MainViewModelFactory
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions


class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    private var mInterstitialAd: InterstitialAd? = null

    private lateinit var exitDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_MaximusTask)
        setContentView(binding.root)

        // ADs Initialization
        MobileAds.initialize(this) {}

        // Native ADs
        nativeAD()

        // InterstitialADs
        interstitialAds()

        // Initialize the exit dialog
        exitDialog = createExitDialog()

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

        binding.btnAds.setOnClickListener {
            if (mInterstitialAd != null) {
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        Toast.makeText(this@MainActivity, "AD Dismissed", Toast.LENGTH_SHORT).show()
                        interstitialAds()
                    }

                }
                mInterstitialAd?.show(this)

            } else {
                Toast.makeText(this, "AD is not Loaded Yet", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun interstitialAds() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            this.resources.getString(R.string.AdmobIntersterial),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
    }

    private fun nativeAD() {
        val adLoader = AdLoader.Builder(this, resources.getString(R.string.AdmobNative))
            .forNativeAd { ad: NativeAd ->
                // Show the ad.
                val styles =
                    NativeTemplateStyle.Builder()
                        .withMainBackgroundColor(ColorDrawable(Color.WHITE)).build()
                val template = findViewById<TemplateView>(R.id.my_template)
                template.setStyles(styles)
                template.setNativeAd(ad)

                // If this callback occurs after the activity is destroyed, you
                // must call destroy and return or you may get a memory leak.
                // Note `isDestroyed` is a method on Activity.
                if (isDestroyed) {
                    ad.destroy()
                    return@forNativeAd
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to Load Native Ad",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()

        // Making AD request to Google's service
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun setFact(facts: List<CatFacts>) {
        val fact = facts[(facts.indices).random()].text
        binding.factsTv.text = fact
        mainViewModel.currentFact = fact
    }

    private fun createExitDialog(): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                // User selected Yes, exit the app
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                // User selected No, dismiss the dialog
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
    }

    override fun onBackPressed() {
        exitDialog.show()
    }
}