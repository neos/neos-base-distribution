<?php
namespace F3\Demo\Tests\Selenium\Backend;
require_once dirname(__FILE__).'/../SeleniumTestCase.php';


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
 * Common Backend Selenese functions
 *
 * 
 * @license http://www.gnu.org/licenses/lgpl.html GNU Lesser General Public License, version 3 or later
 */
class BackendTestCase extends \F3\Demo\Tests\Selenium\SeleniumTestCase {
   
    
    /**
     * does a backend login
     * 
     * @param string $username
     * @param string $password
     * @author Daniel Pötzinger
     */
    protected function backendLogin($username,$password) {
    	$this->open("/typo3");
        $this->checkText("Username");
        $this->checkText("Password");
        $this->type("ext-comp-1004", $username);
        $this->type("ext-comp-1005", $password);
    	$this->clickLink('ext-gen22');  
    }

  
}
?>