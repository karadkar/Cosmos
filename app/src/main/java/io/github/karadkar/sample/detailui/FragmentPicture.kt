package io.github.karadkar.sample.detailui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import io.github.karadkar.sample.databinding.FragmentPictureBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel

class FragmentPicture : Fragment() {
    private lateinit var binding: FragmentPictureBinding
    private lateinit var imageId: String
    private val viewModel: PictureDetailViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPictureBinding.inflate(layoutInflater, container, false)
        imageId = arguments?.getString(KEY_IMAGE_ID) ?: error("image id is needed")

        val pictureDetails = viewModel.getPictureDetail(imageId)
        Picasso.get()
            .load(pictureDetails.imageUrl)
            .into(binding.ivPicture)

        return binding.root
    }

    companion object {
        private const val KEY_IMAGE_ID = "key.fragment.picture.imageId"
        fun getInstance(imageId: String): FragmentPicture {
            return FragmentPicture().also {
                it.arguments = Bundle().apply {
                    putString(KEY_IMAGE_ID, imageId)
                }
            }
        }
    }
}