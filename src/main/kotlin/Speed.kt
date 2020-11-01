object Speed {
    enum class MembershipFunction {
        FAST, // Быстрая скорость
        MEDIUM, // Средняя скорость
        SLOW, // Медленная скорость
    }

    // Функция принадлежности для скорости
    fun calculate(speed: Int, membership: MembershipFunction) =
        when (membership) {
            MembershipFunction.FAST ->
                when {
                    speed >= 30 -> 1.0
                    speed in 15..30 -> (speed - 30) / 15.0
                    else -> 0.2
                }
            MembershipFunction.MEDIUM -> {
                when {
                    speed >= 15 -> 1.0
                    speed in 10..15 -> (15 - speed) / 5.0
                    else -> 0.0
                }
            }
            MembershipFunction.SLOW -> {
                when {
                    speed <= 10 -> 1.0
                    speed in 5..10 -> (10 - speed) / 5.0
                    else -> 0.2
                }
            }
        }
}
