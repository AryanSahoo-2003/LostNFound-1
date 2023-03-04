package com.iitp.trakon.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iitp.trakon.R
import com.iitp.trakon.models.Users
import com.iitp.trakon.utils.Constants
import kotlinx.android.synthetic.main.activity_privacy_policy.*
import kotlinx.android.synthetic.main.nav_header.*
import java.io.IOException

class PrivacyPolicy : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toggle : ActionBarDrawerToggle
    private val mFireStore= FirebaseFirestore.getInstance()
    protected lateinit var  drawerLayout : DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        var alpha= privacy_web
        alpha.webViewClient= WebViewClient()

        if(checkForInternet(this))
        {
            alpha.loadUrl("https://stc.iitp.ac.in/TrackOn/Privacy.html")
        }
        else{
            alpha.loadUrl("file:///android_asset/no_internet.html")
        }


        alpha.settings.javaScriptEnabled=true
        alpha.settings.builtInZoomControls=true;
        alpha.settings.displayZoomControls=false
        alpha.settings.allowFileAccess=false
        alpha.settings.domStorageEnabled=true;
        alpha.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        alpha.settings.saveFormData=true;
        alpha.settings.useWideViewPort=true;
        alpha.settings.savePassword=true;
        alpha.settings.cacheMode=WebSettings.LOAD_DEFAULT
        alpha.settings.setGeolocationEnabled(true)
        alpha.settings.allowContentAccess=true
        alpha.settings.loadsImagesAutomatically=true
        alpha.scrollBarStyle= View.SCROLLBARS_INSIDE_OVERLAY;



    drawerLayout = findViewById(R.id.navigationBar)
    val navView : NavigationView = findViewById(R.id.navView)
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

    toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close)
    drawerLayout.addDrawerListener(toggle)

    toggle.syncState()
    val icon = toolbar.navigationIcon
    icon?.setVisible(false,false)

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    //
    var putha: Users
    mFireStore.collection(Constants.USERS)
    .document(getcurrentUserID()).get().addOnSuccessListener { document ->
        putha = document.toObject(Users::class.java)!!
        name_display.text=putha.name;
        roll_display.text=putha.roll;
        InstituteEmail.text=putha.email
        try {
            Glide     //using Glide to display image from url
                .with(this)
                .load(putha.image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(Profile_pic_image);
        }catch (e: IOException){
            e.printStackTrace()
        }

    }

    //



    navView.setNavigationItemSelectedListener {

        when(it.itemId)
        {
            R.id.check_found_item-> startActivity(Intent(this,Tabs::class.java))
            R.id.update_button_butoon-> startActivity(Intent(this,UpdatePassword::class.java))
            R.id.my_found_post_button-> startActivity(Intent(this,TabsFillItem::class.java))
            R.id.archive_item-> startActivity(Intent(this,ArchieveTab::class.java))
            R.id.logout_profile-> {
                var sharedPreferences: SharedPreferences =getSharedPreferences("logindata", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,MainActivity::class.java))
//                islogin="0"
            }
            R.id.privacy_policy->{
                startActivity(Intent(this,PrivacyPolicy::class.java))
            }
            R.id.my_update_profile-> alpha()
        }
        drawerLayout.closeDrawers()
        true

    }




}
fun alpha() {
    var loggedInUser: Users
    var beta: String
    mFireStore.collection(Constants.USERS)
        .document(getcurrentUserID()).get().addOnSuccessListener { document ->
            loggedInUser = document.toObject(Users::class.java)!!
            beta = document.id

            setUpdate(beta, loggedInUser)
            setDetails(loggedInUser)
        }
}

fun setUpdate(uid:String,user: Users)
{

    val name=user.name
    val phone=user.mobile
    val whatsapp=user.whatsapp
    val image=user.image
    val roll=user.roll

    val intent = Intent(this, Updateprofile::class.java)
    //listener?.onClick(AlbumsData)
    intent.putExtra("name", name)
    intent.putExtra("phone", phone)
    intent.putExtra("roll", roll)
    intent.putExtra("whatsapp", whatsapp)
    intent.putExtra("image", image)
    intent.putExtra("uid", uid)
    startActivity(intent)
}

fun setDetails(user: Users)
{

    name_display.text=user.name;
    roll_display.text=user.roll;
    InstituteEmail.text=user.email
    try {
        Glide     //using Glide to display image from url
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(Profile_pic_image);
    }catch (e: IOException){
        e.printStackTrace()
    }
}

private fun getcurrentUserID():String{
    return FirebaseAuth.getInstance().currentUser!!.uid
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if(toggle.onOptionsItemSelected(item)){
        return true
    }
    return super.onOptionsItemSelected(item)
}
override fun onBackPressed(){
    startActivity(Intent(this,Tabs::class.java))
}

override fun onNavigationItemSelected(item: MenuItem): Boolean {
    when(item.itemId)
    {
        R.id.check_found_item-> startActivity(Intent(this,FoundActivity::class.java))
        R.id.update_button_butoon-> startActivity(Intent(this,UpdatePassword::class.java))
        R.id.my_found_post_button-> startActivity(Intent(this,MyFoundActivity::class.java))
        R.id.logout_profile-> {
            var sharedPreferences: SharedPreferences =getSharedPreferences("logindata",
                AppCompatActivity.MODE_PRIVATE
            );
            sharedPreferences.edit().clear().commit();
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,MainActivity::class.java))
//                islogin="0"
        }
        R.id.privacy_policy->{
            startActivity(Intent(this,PrivacyPolicy::class.java))
        }
        R.id.my_update_profile-> alpha()
    }
    drawerLayout.closeDrawers()
    return true
}
    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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