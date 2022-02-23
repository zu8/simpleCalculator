package com.alzu.android.simplecalculator

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


class MySettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        Toast.makeText(requireContext(), "theme is changed", LENGTH_SHORT).show()
        requireActivity().recreate()
        return super.onPreferenceTreeClick(preference)
    }
}