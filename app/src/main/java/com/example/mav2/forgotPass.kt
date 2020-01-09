package com.example.mav2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_forgot_pass.*
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast


class forgotPass : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        supportActionBar?.title = "Forgot Password"



        submit_butt.setOnClickListener {

            FirebaseAuth.getInstance().sendPasswordResetEmail(reset_email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("Email Sent")
                    }
                }


            finish()
        }

    }
}
