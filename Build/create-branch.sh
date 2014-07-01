#!/bin/bash

#
# Create a new branch for the distribution and the packages
#
# - TYPO3.Neos
# - TYPO3.Neos.NodeTypes
# - TYPO3.Neos.Kickstarter
# - TYPO3.TYPO3CR
# - TYPO3.TypoScript
# - TYPO3.NeosDemoTypo3Org
# - TYPO3.Media
#
# Needs the following arguments
#
# $1 BRANCH    the branch to create
# $2 BUILD_URL used in commit message
#

source $(dirname ${BASH_SOURCE[0]})/BuildEssentials/ReleaseHelpers.sh

if [ -z "$1" ] ; then
	echo >&2 "No branch specified (e.g. 2.1) as first parameter"
	exit 1
fi
BRANCH=$1

if [ -z "$2" ] ; then
	echo >&2 "No build URL given as second parameter"
	exit 1
fi
BUILD_URL="$2"

# branch distribution
git checkout -b ${BRANCH} origin/master

# branch packages
for PACKAGE in TYPO3.Neos TYPO3.Neos.NodeTypes TYPO3.Neos.Kickstarter TYPO3.TYPO3CR TYPO3.TypoScript TYPO3.Media ; do
	git --git-dir "Packages/Application/${PACKAGE}/.git" --work-tree "Packages/Application/${PACKAGE}" checkout -b ${BRANCH} origin/master
done
cd Packages/Sites/TYPO3.NeosDemoTypo3Org && git checkout -b ${BRANCH} origin/master ; cd -

$(dirname ${BASH_SOURCE[0]})/set-dependencies.sh "${BRANCH}.*@dev" ${BRANCH} "${BUILD_URL}"

push_branch ${BRANCH}
for PACKAGE in TYPO3.Neos TYPO3.Neos.NodeTypes TYPO3.Neos.Kickstarter TYPO3.TYPO3CR TYPO3.TypoScript TYPO3.Media ; do
	push_branch ${BRANCH} "Packages/Application/${PACKAGE}"
done
push_branch ${BRANCH} "Packages/Sites/TYPO3.NeosDemoTypo3Org"
