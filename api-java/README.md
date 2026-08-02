# 🧠 TechMind - API Back-End (Java & Spring Boot)

Microservicio principal del proyecto **TechMind**, desarrollado para el **Hackathon ONE G9 (Alura Latam + Oracle)** organizado con **No Country**.

Esta API REST actúa como punto de entrada para el procesamiento y la clasificación inteligente de contenidos técnicos.

---

## 🛠️ Requisitos Previos

Asegúrate de tener instalado:

1. **Java JDK 25** (o superior).
2. **Apache Maven**.
3. **Postman** o cualquier cliente HTTP.

---

## 🚀 Ejecución

Abre una terminal en:

```bash
G9-LATAM-Team-07\api-java
```

Ejecuta:

```bash
mvn spring-boot:run
```

Si el arranque es exitoso, verás líneas como:

- `Tomcat started on port 8080 (http) with context path '/'`
- `Started TechmindApplication in ... seconds`

---

Si el puerto esta ocupado, ejecuta:

```bash
netstat -ano | findstr :8080
```

Una vez encontrado el puerto, colocalo en las "XXXXX":

```bash
taskkill /PID XXXXX /F
```

---

## 🌐 Endpoint disponible

- `POST http://localhost:8080/contenido`

### Headers

```http
Content-Type: application/json
```

### Body de ejemplo

```json
{
  "titulo": "Introducción a Spring Boot",
  "texto": "En este contenido se presentan los conceptos básicos para la creación de APIs REST utilizando Java y Spring Boot."
}
```

```json
{
  "titulo": "Análisis Exploratorio de Datos",
  "texto": "Procesamiento y limpieza de grandes volúmenes de texto utilizando Python, Pandas y preparación de matrices en Jupyter Notebooks."
}
```

```json
{
  "titulo": "Componentes Reactivos con Next.js",
  "texto": "Una guía completa para estructurar layouts, manejar estados globales y optimizar el renderizado utilizando React y TailwindCSS."
}
```

### Respuesta de ejemplo

```json
{
  "categoria": "Backend",
  "probabilidad": 0.89,
  "informacion_adicional": [
    "Java",
    "Spring Boot",
    "API REST"
  ]
}
```

---

## 📌 Notas

- El servicio usa actualmente un modo mock en `ClasificacionService`, por lo que devuelve una respuesta simulada.
- El endpoint requiere que `titulo` y `texto` no estén vacíos.
- El campo `texto` debe tener al menos 10 caracteres.
- Si necesitas cambiar el puerto, edita `src/main/resources/application.properties`.