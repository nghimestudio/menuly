#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/worker"

if [[ -f "$ROOT/.env" ]]; then
  # shellcheck disable=SC1091
  set -a
  source "$ROOT/.env"
  set +a
fi

if [[ -z "${REPLICATE_API_TOKEN:-}" ]]; then
  echo "Set REPLICATE_API_TOKEN in .env or environment"
  exit 1
fi

npm install
echo "$REPLICATE_API_TOKEN" | npx wrangler secret put REPLICATE_API_TOKEN
npx wrangler deploy
echo ""
echo "Update android/app/build.gradle.kts API_BASE_URL (release) to your workers.dev URL"
