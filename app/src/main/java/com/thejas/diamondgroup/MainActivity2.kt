package com.thejas.diamondgroup
import com.opencsv.CSVReader
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thejas.diamondgroup.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var newRecylerview: RecyclerView
    private lateinit var newArrayList: ArrayList<Vehicles>
    private lateinit var tempArrayList: ArrayList<Vehicles>

    lateinit var number: Array<String>
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain2Binding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MyAdapter

    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnImport: Button = findViewById(R.id.btn_import)
        database = FirebaseDatabase.getInstance().reference
        val btnclear: Button = findViewById(R.id.btn_clear)
        btnclear.setOnClickListener{
            Toast.makeText(this@MainActivity2,"Data Cleared",Toast.LENGTH_SHORT).show()
            newArrayList.clear()
            tempArrayList.clear()
            adapter.notifyDataSetChanged()

            saveDataToFirebase(newArrayList)
        }
        btnImport.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, 1)
        }

        newArrayList = arrayListOf()
        tempArrayList = arrayListOf()

        // Load data from SharedPreferences
        loadDataFromFirebase()

        newRecylerview = findViewById(R.id.recyclerView)
        newRecylerview.layoutManager = LinearLayoutManager(this)
        newRecylerview.setHasFixedSize(true)

        adapter = MyAdapter(newArrayList)
        newRecylerview.adapter = adapter

        setSupportActionBar(binding.appBarMain2.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

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
    }

    private fun loadDataFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("vehicle_data", null)
        val type = object : TypeToken<ArrayList<Vehicles>>() {}.type
        if (json != null) {
            newArrayList = gson.fromJson(json, type)
        } else {
            newArrayList = arrayListOf()
        }
        tempArrayList.addAll(newArrayList) // Initially populate tempArrayList with all data
    }


    private fun getUserdata() {
        for (i in number.indices) {
            val numbert = Vehicles(number[i])
            newArrayList.add(numbert)
        }
        tempArrayList.addAll(newArrayList) // Initially populate tempArrayList with all data
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val filePath: Uri? = data?.data
            if (filePath != null) {
                try {
                    val inputStream = contentResolver.openInputStream(filePath)
                    val reader = CSVReader(InputStreamReader(inputStream))
                    var nextLine: Array<String>?
                    // Read the header row to get column indices
                    val header = reader.readNext()
                    val vehIdIndex = header.indexOf("Veh ID")

                    // Read the rest of the rows
                    while (reader.readNext().also { nextLine = it } != null) {
                        if (vehIdIndex != -1) {
                            val vehicleNumber = nextLine!![vehIdIndex] // Use the dynamic index for "Veh ID"
                            val vehicle = Vehicles(vehicleNumber)
                            newArrayList.add(vehicle)
                        }
                    }

                    // Save the new data to SharedPreferences
                    saveDataToFirebase(newArrayList)
                    // Notify the adapter about the data change
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this@MainActivity2, "Data Imported", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to read file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "File path is null", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveDataToSharedPreferences(data: ArrayList<Vehicles>) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(data)
        editor.putString("vehicle_data", json)
        editor.apply()
    }

    private fun saveDataToFirebase(data: ArrayList<Vehicles>) {
        database.child("vehicle_data").setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved to Firebase", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save data to Firebase", Toast.LENGTH_SHORT).show()
            }
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









    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_activity2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                // Handle logout here
                // For now, let's show a toast message
                sessionManager.setLoggedIn(false,"admin")
                showLogoutConfirmationDialog()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Are you sure you want to logout?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            // User clicked "Yes", perform logout
            sessionManager.setLoggedIn(false,"admin")

            // Redirect to the login screen
            val intent = Intent(this@MainActivity2, LoginScreen::class.java)
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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

