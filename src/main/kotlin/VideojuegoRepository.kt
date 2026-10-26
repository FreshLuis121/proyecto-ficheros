package org.example

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.serialization.encodeToString
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.json.Json
import java.io.RandomAccessFile

// Rutas de los ficheros
val rutaCSV: Path = Path.of("datos", "datos_ini", "videojuegos.csv")
val rutaXML: Path = Path.of("datos", "datos_ini", "videojuegos.xml")
val rutaJSON: Path = Path.of("datos", "datos_ini", "videojuegos.json")
val rutaBinario: Path = Path.of("datos", "datos_fin", "videojuegos.dat")

// Constantes para Fichero Binario
const val TITULO_LEN = 40
const val GENERO_LEN = 20
const val RECORD_SIZE = 4 + (TITULO_LEN * 2) + (GENERO_LEN * 2) + 4 + 8

// Función para leer el CSV y convertirlo en la lista de objetos Videojuego
fun leerDatosCSV(ruta: Path): MutableList<Videojuego> {
    val videojuegos = mutableListOf<Videojuego>()

    if (!Files.exists(ruta) || !Files.isReadable(ruta)) {
        println("Aviso: El fichero no existe o no se puede leer.")
        return videojuegos
    }

    val reader = csvReader {
        delimiter = ';'
    }

    val filas = reader.readAll(ruta.toFile())

    val leidos = filas.mapNotNull { columnas ->
        if (columnas.size >= 5) {
            try {
                val id = columnas[0].trim().toInt()
                val titulo = columnas[1].trim()
                val genero = columnas[2].trim()
                val anio = columnas[3].trim().toInt()
                val nota = columnas[4].trim().toDouble()
                Videojuego(id, titulo, genero, anio, nota)
            } catch (e: Exception) {
                println("Fila inválida ignorada: $columnas")
                null
            }
        } else {
            null
        }
    }
    videojuegos.addAll(leidos)
    return videojuegos
}

// Función para guardar de nuevo toda la lista en el fichero CSV
fun escribirCSV(ruta: Path, videojuegos: List<Videojuego>) {
    try {
        if (Files.notExists(ruta.parent)) {
            Files.createDirectories(ruta.parent)
        }
        val fichero: File = ruta.toFile()

        csvWriter {
            delimiter = ';'
        }.writeAll(
            videojuegos.map { v ->
                listOf(
                    v.id.toString(),
                    v.titulo,
                    v.genero,
                    v.anio.toString(),
                    v.nota.toString()
                )
            },
            fichero
        )
    } catch (e: Exception) {
        println("Error al escribir el fichero CSV: ${e.message}")
    }
}

// Clase que representa la etiqueta raíz del XML
@JacksonXmlRootElement(localName = "videojuegos")
data class VideojuegosXML(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "videojuego")
    val listaVideojuegos: List<Videojuego> = emptyList()
)

// Función para leer y deserializar el fichero XML utilizando Jackson
fun leerVideojuegosXML(ruta: Path): List<Videojuego> {
    var lista: List<Videojuego> = emptyList()
    if (!Files.isReadable(ruta)) {
        println("Error: No se puede leer el fichero XML en la ruta: $ruta")
    } else {
        try {
            val fichero = ruta.toFile()
            val xmlMapper = XmlMapper().registerKotlinModule()

            val contenedor: VideojuegosXML = xmlMapper.readValue(fichero)
            lista = contenedor.listaVideojuegos
            println("--- Información leída con éxito de: $ruta ---")
        } catch (e: Exception) {
            println("Error al procesar el XML: ${e.message}")
        }
    }
    return lista
}

// Función para fusionar listas importadas en el catálogo
fun integrarCatalogo(catalogoActual: MutableList<Videojuego>, nuevos: List<Videojuego>): Int {
    var contadorNuevos = 0
    for (videojuegoNuevo in nuevos) {
        val existe = catalogoActual.any { it.id == videojuegoNuevo.id }
        if (!existe) {
            catalogoActual.add(videojuegoNuevo)
            contadorNuevos++
        }
    }
    return contadorNuevos
}

// Función para leer y deserializar el fichero JSON
fun leerVideojuegosJSON(ruta: Path): List<Videojuego> {
    var lista: List<Videojuego> = emptyList()
    if (!Files.isReadable(ruta)) {
        println("Error: No se puede leer el fichero JSON en la ruta: $ruta")
    } else {
        try {
            val jsonString = Files.readString(ruta)
            lista = Json.decodeFromString<List<Videojuego>>(jsonString)
            println("--- Información leída con éxito de: $ruta ---")
        } catch (e: Exception) {
            println("Error al procesar el JSON: ${e.message}")
        }
    }
    return lista
}

// Función para escribir y guardar datos en un fichero XML
fun escribirXML(ruta: Path, videojuegos: List<Videojuego>) {
    try {
        if (Files.notExists(ruta.parent)) {
            Files.createDirectories(ruta.parent)
        }
        val xmlMapper = XmlMapper().registerKotlinModule()
        val contenedor = VideojuegosXML(videojuegos)
        val xmlString = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contenedor)
        Files.writeString(ruta, xmlString)
        println("--- Información guardada con éxito en XML: $ruta ---")
    } catch (e: Exception) {
        println("Error al escribir el fichero XML: ${e.message}")
    }
}

// Función para escribir y guardar datos en un fichero JSON
fun escribirJSON(ruta: Path, videojuegos: List<Videojuego>) {
    try {
        if (Files.notExists(ruta.parent)) {
            Files.createDirectories(ruta.parent)
        }
        val jsonConfigurador = Json { prettyPrint = true }
        val jsonString = jsonConfigurador.encodeToString(videojuegos)
        Files.writeString(ruta, jsonString)
        println("--- Información guardada con éxito en JSON: $ruta ---")
    } catch (e: Exception) {
        println("Error al escribir el fichero JSON: ${e.message}")
    }
}

// Función de conversión exigida
fun convertir(
    origen: Path,
    destino: Path,
    leer: (Path) -> List<Videojuego>,
    escribir: (Path, List<Videojuego>) -> Unit
) {
    if (!Files.exists(origen) || !Files.isReadable(origen)) {
        println("Error: El fichero de origen no existe o no se puede leer: $origen")
        return
    }

    try {
        val datos = leer(origen)

        if (datos.isNotEmpty()) {
            escribir(destino, datos)
            println("¡Conversión realizada con éxito!")
            println("-> Fichero generado: ${destino.toAbsolutePath()}")
            println("-> Número de registros convertidos: ${datos.size}")
        } else {
            println("El fichero origen está vacío o no contiene registros válidos.")
        }
    } catch (e: Exception) {
        println("Ocurrió un error durante la conversión: ${e.message}")
    }
}

// Función para escribir un texto con un tamaño fijo en bytes
fun writeFixedString(raf: RandomAccessFile, str: String, length: Int) {
    val sb = StringBuilder(str)
    if (sb.length > length) {
        sb.setLength(length)
    } else {
        while (sb.length < length) {
            sb.append(' ')
        }
    }
    raf.writeChars(sb.toString())
}

// Función para leer un texto de tamaño fijo y quitarle los espacios que sobren
fun readFixedString(raf: RandomAccessFile, length: Int): String {
    val chars = CharArray(length)
    for (i in 0 until length) {
        chars[i] = raf.readChar()
    }
    return String(chars).trim()
}
//Borra el binario anterior y guarda los datos del CSV y intercambio aquí
fun importarABinario(origen: Path, origenLectura: (Path) -> List<Videojuego>) {
    try {
        if (Files.notExists(rutaBinario.parent)) {
            Files.createDirectories(rutaBinario.parent)
        }
        val lista = origenLectura(origen)
        if (lista.isEmpty()) {
            println("El fichero origen está vacío. No se pudo importar.")
            return
        }
// Abrimos el fichero binario en modo lectura y escritura
        RandomAccessFile(rutaBinario.toFile(), "rw").use { raf ->
            raf.setLength(0)
            for (v in lista) {
                raf.writeInt(v.id)
                writeFixedString(raf, v.titulo, TITULO_LEN)
                writeFixedString(raf, v.genero, GENERO_LEN)
                raf.writeInt(v.anio)
                raf.writeDouble(v.nota)
            }
        }
        println("¡Importación a binario realizada con éxito!")
        println("-> Fichero generado: ${rutaBinario.toAbsolutePath()}")
        println("-> Registros importados: ${lista.size}")
    } catch (e: Exception) {
        println("Error al importar al fichero binario: ${e.message}")
    }
}
//Lee el fichero binario entero y lo muestra por la consola
fun leerFicheroBinario() {
    if (!Files.exists(rutaBinario)) {
        println("El fichero binario aún no existe. Importa datos primero.")
        return
    }

    try {
        RandomAccessFile(rutaBinario.toFile(), "r").use { raf ->
            println("\n===== CONTENIDO DEL FICHERO BINARIO =====")
            while (raf.filePointer < raf.length()) {
                val id = raf.readInt()
                val titulo = readFixedString(raf, TITULO_LEN)
                val genero = readFixedString(raf, GENERO_LEN)
                val anio = raf.readInt()
                val nota = raf.readDouble()
                println("ID: $id | Título: $titulo | Género: $genero | Año: $anio | Nota: $nota")
            }
            println("=========================================")
        }
    } catch (e: Exception) {
        println("Error al leer el fichero binario: ${e.message}")
    }
}
// Función para comprobar si un ID ya está guardado en el binario
fun existeIdEnBinario(idBuscado: Int): Boolean {
    if (!Files.exists(rutaBinario)) return false
    RandomAccessFile(rutaBinario.toFile(), "r").use { raf ->
        while (raf.filePointer < raf.length()) {
            val id = raf.readInt()
            if (id == idBuscado) return true
            raf.seek(raf.filePointer + RECORD_SIZE - 4)
        }
    }
    return false
}
//Pide un ID nuevo y los datos, y los añade al final del fichero binario
fun añadirRegistroBinario() {
    var id: Int
    while (true) {
        print("Introduce el ID (número entero que no exista): ")
        val input = readlnOrNull()?.toIntOrNull()
        if (input != null) {
            if (existeIdEnBinario(input)) {
                println("Error: Ya existe un videojuego con ese ID en el fichero binario.")
            } else {
                id = input
                break
            }
        } else {
            println("Por favor, introduce un número entero válido.")
        }
    }

    print("Introduce el título: ")
    val titulo = readlnOrNull()?.takeIf { it.isNotBlank() } ?: "Desconocido"

    print("Introduce el género: ")
    val genero = readlnOrNull()?.takeIf { it.isNotBlank() } ?: "Desconocido"

    var anio: Int
    while (true) {
        print("Introduce el año (ej. 2023): ")
        val input = readlnOrNull()?.toIntOrNull()
        if (input != null) {
            anio = input
            break
        }
        println("Año inválido. Introduce un número entero.")
    }

    var nota: Double
    while (true) {
        print("Introduce la nota (ej. 9.5): ")
        val input = readlnOrNull()?.toDoubleOrNull()
        if (input != null) {
            nota = input
            break
        }
        println("Nota inválida. Introduce un número decimal.")
    }

    try {
        RandomAccessFile(rutaBinario.toFile(), "rw").use { raf ->
            raf.seek(raf.length())
            raf.writeInt(id)
            writeFixedString(raf, titulo, TITULO_LEN)
            writeFixedString(raf, genero, GENERO_LEN)
            raf.writeInt(anio)
            raf.writeDouble(nota)
        }
        println("¡Registro añadido con éxito al final del fichero binario!")
    } catch (e: Exception) {
        println("Error al añadir el registro: ${e.message}")
    }
}
//Busca un videojuego por su ID y actualiza sus datos
fun modificarRegistroBinario() {
    if (!Files.exists(rutaBinario)) {
        println("El fichero binario está vacío o no existe.")
        return
    }

    var idBuscado: Int
    while (true) {
        print("Introduce el ID del videojuego a modificar: ")
        val input = readlnOrNull()?.toIntOrNull()
        if (input != null) {
            idBuscado = input
            break
        }
        println("Introduce un número entero válido.")
    }

    try {
        RandomAccessFile(rutaBinario.toFile(), "rw").use { raf ->
            var encontrado = false
            while (raf.filePointer < raf.length()) {
                val posicionRegistro = raf.filePointer
                val id = raf.readInt()
                val tituloActual = readFixedString(raf, TITULO_LEN)
                val generoActual = readFixedString(raf, GENERO_LEN)
                val anioActual = raf.readInt()
                val notaActual = raf.readDouble()
// Si coincide el ID, modificamos
                if (id == idBuscado) {
                    encontrado = true
                    println("¡Encontrado! Videojuego: '$tituloActual' (Género: $generoActual, Año: $anioActual, Nota: $notaActual)")

                    print("Introduce el nuevo título (o pulsa enter para dejarlo igual): ")
                    val nuevoTituloInput = readlnOrNull()
                    val tituloFinal = if (nuevoTituloInput.isNullOrBlank()) tituloActual else nuevoTituloInput

                    print("Introduce la nueva nota (o pulsa enter para dejarla igual): ")
                    val nuevaNotaInput = readlnOrNull()?.toDoubleOrNull()
                    val notaFinal = nuevaNotaInput ?: notaActual
// Volvemos al inicio exacto del registro para sobrescribirlo con los nuevos datos
                    raf.seek(posicionRegistro)
                    raf.writeInt(id)
                    writeFixedString(raf, tituloFinal, TITULO_LEN)
                    writeFixedString(raf, generoActual, GENERO_LEN)
                    raf.writeInt(anioActual)
                    raf.writeDouble(notaFinal)

                    println("¡Registro modificado con éxito!")
                    break
                }
            }
            if (!encontrado) {
                println("No se ha encontrado ningún videojuego con el ID $idBuscado.")
            }
        }
    } catch (e: Exception) {
        println("Error al modificar el registro: ${e.message}")
    }
}
//Busca un ID, pide confirmación y lo borra recreando el fichero sin él
fun eliminarRegistroBinario() {
    if (!Files.exists(rutaBinario)) {
        println("El fichero binario está vacío o no existe.")
        return
    }

    var idBuscado: Int
    while (true) {
        print("Introduce el ID del videojuego a eliminar: ")
        val input = readlnOrNull()?.toIntOrNull()
        if (input != null) {
            idBuscado = input
            break
        }
        println("Introduce un número entero válido.")
    }

    try {
        var encontrado = false
//Comprobamos si existe para avisar al usuario
        RandomAccessFile(rutaBinario.toFile(), "r").use { raf ->
            while (raf.filePointer < raf.length()) {
                val id = raf.readInt()
                val titulo = readFixedString(raf, TITULO_LEN)
                val genero = readFixedString(raf, GENERO_LEN)
                val anio = raf.readInt()
                val nota = raf.readDouble()

                if (id == idBuscado) {
                    encontrado = true
                    println("¡Encontrado! Título: '$titulo' (ID: $id)")
                }
            }
        }

        if (!encontrado) {
            println("No se ha encontrado ningún videojuego con el ID $idBuscado.")
            return
        }

        print("¿Estás seguro de que deseas eliminar este registro? (s/n): ")
        val confirmacion = readlnOrNull()?.trim()?.lowercase()
        if (confirmacion == "s" || confirmacion == "si") {
            //Para borrar en un fichero binario de tamaño fijo, leemos todo y reescribimos omitiendo el ID borrado
            RandomAccessFile(rutaBinario.toFile(), "rw").use { rafR ->
                RandomAccessFile(rutaBinario.toFile(), "r").use { rafL ->
                    rafR.setLength(0)
                    while (rafL.filePointer < rafL.length()) {
                        val id = rafL.readInt()
                        val titulo = readFixedString(rafL, TITULO_LEN)
                        val genero = readFixedString(rafL, GENERO_LEN)
                        val anio = rafL.readInt()
                        val nota = rafL.readDouble()
                        //Guardamos todos menos el que queremos eliminar
                        if (id != idBuscado) {
                            rafR.writeInt(id)
                            writeFixedString(rafR, titulo, TITULO_LEN)
                            writeFixedString(rafR, genero, GENERO_LEN)
                            rafR.writeInt(anio)
                            rafR.writeDouble(nota)
                        }
                    }
                }
            }
            println("¡Registro eliminado con éxito del fichero binario!")
        } else {
            println("Operación de borrado cancelada.")
        }
    } catch (e: Exception) {
        println("Error al eliminar el registro: ${e.message}")
    }
}