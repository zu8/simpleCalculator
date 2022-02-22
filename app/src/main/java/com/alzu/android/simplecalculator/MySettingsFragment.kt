package com.alzu.android.simplecalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


class MySettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        return view
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {

        val tst = Toast.makeText(requireContext(), "theme is changed", LENGTH_SHORT).show()
        requireActivity().recreate()
        return super.onPreferenceTreeClick(preference)
    }
}