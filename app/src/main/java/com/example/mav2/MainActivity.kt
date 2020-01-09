package com.example.mav2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

//        if(mAuth.currentUser != null){
//            val intent = Intent(this,homev2Activity::class.java)
//            finish()
//            startActivity(intent)
//        }


        txt_forgetPass.setOnClickListener {
            val intent = Intent(this,forgotPass::class.java)
            startActivity(intent)
        }


        button_register.setOnClickListener {

            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)

        }

        button_login.setOnClickListener {
            val email = tf_email.text.toString()
            val pass = tf_password.text.toString()

            if(TextUtils.isEmpty(email)){
                tf_email.error = "Email is Require"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(pass)){
                tf_password.error = "Password is Require"
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener{
                if(!it.isSuccessful){
                    toast("Email or Password Invalid")
                    return@addOnCompleteListener
                }else{

                    val intent = Intent(this,homev2Activity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    finish()
                    startActivity(intent)


                }
            }



        }



    }
}
