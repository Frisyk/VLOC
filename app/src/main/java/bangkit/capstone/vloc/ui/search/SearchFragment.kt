package bangkit.capstone.vloc.ui.search

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.FragmentSearchBinding
import bangkit.capstone.vloc.ui.getImageUri
import bangkit.capstone.vloc.ui.reduceFileImage
import bangkit.capstone.vloc.ui.uriToFile
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

//    private val requestPermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//                getMyLocation()
//            }
//        }

//    private fun getMyLocation() {
//        if (ContextCompat.checkSelfPermission(
//                requireContext().applicationContext,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                if (location != null) {
//                    this.location = location
//                } else {
//                    binding?.locationCheckBox?.isChecked = false
//                    Toast.makeText(requireContext(), getString(R.string.location_no_granted), Toast.LENGTH_SHORT ).show()
//                }
//            }
//        } else {
//            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }

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
