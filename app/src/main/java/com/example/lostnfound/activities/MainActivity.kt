package com.example.lostnfound.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lostnfound.R
import com.example.lostnfound.activities.SignupPage
import com.example.lostnfound.firebase.firestoreclass
import com.example.lostnfound.models.Users
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_signup_page.*


class MainActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        println(Signup)
        Signup.setOnClickListener {
            startActivity(Intent(this, SignupPage::class.java))
        }
        r_password.setOnClickListener {
            val email = Username.text.toString()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }

        Login_button.setOnClickListener{
            signInRegistered()
        }

//        setUpActionBar()//Action Bar


}
//    private fun setUpActionBar(){
//        setSupportActionBar(toolbar_login)
//
//        val actionBar=supportActionBar
//        if(actionBar !=null){
//            actionBar.setDisplayHomeAsUpEnabled(true)
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
//        }
//        toolbar_login.setNavigationOnClickListener{onBackPressed()}
//    }
    private fun signInRegistered()
    {
        val email=Username.text.toString().trim{it <=' '}

        val password=Login_password.text.toString().trim{it <=' '}

        if(validateForm(email, password))
        {
            showProgressDialog("Please Wait")
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        if(auth.currentUser?.isEmailVerified==true){
                            firestoreclass().signInUser(this)
                        }
                        else{
                            Toast.makeText(this, "Please Verify your email",
                                Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(baseContext, task.exception.toString(),
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }
    private fun validateForm(email:String,password:String):Boolean {
        return when {
            TextUtils.isEmpty(password.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@MainActivity,
                    "Please Enter Your Password.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(email.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@MainActivity,
                    "Please Enter Your Email.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }
        fun signInSuccess(user: Users){
//            hideProgressDialog()
//            val name=user.name
            Toast.makeText(this,"Login successful :) ",
                Toast.LENGTH_SHORT).show()
//            AfterLoginPage().setName(user.name)
            startActivity(Intent(this,AfterLoginPage::class.java))
            finish()
        }
    }
