package com.furkankavak.dailytodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.furkankavak.dailytodo.databinding.ActivityAddNewTaskBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddNewTaskActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddNewTaskBinding
    private lateinit var newTask : String
    private lateinit var database: DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        newTask = binding.enterTask.text.toString()

        val isUpdate = intent.getStringExtra("isUpdate")
        val oldText = intent.getStringExtra("oldText")
        val id = intent.getStringExtra("id")

        if(isUpdate == "OK"){
            setUpdateLayout()
            binding.editTask.setText(oldText)
            binding.updateButton.setOnClickListener {
                updateTask(id)
            }
        }

        binding.saveButton.setOnClickListener {
            saveTask()
        }

    }

    private fun saveTask(){
        newTask = binding.enterTask.text.toString()
        val userId = auth.currentUser!!.uid
        val noteId = FirebaseDatabase.getInstance().reference.child("User").push().key.toString()
        val note = UserNoteModel(userId,noteId,newTask)
        if(binding.enterTask.text!=null){
            database.child("User").child(userId).child(noteId).setValue(note).addOnSuccessListener {
                Toast.makeText(this@AddNewTaskActivity, "Task saved successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddNewTaskActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@AddNewTaskActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this@AddNewTaskActivity, "Please enter task", Toast.LENGTH_SHORT).show()
        }


    }

    private fun setUpdateLayout(){
        binding.taskLayout.visibility = View.GONE
        binding.enterTask.visibility = View.GONE
        binding.saveButton.visibility = View.GONE
        binding.updateLayout.visibility = View.VISIBLE
        binding.editTask.visibility = View.VISIBLE
        binding.updateButton.visibility = View.VISIBLE
    }

    private fun updateTask(id : String?){
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val database = FirebaseDatabase.getInstance().getReference("User/$currentUser")
        database.child("$id/note").setValue(binding.editTask.text.toString())
        val switch = Intent(this@AddNewTaskActivity, HomeActivity::class.java)
        startActivity(switch)
        finish()

    }

}
