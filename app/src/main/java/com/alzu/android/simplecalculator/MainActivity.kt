package com.alzu.android.simplecalculator

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import com.alzu.android.simplecalculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tvExp: TextView
    private lateinit var tvRes: TextView
    private var operations: List<String> = listOf("+","-","*","/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tvExp = binding.expression
        TextViewCompat.setAutoSizeTextTypeWithDefaults(tvExp,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        tvRes = binding.result
        initialize()
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString("expression",tvExp.text.toString())
        outState.putString("result",tvRes.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        tvExp.text = savedInstanceState.getString("expression")
        tvRes.text = savedInstanceState.getString("result")
    }

    private fun editExpression(sym: String) {
        if (tvRes.text.isNotEmpty()) {
            if (tvRes.text.toString() == getString(R.string.zero_error)) tvRes.text = ""
            tvExp.text = tvRes.text
            tvRes.text = ""
        }
        var tmp = tvExp.text.toString()
        tmp += sym
        tvExp.text = tmp
    }

    private fun initialize() {
        // digit buttons
        binding.btn1.setOnClickListener { editExpression("1") }
        binding.btn2.setOnClickListener { editExpression("2") }
        binding.btn3.setOnClickListener { editExpression("3") }
        binding.btn4.setOnClickListener { editExpression("4") }
        binding.btn5.setOnClickListener { editExpression("5") }
        binding.btn6.setOnClickListener { editExpression("6") }
        binding.btn7.setOnClickListener { editExpression("7") }
        binding.btn8.setOnClickListener { editExpression("8") }
        binding.btn9.setOnClickListener { editExpression("9") }
        binding.btn0.setOnClickListener { editExpression("0") }
        binding.btn00.setOnClickListener {
            if (tvExp.text.isEmpty()||
                operations.contains(tvExp.text.last().toString())) editExpression("0")
            else if (tvExp.text.toString() == "0") editExpression("")
            else editExpression("00")
        }
        binding.btnDot.setOnClickListener {
            if (tvExp.text.isEmpty() ||
                operations.contains(tvExp.text.last().toString()) ) editExpression("0.")
            else editExpression(".")
        }

        // standard operations buttons (+,-,*,/)
        binding.btnDiv.setOnClickListener {
            if( tvExp.text.isNotEmpty() &&
               !operations.contains(tvExp.text.last().toString()) ) editExpression("÷")
        }
        binding.btnMult.setOnClickListener {
            if( tvExp.text.isNotEmpty() &&
                !operations.contains(tvExp.text.last().toString()) ) editExpression("×") }
        binding.btnPlus.setOnClickListener {
            if( tvExp.text.isNotEmpty() &&
                !operations.contains(tvExp.text.last().toString()) ) editExpression("+") }
        binding.btnSubstr.setOnClickListener {
            if (tvExp.text.isEmpty()) editExpression("-")
            else if(tvExp.text.last().toString() == "+") {
                tvExp.text = tvExp.text.
                replaceRange(tvExp.text.length-1,tvExp.text.length,"-")
            }
            else if( !operations.contains(tvExp.text.last().toString())) editExpression("-") }

        // clear action
        binding.btnC.setOnClickListener {
            tvExp.text = ""
            tvRes.text = ""
        }

        // backspace action
        binding.btnBack.setOnClickListener {
            if (tvExp.text.isNotEmpty()){
                val tmp: String = tvExp.text.toString()
                if (tmp.endsWith("sin") || tmp.endsWith("cos")) {
                    tvExp.text = tmp.subSequence(0,tmp.length-3)
                }
                else if (tmp.endsWith("tn")) tvExp.text = tmp.subSequence(0,tmp.length-2)
                else tvExp.text = tmp.subSequence(0,tmp.length-1)
            }
            else editExpression("")
        }

        // advanced buttons (in land-layout only) ( (,),√,∛,tn,sin,cos,π,x^y,1/x)
        binding.btnLBracket?.setOnClickListener {
            editExpression("(")
        }
        binding.btnRBracket?.setOnClickListener {
            editExpression(")")
        }
        binding.btnSqrt?.setOnClickListener {
            editExpression("√")
        }
        binding.btnCubRoot?.setOnClickListener {
            editExpression("∛")
        }
        binding.btnTn?.setOnClickListener {
            editExpression("tn")
        }
        binding.btnSin?.setOnClickListener {
            editExpression("sin")
        }
        binding.btnCos?.setOnClickListener {
            editExpression("cos")
        }
        binding.btnPi?.setOnClickListener {editExpression("π")}
        binding.btnXPowY?.setOnClickListener {editExpression("^") }
        binding.btn1ToX?.setOnClickListener { editExpression("^(-1)") }

        // button "="
        binding.btnEquals.setOnClickListener {calculate()}
    }

    // change some symbols in expression string to become "understandable" for parser exp4j
    private fun changeSomeSymbols(str: String): String{
        val listOfCharsToChange: List<String> = listOf("×","÷","√","∛","t")
        val testList: MutableList<String> = mutableListOf()
        str.splitToSequence("").forEach { e -> testList.add(e) }
        for(i in testList.indices){
            for (j in listOfCharsToChange.indices){
                if (testList[i] == listOfCharsToChange[j]){
                    when(listOfCharsToChange[j]){
                        "×" -> testList[i] = "*"
                        "÷" -> testList[i] = "/"
                        "√" -> testList[i] = "sqrt"
                        "∛" -> testList[i] = "cbrt"
                        "t"-> {
                            testList[i] = "tan"
                            testList[i+1] = ""
                        }
                    }
                }
            }
        }
        return testList.joinToString("")
    }

    private fun calculate() {
        try {
            val stringToParse: String = tvExp.text.toString()
            val resultString = changeSomeSymbols(stringToParse)
            val impl = ExpressionBuilder(resultString).build()
            val endResult = impl.evaluate()
            val longRes = endResult.toLong()
            val doubleRes = endResult.toFloat()
            if(tvExp.text.isNotEmpty()&& tvRes.text.isNotEmpty()) editExpression("")
            if (endResult == longRes.toDouble()) {
                tvRes.text = longRes.toString()
            } else {
                tvRes.text = doubleRes.toString()
            }
        } catch (e: ArithmeticException){
            tvRes.text = getString(R.string.zero_error)
        }
        catch (e: Exception) {
            Log.d("Error", "${e.message}")
        }
    }
}