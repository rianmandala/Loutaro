package com.example.loutaro.ui.withdrawFreelancer

import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Withdrawal
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference

class WithdrawFreelancerViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {


    fun withdrawMoney(withdrawal: Withdrawal): Task<DocumentReference> {
        return loutaroRepository.withdrawalMoney(withdrawal)
    }

    fun decreaseBalance(idFreelancer: String, balance: Long): Task<Void> {
        return loutaroRepository.decreaseBalanceFreelancer(idFreelancer, balance)
    }

}