package com.furkankavak.dailytodo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furkankavak.dailytodo.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var noteList: ArrayList<UserNoteModel>
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var currentUser : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.settingsButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, SettingsActivity::class.java)
            startActivity(intent)

        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, AddNewTaskActivity::class.java)
            startActivity(intent)
        }
        getTask()
        swipe()


    }

    private fun getTask(){
        currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        userRecyclerView = binding.recyclerView
        FirebaseDatabase.getInstance().getReference("User/$currentUser").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("log", "onDataChange : $snapshot")
                if(snapshot.exists()){
                    noteList = arrayListOf()
                    for(data in snapshot.children){
                        val model = data.getValue(UserNoteModel::class.java)
                        if(model!!.userId == FirebaseAuth.getInstance().currentUser!!.uid){
                            noteList.add(model)
                        }
                    }
                    userRecyclerView.adapter = MyAdapter(noteList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, error.toString(), Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun swipe(){
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
               onSwiped(position, false)
            }
        })

        val itemTouchHelper2 = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                onSwiped(position,true)
            }
        })

        itemTouchHelper.attachToRecyclerView(userRecyclerView)
        itemTouchHelper2.attachToRecyclerView(userRecyclerView)

    }

    private fun onSwiped(position: Int, RorL : Boolean) {
        val reference = FirebaseDatabase.getInstance().getReference("User/$currentUser")
        val selectedData = noteList[position].noteId
        val selectedText = noteList[position].note

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val id = userSnapshot.child("noteId").getValue(String::class.java)
                    if(id == selectedData.toString()){
                        Log.d("User Name", id)
                       if(RorL){
                           updateData(id, selectedText)
                       }else{
                           deleteData(id)
                       }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Task deletion progress failed", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun deleteData(id : String?){
        val database = FirebaseDatabase.getInstance().getReference("User/$currentUser").child("$id")
        val mTask = database.removeValue()
        mTask.addOnSuccessListener {
            Toast.makeText(this@HomeActivity, "Task deleted successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@HomeActivity, "Task deletion progress failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateData(id : String?, text : String?){
        val intent = Intent(this@HomeActivity, AddNewTaskActivity::class.java)
        intent.putExtra("isUpdate", "OK")
        intent.putExtra("id", id)
        intent.putExtra("oldText", text)
        startActivity(intent)
        finish()


    }

    fun refresh(){
        val adapter = MyAdapter(noteList)
        adapter.refreshDataset()
    }



}