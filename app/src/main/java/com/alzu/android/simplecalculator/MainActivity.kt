package com.alzu.android.simplecalculator



import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.preference.PreferenceManager
import com.alzu.android.simplecalculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.ArithmeticException
import java.util.logging.Level
import java.util.logging.Logger


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val dark = sp.getBoolean("switch_theme",false)
        if (dark) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null){
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CalculatorFragment>(R.id.fragmentContainerView,"CALC")
            }
        }
        binding.btnTheme.setOnClickListener {
            if(supportFragmentManager.findFragmentByTag("PREFS") == null) {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<MySettingsFragment>(R.id.fragmentContainerView, "PREFS")
                    addToBackStack(null)
                }
            }
        }
    }
}