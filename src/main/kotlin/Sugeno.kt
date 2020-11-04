import Constants.WEIGHT
import com.github.onotoliy.fuzzycontroller.algorithms.Sugeno
import com.github.onotoliy.fuzzycontroller.algorithms.SugenoBuilder
import com.github.onotoliy.fuzzycontroller.algorithms.SugenoBuilder.rule
import com.github.onotoliy.fuzzycontroller.mf.MembershipFunctionBuilder.sLine
import com.github.onotoliy.fuzzycontroller.mf.MembershipFunctionBuilder.zLine
import com.github.onotoliy.fuzzycontroller.operators.Operator
import com.github.onotoliy.fuzzycontroller.operators.OperatorBuilder.and
import com.github.onotoliy.fuzzycontroller.utils.Utils.getOrThrow
import com.github.onotoliy.fuzzycontroller.utils.Utils.unmodifiableList
import com.github.onotoliy.fuzzycontroller.variables.Term
import com.github.onotoliy.fuzzycontroller.variables.Variable
import com.github.onotoliy.fuzzycontroller.variables.VariableBuilder
import kotlin.math.sqrt

fun main() {
    val distance: Variable = VariableBuilder.of(
        "distance", mapOf(
            "distance_000" to zLine(300.0, 1500.0),
            "distance_050" to zLine(80.0, 300.0),
            "distance_100" to zLine(5.0, 80.0)
        )
    )
    val speed: Variable = VariableBuilder.of(
        "speed", mapOf(
            "speed_000" to sLine(15.0, 30.0),
            "speed_050" to sLine(10.0, 15.0),
            "speed_100" to sLine(5.0, 10.0)
        )
    )
    val rules: List<Sugeno.Rule> = listOf(
        rule(
            and(speed.`is`("speed_000"), distance.`is`("distance_000")),
            condition(0.00, speed.get("speed_000"), distance.get("distance_000"))
        ),
        rule(
            and(speed.`is`("speed_000"), distance.`is`("distance_050")),
            condition(0.50, speed.get("speed_000"), distance.get("distance_050"))
        ),
        rule(
            and(speed.`is`("speed_000"), distance.`is`("distance_100")),
            condition(1.00, speed.get("speed_000"), distance.get("distance_100"))
        ),

        rule(
            and(speed.`is`("speed_050"), distance.`is`("distance_000")),
            condition(0.1, speed.get("speed_050"), distance.get("distance_000"))
        ),
        rule(
            and(speed.`is`("speed_050"), distance.`is`("distance_050")),
            condition(0.50, speed.get("speed_050"), distance.get("distance_050"))
        ),
        rule(
            and(speed.`is`("speed_050"), distance.`is`("distance_100")),
            condition(1.0, speed.get("speed_050"), distance.get("distance_100"))
        ),

        rule(
            and(speed.`is`("speed_100"), distance.`is`("distance_000")),
            condition(0.1, speed.get("speed_100"), distance.get("distance_000"))
        ),
        rule(
            and(speed.`is`("speed_100"), distance.`is`("distance_050")),
            condition(0.50, speed.get("speed_100"), distance.get("distance_050"))
        ),
        rule(
            and(speed.`is`("speed_100"), distance.`is`("distance_100")),
            condition(1.0, speed.get("speed_100"), distance.get("distance_100"))
        ),
    )
    val sugeno = SugenoBuilder.of(rules)

    // Пользовательские данные, которые можно изменять
    var newDistance = 600.0
    var newSpeed = 10.0

    // Ниже код в данной функции лучше не редактировать
    var newKineticEnergy = calculateKineticEnergy(WEIGHT, newSpeed)

    // Временной шаг в секундах
    var i = 1.0
    loop@ while (true) {
        // Расчет ошибки
        val errBrake = run(newSpeed, newDistance)

        println("Шаг $i. Ваша скорость $newSpeed и расстояние $newDistance. Ошибка $errBrake. Кинетическая энергия $newKineticEnergy")

        // Подсчет кинетической энергии
        newKineticEnergy = calculateKineticEnergy(WEIGHT, newSpeed)

        // Подсчет кинетической энергии с ошибкой
        val newKineticEnergyWithError = newKineticEnergy - (newKineticEnergy * (calc(sugeno, mapOf("speed" to newSpeed, "distance" to newDistance))))

        // Расчет дистанции
        newDistance = calculateDistance(newDistance, newSpeed)

        // Расчет новой скорости, исходя из новой кинетической энергии и массы автомоболия
        newSpeed =
            if (sqrt((2.0 * newKineticEnergyWithError) / WEIGHT) <= 0.0) 0.0 else sqrt((2.0 * newKineticEnergyWithError) / WEIGHT)

        if (newDistance <= 0.0) {
            println("Вы врезались в стену. Ваша скорость $newSpeed и расстояние $newDistance")
            break@loop
        } else if (newSpeed <= 0.0) {
            println("Вы остановили машину. Ваша скорость $newSpeed и расстояние $newDistance")
            break@loop
        }

        i++
    }
}

private fun calc(sugeno: Sugeno, parameters: Map<String, Double>): Double {
    return sugeno.clarity(parameters)
}

private fun condition(weight: Double, speed: Term, distance: Term): Operator {
    return object : Operator {
        override fun calc(parameters: Map<String?, Double?>?): Double {
            val test = weight * speed.calc(getOrThrow(parameters, "speed")!!) + weight * distance.calc(
                getOrThrow(
                    parameters,
                    "distance"
                )!!
            )

            return test
        }

        override fun getTerms(): MutableList<Term> =
            unmodifiableList(unmodifiableList(speed), unmodifiableList(distance))
    }
}
