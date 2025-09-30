package com.example.contact_list_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contact_list_app.model.ContactModel

class ContactDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val isEditing = intent.getBooleanExtra("isEditing", false)
//        val contact = intent.getParcelableExtra("contact", )

//        val fragment = if (isEditing && contact != null)
//            ContactDetailFragment.newInstance(contact)
//        else AddContactFragment
//
//        supportFragmentManager.beginTransaction().replace(R.id.flContactDetailFragmentContainer, fragment).commit()
    }
}