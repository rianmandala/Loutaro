package com.example.loutaro.data.source.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FireAuthService {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerWithEmailAndPass(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email,password)
    }

    fun signWithEmailAndPass(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    fun sendEmailVerification(): Task<Void>? {
        return getCurrentUser()?.sendEmailVerification()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signOut(){
        return firebaseAuth.signOut()
    }

}