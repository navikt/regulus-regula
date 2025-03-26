#!/bin/bash
set -e

# Commit bumped version
git config user.name "github-actions[bot]"
git config user.email "github-actions[bot]@users.noreply.github.com"
git add lib/version
git commit -m "Set next version to $VERSION [skip ci]"
git push origin HEAD

VERSION=$(cat lib/version)
LAST_TAG=$(git tag --sort=-creatordate | head -n 1)
COMMITS=$(git log ${LAST_TAG}..HEAD --pretty=format:"* %s")

# Create git tag
git tag "$VERSION"
git push origin "$VERSION"

# Create GitHub release
gh release create "$VERSION" -t "Release $VERSION" -n "$COMMITS"

# Write to GitHub Actions summary
if [ -n "$GITHUB_STEP_SUMMARY" ]; then
  {
    echo "## Release $VERSION"
    echo ""
    echo "$COMMITS"
  } >> "$GITHUB_STEP_SUMMARY"
fi
