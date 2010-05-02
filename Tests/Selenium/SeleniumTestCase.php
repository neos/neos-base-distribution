<?php
namespace F3\Demo\Tests\Selenium;
require_once 'PHPUnit/Extensions/SeleniumTestCase.php';


/*                                                                        *
 * This script is part of the TYPO3 project - inspiring people to share!  *
 *                                                                        *
 * TYPO3 is free software; you can redistribute it and/or modify it under *
 * the terms of the GNU General Public License version 2 as published by  *
 * the Free Software Foundation.                                          *
 *                                                                        *
 * This script is distributed in the hope that it will be useful, but     *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHAN-    *
 * TABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General      *
 * Public License for more details.                                       *
 *                                                                        */


/**
 * Base Testcase for Selenium Test
 * 
 * Propably later phpunit version have better support to set url and other selenium parameters flexible
 *  - for now this is done here - reading the given configuration file
 *
 * 
 * @license http://www.gnu.org/licenses/lgpl.html GNU Lesser General Public License, version 3 or later
 */
class SeleniumTestCase extends \PHPUnit_Extensions_SeleniumTestCase {
    
	
	/**
	 * @param  string $name
	 * @param  array  $data
	 * @param  string $dataName
	 * @param  array  $browser
	 * @throws InvalidArgumentException
	 * @author Daniel Pötzinger
	 */
	public function __construct($name = NULL, array $data = array(), $dataName = '', array $browser = array()) {		
		parent::__construct ( $name, $data, $dataName, $browser );
		$configuration = $this->getSeleniumTestConfigurationForBrowser($browser['browser']);		
		$this->captureScreenshotOnFailure = $configuration ['captureScreenshotOnFailure'];
		$this->screenshotPath = $configuration ['screenshotPath'];
		$this->screenshotUrl = $configuration ['screenshotUrl'];
		$this->setBrowserUrl ($configuration['url'] );
	}
	
	/**
	 * Quick acces to selenium commands - doing a click on a element
	 * 
	 * @param string $path Selenese selector
	 */
	protected function clickLink($path) {
		$this->checkElement ( $path );
		$this->clickAndWait ( $path );
	}
	
	/**
	 * Makes an assert to check the existence of a element	 
	 *  
	 * @param string $path Selenese selector
	 */
	protected function checkElement($path) {
		$this->assertTrue ( $this->isElementPresent ( $path ), 'element not found: ' . $path );
	}
	
	/**
	 * Makes an assert to check if a text is present
	 * 
	 * @param string $text
	 */
	protected function checkText($text) {
		$this->assertTrue ( $this->isTextPresent ( $text ), 'text not found: ' . $text );
	}
	
	/**
	 * opens the startpage (plain domain) and deletes relevant cookies
	 * 
	 */
	protected function resetCookiesAndOpenStartPage() {
	 	$this->open("/");
    	$this->deleteCookie("fe_typo_user", "");
    	$this->deleteCookie("be_typo_user", "");	 	
	}
	
	/**
	 * This methods reads the extended selenium configuration from the phpunit configurationfile to read things like "url".
	 * So they dont need to be hardcoded in the UnitTests and can be used on any domain.
	 * 
	 * 
	 * Hopefully this workaround will be replaced by a proper concept in PHPUnit
	 * 
	 * @param string $browser
	 * @throws InvalidArgumentException
	 * @return array width extended selenium parameters for the browser
	 */
	private function getSeleniumTestConfigurationForBrowser($browser) {
		$configuration = array();
	
		$cli_configuration=$this->parseParameters($GLOBALS['argv']);
		if (!isset($cli_configuration['configuration'])) {
			throw new \InvalidArgumentException ('the extended configuration for the seleniumtest is expected in the XML File. But no --configuration argument given.');
		}
		
		$realpath = realpath($cli_configuration['configuration']);

        if ($realpath === FALSE) {
        	throw new \InvalidArgumentException ('Could not read configuration');
        }
        $document = \PHPUnit_Util_XML::load(file_get_contents($realpath));
        $xpath    = new \DOMXPath($document);
        foreach ($xpath->query('selenium/browser') as $config) {            
			if ($browser == (string)$config->getAttribute('browser')) {
				if ((string)$config->getAttribute('url')=='') {
					throw new \InvalidArgumentException ('At least the url argument needs to be set in the selenium/browser element of your configurationfile');
				}
				return array(
							'url' 							=> 	(string)$config->getAttribute('url'),
							'captureScreenshotOnFailure'	=> 	(string)$config->getAttribute('captureScreenshotOnFailure'),
							'screenshotPath'				=> 	(string)$config->getAttribute('screenshotPath'),
							'screenshotUrl'					=> 	(string)$config->getAttribute('screenshotUrl'),
				
						);
			}
        }
        throw new \InvalidArgumentException ('No configuration in configurationfile found for the browser:'.$browser);		
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
    private static function parseParameters(array $params, $noopt = array()) {
        $result = array();
        // could use getopt() here (since PHP 5.3.0), but it doesn't work relyingly
        reset($params);
        while (list($tmp, $p) = each($params)) {
            if ($p{0} == '-') {
                $pname = substr($p, 1);
                $value = true;
                if ($pname{0} == '-') {
                    $pname = substr($pname, 1); // long-opt (--<param>)
                    if (strpos($p, '=') !== false) {
                    	list($pname, $value) = explode('=', substr($p, 2), 2); // value specified inline (--<param>=<value>)
                    }
                }
                // check if next parameter is a descriptor or a value
                $nextparm = current($params);
                if (!in_array($pname, $noopt) && $value === true && $nextparm !== false && $nextparm{0} != '-') list($tmp, $value) = each($params);
                $result[$pname] = $value;
            } else {
            	$result[] = $p; // param doesn't belong to any option
            }
        }
        return $result;
    }
}
