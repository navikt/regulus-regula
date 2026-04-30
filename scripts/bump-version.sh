#!/bin/bash
set -e

# Bump gradle/version file
VERSION=$(cat gradle/version)
echo "Bumping from $VERSION to $((VERSION + 1))"
echo $((VERSION + 1)) > gradle/version

# Commit bumped version
git config user.name "github-actions[bot]"
git config user.email "github-actions[bot]@users.noreply.github.com"
git add gradle/version
git commit -m "chore: v$VERSION released! set next version to v$((VERSION + 1)) [skip ci]"
git push origin HEAD
