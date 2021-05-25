package com.example.budgetplanning

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.forEach
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController

// TODO: Добавить страницу истории ручных изменений бюджета

class MainActivity : AppCompatActivity() {

    lateinit var mainMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        setupActionBarWithNavController(findNavController(R.id.nav_host_fragment))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        this.mainMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                true
            }
            R.id.action_statistics -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_FirstFragment_to_statisticsFragment)
                true
            }
            R.id.action_show_for_period -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPopupMenu(v: View, menuResId: Int) {
        val popupMenu: PopupMenu = PopupMenu(this, v)
        popupMenu.inflate(menuResId)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}