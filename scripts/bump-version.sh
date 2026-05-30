#!/usr/bin/env bash
set -e

# ---------------------------------------------------------------------------
# Usage:
#   ./scripts/bump-version.sh <neue-version>       # nur Dateien updaten
#   ./scripts/bump-version.sh <neue-version> -y    # Dateien + git commit + tag + push
#
# Beispiele:
#   ./scripts/bump-version.sh 3.0.0-pre.9-SNAPSHOT
#   ./scripts/bump-version.sh 3.0.0 -y
# ---------------------------------------------------------------------------

ROOT="$(cd "$(dirname "$0")/.." && pwd)"

VERSION=""
AUTO_GIT=false

for arg in "$@"; do
  case "$arg" in
    -y) AUTO_GIT=true ;;
    *)  VERSION="$arg" ;;
  esac
done

if [ -z "$VERSION" ]; then
  echo "Fehler: Keine Version angegeben."
  echo "Verwendung: $0 <version> [-y]"
  exit 1
fi

echo "Setze Version: $VERSION"

# build.gradle.kts — version = "..."
sed -i '' "s/version = \"[^\"]*\"/version = \"$VERSION\"/" "$ROOT/build.gradle.kts"
echo "  build.gradle.kts"

# gradle/libs.versions.toml — velocloud = "..."
sed -i '' "s/^velocloud = \"[^\"]*\"/velocloud = \"$VERSION\"/" "$ROOT/gradle/libs.versions.toml"
echo "  gradle/libs.versions.toml"

# CLAUDE.md — Current version: `...`
sed -i '' "s/Current version: \`[^\`]*\`/Current version: \`$VERSION\`/" "$ROOT/CLAUDE.md"
echo "  CLAUDE.md"

echo "Fertig — Version ist jetzt: $VERSION"

if [ "$AUTO_GIT" = true ]; then
  echo ""
  echo "Führe Git-Befehle aus..."

  cd "$ROOT"

  git add build.gradle.kts gradle/libs.versions.toml CLAUDE.md
  git commit -m "chore: bump version to $VERSION"
  echo "  Commit erstellt"

  # Tag nur bei Release-Version (kein SNAPSHOT)
  if [[ "$VERSION" != *"-SNAPSHOT" ]]; then
    git tag "v$VERSION"
    echo "  Tag v$VERSION gesetzt"
    git push origin HEAD
    git push origin "v$VERSION"
    echo "  Gepusht — Release-Workflow startet automatisch auf GitHub"
  else
    git push origin HEAD
    echo "  Gepusht (SNAPSHOT — kein Tag)"
  fi
fi
