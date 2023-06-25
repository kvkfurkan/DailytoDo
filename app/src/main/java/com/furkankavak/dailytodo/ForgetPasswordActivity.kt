package com.furkankavak.dailytodo

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.furkankavak.dailytodo.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityForgetPasswordBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.resetPassButton.setOnClickListener {
            resetPass()
        }

    }

    private fun resetPass(){
        val mail = binding.enterEmail.text.toString()
        auth.sendPasswordResetEmail(mail)
            .addOnSuccessListener {
                    infoDialog()
                    val intent = Intent(this@ForgetPasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
            }.addOnFailureListener {
                Toast.makeText(this@ForgetPasswordActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    private fun infoDialog(){
        val dialog = Dialog(this@ForgetPasswordActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.reset_pass_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val okButton : Button = dialog.findViewById(R.id.okButton)

        okButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}