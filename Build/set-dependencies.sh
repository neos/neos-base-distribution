#!/bin/bash

#
# Updates the dependencies in composer.json files of the dist and its
# packages.
#
# Needs the following parameters
#
# VERSION          the version that is "to be released"
# BRANCH           the branch that is worked on, used in commit message
# BUILD_URL        used in commit message
#

source $(dirname ${BASH_SOURCE[0]})/BuildEssentials/ReleaseHelpers.sh

COMPOSER_PHAR="$(dirname ${BASH_SOURCE[0]})/../composer.phar"
if [ ! -f ${COMPOSER_PHAR} ]; then
	echo >&2 "No composer.phar, expected it at ${COMPOSER_PHAR}"
	exit 1
fi

if [ -z "$1" ] ; then
	echo >&2 "No version specified (e.g. 2.1.*) as first parameter."
	exit 1
else
	if [[ $1 =~ (dev)-.+ || $1 =~ .+@(dev) || $1 =~ (alpha|beta|RC)[0-9]+ ]] ; then
		VERSION=$1
		STABILITY_FLAG=${BASH_REMATCH[1]}
	else
		if [[ $1 =~ ([0-9]+\.[0-9]+)\.[0-9] ]] ; then
			VERSION=${BASH_REMATCH[1]}.*
		else
			echo >&2 "Version $1 could not be parsed."
			exit 1
		fi
	fi
fi

if [ -z "$2" ] ; then
	echo >&2 "No branch specified (e.g. 2.1) as second parameter."
	exit 1
fi
BRANCH=$2

if [ -z "$3" ] ; then
	echo >&2 "No build URL specified as third parameter."
	exit 1
fi
BUILD_URL="$3"

if [[ ${STABILITY_FLAG} ]] ; then
	php "${COMPOSER_PHAR}" require --no-update "typo3/typoscript:@${STABILITY_FLAG}"
	php "${COMPOSER_PHAR}" require --no-update "typo3/typo3cr:@${STABILITY_FLAG}"
else
	php $(dirname ${BASH_SOURCE[0]})/BuildEssentials/FilterStabilityFlags.php
fi
php "${COMPOSER_PHAR}" require --no-update "typo3/neos:${VERSION}"
php "${COMPOSER_PHAR}" require --no-update "typo3/neos-nodetypes:${VERSION}"
php "${COMPOSER_PHAR}" require --no-update "typo3/neosdemotypo3org:${VERSION}"
php "${COMPOSER_PHAR}" require --no-update "typo3/neos-kickstarter:${VERSION}"
commit_manifest_update ${BRANCH} "${BUILD_URL}"

php "${COMPOSER_PHAR}" --working-dir=Packages/Application/TYPO3.Neos require --no-update "typo3/typo3cr:${VERSION}"
php "${COMPOSER_PHAR}" --working-dir=Packages/Application/TYPO3.Neos require --no-update "typo3/typoscript:${VERSION}"
php "${COMPOSER_PHAR}" --working-dir=Packages/Application/TYPO3.Neos require --no-update "typo3/media:${VERSION}"
commit_manifest_update ${BRANCH} "${BUILD_URL}" "Packages/Application/TYPO3.Neos"

for PACKAGE in TYPO3.Neos.NodeTypes TYPO3.Neos.Kickstarter ; do
	php "${COMPOSER_PHAR}" --working-dir=Packages/Application/${PACKAGE} require --no-update "typo3/neos:${VERSION}"
	commit_manifest_update ${BRANCH} "${BUILD_URL}" "Packages/Application/${PACKAGE}"
done
