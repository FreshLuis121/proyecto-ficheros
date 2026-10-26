package org.example

// Muestra por pantalla todos los videojuegos de la lista actual
fun listarVideojuegos(lista: List<Videojuego>) {
    if (lista.isEmpty()) {
        println("\nNo hay videojuegos registrados.")
        return
    }
    println("\n--- LISTA DE VIDEOJUEGOS ---")
    for (v in lista) {
        println("ID: ${v.id} | Título: ${v.titulo} | Género: ${v.genero} | Año: ${v.anio} | Nota: ${v.nota}")
    }
}

// Busca un videojuego por su ID y lo muestra si existe
fun buscarPorId(lista: List<Videojuego>) {
    print("Introduce el ID del videojuego a buscar: ")
    val id = readlnOrNull()?.toIntOrNull() // Leemos de consola de forma segura
    if (id == null) {
        println("ID no válido.")
        return
    }

    val videojuego = lista.find { it.id == id } // Buscamos en la lista el elemento que coincida
    if (videojuego != null) {
        println("\nEncontrado -> Título: ${videojuego.titulo}, Género: ${videojuego.genero}, Nota: ${videojuego.nota}")
    } else {
        println("\nNo existe ningún videojuego con el ID $id.")
    }
}

// Pide los datos de un nuevo juego, calcula el ID automático y lo guarda
fun añadirVideojuego(lista: MutableList<Videojuego>) {
    println("\n--- AÑADIR NUEVO VIDEOJUEGO ---")

    print("Título: ")
    val titulo = readlnOrNull()?.trim() ?: ""
    if (titulo.isEmpty()) {
        println("El título no puede estar vacío.")
        return
    }

    print("Género: ")
    val genero = readlnOrNull()?.trim() ?: ""

    print("Año: ")
    val anio = readlnOrNull()?.toIntOrNull()
    if (anio == null) {
        println("Año inválido.")
        return
    }

    print("Nota (0.0 - 10.0): ")
    val nota = readlnOrNull()?.toDoubleOrNull()
    if (nota == null || nota !in 0.0..10.0) { // Confirmamos que esté dentro del rango correcto
        println("Nota fuera de rango o inválida.")
        return
    }

    // Generamos un ID nuevo automático: buscamos el ID máximo actual y le sumamos 1 (si está vacía, empieza en 1)
    val nuevoId = if (lista.isNotEmpty()) lista.maxOf { it.id } + 1 else 1

    val nuevoVideojuego = Videojuego(nuevoId, titulo, genero, anio, nota)
    lista.add(nuevoVideojuego)

    escribirCSV(rutaCSV, lista)
    println("¡Videojuego añadido y guardado con éxito (ID: $nuevoId)!")
}

// Modifica la nota de un videojuego existente buscando por su ID
fun modificarNota(lista: MutableList<Videojuego>) {
    print("Introduce el ID del videojuego a modificar: ")
    val id = readlnOrNull()?.toIntOrNull()
    val videojuego = lista.find { it.id == id }

    if (videojuego == null) {
        println("No se encontró ese ID.")
        return
    }

    print("Introduce la nueva nota (0.0 - 10.0): ")
    val nuevaNota = readlnOrNull()?.toDoubleOrNull()
    if (nuevaNota == null || nuevaNota !in 0.0..10.0) {
        println("Nota inválida.")
        return
    }

    // Reemplazamos el elemento antiguo por una copia con la nueva nota
    val index = lista.indexOf(videojuego)
    lista[index] = videojuego.copy(nota = nuevaNota)

    escribirCSV(rutaCSV, lista) // Aplicamos los cambios
    println("¡Nota actualizada con éxito!")
}

// Elimina un videojuego pidiendo confirmación al usuario
fun eliminarVideojuego(lista: MutableList<Videojuego>) {
    print("Introduce el ID del videojuego a eliminar: ")
    val id = readlnOrNull()?.toIntOrNull()
    val videojuego = lista.find { it.id == id }

    if (videojuego == null) {
        println("No se encontró ese ID.")
        return
    }

    print("¿Estás seguro de eliminar '${videojuego.titulo}'? (s/n): ")
    val confirmacion = readlnOrNull()?.trim()?.lowercase()

    if (confirmacion == "s") {
        lista.remove(videojuego)
        escribirCSV(rutaCSV, lista)
        println("¡Videojuego eliminado correctamente!")
    } else {
        println("Operación cancelada.")
    }
}