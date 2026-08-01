package com.example.data.model

data class RateChartConfig(
    val baseCowFat: Double = 3.5,
    val baseCowSnf: Double = 8.5,
    val baseCowPrice: Double = 38.0,

    val baseBuffaloFat: Double = 6.0,
    val baseBuffaloSnf: Double = 9.0,
    val baseBuffaloPrice: Double = 55.0,

    val fatStepFactor: Double = 0.50, // Per 0.1 FAT difference
    val snfStepFactor: Double = 0.40  // Per 0.1 SNF difference
) {
    fun calculateRate(milkType: String, fat: Double, snf: Double, customerFixedRate: Double = 0.0): Double {
        if (customerFixedRate > 0.0) return customerFixedRate

        val (baseFat, baseSnf, basePrice) = when (milkType) {
            "Buffalo" -> Triple(baseBuffaloFat, baseBuffaloSnf, baseBuffaloPrice)
            else -> Triple(baseCowFat, baseCowSnf, baseCowPrice)
        }

        val fatDiff = (fat - baseFat) * 10.0
        val snfDiff = (snf - baseSnf) * 10.0

        val calculatedRate = basePrice + (fatDiff * fatStepFactor) + (snfDiff * snfStepFactor)
        return kotlin.math.max(20.0, (calculatedRate * 100.0).roundToTwoDecimals() / 100.0)
    }

    private fun Double.roundToTwoDecimals(): Double {
        return kotlin.math.round(this * 100.0)
    }
}
