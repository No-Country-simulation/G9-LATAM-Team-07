from pathlib import Path
import pandas as pd


# Ruta raíz del proyecto
PROJECT_ROOT = Path(__file__).resolve().parent.parent

# Rutas de entrada y salida
TOPICS_PATH = PROJECT_ROOT / "data" / "external" / "topics.csv"
SOURCES_PATH = PROJECT_ROOT / "data" / "external" / "sources.csv"


def generate_sources():
    """Genera sources.csv a partir de topics.csv."""

    # Verificar que topics.csv exista
    if not TOPICS_PATH.exists():
        raise FileNotFoundError(
            f"No se encontró el archivo: {TOPICS_PATH}"
        )

    # Leer el catálogo maestro
    topics = pd.read_csv(TOPICS_PATH)

    # Limpiar espacios invisibles en los nombres de las columnas

    topics.columns = topics.columns.str.strip()

    # Validar columnas necesarias
    required_columns = [
        "categoria",
        "tecnologia",
        "sitio_oficial",
        "github",
        "tutorial"
    ]

    missing_columns = [
        column for column in required_columns
        if column not in topics.columns
    ]

    if missing_columns:
        raise ValueError(
            f"Faltan columnas en topics.csv: {missing_columns}"
        )

    sources = []

    # Recorrer cada tecnología
    for _, row in topics.iterrows():

        # Documentación oficial
        if pd.notna(row["sitio_oficial"]):
            sources.append({
                "categoria": row["categoria"],
                "tecnologia": row["tecnologia"],
                "titulo": f"{row['tecnologia']} Official Docs",
                "tipo": "docs",
                "url": row["sitio_oficial"],
                "idioma": "en"
            })

        # GitHub
        if pd.notna(row["github"]):
            sources.append({
                "categoria": row["categoria"],
                "tecnologia": row["tecnologia"],
                "titulo": f"{row['tecnologia']} GitHub",
                "tipo": "github",
                "url": row["github"],
                "idioma": "en"
            })

        # Tutorial
        if pd.notna(row["tutorial"]):
            sources.append({
                "categoria": row["categoria"],
                "tecnologia": row["tecnologia"],
                "titulo": f"{row['tecnologia']} Tutorial",
                "tipo": "tutorial",
                "url": row["tutorial"],
                "idioma": "en"
            })

    # Crear DataFrame
    sources_df = pd.DataFrame(sources)

    # Guardar resultado
    sources_df.to_csv(
        SOURCES_PATH,
        index=False,
        encoding="utf-8"
    )

    print("✅ sources.csv generado correctamente")
    print(f"📊 Total de fuentes: {len(sources_df)}")
    print(f"📁 Archivo: {SOURCES_PATH}")


if __name__ == "__main__":
    generate_sources()