package io.github.karadkar.sample.detailui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.karadkar.sample.databinding.ActivityPictureDetailBinding
import org.koin.android.viewmodel.ext.android.viewModel


class PictureDetailActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPictureDetailBinding

    private val viewModel: PictureDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.vpPictures.adapter = PicturesAdapter(
            supportFragmentManager,
            imageIds = viewModel.getTotalImageIds()
        )
    }

}