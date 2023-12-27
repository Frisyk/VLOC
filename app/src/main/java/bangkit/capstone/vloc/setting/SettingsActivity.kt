package bangkit.capstone.vloc.setting


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.FragmentProfileBinding
import bangkit.capstone.vloc.databinding.SettingsActivityBinding
import bangkit.capstone.vloc.ui.home.MainActivity
import bangkit.capstone.vloc.ui.login.LoginActivity
import bangkit.capstone.vloc.ui.profile.ProfileViewModel


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private val viewModel by viewModels<SettingViewModel> {
            ViewModelFactory.getInstance(requireContext())
        }
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val darkModePreference = findPreference<ListPreference>(getString(R.string.pref_key_dark))
            darkModePreference?.setOnPreferenceChangeListener { _, newValue ->
                val nightMode = when (newValue) {
                    getString(R.string.pref_dark_follow_system) -> DarkMode.FOLLOW_SYSTEM.value
                    getString(R.string.pref_dark_on) -> DarkMode.ON.value
                    getString(R.string.pref_dark_off) -> DarkMode.OFF.value
                    else -> DarkMode.FOLLOW_SYSTEM.value
                }
                updateTheme(nightMode)
                true
            }

            val languagePref = findPreference<Preference>(getString(R.string.pref_key_language))
            languagePref?.setOnPreferenceClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            if (preference.key == "key_logout") {
                viewModel.logout()
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            return super.onPreferenceTreeClick(preference)
        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }
    }
}