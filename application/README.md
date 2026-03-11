# application

Backend модуль `adaptive-tree-analysis` на Spring Boot.

## Запуск

```bash
mvn spring-boot:run
```

Перед запуском переконайся, що доступна PostgreSQL база:
- `DB_URL` (default: `jdbc:postgresql://localhost:5432/adaptive_tree_analysis`)
- `DB_USERNAME` (default: `postgres`)
- `DB_PASSWORD` (default: `postgres`)

## Перевірка health endpoint

```bash
curl http://localhost:8080/api/v1/health
```

## API контракт для генерації DTO

OpenAPI контракт зберігається у:

`src/main/resources/openapi/adaptive-tree-analysis.openapi.yaml`

## Схема БД для benchmark

Початкова міграція:

`src/main/resources/db/migration/V1__init_benchmark_schema.sql`

Створює таблиці:
- `datasets`
- `experiments`
- `experiment_stats`

Міграції застосовуються через Flyway після налаштування PostgreSQL datasource.

## Benchmark API (мінімум)

- `POST /api/v1/datasets`
- `GET /api/v1/datasets`
- `POST /api/v1/experiments`
- `GET /api/v1/experiments`
- `GET /api/v1/experiments/{experimentId}`
- `POST /api/v1/experiments/{experimentId}/stats`
- `GET /api/v1/experiments/{experimentId}/stats`
