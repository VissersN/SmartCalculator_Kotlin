package calculator

import java.util.*
import kotlin.math.pow
import java.math.BigInteger


enum class OperationType {

    ADD,
    SUBSTRACT,
    TIMES,
    DIVIDE,
    POWER,
    PARLEFT,
    PARRIGHT
}

val storage: MutableMap<String, BigInteger> = mutableMapOf()
val regexExpression = Regex ("([+-]+|[*/]|[()]|[0-9]*|[a-zA-Z]*)")


fun main() {
    Start@
    while (true) {
        val input = Scanner(System.`in`)
        val line = input.nextLine()

        if (line.isEmpty()) continue // detects empty line and continues application

//commands
        if ("/.*".toRegex().matches(line)) {
            when (line) {
                "/exit" -> {
                    println("Bye!")
                    break
                }

                "/help" -> {
                    println(
                        "The program calculates the sum of numbers. \n" +
                                "Also, as you remember from school math, two adjacent\n" +
                                " minus signs turn into a plus. Therefore, if the user \n" +
                                " inputs --, it should be read as +; if they input ----, \n" +
                                " it should be read as ++, and so on. The smart calculator \n" +
                                " ought to have such a feature."
                    )
                    continue
                }

                else -> {
                    println("Unknown command")
                    continue
                }
            }
        }

        //declarations of variables
        if ("=".toRegex().containsMatchIn(line)) {
            declarationVariables(line)
            continue
        }
        //putting together an input to process to postfix and calculate
        val spacedInput = line.replace("(", "( ").replace(")", " )")
        val inputList = spacedInput.split(" ").filter { it.isNotBlank() }
        if (!expressionCheck(inputList)) continue // TODO: Deze klopt nog niet.
        val postFix = fromInfixToPostfix(inputList)
        println(calculatePostFix(postFix))
    }
}

private fun declarationVariables(line: String) {
    val spaceLessLine = line.replace(" ", "") // remove spaces from input
    val listOfDeclaration = spaceLessLine.split("=")
    if (!checkAssignment(listOfDeclaration)) return
    if ("[+-]?[0-9]+".toRegex().matches(listOfDeclaration[1])) {
        storage[listOfDeclaration[0]] = listOfDeclaration[1].toBigInteger()
    } else if ("[a-zA-Z]+".toRegex().matches(listOfDeclaration[1])) {
        if (!storage.containsKey(listOfDeclaration[1])) {
            println("Invalid assignment")
            return
        }
        storage[listOfDeclaration[0]] = storage[listOfDeclaration[1]]!!
    }
    return
}

fun fromInfixToPostfix(input: List<CharSequence>): MutableList<Any> {
    val output: MutableList<Any> = mutableListOf()
    val stack: MutableList<Any> = mutableListOf()
    input.forEach {
        if ("[0-9a-zA-Z]+".toRegex().matches(it)) {
            output.add(it)
        } else {
            when (detectOperation(it.toString())) {
                OperationType.ADD, OperationType.SUBSTRACT -> {
                    while ((stack.isNotEmpty() && (stack.last() != OperationType.PARLEFT))) { // no additional checks are needed as ADD or SUBSTRACT are of the lowest precedence
                        output.add(stack.last()) // adding top operator to output
                        stack.removeAt(stack.size - 1) // remove top operator from stack
                    }
                    stack.add(detectOperation(it.toString()))
                }

                OperationType.TIMES, OperationType.DIVIDE -> {
                    while ((stack.isNotEmpty() && (stack.last() != OperationType.PARLEFT) &&
                                !(stack.last() == OperationType.ADD || stack.last() == OperationType.SUBSTRACT))
                    ) {
                        output.add(stack.last())
                        stack.removeAt(stack.size - 1)
                    }
                    stack.add(detectOperation(it.toString()))
                }

                OperationType.POWER, OperationType.PARLEFT -> stack.add(detectOperation(it.toString())) // always adds to the stack without removing top element from stack

                OperationType.PARRIGHT -> {
                    while (stack.isNotEmpty() && stack.last() != OperationType.PARLEFT) {
                        output.add(stack.last()) // moves all the operators to output
                        stack.removeAt(stack.size - 1)
                    }
                    stack.removeAt(stack.size - 1) // removes the left parenthesis
                }
            }
        }
    }
    while (stack.isNotEmpty()) {
        output.add(stack.removeAt(stack.size - 1))

    }
    return output
}

fun calculatePostFix(rpn: MutableList<Any>): BigInteger {
    val stack: MutableList<BigInteger> = mutableListOf()
    while (rpn.isNotEmpty()) {
        if (rpn[0] !in OperationType.values()) {
            if ("[0-9]+".toRegex().matches(rpn[0].toString())) {
                stack.add(rpn[0].toString().toBigInteger())
                rpn.removeAt(0)
            } else {
                if (!storage.containsKey(rpn[0])) { //TODO: Waarden ophalen werkt nog niet goed.
                    println("Unknown variable")
                }
                stack.add(storage.getValue(rpn[0].toString().lowercase()))
                rpn.removeAt(0)
            }
        } else {
            val operator = rpn[0] as OperationType
            val intermediateResult = operate(stack[stack.size -2], stack[stack.size - 1], operator)
            repeat(2) {stack.removeAt(stack.size - 1)}
            rpn.removeAt(0)
            stack.add(intermediateResult)
        }
    }
return stack[0]
}

// checks if there's a valid variable name on the left, numbers on the right side and no more than two arguments
fun checkAssignment(listOfDeclaration: List<String>): Boolean {
    try {
        if (!"[a-zA-Z]+".toRegex().matches(listOfDeclaration[0])) {
            throw IllegalArgumentException("Invalid identifier")
        }
        if (!"[+-]?[0-9]+|[a-zA-Z]+".toRegex().matches(listOfDeclaration[1])) {
            throw IllegalArgumentException("Invalid assignment")
        }
        if (listOfDeclaration.size >= 3) {
            throw IllegalArgumentException("Invalid assignment")
        }
    } catch (e: IllegalArgumentException) {
        println(e.message)
        return false
    }
    return true
}

fun expressionCheck(list: List<String>): Boolean {
    try {
        var parLeftPresent = false
        var parRightPresent = false
        for (i in list.indices) {
            if (!regexExpression.matches(list[i])) {
                throw IllegalArgumentException()
            }
            if ("[(]".toRegex().containsMatchIn(list[i])) {
                parLeftPresent = true
            }
            if ("[)]".toRegex().containsMatchIn(list[i])) {
                parRightPresent = true
            }
        }
        if (!parLeftPresent == parRightPresent) { // checks if left and right parenthesis both exists, of both are not
            throw IllegalArgumentException()
        }
    } catch (e: IllegalArgumentException) {
        println("Invalid expression")
        return false
    }
    return true
}


fun detectOperation(s: String): OperationType {
    val listOperators = s.split("")
    var currentOperator = OperationType.ADD
    for (i in listOperators.indices) {
        when (listOperators[i]) {
//          "+" -> bij een plus verandert er niet aan het operator type

            "-" -> if (currentOperator == OperationType.ADD) {
                currentOperator = OperationType.SUBSTRACT
            } else if (currentOperator == OperationType.SUBSTRACT) {
                currentOperator = OperationType.ADD
            }

            "*" -> currentOperator = OperationType.TIMES
            "/" -> currentOperator = OperationType.DIVIDE
            "^" -> currentOperator = OperationType.POWER
            "(" -> currentOperator = OperationType.PARLEFT
            ")" -> currentOperator = OperationType.PARRIGHT
        }
    }
    return currentOperator
}

fun operate(value1: BigInteger, value2: BigInteger, operator: OperationType): BigInteger {
    when (operator) {
        OperationType.ADD -> {
            return value2 + value1
        }

        OperationType.SUBSTRACT -> {
            return value1 - value2
        }

        OperationType.TIMES -> {
            return value1 * value2
        }

        OperationType.DIVIDE -> {
            return value1 / value2
        }

        OperationType.POWER -> {
            return value1.toDouble().pow(value2.toDouble()).toInt().toBigInteger()
        }

        OperationType.PARLEFT, OperationType.PARRIGHT -> TODO()
    }
}
