package com.example.loutaro.ui.paymentProject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.loutaro.R
import com.example.loutaro.databinding.ActivityPaymentProjectBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PaymentProjectActivity : BaseActivity() {
    private lateinit var binding: ActivityPaymentProjectBinding
    var paymentStatus=false
    private  val paymentProjectViewModel: PaymentProjectViewModel by viewModels {
        ViewModelFactory.getInstance()
    }
    companion object{
        const val EXTRA_STATUS_PAYMENT="EXTRA_STATUS_PAYMENT"
        const val EXTRA_PRICE_PAYMENT="EXTRA_PRICE_PAYMENT"
        const val EXTRA_ID_PROJECT="EXTRA_ID_PROJECT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarTitle(getString(R.string.payment))
        activatedToolbarBackButton()
        paymentStatus= intent.getBooleanExtra(EXTRA_STATUS_PAYMENT,false)
        val pricePayment = intent.getIntExtra(EXTRA_PRICE_PAYMENT,0)
        val idProject = intent.getStringExtra(EXTRA_ID_PROJECT)

        binding.run {
            tvPricePayment.text = "$${pricePayment}"

            checkPaymentStatus()

            btnPaymentProject.setOnClickListener {
                if(!paymentStatus){
                    showProgressDialog(message = getString(R.string.please_wait))
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            paymentProjectViewModel.confirmPayment(idProject.toString()).await()
                            withContext(Dispatchers.Main){
                                paymentStatus=true
                                checkPaymentStatus()
                                closeProgressDialog()
                                showSnackbar(getString(R.string.success_confirm_payment_project))
                            }

                        }catch (e: Exception){
                            Log.e("error","Error when try to confirm payment")
                            closeProgressDialog()
                        }
                    }
                }else{
                    showWarningSnackbar(getString(R.string.already_confirm_payment))
                }

            }
        }


    }

    private fun checkPaymentStatus(){
        binding.run {
            if(paymentStatus){
                btnPaymentProject.text = getString(R.string.you_already_confirm_payment)
            }else{
                btnPaymentProject.text = getString(R.string.confirm_payment)
            }
        }
    }
}