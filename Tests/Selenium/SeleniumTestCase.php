<?php
namespace TYPO3\Demo\Tests\Selenium;

/*
 * This script belongs to the TYPO3 project.                              *
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

require_once(__DIR__ . '/../../Packages/Framework/TYPO3/FLOW3/Tests/SeleniumTestCase.php');

/**
 * Base Testcase for Selenium Test
 *
 * Probably later phpunit version have better support to set url and other
 * selenium parameters flexible - for now this is done here
 *
 * @license http://www.gnu.org/licenses/gpl.html GNU General Public License, version 3 or later
 */
class SeleniumTestCase extends \TYPO3\FLOW3\Tests\SeleniumTestCase {
	protected function getSettingsFileName() {
		return __DIR__ . '/settings.xml';
	}
}

?>