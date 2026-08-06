from pathlib import Path
import pandas as pd
import requests
from tqdm import tqdm


# Ruta raíz del proyecto
PROJECT_ROOT = Path(__file__).resolve().parent.parent

SOURCES_PATH = PROJECT_ROOT / "data" / "external" / "sources.csv"
OUTPUT_PATH = PROJECT_ROOT / "data" / "external" / "sources_validated.csv"


def validate_url(url):
    """
    Verifica si una URL responde correctamente.
    """

    try:
        response = requests.get(
            url,
            timeout=10,
            headers={
                "User-Agent": "TechMind-DataCollector/1.0"
            }
        )

        return {
            "status_code": response.status_code,
            "accessible": response.status_code < 400,
            "error": ""
        }

    except requests.RequestException as error:

        return {
            "status_code": None,
            "accessible": False,
            "error": str(error)
        }


def main():

    # Leer sources.csv
    sources = pd.read_csv(SOURCES_PATH)

    results = []

    print(f"🔍 Validando {len(sources)} fuentes...\n")

    for _, row in tqdm(
        sources.iterrows(),
        total=len(sources)
    ):

        result = validate_url(row["url"])

        results.append({
            "categoria": row["categoria"],
            "tecnologia": row["tecnologia"],
            "titulo": row["titulo"],
            "tipo": row["tipo"],
            "url": row["url"],
            "idioma": row["idioma"],
            "status_code": result["status_code"],
            "accessible": result["accessible"],
            "error": result["error"]
        })

    validated_sources = pd.DataFrame(results)

    validated_sources.to_csv(
        OUTPUT_PATH,
        index=False,
        encoding="utf-8"
    )

    print("\n✅ Validación completada")
    print(f"📁 Archivo: {OUTPUT_PATH}")

    print("\n📊 Resumen:")
    print(
        validated_sources["accessible"]
        .value_counts()
    )


if __name__ == "__main__":
    main()