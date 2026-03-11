FROM node:20-alpine AS ui-build
WORKDIR /workspace/ui

COPY ui/package.json ui/package-lock.json ./
RUN npm ci

COPY ui/index.html ./
COPY ui/tsconfig.json ./
COPY ui/vite.config.ts ./
COPY ui/public ./public
COPY ui/src ./src
COPY backend-ui-dto/generated ../backend-ui-dto/generated

RUN npm run build

FROM caddy:2.8-alpine

COPY deploy/Caddyfile /etc/caddy/Caddyfile
COPY --from=ui-build /workspace/ui/dist /srv
