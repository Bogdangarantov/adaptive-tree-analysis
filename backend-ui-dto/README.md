# backend-ui-dto

Папка для контрактного шару між `application` і `ui`.

## Принцип
- Джерело правди для DTO: backend (`application`).
- DTO для frontend не пишуться вручну.
- Після зміни API-контракту backend потрібно виконати генерацію типів у `generated/`.

## Структура
- `generated/` — згенеровані артефакти (TypeScript типи/DTO).
- `scripts/generate-dto.sh` — скрипт генерації з OpenAPI.
- `package.json` — залежність `openapi-typescript` і npm-скрипт `generate`.

## Джерело контракту
- `application/src/main/resources/openapi/adaptive-tree-analysis.openapi.yaml`

## Правило змін
1. Змінити контракт у backend.
2. Згенерувати DTO у `backend-ui-dto/generated`.
3. Оновити імпорти у `ui`.

## Генерація

```bash
cd backend-ui-dto
npm install
npm run generate
```
