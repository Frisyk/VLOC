package bangkit.capstone.vloc.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.FragmentHomeBinding
import bangkit.capstone.vloc.ui.hero.WelcomeActivity
import bangkit.capstone.vloc.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class HomeFragment : Fragment(){
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        setContent()
        setBanner()
        setHasOptionsMenu(true)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSession().observe(viewLifecycleOwner) {
            if (!it.isLogin) {
                binding?.loginBtn?.setOnClickListener {
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                }
            } else {
                binding?.loginBtn?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_logout))
                binding?.loginBtn?.setOnClickListener {
                    viewModel.logout()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                }
            }
        }

    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.exit_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
    private fun showSnackbar(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setBanner(){
        val horizontalLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding?.rvBanner?.layoutManager = horizontalLayoutManager

        lifecycleScope.launch(Dispatchers.Main, start = CoroutineStart.DEFAULT) {
            var position = 1
            repeat(10) {
                delay(3000)
                binding?.rvBanner?.smoothScrollToPosition(position)
                when {
                    position+1 == 10 -> position = 0
                    position == 0 -> position = position++
                    else -> position++
                }
            }
        }

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            val adapter = DestinationAdapter()
            if (!user.isLogin) {
                binding?.greetings?.text = getString(R.string.hi_user)
            } else {
                binding?.greetings?.text = getString(R.string.greetingformat, user.name )
            }
            binding?.rvBanner?.adapter = adapter
//            viewModel.getAllDestination(null)?.observe(viewLifecycleOwner) { response ->
//                if (response != null) {
//                    adapter.submitData(lifecycle, response)
//                } else {
//                    showSnackbar(getString(R.string.failed_message))
//                }
//            }
            viewModel.response.observe(viewLifecycleOwner){response ->
                if (response != null) {
                    adapter.submitList(response)
                } else {
                    showSnackbar(getString(R.string.failed_message))
                }

            }
        }

    }

    private fun setContent() {
        val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding?.rvDestination?.layoutManager = layoutManager

        val category = binding?.categoriesSpinner

        category?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = adapterView?.getItemAtPosition(position)
                if (item != "all") {
                    viewModel.getLocationByCategory(item.toString())
                } else {
                    viewModel.getLocation()
                }
                if (item != null) {
                    val adapter = DestinationAdapter()
                    binding?.rvDestination?.adapter = adapter
//                    binding?.rvDestination?.adapter = adapter.withLoadStateFooter(
//                        footer = LoadingAdapter {
//                            adapter.retry()
//                        }
//                    )
//                    viewModel.getAllDestination(item.toString())?.observe(viewLifecycleOwner) { response ->
//                        if (response != null) {
//                            adapter.submitData(lifecycle, response)
//                        } else {
//                            showSnackbar(getString(R.string.failed_message))
//                        }
//                    }
                    viewModel.response.observe(viewLifecycleOwner){response ->
                        if (response != null) {
                            adapter.submitList(response)
                        } else {
                            showSnackbar(getString(R.string.failed_message))
                        }

                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) = Unit
        }

    }


    private fun showLoading(isLoading: Boolean) {
        binding?.loadingBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
