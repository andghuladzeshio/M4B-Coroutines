package com.example.testproj

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

//        findViewById<Button>(R.id.helloWorldButton).setOnClickListener {
//            mainViewModel.onStop()
//        }

        mainViewModel.dataLiveData.observe(this) {
            Log.d("logkata", it.toString())
        }
    }
}