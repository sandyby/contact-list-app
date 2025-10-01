package com.example.contact_list_app.adapter

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
    private val onItemClick: (ContactModel, Int) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var filteredContacts: MutableList<ContactModel> = contacts.toMutableList()

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView = itemView.findViewById(R.id.contact_photo)
        private val name: TextView = itemView.findViewById(R.id.contact_name)
        private val phone: TextView = itemView.findViewById(R.id.contact_phone)

        fun bind(contact: ContactModel) {
            photo.setImageResource(R.drawable.profile_default_foreground)
            name.text = contact.fullName
            phone.text = contact.phone
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION && pos < filteredContacts.size) {
                    onItemClick(filteredContacts[pos], pos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount() = filteredContacts.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(filteredContacts[position])
    }

    fun setData(newContacts: List<ContactModel>) {
        contacts.clear()
        contacts.addAll(newContacts.sortedBy { it.fullName })
        filteredContacts = contacts.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredContacts = if (query.isEmpty()) {
            contacts.toMutableList()
        } else {
            contacts.filter {
                it.fullName.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun getItem(position: Int): ContactModel? {
        return if (position in 0 until filteredContacts.size) filteredContacts[position] else null
    }

    fun addItem(contact: ContactModel) {
        contacts.add(contact)
        contacts.sortBy { it.fullName }
        filter("") // refresh
    }

    fun updateItem(position: Int, contact: ContactModel) {
        val oldContact = filteredContacts.getOrNull(position) ?: return
        val index = contacts.indexOf(oldContact)
        if (index != -1) {
            contacts[index] = contact
            contacts.sortBy { it.fullName }
            filter("")
        }
    }

    fun removeItem(position: Int) {
        val contact = filteredContacts.getOrNull(position) ?: return
        contacts.remove(contact)
        filteredContacts.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(contact: ContactModel, position: Int) {
        contacts.add(contact)
        contacts.sortBy { it.fullName }
        filter("")
    }
}
