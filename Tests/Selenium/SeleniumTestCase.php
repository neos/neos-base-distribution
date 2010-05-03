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
 * Propably later phpunit version have better support to set url and other selenium parameters flexible
 *  - for now this is done here - reading the given configuration file
 *
 * @version $Id$
 * @license http://www.gnu.org/licenses/gpl.html GNU General Public License, version 3 or later
 */
class SeleniumTestCase extends \PHPUnit_Extensions_SeleniumTestCase {

	/**
	 * @param  string $name
	 * @param  array  $data
	 * @param  string $dataName
	 * @param  array  $browser
	 * @throws \InvalidArgumentException
	 * @author Daniel Pötzinger
	 */
	public function __construct($name = NULL, array $data = array(), $dataName = '', array $browser = array()) {
		parent::__construct($name, $data, $dataName, $browser);

		$configuration = $this->getSeleniumTestConfigurationForBrowser($browser['browser']);
		$this->captureScreenshotOnFailure = $configuration ['captureScreenshotOnFailure'];
		$this->screenshotPath = $configuration ['screenshotPath'];
		$this->screenshotUrl = $configuration ['screenshotUrl'];
		$this->setBrowserUrl($configuration['url']);
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
		$this->open("/");
		$this->deleteCookie("fe_typo_user", "");
		$this->deleteCookie("be_typo_user", "");
	}

	/**
	 * This methods reads the extended selenium configuration from the phpunit
	 * configurationfile to read things like "url".
	 * So they dont need to be hardcoded in the UnitTests and can be used on any domain.
	 *
	 * Hopefully this workaround will be replaced by a proper concept in PHPUnit
	 *
	 * @param string $browser
	 * @throws \InvalidArgumentException
	 * @return array with extended selenium parameters for the browser
	 */
	protected function getSeleniumTestConfigurationForBrowser($browser) {
		$cliConfiguration = $this->parseParameters($GLOBALS['argv']);
		if (!isset($cliConfiguration['configuration'])) {
			throw new \InvalidArgumentException('The extended configuration for the Selenium tests is expected in a XML File. But no --configuration argument was given.');
		}

		$configurationPathAndFileName = realpath($cliConfiguration['configuration']);
		if ($configurationPathAndFileName === FALSE) {
			throw new \InvalidArgumentException('Could not read configuration file.');
		}

		$configurations = new \DOMXPath(\PHPUnit_Util_XML::load(file_get_contents($configurationPathAndFileName)));
		foreach ($configurations->query('selenium/browser') as $configuration) {
			if ($browser === (string) $configuration->getAttribute('browser')) {
				if ((string) $configuration->getAttribute('url') === '') {
					throw new \InvalidArgumentException('At least the url argument needs to be set in the selenium/browser element of your configurationfile');
				}
				return array(
					'url' => (string) $configuration->getAttribute('url'),
					'captureScreenshotOnFailure' => (string) $configuration->getAttribute('captureScreenshotOnFailure'),
					'screenshotPath' => (string) $configuration->getAttribute('screenshotPath'),
					'screenshotUrl' => (string) $configuration->getAttribute('screenshotUrl'),
				);
			}
		}

		throw new \InvalidArgumentException('No configuration in configurationfile found for the browser:' . $browser);
	}

	/**
	 * Parses $GLOBALS['argv'] style arguments for parameters and assigns them to an array.
	 *
	 * Supports:
	 * -e
	 * -e <value>
	 * --long-param
	 * --long-param=<value>
	 * --long-param <value>
	 * <value>
	 *
	 * @param array the _SERVER[argv] array
	 * @param array $noopt List of parameters without values
	 * @return array the parsed cli arguments array (key=>value)
	 */
	protected function parseParameters(array $rawParameters, array $noopt = array()) {
		$parsedParameters = array();
		// could use getopt() here (since PHP 5.3.0), but it doesn't work relyingly
		reset($rawParameters);
		while (list(, $parameter) = each($rawParameters)) {
			if ($parameter{0} === '-') {
				$name = substr($parameter, 1);
				$value = TRUE;
				if ($name{0} === '-') {
					$name = substr($name, 1); // long-opt (--<param>)
					if (strpos($parameter, '=') !== FALSE) {
						list($name, $value) = explode('=', substr($parameter, 2), 2); // value specified inline (--<param>=<value>)
					}
				}
				// check if next parameter is a descriptor or a value
				$nextParameter = current($rawParameters);
				if (!in_array($name, $noopt) && $value === TRUE && $nextParameter !== FALSE && $nextParameter{0} !== '-')
					list(, $value) = each($rawParameters);
				$parsedParameters[$name] = $value;
			} else {
				$parsedParameters[] = $parameter; // param doesn't belong to any option
			}
		}
		return $parsedParameters;
	}

}

?>