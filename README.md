# Numbers Converter

Aplicación web que convierte **números a letras** y **letras a números**, en **español** e **inglés**.
Está formada por dos proyectos independientes que se orquestan con Docker Compose:

| Proyecto | Descripción | Stack | Puerto |
|----------|-------------|-------|--------|
| [`sbj-convert-numbers`](./sbj-convert-numbers) | API REST con la lógica de conversión | Java 17, Spring Boot 3.5.4, Maven | `9090` |
| [`react-convert-numbers`](./react-convert-numbers) | Interfaz web | React 19, TypeScript, Vite, Tailwind CSS | `3002` (host) → `3000` (contenedor) |

![Pantalla principal](docs/images/home.png)

## Capturas de pantalla

| Número → Letras (ES) | Letras → Número (ES) |
|---|---|
| ![ES número a letras](docs/images/es-number-to-words.png) | ![ES letras a número](docs/images/es-words-to-number.png) |

| Número → Words (EN) | Vista móvil |
|---|---|
| ![EN number to words](docs/images/en-number-to-words.png) | <img src="docs/images/mobile.png" width="260" alt="Vista móvil"> |

Documentación interactiva de la API (Swagger UI):

![Swagger UI](docs/images/swagger.png)

## Estructura del repositorio

```
numbers_converter/
├── docker-compose.yml          # Orquesta backend y frontend
├── docs/images/                # Capturas usadas en este README
├── sbj-convert-numbers/        # Backend (Spring Boot)
└── react-convert-numbers/      # Frontend (React + Vite)
```

## Inicio rápido (Docker)

Requisitos: Docker y Docker Compose.

```bash
docker compose up -d --build
```

| Servicio | URL |
|----------|-----|
| Frontend | http://localhost:3002 |
| API | http://localhost:9090/api |
| Swagger UI | http://localhost:9090/api/swagger-ui/index.html |
| Health | http://localhost:9090/api/management/health |

Detener y eliminar los contenedores:

```bash
docker compose down
```

> **Nota sobre `VITE_API_URL`:** Vite incrusta esta variable en el bundle **en tiempo de compilación** y la
> ejecuta el navegador, no el contenedor. Por eso el `docker-compose.yml` la define como
> `http://localhost:9090/api` (URL del backend publicada en el host) y no con el nombre interno de la red
> Docker, que un navegador no puede resolver. Si despliegas en otro host, cambia `args.VITE_API_URL` y
> vuelve a ejecutar `docker compose up -d --build`.

## Prueba rápida de la API

```bash
curl -X POST http://localhost:9090/api/v1/numbers/es/convert \
  -H "Content-Type: application/json" -d '{"number": 2024}'
# {"number":2024,"words":"dos mil veinticuatro","timestamp":"..."}

curl -X POST http://localhost:9090/api/v1/numbers/en/to-number \
  -H "Content-Type: application/json" -d '{"text": "two thousand twenty-four"}'
```

## Arquitectura

```mermaid
flowchart LR
    U([Usuario]) -->|HTTP :3002| FE["Frontend<br/>React + Vite<br/>(serve :3000)"]
    FE -->|"fetch POST /api/v1/numbers/{es|en}/..."| BE["Backend<br/>Spring Boot :9090"]
    BE --> F[NumberConverterFactory]
    F --> ES[SpanishNumberConverter]
    F --> EN[EnglishNumberConverter]
    DEV([Desarrollador]) -.->|Swagger UI| BE
    subgraph Docker network: app-network
        FE
        BE
    end
```

### Diagrama de despliegue

```mermaid
flowchart TB
    subgraph Host
        B[Navegador]
        subgraph Docker["Docker Compose · app-network"]
            C1["convert-numbers-frontend<br/>node:alpine + serve<br/>3002→3000"]
            C2["convert-numbers-backend<br/>Amazon Corretto 17<br/>9090→9090"]
        end
    end
    B -->|":3002"| C1
    B -->|":9090/api"| C2
```

### Diagrama de secuencia: número → letras

```mermaid
sequenceDiagram
    actor U as Usuario
    participant UI as React (NumberToWordsComponent)
    participant F as TransactionInterceptor / LoggingFilter
    participant C as SpanishNumberController
    participant A as AbstractNumberConverterController
    participant S as SpanishNumberConverter

    U->>UI: escribe "2024"
    UI->>UI: valida entero y rango
    UI->>F: POST /api/v1/numbers/es/convert {"number":2024}
    F->>F: genera transaction-id (MDC)
    F->>C: request
    C->>C: @Valid SpanishNumberRequestDTO
    C->>A: convertNumberToWords(2024)
    A->>S: toWords(2024)
    S-->>A: "dos mil veinticuatro"
    A-->>C: NumberResponseDTO
    C-->>UI: 200 {number, words, timestamp}
    UI-->>U: muestra resultado (copiable)
    F-)F: registra request/response (asíncrono)
```

### Diagrama de clases (backend)

```mermaid
classDiagram
    direction LR
    class Language {
        <<enumeration>>
        ES
        EN
        +code() String
        +from(Locale) Language
    }
    class NumberConverter {
        <<interface>>
        +language() Language
        +toWords(long) String
        +toNumber(String) long
    }
    class SpanishNumberConverter
    class EnglishNumberConverter
    class NumberConverterFactory {
        +of(Language) NumberConverter
        +of(Locale) NumberConverter
        +of(String) NumberConverter
    }
    class AbstractNumberConverterController {
        <<abstract>>
        -NumberConverter numberConverter
        #convertNumberToWords(Long) ResponseEntity
        #convertWordsToNumber(String) ResponseEntity
    }
    class SpanishNumberController {
        +convertNumberToWords(SpanishNumberRequestDTO)
        +convertWordsToNumber(SpanishWordsRequestDTO)
    }
    class EnglishNumberController {
        +convertNumberToWords(EnglishNumberRequestDTO)
        +convertWordsToNumber(EnglishWordsRequestDTO)
    }
    class NumberResponseDTO {
        <<record>>
        Long number
        String words
        String timestamp
    }
    class WordsResponseDTO {
        <<record>>
        String text
        Long number
        String timestamp
    }
    class ErrorResponseDTO {
        <<record>>
        String message
        String error
        String timestamp
        String path
    }
    class GlobalExceptionHandler
    class ServiceException

    NumberConverter <|.. SpanishNumberConverter
    NumberConverter <|.. EnglishNumberConverter
    NumberConverterFactory ..> NumberConverter : crea / resuelve
    NumberConverterFactory ..> Language
    AbstractNumberConverterController --> NumberConverter
    AbstractNumberConverterController <|-- SpanishNumberController
    AbstractNumberConverterController <|-- EnglishNumberController
    AbstractNumberConverterController ..> NumberResponseDTO
    AbstractNumberConverterController ..> WordsResponseDTO
    AbstractNumberConverterController ..> ServiceException
    GlobalExceptionHandler ..> ErrorResponseDTO
    GlobalExceptionHandler ..> ServiceException
```

### Diagrama de componentes (frontend)

```mermaid
flowchart TD
    M[main.jsx] --> A[App.jsx]
    A --> C["NumberToWordsComponent.tsx"]
    C --> S["Estado: idioma · modo · entrada · resultado"]
    C --> V["Validación: rangos por idioma y regex de texto"]
    C --> I18N["CONTENT es / en"]
    C -->|fetch| API["POST {VITE_API_URL}/v1/numbers/{idioma}/convert | to-number"]
```

## Límites por idioma

| Idioma | Rango numérico | Escala |
|--------|----------------|--------|
| Español | −999 999 999 999 999 a 999 999 999 999 999 | larga (billón = 10¹²) |
| Inglés | −9 223 372 036 854 775 807 a 9 223 372 036 854 775 807 | corta (billion = 10⁹) |

El texto de entrada admite hasta 300 caracteres (letras y espacios; en inglés también guiones).

## Documentación detallada

- [Backend – `sbj-convert-numbers`](./sbj-convert-numbers/README.md)
- [Frontend – `react-convert-numbers`](./react-convert-numbers/README.md)
