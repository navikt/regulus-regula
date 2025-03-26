#!/bin/bash
set -e

VERSION=$(cat lib/version)
LAST_TAG=$(git tag --sort=-creatordate | head -n 1)
COMMITS=$(git log ${LAST_TAG}..HEAD --pretty=format:"* %s")

if [ -z "$VERSION" ]; then
  echo "No version found in lib/version"
  exit 1
fi

echo "Got version $VERSION"
echo "Last tag is $LAST_TAG"
echo "Commits since last tag:"
echo "$COMMITS"

# Create git tag
git tag "$VERSION"
git push origin "$VERSION"

# Create GitHub release
gh release create "$VERSION" -t "v$VERSION" -n "$COMMITS"

# Write to GitHub Actions summary
if [ -n "$GITHUB_STEP_SUMMARY" ]; then
  {
    echo "## Release $VERSION"
    echo ""
    echo "$COMMITS"
  } >> "$GITHUB_STEP_SUMMARY"
fi
