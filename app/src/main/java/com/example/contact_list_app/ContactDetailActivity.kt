package com.example.contact_list_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ContactDetailActivity : AppCompatActivity() {
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

        val fullName = intent.getStringExtra("fullName")
        val phoneNumber = intent.getStringExtra("phoneNumber")

        findViewById<TextView>(R.id.tvFullName).text = fullName
        findViewById<TextView>(R.id.tvPhoneNumber).text = phoneNumber
//        findViewById<ImageView>(R.id.ivProfilePhoto).setImageResource()

        val backBtn: ImageButton = findViewById(R.id.ibBackBtn)
        backBtn.setOnClickListener {
            sendResultAndFinish()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun sendResultAndFinish() {
        val result = Intent().apply {
            putExtra("position", intent.getIntExtra("position", -1))
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                sendResultAndFinish()
                onBackPressedDispatcher.onBackPressed()
                true
            }

//            R.id.action_add_contact -> {
//                val intent = Intent(this, AddContactActivity::class.java)
//                startActivity(intent)
//                true
//            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}