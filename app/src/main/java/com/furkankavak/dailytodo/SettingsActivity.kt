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
import com.furkankavak.dailytodo.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        binding.email.text = email.toString()

        binding.deleteAllButton.setOnClickListener {
            deleteAllTasks(false)
        }

        binding.passwordChangeButton.setOnClickListener {
            val intent = Intent(this@SettingsActivity, ChangePasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.deleteAccountButton.setOnClickListener {
            deleteAccount()
        }

        binding.logOutButton.setOnClickListener {
            logOut()
        }
    }

    private fun deleteAllTasks(isDelete : Boolean){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            val database = FirebaseDatabase.getInstance()
            val notesRef = database.reference.child("User/$uid/")

            notesRef.orderByChild("userId").equalTo(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (noteSnapshot in snapshot.children) {
                            noteSnapshot.ref.removeValue()
                        }
                        if(!isDelete){
                            val con = HomeActivity()
                            con.refresh()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@SettingsActivity, "Somethings went wrong", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun logOut(){
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun deleteAccount(){
        val dialog = Dialog(this@SettingsActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val yesButton : Button = dialog.findViewById(R.id.yesButton)
        val noButton : Button = dialog.findViewById(R.id.noButton)

        yesButton.setOnClickListener {
            val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@SettingsActivity, "Account successfully deleted.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@SettingsActivity, "Somethings went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            deleteAllTasks(true)
        }

        noButton.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()
    }
}