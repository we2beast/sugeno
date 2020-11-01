object Distance {
    enum class MembershipFunction {
        FARAWAY, // Далеко
        NEARLY, // Близко
        EXTREMELY_CLOSE, // Крайне близко
    }

    // Функция принадлежности для расстояния (метры)
    fun calculate(distance: Int, membership: MembershipFunction) =
        when (membership) {
            MembershipFunction.FARAWAY ->
                when {
                    distance <= 600 -> 0.0
                    distance in 501..899 -> (distance - 501) / 398.0
                    else -> 1.0
                }
            MembershipFunction.NEARLY -> {
                when {
                    distance >= 300 -> 1.0
                    distance in 71..200 -> (200 - distance) / 129.0
                    distance in 201..599 -> (599.0 - distance) / 398.0
                    else -> 0.0
                }
            }
            MembershipFunction.EXTREMELY_CLOSE -> {
                when {
                    distance <= 50 -> 1.0
                    distance in 51..80 -> (80.0 - distance) / 29.0
                    else -> 0.0
                }
            }
        }
}
