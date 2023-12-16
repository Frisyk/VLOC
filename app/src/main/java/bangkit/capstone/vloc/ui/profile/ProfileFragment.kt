package bangkit.capstone.vloc.ui.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.FragmentHomeBinding
import bangkit.capstone.vloc.databinding.FragmentProfileBinding
import bangkit.capstone.vloc.ui.home.DestinationAdapter
import bangkit.capstone.vloc.ui.home.LoadingAdapter
import bangkit.capstone.vloc.ui.home.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

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
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            val token = user.token
            val adapter = DestinationAdapter()
            binding?.rvDestination?.adapter = adapter.withLoadStateFooter(
                footer = LoadingAdapter {
                    adapter.retry()
                }
            )
            binding?.nameProfile?.text = user.name
            viewModel.getAllStory("Bearer $token")?.observe(viewLifecycleOwner) { response ->
                if (response != null) {
                    adapter.submitData(lifecycle, response)
                } else {
                    showSnackbar(getString(R.string.failed_message))
                }
            }

        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}