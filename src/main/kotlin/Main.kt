package org.example

import java.nio.file.Path

// Rutas de destino para los ficheros convertidos
val destinoCSV: Path = Path.of("datos", "datos_fin", "videojuegos.csv")
val destinoXML: Path = Path.of("datos", "datos_fin", "videojuegos.xml")
val destinoJSON: Path = Path.of("datos", "datos_fin", "videojuegos.json")

fun main() {
    val catalogo = leerDatosCSV(rutaCSV)
    var opcionPrincipal: Int

    do {
        println("\n===== CATÁLOGO DE VIDEOJUEGOS =====")
        println("1. Gestión CSV")
        println("2. Leer catálogo desde XML")
        println("3. Leer catálogo desde JSON")
        println("4. Conversiones entre formatos")
        println("5. Gestión fichero BIN")
        println("0. Salir")
        println("===================================")
        print("Elige una opción: ")

        opcionPrincipal = readlnOrNull()?.toIntOrNull() ?: -1

        when (opcionPrincipal) {
            1 -> {
                // Submenú CSV
                var opcionSub: Int
                do {
                    println("\n--- Gestión CSV ---")
                    println("1. Listar todos los videojuegos")
                    println("2. Buscar videojuego por ID")
                    println("3. Añadir nuevo videojuego")
                    println("4. Modificar nota de un videojuego")
                    println("5. Eliminar videojuego")
                    println("0. Volver al menú principal")
                    print("Elige una opción: ")

                    opcionSub = readlnOrNull()?.toIntOrNull() ?: -1

                    when (opcionSub) {
                        1 -> listarVideojuegos(catalogo)
                        2 -> buscarPorId(catalogo)
                        3 -> añadirVideojuego(catalogo)
                        4 -> modificarNota(catalogo)
                        5 -> eliminarVideojuego(catalogo)
                        0 -> println("Volviendo al menú principal...")
                        else -> println("Opción no válida.")
                    }
                } while (opcionSub != 0)
            }
            2 -> {
                val listaXML = leerVideojuegosXML(rutaXML)
                if (listaXML.isNotEmpty()) {
                    val nuevosAgregados = integrarCatalogo(catalogo, listaXML)

                    escribirCSV(rutaCSV, catalogo)

                    println("¡Importación XML realizada con éxito!")
                    println("-> Se han integrado $nuevosAgregados videojuegos nuevos al catálogo.")
                    println("-> El fichero CSV ha sido actualizado.")
                } else {
                    println("No se pudo importar la información desde el XML.")
                }
            }
            3 -> {
                val listaJSON = leerVideojuegosJSON(rutaJSON)
                if (listaJSON.isNotEmpty()) {
                    val nuevosAgregados = integrarCatalogo(catalogo, listaJSON)
                    escribirCSV(rutaCSV, catalogo)
                    println("¡Importación JSON realizada con éxito!")
                    println("-> Se han integrado $nuevosAgregados videojuegos nuevos al catálogo.")
                    println("-> El fichero CSV ha sido actualizado.")
                } else {
                    println("No se pudo importar la información desde el JSON.")
                }
            }
            4 -> {
                // Submenú de conversiones
                var opcionConv: Int
                do {
                    println("\n--- Conversiones ---")
                    println("1. CSV -> JSON")
                    println("2. CSV -> XML")
                    println("3. JSON -> CSV")
                    println("4. JSON -> XML")
                    println("5. XML -> CSV")
                    println("6. XML -> JSON")
                    println("0. Volver")
                    print("Elige una opción de conversión: ")

                    opcionConv = readlnOrNull()?.toIntOrNull() ?: -1

                    when (opcionConv) {
                        1 -> convertir(rutaCSV, destinoJSON, ::leerDatosCSV, ::escribirJSON)
                        2 -> convertir(rutaCSV, destinoXML, ::leerDatosCSV, ::escribirXML)
                        3 -> convertir(rutaJSON, destinoCSV, ::leerVideojuegosJSON, ::escribirCSV)
                        4 -> convertir(rutaJSON, destinoXML, ::leerVideojuegosJSON, ::escribirXML)
                        5 -> convertir(rutaXML, destinoCSV, ::leerVideojuegosXML, ::escribirCSV)
                        6 -> convertir(rutaXML, destinoJSON, ::leerVideojuegosXML, ::escribirJSON)
                        0 -> println("Volviendo al menú principal...")
                        else -> println("Opción no válida.")
                    }
                } while (opcionConv != 0)
            }
            5 -> {
                // Submenú CRUD fichero BIN
                var opcionBin: Int
                do {
                    println("\n===== CRUD fichero BIN =====")
                    println("1. Importar información de un fichero de intercambio.")
                    println("2. Leer información del fichero binario.")
                    println("3. Añadir un registro nuevo")
                    println("4. Modificar un registro existente (por ID)")
                    println("5. Eliminar un registro existente (por ID)")
                    println("0. Volver")
                    print("Elige una opción: ")

                    opcionBin = readlnOrNull()?.toIntOrNull() ?: -1

                    when (opcionBin) {
                        1 -> importarABinario(rutaCSV, ::leerDatosCSV)
                        2 -> leerFicheroBinario()
                        3 -> añadirRegistroBinario()
                        4 -> modificarRegistroBinario()
                        5 -> eliminarRegistroBinario()
                        0 -> println("Volviendo al menú principal...")
                        else -> println("Opción no válida.")
                    }
                } while (opcionBin != 0)
            }
            0 -> println("\n¡Saliendo de la aplicación!")
            else -> println("Opción no válida.")
        }
    } while (opcionPrincipal != 0)
}