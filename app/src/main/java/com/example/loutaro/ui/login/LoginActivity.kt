package com.example.loutaro.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.example.loutaro.ui.main.MainActivity
import com.example.loutaro.R
import com.example.loutaro.databinding.ActivityLoginBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.register.RegisterActivity
import com.example.loutaro.viewmodel.ViewModelFactory

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var statusCompleteValidate= false
    private val  loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarTitle(getString(R.string.log_in))
        activatedToolbarBackButton()

        loginViewModel.statusLoginWithEmailAndPass.observe(this){
            closeProgressDialog()
            if(it.status){
                if(loginViewModel.getCurrentUser()?.isEmailVerified == true){
                    val id = getCurrentUser()?.uid
                    if(binding.rgLoginAs.checkedRadioButtonId==R.id.rb_login_as_freelancer){
                        if (id != null) {
                            loginViewModel.isFreelancerExist(id)
                        }
                    }
                    else{
                        if (id != null) {
                            loginViewModel.isBusinessManExist(id)
                        }
                    }
                }else{
                    showWarningSnackbar(getString(R.string.message_email_not_verified_yet))
                }
            }else{
                showErrorSnackbar(readableExternalError(it.response.toString()))
            }
        }

        loginViewModel.responseIsFreelancerExist.observe(this){ response->
            if(response){
                initTinyDB()
                setStatusLoginToDB(true)
                setUserTypeLogin(getString(R.string.value_freelancer))
                navigateRoot(MainActivity::class.java)
            }else{
                showErrorSnackbar(getString(R.string.email_not_registered_as, getString(R.string.freelancer)))
            }
        }

        loginViewModel.responseIsBusinessManExist.observe(this){ response->
            if(response){
                initTinyDB()
                setStatusLoginToDB(true)
                setUserTypeLogin(getString(R.string.value_business_man))
                navigateRoot(MainActivity::class.java)
            }else{
                showErrorSnackbar(getString(R.string.email_not_registered_as, getString(R.string.business_man)))
            }
        }

        binding.run{
            inputEmail.requestFocus()
            btnLogin.setOnClickListener{
                if(completeValidate()){
                    showProgressDialog(message = getString(R.string.please_wait))
                    loginViewModel.loginWithEmailAndPass(inputEmail.text.toString(), inputPass.text.toString())
                }
            }

            btnRegister.setOnClickListener {
                navigateForward(RegisterActivity::class.java)
            }

            inputEmail.doAfterTextChanged {
                validateRequire(it.toString(), txtInputEmail, getString(R.string.email))
            }
            inputPass.doAfterTextChanged {
                validateRequire(it.toString(), txtInputPass, getString(R.string.password))
            }
        }
    }

    private fun completeValidate(): Boolean{
        binding.run {
            statusCompleteValidate = validateRequire(inputEmail.text.toString(), txtInputEmail, getString(R.string.name))
            statusCompleteValidate = validateRequire(inputPass.text.toString(), txtInputPass, getString(R.string.password))
        }
        return statusCompleteValidate
    }

}