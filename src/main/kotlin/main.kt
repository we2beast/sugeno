import Constants.WEIGHT
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Функция расчета кинетической энергии автомобиля
 * @param weight - Масса автомобиля
 * @param speed - Скорость автомобиля
 * @return Кинетическая энергия
 */
fun calculateKineticEnergy(weight: Double, speed: Double) = (weight * (speed * speed)) / 2.0

/**
 * Функция расчета расстояния
 * @param distance - Расстояние от автомобиля до препятствия
 * @param speed - Скорость автомобиля
 * @return Расстояние между автомобилем и препятствием
 */
fun calculateDistance(distance: Double, speed: Double) = distance - speed


fun main(args: Array<String>) {
    val speed = 17.0 // м/с
    val distance = 330.0 // метры

    var newDistance: Double = distance - speed * 1
    var newSpeed = speed
    var newKineticEnergy = calculateKineticEnergy(WEIGHT, newSpeed)

    var i = 1.0
    loop@ while (true) {
        // Расчет ошибки
        val errBrake = run(newSpeed, newDistance)

        println("Шаг $i. Ваша скорость $newSpeed и расстояние $newDistance. Ошибка $errBrake. Кинетическая энергия $newKineticEnergy")

        // Подсчет кинетической энергии
        newKineticEnergy = calculateKineticEnergy(WEIGHT, newSpeed)

        // Подсчет кинетической энергии с ошибкой
        val newKineticEnergyWithError = if (newKineticEnergy - errBrake <= 0.0) 0.0 else newKineticEnergy - errBrake

        newDistance = calculateDistance(newDistance, newSpeed)
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

/**
 * Функция расчета нечеткого вывода
 * @param distance - Расстояние от автомобиля до препятствия
 * @param speed - Скорость автомобиля
 * @return Вывод нечеткого контроллера
 */
fun run(speed: Double, distance: Double): Double {
    // Фаззификация для скорости автомобиля
    val mapSpeed = mapOf(
        Speed.MembershipFunction.FAST to Speed.calculate(speed.toInt(), Speed.MembershipFunction.FAST),
        Speed.MembershipFunction.MEDIUM to Speed.calculate(speed.toInt(), Speed.MembershipFunction.MEDIUM),
        Speed.MembershipFunction.SLOW to Speed.calculate(speed.toInt(), Speed.MembershipFunction.SLOW)
    )

    // Фаззификация для расстояния
    val mapDistance = mapOf(
        Distance.MembershipFunction.FARAWAY to Distance.calculate(
            distance.toInt(),
            Distance.MembershipFunction.FARAWAY
        ),
        Distance.MembershipFunction.NEARLY to Distance.calculate(distance.toInt(), Distance.MembershipFunction.NEARLY),
        Distance.MembershipFunction.EXTREMELY_CLOSE to Distance.calculate(
            distance.toInt(),
            Distance.MembershipFunction.EXTREMELY_CLOSE
        )
    )

    // Алгоритм сугено. Вычисление выходных переменных
    val mapBraking = calculateBraking(speed, distance)

    // Вычисление истинности посылок
    val databaseOfKnowledge = mapSpeed.map { fuzzySpeed ->
        mapDistance.map { fuzzyDistance ->
            min(fuzzySpeed.value, fuzzyDistance.value)
        }
    }

    // Вычисление значений выходной переменной
    val sugeno = mutableListOf<Double>()
    for (i in databaseOfKnowledge.indices) {
        for (j in mapBraking.indices) {
            sugeno += databaseOfKnowledge[i][j] * mapBraking[i][j]
        }
    }

    var sugenoSum = sugeno.sum() / databaseOfKnowledge.sumByDouble { it.sum() }

    sugenoSum = if (sugenoSum > 0) sugenoSum else 0.0

    return sugenoSum
}

// Правила Сугено
fun calculateBraking(speed: Double, distance: Double): List<List<Double>> {
    return listOf(
        listOf(0.0, 70.0 * speed + 12.0 * distance, 80.0 * speed + 120.0 * distance),
        listOf(12.0 * speed + 20.0 * distance, 30.0 * speed + 16.0 * distance, 10.0 * speed + 70.0 * distance),
        listOf(0.0, 12.0 * speed + 10.0 * distance, 34.0 * speed + 120.0 * distance),
    )
}

