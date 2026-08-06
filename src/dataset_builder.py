from pathlib import Path

import pandas as pd


# =========================
# RUTAS
# =========================

PROJECT_ROOT = Path(__file__).resolve().parent.parent

INPUT_PATH = (
    PROJECT_ROOT
    / "data"
    / "raw"
    / "raw_documents.csv"
)

OUTPUT_PATH = (
    PROJECT_ROOT
    / "data"
    / "processed"
    / "dataset.csv"
)


# =========================
# CONFIGURACIÓN
# =========================

CHUNK_SIZE = 2000
MIN_CHUNK_SIZE = 300


# =========================
# FUNCIONES
# =========================

def split_text(text, chunk_size=CHUNK_SIZE):
    """
    Divide un texto largo en fragmentos.
    """

    words = text.split()

    chunks = []

    current_chunk = []

    current_length = 0

    for word in words:

        word_length = len(word) + 1

        if (
            current_length + word_length
            > chunk_size
        ):

            chunk = " ".join(
                current_chunk
            )

            if len(chunk) >= MIN_CHUNK_SIZE:

                chunks.append(
                    chunk
                )

            current_chunk = [
                word
            ]

            current_length = word_length

        else:

            current_chunk.append(
                word
            )

            current_length += word_length

    # Último fragmento
    if current_chunk:

        chunk = " ".join(
            current_chunk
        )

        if len(chunk) >= MIN_CHUNK_SIZE:

            chunks.append(
                chunk
            )

    return chunks


def build_dataset():

    print(
        "📂 Cargando documentos..."
    )

    documents = pd.read_csv(
        INPUT_PATH
    )

    dataset_rows = []

    print(
        f"📄 Documentos originales: "
        f"{len(documents)}"
    )

    for _, row in documents.iterrows():

        chunks = split_text(
            row["texto"]
        )

        for index, chunk in enumerate(
            chunks,
            start=1
        ):

            dataset_rows.append({

                "titulo": (
                    f"{row['tecnologia']} - "
                    f"{row['titulo']} - "
                    f"Parte {index}"
                ),

                "texto": chunk,

                "categoria": (
                    row["categoria"]
                )

            })

    dataset = pd.DataFrame(
        dataset_rows
    )

    # Eliminar duplicados
    dataset = dataset.drop_duplicates(
        subset=["texto"]
    )

    # Crear carpeta si no existe
    OUTPUT_PATH.parent.mkdir(
        parents=True,
        exist_ok=True
    )

    # Guardar dataset
    dataset.to_csv(
        OUTPUT_PATH,
        index=False,
        encoding="utf-8"
    )

    print(
        "\n✅ Dataset generado"
    )

    print(
        f"📊 Total de registros: "
        f"{len(dataset)}"
    )

    print(
        f"📁 Archivo: {OUTPUT_PATH}"
    )

    print(
        "\n📊 Distribución:"
    )

    print(
        dataset["categoria"]
        .value_counts()
    )


if __name__ == "__main__":

    build_dataset()