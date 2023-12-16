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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.FragmentSearchBinding
import bangkit.capstone.vloc.ui.getImageUri
import bangkit.capstone.vloc.ui.home.DestinationAdapter
import bangkit.capstone.vloc.ui.home.LoadingAdapter
import bangkit.capstone.vloc.ui.reduceFileImage
import bangkit.capstone.vloc.ui.uriToFile
import com.google.android.material.transition.MaterialFadeThrough
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

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

        val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding?.rvDestination?.layoutManager = layoutManager

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            val token = user.token
            val adapter = DestinationAdapter()
            binding?.rvDestination?.adapter = adapter.withLoadStateFooter(
                footer = LoadingAdapter {
                    adapter.retry()
                }
            )
            viewModel.getAllStory("Bearer $token")?.observe(viewLifecycleOwner) { response ->
                if (response != null) {
                    adapter.submitData(lifecycle, response)
                }
            }
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.response.observe(viewLifecycleOwner) {
            if (!it.error) {
                showLoading(false)
                showToast(it.message)
//                val navController = findNavController()
//                navController.navigate(R.id.navigation_home)
            } else {
                showToast(it.message)
            }
        }

        binding?.galleryButton?.setOnClickListener { startGallery() }
        binding?.cameraButton?.setOnClickListener { startCamera() }
        binding?.uploadButton?.setOnClickListener {
            viewModel.getSession().observe(viewLifecycleOwner) { user ->
                val token = "Bearer ${user.token}"
                if (token.isNotEmpty()) {
                    performUpload(token)
                } else {
                    showToast(getString(R.string.failed_message))
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
        currentImageUri?.let {
            binding?.previewImageView?.setImageURI(it)
        }
    }

    private fun performUpload(token: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            val description = "hehehehehe"

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            viewModel.postStory(token, multipartBody, requestBody)
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
