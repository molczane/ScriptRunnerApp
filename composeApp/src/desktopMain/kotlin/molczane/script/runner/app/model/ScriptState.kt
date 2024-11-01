package molczane.script.runner.app.model

data class ScriptState(
    var scriptText: String = "fun isPrime(num: Int): Boolean {\n" +
            "    if (num < 2) return false\n" +
            "    for (i in 2..num / 2) {\n" +
            "        if (num % i == 0) return false\n" +
            "    }\n" +
            "    return true\n" +
            "}\n" +
            "\n" +
            "var number = 2\n" +
            "\n" +
            "println(\"Starting to calculate prime numbers...\")\n" +
            "while (true) {\n" +
            "    if (isPrime(number)) {\n" +
            "        println(\"Prime found: \$number\")\n" +
            "    }\n" +
            "    number++\n" +
            "\n" +
            "    // Simulate a delay to make the process run longer\n" +
            "    Thread.sleep(1000) // 1 second delay\n" +
            "}"
)
