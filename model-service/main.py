from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
import random # MOCK

app = FastAPI(
    title="TechMind - Servicio de organización inteligente del conocimiento técnico",
    version="0.1.0",
    description="Clasifica documentación técnica. Estado actual: modelo simulado."
)

class PredictRequest(BaseModel):
    texto: str = Field(
        ...,
        description="Texto a clasificar"
    )

class PredictResponse(BaseModel):
    label: str
    confidence: float
    keywords: list[str]

# --- MOCK ---

CATEGORIAS = ["Backend", "Frontend", "Data Science", "DevOps", "Bases de Datos"]

PISTAS = {
    "Backend": ["api", "spring", "java", "endpoint", "servidor", "rest"],
    "Frontend": ["react", "css", "html", "componente", "navegador", "ui"],
    "Data Science": ["modelo", "dataset", "pandas", "entrenar", "datos", "ml"],
    "DevOps": ["docker", "kubernetes", "pipeline", "despliegue", "ci", "cd"],
    "Bases de Datos": ["sql", "postgres", "consulta", "tabla", "índice", "base de datos"],
}


def clasificar_mock(texto: str) -> PredictResponse:
    texto_min = texto.lower()

    # 1) Contar cuántas pistas de cada categoría aparecen en el texto
    mejor_categoria = None
    mejor_puntaje = 0
    for categoria, pistas in PISTAS.items():
        puntaje = 0
        for pista in pistas:
            if pista in texto_min:
                puntaje += 1
        if puntaje > mejor_puntaje:
            mejor_puntaje = puntaje
            mejor_categoria = categoria

    # 2) Si ninguna categoría tuvo coincidencias, elegir al azar con baja confianza
    if mejor_categoria is None:
        mejor_categoria = random.choice(CATEGORIAS)
        confianza = round(random.uniform(0.40, 0.60), 2)
    else:
        confianza = round(random.uniform(0.75, 0.98), 2)

    # 3) Keywords placeholder: las palabras "largas" del texto
    keywords = []
    for palabra in texto_min.split():
        limpia = palabra.strip(".,;:()¿?¡!\"'")
        if len(limpia) > 4 and limpia not in keywords:
            keywords.append(limpia)
    keywords = keywords[:5]
    if not keywords:
        keywords = ["sin_keywords"]

    # 4) Devolver el resultado usando el molde (con los nombres acordados con Java)
    return PredictResponse(label=mejor_categoria, confidence=confianza, keywords=keywords)


# --- ENDPOINTS ---

@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    if not req.texto or not req.texto.strip():
        raise HTTPException(status_code=400, detail="El campo 'texto' no puede estar vacío")

    return clasificar_mock(req.texto)


@app.get("/health")
def health():
    return {"status": "ok", "modo": "mock"}
