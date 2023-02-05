package com.example.lostnfound.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

}

    private fun signInRegistered()
    {
        val email=Username.text.toString().trim{it <=' '}
        val password=Login_password.text.toString().trim{it <=' '}
        if(validateForm(email, password))
        {
            intent.putExtra("email",email)
            showProgressDialog("Please Wait")
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        if(auth.currentUser?.isEmailVerified==true){
                            savedData(email);
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
    //new code started 23:28 - 01-02-2023
    fun savedData(email: String){
        var sharedPreferences:SharedPreferences=getSharedPreferences("logindata", MODE_PRIVATE);
        var editorShared:SharedPreferences.Editor=sharedPreferences.edit();
        editorShared.putBoolean("logincounter",true);
        editorShared.putString("useremail",email);
        editorShared.putString("uidUser", FirebaseAuth.getInstance().currentUser?.uid!!);
        editorShared.apply();
        Log.d("loginStatus","true/false")
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

            if(user.valid==false) {
                Toast.makeText(this,"You have been Banned :(",
                    Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
            }
            else{
                Toast.makeText(this,"Login successful :) ",
                    Toast.LENGTH_SHORT).show()

                var intentToNav : Intent = Intent(this,Tabs::class.java)
                startActivity(intentToNav)
                finish()
            }
        }
    override fun onBackPressed(){
        finish()
        System.exit(0)
    }
    }
