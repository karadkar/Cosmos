package io.github.karadkar.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.karadkar.sample.databinding.ActivityNasaPicturesBinding

class NasaPicturesActivity : AppCompatActivity() {
    lateinit var binding: ActivityNasaPicturesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNasaPicturesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}