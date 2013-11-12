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
	if [[ $1 =~ (dev)-.+ || $1 =~ (alpha|beta)[0-9]+ ]] ; then
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
	php "${COMPOSER_PHAR}" require --no-update "typo3/eel:@${STABILITY_FLAG}"
	php "${COMPOSER_PHAR}" require --no-update "typo3/fluid:@${STABILITY_FLAG}"
	php "${COMPOSER_PHAR}" require --no-update "typo3/party:@${STABILITY_FLAG}"
else
	php $(dirname ${BASH_SOURCE[0]})/BuildEssentials/FilterStabilityFlags.php
fi
php "${COMPOSER_PHAR}" require --no-update "typo3/flow:${VERSION}"
php "${COMPOSER_PHAR}" require --no-update "typo3/welcome:${VERSION}"
php "${COMPOSER_PHAR}" require --dev --no-update "typo3/kickstart:${VERSION}"
php "${COMPOSER_PHAR}" require --dev --no-update "typo3/buildessentials:${VERSION}"
commit_manifest_update ${BRANCH} "${BUILD_URL}"

php "${COMPOSER_PHAR}" --working-dir=Packages/Framework/TYPO3.Flow require --no-update "typo3/eel:${VERSION}"
php "${COMPOSER_PHAR}" --working-dir=Packages/Framework/TYPO3.Flow require --no-update "typo3/fluid:${VERSION}"
php "${COMPOSER_PHAR}" --working-dir=Packages/Framework/TYPO3.Flow require --no-update "typo3/party:${VERSION}"
commit_manifest_update ${BRANCH} "${BUILD_URL}" "Packages/Framework/TYPO3.Flow"

for PACKAGE in `ls Packages/Framework` ; do
	if [ ${PACKAGE} != "TYPO3.Flow" ] ; then
		php "${COMPOSER_PHAR}" --working-dir=Packages/Framework/${PACKAGE} require --no-update "typo3/flow:${VERSION}"
		commit_manifest_update ${BRANCH} "${BUILD_URL}" "Packages/Framework/${PACKAGE}"
	fi
done
