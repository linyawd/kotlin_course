data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val inStock: Boolean
)

val products = listOf(
    Product(1, "Google Pixel 9", "Smartphones", 899.0, 4.8, true),
    Product(2, "iPhone 16 Pro", "Smartphones", 1199.0, 4.9, true),
    Product(3, "Galaxy A55", "Smartphones", 399.0, 4.3, false),
    Product(4, "MacBook Air M3", "Laptops", 1299.0, 4.9, true),
    Product(5, "ThinkPad X1 Carbon", "Laptops", 1499.0, 4.6, false),
    Product(6, "Dell XPS 13", "Laptops", 1150.0, 4.4, true),
    Product(7, "Sony WH-1000XM5", "Audio", 349.0, 4.7, true),
    Product(8, "AirPods Pro 2", "Audio", 249.0, 4.8, false),
    Product(9, "Pixel Buds Pro 2", "Audio", 229.0, 4.5, true)
)

// Функція-розширення для Частини 4
fun List<Product>.filterAndTransform(
    predicate: (Product) -> Boolean,
    transform: (Product) -> String
): List<String> {
    val result = mutableListOf<String>()
    for (item in this) {
        if (predicate(item)) {
            result.add(transform(item))
        }
    }
    return result
}

fun main() {
    println("Частина 1")
    products
        .filter { it.inStock && it.rating >= 4.7 && it.price < 1000.0 }
        .sortedByDescending { it.rating }
        .map { "Назва: ${it.name} | Рейтинг: ${it.rating} | Ціна: \$${it.price}" }
        .forEach { println(it) }

    println("\nЧастина 2")

    // 1. Пошук першого ноутбука дорожчого за 1200.0
    val expensiveLaptop = products.find { it.category == "Laptops" && it.price > 1200.0 }
    println("Перший ноутбук > $1200: ${expensiveLaptop?.name ?: "Товар не знайдено"}")

    // 2. Чи є аудіо дорожче за 300.0
    val hasExpensiveAudio = products.any { it.category == "Audio" && it.price > 300.0 }
    println("Є аудіо дорожче за $300: $hasExpensiveAudio")

    // 3. Чи всі смартфони мають рейтинг > 4.0
    val allSmartphonesGood = products.filter { it.category == "Smartphones" }.all { it.rating > 4.0 }
    println("Усі смартфони мають рейтинг > 4.0: $allSmartphonesGood")

    println("\nЧастина 3")

    // 1. Розділення за наявністю
    val (available, outOfStock) = products.partition { it.inStock }
    println("В наявності: ${available.size} товарів")
    println("Немає в наявності: ${outOfStock.size} товарів")

    // 2 & 3. Групування за категорією та пошук найдорожчого
    val groupedByCategory = products.groupBy { it.category }

    groupedByCategory.forEach { (category, productList) ->
        val maxProduct = productList.maxByOrNull { it.price }
        if (maxProduct != null) {
            println("Категорія $category -> Найдорожчий: ${maxProduct.name} (\$${maxProduct.price})")
        }
    }

    println("\nЧастина 4")

    // Виклик із синтаксисом Trailing Lambda
    val promoMessages = products.filterAndTransform({ it.price < 300.0 }) { product ->
        "Акційна ціна на ${product.name}: лише \$${product.price}!"
    }

    promoMessages.forEach { println(it) }
}