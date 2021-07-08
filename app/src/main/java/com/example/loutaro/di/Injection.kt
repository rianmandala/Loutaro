package com.example.loutaro.di

import com.example.loutaro.data.source.LoutaroRepository

object Injection {
    fun provideLoutaroRepository(): LoutaroRepository{
        return LoutaroRepository.getInstance()
    }
}