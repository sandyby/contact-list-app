package com.example.contact_list_app.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contact_list_app.R
import com.example.contact_list_app.model.ContactModel

class ContactAdapter(
    private val contacts: MutableList<ContactModel>,
    private val onItemClick: (ContactModel) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView = itemView.findViewById(R.id.contact_photo)
        private val name: TextView = itemView.findViewById(R.id.contact_name)
        private val phone: TextView = itemView.findViewById(R.id.contact_phone)

        fun bind(contact: ContactModel) {
            photo.setImageResource(R.drawable.profile_default_foreground)
            name.text = contact.fullName
            phone.text = contact.phone
            itemView.setOnClickListener { onItemClick(contact) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)

        Log.d("RecyclerView", "onCreateViewHolder dipanggil untuk viewType: $viewType")
        return ContactViewHolder(view)
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        Log.d("RecyclerView", "onBindViewHolder dipanggil untuk posisi: $position, data: ${contacts[position].fullName}")
        holder.bind(contacts[position])
    }

    fun setData(newContacts: List<ContactModel>) {
        contacts.clear()
        contacts.addAll(newContacts)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        contacts.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(contact: ContactModel, position: Int) {
        contacts.add(position, contact)
        notifyItemInserted(position)
    }

    fun getItem(position: Int): ContactModel {
        return contacts[position]
    }
}
