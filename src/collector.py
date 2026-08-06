from pathlib import Path
from datetime import datetime

import pandas as pd
import requests
from bs4 import BeautifulSoup
from tqdm import tqdm


# =========================
# RUTAS DEL PROYECTO
# =========================

PROJECT_ROOT = Path(__file__).resolve().parent.parent

SOURCES_PATH = (
    PROJECT_ROOT
    / "data"
    / "external"
    / "sources_validated.csv"
)

OUTPUT_PATH = (
    PROJECT_ROOT
    / "data"
    / "raw"
    / "raw_documents.csv"
)


# =========================
# CONFIGURACIÓN
# =========================

HEADERS = {
    "User-Agent": "TechMind-DataCollector/1.0"
}


# =========================
# FUNCIONES
# =========================

def download_page(url):
    """
    Descarga una página web.
    """

    try:

        response = requests.get(
            url,
            headers=HEADERS,
            timeout=15
        )

        if response.status_code >= 400:

            return None

        return response.text

    except requests.RequestException:

        return None


def extract_text(html):
    """
    Extrae y limpia el texto principal de una página HTML.
    """

    soup = BeautifulSoup(
        html,
        "html.parser"
    )

    # Eliminar elementos que normalmente no contienen
    # contenido técnico útil
    for element in soup([
        "script",
        "style",
        "nav",
        "footer",
        "header",
        "aside"
    ]):

        element.decompose()

    # Obtener texto
    text = soup.get_text(
        separator=" ",
        strip=True
    )

    # Normalizar espacios
    text = " ".join(
        text.split()
    )

    return text


def collect_document(row):
    """
    Descarga y procesa una fuente.
    """

    html = download_page(
        row["url"]
    )

    if html is None:

        return None

    text = extract_text(
        html
    )

    # Evitar documentos vacíos o demasiado pequeños
    if len(text) < 100:

        return None

    return {
        "categoria": row["categoria"],
        "tecnologia": row["tecnologia"],
        "titulo": row["titulo"],
        "tipo": row["tipo"],
        "url": row["url"],
        "idioma": row["idioma"],
        "texto": text,
        "longitud_texto": len(text),
        "fecha_recoleccion": datetime.now().isoformat()
    }


# =========================
# PROGRAMA PRINCIPAL
# =========================

def main():

    print("📂 Cargando catálogo de fuentes...")

    sources = pd.read_csv(
        SOURCES_PATH
    )

    documents = []

    print(
        f"🔍 Procesando {len(sources)} fuentes...\n"
    )

    for _, row in tqdm(
        sources.iterrows(),
        total=len(sources)
    ):

        document = collect_document(
            row
        )

        if document is not None:

            documents.append(
                document
            )

    # Crear DataFrame
    documents_df = pd.DataFrame(
        documents
    )

    # Guardar dataset
    documents_df.to_csv(
        OUTPUT_PATH,
        index=False,
        encoding="utf-8"
    )

    print("\n✅ Recolección completada")

    print(
        f"📄 Documentos obtenidos: "
        f"{len(documents_df)}"
    )

    print(
        f"📁 Archivo generado: "
        f"{OUTPUT_PATH}"
    )


if __name__ == "__main__":

    main()