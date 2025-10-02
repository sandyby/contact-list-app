package com.example.contact_list_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        val backBtn: ImageButton = findViewById(R.id.ibBackBtn)
        backBtn.setOnClickListener {
            val result = Intent().apply {
                putExtra("fullName", tvFullName.text.toString())
                putExtra("phoneNumber", tvPhoneNumber.text.toString())
                putExtra("position", intent.getIntExtra("position", -1))
            }
            setResult(Activity.RESULT_OK, result)
            finish()
        }

        val editBtn: ImageButton = findViewById(R.id.ibEditBtn)
        editBtn.setOnClickListener {
            val result = Intent(this, EditContactActivity::class.java).apply {
                putExtra("fullName", intent.getStringExtra("fullName"))
                putExtra("phoneNumber", intent.getStringExtra("phoneNumber"))
                putExtra("position", intent.getIntExtra("position", -1))
            }
            editContactLauncher.launch(result)
        }

        onBackPressedDispatcher.addCallback(this) {
            val result = Intent().apply {
                putExtra("fullName", tvFullName.text.toString())
                putExtra("phoneNumber", tvPhoneNumber.text.toString())
                putExtra("position", intent.getIntExtra("position", -1))
            }
            setResult(Activity.RESULT_OK, result)
            finish()
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

            tvFullName.text = newFullName
            tvPhoneNumber.text = newPhoneNumber

            val forwardResult = Intent().apply {
                putExtra("fullName", newFullName)
                putExtra("phoneNumber", newPhoneNumber)
                putExtra("position", position)
            }
            setResult(Activity.RESULT_OK, forwardResult)
        }
    }
}