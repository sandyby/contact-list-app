package com.example.contact_list_app

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contact_list_app.model.ContactModel

class ContactDetailActivity : AppCompatActivity() {
    private lateinit var tvFullName: TextView
    private lateinit var tvPhoneNumber: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.actContactDetail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvFullName = findViewById(R.id.tvFullName)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)

        val fullName = intent.getStringExtra("fullName")
        val phoneNumber = intent.getStringExtra("phoneNumber")

        tvFullName.text = fullName
        tvPhoneNumber.text = phoneNumber
//        findViewById<ImageView>(R.id.ivProfilePhoto).setImageResource()

        val backBtn: ImageButton = findViewById(R.id.ibBackBtn)
        backBtn.setOnClickListener {


            val result = Intent().apply {
                putExtra("fullName", tvFullName.text.toString())
                putExtra("phoneNumber", tvPhoneNumber.text.toString())
                putExtra("position", intent.getIntExtra("position", -1))
            }
            setResult(Activity.RESULT_OK, result)
            finish()
//            onBackPressedDispatcher.onBackPressed()
        }

        val editBtn: ImageButton = findViewById(R.id.ibEditBtn)
        editBtn.setOnClickListener {
            val result = Intent(this, EditContactActivity::class.java).apply {
                putExtra("fullName", intent.getStringExtra("fullName"))
                putExtra("phoneNumber", intent.getStringExtra("phoneNumber"))
                putExtra("position", intent.getIntExtra("position", -1))
            }
            editContactLauncher.launch(result)
//            startActivityForResult(result, MainActivity.EDIT_DETAIL_CONTACT)
        }
    }


    private val editContactLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val newFullName = data?.getStringExtra("fullName")
            val newPhoneNumber = data?.getStringExtra("phoneNumber")
            val position = data?.getIntExtra("position", -1) ?: -1

//            if (position != -1 && fullName != null && phoneNumber != null) {
//                contactAdapter.updateItem(position, ContactModel(fullName, phoneNumber))
//            }

            tvFullName.text = newFullName
            tvPhoneNumber.text = newPhoneNumber

            // Forward the update back to MainActivity
            val forwardResult = Intent().apply {
                putExtra("fullName", newFullName)
                putExtra("phoneNumber", newPhoneNumber)
                putExtra("position", position)
            }
            setResult(Activity.RESULT_OK, forwardResult)
        }
    }

//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        data: Intent?,
//    ) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == MainActivity.EDIT_DETAIL_CONTACT && resultCode == Activity.RESULT_OK && data != null) {
//            val newFullName = data.getStringExtra("fullName")
//            val newPhoneNumber = data.getStringExtra("phoneNumber")
//            val newPosition = data.getIntExtra("position", -1)
//
//            tvFullName.text = newFullName
//            tvPhoneNumber.text = newPhoneNumber
//
//            val forwardResult = Intent().apply {
//                putExtra("fullName", newFullName)
//                putExtra("phoneNumber", newPhoneNumber)
//                putExtra("position", newPosition)
//            }
//            setResult(Activity.RESULT_OK, forwardResult)
////            finish()
//        }
//
//        Toast.makeText(this, "HELLO", Toast.LENGTH_LONG).show()
//    }
}