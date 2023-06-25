package com.furkankavak.dailytodo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.furkankavak.dailytodo.databinding.ItemListBinding



class MyAdapter(private val noteList : ArrayList<UserNoteModel>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.textViewTask.text = noteList[position].note
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshDataset() {
        notifyDataSetChanged()
    }
}

