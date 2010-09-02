<?php
declare(ENCODING = 'utf-8');
namespace F3\Demo\Tests\Selenium\Backend;

/*                                                                        *
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

require_once(__DIR__ . '/../SeleniumTestCase.php');

/**
 * Common Backend Selenese functions
 *
 * @license http://www.gnu.org/licenses/gpl.html GNU General Public License, version 3 or later
 */
class BackendTestCase extends \F3\Demo\Tests\Selenium\SeleniumTestCase {

	/**
	 * does a backend login
	 *
	 * @param string $username
	 * @param string $password
	 * @return void
	 * @author Daniel Pötzinger
	 */
	protected function backendLogin($username, $password) {
		$this->open("/typo3/login");
		$this->type("//input[@name='F3[FLOW3][Security][Authentication][Token][UsernamePassword][username]']", $username);
		$this->type("//input[@name='F3[FLOW3][Security][Authentication][Token][UsernamePassword][password]']", $password);
		$this->clickAndWait("//input[@type='submit']");
	}
}
?>