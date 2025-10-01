package com.example.contact_list_app

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contact_list_app.adapter.ContactAdapter
import com.example.contact_list_app.api.RetrofitClient
import com.example.contact_list_app.model.ContactModel
import com.example.contact_list_app.model.UserResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var contactAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter: click => edit dialog (we receive contact + current position)
        contactAdapter = ContactAdapter(mutableListOf()) { contact, position ->
            showEditContactDialog(contact, position, recyclerView)
        }
        recyclerView.adapter = contactAdapter

        // Swipe to delete with confirmation + snackbar undo (safe handling)
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val position = vh.adapterPosition
                val contact = contactAdapter.getItem(position)
                if (contact == null) {
                    // position invalid; restore view
                    contactAdapter.notifyItemChanged(position)
                    return
                }

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Hapus Kontak?")
                    .setMessage("Apakah kamu yakin ingin menghapus ${contact.fullName}?")
                    .setPositiveButton("Ya") { _, _ ->
                        contactAdapter.removeItem(position)
                        Snackbar.make(findViewById(R.id.recycler_view),
                            "${contact.fullName} dihapus",
                            Snackbar.LENGTH_LONG
                        ).setAction("Undo") {
                            contactAdapter.restoreItem(contact, position)
                        }.show()
                    }
                    .setNegativeButton("Batal") { _, _ ->
                        contactAdapter.notifyItemChanged(position)
                    }
                    .setCancelable(false)
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // FAB: add new contact (uses same dialog layout as edit)
        val fabAdd: FloatingActionButton = findViewById(R.id.fab_add)
        fabAdd.setOnClickListener {
            val inputLayout = layoutInflater.inflate(R.layout.dialog_add_contact, null)
            val firstNameInput = inputLayout.findViewById<EditText>(R.id.et_first_name)
            val lastNameInput = inputLayout.findViewById<EditText>(R.id.et_last_name)
            val phoneInput = inputLayout.findViewById<EditText>(R.id.et_phone)

            val dialog = AlertDialog.Builder(this)
                .setTitle("Tambah Kontak Baru")
                .setView(inputLayout)
                .setPositiveButton("Tambah", null) // we'll override later to prevent auto-dismiss
                .setNegativeButton("Batal", null)
                .create()

            dialog.show()

            // Override positive button to keep dialog open on invalid input
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val firstName = firstNameInput.text.toString().trim()
                val lastName = lastNameInput.text.toString().trim()
                val phone = phoneInput.text.toString().trim()

                when {
                    TextUtils.isEmpty(firstName) -> {
                        firstNameInput.error = "First name tidak boleh kosong"
                        firstNameInput.requestFocus()
                    }
                    TextUtils.isEmpty(lastName) -> {
                        lastNameInput.error = "Last name tidak boleh kosong"
                        lastNameInput.requestFocus()
                    }
                    TextUtils.isEmpty(phone) -> {
                        phoneInput.error = "Phone tidak boleh kosong"
                        phoneInput.requestFocus()
                    }
                    !phone.matches(Regex("^[0-9+ ]{6,15}$")) -> {
                        phoneInput.error = "Nomor telepon tidak valid"
                        phoneInput.requestFocus()
                    }
                    else -> {
                        val newContact = ContactModel(
                            fullName = "$firstName $lastName",
                            phone = phone
                        )
                        contactAdapter.addItem(newContact)
                        recyclerView.scrollToPosition(0)
                        dialog.dismiss()
                    }
                }
            }
        }

        fetchContacts()
    }

    // Fetch contacts with basic error handling
    private fun fetchContacts() {
        RetrofitClient.instance.getUsers().enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val users = response.body()?.users ?: emptyList()
                    val contacts = users.map {
                        ContactModel(
                            fullName = "${it.firstName} ${it.lastName}",
                            phone = it.phone
                        )
                    }
                    contactAdapter.setData(contacts)
                } else {
                    Toast.makeText(this@MainActivity, "Gagal ambil data: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Edit dialog: prefill data, validate, and update adapter safely
    private fun showEditContactDialog(contact: ContactModel, position: Int, recyclerView: RecyclerView) {
        val inputLayout = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val firstNameInput = inputLayout.findViewById<EditText>(R.id.et_first_name)
        val lastNameInput = inputLayout.findViewById<EditText>(R.id.et_last_name)
        val phoneInput = inputLayout.findViewById<EditText>(R.id.et_phone)

        // Prefill existing values (split fullName)
        val nameParts = contact.fullName.split(" ", limit = 2)
        firstNameInput.setText(nameParts.getOrNull(0) ?: "")
        lastNameInput.setText(nameParts.getOrNull(1) ?: "")
        phoneInput.setText(contact.phone)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Kontak")
            .setView(inputLayout)
            .setPositiveButton("Simpan", null) // override to prevent auto-dismiss
            .setNegativeButton("Batal", null)
            .create()

        dialog.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()

            when {
                TextUtils.isEmpty(firstName) -> {
                    firstNameInput.error = "First name tidak boleh kosong"
                    firstNameInput.requestFocus()
                }
                TextUtils.isEmpty(lastName) -> {
                    lastNameInput.error = "Last name tidak boleh kosong"
                    lastNameInput.requestFocus()
                }
                TextUtils.isEmpty(phone) -> {
                    phoneInput.error = "Phone tidak boleh kosong"
                    phoneInput.requestFocus()
                }
                !phone.matches(Regex("^[0-9+\\- ]{6,18}$")) -> {
                    phoneInput.error = "Nomor telepon tidak valid"
                    phoneInput.requestFocus()
                }
                else -> {
                    // Double-check position is still valid before updating
                    val current = contactAdapter.getItem(position)
                    if (current == null) {
                        Toast.makeText(this, "Posisi tidak valid (data mungkin berubah).", Toast.LENGTH_SHORT).show()
                        contactAdapter.notifyDataSetChanged()
                        dialog.dismiss()
                        return@setOnClickListener
                    }

                    val updatedContact = ContactModel(
                        fullName = "$firstName $lastName",
                        phone = phone
                    )
                    contactAdapter.updateItem(position, updatedContact)
                    recyclerView.scrollToPosition(position)
                    dialog.dismiss()
                }
            }
        }
    }
}
