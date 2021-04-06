package io.github.karadkar.sample.detailui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.github.karadkar.sample.databinding.FragmentPictureBinding
import io.github.karadkar.sample.utils.gone
import io.github.karadkar.sample.utils.visible
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PictureDetailFragment : Fragment() {
    private lateinit var binding: FragmentPictureBinding
    private lateinit var imageId: String
    private val viewModel: PictureDetailViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPictureBinding.inflate(layoutInflater, container, false)
        imageId = arguments?.getString(KEY_IMAGE_ID) ?: error("image id is needed")

        val pictureDetails = viewModel.getPictureDetail(imageId)
        binding.progressBar.visible()
        Picasso.get()
            .load(pictureDetails.imageUrl)
            .into(binding.tivPicture, object : Callback {
                override fun onSuccess() {
                    binding.progressBar.gone()
                }

                override fun onError(e: Exception?) {
                    binding.progressBar.gone()
                    Toast.makeText(this@PictureDetailFragment.context, "error loading image", Toast.LENGTH_SHORT).show()
                }
            })

        return binding.root
    }

    companion object {
        //TODO: pass all data as Parcel object and get rid of view-model in fragment
        private const val KEY_IMAGE_ID = "key.fragment.picture.imageId"
        fun getInstance(imageId: String): PictureDetailFragment {
            return PictureDetailFragment().also {
                it.arguments = Bundle().apply {
                    putString(KEY_IMAGE_ID, imageId)
                }
            }
        }
    }
}