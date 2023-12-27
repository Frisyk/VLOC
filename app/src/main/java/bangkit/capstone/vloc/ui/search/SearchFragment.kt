package bangkit.capstone.vloc.ui.search

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.FragmentSearchBinding
import bangkit.capstone.vloc.ui.getImageUri
import bangkit.capstone.vloc.ui.uriToFile
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialFadeThrough
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<SearchViewModel> {
        ViewModelFactory.getInstance(requireActivity().application)
    }

    private var currentImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        enterTransition = MaterialFadeThrough()


        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.response.observe(viewLifecycleOwner) { predict ->
            if (predict.status != "fail") {
                showLoading(false)
                binding?.nameResult?.text = predict.result.destinationName
                binding?.predictResultAccuration?.text = getString(R.string.accuration, predict.probabilitis)
                Glide.with(requireActivity())
                    .load(predict.result.destinationImgUrl)
                    .into(binding?.imageResult!!)
                binding?.addressResult?.text = predict.result.address
                binding?.directButton?.setOnClickListener{ view ->
                    val bundle = bundleOf("destinationId" to predict.result.id)
                    view.findNavController().navigate(R.id.action_navigation_home_to_navigation_details2, bundle)
                }
            }
        }

        binding?.galleryButton?.setOnClickListener {
            viewModel.getSession().observe(viewLifecycleOwner) {
                if (it.isLogin) {
                    startGallery()
                } else {
                    showToast(getString(R.string.predict_message))
                }
            }
        }
        binding?.cameraButton?.setOnClickListener {
            viewModel.getSession().observe(viewLifecycleOwner) {
                if (it.isLogin) {
                    startCamera()
                } else {
                    showToast(getString(R.string.predict_message))
                }
            }
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter) {
            AnimationUtils.loadAnimation(context, R.anim.fade_in)
        } else {
            AnimationUtils.loadAnimation(context, R.anim.fade_out)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d(TAG, getString(R.string.media_not_selected))
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            val token = user.token
            if (token.isNotEmpty()) {
                predictLocation(token)
                binding?.predictResult?.visibility = View.VISIBLE
            } else {
                showToast(getString(R.string.failed_message))
            }
        }
        currentImageUri?.let {
            binding?.previewImage?.setImageURI(it)
        }
    }

    private fun predictLocation(token: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext())

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "file",
                imageFile.name,
                requestImageFile
            )
            viewModel.predictImage(token, multipartBody)
        } ?: showToast(getString(R.string.warning))
    }


    private fun showLoading(isLoading: Boolean) {
        binding?.progressIndicator?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "Photo Picker"
    }
}
