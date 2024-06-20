package com.thejas.diamondgroup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OtherPerson : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var logoutBtn : Button

    private lateinit var newRecylerview: RecyclerView
    private lateinit var newArrayList: ArrayList<Vehicles>
    private lateinit var tempArrayList: ArrayList<Vehicles>
    lateinit var number: Array<String>
    private lateinit var adapter: MyAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_other_person)
        sessionManager = SessionManager(this)
        logoutBtn = findViewById(R.id.logout)

        database = FirebaseDatabase.getInstance().reference
        newArrayList = arrayListOf()
        tempArrayList = arrayListOf()

        // Load data from SharedPreferences
        loadDataFromFirebase()

        newRecylerview = findViewById(R.id.recyclerView)
        newRecylerview.layoutManager = LinearLayoutManager(this)
        newRecylerview.setHasFixedSize(true)

        adapter = MyAdapter(newArrayList)
        newRecylerview.adapter = adapter

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // Handle if needed, but we'll use onQueryTextChange for live filtering
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    adapter.updateList(newArrayList)

                    loadDataFromFirebase()
                    adapter = MyAdapter(newArrayList)
                    newRecylerview.adapter = adapter
                    // Show all vehicles if the search query is empty
                } else {
                    val query = newText.toLowerCase() // Convert the search query to lowercase
                    tempArrayList.clear() // Clear temporary list for new search
                    for (vehicle in newArrayList) {
                        if (vehicle.number.toLowerCase().startsWith(query)) {
                            tempArrayList.add(vehicle)
                        }
                    }
                    adapter.updateList(tempArrayList)
                }
                return true
            }
        })

        logoutBtn.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Are you sure you want to logout?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            // User clicked "Yes", perform logout
            sessionManager.setLoggedIn(false,"user")

            // Redirect to the login screen
            val intent = Intent(this@OtherPerson, LoginScreen::class.java)
            startActivity(intent)
            finish()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
            // User clicked "No", close the dialog
            dialog.dismiss()
        }

        // Create and show the alert dialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun loadDataFromFirebase() {
        database.child("vehicle_data").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Deserialize the data from Firebase snapshot
                val gson = Gson()
                val json = gson.toJson(snapshot.value)
                val type = object : TypeToken<ArrayList<Vehicles>>() {}.type
                val loadedData: ArrayList<Vehicles> = gson.fromJson(json, type)

                // Clear the existing lists and add the loaded data
                newArrayList.clear()
                tempArrayList.clear()

                newArrayList.addAll(loadedData)
                tempArrayList.addAll(loadedData)

                // Notify the adapter about the data change
                adapter.notifyDataSetChanged()

                Toast.makeText(this, "Data loaded from Firebase", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No data found in Firebase", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load data from Firebase", Toast.LENGTH_SHORT).show()
        }
    }
}