package com.example.lostnfound.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lostnfound.R
import com.example.lostnfound.firebase.firestoreclass
import com.example.lostnfound.models.Lost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_fill_lost_item.*
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_Msg
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_Name
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_Phone
import kotlinx.android.synthetic.main.activity_fill_lost_item.update_found
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_where
import java.text.SimpleDateFormat
import java.util.*

class FillLostItem : BaseActivity() {
    companion  object{
        const val  READ_STORAGE_PERMISSION_CODE=1
       const val PICK_IMAGE_REQUEST_CODE=2
    }
    var selectedImageFileUri: Uri?=null
    var imageUrl:String="https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.britannica.com%2Ftopic%2FIron-Man-comic-book-character&psig=AOvVaw0_YiG1-nVaLvJs7iaeoweJ&ust=1668330857672000&source=images&cd=vfe&ved=0CBAQjRxqFwoTCKiIhPamqPsCFQAAAAAdAAAAABAD"

    override fun onCreate(savedInstanceState: Bundle?) {
        val mycalender=Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            mycalender.set(Calendar.YEAR,i)
            mycalender.set(Calendar.MONTH,i2)
            mycalender.set(Calendar.DAY_OF_MONTH,i3)
            updateLabel(mycalender)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_lost_item)
        update_found.setOnClickListener{
            if(selectedImageFileUri!=null){
                uploadUserImage()
            }
        }
        findViewById<ImageView>(R.id.Lost_When).setOnClickListener{
            DatePickerDialog(this,datePicker,mycalender.get(Calendar.YEAR),mycalender.get(Calendar.MONTH),mycalender.get(Calendar.DAY_OF_MONTH)).show()
        }
        Lost_Image_butoon.setOnClickListener{
            if(ContextCompat.checkSelfPermission(
                    this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ==PackageManager.PERMISSION_GRANTED){
                showImageChosser()

            }else{
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
        findViewById<TextView>(R.id.Lost_When_text).setText(sdf.format(mycalender.time))

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== SignupPage.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                showImageChosser()
            }
        }else{
            Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
        }
    }
    private fun showImageChosser(){
        val gallery=Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(gallery, SignupPage.PICK_IMAGE_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK
            && requestCode== SignupPage.PICK_IMAGE_REQUEST_CODE
            && data!!.data!=null){
            selectedImageFileUri=data.data
            Toast.makeText(this,"Image Selected Successfully",Toast.LENGTH_SHORT).show()
        }
    }
    private fun registerLostItem(image:String){
        val name=Lost_Name.text.toString()
        val phone=Lost_Phone.text.toString().trim{it <=' '}
        val place=Lost_where.text.toString()
        val date=Lost_When_text.text.toString()
        val litem=Lost_item.text.toString()
        val description=Lost_Msg.text.toString()

        var newdate=""
        newdate+=date.substring(6,10)+"-"+date.substring(3,5)+"-"+date.substring(0,2)

        val userid= FirebaseAuth.getInstance().currentUser!!.uid
        val useremail= FirebaseAuth.getInstance().currentUser!!.email
        if(validateLostForm(name,phone,place,litem,date,description)){
            showProgressDialog("Please Wait")
             val lost= useremail?.let { Lost(name,phone,place,description,image,userid, it,newdate,litem,"", FieldValue.serverTimestamp()) }
            if (lost != null) {
                firestoreclass().registerLostItem(this,lost)
            }
        }
    }

    private fun validateLostForm(name:String,phone:String,where:String,litem:String,date:String,descprition:String):Boolean {
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
            (phone.length!=10)->{
                Toast.makeText(
                    this,
                    "Please Enter valid Mobile Number.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(litem.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Your Mobile Number.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(date.trim { it <= ' ' }) -> {
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
            } TextUtils.isEmpty(descprition.trim { it <= ' ' }) -> {
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
                    "Lost_Item" + System.currentTimeMillis() + "." + getFileExt(
                        selectedImageFileUri
                    )
                )
            sRef.putFile(selectedImageFileUri!!).addOnSuccessListener {

                sRef.downloadUrl
                    .addOnCompleteListener {
                        imageUrl = it.result!!.toString()
                        registerLostItem(imageUrl)
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
    fun LostitemRegistered(){
        hideProgressDialog()
        Toast.makeText(this,
            "Your item has been successfully Uploaded", Toast.LENGTH_LONG).show()
         startActivity((Intent(this,LostActivity::class.java)))
        this.finish()
    }

    private fun getFileExt(uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}