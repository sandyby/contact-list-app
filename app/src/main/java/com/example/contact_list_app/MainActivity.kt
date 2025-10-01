package com.example.contact_list_app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.Composable
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contact_list_app.adapter.ContactAdapter
import com.example.contact_list_app.api.RetrofitClient
import com.example.contact_list_app.model.ContactModel
import com.example.contact_list_app.model.UserResponse
import com.google.android.material.appbar.MaterialToolbar
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

        // Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        contactAdapter = ContactAdapter(mutableListOf()) { contact ->
            val intent = Intent(this, ContactDetailActivity::class.java).apply {
                putExtra("name", contact.fullName)
                putExtra("phoneNumber", contact.phone)
            }
            startActivity(intent)
        }
        recyclerView.adapter = contactAdapter

        // Swipe delete
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val position = vh.adapterPosition
                val contact = contactAdapter.getItem(position)
                if (contact == null) {
                    contactAdapter.notifyItemChanged(position)
                    return
                }

                AlertDialog.Builder(this@MainActivity).setTitle("Hapus Kontak?")
                    .setMessage("Apakah kamu yakin ingin menghapus ${contact.fullName}?")
                    .setPositiveButton("Ya") { _, _ ->
                        contactAdapter.removeItem(position)
                        Snackbar.make(
                            recyclerView, "${contact.fullName} dihapus", Snackbar.LENGTH_LONG
                        ).setAction("Undo") {
                            contactAdapter.restoreItem(contact, position)
                        }.show()
                    }.setNegativeButton("Batal") { _, _ ->
                        contactAdapter.notifyItemChanged(position)
                    }.setCancelable(false).show()
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // FAB Add
        val fabAdd: FloatingActionButton = findViewById(R.id.fab_add)
        fabAdd.setOnClickListener()
        {
            showAddContactDialog(recyclerView)
        }

        // Fetch data dari API
        fetchContacts()
    }

//    @Composable
//    fun ContactDetail() {
//        val navController = rememberNavController()
//        NavHost(navController, "contactList")
//
//        // RecyclerView
//        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        contactAdapter = ContactAdapter(mutableListOf())
//        //    { contact, position ->
//        { contact ->
//            val intent = Intent(this, ContactDetailActivity::class.java).apply {
//                putExtra("name", contact.fullName)
//                putExtra("phoneNumber", contact.phone)
//            }
//            startActivity(intent)
////        showEditContactDialog(contact, position, recyclerView)
////        } as (ContactModel) -> Unit
//        }
//        recyclerView.adapter = contactAdapter
//
//    }

    // 🔹 Toolbar menu (Search)
    override fun onCreateOptionsMenu(menuObj: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menuObj)

        val searchItem = menuObj?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search a contact..."

//        val addContactItem = menuObj?.findItem(R.id.action_add_contact)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                contactAdapter.filter(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                contactAdapter.filter(newText ?: "")
                return true
            }
        })

        return true
    }


    // 🔹 Fetch data API
    private fun fetchContacts() {
        RetrofitClient.instance.getUsers().enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.isSuccessful) {
                    val users = response.body()?.users ?: emptyList()
                    val contacts = users.map {
                        ContactModel(
                            fullName = "${it.firstName} ${it.lastName}", phone = it.phone
                        )
                    }
                    contactAdapter.setData(contacts)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Gagal ambil data: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // 🔹 Dialog Tambah Kontak
    private fun showAddContactDialog(recyclerView: RecyclerView) {
        val inputLayout = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val firstNameInput = inputLayout.findViewById<EditText>(R.id.et_first_name)
        val lastNameInput = inputLayout.findViewById<EditText>(R.id.et_last_name)
        val phoneInput = inputLayout.findViewById<EditText>(R.id.et_phone)

        val dialog =
            AlertDialog.Builder(this).setTitle("Tambah Kontak Baru").setView(inputLayout)
                .setPositiveButton("Tambah", null).setNegativeButton("Batal", null).create()

        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim().orEmpty()
            val phone = phoneInput.text.toString().trim()

            when {
                TextUtils.isEmpty(firstName) -> firstNameInput.error = "Wajib diisi"
//                TextUtils.isEmpty(lastName) -> lastNameInput.error = "Wajib diisi"
                TextUtils.isEmpty(phone) -> phoneInput.error = "Wajib diisi"
//                !phone.matches(Regex("^[0-9+\\- ]{6,20}$")) -> phoneInput.error =
                !phone.matches(Regex("^[0-9]{6,20}$")) -> phoneInput.error =
                    "Format nomor tidak valid"

                else -> {
                    val newContact = ContactModel("$firstName $lastName", phone)
                    contactAdapter.addItem(newContact)
                    recyclerView.scrollToPosition(0)
                    dialog.dismiss()
                }
            }
        }
    }

    // 🔹 Dialog Edit Kontak
    private fun showEditContactDialog(
        contact: ContactModel, position: Int, recyclerView: RecyclerView
    ) {
        val inputLayout = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val firstNameInput = inputLayout.findViewById<EditText>(R.id.et_first_name)
        val lastNameInput = inputLayout.findViewById<EditText>(R.id.et_last_name)
        val phoneInput = inputLayout.findViewById<EditText>(R.id.et_phone)

        val nameParts = contact.fullName.split(" ", limit = 2)
        firstNameInput.setText(nameParts.getOrNull(0) ?: "")
        lastNameInput.setText(nameParts.getOrNull(1) ?: "")
        phoneInput.setText(contact.phone)

        val dialog = AlertDialog.Builder(this).setTitle("Edit Kontak").setView(inputLayout)
            .setPositiveButton("Simpan", null).setNegativeButton("Batal", null).create()

        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()

            when {
                TextUtils.isEmpty(firstName) -> firstNameInput.error = "Wajib diisi"
                TextUtils.isEmpty(lastName) -> lastNameInput.error = "Wajib diisi"
                TextUtils.isEmpty(phone) -> phoneInput.error = "Wajib diisi"
                !phone.matches(Regex("^[0-9+\\- ]{6,20}$")) -> phoneInput.error =
                    "Format nomor tidak valid"

                else -> {
                    val updatedContact = ContactModel("$firstName $lastName", phone)
                    contactAdapter.updateItem(position, updatedContact)
                    recyclerView.scrollToPosition(position)
                    dialog.dismiss()
                }
            }
        }
    }
}