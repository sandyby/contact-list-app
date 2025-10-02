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

class AddContactActivity : AppCompatActivity() {
    private lateinit var etFullName: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_contact)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etFullName = findViewById(R.id.etFullName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        btnSave.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()

            when {
                TextUtils.isEmpty(fullName) -> etFullName.error = "Fill in your full name!"
                TextUtils.isEmpty(phoneNumber) -> etPhoneNumber.error = "Fill in your phone number!"
                !phoneNumber.matches(Regex("^[0-9+\\- ]{6,20}$")) -> etPhoneNumber.error =
                    "Invalid phone number format!"

                else -> {
                    val result = Intent()
                    result.putExtra("fullName", fullName)
                    result.putExtra("phoneNumber", phoneNumber)
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