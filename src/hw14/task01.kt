import kotlin.random.Random

class QuadraticEquation(var a: Double, var b: Double, var c: Double) {

    init {
        require(a != 0.0) { "Коефіцієнт 'a' не може бути нулем!" }
    }

    // Вторинні конструктори
    constructor(a: Int, b: Int, c: Int) : this(a.toDouble(), b.toDouble(), c.toDouble())
    constructor(b: Double, c: Double) : this(1.0, b, c)

    // Кастомний сетер для a
    var coefficientA: Double = a
        set(value) {
            require(value != 0.0) { "Коефіцієнт 'a' не може бути нулем!" }
            field = value
        }

    // Обчислювана властивість (кожного разу розраховується заново)
    val discriminant: Double
        get() = b * b - 4 * a * c

    override fun toString(): String {
        return "${a}x^2 + (${b})x + ${c} = 0"
    }

    fun solve(): List<Double> {
        val d = discriminant
        return when {
            d > 0.0 -> {
                val x1 = (-b + Math.sqrt(d)) / (2 * a)
                val x2 = (-b - Math.sqrt(d)) / (2 * a)
                listOf(x1, x2)
            }
            d == 0.0 -> {
                val x = -b / (2 * a)
                listOf(x)
            }
            else -> emptyList()
        }
    }
}

fun main() {
    // Генерація 100 рівнянь
    val equations = mutableListOf<QuadraticEquation>()
    for (i in 1..100) {
        var randomA = Random.nextInt(-50, 51).toDouble()
        while (randomA == 0.0) { // перевірка, щоб 'a' не дорівнювало 0
            randomA = Random.nextInt(-50, 51).toDouble()
        }
        val randomB = Random.nextInt(-50, 51).toDouble()
        val randomC = Random.nextInt(-50, 51).toDouble()

        equations.add(QuadraticEquation(randomA, randomB, randomC))
    }

    // Фільтрація тих, які мають 2 корені
    val twoRootsEquations = equations.filter { it.solve().size == 2 }

    println(" Рівняння з 2 коренями \n")
    twoRootsEquations.forEach { eq ->
        println("Рівняння: $eq | Корені: ${eq.solve()}")
    }

    println("\nЗагальна кількість рівнянь з 2 коренями: ${twoRootsEquations.size}")
}