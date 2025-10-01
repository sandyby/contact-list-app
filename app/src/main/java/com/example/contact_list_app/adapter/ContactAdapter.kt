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
    private val onItemClick: (ContactModel, Int) -> Unit   // now provides current pos
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView = itemView.findViewById(R.id.contact_photo)
        private val name: TextView = itemView.findViewById(R.id.contact_name)
        private val phone: TextView = itemView.findViewById(R.id.contact_phone)

        fun bind(contact: ContactModel) {
            photo.setImageResource(R.drawable.profile_default_foreground)
            name.text = contact.fullName
            phone.text = contact.phone

            // Use adapterPosition at click time to avoid stale positions
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION && pos in 0 until contacts.size) {
                    onItemClick(contacts[pos], pos)
                }
            }
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

    fun getItem(position: Int): ContactModel? {
        return if (position in 0 until contacts.size) contacts[position] else null
    }

    fun addItem(contact: ContactModel) {
        contacts.add(0, contact)
        notifyItemInserted(0)
    }

    fun removeItem(position: Int) {
        if (position in 0 until contacts.size) {
            contacts.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun restoreItem(contact: ContactModel, position: Int) {
        val pos = position.coerceIn(0, contacts.size) // safe
        contacts.add(pos, contact)
        notifyItemInserted(pos)
    }

    fun updateItem(position: Int, contact: ContactModel) {
        if (position in 0 until contacts.size) {
            contacts[position] = contact
            notifyItemChanged(position)
        }
    }
}
