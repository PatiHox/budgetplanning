package com.example.budgetplanning.fragments.settings_fragment

import android.os.Bundle
import android.view.Menu
import androidx.preference.PreferenceFragmentCompat
import com.example.budgetplanning.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
//        menu.setGroupVisible(R.id.statistics_group, false)
    }
}