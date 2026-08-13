# Frontend - TechMind

Este proyecto es el frontend de la aplicación TechMind, desarrollado con React + Vite.

## Requisitos

Antes de empezar, asegúrate de tener instalado:

- Node.js 20+ recomendado
- npm o pnpm
- Git

## 1) Entrar a la carpeta del frontend

Abre una terminal y dirígete a la carpeta del proyecto frontend:

```bash
cd frontend
```

## 2) Instalar dependencias

Ejecuta:

```bash
npm install
```

Esto descargará todas las dependencias necesarias para correr la app.

## 3) Ejecutar el proyecto en modo desarrollo

Una vez instaladas las dependencias, corre:

```bash
npm run dev
```

Esto levantará el servidor de desarrollo de Vite. En la terminal te aparecerá una URL similar a:

```bash
http://localhost:5173/
```

Abre esa URL en tu navegador para ver la aplicación.

## 4) Construir la versión de producción

Si quieres generar una build lista para producción:

```bash
npm run build
```

Luego puedes previsualizarla con:

```bash
npm run preview
```

## 5) Estructura importante

- `src/` → código principal de la aplicación
- `src/components/` → componentes reutilizables
- `src/services/` → llamadas a la API
- `vite.config.js` → configuración de Vite

## 6) Conexión con el backend

El frontend consume la API del backend desde `src/services/api.js`.

Si necesitas apuntar a tu backend local, revisa ese archivo y cambia la URL base según tu entorno.

Ejemplo:

```js
const API_BASE_URL = 'http://localhost:8080';
```

## 7) Solución de problemas comunes

### Error de dependencias

Si aparece un error al instalar paquetes, intenta:

```bash
rm -rf node_modules package-lock.json
npm install
```

### Puerto ocupado

Si el puerto 5173 ya está en uso, Vite intentará abrir otro puerto automáticamente. También puedes probar:

```bash
npm run dev -- --host
```

## Comandos rápidos

```bash
cd frontend
npm install
npm run dev
```

Si quieres, también te puedo dejar un README más completo con una sección de "Cómo contribuir" o con la explicación de cada archivo del frontend.
