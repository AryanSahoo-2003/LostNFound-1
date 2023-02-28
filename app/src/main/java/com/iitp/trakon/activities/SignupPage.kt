package com.iitp.trakon.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.Glide
import com.iitp.trakon.R
import com.iitp.trakon.firebase.firestoreclass
import com.iitp.trakon.models.Users
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.new_signup.*


class SignupPage : BaseActivity() {
    companion object {
        const val READ_STORAGE_PERMISSION_CODE = 1
        const val PICK_IMAGE_REQUEST_CODE = 2
        const val TAG = "SignupPageTag";
    }

    private val auth = FirebaseAuth.getInstance()
    var selectedImageFileUri: Uri? = null
    var imageUrl: String =
        "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.britannica.com%2Ftopic%2FIron-Man-comic-book-character&psig=AOvVaw0_YiG1-nVaLvJs7iaeoweJ&ust=1668330857672000&source=images&cd=vfe&ved=0CBAQjRxqFwoTCKiIhPamqPsCFQAAAAAdAAAAABAD"
    private lateinit var name: String
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.new_signup)
        findViewById<TextInputEditText>(R.id.register_password).doOnTextChanged { text, start, before, count ->
            if (text!!.length < 6) {
                Signup_button.isEnabled = false
                findViewById<TextInputLayout>(R.id.password_layout).isHelperTextEnabled = true
                Signup_button.setBackgroundColor(Color.BLACK)
            } else {
                Signup_button.setBackgroundColor(Color.rgb(18, 37, 143))
                Signup_button.isEnabled = true
                findViewById<TextInputLayout>(R.id.password_layout).isHelperTextEnabled = false

            }
        }


        new_signup_terms_condition_checkbox.setOnClickListener {
            // Displaying privacy policy
            if(new_signup_terms_condition_checkbox.isChecked) {
                displayPrivacyDialog();
            }
        }

        new_signup_terms_condition_link.setOnClickListener {
            // Displaying privacy policy
            displayPrivacyDialog();
        }

        Signup_button.setOnClickListener {

            if (!new_signup_terms_condition_checkbox.isChecked) {
                Toast.makeText(
                    this,
                    "Please accept the Terms and Conditions to proceed!!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "onCreate: Tnc not accepted yet!!")
                return@setOnClickListener;
            }

//            showProgressDialog("")
            name = register_name.text.toString()
            val roll = register_roll.text.toString().trim { it <= ' ' }
            val email = register_insti_email.text.toString().trim { it <= ' ' }
            val password = register_password.text.toString().trim { it <= ' ' }
            val confirm = register_confirm_password.text.toString().trim { it <= ' ' }
            val whatsapp = register_whatsapp.text.toString().trim { it <= ' ' }
            val mobile = register_mobile.text.toString().trim { it <= ' ' }


            if (validateForm(name, roll, email, password, confirm, whatsapp, mobile)) {
                if (register_password.text!!.length < 6) {
                    Toast.makeText(
                        this,
                        "Password must contain atleast 6 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (selectedImageFileUri != null) {
                    showProgressDialog("Please Wait")
                    uploadUserImage()
                } else {
                    showProgressDialog("Please Wait")
                    registerUser("")
                }
            }
        }
        findViewById<Button>(R.id.Upload_profile_pic_button).setOnClickListener {
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
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    /*
        Function to display privacy policy dialog to user
     */
    private fun displayPrivacyDialog() {
        Log.d(TAG, "displayPrivacyDialog: Inside")

        // Creating AlertDialog Builder
        var alertBuilder = AlertDialog.Builder(this);
        alertBuilder.setTitle("Privacy Policy")
        alertBuilder.setMessage("Privacy Policy\n" +
                "IIT Patna built the TrackOn app as a Free app. This SERVICE is provided by IIT Patna at no cost and is intended for use as is.\n" +
                "\n" +
                "This page is used to inform visitors regarding our policies with the collection, use, and disclosure of Personal Information if anyone decided to use our Service.\n" +
                "\n" +
                "If you choose to use our Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that we collect is used for providing and improving the Service. We will not use or share your information with anyone except as described in this Privacy Policy.\n" +
                "\n" +
                "The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which are accessible at TrackOn unless otherwise defined in this Privacy Policy.\n" +
                "\n" +
                "Information Collection and Use\n" +
                "\n" +
                "For a better experience, while using our Service, we may require you to provide us with certain personally identifiable information. The information that we request will be retained by us and used as described in this privacy policy.\n" +
                "\n" +
                "The app does use third-party services that may collect information used to identify you.\n" +
                "\n" +
                "Link to the privacy policy of third-party service providers used by the app\n" +
                "\n" +
                "Google Play Services\n" +
                "Firebase Crashlytics\n" +
                "Log Data\n" +
                "\n" +
                "We want to inform you that whenever you use our Service, in a case of an error in the app we collect data and information (through third-party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilizing our Service, the time and date of your use of the Service, and other statistics.\n" +
                "\n" +
                "Cookies\n" +
                "\n" +
                "Cookies are files with a small amount of data that are commonly used as anonymous unique identifiers. These are sent to your browser from the websites that you visit and are stored on your device's internal memory.\n" +
                "\n" +
                "This Service does not use these “cookies” explicitly. However, the app may use third-party code and libraries that use “cookies” to collect information and improve their services. You have the option to either accept or refuse these cookies and know when a cookie is being sent to your device. If you choose to refuse our cookies, you may not be able to use some portions of this Service.\n" +
                "\n" +
                "Service Providers\n" +
                "\n" +
                "We may employ third-party companies and individuals due to the following reasons:\n" +
                "\n" +
                "To facilitate our Service;\n" +
                "To provide the Service on our behalf;\n" +
                "To perform Service-related services; or\n" +
                "To assist us in analyzing how our Service is used.\n" +
                "We want to inform users of this Service that these third parties have access to their Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.\n" +
                "\n" +
                "Security\n" +
                "\n" +
                "We value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and we cannot guarantee its absolute security.\n" +
                "\n" +
                "Links to Other Sites\n" +
                "\n" +
                "This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by us. Therefore, we strongly advise you to review the Privacy Policy of these websites. We have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.\n" +
                "\n" +
                "Children’s Privacy\n" +
                "\n" +
                "These Services do not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13 years of age. In the case we discover that a child under 13 has provided us with personal information, we immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact us so that we will be able to do the necessary actions.\n" +
                "\n" +
                "Changes to This Privacy Policy\n" +
                "\n" +
                "We may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. We will notify you of any changes by posting the new Privacy Policy on this page.\n" +
                "\n" +
                "This policy is effective as of 2023-02-23\n" +
                "\n" +
                "Contact Us\n" +
                "\n" +
                "If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us at stc.iitp@gmail.com.\n" +
                "\n" +
                "This privacy policy page was created at privacypolicytemplate.net and modified/generated by App Privacy Policy Generator")

        alertBuilder.setPositiveButton("Close") { dialogInterface, which ->
            dialogInterface.dismiss();
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = alertBuilder.create()

        // Set other dialog properties
        alertDialog.setCancelable(false)

        // Displaying Alert dialog
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
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
        startActivityForResult(gallery, PICK_IMAGE_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            selectedImageFileUri = data.data
            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show()
        }
        Glide.with(this)
            .load(selectedImageFileUri)
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(showImageSignup)
    }

    private fun registerUser(image: String) {
        name = register_name.text.toString()
        val roll = register_roll.text.toString().trim { it <= ' ' }
        val email = register_insti_email.text.toString().trim { it <= ' ' }
        val password = register_password.text.toString().trim { it <= ' ' }
        val confirm = register_confirm_password.text.toString().trim { it <= ' ' }
        val whatsapp = register_whatsapp.text.toString().trim { it <= ' ' }
        val mobile = register_mobile.text.toString().trim { it <= ' ' }



        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener() {

                hideProgressDialog()
                if (it.isSuccessful) {

                    val user = Users(name, roll, email, whatsapp, mobile, image)
                    firestoreclass().registerUser(this, user)
                    auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Please Verify your email to Login. A mail has been sent to your email",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {

//                    TODO("handle various exceptions")
                    when (it.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            Toast.makeText(
                                this,
                                "User is already registered :|", Toast.LENGTH_SHORT
                            ).show()
                        }
                        is FirebaseNetworkException -> {
                            Toast.makeText(
                                this,
                                "Poor internet connection :(", Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Toast.makeText(
                                this,
                                it.exception.toString(), Toast.LENGTH_SHORT
                            ).show()

                        }
                    }


                }
            })
        // Your Firebase Firestore code here
//        } catch (e: FirebaseFirestoreException) {
//            when (e.code) {
//                FirebaseFirestoreException.Code.ALREADY_EXISTS -> {
//                    // Handle the collision exception and provide a custom message
//                    Toast.makeText(this,"Firestore : Email already exists",Toast.LENGTH_SHORT).show()
//                }
//                else -> {
//                    Toast.makeText(this,e.code.toString(),Toast.LENGTH_SHORT).show()
//                    // Handle other exceptions
//                    Log.e("Firestore", "An error occurred: ${e.message}")
//                }
//            }
//        }


    }

    private fun validateForm(
        name: String,
        roll: String,
        email: String,
        password: String,
        confirm: String,
        whatsapp: String,
        mobile: String
    ): Boolean {
        return when {//checking if the field are empty
            TextUtils.isEmpty(name.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@SignupPage,
                    "Please Enter Your Name.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(roll.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@SignupPage,
                    "Please Enter Your Roll Number.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(password.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@SignupPage,
                    "Please Enter Your Password.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(confirm.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@SignupPage,
                    "Please Confirm Password.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            (mobile.length != 10) -> {
                Toast.makeText(
                    this,
                    "Please Enter valid Mobile Number.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            (whatsapp.length != 10) -> {
                Toast.makeText(
                    this,
                    "Please Enter valid Whatsapp Number.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(email.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@SignupPage,
                    "Please Enter Your Email.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(whatsapp.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@SignupPage,
                    "Please Enter Your Whatsapp Number.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(mobile.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@SignupPage,
                    "Please Enter Your Mobile Number.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            password.trim { it <= ' ' } != confirm.trim { it <= ' ' } -> {
                Toast.makeText(
                    this@SignupPage,
                    "Password and Confirm Password should be same",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            !email.endsWith("@iitp.ac.in") -> {
                Toast.makeText(
                    this@SignupPage,
                    "Invalid Institute Email",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }

    fun userRegisteredSuccess() {

        Toast.makeText(
            this@SignupPage,
            "You have been registered successfully", Toast.LENGTH_SHORT
        ).show()

        auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
            Toast.makeText(
                this,
                "Please Verify your email to Login. A mail has been sent to your email",
                Toast.LENGTH_LONG
            ).show()
        }
        startActivity(Intent(this, MainActivity::class.java))
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun uploadUserImage() {
//        showProgressDialog("Please Wait")
        if (selectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference
                .child(
                    "User_Image" + System.currentTimeMillis() + "." + getFileExt(
                        selectedImageFileUri
                    )
                )
            sRef.putFile(selectedImageFileUri!!).addOnSuccessListener {

                sRef.downloadUrl
                    .addOnCompleteListener {
                        imageUrl = it.result!!.toString()
                        registerUser(imageUrl)
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

    private fun getFileExt(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}


