package com.example.mav2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_homev2.*

class homev2Activity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener  = BottomNavigationView.OnNavigationItemSelectedListener {
            item->when(item.itemId){
        R.id.nav_home ->{
            launchHomeFragment()
            return@OnNavigationItemSelectedListener true
        }
        R.id.nav_newActivity ->{
            launchAddActivityFragment()
            return@OnNavigationItemSelectedListener true
        }
        R.id.nav_myProfile ->{
        launchMyProfileFragment()
            return@OnNavigationItemSelectedListener true
        }
    }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homev2)
        launchHomeFragment()
        bottom_navigationV2.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)



    }

         fun launchHomeFragment(){
        val trans = supportFragmentManager.beginTransaction()
        val fragment = homeview()
        trans.replace(R.id.fragment_holder,fragment)
        trans.addToBackStack(null)
        trans.commit()

    }

    fun launchAddActivityFragment(){
        val trans = supportFragmentManager.beginTransaction()
        val fragment = addactivity()
        trans.replace(R.id.fragment_holder,fragment)
        trans.addToBackStack(null)
        trans.commit()

    }
    fun launchMyProfileFragment(){
        val trans = supportFragmentManager.beginTransaction()
        val fragment = MyProfile()
        trans.replace(R.id.fragment_holder,fragment)
        trans.addToBackStack(null)
        trans.commit()

    }


}
