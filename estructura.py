# ============================================================
# ANALIZADOR DE PROYECTOS PARA PYDROID 3
# ============================================================
# Coloca este archivo dentro de la carpeta que quieres analizar
# y ejecútalo desde Pydroid 3.
#
# No requiere instalar ninguna librería externa.
# ============================================================

import os
import sys
from pathlib import Path
from datetime import datetime

# ------------------------------------------------------------
# CONFIGURACIÓN
# ------------------------------------------------------------

# Extensiones -> lenguaje
LENGUAJES = {
    ".py": "Python",
    ".pyw": "Python",
    ".js": "JavaScript",
    ".jsx": "JavaScript / JSX",
    ".ts": "TypeScript",
    ".tsx": "TypeScript / JSX",
    ".java": "Java",
    ".kt": "Kotlin",
    ".kts": "Kotlin",
    ".c": "C",
    ".h": "C / Header",
    ".cpp": "C++",
    ".cc": "C++",
    ".hpp": "C++ / Header",
    ".cs": "C#",
    ".go": "Go",
    ".rs": "Rust",
    ".swift": "Swift",
    ".php": "PHP",
    ".rb": "Ruby",
    ".lua": "Lua",
    ".dart": "Dart",
    ".sql": "SQL",
    ".sh": "Shell",
    ".bash": "Bash",
    ".zsh": "Zsh",
    ".html": "HTML",
    ".htm": "HTML",
    ".css": "CSS",
    ".scss": "SCSS",
    ".xml": "XML",
    ".json": "JSON",
    ".yaml": "YAML",
    ".yml": "YAML",
    ".toml": "TOML",
    ".md": "Markdown",
}

# Archivos/carpetas que normalmente conviene ignorar.
IGNORAR = {
    ".git",
    ".idea",
    ".gradle",
    "__pycache__",
    ".pytest_cache",
    "node_modules",
    ".venv",
    "venv",
    "build",
    "dist",
}


# ------------------------------------------------------------
# FUNCIONES
# ------------------------------------------------------------

def peso_humano(bytes_size):
    """Convierte bytes a KB, MB, GB, etc."""

    unidades = ["B", "KB", "MB", "GB", "TB"]

    tamaño = float(bytes_size)

    for unidad in unidades:
        if tamaño < 1024:
            return f"{tamaño:.2f} {unidad}"

        tamaño /= 1024

    return f"{tamaño:.2f} PB"


def lenguaje_archivo(ruta):
    """Determina el lenguaje según la extensión."""

    extension = ruta.suffix.lower()

    return LENGUAJES.get(extension, "Desconocido")


def contar_codigo(ruta):
    """
    Cuenta líneas totales, líneas de código,
    comentarios y líneas vacías.
    """

    resultado = {
        "total": 0,
        "codigo": 0,
        "comentarios": 0,
        "vacias": 0
    }

    try:
        with open(
            ruta,
            "r",
            encoding="utf-8",
            errors="ignore"
        ) as archivo:

            for linea in archivo:

                resultado["total"] += 1

                texto = linea.strip()

                if not texto:
                    resultado["vacias"] += 1

                elif texto.startswith(
                    ("#", "//", "/*", "*", "--")
                ):
                    resultado["comentarios"] += 1

                else:
                    resultado["codigo"] += 1

    except Exception:
        pass

    return resultado


def obtener_archivos(ruta):
    """
    Obtiene archivos de forma recursiva.
    """

    for elemento in ruta.iterdir():

        if elemento.name in IGNORAR:
            continue

        if elemento.is_file():
            yield elemento

        elif elemento.is_dir():

            yield from obtener_archivos(elemento)


def peso_carpeta(ruta):
    """Calcula el tamaño total de una carpeta."""

    total = 0

    try:

        for archivo in obtener_archivos(ruta):

            try:
                total += archivo.stat().st_size
            except Exception:
                pass

    except Exception:
        pass

    return total


def mostrar_archivo(ruta, nivel, informe):
    """Muestra información de un archivo."""

    try:
        tamaño = ruta.stat().st_size
    except Exception:
        tamaño = 0

    extension = ruta.suffix.lower()

    lenguaje = lenguaje_archivo(ruta)

    indent = "    " * nivel

    linea = (
        f"{indent}├── 📄 {ruta.name} "
        f"[{peso_humano(tamaño)}]"
    )

    print(linea)
    informe.append(linea)

    # Si conocemos el lenguaje, analizamos el código
    if extension in LENGUAJES:

        datos = contar_codigo(ruta)

        detalle = (
            f"{indent}│   "
            f"💻 {lenguaje} | "
            f"líneas: {datos['total']} | "
            f"código: {datos['codigo']} | "
            f"comentarios: {datos['comentarios']} | "
            f"vacías: {datos['vacias']}"
        )

        print(detalle)
        informe.append(detalle)


def mostrar_arbol(ruta, nivel, informe):
    """Muestra la estructura recursivamente."""

    try:
        elementos = sorted(
            ruta.iterdir(),
            key=lambda x: (not x.is_dir(), x.name.lower())
        )
    except PermissionError:

        mensaje = (
            "    " * nivel +
            "⚠️ Sin permisos: " +
            str(ruta)
        )

        print(mensaje)
        informe.append(mensaje)

        return

    for elemento in elementos:

        if elemento.name in IGNORAR:
            continue

        indent = "    " * nivel

        if elemento.is_dir():

            tamaño = peso_carpeta(elemento)

            linea = (
                f"{indent}├── 📁 {elemento.name}/ "
                f"[{peso_humano(tamaño)}]"
            )

            print(linea)
            informe.append(linea)

            mostrar_arbol(
                elemento,
                nivel + 1,
                informe
            )

        elif elemento.is_file():

            mostrar_archivo(
                elemento,
                nivel,
                informe
            )


def resumen(ruta, informe):
    """Genera estadísticas generales."""

    archivos = list(obtener_archivos(ruta))

    total_bytes = 0
    lenguajes = {}
    extensiones = {}

    total_lineas = 0
    total_codigo = 0
    total_comentarios = 0

    for archivo in archivos:

        try:
            total_bytes += archivo.stat().st_size
        except Exception:
            pass

        extension = archivo.suffix.lower()

        extensiones[extension or "(sin extensión)"] = (
            extensiones.get(
                extension or "(sin extensión)",
                0
            ) + 1
        )

        if extension in LENGUAJES:

            lenguaje = LENGUAJES[extension]

            lenguajes[lenguaje] = (
                lenguajes.get(lenguaje, 0) + 1
            )

            datos = contar_codigo(archivo)

            total_lineas += datos["total"]
            total_codigo += datos["codigo"]
            total_comentarios += datos["comentarios"]

    print("\n")
    print("=" * 65)
    print("RESUMEN DEL PROYECTO")
    print("=" * 65)

    informe.append("")
    informe.append("=" * 65)
    informe.append("RESUMEN DEL PROYECTO")
    informe.append("=" * 65)

    datos_generales = [
        f"📁 Carpeta: {ruta}",
        f"📄 Archivos: {len(archivos)}",
        f"⚖️ Tamaño total: {peso_humano(total_bytes)}",
        f"📝 Líneas totales: {total_lineas}",
        f"💻 Líneas de código: {total_codigo}",
        f"💬 Líneas de comentarios: {total_comentarios}",
    ]

    for dato in datos_generales:

        print(dato)
        informe.append(dato)

    print("\nLENGUAJES DETECTADOS:")
    informe.append("\nLENGUAJES DETECTADOS:")

    if lenguajes:

        for lenguaje, cantidad in sorted(
            lenguajes.items(),
            key=lambda x: -x[1]
        ):

            texto = f"  • {lenguaje}: {cantidad} archivo(s)"

            print(texto)
            informe.append(texto)

    else:

        print("  No se detectaron archivos de código.")
        informe.append(
            "  No se detectaron archivos de código."
        )

    print("\nEXTENSIONES:")
    informe.append("\nEXTENSIONES:")

    for extension, cantidad in sorted(
        extensiones.items(),
        key=lambda x: -x[1]
    ):

        texto = f"  • {extension}: {cantidad}"

        print(texto)
        informe.append(texto)


# ------------------------------------------------------------
# PROGRAMA PRINCIPAL
# ------------------------------------------------------------

def main():

    print("=" * 65)
    print("   ANALIZADOR DE ESTRUCTURA DE PROYECTOS")
    print("   Compatible con Pydroid 3")
    print("=" * 65)

    # Directorio donde está guardado este script
    carpeta = Path(__file__).resolve().parent

    print("\n📍 Analizando:")
    print(carpeta)

    print("\n")
    print("=" * 65)
    print("ESTRUCTURA")
    print("=" * 65)

    informe = []

    informe.append("=" * 65)
    informe.append("ESTRUCTURA DEL PROYECTO")
    informe.append("=" * 65)
    informe.append(f"Carpeta: {carpeta}")
    informe.append("")

    # Mostrar árbol
    mostrar_arbol(
        carpeta,
        0,
        informe
    )

    # Resumen
    resumen(
        carpeta,
        informe
    )

    # --------------------------------------------------------
    # Guardar informe
    # --------------------------------------------------------

    archivo_informe = carpeta / "estructura_proyecto.txt"

    try:

        with open(
            archivo_informe,
            "w",
            encoding="utf-8"
        ) as archivo:

            archivo.write(
                "INFORME DE ESTRUCTURA DEL PROYECTO\n"
            )

            archivo.write(
                f"Generado: {datetime.now()}\n\n"
            )

            archivo.write(
                "\n".join(informe)
            )

        print("\n")
        print("=" * 65)
        print("✅ INFORME GENERADO")
        print("=" * 65)

        print(archivo_informe)

    except Exception as error:

        print(
            "\n⚠️ No se pudo guardar el informe:"
        )

        print(error)

    print("\n")
    input("Presiona ENTER para terminar...")


if __name__ == "__main__":
    main()