package io.github.karadkar.sample.detailui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.github.karadkar.sample.R
import io.github.karadkar.sample.databinding.FragmentPictureBinding
import io.github.karadkar.sample.utils.gone
import io.github.karadkar.sample.utils.visible
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PictureDetailFragment : Fragment(), Callback {
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
            .error(R.drawable.ic_broken_image)
            .into(binding.tivPicture, this)

        return binding.root
    }

    override fun onSuccess() {
        binding.apply {
            progressBar.gone()
            tivPicture.scaleType = ImageView.ScaleType.FIT_CENTER
            tivPicture.isZoomEnabled = true
        }
    }

    override fun onError(e: Exception?) {
        binding.apply {
            progressBar.gone()
            tivPicture.scaleType = ImageView.ScaleType.CENTER
            tivPicture.isZoomEnabled = false
        }
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