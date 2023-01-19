package com.example.lostnfound.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.example.lostnfound.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_update_password.*

class UpdatePassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)
        val user = FirebaseAuth.getInstance().currentUser
        val txtNewPass: Editable? = new_pass.text
        button_update.setOnClickListener {
            if(new_pass.text.trim{it <= ' '}==confirm_new_pass.text.trim{it <= ' '}) {
                user!!.updatePassword(txtNewPass.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Your Password Has Been Updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this,AfterLoginPage::class.java))
                        this.finish()
                    } else {
                        Toast.makeText(this, "Error Update", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"New Password and Confirm Password should be same",Toast.LENGTH_SHORT).show()
            }
        }
    }
}