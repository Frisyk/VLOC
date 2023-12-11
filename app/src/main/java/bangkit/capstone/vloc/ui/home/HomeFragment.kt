package bangkit.capstone.vloc.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireContext())
        binding?.rvDestination?.layoutManager = layoutManager

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            val token = user.token
            val adapter = DestinationAdapter()
            binding?.rvDestination?.adapter = adapter.withLoadStateFooter(
                footer = LoadingAdapter {
                    adapter.retry()
                }
            )
            viewModel.getAllDestination("Bearer $token")?.observe(viewLifecycleOwner) { response ->
                if (response != null) {
                    adapter.submitData(lifecycle, response)
                } else {
                    binding?.texts?.text = response
                }
            }

        }

        return binding?.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
