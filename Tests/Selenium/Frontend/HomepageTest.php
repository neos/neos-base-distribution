<?php
namespace TYPO3\Demo\Tests\Selenium\Frontend;

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
 * Verify that frontend rendering works as expected for the Demopackage
 *
 * @license http://www.gnu.org/licenses/gpl.html GNU General Public License, version 3 or later
 */
class HomepageTest extends \TYPO3\Demo\Tests\Selenium\SeleniumTestCase {

	/**
	 * @test
	 * @author Karsten Dambekalns <karsten@typo3.org>
	 */
	public function homepageContainsExpectedText() {
		$this->openAndWait('/');
		$this->assertHomepageTextPresent();
	}

	/**
	 * @test
	 * @author Karsten Dambekalns <karsten@typo3.org>
	 */
	public function homepageCalledWithNameContainsExpectedText() {
		$this->open('/homepage.html');
		$this->assertHomepageTextPresent();
	}

	/**
	 * Helper which checks the homepage text is there.
	 */
	protected function assertHomepageTextPresent() {
		$this->assertEquals("TYPO3 Neos Demo Site", $this->getText("css=#header h1"));
		$this->checkText('This is the TYPO3 Neos demo website.');
	}

	/**
	 * @test
	 * @author Karsten Dambekalns <karsten@typo3.org>
	 */
	public function anotherPageContainsExpectedText() {
		$this->open('/homepage/anotherpage');
		$this->checkText('This is another page which exists for the sole purpose to demonstrate sub pages in TYPO3 Neos.');
	}

}
?>