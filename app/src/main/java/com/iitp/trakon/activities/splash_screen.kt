package com.iitp.trakon.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.iitp.trakon.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlin.properties.Delegates

class splash_screen : AppCompatActivity() {
    private var valid_user by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lostLottie.playAnimation()
        lostLottie.speed=1f
        foundLottie.playAnimation()
        foundLottie.speed=1f
        val handler = Handler()

        handler.postDelayed({ testToast() }, 8000)

        FirebaseFirestore.getInstance().collection("founditem").get().addOnSuccessListener {
            for(document in it){
                var time: Timestamp= document.data["timestamp"] as Timestamp

                if((-(time.seconds*1000+time.nanoseconds/1000000) + System.currentTimeMillis()) > 7776000000){

                    FirebaseFirestore.getInstance().collection("founditem").document(document.id).delete()
                }
            }
        }

        FirebaseFirestore.getInstance().collection("lostitem").get().addOnSuccessListener {
            for(document in it){
                var time: Timestamp= document.data["timestamp"] as Timestamp

                if((-(time.seconds*1000+time.nanoseconds/1000000) + System.currentTimeMillis()) > 7776000000){

                    FirebaseFirestore.getInstance().collection("founditem").document(document.id).delete()
                }
            }
        }


    }
    fun testToast(){
        if(!checkForInternet(this)){
            Toast.makeText(this,"Please connect to the internet :<",Toast.LENGTH_SHORT).show()
            System.exit(0)
        }
        var sharedPreferences: SharedPreferences =getSharedPreferences("logindata", MODE_PRIVATE);
        var editorShared: SharedPreferences.Editor=sharedPreferences.edit();
        var counter:Boolean=sharedPreferences.getBoolean("logincounter", MODE_PRIVATE.toString().toBoolean());
        var emailUser: String? =sharedPreferences.getString("useremail", MODE_PRIVATE.toString())
        var uidUser: String? =sharedPreferences.getString("uidUser", MODE_PRIVATE.toString())

        valid_user=true
        Log.d("sahoo",uidUser.toString())

//        sharedPreferences.edit().clear().apply()
//        sharedPreferences.edit().clear().commit()

            if (counter) {
                FirebaseFirestore.getInstance().collection("users").document(uidUser.toString()).get().addOnSuccessListener {
                    valid_user = it.data?.get("valid") as Boolean
                    if (valid_user) {
                        startActivity(Intent(this, Tabs::class.java))
                        finish()
                        Log.d("loginStatus", "true/false....Splash_Tabs")
                    } else {
                        Toast.makeText(this, "You have been blocked by admin :(", Toast.LENGTH_LONG)
                            .show()
                        finish()
                        System.exit(0)
                    }
                }
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                Log.d("loginStatus", "true/false....Splash_Main")
                finish()
            }

    }
    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

}