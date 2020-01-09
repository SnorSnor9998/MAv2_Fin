package com.example.mav2

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.mav2.databinding.UpdatePasswordFragmentBinding
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.update_password_fragment.*


class UpdatePassword : Fragment() {



    companion object {
        fun newInstance() = UpdatePassword()
    }


    private lateinit var viewModel: UpdatePasswordViewModel

    private lateinit var binding: UpdatePasswordFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.update_password_fragment,
            container,
            false
        )
        Log.i("UpdatePasswordFragment", "Called ViewModelProviders.of")

        viewModel = ViewModelProviders.of(this).get(UpdatePasswordViewModel::class.java)

        // Set the viewmodel for databinding - this allows the bound layout access
        // to all the data in the VieWModel
        binding.updatePasswordViewModel = viewModel

        // Specify the current activity as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates

        binding.lifecycleOwner = this

        binding.btnAuthentication.setOnClickListener { showChangePasswordLayout() }





        return binding.root
    }

    fun showChangePasswordLayout(){
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email.toString()
        val password = binding.authenticationField.text.toString()
        if(password!=""){
            val credentials = EmailAuthProvider.getCredential(email,password)

            user?.reauthenticate(credentials)?.addOnCompleteListener{
                if(it.isSuccessful){
                    binding.authenticationLayout.isVisible = false
                    binding.updatePasswordLayout.isVisible = true
                    binding.btnSavePassword.setOnClickListener{
                        val newPassword = binding.editText5.text.toString()
                        val newPassword2 = binding.editText6.text.toString()

                        if(newPassword==""||newPassword2==""){
                            Toast.makeText(this.requireContext(),"Please fill up the form", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            if(newPassword==newPassword2){
                                user?.updatePassword(newPassword)
                                Toast.makeText(this.requireContext(),"Password updated", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                Toast.makeText(this.requireContext(),"Password doesn't match", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
                else{
                    Toast.makeText(this.requireContext(),"Incorrect password", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else{
            Toast.makeText(this.requireContext(),"Please enter your password", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UpdatePasswordViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
