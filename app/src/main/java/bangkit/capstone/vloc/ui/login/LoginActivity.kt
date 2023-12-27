package bangkit.capstone.vloc.ui.login


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import bangkit.capstone.vloc.ui.home.MainActivity
import bangkit.capstone.vloc.ViewModelFactory
import bangkit.capstone.vloc.databinding.ActivityLoginBinding
import bangkit.capstone.vloc.ui.register.RegisterActivity

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        registerAction()
        playAnimation()
        validateEmail()
        validatePassword()

        binding?.loginButton?.setOnClickListener {
            setupAction()
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun registerAction(){
        binding?.registerTv?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
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

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
        buttonValidation()
    }

    private fun setupAction() {
        val email = binding?.emailEditText?.text
        val password = binding?.passwordEditText?.text.toString()

        viewModel.postLogin(email.toString(), password)
        viewModel.response.observe(this) {
            if (it.status.isNotEmpty()) {
                showToast(it.status)

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                showToast(it.status)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.loadingBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun buttonValidation() {
        val emailEditTextLayout = binding?.emailEditTextLayout
        val passwordEditTextLayout = binding?.passwordEditTextLayout
        val loginButton = binding?.loginButton

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                val email = emailEditTextLayout?.editText?.text.toString().trim()
                val password = passwordEditTextLayout?.editText?.text.toString().trim()

                val isNotEmpty = email.isNotEmpty() && password.isNotEmpty()

                loginButton?.isEnabled = isNotEmpty
            }
        }

        emailEditTextLayout?.editText?.addTextChangedListener(textWatcher)
        passwordEditTextLayout?.editText?.addTextChangedListener(textWatcher)
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }
    private fun playAnimation() {

        val viewsToAnimate = listOf(
            binding?.titleTextView,
            binding?.messageTextView,
            binding?.emailTextView,
            binding?.emailEditTextLayout,
            binding?.passwordTextView,
            binding?.passwordEditTextLayout,
            binding?.loginButton
        )

        val animatorSet = AnimatorSet()
        val animations = viewsToAnimate
            .filterNotNull()
            .map { view ->
                ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(100)
            }

        animatorSet.playSequentially(*animations.toTypedArray())
        animatorSet.startDelay = 50
        animatorSet.start()
    }
}
