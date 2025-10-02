package com.example.contact_list_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditContactActivity : AppCompatActivity() {
    private lateinit var etFullName: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_contact)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etFullName = findViewById(R.id.etFullName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        val fullName = intent.getStringExtra("fullName") ?: ""
        val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
        val position = intent.getIntExtra("position", -1)

        etFullName.setText(fullName)
        etPhoneNumber.setText(phoneNumber)

        btnSave.setOnClickListener {
            val updatedFullName = etFullName.text.toString().trim()
            val updatedPhoneNumber = etPhoneNumber.text.toString().trim()

            when {
                TextUtils.isEmpty(updatedFullName) -> etFullName.error = "Full name can't be empty!"
                TextUtils.isEmpty(updatedPhoneNumber) -> etFullName.error =
                    "Phone number can't be empty!"

                !updatedPhoneNumber.matches(Regex("^[0-9+\\- ]{6,20}$")) -> etPhoneNumber.error =
                    "Invalid phone number format!"

                else -> {
                    val result = Intent().apply {
                        putExtra("fullName", updatedFullName)
                        putExtra("phoneNumber", updatedPhoneNumber)
                        putExtra("position", position)
                    }
                    setResult(Activity.RESULT_OK, result)
                    finish()
                }
            }
        }

        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}