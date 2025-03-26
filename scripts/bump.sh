#!/bin/bash
set -e

VERSION=$(cat lib/version)
LAST_TAG=$(git tag --sort=-creatordate | head -n 1)
COMMITS=$(git log ${LAST_TAG}..HEAD --pretty=format:"* %s")

# Create git tag
git tag "$VERSION"
git push origin "$VERSION"

# Create GitHub release
gh release create "$VERSION" -t "Release $VERSION" -n "$COMMITS"
