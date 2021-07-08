package com.example.loutaro.ui.onboarding

import androidx.lifecycle.ViewModel
import com.example.loutaro.data.source.LoutaroRepository

class OnBoardingViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    fun getCurrentUser()= loutaroRepository.getCurrentUser()

}