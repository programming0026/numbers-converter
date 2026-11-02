# react-convert-numbers

Interfaz web (React + TypeScript) para convertir **números a letras** y **letras a números** en español e inglés.
Consume la API de [`sbj-convert-numbers`](../sbj-convert-numbers).

![Pantalla principal](docs/images/home.png)

## Características

- Selector de **idioma** (Español / English) y de **modo** (Número → Letras / Letras → Número).
- Conversión en tiempo real mientras se escribe.
- Validación en el cliente: entero válido, rango por idioma, y patrón de texto permitido (máx. 300 caracteres).
- Copiar resultado al portapapeles, botón de limpiar y ejemplos clicables.
- Interfaz responsiva e i18n completa (es/en).

| Número → Letras (ES) | Letras → Número (ES) |
|---|---|
| ![ES](docs/images/es-number-to-words.png) | ![ES](docs/images/es-words-to-number.png) |

| Number → Words (EN) | Móvil |
|---|---|
| ![EN](docs/images/en-number-to-words.png) | <img src="docs/images/mobile.png" width="260" alt="Vista móvil"> |

## Stack

React 19 · TypeScript · Vite · Tailwind CSS 4 · lucide-react · ESLint

## Configuración

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `VITE_API_URL` | URL base de la API | `http://localhost:9090/api` |

Vite incrusta la variable **al compilar** (`.env` o `--build-arg` en Docker). Como la usa el navegador, debe
apuntar a una URL accesible desde el host (no a un nombre interno de la red Docker).

## Desarrollo local

```bash
npm install
npm run dev        # http://localhost:5173
npm run build      # genera dist/
npm run preview
npm run lint
```

Requiere el backend en ejecución (por defecto `http://localhost:9090/api`). El backend tiene CORS abierto.

## Docker

Con el stack completo, desde la raíz del repositorio padre:

```bash
docker compose up -d --build        # UI en http://localhost:3002
```

Solo el frontend:

```bash
docker network create -d bridge services-net
docker build --no-cache -f Dockerfile \
  --build-arg VITE_API_URL=http://localhost:9090/api -t react-convert-numbers:0.0.1 .
docker run --network=services-net --name=react-convert-numbers -d -p 3000:3000 react-convert-numbers:0.0.1
docker rm -f react-convert-numbers  # eliminar
```

> `VITE_API_URL` se lee en tiempo de compilación; pásala con `--build-arg`. Un `--env-file` en `docker run`
> no afecta al bundle ya generado.

La imagen es multi-etapa: `node:alpine` compila con Vite y sirve `dist/` con `serve` en el puerto 3000.

## Arquitectura

```
src/
├── main.jsx                          # punto de entrada
├── App.jsx
└── components/
    └── NumberToWordsComponent.tsx    # estado, validación, i18n, llamadas a la API y UI
```

### Flujo del componente

```mermaid
flowchart TD
    A[Usuario escribe en #entrada] --> B{modo}
    B -->|toWords| C[parseValidInteger]
    C -->|inválido| X[Mensaje de error]
    C --> D{¿dentro del rango del idioma?}
    D -->|no| X
    D -->|sí| E["POST /v1/numbers/{idioma}/convert<br/>body {number}"]
    B -->|toNumber| F{¿coincide con WORDS_PATTERNS?}
    F -->|no| X
    F -->|sí| G["POST /v1/numbers/{idioma}/to-number<br/>body {text}"]
    E --> H[setResultado data.words]
    G --> I[setResultado extractNumberLiteral]
    E -.->|error HTTP o red| X
    G -.->|error HTTP o red| X
```

### Secuencia

```mermaid
sequenceDiagram
    actor U as Usuario
    participant C as NumberToWordsComponent
    participant API as sbj-convert-numbers

    U->>C: elige idioma y modo
    U->>C: escribe entrada
    C->>C: useEffect → valida
    C->>API: POST /api/v1/numbers/{es|en}/{convert|to-number}
    API-->>C: 200 {words | number} / 4xx {message}
    C-->>U: resultado o mensaje de error
    U->>C: Copiar
    C->>C: navigator.clipboard.writeText
```

Nota: en modo letras → número, el valor se extrae del texto crudo de la respuesta (`extractNumberLiteral`) para
conservar la precisión de enteros mayores a 2⁵³ que `JSON.parse` redondearía; del mismo modo, el número se envía
como literal para no perder precisión.
