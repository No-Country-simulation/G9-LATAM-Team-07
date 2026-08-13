import os
import logging
import urllib.request
import re
from typing import Optional, List, Dict, Any
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
import joblib

# Configuración de logs
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("predict-service")

TAGS = [
    {"name": "Predicción", "description": "Clasifica un texto técnico."},
    {"name": "Consulta", "description": "Contenidos ya clasificados."},
    {"name": "Estado", "description": "Estado del servicio."},
]

app = FastAPI(
    title="TechMind - Predict Service",
    description=(
        "Clasifica contenido técnico en ocho categorías, "
        "Backend, Frontend, Data Science, Machine Learning, DevOps, "
        "Cloud, Cybersecurity y Bases de Datos."
    ),
    version="1.0.0",
    openapi_tags=TAGS,
)


# Modelo V2 desde OCI Object Storage
MODEL_PATH = "modelo_v2.joblib"
OCI_PAR_URL = os.getenv(
    "OCI_PAR_URL",
    "https://objectstorage.sa-santiago-1.oraclecloud.com/p/V0vT6MGLrTvITktjAIxo4hxjyBqRKFlxPB6eRffxdSHJe7yE7UEcCe8Lve3FOPI7/n/axt0oymbmmin/b/techmind-datasets/o/models/modelo_v2.joblib"
)

# ---------------------------------------------------------
# Base de datos en memoria para almacenamiento temporal
# ---------------------------------------------------------
db_contenidos: List[Dict[str, Any]] = []

# ---------------------------------------------------------
# Stopwords (Español e Inglés)
# ---------------------------------------------------------
STOPWORDS_ES = [
    "de", "la", "que", "el", "en", "y", "a", "los", "del", "se", "las", "por", "un", "para", "con", "no", 
    "una", "su", "al", "lo", "como", "más", "pero", "sus", "le", "ya", "o", "este", "sí", "porque", "esta", 
    "entre", "cuando", "muy", "sin", "sobre", "también", "me", "hasta", "hay", "donde", "quien", "desde", 
    "todo", "nos", "durante", "todos", "uno", "les", "ni", "contra", "otros", "ese", "eso", "ante", "ellos", 
    "e", "esto", "mí", "antes", "algunos", "qué", "unos", "yo", "otro", "otras", "otra", "él", "tanto", 
    "esa", "estos", "mucho", "quienes", "nada", "muchos", "cual", "poco", "ella", "estar", "estas", "algunas", 
    "algo", "nosotros", "mi", "mis", "tú", "te", "ti", "tu", "tus", "ellas", "nosotras", "vosotros", "vosotras", 
    "os", "mío", "mía", "míos", "mías", "tuyo", "tuya", "tuyos", "tuyas", "suyo", "suya", "suyos", "suyas", 
    "nuestro", "nuestra", "nuestros", "nuestras", "vuestro", "vuestra", "vuestros", "vuestras", "esos", "esas"
]

STOPWORDS_EN = [
    "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", 
    "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", 
    "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", 
    "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", 
    "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", 
    "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", 
    "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", 
    "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", 
    "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", 
    "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", 
    "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", 
    "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", 
    "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", 
    "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", 
    "you've", "your", "yours", "yourself", "yourselves"
]

ALL_STOPWORDS = set(STOPWORDS_ES + STOPWORDS_EN)

model = None


def download_model(force_redownload: bool = True):
    """Descarga modelo_v2.joblib desde OCI."""
    global MODEL_PATH, OCI_PAR_URL

    if force_redownload and os.path.exists(MODEL_PATH):
        logger.info(f"Eliminando {MODEL_PATH} anterior para descargar la última versión...")
        os.remove(MODEL_PATH)

    if not os.path.exists(MODEL_PATH):
        logger.info(f"Descargando {MODEL_PATH} desde OCI Object Storage...")
        try:
            req = urllib.request.Request(
                OCI_PAR_URL,
                headers={"User-Agent": "Mozilla/5.0"}
            )
            with urllib.request.urlopen(req) as response, open(MODEL_PATH, "wb") as out_file:
                out_file.write(response.read())

            size_mb = os.path.getsize(MODEL_PATH) / (1024 * 1024)
            logger.info(f"Modelo descargado exitosamente de OCI ({size_mb:.2f} MB).")
        except Exception as e:
            logger.error(f"Error al descargar el modelo desde OCI: {e}")
            raise RuntimeError(f"No se pudo descargar el modelo desde OCI: {e}")
    else:
        logger.info(f"El archivo {MODEL_PATH} ya existe localmente.")


@app.on_event("startup")
def load_model():
    """Descarga e inicializa el modelo al arrancar el contenedor."""
    global model
    try:
        download_model(force_redownload=True)
        logger.info("Cargando el modelo preentrenado en memoria...")
        model = joblib.load(MODEL_PATH)
        logger.info("Modelo cargado exitosamente en memoria.")
    except Exception as e:
        logger.error(f"Error al cargar el archivo de modelo: {e}")


class PredictionInput(BaseModel):
    titulo: Optional[str] = Field(
        "",
        description="Título del contenido. Es opcional, se concatena al texto antes de clasificar.",
        examples=["Payment service deployment runbook"]
    )
    texto: str = Field(
        ...,
        description="Texto a clasificar.",
        examples=["Steps to roll out the payment service: build the docker image and run the pipeline."]
    )


class PredictionOutput(BaseModel):
    label: str = Field(
        description="Categoría detectada por el modelo.",
        examples=["DevOps"]
    )
    confidence: float = Field(
        description=(
            "Confianza de la predicción, de 0 a 1. "
            "Hoy el servicio devuelve siempre 1.0, falta conectar predict_proba del modelo."
        ),
        examples=[1.0]
    )
    keywords: List[str] = Field(
        description="Hasta 5 palabras clave extraídas del texto, sin stopwords.",
        examples=[["payment", "docker", "pipeline"]]
    )


class ErrorResponse(BaseModel):
    detail: str = Field(
        examples=["El modelo no está disponible o no se cargó correctamente."]
    )



def preprocess_text(text: str) -> str:
    """Elimina stopwords en español e inglés."""
    words = text.lower().split()
    filtered_words = [w for w in words if w not in ALL_STOPWORDS]
    return " ".join(filtered_words)


def extract_keywords(text: str, top_n: int = 5) -> List[str]:
    """Extrae palabras clave descartando las stopwords."""
    words = re.findall(r'\b[a-zA-Z]{3,}\b', text.lower())
    keywords = []
    for word in words:
        if word not in ALL_STOPWORDS and word not in keywords:
            keywords.append(word)
        if len(keywords) == top_n:
            break
    return keywords or ["contenido", "general"]


# ---------------------------------------------------------
# ENDPOINTS GET
# ---------------------------------------------------------

@app.get("/", tags=["Estado"], summary="Estado del servicio")
def read_root():
    return {
        "service": "predict-service",
        "status": "ready" if model is not None else "model_not_loaded"
    }


@app.get("/contenido", tags=["Consulta"], summary="Lista todo lo clasificado")
def get_todos_los_contenidos():
    """Obtiene todos los contenidos guardados."""
    return db_contenidos


@app.get("/contenido/categoria/{categoria}", tags=["Consulta"], summary="Filtra por categoría")
@app.get("/categoria/{categoria}", include_in_schema=False)
def get_contenidos_por_categoria(categoria: str):
    """Filtra y obtiene los contenidos por su etiqueta/categoría."""
    resultados = [
        item for item in db_contenidos 
        if item.get("label", "").lower() == categoria.lower()
    ]
    return resultados


# ---------------------------------------------------------
# ENDPOINTS POST (PREDICCIÓN)
# ---------------------------------------------------------

@app.post(
    "/predict",
    response_model=PredictionOutput,
    tags=["Predicción"],
    summary="Clasifica un texto",
    responses={
        400: {"model": ErrorResponse, "description": "Error al procesar la predicción"},
        503: {"model": ErrorResponse, "description": "El modelo no está cargado"},
    },
)
@app.post("/contenido", response_model=PredictionOutput, include_in_schema=False)
def predict(data: PredictionInput):
    if model is None:
        raise HTTPException(
            status_code=503,
            detail="El modelo no está disponible o no se cargó correctamente."
        )
    
    try:
        raw_text = f"{data.titulo or ''} {data.texto or ''}".strip()
        cleaned_text = preprocess_text(raw_text)
        
        # Transformación del texto con el vectorizador del dict de joblib
        if isinstance(model, dict):
            vectorizer = model['vectorizer']
            clf = model['model']
            text_vec = vectorizer.transform([cleaned_text])
            prediction = clf.predict(text_vec)
        else:
            prediction = model.predict([cleaned_text])

        categoria = str(prediction[0])
        keywords = extract_keywords(raw_text)

        # Estructura del resultado
        resultado = {
            "titulo": data.titulo,
            "texto": data.texto,
            "label": categoria,
            "confidence": 1.0,
            "keywords": keywords
        }

        # Guardar en almacenamiento temporal
        db_contenidos.append(resultado)

        # Mapeo de respuesta para Java PythonResponse
        return {
            "label": categoria,
            "confidence": 1.0,
            "keywords": keywords
        }
    except Exception as e:
        logger.error(f"Error al procesar la predicción: {e}")
        raise HTTPException(status_code=400, detail=f"Error en la predicción: {str(e)}")
