object Speed {
    enum class MembershipFunction {
        FAST, // Быстрая скорость
        MEDIUM, // Средняя скорость
        SLOW, // Медленная скорость
    }

    // Функция принадлежности для скорости (м/с)
    fun calculate(x: Int, membership: MembershipFunction): Double {
        when (membership) {
            MembershipFunction.FAST -> {
                if (x <= 15.0) {
                    return 0.2
                }

                if (x < 30.0) {
                    return (30.0 - x) / (30.0 - 15.0)
                }

                return 1.0
            }
            MembershipFunction.MEDIUM -> {
                if (x <= 10.0) {
                    return 1.0
                }

                if (x < 15.0) {
                    return (15.0 - x) / (15.0 - 10.0)
                }

                return 0.1
            }
            MembershipFunction.SLOW -> {
                if (x <= 5.0) {
                    return 1.0
                }

                if (x < 10.0) {
                    return (10.0 - x) / (10.0 - 5.0)
                }

                return 0.1
            }
        }
    }
}
