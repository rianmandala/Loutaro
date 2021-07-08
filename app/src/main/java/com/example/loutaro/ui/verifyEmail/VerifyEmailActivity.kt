package com.example.loutaro.ui.verifyEmail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.loutaro.R
import com.example.loutaro.data.source.firebase.FireAuthService
import com.example.loutaro.databinding.ActivityVerifyEmailBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.login.LoginActivity

class VerifyEmailActivity : BaseActivity() {
    private lateinit var binding: ActivityVerifyEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarTitle(getString(R.string.verify_email))
        activatedToolbarBackButton()
        binding.run {
            tvInfoToVerifyEmail.text = getString(R.string.info_to_verify_email,FireAuthService.getCurrentUser()?.email)
            btnLoginNow.setOnClickListener {
                navigateForward(LoginActivity::class.java)
            }
        }
    }

}