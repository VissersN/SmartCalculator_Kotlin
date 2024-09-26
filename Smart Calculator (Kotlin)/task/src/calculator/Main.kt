package calculator

import java.util.*

enum class operationType {
    ADD,
    SUBSTRACT,
    TIMES,
    DIVIDE
}

val storage: MutableMap<String, Int> = mutableMapOf()
val regexExpression = Regex("([+\\-]*[0-9]*|[a-zA-Z]*)")

fun main() {
    Start@
    while (true) {
        val input = Scanner(System.`in`)
        val line = input.nextLine()

        if (!line.isNotEmpty()) continue // detects empty line and continues application

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
            val spaceLessLine = line.replace(" ","") // remove spaces from input
            val listOfDeclaration = spaceLessLine.split("=")
            if(!checkAssignment(listOfDeclaration)) continue
            if ("[0-9]+".toRegex().matches(listOfDeclaration[1])) {
                storage[listOfDeclaration[0]] = listOfDeclaration[1].toInt()
            } else if ("[a-zA-Z]+".toRegex().matches(listOfDeclaration[1])) {
                if (!storage.containsKey(listOfDeclaration[1])) {
                    println("Invalid assignment")
                    continue@Start
                }
                storage[listOfDeclaration[0]] = storage[listOfDeclaration[1]]!!
            }
            continue
        }
        val listOfValuesAndOperators = line.split(" ")
        if (!expressionCheck(listOfValuesAndOperators)) continue
        var result = 0
        var operator = operationType.ADD
        var changingValue = 0
        for (i in listOfValuesAndOperators.indices) {
            if (i == 0 || i % 2 == 0) {
                if ("[0-9]+".toRegex().matches(listOfValuesAndOperators[i])) {
                    changingValue = listOfValuesAndOperators[i].toInt()
                } else if ("[a-zA-Z]+".toRegex().matches(listOfValuesAndOperators[i])) {
                    if (!storage.containsKey(listOfValuesAndOperators[i])) {
                        println("Unknown variable")
                        continue@Start
                    }
                    changingValue = storage[listOfValuesAndOperators[i]]!!
                }
                result = operate(changingValue = changingValue, currentResult = result, operator)
            } else {
                operator = detectOperation(listOfValuesAndOperators[i])

            }
        }

//            val result = listOfInts.sum()

        println(result)

    }
}
// checks if there's a valid variable name on the left, numbers on the right side and no more than two arguments
fun checkAssignment(listOfDeclaration: List<String>): Boolean {
    try {
        if (!"[a-zA-Z]+".toRegex().matches(listOfDeclaration[0])) {
            throw IllegalArgumentException("Invalid identifier")
        }
        if (!regexExpression.matches(listOfDeclaration[1])) {
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
        for (i in list.indices) {
//            val regexExpression = Regex("([+\\-]*[0-9]*|[a-zA-Z]*)")
            if (!regexExpression.matches(list[i])) {
                throw IllegalArgumentException()
            }
        }
    } catch (e: IllegalArgumentException) {
        println("Invalid expression")
        return false
    }
    return true
}


fun detectOperation(s: String): operationType {
    val listOperators = s.split("")
    var currentOperator = operationType.ADD
    for (i in listOperators.indices) {
        when (listOperators[i]) {
            "+" -> when (currentOperator) {
                operationType.ADD -> currentOperator = operationType.ADD
                operationType.SUBSTRACT -> currentOperator = operationType.SUBSTRACT
                operationType.TIMES -> TODO()
                operationType.DIVIDE -> TODO()
            }

            "-" -> when (currentOperator) {
                operationType.ADD -> currentOperator = operationType.SUBSTRACT
                operationType.SUBSTRACT -> currentOperator = operationType.ADD
                operationType.TIMES -> TODO()
                operationType.DIVIDE -> TODO()
            }
        }
    }
    return currentOperator
}

fun operate(changingValue: Int, currentResult: Int, operator: operationType): Int {
    when (operator) {
        operationType.ADD -> {
            return currentResult + changingValue
        }

        operationType.SUBSTRACT -> {
            return currentResult - changingValue
        }

        operationType.TIMES -> {
            return currentResult * changingValue
        }

        operationType.DIVIDE -> {
            return currentResult / changingValue
        }
    }
}
