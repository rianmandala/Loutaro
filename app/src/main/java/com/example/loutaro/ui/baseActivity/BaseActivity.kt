package com.example.loutaro.ui.baseActivity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.loutaro.R
import com.example.loutaro.data.source.firebase.FireAuthService
import com.example.loutaro.data.source.tinyDb.TinyDB
import com.example.loutaro.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

open class BaseActivity : AppCompatActivity() {

    val external_error = listOf(
        "The email address is badly formatted",
        "A network error",
        "The password is invalid or the user does not have a password",
        "There is no user record corresponding to this identifier"
    )

    private var doubleBackToExitPressedOnce = false
    private var toolbarBackButtonDisplayStatus =false
    private lateinit var progressDialog: ProgressDialog

    private lateinit var tinyDb: TinyDB

    fun getCurrentUser() = FireAuthService.getCurrentUser()

    fun logOut() {
        FireAuthService.signOut()
        setStatusLoginToDB(false)
        navigateRoot(LoginActivity::class.java)
    }

    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    fun initTinyDB(context: Context = this){
        tinyDb = TinyDB(context)
    }

    fun setStatusLoginToDB(status: Boolean){
        tinyDb.putBoolean(getString(R.string.key_status_login), status)
    }

    fun getStatusLoginFromDB(context: Context = this): Boolean {
        return tinyDb.getBoolean(context.resources.getString(R.string.key_status_login))
    }

    fun setLanguageCode(languageCode: String){
        tinyDb.putString(getString(R.string.key_language_code), languageCode)
    }

    fun getLanguageCode(context: Context=this): String? {
        return tinyDb.getString(context.resources.getString(R.string.key_language_code))
    }

    fun setUserTypeLogin(userType: String){
        tinyDb.putString(getString(R.string.key_loutaro_user_type_login), userType)
    }

    fun getUserTypeLogin(context: Context=this): String? {
        return tinyDb.getString(context.resources.getString(R.string.key_loutaro_user_type_login))
    }

    fun isUserFreelancer(context: Context=this): Boolean {
        return getUserTypeLogin(context)==context.resources.getString(R.string.value_freelancer)
    }

    fun isUserBusinessMan(context: Context=this): Boolean {
        return getUserTypeLogin(context)==context.resources.getString(R.string.value_business_man)
    }

    fun activatedToolbarBackButton() {
        toolbarBackButtonDisplayStatus=true
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        showSnackbar(getString(R.string.click_back_again_to_exit))

        Handler().postDelayed({
            this.doubleBackToExitPressedOnce = false
        }, 2000)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if(toolbarBackButtonDisplayStatus){
            super.onBackPressed()
        }else{
            doubleBackToExit()
        }
    }

    fun showSnackbar(message: String){
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        snackbar.show()
    }

    fun showErrorSnackbar(message: String){
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.error))
        snackbar.show()
    }

    fun showWarningSnackbar(message: String){
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary))
        snackbar.show()
    }

    fun showProgressDialog(title: String = "", message: String = "", context: Context = this) {
        progressDialog = ProgressDialog(context)
        progressDialog.run{
            setTitle(title)
            setMessage(message)
            setCancelable(false)
            show()
        }
    }

    fun getRelativeTimeFromNow(timeStart: Long): CharSequence? {
        return android.text.format.DateUtils.getRelativeTimeSpanString(timeStart!!)
    }

    fun setProgressUploadDialog(progress: String){
        progressDialog.setMessage(progress)
    }

    fun closeProgressDialog(){
        progressDialog.dismiss()
    }

    fun navigateRoot(destination: Class<*>, context: Context = this){
        val destinationIntent = Intent(context, destination)
        destinationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(destinationIntent)
        finish()
    }

    fun navigateForward(destination: Class<*>, context: Context = this){
        val destinationIntent = Intent(context, destination)
        context.startActivity(destinationIntent)
    }

    fun validateRequire(field: String, input: TextInputLayout, inputName: String): Boolean {
        return if(field.trim().isEmpty() || field.trim().isBlank()){
            input.isErrorEnabled=true
            input.error = getString(R.string.required, inputName)
            false
        }else{
            input.isErrorEnabled=false
            input.error = null
            true
        }
    }

    fun setViewToVisible(vararg views: View){
        for (view in views){
            view.visibility = View.VISIBLE
        }
    }

    fun setViewToInvisible(vararg views: View){
        for(view in views){
            view.visibility = View.INVISIBLE
        }
    }

    fun setViewToGone(vararg views: View){
        for(view in views){
            view.visibility = View.GONE
        }
    }

    fun readableExternalError(originalError: String): String {
        var readableError = ""
        for(index in 0.until(external_error.size)){
            if(originalError.contains(external_error[index])){
                readableError = resources.getStringArray(R.array.external_error)[index]
            }
        }
        return readableError
    }
}