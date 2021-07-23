package com.example.loutaro.ui.withdrawFreelancer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.loutaro.R
import com.example.loutaro.data.entity.Withdrawal
import com.example.loutaro.databinding.ActivityWithdrawFreelancerBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class WithdrawFreelancerActivity : BaseActivity() {
    private lateinit var binding: ActivityWithdrawFreelancerBinding
    private val withdrawFreelancerViewModel: WithdrawFreelancerViewModel by viewModels {
        ViewModelFactory.getInstance()
    }
    companion object{
        const val EXTRA_BALANCE_AMOUNT = "EXTRA_BALANCE_AMOUNT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawFreelancerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarTitle(getString(R.string.withdraw_payment))
        activatedToolbarBackButton()

        val balanceAmount = intent.getLongExtra(EXTRA_BALANCE_AMOUNT,0)

        binding.run {

            btnWithdrawMonoy.setOnClickListener {
                if(edtWithdrawMoney.editText?.text.toString().toLong() <= balanceAmount){
                    showProgressDialog(message = getString(R.string.please_wait))
                    CoroutineScope(Dispatchers.IO).launch {
                        try{
                            val idFreelancer = getCurrentUser()?.uid.toString()
                            withdrawFreelancerViewModel.withdrawMoney(Withdrawal(idUser = idFreelancer,email = edtEmailPaypal.editText?.text.toString(),amount = binding.edtWithdrawMoney.editText?.text.toString().toLong())).await()
//                            val balanceFreelancer = balanceAmount - edtWithdrawMoney.editText?.text.toString().toLong()
//                            withdrawFreelancerViewModel.decreaseBalance(idFreelancer = idFreelancer, balance = balanceFreelancer)
                            withContext(Dispatchers.Main){
                                edtEmailPaypal.editText?.setText("")
                                edtWithdrawMoney.editText?.setText("")
                                closeProgressDialog()
                                showSnackbar(message = getString(R.string.wait_admin_confirmation_withdrawal))
                            }

                        }catch (e: Exception){
                            Log.e("error","Error when try to withdraw money")
                        }
                    }
                }else{
                    showWarningSnackbar(message = getString(R.string.sorry_your_withdrawa_bigger_than_balance))
                }

            }
        }
    }
}