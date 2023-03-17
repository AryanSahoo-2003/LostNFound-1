package com.iitp.trakon.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.iitp.trakon.R
import com.iitp.trakon.firebase.firestoreclass
import com.iitp.trakon.models.Query_model
import kotlinx.android.synthetic.main.activity_qurery.*
import kotlinx.android.synthetic.main.activity_updateprofile.*

class qurery : AppCompatActivity() {
    private var querySelectedForSending : String = ""
    private lateinit var u_id:String
    private var selectedImageFileUri: Uri? = null
    private var imageUrl: String? ="null"
    private lateinit var progressDialog:ProgressDialog
    private var autha:FirebaseAuth=FirebaseAuth.getInstance()
    private var status = autha.currentUser?.isAnonymous
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_qurery)



        val name1 = intent.getStringExtra("nameExisting")
        val phone1 = intent.getStringExtra("phoneExisting")
        val email1=  intent.getStringExtra("emailExisting")
        if(autha.currentUser?.isAnonymous==false){
            query_PersonName.setText(name1)
            query_Email.setText(email1)
            query_PhoneNumber.setText(phone1)
        }

        val issuesSpinner = findViewById<Spinner>(R.id.spinnerDinner)

        // Define an array of issues
        val issuesArray = arrayOf("  Login issue","  Password issue", "  Connectivity issue", "  App crashing", "  Storage issue","  Others..")

        // Create an ArrayAdapter to populate the spinner with the array of issues
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, issuesArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        issuesSpinner.adapter = adapter

        // Set a listener to the spinner to handle when a user selects an issue
        issuesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                querySelectedForSending = issuesArray[position]
                Toast.makeText(this@qurery, "Selected issue: $querySelectedForSending", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                querySelectedForSending = "Password issue"
            }
        }
        materialButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                showImageChosser()

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    SignupPage.READ_STORAGE_PERMISSION_CODE
                )
            }
        }


        query_Submit.setOnClickListener {

            if(selectedImageFileUri==null){
                updateProfile(imageUrl!!.toString(),status)
            }
            else if(selectedImageFileUri!=null){
                uploadUserImage(status)
            }

        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SignupPage.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChosser()
            }
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImageChosser() {
        val gallery = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(gallery,SignupPage.PICK_IMAGE_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == SignupPage.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            selectedImageFileUri = data.data
            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show()
        }
//        showImgProfile.visibility= View.VISIBLE
//        Glide.with(this)
//            .load(selectedImageFileUri)
//            .placeholder(R.drawable.ic_baseline_person_24)
//            .into(showImgProfile)
    }

    fun showProgressDialog(text:String)
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Posting Query")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun getFileExt(uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun updateProfile(image:String,status: Boolean?)
    {
        val name = query_PersonName.text.toString()
        val phone = query_PhoneNumber.text.toString().trim { it <= ' ' }
        val email=query_Email.text.toString().trim{it <= ' '}
        val desc= query_Description.text.toString()
        if (validateLostForm(name,phone,email)) {
            showProgressDialog("Please Wait")
            val query_item=Query_model(querySelectedForSending,name,phone,email,desc,image)
            Log.d("aryan",query_item.name)
            val currentuser=autha.currentUser
            Log.d("hija",(status==null).toString())
            if(status  == true||status==null) {
                autha.signInAnonymously().addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                     FirebaseFirestore.getInstance().collection("query").add(query_item).addOnSuccessListener {
                         QueryRegistered()
                         hideProgressDialog()
                         startActivity((Intent(this, MainActivity::class.java)))
                         this.finish()
                     }.addOnFailureListener{
                         Log.d("jadu",it.toString())
                         hideProgressDialog()
                         Toast.makeText(this,"Some Error Occurred :|",Toast.LENGTH_SHORT).show()
                     }
                    } else {
//                        Log.d("hija",currentuser?.isAnonymous.toString())
                        Log.d("jadu", it.exception.toString())
                        Toast.makeText(this,"Some Error Occurred :|",Toast.LENGTH_SHORT).show()
                        hideProgressDialog()
                    }
                }
            }
            else{
                Log.d("hija",currentuser?.isAnonymous.toString())
                FirebaseFirestore.getInstance().collection("query").add(query_item).addOnSuccessListener {
                    QueryRegistered()
                    hideProgressDialog()
                    startActivity(Intent(this,Tabs::class.java))
                    this.finish()
                }.addOnFailureListener{
                    Log.d("jadu",it.toString())
                    Toast.makeText(this,"Some Error Occurred :|",Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }

        }
    }

    private fun uploadUserImage(status: Boolean?) {
//        showProgressDialog("Please wait")
        if (selectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference
                .child(
                    "Query" + System.currentTimeMillis() + "." + getFileExt(
                        selectedImageFileUri
                    )
                )
            sRef.putFile(selectedImageFileUri!!).addOnSuccessListener {

                sRef.downloadUrl
                    .addOnCompleteListener {
                        imageUrl = it.result!!.toString()
                        updateProfile(imageUrl!!,status)
                        Log.d("aryan",imageUrl.toString())
                    }

            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(status == true|| status==null){
            startActivity(Intent(this,MainActivity::class.java))
        }
        else {
            startActivity(Intent(this, Tabs::class.java))
        }
    }

    fun QueryRegistered(){
//        hideProgressDialog()
        Toast.makeText(
            this,
            "Your query has been successfully registered", Toast.LENGTH_LONG
        ).show()
    }
    fun hideProgressDialog(){
//        mProgressDialog.dismiss()
        progressDialog.dismiss()
    }

    private fun validateLostForm(
        name: String,
        phone: String,
        email:String
    ): Boolean {
        return when {//checking if the field are empty
            TextUtils.isEmpty(name.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Your Name.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(phone.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Your Mobile Number.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            !email.endsWith("@iitp.ac.in") -> {
                Toast.makeText(
                    this@qurery,
                    "Invalid Institute Email",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            (phone.length!=10)->{
                Toast.makeText(
                    this,
                    "Please Enter valid Mobile Number.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }
}