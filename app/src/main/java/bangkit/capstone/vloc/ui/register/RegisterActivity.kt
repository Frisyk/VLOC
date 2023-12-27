package bangkit.capstone.vloc.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.ActivityRegisterBinding
import bangkit.capstone.vloc.ui.home.MainActivity
import bangkit.capstone.vloc.ui.login.LoginActivity

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        playAnimation()

        binding?.registerButton?.setOnClickListener {
            setupAction()
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.response.observe(this) {
            if (!it.error) {
                showToast(it.message)
                showLoading(false)
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                showToast(it.message)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }
    @Suppress("DEPRECATION")
    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        loginAction()
        validateName()
        validateEmail()
        validatePassword()
        buttonValidation()
    }

    private fun validateName() {
        binding?.nameEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    binding?.nameEditTextLayout?.error = "Name cannot be empty"
                } else {
                    binding?.nameEditTextLayout?.error = null
                }
            }
        })
    }
    private fun validateEmail() {
        binding?.emailEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && !isValidEmail(s.toString())) {
                    binding?.emailEditTextLayout?.error = "Enter a valid email address"
                } else {
                    binding?.emailEditTextLayout?.error = null
                }
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun validatePassword() {
        binding?.passwordEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length < 8) {
                    binding?.passwordEditTextLayout?.error = "Password should be at least 8 characters"
                } else {
                    binding?.passwordEditTextLayout?.error = null
                }
            }
        })

    }

    private fun loginAction() {
        binding?.loginTv?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    private fun setupAction() {
        val name = binding?.nameEditText?.text.toString().trim()
        val email = binding?.emailEditText?.text.toString().trim()
        val password = binding?.passwordEditText?.text.toString().trim()


        viewModel.postRegister(name, email, password)
    }

    private fun buttonValidation() {
        val nameEditTextLayout = binding?.nameEditTextLayout
        val emailEditTextLayout = binding?.emailEditTextLayout
        val passwordEditTextLayout = binding?.passwordEditTextLayout
        val registerButton = binding?.registerButton

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                val name = nameEditTextLayout?.editText?.text.toString().trim()
                val email = emailEditTextLayout?.editText?.text.toString().trim()
                val password = passwordEditTextLayout?.editText?.text.toString().trim()

                val isNotEmpty = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()

                registerButton?.isEnabled = isNotEmpty
            }
        }

        nameEditTextLayout?.editText?.addTextChangedListener(textWatcher)
        emailEditTextLayout?.editText?.addTextChangedListener(textWatcher)
        passwordEditTextLayout?.editText?.addTextChangedListener(textWatcher)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.loadingBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }
    private fun playAnimation() {
        val animators = listOf(
            ObjectAnimator.ofFloat(binding?.titleTextView, View.ALPHA, 1f).setDuration(100),
            ObjectAnimator.ofFloat(binding?.nameTextView, View.ALPHA, 1f).setDuration(100),
            ObjectAnimator.ofFloat(binding?.nameEditTextLayout, View.ALPHA, 1f).setDuration(100),
            ObjectAnimator.ofFloat(binding?.emailTextView, View.ALPHA, 1f).setDuration(100),
            ObjectAnimator.ofFloat(binding?.emailEditTextLayout, View.ALPHA, 1f).setDuration(100),
            ObjectAnimator.ofFloat(binding?.passwordTextView, View.ALPHA, 1f).setDuration(100),
            ObjectAnimator.ofFloat(binding?.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100),
            ObjectAnimator.ofFloat(binding?.registerButton, View.ALPHA, 1f).setDuration(100)
        )

        AnimatorSet().apply {
            playSequentially(animators)
            startDelay = 50
        }.start()
    }
}
