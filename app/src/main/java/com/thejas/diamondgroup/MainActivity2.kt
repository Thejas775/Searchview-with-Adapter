package com.thejas.diamondgroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        newArrayList = arrayListOf()
        tempArrayList = arrayListOf()
        number = arrayOf(
            "MH123123", "MH123456", "MH789012", "KA012345", "TN987654", "KL567890", // Existing numbers
            "KA567890", "TN123456", "KL098765", "MH789123", "KA345678", // More random numbers
            "TN543210", "KL901234", "MH567890", "KA654321", "TN234567", // Even more random numbers
            "MH234567", "KA789012", "TN901234", "KL345678" // Additional random numbers
        )

        getUserdata()

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

        // Handle search query in the SearchView
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // Handle if needed, but we'll use onQueryTextChange for live filtering
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    adapter.updateList(newArrayList)

                    getUserdata()
                    adapter = MyAdapter(newArrayList)
                    newRecylerview.adapter = adapter


             //       adapter.updateList(newArrayList)
                //    Log.d("MainActivity2", "Array: $newArrayList")
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

    private fun getUserdata() {
        for (i in number.indices) {
            val numbert = Vehicles(number[i])
            newArrayList.add(numbert)
        }
        tempArrayList.addAll(newArrayList) // Initially populate tempArrayList with all data
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
                sessionManager.setLoggedIn(false)
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
            sessionManager.setLoggedIn(false)

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
