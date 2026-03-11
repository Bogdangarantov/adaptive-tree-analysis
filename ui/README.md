# ui

Frontend модуль `adaptive-tree-analysis` на Vue 3 + D3.js.
Структура включає Vue Router і окремі сторінки в `src/views`.
DTO-типи API імпортуються із `backend-ui-dto/generated` через alias `@dto/*`.
Головний маршрут `/` рендерить Vue view `GeneratedUiView`, який відтворює UI з `ui-generated`.

## Встановлення залежностей

```bash
npm install
```

## Запуск dev-сервера

```bash
npm run dev
```

Важливо: для API-запитів backend має працювати на `http://localhost:8080` (Vite proxy `'/api' -> ':8080'`).

## Оновлення DTO після змін backend API

```bash
cd ../backend-ui-dto
npm install
npm run generate
```
