package com.example.loutaro.ui.register

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.example.loutaro.R
import com.example.loutaro.data.entity.BusinessMan
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.databinding.ActivityRegisterBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.country.SelectCountryActivity
import com.example.loutaro.ui.createProfile.CreateProfileActivity
import com.example.loutaro.ui.login.LoginActivity
import com.example.loutaro.ui.verifyEmail.VerifyEmailActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var statusCompleteValidate= false
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance()
    }
    companion object {
        private const val REQUEST_CODE = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbarTitle(getString(R.string.register))
        activatedToolbarBackButton()

        binding.run{
            inputCountry.setOnClickListener{
                var value= ""
                if(getString(R.string.select_country)!=binding.inputCountry.text.toString()){
                    value=binding.inputCountry.text.toString()
                }
                val selectCountryIntent = Intent(this@RegisterActivity,SelectCountryActivity::class.java)
                selectCountryIntent.putExtra(SelectCountryActivity.EXTRA_SELECTED_VALUE, value)
                startActivityForResult(selectCountryIntent, REQUEST_CODE)
            }

            inputName.requestFocus()

            inputName.doAfterTextChanged {
                validateRequire(it.toString(),txtInputName, getString(R.string.name))
            }
            inputEmail.doAfterTextChanged {
                validateRequire(it.toString(), txtInputEmail, getString(R.string.email))
            }
            inputPass.doAfterTextChanged {
                validateRequire(it.toString(), txtInputPass, getString(R.string.password))
            }
            inputCountry.doAfterTextChanged {
                validateRequire(it.toString(), txtInputPass, getString(R.string.country))
            }
            registerViewModel.statusRegisterWithEmailAndPass.observe(this@RegisterActivity) {
                if(it.status){
                    if(rgRegisterAs.checkedRadioButtonId==R.id.rb_register_as_freelancer){
                        registerViewModel.setDataFreelancer(it.uid!!, Freelancer(
                                name = inputName.text.toString(),
                                email = inputEmail.text.toString(),
                                country = inputCountry.text.toString()
                        ))
                        navigateForward(CreateProfileActivity::class.java)
                    }else{
                        registerViewModel.setDataBusinessMan(it.uid!!, BusinessMan(
                                name = inputName.text.toString(),
                                email = inputEmail.text.toString(),
                                country = inputCountry.text.toString()
                        ))
                        registerViewModel.sendEmailVerification()?.addOnFailureListener { email->
                            showErrorSnackbar(email.message.toString())
                        }
                        navigateForward(VerifyEmailActivity::class.java)
                    }
                    closeProgressDialog()
                }else{
                    closeProgressDialog()
                    showErrorSnackbar(readableExternalError(it.message.toString()))
                }
            }
            btnRegister.setOnClickListener {
                if(completeValidate()){
                    showProgressDialog(message = getString(R.string.please_wait))
                    registerViewModel.registerWithEmailAndPass(inputEmail.text.toString(), inputPass.text.toString())
                }
            }
            btnLogin.setOnClickListener {
                navigateForward(LoginActivity::class.java)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== REQUEST_CODE){
            if(resultCode==SelectCountryActivity.RESULT_CODE){
                val selectableValue = data?.getStringExtra(SelectCountryActivity.EXTRA_SELECTED_VALUE)
                binding.run {
                    inputCountry.text.clear()
                    inputCountry.text.insert(0,selectableValue)
                }
            }
        }

    }

    private fun completeValidate(): Boolean{
        binding.run {
            statusCompleteValidate= validateRequire(inputName.text.toString(), txtInputName, getString(R.string.name))
            statusCompleteValidate= validateRequire(inputEmail.text.toString(), txtInputEmail, getString(R.string.email))
            statusCompleteValidate= validateRequire(inputPass.text.toString(), txtInputPass, getString(R.string.password))
        }
        return statusCompleteValidate
    }

}