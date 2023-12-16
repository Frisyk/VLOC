package bangkit.capstone.vloc.ui.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.FragmentDetailsBinding
import bangkit.capstone.vloc.databinding.FragmentHomeBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.launch

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


        viewModel.getSession().observe(viewLifecycleOwner) {
            if (it != null) {
                val token = it.token
                lifecycleScope.launch {
                    viewModel.getDetails("Bearer $token", destinationId)
                }
            } else {
                binding?.loginInDetails?.visibility = View.VISIBLE
            }
        }



        viewModel.destination.observe(viewLifecycleOwner) { response ->
                binding?.tvdetail?.text = response?.story?.description
                binding?.tvname?.text = response?.story?.name
                binding?.ivdetail?.let { imageView ->
                    Glide.with(imageView)
                        .load(response?.story?.photoUrl)
                        .into(imageView)
                }
        }
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}