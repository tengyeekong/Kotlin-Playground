#!/bin/bash

#PROJECT_ROOT=$(cd -P "$(dirname "$(readlink "${BASH_SOURCE[0]}" || echo "${BASH_SOURCE[0]}")")/.." && pwd)
#echo "Project Root:" "${PROJECT_ROOT}"
#
#cd "$PROJECT_ROOT" || exit

set -exo pipefail

#echo "--- Install dependencies"
#yarn

## We use fastlane to run test, because fastlane give us better output
echo "+++ Tests"
#yarn bundle exec fastlane test

## Zip HTML reports
#npx run-func zip.js zipFile "$PROJECT_ROOT" "app/build/reports/tests/testProdReleaseUnitTest"

# temporary we just try to build the app, the tests is too heavy
# yarn fastlane run gradle tasks:app:build
