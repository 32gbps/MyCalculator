package com.example.mycalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Operations(val sign: String){
    NONE(""),
    ADDITION("+"),
    SUBSTRACTION("-"),
    MULTIPLY("*"),
    DIVISION("/"),
    PERCENTAGE("%");
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Calculator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Calculator() {
    val lOperand = remember{mutableStateOf("0")}
    val rOperand = remember{mutableStateOf("")}

    val operation = remember{mutableStateOf(Operations.NONE)}

    /*
    Здесь запоминается последняя операция,
    значение правого операнда и вид оператора.
    При повторном нажатии "=" операция повторяется
      */
    val rOperand_last = remember{mutableStateOf("")}
    val operation_last = remember{mutableStateOf(Operations.NONE)}

    val setLOperand = {
            value: String ->
        if(lOperand.value == "0")
            lOperand.value = value
        else
            lOperand.value += value
    }
    val setROperand = {
            value: String ->
        if(rOperand.value == "0")
            rOperand.value = value
        else
            rOperand.value += value
    }
    val setLPeriod = {
        if(lOperand.value == "0" || lOperand.value.isEmpty())
            lOperand.value = "0."
        else if(!lOperand.value.contains('.'))
            lOperand.value += "."
    }
    val setRPeriod = {
        if(rOperand.value == "0" || rOperand.value.isEmpty())
            rOperand.value = "0."
        else if(!rOperand.value.contains('.'))
            rOperand.value += "."
    }
    val setPeriodSign = {
        if(operation.value == Operations.NONE)
            setLPeriod()
        else
            setRPeriod()
    }
    val setValue = {
            value: String ->
        if(operation.value == Operations.NONE)
            setLOperand(value)
        else
            setROperand(value)
    }

    val setDefaultState = {
        lOperand.value = "0"
        rOperand.value = ""
        operation.value = Operations.NONE

        rOperand_last.value = ""
        operation_last.value = Operations.NONE
    }

    val erase = { input:Int ->
        if(input == -1) {
            if (operation.value == Operations.NONE)
                lOperand.value = "0"
            else
                rOperand.value = "0"
        }
        else if(input == 0)
            setDefaultState()
        else
        {
            if(!rOperand.value.isEmpty())
                rOperand.value = rOperand.value.dropLast(input)
            else if(rOperand.value.isEmpty() && operation.value != Operations.NONE)
                operation.value = Operations.NONE
            else if(lOperand.value.length > 1 && lOperand.value != "0")
                lOperand.value = lOperand.value.dropLast(input)
            else
                lOperand.value = "0"
        }
    }
    val subCalculate: (Double,Double,Operations) -> String = {
            left:Double, right:Double, op:Operations ->
        var result = 0.0
        when(op){
            Operations.ADDITION -> result = left + right
            Operations.SUBSTRACTION -> result = left - right
            Operations.MULTIPLY -> result = left * right
            Operations.DIVISION -> result = left / right
            Operations.PERCENTAGE -> result = left * (right/100)
            else -> {}
        }

        var answer = result.toString().dropLastWhile { x->x =='0' }
        if(answer.endsWith('.'))
        {
            answer = answer.dropLast(1)
        }
        answer
    }
    val calculate ={
        if(operation.value != Operations.NONE && !rOperand.value.isEmpty())
        {
            lOperand.value = subCalculate(lOperand.value.toDouble(),
                rOperand.value.toDouble(),
                operation.value)
            operation_last.value = operation.value
            operation.value = Operations.NONE
            rOperand_last.value = rOperand.value
            rOperand.value = ""
        }
        else if(!rOperand_last.value.isEmpty() && operation_last.value != Operations.NONE)
            lOperand.value = subCalculate(lOperand.value.toDouble(),
                rOperand_last.value.toDouble(),
                operation_last.value)
    }

    val setOperator = { op:Operations ->
        if(operation.value != Operations.NONE)
            calculate()
         operation.value = op}


    Column(Modifier.background(Color.Black).fillMaxHeight().wrapContentHeight(Alignment.Bottom)) {
        val md = Modifier.wrapContentSize()
        val modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(2.dp)
            .weight(1f)
        Row(md.fillMaxWidth().aspectRatio(4f), verticalAlignment = Alignment.CenterVertically) {
            Text(text = lOperand.value + operation.value.sign + rOperand.value,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right,
                color = Color.White,
                fontSize = 32.sp)

        }
        Row(md) {
            Btn({ erase(-1)}, "CE", modifier)
            Btn({ erase(0) }, "C", modifier)
            Btn({ erase(1) }, "<x", modifier)
            Btn({ setOperator(Operations.DIVISION)}, "/", modifier)
        }
        Row(md) {
            Btn({ setValue("7") }, "7", modifier)
            Btn({ setValue("8") }, "8", modifier)
            Btn({ setValue("9") }, "9", modifier)
            Btn({ setOperator(Operations.MULTIPLY)}, "*", modifier)
        }
        Row(md) {
            Btn({ setValue("4") }, "4", modifier)
            Btn({ setValue("5") }, "5", modifier)
            Btn({ setValue("6") }, "6", modifier)
            Btn({ setOperator(Operations.SUBSTRACTION) }, "-", modifier)
        }
        Row(md) {
            Btn({ setValue("1") }, "1", modifier)
            Btn({ setValue("2") }, "2", modifier)
            Btn({ setValue("3") }, "3", modifier)
            Btn({ setOperator(Operations.ADDITION)}, "+", modifier)
        }
        Row(md) {
            Btn({ setOperator(Operations.PERCENTAGE) }, "%", modifier)
            Btn({ setValue("0")}, "0", modifier)
            Btn({ setPeriodSign() }, ".", modifier)
            Btn({ calculate() }, "=", modifier)
        }
    }
}
@Composable
fun Btn(onClick: ()-> Unit, label:String, modifier: Modifier = Modifier){
    val btnClrs = ButtonColors(Color.DarkGray, Color.LightGray, Color.LightGray, Color.DarkGray)
    Button(
        onClick = onClick,
        colors = btnClrs,
        modifier = modifier,
        shape = RoundedCornerShape(26.dp),
        content = { BText(label)})
}
@Composable
private fun BText(text:String){
    Text(text,
        textAlign = TextAlign.Center,
        fontSize = 36.sp)
}

