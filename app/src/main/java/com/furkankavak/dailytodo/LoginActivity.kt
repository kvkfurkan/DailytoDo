package com.furkankavak.dailytodo

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.furkankavak.dailytodo.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth


    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        val signUp = binding.signUp
        val logIn = binding.logIn
        val logInButton = binding.logInButton
        val signUpButton = binding.signUpButton
        val forgetPassword = binding.forgetPass

        val currentUser = auth.currentUser
        if(currentUser != null) {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }



        signUp.setOnClickListener {
           setSignUp()
        }

        logIn.setOnClickListener {
            setLogIn()
        }

        signUpButton.setOnClickListener {
            signUpFun()
        }

        logInButton.setOnClickListener {
            logInFun()
        }

        forgetPassword.setOnClickListener {
            forgetPass()
        }

    }


    private fun signUpFun(){
        val email = binding.eMailSignUp.text.toString().trim()
        val pass = binding.passwordSignup.text.toString().trim()
        val verifyPass = binding.passwordConfirm.text.toString().trim()

        if(email.isNotEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()){
            if(pass == verifyPass){
                auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            infoDialog()
                            setLogIn()
                        }?.addOnFailureListener {
                            Toast.makeText(this@LoginActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener {
                    Toast.makeText(this@LoginActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(applicationContext, "Password is not same", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(applicationContext, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logInFun(){
        val email = binding.eMailLogin.text.toString().trim()
        val pass = binding.passwordLogin.text.toString().trim()

        if(email.isNotEmpty() && pass.isNotEmpty()){
            auth.signInWithEmailAndPassword(email,pass).addOnSuccessListener {
               val verification = auth.currentUser?.isEmailVerified
                if(verification == true){
                    binding.progressBar.visibility = View.VISIBLE
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    infoDialog()
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setSignUp(){
        binding.signUp.background = ResourcesCompat.getDrawable(resources, R.drawable.switch_trcks, null)
        binding.signUp.setTextColor(ResourcesCompat.getColor(resources, R.color.textColor, null))
        binding.logIn.background = null
        binding.signUpLayout.visibility = View.VISIBLE
        binding.logInLayout.visibility = View.GONE
        binding.logInButton.visibility = View.GONE
        binding.signUpButton.visibility = View.VISIBLE
        binding.logIn.setTextColor(ResourcesCompat.getColor(resources, R.color.blue, null))
    }

    private fun setLogIn(){
        binding.signUp.background = null
        binding.signUp.setTextColor(ResourcesCompat.getColor(resources, R.color.blue, null))
        binding.logIn.background = ResourcesCompat.getDrawable(resources, R.drawable.switch_trcks, null)
        binding.signUpLayout.visibility = View.GONE
        binding.logInLayout.visibility = View.VISIBLE
        binding.signUpButton.visibility = View.GONE
        binding.logInButton.visibility = View.VISIBLE
        binding.logIn.setTextColor(ResourcesCompat.getColor(resources, R.color.textColor, null))
    }

    private fun infoDialog(){
        val dialog = Dialog(this@LoginActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.info_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val okButton : Button = dialog.findViewById(R.id.okButton)

        okButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun forgetPass(){
        val intent = Intent(this@LoginActivity, ForgetPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }


}