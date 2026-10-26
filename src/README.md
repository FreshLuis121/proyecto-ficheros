# Proyecto Final: Catálogo de Videojuegos (Gestión de Ficheros)

Aplicación desarrollada en **Kotlin** para la gestión integral de un catálogo de videojuegos, implementando operaciones CRUD y persistencia a través de múltiples formatos de ficheros (CSV, XML, JSON y Fichero Binario de acceso aleatorio).

---

##  Estructura del Proyecto

El proyecto sigue una arquitectura modular y limpia, separando la lógica de negocio y la persistencia por tipo de formato:
- `datos/datos_ini/`: Contiene los ficheros originales de intercambio (`videojuegos.csv`, `videojuegos.xml`, `videojuegos.json`).
- `datos/datos_fin/`: Directorio donde se generan los ficheros de salida procesados y el fichero binario (`videojuegos.dat`).
- `src/main/kotlin/`: Clases principales del sistema de gestión y menús.

---

##  Diseño del Fichero Binario (`videojuegos.dat`)

El fichero binario utiliza una arquitectura de **registros de longitud fija** gestionada mediante `RandomAccessFile`.

### Cálculo del Tamaño por Registro (136 bytes en total):
| Campo | Tipo de Dato | Longitud / Tamaño | Bytes Ocupados |
| :--- | :--- | :--- | :--- |
| **ID** | `Int` | 4 bytes | 4 bytes |
| **Título** | `String` (fijo) | 40 caracteres (UTF-16) | 80 bytes |
| **Género** | `String` (fijo) | 20 caracteres (UTF-16) | 40 bytes |
| **Año** | `Int` | 4 bytes | 4 bytes |
| **Nota** | `Double` | 8 bytes | 8 bytes |
| **TOTAL** | | | **136 bytes/registro** |

---

## Funcionalidades Principales

1. **Gestión de Ficheros de Intercambio (CSV, XML, JSON):**
    - Lectura y validación de estructuras estructuradas mediante librerías especializadas (Jackson para XML, Kotlinx Serialization para JSON y Kotlin-CSV).
    - Conversión cruzada entre formatos.
2. **CRUD Completo en Fichero Binario (`menuBIN`):**
    - **Importar:** Vuelca y transforma los datos desde un fichero de origen al formato binario estructurado de tamaño fijo.
    - **Leer:** Recorre secuencialmente el fichero binario mostrando todos los registros formateados por consola.
    - **Añadir:** Valida la unicidad del ID y añade un nuevo registro al final del fichero (`raf.seek(raf.length())`).
    - **Modificar:** Busca un registro por su ID y actualiza sus campos manteniendo la integridad de los bytes fijos.
    - **Eliminar:** Realiza un borrado lógico/físico reescribiendo el fichero binario completo omitiendo el registro seleccionado.

---

## Instrucciones de Ejecución

1. Abre el proyecto en **IntelliJ IDEA**.
2. Asegúrate de que las dependencias de Gradle estén sincronizadas.
3. Ejecuta el archivo principal (`MainKt`).
4. Sigue las instrucciones del menú interactivo por consola para realizar conversiones, consultas o la gestión del fichero binario.

---
## Decisiones de Diseño

1. **Estructura de Registros de Longitud Fija en Binario:**
   - Se ha optado por un tamaño estricto por registro (136 bytes) utilizando codificación UTF-16 para las cadenas de texto (`writeChars`), asegurando que cada carácter ocupe exactamente 2 bytes. Esto permite calcular saltos exactos mediante `raf.seek()` sin necesidad de metadatos adicionales de longitud variable.

2. **Modularización de la Persistencia:**
   - El código se ha desacoplado separando las operaciones según el formato del fichero (CSV, XML, JSON y Binario). Esto facilita el mantenimiento del código, la reutilización de funciones de mapeo y el cumplimiento del principio de responsabilidad única.

3. **Gestión de Identificadores Únicamente Válidos:**
   - En las operaciones de inserción y modificación en el fichero binario (`añadirRegistroBinario`), se valida de forma imperativa la no duplicidad del ID recorriendo el fichero de manera secuencial y saltando de registro en registro por bloques de bytes fijos.

4. **Tratamiento de Excepciones y Robustez:**
   - Se utilizan bloques `try-catch` y comprobaciones con la API `java.nio.file.Files` para prevenir fallos críticos ante ficheros de origen inexistentes, vacíos o malformados, garantizando una experiencia de usuario controlada en la consola.