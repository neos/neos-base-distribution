<?php
declare(ENCODING = 'utf-8');
namespace F3\Build;

/*                                                                        *
 * This script belongs to the FLOW3 build system.                         *
 *                                                                        *
 * It is free software; you can redistribute it and/or modify it under    *
 * the terms of the GNU General Public License as published by the Free   *
 * Software Foundation, either version 3 of the License, or (at your      *
 * option) any later version.                                             *
 *                                                                        *
 * This script is distributed in the hope that it will be useful, but     *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHAN-    *
 * TABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General      *
 * Public License for more details.                                       *
 *                                                                        *
 * You should have received a copy of the GNU General Public License      *
 * along with the script.                                                 *
 * If not, see http://www.gnu.org/licenses/gpl.html                       *
 *                                                                        *
 * The TYPO3 project - inspiring people to share!                         *
 *                                                                        */

/**
 * A simple class loader that deals with the Framework classes and is intended
 * for use with PHPUnit.
 *
 * PHPUnit offers the possibility to use a "bootstrap" file with every test.
 *
 * This file can be used and will register a simple autoloader so that the files
 * used in the tests can be found.
 *
 * To use PHPUnit with Netbeans create a folder to be used as project test folder
 * and populate it with something like
 *  for i in `ls ../Packages/Framework/` ;
 *    do ln -s ../Packages/Framework/$i/Tests/Unit $i ;
 *  done
 *
 * @param string $className
 * @return void
 * @author Karsten Dambekalns <karsten@typo3.org>
 */
function loadClassForTesting($className) {
	$classNameParts = explode('\\', $className);
	if (is_array($classNameParts) && $classNameParts[0] === 'F3') {
		$packagesBasePathIterator = new \DirectoryIterator(dirname(__FILE__) . '/../../Packages/');
		foreach ($packagesBasePathIterator as $fileInfo) {
			if ($fileInfo->isDir() && !$fileInfo->isDot()) {
				$classFilePathAndName = dirname(__FILE__) . '/../../Packages/' . $fileInfo->getFilename() . '/' . $classNameParts[1] . '/Classes/';
				$classFilePathAndName .= implode(array_slice($classNameParts, 2, -1), '/') . '/';
				$classFilePathAndName .= end($classNameParts) . '.php';
				if (file_exists($classFilePathAndName)) {
					require($classFilePathAndName);
					break;
				}
			}
		}
	}
}

spl_autoload_register('F3\Build\loadClassForTesting');
set_include_path(get_include_path() . ':' . dirname(__FILE__) . '/../Resources/PHP');

$_SERVER['FLOW3_ROOTPATH'] = dirname(__FILE__) . '/../../';
$_SERVER['FLOW3_WEBPATH'] = dirname(__FILE__) . '/../../Web/';
\F3\FLOW3\Core\Bootstrap::defineConstants();

?>