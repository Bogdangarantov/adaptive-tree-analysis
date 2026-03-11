#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/../.." && pwd)"
OPENAPI_FILE="${ROOT_DIR}/application/src/main/resources/openapi/adaptive-tree-analysis.openapi.yaml"
OUTPUT_FILE="${ROOT_DIR}/backend-ui-dto/generated/api-types.ts"

if [[ ! -f "${OPENAPI_FILE}" ]]; then
  echo "OpenAPI contract not found: ${OPENAPI_FILE}" >&2
  exit 1
fi

npx openapi-typescript "${OPENAPI_FILE}" -o "${OUTPUT_FILE}"
echo "Generated DTO types: ${OUTPUT_FILE}"
