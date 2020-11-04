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
    // Пользовательские данные, которые можно изменять
    val speed = 17.0 // м/с
    val distance = 600.0 // метры


    // Ниже код в данной функции лучше не редактировать
    var newDistance = distance
    var newSpeed = speed
    var newKineticEnergy = calculateKineticEnergy(WEIGHT, newSpeed)

    // Временной шаг в секундах
    var i = 1.0
    loop@ while (true) {
        // Расчет ошибки
        val errBrake = run(newSpeed, newDistance)

        println("Шаг $i. Ваша скорость $newSpeed и расстояние $newDistance. Ошибка $errBrake. Кинетическая энергия $newKineticEnergy")

        // Подсчет кинетической энергии
        newKineticEnergy = calculateKineticEnergy(WEIGHT, newSpeed)

        val err = newKineticEnergy - (newKineticEnergy * (errBrake )) / 10

        // Подсчет кинетической энергии с ошибкой
        val newKineticEnergyWithError = if (err <= 0.0) 0.0 else err

        // Расчет новой скорости, исходя из новой кинетической энергии и массы автомоболия
        newSpeed =
            if (sqrt((2.0 * newKineticEnergyWithError) / WEIGHT) <= 0.0) 0.0 else sqrt((2.0 * newKineticEnergyWithError) / WEIGHT)

        // Расчет дистанции
        newDistance = calculateDistance(newDistance, newSpeed)

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

    // Суммирование всех значений матриц
    var sugenoSum = sugeno.sum() / databaseOfKnowledge.sumByDouble { it.sum() }

    sugenoSum = if (sugenoSum > 0) sugenoSum else 0.0

    return sugenoSum
}

/**
 * Функция правил Сугено
 * @param distance - Расстояние от автомобиля до препятствия
 * @param speed - Скорость автомобиля
 * @return Матрица правил для Сугено
 */
fun calculateBraking(speed: Double, distance: Double): List<List<Double>> {
    return listOf(
        listOf(
            0.0 * Speed.calculate(speed.toInt(), Speed.MembershipFunction.FAST) + 0.0 * Distance.calculate(distance.toInt(), Distance.MembershipFunction.FARAWAY),
            0.5 * Speed.calculate(speed.toInt(), Speed.MembershipFunction.FAST) + 1.5 * Distance.calculate(distance.toInt(), Distance.MembershipFunction.NEARLY),
            10.3 * Speed.calculate(speed.toInt(), Speed.MembershipFunction.FAST) + 1.8 * Distance.calculate(distance.toInt(), Distance.MembershipFunction.EXTREMELY_CLOSE),
        ),
        listOf(
            0.0 * Speed.calculate(speed.toInt(), Speed.MembershipFunction.MEDIUM) + 0.0 * Distance.calculate(distance.toInt(), Distance.MembershipFunction.FARAWAY),
            0.7 * Speed.calculate(speed.toInt(), Speed.MembershipFunction.MEDIUM) + 1.5 * Distance.calculate(distance.toInt(), Distance.MembershipFunction.NEARLY),
            1.3 * Speed.calculate(speed.toInt(), Speed.MembershipFunction.MEDIUM) + 3.5 * Distance.calculate(distance.toInt(), Distance.MembershipFunction.EXTREMELY_CLOSE),
        ),
        listOf(
            0.2 * Speed.calculate(speed.toInt(), Speed.MembershipFunction.SLOW) + 0.3 * Distance.calculate(distance.toInt(), Distance.MembershipFunction.FARAWAY),
            2.5 * Speed.calculate(speed.toInt(), Speed.MembershipFunction.SLOW) + 2.5 * Distance.calculate(distance.toInt(), Distance.MembershipFunction.NEARLY),
            13.3 * Speed.calculate(speed.toInt(), Speed.MembershipFunction.SLOW) + 15.5 * Distance.calculate(distance.toInt(), Distance.MembershipFunction.EXTREMELY_CLOSE),
        ),
    )
}

