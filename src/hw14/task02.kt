class Employee(
    val firstName: String,
    val lastName: String,
    var position: String
) {
    var bonus: Int = 0

    override fun toString(): String {
        return "Employee(firstName='$firstName', lastName='$lastName', position='$position')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Employee) return false

        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + position.hashCode()
        return result
    }

    operator fun component1(): String = firstName
    operator fun component2(): String = lastName
    operator fun component3(): String = position

    fun copy(
        firstName: String = this.firstName,
        lastName: String = this.lastName,
        position: String = this.position
    ): Employee {
        val newEmployee = Employee(firstName, lastName, position)
        newEmployee.bonus = this.bonus
        return newEmployee
    }
}

fun main() {
    val emp1 = Employee("Іван", "Іванов", "Developer")
    val emp2 = Employee("Іван", "Іванов", "Developer")
    val emp3 = emp1

    println("emp1: $emp1")

    // Посилання на різні об'єкти в пам'яті
    println("emp1 === emp2: ${emp1 === emp2}") // false
    // Рівність за значеннями полів через перевизначений equals()
    println("emp1 == emp2: ${emp1 == emp2}")   // true
    // Посилання на один і той самий об'єкт
    println("emp1 === emp3: ${emp1 === emp3}") // true

    val (name, surname, pos) = emp1
    println("Розпаковані дані: $name $surname, посада: $pos")

    val empPromoted = emp1.copy(position = "Senior Developer")
    println("Новий працівник: $empPromoted")

    // Поле поза конструктором не впливає на equals()
    val empA = Employee("Петро", "Петренко", "QA")
    empA.bonus = 100

    val empB = Employee("Петро", "Петренко", "QA")
    empB.bonus = 500

    println("empA == empB (з різним бонусом): ${empA == empB}") // true
}