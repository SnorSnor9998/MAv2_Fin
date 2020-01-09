package com.example.mav2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mav2.`class`.user
import com.example.mav2.databinding.MyProfileFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MyProfile : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    companion object {
        fun newInstance() = MyProfile()
    }

    private lateinit var viewModel: MyProfileViewModel

    private lateinit var binding:MyProfileFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.my_profile_fragment,
            container,
            false
        )
        Log.i("MyProfileFragment", "Called ViewModelProviders.of")

        viewModel = ViewModelProviders.of(this).get(MyProfileViewModel::class.java)

        binding.myProfileViewModel = viewModel

        mAuth = FirebaseAuth.getInstance()
        binding.lifecycleOwner = this

        getUserName()
        binding.txtUserMail.text = mAuth.currentUser?.email

        binding.btnLogout.setOnClickListener { logout() }

        binding.btnChangePassword.setOnClickListener { startUpdatePassword() }
        binding.btnEditProfile.setOnClickListener { startEditProfile() }
        binding.btnMyActivity.setOnClickListener { startMyActivity() }


        return binding.root
    }

    private fun startEditProfile(){
        var fr = fragmentManager?.beginTransaction()
        fr?.replace(R.id.fragment_holder, UpdateProfile())
        fr?.commit()
    }

    private fun startUpdatePassword(){
        var fr = fragmentManager?.beginTransaction()
        fr?.replace(R.id.fragment_holder, UpdatePassword())
        fr?.commit()

    }

    private fun startMyActivity(){

        var fr = fragmentManager?.beginTransaction()
        var home = myAcitivity()
        var bundle = Bundle()
        bundle.putInt("isProfile",1)
        home.arguments = bundle

        fr?.replace(R.id.fragment_holder,home)
        fr?.commit()
    }

    private fun getUserName() {

        val ref = FirebaseDatabase.getInstance().getReference("/Users").orderByChild("user_id").equalTo(mAuth.currentUser?.uid)

        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    val userDetail = it.getValue(user::class.java)

                    if(userDetail!=null){
                        binding.txtUserName.text = userDetail.username
                    }
                }


            }

        })

    }



    private fun logout(){
        mAuth.signOut()
        val intent = Intent(activity,MainActivity::class.java)
        intent.setFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)


    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
