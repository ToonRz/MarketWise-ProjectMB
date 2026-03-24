# GitHub Upload Scan Report

Date: 2026-03-24 (UTC)

## Scope
Scanned repository for commonly non-committable files:
- Local environment/config: `.env`, `.env.*`, `local.properties`
- Secrets/certificates: `*.keystore`, `*.jks`, `*.p12`, `*.pem`, `*.key`
- Build artifacts: `*.apk`, `*.aab`
- Service credentials: `google-services.json`
- IDE metadata: `.idea/`, `*.iml` (tracked-file check)

## Findings
- No sensitive files or build artifacts were found in the current tracked files.
- Existing ignore files are present:
  - `.gitignore`
  - `MarketWiseProject/.gitignore`
  - `MarketWiseProject/app/.gitignore`

## Recommendation
- Keep API keys only in local `local.properties` or CI secret variables.
- Rotate keys immediately if any credential is ever committed.
