package com.example.contact_list_app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contact_list_app.R
import com.example.contact_list_app.model.ContactModel
import com.google.android.material.card.MaterialCardView

class ContactAdapter(
    private val contacts: MutableList<ContactModel>,
    private val context: Context,
    private val onItemClick: (ContactModel) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val photo: ImageView = itemView.findViewById(R.id.contact_photo)
        public val name: TextView = itemView.findViewById(R.id.contact_name)
        public val phone: TextView = itemView.findViewById(R.id.contact_phone)
        public val layout: LinearLayout = itemView.findViewById(R.id.llItemContact)
        public val card: MaterialCardView = itemView.findViewById(R.id.mcvItemContact)

        fun bind(contact: ContactModel) {
            photo.setImageResource(R.drawable.profile_default_foreground)
            name.text = contact.fullName
            phone.text = contact.phone
            itemView.setOnClickListener { onItemClick(contact) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_contact, parent, false)

        Log.d("RecyclerView", "onCreateViewHolder dipanggil untuk viewType: $viewType")
        return ContactViewHolder(view)
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        Log.d("RecyclerView", "onBindViewHolder dipanggil untuk posisi: $position, data: ${contacts[position].fullName}")

        holder.card.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_anim))
        holder.phone.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_anim))

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
