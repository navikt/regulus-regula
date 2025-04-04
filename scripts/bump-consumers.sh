#!/bin/bash

#VERSION=$(cat lib/version)
VERSION=69
BRANCH="bump-regula-v$VERSION"
REPOS=(
  "navikt/syfosmregler"
  "navikt/syfosmpapirregler"
)

gh auth login --with-token $GOD_TOKEN

for REPO in "${REPOS[@]}"; do
  NAME="${REPO##*/}"
  gh repo clone "$REPO" "$NAME"
  cd "$NAME"

  git config user.name "github-actions[bot]"
  git config user.email "github-actions[bot]@users.noreply.github.com"

  git checkout -b "$BRANCH"

  sed -i -E "s|implementation\(\"no\.nav\.tsm\.regulus:regula:[0-9]+\"\)|implementation(\"no.nav.tsm.regulus:regula:$VERSION\")|" build.gradle.kts

  git commit -am "chore: bump regula to version $VERSION [automated]"
  git push --set-upstream origin "$BRANCH"

  gh pr create --fill

  cd ..
done
