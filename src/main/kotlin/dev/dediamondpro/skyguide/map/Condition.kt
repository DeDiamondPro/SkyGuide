package dev.dediamondpro.skyguide.map

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class Condition(private val condition: String) {
    private val orConditions = mutableListOf<Condition>()
    private var variable: Variable? = null
    private var smallerThen = false
    private var compareValue: Float? = null
    private var nextCondition: Condition? = null

    init {
        parseCondition()
    }

    fun isEmpty(): Boolean {
        return variable == null || compareValue == null
    }

    fun evaluate(x: Float, y: Float, z: Float): Boolean {
        for (condition in orConditions) if (condition.evaluate(x, y, z)) return true
        if (isEmpty()) return false
        val value = if (variable == Variable.X) x else if (variable == Variable.Y) y else z
        val evaluated = smallerThen && value <= compareValue!! || !smallerThen && value >= compareValue!!
        if (nextCondition == null) return evaluated
        return evaluated && nextCondition!!.evaluate(x, y, z)
    }

    private fun parseCondition() {
        val conditions = condition.lowercase(Locale.getDefault()).replace(" ", "").split("||")
        if (conditions.isEmpty()) return
        for (i in 1 until conditions.size) orConditions.add(Condition(conditions[i]))
        val match = regex.matchEntire(conditions[0])
        if (match == null) {
            if (conditions[0].isNotEmpty()) println("Unable to parse condition ${conditions[0]}")
            return
        }
        val (variable, operator, value, chain, next) = match.destructured
        this.variable = if (variable == "x") Variable.X else if (variable == "y") Variable.Y else Variable.Z
        smallerThen = operator == "<"
        compareValue = value.toFloatOrNull()
        if (chain.isEmpty() || next.isEmpty()) return
        nextCondition = Condition(next)
    }

    companion object {
        private val regex = Regex("(?<variable>[x-z])(?<operator>[<>])(?<value>-?[0-9]+)(?<chain>&&)?(?<next>.+)?")
    }

    enum class Variable {
        X,
        Y,
        Z
    }
}