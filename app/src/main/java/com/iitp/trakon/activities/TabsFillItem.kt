package com.iitp.trakon.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.iitp.trakon.R
import com.iitp.trakon.firebase.firestoreclass
import com.iitp.trakon.models.Users
import com.iitp.trakon.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.nav_header.*
import java.io.IOException

class TabsFillItem() : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private lateinit var pager: ViewPager // creating object of ViewPager
    private lateinit var tab: TabLayout // creating object of TabLayout
    private lateinit var bar: Toolbar // creating object of ToolBar
    lateinit var toggle : ActionBarDrawerToggle
    private val mFireStore= FirebaseFirestore.getInstance()
    protected lateinit var  drawerLayout : DrawerLayout
    lateinit var islogin:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs_fill_item)

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        firestoreclass().signInUser(this)

        // set the references of the declared objects above
        pager = findViewById(R.id.viewPager)
        tab = findViewById(R.id.tabs)
        val adapter = ViewPagerAdapter(supportFragmentManager)

        // add fragment to the list
        adapter.addFragment(MyFoundActivity(), "Found")
        adapter.addFragment(MyLostActivity(), "Lost")

        // Adding the Adapter to the ViewPager
        pager.adapter = adapter

        // bind the viewPager with the TabLayout.
        tab.setupWithViewPager(pager)
        drawerLayout = findViewById(R.id.navigationBar)
        val navView : NavigationView = findViewById(R.id.navView)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
        val icon = toolbar.navigationIcon
        if(icon?.isVisible() == true){
            icon.setVisible(false,false)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var putha:Users
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
//                    islogin="0"
                    startActivity(Intent(this,MainActivity::class.java))

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
                var sharedPreferences:SharedPreferences=getSharedPreferences("logindata", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,MainActivity::class.java))
//                islogin="0"
            }
            R.id.my_update_profile-> alpha()
        }
        drawerLayout.closeDrawers()
        return true
    }
}
