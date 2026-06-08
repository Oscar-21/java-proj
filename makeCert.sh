#!/usr/bin/env bash
#
# Generate a self-signed certificate for Microsoft Graph app-only (certificate)
# authentication and produce the three artifacts you need:
#
#   * <name>.pfx          PKCS#12 bundle (private key + cert), password-protected
#   * <name>.pfx.base64   base64 of the PFX, single line  -> GRAPH_CERTIFICATE_BASE64
#   * <name>.cer          public certificate (DER)         -> upload to Azure app registration
#
# Requires: openssl
#
# Usage:
#   PFX_PASSWORD='ChangeMe!' ./make-graph-cert.sh
#
# Optional overrides (env vars):
#   CERT_CN     subject common name   (default: graph-app)
#   CERT_DAYS   validity in days      (default: 365)
#   OUT_DIR     output directory      (default: ./certs)
#   BASENAME    base file name        (default: graph-cert)
#
set -euo pipefail

# ---- Configuration ---------------------------------------------------------
CN="${CERT_CN:-graph-app}"
DAYS="${CERT_DAYS:-365}"
OUT_DIR="${OUT_DIR:-./certs}"
BASENAME="${BASENAME:-graph-cert}"
PFX_PASSWORD="djksldjk883kjj32kkddskjd8"
# ----------------------------------------------------------------------------

if [[ -z "$PFX_PASSWORD" ]]; then
  echo "ERROR: PFX_PASSWORD is not set." >&2
  echo "       Run as:  PFX_PASSWORD='YourStrongPassword' $0" >&2
  exit 1
fi

command -v openssl >/dev/null 2>&1 || {
  echo "ERROR: openssl is not installed or not on PATH." >&2
  exit 1
}

mkdir -p "$OUT_DIR"

KEY="$OUT_DIR/$BASENAME.key"   # intermediate (private key, unencrypted)
CRT="$OUT_DIR/$BASENAME.crt"   # intermediate (public cert, PEM)
PFX="$OUT_DIR/$BASENAME.pfx"
CER="$OUT_DIR/$BASENAME.cer"
B64="$OUT_DIR/$BASENAME.pfx.base64"

# Export the password so openssl can read it from the environment instead of
# the command line (keeps it out of the process list / `ps` output).
export PFX_PASSWORD

echo ">> Generating private key + self-signed certificate (CN=$CN, ${DAYS}d)..."
openssl req -x509 -newkey rsa:2048 -sha256 -days "$DAYS" -nodes \
  -keyout "$KEY" -out "$CRT" -subj "/CN=$CN"

echo ">> Bundling into password-protected PFX (PKCS#12)..."
openssl pkcs12 -export \
  -inkey "$KEY" -in "$CRT" \
  -out "$PFX" -passout env:PFX_PASSWORD

echo ">> Exporting public certificate as DER-encoded .cer for Azure..."
openssl x509 -in "$CRT" -outform DER -out "$CER"

echo ">> Base64-encoding the PFX as a single line..."
openssl base64 -A -in "$PFX" -out "$B64"

# Lock down permissions on anything containing the private key.
chmod 600 "$KEY" "$PFX" "$B64"

echo
echo "Done. Files written to $OUT_DIR/"
echo "  $(basename "$PFX")          -> the PFX (keep secret)"
echo "  $(basename "$B64")   -> set as GRAPH_CERTIFICATE_BASE64 (keep secret)"
echo "  $(basename "$CER")          -> upload to Azure app registration > Certificates & secrets"
echo
echo "Intermediate PEM files you can delete once you've verified the PFX works:"
echo "  $(basename "$KEY")  $(basename "$CRT")"
echo
echo "Reminder: GRAPH_CERTIFICATE_PASSWORD must be set to the same password you used here."