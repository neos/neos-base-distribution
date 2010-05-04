<?php
declare(ENCODING = 'utf-8');
namespace F3\Demo\Tests\Selenium;

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

require_once('PHPUnit/Extensions/SeleniumTestCase.php');

/**
 * Base Testcase for Selenium Test
 *
 * Propably later phpunit version have better support to set url and other
 * selenium parameters flexible - for now this is done here
 *
 * @version $Id$
 * @license http://www.gnu.org/licenses/gpl.html GNU General Public License, version 3 or later
 */
class SeleniumTestCase extends \PHPUnit_Extensions_SeleniumTestCase {

	/**
	 * Disable the backup and restoration of the $GLOBALS array.
	 */
	protected $backupGlobals = FALSE;

	/**
	 * Enable or disable the backup and restoration of static attributes.
	 */
	protected $backupStaticAttributes = TRUE;

    /**
     * Whether or not this test is running in a separate PHP process.
     *
     * @var    boolean
     */
    protected $inIsolation = TRUE;

	/**
	 *
	 * @var array
	 */
	public static $browsers = array(
		array(
			'name'    => 'Firefox Testing Phoenix',
			'browser' => '*chrome',
			'host'    => 'selenium.phoenix.hosting.netlogix.de',
			'port'    => 4444,
			'timeout' => 30000,
		),
	);

	public function setUp() {
#		$this->captureScreenshotOnFailure = $configuration ['captureScreenshotOnFailure'];
#		$this->screenshotPath = $configuration ['screenshotPath'];
#		$this->screenshotUrl = $configuration ['screenshotUrl'];
		$this->setBrowserUrl('http://latest.phoenix.demo.typo3.org/');
	}

	/**
	 * Quick access to selenium commands - doing a click on a element
	 *
	 * @param string $path Selenese selector
	 * @return void
	 */
	protected function clickLink($path) {
		$this->checkElement($path);
		$this->clickAndWait($path);
	}

	/**
	 * Makes an assert to check the existence of a element
	 *
	 * @param string $path Selenese selector
	 * @return void
	 */
	protected function checkElement($path) {
		$this->assertTrue($this->isElementPresent($path), 'element not found: ' . $path);
	}

	/**
	 * Makes an assert to check if a text is present
	 *
	 * @param string $text
	 * @return void
	 */
	protected function checkText($text) {
		$this->assertTrue($this->isTextPresent($text), 'text not found: ' . $text);
	}

	/**
	 * Opens the startpage (plain domain) and deletes relevant cookies
	 *
	 * @return void
	 */
	protected function resetCookiesAndOpenStartPage() {
		$this->open('/');
		$this->deleteCookie('fe_typo_user', '');
		$this->deleteCookie('be_typo_user', '');
	}

}

?>