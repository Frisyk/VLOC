package bangkit.capstone.vloc.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.data.local.database.Favorites
import bangkit.capstone.vloc.databinding.FragmentDetailsBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.launch
import java.util.Calendar

@Suppress("DEPRECATION")
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<DetailsViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val destinationId = arguments?.getString("destinationId")

        exitTransition = MaterialFadeThrough()

        lifecycleScope.launch {
            viewModel.getDetails(destinationId!!)
        }

        setContent()

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding?.detailsToolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setContent() {
        viewModel.status.observe(viewLifecycleOwner) {
            showSnackbar(it)
        }

        viewModel.location.observe(viewLifecycleOwner) { response ->
            binding?.tvdetail?.text = response?.destionation?.destinationDetails
            binding?.tvname?.text = response?.destionation?.destinationName
            binding?.htmNominal?.text = getString(R.string.htm_price, response?.destionation?.fee.toString() )
            binding?.tvTime?.text = response?.destionation?.openTime
            binding?.ratingScore?.text = getString(R.string.rating_score, response?.destionation?.rating)
            binding?.ivdetail?.let { imageView ->
                Glide.with(imageView)
                    .load(response?.destionation?.destinationImgUrl)
                    .into(imageView)
            }
            binding?.img1?.let { image ->
                Glide.with(image)
                    .load(response?.moreImgUrl1)
                    .into(image)
            }
            binding?.img2?.let { image ->
                Glide.with(image)
                    .load(response?.moreImgUrl2)
                    .into(image)
            }
            binding?.img3?.let { image ->
                Glide.with(image)
                    .load(response?.moreImgUrl3)
                    .into(image)
            }
            openMaps(response?.destionation?.latitude.toString(), response?.destionation?.longitude.toString())
            viewModel.getSession().observe(viewLifecycleOwner) {
                if (it.isLogin) {
                    addRemoveFavorites(response?.destionation?.id!!, response.destionation.destinationImgUrl)
                } else {
                    binding?.btnFav?.isEnabled = false
                }
            }

        }
    }

    private fun addRemoveFavorites(id: String, url: String) {
        val timestamp = getTimestamp()
        val data = Favorites(id, url, timestamp)
        viewModel.checkFavoriteStatus(id).observe(viewLifecycleOwner) { isFav ->
            if(!isFav) {
                binding?.btnFav?.setOnClickListener {
                    viewModel.senFav(data)
                    showSnackbar("add to favorite")
                }
            } else {
                binding?.btnFav?.setOnClickListener {
                    viewModel.deleteFav(data)
                    showSnackbar("delete from favorite")
                }
            }

            if (isFav) {
                binding?.btnFav?.setImageDrawable(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_fav)
                )
            } else {
                binding?.btnFav?.setImageDrawable(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_fav_border)
                )
            }
        }

    }

    private fun openMaps(latitude: String, longitude: String) {
        val mapUri = Uri.parse("https://maps.google.com/maps?daddr=$latitude,$longitude")
        val intent = Intent(Intent.ACTION_VIEW, mapUri)

        binding?.btnMaps?.setOnClickListener{
            startActivity(intent)
        }
    }


    private fun getTimestamp(): Long {
        val calendar = Calendar.getInstance()
        return calendar.timeInMillis
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}