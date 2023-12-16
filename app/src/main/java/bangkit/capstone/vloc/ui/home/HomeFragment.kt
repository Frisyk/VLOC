package bangkit.capstone.vloc.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.FragmentHomeBinding
import bangkit.capstone.vloc.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {
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

//        (requireActivity() as AppCompatActivity).supportActionBar?.hide()


        val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding?.rvDestination?.layoutManager = layoutManager

        val horizontalLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding?.rvDestination2?.layoutManager = horizontalLayoutManager

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            requireActivity().invalidateOptionsMenu()
            val token = user.token
            val adapter = DestinationAdapter()
            binding?.rvDestination?.adapter = adapter.withLoadStateFooter(
                footer = LoadingAdapter {
                    adapter.retry()
                }
            )
            binding?.rvDestination2?.adapter = adapter
            viewModel.getAllStory("Bearer $token")?.observe(viewLifecycleOwner) { response ->
                if (response != null) {
                    adapter.submitData(lifecycle, response)
                } else {
                    showSnackbar(getString(R.string.failed_message))
                }
            }
        }

        setHasOptionsMenu(true)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.toolbar?.inflateMenu(R.menu.home_menu)

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login_logout -> {
                viewModel.getSession().observe(viewLifecycleOwner) {
                    if (!it.isLogin) {
                        startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    } else {
                        viewModel.logout()

                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val loginLogoutItem = menu.findItem(R.id.login_logout)

        viewModel.getSession().observe(viewLifecycleOwner) { session ->
            val iconResId = if (session.isLogin) R.drawable.ic_logout else R.drawable.ic_login
            loginLogoutItem.icon = ContextCompat.getDrawable(requireContext(), iconResId)
        }

    }


    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    private fun showSnackbar(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }


    private fun showLoading(isLoading: Boolean) {
        binding?.loadingBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
