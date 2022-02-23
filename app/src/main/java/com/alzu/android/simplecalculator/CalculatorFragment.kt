package com.alzu.android.simplecalculator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.alzu.android.simplecalculator.databinding.FragmentCalculatorBinding
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.ArithmeticException

class CalculatorFragment: Fragment() {
    private var binding: FragmentCalculatorBinding? = null
    private lateinit var tvExp: TextView
    private lateinit var tvRes: TextView
    private var operations: List<String> = listOf("+","-","*","/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("onCreate1_FrCalc","expression = EMPTY")
        setFragmentResultListener("requestKey") { _, bundle ->
            tvExp.text = bundle.getString("expr").toString()
            tvRes.text = bundle.getString("resu").toString()
            Log.i("onCreate2_FrCalc","expression = ${tvExp.text}, result = ${tvRes.text}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalculatorBinding.inflate(layoutInflater)
        binding?.let{ tvExp = it.expression}
        TextViewCompat.setAutoSizeTextTypeWithDefaults(tvExp, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        binding?.let{ tvRes = it.result }
        Log.i("onCreateView_FrCalc","expression = ${tvExp.text}, result = ${tvRes.text}")
        initialize()

        binding?.let{ return it.root}
        return null
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
        digitIni()
        // standard operations buttons (+,-,*,/)
        operatorsIni()
        // dot btn
        dotIni()
        // backspace action
        backspaceIni()
        // advanced buttons (in land-layout only) ( (,),√,∛,tn,sin,cos,π,x^y,1/x)
        advancedOperationsIni()
        // clear action
        binding?.let {
            it.btnC.setOnClickListener {
            tvExp.text = ""
            tvRes.text = ""
            }
        }
        // button "="
        binding?.let{ it.btnEquals.setOnClickListener {calculate()} }
    }

    //  setting digit button's listeners
    private fun digitIni(){
        binding?.let{ it.btn1.setOnClickListener { editExpression("1") }}
        binding?.let{ it.btn2.setOnClickListener { editExpression("2") }}
        binding?.let{ it.btn3.setOnClickListener { editExpression("3") }}
        binding?.let{ it.btn4.setOnClickListener { editExpression("4") }}
        binding?.let{ it.btn5.setOnClickListener { editExpression("5") }}
        binding?.let{ it.btn6.setOnClickListener { editExpression("6") }}
        binding?.let{ it.btn7.setOnClickListener { editExpression("7") }}
        binding?.let{ it.btn8.setOnClickListener { editExpression("8") }}
        binding?.let{ it.btn9.setOnClickListener { editExpression("9") }}
        binding?.let{ it.btn0.setOnClickListener { editExpression("0") }}
        binding?.let {
            it.btn00.setOnClickListener {
                if (tvExp.text.isEmpty() ||
                    operations.contains(tvExp.text.last().toString())
                ) editExpression("0")
                else if (tvExp.text.toString() == "0") editExpression("")
                else editExpression("00")
            }
        }
    }

    // standard operators listeners initialisation
    private fun operatorsIni(){
        binding?.let{ it.btnDiv.setOnClickListener {
            if( tvExp.text.isNotEmpty() &&
                !operations.contains(tvExp.text.last().toString()) ) editExpression("÷")
            }
        }
        binding?.let{ it.btnMult.setOnClickListener {
            if( tvExp.text.isNotEmpty() &&
                !operations.contains(tvExp.text.last().toString()) ) editExpression("×") }
        }
        binding?.let {
            it.btnPlus.setOnClickListener {
                if (tvExp.text.isNotEmpty() &&
                    !operations.contains(tvExp.text.last().toString())
                ) editExpression("+")
            }
        }
        binding?.let {
            it.btnSubstr.setOnClickListener {
                if (tvExp.text.isEmpty()) editExpression("-")
                else if (tvExp.text.last().toString() == "+") {
                    tvExp.text =
                        tvExp.text.replaceRange(tvExp.text.length - 1, tvExp.text.length, "-")
                } else if (!operations.contains(tvExp.text.last().toString())) editExpression("-")
            }
        }
    }

    // backspace listener initialisation
    private fun backspaceIni(){
        binding?.let {
            it.btnBack.setOnClickListener {
                if (tvExp.text.isNotEmpty()) {
                    val tmp: String = tvExp.text.toString()
                    if (tmp.endsWith("sin") || tmp.endsWith("cos")) {
                        tvExp.text = tmp.subSequence(0, tmp.length - 3)
                    } else if (tmp.endsWith("tn")) tvExp.text = tmp.subSequence(0, tmp.length - 2)
                    else tvExp.text = tmp.subSequence(0, tmp.length - 1)
                } else editExpression("")
            }
        }
    }

    // setting dot-button listener
    private fun dotIni(){
        binding?.let {
            it.btnDot.setOnClickListener {
                if (tvExp.text.isEmpty() ||
                    operations.contains(tvExp.text.last().toString())
                ) editExpression("0.")
                else editExpression(".")
            }
        }
    }
    // setting advanced-operation(in land-mode only) button's listeners
    private fun advancedOperationsIni(){
        binding?.let{ it.btnLBracket?.setOnClickListener { editExpression("(") }}
        binding?.let{ it.btnRBracket?.setOnClickListener { editExpression(")") }}
        binding?.let{ it.btnSqrt?.setOnClickListener { editExpression("√") }}
        binding?.let{ it.btnCubRoot?.setOnClickListener { editExpression("∛") }}
        binding?.let{ it.btnTn?.setOnClickListener { editExpression("tn") }}
        binding?.let{ it.btnSin?.setOnClickListener { editExpression("sin") }}
        binding?.let{ it.btnCos?.setOnClickListener { editExpression("cos") }}
        binding?.let{ it.btnPi?.setOnClickListener { editExpression("π")}}
        binding?.let{ it.btnXPowY?.setOnClickListener { editExpression("^") }}
        binding?.let{ it.btn1ToX?.setOnClickListener { editExpression("^(-1)") }}
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

    override fun onStop() {
        super.onStop()
        val myBundle = Bundle()
        myBundle.putString("expr",tvExp.text.toString())
        myBundle.putString("resu",tvRes.text.toString())
        // save values from text fields using FragmentResultAPI
        parentFragmentManager.setFragmentResult("requestKey",myBundle)
        Log.i("onStop_FrCalc","val1 = ${tvExp.text}, val2 = ${tvRes.text}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // setting binding to null to avoid the memory leak
        binding = null
        Log.i("onDestroyView_FrCalc","binding = $binding")
    }

}