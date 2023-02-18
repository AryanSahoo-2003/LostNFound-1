package com.iitp.trakon.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.iitp.trakon.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_fill_lost_item.*
import kotlinx.android.synthetic.main.activity_update_found.*
import kotlinx.android.synthetic.main.activity_updateprofile.*

class Updateprofile : BaseActivity() {

    private lateinit var u_id:String
    private var selectedImageFileUri: Uri? = null
    private var imageUrl: String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updateprofile)


        val bundle:Bundle?=intent.extras
        val name=bundle!!.getString("name")
        val phone= bundle!!.getString("phone")
        val roll=bundle!!.getString("roll")
        val whatsapp=bundle!!.getString("whatsapp")
        val image=bundle!!.getString("image")
        u_id= bundle!!.getString("uid").toString()

        Update_Profile_name.setText(name)
        Update_profile_phone.setText(phone)
        Update_profile_roll.setText(roll)
        Update_profile_whatsapp.setText(whatsapp)

        if (image != null) {
            imageUrl=image.toString()
        }

        update_profile_button.setOnClickListener {
            Log.d("aryan",imageUrl.toString())
            showProgressDialog("Please Wait")
            if(imageUrl!=null && selectedImageFileUri==null){
//                Toast.makeText(this,"Please select an image",Toast.LENGTH_SHORT).show()
                updateProfile(imageUrl!!.toString())
            }
            else if(selectedImageFileUri!=null){
                uploadUserImage()
            }
        }

        Upload_profile_pic_button.setOnClickListener {
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
        startActivityForResult(gallery, SignupPage.PICK_IMAGE_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == SignupPage.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            selectedImageFileUri = data.data
        }
        Glide.with(this)
            .load(selectedImageFileUri)
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(showImgProfile)
    }

    private fun uploadUserImage() {
//        showProgressDialog("Please wait")
        if (selectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference
                .child(
                    "Found_Item" + System.currentTimeMillis() + "." + getFileExt(
                        selectedImageFileUri
                    )
                )
            sRef.putFile(selectedImageFileUri!!).addOnSuccessListener {

                sRef.downloadUrl
                    .addOnCompleteListener {
                        imageUrl = it.result!!.toString()
                        updateProfile(imageUrl!!)
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

    private fun getFileExt(uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun updateProfile(image:String)
    {

        val name = Update_Profile_name.text.toString()
        val phone = Update_profile_phone.text.toString().trim { it <= ' ' }
        val roll = Update_profile_roll.text.toString()
        val whatsapp = Update_profile_whatsapp.text.toString().trim{ it <= ' '}
        if (validateLostForm(name, phone,roll ,whatsapp)) {
//            showProgressDialog("Please Wait")
            FirebaseFirestore.getInstance().collection("users").document(u_id)
                .update("name", Update_Profile_name.text.toString())
            FirebaseFirestore.getInstance().collection("users").document(u_id)
                .update("phone", Update_profile_phone.text.toString())
            FirebaseFirestore.getInstance().collection("users").document(u_id)
                .update("roll", Update_profile_roll.text.toString())
            FirebaseFirestore.getInstance().collection("users").document(u_id)
                .update("whatsapp",whatsapp)
            FirebaseFirestore.getInstance().collection("users").document(u_id)
                .update("image",image).addOnSuccessListener {
                    hideProgressDialog()
                    Toast.makeText(this,"Updates are successful",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,Tabs::class.java))
                    this.finish()
                }
        }
    }


    private fun validateLostForm(
        name: String,
        phone: String,
        roll: String,
        whatsapp: String
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
            TextUtils.isEmpty(whatsapp.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Your Whatsapp Number.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(roll.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Your Roll Number.",
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
            (whatsapp.length!=10)->{
                Toast.makeText(
                    this,
                    "Please Enter valid Whatsapp Number.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }
    override fun onBackPressed(){
        startActivity(Intent(this,Tabs::class.java))
    }


}