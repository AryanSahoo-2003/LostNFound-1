package com.iitp.trakon.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.iitp.trakon.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedONce=false
//    private lateinit var mProgressDialog:Dialog
    lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)



    }
    open fun showProgressDialog(text:String)
    {   progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading Data")
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.show()


    }
    fun hideProgressDialog(){
//        mProgressDialog.dismiss()
        progressDialog.dismiss()
    }

    fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        if(doubleBackToExitPressedONce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedONce=true
        Toast.makeText(
            this,
            "Please click back again to exit",
            Toast.LENGTH_SHORT
        ).show()
        Handler().postDelayed({doubleBackToExitPressedONce=false},2000)
    }
    fun showErrorSnackBar(message:String){
        val snackBar=Snackbar.make(findViewById(android.R.id.content),
        message,Snackbar.LENGTH_LONG)
        val snackBarView=snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
        snackBar.show()
    }
}