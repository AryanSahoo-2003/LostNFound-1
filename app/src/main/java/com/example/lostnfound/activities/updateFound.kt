package com.example.lostnfound.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lostnfound.R
import com.example.lostnfound.models.Lost
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_fill_found_item.*
import kotlinx.android.synthetic.main.activity_fill_lost_item.*
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_Msg
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_Name
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_Phone
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_where
import kotlinx.android.synthetic.main.activity_update_found.*
import java.text.SimpleDateFormat
import java.util.*

var selectedImageFileUri: Uri? = null
var imageUrl: String? =null

class updateFound : AppCompatActivity() {
    private lateinit var doc_id:String
    override fun onCreate(savedInstanceState: Bundle?) {

        val mycalender= Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            mycalender.set(Calendar.YEAR,i)
            mycalender.set(Calendar.MONTH,i2)
            mycalender.set(Calendar.DAY_OF_MONTH,i3)
            updateLabel(mycalender)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_found)

        findViewById<ImageView>(R.id.my_Found_when).setOnClickListener{
            DatePickerDialog(this,datePicker,mycalender.get(Calendar.YEAR),mycalender.get(Calendar.MONTH),mycalender.get(Calendar.DAY_OF_MONTH)).show()
        }


        val bundle:Bundle?=intent.extras
        val name=bundle!!.getString("name")
        val phone=bundle!!.getString("phone")
        val place=bundle!!.getString("place")
        val date=bundle!!.getString("date")
        val keyword=bundle!!.getString("keyword")
        val desc=bundle!!.getString("desc")
        val image=bundle!!.getString("image")
        doc_id= bundle!!.getString("doc_id").toString()
//        Toast.makeText(this, "$name 123",Toast.LENGTH_LONG).show()
        Lost_Name.setText(name)
        Lost_Phone.setText(phone)
        Lost_where.setText(place)
        Lost_Msg.setText(desc)
        my_found_when_text.setText(date)
        my_found_item.setText(keyword)
        if (image != null) {
            imageUrl=image.toString()
        }
        update_found_item.setOnClickListener {
            if(imageUrl!=null && selectedImageFileUri==null){
//                Toast.makeText(this,"Please select an image",Toast.LENGTH_SHORT).show()
                updatefound(imageUrl!!.toString())
            }
            else if(selectedImageFileUri!=null){
                uploadUserImage()
            }
        }
       update_image_found.setOnClickListener {
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

    private fun updateLabel(mycalender: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat,Locale.UK)
        findViewById<TextView>(R.id.my_found_when_text).setText(sdf.format(mycalender.time))

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
    }
    fun updatefound(image:String)
    {
        val name = Lost_Name.text.toString()
        val phone = Lost_Phone.text.toString().trim { it <= ' ' }
        val place = Lost_where.text.toString()
        val description = Lost_Msg.text.toString()
        if (validateLostForm(name, phone, place, description)) {
//            showProgressDialog("Please Wait")
            FirebaseFirestore.getInstance().collection("founditem").document(doc_id)
                .update("name", Lost_Name.text.toString())
            FirebaseFirestore.getInstance().collection("founditem").document(doc_id)
                .update("phone", Lost_Phone.text.toString())
            FirebaseFirestore.getInstance().collection("founditem").document(doc_id)
                .update("place", Lost_where.text.toString())
            FirebaseFirestore.getInstance().collection("founditem").document(doc_id)
                .update("description", Lost_Msg.text.toString())
            FirebaseFirestore.getInstance().collection("founditem").document(doc_id)
                .update("date_time", my_found_when_text.text.toString())
            FirebaseFirestore.getInstance().collection("founditem").document(doc_id)
                .update("item_lost", my_found_item.text.toString())
            FirebaseFirestore.getInstance().collection("founditem").document(doc_id)
                .update("image", image.toString()).addOnSuccessListener {
                    Toast.makeText(this,"Updates are successful",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MyFoundActivity::class.java))
                    this.finish()
                }
        }
    }


    private fun validateLostForm(
        name: String,
        phone: String,
        where: String,
        descprition: String
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
            TextUtils.isEmpty(where.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter The place of Lost.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(descprition.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Description Of Item Lost.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
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
                        updatefound(imageUrl!!)
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
}