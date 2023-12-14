package bangkit.capstone.vloc.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.ActivityMainBinding
import bangkit.capstone.vloc.ui.hero.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showContent()
        observeSession()
        showGreetings()
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_home)
            }
        }
    }

    private fun showContent() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_home, R.id.navigation_profile, R.id.navigation_search, R.id.navigation_details
        ).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    private fun showGreetings() {
        val currentTime = Calendar.getInstance()
        val hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY)

        val greetingMessage = when {
            hourOfDay < 12 -> "Selamat Pagi"
            hourOfDay < 18 -> "Selamat Siang"
            else -> "Selamat Malam"
        }


        Snackbar.make(findViewById(R.id.container), greetingMessage, Snackbar.LENGTH_LONG).show()
    }

    private fun showToast() {
        Toast.makeText(this, R.string.failed_message, Toast.LENGTH_SHORT).show()
    }

}
