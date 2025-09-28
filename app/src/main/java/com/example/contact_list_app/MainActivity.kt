package com.example.contact_list_app

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contact_list_app.adapter.ContactAdapter
import com.example.contact_list_app.api.RetrofitClient
import com.example.contact_list_app.model.ContactModel
import com.example.contact_list_app.model.UserResponse
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

        contactAdapter = ContactAdapter(mutableListOf()) { contact ->
            showContactDialog(contact)
        }

        recyclerView.adapter = contactAdapter

        // Swipe to delete
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                contactAdapter.removeItem(vh.adapterPosition)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        fetchContacts()
    }

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
                    // handle non-200 response
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                // handle failure: show Toast or log
            }
        })
    }

    private fun showContactDialog(contact: ContactModel) {
        AlertDialog.Builder(this)
            .setTitle("Contact Selected")
            .setMessage("Name: ${contact.fullName}\nPhone: ${contact.phone}")
            .setPositiveButton("OK", null)
            .show()
    }
}
