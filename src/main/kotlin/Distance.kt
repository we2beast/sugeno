object Distance {
    enum class MembershipFunction {
        FARAWAY, // Далеко
        NEARLY, // Близко
        EXTREMELY_CLOSE, // Крайне близко
    }

    // Функция принадлежности для расстояния (метры)
    fun calculate(x: Int, membership: MembershipFunction): Double {
        when (membership) {
            MembershipFunction.FARAWAY -> {
                if (x <= 200.0) {
                    return 1.0
                }

                if (x < 1000.0) {
                    return (1000.0 - x) / (1000.0 - 200.0)
                }

                return 0.0
            }
            MembershipFunction.NEARLY -> {
                if (x <= 80.0) {
                    return 1.0
                }

                if (x < 140.0) {
                    return (140.0 - x) / (140.0 - 80.0)
                }

                return 0.01
            }
            MembershipFunction.EXTREMELY_CLOSE -> {
                if (x <= 30.0) {
                    return 1.0
                }

                if (x < 80.0) {
                    return (80.0 - x) / (80.0 - 30.0)
                }

                return 0.01
            }
        }
    }

}
