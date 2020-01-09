package com.example.mav2

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mav2.`class`.user
import com.example.mav2.databinding.UpdateProfileFragmentBinding
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception


class UpdateProfile : Fragment() {

    companion object {
        fun newInstance() = UpdateProfile()
    }

    private lateinit var viewModel: UpdateProfileViewModel

    private lateinit var binding: UpdateProfileFragmentBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.update_profile_fragment,
            container,
            false
        )
        Log.i("UpdateProfileFragment", "Called ViewModelProviders.of")

        mAuth = FirebaseAuth.getInstance()
        initValue()

        binding.btnSave.setOnClickListener { getPassword() }




        return binding.root
    }

    private fun getPassword(){

        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("To update please enter your password")
        val input = EditText(this.context)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Confirm"){dialog, which ->
            val password = input.text.toString()
            if(password!="")
                SaveUpdatedToDatabse(password)
            else{
                Toast.makeText(this.requireContext(),"Please enter your password", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()


    }

    private fun SaveUpdatedToDatabse(password:String){
        val mail = mAuth.currentUser?.email
        val ref = FirebaseDatabase.getInstance().reference


        if(binding.editText.text.toString()==""||binding.editText2.text.toString()==""||binding.editText3.text.toString()==""){
            Toast.makeText(this.requireContext(),"Please fill up the form", Toast.LENGTH_SHORT).show()
        }
        else{

                val credentials = EmailAuthProvider.getCredential(binding.editText2.text.toString(),password)
                mAuth.currentUser?.reauthenticate(credentials)?.addOnCompleteListener{
                    if(it.isSuccessful){
                        try{
                            mAuth.uid?.let { ref.child("Users").child(it).child("username").setValue(binding.editText.text.toString()) }
                            mAuth.uid?.let { ref.child("Users").child(it).child("email").setValue(binding.editText2.text.toString()) }
                            mAuth.uid?.let { ref.child("Users").child(it).child("phnum").setValue(binding.editText3.text.toString()) }
                            mAuth.currentUser?.updateEmail(binding.editText2.text.toString())

                            Toast.makeText(this.requireContext(),"Profile updated", Toast.LENGTH_SHORT).show()
                         }catch (e:Exception){
                            e.printStackTrace()
                            Toast.makeText(this.requireContext(),"Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(this.requireContext(),"Incorrect password", Toast.LENGTH_SHORT).show()
                    }

                    }


        }

    }

    private fun initValue() {

        val ref = FirebaseDatabase.getInstance().getReference("/Users").orderByChild("user_id").equalTo(mAuth.currentUser?.uid)

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    val userDetail = it.getValue(user::class.java)

                    if(userDetail!=null){
                        binding.editText.setText(userDetail.username)
                        binding.editText2.setText(userDetail.email)
                        binding.editText3.setText(userDetail.phnum)
                    }
                }
            }
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UpdateProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
