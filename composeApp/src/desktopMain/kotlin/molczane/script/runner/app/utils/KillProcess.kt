package molczane.script.runner.app.utils

fun killProcess(process: Process) {
    try {
        if (process.isAlive) {
            process.children().forEach { child ->
                if(child.isAlive) {
                    child.destroyForcibly()
                }
            }
            process.destroy() // Próba łagodnego zakończenia procesu
            if (!process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
                // Jeśli proces nie zakończy się w ciągu 5 sekund, wymuś zakończenie
                process.destroyForcibly()
                println("Proces został wymuszony do zakończenia.")
            } else {
                println("Proces został zakończony łagodnie.")
            }
        } else {
            println("Proces już nie jest aktywny.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        println("Wystąpił błąd podczas próby zakończenia procesu: ${e.message}")
    }
}

