package com.furkankavak.dailytodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.furkankavak.dailytodo.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

       binding.passwordChangeButton.setOnClickListener {
           changePassword()
       }



    }

    private fun changePassword(){

        val oldPassword = binding.oldPass.text.toString()
        val newPassword = binding.newPass.text.toString()
        val confirmPassword = binding.confirmPass.text.toString()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(oldPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (newPassword == confirmPassword){
                    if(oldPassword != newPassword){
                        val credential = EmailAuthProvider
                            .getCredential(currentUser?.email!!, oldPassword)
                        currentUser.reauthenticate(credential)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    currentUser.updatePassword(newPassword)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(this, "Password changed successfully.", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this@ChangePasswordActivity, SettingsActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }
                                        }

                                } else {
                                    Toast.makeText(this, "Re-Authentication failed.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }else{
                        Toast.makeText(this@ChangePasswordActivity, "The old password and the new password cannot be the same.", Toast.LENGTH_SHORT).show()
                    }

                }else{
                    Toast.makeText(this@ChangePasswordActivity, "Password mismatching", Toast.LENGTH_SHORT).show()
                }


        }else{
            Toast.makeText(this@ChangePasswordActivity, "Please fill the all fields", Toast.LENGTH_SHORT).show()
        }
    }
}