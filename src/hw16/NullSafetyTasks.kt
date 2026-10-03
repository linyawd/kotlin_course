data class Address(
    val street: String,
    val city: String?,
    val postalCode: String?
)

data class Client(
    val id: Int,
    val name: String,
    val email: String?,
    val address: Address?,
    val extraData: Any?
)

val clientList = listOf(
    Client(1, "Олена", "olena@example.com", Address("вул. Саксаганського, 10", "Київ", "01033"), "VIP-клієнт"),
    Client(2, "Богдан", null, Address("вул. Городоцька, 45", "Львів", null), 42),
    Client(3, "Марія", "maria@example.com", null, null),
    Client(4, "Дмитро", null, Address("вул. Соборна, 1", null, null), "Очікує дзвінка")
)

fun getShippingLabel(client: Client): String {
    val address = client.address ?: return "Самовивіз: Клієнт ${client.name} не надав адреси"
    val city = address.city ?: "Місто не вказано"
    val postalCode = address.postalCode ?: "Індекс невідомий"

    return "${address.street}, $city, $postalCode"
}

fun printClientNote(client: Client) {
    val note = client.extraData as? String ?: "Додаткові примітки відсутні"
    println("Клієнт ${client.name} -> Примітка: $note")
}

fun getClientEmailOrThrow(client: Client): String {
    return client.email ?: throw IllegalArgumentException("Клієнт з ID ${client.id} не має електронної пошти!")
}

fun forceGetPostalCode(client: Client): String {
    // Оператор !! використовують, коли ми на 100% впевнені, що значення не null (наприклад, після зовнішньої валідації чи перевірки фреймворком), хоча компилятор цього не знає.
    return client.address!!.postalCode!!
}

fun main() {
    println("--- Завдання 1 ---")
    println("Адреса Олена: ${getShippingLabel(clientList[0])}")
    println("Адреса Богдан: ${getShippingLabel(clientList[1])}")
    println("Адреса Марія: ${getShippingLabel(clientList[2])}")

    println("\n--- Завдання 2 ---")
    clientList.forEach { printClientNote(it) }

    println("\n--- Завдання 3 ---")
    val validEmails: List<String> = clientList.map { it.email }.filterNotNull()
    val shortestEmailLength = validEmails.minByOrNull { it.length }?.length ?: 0
    println("База email для розсилки: $validEmails")
    println("Довжина найкоротшого email: $shortestEmailLength")

    println("\n--- Завдання 4 ---")
    try {
        getClientEmailOrThrow(clientList[1])
    } catch (e: IllegalArgumentException) {
        println("Перехоплено виняток: ${e.message}")
    }

    println("\n--- Завдання 5 ---")
    println("Індекс клієнта Олена: ${forceGetPostalCode(clientList[0])}")
    try {
        forceGetPostalCode(clientList[1])
    } catch (e: NullPointerException) {
        println("Перехоплено очікуваний NPE: ${e.message}")
    }
}