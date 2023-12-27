package bangkit.capstone.vloc.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.data.local.database.Favorites
import bangkit.capstone.vloc.databinding.FragmentProfileBinding
import bangkit.capstone.vloc.setting.SettingsActivity
import bangkit.capstone.vloc.ui.search.SearchFragment
import bangkit.capstone.vloc.ui.uriToFile
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private var currentImageUri: Uri? = null

    private val adapter = FavoritesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        enterTransition = MaterialFadeThrough()

        val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding?.rvDestination?.layoutManager = layoutManager

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        showProfile()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setToolbar() {
        viewModel.getSession().observe(viewLifecycleOwner) {
            if (!it.isLogin) {
                binding?.settingBtn?.visibility = View.GONE
            } else {
                binding?.settingBtn?.visibility = View.VISIBLE
                binding?.settingBtn?.setOnClickListener {
                    startActivity(Intent(requireActivity(), SettingsActivity::class.java))
                }
            }
        }
    }
    private fun showProfile() {
        viewModel.getSession().observe(viewLifecycleOwner) {
            viewModel.getUserDetails(it.token, it.id)
            Handler().postDelayed({
                viewModel.getUserFavorites(it.token, it.id)
            }, 3000)
            if (it.isLogin) {
                setToolbar()
                showFavorites()
                viewModel.userResponse.observe(viewLifecycleOwner) { user ->
                    binding?.nameProfile?.text = user.username
                    binding?.imgProfile?.let { imageView ->
                        Glide.with(imageView)
                            .load(user.image)
                            .centerCrop()
                            .into(imageView)
                    }
                }
            } else {
                binding?.nameProfile?.text = getString(R.string.guest)
                showSnackBar(getString(R.string.you_haven_t_login_yet))
            }
        }
    }

    private fun showFavorites() {
        // local database
        viewModel.getFavorites().observe(viewLifecycleOwner) { favorites ->
            val items = arrayListOf<Favorites>()
            favorites.map {
                val item = Favorites(id = it.id, destinationUrl = it.destinationUrl, timeStamp = it.timeStamp)
                items.add(item)
            }
            adapter.submitList(items)
            binding?.rvDestination?.adapter = adapter
        }
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
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
            Log.d(SearchFragment.TAG, getString(R.string.media_not_selected))
        }
    }

    private fun showImage() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            val token = user.token
            if (user.isLogin) {
                binding?.btnChange?.setOnClickListener {startGallery()}
                changePhotoProfile(token, user.id)
            } else {
                showSnackBar(getString(R.string.failed_message))
            }
        }
        currentImageUri?.let {
            binding?.imgProfile?.setImageURI(it)
        }
    }

    private fun changePhotoProfile(token: String, userId: Int) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext())

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestImageFile
            )
            viewModel.changeProfile(token, userId, multipartBody)
        } ?: showSnackBar(getString(R.string.warning))
    }


}