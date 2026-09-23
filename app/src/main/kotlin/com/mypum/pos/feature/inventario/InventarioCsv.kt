package com.mypum.pos.feature.inventario

import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.model.enumss.UnidadMedida
import java.math.BigDecimal

object InventarioCsv {

    private val headers = listOf(
        "id",
        "codigo",
        "nombre",
        "precio",
        "costo",
        "stock",
        "stockMinimo",
        "categoria",
        "esGranel",
        "unidadMedida",
        "activo"
    )

    fun exportar(productos: List<Producto>): String {
        return buildString {
            appendLine(headers.joinToString(",") { escapar(it) })

            productos.forEach { producto ->
                appendLine(
                    listOf(
                        producto.id.toString(),
                        producto.codigo.orEmpty(),
                        producto.nombre,
                        producto.precio.toPlainString(),
                        producto.costo.toPlainString(),
                        producto.stock.toPlainString(),
                        producto.stockMinimo.toPlainString(),
                        producto.categoria.orEmpty(),
                        producto.esGranel.toString(),
                        producto.unidadMedida.name,
                        producto.activo.toString()
                    ).joinToString(",") { escapar(it) }
                )
            }
        }
    }

    fun importar(csv: String): Resultado {
        val lineas = csv
            .replace("\r\n", "\n")
            .replace('\r', '\n')
            .lines()
            .filter { it.isNotBlank() }

        if (lineas.isEmpty()) {
            return Resultado.Error("El archivo CSV está vacío.")
        }

        val delimitador = detectarDelimitador(lineas.first())

        val encabezados = parsearLinea(
            lineas.first(),
            delimitador
        ).mapIndexed { index, valor ->
            if (index == 0) {
                valor.removePrefix("\uFEFF")
            } else {
                valor
            }
        }.map { it.trim() }

        if (encabezados != headers) {
            return Resultado.Error(
                "El archivo no tiene el formato de inventario MyPuM esperado."
            )
        }

        val productos = mutableListOf<Producto>()
        val errores = mutableListOf<String>()
        val codigos = mutableSetOf<String>()

        lineas.drop(1).forEachIndexed { index, linea ->
            val numeroLinea = index + 2

            try {
                val campos = parsearLinea(linea, delimitador)

                if (campos.size != headers.size) {
                    throw IllegalArgumentException(
                        "se esperaban ${headers.size} columnas y llegaron ${campos.size}"
                    )
                }

                // El ID del archivo NO se utiliza como identidad de Room.
                // Solo se conserva para compatibilidad con archivos exportados.
                val id = campos[0].trim().toLongOrNull() ?: 0L

                val codigo = campos[1]
                    .trim()
                    .ifBlank { null }

                if (codigo != null && !codigos.add(codigo)) {
                    throw IllegalArgumentException(
                        "código duplicado: $codigo"
                    )
                }

                val nombre = campos[2].trim()

                if (nombre.isBlank()) {
                    throw IllegalArgumentException("nombre vacío")
                }

                val precio = decimal(campos[3], "precio")
                val costo = decimal(campos[4], "costo")
                val stock = decimal(campos[5], "stock")
                val stockMinimo = decimal(
                    campos[6],
                    "stock mínimo"
                )

                val categoria = campos[7]
                    .trim()
                    .ifBlank { null }

                val esGranel = booleano(
                    campos[8],
                    "esGranel"
                )

                val unidad = runCatching {
                    UnidadMedida.valueOf(
                        campos[9].trim().uppercase()
                    )
                }.getOrElse {
                    throw IllegalArgumentException(
                        "unidadMedida inválida: ${campos[9]}"
                    )
                }

                val activo = booleano(
                    campos[10],
                    "activo"
                )

                productos += Producto(
                    id = id,
                    nombre = nombre,
                    codigo = codigo,
                    precio = precio,
                    costo = costo,
                    stock = stock,
                    stockMinimo = stockMinimo,
                    categoria = categoria,
                    esGranel = esGranel,
                    unidadMedida = unidad,
                    activo = activo
                )
            } catch (e: Exception) {
                errores +=
                    "Línea $numeroLinea: ${e.message ?: "dato inválido"}"
            }
        }

        if (errores.isNotEmpty()) {
            return Resultado.Error(
                "No se importó ningún producto porque el archivo contiene errores:\n\n" +
                    errores.take(10).joinToString("\n") +
                    if (errores.size > 10) {
                        "\n... y ${errores.size - 10} errores más."
                    } else {
                        ""
                    }
            )
        }

        return Resultado.Exito(productos)
    }

    private fun detectarDelimitador(encabezado: String): Char {
        var comas = 0
        var puntoYComas = 0
        var entreComillas = false

        encabezado.forEach { c ->
            when {
                c == '"' -> entreComillas = !entreComillas
                !entreComillas && c == ',' -> comas++
                !entreComillas && c == ';' -> puntoYComas++
            }
        }

        return if (puntoYComas > comas) ';' else ','
    }

    private fun decimal(
        valorOriginal: String,
        campo: String
    ): BigDecimal {
        val valor = valorOriginal
            .trim()
            .replace(" ", "")

        val normalizado = when {
            valor.contains(',') && valor.contains('.') -> {
                if (valor.lastIndexOf(',') > valor.lastIndexOf('.')) {
                    valor.replace(".", "").replace(',', '.')
                } else {
                    valor.replace(",", "")
                }
            }

            valor.contains(',') -> {
                valor.replace(',', '.')
            }

            else -> valor
        }

        return normalizado.toBigDecimalOrNull()
            ?: throw IllegalArgumentException("$campo inválido")
    }

    private fun booleano(
        valorOriginal: String,
        campo: String
    ): Boolean {
        return when (
            valorOriginal.trim().lowercase()
        ) {
            "true", "1", "si", "sí" -> true
            "false", "0", "no" -> false
            else -> throw IllegalArgumentException(
                "$campo inválido: $valorOriginal"
            )
        }
    }

    private fun escapar(valor: String): String {
        val necesitaComillas =
            valor.contains(",") ||
                valor.contains(";") ||
                valor.contains("\"") ||
                valor.contains("\n")

        val escapado = valor.replace(
            "\"",
            "\"\""
        )

        return if (necesitaComillas) {
            "\"$escapado\""
        } else {
            escapado
        }
    }

    private fun parsearLinea(
        linea: String,
        delimitador: Char
    ): List<String> {
        val resultado = mutableListOf<String>()
        val actual = StringBuilder()

        var entreComillas = false
        var i = 0

        while (i < linea.length) {
            val c = linea[i]

            when {
                c == '"' -> {
                    if (
                        entreComillas &&
                        i + 1 < linea.length &&
                        linea[i + 1] == '"'
                    ) {
                        actual.append('"')
                        i++
                    } else {
                        entreComillas = !entreComillas
                    }
                }

                c == delimitador && !entreComillas -> {
                    resultado += actual.toString()
                    actual.clear()
                }

                else -> {
                    actual.append(c)
                }
            }

            i++
        }

        resultado += actual.toString()

        if (entreComillas) {
            throw IllegalArgumentException(
                "comillas sin cerrar"
            )
        }

        return resultado
    }

    sealed interface Resultado {

        data class Exito(
            val productos: List<Producto>
        ) : Resultado

        data class Error(
            val mensaje: String
        ) : Resultado
    }
}
