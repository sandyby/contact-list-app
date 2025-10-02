package com.example.contact_list_app

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        contactAdapter = ContactAdapter(mutableListOf()) { contact, position ->
            val intent = Intent(this, ContactDetailActivity::class.java).apply {
                putExtra("fullName", contact.fullName)
                putExtra("phoneNumber", contact.phone)
                putExtra("position", position)
            }
            viewContactLauncher.launch(intent)
        }
        recyclerView.adapter = contactAdapter

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val position = vh.bindingAdapterPosition
                val contact = contactAdapter.getItem(position)
                if (contact == null) {
                    contactAdapter.notifyItemChanged(position)
                    return
                }

                AlertDialog.Builder(this@MainActivity).setTitle("Delete Contact?")
                    .setMessage("Are you sure want to delete ${contact.fullName}?")
                    .setPositiveButton("Yes") { _, _ ->
                        contactAdapter.removeItem(position)
                        Snackbar.make(
                            recyclerView,
                            "${contact.fullName} successfully deleted",
                            Snackbar.LENGTH_LONG
                        ).setAction("Undo") {
                            contactAdapter.restoreItem(contact, position)
                        }.show()
                    }.setNegativeButton("Nevermind") { _, _ ->
                        contactAdapter.notifyItemChanged(position)
                    }.setCancelable(false).show()
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val fabAdd: FloatingActionButton = findViewById(R.id.fab_add)
        fabAdd.setOnClickListener()
        {
            val intent = Intent(this, AddContactActivity::class.java)
            addContactLauncher.launch(intent)
        }

        fetchContacts()
    }

    private val addContactLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val fullName = data.getStringExtra("fullName") ?: return@registerForActivityResult
            val phoneNumber = data.getStringExtra("phoneNumber") ?: return@registerForActivityResult

            val newContact = ContactModel(fullName, phoneNumber)
            val position = contactAdapter.addItem(newContact)

            val detailIntent = Intent(this, ContactDetailActivity::class.java).apply {
                putExtra("fullName", fullName)
                putExtra("phoneNumber", phoneNumber)
                putExtra("position", position)
            }
            viewContactLauncher.launch(detailIntent)
        }
    }

    private val viewContactLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val fullName = data.getStringExtra("fullName") ?: return@registerForActivityResult
            val phoneNumber = data.getStringExtra("phoneNumber") ?: return@registerForActivityResult
            val position = data.getIntExtra("position", -1)

            if (position != -1) {
                contactAdapter.updateItem(position, ContactModel(fullName, phoneNumber))
                recyclerView.scrollToPosition(position)
            }
        }
    }

    override fun onCreateOptionsMenu(menuObj: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menuObj)

        val searchItem = menuObj?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search a contact..."

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

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                return true
            }
        })
        return true
    }

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
}