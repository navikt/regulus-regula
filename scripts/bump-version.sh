#!/bin/bash
set -e

# Bump libs/version file
VERSION=$(cat lib/version)
echo "Bumping from $VERSION to $((VERSION + 1))"
echo $((VERSION + 1)) > lib/version

# Commit bumped version
git config user.name "github-actions[bot]"
git config user.email "github-actions[bot]@users.noreply.github.com"
git add lib/version
git commit -m "chore: set next version to $((VERSION + 1)) [skip ci]"
git push origin HEAD
