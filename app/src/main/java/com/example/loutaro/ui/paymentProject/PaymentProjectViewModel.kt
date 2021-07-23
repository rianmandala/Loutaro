package com.example.loutaro.ui.paymentProject

import androidx.lifecycle.ViewModel
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task

class PaymentProjectViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    fun confirmPayment(idProject: String): Task<Void> {
        return loutaroRepository.confirmPayment(idProject)
    }

}