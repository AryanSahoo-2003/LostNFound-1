package com.example.lostnfound.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.lostnfound.R
import com.example.lostnfound.firebase.firestoreclass
import com.example.lostnfound.models.Lost
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_fill_found_item.*
import kotlinx.android.synthetic.main.activity_fill_lost_item.*
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_Msg
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_Name
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_Phone
import kotlinx.android.synthetic.main.activity_fill_lost_item.Lost_where
import kotlinx.android.synthetic.main.activity_fill_lost_item.update_found
import kotlinx.android.synthetic.main.activity_update_found.*
import java.text.SimpleDateFormat
import java.util.*

class FillLostItem : BaseActivity() {
    companion  object{
        const val  READ_STORAGE_PERMISSION_CODE=1
       const val PICK_IMAGE_REQUEST_CODE=2
    }
    var test:ArrayList<String> = ArrayList()
    var alphaBeta : ArrayList<Uri> = ArrayList()
    lateinit var gallery:Intent
    var selectedImageFileUri: Uri?=null
    var imageUrl:ArrayList<String> = ArrayList()
    lateinit var courseRV: RecyclerView
    lateinit var courseAdapter: ImageSliderAdapter

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

            val name = Lost_Name.text.toString()
            val phone = Lost_Phone.text.toString().trim { it <= ' ' }
            val place = Lost_where.text.toString()
            val description = Lost_Msg.text.toString()
            val date= Lost_When_text.text.toString()
            val fitem=Lost_item.text.toString()
            if (validateLostForm(name, phone, place, description,date,fitem)) {

                imageUrl.clear()
                val uploadedImageUrlTasks: List<Task<Uri>> = ArrayList()
                lateinit var lastTask: StorageTask<UploadTask.TaskSnapshot>;
                showProgressDialog("Please wait")
                var count = 0;
                var l = alphaBeta.size - 1;
                for (i in 0..alphaBeta.size - 2) {
                    selectedImageFileUri = alphaBeta[i]
                    Log.d("aryan", selectedImageFileUri.toString())
                    val sRef: StorageReference = FirebaseStorage.getInstance().reference
                        .child(
                            "Lost_Item" + System.currentTimeMillis() + "." + getFileExt(
                                selectedImageFileUri
                            )
                        )

                    lastTask = sRef.putFile(selectedImageFileUri!!).addOnSuccessListener {
                        Log.d("aryan", "hue hue hue hue")
                        sRef.downloadUrl
                            .addOnCompleteListener {
                                imageUrl.add(it.result!!.toString())
                                Log.d("aryan", it.result!!.toString())
                            }
                    }.addOnFailureListener {
                        count++;
                    }
                }



                selectedImageFileUri = alphaBeta[l];
                val sRef: StorageReference = FirebaseStorage.getInstance().reference
                    .child(
                        "Lost_Item" + System.currentTimeMillis() + "." + getFileExt(
                            selectedImageFileUri
                        )
                    )

                sRef.putFile(selectedImageFileUri!!).addOnSuccessListener {
                    Log.d("aryan", "hue hue hue hue")
                    sRef.downloadUrl
                        .addOnCompleteListener {
                            imageUrl.add(it.result!!.toString())
                            Log.d("aryan", it.result!!.toString())
                            registerLostItem(imageUrl)
                            Toast.makeText(
                                this,
                                "Total Image uploaded : ${imageUrl.size}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }.addOnFailureListener {

                }
            }

        }
        findViewById<ImageView>(R.id.Lost_When).setOnClickListener{
           var alpha= DatePickerDialog(this,datePicker,mycalender.get(Calendar.YEAR),mycalender.get(Calendar.MONTH),mycalender.get(Calendar.DAY_OF_MONTH))
            var beta =alpha.datePicker
            beta.maxDate=System.currentTimeMillis()
            alpha.show()
        }
        findViewById<TextInputEditText>(R.id.Lost_When_text).setOnClickListener{
            var alpha= DatePickerDialog(this,datePicker,mycalender.get(Calendar.YEAR),mycalender.get(Calendar.MONTH),mycalender.get(Calendar.DAY_OF_MONTH))
            var beta =alpha.datePicker
            beta.maxDate=System.currentTimeMillis()
            alpha.show()
        }

        lost_image.setOnClickListener{
            alphaBeta.clear()
            test.clear()
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
        gallery = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
//        gallery.putExtra(Intent.EXTRA_SELECTION_LIMIT,5)
        startActivityForResult(gallery, FillFoundItem.PICK_IMAGE_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == SignupPage.PICK_IMAGE_REQUEST_CODE
        ) if(data!!.clipData!=null){;
            for (i in 0 until data.clipData!!.itemCount) {
                val imageUri = data.clipData!!.getItemAt(i).uri
                if(alphaBeta.size <5)
                {
                    alphaBeta.add(imageUri)
                }else{

                    Toast.makeText(this, "Limit of 5 images reached", Toast.LENGTH_SHORT).show()
                }
            }
            Toast.makeText(this,"Image Selected Successfully",Toast.LENGTH_SHORT).show()
        }
        else if(data?.data != null) {
            if(alphaBeta.size <5)
            {
                alphaBeta.add(data.data!!)
            }else{
                Toast.makeText(this, "Limit of 5 images reached", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(this,"Image Selected Successfully",Toast.LENGTH_SHORT).show()
        }
        var lsize = alphaBeta.size
        for(i in 0..lsize-1){
            test.add(alphaBeta[i].toString())
        }
        Log.d("aryan",test.toString())
        courseRV = findViewById<RecyclerView>(R.id.shortImgRecyclerView)
        courseRV.setHasFixedSize(true)
        courseRV.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL,false)
        val snapHelper: SnapHelper = LinearSnapHelper()
        courseRV.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(courseRV)
        courseAdapter = ImageSliderAdapter(test)

        courseRV.adapter = courseAdapter

    }


    private fun registerLostItem(image: ArrayList<String>) {
        val name = Lost_Name.text.toString()
        val phone = Lost_Phone.text.toString().trim { it <= ' ' }
        val place = Lost_where.text.toString()
        val description = Lost_Msg.text.toString()
        val date= Lost_When_text.text.toString()
        val fitem=Lost_item.text.toString()
        val userid = FirebaseAuth.getInstance().currentUser!!.uid
        val useremail = FirebaseAuth.getInstance().currentUser!!.email

        var newdate=""
        newdate+=date.substring(6,10)+"-"+date.substring(3,5)+"-"+date.substring(0,2)


            val lost = useremail?.let { Lost(name, phone, place, description, image, userid, it,newdate,fitem,"","","", false,FieldValue.serverTimestamp()) }
            if (lost != null) {
                firestoreclass().registerLostItem(this, lost)
            }

    }

    private fun validateLostForm(
        name:String,
        phone:String,
        where:String,
        descprition:String,
        date:String,
        litem:String):Boolean {
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
                    "Please Enter Lost Item.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(date.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Date.",
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
            registerLostItem(imageUrl)
        }

    fun LostitemRegistered(){
        hideProgressDialog()
        Toast.makeText(this,
            "Your item has been successfully Uploaded", Toast.LENGTH_LONG).show()
         startActivity((Intent(this,Tabs::class.java)))
        this.finish()
    }

    private fun getFileExt(uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}