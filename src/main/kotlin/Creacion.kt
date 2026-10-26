package org.example
import java.nio.file.Path
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.extension

fun main() {
    rutas()
    listar()
}

fun rutas() {
    val carpetaIni = Path.of("datos","datos_ini")
    val carpetaFin = Path.of("datos","datos_fin")

    println("Creacion de rutas de proyecto")
    println("Creando rutas...")
try {


    if (Files.notExists(carpetaIni)) {
        println("Creación de ruta para datos_ini")
        Files.createDirectories(carpetaIni)
    }

    if (Files.notExists(carpetaFin)) {
        println("Creación de ruta para datos_fin")
        Files.createDirectories(carpetaFin)
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    }

}
fun listar(){
    val carpetaBase = Path.of("datos")

    println("--- Estructura final ---")
    println("[DIR] datos")
    try {
        Files.walk(carpetaBase).use { stream ->
            stream.sorted().forEach { path ->

                val profundidad = path.nameCount - carpetaBase.nameCount
                val tabulacion = "\t".repeat(profundidad)
                val prefijo = if (Files.isDirectory(path)) "[DIR] " else "[FILE]"

                if (profundidad > 0) {
                    println("$tabulacion$prefijo ${path.fileName}")
                }
            }
        }
    } catch (e: Exception) {
        println("\n--- Ocurrió un error durante la generación del informe---")
        e.printStackTrace()
    }
}


