import kotlin.random.Random

fun generateRandomArray_task(size: Int, maxValue: Int): IntArray? {
    if (size <= 0 || maxValue <= 0) return null
    return IntArray(size) { Random.nextInt(0, maxValue + 1) }
}

fun main() {
    generateRandomArray_task(size = 10, maxValue = 50)
        ?.apply {
            for (i in indices) {
                if (this[i] % 2 != 0) {
                    this[i] *= 2
                } else {
                    this[i] /= 2
                }
            }
        }
        ?.also {
            println("Модифікований масив: ${it.contentToString()}")
        }
        ?.maxOrNull()
        ?.let {
            println("Максимальне значення масиву: $it")
        } ?: println("Помилка вхідних даних")
}