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

/**
 * Verify that frontend rendering works as expected for the Demopackage
 *
 * @version $Id$
 * @license http://www.gnu.org/licenses/gpl.html GNU General Public License, version 3 or later
 */
class HomepageTest extends \F3\Demo\Tests\Selenium\SeleniumTestCase {

	/**
	 * @test
	 * @author Karsten Dambekalns <karsten@typo3.org>
	 */
	public function homepageContainsExpectedText() {
		$this->open('/');
		$this->assertEquals("Welcome to TYPO3 Phoenix!", $this->getText("//div[@id='header']/h1"));
		$this->checkText('The fact that you can read these lines means that TYPO3 Phoenix is able to render content.');
	}

	/**
	 * @test
	 * @author Karsten Dambekalns <karsten@typo3.org>
	 */
	public function homepageCalledWithNameContainsExpectedText() {
		$this->open('/homepage.html');
		$this->assertEquals("Welcome to TYPO3 Phoenix!", $this->getText("//div[@id='header']/h1"));
		$this->checkText('The fact that you can read these lines means that TYPO3 Phoenix is able to render content.');
	}

	/**
	 * @test
	 * @author Karsten Dambekalns <karsten@typo3.org>
	 */
	public function anotherPageContainsExpectedText() {
		$this->open('/homepage/anotherpage');
		$this->checkText('This is another page which exists for the solely purpose to demonstrate sub pages in TYPO3 Phoenix.');
	}

}
?>